 /*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Verena Käfer - initial version
 *    Albert Flaig - data model refactoring
 *    Tobias Hirning - some refactoring, i18n
 *    Wolfgang Kraus - code cleanup
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.tsmtest.Activator;
import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.io.pdf.ExportPdf;
import net.sourceforge.tsmtest.io.pdf.FontsToolsConstants.ExportType;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.itextpdf.text.DocumentException;

/**
 * @author Verena Käfer
 * 
 *         This class creates a basic export wizard and adds one page.
 * 
 */
public class PdfWizard extends Wizard implements INewWizard {

    private PdfPage page;

    /**
     * The constructor
     */
    public PdfWizard() {
	super();
	setNeedsProgressMonitor(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
	ISelection selection = Activator.getDefault().getWorkbench()
		.getActiveWorkbenchWindow().getSelectionService()
		.getSelection();
	if (selection != null) {
	    page = new PdfPage(Messages.PdfWizard_0,
		    (IStructuredSelection) selection);
	    addPage(page);
	} else {
	    IStructuredSelection s = new IStructuredSelection() {

		@Override
		public boolean isEmpty() {
		    // TODO Auto-generated method stub
		    return false;
		}

		@Override
		public List toList() {
		    // TODO Auto-generated method stub
		    return null;
		}

		@Override
		public Object[] toArray() {
		    // TODO Auto-generated method stub
		    return null;
		}

		@Override
		public int size() {
		    // TODO Auto-generated method stub
		    return 0;
		}

		@Override
		public Iterator iterator() {
		    // TODO Auto-generated method stub
		    return null;
		}

		@Override
		public Object getFirstElement() {
		    // TODO Auto-generated method stub
		    return null;
		}
	    };
	    page = new PdfPage(Messages.PdfWizard_0, s);

	}
    }

    private class InternalRunnable implements Runnable {
	private String path;

	@Override
	public void run() {
	    path = page.getPath();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
	final ExportType exportType = page.getExportType();
	boolean yesToAll = false;
	boolean noToAll = false;
	int answer = 0;

	try {
	    if (page.getProtocols().isEmpty()) {
		page.setErrorMessage(Messages.PdfWizard_1);
		return false;
	    }
	} catch (CoreException e1) {
	    page.setErrorMessage(e1.getLocalizedMessage());
	    return false;
	}
	
	final List<TSMResource> newList;
	try {
	    newList = page.getProtocols();
	} catch (CoreException e1) {
	    page.setErrorMessage(e1.getLocalizedMessage());
	    return false;
	}

	// check if path is valid
	if (page.getPath().equals("")) { //$NON-NLS-1$
	    page.setErrorMessage(Messages.PdfWizard_3);
	    return false;
	}

	if (exportType.equals(ExportType.ONE_FILE)) {
	    File file = new File(page.getPath());
	    // file is a folder
	    if (file.isDirectory()) {
		page.setErrorMessage(Messages.PdfWizard_4);
		return false;
	    }

	    // file has to be overwritten
	    if (file.exists()) {
		final boolean confirm = MessageDialog.openConfirm(getShell(),
			Messages.PdfWizard_5, Messages.PdfWizard_6);
		if (!confirm) {
		    return false;
		}
	    }

	} else if (exportType.equals(ExportType.MULTIPLE_FILES)){
	    final File directory = new File(page.getPath());
	    // file is not a folder
	    if (!directory.isDirectory()) {
		page.setErrorMessage(Messages.PdfWizard_7);
		return false;
	    }

	    for (int i = 0; i < newList.size(); i++) {
		TSMResource resource = newList.get(i);
		final String fileName = page.getPath() + resource.getPath()
			+ "/" //$NON-NLS-1$
			+ resource.getName() + ".pdf"; //$NON-NLS-1$
		File target = new File(fileName);

		// Check whether file already exists.
		if (target.exists()) {
		    // If user has chosen no to all every following file is not
		    // exported.
		    if (noToAll) {
			newList.remove(i);
			i--;
		    }
		    // If user hasn't chosen to overwrite every file we ask him
		    // again.
		    else if (!yesToAll) {
			// We use a custom MessageDialog where the first button
			// "Yes" is default.
			MessageDialog dialog = new MessageDialog(getShell(),
				Messages.PdfWizard_10, null,
				Messages.PdfWizard_11 + resource.getPath()
					+ resource.getName()
					+ Messages.PdfWizard_12,
				MessageDialog.QUESTION, new String[] {
					Messages.PdfWizard_9,
					Messages.PdfWizard_16,
					Messages.PdfWizard_17,
					Messages.PdfWizard_18 }, 0);

			// Show the dialog.
			answer = dialog.open();

			switch (answer) {
			// Button "Yes".
			case 0:
			    break;
			// Button "No".
			case 1:
			    newList.remove(i);
			    // We need to decrement i so that the for-loop is
			    // still executed.
			    i--;
			    break;
			// Button "Yes to all".
			case 2:
			    yesToAll = true;
			    break;
			// Button "No to all".
			case 3:
			    noToAll = true;
			    newList.remove(i);
			    // We need to decrement i so that the for-loop is
			    // still executed.
			    i--;
			    break;
			default:
			    break;
			}

		    }
		}
	    }

	}
	// monitor
	final IRunnableWithProgress op = new IRunnableWithProgress() {
	    public void run(final IProgressMonitor monitor)
		    throws InvocationTargetException {
		InternalRunnable r = new InternalRunnable();
		Display.getDefault().syncExec(r);
		monitor.beginTask("Export", IProgressMonitor.UNKNOWN);
		try {
		    ExportPdf.print(newList, r.path, exportType,
			    getSubMonitor(monitor, 1));
		} catch (DocumentException e) {
		    throw new InvocationTargetException(e);
		} catch (IOException e) {
		    throw new InvocationTargetException(e);
		}
	    }
	};
	try {
	    getContainer().run(true, true, op);
	} catch (InterruptedException e) {
	    page.setErrorMessage(e.getLocalizedMessage());
	    // return false;
	} catch (InvocationTargetException e) {
	    Throwable realException = e.getTargetException();
	    MessageDialog.openError(getShell(), Messages.NewTestcaseWizard_4,
		    realException.getMessage());
	    // return false;
	}
	// end monitor
	return true;

	// return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    /**
     * @param monitor
     * @param taskAmount
     * @return A submonitor for pMonitor
     */
    public static IProgressMonitor getSubMonitor(IProgressMonitor monitor,
	    int taskAmount) {
	if (monitor == null) {
	    return new NullProgressMonitor();
	}
	if (monitor instanceof NullProgressMonitor) {
	    return monitor;
	}
	return new SubProgressMonitor(monitor, taskAmount,
		SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
    }
}