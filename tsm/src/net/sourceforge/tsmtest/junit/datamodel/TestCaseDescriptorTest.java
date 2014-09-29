 /*******************************************************************************
 * Copyright (c) 2012-2013 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Hirning - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.junit.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;

import org.junit.Test;

/**
 * @author Tobias Hirning
 *
 */
public class TestCaseDescriptorTest {

    private static final String MAXIMUM_DURATION = "99:99";
    private static final String GERMAN_UMLAUTS = "äöüß ÄÖÜß";

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getId()}.
     */
    @Test
    public final void testGetId() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setId(999999999);
	assertEquals(999999999, testTestCaseDescriptor.getId());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setId(long)}.
     */
    @Test
    public final void testSetId() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setId(999999999);
	assertEquals(999999999, testTestCaseDescriptor.getId());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getAuthor()}.
     */
    @Test
    public final void testGetAuthor() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setAuthor(GERMAN_UMLAUTS);
	assertEquals(GERMAN_UMLAUTS, testTestCaseDescriptor.getAuthor());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setAuthor(java.lang.String)}.
     */
    @Test
    public final void testSetAuthor() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setAuthor(GERMAN_UMLAUTS);
	assertEquals(GERMAN_UMLAUTS, testTestCaseDescriptor.getAuthor());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getAssignedTo()}.
     */
    @Test
    public final void testGetAssignedTo() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setAssignedTo(GERMAN_UMLAUTS);
	assertEquals(GERMAN_UMLAUTS, testTestCaseDescriptor.getAssignedTo());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setAssignedTo(java.lang.String)}.
     */
    @Test
    public final void testSetAssignedTo() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setAssignedTo(GERMAN_UMLAUTS);
	assertEquals(GERMAN_UMLAUTS, testTestCaseDescriptor.getAssignedTo());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getPriority()}.
     */
    @Test
    public final void testGetPriority() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(DataModelTypes.PriorityType.medium, testTestCaseDescriptor.getPriority());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setPriority(net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType)}.
     */
    @Test
    public final void testSetPriority() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(DataModelTypes.PriorityType.medium, testTestCaseDescriptor.getPriority());
	testTestCaseDescriptor.setPriority(DataModelTypes.PriorityType.high);
	assertEquals(DataModelTypes.PriorityType.high, testTestCaseDescriptor.getPriority());
	testTestCaseDescriptor.setPriority(DataModelTypes.PriorityType.low);
	assertEquals(DataModelTypes.PriorityType.low, testTestCaseDescriptor.getPriority());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getExpectedDuration()}.
     */
    @Test
    public final void testGetExpectedDuration() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals("00:00", testTestCaseDescriptor.getExpectedDuration());
	testTestCaseDescriptor.setExpectedDuration(MAXIMUM_DURATION);
	assertEquals(MAXIMUM_DURATION, testTestCaseDescriptor.getExpectedDuration());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setExpectedDuration(java.lang.String)}.
     */
    @Test
    public final void testSetExpectedDuration() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setExpectedDuration(MAXIMUM_DURATION);
	assertEquals(MAXIMUM_DURATION, testTestCaseDescriptor.getExpectedDuration());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getRealDuration()}.
     */
    @Test
    public final void testGetRealDuration() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals("", testTestCaseDescriptor.getRealDuration());
	testTestCaseDescriptor.setRealDuration(MAXIMUM_DURATION);
	assertEquals(MAXIMUM_DURATION, testTestCaseDescriptor.getRealDuration());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setRealDuration(java.lang.String)}.
     */
    @Test
    public final void testSetRealDuration() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setRealDuration(MAXIMUM_DURATION);
	assertEquals(MAXIMUM_DURATION, testTestCaseDescriptor.getRealDuration());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getShortDescription()}.
     */
    @Test
    public final void testGetShortDescription() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals("", testTestCaseDescriptor.getShortDescription());
	testTestCaseDescriptor.setShortDescription(GERMAN_UMLAUTS);
	assertEquals(GERMAN_UMLAUTS, testTestCaseDescriptor.getShortDescription());	
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setShortDescription(java.lang.String)}.
     */
    @Test
    public final void testSetShortDescription() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setShortDescription(GERMAN_UMLAUTS);
	assertEquals(GERMAN_UMLAUTS, testTestCaseDescriptor.getShortDescription());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getRichTextPrecondition()}.
     */
    @Test
    public final void testGetRichTextPrecondition() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals("", testTestCaseDescriptor.getRichTextPrecondition());
	testTestCaseDescriptor.setRichTextPrecondition(GERMAN_UMLAUTS);
	assertEquals(GERMAN_UMLAUTS, testTestCaseDescriptor.getRichTextPrecondition());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setRichTextPrecondition(java.lang.String)}.
     */
    @Test
    public final void testSetRichTextPrecondition() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setRichTextPrecondition(GERMAN_UMLAUTS);
	assertEquals(GERMAN_UMLAUTS, testTestCaseDescriptor.getRichTextPrecondition());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getSteps()}.
     */
    @Test
    public final void testGetSteps() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();
	testTestCaseDescriptor.addStep(testTestStepDescriptor);
	assertEquals(1, testTestCaseDescriptor.getSteps().size());
	assertEquals(testTestStepDescriptor, testTestCaseDescriptor.getSteps().get(0));
	
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#addStep(net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor)}.
     */
    @Test
    public final void testAddStep() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();
	TestStepDescriptor testTestStepDescriptor2 = new TestStepDescriptor();
	TestStepDescriptor testTestStepDescriptor3 = new TestStepDescriptor();
	testTestCaseDescriptor.addStep(testTestStepDescriptor);
	testTestCaseDescriptor.addStep(testTestStepDescriptor2);
	testTestCaseDescriptor.addStep(testTestStepDescriptor3);
	assertEquals(3, testTestCaseDescriptor.getSteps().size());
	assertEquals(testTestStepDescriptor, testTestCaseDescriptor.getSteps().get(0));
	assertEquals(testTestStepDescriptor2, testTestCaseDescriptor.getSteps().get(1));
	assertEquals(testTestStepDescriptor3, testTestCaseDescriptor.getSteps().get(2));
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setSteps(java.util.List)}.
     */
    @Test
    public final void testSetSteps() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();
	TestStepDescriptor testTestStepDescriptor2 = new TestStepDescriptor();
	TestStepDescriptor testTestStepDescriptor3 = new TestStepDescriptor();
	List<TestStepDescriptor> testStepList = new ArrayList<TestStepDescriptor>();
	testStepList.add(testTestStepDescriptor);
	testStepList.add(testTestStepDescriptor2);
	testStepList.add(testTestStepDescriptor3);
	
	testTestCaseDescriptor.setSteps(testStepList);
	assertEquals(3, testTestCaseDescriptor.getSteps().size());
	assertEquals(testTestStepDescriptor, testTestCaseDescriptor.getSteps().get(0));
	assertEquals(testTestStepDescriptor2, testTestCaseDescriptor.getSteps().get(1));
	assertEquals(testTestStepDescriptor3, testTestCaseDescriptor.getSteps().get(2));	
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getNumberOfExecutions()}.
     */
    @Test
    public final void testGetNumberOfExecutions() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(0, testTestCaseDescriptor.getNumberOfExecutions());
	testTestCaseDescriptor.setNumberOfExecutions(Integer.MAX_VALUE);
	assertEquals(Integer.MAX_VALUE, testTestCaseDescriptor.getNumberOfExecutions());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setNumberOfExecutions(int)}.
     */
    @Test
    public final void testSetNumberOfExecutions() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(0, testTestCaseDescriptor.getNumberOfExecutions());
	testTestCaseDescriptor.setNumberOfExecutions(Integer.MAX_VALUE);
	assertEquals(Integer.MAX_VALUE, testTestCaseDescriptor.getNumberOfExecutions());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#increaseNumberOfExecutions()}.
     */
    @Test
    public final void testIncreaseNumberOfExecutions() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(0, testTestCaseDescriptor.getNumberOfExecutions());
	testTestCaseDescriptor.increaseNumberOfExecutions();
	assertEquals(1, testTestCaseDescriptor.getNumberOfExecutions());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getNumberOfFailures()}.
     */
    @Test
    public final void testGetNumberOfFailures() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(0, testTestCaseDescriptor.getNumberOfFailures());
	testTestCaseDescriptor.setNumberOfFailures(Integer.MAX_VALUE);
	assertEquals(Integer.MAX_VALUE, testTestCaseDescriptor.getNumberOfFailures());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setNumberOfFailures(int)}.
     */
    @Test
    public final void testSetNumberOfFailures() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(0, testTestCaseDescriptor.getNumberOfFailures());
	testTestCaseDescriptor.setNumberOfFailures(Integer.MAX_VALUE);
	assertEquals(Integer.MAX_VALUE, testTestCaseDescriptor.getNumberOfFailures());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#increaseNumberOfFailures()}.
     */
    @Test
    public final void testIncreaseNumberOfFailures() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(0, testTestCaseDescriptor.getNumberOfFailures());
	testTestCaseDescriptor.increaseNumberOfFailures();
	assertEquals(1, testTestCaseDescriptor.getNumberOfFailures());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getLastExecution()}.
     */
    @Test
    public final void testGetLastExecution() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	Date testDate = new Date();
	testTestCaseDescriptor.setLastExecution(testDate);
	assertEquals(testDate, testTestCaseDescriptor.getLastExecution());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setLastExecution(java.util.Date)}.
     */
    @Test
    public final void testSetLastExecution() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	Date testDate = new Date();
	testTestCaseDescriptor.setLastExecution(testDate);
	assertEquals(testDate, testTestCaseDescriptor.getLastExecution());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getStatus()}.
     */
    @Test
    public final void testGetStatus() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(DataModelTypes.StatusType.notExecuted, testTestCaseDescriptor.getStatus());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setStatus(net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType)}.
     */
    @Test
    public final void testSetStatus() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(DataModelTypes.StatusType.notExecuted, testTestCaseDescriptor.getStatus());
	testTestCaseDescriptor.setStatus(DataModelTypes.StatusType.failed);
	assertEquals(DataModelTypes.StatusType.failed, testTestCaseDescriptor.getStatus());
	testTestCaseDescriptor.setStatus(DataModelTypes.StatusType.passedWithAnnotation);
	assertEquals(DataModelTypes.StatusType.passedWithAnnotation, testTestCaseDescriptor.getStatus());
	testTestCaseDescriptor.setStatus(DataModelTypes.StatusType.passed);
	assertEquals(DataModelTypes.StatusType.passed, testTestCaseDescriptor.getStatus());
	testTestCaseDescriptor.setStatus(DataModelTypes.StatusType.failed);
	assertEquals(DataModelTypes.StatusType.failed, testTestCaseDescriptor.getStatus());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getCreationDate()}.
     */
    @Test
    public final void testGetCreationDate() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	Date testDate = new Date();
	testTestCaseDescriptor.setCreationDate(testDate);
	assertEquals(testDate, testTestCaseDescriptor.getCreationDate());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setCreationDate(java.util.Date)}.
     */
    @Test
    public final void testSetCreationDate() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	Date testDate = new Date();
	testTestCaseDescriptor.setCreationDate(testDate);
	assertEquals(testDate, testTestCaseDescriptor.getCreationDate());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getRichTextResult()}.
     */
    @Test
    public final void testGetRichTextResult() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals("", testTestCaseDescriptor.getRichTextResult());
	testTestCaseDescriptor.setRichTextResult(GERMAN_UMLAUTS);
	assertEquals(GERMAN_UMLAUTS, testTestCaseDescriptor.getRichTextResult());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setRichTextResult(java.lang.String)}.
     */
    @Test
    public final void testSetRichTextResult() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setRichTextResult(GERMAN_UMLAUTS);
	assertEquals(GERMAN_UMLAUTS, testTestCaseDescriptor.getRichTextResult());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#equals(java.lang.Object)}.
     */
    @Test
    public final void testEqualsObject() {
	TestCaseDescriptor testTestCaseDescriptor1 = new TestCaseDescriptor();
	TestCaseDescriptor testTestCaseDescriptor2 = new TestCaseDescriptor();
	testTestCaseDescriptor1.setAssignedTo(GERMAN_UMLAUTS);
	testTestCaseDescriptor2.setAssignedTo(GERMAN_UMLAUTS);
	
	testTestCaseDescriptor1.setAuthor(GERMAN_UMLAUTS);
	testTestCaseDescriptor2.setAuthor(GERMAN_UMLAUTS);
	
	Date testDateCreationDate = new Date();
	testTestCaseDescriptor1.setCreationDate(testDateCreationDate);
	testTestCaseDescriptor2.setCreationDate(testDateCreationDate);
	
	testTestCaseDescriptor1.setExpectedDuration(MAXIMUM_DURATION);
	testTestCaseDescriptor2.setExpectedDuration(MAXIMUM_DURATION);
	
	Date testDateLastChangedOn = new Date();
	testTestCaseDescriptor1.setLastChangedOn(testDateLastChangedOn);
	testTestCaseDescriptor2.setLastChangedOn(testDateLastChangedOn);
	
	Date testDateLastExecution = new Date();
	testTestCaseDescriptor1.setLastExecution(testDateLastExecution);
	testTestCaseDescriptor2.setLastExecution(testDateLastExecution);
	
	testTestCaseDescriptor1.setNumberOfExecutions(Integer.MAX_VALUE);
	testTestCaseDescriptor2.setNumberOfExecutions(Integer.MAX_VALUE);
	
	testTestCaseDescriptor1.setNumberOfFailures(Integer.MAX_VALUE);
	testTestCaseDescriptor2.setNumberOfFailures(Integer.MAX_VALUE);
	
	testTestCaseDescriptor1.setPriority(PriorityType.medium);
	testTestCaseDescriptor2.setPriority(PriorityType.medium);
	
	testTestCaseDescriptor1.setRealDuration(MAXIMUM_DURATION);
	testTestCaseDescriptor2.setRealDuration(MAXIMUM_DURATION);
	
	testTestCaseDescriptor1.setRichTextResult(GERMAN_UMLAUTS);
	testTestCaseDescriptor2.setRichTextResult(GERMAN_UMLAUTS);
	
	testTestCaseDescriptor1.setShortDescription(GERMAN_UMLAUTS);
	testTestCaseDescriptor2.setShortDescription(GERMAN_UMLAUTS);
	
	testTestCaseDescriptor1.setStatus(StatusType.passedWithAnnotation);
	testTestCaseDescriptor2.setStatus(StatusType.passedWithAnnotation);
	
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();
	testTestStepDescriptor.setExpectedResult(GERMAN_UMLAUTS);
	testTestStepDescriptor.setRealResult(GERMAN_UMLAUTS);
	testTestStepDescriptor.setRichTextDescription(GERMAN_UMLAUTS);
	testTestStepDescriptor.setStatus(StatusType.failed);
	testTestCaseDescriptor1.addStep(testTestStepDescriptor);
	testTestCaseDescriptor2.addStep(testTestStepDescriptor);
	
	assertTrue(testTestCaseDescriptor1.equals(testTestCaseDescriptor2));
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#clone()}.
     */
    @Test
    public final void testClone() {
	// TODO
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#deepClone()}.
     */
    @Test
    public final void testDeepClone() {
	// TODO
    }

}
