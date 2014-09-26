 /*******************************************************************************
 * Copyright (c) 2012-2013 Wolfgang Kraus and Bernhard Wetzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wolfgang Kraus - initial version
 *    Bernhard Wetzel - initial version
 *    Tobias Hirning - some refactoring, i18n
 *    Daniel Hertl - added rich text editor
 *    Albert Flaig - data model refactoring
 *    Verena Käfer - worked on lastChange attribute
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.runtest.view;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.SWTUtils;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.EditorPartInput;
import net.sourceforge.tsmtest.datamodel.ResourceEditorInput;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.AbstractDataModel.DataModelObservable;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;
import net.sourceforge.tsmtest.gui.ResourceManager;
import net.sourceforge.tsmtest.gui.newtestcase.view.ViewTestCase;
import net.sourceforge.tsmtest.gui.newtestcase.view.ViewTestCaseExceptionDialog;
import net.sourceforge.tsmtest.gui.report.ViewReport;
import net.sourceforge.tsmtest.gui.richtexteditor.RichText;
import net.sourceforge.tsmtest.gui.runtest.model.TestResult;
import net.sourceforge.tsmtest.wizards.NewTestcaseWizard;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;

/**
 * An Editor to execute a TestCaseObject
 * 
 * @author Wolfgang Kraus
 * @author Bernhard Wetzel
 * 
 */
public class EditorRunTest extends EditorPartInput implements
	DataModelObservable {

    public static final String ID = "net.sourceforge.tsmtest.gui.runtest.view.editorruntest"; //$NON-NLS-1$
    private static final String INITTIME = "00:00:00"; //$NON-NLS-1$
    // Our testcase to execute
    private TSMTestCase input;

    private RichText richTextPreCon;
    private Text txtTime;
    private Text txtTestCaseName;
    private Text txtTester;
    private Text txtRevision;
    private Composite parent;
    private static String lastRevision = 0 + "";
    private static String lastTester = "";
    private Button btnDone;

    private RunTestStepSash stepSash;

    // Whether there is unsaved input
    private boolean dirty;

    // Used for the elapsed time
    private Thread thread;
    private boolean stopthread;
    private boolean pausethread = false;
    private Calendar startTime = Calendar.getInstance();
    private long currentTime = 0;
    private Button btnTimer;

    private boolean descriptionChanged = false;

    private String[] buttonName = { "OK" };
    private Color red = new Color(null, 255, 100, 100);
    private Color white = new Color(null, 255, 255, 255);

    // --------------------------

    private static Vector<EditorRunTest> sessions;
    private final Logger log = Logger.getLogger(EditorRunTest.class);

    private static void pauseAllTimers() {
	for (final EditorRunTest editor : sessions) {
	    editor.changeTimerState(true);
	}
    }

    /**
     * Changes the timer to the given state, updates the current time in
     * <code>diff</code> if it pauses
     * 
     * @param pause
     *            whether the timer should be paused
     */
    private void changeTimerState(final boolean pause) {
	if (pause == pausethread) {
	    return;
	}
	if (pausethread) {
	    startTime = Calendar.getInstance();
	} else {
	    final long diff = Calendar.getInstance().getTimeInMillis()
		    - startTime.getTimeInMillis();

	    currentTime += diff;
	}
	if (pause) {
	    btnTimer.setImage(ResourceManager.getImgStart());
	} else {
	    btnTimer.setImage(ResourceManager.getImgPause());
	}
	pausethread = pause;
    }

    /**
     * Changes the state of the timer, updates the current time in
     * <code>diff</code> if it pauses
     */
    private void changeTimerState() {
	changeTimerState(!pausethread);
    }

    /**
     * Asks for confirmation to load the execution data if the time is not
     * empty
     * 
     * @return a MessageDialog whether to load the saved execution data.
     */
    private boolean confirmLoading() {
	if (input.getData().getRealDuration().isEmpty()) {
	    return false;
	}

	return MessageDialog.open(MessageDialog.QUESTION, null,
		Messages.EditorRunTest_2, Messages.EditorRunTest_3, SWT.NONE);
    }

    /**
     * Creates a new Vector to hold our sessions
     */
    private synchronized void initVector() {
	if (sessions == null) {
	    sessions = new Vector<EditorRunTest>(3, 1);
	}
    }

    @Override
    public void createPartControl(final Composite parent) {
	this.parent = parent;
	final Color white = new Color(null, 255, 255, 255);
	this.parent.setLayout(new GridLayout(1, false));

	parent.addDisposeListener(new DisposeListener() {

	    /*
	     * Detach a non-ui thread to extract and save the images
	     */
	    @Override
	    public void widgetDisposed(final DisposeEvent e) {
		final Job j = new Job("Saving images") {
		    @Override
		    protected IStatus run(final IProgressMonitor monitor) {
			// TODO: add Method for images
			return Status.OK_STATUS;
		    }

		};
		j.schedule();
	    }

	});

	DataModel.getInstance().register(this);

	if (sessions == null) {
	    initVector();
	}

	final ModifyListener listen = new ModifyListener() {
	    @Override
	    public void modifyText(final ModifyEvent e) {
		setDirty(true);
	    }
	};

	final ModifyListener lastChangedOnListener = new ModifyListener() {
	    @Override
	    public void modifyText(final ModifyEvent e) {
		descriptionChanged = true;
	    }
	};
	// Apparently the tabbar is not to be used anymore
	// TabBar tb = new TabBar(parent, SWT.BORDER);
	// tb.addListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// changeTimerState(true);
	// }
	// });
	// tb.setFile(input);

	final Group mainSettings = new Group(this.parent, SWT.CENTER);
	mainSettings.setLayout(new GridLayout(1, true));
	mainSettings.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 1, 1));
	final Composite cmpFirst = new Composite(mainSettings, SWT.NONE);

	cmpFirst.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1));
	cmpFirst.setLayout(new GridLayout(11, false));

	final Button btnEditTest = new Button(cmpFirst, SWT.NONE);
	btnEditTest.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		final Display d = txtTestCaseName.getDisplay();
		closeEditor(true);

		d.asyncExec(new Runnable() {
		    @Override
		    public void run() {
			try {
			    ViewTestCase.openGUI(input);
			} catch (final PartInitException e) {
			    log.error(e.getMessage());
			    for (final StackTraceElement st : e.getStackTrace()) {
				log.error(st.toString());
			    }
			}
		    }
		});
	    }
	});
	btnEditTest.setText(Messages.EditorRunTest_4);

	final Button btnNewTest = new Button(cmpFirst, SWT.NONE);
	btnNewTest.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		try {
		    final IStructuredSelection iss = new StructuredSelection(
			    input.getParent());
		    NewTestcaseWizard.open(iss);
		} catch (final CoreException e1) {
		    log.error(Messages.EditorRunTest_5);
		    log.error(e1.getMessage());
		    for (final StackTraceElement st : e1.getStackTrace()) {
			log.error(st.toString());
		    }

		}
	    }
	});
	btnNewTest.setText(Messages.EditorRunTest_6);
	final Label lblSpace1 = new Label(cmpFirst, SWT.FILL);
	lblSpace1
		.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	txtTestCaseName = new Text(cmpFirst, SWT.READ_ONLY);
	txtTestCaseName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
		false, 3, 1));
	Font font = txtTestCaseName.getFont();
	int height = 16;
	int style = 0;
	String name = null;
	for (final FontData data : font.getFontData()) {
	    height = data.getHeight();
	    style = data.getStyle() | SWT.BOLD;
	    name = data.getName();
	}
	font = new Font(font.getDevice(), name, height, style);
	txtTestCaseName.setFont(font);
	font.dispose();
	// Begin short description --------------------------------------------
	final Label lbShortDes = new Label(cmpFirst, SWT.NONE);
	lbShortDes.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
		false, 1, 1));
	lbShortDes.setImage(ResourceManager.getImgInformation());

	final ToolTipListener toolTipListener = new ToolTipListener();
	toolTipListener.setDisplayShell(getSite().getShell());
	String shortDes = input.getData().getShortDescription();
	if (shortDes.isEmpty() || shortDes.matches("<html><p></p></html>")) { //$NON-NLS-1$
	    shortDes = Messages.EditorRunTest_0;
	}
	toolTipListener.setHtmlToolTip(input.getProject().getName(), shortDes);

	lbShortDes.addListener(SWT.Dispose, toolTipListener);
	lbShortDes.addListener(SWT.KeyDown, toolTipListener);
	lbShortDes.addListener(SWT.MouseExit, toolTipListener);
	lbShortDes.addListener(SWT.MouseEnter, toolTipListener);
	lbShortDes.addListener(SWT.MouseDown, toolTipListener);
	lbShortDes.addListener(SWT.MouseHover, toolTipListener);
	// End short description ----------------------------------------------

	final Label lblSpace2 = new Label(cmpFirst, SWT.FILL);
	lblSpace2
		.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	final Label lblTimer = new Label(cmpFirst, SWT.NONE);
	lblTimer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
		false, 1, 1));
	lblTimer.setText(Messages.EditorRunTest_9);

	txtTime = new Text(cmpFirst, SWT.NONE);
	txtTime.setEditable(false);
	txtTime.setText("00:00:00"); //$NON-NLS-1$
	final GridData gd_txtTime = new GridData(SWT.LEFT, SWT.CENTER, false,
		false, 1, 1);
	txtTime.setLayoutData(gd_txtTime);
	txtTime.setBackground(white);

	btnTimer = new Button(cmpFirst, SWT.NONE);
	btnTimer.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		changeTimerState();
	    }
	});
	btnTimer.setImage(ResourceManager.getImgPause());

	final GridData gd_button = new GridData(SWT.LEFT, SWT.CENTER, false,
		false, 1, 1);
	gd_button.widthHint = 34;
	btnTimer.setLayoutData(gd_button);
	btnTimer.setImage(ResourceManager.getImgPause());

	final Composite cmpSecond = new Composite(mainSettings, SWT.None);
	cmpSecond.setLayout(new GridLayout(7, false));
	cmpSecond
		.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	final Label lbRevision = new Label(cmpSecond, SWT.NONE);
	lbRevision.setText(Messages.EditorRunTest_22);

	txtRevision = new Text(cmpSecond, SWT.BORDER);
	txtRevision.setEditable(true);
	txtRevision.setTextLimit(10);
	final GridData gdRevision = new GridData(SWT.FILL, SWT.CENTER, false,
		false, 2, 1);
	gdRevision.minimumWidth = 180;
	gdRevision.widthHint = 180;
	txtRevision.setLayoutData(gdRevision);
	txtRevision.addModifyListener(listen);
	txtRevision.addModifyListener(lastChangedOnListener);

	final Label lblSpace3 = new Label(cmpSecond, SWT.None);
	lblSpace3
		.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	final Label lbTester = new Label(cmpSecond, SWT.NONE);
	lbTester.setText(Messages.EditorRunTest_11);

	txtTester = new Text(cmpSecond, SWT.BORDER);
	txtTester.setEditable(true);
	final GridData gdTester = new GridData(SWT.FILL, SWT.CENTER, false,
		false, 2, 1);
	gdTester.minimumWidth = 180;
	gdTester.widthHint = 180;
	txtTester.setLayoutData(gdTester);
	txtTester.addModifyListener(listen);
	txtTester.addModifyListener(lastChangedOnListener);

	final GridData gd_Pre = new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1);
	gd_Pre.minimumHeight = 100;
	gd_Pre.heightHint = 100;
	gd_Pre.minimumWidth = this.parent.getSize().x;

	final Group grpPreCon = new Group(this.parent, SWT.NONE);
	grpPreCon.setLayout(new GridLayout());
	grpPreCon.setLayoutData(gd_Pre);
	grpPreCon.setText(Messages.EditorRunTest_12);

	richTextPreCon = new RichText(grpPreCon, SWT.V_SCROLL);
	richTextPreCon.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		true));
	richTextPreCon.addModifyListener(lastChangedOnListener);
	richTextPreCon.addModifyListener(listen);
	richTextPreCon.setProjectName(input.getProject().getName());

	//Indicates whether the old execution data is loaded.
	boolean load = false;
	if (!input.getData().getRealDuration().equals(INITTIME)) {
	    if (input.getData().getRealDuration().split(":").length == 3) {
		load = confirmLoading();
	    }
	}

	//Load old execution data.
	if (load) {
	    log.debug(Messages.EditorRunTest_13);
	    final String[] times = input.getData().getRealDuration().split(":"); //$NON-NLS-1$
	    try {
		final int t0 = Integer.parseInt(times[0]);
		final int t1 = Integer.parseInt(times[1]);
		final int t2 = Integer.parseInt(times[2]);
		currentTime += t0 * 60 * 60 * 1000;
		currentTime += t1 * 60 * 1000;
		currentTime += t2 * 1000;
	    } catch (final NumberFormatException e) {
		log.error(Messages.EditorRunTest_15
			+ input.getData().getRealDuration()
			+ Messages.EditorRunTest_16 + e.getStackTrace());
	    }
	} else {
	    log.debug(Messages.EditorRunTest_17);
	}

	// --- Begin header SashForm ------------------------------------------
	stepSash = new RunTestStepSash(this.parent, listen,
		lastChangedOnListener, load, input);
	stepSash.initSteps(input.getData().getSteps());

	final Composite btnComponent = new Composite(this.parent, SWT.NONE);
	btnComponent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
		false, 1, 1));
	btnComponent.setLayout(new GridLayout(1, false));

	final Composite Actions = new Composite(btnComponent, SWT.NONE);
	Actions.setLayout(new GridLayout(1, false));
	Actions.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
		1, 1));

	btnDone = new Button(Actions, SWT.NONE);
	btnDone.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseDown(final MouseEvent e) {
		saveTestProtocoll();
	    }
	});
	final GridData gd_btnDone = new GridData(SWT.FILL, SWT.CENTER, false,
		false, 1, 1);
	gd_btnDone.widthHint = 565;
	btnDone.setLayoutData(gd_btnDone);
	btnDone.setText(Messages.EditorRunTest_18);
	setFields();

	white.dispose();
	setFocus();
	txtRevision.setSelection(0, txtRevision.getText().length());
	sessions.add(this);
    }

    private void closeEditor(final boolean save) {
	getEditorSite().getPage().closeEditor(this, save);
    }

    @Override
    public void dispose() {
	sessions.remove(this);
	stopthread = true;
	super.dispose();
    }

    private TestCaseDescriptor updateTestCaseDescriptor() {
	changeTimerState(true);

	final TestCaseDescriptor testCase = input.createDataCopy();

	if (descriptionChanged) {
	    testCase.setLastChangedOn(new Date());
	}

	testCase.setRealDuration(txtTime.getText());

	testCase.setAssignedTo(txtTester.getText());
	testCase.setRichTextPrecondition(richTextPreCon.getFormattedText());
	testCase.setSteps(stepSash.getAllSteps());

	int revision = 0;
	try {
	    if (txtRevision.getText().isEmpty()) {
		txtRevision.setBackground(red);
		final MessageDialog diag = new MessageDialog(null,
			Messages.EditorRunTest_1, null,
			Messages.EditorRunTest_19, 0, buttonName, 0);
		diag.setBlockOnOpen(true);
		diag.open();
		diag.close();
		return null;
	    } else if (Long.parseLong(txtRevision.getText()) == 0) {
		final MessageBox messageBox = new MessageBox(parent.getShell(),
			SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setMessage(Messages.EditorRunTest_14);
		messageBox.setText(Messages.EditorRunTest_15);
		if (messageBox.open() == SWT.NO) {
		    return null;
		}
	    } else if (Long.parseLong(txtRevision.getText()) < 0) {
		final MessageBox messageBox = new MessageBox(parent.getShell(),
			SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setMessage(Messages.EditorRunTest_10);
		messageBox.setText(Messages.EditorRunTest_1);
		if (messageBox.open() == SWT.YES) {
		    txtRevision.setText(Math.abs(Long.parseLong(txtRevision
			    .getText())) + "");
		}
	    } else if (Long.parseLong(txtRevision.getText()) > 2147483647) {
		txtRevision.setBackground(red);
		final MessageDialog diag = new MessageDialog(null,
			Messages.EditorRunTest_1, null,
			Messages.EditorRunTest_7, 0, buttonName, 0);
		diag.setBlockOnOpen(true);
		diag.open();
		diag.close();
		return null;
	    }
	    revision = Integer.parseInt(txtRevision.getText());
	    txtRevision.setBackground(white);
	} catch (final NumberFormatException e) {
	    txtRevision.setBackground(red);
	    final MessageDialog diag = new MessageDialog(null,
		    Messages.EditorRunTest_1, null, Messages.EditorRunTest_8,
		    0, buttonName, 0);
	    diag.setBlockOnOpen(true);
	    diag.open();
	    diag.close();
	    return null;
	}
	testCase.setRevisionNumber(revision);

	return testCase;
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
	final TestCaseDescriptor testCase = updateTestCaseDescriptor();

	if (txtTester.getText().trim().equals("")) { //$NON-NLS-1$
	    new ViewTestCaseExceptionDialog(Display.getCurrent()
		    .getActiveShell(), Messages.EditorRunTest_20).open();
	    return;
	} else if (testCase == null) {
	    return;
	}

	monitor.subTask(Messages.EditorRunTest_21);

	try {
	    input = input.update(testCase);
	} catch (final DataModelException e) {
	    log.error(e.getMessage());
	    for (final StackTraceElement st : e.getStackTrace()) {
		log.error(st.toString());
	    }
	}

	monitor.done();
	setDirty(false);
	changeTimerState(false);
    }

    private void saveTestProtocoll() {

	final TestCaseDescriptor testCase = updateTestCaseDescriptor();
	if (testCase == null) {
	    return;
	}
	// getting the worst status
	StatusType worstStatus = StatusType.passed;
	for (final TestStepDescriptor ts : testCase.getSteps()) {
	    final StatusType sType = ts.getStatus();
	    if (worstStatus == StatusType.failed) {
		break;
	    } else if (worstStatus == StatusType.passed) {
		switch (sType) {
		case passed:
		    break;
		default:
		    worstStatus = sType;
		}
	    } else if (worstStatus == StatusType.passedWithAnnotation) {
		switch (sType) {
		case passed:
		case passedWithAnnotation:
		    break;
		default:
		    worstStatus = sType;
		}
	    } else if (worstStatus == StatusType.notExecuted) {
		switch (sType) {
		case passed:
		case passedWithAnnotation:
		case notExecuted:
		    break;
		default:
		    worstStatus = sType;
		}
	    }
	}

	// getting revision
	int revision = 0;
	try {
	    revision = Integer.parseInt(txtRevision.getText());
	} catch (final NumberFormatException e) {
	    return;
	}
	setLastRevision(revision + "");
	setLastTester(txtTester.getText());
	testCase.setRevisionNumber(revision);

	// Getting the final description for the run
	final DialogRunTest dialog = new DialogRunTest(txtTime.getText(),
		txtTester.getText(), worstStatus, input.getProject().getName());
	StatusType caseStatus;

	if (dialog.open() == Window.OK) {
	    final TestResult testResult = dialog.getRunTestValue();
	    testCase.setRichTextResult(testResult.getDescription());
	    caseStatus = testResult.getState();
	    if (testResult.isUpdateTime()) {
		testCase.setExpectedDuration(testCase.getRealDuration());
	    }
	    testCase.setStatus(caseStatus);
	} else {
	    changeTimerState(false);
	    return;
	}

	if (caseStatus == StatusType.failed) {
	    testCase.increaseNumberOfFailures();
	}
	testCase.increaseNumberOfExecutions();
	testCase.setLastExecution(new Date());

	try {
	    final TSMReport report = input.createReport(testCase.deepClone());

	    currentTime = 0;
	    testCase.setRealDuration(INITTIME);

	    input = input.update(testCase);

	    setDirty(false);
	    closeEditor(true);
	    ViewReport.openGUI(report);

	} catch (final DataModelException e) {
	    log.error(e.getMessage());
	    for (final StackTraceElement st : e.getStackTrace()) {
		log.error(st.toString());
	    }
	} catch (final PartInitException e) {
	    log.error(e.getMessage());
	    for (final StackTraceElement st : e.getStackTrace()) {
		log.error(st.toString());
	    }
	}

    }

    private void setLastTester(final String tester) {
	lastTester = tester;
    }

    @Override
    public void doSaveAs() {
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
     * Sets the dirty bit and fires the property changed if it changed
     * 
     * @param dirt
     *            whether the editor is dirty
     */
    private void setDirty(final boolean dirt) {
	if (dirty != dirt) {
	    // eclipse seems to do quite much if a property changes,
	    // so we'll keep it to a minimum
	    dirty = dirt;
	    firePropertyChange(PROP_DIRTY);
	}
    }

    /**
     * Sets the text fields of the editor
     * 
     * @see EditorRunTest.setSteps()
     */
    private void setFields() {

	if (input.getData().getRichTextPrecondition() != null) {
	    try {
		richTextPreCon.setFormattedText(input.getData()
			.getRichTextPrecondition());
	    } catch (final ParserConfigurationException e) {
		log.error(e.getMessage());
		for (final StackTraceElement st : e.getStackTrace()) {
		    log.error(st.toString());
		}
	    } catch (final SAXException e) {
		log.error(e.getMessage());
		for (final StackTraceElement st : e.getStackTrace()) {
		    log.error(st.toString());
		}
	    } catch (final IOException e) {
		log.error(e.getMessage());
		for (final StackTraceElement st : e.getStackTrace()) {
		    log.error(st.toString());
		}
	    }
	}

	if (input.getName() != null) {
	    txtTestCaseName.setText(input.getName());
	}

	if ((input.getData().getAssignedTo() != null)
		&& (input.getData().getAssignedTo().length() > 0)) {
	    txtTester.setText(input.getData().getAssignedTo());
	} else {
	    //If none tester was set we use the user name.
	    if (lastTester.equals("")) {
		txtTester.setText(System.getProperty("user.name")); //$NON-NLS-1$
	    } else {
		txtTester.setText(lastTester);
	    }
	}

	txtRevision.setText(getLastRevision());

	setDirty(false);
	descriptionChanged = false;
    }

    @Override
    public void setFocus() {
	pauseAllTimers();
	changeTimerState(false);
	txtRevision.setFocus();
    }

    /**
     * Used for updating our real duration
     * 
     */
    private class TimeRunner implements Runnable {
	@Override
	public void run() {
	    final Runnable runnable = new Runnable() {

		@Override
		public void run() {
		    if (pausethread) {
			return;
		    }

		    final long diff = Calendar.getInstance().getTimeInMillis()
			    - startTime.getTimeInMillis();

		    if (txtTime != null && !txtTime.isDisposed()) {
			txtTime.setText(String.format(
				"%02d:%02d:%02d", //$NON-NLS-1$
				TimeUnit.MILLISECONDS.toHours(currentTime
					+ diff),
				TimeUnit.MILLISECONDS.toMinutes(currentTime
					+ diff) % 60,
				TimeUnit.MILLISECONDS.toSeconds(currentTime
					+ diff) % 60));
		    }
		}
	    };
	    while (!stopthread) {
		try {
		    Thread.sleep(200);
		} catch (final InterruptedException e) {
		}
		Display.getDefault().syncExec(runnable);
	    }
	}
    }

    @Override
    protected void initInput(final TSMTestCase input) {
	this.input = input;

	setPartName(input.getName());

	stopthread = false;
	thread = new Thread(new TimeRunner());
	thread.start();
    }

    @Override
    protected void initInput(final TSMReport input) {
	// not needed
    }

    @Override
    public TSMTestCase getTestCaseInput() {
	return input;
    }

    @Override
    public TSMReport getReportInput() {
	// not needed
	return null;
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
	    // check if this editor is instance of EditorRunTest
	    if (editor instanceof EditorRunTest) {
		page.activate(editor);
	    } else {
		page.closeEditor(editor, true);
		editor = page.openEditor(
			input != null ? new ResourceEditorInput(input) : null,
			ID);
	    }
	}
	return editor;
    }

    @Override
    public void dataModelChanged() {
	if (input == null) {
	    closeEditor(false);
	} else {
	    input = input.getNewestVersion();
	    Display.getDefault().asyncExec(new Runnable() {
		@Override
		public void run() {
		    if (txtTestCaseName.isDisposed()) {
			return;
		    }
		    setPartName(input.getName());
		    txtTestCaseName.setText(input.getName());
		}
	    });
	}
    }

    public String getLastRevision() {
	return lastRevision;
    }

    public void setLastRevision(final String lastRevision) {
	EditorRunTest.lastRevision = lastRevision;
    }

}
