 /*******************************************************************************
 * Copyright (c) 2012-2013 Wolfgang Kraus and Bernhard Wetzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wolfgang Kraus - initial version
 *    Bernhard Wetzel - initial version
 *    Tobias Hirning - some refactoring, i18n
 *    Albert Flaig - data model refactoring
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.runtest.handler;

import java.util.ArrayList;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionModel;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionObservable;
import net.sourceforge.tsmtest.gui.runtest.view.EditorRunTest;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;

/**
 * An Editor to execute a TestCaseObject
 * 
 * @author Wolfgang Kraus
 * @author Bernhard Wetzel
 * 
 */

public class CallRunTest extends AbstractHandler implements SelectionObservable {
    public static final String ID = "net.sourceforge.tsmtest.gui.runtest.controller.callruntest"; //$NON-NLS-1$

    public CallRunTest() {
	setBaseEnabled(false);
	SelectionManager.instance.register(this);
    }

    public void execute(TSMTestCase testCase) {
	try {
	    EditorRunTest.openGUI(testCase);
	} catch (PartInitException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	TSMTestCase input = null;
	SelectionModel sm = null;
	sm = SelectionManager.instance.getSelection();
	if (sm == null) {
	    throw new ExecutionException(Messages.CallRunTest_1);
	} else if (sm.getTestCases().size() > 0) {
	    input = sm.getTestCases().get(0);
	} else {
	    ArrayList<TSMTestCase> testCases = SelectionManager.instance
		    .getSelection(event).getTestCases();
	    if (testCases != null && testCases.size() > 0) {
		input = testCases.get(0);
	    }
	}
	try {
	    return EditorRunTest.openGUI(input);
	} catch (PartInitException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    @Override
    public void selectionChanged() {
	setBaseEnabled(!SelectionManager.instance.getSelection().getTestCases()
		.isEmpty());
    }

}
