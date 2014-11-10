/*******************************************************************************
 * Copyright (c) 2012-2013 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Tobias Hirning - initial version
 * 	Verena Käfer - added new exception for forbidden characters
 * 	Bernhard Wetzel - updated exceptions and added comments
 * 	Albert Flaig - data model refactoring
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;

/**
 * @author Tobias Hirning
 * 
 */
public class DataModelException extends Exception {
    private static final long serialVersionUID = 709217598599419212L;

    public static final String TESTSTEPOBJECT_NULL = Messages.DataModelException_0;
    public static final String RICHTEXTDESCRIPTION_NULL = Messages.DataModelException_1;
    public static final String RICHTEXTDESCRIPTION_NULL_EMPTY = Messages.DataModelException_2;
    public static final String RICHTEXTDESCRIPTION_TOO_LONG = Messages.DataModelException_3;
    public static final String TESTCASEOBJECT_NULL = Messages.DataModelException_4;
    public static final String ASSIGNEDTO_NULL = Messages.DataModelException_5;
    public static final String AUTHOR_NULL_EMPTY = Messages.DataModelException_6;
    public static final String CREATIONDATE_NULL = Messages.DataModelException_7;
    public static final String EXPECTEDDURATION_NULL = Messages.DataModelException_8;
    public static final String LASTEXECUTION_NULL = Messages.DataModelException_9;
    public static final String NUMBEROFEXECUTIONS_NULL = Messages.DataModelException_10;
    public static final String NUMBEROFFAILURES_NULL = Messages.DataModelException_11;
    public static final String PRIORITY_NULL = Messages.DataModelException_12;
    public static final String REALDURATION_NULL = Messages.DataModelException_13;
    public static final String REAL_DURATION_NOT_VALID = Messages.DataModelException_38;
    public static final String REAL_DURATION_EMPTY  = Messages.DataModelException_39;
    public static final String RICHTEXTPRECONDITION_NULL = Messages.DataModelException_14;
    public static final String RICHTEXTRESULT_NULL = Messages.DataModelException_15;
    public static final String SHORTDESCRIPTION_NULL = Messages.DataModelException_16;
    public static final String STATUS_NULL = Messages.DataModelException_17;
    public static final String PROJECTOBJECT_NULL = Messages.DataModelException_18;
    public static final String NAME_NULL_EMPTY = Messages.DataModelException_19;
    public static final String NAME_TOO_LONG = Messages.DataModelException_20;
    public static final String NAME_CONTAINS_WRONG_CHARACTER = Messages.DataModelException_21;
    public static final String IPROJECT_NULL = Messages.DataModelException_22;
    public static final String IFOLDER_NULL = Messages.DataModelException_23;
    public static final String PACKAGEOBJECT_NULL = Messages.DataModelException_24;
    public static final String ADD_TESTSTEP_FAILED = Messages.DataModelException_25;
    public static final String TESTCASE_NULL = Messages.DataModelException_26;
    public static final String TESTSTEP_NULL = Messages.DataModelException_27;
    public static final String PACKAGE_NULL = Messages.DataModelException_28;
    public static final String PROJECT_NULL = Messages.DataModelException_29;
    public static final String TESTER_NULL = Messages.DataModelException_30;
    public static final String RESOURCE_RENAME_ERROR = Messages.DataModelException_31;
    public static final String RESOURCE_NOT_FOUND_ERROR = Messages.DataModelException_32;
    public static final String PROJECT_CREATION_ERROR = Messages.DataModelException_33;
    public static final String TESTCASE_CREATION_ERROR = Messages.DataModelException_34;

    public static final String TESTCASEPROTOCOL_CREATION_ERROR = Messages.DataModelException_35;

    public static final String PACKAGE_CREATION_ERROR = Messages.DataModelException_36;
    public static final String TESTCASE_NOTEXECTUTED = Messages.DataModelException_37;

    /**
     * not used
     */
    public DataModelException() {
	// TODO Auto-generated constructor stub
    }

    /**
     * Displays the exceptions
     * @param arg0
     */
    public DataModelException(String arg0) {
	super(arg0);
	// TODO Auto-generated constructor stub
    }

    /**
     * Displays the throws
     * @param arg0
     */
    public DataModelException(Throwable arg0) {
	super(arg0);
	// TODO Auto-generated constructor stub
    }

    /**
     * Displays the exceptions + throws
     * @param arg0
     * @param arg1
     */
    public DataModelException(String arg0, Throwable arg1) {
	super(arg0, arg1);
	// TODO Auto-generated constructor stub
    }

    /**
     * Checks whether the right fields are set in the test case
     * 
     * @param testCase
     * @throws DataModelException
     *             if no name set, if name too long, if no author is set, if no
     *             creation date is set, if short description too long, if name
     *             contains colons or if testCase is null
     */
    public static void verifyTestCase(String name, TestCaseDescriptor testCase)
	    throws DataModelException {
	if (testCase == null) {
	    throw new DataModelException(DataModelException.TESTCASEOBJECT_NULL);
	} else if (name == null || name.isEmpty()) {
	    throw new DataModelException(DataModelException.NAME_NULL_EMPTY);
	} else if (name.length() > DataModelTypes.NAME_MAX_LENGTH) {
	    throw new DataModelException(DataModelException.NAME_TOO_LONG);
	} else if (testCase.getAuthor() == null) {
	    throw new DataModelException(DataModelException.AUTHOR_NULL_EMPTY);
	} else if (testCase.getCreationDate() == null) {
	    throw new DataModelException(DataModelException.CREATIONDATE_NULL);
	} else if (name.contains("<")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains(">")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("?")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("\"")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains(":")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("|")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("\\")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("/")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("*")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	}
    }

    /**
     * Checks whether the right fields are set in the package
     * 
     * @param packageObject
     * @throws DataModelException
     *             if no name set, if name too long, if name contains colons or
     *             if p is null.
     */
    public static void verifyPackage(String packageName)
	    throws DataModelException {
	if (packageName == null) {
	    throw new DataModelException(DataModelException.PROJECTOBJECT_NULL);
	} else if (packageName == null || packageName.isEmpty()) {
	    throw new DataModelException(DataModelException.NAME_NULL_EMPTY);
	} else if (packageName.length() > DataModelTypes.NAME_MAX_LENGTH) {
	    throw new DataModelException(DataModelException.NAME_TOO_LONG);
	} else if (packageName.contains("<")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (packageName.contains(">")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (packageName.contains("?")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (packageName.contains(":")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (packageName.contains("*")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (packageName.contains("\"")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (packageName.contains("\\")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (packageName.contains("/")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (packageName.contains("|")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	}
    }

    /**
     * Checks whether the right fields are set in the project
     * 
     * @param projectName
     * @throws DataModelException
     *             if no name set, if name too long, if name contains colons or
     *             if p is null.
     */
    public static void verifyProject(String projectName)
	    throws DataModelException {
	if (projectName == null) {
	    throw new DataModelException(DataModelException.PROJECTOBJECT_NULL);
	}
	if (projectName == null || projectName.isEmpty()) {
	    throw new DataModelException(DataModelException.NAME_NULL_EMPTY);
	} else if (projectName.length() > DataModelTypes.NAME_MAX_LENGTH) {
	    throw new DataModelException(DataModelException.NAME_TOO_LONG);
	} else if (projectName.contains("<")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (projectName.contains(">")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (projectName.contains("?")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (projectName.contains(":")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (projectName.contains("|")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (projectName.contains("*")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (projectName.contains("\"")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (projectName.contains("\\")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (projectName.contains("/")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	}
    }

    /**
     * Checks whether the right fields are set in the test step
     * 
     * @param testStep
     * @throws DataModelException
     *             if the argument or the rich text description is null or the
     *             rich text description too long.
     */
    public static void verifyTestStep(TestStepDescriptor testStep)
	    throws DataModelException {
	if (testStep == null) {
	    throw new DataModelException(DataModelException.TESTCASEOBJECT_NULL);
	} else if (testStep.getRichTextDescription() == null) {
	    throw new DataModelException(
		    DataModelException.RICHTEXTDESCRIPTION_NULL);
	}
	if (testStep.getRichTextDescription().length() > 500) {
	    throw new DataModelException(
		    DataModelException.RICHTEXTDESCRIPTION_TOO_LONG);
	}
    }

    /**
     * Checks whether the right fields are set in the test case
     * 
     * @param TSMReport
     * @throws DataModelException
     *             if no name set, if name too long, if no author is set, if no
     *             creation date is set, if short description too long, if name
     *             contains colons or if t is null
     */
    public static void verifyReport(String name, TestCaseDescriptor testCase)
	    throws DataModelException {
	if (testCase == null) {
	    throw new DataModelException(DataModelException.TESTCASEOBJECT_NULL);
	} else if (name == null || name.isEmpty()) {
	    throw new DataModelException(DataModelException.NAME_NULL_EMPTY);
	}
	// We check explicit for 220 characters as a report has the name of the
	// test case and the date of the creation.
	else if (name.length() > 220) {
	    throw new DataModelException(DataModelException.NAME_TOO_LONG);
	} else if (testCase.getAuthor() == null) {
	    throw new DataModelException(DataModelException.AUTHOR_NULL_EMPTY);
	} else if (testCase.getCreationDate() == null) {
	    throw new DataModelException(DataModelException.CREATIONDATE_NULL);
	} else if (name.contains("<")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains(">")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("?")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("\"")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains(":")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("|")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("\\")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("/")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	} else if (name.contains("*")) { //$NON-NLS-1$
	    throw new DataModelException(
		    DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	}
    }

    /**
     * Verifies that a given duration is between 00:00:00 and 23:59:59.
     * @param duration Duration to check.
     * @throws DataModelException If duration is not null, empty or not valid (hh:mm:ss format).
     */
    public static void verifyDuration(String duration) throws DataModelException {
	if (duration == null) {
	    throw new DataModelException(DataModelException.REALDURATION_NULL);
	}
	if (duration.isEmpty()) {
	    throw new DataModelException(DataModelException.REAL_DURATION_EMPTY);
	}
	//Split duration into hours, minutes and seconds.
	final String[] splittedDuration = duration.split(":"); //$NON-NLS-1$

	//Duration needs to have three fields.
	if (splittedDuration.length == 3) {
	    //Hours
	    if (splittedDuration[0].matches("\\b0?([0-9]|1[0-9]|2[0-3])\\b")) {
		//Minutes
		if (splittedDuration[1].matches("\\b0?([0-9]|[1-5][0-9])\\b")) {
		    //Seconds
		    if (splittedDuration[2].matches("\\b0?([0-9]|[1-5][0-9])\\b")) {
			//Everything was fine, so we can return.
			return;
		    } else {
			throw new DataModelException(DataModelException.REAL_DURATION_NOT_VALID);
		    }
		} else {
		    throw new DataModelException(DataModelException.REAL_DURATION_NOT_VALID);
		}
	    } else {
		throw new DataModelException(DataModelException.REAL_DURATION_NOT_VALID);
	    }
	} else {
	    throw new DataModelException(DataModelException.REAL_DURATION_NOT_VALID);
	}
    }
}
