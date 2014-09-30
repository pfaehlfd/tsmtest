 /*******************************************************************************
 * Copyright (c) 2012-2013 Jenny Krüwald.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jenny Krüwald - initial version
 *    Wolfgang Kraus - fixed SWTException
 *    Albert Flaig - some fixes
 *    Verena Käfer - i18n
 *******************************************************************************/ 
package net.sourceforge.tsmtest.gui.filter;

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.AbstractDataModel.DataModelObservable;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class FilterView extends ViewPart implements DataModelObservable {
    public static final String ID = "net.sourceforge.tsmtest.gui.filter.Filter"; //$NON-NLS-1$
    private Text textFieldName;
    private Combo textFieldCreator;
    private DateTime dateTimeLastExecution;
    private DateTime dateTimeCreationDate;
    private DateTime dateTimeLastChangedOn;
    private Button buttonNotAssignedTestCases;
    private Button buttonNotExecuted;
    private Button buttonHigh;
    private Button buttonPassed;
    private Button buttonMedium;
    private Button buttonPassedWithAnnotation;
    private Button buttonLastExecutionOn;
    private Button buttonCreationDate;
    private Button buttonLastChangedOn;
    private Button buttonLow;
    private Button buttonFailed;
    private Label labelCreator;
    private Button buttonTestcases;

    public FilterView() {
	DataModel.getInstance().register(this);
    }

    /**
     * Create contents of the view part.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(final Composite parent) {
	final Composite container = new Composite(parent, SWT.NONE);
	container.setLayout(new GridLayout(5, true));
	Group groupSearchMode;

	groupSearchMode = new Group(container, SWT.NONE);
	groupSearchMode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 2, 1));
	groupSearchMode.setText(Messages.FilterView_0);
	groupSearchMode.setLayout(new GridLayout(2, true));

	buttonTestcases = new Button(groupSearchMode, SWT.RADIO);
	buttonTestcases.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
		false, 1, 1));
	buttonTestcases.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		testCaseSelected();
		labelCreator.setText(Messages.FilterView_1);
	    }

	});
	buttonTestcases.setText(Messages.FilterView_2);
	buttonTestcases.setSelection(true);

	Button buttonProtocols;
	buttonProtocols = new Button(groupSearchMode, SWT.RADIO);
	buttonProtocols.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
		false, 1, 1));
	buttonProtocols.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setFilterForTestCases(false);
		buttonHigh.setEnabled(false);
		buttonMedium.setEnabled(false);
		buttonLow.setEnabled(false);
		buttonLastExecutionOn.setEnabled(false);
		dateTimeLastExecution.setEnabled(false);
		buttonLastChangedOn.setEnabled(false);
		dateTimeLastChangedOn.setEnabled(false);
		buttonPassed.setEnabled(true);
		buttonPassedWithAnnotation.setEnabled(true);
		buttonFailed.setEnabled(true);
		labelCreator.setText(Messages.FilterView_3);
		textFieldCreator.setItems(TSMReport.getAllTesters());
		buttonNotAssignedTestCases.setEnabled(false);
		buttonNotExecuted.setEnabled(true);
	    }
	});
	buttonProtocols.setText(Messages.FilterView_4);
	new Label(container, SWT.NONE);
	new Label(container, SWT.NONE);
	new Label(container, SWT.NONE);

	final Label labelPriority = new Label(container, SWT.NONE);
	labelPriority.setText(Messages.FilterView_5);

	final Label labelStatus = new Label(container, SWT.NONE);
	labelStatus.setText(Messages.FilterView_6);

	final Label labelName = new Label(container, SWT.NONE);
	labelName.setText(Messages.FilterView_7);
	new Label(container, SWT.NONE);

	labelCreator = new Label(container, SWT.NONE);
	labelCreator.setText(Messages.FilterView_8);

	buttonHigh = new Button(container, SWT.CHECK);
	buttonHigh.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setPriority(PriorityType.high,
			buttonHigh.getSelection());
	    }
	});
	buttonHigh.setText(Messages.FilterView_9);

	buttonPassed = new Button(container, SWT.CHECK);
	buttonPassed.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setStatus(StatusType.passed,
			buttonPassed.getSelection());
	    }
	});
	buttonPassed.setText(Messages.FilterView_10);

	textFieldName = new Text(container, SWT.BORDER);
	textFieldName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	textFieldName.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(final KeyEvent e) {
		FilterModel.getInstance().setName(textFieldName.getText());
	    }
	});

	textFieldCreator = new Combo(container, SWT.BORDER);
	textFieldCreator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
		1));
	textFieldCreator.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(final KeyEvent e) {
		FilterModel.getInstance().setCreator(textFieldCreator.getText());
	    }
	});
	textFieldCreator.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		FilterModel.getInstance().setCreator(textFieldCreator.getText());
	    }
	});

	buttonMedium = new Button(container, SWT.CHECK);
	buttonMedium.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setPriority(PriorityType.medium,
			buttonMedium.getSelection());
	    }
	});
	buttonMedium.setText(Messages.FilterView_11);

	buttonPassedWithAnnotation = new Button(container, SWT.CHECK);
	buttonPassedWithAnnotation.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setStatus(
			StatusType.passedWithAnnotation,
			buttonPassedWithAnnotation.getSelection());
	    }
	});
	buttonPassedWithAnnotation.setText(Messages.FilterView_12);

	buttonLastExecutionOn = new Button(container, SWT.CHECK);
	buttonLastExecutionOn.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		if (buttonLastExecutionOn.getSelection()) {
		    dateTimeLastExecution.setEnabled(true);
		    buttonNotExecuted.setEnabled(false);
		    FilterModel.getInstance().setLastExecution(
			    getDate(dateTimeLastExecution));
		} else {
		    buttonNotExecuted.setEnabled(true);
		    dateTimeLastExecution.setEnabled(false);
		    FilterModel.getInstance().setLastExecution(null);
		}
	    }
	});
	buttonLastExecutionOn.setText(Messages.FilterView_13);

	buttonCreationDate = new Button(container, SWT.CHECK);
	buttonCreationDate.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		if (buttonCreationDate.getSelection()) {
		    dateTimeCreationDate.setEnabled(true);
		    FilterModel.getInstance().setCreationTime(
			    getDate(dateTimeCreationDate));
		} else {
		    dateTimeCreationDate.setEnabled(false);
		    FilterModel.getInstance().setCreationTime(null);
		}
	    }
	});
	buttonCreationDate.setText(Messages.FilterView_14);

	buttonLastChangedOn = new Button(container, SWT.CHECK);
	buttonLastChangedOn.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		if (buttonLastChangedOn.getSelection()) {
		    dateTimeLastChangedOn.setEnabled(true);
		    FilterModel.getInstance()
			    .setLastChange(getDate(dateTimeLastChangedOn));
		} else {
		    dateTimeLastChangedOn.setEnabled(false);
		    FilterModel.getInstance().setLastChange(null);
		}
	    }
	});
	buttonLastChangedOn.setText(Messages.FilterView_15);

	buttonLow = new Button(container, SWT.CHECK);
	buttonLow.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setPriority(PriorityType.low,
			buttonLow.getSelection());
	    }
	});
	buttonLow.setText(Messages.FilterView_16);

	buttonFailed = new Button(container, SWT.CHECK);
	buttonFailed.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setStatus(StatusType.failed,
			buttonFailed.getSelection());
	    }
	});
	buttonFailed.setText(Messages.FilterView_17);

	dateTimeLastExecution = new DateTime(container, SWT.BORDER);
	dateTimeLastExecution.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setLastExecution(getDate(dateTimeLastExecution));
	    }
	});
	dateTimeLastExecution.setEnabled(false);
	dateTimeCreationDate = new DateTime(container, SWT.BORDER);
	dateTimeCreationDate.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setCreationTime(getDate(dateTimeCreationDate));
	    }
	});
	dateTimeCreationDate.setEnabled(false);
	dateTimeLastChangedOn = new DateTime(container, SWT.BORDER);
	dateTimeLastChangedOn.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setLastChange(getDate(dateTimeLastChangedOn));
	    }
	});
	dateTimeLastChangedOn.setEnabled(false);
	new Label(container, SWT.NONE);

	buttonNotExecuted = new Button(container, SWT.CHECK);
	buttonNotExecuted.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setStatus(StatusType.notExecuted,
			buttonNotExecuted.getSelection());
		if (buttonTestcases.getSelection()) {
		    buttonLastExecutionOn.setEnabled(!buttonNotExecuted.getSelection());
		} else if (buttonNotExecuted.getSelection()) {
		    buttonLastExecutionOn.setSelection(false);
		}
	    }
	});
	buttonNotExecuted.setText(Messages.FilterView_18);

	buttonNotAssignedTestCases = new Button(container, SWT.CHECK);
	buttonNotAssignedTestCases.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setUnassigned(
			buttonNotAssignedTestCases.getSelection());
	    }
	});
	buttonNotAssignedTestCases.setText(Messages.FilterView_19);

	final Button buttonReset = new Button(container, SWT.NONE);
	buttonReset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
		2, 1));
	buttonReset.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		buttonHigh.setSelection(false);
		buttonMedium.setSelection(false);
		buttonLow.setSelection(false);
		buttonPassed.setSelection(false);
		buttonPassedWithAnnotation.setSelection(false);
		buttonFailed.setSelection(false);
		buttonNotExecuted.setSelection(false);
		buttonNotAssignedTestCases.setSelection(false);
		buttonLastExecutionOn.setSelection(false);
		buttonCreationDate.setSelection(false);
		buttonLastChangedOn.setSelection(false);
		textFieldName.setText(""); //$NON-NLS-1$
		textFieldCreator.setText(""); //$NON-NLS-1$
		dateTimeLastExecution.setEnabled(false);
		dateTimeLastExecution.setDate(Calendar.getInstance().get(Calendar.YEAR),
			Calendar.getInstance().get(Calendar.MONTH), Calendar
				.getInstance().get(Calendar.DAY_OF_MONTH));
		dateTimeCreationDate.setEnabled(false);
		dateTimeLastChangedOn.setDate(Calendar.getInstance().get(Calendar.YEAR),
			Calendar.getInstance().get(Calendar.MONTH), Calendar
				.getInstance().get(Calendar.DAY_OF_MONTH));
		dateTimeLastChangedOn.setEnabled(false);
		dateTimeLastChangedOn.setDate(Calendar.getInstance().get(Calendar.YEAR),
			Calendar.getInstance().get(Calendar.MONTH), Calendar
				.getInstance().get(Calendar.DAY_OF_MONTH));
		FilterModel.getInstance().reset();
	    }
	});
	buttonReset.setText(Messages.FilterView_22);

	createActions();
	initializeToolBar();
	initializeMenu();

	// because NullPointerException
	testCaseSelected();
    }

    private Date getDate(final DateTime widget) {
	final Calendar calendar = Calendar.getInstance();
	calendar.set(widget.getYear(), widget.getMonth(), widget.getDay(),
		widget.getHours(), widget.getMinutes(), widget.getSeconds());
	return calendar.getTime();
    }

    /**
     * Create the actions.
     */
    private void createActions() {
	// Create the actions
    }

    /**
     * Initialize the toolbar.
     */
    private void initializeToolBar() {
	final IToolBarManager toolbarManager = getViewSite().getActionBars()
		.getToolBarManager();
    }

    /**
     * Initialize the menu.
     */
    private void initializeMenu() {
	final IMenuManager menuManager = getViewSite().getActionBars()
		.getMenuManager();
    }

    @Override
    public void setFocus() {
	// Set the focus
    }

    private void testCaseSelected() {
	FilterModel.getInstance().setFilterForTestCases(true);
	buttonPassed.setEnabled(false);
	buttonPassedWithAnnotation.setEnabled(false);
	buttonFailed.setEnabled(false);
	buttonHigh.setEnabled(true);
	buttonMedium.setEnabled(true);
	buttonLow.setEnabled(true);
	buttonLastExecutionOn.setEnabled(true);
	dateTimeLastExecution.setEnabled(buttonLastExecutionOn.getSelection());
	buttonLastChangedOn.setEnabled(true);
	dateTimeLastChangedOn.setEnabled(buttonLastChangedOn.getSelection());
	buttonNotAssignedTestCases.setEnabled(true);
	textFieldCreator.setItems(TSMTestCase.getAllCreators());
	if (buttonNotExecuted.getSelection()) {
	    //Disable "last execution on" button when filtering for status "not executed".
	    buttonLastExecutionOn.setEnabled(false);
	} else if (buttonLastExecutionOn.getSelection()) {
	    //Disable "not executed" button when filtering for "last execution on".
	    buttonNotExecuted.setEnabled(false);
	}
    }

    @Override
    public void dataModelChanged() {
	Display.getDefault().asyncExec(new Runnable() {
	    @Override
	    public void run() {
		if (buttonTestcases.isDisposed()) {
		    return;
		}
		if (buttonTestcases.getSelection()) {
		    textFieldCreator.setItems(TSMTestCase.getAllCreators());
		} else {
		    textFieldCreator.setItems(TSMReport.getAllTesters());
		}
	    }

	});
    }
}
