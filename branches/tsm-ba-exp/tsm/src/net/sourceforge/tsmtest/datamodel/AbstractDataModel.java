/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Verena Käfer - worked on progress bar
 * 	Jenny Krüwald - changed usage from List to Set
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.sourceforge.tsmtest.datamodel.descriptors.ITestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;

import org.eclipse.ui.IWorkingSet;

/**
 * @author Albert Flaig
 *
 */
public abstract class AbstractDataModel {

    /**
     * Must be implemented to receive data model updates.
     */
    public interface DataModelObservable {
	public void dataModelChanged();
    }

    /**
     * When disabled the data model wont track file system changes anymore.
     * Should only be disabled when chaining an event of file resource changes
     * and be enabled afterwards.
     * 
     * @param enabled
     */
    public abstract void setEnabled(boolean enabled);

    /**
     * Refreshes the DataModel
     * 
     * @throws DataModelException
     *             In the case something went wrong
     */
    public abstract void refresh() throws DataModelException;

    protected abstract TSMPackage createPackage(String name,
	    TSMContainer container) throws DataModelException;

    protected abstract TSMTestCase createTestCase(String name,
	    TestCaseDescriptor testCase, TSMContainer container)
	    throws DataModelException;

    protected abstract List<TSMTestCase> getTestCases(TSMContainer container);

    protected abstract List<TSMPackage> getPackages(TSMContainer container);

    protected abstract List<TSMReport> getReports(TSMContainer container);

    protected abstract List<TSMResource> getChildren(TSMContainer container,
	    boolean includeReports);

    protected abstract Collection<TSMPackage> getPackages();

    /**
     * Creates a new project, initializing the default data
     * 
     * @param name
     *            Name of the new project
     * @param workingSets
     *            Optional association to a working set
     * @return the created project
     * @throws DataModelException
     */
    protected abstract TSMProject createProject(String name,
	    IWorkingSet[] workingSets) throws DataModelException;

    /**
     * Returns all values of the projects-map
     * 
     * @return projects
     */
    protected abstract Collection<TSMProject> getProjects();

    protected abstract TSMReport updateTestCaseReport(String name,
	    TSMReport testCaseProtocol, TestCaseDescriptor data)
	    throws DataModelException;

    /**
     * @param file
     *            usually an IResource depending on the implementation.
     * @return
     */
    public abstract TSMResource convertToTSMResource(Object file);

    protected abstract TSMTestCase getTestCaseById(long id);

    /**
     * Gets all testers.
     * @return A set with all testers.
     */
    protected abstract Set<String> getAllTesters();

    protected abstract Collection<TSMReport> getReports();

    protected abstract void rename(TSMResource tsmResource, String name)
	    throws DataModelException;

    protected abstract boolean exists(TSMResource tsmResource);

    protected abstract TSMTestCase updateTestCase(String newName,
	    TSMTestCase tsmTestCase, TestCaseDescriptor data)
	    throws DataModelException;

    protected abstract Collection<TSMReport> getReportOfTestCase(long id);

    protected abstract TSMReport createReport(String name,
	    TestCaseDescriptor newReport, TSMTestCase tsmTestCase)
	    throws DataModelException;

    protected abstract Collection<TSMTestCase> getTestCases();

    protected abstract TSMReport getReportByData(ITestCaseDescriptor data);

    /**
     * @return a Set of Strings with the name of all creators of test cases.
     */
    protected abstract Set<String> getAllCreators();

    protected abstract void pasteFiles(ArrayList<TSMResource> filesToCopy,
	    TSMContainer destination, boolean copy) throws DataModelException;

    public abstract void register(DataModelObservable observable);

    protected abstract void delete(TSMResource tsmResource)
	    throws DataModelException;

    public abstract void unregister(DataModelObservable listener);
}