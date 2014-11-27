 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Bernhard Wetzel - added comments
 *******************************************************************************/
package net.sourceforge.tsmtest;

import net.sourceforge.tsmtest.datamodel.DataModelTypes;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Albert Flaig
 *
 */
public class TSMImageContentFilter extends ViewerFilter {

    @Override
    public boolean select(final Viewer viewer, final Object parentElement,
	    final Object element) {
	// no IFolder -> return true
	if (element instanceof IFolder) {
	    final IFolder folder = (IFolder) element;
	    if (folder.getName().startsWith(".")) {
		return false;
	    }
	    // no imageFolder -> return true
	    if (folder.getName().equals(DataModelTypes.imageFolderName)) {
		// no IProject -> return true
		if (folder.getParent() instanceof IProject) {
		    final IProject project = (IProject) folder.getParent();
		    try {
			// if projectNature found -> return false
			if (project.getNature(DataModelTypes.TSM_NATURE) != null) {
			    return false;
			}
		    } catch (final CoreException e) {
			return true;
		    }
		}
	    }
	}
	return true;
    }

}
