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

import net.sourceforge.tsmtest.datamodel.SelectionManager;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;

public class CopyAction extends Action {

    private final Clipboard clipboard;

    public CopyAction(final String name, final IWorkbenchWindow window,
	    final Clipboard clipboard) {
	super(name);
	final ISharedImages sharedImages = window.getWorkbench()
		.getSharedImages();
	setDisabledImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
	setImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
	setHoverImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
	setAccelerator(SWT.CTRL | 'C');
	this.clipboard = clipboard;
    }

    @Override
    public void run() {
	// clipboard.setContents(data, dataTypes)
	PasteAction.setClipboard(SelectionManager.instance.getSelection()
		.getAllResources(), true);
    }
}
