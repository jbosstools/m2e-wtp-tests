package org.eclipse.m2e.wtp.tests.extras;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.wtp.jaxrs.internal.MavenJaxRsConstants;
import org.eclipse.m2e.wtp.preferences.ConfiguratorEnabler;
import org.eclipse.m2e.wtp.tests.AbstractWTPTestCase;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.junit.Test;


@SuppressWarnings("restriction")
public class JpaConfiguratorTest extends AbstractWTPTestCase {

	private static final IProjectFacet JPA_FACET = ProjectFacetsManager.getProjectFacet("jpt.jpa");

	private static final IProjectFacetVersion JPA_FACET_VERSION_1_0 = JPA_FACET.getVersion("1.0");

	private static final IProjectFacetVersion JPA_FACET_VERSION_2_0 = JPA_FACET.getVersion("2.0");

	@Test
	public void testSimpleJavaProjects() throws Exception {
		IProject project = importProject( "projects/jpa/simple-2.0/pom.xml");
		waitForJobsToComplete();
		
		assertIsJpaProject(project, JPA_FACET_VERSION_2_0);
		assertNoErrors(project);
		
		project = importProject( "projects/jpa/simple-1.0/pom.xml");
		waitForJobsToComplete();
		assertIsJpaProject(project, JPA_FACET_VERSION_1_0);
		assertNoErrors(project);
		
	}	

	protected void assertIsJpaProject(IProject project, IProjectFacetVersion expectedJpaVersion) throws Exception {
		IFacetedProject facetedProject = ProjectFacetsManager.create(project);
		assertNotNull(project.getName() + " is not a faceted project", facetedProject);
		assertEquals("Unexpected JPA Version", expectedJpaVersion, facetedProject.getInstalledVersion(JPA_FACET));
		assertTrue("Java Facet is missing",	facetedProject.hasProjectFacet(JavaFacet.FACET));
	}
	
	@Test
	public void testMultiModule()  throws Exception {
		IProject[] projects = importProjects("projects/jpa/multi", 
				new String[]{ "pom.xml",
							  "multi-ear/pom.xml",
							  "multi-ejb/pom.xml",
							  "multi-web/pom.xml"}, 
				new ResolverConfiguration());
		waitForJobsToComplete();
		
		IProject pom = projects[0];
		IProject ear = projects[1];
		IProject ejb = projects[2];
		IProject web = projects[3];

		assertNoErrors(pom);
		assertNoErrors(ejb);
		assertNoErrors(ear);
		assertNoErrors(web);
		
		assertIsJpaProject(ejb, JPA_FACET_VERSION_2_0);
	}

	@Test
	public void test399104_disableJpaConfigurator() throws Exception {
		ConfiguratorEnabler enabler = getConfiguratorEnabler("org.eclipse.m2e.wtp.jpa.enabler");
		IProject project;
		try {
			enabler.setEnabled(false);
			project = importProject("projects/jpa/simple-2.0/pom.xml");
			waitForJobsToComplete();
			assertNoErrors(project);
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			assertNull(project.getName() + " shouldn't be a Faceted project ",facetedProject);
		} finally {
			enabler.setEnabled(true);
		}
		updateProject(project);
		assertNoErrors(project);
		assertIsJpaProject(project, JPA_FACET_VERSION_2_0);
	}
	

	@Test
	public void test400577_dontGenerateFiles() throws Exception {
		IProject project = importProject( "projects/jpa/non-default-source/pom.xml");
		waitForJobsToComplete();
		
		assertIsJpaProject(project, JPA_FACET_VERSION_2_0);
		assertNoErrors(project);
		IFile badPersistenceXml =project.getFile("src/main/resources/META-INF/persistence.xml"); 
		assertFalse(badPersistenceXml + " should not have been generated",badPersistenceXml.exists());
	}	
	
	@Test
	public void test406824_forceDisableJpaConfigurator() throws Exception {
		IProject project = importProject("projects/jpa/jpa-disabled/pom.xml");
		waitForJobsToComplete();
		assertNoErrors(project);
		IFacetedProject facetedProject = ProjectFacetsManager.create(project);
		assertNull(project.getName() + " should not be a faceted project", facetedProject);
	}
	
	
	@Test
	public void test406824_forceEnableJpaConfigurator() throws Exception {
		ConfiguratorEnabler enabler = getConfiguratorEnabler("org.eclipse.m2e.wtp.jpa.enabler");
		IProject project;
		try {
			enabler.setEnabled(false);
			project = importProject("projects/jpa/jpa-enabled/pom.xml");
			waitForJobsToComplete();
			assertNoErrors(project);
			assertIsJpaProject(project, JPA_FACET_VERSION_2_0);
		} finally {
			enabler.setEnabled(true);
		}
	}
	
}
