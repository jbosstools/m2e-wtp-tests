package org.maven.ide.eclipse.wtp.overlay.modulecore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.componentcore.internal.flat.FlatVirtualComponent;
import org.eclipse.wst.common.componentcore.internal.flat.IFlatFile;
import org.eclipse.wst.common.componentcore.internal.flat.IFlatFolder;
import org.eclipse.wst.common.componentcore.internal.flat.IFlatResource;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualFile;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

/**
 * Virtual folder mapping a FlatVirtualComponent
 * 
 * @author Fred Bricon
 */
@SuppressWarnings("restriction")
public class CompositeVirtualFolder implements IVirtualFolder {

	private FlatVirtualComponent flatVirtualComponent;
	private IPath runtimePath;
	private IProject project;
	
	public CompositeVirtualFolder(FlatVirtualComponent aFlatVirtualComponent, IPath aRuntimePath) {
		this.flatVirtualComponent = aFlatVirtualComponent;
		if (flatVirtualComponent != null && flatVirtualComponent.getComponent() != null) {
			project = flatVirtualComponent.getComponent().getProject();
		}
		this.runtimePath = aRuntimePath;
	}

	public IProject getProject() {
		return project;
	}

	public IPath getRuntimePath() {
		return runtimePath;
	}

	public IVirtualResource[] members() throws CoreException {	 
		System.out.println("Getting members from "+project.getName());
		IFlatResource[] flatResources = flatVirtualComponent.fetchResources();
		List<IVirtualResource> members = new ArrayList<IVirtualResource>(flatResources.length);
		for (IFlatResource flatResource : flatResources) {
			IVirtualResource resource = convert(flatResource);
			if (resource != null) {
				members.add(resource);	
			}
		}
		IVirtualResource[] result = new IVirtualResource[members.size()];
		members.toArray(result);
		System.out.println("-------------------");
		return result;
	}

	private IVirtualResource convert(IFlatResource flatResource) {
		// TODO How do I convert an IFlatResource into a IVirtualResource ??
		IVirtualResource virtualResource = null;
		if (flatResource instanceof IFlatFolder) {
			//TODO extract convert method to a utility class?
			virtualResource = convertFolder((IFlatFolder) flatResource);
		} else if (flatResource instanceof IFlatFile){
			virtualResource = convertFile((IFlatFile) flatResource);
		}
		return virtualResource;
	}

	private IVirtualFolder convertFolder(IFlatFolder flatFolder) {
		//Do we need to go recursive? Please no!!! for (IFlatResource flatResource  : flatFolder.members()) { convert(...)};
		IFlatResource[] flatMembers = flatFolder.members();
		List<IVirtualResource> membersList = new ArrayList<IVirtualResource>(flatMembers.length);
		for (IFlatResource flatResource : flatMembers) {
			IVirtualResource resource = convert(flatResource);
			if (resource != null) {
				membersList.add(resource);	
			}
		}
		final IVirtualResource[] folderMembers = new IVirtualResource[membersList.size()];
		membersList.toArray(folderMembers);
		VirtualFolder vf = new VirtualFolder(project, flatFolder.getModuleRelativePath().append(flatFolder.getName())) {
			@Override
			public IVirtualResource[] members() throws CoreException {
				return folderMembers; 
			}
		}; 
		return vf;
		
	}

	private IVirtualFile convertFile(IFlatFile flatFile) {
		IFile f = (IFile)flatFile.getAdapter(IFile.class);
		VirtualFile vf = null;
		if (f == null) {
			File underlyingFile = (File)flatFile.getAdapter(File.class);
			//How do we get external files? 
			System.err.println("need to get " + underlyingFile);
		} else {
			vf = new VirtualFile(project, flatFile.getModuleRelativePath(), f);
		}
		return vf;
	}
	
	public void create(int arg0, IProgressMonitor arg1) throws CoreException {
		// TODO Auto-generated method stub
	}

	public boolean exists(IPath arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public IVirtualResource findMember(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualResource findMember(IPath arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualResource findMember(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualResource findMember(IPath arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualFile getFile(IPath arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualFile getFile(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualFolder getFolder(IPath arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualFolder getFolder(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualResource[] getResources(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualResource[] members(int arg0) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public void createLink(IPath arg0, int arg1, IProgressMonitor arg2)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void delete(int arg0, IProgressMonitor arg1) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public IVirtualComponent getComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFileExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualContainer getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPath getProjectRelativePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResourceType() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IResource getUnderlyingResource() {
		// TODO Auto-generated method stub
		return null;
	}

	public IResource[] getUnderlyingResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPath getWorkspaceRelativePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAccessible() {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeLink(IPath arg0, int arg1, IProgressMonitor arg2)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void setResourceType(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean contains(ISchedulingRule rule) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isConflicting(ISchedulingRule rule) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public IContainer getUnderlyingFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	public IContainer[] getUnderlyingFolders() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
