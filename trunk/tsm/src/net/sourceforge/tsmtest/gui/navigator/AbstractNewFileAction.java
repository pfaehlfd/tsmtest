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

import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionObservable;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class AbstractNewFileAction extends Action implements
	SelectionObservable {

    private final TSMTreeViewer viewer;

    protected abstract ImageDescriptor getImage();

    protected abstract String getBaseNewName();

    protected abstract void create(String name, TSMContainer container,
	    TSMTreeViewer viewer) throws DataModelException;

    public AbstractNewFileAction(final String name, final TSMTreeViewer viewer) {
	super(name);
	final ImageDescriptor img = getImage();
	setImageDescriptor(img);
	setHoverImageDescriptor(img);
	this.viewer = viewer;
	SelectionManager.instance.register(this);
	selectionChanged();
    }

    @Override
    public void run() {
	run(viewer);
    }

    protected void run(final TSMTreeViewer viewer) {
	final TSMContainer cont = SelectionManager.instance.getSelection()
		.getFirstContainer();
	// check for name collisions in this container
	final String baseNewName = getBaseNewName();
	String newName = baseNewName;
	boolean collision = false;
	int i = 1;
	do {
	    if (i > 1) {
		newName = baseNewName + " (" + i + ")";
		collision = false;
	    }
	    for (final TSMResource child : cont.getChildren()) {
		if (child.getName().toLowerCase().equals(newName.toLowerCase())) {
		    collision = true;
		    break;
		}
	    }
	    i++;
	} while (collision);
	// create the file
	try {
	    create(newName, cont, viewer);
	} catch (final DataModelException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
    }
}
