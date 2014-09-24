/*******************************************************************************
 * Copyright (c) 2012-2013 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Tobias Hirning - initial version
 * 	Verena Käfer - added methods, some refactoring
 * 	Albert Flaig - data model refactoring, various fixes
 * 	Wolfgang Kraus - data model refactoring, various fixes
 * 	Daniel Hertl - added debug code
 * 	Jenny Krüwald - changed use of List to Set
 *
 *******************************************************************************/
/**
 * This class provides temporary test case objects for the view.
 */
package net.sourceforge.tsmtest.datamodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.tsmtest.datamodel.descriptors.ITestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;

/**
 * @author Albert Flaig
 *
 */
public class TSMTestCase extends TSMResource {
    private static final DateFormat durationFormat = createDateFormat();
    private TestCaseDescriptor data;

    protected TSMTestCase(final String name, final TSMContainer parent,
	    final TestCaseDescriptor data) {
	super(name, parent);
	this.data = data;
    }

    /**
     * Creates the DateFormat object for the duration.
     * @return an DateFormat object with the format "HH:mm".
     */
    private static DateFormat createDateFormat() {
	final DateFormat durationFormat = new SimpleDateFormat("HH:mm");
	durationFormat.setLenient(false);
	return durationFormat;
    }

    public ITestCaseDescriptor getData() {
	return data;
    }

    /**
     * This test case may have been changed by another action (e.g. rename).
     * This method returns the newest version of this test case.
     * 
     * @return the newest version of this test case
     */
    public TSMTestCase getNewestVersion() {
	// we get the id of the current input and search for it in the
	// refreshed data model. This only works for TSMTestCase.
	return find(getData().getId());
    }

    public TestCaseDescriptor createDataCopy() {
	return data.deepClone();
    }

    protected void setData(final TestCaseDescriptor data)
	    throws DataModelException {
	this.data = data;
    }

    public TSMTestCase update(final String newName,
	    final TestCaseDescriptor data) throws DataModelException {
	return DataModel.getInstance().updateTestCase(newName, this, data);
    }

    public TSMTestCase update(final TestCaseDescriptor data)
	    throws DataModelException {
	return update(getName(), data);
    }

    public Collection<TSMReport> getReports() {
	return DataModel.getInstance().getReportOfTestCase(getData().getId());
    }

    public TSMReport createReport(final TestCaseDescriptor newReport)
	    throws DataModelException {
	final String name = TSMReport.getDefaultName(getName(),
		newReport.getLastExecution());
	return DataModel.getInstance().createReport(name, newReport, this);
    }

    public static Collection<TSMTestCase> list() {
	return DataModel.getInstance().getTestCases();
    }

    public static TSMTestCase find(final long id) {
	return DataModel.getInstance().getTestCaseById(id);
    }

    @Override
    public String getExtension() {
	return DataModelTypes.TSM_TEST_CASE_EXTENSION;
    }

    public static int getCategory() {
	return 1;
    }

    /**
     * @param creationDate The date of the creation of test case.
     * @return A random and unique ID for a test case.
     */
    public static long generateID(final Date creationDate) {
	return creationDate.getTime()
		+ (long) (Math.random() * Integer.MAX_VALUE);
    }

    /**
     * @return An array of Strings with the name of all creators of test cases.
     */
    public static String[] getAllCreators() {
	final Set<String> list = DataModel.getInstance().getAllCreators();
	final String[] array = new String[list.size()];
	int i = 0;
	for (final Iterator<String> iterator = list.iterator(); iterator
		.hasNext(); i++) {
	    array[i] = iterator.next();
	}
	return array;
    }
    
    /**
     * @return an DateFormat object with the format "HH:mm".
     */
    public static synchronized DateFormat getDurationFormat() {
	return durationFormat;
    }
}
