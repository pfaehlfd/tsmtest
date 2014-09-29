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

public class CutAction extends Action {

    public CutAction(final String name, final IWorkbenchWindow window,
	    final Clipboard clipboard) {
	super(name);
	final ISharedImages sharedImages = window.getWorkbench()
		.getSharedImages();
	setDisabledImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
	setImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
	setHoverImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
	setAccelerator(SWT.CTRL | 'X');
    }

    @Override
    public void run() {
	PasteAction.setClipboard(SelectionManager.getInstance().getSelection()
		.getAllResources(), false);
    }
}
