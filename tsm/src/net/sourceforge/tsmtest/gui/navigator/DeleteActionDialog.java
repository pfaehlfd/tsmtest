 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *******************************************************************************/ 
package net.sourceforge.tsmtest.gui.navigator;

import java.util.Collection;

import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.providers.TSMResourceLabelProvider;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class DeleteActionDialog extends Dialog {

    private String title = "";
    private String message = "";
    private boolean checked = false;
    private Collection<TSMResource> input;
    private Collection<TSMResource> input2;
    private String checkBoxText = "";
    private String checkBoxTooltip = "";

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public DeleteActionDialog(final Shell parentShell) {
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
	final Composite container = (Composite) super.createDialogArea(parent);

	final Label lblNewLabel = new Label(container, SWT.WRAP);
	lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
		false, 1, 1));
	lblNewLabel.setText(message);

	final TreeViewer listViewer = new TreeViewer(container, SWT.BORDER
		| SWT.V_SCROLL);
	final Tree list = listViewer.getTree();
	list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	listViewer.setLabelProvider(TSMResourceLabelProvider.DEFAULT);
	listViewer.setSorter(new ViewerSorter());
	listViewer.addDoubleClickListener(new IDoubleClickListener() {
	    @Override
	    public void doubleClick(final DoubleClickEvent event) {
		final Object element = ((IStructuredSelection) event
			.getSelection()).getFirstElement();
		if (element != null) {
		    if (element instanceof TSMContainer) {
			if (listViewer.getExpandedState(element)) {
			    listViewer.collapseToLevel(element, 1);
			} else {
			    listViewer.expandToLevel(element, 1);
			}
		    }
		}
	    }
	});
	listViewer.setContentProvider(new ITreeContentProvider() {
	    @Override
	    public void inputChanged(final Viewer viewer,
		    final Object oldInput, final Object newInput) {
	    }

	    @Override
	    public void dispose() {
	    }

	    @Override
	    public boolean hasChildren(final Object element) {
		return getChildren(element).length != 0;
	    }

	    @Override
	    public Object getParent(final Object element) {
		if (element instanceof TSMResource) {
		    return ((TSMResource) element).getParent();
		}
		return null;
	    }

	    @Override
	    public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	    }

	    @Override
	    public Object[] getChildren(final Object parentElement) {
		if (parentElement instanceof TSMContainer) {
		    final TSMContainer container = (TSMContainer) parentElement;
		    return (container.getChildren().toArray());
		} else if (parentElement instanceof Collection) {
		    return ((Collection<?>) parentElement).toArray();
		}
		return new Object[0];
	    }
	});
	listViewer.setInput(input);

	final Button btnCheckButton = new Button(container, SWT.CHECK);
	btnCheckButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		checked = btnCheckButton.getSelection();
		if (checked) {
		    listViewer.setInput(input2);
		} else {
		    listViewer.setInput(input);
		}
	    }
	});
	btnCheckButton.setText(checkBoxText);
	btnCheckButton.setToolTipText(checkBoxTooltip);
	btnCheckButton.setEnabled(!input.equals(input2));

	return container;
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
	createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
		true);
	createButton(parent, IDialogConstants.CANCEL_ID,
		IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void configureShell(final Shell newShell) {
	super.configureShell(newShell);
	newShell.setText(title);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
	return new Point(450, 300);
    }

    public void setTitle(final String title) {
	this.title = title;
    }

    public void setMessage(final String message) {
	this.message = message;
    }

    public void setCheckBoxText(final String text) {
	checkBoxText = text;
    }

    public void setCheckBoxTooltip(final String text) {
	checkBoxTooltip = text;
    }

    /**
     * This method should only be called after the user has pressed OK.
     * 
     * @return Whether the checkbox has been checked by the user.
     */
    public boolean isChecked() {
	return checked;
    }

    /**
     * Sets the resources which should be shown in the tree.
     * 
     * @param input
     *            The resources to be shown when the checkbox is <b>not</b>
     *            checked.
     * @param input2
     *            The resources to be shown when the checkbox <b>is</b> checked.
     */
    public void setInput(final Collection<TSMResource> input,
	    final Collection<TSMResource> input2) {
	this.input = input;
	this.input2 = input2;
    }

}
