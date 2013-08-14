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
    private Text text;
    private Combo text_1;
    private DateTime dateTime;
    private DateTime dateTime_1;
    private DateTime dateTime_2;
    private Button btnUnassignedTestCases;
    protected Button btnNotExecuted;
    private Button btnHigh;
    private Button btnPassed;
    private Button btnMedium;
    private Button btnPassedWithAnnotation;
    private Button btnLastExecution;
    private Button btnCreationDate;
    private Button btnLastChange;
    private Button btnLow;
    private Button btnFailed;
    private Label lblCreator;
    private Group grpSuchmodus;
    private Button btnTestcases;
    private Button btnReports;

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

	grpSuchmodus = new Group(container, SWT.NONE);
	grpSuchmodus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 2, 1));
	grpSuchmodus.setText(Messages.FilterView_0);
	grpSuchmodus.setLayout(new GridLayout(2, true));

	btnTestcases = new Button(grpSuchmodus, SWT.RADIO);
	btnTestcases.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
		false, 1, 1));
	btnTestcases.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		testCaseSelected();
		lblCreator.setText(Messages.FilterView_1);
	    }

	});
	btnTestcases.setText(Messages.FilterView_2);
	btnTestcases.setSelection(true);

	btnReports = new Button(grpSuchmodus, SWT.RADIO);
	btnReports.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
		false, 1, 1));
	btnReports.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setTestCases(false);
		btnHigh.setEnabled(false);
		btnMedium.setEnabled(false);
		btnLow.setEnabled(false);
		btnLastExecution.setEnabled(false);
		dateTime.setEnabled(false);
		btnLastChange.setEnabled(false);
		dateTime_2.setEnabled(false);
		btnPassed.setEnabled(true);
		btnPassedWithAnnotation.setEnabled(true);
		btnFailed.setEnabled(true);
		lblCreator.setText(Messages.FilterView_3);
		text_1.setItems(TSMReport.getAllTesters());
		btnUnassignedTestCases.setEnabled(false);
		btnNotExecuted.setEnabled(true);
	    }
	});
	btnReports.setText(Messages.FilterView_4);
	new Label(container, SWT.NONE);
	new Label(container, SWT.NONE);
	new Label(container, SWT.NONE);

	final Label lblPrioritt = new Label(container, SWT.NONE);
	lblPrioritt.setText(Messages.FilterView_5);

	final Label lblStatus = new Label(container, SWT.NONE);
	lblStatus.setText(Messages.FilterView_6);

	final Label lblName = new Label(container, SWT.NONE);
	lblName.setText(Messages.FilterView_7);
	new Label(container, SWT.NONE);

	lblCreator = new Label(container, SWT.NONE);
	lblCreator.setText(Messages.FilterView_8);

	btnHigh = new Button(container, SWT.CHECK);
	btnHigh.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setPriority(PriorityType.high,
			btnHigh.getSelection());
	    }
	});
	btnHigh.setText(Messages.FilterView_9);

	btnPassed = new Button(container, SWT.CHECK);
	btnPassed.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setStatus(StatusType.passed,
			btnPassed.getSelection());
	    }
	});
	btnPassed.setText(Messages.FilterView_10);

	text = new Text(container, SWT.BORDER);
	text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	text.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(final KeyEvent e) {
		FilterModel.getInstance().setName(text.getText());
	    }
	});

	text_1 = new Combo(container, SWT.BORDER);
	text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
		1));
	text_1.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(final KeyEvent e) {
		FilterModel.getInstance().setCreator(text_1.getText());
	    }
	});
	text_1.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		FilterModel.getInstance().setCreator(text_1.getText());
	    }
	});

	btnMedium = new Button(container, SWT.CHECK);
	btnMedium.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setPriority(PriorityType.medium,
			btnMedium.getSelection());
	    }
	});
	btnMedium.setText(Messages.FilterView_11);

	btnPassedWithAnnotation = new Button(container, SWT.CHECK);
	btnPassedWithAnnotation.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setStatus(
			StatusType.passedWithAnnotation,
			btnPassedWithAnnotation.getSelection());
	    }
	});
	btnPassedWithAnnotation.setText(Messages.FilterView_12);

	btnLastExecution = new Button(container, SWT.CHECK);
	btnLastExecution.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		if (btnLastExecution.getSelection()) {
		    dateTime.setEnabled(true);
		    btnNotExecuted.setEnabled(false);
		    FilterModel.getInstance().setLastExecution(
			    getDate(dateTime));
		} else {
		    btnNotExecuted.setEnabled(true);
		    dateTime.setEnabled(false);
		    FilterModel.getInstance().setLastExecution(null);
		}
	    }
	});
	btnLastExecution.setText(Messages.FilterView_13);

	btnCreationDate = new Button(container, SWT.CHECK);
	btnCreationDate.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		if (btnCreationDate.getSelection()) {
		    dateTime_1.setEnabled(true);
		    FilterModel.getInstance().setCreationTime(
			    getDate(dateTime_1));
		} else {
		    dateTime_1.setEnabled(false);
		    FilterModel.getInstance().setCreationTime(null);
		}
	    }
	});
	btnCreationDate.setText(Messages.FilterView_14);

	btnLastChange = new Button(container, SWT.CHECK);
	btnLastChange.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		if (btnLastChange.getSelection()) {
		    dateTime_2.setEnabled(true);
		    FilterModel.getInstance()
			    .setLastChange(getDate(dateTime_2));
		} else {
		    dateTime_2.setEnabled(false);
		    FilterModel.getInstance().setLastChange(null);
		}
	    }
	});
	btnLastChange.setText(Messages.FilterView_15);

	btnLow = new Button(container, SWT.CHECK);
	btnLow.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setPriority(PriorityType.low,
			btnLow.getSelection());
	    }
	});
	btnLow.setText(Messages.FilterView_16);

	btnFailed = new Button(container, SWT.CHECK);
	btnFailed.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setStatus(StatusType.failed,
			btnFailed.getSelection());
	    }
	});
	btnFailed.setText(Messages.FilterView_17);

	dateTime = new DateTime(container, SWT.BORDER);
	dateTime.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setLastExecution(getDate(dateTime));
	    }
	});
	dateTime.setEnabled(false);
	dateTime_1 = new DateTime(container, SWT.BORDER);
	dateTime_1.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setCreationTime(getDate(dateTime_1));
	    }
	});
	dateTime_1.setEnabled(false);
	dateTime_2 = new DateTime(container, SWT.BORDER);
	dateTime_2.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setLastChange(getDate(dateTime_2));
	    }
	});
	dateTime_2.setEnabled(false);
	new Label(container, SWT.NONE);

	btnNotExecuted = new Button(container, SWT.CHECK);
	btnNotExecuted.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setStatus(StatusType.notExecuted,
			btnNotExecuted.getSelection());
		if (btnTestcases.getSelection()) {
		    btnLastExecution.setEnabled(!btnNotExecuted.getSelection());
		} else if (btnNotExecuted.getSelection()) {
		    btnLastExecution.setSelection(false);
		}
	    }
	});
	btnNotExecuted.setText(Messages.FilterView_18);

	btnUnassignedTestCases = new Button(container, SWT.CHECK);
	btnUnassignedTestCases.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		FilterModel.getInstance().setUnassigned(
			btnUnassignedTestCases.getSelection());
	    }
	});
	btnUnassignedTestCases.setText(Messages.FilterView_19);

	final Button btnReset = new Button(container, SWT.NONE);
	btnReset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
		2, 1));
	btnReset.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		btnHigh.setSelection(false);
		btnMedium.setSelection(false);
		btnLow.setSelection(false);
		btnPassed.setSelection(false);
		btnPassedWithAnnotation.setSelection(false);
		btnFailed.setSelection(false);
		btnNotExecuted.setSelection(false);
		btnUnassignedTestCases.setSelection(false);
		btnLastExecution.setSelection(false);
		btnCreationDate.setSelection(false);
		btnLastChange.setSelection(false);
		text.setText(""); //$NON-NLS-1$
		text_1.setText(""); //$NON-NLS-1$
		dateTime.setEnabled(false);
		dateTime.setDate(Calendar.getInstance().get(Calendar.YEAR),
			Calendar.getInstance().get(Calendar.MONTH), Calendar
				.getInstance().get(Calendar.DAY_OF_MONTH));
		dateTime_1.setEnabled(false);
		dateTime_2.setDate(Calendar.getInstance().get(Calendar.YEAR),
			Calendar.getInstance().get(Calendar.MONTH), Calendar
				.getInstance().get(Calendar.DAY_OF_MONTH));
		dateTime_2.setEnabled(false);
		dateTime_2.setDate(Calendar.getInstance().get(Calendar.YEAR),
			Calendar.getInstance().get(Calendar.MONTH), Calendar
				.getInstance().get(Calendar.DAY_OF_MONTH));
		FilterModel.getInstance().reset();
	    }
	});
	btnReset.setText(Messages.FilterView_22);

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
	FilterModel.getInstance().setTestCases(true);
	btnPassed.setEnabled(false);
	btnPassedWithAnnotation.setEnabled(false);
	btnFailed.setEnabled(false);
	btnHigh.setEnabled(true);
	btnMedium.setEnabled(true);
	btnLow.setEnabled(true);
	btnLastExecution.setEnabled(true);
	dateTime.setEnabled(btnLastExecution.getSelection());
	btnLastChange.setEnabled(true);
	dateTime_2.setEnabled(btnLastChange.getSelection());
	btnUnassignedTestCases.setEnabled(true);
	text_1.setItems(TSMTestCase.getAllCreators());
	if (btnNotExecuted.getSelection()) {
	    btnLastExecution.setEnabled(false);
	} else if (btnLastExecution.getSelection()) {
	    btnNotExecuted.setEnabled(false);
	}
    }

    @Override
    public void dataModelChanged() {
	Display.getDefault().asyncExec(new Runnable() {
	    @Override
	    public void run() {
		if (btnTestcases.isDisposed()) {
		    return;
		}
		if (btnTestcases.getSelection()) {
		    text_1.setItems(TSMTestCase.getAllCreators());
		} else {
		    text_1.setItems(TSMReport.getAllTesters());
		}
	    }

	});
    }
}
