/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Bernhard Wetzel - added comments
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel.descriptors;

import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;

/**
 * @author Albert Flaig
 *
 */
public class TestStepDescriptor implements ITestStepDescriptor, Cloneable {

    /**
     * Holds the description of the tesnewContentt step as a rich text in html
     * format.
     */
    private String richTextDescription = "";
    /**
     * Holds the expected result of the test step.
     */
    private String expectedResult = "";
    /**
     * Holds the real result of the test case.
     */
    private String realResult = "";
    /**
     * Holds the current status of the newContenttest step.
     */
    private StatusType status = StatusType.notExecuted;
    /**
     * Returns the description of a test case in html format
     */
    public String getRichTextDescription() {
	return richTextDescription;
    }

    public void setRichTextDescription(String richTextDescription) {
	this.richTextDescription = richTextDescription;
    }

    public String getExpectedResult() {
	return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
	this.expectedResult = expectedResult;
    }

    public String getRealResult() {
	return realResult;
    }

    public void setRealResult(String realResult) {
	this.realResult = realResult;
    }

    public StatusType getStatus() {
	return status;
    }

    public void setStatus(StatusType status) {
	this.status = status;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}
