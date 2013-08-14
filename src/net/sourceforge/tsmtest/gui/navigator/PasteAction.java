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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMProject;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.providers.TSMHierarchyContentProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;

public class PasteAction extends Action {

    // FIXME Better clipboard handling
    private static ArrayList<TSMResource> customClipboard = null;
    private static boolean copy;
    public static boolean enabled = false;
    private static final Collection<PasteAction> allActions = new ArrayList<PasteAction>();

    public PasteAction(final String name, final IWorkbenchWindow window,
	    final Clipboard clipboard) {
	super(name);
	final ISharedImages sharedImages = window.getWorkbench()
		.getSharedImages();
	setDisabledImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
	setImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
	setHoverImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
	setAccelerator(SWT.CTRL | 'V');
	allActions.add(this);
    }

    public static void setClipboard(
	    final ArrayList<TSMResource> customClipboard, final boolean copy) {
	PasteAction.customClipboard = customClipboard;
	PasteAction.copy = copy;
	enabled = customClipboard != null;
	for (final PasteAction action : allActions) {
	    if (action != null) {
		action.setEnabled(enabled);
	    }
	}
    }

    @Override
    public void run() {
	if (customClipboard == null) {
	    return;
	}
	final ArrayList<TSMResource> filesToCopy = customClipboard;
	setClipboard(null, copy);
	// check if files within a package are selected and remove the files
	// 1.) What we do here is iterate through every selected file
	for (final Iterator<TSMResource> iterator = filesToCopy.iterator(); iterator
		.hasNext();) {
	    final TSMResource tsmResource = iterator.next();
	    // except for reports, they cannot be copied
	    if (tsmResource instanceof TSMReport) {
		iterator.remove();
		continue;
	    }
	    // 2.) For every selected file we check if one of its parents is
	    // selected too (stop when propagated up to project)
	    TSMResource parent = tsmResource;
	    while (parent != null && !(parent instanceof TSMProject)) {
		parent = (TSMResource) TSMHierarchyContentProvider.DEFAULT
			.getParent(parent);
		// 3.) If this is the case, remove this file from the selected
		// files.
		if (filesToCopy.contains(parent)) {
		    iterator.remove();
		    break;
		}
	    }
	}
	// Now if we are moving files, move the reports, too
	if (!copy) {
	    final ArrayList<TSMReport> reportsToAddToFilesToCopy = new ArrayList<TSMReport>();
	    for (final TSMResource res : filesToCopy) {
		if (res instanceof TSMTestCase) {
		    final TSMTestCase testCase = (TSMTestCase) res;
		    for (final TSMReport report : testCase.getReports()) {
			if (!filesToCopy.contains(report)) {
			    reportsToAddToFilesToCopy.add(report);
			}
		    }
		}
	    }
	    filesToCopy.addAll(reportsToAddToFilesToCopy);
	}
	// get destination
	TSMResource destination = SelectionManager.instance.getSelection()
		.getFirstResource();
	if (!(destination instanceof TSMContainer)) {
	    destination = destination.getParent();
	}
	// paste them
	try {
	    ((TSMContainer) destination).paste(filesToCopy, copy);
	} catch (final DataModelException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}