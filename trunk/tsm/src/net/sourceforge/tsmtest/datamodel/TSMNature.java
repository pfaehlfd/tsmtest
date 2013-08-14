/*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Verena Käfer - initial version
 * 
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Verena Käfer
 *
 */
public class TSMNature implements IProjectNature {
    public static final String NATURE_ID = "net.sourceforge.tsmtest.datamodel.TSMNature";

    private IProject project;

    public void configure() throws CoreException {
	// not needed
    }

    public void deconfigure() throws CoreException {
	// not needed
    }

    public IProject getProject() {
	return project;
    }

    public void setProject(IProject project) {
	this.project = project;
    }
}