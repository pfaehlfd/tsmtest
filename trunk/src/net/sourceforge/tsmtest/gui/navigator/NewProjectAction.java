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
import net.sourceforge.tsmtest.SWTUtils;
import net.sourceforge.tsmtest.gui.ResourceManager;
import net.sourceforge.tsmtest.wizards.ProjectWizard;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class NewProjectAction extends Action {

    public NewProjectAction(final String name) {
	super(name);
	final ImageDescriptor img = Activator
		.getImageDescriptor(ResourceManager.getPathTSMProject());
	setImageDescriptor(img);
	setHoverImageDescriptor(img);
	setDisabledImageDescriptor(img);
    }

    @Override
    public void run() {
	SWTUtils.openWizard(ProjectWizard.id);
    }
}
