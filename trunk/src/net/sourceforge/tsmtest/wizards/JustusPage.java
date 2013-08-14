 /*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Verena Käfer - initial version
 *    Tobias Hirning - i18n
 *    Albert Flaig - some fixes
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;

import net.sourceforge.tsmtest.Messages;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardResourceImportPage;

public class JustusPage extends WizardResourceImportPage {

    Text text;
    Label importLabel;
    Composite p;
    boolean sequenceIsCase = true;

    protected JustusPage(final String name, final IStructuredSelection selection) {
	super(name, selection);
	setDescription(Messages.JustusPage_0);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.tsmtest.wizards.ImportWizardPage#createSourceGroup(org.eclipse
     * .swt.widgets.Composite)
     */
    @Override
    protected void createSourceGroup(final Composite parent) {
	// top level group
	final Composite sourceComposite = new Composite(parent, SWT.NONE);
	sourceComposite.setFont(parent.getFont());
	final GridData gridData = new GridData();
	gridData.horizontalAlignment = GridData.FILL;
	gridData.grabExcessHorizontalSpace = true;
	sourceComposite.setLayoutData(gridData);

	final GridLayout layout = new GridLayout();
	layout.numColumns = 3;
	sourceComposite.setLayout(layout);
	sourceComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
		| GridData.HORIZONTAL_ALIGN_FILL));

	final Label label = new Label(sourceComposite, SWT.LEFT);
	label.setText(Messages.JustusPage_1);
	text = new Text(sourceComposite, SWT.SEARCH);
	text.setLayoutData(gridData);
	final Button button = new Button(sourceComposite, SWT.PUSH);
	button.setText(Messages.JustusPage_2);
	setButtonLayoutData(button);
	button.addSelectionListener(new SelectionAdapter() {
	    /*
	     * (non-Javadoc)
	     * 
	     * @see
	     * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
	     * .swt.events.SelectionEvent)
	     */
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		handleFileBrowse();
	    }

	});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IPath queryForContainer(final IContainer initialSelection,
	    final String msg, final String title) {
	final PackageSelectionDialog dialog = new PackageSelectionDialog(
		getControl().getShell(), initialSelection,
		allowNewContainerName(), msg);
	if (title != null) {
	    dialog.setTitle(title);
	}
	dialog.showClosedProjects(false);
	dialog.open();
	final Object[] result = dialog.getResult();
	if (result != null && result.length == 1) {
	    return (IPath) result[0];
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.tsmtest.wizards.ImportWizardPage#getFileProvider()
     */
    @Override
    protected ITreeContentProvider getFileProvider() {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.tsmtest.wizards.ImportWizardPage#getFolderProvider()
     */
    @Override
    protected ITreeContentProvider getFolderProvider() {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.dialogs.WizardDataTransferPage#createOptionsGroup(org.
     * eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createOptionsGroup(final Composite parent) {

	final Group options = new Group(parent, SWT.NONE);
	options.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
		| GridData.HORIZONTAL_ALIGN_FILL));
	options.setLayout(new GridLayout(2, false));
	options.setText(Messages.JustusPage_4);

	final Button sequenceIsTestCase = new Button(options, SWT.RADIO);
	sequenceIsTestCase.setText(Messages.JustusPage_5);
	sequenceIsTestCase.setSelection(true);

	sequenceIsTestCase.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		sequenceIsCase = true;
	    }

	    @Override
	    public void widgetDefaultSelected(final SelectionEvent e) {
		sequenceIsCase = true;
	    }

	});

	final Composite f = new Composite(options, SWT.None);

	final Button testCaseIsTestCase = new Button(options, SWT.RADIO);
	testCaseIsTestCase.setText(Messages.JustusPage_6);

	testCaseIsTestCase.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		sequenceIsCase = false;
	    }

	    @Override
	    public void widgetDefaultSelected(final SelectionEvent e) {
		sequenceIsCase = false;
	    }

	});
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.tsmtest.wizards.ImportWizardPage#allowNewContainerName()
     */
    @Override
    protected boolean allowNewContainerName() {
	return false;
    }

    /**
     * Opens a file chooser
     */
    private void handleFileBrowse() {
	final FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
	final String[] extensions = new String[1];
	extensions[0] = "*.jdf;*.xml"; //$NON-NLS-1$
	dialog.setFilterExtensions(extensions);
	final String path = dialog.open();
	if (path == null) {
	    text.setText(""); //$NON-NLS-1$
	} else {
	    text.setText(path);
	}
	setErrorMessage(null);
    }

    /**
     * @return The path of the source
     */
    public String getSource() {
	return text.getText();
    }

    /**
     * @return The path of the destination
     */
    public IPath getDestination() {
	return getContainerFullPath();
    }

    /**
     * @return If a sequrnce should be imported as a test case
     */
    public boolean isSequenceTestCase() {
	return sequenceIsCase;
    }

}
