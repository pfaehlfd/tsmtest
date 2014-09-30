/*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Verena Käfer - initial version
 * 	Albert Flaig - various fixes, data model refactoring
 * 	Tobias Hirning - some refactoring
 * 	Wolfang Kraus - various fixes
 * 	Bernhard Wetzel - some fixes, added comments
 *
 *******************************************************************************/
/**
 * This class implements the loading methods
 */
package net.sourceforge.tsmtest.datamodel;

import java.util.Date;

import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;

import org.jdom2.Document;
import org.jdom2.Element;

/**
 * @author Verena Käfer
 */
public class Load {

    /**
     * Loads the Test case data out of the given document
     * 
     * @param doc
     *            The Document of the test case
     * @param parent
     * @return a test case
     */
    public static TestCaseDescriptor loadTestCase(Document doc) {
	Element root = doc.getRootElement();

	// Meta data
	TestCaseDescriptor testCase = new TestCaseDescriptor();
	testCase.setId(Long.valueOf(root.getAttributeValue("id")));

	testCase.setAssignedTo(root.getChildText("assignedTo"));

	testCase.setAuthor(root.getChildText("author"));

	try {
	    Date creationDate = new Date(Long.parseLong(root
		    .getChildText("creationDate")));
	    testCase.setCreationDate(creationDate);
	} catch (NumberFormatException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	testCase.setExpectedDuration(root.getChildText("expectedDuration"));

	Date lastExecution;

	try {
	    if (root.getChildText("lastExecution").equals("not executed")) {
		lastExecution = null;
	    } else {
		lastExecution = new Date(Long.parseLong(root
			.getChildText("lastExecution")));
	    }
	    testCase.setLastExecution(lastExecution);
	} catch (NumberFormatException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	Date lastChangedOn = new Date(Long.parseLong(root
		.getChildText("lastChangedOn")));
	testCase.setLastChangedOn(lastChangedOn);

	testCase.setNumberOfExecutions(Integer.valueOf(root
		.getChildText("numberOfExecutions")));

	testCase.setNumberOfFailures(Integer.valueOf(root
		.getChildText("numberOfFailures")));

	// priority
	if (root.getChildText("priority").equals("low")) {
	    testCase.setPriority(PriorityType.low);
	} else if (root.getChildText("priority").equals("medium")) {
	    testCase.setPriority(PriorityType.medium);
	} else {
	    testCase.setPriority(PriorityType.high);
	}

	testCase.setRealDuration(root.getChildText("realDuration"));

	testCase.setRichTextPrecondition(root
		.getChildText("richTextPrecondition"));

	testCase.setRichTextResult(root.getChildText("richTextResult"));

	testCase.setShortDescription(root.getChildText("shortDescription"));

	// Test steps
	for (Element currentElement : root.getChild("steps").getChildren()) {

	    TestStepDescriptor addTestStep = new TestStepDescriptor();

	    addTestStep.setExpectedResult(currentElement.getChildText("expectedResult"));

	    addTestStep.setRealResult(currentElement.getChildText("realResult"));

	    addTestStep.setRichTextDescription(currentElement
		    .getChildText("richTextDescription"));

	    String statusText = currentElement.getChildText("stepStatus");
	    StatusType status = StatusType.notExecuted;

	    if (statusText.equals("passed")) {
		status = StatusType.passed;
	    } else if (statusText.equals("passedWithAnnotation")) {
		status = StatusType.passedWithAnnotation;
	    } else if (statusText.equals("failed")) {
		status = StatusType.failed;
	    } else if (statusText.equals("notExecuted")) {
		status = StatusType.notExecuted;
	    }
	    addTestStep.setStatus(status);
	    testCase.addStep(addTestStep);
	}

	// status
	if (root.getChildText("status").equals("passed")) {
	    testCase.setStatus(StatusType.passed);
	} else if (root.getChildText("status").equals("passedWithAnnotation")) {
	    testCase.setStatus(StatusType.passedWithAnnotation);
	} else if (root.getChildText("status").equals("failed")) {
	    testCase.setStatus(StatusType.failed);
	} else {
	    testCase.setStatus(StatusType.notExecuted);
	}
	
	return testCase;
    }

    /**
     * Loads the Protocol data out of the given document
     * 
     * @param doc
     *            The Document of the protocol
     * @param parent
     * @return A test case protocol
     */
    public static TestCaseDescriptor loadProtocol(Document doc) {

	Element root = doc.getRootElement();
	TestCaseDescriptor testCaseDescriptor = new TestCaseDescriptor();

	// Meta data
	testCaseDescriptor.setId(Long.valueOf(root.getAttributeValue("id")));

	testCaseDescriptor.setAssignedTo(root.getChildText("assignedTo"));

	testCaseDescriptor.setAuthor(root.getChildText("author"));

	try {
	    Date creationDate = new Date(Long.parseLong(root
		    .getChildText("creationDate")));
	    testCaseDescriptor.setCreationDate(creationDate);
	} catch (NumberFormatException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	testCaseDescriptor.setExpectedDuration(root.getChildText("expectedDuration"));

	Date lastExecution;

	try {
	    if (root.getChildText("lastExecution").equals("not executed")) {
		lastExecution = null;
	    } else {
		lastExecution = new Date(Long.parseLong(root
			.getChildText("lastExecution")));
	    }
	    testCaseDescriptor.setLastExecution(lastExecution);
	} catch (NumberFormatException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	Date lastChangedOn = new Date(Long.parseLong(root
		.getChildText("lastChangedOn")));
	testCaseDescriptor.setLastChangedOn(lastChangedOn);

	testCaseDescriptor.setNumberOfExecutions(Integer.valueOf(root
		.getChildText("numberOfExecutions")));

	testCaseDescriptor.setNumberOfFailures(Integer.valueOf(root
		.getChildText("numberOfFailures")));

	// priority
	if (root.getChildText("priority").equals("low")) {
	    testCaseDescriptor.setPriority(PriorityType.low);
	} else if (root.getChildText("priority").equals("medium")) {
	    testCaseDescriptor.setPriority(PriorityType.medium);
	} else {
	    testCaseDescriptor.setPriority(PriorityType.high);
	}

	testCaseDescriptor.setRealDuration(root.getChildText("realDuration"));

	testCaseDescriptor.setRichTextPrecondition(root.getChildText("richTextPrecondition"));

	testCaseDescriptor.setRichTextResult(root.getChildText("richTextResult"));

	testCaseDescriptor.setShortDescription(root.getChildText("shortDescription"));
	
	int revision = 0;
	try {
	    String revisionText = root.getChildText("revision");
	    if (revisionText != null) {
		revision = Integer.parseInt(root.getChildText("revision"));
	    }
	} catch(NumberFormatException e) {
	    // revision was not a number or non-existent, we keep it as 0
	}
	testCaseDescriptor.setRevisionNumber(revision);

	for (Element currentElement : root.getChild("steps").getChildren()) {

	    TestStepDescriptor addTestStep = new TestStepDescriptor();

	    addTestStep.setExpectedResult(currentElement.getChildText("expectedResult"));

	    addTestStep.setRealResult(currentElement.getChildText("realResult"));

	    addTestStep.setRichTextDescription(currentElement
		    .getChildText("richTextDescription"));

	    String statusText = currentElement.getChildText("stepStatus");
	    StatusType status = StatusType.notExecuted;

	    if (statusText.equals("passed")) {
		status = StatusType.passed;
	    } else if (statusText.equals("passedWithAnnotation")) {
		status = StatusType.passedWithAnnotation;
	    } else if (statusText.equals("failed")) {
		status = StatusType.failed;
	    } else if (statusText.equals("notExecuted")) {
		status = StatusType.notExecuted;
	    }
	    addTestStep.setStatus(status);
	    testCaseDescriptor.addStep(addTestStep);
	}

	// status
	if (root.getChildText("status").equals("passed")) {
	    testCaseDescriptor.setStatus(StatusType.passed);
	} else if (root.getChildText("status").equals("passedWithAnnotation")) {
	    testCaseDescriptor.setStatus(StatusType.passedWithAnnotation);
	} else if (root.getChildText("status").equals("failed")) {
	    testCaseDescriptor.setStatus(StatusType.failed);
	} else {
	    testCaseDescriptor.setStatus(StatusType.notExecuted);
	}

	return testCaseDescriptor;

    }
}
