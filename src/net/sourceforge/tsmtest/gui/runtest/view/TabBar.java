 /*******************************************************************************
 * Copyright (c) 2012-2013 Wolfgang Kraus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wolfgang Kraus - initial version
 *    Bernhard Wetzel - some fixes
 *    Tobias Hirning - some refactoring
 *    Albert Flaig - data model refactoring
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.runtest.view;

import java.util.List;

import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.AbstractDataModel.DataModelObservable;
import net.sourceforge.tsmtest.gui.runtest.handler.CallRunTest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * An Editor to execute a TestCaseObject
 * 
 * @author Wolfgang Kraus
 * 
 */
public class TabBar implements DataModelObservable {
    public final static String ID = "net.sourceforge.tsmtest.gui.runtest.view.tabbar";
    private TSMTestCase testFile;
    private List<TSMTestCase> allFileList;
    private ScrolledComposite scCases;
    private static final String fileID = "net.sourceforge.tsmtest.gui.runtest.IFile";
    private SelectionAdapter listen;
    private TSMContainer currentPackage;

    public TabBar(Composite parent, int style) {

	scCases = new ScrolledComposite(parent, SWT.H_SCROLL);
	scCases.setLayout(new GridLayout());
	GridData gd_scCases = new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1);
	scCases.setLayoutData(gd_scCases);
	scCases.setExpandHorizontal(true);
	scCases.setExpandVertical(true);
	scCases.setAlwaysShowScrollBars(true);
	scCases.setVisible(true);
	DataModel.getInstance().register(this);
    }

    public void addListener(SelectionAdapter adapter) {
	listen = adapter;
    }

    /**
     * Updates the TabBar to reflect the package of the given testcase
     * 
     * @param tcf
     *            the TSMTestCase of the testcase
     */
    public void setFile(TSMTestCase tcf) {
	// Somehow it's under the impression that it is disposed, then we do nothing
	if (scCases.isDisposed()){
	    return;
	}
	// Removing out references to the old data
	allFileList = null;
	testFile = tcf;

	currentPackage = tcf.getParent();
	allFileList = currentPackage.getTestCases();

	SashForm sfCases = new SashForm(scCases, SWT.HORIZONTAL);
	sfCases.setLayout(new GridLayout());

	int myWidth = 0;
	int[] weights = new int[allFileList.size()];
	int myPos = 0;

	// Get the average font width
	GC gc = new GC(scCases);
	int wi = gc.getFontMetrics().getAverageCharWidth();
	gc.dispose();

	for (int i = 0; i < allFileList.size(); i++) {
	    TSMTestCase f = allFileList.get(i);
	    final Button b = new Button(sfCases, SWT.NONE);

	    b.setText(f.getName());
	    // We set the associated TestCaseObject as Data so we can retrive
	    // it later if it should be executed.
	    b.setData(fileID, f);
	    b.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
		    CallRunTest ctt = new CallRunTest();
		    ctt.execute((TSMTestCase) b.getData(fileID));
		}
	    });
	    if (listen != null) {
		b.addSelectionListener(listen);
	    }
	    // Calculating the needed width for the buttons
	    int chars = b.getText().length();
	    int w = (int) (chars * wi * 1.2);
	    myWidth += w;
	    weights[i] = w;
	    b.setSize(w, SWT.DEFAULT);

	    // We cannot open our current testcase again
	    if (f.equals(tcf)) {
		Color blue = new Color(scCases.getDisplay(), 0, 0, 200);
		b.setBackground(blue);
		b.setEnabled(false);
		b.setForeground(blue);
		myPos = myWidth + scCases.getParent().getSize().x - 100;
		blue.dispose();
	    }
	}
	sfCases.setWeights(weights);
	scCases.setContent(sfCases);
	scCases.setMinSize(myWidth, SWT.DEFAULT);
	scCases.setOrigin(myPos, 0);
    }

    @Override
    public void dataModelChanged() {
	Display.getDefault().asyncExec(new Runnable() {
	    public void run() {
		// If the changed file is in our current package we need to
		// update our buttons
		if(currentPackage.exists() && currentPackage.getTestCases().size() > 0) {
		    if(testFile.exists()) {
			setFile(testFile);
		    } else {
			setFile(currentPackage.getTestCases().get(0));
		    }
		} else {
		    // TODO Close RunTest
		}
	    }
	});
    }
}
