/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2e.wtp.tests.conversion;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.core.project.conversion.IProjectConversionEnabler;
import org.eclipse.m2e.wtp.tests.AbstractWTPTestCase;

/**
 * class copied from m2e-core-tests
 *
 * @author Fred Bricon
 */
@SuppressWarnings("restriction")
public abstract class AbstractWtpProjectConversionTestCase extends AbstractWTPTestCase {

	private static final String OVERRIDE_COMPILER_VERSION_KEY = "org.eclipse.m2e.jdt.conversion.compiler.version";

	protected abstract String getPackagingUnderTest();

	/**
	 * Instanciates a new default Maven Model, using the projectName as groupId
	 * and artifactId, having a default version of 0.0.1-SNAPSHOT.
	 */
	protected Model initDefaultModel(String projectName, String packaging) {
		Model model = new Model();
		model.setModelVersion("4.0.0"); //$NON-NLS-1$
		model.setArtifactId(projectName);
		model.setGroupId(projectName);
		model.setVersion("0.0.1-SNAPSHOT");//$NON-NLS-1$
		model.setPackaging(packaging);
		return model;
	}

	protected void convert(IProject project) throws CoreException, InterruptedException {
		convert(project, getPackagingUnderTest());
	}

	/**
	 * Converts an Eclipse project to a Maven project with a given packaging
	 * (generates a pom.xm and enables the Maven nature)
	 */
	protected void convert(IProject project, String packaging) throws CoreException, InterruptedException {
		Model model = initDefaultModel(project.getName(), packaging);
		convert(project, model);
	}

	protected void convert(IProject project, Model model) throws CoreException, InterruptedException {
		MavenPlugin.getProjectConversionManager().convert(project, model, monitor);
		createPomXml(project, model);
		ResolverConfiguration configuration = new ResolverConfiguration();
		MavenPlugin.getProjectConfigurationManager().enableMavenNature(project, configuration, monitor);
		waitForJobsToComplete(monitor);
	}

	/**
	 * Serializes the maven model to &lt;project&gt;/pom.xml
	 */
	protected void createPomXml(IProject project, Model model) throws CoreException {
		MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();
		mavenModelManager.createMavenModel(project.getFile(IMavenConstants.POM_FILE_NAME), model);
	}

	/**
	 * Asserts the contents of the file is identical to the expectedFile
	 */
	protected void assertEquals(String message, IFile expectedFile, IFile file) throws Exception {
		assertEquals(message, getAsString(expectedFile), getAsString(file));
	}

	/**
	 * Asserts the generated pom.xml is identical to
	 * &lt;project&gt;/expectedPom.xml
	 */
	protected void verifyGeneratedPom(IProject project) throws Exception {
		assertEquals("pom.xml comparison failed", project.getFile("expectedPom.xml"),
				project.getFile(IMavenConstants.POM_FILE_NAME));
	}

	protected IProject testProjectConversion(String projectName) throws Exception {
		deleteProject(projectName);

		// Import existing regular Eclipse project
		IProject project = createExisting(projectName, "projects/conversion/" + projectName);
		assertTrue(projectName + " was not created!", project.exists());
		checkForErrors(project);

		// Check project has proper conversion enabler configured
		assertConversionEnabler(project, getPackagingUnderTest());

		// Check the project converts and builds correctly
		assertConvertsAndBuilds(project);

		return project;
	}

	private void assertConversionEnabler(IProject project, String packagingUnderTest) {
		IProjectConversionEnabler conversionEnabler = MavenPlugin.getProjectConversionManager()
				.getConversionEnablerForProject(project);
		assertNotNull(conversionEnabler);
		IStatus conversionStatus = conversionEnabler.canBeConverted(project);
		assertEquals(IStatus.OK, conversionStatus.getSeverity());
		String[] packagings = conversionEnabler.getPackagingTypes(project);
		assertEquals("Expected one packaging but got " + packagings, 1, packagings.length);
		assertEquals(packagingUnderTest, packagings[0]);
	}

	protected void assertConvertsAndBuilds(IProject project) throws CoreException, InterruptedException, Exception {

		try {
			overridePluginsVersions();

			// Convert the project to a Maven project (generates pom.xml,
			// enables Maven nature)
			convert(project);

			// Checks the generated pom.xml is identical to
			// /<projectName>/expectedPom.xml
			verifyGeneratedPom(project);

			// Checks the Maven project builds without errors
			project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
			waitForJobsToComplete();
			checkForErrors(project);
		} finally {
			restorePluginsVersion();
		}
	}

	protected void overridePluginsVersions() {
		forcePluginVersion(OVERRIDE_COMPILER_VERSION_KEY, getCompilerVersion());
		forcePluginVersion(getOverrideSystemPropertyKey(), getTestedPluginVersion());
	}

	protected String getCompilerVersion() {
		// Lock compiler version so that m2e-jdt evolutions don't bite us
		return "2.3.2";
	}

	protected void checkForErrors(IProject project) throws CoreException {
		assertNoErrors(project);
	}

	protected void forcePluginVersion(String key, String version) {
		if (key != null) {
			if (version == null) {
				System.clearProperty(key);
			} else {
				System.setProperty(key, version);
			}
		}
	}

	protected void restorePluginsVersion() {
		forcePluginVersion(OVERRIDE_COMPILER_VERSION_KEY, null);
		forcePluginVersion(getOverrideSystemPropertyKey(), null);
	}

	abstract protected String getOverrideSystemPropertyKey();

	abstract protected String getTestedPluginVersion();
}
