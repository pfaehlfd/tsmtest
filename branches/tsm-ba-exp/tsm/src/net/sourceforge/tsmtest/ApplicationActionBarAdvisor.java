
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

import net.sourceforge.tsmtest.gui.filter.FilterView;
import net.sourceforge.tsmtest.gui.quickview.view.Quickview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
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
    private IAction closeAction;
    private IAction closeAllAction;
    private IAction deleteAction;
    private IAction copyAction;
    private IAction refreshAction;
    private IAction cutAction1;
    private IAction pasteAction1;
    private IAction saveAsAction;
    private IAction saveAllAction;
    private IAction showViewMenuAction;
    private IAction resetPerspectiveAction;
    private IAction preferencesAction;
    private IAction showFilterAction;
    private IAction showQuickviewAction;
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

	IAction newAction;
	newAction = ActionFactory.NEW.create(window);
	register(newAction);

	closeAction = ActionFactory.CLOSE.create(window);
	register(closeAction);

	IAction closeAllSavedAction;
	closeAllSavedAction = ActionFactory.CLOSE_ALL_SAVED.create(window);
	register(closeAllSavedAction);

	closeAllAction = ActionFactory.CLOSE_ALL.create(window);
	register(closeAllAction);

	deleteAction = ActionFactory.DELETE.create(window);
	register(deleteAction);

	copyAction = ActionFactory.COPY.create(window);
	register(copyAction);

	IAction cutAction;
	cutAction = ActionFactory.CUT.create(window);
	register(cutAction);

	IAction pasteAction;
	pasteAction = ActionFactory.PASTE.create(window);
	register(pasteAction);

	refreshAction = ActionFactory.REFRESH.create(window);
	register(refreshAction);

	cutAction1 = ActionFactory.CUT.create(window);
	register(cutAction1);

	pasteAction1 = ActionFactory.PASTE.create(window);
	register(pasteAction1);

	saveAsAction = ActionFactory.SAVE_AS.create(window);
	register(saveAsAction);

	saveAllAction = ActionFactory.SAVE_ALL.create(window);
	register(saveAllAction);

	showViewMenuAction = ActionFactory.SHOW_VIEW_MENU.create(window);
	showViewMenuAction.setText(Messages.ApplicationActionBarAdvisor_1);
	register(showViewMenuAction);

	resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
	register(resetPerspectiveAction);
	
	//Toggle button to show and hide the filter view.
	showFilterAction = new Action(Messages.ApplicationActionBarAdvisor_7, IAction.AS_CHECK_BOX) {
	  @Override
	  public void run() {
	      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	      if (showFilterAction.isChecked()) {
		  try {
		    page.showView(FilterView.ID);
		} catch (PartInitException e) {
		    e.printStackTrace();
		}
	      } else {
		  page.hideView(page.findView(FilterView.ID));
	      }
	  }
	};
	showFilterAction.setChecked(true);
	
	//Toggle button to show and hide the quick view.
	showQuickviewAction = new Action(Messages.ApplicationActionBarAdvisor_9, IAction.AS_CHECK_BOX) {
	  @Override
	  public void run() {
	      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	      if (showQuickviewAction.isChecked()) {
		  try {
		    page.showView(Quickview.ID);
		} catch (PartInitException e) {
		    e.printStackTrace();
		}
	      } else {
		  page.hideView(page.findView(Quickview.ID));
	      }
	  }
	};
	showQuickviewAction.setChecked(true);
	

	IAction showViewMenuAction1;
	showViewMenuAction1 = ActionFactory.SHOW_VIEW_MENU.create(window);
	register(showViewMenuAction1);

	preferencesAction = ActionFactory.PREFERENCES.create(window);
	register(preferencesAction);

    }

    protected void fillMenuBar(IMenuManager menuBar) {
	MenuManager fileMenu = new MenuManager(
		Messages.ApplicationActionBarAdvisor_2, "fileTSM"); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$
	MenuManager editMenu = new MenuManager(
		Messages.ApplicationActionBarAdvisor_4, "edit"); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$
	MenuManager windowMenu = new MenuManager(
		Messages.ApplicationActionBarAdvisor_6, "window"); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$
	MenuManager helpMenu = new MenuManager(
		Messages.ApplicationActionBarAdvisor_8, "helpTSM"); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$
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
	editMenu.add(cutAction1);

	editMenu.add(copyAction);
	editMenu.add(pasteAction1);
	editMenu.add(new Separator());
	editMenu.add(deleteAction);

	windowMenu.add(showViewMenuAction);
	windowMenu.add(new Separator());
	windowMenu.add(resetPerspectiveAction);
	windowMenu.add(new Separator());
	windowMenu.add(showFilterAction);
	windowMenu.add(showQuickviewAction);
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

    /* (non-Javadoc)
     * @see org.eclipse.ui.application.ActionBarAdvisor#fillCoolBar(org.eclipse.jface.action.ICoolBarManager)
     */
    protected void fillCoolBar(ICoolBarManager coolBar) {
	IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
	coolBar.add(new ToolBarContributionItem(toolbar, "main")); //$NON-NLS-1$
	toolbar.add(saveAction);
	toolbar.add(showFilterAction);
	toolbar.add(showQuickviewAction);
    }
}
