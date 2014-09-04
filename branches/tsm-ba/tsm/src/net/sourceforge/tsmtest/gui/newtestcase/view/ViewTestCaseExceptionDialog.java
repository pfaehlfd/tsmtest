 /*******************************************************************************
 * Copyright (c) 2012-2013 Daniel Hertl.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Hertl - initial version
 *    Bernhard Wetzel - bugfix
 *    Wolfgang Kraus - i18n
 *    Tobias Hirning - i18n
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.newtestcase.view;

import net.sourceforge.tsmtest.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;

/**
 * @author Daniel Hertl
 *
 */
public class ViewTestCaseExceptionDialog extends Dialog {
    public final static String ID="net.sourceforge.tsmtest.gui.newtestcase.view.viewtestcasseexceptiondialog"; //$NON-NLS-1$
    private String errorMessage;

    /**
     * Create the dialog.
     * @param parentShell
     * @param tempErrorMessage 
     */
    public ViewTestCaseExceptionDialog(Shell parentShell, String tempErrorMessage) {
	super(parentShell);
	errorMessage = tempErrorMessage;
	
    }

    /**
     * Create contents of the dialog.
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
	Composite container = (Composite) super.createDialogArea(parent);
	container.setLayout(new GridLayout(1, false));
	
	Label lblfollowinErrorOccurred = new Label(container, SWT.NONE);
	lblfollowinErrorOccurred.setText(Messages.ViewTestCaseExceptionDialog_1);
	
	Label label = new Label(container, SWT.NONE);
	label.setText(errorMessage);

	return container;
    }

    /**
     * Create contents of the button bar.
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
	createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
		true);
//	createButton(parent, IDialogConstants.CANCEL_ID,
//		IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
	return new Point(450, 215);
    }

}
