/*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 	Verena Käfer - initial version
 * 	Tobias Hirning - i18n
 * 	Albert Flaig - data model clean up
 * 	Wolfgang Kraus - some fixes
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.io.justus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;
import net.sourceforge.tsmtest.gui.richtexteditor.RichText;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.jdom2.Element;

/**
 * @author Verena Käfer
 * 
 *         This class holds the methods for the import of Justus files.
 * 
 */
public class JustusImport {

    /**
     * @param source
     *            The String path given by the wizard.
     * @param parentPackage
     *            IPAth to the parent package in the explorer given by the
     *            wizard.
     * @return true if the import was successful otherwise false.
     * @throws CoreException
     * @throws ParseException
     * @throws DataModelException
     */

    public static boolean importFile(final Element root,
	    final IPath parentPackage, final boolean isSeqeuenceTestCase,
	    final IProgressMonitor monitor, final int number)
	    throws CoreException, DataModelException, ParseException {

	boolean importSuccessful = false;

	final IProgressMonitor m1 = startMonitor(monitor, "root", 1);

	// Disable data model refreshing because a sequence of new files will be
	// created
	DataModel.getInstance().setEnabled(false);
	try {
	    // go through the elements and import them
	    for (final Element e : root.getChildren()) {
		if (!m1.isCanceled()) {
		    if (readPackages(e, parentPackage, "<html>",
			    isSeqeuenceTestCase, getSubMonitor(m1, number),
			    number)) {
			importSuccessful = true;
		    }
		} else {
		    m1.done();
		}
	    }
	} finally {
	    // make sure to re-enable the data model again even if something
	    // fails
	    DataModel.getInstance().setEnabled(true);
	}
	ResourcesPlugin.getWorkspace().getRoot()
		.refreshLocal(IResource.DEPTH_INFINITE, null);

	m1.done();

	DataModel.getInstance().refresh();

	return importSuccessful;
    }

    /**
     * @param parentElement
     *            The element which's child elements are proceeded.
     * @param parentPath
     *            The IPath to the parent package in the explorer.
     * @param preCondition
     *            The summarized preconditions of the parent folders.
     * @param isSeqeuenceTestCase
     * @return true if the import of the element was successful otherwise false.
     * @throws CoreException
     * @throws DataModelException
     * @throws ParseException
     */

    private static synchronized boolean readPackages(
	    final Element parentElement, final IPath parentPath,
	    final String preCondition, final boolean isSeqeuenceTestCase,
	    final IProgressMonitor monitor, final int number)
	    throws CoreException, DataModelException, ParseException {

	int counter = 1;
	String monitorName = "name";
	if (parentElement.getAttributeValue("name") != null) {
	    monitorName = parentElement.getAttributeValue("name");
	} else if (parentElement.getAttributeValue("title") != null) {
	    monitorName = parentElement.getAttributeValue("title");
	}
	final IProgressMonitor m1 = startMonitor(monitor, " -> " + monitorName,
		number);
	for (final Element currentElement : parentElement.getChildren()) {
	    if (!m1.isCanceled()) {
		// if (!m1.isCanceled()) {
		// path of root + path of parent package
		final IPath oldIPath = ResourcesPlugin.getWorkspace().getRoot()
			.getLocation().append(parentPath.toString());
		final IContainer parentFolder = ResourcesPlugin.getWorkspace()
			.getRoot().getContainerForLocation(oldIPath);
		TSMContainer tsmContainer = (TSMContainer) DataModel
			.getInstance().convertToTSMResource(parentFolder);
		// We wait until the parent container is found to avoid null
		// pointer exceptions caused by timing problems.
		while (tsmContainer == null) {
		    tsmContainer = (TSMContainer) DataModel.getInstance()
			    .convertToTSMResource(parentFolder);
		}

		String newPathString = parentPath.toString();
		newPathString = newPathString + "/" //$NON-NLS-1$
			+ currentElement.getAttributeValue("name"); //$NON-NLS-1$
		Path path = new Path(newPathString);
		IPath iPath = path;

		String name;

		// All sequences will be transformed into packages
		if (currentElement.getName().equals("sequence")) { //$NON-NLS-1$
		    TSMPackage tsmPackage = null;

		    if (currentElement.getAttributeValue("name") != null) {
			name = currentElement.getAttributeValue("name"); //$NON-NLS-1$
		    } else {
			name = "name";
		    }
		    String newName = name;
		    // folder exists so append index
		    while (parentFolder.findMember(newName) != null) {
			newName = name + counter;
			counter++;
		    }
		    name = newName;

		    tsmPackage = tsmContainer
			    .createPackage(deleteCharacters(name));
		    // ResourcesPlugin.getWorkspace().getRoot()
		    // .refreshLocal(IResource.DEPTH_INFINITE, null);
		    if (tsmPackage == null) {
			return false;
		    }
		    m1.worked(1);
		    // add the current precondition
		    String newPreCondition = preCondition;
		    if (currentElement.getChildText("pre") != null) {
			if (!currentElement.getChildText("pre").equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			    newPreCondition = preCondition
				    + "<p>" //$NON-NLS-1$
				    + replaceCharacters(currentElement
					    .getChildText("pre")) + "</p><p></p>"; //$NON-NLS-1$ //$NON-NLS-2$
			}

		    }

		    // add the new created folder to the path and transfer it
		    // into
		    // an IPath
		    newPathString = parentPath.toString();
		    newPathString = newPathString + "/" + deleteCharacters(name); //$NON-NLS-1$
		    path = new Path(newPathString);
		    iPath = path;
		    readPackages(currentElement, iPath, newPreCondition,
			    isSeqeuenceTestCase, getSubMonitor(m1, number),
			    number - 1);

		} else if (currentElement.getName().equals("testcase")) { //$NON-NLS-1$
		    if (!isSeqeuenceTestCase) {
			if (!createTestCase(currentElement, preCondition,
				tsmContainer, getSubMonitor(m1, number), number)) {
			    return false;
			}
		    } else {
			// System.out.println("current element: " +
			// currentElement.getAttributeValue("title"));
			return createSequence(
				currentElement.getParentElement(),
				preCondition, tsmContainer,
				getSubMonitor(monitor, number), number);
		    }
		} else if (currentElement.getName().equals("tsa")) { //$NON-NLS-1$
		    break;
		}
		// } else {
		// m1.done();
		// }
		m1.worked(1);
	    } else {
		m1.done();
	    }
	}
	m1.done();
	return true;
    }

    /**
     * @param newString
     *            The name of the test case or package which should be parsed.
     * @return The parsed string
     */
    private static String deleteCharacters(final String name) {
	String newString = name.replace("<", "#"); //$NON-NLS-1$ //$NON-NLS-2$
	newString = newString.replace(">", "#"); //$NON-NLS-1$ //$NON-NLS-2$
	newString = newString.replace("?", "#"); //$NON-NLS-1$ //$NON-NLS-2$
	newString = newString.replace(":", "#"); //$NON-NLS-1$ //$NON-NLS-2$
	newString = newString.replace("*", "#"); //$NON-NLS-1$ //$NON-NLS-2$
	newString = newString.replace("\"", "#"); //$NON-NLS-1$ //$NON-NLS-2$
	newString = newString.replace("\\", "#"); //$NON-NLS-1$ //$NON-NLS-2$
	newString = newString.replace("/", "#"); //$NON-NLS-1$ //$NON-NLS-2$
	newString = newString.replace("|", "#"); //$NON-NLS-1$ //$NON-NLS-2$
	if (newString.length() > 200) {
	    newString = newString.substring(0, 200);
	}
	return newString;
    }

    /**
     * The method replaces several characters. Needed for rich text
     * 
     * @param text
     *            The text to parse
     * @return The text with replaced characters.
     */
    private static String replaceCharacters(final String text) {

	final String[][] escapes = new String[RichText.escapes.length + 2][2];
	System.arraycopy(RichText.escapes, 0, escapes, 0,
		RichText.escapes.length);

	// As the imported newline-character is dependent on the origination OS
	// we need to consider both
	escapes[escapes.length - 2] = new String[] { "\r\n", "</p><p>" };
	escapes[escapes.length - 1] = new String[] { "\n", "</p><p>" };
	String newText = text;
	newText.replace("&gt;", ">"); //$NON-NLS-1$ //$NON-NLS-2$
	newText.replace("&lt;", "<"); //$NON-NLS-1$ //$NON-NLS-2$
	for (final String[] replace : escapes) {
	    newText = newText.replace(replace[0], replace[1]);
	}
	return newText;
    }

    /**
     * @param currentElement
     *            The element for the test case
     * @param preCondition
     *            The current precondition
     * @param tsmContainer
     *            The parent container
     * @param monitor
     *            The monitor for the progress bar
     * @param number
     *            The amount of elements, needed for the monitor
     * @return True if successful
     * @throws ParseException
     * @throws CoreException
     */

    private static boolean createTestCase(final Element currentElement,
	    final String preCondition, final TSMContainer tsmContainer,
	    final IProgressMonitor monitor, final int number)
	    throws ParseException, CoreException {
	final IProgressMonitor m1 = startMonitor(monitor, " -> "
		+ currentElement.getAttributeValue("title"), 1);

	if (!m1.isCanceled()) {
	    // create a new test case for every element
	    String name;
	    int counter = 1;
	    if (currentElement.getAttributeValue("title") != null) {
		name = currentElement.getAttributeValue("title"); //$NON-NLS-1$
	    } else {
		name = "name";
	    }

	    final TestCaseDescriptor testCase = new TestCaseDescriptor();
	    // map Justus priorities on our priorities
	    if (currentElement.getAttributeValue("priority") != null) {
		if (currentElement.getAttributeValue("priority").equals("A") //$NON-NLS-1$ //$NON-NLS-2$
			|| currentElement.getAttributeValue("priority").equals( //$NON-NLS-1$
				"B")) { //$NON-NLS-1$
		    testCase.setPriority(PriorityType.high);
		} else if (currentElement.getAttributeValue("priority").equals( //$NON-NLS-1$
			"C")) { //$NON-NLS-1$
		    testCase.setPriority(PriorityType.medium);
		} else {
		    testCase.setPriority(PriorityType.low);
		}
	    } else {
		testCase.setPriority(PriorityType.medium);
	    }

	    if (currentElement.getAttributeValue("creator") != null) {
		testCase.setAuthor(currentElement.getAttributeValue("creator")); //$NON-NLS-1$
	    }

	    if (currentElement.getAttributeValue("created") != null) {

		final SimpleDateFormat formatDate = new SimpleDateFormat(
			"dd.mm.yyyy"); //$NON-NLS-1$
		final Date date = formatDate.parse(currentElement
			.getAttributeValue("created")); //$NON-NLS-1$
		testCase.setCreationDate(date);

	    } else {
		testCase.setCreationDate(new Date());
	    }

	    if (currentElement.getAttributeValue("effort") != null) {
		testCase.setExpectedDuration(currentElement
			.getAttributeValue("effort")); //$NON-NLS-1$
	    }

	    // add <html><p> before rich texts and </p></html> after
	    // them
	    if (currentElement.getChildText("description") != null) {
		testCase.setShortDescription("<html><p>" //$NON-NLS-1$
			+ replaceCharacters(currentElement
				.getChildText("description")) + "</p></html>"); //$NON-NLS-1$ //$NON-NLS-2$
	    } else {
		testCase.setShortDescription("<html><p></p></html>");
	    }

	    if (currentElement.getChildText("pre") != null) {
		String newPreCondition = preCondition;
		if (!currentElement.getChildText("pre").equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
		    newPreCondition = newPreCondition
			    + "<p>" //$NON-NLS-1$
			    + replaceCharacters(currentElement
				    .getChildText("pre")) + "</p></html>"; //$NON-NLS-1$ //$NON-NLS-2$
		    testCase.setRichTextPrecondition(newPreCondition);
		} else {
		    newPreCondition = newPreCondition + "</html>"; //$NON-NLS-1$
		    testCase.setRichTextPrecondition(newPreCondition);
		}
	    } else {
		testCase.setRichTextPrecondition("<html><p></p></html>");
	    }

	    // add one test step
	    final TestStepDescriptor step = new TestStepDescriptor();

	    if (currentElement.getChildText("actions") != null) {
		step.setRichTextDescription("<html><p>" //$NON-NLS-1$
			+ replaceCharacters(currentElement
				.getChildText("actions")) + "</p></html>"); //$NON-NLS-1$ //$NON-NLS-2$
	    } else {
		step.setRichTextDescription("<html><p></p></html>"); //$NON-NLS-1$ 
	    }

	    if (currentElement.getChildText("post") != null) {
		step.setExpectedResult("<html><p>" //$NON-NLS-1$
			+ replaceCharacters(currentElement.getChildText("post")) //$NON-NLS-1$
			+ "</p></html>"); //$NON-NLS-1$
	    } else {
		step.setExpectedResult("<html><p></p></html>"); //$NON-NLS-1$
	    }

	    testCase.addStep(step);
	    testCase.setId(TSMTestCase.generateID(new Date()));

	    Boolean exists = true;
	    // while test case with name exists append index and try
	    // again.
	    // Necessary in this way because it takes some time to
	    // create
	    // test cases and so it is possible that an earlier check is
	    // positive and we get an error message though.
	    while (exists) {
		try {
		    name = deleteCharacters(name);
		    final TSMTestCase createdTestCase = tsmContainer
			    .createTestCase(name, testCase);
		    // ResourcesPlugin.getWorkspace().getRoot()
		    // .refreshLocal(IResource.DEPTH_INFINITE, null);
		    if (createdTestCase == null) {
			return false;
		    }
		    exists = false;
		} catch (final DataModelException ex) {
		    // test case already exists so append an index and
		    // try
		    // it again
		    String newName = name;
		    newName = name + counter;
		    counter++;
		    name = newName;
		}
	    }
	} else {
	    m1.done();
	}
	m1.worked(1);
	m1.done();
	return true;
    }

    /**
     * @param parentElement
     *            The element for the test case
     * @param preCondition
     *            The current precondition
     * @param tsmContainer
     *            The parent container
     * @param monitor
     *            The monitor for the progress bar
     * @param number
     *            Amount of elements. Needen for the progress bar
     * @return True if successful
     * @throws ParseException
     * @throws CoreException
     */

    private static boolean createSequence(final Element parentElement,
	    final String preCondition, final TSMContainer tsmContainer,
	    final IProgressMonitor monitor, final int number)
	    throws ParseException, CoreException {
	final IProgressMonitor m1 = startMonitor(monitor, "", 1);
	if (!m1.isCanceled()) {
	    String name;
	    int counter = 1;
	    int hours = 0;
	    int minutes = 0;
	    String newPreCondition = preCondition;
	    String description = "<html>";
	    final TestCaseDescriptor testCase = new TestCaseDescriptor();
	    if (parentElement.getAttributeValue("name") != null) {
		name = parentElement.getAttributeValue("name"); //$NON-NLS-1$
	    } else {
		name = "name";
	    }
	    final int priorities[] = { 0, 0, 0 };
	    if (parentElement.getChildText("description") != null) {
		if (!parentElement.getChildText("description").equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
		    description = description
			    + "<p>" //$NON-NLS-1$
			    + replaceCharacters(parentElement
				    .getChildText("description")) + "</p>"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	    }

	    if (parentElement.getAttributeValue("creator") != null) {
		testCase.setAuthor(parentElement.getAttributeValue("creator")); //$NON-NLS-1$
	    }

	    if (parentElement.getAttributeValue("created") != null) {

		final SimpleDateFormat formatDate = new SimpleDateFormat(
			"dd.mm.yyyy"); //$NON-NLS-1$
		final Date date = formatDate.parse(parentElement
			.getAttributeValue("created")); //$NON-NLS-1$
		testCase.setCreationDate(date);

	    } else {
		testCase.setCreationDate(new Date());
	    }

	    // every test case is one test step
	    for (final Element currentElement : parentElement.getChildren()) {
		if (!m1.isCanceled()) {
		    // just test cases
		    if (currentElement.getName().equals("testcase")) {
			if (currentElement.getAttributeValue("priority") != null) {
			    // priorities[0] is high, 1 is medium and 2 is low
			    if (currentElement
				    .getAttributeValue("priority").equals("A") //$NON-NLS-1$ //$NON-NLS-2$
				    || currentElement.getAttributeValue(
					    "priority").equals( //$NON-NLS-1$
					    "B")) { //$NON-NLS-1$
				priorities[0]++;
			    } else if (currentElement.getAttributeValue(
				    "priority").equals( //$NON-NLS-1$
				    "C")) { //$NON-NLS-1$
				priorities[1]++;
			    } else {
				priorities[2]++;
			    }
			} else {
			    priorities[1]++;
			}
		    }

		    if (currentElement.getAttributeValue("effort") != null) {
			final String[] time = currentElement.getAttributeValue(
				"effort").split(":");
			hours = hours + Integer.parseInt(time[0]);
			minutes = minutes + Integer.parseInt(time[1]);
		    }

		    // add <html><p> before rich texts and </p></html> after
		    // them
		    if (currentElement.getChildText("description") != null) {
			if (!currentElement
				.getChildText("description").equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			    description = description
				    + "<p>" //$NON-NLS-1$
				    + replaceCharacters(currentElement
					    .getChildText("description")) + "</p>"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		    }
		    if (currentElement.getChildText("pre") != null) {

			if (!currentElement.getChildText("pre").equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			    newPreCondition = newPreCondition
				    + "<p>" //$NON-NLS-1$
				    + replaceCharacters(currentElement
					    .getChildText("pre")) + "</p>"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		    }

		    final TestStepDescriptor step = new TestStepDescriptor();

		    if (currentElement.getChildText("actions") != null) {
			step.setRichTextDescription("<html><p>" //$NON-NLS-1$
				+ replaceCharacters(currentElement
					.getChildText("actions")) + "</p></html>"); //$NON-NLS-1$ //$NON-NLS-2$
		    } else {
			step.setRichTextDescription("<html><p></p></html>"); //$NON-NLS-1$ 
		    }

		    if (currentElement.getChildText("post") != null) {
			step.setExpectedResult("<html><p>" //$NON-NLS-1$
				+ replaceCharacters(currentElement
					.getChildText("post")) //$NON-NLS-1$
				+ "</p></html>"); //$NON-NLS-1$
		    } else {
			step.setExpectedResult("<html><p></p></html>"); //$NON-NLS-1$
		    }
		    // step is only added if it is not empty
		    if (!(step.getRichTextDescription().equals(
			    "<html><p></p></html>") && step.getExpectedResult()
			    .equals("<html><p></p></html>"))) {
			testCase.addStep(step);
		    }
		} else {
		    m1.done();
		}
	    }

	    testCase.setId(TSMTestCase.generateID(new Date()));

	    hours = hours + minutes % 60;
	    minutes = minutes / 60;
	    final String effort = hours + ":" + minutes;
	    testCase.setExpectedDuration(effort);

	    testCase.setRichTextPrecondition(newPreCondition + "</html>");

	    testCase.setShortDescription(description + "</html>");

	    if (priorities[0] >= priorities[1]) {
		if (priorities[0] >= priorities[2]) {
		    testCase.setPriority(PriorityType.high);
		} else {
		    testCase.setPriority(PriorityType.low);
		}
	    } else {
		if (priorities[1] >= priorities[2]) {
		    testCase.setPriority(PriorityType.medium);
		} else {
		    testCase.setPriority(PriorityType.low);
		}
	    }

	    Boolean exists = true;
	    // while test case with name exists append index and try
	    // again.
	    // Necessary in this way because it takes some time to
	    // create
	    // test cases and so it is possible that an earlier check is
	    // positive and we get an error message though.
	    name = deleteCharacters(name);
	    String newName = name;
	    while (exists) {
		try {
		    final TSMTestCase createdTestCase = tsmContainer
			    .createTestCase(newName, testCase);
		    // ResourcesPlugin.getWorkspace().getRoot()
		    // .refreshLocal(IResource.DEPTH_INFINITE, null);
		    if (createdTestCase == null) {
			return false;
		    }
		    // no exception so creation was successful and we can
		    // finish
		    exists = false;
		    return true;
		} catch (final DataModelException ex) {
		    // test case already exists so append an index and
		    // try
		    // it again

		    newName = name + counter;
		    counter++;
		    // System.out.println(ex.getLocalizedMessage());
		    // System.out.println(newName);
		}
	    }
	} else {
	    m1.done();
	}
	m1.worked(1);
	m1.done();
	return true;
    }

    /**
     * @param monitor
     * @param pMainTaskName
     * @param taskAmount
     * @return a started monitor
     */
    public static IProgressMonitor startMonitor(final IProgressMonitor monitor,
	    final String pMainTaskName, final int taskAmount) {
	IProgressMonitor newMonitor = monitor;
	if (newMonitor == null) {
	    newMonitor = new NullProgressMonitor();
	}
	newMonitor.beginTask(pMainTaskName == null ? "" : pMainTaskName,
		taskAmount);
	newMonitor.subTask(" ");
	return newMonitor;
    }

    /**
     * @param monitor
     * @param taskAmount
     * @return A sub monitor
     */
    public static IProgressMonitor getSubMonitor(
	    final IProgressMonitor monitor, final int taskAmount) {
	if (monitor == null) {
	    return new NullProgressMonitor();
	}
	if (monitor instanceof NullProgressMonitor) {
	    return monitor;
	}
	return new SubProgressMonitor(monitor, taskAmount,
		SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
    }
}
