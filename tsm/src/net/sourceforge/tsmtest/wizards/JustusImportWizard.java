 /*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Verena Käfer - initial version
 *    Tobias Hirning - i18n
 *    Wolfgang Kraus - code cleanup
 *    Albert Flaig - some fixes
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.tsmtest.Activator;
import net.sourceforge.tsmtest.IDEWorkbenchMessages;
import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.io.justus.JustusImport;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * @author Verena Käfer
 * 
 *         This class creates a basic export wizard and adds one page.
 * 
 */
public class JustusImportWizard extends Wizard implements INewWizard {

    private JustusPage page;
    private int elements;

    /**
     * The constructor
     */
    public JustusImportWizard() {
	super();
	setNeedsProgressMonitor(true);
	setWindowTitle(Messages.JustusImportWizard_0);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
	final ISelection selection = Activator.getDefault().getWorkbench()
		.getActiveWorkbenchWindow().getSelectionService()
		.getSelection();
	if (selection != null) {
	    page = new JustusPage(Messages.JustusImportWizard_1,
		    (IStructuredSelection) selection);
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
	    page = new JustusPage(Messages.JustusImportWizard_1, s);
	}
	addPage(page);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(final IWorkbench workbench,
	    final IStructuredSelection selection) {
    }

    class InternalRunnable implements Runnable {
	private IPath dest;
	private boolean sequence;

	@Override
	public void run() {
	    dest = page.getDestination();
	    sequence = page.isSequenceTestCase();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
	// check if xml file is selected
	final String source = page.getSource();
	if (source.endsWith(".xml") || source.endsWith(".jdf")) { //$NON-NLS-1$ //$NON-NLS-2$
	    // System.out.println(page.getDestination());
	    // destination folder exists
	    if (page.getDestination().segment(1) != null
		    && page.getDestination().segment(1)
			    .equals(DataModelTypes.imageFolderName)) {
		page.setErrorMessage(IDEWorkbenchMessages.ContainerSelectionDialog_1);
		return false;
	    }
	    if (ResourcesPlugin.getWorkspace().getRoot()
		    .exists(page.getDestination())) {
		final Element root = checkJustusFile(source);
		if (root != null) {
		    try {
			elements = countElements(root);
		    } catch (final CoreException e1) {
			page.setErrorMessage(e1.getLocalizedMessage());
			return false;
		    }

		    // monitor
		    final IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor)
				throws InvocationTargetException {
			    final InternalRunnable r = new InternalRunnable();
			    Display.getDefault().syncExec(r);
			    try {
				monitor.beginTask("Import",
					IProgressMonitor.UNKNOWN);
				JustusImport.importFile(root, r.dest,
					r.sequence,
					getSubMonitor(monitor, elements),
					elements);
			    } catch (final CoreException e) {
				page.setErrorMessage(e.getLocalizedMessage());
			    } catch (final DataModelException e) {
				page.setErrorMessage(e.getLocalizedMessage());
			    } catch (final ParseException e) {
				page.setErrorMessage(e.getLocalizedMessage());
			    } finally {
				monitor.done();
			    }
			}
		    };

		    try {
			getContainer().run(true, true, op);
		    } catch (final InterruptedException e) {
			page.setErrorMessage(e.getLocalizedMessage());
			return false;
		    } catch (final InvocationTargetException e) {
			final Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(),
				Messages.NewTestcaseWizard_4,
				realException.getMessage());
			return false;
		    }
		    // end monitor

		} else {
		    page.setErrorMessage(Messages.JustusImportWizard_4);
		    return false;
		}
	    } else {
		page.setErrorMessage(Messages.JustusImportWizard_5);
		return false;
	    }
	} else {
	    page.setErrorMessage(Messages.JustusImportWizard_2);
	    return false;
	}
	return true;
    }

    /*
     * See comment below
     */
    @SuppressWarnings("deprecation")
    private Element checkJustusFile(final String source) {
	Document doc = null;
	final SAXBuilder builder = new SAXBuilder(false);
	// doctype is ignored as the needed file isn't available for all users
	// and it throws a fileNotFoundException otherwise.
	builder.setValidation(false);
	builder.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
	builder.setFeature(
		"http://apache.org/xml/features/nonvalidating/load-dtd-grammar", //$NON-NLS-1$
		false);
	builder.setFeature(
		"http://apache.org/xml/features/nonvalidating/load-external-dtd", //$NON-NLS-1$
		false);
	try {
	    doc = builder.build(new File(source));
	} catch (final JDOMException e) {
	    return null;
	} catch (final IOException e) {
	    return null;
	}
	final Element root = doc.getRootElement();
	// System.out.println(root.getName());
	if (root.getChild("sequence") != null) { //$NON-NLS-1$
	    if (root.getChild("sequence").getChildText("description") != null) { //$NON-NLS-1$ //$NON-NLS-2$
		//Justus sets the description text according to the language that was first set, when the project was created.
		//Check for english description.
		if (root.getChild("sequence") //$NON-NLS-1$
			.getChildText("description") //$NON-NLS-1$
			.equals("This is the root node of the project." ) || //$NON-NLS-1$
			
			//Check for german description.
			root.getChild("sequence") //$NON-NLS-1$
				.getChildText("description") //$NON-NLS-1$
				.equals("Dies ist der Wurzelknoten des Projekts.")) { //$NON-NLS-1$
		return root;
		}
	    }
	}
	return null;
    }

    /**
     * The start method to count elements
     * 
     * @param root
     *            The root element
     * @return The amount of elements
     * @throws CoreException
     */
    private int countElements(final Element root) throws CoreException {

	int counter = 0;
	for (final Element e : root.getChildren()) {
	    counter++;
	    counter = count(e, counter);
	}
	return counter;
    }

    /**
     * A recursive method to count elements
     * 
     * @param e
     *            The start element
     * @param counter
     * @return The amount of all child elements
     */
    private int count(final Element e, int counter) {
	for (final Element element : e.getChildren()) {
	    if (element.getName().equals("sequence")
		    || element.getName().equals("testcase")) {
		counter++;
		if (element.getName().equals("sequence")) {
		    counter = count(element, counter);
		}
	    }

	}
	return counter;

    }

    /**
     * @param monitor
     * @param taskAmount
     * @return A submonitor for pMonitor
     */
    public static IProgressMonitor getSubMonitor(
	    final IProgressMonitor monitor, final int taskAmount) {
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