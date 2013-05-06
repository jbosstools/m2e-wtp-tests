/*************************************************************************************
 * Copyright (c) 2011-2013 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Fred Bricon (Red Hat, Inc.) - initial API and implementation
 ************************************************************************************/
package org.eclipse.m2e.wtp.tests.facets;

import org.eclipse.core.resources.IProject;
import org.eclipse.jst.j2ee.web.project.facet.WebFacetUtils;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.wtp.facets.FacetDetectorManager;
import org.eclipse.m2e.wtp.tests.AbstractWTPTestCase;

public class FacetDetectorManagerTest extends AbstractWTPTestCase {

	public void testFacetDetectorManagerTest() throws Exception {
		IProject[] projects = importProjects("projects/407315/", new String[]{"detect-my-name/pom.xml", "maven/pom.xml"}, new ResolverConfiguration());
		
		FacetDetectorManager facetDetectorManager = FacetDetectorManager.getInstance();
		//TestFacetDetector2 satisfies both detectors. First one by priority 
		//(org.eclipse.m2e.wtp.tests.facets.TestFacetDetector2) must win
		IMavenProjectRegistry registry = MavenPlugin.getMavenProjectRegistry();
		
		assertEquals(WebFacetUtils.WEB_22, facetDetectorManager.findFacetVersion(registry.getProject(projects[0]), 
				                           "foo.bar", 
				                           monitor));
		//maven only satisfies the 2nd detector
		assertEquals(WebFacetUtils.WEB_30, facetDetectorManager.findFacetVersion(registry.getProject(projects[1]), 
				                           "foo.bar", 
				                           monitor));
	}
}
