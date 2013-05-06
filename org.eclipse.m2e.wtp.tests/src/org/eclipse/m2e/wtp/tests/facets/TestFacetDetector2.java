package org.eclipse.m2e.wtp.tests.facets;

import java.util.Map;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.j2ee.web.project.facet.WebFacetUtils;
import org.eclipse.m2e.wtp.facets.AbstractFacetDetector;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public class TestFacetDetector2 extends AbstractFacetDetector {

	@Override
	public IProjectFacetVersion findFacetVersion(IProject project,
			MavenProject mavenProject, Map<?, ?> context,
			IProgressMonitor monitor) throws CoreException {
		if ("detect-my-name".equals(project.getName())) {
			return WebFacetUtils.WEB_22;
		}
		return null;
	}

}
