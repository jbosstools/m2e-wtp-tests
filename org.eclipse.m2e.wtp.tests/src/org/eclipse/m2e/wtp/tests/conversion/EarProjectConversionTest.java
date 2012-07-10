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

import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * Test EJB project conversion
 *  
 * @author Fred Bricon
 */
public class EarProjectConversionTest extends AbstractWtpProjectConversionTestCase {

	@Override
	protected String getPackagingUnderTest() {
		return "ear";
	}

	public void testEar60ProjectConversion() throws Exception {
		//Checks an EAR 6.0 project without application.xml configures the proper <version> 
		//and sets <generateApplicationXml> to false 
		testProjectConversion("ear60");
	}
	
	public void testEar60WithDD() throws Exception {
		//Checks an EAR 6.0 project with an application.xml configures the proper <version> 
		//and doesn't set <generateApplicationXml>, as it's true by default 
		testProjectConversion("ear60-dd");
	}

	public void testNoCustomizationNeededProjectConversion() throws Exception {
		// Checks an EAR 1.3 project with maven layout produces a minimal pom.xml
		testProjectConversion("ear13");
	}
	
    protected void checkForErrors(IProject project) throws CoreException {
	   	List<IMarker> markers = findErrorMarkers(project);
	   	Iterator<IMarker> ite = markers.iterator();
	   	while (ite.hasNext()) {
	   		IMarker m = ite.next();
	   		String msg = m.getAttribute(IMarker.MESSAGE, null);
	   		//Ignore application.xml validation error as the test EAR projects are incomplete
	   		if (msg.startsWith("IWAE0053E")) {
	   			ite.remove();
	   		}
	   	}
	   	if (!markers.isEmpty()) {
	   		Assert.assertEquals("Unexpected error markers " + toString(markers), 0, markers.size());
	   	}
	}

}
