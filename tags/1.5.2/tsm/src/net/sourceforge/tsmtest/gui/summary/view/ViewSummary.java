 /*******************************************************************************
 * Copyright (c) 2012-2013 Wolfgang Kraus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wolfgang Kraus - initial version
 *    Bernhard Wetzel - fix
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.summary.view;

import java.util.ArrayList;

import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionModel;
import net.sourceforge.tsmtest.gui.overview.view.OverviewStepSash;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Wolfgang Kraus
 *
 */
public class ViewSummary extends ViewPart{
    public static final String ID = "net.sourceforge.tsmtest.gui.summary.view.viewsummary";

    @Override
    public void createPartControl(Composite parent) {
	OverviewStepSash sash;
	parent.setLayout(new GridLayout());
	SelectionModel sm = SelectionManager.getInstance().getSelection();
	ArrayList<TSMTestCase> tcs = sm.getTestCases();
	sash = new OverviewStepSash(parent);
	sash.initSteps(tcs);
    }

    @Override
    public void setFocus() {
    }

}
