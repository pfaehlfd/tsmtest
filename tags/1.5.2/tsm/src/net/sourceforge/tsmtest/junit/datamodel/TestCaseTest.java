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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.TSMProject;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;

import org.junit.Test;

/**
 * @author Tobias Hirning
 * 
 */
public class TestCaseTest {

    @Test
    public final void test() {
	try {
	    // We need a project to create a TestCase.
	    TSMProject testProject = TSMProject.create("testProject", null);
	    // Create a TestCaseDescriptor with some content to add to the model
	    TestCaseDescriptor testTestCaseDescriptor = new TestCaseDescriptor();

	    Date testDate = new Date();
	    testTestCaseDescriptor.setAssignedTo("äöüß ÄÖÜ");
	    testTestCaseDescriptor.setAuthor("äöüß ÄÖÜ");
	    testTestCaseDescriptor.setCreationDate(testDate);
	    testTestCaseDescriptor.setExpectedDuration("99:99");
	    testTestCaseDescriptor.setId(9999999);
	    testTestCaseDescriptor.setLastExecution(testDate);
	    testTestCaseDescriptor.setNumberOfExecutions(Integer.MAX_VALUE);
	    testTestCaseDescriptor.setNumberOfFailures(Integer.MAX_VALUE);
	    testTestCaseDescriptor
		    .setPriority(DataModelTypes.PriorityType.high);
	    testTestCaseDescriptor.setRealDuration("99:99");
	    testTestCaseDescriptor.setRichTextPrecondition("äöüß ÄÖÜ");
	    testTestCaseDescriptor.setRichTextResult("äöüß ÄÖÜ");
	    testTestCaseDescriptor.setShortDescription("äöüß ÄÖÜ");
	    testTestCaseDescriptor
		    .setStatus(DataModelTypes.StatusType.passedWithAnnotation);

	    List<TestStepDescriptor> testList = new ArrayList<TestStepDescriptor>();
	    TestStepDescriptor testStepDescriptor = new TestStepDescriptor();
	    testStepDescriptor.setExpectedResult("äöüß ÄÖÜ");
	    testStepDescriptor.setRealResult("äöüß ÄÖÜ");
	    testStepDescriptor.setActionRichText("äöüß ÄÖÜ");
	    testStepDescriptor
		    .setStatus(DataModelTypes.StatusType.passedWithAnnotation);
	    TestStepDescriptor testStepDescriptor2 = new TestStepDescriptor();
	    TestStepDescriptor testStepDescriptor3 = new TestStepDescriptor();
	    testList.add(testStepDescriptor);
	    testList.add(testStepDescriptor2);
	    testList.add(testStepDescriptor3);

	    testTestCaseDescriptor.setSteps(testList);

	    // Now we first create the TestCase
	    testProject.createTestCase("testTestCase", testTestCaseDescriptor);
	    
	    // Get the created TestCase from the model
	    TSMTestCase testTestCase = testProject.getTestCases().get(0);
	    
	    // Check if the whole content was set correctly.
	    assertEquals("äöüß ÄÖÜ", testTestCase.getData().getAssignedTo());
	    assertEquals("äöüß ÄÖÜ", testTestCase.getData().getAuthor());
	    assertEquals(testDate.getTime(), testTestCase.getData().getCreationDate().getTime());
	    assertEquals("99:99", testTestCase.getData().getExpectedDuration());
	    assertEquals(9999999, testTestCase.getData().getId());
	    assertEquals(testDate.getTime(), testTestCase.getData().getLastExecution().getTime());
	    assertEquals(Integer.MAX_VALUE, testTestCase.getData()
		    .getNumberOfExecutions());
	    assertEquals(Integer.MAX_VALUE, testTestCase.getData().getNumberOfFailures());
	    assertEquals(DataModelTypes.PriorityType.high, testTestCase
		    .getData().getPriority());
	    assertEquals("99:99", testTestCase.getData().getRealDuration());
	    assertEquals("äöüß ÄÖÜ", testTestCase.getData()
		    .getRichTextPrecondition());
	    assertEquals("äöüß ÄÖÜ", testTestCase.getData().getRichTextResult());
	    assertEquals("äöüß ÄÖÜ", testTestCase.getData()
		    .getShortDescription());
	    assertEquals(DataModelTypes.StatusType.passedWithAnnotation,
		    testTestCase.getData().getStatus());
	    
	    List<TestStepDescriptor> testList2 = testTestCase.getData().getSteps();
	    assertTrue(testList != null);
	    TestStepDescriptor getTestStep = testList2.get(0);
	    assertEquals("äöüß ÄÖÜ", getTestStep.getExpectedResult());
	    assertEquals("äöüß ÄÖÜ", getTestStep.getRealResult());
	    assertEquals("äöüß ÄÖÜ", getTestStep.getActionRichText());
	    assertEquals(DataModelTypes.StatusType.passedWithAnnotation, getTestStep.getStatus());
	    
	    TestStepDescriptor getTestStep2 = testList2.get(1);
	    assertEquals("", getTestStep2.getExpectedResult());
	    assertEquals("", getTestStep2.getRealResult());
	    assertEquals("", getTestStep2.getActionRichText());
	    assertEquals(DataModelTypes.StatusType.notExecuted, getTestStep2.getStatus());
	    
	    TestStepDescriptor getTestStep3 = testList2.get(2);
	    assertEquals("", getTestStep3.getExpectedResult());
	    assertEquals("", getTestStep3.getRealResult());
	    assertEquals("", getTestStep3.getActionRichText());
	    assertEquals(DataModelTypes.StatusType.notExecuted, getTestStep3.getStatus());
	    

	} catch (DataModelException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

}
