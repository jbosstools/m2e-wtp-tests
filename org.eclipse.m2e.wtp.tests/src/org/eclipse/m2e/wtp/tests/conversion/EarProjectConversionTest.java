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
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

/**
 * Test EAR project conversion
 * 
 * @author Fred Bricon
 */
public class EarProjectConversionTest extends AbstractWtpProjectConversionTestCase {

	@Override
	protected String getPackagingUnderTest() {
		return "ear";
	}

	@Test
	public void testEar60ProjectConversion() throws Exception {
		// Checks an EAR 6.0 project without application.xml configures the
		// proper <version>
		// and sets <generateApplicationXml> to false
		testProjectConversion("ear60");
	}

	@Test
	public void testEar60WithDD() throws Exception {
		// Checks an EAR 6.0 project with an application.xml configures the
		// proper <version>
		// and doesn't set <generateApplicationXml>, as it's true by default
		testProjectConversion("ear60-dd");
	}

	@Test
	public void testNoCustomizationNeededProjectConversion() throws Exception {
		// Checks an EAR 1.3 project with maven layout produces a minimal
		// pom.xml
		testProjectConversion("ear13");
	}

	@Test
	public void testEarWithoutContentFolder() throws Exception {
		// Checks an EAR without a content folder can be converted and the
		// pom.xml is created.
		testProjectConversion("EARNoContent");
	}

	@Test
	public void testEarWithoutContentFolderWithoutDefaultRootSourceTag() throws Exception {
		// Checks an EAR without a content folder, and without the attribute
		// tag="defaultRootSource
		// in .settings/org.eclipse.wst.common.component can be converted and
		// the pom.xml is created.
		testProjectConversion("EARNoContentNoTag");
	}


	@Test
	public void testEar14WithoutContentFolderWithoutDefaultRootSourceTag() throws Exception {
		// Checks an EAR 1.4 without a content folder, and without the attribute
		// tag="defaultRootSource
		// in .settings/org.eclipse.wst.common.component can be converted and
		// the pom.xml is created.
		testProjectConversion("480163");
	}
	
	@Test
	public void test393611_Ear50ProjectConversion() throws Exception {
		// Checks an EAR 5.0 project configures the proper <version>
		testProjectConversion("ear50");
	}

	@Test
	public void test390931_DefaultLibDirConfiguration() throws Exception {
		// Checks an EAR 1.4 with a weblogic structure configures the proper
		// <defaultLibBundleDir>
		testProjectConversion("weblo");
	}

	@Override
	protected void checkForErrors(IProject project) throws CoreException {
		super.checkForErrors(project, "application.xml");
	}

	@Override
	protected String getOverrideSystemPropertyKey() {
		return "org.eclipse.m2e.wtp.conversion.earplugin.version";
	}

	@Override
	protected String getTestedPluginVersion() {
		return "2.9";
	} 
}
