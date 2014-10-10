/*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer, Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Tobias Hirning - initial version
 * 	Verena Käfer - initial version
 * 	Albert Flaig - some fixes, data model refactoring
 *
 *******************************************************************************/
/**
 * This class provides the types PriorityType and StatusType.
 */
package net.sourceforge.tsmtest.datamodel;

import java.text.SimpleDateFormat;

/**
 * @author Verena Käfer
 * @author Tobias Hirning
 * 
 */
public class DataModelTypes {
    /**
     * Defines the possible priorities for a test case. Don't change the order!
     */
    public enum PriorityType {
	low, medium, high;
    };

    /**
     * Defines the possible states for a test case or test step.
     */
    public enum StatusType {
	passed, passedWithAnnotation, failed, notExecuted
    };

    /**
     * The ID of the contentType of a test case file.
     */
    public static final String CONTENT_TYPE_ID_TESTCASE = "net.sourceforge.tsmtest.contenttype.testcase";
    //Categories for TSMViewerComparator.
    static final int CATEGORY_TSMTESTCASE = 1;
    static final int CATEGORY_TSMREPORT = 2;
    static final int CATEGORY_TSMPACKAGE = 3;
    static final int CATEGORY_TSMPROJECT = 4;

    /**
     * The ID of the protocol.
     */
    public static final String CONTENT_TYPE_ID_PROTOCOL = "net.sourceforge.tsmtest.contenttype.testcaseprotocol";
    /**
     * The project nature for Eclipse.
     */
    public static final String TSM_NATURE = "net.sourceforge.tsmtest.datamodel.TSMNature";
    /**
     * The file name extension for test cases.
     */
    public static final String TSM_TEST_CASE_EXTENSION = ".xml";
    /**
     * The file name extension for reports.
     */
    public static final String TSM_REPORT_EXTENSION = ".xml";
    
    /**
     * Global date format for durations and dates of last execution.
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
	    "yyyy-MM-dd, HH:mm:ss");

    /**
     * Folder name for the images.
     */
    public static final String imageFolderName = "images";
    
    /**
     * Maximum length for names (projects, packets, test cases, file names, ...)
     */
    public static final int NAME_MAX_LENGTH = 200;

    /**
     * @param file
     * @return -1 if file type was not detected, the corresponding
     *         <code>getCategory()</code>-value else.
     */
    public static int getCategory(final Object file) {
	if (file instanceof TSMTestCase) {
	    return TSMTestCase.getCategory();
	} else if (file instanceof TSMReport) {
	    return TSMReport.getCategory();
	} else if (file instanceof TSMPackage) {
	    return TSMPackage.getCategory();
	} else if (file instanceof TSMProject) {
	    return TSMProject.getCategory();
	}
	return -1;
    }
    
    /**
     * @return the date format for the data model.
     */
    public static synchronized SimpleDateFormat getDateFormat() {
	return dateFormat;
    }
}
