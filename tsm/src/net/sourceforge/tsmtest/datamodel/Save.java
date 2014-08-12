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
 * 	Daniel Hertl - some fixes
 * 	Wolfang Kraus - various fixes
 * 	Bernhard Wetzel - some fixes, added comments
 * 	Jenny Krüwald - some changes for the filterview
 *
 *******************************************************************************/
/**
 * This class implements the save method 
 * which saves a project in several XML files
 */
package net.sourceforge.tsmtest.datamodel;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;

import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;

import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * @author Verena Käfer
 * 
 */
public class Save {

    private static Logger log = Logger.getLogger(Save.class);

    /**
     * This method extracts from the xmlCode all img tags and calls
     * <code>DataModel.getInstance().copyImageToWorkspace(path)</code> on each
     * of the paths to copy the images to the workspace and replaces the path to
     * the return value of said method.
     * 
     * @param xmlCode
     *            the xml code to copy the images to workspace from
     * @param project
     * @return the new xmlCode with the img path adjusted.
     */
    private static String copyImagesToWorkspace(final String xmlCode,
	    final String project) {
	// TODO remove unused method
	return xmlCode;
	// Pattern pattern = Pattern.compile("\\&lt;img src=\"(.+?)\"");
	// Matcher m = pattern.matcher(xmlCode);
	// StringBuffer sb = new StringBuffer(xmlCode.length());
	// while (m.find()) {
	// String path = m.group(1);
	// path = DataModel.getInstance().copyImageToProject(path, project);
	// m.appendReplacement(sb, Matcher.quoteReplacement(path));
	// }
	// m.appendTail(sb);
	// return sb.toString();
    }

    private static void deleteUnusedImagesFromWorkspace() {

    }

    /**
     * @param t
     *            the test case to save
     * @param file
     *            The IFile of the testCase
     * @return true if saving was successful
     * @throws DataModelException
     */
    public synchronized static InputStream saveTestCase(
	    final TestCaseDescriptor t, final String project)
	    throws DataModelException {

	final XMLOutputter outputter = new XMLOutputter(
		Format.getPrettyFormat());
	final Document doc = new Document();
	final Element rootTestCase = new Element("tsmtestcase");
	doc.setRootElement(rootTestCase);

	final Attribute id = new Attribute("id", Long.toString(t.getId()));
	rootTestCase.setAttribute(id);

	final Element assignedTo = new Element("assignedTo");
	assignedTo.setText(t.getAssignedTo());
	rootTestCase.addContent(assignedTo);

	final Element author = new Element("author");
	author.setText(t.getAuthor());
	rootTestCase.addContent(author);

	final Element expectedDuration = new Element("expectedDuration");
	expectedDuration.setText(t.getExpectedDuration());
	rootTestCase.addContent(expectedDuration);

	final Element realDuration = new Element("realDuration");
	realDuration.setText(t.getRealDuration());
	rootTestCase.addContent(realDuration);

	final Element richTextPrecondition = new Element("richTextPrecondition");
	richTextPrecondition.setText(t.getRichTextPrecondition());
	rootTestCase.addContent(richTextPrecondition);

	// TODO pictures
	final Element richTextResult = new Element("richTextResult");
	richTextResult.setText(t.getRichTextResult());
	rootTestCase.addContent(richTextResult);

	final Element shortDescription = new Element("shortDescription");
	shortDescription.setText(t.getShortDescription());
	rootTestCase.addContent(shortDescription);

	final Element creationDateElement = new Element("creationDate");
	creationDateElement.setText(t.getCreationDate().getTime() + "");
	rootTestCase.addContent(creationDateElement);

	final Element lastExecutionElement = new Element("lastExecution");

	if (t.getLastExecution() == null) {
	    lastExecutionElement.setText("not executed");
	} else {
	    lastExecutionElement.setText(t.getLastExecution().getTime() + "");

	}

	rootTestCase.addContent(lastExecutionElement);

	final Element lastChangedOnElement = new Element("lastChangedOn");
	lastChangedOnElement.setText(new Date().getTime() + "");
	rootTestCase.addContent(lastChangedOnElement);

	final Element numberOfExecutions = new Element("numberOfExecutions");
	numberOfExecutions.setText(Integer.toString(t.getNumberOfExecutions()));
	rootTestCase.addContent(numberOfExecutions);

	final Element numberOfFailures = new Element("numberOfFailures");
	numberOfFailures.setText(Integer.toString(t.getNumberOfFailures()));
	rootTestCase.addContent(numberOfFailures);

	final Element priority = new Element("priority");
	priority.setText(t.getPriority().toString());
	rootTestCase.addContent(priority);

	final Element status = new Element("status");
	status.setText(t.getStatus().toString());
	rootTestCase.addContent(status);

	final Element revision = new Element("revision");
	revision.setText(t.getRevisionNumber() + "");
	rootTestCase.addContent(revision);

	// list of all steps
	final Element steps = new Element("steps");
	for (final TestStepDescriptor step : t.getSteps()) {
	    final Element testStep = new Element("TestStep");

	    final Element expectedResult = new Element("expectedResult");
	    expectedResult.setText(step.getExpectedResult());
	    testStep.addContent(expectedResult);

	    final Element realResult = new Element("realResult");
	    realResult.setText(step.getRealResult());
	    testStep.addContent(realResult);

	    // TODO pictures, boolean
	    final Element richTextDescription = new Element(
		    "richTextDescription");
	    richTextDescription.setText(step.getRichTextDescription());
	    testStep.addContent(richTextDescription);

	    final Element stepStatus = new Element("stepStatus");
	    stepStatus.setText(step.getStatus().toString());
	    testStep.addContent(stepStatus);

	    steps.addContent(testStep);
	}

	rootTestCase.addContent(steps);

	try {
	    final StringWriter stringWriter = new StringWriter();
	    outputter.output(doc, stringWriter);
	    stringWriter.flush();
	    stringWriter.close();
	    return new ByteArrayInputStream(copyImagesToWorkspace(
		    stringWriter.toString(), project).getBytes("UTF-8"));
	} catch (final FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * @param p
     *            the protocol
     * @param testCaseID
     * @param file
     *            The IFile of the testCase of the protocol
     * @return true if saving was successful
     */
    public synchronized static InputStream saveTestCaseProtocol(
	    final TestCaseDescriptor p, final String project) {
	final XMLOutputter outputter = new XMLOutputter(
		Format.getPrettyFormat());
	final Document doc = new Document();
	final Element rootTestCaseProtocol = new Element("tsmtestcaseprotocol");
	doc.setRootElement(rootTestCaseProtocol);

	final Attribute id = new Attribute("id", p.getId() + "");
	rootTestCaseProtocol.setAttribute(id);

	final Element assignedTo = new Element("assignedTo");
	assignedTo.setText(p.getAssignedTo());
	rootTestCaseProtocol.addContent(assignedTo);

	final Element author = new Element("author");
	author.setText(p.getAuthor());
	rootTestCaseProtocol.addContent(author);

	final Element expectedDuration = new Element("expectedDuration");
	expectedDuration.setText(p.getExpectedDuration());
	rootTestCaseProtocol.addContent(expectedDuration);

	final Element realDuration = new Element("realDuration");
	realDuration.setText(p.getRealDuration());
	rootTestCaseProtocol.addContent(realDuration);

	// TODO pictures
	final Element richTextPrecondition = new Element("richTextPrecondition");
	richTextPrecondition.setText(p.getRichTextPrecondition());
	rootTestCaseProtocol.addContent(richTextPrecondition);

	// TODO pictures
	final Element richTextResult = new Element("richTextResult");
	richTextResult.setText(p.getRichTextResult());
	rootTestCaseProtocol.addContent(richTextResult);

	final Element shortDescription = new Element("shortDescription");
	shortDescription.setText(p.getShortDescription());
	rootTestCaseProtocol.addContent(shortDescription);

	final Element creationDateElement = new Element("creationDate");
	creationDateElement.setText(p.getCreationDate().getTime() + "");
	rootTestCaseProtocol.addContent(creationDateElement);

	StringBuilder lastExecution;

	if (p.getLastExecution() == null) {
	    lastExecution = new StringBuilder("not executed");
	} else {

	    lastExecution = new StringBuilder(p.getLastExecution().getTime()
		    + "");
	}

	final Element lastExecutionElement = new Element("lastExecution");
	lastExecutionElement.setText(lastExecution.toString());
	rootTestCaseProtocol.addContent(lastExecutionElement);

	final Element lastChangedOnElement = new Element("lastChangedOn");
	lastChangedOnElement.setText(p.getLastChangedOn().getTime() + "");
	rootTestCaseProtocol.addContent(lastChangedOnElement);

	final Element numberOfExecutions = new Element("numberOfExecutions");
	numberOfExecutions.setText(Integer.toString(p.getNumberOfExecutions()));
	rootTestCaseProtocol.addContent(numberOfExecutions);

	final Element numberOfFailures = new Element("numberOfFailures");
	numberOfFailures.setText(Integer.toString(p.getNumberOfFailures()));
	rootTestCaseProtocol.addContent(numberOfFailures);

	final Element priority = new Element("priority");
	priority.setText(p.getPriority().toString());
	rootTestCaseProtocol.addContent(priority);

	final Element status = new Element("status");
	status.setText(p.getStatus().toString());
	rootTestCaseProtocol.addContent(status);

	final Element revision = new Element("revision");
	revision.setText(p.getRevisionNumber() + "");
	rootTestCaseProtocol.addContent(revision);
	// list of all steps
	final Element steps = new Element("steps");
	for (final TestStepDescriptor step : p.getSteps()) {
	    final Element testStep = new Element("TestStep");
	    // TODO Ids
	    final Attribute stepID = new Attribute("id", "0");
	    testStep.setAttribute(stepID);

	    final Element expectedResult = new Element("expectedResult");
	    expectedResult.setText(step.getExpectedResult());
	    testStep.addContent(expectedResult);

	    final Element realResult = new Element("realResult");
	    realResult.setText(step.getRealResult());
	    testStep.addContent(realResult);

	    // TODO pictures, boolean
	    final Element richTextDescription = new Element(
		    "richTextDescription");
	    richTextDescription.setText(step.getRichTextDescription());
	    testStep.addContent(richTextDescription);

	    final Element stepStatus = new Element("stepStatus");
	    stepStatus.setText(step.getStatus().toString());
	    testStep.addContent(stepStatus);

	    steps.addContent(testStep);
	}

	rootTestCaseProtocol.addContent(steps);

	try {
	    final StringWriter stringWriter = new StringWriter();
	    outputter.output(doc, stringWriter);
	    stringWriter.flush();
	    stringWriter.close();

	    return new ByteArrayInputStream(copyImagesToWorkspace(
		    stringWriter.toString(), project).getBytes("UTF-8"));
	} catch (final FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }
}
