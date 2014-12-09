 /*******************************************************************************
 * Copyright (c) 2012-2013 Daniel Hertl.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Hertl - initial version
 *    Florian Krüger - various changes
 *    Tobias Hirning - some fixes
 *    Albert Flaig - various changes
 *    Wolfgang Kraus - various changes
 *    Verena Käfer - some fixes
 *    Bernhard Wetzel - various changes
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.newtestcase.view;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.SWTUtils;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.EditorPartInput;
import net.sourceforge.tsmtest.datamodel.ResourceEditorInput;
import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.AbstractDataModel.DataModelObservable;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;
import net.sourceforge.tsmtest.gui.IBreadCrumbListener;
import net.sourceforge.tsmtest.gui.TSMBreadcrumbViewer;
import net.sourceforge.tsmtest.gui.report.ViewReport;
import net.sourceforge.tsmtest.gui.richtexteditor.RichText;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;

/**
 * @author Daniel Hertl
 *
 */
public class ViewTestCase extends EditorPartInput implements
	DataModelObservable {
    public static final String ID = "net.sourceforge.tsmtest.gui.newtestcase.view.viewtestcase"; //$NON-NLS-1$
    private Text txtName;
    private Text txtPackage;
    private Text txtCreator;
    private Text txtDuration;
    private PriorityType priority;
    private Combo comboPriority;
    private RichText richTextEditShortDes;
    private RichText richTextEditPreCon;
    private Group preCondition;
    private Group shortDescription;
    private Color backgroundColorTextfield;
    private Color backgroundColorGroupField;
    private final Color colorError = new Color(null, 255, 164, 119);
    private final String ERROR_NAME_CHAR = Messages.ViewTestCase_1;
    private final String ERROR_NAME_EMPTY = Messages.ViewTestCase_2;
    private final String ERROR_DURATION_EMPTY = Messages.ViewTestCase_5;
    private final String ERROR_DURATION_INVALID = Messages.ViewTestCase_6;
    private String tempErrorMessage;
    private StepSash sashSteps;
    private Text txtAssignedTo;
    private boolean dirty;
    private Text txtProj;
    private TSMTestCase input;
    private volatile boolean saving = false;
    private ModifyListener dirtyListen;

    public ViewTestCase() {
    }

    /**
     * Create contents of the editor part.
     * 
     * @param parent
     */

    @Override
    public void createPartControl(final Composite parent) {
	parent.addDisposeListener(new DisposeListener() {
	    /*
	     * Detach a non-ui thread to extract and save the images
	     */
	    @Override
	    public void widgetDisposed(final DisposeEvent e) {
		final Job job = new Job("Saving images") {
		    @Override
		    protected IStatus run(final IProgressMonitor monitor) {
			// TODO: add Method for images
			return Status.OK_STATUS;
		    }
		};
		job.schedule();
	    }

	});

	DataModel.getInstance().register(this);

	parent.setLayout(new GridLayout(2, false));

	TSMBreadcrumbViewer viewer;
	viewer = new TSMBreadcrumbViewer(parent, SWT.NONE);
	viewer.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
	viewer.setInput(input);
	viewer.addBreadCrumbListener(new IBreadCrumbListener() {
	    @Override
	    public boolean selectionChanged(final TSMReport report) {
		try {
		    ViewReport.openGUI(report);
		    return true;
		} catch (final PartInitException e) {
		    e.printStackTrace();
		    return false;
		}
	    }

	    @Override
	    public boolean selectionChanged(final TSMTestCase testCase) {
		try {
		    ViewTestCase.openGUI(testCase);
		    return true;
		} catch (final PartInitException e) {
		    e.printStackTrace();
		    return false;
		}
	    }
	});

	dirtyListen = new ModifyListener() {
	    @Override
	    public void modifyText(final ModifyEvent e) {
		setDirty(true);
	    }
	};

	Group mainSettings;
	mainSettings = new Group(parent, SWT.NONE);
	final GridData gridDataMainSettings = new GridData(SWT.FILL, SWT.CENTER,
		true, false, 2, 1);
	gridDataMainSettings.heightHint = 74;
	mainSettings.setLayoutData(gridDataMainSettings);
	mainSettings.setText(Messages.ViewTestCase_8);
	mainSettings.setLayout(new GridLayout(8, false));

	final CLabel lblName = new CLabel(mainSettings, SWT.NONE);
	lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1,
		1));
	lblName.setText(Messages.ViewTestCase_10);

	txtName = new Text(mainSettings, SWT.BORDER);
	txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	txtName.addModifyListener(dirtyListen);
	txtName.setTextLimit(DataModelTypes.NAME_MAX_LENGTH);

	Label lblProject;
	lblProject = new Label(mainSettings, SWT.NONE);
	lblProject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
		false, 1, 1));
	lblProject.setText(Messages.ViewTestCase_11);

	txtProj = new Text(mainSettings, SWT.BORDER);
	txtProj.setText(""); //$NON-NLS-1$
	txtProj.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	txtProj.setEditable(false);

	final Label lblPackage = new Label(mainSettings, SWT.NONE);
	lblPackage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
		1, 1));
	lblPackage.setText(Messages.ViewTestCase_13);

	txtPackage = new Text(mainSettings, SWT.BORDER);
	txtPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1));
	txtPackage.setEditable(false);

	final Label lblPriority = new Label(mainSettings, SWT.NONE);
	lblPriority.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
		false, 1, 1));
	lblPriority.setText(Messages.ViewTestCase_14);

	comboPriority = new Combo(mainSettings, SWT.READ_ONLY);
	comboPriority.setItems(new String[] { Messages.ViewTestCase_15,
		Messages.ViewTestCase_16, Messages.ViewTestCase_17 });
	comboPriority.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 1, 1));
	comboPriority.select(1);

	comboPriority.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		setDirty(true);
		if (comboPriority.getSelectionIndex() == 0) {
		    priority = PriorityType.low;
		} else if (comboPriority.getSelectionIndex() == 1) {
		    priority = PriorityType.medium;
		} else if (comboPriority.getSelectionIndex() == 2) {
		    priority = PriorityType.high;
		}
	    }
	});
	priority = PriorityType.medium;

	final Label lblCreator = new Label(mainSettings, SWT.NONE);
	lblCreator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
		1, 1));
	lblCreator.setText(Messages.ViewTestCase_18);

	txtCreator = new Text(mainSettings, SWT.BORDER);
	txtCreator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1));
	txtCreator.addModifyListener(dirtyListen);

	Label lblAssignedTo;
	lblAssignedTo = new Label(mainSettings, SWT.NONE);
	lblAssignedTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
		false, 1, 1));
	lblAssignedTo.setText(Messages.ViewTestCase_19);

	txtAssignedTo = new Text(mainSettings, SWT.BORDER);
	txtAssignedTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 1, 1));
	txtAssignedTo.addModifyListener(dirtyListen);

	final Label lblDuration = new Label(mainSettings, SWT.NONE);
	lblDuration.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
		false, 1, 1));
	lblDuration.setText(Messages.ViewTestCase_20);

	txtDuration = new Text(mainSettings, SWT.BORDER);
	txtDuration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1));
	txtDuration.setText("00:00"); //$NON-NLS-1$
	txtDuration.addModifyListener(dirtyListen);

	final GridData gridDataGroup = new GridData(SWT.FILL, SWT.FILL, true, false,
		1, 1);
	gridDataGroup.minimumHeight = 200;

	final GridData gridDataGroup11 = new GridData(SWT.FILL, SWT.FILL, true,
		false, 1, 1);
	gridDataGroup11.minimumHeight = 200;

	shortDescription = new Group(parent, SWT.NONE);
	shortDescription.setLayout(new GridLayout(1, false));
	shortDescription.setLayoutData(gridDataGroup);
	shortDescription.setText(Messages.ViewTestCase_22);

	// insert RichTextEditor

	richTextEditShortDes = new RichText(shortDescription, SWT.WRAP
		| SWT.MULTI | SWT.V_SCROLL);
	final GridData gridDataRichTextEditor = new GridData(SWT.FILL, SWT.CENTER,
		true, false, 1, 1);
	gridDataRichTextEditor.widthHint = 98;
	gridDataRichTextEditor.heightHint = 100;
	richTextEditShortDes.addModifyListener(dirtyListen);
	richTextEditShortDes.setLayoutData(gridDataRichTextEditor);

	preCondition = new Group(parent, SWT.NONE);
	preCondition.setLayout(new GridLayout(1, false));
	preCondition.setLayoutData(gridDataGroup11);
	preCondition.setText(Messages.ViewTestCase_23);

	// insert RichTextEditor

	richTextEditPreCon = new RichText(preCondition, SWT.WRAP | SWT.MULTI
		| SWT.V_SCROLL);
	final GridData gridDataRichTextEditorPre = new GridData(SWT.FILL,
		SWT.CENTER, true, false, 1, 1);
	gridDataRichTextEditorPre.heightHint = 100;
	richTextEditPreCon.addModifyListener(dirtyListen);
	richTextEditPreCon.setLayoutData(gridDataRichTextEditorPre);

	sashSteps = new StepSash(parent, dirtyListen, input);
	sashSteps.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
		1));
	sashSteps.addSashesAtTop(1);

	backgroundColorGroupField = shortDescription.getBackground();
	backgroundColorTextfield = txtName.getBackground();
	richTextEditPreCon.setProjectName(input.getProject().getName());
	richTextEditShortDes.setProjectName(input.getProject().getName());
	doLoad();
	sashSteps.initCompleted();
    }

    /**
     * Sets the dirty bit and fires the property changed
     * 
     * @param dirt
     *            whether the editor is dirty
     */
    private void setDirty(final boolean dirt) {
	dirty = dirt;
	firePropertyChange(PROP_DIRTY);

    }

    @Override
    public void dispose() {
	colorError.dispose();
	super.dispose();
    }

    @Override
    public void setFocus() {
	// Set the focus
	txtName.setFocus();
    }

    private void doLoad() {
	if (input != null) {
	    try {
		txtName.setText(input.getName());
		setPartName(input.getName());
		txtProj.setText(input.getProject().getName());
		final TSMContainer cont = input.getParent();
		txtPackage.setText((cont instanceof TSMPackage) ? cont
			.getName() : Messages.ViewTestCase_32);
		txtCreator.setText(input.getData().getAuthor());
		txtAssignedTo.setText(input.getData().getAssignedTo());
		txtDuration.setText(input.getData().getExpectedDuration());
		comboPriority.select(input.getData().getPriority().ordinal());
		richTextEditShortDes.setFormattedText(input.getData()
			.getShortDescription());
		richTextEditPreCon.setFormattedText(input.getData()
			.getRichTextPrecondition());
		sashSteps.initSteps(input.getData().getSteps());
	    } catch (final ParserConfigurationException e) {
		// new RichTextErrorMessages(getSite().getShell(),
		// "Parsing Error",
		// "BlaBlubb", parsException, 0);
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (final SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (final IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else {
	    txtPackage.setText(Messages.ViewTestCase_33);
	}
	setDirty(false);
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
	final TestCaseDescriptor testCaseData = input.createDataCopy();
	testCaseData.setAssignedTo(txtAssignedTo.getText());
	testCaseData.setAuthor(txtCreator.getText());
	testCaseData.setPriority(priority);
	testCaseData.setExpectedDuration(txtDuration.getText());
	testCaseData.setShortDescription(richTextEditShortDes
		.getFormattedText());
	testCaseData.setRichTextPrecondition(richTextEditPreCon
		.getFormattedText());

	// Setting the Steps
	final List<TestStepDescriptor> stepStrings = sashSteps.getAllSteps();
	// removing empty steps
	for (final Iterator<TestStepDescriptor> iterator = stepStrings
		.iterator(); iterator.hasNext();) {
	    final TestStepDescriptor s = iterator.next();
	    final String strExp = s.getExpectedResult();
	    final String strDes = s.getActionRichText();
	    if (((strDes.equals("<html><p></p></html>") || strDes.isEmpty()) && (strExp
		    .equals("<html><p></p></html>") || strExp.isEmpty()))) {
		iterator.remove();
	    }

	}
	testCaseData.setSteps(stepStrings);

	// change tab name if needed
	final String name = txtName.getText();
	setPartName(name);

	if (validate(name, testCaseData)) {
	    try {
		saveTestcase(name, testCaseData);
		setDirty(false);
	    } catch (final DataModelException e) {
		// Show the error message of the exception
		new ViewTestCaseExceptionDialog(Display.getCurrent()
			.getActiveShell(), e.getMessage()).open();
		return;
	    }
	} else {
	    new ViewTestCaseExceptionDialog(Display.getCurrent()
		    .getActiveShell(), tempErrorMessage).open();
	    monitor.setCanceled(true);
	    return;
	}
    }

    private boolean validate(final String name,
	    final TestCaseDescriptor testCaseData) {
	boolean isValid = true;

	resetFieldBackgrounds();
	if (name.trim().equals("")) {
	    isValid = false;
	    errorMessageNameEmpty();
	}
	if (name.matches(".*[<>?|\".:_\\*/].*") || name.matches(".*\\\\.*")) {
	    isValid = false;
	    errorMessageNameNotValid();
	}

	if (testCaseData.getExpectedDuration().length() > 0) {
	    // check if right format
	    final String[] splittedDuration = testCaseData.getExpectedDuration().split(":");
	    String expectedDuration = testCaseData.getExpectedDuration();
	    try {
		// HH:something
		if (splittedDuration.length > 1) {
		    // HH:MMsomething
		    if (splittedDuration[1].length() == 2) {
			expectedDuration = splittedDuration[0] + ":" + splittedDuration[1];
			TSMTestCase.getDurationFormat().parse(expectedDuration);
		    } else {
			throw new ParseException(null, 0);
		    }

		} else {
		    throw new ParseException(null, 0);
		}
	    } catch (final ParseException e) {
		isValid = false;
		errorMessageDurationNotValid();
	    }
	}
	return isValid;
    }

    @Override
    public void doSaveAs() {
	// TODO Do the Save As operation
    }

    @Override
    public boolean isDirty() {
	return dirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
	return false;
    }

    /**
     * Sets the error message if the name is not valid.
     */
    public void errorMessageNameNotValid() {
	txtName.setBackground(colorError);
	tempErrorMessage = tempErrorMessage + "\n" + ERROR_NAME_CHAR;
    }

    /**
     * Sets the error message if the duration is not valid.
     */
    public void errorMessageDurationNotValid() {
	txtDuration.setBackground(colorError);
	tempErrorMessage = tempErrorMessage + "\n" + ERROR_DURATION_INVALID;
    }

    /**
     * Sets the error message if the duration field is empty.
     */
    public void errorMessageDurationEmpty() {
	txtDuration.setBackground(colorError);
	tempErrorMessage = tempErrorMessage + "\n" + ERROR_DURATION_EMPTY;
    }

    /**
     * Reset background color of the gui input fields.
     */
    public void resetFieldBackgrounds() {
	tempErrorMessage = "";
	preCondition.setBackground(backgroundColorGroupField);
	txtName.setBackground(backgroundColorTextfield);
	txtCreator.setBackground(backgroundColorTextfield);
	txtDuration.setBackground(backgroundColorTextfield);
	shortDescription.setBackground(backgroundColorGroupField);
    }

    /**
     * Sets the error message if the name field is empty.
     */
    public void errorMessageNameEmpty() {
	txtName.setBackground(colorError);
	tempErrorMessage = tempErrorMessage + "\n" + ERROR_NAME_EMPTY;
    }

    private synchronized void saveTestcase(final String name,
	    final TestCaseDescriptor testCase) throws DataModelException {
	// File exists? -> Overwrite it
	if (input != null) {
	    saving = true;
	    input = input.update(name, testCase);
	    saving = false;
	} else {
	    throw new DataModelException(DataModelException.TESTCASE_NULL);
	}
    }

    public static IEditorPart openGUI(final TSMTestCase input)
	    throws PartInitException {
	final IWorkbenchPage page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();
	IEditorPart editor = SWTUtils.findOpenEditor(input);
	if (editor == null) {
	    editor = page.openEditor(input != null ? new ResourceEditorInput(
		    input) : null, ID);
	} else {
	    page.activate(editor);
	}
	return editor;
    }

    @Override
    protected void initInput(final TSMTestCase input) {
	this.input = input;
    }

    @Override
    protected void initInput(final TSMReport input) {
	// Not needed
    }

    @Override
    public TSMTestCase getTestCaseInput() {
	return input;
    }

    @Override
    public TSMReport getReportInput() {
	// Not needed
	return null;
    }

    @Override
    public void dataModelChanged() {
	input = input == null ? null : input.getNewestVersion();
	if ((!saving) && (input == null)) {
	    getEditorSite().getPage().closeEditor(this, false);
	} else {
	    Display.getDefault().asyncExec(new Runnable() {
		@Override
		public void run() {
		    if (txtName.isDisposed() || input == null) {
			return;
		    }
		    setPartName(input.getName());
		    txtName.removeModifyListener(dirtyListen);
		    txtName.setText(input.getName());
		    txtName.addModifyListener(dirtyListen);
		    txtProj.setText(input.getProject().getName());
		    final TSMContainer cont = input.getParent();
		    txtPackage.setText((cont instanceof TSMPackage) ? cont
			    .getName() : Messages.ViewTestCase_32);

		}
	    });
	}
    }

    public void setFocusOnName() {
	txtName.selectAll();
	txtName.setFocus();
    }
}
