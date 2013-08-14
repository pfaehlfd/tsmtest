 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Verena Käfer - i18n
 *******************************************************************************/ 
package net.sourceforge.tsmtest.gui.navigator;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMResource;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RenameAction extends Action {

    private final RenameDialog dialog;
    private final TSMTreeViewer tsmViewer;

    public RenameAction(final String name, final TSMTreeViewer tsmViewer) {
	super(name);
	this.tsmViewer = tsmViewer;
	dialog = new RenameDialog(tsmViewer.getControl().getShell());
    }

    @Override
    public void run() {
	final TSMResource resource = SelectionManager.instance.getSelection()
		.getFirstResource();
	if (resource != null) {
	    tsmViewer.editElement(resource);
	    // dialog.setName(resource.getName());
	}
	// if (dialog.open() == Window.OK) {
	// try {
	// resource.rename(dialog.getName());
	// } catch (final DataModelException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
    }

    class RenameDialog extends Dialog {
	private Text text;
	private String defaultText = ""; //$NON-NLS-1$

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public RenameDialog(final Shell parentShell) {
	    super(parentShell);
	    setBlockOnOpen(true);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
	    final Composite container = (Composite) super
		    .createDialogArea(parent);
	    container.setLayout(new GridLayout(1, false));

	    final Label lblEnterNewName = new Label(container, SWT.NONE);
	    lblEnterNewName.setText(Messages.RenameAction_1);

	    text = new Text(container, SWT.BORDER);
	    text.setText(defaultText);
	    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		    1, 1));

	    return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
	    createButton(parent, IDialogConstants.OK_ID,
		    IDialogConstants.OK_LABEL, true);
	    createButton(parent, IDialogConstants.CANCEL_ID,
		    IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
	    return new Point(450, 300);
	}

	@Override
	protected void okPressed() {
	    defaultText = text.getText();
	    super.okPressed();
	}

	public void setName(final String name) {
	    defaultText = name;
	}

	public String getName() {
	    return defaultText;
	}
    }
}
