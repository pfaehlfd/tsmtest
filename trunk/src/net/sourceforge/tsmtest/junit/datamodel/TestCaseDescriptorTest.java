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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;

import org.junit.Test;

/**
 * @author Tobias Hirning
 *
 */
public class TestCaseDescriptorTest {

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
	testTestCaseDescriptor.setAuthor("äöüß ÄÖÜß");
	assertEquals("äöüß ÄÖÜß", testTestCaseDescriptor.getAuthor());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setAuthor(java.lang.String)}.
     */
    @Test
    public final void testSetAuthor() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setAuthor("äöüß ÄÖÜß");
	assertEquals("äöüß ÄÖÜß", testTestCaseDescriptor.getAuthor());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getAssignedTo()}.
     */
    @Test
    public final void testGetAssignedTo() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setAssignedTo("äöüß ÄÖÜß");
	assertEquals("äöüß ÄÖÜß", testTestCaseDescriptor.getAssignedTo());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setAssignedTo(java.lang.String)}.
     */
    @Test
    public final void testSetAssignedTo() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setAssignedTo("äöüß ÄÖÜß");
	assertEquals("äöüß ÄÖÜß", testTestCaseDescriptor.getAssignedTo());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getPriority()}.
     */
    @Test
    public final void testGetPriority() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(DataModelTypes.PriorityType.low, testTestCaseDescriptor.getPriority());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setPriority(net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType)}.
     */
    @Test
    public final void testSetPriority() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(DataModelTypes.PriorityType.low, testTestCaseDescriptor.getPriority());
	testTestCaseDescriptor.setPriority(DataModelTypes.PriorityType.medium);
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
	assertEquals("", testTestCaseDescriptor.getExpectedDuration());
	testTestCaseDescriptor.setExpectedDuration("99:99");
	assertEquals("99:99", testTestCaseDescriptor.getExpectedDuration());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setExpectedDuration(java.lang.String)}.
     */
    @Test
    public final void testSetExpectedDuration() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setExpectedDuration("99:99");
	assertEquals("99:99", testTestCaseDescriptor.getExpectedDuration());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getRealDuration()}.
     */
    @Test
    public final void testGetRealDuration() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals("", testTestCaseDescriptor.getRealDuration());
	testTestCaseDescriptor.setRealDuration("99:99");
	assertEquals("99:99", testTestCaseDescriptor.getRealDuration());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setRealDuration(java.lang.String)}.
     */
    @Test
    public final void testSetRealDuration() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setRealDuration("99:99");
	assertEquals("99:99", testTestCaseDescriptor.getRealDuration());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getShortDescription()}.
     */
    @Test
    public final void testGetShortDescription() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals("", testTestCaseDescriptor.getShortDescription());
	testTestCaseDescriptor.setShortDescription("äöüß ÄÖÜß");
	assertEquals("äöüß ÄÖÜß", testTestCaseDescriptor.getShortDescription());	
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setShortDescription(java.lang.String)}.
     */
    @Test
    public final void testSetShortDescription() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setShortDescription("äöüß ÄÖÜß");
	assertEquals("äöüß ÄÖÜß", testTestCaseDescriptor.getShortDescription());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#getRichTextPrecondition()}.
     */
    @Test
    public final void testGetRichTextPrecondition() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals("", testTestCaseDescriptor.getRichTextPrecondition());
	testTestCaseDescriptor.setRichTextPrecondition("äöüß ÄÖÜß");
	assertEquals("äöüß ÄÖÜß", testTestCaseDescriptor.getRichTextPrecondition());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setRichTextPrecondition(java.lang.String)}.
     */
    @Test
    public final void testSetRichTextPrecondition() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setRichTextPrecondition("äöüß ÄÖÜß");
	assertEquals("äöüß ÄÖÜß", testTestCaseDescriptor.getRichTextPrecondition());
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
	testTestCaseDescriptor.setNumberOfExecutions(2147483647);
	assertEquals(2147483647, testTestCaseDescriptor.getNumberOfExecutions());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setNumberOfExecutions(int)}.
     */
    @Test
    public final void testSetNumberOfExecutions() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(0, testTestCaseDescriptor.getNumberOfExecutions());
	testTestCaseDescriptor.setNumberOfExecutions(2147483647);
	assertEquals(2147483647, testTestCaseDescriptor.getNumberOfExecutions());
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
	testTestCaseDescriptor.setNumberOfFailures(2147483647);
	assertEquals(2147483647, testTestCaseDescriptor.getNumberOfFailures());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setNumberOfFailures(int)}.
     */
    @Test
    public final void testSetNumberOfFailures() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	assertEquals(0, testTestCaseDescriptor.getNumberOfFailures());
	testTestCaseDescriptor.setNumberOfFailures(2147483647);
	assertEquals(2147483647, testTestCaseDescriptor.getNumberOfFailures());
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
	testTestCaseDescriptor.setRichTextResult("äöüß ÄÖÜß");
	assertEquals("äöüß ÄÖÜß", testTestCaseDescriptor.getRichTextResult());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#setRichTextResult(java.lang.String)}.
     */
    @Test
    public final void testSetRichTextResult() {
	TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();
	testTestCaseDescriptor.setRichTextResult("äöüß ÄÖÜß");
	assertEquals("äöüß ÄÖÜß", testTestCaseDescriptor.getRichTextResult());
    }

    /**
     * Test method for {@link net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor#equals(java.lang.Object)}.
     */
    @Test
    public final void testEqualsObject() {
	// TODO
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
