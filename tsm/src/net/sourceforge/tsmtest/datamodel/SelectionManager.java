/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Tobias Hirning - some refactoring 	
 * 	Wolfang Kraus - added support for the graph
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.tsmtest.Activator;
import net.sourceforge.tsmtest.TSMPropertyTester;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Manages SelectionChanges. <br>
 * Just use <code>SelectionManager.instance.register(this);</code> in the
 * <code>createPartControl()</code>-Method. <br>
 * and<br>
 * <code>SelectionManager.instance.unregister(this);</code> in the
 * <code>dispose()</code>-Method. Remember to let your ViewPart implement
 * {@link SelectionObservable}.
 * 
 * @author Albert Flaig
 */
public final class SelectionManager {
    /**
     * Returns the Singleton of this class.
     */
    public static final SelectionManager instance = new SelectionManager();

    /**
     * The array of observables to notify when a selection change occurs.
     */
    private final ArrayList<SelectionObservable> observables = new ArrayList<SelectionObservable>();

    /**
     * Map of resources which failed to be resolved and their corresponding
     * exception.
     */
    private final Map<IFile, CoreException> failedResources = new HashMap<IFile, CoreException>();

    /**
     * The global selection change listener, which resolves selection changes in
     * the eclipse window and sends notify to observables.
     */
    private final ISelectionListener serviceListener = new ISelectionListener() {
	@Override
	public void selectionChanged(final IWorkbenchPart part,
		final ISelection selection) {
	    if (selection instanceof IStructuredSelection) {
		final SelectionModel sm = processSelection(selection);
		changeSelection(sm);
	    }
	}
    };

    /**
     * The current selection.
     */
    private SelectionModel selectionModel;

    private SelectionManager() {
	Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
		.getSelectionService().addSelectionListener(serviceListener);
    }

    /**
     * Register for Selection changes. Should be called in the
     * <code>createPartControl()</code>-Method of a ViewPart.
     * 
     * @param listener
     *            The listener to receive selection updates
     * @usage <code>SelectionManager.instance.register(this);</code>
     */
    public void register(final SelectionObservable listener) {
	observables.add(listener);
    }

    /**
     * Unregister for Selection changes. Should be called in the
     * <code>dispose()</code>-Method of a ViewPart.
     * 
     * @param listener
     *            the listener which receives selection updates
     * @usage <code>SelectionManager.instance.unregister(this);</code>
     */
    public void unregister(final SelectionObservable listener) {
	observables.remove(listener);
    }

    /**
     * Changes the current selection and notifies each listener.
     * 
     * @param selectionModel
     */
    private void changeSelection(final SelectionModel selectionModel) {
	this.selectionModel = selectionModel;
	for (final SelectionObservable listener : observables) {
	    listener.selectionChanged();
	}
	TSMPropertyTester.refresh();
    }

    /**
     * @return the current selection.
     * @throws SelectionException
     *             is thrown when at least one file of the selection failed to
     *             be resolved.
     */
    public SelectionModel getSelection() {
	// nothing selected yet? check current selection
	if (selectionModel == null) {
	    final ISelection selection = Activator.getDefault().getWorkbench()
		    .getActiveWorkbenchWindow().getSelectionService()
		    .getSelection();
	    if (selection instanceof IStructuredSelection) {
		final SelectionModel sm = processSelection(selection);
		changeSelection(sm);
		// at least one resource failed to be resolved? -> throw
		// exception
		if (!failedResources.isEmpty()) {
		    // throw new SelectionException(failedResources, sm);
		}
		return sm;
	    } else {
		// FIXME if the current selection is not structured selection,
		// it returns an empty selectionModel. This is a workaround as
		// it should only return an empty selectionModel when a
		// structeredSelection never even once occurred. As it is right
		// now this is not the case.
		final SelectionModel sm = new SelectionModel();
		// at least one resource failed to be resolved? -> throw
		// exception
		if (!failedResources.isEmpty()) {
		    // throw new SelectionException(failedResources, sm);
		}
		changeSelection(sm);
		return sm;
	    }
	}
	// at least one resource failed to be resolved? -> throw exception
	if (!failedResources.isEmpty()) {
	    // throw new SelectionException(failedResources, selectionModel);
	}
	return selectionModel;
    }

    public SelectionModel getSelection(final ExecutionEvent event) {
	final ISelection is = HandlerUtil.getCurrentSelection(event);
	final SelectionModel sm = processSelection(is);
	if (sm != null) {
	    changeSelection(sm);
	}
	// at least one resource failed to be resolved? -> throw
	// exception
	if (!failedResources.isEmpty()) {
	    // throw new SelectionException(failedResources, sm);
	}
	return selectionModel;
    }

    public SelectionModel processSelection(final ISelection iSelection) {
	// clear the failed resources map since we are loading anew.
	failedResources.clear();
	if (!(iSelection instanceof IStructuredSelection)) {
	    return null;
	}
	final IStructuredSelection ss = (IStructuredSelection) iSelection;
	final SelectionModel sm = new SelectionModel();
	for (final Object res : ss.toArray()) {
	    TSMResource resource = null;
	    if (res instanceof IResource) {
		resource = DataModel.getInstance().convertToTSMResource(res);
		if (resource == null) {
		    continue;
		}
	    }
	    if (res instanceof TSMResource) {
		resource = (TSMResource) res;
	    }
	    if (resource instanceof TSMTestCase) {
		sm.add((TSMTestCase) resource);
	    } else if (resource instanceof TSMReport) {
		sm.add((TSMReport) resource);
	    } else if (resource instanceof TSMProject) {
		sm.add((TSMProject) resource);
	    } else if (resource instanceof TSMPackage) {
		sm.add((TSMPackage) resource);
	    }
	}
	return sm;
    }

    /**
     * Need to be caught to check for failed resources. <br>
     * <b>Note</b>: This is only thrown when trying to get the content type of
     * certain selected files. If you do not need to know if certain files are
     * test-case-files or test-case-protocol-files, then you can safely ignore
     * this exception and use <code>getPassedResources()</code>
     * 
     * @author Albert Flaig
     * @see {@link getFailedResources()}
     * @see {@link getPassedResources()}
     */
    public class SelectionException extends Exception {
	private static final long serialVersionUID = 1L;
	private final Map<IFile, CoreException> failedResources;
	private final SelectionModel selectionModel;

	/**
	 * @return a map which stores all files which have failed to be resolved
	 *         and the corresponding exception.
	 */
	public Map<IFile, CoreException> getFailedFiles() {
	    return failedResources;
	}

	/**
	 * You can still get the selected resources with this method. But
	 * remember to handle this exception and note that the failed files are
	 * included in the otherFiles-List.
	 * 
	 * @return the current selection model with failed files included in the
	 *         otherFiles-list.
	 */
	public SelectionModel getSelection() {
	    return selectionModel;
	}

	public SelectionException(
		final Map<IFile, CoreException> failedResources,
		final SelectionModel passedResources) {
	    this.failedResources = failedResources;
	    selectionModel = passedResources;
	}
    }

    /**
     * Must be implemented to receive selection updates.
     */
    public interface SelectionObservable {
	/**
	 * @param selectionModel
	 *            stores the objects which are currently selected.
	 */
	public void selectionChanged();
    }

    /**
     * This class stores the selection in form of objects of testCases, packages
     * and projects for easy handling. <br>
     * <b>Note</b>: Failed files are included in <i>otherFiles</i>.
     */
    public class SelectionModel {
	private final ArrayList<TSMReport> testCaseProtocols = new ArrayList<TSMReport>();
	private final ArrayList<TSMTestCase> testCases = new ArrayList<TSMTestCase>();
	private final ArrayList<TSMPackage> packages = new ArrayList<TSMPackage>();
	private final ArrayList<TSMProject> projects = new ArrayList<TSMProject>();
	private TSMResource firstResource;
	private TSMResource firstFile;

	/**
	 * @return a list of all selected files which are (testCases &#x222A;
	 *         testCaseProtocols)
	 */
	public ArrayList<TSMResource> getAllFiles() {
	    final ArrayList<TSMResource> list = new ArrayList<TSMResource>();
	    list.addAll(testCaseProtocols);
	    list.addAll(testCases);
	    return list;
	}

	/**
	 * 
	 * @return a list of all selected TSMResources
	 */
	public ArrayList<TSMResource> getAllResources() {
	    final ArrayList<TSMResource> list = new ArrayList<TSMResource>();
	    list.addAll(testCaseProtocols);
	    list.addAll(testCases);
	    list.addAll(packages);
	    list.addAll(projects);
	    return list;
	}

	/**
	 * @return a List of selected TestCases
	 */
	public ArrayList<TSMTestCase> getTestCases() {
	    return testCases;
	}

	/**
	 * @return a List of selected Packages
	 */
	public ArrayList<TSMPackage> getPackages() {
	    return packages;
	}

	/**
	 * @return a List of selected Projects
	 */
	public ArrayList<TSMProject> getProjects() {
	    return projects;
	}

	/**
	 * @return a List of selected Protocols
	 */
	public ArrayList<TSMReport> getProtocols() {
	    return testCaseProtocols;
	}

	/**
	 * Returns the number of selected objects in general.
	 * 
	 * @return <code>testCaseProtocols.size() + testCases.size() + folders.size() + projects.size()</code>
	 * @note it does include files which have failed to be resolved. See the
	 *       {@link SelectionException} for that.
	 */
	public int getSize() {
	    return testCaseProtocols.size() + testCases.size()
		    + packages.size() + projects.size();
	}

	/**
	 * @return whether at least one resource is selected
	 * @note equals <code>getSize() == 0</code>
	 */
	public boolean isEmpty() {
	    return getSize() == 0;
	}

	/**
	 * @return the first resource which has been selected. In the package
	 *         explorer this is the top selected resource. Can also return
	 *         null if the selection is empty.
	 */
	public TSMResource getFirstResource() {
	    return firstResource;
	}

	/**
	 * @return the first file which has been selected. In the package
	 *         explorer this is the top selected file. Can also return null
	 *         if the selection is empty or no file is selected.
	 */
	public TSMResource getFirstFile() {
	    return firstFile;
	}

	/**
	 * Checks whether the first resource is a File. <br>
	 * <i>When true:</i> The container of the resource is returned <br>
	 * <i>When false:</i> The resource is returned
	 * 
	 * @return the selected container, or when a file is selected then its
	 *         container. Can also return null if the selection is empty.
	 * @see {@link getFirstResource()}
	 */
	public TSMContainer getFirstContainer() {
	    if (firstResource == null) {
		return null;
	    }
	    if (firstResource instanceof TSMContainer) {
		return (TSMContainer) firstResource;
	    }
	    return firstResource.getParent();
	}

	void add(final TSMTestCase res) {
	    if (firstResource == null) {
		firstResource = res;
	    }
	    if (firstFile == null) {
		firstFile = res;
	    }
	    testCases.add(res);
	}

	void add(final TSMReport res) {
	    if (firstResource == null) {
		firstResource = res;
	    }
	    if (firstFile == null) {
		firstFile = res;
	    }
	    testCaseProtocols.add(res);
	}

	void add(final TSMPackage res) {
	    if (firstResource == null) {
		firstResource = res;
	    }
	    packages.add(res);
	}

	void add(final TSMProject res) {
	    if (firstResource == null) {
		firstResource = res;
	    }
	    projects.add(res);
	}
    }

}