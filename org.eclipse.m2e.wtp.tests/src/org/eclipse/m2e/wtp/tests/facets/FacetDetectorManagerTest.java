package org.eclipse.m2e.wtp.tests.facets;

import org.eclipse.core.resources.IProject;
import org.eclipse.jst.j2ee.web.project.facet.WebFacetUtils;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.wtp.facets.FacetDetectorManager;
import org.eclipse.m2e.wtp.tests.AbstractWTPTestCase;

public class FacetDetectorManagerTest extends AbstractWTPTestCase {

	public void testFacetDetectorManagerTest() throws Exception {
		IProject[] projects = importProjects("projects/407315/", new String[]{"detect-my-name/pom.xml", "maven/pom.xml"}, new ResolverConfiguration());
		
		FacetDetectorManager facetDetectorManager = FacetDetectorManager.getInstance();
		//TestFacetDetector2 satisfies both detectors. First one by priority 
		//(org.eclipse.m2e.wtp.tests.facets.TestFacetDetector2) must win
		assertEquals(WebFacetUtils.WEB_22, facetDetectorManager.findFacetVersion(projects[0], null, "foo.bar", monitor));
		//maven only satisfies the 2nd detector
		assertEquals(WebFacetUtils.WEB_30, facetDetectorManager.findFacetVersion(projects[1], null, "foo.bar", monitor));
	}
}
