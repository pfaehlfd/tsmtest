
 /*******************************************************************************
 * Copyright (c) 2012-2013 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobbias Hirning - initial version
 *    Verena Käfer - i18n
 *******************************************************************************/
package net.sourceforge.tsmtest;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    private IWorkbenchAction exitAction;
    private IWorkbenchAction saveAction;
    private IAction renameAction;
    private IAction moveAction;
    private IAction importAction;
    private IAction exportAction;
    private IAction aboutAction;
    private IAction newWizardDropDownAction;
    private IAction newAction;
    private IAction closeAction;
    private IAction closeAllSavedAction;
    private IAction closeAllAction;
    private IAction deleteAction;
    private IAction copyAction;
    private IAction cutAction;
    private IAction pasteAction;
    private IAction refreshAction;
    private IAction cutAction_1;
    private IAction pasteAction_1;
    private IAction saveAsAction;
    private IAction saveAllAction;
    private IAction showViewMenuAction;
    private IAction resetPerspectiveAction;
    private IAction showViewMenuAction_1;
    private IAction preferencesAction;
    private ManualAction manualAction;

    // Actions - important to allocate these only in makeActions, and then use
    // them
    // in the fill methods. This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
	super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
	exitAction = ActionFactory.QUIT.create(window);
	register(exitAction);

	saveAction = ActionFactory.SAVE.create(window);
	register(saveAction);

	renameAction = ActionFactory.RENAME.create(window);
	register(renameAction);

	moveAction = ActionFactory.MOVE.create(window);
	register(moveAction);

	importAction = ActionFactory.IMPORT.create(window);
	register(importAction);

	exportAction = ActionFactory.EXPORT.create(window);
	register(exportAction);

	manualAction = new ManualAction(Messages.ApplicationActionBarAdvisor_3);
	manualAction.setToolTipText(Messages.ApplicationActionBarAdvisor_5);

	aboutAction = ActionFactory.ABOUT.create(window);
	register(aboutAction);

	newWizardDropDownAction = ActionFactory.NEW_WIZARD_DROP_DOWN
		.create(window);
	newWizardDropDownAction.setText(Messages.ApplicationActionBarAdvisor_0);
	register(newWizardDropDownAction);

	newAction = ActionFactory.NEW.create(window);
	register(newAction);

	closeAction = ActionFactory.CLOSE.create(window);
	register(closeAction);

	closeAllSavedAction = ActionFactory.CLOSE_ALL_SAVED.create(window);
	register(closeAllSavedAction);

	closeAllAction = ActionFactory.CLOSE_ALL.create(window);
	register(closeAllAction);

	deleteAction = ActionFactory.DELETE.create(window);
	register(deleteAction);

	copyAction = ActionFactory.COPY.create(window);
	register(copyAction);

	cutAction = ActionFactory.CUT.create(window);
	register(cutAction);

	pasteAction = ActionFactory.PASTE.create(window);
	register(pasteAction);

	refreshAction = ActionFactory.REFRESH.create(window);
	register(refreshAction);

	cutAction_1 = ActionFactory.CUT.create(window);
	register(cutAction_1);

	pasteAction_1 = ActionFactory.PASTE.create(window);
	register(pasteAction_1);

	saveAsAction = ActionFactory.SAVE_AS.create(window);
	register(saveAsAction);

	saveAllAction = ActionFactory.SAVE_ALL.create(window);
	register(saveAllAction);

	showViewMenuAction = ActionFactory.SHOW_VIEW_MENU.create(window);
	showViewMenuAction.setText(Messages.ApplicationActionBarAdvisor_1);
	register(showViewMenuAction);

	resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
	register(resetPerspectiveAction);

	showViewMenuAction_1 = ActionFactory.SHOW_VIEW_MENU.create(window);
	register(showViewMenuAction_1);

	preferencesAction = ActionFactory.PREFERENCES.create(window);
	register(preferencesAction);

    }

    protected void fillMenuBar(IMenuManager menuBar) {
	MenuManager fileMenu = new MenuManager(
		Messages.ApplicationActionBarAdvisor_2, "fileTSM"); //$NON-NLS-2$ //$NON-NLS-1$
	MenuManager editMenu = new MenuManager(
		Messages.ApplicationActionBarAdvisor_4, "edit"); //$NON-NLS-2$ //$NON-NLS-1$
	MenuManager windowMenu = new MenuManager(
		Messages.ApplicationActionBarAdvisor_6, "window"); //$NON-NLS-2$ //$NON-NLS-1$
	MenuManager helpMenu = new MenuManager(
		Messages.ApplicationActionBarAdvisor_8, "helpTSM"); //$NON-NLS-2$ //$NON-NLS-1$
	fileMenu.add(newWizardDropDownAction);
	fileMenu.add(new Separator());
	fileMenu.add(closeAction);
	fileMenu.add(closeAllAction);
	fileMenu.add(new Separator());
	fileMenu.add(saveAction);
	fileMenu.add(saveAsAction);
	fileMenu.add(saveAllAction);
	fileMenu.add(new Separator());
	fileMenu.add(moveAction);
	fileMenu.add(renameAction);
	fileMenu.add(refreshAction);
	fileMenu.add(new Separator());
	fileMenu.add(importAction);
	fileMenu.add(exportAction);
	fileMenu.add(new Separator());

	fileMenu.add(exitAction);
	editMenu.add(cutAction_1);

	editMenu.add(copyAction);
	editMenu.add(pasteAction_1);
	editMenu.add(new Separator());
	editMenu.add(deleteAction);

	windowMenu.add(showViewMenuAction);
	windowMenu.add(new Separator());
	windowMenu.add(resetPerspectiveAction);
	windowMenu.add(new Separator());
	windowMenu.add(preferencesAction);

	helpMenu.add(manualAction);
	helpMenu.add(aboutAction);
	menuBar.removeAll();
	menuBar.add(fileMenu);
	menuBar.add(editMenu);
	menuBar.add(windowMenu);
	menuBar.add(helpMenu);
    }
}
