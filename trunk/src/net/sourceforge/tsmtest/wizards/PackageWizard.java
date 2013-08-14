 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Bernhard Wetzel - various fixes
 *    Verena Käfer - enhancements
 *    Wolfgang Kraus - some fixes
 *    Tobias Hirning - some refactoring, i18n
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.TSMContainer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class PackageWizard extends Wizard implements INewWizard {
    private NewPackageWizardPage mainPage;
    private IStructuredSelection selection;
    public final static String id = "net.sourceforge.tsmtest.wizards.new.TSMPackageWizard"; //$NON-NLS-1$

    public PackageWizard() {
	super();
	setNeedsProgressMonitor(true);
	setWindowTitle(Messages.PackageWizard_1);
    }

    @Override
    public void addPages() {
	super.addPages();
	mainPage = new NewPackageWizardPage(Messages.PackageWizard_2, selection);
	mainPage.setTitle(Messages.PackageWizard_3);
	mainPage.setDescription(Messages.PackageWizard_4);

	addPage(mainPage);
    }

    @Override
    public boolean performFinish() {
	final IFolder folder = ResourcesPlugin.getWorkspace().getRoot()
		.getFolder(mainPage.getFolderPath());

	final IContainer container = folder.getParent();

	final TSMContainer tsmContainer = (TSMContainer) DataModel
		.getInstance().convertToTSMResource(container);

	try {
	    tsmContainer.createPackage(folder.getName());
	} catch (final DataModelException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return true;
    }

    @Override
    public void init(final IWorkbench workbench,
	    final IStructuredSelection selection) {
	this.selection = selection;
    }

}
