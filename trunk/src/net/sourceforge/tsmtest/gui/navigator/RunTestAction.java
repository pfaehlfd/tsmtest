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

import net.sourceforge.tsmtest.Activator;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionObservable;
import net.sourceforge.tsmtest.gui.ResourceManager;
import net.sourceforge.tsmtest.gui.runtest.view.EditorRunTest;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PartInitException;

public class RunTestAction extends Action implements SelectionObservable {

    public RunTestAction(final String name) {
	super(name);
	final ImageDescriptor icon = Activator
		.getImageDescriptor(ResourceManager.getPathStart());
	setImageDescriptor(icon);
	setHoverImageDescriptor(icon);
	// setAccelerator(SWT.CTRL | 'T');
	SelectionManager.instance.register(this);
    }

    @Override
    public void run() {
	final TSMResource res = SelectionManager.instance.getSelection()
		.getFirstResource();

	if (res != null && res instanceof TSMTestCase) {
	    try {
		EditorRunTest.openGUI((TSMTestCase) res);
	    } catch (final PartInitException e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void selectionChanged() {
	final TSMResource res = SelectionManager.instance.getSelection()
		.getFirstResource();
	if (res != null && res instanceof TSMTestCase) {
	    setEnabled(true);
	} else {
	    setEnabled(false);
	}
    }
}
