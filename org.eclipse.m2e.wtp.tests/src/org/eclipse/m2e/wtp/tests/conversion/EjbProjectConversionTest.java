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

import org.junit.Test;

/**
 * Test EJB project conversion
 *  
 * @author Fred Bricon
 */
public class EjbProjectConversionTest extends AbstractWtpProjectConversionTestCase {

	@Override
	protected String getPackagingUnderTest() {
		return "ejb";
	}


	@Test
	public void testEjb31ProjectConversion() throws Exception {
		testProjectConversion("ejb31");
	}
	
	@Test
	public void testNoCustomizationNeededProjectConversion() throws Exception {
		// Checks an EJB 2.1 project with maven layout and Java 1.5 produces
		// a minimal pom.xml
		testProjectConversion("ejb21");
	}	
}
