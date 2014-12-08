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
import java.util.BitSet;
import java.util.List;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.descriptors.ITestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.io.pdf.FontsToolsConstants.ExportType;
import net.sourceforge.tsmtest.io.pdf.FontsToolsConstants.ExportedFilesType;

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
    public List<TSMResource> getExportList() throws CoreException,
	    NumberFormatException {
	ExportedFilesType exportFilesType = getTypeOfExportFiles();
	final List<IResource> list = getSelectedResources();
	// go through all files and check if test case or protocol. If yes add
	// it to newList
	List<IFile> exportList = new ArrayList<IFile>();
	for (final IResource currentRessource : list) {
	    // currentRessource is IFile
	    if (currentRessource instanceof IFile) {
		// currentRessource is protocol or test case
		// all files
		if (exportFilesType == ExportedFilesType.ALL_FILES) {
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
				exportList.add((IFile) currentRessource);
			    }
			}
		    }

		    // only test cases
		} else if (exportFilesType == ExportedFilesType.TEST_CASES) {
		    if (((IFile) currentRessource).getContentDescription() != null) {
			if (((IFile) currentRessource).getContentDescription()
				.getContentType() != null) {
			    if (((IFile) currentRessource)
				    .getContentDescription()
				    .getContentType()
				    .getId()
				    .equals(DataModelTypes.CONTENT_TYPE_ID_TESTCASE)) {
				exportList.add((IFile) currentRessource);
			    }
			}
		    }

		    // only protocols
		} else if (exportFilesType == ExportedFilesType.PROTOCOLS) {
		    if (((IFile) currentRessource).getContentDescription() != null) {
			if (((IFile) currentRessource).getContentDescription()
				.getContentType() != null) {
			    if (((IFile) currentRessource)
				    .getContentDescription()
				    .getContentType()
				    .getId()
				    .equals(DataModelTypes.CONTENT_TYPE_ID_PROTOCOL)) {
				exportList.add((IFile) currentRessource);
			    }
			}
		    }
		    // only protocols with a certain revision
		} else {
		    if (((IFile) currentRessource).getContentDescription() != null) {
			if (((IFile) currentRessource).getContentDescription()
				.getContentType() != null) {
			    String revisionText = getRevision();
			    
			    //Parse revisions
			    int revisionFrom;
			    int revisionTo;
			    int revisionList[] = null;
			    
			    
			    //Revision range was entered.
			    if (revisionText.contains("-")) {
				String[] revisionRange = revisionText.split("-");
				revisionFrom = Integer.valueOf(revisionRange[0]);
				revisionTo = Integer.valueOf(revisionRange[1]);
				
				revisionList = new int[revisionTo+1];
				
				//"Calculate" all revisions between start and end.
				for (int current = revisionFrom; current <= revisionTo; current++) {
				    revisionList[current] = current;
				}
			    } 
			    //Specific revisions was entered.
			    else if (revisionText.contains(",")) {
				String[] revisionsSplitted = revisionText.split(",");
				revisionList = new int[revisionsSplitted.length];

				//Convert array of strings into an array of ints.
				for (int i = 0; i < revisionsSplitted.length; i++) {
				    revisionList[i] = Integer.valueOf(revisionsSplitted[i]);
				    }
				}
			    //A single revision was entered.
			    else {
				revisionList = new int[1];
				//Convert the single revision into an int.
				//If it is an invalid input catch the exception.
				try {
				    revisionList[0] =  Integer.valueOf(revisionText);
				} catch (NumberFormatException e) {
				    //Do nothing. PdfWizard.performFinish() already displays
				    //an adequate error message on the GUI.
				}
			    }
			    for (int currentRevision : revisionList) {
				if (((IFile) currentRessource)
				    .getContentDescription()
				    .getContentType()
				    .getId()
				    .equals(DataModelTypes.CONTENT_TYPE_ID_PROTOCOL)) {
				    TSMResource res = DataModel.getInstance()
					.convertToTSMResource(currentRessource);
				    TSMReport report = (TSMReport) res;
				    TestCaseDescriptor tcd = report.createDataCopy();
				    if (tcd.getRevisionNumber() == currentRevision) {
					exportList.add((IFile) currentRessource);
					}
				}
			    }
			}
		    }
		}
	    }
	}
	final List<TSMResource> tsmList = new ArrayList<TSMResource>();
	
	//Remove every report that does match the filter.
	for (final IFile res : exportList) {
	    TSMResource tsmResource = DataModel.getInstance().convertToTSMResource(res);
	    //Only remove when it is a report and the filter is enabled.
	    if (tsmResource instanceof TSMReport && ExportFilter.getFilterEnabled()) {
		//Get the report.
		ITestCaseDescriptor currentReport  = ((TSMReport) tsmResource).getData();
		
		/**
		 * Properties of the current report like status and priority.
		 * @see ExportFilter.filterBitSet
		 */
		BitSet currentReportBitSet = new BitSet();
		StatusType currentReportStatusType = currentReport.getStatus();
		PriorityType currentReportPriorityType = currentReport.getPriority();
		
		//Read status types.
		if (currentReportStatusType == StatusType.passed) {
		    currentReportBitSet.set(0);
		} 
		if (currentReportStatusType == StatusType.passedWithAnnotation) {
		    currentReportBitSet.set(1);
		}
		if (currentReportStatusType == StatusType.failed) {
		    currentReportBitSet.set(2);
		}
		if (currentReportStatusType == StatusType.notExecuted) {
		    currentReportBitSet.set(3);
		}

		//Read priorities.
		if (currentReportPriorityType == PriorityType.low) {
		    currentReportBitSet.set(4);
		}
		if (currentReportPriorityType == PriorityType.medium) {
		    currentReportBitSet.set(5);
		}
		if (currentReportPriorityType == PriorityType.high) {
		    currentReportBitSet.set(6);
		}
		
		boolean exportCurrentReport = false;
		for (int index = currentReportBitSet.nextSetBit(0); index >= 0; index = currentReportBitSet.nextSetBit(index+1)) {
		    //First four bits represent the status.
		    if (index <= 3) {
			 if (ExportFilter.getFilterBitSet().get(index)) {
			     exportCurrentReport = true;
			 } else {
			     exportCurrentReport = false;
			 }
		     } 
		    //Last three bits represent the priorities
		    else if (index > 3) {
			//Only export if we already have the right status.
			 if (ExportFilter.getFilterBitSet().get(index) && exportCurrentReport) {
			     exportCurrentReport = true;
			 } else {
			     exportCurrentReport = false;
			 }
		     }
		 }
		
		//Only export if all tests are passed.
		if (exportCurrentReport) {
		    tsmList.add(tsmResource);
		}
	    } 
	    //If we don't have a protocol or filter is not enabled export everything that was select
	    //(and not previously removed due to restriction on revision numbers).
	    else {
		tsmList.add(tsmResource);
	    }
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
	if (getExportType() == ExportType.ONE_FILE) {
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
	} else if (getExportType() == ExportType.MULTIPLE_FILES){

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
