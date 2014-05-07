/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.m2e.wtp.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.codehaus.plexus.util.IOUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.j2ee.classpathdep.IClasspathDependencyConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.web.project.facet.WebFacetUtils;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.jdt.internal.BuildPathManager;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.ClasspathHelpers;
import org.eclipse.m2e.wtp.MavenWtpPlugin;
import org.eclipse.m2e.wtp.preferences.ConfiguratorEnabler;
import org.eclipse.m2e.wtp.preferences.IMavenWtpPreferences;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.junit.BeforeClass;
import org.osgi.framework.Version;

@SuppressWarnings("restriction")
public abstract class AbstractWTPTestCase extends AbstractMavenProjectTestCase {

  protected static final IProjectFacetVersion DEFAULT_WEB_VERSION = WebFacetUtils.WEB_FACET.getVersion("2.5");
  protected static final IProjectFacet EJB_FACET = ProjectFacetsManager.getProjectFacet(IJ2EEFacetConstants.EJB); 
  protected static final IProjectFacet UTILITY_FACET = ProjectFacetsManager.getProjectFacet(IJ2EEFacetConstants.UTILITY);
  protected static final IProjectFacetVersion UTILITY_10 = UTILITY_FACET.getVersion("1.0");
  protected static final IProjectFacet EAR_FACET = ProjectFacetsManager.getProjectFacet(IJ2EEFacetConstants.ENTERPRISE_APPLICATION);
  protected static final IProjectFacetVersion DEFAULT_EAR_FACET = IJ2EEFacetConstants.ENTERPRISE_APPLICATION_13;

  protected static final String MAVEN_CLASSPATH_CONTAINER = "org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER";
  protected static final String JRE_CONTAINER_J2SE_1_5 = "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/J2SE-1.5";

  protected static final String CLASSPATH_ARCHIVENAME_ATTRIBUTE;
  
  private long startTime;
  
  static {
    String archiveNameAttribute = null;
    try {
      Field classpathArchiveNameField = IClasspathDependencyConstants.class.getField("CLASSPATH_ARCHIVENAME_ATTRIBUTE");
      archiveNameAttribute = (String)classpathArchiveNameField.get(null);
    } catch (Exception e) {
      System.err.println("IClasspathDependencyConstants.CLASSPATH_ARCHIVENAME_ATTRIBUTE not available");
    }
    CLASSPATH_ARCHIVENAME_ATTRIBUTE = archiveNameAttribute;
  }
  
  protected static final boolean canRunJavaEe7Tests = checkJavaEe7Compatibility();

  protected static IClasspathContainer getWebLibClasspathContainer(IJavaProject project) throws JavaModelException {
    IClasspathEntry[] entries = project.getRawClasspath();
    for(int i = 0; i < entries.length; i++ ) {
      IClasspathEntry entry = entries[i];
      if(entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER && "org.eclipse.jst.j2ee.internal.web.container".equals(entry.getPath().segment(0))) {
        return JavaCore.getClasspathContainer(entry.getPath(), project);
      }
    }
    return null;
  }

  
  @BeforeClass
  public static void beforeClass() {
	  ValidationFramework.getDefault().suspendAllValidation(true);
  }
  
    @Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
	  	long s = System.currentTimeMillis();
		super.setUp();
		long e = System.currentTimeMillis() - s;
		System.err.println("Setup took " + e  + " ms");
		startTime = System.currentTimeMillis();
	}

  
    @Override
	protected void tearDown() throws Exception {
		long elapsed = System.currentTimeMillis() - startTime;
		System.err.println("Test ran in " + elapsed + " ms");

	  	long s = System.currentTimeMillis();
		super.tearDown();
		long e = System.currentTimeMillis() - s;
		System.err.println("teardown took " + e  + " ms");
	}
  
  
  private static boolean hasExtraAttribute(IClasspathEntry entry, String expectedAttribute) {
    for (IClasspathAttribute cpa : entry.getExtraAttributes()) {
      if (expectedAttribute.equals(cpa.getName())){
        return true;
      }
    }
    return false;
  }

  protected String toString(IVirtualReference[] references) {
    StringBuilder sb = new StringBuilder("[");
    
    String sep = "";
    for(IVirtualReference reference : references) {
      IVirtualComponent component = reference.getReferencedComponent();
      sb.append(sep).append(reference.getRuntimePath() + " - ");
      sb.append(component.getName());
      sb.append(" " + component.getMetaProperties());
      sep = ", ";
    }
    
    return sb.append(']').toString();
  }

  protected String toString(IFile[] files) {
    StringBuilder sb = new StringBuilder("[");
    
    String sep = "";
    for(IFile file : files) {
      sb.append(sep).append(file.getFullPath());
      sep = ", ";
    }
    
    return sb.append(']').toString();
  }

  protected void assertHasMarker(String expectedMessage, List<IMarker> markers) throws CoreException {
    Pattern p = Pattern.compile(expectedMessage);
    for (IMarker marker : markers) {
      String markerMsg = marker.getAttribute(IMarker.MESSAGE).toString(); 
      if (p.matcher(markerMsg).find()) {
        return ;
      }
    }
    fail("[" + expectedMessage + "] is not a marker. Existing markers are :"+toString(markers));
  }

  protected void assertMissingMarker(String expectedMessage, List<IMarker> markers) throws CoreException {
    Pattern p = Pattern.compile(expectedMessage);
    for (IMarker marker : markers) {
      String markerMsg = marker.getAttribute(IMarker.MESSAGE).toString(); 
      if (p.matcher(markerMsg).find()) {
        fail("[" + expectedMessage + "] was found but should be missing. Existing markers are :"+toString(markers)) ;
      }
    }
  }
  
  protected void assertNotDeployable(IClasspathEntry entry) {
    assertDeployable(entry, false);
  }

  protected void assertDeployable(IClasspathEntry entry, boolean expectedDeploymentStatus) {
    //Useless : IClasspathDependencyConstants.CLASSPATH_COMPONENT_DEPENDENCY doesn't seem to be used in WTP 3.2.0. Has it ever worked???
    //assertEquals(entry.toString() + " " + IClasspathDependencyConstants.CLASSPATH_COMPONENT_DEPENDENCY, expectedDeploymentStatus,      hasExtraAttribute(entry, IClasspathDependencyConstants.CLASSPATH_COMPONENT_DEPENDENCY));
    assertEquals(entry.toString() + " " + IClasspathDependencyConstants.CLASSPATH_COMPONENT_NON_DEPENDENCY, !expectedDeploymentStatus, hasExtraAttribute(entry, IClasspathDependencyConstants.CLASSPATH_COMPONENT_NON_DEPENDENCY));
  }

  protected static IClasspathEntry[] getClassPathEntries(IProject project) throws Exception {
    IJavaProject javaProject = JavaCore.create(project);
    IClasspathContainer container = BuildPathManager.getMaven2ClasspathContainer(javaProject);
    return container.getClasspathEntries();
  }

  protected static IResource[] getUnderlyingResources(IProject project) {
    IVirtualComponent component = ComponentCore.createComponent(project);
    IVirtualFolder root = component.getRootFolder();
    IResource[] underlyingResources = root.getUnderlyingResources();
    return underlyingResources;
  }

  protected static String getAsString(IFile file) throws IOException, CoreException {
    assert file != null;
    assert file.isAccessible();
    InputStream ins = null;
    String content = null;
    try {
      ins = file.getContents();
      content = IOUtil.toString(ins, 1024).replaceAll("\r\n", "\n");
    } finally {
      IOUtil.close(ins);   
    }
    return content;
  }

  protected static String getAsString(File file) throws IOException {
	    assert file != null;
	    assert file.isFile();
	    InputStream ins = null;
	    String content = null;
	    try {
	      ins = new FileInputStream(file);
	      content = IOUtil.toString(ins, 1024).replaceAll("\r\n", "\n");
	    } finally {
	      IOUtil.close(ins);   
	    }
	    return content;
	  }

  
  public AbstractWTPTestCase() {
    super();
  }

  /**
   * Replace the project pom.xml with a new one, triggers new build
   * @param project
   * @param newPomName
   * @throws Exception
   */
  protected void updateProject(IProject project, String newPomName) throws Exception {
    updateProject(project, newPomName, -1);
  }

  /**
   * Replace the project pom.xml with a new one, triggers new build, wait for waitTime milliseconds.
   * @param project
   * @param newPomName
   * @param waitTime
   * @throws Exception
   */
  protected void updateProject(IProject project, String newPomName, int waitTime) throws Exception {    
    
    if (newPomName != null) {
      copyContent(project, newPomName, "pom.xml");
    }
    
    IProjectConfigurationManager configurationManager = MavenPlugin.getDefault().getProjectConfigurationManager();
    ResolverConfiguration configuration = new ResolverConfiguration();
    configurationManager.enableMavenNature(project, configuration, monitor);
    configurationManager.updateProjectConfiguration(project, monitor);
    
    waitForJobsToComplete();
    project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
    if (waitTime > 0 ) {
      Thread.sleep(waitTime);
    }
    waitForJobsToComplete();
  }
  protected void waitForJobsToComplete() throws InterruptedException, CoreException {
	    waitForJobsToComplete(monitor);
  }
  
  public static void waitForJobsToComplete(IProgressMonitor monitor) throws InterruptedException, CoreException {
	  long s = System.currentTimeMillis();
	  AbstractMavenProjectTestCase.waitForJobsToComplete(monitor);
	  long e = System.currentTimeMillis() -s ;
	  System.err.println("Waited for jobs to complete for " + e + " ms");
  }
  
  
  protected void updateProject(IProject project) throws Exception {   
    updateProject(project, null, -1);
  }
  
  protected void assertContains(String findMe, String holder) {
    assertTrue("'" +findMe + "' is missing from : \n" + holder, holder.contains(findMe));
  }
  
  protected void assertNotContains(String findMe, String holder) {
    assertFalse("'" +findMe + "' was found in : \n" + holder, holder.contains(findMe));
  }
  

  protected void useBuildDirforGeneratingFiles(IProject project, boolean b) {
    IMavenWtpPreferences preferences = MavenWtpPlugin.getDefault().getMavenWtpPreferencesManager().getPreferences(project);
    preferences.setApplicationXmGeneratedInBuildDirectory(b);
    preferences.setWebMavenArchiverUsesBuildDirectory(b);
    MavenWtpPlugin.getDefault().getMavenWtpPreferencesManager().savePreferences(preferences, null);
  }

  protected void useBuildDirforGeneratingFiles(boolean b) {
    useBuildDirforGeneratingFiles(null, b);
  }  
  
  protected void checkForErrors(IProject project, String ... ignoredFiles ) throws CoreException {
	   	List<IMarker> markers = findErrorMarkers(project);
	   	if (ignoredFiles != null) {
	   		Iterator<IMarker> ite = markers.iterator();
	   		while (ite.hasNext()) {
	   			IMarker m = ite.next();
	   			for (String fileName : ignoredFiles) {
	   				if (m.getResource().getName().endsWith(fileName)) {
	   					ite.remove();
	   				}
	   			}
	   		}
	   	}
	   	if (!markers.isEmpty()) {
	   		Assert.assertEquals("Unexpected error markers " + toString(markers), 0, markers.size());
	   	}
	}
  
  protected ConfiguratorEnabler getConfiguratorEnabler(String id) {
		for ( ConfiguratorEnabler e : MavenWtpPlugin.getDefault().getMavenWtpPreferencesManager().getConfiguratorEnablers()) {
			if (e.getId().equals(id)) {
				return e;
			}
		}
		fail("ConfiguratorEnabler "+ id + " not found");
		return null;
  }		

  private static boolean checkJavaEe7Compatibility() {
	 String version = System.getProperty("java.specification.version");
	 double javaVersion = Double.parseDouble(version);
	 if (javaVersion  < 1.7) {
		System.err.println("Can't run Java EE 7 tests with Java "+javaVersion);  
		return false;
	 }
	 Version j2eeVersion = Platform.getBundle("org.eclipse.jst.j2ee.web").getVersion();
	 Version threshold = new Version(1, 1, 700);
	 if (j2eeVersion == null || j2eeVersion.compareTo(threshold) < 0) {
	    System.err.println("Can't run Java EE 7 tests with org.eclipse.jst.j2ee.web "+j2eeVersion.toString());  
	 	return false;
	 }
	 return true;
	}
  
  protected void assertArchiveNameAttribute(IClasspathEntry cpe, String expectedValue) {
  	IClasspathAttribute archiveNameAttribute = ClasspathHelpers.getClasspathAttribute(cpe, CLASSPATH_ARCHIVENAME_ATTRIBUTE);
  	assertNotNull(CLASSPATH_ARCHIVENAME_ATTRIBUTE+" is missing", archiveNameAttribute);
  	assertEquals(expectedValue, archiveNameAttribute.getValue());
  }
  
  protected static void assertNoErrors(IProject project) throws CoreException {
	  List<IMarker> markers = findErrorMarkers(project);
	  Iterator<IMarker> ite = markers.iterator(); 
	  while (ite.hasNext()) {
		  IMarker m = ite.next();
		  if ("org.eclipse.wst.xml.core.validationMarker".equals(m.getType())
				  && m.getAttribute(IMarker.MESSAGE).toString().contains("Referenced file contains errors")
				  ) {
			  ite.remove();
		  }
	  }
	  org.junit.Assert.assertEquals("Unexpected error markers " + toString(markers), 0, markers.size());
  }
}
