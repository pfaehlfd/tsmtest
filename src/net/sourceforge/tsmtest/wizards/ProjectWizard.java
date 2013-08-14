 /*******************************************************************************
 * Copyright (c) 2012-2013 Bernhard Wetzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bernhard Wetzel - initial version
 *    Verena Käfer - some fixes
 *    Daniel Hertl - enhancements
 *    Albert Flaig - data model refactoring
 *    Tobias Hirning - some refactoring, i18n
 *    Wolfgang Kraus - some refactoring
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.TSMProject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class ProjectWizard extends Wizard implements INewWizard, IExecutableExtension {
    private ProjectWizardPage mainPage;
    private IStructuredSelection selection;
    IConfigurationElement configElement;
    public final static String id = "net.sourceforge.tsmtest.wizards.new.TSMProjectWizard"; //$NON-NLS-1$

    public ProjectWizard() {
	super();
	setNeedsProgressMonitor(true);
	setWindowTitle(Messages.ProjectWizard_1);
    }
    
    @Override
    public void addPages() {
        super.addPages();
        
        mainPage = new ProjectWizardPage("basicNewProjectPage", selection); //$NON-NLS-1$
        mainPage.setTitle(Messages.ProjectWizard_3);
        mainPage.setDescription(Messages.ProjectWizard_4);
        
        addPage(mainPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
	this.selection = selection;
    }
    
    @Override
    public boolean performFinish() {
	IWorkingSet[] workingSets = mainPage.getSelectedWorkingSets();

	try {
	    TSMProject project = TSMProject.create(mainPage.getProjectName(), workingSets);
	    while(!project.exists()){
		//wait until project exists
	    }
	    project.createPackage(DataModelTypes.imageFolderName);
	} catch (DataModelException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	BasicNewProjectResourceWizard.updatePerspective(configElement);
	return true;
    }

    @Override
    public void setInitializationData(IConfigurationElement config,
	    String propertyName, Object data) throws CoreException {
	configElement = config;
	
    }

}
