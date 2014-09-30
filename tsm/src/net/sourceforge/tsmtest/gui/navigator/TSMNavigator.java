 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Jenny Krüwald - fixed "clear selection" button
 *    Tobias Hirning - disabled filter code
 *******************************************************************************/ 
package net.sourceforge.tsmtest.gui.navigator;

import net.sourceforge.tsmtest.datamodel.AbstractDataModel.DataModelObservable;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.gui.filter.FilterManager;
import net.sourceforge.tsmtest.gui.filter.FilterManager.FilterListener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.navigator.NavigatorPlugin;
import org.eclipse.ui.part.ViewPart;

@SuppressWarnings("restriction")
public class TSMNavigator extends ViewPart implements DataModelObservable, FilterListener {

    public static final String ID = "net.sourceforge.tsmtest.gui.navigator.TSMNavigator";
    private TSMTreeViewer tsmViewer;

    @Override
    public void createPartControl(final Composite parent) {
	tsmViewer = new TSMTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
		| SWT.V_SCROLL);
	getSite().setSelectionProvider(tsmViewer);
	final Clipboard clipboard = new Clipboard(getSite().getShell()
		.getDisplay());

	initializeToolBar();
	initializeGlobalActions(clipboard);

	new TSMNavigatorContextMenu(tsmViewer, getSite().getWorkbenchWindow(),
		clipboard);

	DataModel.getInstance().register(this);
	FilterManager.getInstance().register(this);
    }

    private void initializeGlobalActions(final Clipboard clipboard) {
	// add cut and paste support
	final IActionBars bars = getViewSite().getActionBars();
	bars.setGlobalActionHandler(ActionFactory.CUT.getId(), 
		new CutAction("", getSite().getWorkbenchWindow(), clipboard));
	bars.setGlobalActionHandler(ActionFactory.COPY.getId(), 
		new CopyAction("",getSite().getWorkbenchWindow(), clipboard));
	bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), 
		new PasteAction("", getSite().getWorkbenchWindow(), clipboard));
	bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), 
		new DeleteAction("", getSite().getWorkbenchWindow()));
    }

    private void initializeToolBar() {
	final IToolBarManager toolbarManager = getViewSite().getActionBars()
		.getToolBarManager();
	final Action collapseAllAction = new Action() {
	    @Override
	    public void run() {
		tsmViewer.collapseAll();
	    }
	};
	final ImageDescriptor collapseAllIcon = NavigatorPlugin
		.getImageDescriptor("icons/full/elcl16/collapseall.gif"); //$NON-NLS-1$
	collapseAllAction.setImageDescriptor(collapseAllIcon);
	collapseAllAction.setHoverImageDescriptor(collapseAllIcon);
	toolbarManager.add(new NewProjectAction(""));
	toolbarManager.add(new NewPackageAction("", tsmViewer));
	toolbarManager.add(new NewTestCaseAction("", tsmViewer));
	toolbarManager.add(new Separator());
	toolbarManager.add(new RunTestAction(""));
	toolbarManager.add(new Separator());
	toolbarManager.add(collapseAllAction);
    }

    @Override
    public void setFocus() {
	// TODO Auto-generated method stub
    }

    @Override
    public void dataModelChanged() {
	Display.getDefault().asyncExec(new Runnable() {
	    @Override
	    public void run() {
		//Get already expanded elements, refresh and expand them again.
		final Object[] expandedElements = tsmViewer
			.getExpandedElements();
		tsmViewer.refresh();
		tsmViewer.setExpandedElements(expandedElements);
	    }
	});
    }
    
    @Override
    public void filterChanged() {
	//Get already expanded elements, refresh and expand them again.
	final Object[] expandedElements = tsmViewer
		.getExpandedElements();
	tsmViewer.refresh();
	tsmViewer.setExpandedElements(expandedElements);
    }
}
