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

import org.eclipse.core.resources.IProject;
import org.eclipse.jst.j2ee.web.project.facet.WebFacetUtils;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.junit.Test;

/**
 * Test Dynamic Web project conversion
 *  
 * @author Fred Bricon
 */
public class WebProjectConversionTest extends AbstractWtpProjectConversionTestCase {

	private static final String OVERRIDE_WARPLUGIN_VERSION_KEY = "org.eclipse.m2e.wtp.conversion.warplugin.version";

	@Override
	protected String getPackagingUnderTest() {
		return "war";
	}

	@Test
	public void testWeb24ProjectConversion() throws Exception {
		// Checks a servlet 2.4 project with standard Eclipse WebContent folder
		// configures <warSourceDirectory>
		testProjectConversion("web24");
	}
	

	@Test
	public void testWeb30ProjectConversion() throws Exception {
		// Checks a servlet 3.0 project with standard Eclipse WebContent folder
		// configures <warSourceDirectory> and sets <failOnMissingWebXml> to false
		testProjectConversion("web30");
	}

	@Test
	public void testWeb31ProjectConversion() throws Exception {
		// Checks a servlet 3.1 project with standard Eclipse WebContent folder
		// configures <warSourceDirectory> and sets <failOnMissingWebXml> to false
		IProject web = testProjectConversion("web31");
		IFacetedProject fWeb = ProjectFacetsManager.create(web);
		assertEquals("Unexpected web facet version;", "3.1", fWeb.getProjectFacetVersion(WebFacetUtils.WEB_FACET).getVersionString());
	}

	@Test
	public void testWeb31NoWebXmlProjectConversion() throws Exception {
		// Checks a servlet 3.1 project without xml keeps its original facet after conversion
		IProject web = testProjectConversion("web31-nowebxml");
		IFacetedProject fWeb = ProjectFacetsManager.create(web);
		assertEquals("Unexpected web facet version;", "3.1", fWeb.getProjectFacetVersion(WebFacetUtils.WEB_FACET).getVersionString());
	}

	@Test
	public void testNoCustomizationNeededProjectConversion() throws Exception {
		// Checks a servlet 2.4 project with maven layout and Java 1.5 produces
		// a minimal pom.xml
		testProjectConversion("no-customization-needed");
	}
	
	@Override
	protected String getOverrideSystemPropertyKey() {
		return OVERRIDE_WARPLUGIN_VERSION_KEY;
	}
	
	@Override
	protected String getTestedPluginVersion() {
		//Lock compiler version so that m2e-wtp evolutions don't bite us
		return "2.4";
	}
}
