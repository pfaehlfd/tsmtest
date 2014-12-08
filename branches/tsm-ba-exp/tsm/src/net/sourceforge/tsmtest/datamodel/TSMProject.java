/*******************************************************************************
 * Copyright (c) 2012-2013 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Tobias Hirning - initial version
 * 	Verena Käfer - added methods for protocol support, some refactoring
 * 	Albert Flaig - data model refactoring, some fixes
 *
 *******************************************************************************/
/**
 * This class provides temporary project objects for the view.
 */
package net.sourceforge.tsmtest.datamodel;

import java.util.Collection;

import org.eclipse.ui.IWorkingSet;


/**
 * @author Albert Flaig
 *
 */
public class TSMProject extends TSMContainer {

    /**
     * Shows whether the project is marked as finished manually.
     */
    private boolean isFinished = false;
    
    protected TSMProject(String name) {
	super(name, null);
    }
    
    /**
     * @return True, if the project is marked as finished. False, otherwise.
     */
    public boolean isFinished() {
	return isFinished;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.tsmtest.datamodel.TSMResource#getProject()
     */
    public TSMProject getProject() {
	return this;
    }
    
    /**
     * @param isFinished
     *            Set whether the project should be marked as finished.
     */
    public void setFinished(boolean isFinished) {
	this.isFinished = isFinished;
	// FIXME Should save a meta information that this project is finished
    }
    
    /**
     * Creates a TSMProject.
     * @param name Name of the new project.
     * @param workingSets Optional association to a working set
     * @return The created project.
     * @throws DataModelException
     */
    public static TSMProject create(String name, IWorkingSet[] workingSets) throws DataModelException {
	return DataModel.getInstance().createProject(name, workingSets);
    }
    
    /**
     * @return A collection of TSMProjects.
     */
    public static Collection<TSMProject> list() {
	return DataModel.getInstance().getProjects();
    }

    /**
     * Getter for the category for TSMViewerComparator.
     * @return The categories of the TSMProject.
     */
    public static int getCategory() {
	return DataModelTypes.CATEGORY_TSMPROJECT;
    }
    
//    public TSMPackage findPackage(String path) {
//	return DataModel.getInstance().findPackage(this, path);
//    }
}
