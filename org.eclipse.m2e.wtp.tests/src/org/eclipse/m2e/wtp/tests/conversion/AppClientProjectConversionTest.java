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
 * Test Application-client project conversion
 *  
 * @author Fred Bricon
 */
public class AppClientProjectConversionTest extends AbstractWtpProjectConversionTestCase {

	@Override
	protected String getPackagingUnderTest() {
		return "app-client";
	}

	@Test
	public void testAppClientProjectConversion() throws Exception {
		testProjectConversion("app-client-1");
	}

	@Test
	public void testMinimalAppClientProjectConversion() throws Exception {
		testProjectConversion("app-client-2");
	}
	

}
