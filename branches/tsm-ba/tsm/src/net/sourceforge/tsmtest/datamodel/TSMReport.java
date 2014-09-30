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
 * 	Wolfgang Kraus - data model refactoring
 * 	Jenny Krüwald - changed use of List to Set
 *
 *******************************************************************************/
/**
 * This class provides temporary test case protocol objects for the view.
 */
package net.sourceforge.tsmtest.datamodel;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.tsmtest.datamodel.descriptors.ITestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;

/**
 * @author Albert Flaig
 *
 */
public class TSMReport extends TSMResource {
    public final static String INITIAL_CONTENT = "<tsmtestcaseprotocol></tsmtestcaseprotocol>";
    private TestCaseDescriptor data;

    protected TSMReport(final String name, final TSMContainer parent,
	    final TestCaseDescriptor data) {
	super(name, parent);
	this.data = data;
    }

    /**
     * Gets the TestCaseDescriptor for the TSMReport.
     * @return The TestCaseDescriptor for the TSMReport.
     */
    public ITestCaseDescriptor getData() {
	return data;
    }

    /**
     * This report may have been changed by another action (e.g. rename). This
     * method returns the newest version of this report.
     * 
     * @return the newest version of this report.
     */
    public TSMReport getNewestVersion() {
	// a tsm report's contents should not be changed anymore, so the
	// only thing to worry about is the report being moved. we need to
	// find out where to.
	return find(getData());
    }

    public TestCaseDescriptor createDataCopy() {
	return data.deepClone();
    }

    protected void setData(final TestCaseDescriptor data) {
	this.data = data;
    }

    public void update(final String newName, final TestCaseDescriptor data)
	    throws DataModelException {
	DataModel.getInstance().updateTestCaseReport(newName, this, data);
    }

    public void update(final TestCaseDescriptor data) throws DataModelException {
	update(getName(), data);
    }

    /**
     * @return Whether this Report is deprecated. A Report is deprecated when
     *         the test case has changed since. Returns false if the
     *         corresponding test case doesn't exist anymore.
     */
    public boolean isDeprecated() {
	final TSMTestCase testCase = getTestCase();
	if (testCase == null) {
	    return false;
	}
	final ITestCaseDescriptor data = testCase.getData();
	if (!data.getRichTextPrecondition().equals(
		getData().getRichTextPrecondition())) {
	    return true;
	}
	if (!data.getShortDescription().equals(getData().getShortDescription())) {
	    return true;
	}
	if (data.getSteps().size() != getData().getSteps().size()) {
	    return true;
	}
	for (int i = 0; i < data.getSteps().size(); i++) {
	    final TestStepDescriptor testStep = data.getSteps().get(i);
	    final TestStepDescriptor testStep2 = getData().getSteps().get(i);
	    if (!testStep.getExpectedResult().equals(
		    testStep2.getExpectedResult())) {
		return true;
	    }
	    if (!testStep.getRichTextDescription().equals(
		    testStep2.getRichTextDescription())) {
		return true;
	    }
	}
	return false;
    }

    public TSMTestCase getTestCase() {
	return DataModel.getInstance().getTestCaseById(data.getId());
    }

    public static Collection<TSMReport> list() {
	return DataModel.getInstance().getReports();
    }

    @Override
    public String getExtension() {
	return DataModelTypes.TSM_REPORT_EXTENSION;
    }

    /**
     * Getter for the default name of a report.
     * @param testCaseName The name of the test case to which the report belongs.
     * @param executionDate The execution date of the test case which is stored in the report.
     * @return The name to be displayed: testCaseName + "_" + executionDate.
     */
    public static String getDefaultName(final String testCaseName,
	    final Date executionDate) {
	final SimpleDateFormat date = new SimpleDateFormat(
		"MM-dd-yyyy_HH-mm-ss");
	final StringBuilder sDate = new StringBuilder(
		date.format(executionDate));
	return testCaseName + "_" + sDate;
    }

    /**
     * Getter for the category for TSMViewerComparator.
     * @return The categories of the TSMReport.
     */
    public static int getCategory() {
	return DataModelTypes.CATEGORY_TSMREPORT;
    }

    /**
     * Gets all testers.
     * @return
     */
    public static String[] getAllTesters() {
	final Set<String> list = DataModel.getInstance().getAllTesters();
	final String[] array = new String[list.size()];
	int i = 0;
	for (final Iterator<String> iterator = list.iterator(); iterator
		.hasNext(); i++) {
	    array[i] = iterator.next();
	}
	return array;
    }

    public static TSMReport find(final ITestCaseDescriptor data) {
	return DataModel.getInstance().getReportByData(data);
    }

}
