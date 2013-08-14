/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Verena Käfer - added lastChangedOn attribute
 * 	Bernhard Wetzel - added revision numbers to execution
 * 	Wolfgang Kraus - fixed addition of empty steps
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel.descriptors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;

/**
 * @author Albert Flaig
 *
 */
public class TestCaseDescriptor implements ITestCaseDescriptor, Cloneable {

    public TestCaseDescriptor() {
	// Default entries
	setPriority(PriorityType.medium);
	setAuthor(System.getProperty("user.name")); //$NON-NLS-1$
	setExpectedDuration("00:00"); //$NON-NLS-1$
	setId(TSMTestCase.generateID(getCreationDate()));
    }

    /**
     * The id of the test case
     */
    private long id;
    /**
     * The author of the test case.
     */
    private String author = "";
    /**
     * Holds the name of the assigned tester.
     */
    private String assignedTo = "";
    /**
     * Holds the current state of the test case.
     */
    private PriorityType priority = PriorityType.low;
    /**
     * Holds the expected duration for the execution of the test case.
     */
    private String expectedDuration = "";
    /**
     * Holds the real duration of the last execution of the test case.
     */
    private String realDuration = "";
    /**
     * Holds the short description of the test case as a rich text in html
     * format.
     */
    private String shortDescription = "";
    /**
     * Holds the precondition of the test case as a rich text in html format.
     */
    private String richTextPrecondition = "";
    /**
     * Holds a list of all test steps.
     */
    private List<TestStepDescriptor> steps = new ArrayList<TestStepDescriptor>();
    /**
     * Holds the number of executions of the test case.
     */
    private int numberOfExecutions = 0;
    /**
     * Holds the number of failures of the test case.
     */
    private int numberOfFailures = 0;
    /**
     * Holds the date of the last execution of the test case.
     */
    private Date lastExecution = null;
    /**
     * Holds the current state of the test case.
     */
    private StatusType status = StatusType.notExecuted;
    /**
     * Holds the creation date of the test case.
     */
    private Date creationDate = new Date();
    /**
     * Holds the result of the test case as a rich text in html format.
     */
    private String richTextResult = "";
    /**
     * Holds the date of the last change
     */
    private Date lastChangedOn = new Date();
    /**
     * Holds the revision of the tested program
     */
    private int revision = 0;

    @Override
    public long getId() {
	return id;
    }

    public void setId(final long id) {
	this.id = id;
    }

    @Override
    public String getAuthor() {
	return author;
    }

    public void setAuthor(final String author) {
	this.author = author;
    }

    @Override
    public String getAssignedTo() {
	return assignedTo;
    }

    public void setAssignedTo(final String assignedTo) {
	this.assignedTo = assignedTo;
    }

    @Override
    public PriorityType getPriority() {
	return priority;
    }

    public void setPriority(final PriorityType priority) {
	this.priority = priority;
    }

    @Override
    public String getExpectedDuration() {
	return expectedDuration;
    }

    public void setExpectedDuration(final String expectedDuration) {
	this.expectedDuration = expectedDuration;
    }

    @Override
    public String getRealDuration() {
	return realDuration;
    }

    public void setRealDuration(final String realDuration) {
	this.realDuration = realDuration;
    }

    @Override
    public String getShortDescription() {
	return shortDescription;
    }

    public void setShortDescription(final String shortDescription) {
	this.shortDescription = shortDescription;
    }

    @Override
    public String getRichTextPrecondition() {
	return richTextPrecondition;
    }

    public void setRichTextPrecondition(final String richTextPrecondition) {
	this.richTextPrecondition = richTextPrecondition;
    }

    @Override
    public List<TestStepDescriptor> getSteps() {
	return steps;
    }

    public void addStep(final TestStepDescriptor step) {
	steps.add(step);
    }

    public void setSteps(final List<TestStepDescriptor> steps) {
	this.steps = steps;
    }

    @Override
    public int getNumberOfExecutions() {
	return numberOfExecutions;
    }

    public void setNumberOfExecutions(final int numberOfExecutions) {
	this.numberOfExecutions = numberOfExecutions;
    }

    public void increaseNumberOfExecutions() {
	numberOfExecutions++;
    }

    @Override
    public int getNumberOfFailures() {
	return numberOfFailures;
    }

    public void setNumberOfFailures(final int numberOfFailures) {
	this.numberOfFailures = numberOfFailures;
    }

    public void increaseNumberOfFailures() {
	numberOfFailures++;
    }

    @Override
    public Date getLastExecution() {
	return lastExecution;
    }

    public void setLastExecution(final Date lastExecution) {
	this.lastExecution = lastExecution;
    }

    @Override
    public StatusType getStatus() {
	return status;
    }

    public void setStatus(final StatusType status) {
	this.status = status;
    }

    @Override
    public Date getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
	this.creationDate = creationDate;
    }

    @Override
    public String getRichTextResult() {
	return richTextResult;
    }

    public void setRichTextResult(final String richTextResult) {
	this.richTextResult = richTextResult;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
	final TestCaseDescriptor clone = (TestCaseDescriptor) super.clone();
	clone.setCreationDate(new Date(creationDate.getTime()));
	if (lastExecution == null) {
	    clone.setLastExecution(null);
	} else {
	    clone.setLastExecution(new Date(lastExecution.getTime()));
	}
	clone.setLastChangedOn(new Date(lastChangedOn.getTime()));
	final List<TestStepDescriptor> newSteps = new ArrayList<TestStepDescriptor>();
	for (final TestStepDescriptor step : steps) {
	    newSteps.add((TestStepDescriptor) step.clone());
	}
	return clone;
    }

    public TestCaseDescriptor deepClone() {
	try {
	    return (TestCaseDescriptor) clone();
	} catch (final CloneNotSupportedException e) {
	    return null;
	}
    }

    @Override
    public int hashCode() {
	int hashCode = getClass().getName().hashCode();
	hashCode += creationDate.hashCode();
	hashCode += expectedDuration.hashCode();
	hashCode += lastChangedOn.hashCode();
	hashCode += lastExecution.hashCode();
	hashCode += numberOfExecutions;
	hashCode += numberOfFailures;
	hashCode += priority.hashCode();
	hashCode += realDuration.hashCode();
	hashCode += richTextPrecondition.hashCode();
	hashCode += richTextResult.hashCode();
	hashCode += shortDescription.hashCode();
	hashCode += status.hashCode();
	for (int i = 0; i < steps.size(); i++) {
	    final TestStepDescriptor testStep = steps.get(i);
	    hashCode += testStep.getExpectedResult().hashCode();
	    hashCode += testStep.getRichTextDescription().hashCode();
	}
	return hashCode;
    }

    @Override
    public boolean equals(final Object other) {
	// Check all attributes
	if (other == null) {
	    return false;
	}
	if (!(other instanceof TestCaseDescriptor)) {
	    return false;
	}
	final TestCaseDescriptor o = (TestCaseDescriptor) other;
	if (!assignedTo.equals(o.assignedTo)) {
	    return false;
	}
	if (!author.equals(o.author)) {
	    return false;
	}
	if (!creationDate.equals(o.creationDate)) {
	    return false;
	}
	if (!expectedDuration.equals(o.expectedDuration)) {
	    return false;
	}
	if (!lastChangedOn.equals(o.lastChangedOn)) {
	    return false;
	}
	if (lastExecution != null ? !lastExecution.equals(o.lastExecution)
		: o.lastExecution != null) {
	    return false;
	}
	if (numberOfExecutions != o.numberOfExecutions) {
	    return false;
	}
	if (numberOfFailures != o.numberOfFailures) {
	    return false;
	}
	if (!priority.equals(o.priority)) {
	    return false;
	}
	if (!realDuration.equals(o.realDuration)) {
	    return false;
	}
	if (!richTextPrecondition.equals(o.richTextPrecondition)) {
	    return false;
	}
	if (!richTextResult.equals(o.richTextResult)) {
	    return false;
	}
	if (!shortDescription.equals(o.shortDescription)) {
	    return false;
	}
	if (!status.equals(o.status)) {
	    return false;
	}
	for (int i = 0; i < steps.size(); i++) {
	    final TestStepDescriptor testStep = steps.get(i);
	    final TestStepDescriptor testStep2 = o.steps.get(i);
	    if (!testStep.getExpectedResult().equals(
		    testStep2.getExpectedResult())) {
		return false;
	    }
	    if (!testStep.getRichTextDescription().equals(
		    testStep2.getRichTextDescription())) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public Date getLastChangedOn() {
	return lastChangedOn;
    }

    public void setLastChangedOn(final Date lastChangedOn) {
	this.lastChangedOn = lastChangedOn;
    }

    /**
     * returns the revision of the tested program
     */
    @Override
    public int getRevisionNumber() {
	return revision;
    }

    /**
     * sets the revision of the tested program
     * 
     * @param revision
     *            version of the tested program
     */
    public void setRevisionNumber(final int revision) {
	this.revision = revision;
    }

}
