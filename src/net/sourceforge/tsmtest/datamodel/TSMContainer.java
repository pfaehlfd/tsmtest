/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Tobias Hirning - some refactoring 	
 * 	Wolfang Kraus - data model refactoring
 * 	Verena Käfer - fix for loading xml files
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;

/**
 * @author Albert Flaig
 *
 */
public abstract class TSMContainer extends TSMResource {

    List<TSMResource> children = new ArrayList<TSMResource>();

    protected TSMContainer(final String name, final TSMContainer parent) {
	super(name, parent);
    }

    public synchronized TSMPackage createPackage(final String name)
	    throws DataModelException {
	return DataModel.getInstance().createPackage(name, this);
    }

    public synchronized TSMTestCase createTestCase(final String name,
	    final TestCaseDescriptor testCase) throws DataModelException {
	return DataModel.getInstance().createTestCase(name, testCase, this);
    }

    protected void add(final TSMResource resource) {
	children.add(resource);
    }

    protected void remove(final TSMResource resource) {
	children.remove(resource);
    }

    public List<TSMTestCase> getTestCases() {
	return DataModel.getInstance().getTestCases(this);
    }

    public List<TSMPackage> getPackages() {
	return DataModel.getInstance().getPackages(this);
    }

    public List<TSMReport> getReports() {
	return DataModel.getInstance().getReports(this);
    }

    public List<TSMResource> getChildren() {
	return DataModel.getInstance().getChildren(this, true);
    }

    public List<TSMResource> getChildrenNoReports() {
	return DataModel.getInstance().getChildren(this, false);
    }

    public void paste(final ArrayList<TSMResource> filesToCopy,
	    final boolean copy) throws DataModelException {
	DataModel.getInstance().pasteFiles(filesToCopy, this, copy);
    }
}
