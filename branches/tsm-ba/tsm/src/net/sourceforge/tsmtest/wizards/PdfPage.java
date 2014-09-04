 /*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Verena Käfer - initial version
 *    Albert Flaig - data model refactoring
 *    Tobias Hirning - some refactoring, i18n
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

public class PdfPage extends ExportWizardPage {
    private Text text;

    protected PdfPage(final String pageName,
	    final IStructuredSelection selection) {
	super(pageName, selection);
	setMessage(Messages.PdfPage_0);
	setTitle(Messages.PdfPage_1);
	setDescription(Messages.PdfPage_2);
	setPageComplete(false);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
     * Event)
     */
    @Override
    public void handleEvent(final Event event) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.tsmtest.wizards.ExportWizardPage#createDestinationGroup(org.eclipse
     * .swt.widgets.Composite)
     */
    @Override
    protected void createDestinationGroup(final Composite parent) {
	// top level group
	final Composite composite = new Composite(parent, SWT.NONE);
	composite.setFont(parent.getFont());
	final GridData gridData = new GridData();
	gridData.horizontalAlignment = GridData.FILL;
	gridData.grabExcessHorizontalSpace = true;
	composite.setLayoutData(gridData);

	final GridLayout layout = new GridLayout();
	layout.numColumns = 2;
	composite.setLayout(layout);
	composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
		| GridData.HORIZONTAL_ALIGN_FILL));

	text = new Text(composite, SWT.SEARCH);
	text.setLayoutData(gridData);
	final Button button = new Button(composite, SWT.PUSH);
	button.setText(Messages.PdfPage_5);
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

	text.addModifyListener(new ModifyListener() {
	    /*
	     * (non-Javadoc)
	     * 
	     * @see
	     * org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.
	     * swt.events.ModifyEvent)
	     */
	    @Override
	    public void modifyText(final ModifyEvent e) {
		setPageComplete(true);
		if (text.getText().equals("")) { //$NON-NLS-1$
		    setPageComplete(false);
		}
	    }
	});

    }

    /**
     * @return The filtered list of the files that should be exported
     * @throws CoreException
     */
    @SuppressWarnings("unchecked")
    public List<TSMResource> getProtocols() throws CoreException,
	    NumberFormatException {
	int exportFiles = getExportFiles();
	final List<IResource> list = getSelectedResources();
	// go through all files and check if test case or protocol. If yes add
	// it to newList
	List<IFile> newList = new ArrayList<IFile>();
	for (final IResource currentRessource : list) {
	    // currentRessource is IFile
	    if (currentRessource instanceof IFile) {
		// currentRessource is protocol or test case
		// all files
		if (exportFiles == 0) {
		    if (((IFile) currentRessource).getContentDescription() != null) {
			if (((IFile) currentRessource).getContentDescription()
				.getContentType() != null) {
			    if (((IFile) currentRessource)
				    .getContentDescription()
				    .getContentType()
				    .getId()
				    .equals(DataModelTypes.CONTENT_TYPE_ID_PROTOCOL)
				    || ((IFile) currentRessource)
					    .getContentDescription()
					    .getContentType()
					    .getId()
					    .equals(DataModelTypes.CONTENT_TYPE_ID_TESTCASE)) {
				newList.add((IFile) currentRessource);
			    }
			}
		    }

		    // only test cases
		} else if (exportFiles == 1) {
		    if (((IFile) currentRessource).getContentDescription() != null) {
			if (((IFile) currentRessource).getContentDescription()
				.getContentType() != null) {
			    if (((IFile) currentRessource)
				    .getContentDescription()
				    .getContentType()
				    .getId()
				    .equals(DataModelTypes.CONTENT_TYPE_ID_TESTCASE)) {
				newList.add((IFile) currentRessource);
			    }
			}
		    }

		    // only protocols
		} else if (exportFiles == 2) {
		    if (((IFile) currentRessource).getContentDescription() != null) {
			if (((IFile) currentRessource).getContentDescription()
				.getContentType() != null) {
			    if (((IFile) currentRessource)
				    .getContentDescription()
				    .getContentType()
				    .getId()
				    .equals(DataModelTypes.CONTENT_TYPE_ID_PROTOCOL)) {
				newList.add((IFile) currentRessource);
			    }
			}
		    }
		    // only protocols with a certain revision
		} else {
		    if (((IFile) currentRessource).getContentDescription() != null) {
			if (((IFile) currentRessource).getContentDescription()
				.getContentType() != null) {
			    String revisionText = getRevision();
			    if (((IFile) currentRessource)
				    .getContentDescription()
				    .getContentType()
				    .getId()
				    .equals(DataModelTypes.CONTENT_TYPE_ID_PROTOCOL)) {
				TSMResource res = DataModel.getInstance()
					.convertToTSMResource(currentRessource);
				TSMReport report = (TSMReport) res;
				TestCaseDescriptor tcd = report
					.createDataCopy();
				if (String.valueOf(tcd.getRevisionNumber())
					.equals(revisionText)) {
				    newList.add((IFile) currentRessource);
				}
			    }
			}
		    }
		}
	    }
	}
	final List<TSMResource> tsmList = new ArrayList<TSMResource>();
	for (final IFile res : newList) {
	    tsmList.add(DataModel.getInstance().convertToTSMResource(res));
	}
	return tsmList;
    }

    public String getPath() {
	return text.getText();
    }

    /**
     * Opens the file browse dialog
     */
    private void handleFileBrowse() {
	if (oneFile()) {
	    final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
	    final String[] extensions = new String[1];
	    extensions[0] = "*.pdf"; //$NON-NLS-1$
	    dialog.setFilterExtensions(extensions);
	    dialog.setFileName("export.pdf"); //$NON-NLS-1$
	    final String path = dialog.open();
	    if (path == null) {
		text.setText(""); //$NON-NLS-1$
	    } else {
		text.setText(path);
	    }
	} else {

	    final DirectoryDialog dialog = new DirectoryDialog(getShell());
	    final String path = dialog.open();
	    if (path == null) {
		text.setText(""); //$NON-NLS-1$
	    } else {
		text.setText(path);
	    }
	}
	setErrorMessage(null);

    }
}
