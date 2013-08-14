 /*******************************************************************************
 * Copyright (c) 2012-2013 Daniel Hertl.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Hertl - initial version
 *    Albert Flaig - various fixes
 *    Bernhard Wetzel - various fixes, some refactoring, i18n
 *    Verena Käfer - enhancements
 *    Tobias Hirning - some refactoring, i18n
 *    Wolfgang Kraus - fix
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.gui.newtestcase.view.ViewTestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class NewTestcaseWizard extends Wizard implements INewWizard {
    private NewTestcaseWizardPage page;
    private static IStructuredSelection selection;
    private TSMTestCase createdTestCase;
    public final static String id = "net.sourceforge.tsmtest.wizards.NewTestcaseWizard"; //$NON-NLS-1$

    public static void open() throws CoreException {
	final IWizardDescriptor descriptor = PlatformUI.getWorkbench()
		.getNewWizardRegistry().findWizard(id);
	if (descriptor != null) {
	    final IWizard wizard = descriptor.createWizard();
	    final WizardDialog wd = new WizardDialog(Display.getCurrent()
		    .getActiveShell(), wizard);
	    wd.setTitle(wizard.getWindowTitle());
	    wd.open();
	}
    }

    public static void open(final IStructuredSelection selected)
	    throws CoreException {
	selection = selected;
	final IWizardDescriptor descriptor = PlatformUI.getWorkbench()
		.getNewWizardRegistry().findWizard(id);
	if (descriptor != null) {
	    final IWizard wizard = descriptor.createWizard();
	    final WizardDialog wd = new WizardDialog(Display.getCurrent()
		    .getActiveShell(), wizard);
	    wd.setTitle(wizard.getWindowTitle());
	    wd.open();
	}
    }

    /**
     * Constructor for NewTestcaseWizard.
     */
    public NewTestcaseWizard() {
	super();
	setNeedsProgressMonitor(true);
	setWindowTitle(Messages.NewTestcaseWizard_1);
    }

    /**
     * Adding the page to the wizard.
     */

    @Override
    public void addPages() {
	page = new NewTestcaseWizardPage(selection);
	page.setTitle(Messages.NewTestcaseWizard_2);
	page.setDescription(Messages.NewTestcaseWizard_3);
	addPage(page);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    @Override
    public boolean performFinish() {
	final String containerName = page.getContainerName();
	final String fileName = page.getFileName();

	final IRunnableWithProgress op = new IRunnableWithProgress() {
	    @Override
	    public void run(final IProgressMonitor monitor)
		    throws InvocationTargetException {
		try {
		    doFinish(containerName, fileName, monitor);
		} catch (final CoreException e) {
		    throw new InvocationTargetException(e);
		} finally {
		    monitor.done();
		}
	    }
	};
	try {
	    getContainer().run(true, true, op);
	} catch (final InterruptedException e) {
	    return false;
	} catch (final InvocationTargetException e) {
	    final Throwable realException = e.getTargetException();
	    MessageDialog.openError(getShell(), Messages.NewTestcaseWizard_4,
		    realException.getMessage());
	    return false;
	}
	try {
	    ViewTestCase.openGUI(createdTestCase);
	} catch (final PartInitException e) {
	    e.printStackTrace();
	}
	return true;
    }

    /**
     * The worker method. It will find the container, create the file if missing
     * or just replace its contents, and open the editor on the newly created
     * file.
     */

    private void doFinish(final String containerName, final String fileName,
	    final IProgressMonitor monitor) throws CoreException {
	// create a sample file
	monitor.beginTask(Messages.NewTestcaseWizard_5 + fileName, 1);

	final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	final IResource resource = root.findMember(new Path(containerName));
	if (!resource.exists() || !(resource instanceof IContainer)) {
	    throwCoreException(Messages.NewTestcaseWizard_6 + containerName
		    + Messages.NewTestcaseWizard_7);
	}
	if (!resource.getProject().hasNature(
		"net.sourceforge.tsmtest.datamodel.TSMNature")) { //$NON-NLS-1$
	    throwCoreException(Messages.NewTestcaseWizard_9);
	}

	final IContainer container = (IContainer) resource;

	// create test case
	final TestCaseDescriptor testCase = new TestCaseDescriptor();

	try {
	    createdTestCase = ((TSMContainer) DataModel.getInstance()
		    .convertToTSMResource(container)).createTestCase(fileName,
		    testCase);
	} catch (final DataModelException e) {
	    throwCoreException(e.getLocalizedMessage());
	}
	monitor.worked(1);

    }

    private void throwCoreException(final String message) throws CoreException {
	final IStatus status = new Status(IStatus.ERROR, "net.sourceforge.tsmtest", //$NON-NLS-1$
		IStatus.OK, message, null);
	throw new CoreException(status);
    }

    @Override
    public void init(final IWorkbench workbench,
	    final IStructuredSelection selection) {
	NewTestcaseWizard.selection = selection;
    }
}