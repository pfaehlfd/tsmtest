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
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.gui.ResourceManager;
import net.sourceforge.tsmtest.gui.newtestcase.view.ViewTestCase;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

public class NewTestCaseAction extends AbstractNewFileAction {

    public NewTestCaseAction(final String name, final TSMTreeViewer viewer) {
	super(name, viewer);
    }

    @Override
    protected ImageDescriptor getImage() {
	return Activator.getImageDescriptor(ResourceManager
		.getPathTSMTestCase());
    }

    @Override
    protected String getBaseNewName() {
	return Messages.NewTestCaseAction_0;
    }

    @Override
    protected void create(final String name, final TSMContainer container,
	    final TSMTreeViewer viewer) throws DataModelException {
	// create test case and open it with test case editor
	try {
	    final TSMTestCase tc = container.createTestCase(name,
		    new TestCaseDescriptor());
	    viewer.refresh(tc);
	    viewer.setSelection(new StructuredSelection(tc), true);
	    final IEditorPart editor = ViewTestCase.openGUI(tc);
	    // focus on the name-text field
	    if (editor instanceof ViewTestCase) {
		((ViewTestCase) editor).setFocusOnName();
	    }
	} catch (final DataModelException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (final PartInitException e) {
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
