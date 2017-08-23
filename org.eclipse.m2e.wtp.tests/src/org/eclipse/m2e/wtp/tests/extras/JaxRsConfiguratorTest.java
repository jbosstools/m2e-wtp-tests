package org.eclipse.m2e.wtp.tests.extras;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.WorkspaceHelpers;
import org.eclipse.m2e.wtp.WTPProjectsUtil;
import org.eclipse.m2e.wtp.jaxrs.internal.MavenJaxRsConstants;
import org.eclipse.m2e.wtp.preferences.ConfiguratorEnabler;
import org.eclipse.m2e.wtp.tests.AbstractWTPTestCase;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.junit.Test;


@SuppressWarnings("restriction")
public class JaxRsConfiguratorTest extends AbstractWTPTestCase {

	@Test
	public void testJBIDE9290_supportMultipleJaxRsImplems() throws Exception {
		IProject[] projects = importProjects("projects/jaxrs/", 
											new String[]{ 
												"jaxrs-jersey/pom.xml",
												"jaxrs-resteasy/pom.xml",
												"jaxrs-javaee-api/pom.xml",
												"jaxrs-rest-10/pom.xml",
											}, 
											new ResolverConfiguration());
		waitForJobsToComplete();
		IProject jersey = projects[0];
		assertIsJaxRsProject(jersey, MavenJaxRsConstants.JAX_RS_FACET_1_1);

		IProject resteasy = projects[1];
		assertIsJaxRsProject(resteasy, MavenJaxRsConstants.JAX_RS_FACET_1_1);

		IProject javaeeapi = projects[2];
		assertIsJaxRsProject(javaeeapi, MavenJaxRsConstants.JAX_RS_FACET_1_1);

		IProject rest_10 = projects[3];
		assertIsJaxRsProject(rest_10, MavenJaxRsConstants.JAX_RS_FACET_1_0);
	}

	@Test
	public void testJBIDE9290_errorMarkers() throws Exception {
		String projectLocation = "projects/jaxrs/jaxrs-error";
		IProject jaxRsProject = importProject(projectLocation+"/pom.xml");
		waitForJobsToComplete(monitor);
		IFacetedProject facetedProject = ProjectFacetsManager.create(jaxRsProject);
		assertNotNull(jaxRsProject.getName() + " is not a faceted project", facetedProject);
		assertFalse("JAX-RS Facet should be missing", facetedProject.hasProjectFacet(MavenJaxRsConstants.JAX_RS_FACET));
		assertHasJaxRsConfigurationError(jaxRsProject, "JAX-RS (REST Web Services) 1.1 can not be installed : One or more constraints have not been satisfied.");
		assertHasJaxRsConfigurationError(jaxRsProject, "JAX-RS (REST Web Services) 1.1 requires Java 1.5 or newer.");

		//Check markers are removed upon configuration update
		updateProject(jaxRsProject, "good.pom.xml", 1000);
		assertNoErrors(jaxRsProject);
		assertIsJaxRsProject(jaxRsProject, MavenJaxRsConstants.JAX_RS_FACET_1_1);
	}

	@Test
	public void testJBIDE12727_badCaching() throws Exception {
		String projectLocation = "projects/jaxrs/chimera/";
		IProject jaxRsProject = importProject(projectLocation+"/jaxrs/jaxrs-chimera/pom.xml");
		waitForJobsToComplete(new NullProgressMonitor());
		IFacetedProject facetedProject = ProjectFacetsManager.create(jaxRsProject);
		assertNotNull(jaxRsProject.getName() + " is not a faceted project", facetedProject);
		assertTrue("JAX-RS Facet should be present", facetedProject.hasProjectFacet(MavenJaxRsConstants.JAX_RS_FACET));

		jaxRsProject.delete(true, monitor);
		waitForJobsToComplete();

		jaxRsProject = importProject(projectLocation+"/nojaxrs/jaxrs-chimera/pom.xml");
		waitForJobsToComplete(new NullProgressMonitor());
		assertNoErrors(jaxRsProject);
		facetedProject = ProjectFacetsManager.create(jaxRsProject);
		assertFalse("JAX-RS Facet should be missing", facetedProject.hasProjectFacet(MavenJaxRsConstants.JAX_RS_FACET));
	}


	private void assertHasJaxRsConfigurationError(IProject project, String message) throws Exception {
		WorkspaceHelpers.assertErrorMarker(MavenJaxRsConstants.JAXRS_CONFIGURATION_ERROR_MARKER_ID, message, 1, "", project);
	}

	private void assertIsJaxRsProject(IProject project,
			IProjectFacetVersion expectedJaxRsVersion) throws Exception {
		assertNoErrors(project);
		IFacetedProject facetedProject = ProjectFacetsManager.create(project);
		assertNotNull(project.getName() + " is not a faceted project", facetedProject);
		assertEquals("Unexpected JAX-RS Version", expectedJaxRsVersion, facetedProject.getInstalledVersion(MavenJaxRsConstants.JAX_RS_FACET));
		assertTrue("Java Facet is missing",	facetedProject.hasProjectFacet(JavaFacet.FACET));
	}

	@Test
	public void test399104_disableJaxRsConfigurator() throws Exception {
		ConfiguratorEnabler enabler = getConfiguratorEnabler("org.eclipse.m2e.wtp.jaxrs.enabler");
		IProject project;
		try {
			enabler.setEnabled(false);
			project = importProject("projects/jaxrs/jaxrs-jersey/pom.xml");
			waitForJobsToComplete();
			assertNoErrors(project);
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			assertNotNull(project.getName() + " is not a faceted project", facetedProject);
			assertNull("Unexpected JAX-RS Facet", facetedProject.getInstalledVersion(MavenJaxRsConstants.JAX_RS_FACET));
		} finally {
			enabler.setEnabled(true);
		}
		updateProject(project);
		assertNoErrors(project);
		assertIsJaxRsProject(project, MavenJaxRsConstants.JAX_RS_FACET_1_1);
	}


	@Test
	public void test406828_forceDisableJaxRsConfigurator() throws Exception {
		IProject project = importProject("projects/jaxrs/jaxrs-disabled/pom.xml");
		waitForJobsToComplete();
		assertNoErrors(project);
		IFacetedProject facetedProject = ProjectFacetsManager.create(project);
		assertNotNull(project.getName() + " is not a faceted project", facetedProject);
		assertNull("Unexpected JAX-RS Facet", facetedProject.getInstalledVersion(MavenJaxRsConstants.JAX_RS_FACET));
	}


	@Test
	public void test406828_forceEnableJaxRsConfigurator() throws Exception {
		ConfiguratorEnabler enabler = getConfiguratorEnabler("org.eclipse.m2e.wtp.jaxrs.enabler");
		IProject project;
		try {
			enabler.setEnabled(false);
			project = importProject("projects/jaxrs/jaxrs-enabled/pom.xml");
			waitForJobsToComplete();
			assertNoErrors(project);
			assertIsJaxRsProject(project, MavenJaxRsConstants.JAX_RS_FACET_1_1);
		} finally {
			enabler.setEnabled(true);
		}
	}

	@Test
	public void test407236_JaxRs20() throws Exception {
		if (!canRunJavaEe7Tests) {
			System.err.println("skipped test407236_JaxRs20()");
			return;
		}
		IProject jaxRsProject = importProject("projects/jaxrs/jaxrs-20/pom.xml");
		waitForJobsToComplete(monitor);
		assertIsJaxRsProject(jaxRsProject, MavenJaxRsConstants.JAX_RS_FACET_2_0);
	}

	@Test
	public void test519228_jaxRsFragment() throws Exception {
		IProject project = importProject("projects/jaxrs/jaxrs-fragment/pom.xml");
		waitForJobsToComplete(monitor);
		assertIsJaxRsProject(project, MavenJaxRsConstants.JAX_RS_FACET_2_0);
		IFacetedProject facetedProject = ProjectFacetsManager.create(project);
		assertTrue("Missing Web Fragment Facet", facetedProject.hasProjectFacet(WTPProjectsUtil.WEB_FRAGMENT_FACET));
	}
}
