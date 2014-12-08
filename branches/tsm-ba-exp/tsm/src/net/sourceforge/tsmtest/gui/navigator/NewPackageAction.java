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

import net.sourceforge.tsmtest.Activator;
import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.gui.ResourceManager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;

public class NewPackageAction extends AbstractNewFileAction {

    public NewPackageAction(final String name, final TSMTreeViewer viewer) {
	super(name, viewer);
    }

    @Override
    protected ImageDescriptor getImage() {
	return Activator
		.getImageDescriptor(ResourceManager.getPathTSMPackage());
    }

    @Override
    protected String getBaseNewName() {
	return Messages.NewPackageAction_0;
    }

    @Override
    protected void create(final String name, final TSMContainer cont,
	    final TSMTreeViewer viewer) throws DataModelException {
	// create package and then edit it
	try {
	    final TSMPackage tc = cont.createPackage(name);
	    viewer.refresh(tc);
	    viewer.setSelection(new StructuredSelection(tc), true);
	    Display.getCurrent().asyncExec(new Runnable() {
		@Override
		public void run() {
		    viewer.editElement(tc);
		}
	    });
	} catch (final DataModelException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public void selectionChanged() {
	if (SelectionManager.getInstance().getSelection().getFirstContainer() == null) {
	    setEnabled(false);
	} else {
	    setEnabled(true);
	}
    }
}
