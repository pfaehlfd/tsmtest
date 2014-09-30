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

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.datamodel.TSMProject;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionModel;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Creates the context menu in the tsm navigator.
 * @author Albert Flaig
 *
 */
public class TSMNavigatorContextMenu {
    public TSMNavigatorContextMenu(final TSMTreeViewer tsmViewer,
	    final IWorkbenchWindow window, final Clipboard clipboard) {
	final MenuManager menuMgr = new MenuManager();

	menuMgr.addMenuListener(new IMenuListener() {
	    @Override
	    public void menuAboutToShow(final IMenuManager manager) {
		final SelectionModel sm = SelectionManager.getInstance()
			.processSelection(tsmViewer.getSelection());
		manager.add(createNewMenu(window, tsmViewer));
		if (sm != null && !sm.isEmpty()) {
		    manager.add(new Separator());
		    if (isTestCase(sm) || isPackage(sm) || isProject(sm)) {
			manager.add(new CutAction(
				Messages.TSMNavigatorContextMenu_0, window,
				clipboard));
			manager.add(new CopyAction(
				Messages.TSMNavigatorContextMenu_1, window,
				clipboard));
			if (PasteAction.enabled) {
			    manager.add(new PasteAction(
				    Messages.TSMNavigatorContextMenu_2, window,
				    clipboard));
			}
		    }
		    manager.add(new Separator());
		    manager.add(new DeleteAction(
			    Messages.TSMNavigatorContextMenu_3, window));
		    if (sm.getSize() == 1) {
			manager.add(new RenameAction(
				Messages.TSMNavigatorContextMenu_4, tsmViewer));
		    }
		    manager.add(new Separator());
		    manager.add(new RefreshAction(
			    Messages.TSMNavigatorContextMenu_5, window));
		    manager.add(new Separator());
		    manager.add(ActionFactory.IMPORT.create(window));
		    manager.add(ActionFactory.EXPORT.create(window));
		    manager.add(new Separator());
		    if (isTestCase(sm) || isPackage(sm) || isProject(sm)) {
			manager.add(new ShowOverviewAction(
				Messages.TSMNavigatorContextMenu_6));
		    }
		    if (isTestCase(sm)) {
			manager.add(new RunTestAction(
				Messages.TSMNavigatorContextMenu_7));
		    }
		}
	    }
	});
	menuMgr.setRemoveAllWhenShown(true);
	final Menu menu = menuMgr.createContextMenu(tsmViewer.getControl());
	tsmViewer.getControl().setMenu(menu);
    }

    /**
     * Determines whether a given selection is a test case.
     * @param sm Selection to be examined.
     * @return true if sm is TSMTestCase, false otherwise.
     */
    private boolean isTestCase(final SelectionModel sm) {
	return (sm != null && sm.getFirstResource() != null && sm
		.getFirstResource() instanceof TSMTestCase);
    }

    /**
     * Determines whether a given selection is a tsm project.
     * @param sm Selection to be examined.
     * @return true if sm is TSMProject, fals otherwise.
     */
    private boolean isProject(final SelectionModel sm) {
	return (sm != null && sm.getFirstResource() != null && sm
		.getFirstResource() instanceof TSMProject);
    }

    /**
     * Determines whether a given selection is a tsm package.
     * @param sm Selection to be examined.
     * @return true if sm is TSMPackage, false otherwise.
     */
    private boolean isPackage(final SelectionModel sm) {
	return (sm != null && sm.getFirstResource() != null && sm
		.getFirstResource() instanceof TSMPackage);
    }

    /**
     * Determines whether a given selection is a tsm report.
     * @param sm The Selection to be examined.
     * @return true if sm is TSMReport, false otherwise.
     */
    private boolean isReport(final SelectionModel sm) {
	return (sm != null && sm.getFirstResource() != null && sm
		.getFirstResource() instanceof TSMReport);
    }

    private IMenuManager createNewMenu(final IWorkbenchWindow window,
	    final TSMTreeViewer tsmViewer) {
	final IMenuManager menu = new MenuManager(
		Messages.TSMNavigatorContextMenu_8);
	menu.add(new NewProjectAction(Messages.TSMNavigatorContextMenu_9));
	menu.add(new NewPackageAction(Messages.TSMNavigatorContextMenu_10,
		tsmViewer));
	menu.add(new NewTestCaseAction(Messages.TSMNavigatorContextMenu_11,
		tsmViewer));
	menu.add(new Separator());
	menu.add(ActionFactory.NEW.create(window));
	return menu;
    }
}
