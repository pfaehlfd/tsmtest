 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Verena Käfer - added lastChangedOn attribute
 *    Bernhard Wetzel - added revision numbers to execution
 *******************************************************************************/ 
package net.sourceforge.tsmtest.datamodel.descriptors;

import java.util.Date;
import java.util.List;

import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;

/**
 * @author Albert Flaig
 *
 */
public interface ITestCaseDescriptor {
    public long getId();
    /**
     * Gets the name of the tester.
     * @return The name of the assigned tester.
     */
    public String getAssignedTo();
    /**
     * Gets the author of the test case.
     * @return The name of the autor of the test case.
     */
    public String getAuthor();
    /**
     * Gets the creation date.
     * @return The date of the creation.
     */
    public Date getCreationDate();
    /**
     * Get the expected duration of the test case.
     * @return The expected duration.
     */
    public String getExpectedDuration();
    /**
     * Gets the last execution on date of the test case.
     * @return The date for last changed on.
     */
    public Date getLastExecution();
    /**
     * Gets the number of executions of the test case.
     * @return The number of executions the test case was already executed.
     */
    public int getNumberOfExecutions();
    /**
     * Gets the number of failues of the test cases.
     * @return The number of times the execution of the test case failed.
     */
    public int getNumberOfFailures();
    /**
     * Gets the priority of the test case.
     * @return The priority of the test case.
     */
    public PriorityType getPriority();
    /**
     * Gets the real duration of the test case.
     * @return The real duration.
     */
    public String getRealDuration();
    /**
     * Gets the rich text precondition of the test case.
     * @return The precondition of the test case as rich text.
     */
    public String getRichTextPrecondition();
    /**
     * Gets the rich text result of the test case.
     * @return The result of the test case as rich text.
     */
    public String getRichTextResult();
    /**
     * Gets the short description of the test case.
     * @return The short description of the test case.
     */
    public String getShortDescription();
    /**
     * Gets the status of the test case.
     * @return The status of the test case.
     */
    public StatusType getStatus();
    /**
     * Gets the test steps of the test case.
     * @return The test steps of the test case as a list.
     */
    public List<TestStepDescriptor> getSteps();
    /**
     * Get the date last changed on of the test case..
     * @return The last changed on date of the test case.
     */
    public Date getLastChangedOn();
    /**
     * Gets the revision number of the test case.
     * @return The revision number of the test case.
     */
    public int getRevisionNumber();
}
