 /*******************************************************************************
 * Copyright (c) 2012-2013 Daniel Hertl.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Hertl - initial version
 *    Albert Flaig - various fixes
 *    Bernhard Wetzel - various fixes, some refactoring, i18n
 *    Verena Käfer - enhancements
 *    Tobias Hirning - some refactoring, i18n
 *    Wolfgang Kraus - fix
 *    Jenny Krüwald - enhancements
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;

import java.util.regex.Pattern;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (tc).
 */

public class NewTestcaseWizardPage extends WizardPage {
    private Text containerText;

    private Text fileText;

    private final IStructuredSelection selection;
    private Label lblpackageproject;

    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param selection
     * 
     * @param pageName
     */
    public NewTestcaseWizardPage(final IStructuredSelection selection) {
	super("wizardPage"); //$NON-NLS-1$
	this.selection = selection;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl(final Composite parent) {
	final Composite container = new Composite(parent, SWT.NULL);
	final GridLayout layout = new GridLayout();
	container.setLayout(layout);
	layout.numColumns = 3;
	layout.verticalSpacing = 9;
	Label label;
	lblpackageproject = new Label(container, SWT.NULL);
	lblpackageproject.setText(Messages.NewTestcaseWizardPage_1);

	containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	containerText.setLayoutData(gd);
	containerText.addModifyListener(new ModifyListener() {
	    @Override
	    public void modifyText(final ModifyEvent e) {
		dialogChanged();
	    }
	});

	final Button button = new Button(container, SWT.PUSH);
	button.setText(Messages.NewTestcaseWizardPage_2);
	button.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		handleBrowse();
	    }
	});
	label = new Label(container, SWT.NULL);
	label.setText(Messages.NewTestcaseWizardPage_3);

	fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	fileText.setLayoutData(gd);
	fileText.addModifyListener(new ModifyListener() {
	    @Override
	    public void modifyText(final ModifyEvent e) {
		dialogChanged();
	    }
	});
	initialize();
	setPageComplete(false);
	dialogChanged();
	setControl(container);
	new Label(container, SWT.NONE);
    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */

    private void initialize() {

	IContainer container = null;
	final Object selection = this.selection.getFirstElement();
	if (selection instanceof IResource) {
	    if (selection instanceof IContainer) {
		container = (IContainer) selection;
	    } else {
		container = ((IResource) selection).getParent();
	    }
	}
	if (container != null) {
	    containerText.setText(container.getFullPath().toString());
	}
	fileText.setTextLimit(DataModelTypes.NAME_MAX_LENGTH);
	// fileText.setText("testcase");
	fileText.setFocus();
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */

    private void handleBrowse() {
	final PackageSelectionDialog dialog = new PackageSelectionDialog(
		getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
		Messages.NewTestcaseWizardPage_4);
	if (dialog.open() == Window.OK) {
	    final Object[] result = dialog.getResult();
	    if (result.length == 1) {
		containerText.setText(((Path) result[0]).toString());
	    }
	}
    }

    /**
     * Ensures that both text fields are set.
     * 
     * @throws SelectionException
     */

    private void dialogChanged() {
	final IResource container = ResourcesPlugin.getWorkspace().getRoot()
		.findMember(new Path(getContainerName()));
	final String fileName = getFileName();

	if (getContainerName().length() == 0) {
	    updateStatus(Messages.NewTestcaseWizardPage_5);
	    return;
	}
	if (container == null
		|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
	    updateStatus(Messages.NewTestcaseWizardPage_6);
	    return;
	}
	if (!container.isAccessible()) {
	    updateStatus(Messages.NewTestcaseWizardPage_7);
	    return;
	}
	if (fileName.length() == 0) {
	    updateStatus(Messages.NewTestcaseWizardPage_8);
	    return;
	}

	if (Pattern.matches(".*[<>?|\".:_\\*/].*", fileName) //$NON-NLS-1$
		|| Pattern.matches(".*\\\\.*", fileName)) { //$NON-NLS-1$
	    updateStatus(DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	    return;
	}

	if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
	    updateStatus(Messages.NewTestcaseWizardPage_11);
	    return;
	}
	if (fileName.trim().equals("")) { //$NON-NLS-1$
	    updateStatus(Messages.NewTestcaseWizardPage_13);
	    return;
	}
	final int dotLoc = fileName.lastIndexOf('.');
	if (dotLoc != -1) {
	    final String ext = fileName.substring(dotLoc + 1);
	    if (!ext.equalsIgnoreCase("xml")) { //$NON-NLS-1$
		updateStatus(Messages.NewTestcaseWizardPage_15);
		return;
	    }

	}
	updateStatus(null);
    }

    private void updateStatus(final String message) {
	setErrorMessage(message);
	setPageComplete(message == null);
    }

    public String getContainerName() {
	return containerText.getText();
    }

    public String getFileName() {
	return fileText.getText();
    }
}