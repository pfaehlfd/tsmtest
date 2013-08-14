/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM Corporation - initial API and implementation 
 *   Sebastian Davids <sdavids@gmx.de> - Fix for bug 19346 - Dialog
 *     font should be activated and used by other components.
 *   Jenny Krüwald - enhancements
 *   Tobias Hirning - some refactoring, i18n
 *   Albert Flaig - fix
 *******************************************************************************/

package net.sourceforge.tsmtest.wizards;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tsmtest.IDEWorkbenchMessages;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionValidator;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.misc.ContainerSelectionGroup;

/**
 * A standard selection dialog which solicits a container resource from the
 * user. The <code>getResult</code> method returns the selected container
 * resource.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 * PackageSelectionDialog dialog = new PackageSelectionDialog(getShell(),
 * 	initialSelection, allowNewContainerName(), msg);
 * dialog.open();
 * Object[] result = dialog.getResult();
 * </pre>
 * 
 * </p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
@SuppressWarnings("restriction")
public class PackageSelectionDialog extends SelectionDialog {
    /**
	 * 
	 */
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    // the widget group;
    ContainerSelectionGroup group;

    // the root resource to populate the viewer with
    private final IContainer initialSelection;

    // allow the user to type in a new container name
    private boolean allowNewContainerName = true;

    // the validation message
    Label statusMessage;

    // for validating the selection
    ISelectionValidator validator;

    // show closed projects by default
    private boolean showClosedProjects = true;

    /**
     * Creates a resource container selection dialog rooted at the given
     * resource. All selections are considered valid.
     * 
     * @param parentShell
     *            the parent shell
     * @param initialRoot
     *            the initial selection in the tree
     * @param allowNewContainerName
     *            <code>true</code> to enable the user to type in a new
     *            container name, and <code>false</code> to restrict the user to
     *            just selecting from existing ones
     * @param message
     *            the message to be displayed at the top of this dialog, or
     *            <code>null</code> to display a default message
     */
    public PackageSelectionDialog(final Shell parentShell,
	    final IContainer initialRoot, final boolean allowNewContainerName,
	    final String message) {
	super(parentShell);
	setTitle(net.sourceforge.tsmtest.IDEWorkbenchMessages.ContainerSelectionDialog_0);
	initialSelection = initialRoot;
	this.allowNewContainerName = allowNewContainerName;
	if (message != null) {
	    setMessage(message);
	} else {
	    setMessage(""); //$NON-NLS-1$
	}
	setShellStyle(getShellStyle() | SWT.SHEET);
	setValidator(getDefaultValidator());
    }

    /*
     * (non-Javadoc) Method declared in Window.
     */
    @Override
    protected void configureShell(final Shell shell) {
	super.configureShell(shell);
	PlatformUI.getWorkbench().getHelpSystem()
		.setHelp(shell, IIDEHelpContextIds.CONTAINER_SELECTION_DIALOG);
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
	// create composite
	final Composite area = (Composite) super.createDialogArea(parent);

	final Listener listener = new Listener() {
	    @Override
	    public void handleEvent(final Event event) {
		if (statusMessage != null && validator != null) {
		    final String errorMsg = validator.isValid(group
			    .getContainerFullPath());
		    if (errorMsg == null || errorMsg.equals(EMPTY_STRING)) {
			statusMessage.setText(EMPTY_STRING);
			getOkButton().setEnabled(true);
		    } else {
			statusMessage.setText(errorMsg);
			getOkButton().setEnabled(false);
		    }
		}
	    }
	};

	// container selection group
	group = new ContainerSelectionGroup(area, listener,
		allowNewContainerName, getMessage(), showClosedProjects);
	if (initialSelection != null) {
	    group.setSelectedContainer(initialSelection);
	}

	statusMessage = new Label(area, SWT.WRAP);
	statusMessage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	statusMessage.setText(" \n "); //$NON-NLS-1$
	statusMessage.setFont(parent.getFont());

	return dialogArea;
    }

    /**
     * The <code>PackageSelectionDialog</code> implementation of this
     * <code>Dialog</code> method builds a list of the selected resource
     * containers for later retrieval by the client and closes this dialog.
     */
    @Override
    protected void okPressed() {

	final List<IPath> chosenContainerPathList = new ArrayList<IPath>();
	final IPath returnValue = group.getContainerFullPath();
	if (returnValue != null) {
	    chosenContainerPathList.add(returnValue);
	}
	setResult(chosenContainerPathList);
	super.okPressed();
    }

    /**
     * Sets the validator to use.
     * 
     * @param validator
     *            A selection validator
     */
    public void setValidator(final ISelectionValidator validator) {
	this.validator = validator;
    }

    public ISelectionValidator getDefaultValidator() {
	return new ISelectionValidator() {
	    @Override
	    public String isValid(final Object selection) {
		if (selection instanceof IPath) {
		    final IPath path = (IPath) selection;
		    if (path.segment(1) != null
			    && path.segment(1).equals(
				    DataModelTypes.imageFolderName)) {
			return IDEWorkbenchMessages.ContainerSelectionDialog_1;
		    }
		}
		return null;
	    }
	};
    }

    /**
     * Set whether or not closed projects should be shown in the selection
     * dialog.
     * 
     * @param show
     *            Whether or not to show closed projects.
     */
    public void showClosedProjects(final boolean show) {
	showClosedProjects = show;
    }
}
