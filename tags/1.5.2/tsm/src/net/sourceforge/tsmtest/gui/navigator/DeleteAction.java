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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.datamodel.TSMProject;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;

public class DeleteAction extends Action {

    private final DeleteActionDialog dialogDelete;

    public DeleteAction(final String name, final IWorkbenchWindow window) {
	super(name);
	final ISharedImages sharedImages = window.getWorkbench()
		.getSharedImages();
	setDisabledImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
	setImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	setHoverImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	setAccelerator(SWT.DEL);
	dialogDelete = new DeleteActionDialog(window.getShell());
	dialogDelete.setTitle(Messages.DeleteAction_2);
	dialogDelete.setCheckBoxText(Messages.DeleteAction_3);
	dialogDelete.setCheckBoxTooltip(Messages.DeleteAction_4);
    }

    @Override
    public void run() {
	List<TSMResource> filesToDelete = SelectionManager.getInstance()
		.getSelection().getAllResources();
	// check if files within a package are selected and remove the files
	// 1.) What we do here is iterate through every selected file
	for (final Iterator<TSMResource> iterator = filesToDelete.iterator(); iterator
		.hasNext();) {
	    final TSMResource tsmResource = iterator.next();
	    // 2.) For every selected file we check if one of its parents is
	    // selected too (stop when propagated up to project)
	    TSMResource parent = tsmResource;
	    while (parent != null && !(parent instanceof TSMProject)) {
		parent = parent.getParent();
		// 3.) If this is the case, remove this file from the selected
		// files.
		if (filesToDelete.contains(parent)) {
		    iterator.remove();
		    break;
		}
	    }
	}
	// remember if all the packages are empty
	boolean allPackagesAreEmpty = true;
	// remember if all the files to delete are all projects
	boolean onlyPackagesSelected = true;
	final ArrayList<TSMResource> filesToDeleteWithReports = new ArrayList<TSMResource>();
	filesToDeleteWithReports.addAll(filesToDelete);
	for (final TSMResource res : filesToDelete) {
	    if (res instanceof TSMPackage) {
		if (((TSMPackage) res).getChildren().size() > 0) {
		    allPackagesAreEmpty = false;
		}
	    } else {
		onlyPackagesSelected = false;
		// Now check if any of the remaining TSMTestCases have
		// TSMReports as child and ask user whether to delete them, too
		if (res instanceof TSMTestCase) {
		    // only ask if the reports are not selected already
		    for (final TSMReport report : ((TSMTestCase) res)
			    .getReports()) {
			if (!filesToDeleteWithReports.contains(report)) {
			    filesToDeleteWithReports.add(report);
			}
		    }
		}
	    }
	}
	// only if no empty packages are selected show a warning message whether
	// to delete those elements
	if (!allPackagesAreEmpty || !onlyPackagesSelected) {
	    if (filesToDelete.size() == 1) {
		if (filesToDelete.get(0) instanceof TSMProject) {
		    dialogDelete.setMessage(Messages.DeleteAction_5);
		} else if (filesToDelete.get(0) instanceof TSMPackage) {
		    dialogDelete.setMessage(Messages.DeleteAction_6);
		} else if (filesToDelete.get(0) instanceof TSMTestCase) {
		    dialogDelete.setMessage(Messages.DeleteAction_7);
		} else if (filesToDelete.get(0) instanceof TSMReport) {
		    dialogDelete.setMessage(Messages.DeleteAction_8);
		}
	    } else if (filesToDelete.size() > 1) {
		dialogDelete.setMessage(Messages.DeleteAction_10);
	    }
	    dialogDelete.setInput(filesToDelete, filesToDeleteWithReports);

	    final int userDecision = dialogDelete.open();
	    if (userDecision != IDialogConstants.OK_ID) {
		return;
	    }
	    // If the user checked to delete the related reports, too
	    if (dialogDelete.isChecked()) {
		filesToDelete = filesToDeleteWithReports;
	    }
	}

	try {
	    DataModel.getInstance().setEnabled(false);
	    for (final TSMResource res : filesToDelete) {
		try {
		    res.delete();
		} catch (final DataModelException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	} finally {
	    DataModel.getInstance().setEnabled(true);
	}
	try {
	    DataModel.getInstance().refresh();
	} catch (final DataModelException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}