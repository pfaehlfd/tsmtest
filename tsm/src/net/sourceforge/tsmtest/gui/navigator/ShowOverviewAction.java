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
import net.sourceforge.tsmtest.gui.ResourceManager;
import net.sourceforge.tsmtest.gui.overview.view.Overview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class ShowOverviewAction extends Action {

    public ShowOverviewAction(final String name) {
	super(name);
	final ImageDescriptor icon = Activator
		.getImageDescriptor(ResourceManager.getPathInformation());
	setImageDescriptor(icon);
	setHoverImageDescriptor(icon);
	// setAccelerator(SWT.CTRL | 'O');
    }

    @Override
    public void run() {
	Overview.openGUI(SelectionManager.instance.getSelection());
    }
}
