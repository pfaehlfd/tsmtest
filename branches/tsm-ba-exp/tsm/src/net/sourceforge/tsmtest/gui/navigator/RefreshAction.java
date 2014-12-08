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

import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModelException;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;

public class RefreshAction extends Action {

    public RefreshAction(final String name, final IWorkbenchWindow window) {
	super(name);
	final ISharedImages sharedImages = window.getWorkbench()
		.getSharedImages();
	setDisabledImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED_DISABLED));
	setImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
	setHoverImageDescriptor(sharedImages
		.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
	setAccelerator(SWT.F5);
    }

    @Override
    public void run() {
	try {
	    DataModel.getInstance().refresh();
	} catch (final DataModelException e) {
	    // cannot handle
	}
    }
}