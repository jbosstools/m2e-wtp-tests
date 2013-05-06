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

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.j2ee.web.project.facet.WebFacetUtils;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.wtp.facets.AbstractFacetDetector;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public class TestFacetDetector1 extends AbstractFacetDetector {

	@Override
	public IProjectFacetVersion findFacetVersion(
			IMavenProjectFacade mavenProjectFacade, Map<?, ?> context,
			IProgressMonitor monitor) throws CoreException {
		IProject project = mavenProjectFacade.getProject();
		if (project == null) {
			return null;
		}
		if (project.hasNature(IMavenConstants.NATURE_ID)) {
			return WebFacetUtils.WEB_30;
		}
		return null;
	}

}
