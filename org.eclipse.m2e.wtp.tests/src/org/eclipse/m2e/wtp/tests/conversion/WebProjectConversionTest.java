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

/**
 * Test Dynamic Web project conversion
 *  
 * @author Fred Bricon
 */
public class WebProjectConversionTest extends AbstractWtpProjectConversionTestCase {

	@Override
	protected String getPackagingUnderTest() {
		return "war";
	}


	public void testWeb24ProjectConversion() throws Exception {
		// Checks a servlet 2.4 project with standard Eclipse WebContent folder
		// configures <warSourceDirectory>
		testProjectConversion("web24");
	}
	

	public void testWeb30ProjectConversion() throws Exception {
		// Checks a servlet 3.0 project with standard Eclipse WebContent folder
		// configures <warSourceDirectory> and sets <failOnMissingWebXml> to false
		testProjectConversion("web30");
	}

	public void testNoCustomizationNeededProjectConversion() throws Exception {
		// Checks a servlet 2.4 project with maven layout and Java 1.5 produces
		// a minimal pom.xml
		testProjectConversion("no-customization-needed");
	}
}
