 /*******************************************************************************
 * Copyright (c) 2012-2013 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Hirning - initial version
 *    Florian Krüger - enhancement
 *    Albert Flaig - refactoring, various fixes
 *    Bernhard Wetzel - some fixes
 *    Wolfgang Kraus - code cleanup, some fixes
 *    Daniel Hertl - enhancement
 *    Jenny Krüwald - added filter view
 *******************************************************************************/
package net.sourceforge.tsmtest;

import net.sourceforge.tsmtest.gui.filter.FilterView;
import net.sourceforge.tsmtest.gui.navigator.TSMNavigator;
import net.sourceforge.tsmtest.gui.quickview.view.Quickview;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

//import net.sourceforge.tsmtest.gui.newtestcase.view.ViewTestCase;

public class Perspective implements IPerspectiveFactory {

    public static final String ID = "net.sourceforge.tsmtest.perspective"; //$NON-NLS-1$

    /**
     * adds left&right view and makes editor visible
     */
    @Override
    public void createInitialLayout(final IPageLayout layout) {
	layout.setEditorAreaVisible(true);
	layout.setFixed(true);
	// left is TSMNaviagator
	final IFolderLayout topLeft = layout.createFolder("topLeft", // NON-NLS-1 //$NON-NLS-1$
		IPageLayout.LEFT, 0.20f, layout.getEditorArea());
	topLeft.addView(TSMNavigator.ID);
	// topLeft.addView(JavaUI.ID_PACKAGES); // NON-NLS-1
	topLeft.addView("org.eclipse.ui.navigator.ProjectExplorer");
	// bottom is filter
	final IFolderLayout bottom = layout.createFolder("bottom", // NON-NLS-1 //$NON-NLS-1$
		IPageLayout.BOTTOM, 0.67f, IPageLayout.ID_EDITOR_AREA);
	bottom.addView(FilterView.ID); // NON-NLS-1
	// right is quickview
	final IFolderLayout right = layout.createFolder("right", // NON-NLS-1 //$NON-NLS-1$
		IPageLayout.RIGHT, 0.78f, IPageLayout.ID_EDITOR_AREA);
	right.addView(Quickview.ID); // NON-NLS-1

    }

}
