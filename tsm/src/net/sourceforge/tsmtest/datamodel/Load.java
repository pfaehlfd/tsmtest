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
     * @param file
     *            The IFile of the test case
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
	for (Element e : root.getChild("steps").getChildren()) {

	    TestStepDescriptor addTestStep = new TestStepDescriptor();

	    addTestStep.setExpectedResult(e.getChildText("expectedResult"));

	    addTestStep.setRealResult(e.getChildText("realResult"));

	    addTestStep.setRichTextDescription(e
		    .getChildText("richTextDescription"));

	    String statusText = e.getChildText("stepStatus");
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
     * @param file
     *            The IFile of the protocol
     * @param parent
     * @return A test case protocol
     */
    public static TestCaseDescriptor loadProtocol(Document doc) {

	Element root = doc.getRootElement();
	TestCaseDescriptor p = new TestCaseDescriptor();

	// Meta data
	p.setId(Long.valueOf(root.getAttributeValue("id")));

	p.setAssignedTo(root.getChildText("assignedTo"));

	p.setAuthor(root.getChildText("author"));

	try {
	    Date creationDate = new Date(Long.parseLong(root
		    .getChildText("creationDate")));
	    p.setCreationDate(creationDate);
	} catch (NumberFormatException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	p.setExpectedDuration(root.getChildText("expectedDuration"));

	Date lastExecution;

	try {
	    if (root.getChildText("lastExecution").equals("not executed")) {
		lastExecution = null;
	    } else {
		lastExecution = new Date(Long.parseLong(root
			.getChildText("lastExecution")));
	    }
	    p.setLastExecution(lastExecution);
	} catch (NumberFormatException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	Date lastChangedOn = new Date(Long.parseLong(root
		.getChildText("lastChangedOn")));
	p.setLastChangedOn(lastChangedOn);

	p.setNumberOfExecutions(Integer.valueOf(root
		.getChildText("numberOfExecutions")));

	p.setNumberOfFailures(Integer.valueOf(root
		.getChildText("numberOfFailures")));

	// priority
	if (root.getChildText("priority").equals("low")) {
	    p.setPriority(PriorityType.low);
	} else if (root.getChildText("priority").equals("medium")) {
	    p.setPriority(PriorityType.medium);
	} else {
	    p.setPriority(PriorityType.high);
	}

	p.setRealDuration(root.getChildText("realDuration"));

	p.setRichTextPrecondition(root.getChildText("richTextPrecondition"));

	p.setRichTextResult(root.getChildText("richTextResult"));

	p.setShortDescription(root.getChildText("shortDescription"));
	
	int revision = 0;
	try {
	    String s = root.getChildText("revision");
	    if (s != null)
		revision = Integer.parseInt(root.getChildText("revision"));
	} catch(NumberFormatException e) {
	    // revision was not a number or non-existent, we keep it as 0
	}
	p.setRevisionNumber(revision);

	for (Element e : root.getChild("steps").getChildren()) {

	    TestStepDescriptor addTestStep = new TestStepDescriptor();

	    addTestStep.setExpectedResult(e.getChildText("expectedResult"));

	    addTestStep.setRealResult(e.getChildText("realResult"));

	    addTestStep.setRichTextDescription(e
		    .getChildText("richTextDescription"));

	    String statusText = e.getChildText("stepStatus");
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
	    p.addStep(addTestStep);
	}

	// status
	if (root.getChildText("status").equals("passed")) {
	    p.setStatus(StatusType.passed);
	} else if (root.getChildText("status").equals("passedWithAnnotation")) {
	    p.setStatus(StatusType.passedWithAnnotation);
	} else if (root.getChildText("status").equals("failed")) {
	    p.setStatus(StatusType.failed);
	} else {
	    p.setStatus(StatusType.notExecuted);
	}

	return p;

    }
}
