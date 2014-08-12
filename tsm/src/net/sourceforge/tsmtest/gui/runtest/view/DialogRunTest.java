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
 *    Albert Flaig - added image path to xml file
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.runtest.view;

import java.util.Calendar;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;
import net.sourceforge.tsmtest.gui.richtexteditor.RichText;
import net.sourceforge.tsmtest.gui.runtest.model.TestResult;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

/**
 * An Editor to execute a TestCaseObject
 * 
 * @author Wolfgang Kraus
 * @author Bernhard Wetzel
 * 
 */
public class DialogRunTest extends InputDialog {
    public final static String ID = "net.sourceforge.tsmtest.gui.runtest.view.overviewDialog"; //$NON-NLS-1$
    private Combo status;
    private RichText richTextField;
    private TestResult rtValue;
    private final StatusType prvWorstStatus;
    private final String labelText;
    private Button btnUpdateTime;
    private final String projectName;

    /**
     * Creates a new InputDialog with the needed fields
     * 
     * @param duration
     *            The real duration as String
     * @param name
     *            of the tester
     * @param worstStatus
     *            of the test execution
     */
    public DialogRunTest(final String duration, final String name,
	    final StatusType worstStatus, final String projectName) {
	super(null, Messages.DialogRunTest_1, null, "", null); //$NON-NLS-1$
	labelText = Messages.DialogRunTest_3 + name + Messages.DialogRunTest_4
		+ Calendar.getInstance().getTime().toString()
		+ Messages.DialogRunTest_5 + duration;
	prvWorstStatus = worstStatus;
	this.projectName = projectName;
	// We want a bigger window than the default size
	setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
	createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
		true);
	createButton(parent, IDialogConstants.CANCEL_ID,
		IDialogConstants.CANCEL_LABEL, false);

	final Button ok = getButton(IDialogConstants.OK_ID);
	ok.setText(Messages.DialogRunTest_6);
	setButtonLayoutData(ok);

	final Button cancel = getButton(IDialogConstants.CANCEL_ID);
	cancel.setText(Messages.DialogRunTest_7);
	setButtonLayoutData(cancel);
	richTextField.setFocus();
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
	final Composite comp = (Composite) super.createDialogArea(parent);
	final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 5, 5);
	gd.minimumHeight = 375;
	gd.minimumWidth = 700;
	comp.setLayoutData(gd);
	// Used for getting rid of the excess space at the top
	final Control[] children = comp.getChildren();

	status = new Combo(comp, SWT.BORDER | SWT.READ_ONLY);
	final String[] states = new String[DataModelTypes.StatusType.values().length];
	for (int j = 0; j < states.length; j++) {
	    states[j] = DataModelTypes.StatusType.values()[j].toString();
	}
	status.setItems(states);
	final int statIndex = status.indexOf(prvWorstStatus.toString());
	status.select(statIndex);

	final Label label = new Label(comp, SWT.CENTER);
	label.setText(labelText);
	label.moveBelow(status);

	richTextField = new RichText(comp, SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
	richTextField.setProjectName(projectName);
	richTextField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		true, 5, 5));
	btnUpdateTime = new Button(comp, SWT.CHECK);
	btnUpdateTime.setText(Messages.DialogRunTest_2);
	// We only hide it because disposing it leads to NPE's
	super.getText().setVisible(false);

	// We have 2 Text-children that we neither see nor use, thus we don't
	// want them to grab any space at the top
	for (final Control control : children) {
	    control.moveBelow(richTextField);
	}

	richTextField.redraw();
	parent.redraw();
	parent.layout();

	return comp;
    }

    @Override
    protected int getInputTextStyle() {
	return (SWT.MULTI | SWT.WRAP);
    }

    @Override
    protected void buttonPressed(final int buttonId) {
	if (buttonId == IDialogConstants.OK_ID) {
	    rtValue = new TestResult();
	    final StatusType sType = StatusType.valueOf(status.getItem(status
		    .getSelectionIndex()));
	    rtValue.setDescription(richTextField.getFormattedText());
	    rtValue.setState(sType);
	    rtValue.setUpdateTime(btnUpdateTime.getSelection());
	} else {
	    rtValue = null;
	}
	super.buttonPressed(buttonId);
    }

    public TestResult getRunTestValue() {
	return rtValue;
    }
}
