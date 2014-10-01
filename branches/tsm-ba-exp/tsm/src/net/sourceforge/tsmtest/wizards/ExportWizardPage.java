/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * Verena Käfer - enhancements
 * Tobias Hirning - i18n
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.tsmtest.IDEWorkbenchMessages;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.io.pdf.FontsToolsConstants.ExportType;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.TypeFilteringDialog;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.dialogs.ResourceTreeAndListGroup;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Abstract superclass for a typical export wizard's main page.
 * <p>
 * Clients may subclass this page to inherit its common destination resource
 * selection facilities.
 * </p>
 * <p>
 * Subclasses must implement
 * <ul>
 * <li><code>createDestinationGroup</code></li>
 * </ul>
 * </p>
 * <p>
 * Subclasses may override
 * <ul>
 * <li><code>allowNewContainerName</code></li>
 * </ul>
 * </p>
 * <p>
 * Subclasses may extend
 * <ul>
 * <li><code>handleEvent</code></li>
 * <li><code>internalSaveWidgetValues</code></li>
 * <li><code>updateWidgetEnablements</code></li>
 * </ul>
 * </p>
 * @author Verena Käfer
 */
@SuppressWarnings("restriction")
public abstract class ExportWizardPage extends WizardDataTransferPage {
    private final IStructuredSelection initialResourceSelection;

    private List<Object> selectedTypes = new ArrayList<Object>();

    // widgets
    private ResourceTreeAndListGroup resourceGroup;

    // private final static String SELECT_TYPES_TITLE =
    // IDEWorkbenchMessages.WizardTransferPage_selectTypes;

    private final static String SELECT_ALL_TITLE = net.sourceforge.tsmtest.IDEWorkbenchMessages.ExportWizardPage_2;

    private final static String DESELECT_ALL_TITLE = net.sourceforge.tsmtest.IDEWorkbenchMessages.ExportWizardPage_3;

    // boolean whether the export should be in one file or not
//    private boolean exportInOneFile = true;
    private ExportType exportType = null;

    /**
     * 0 = all files, 1 = test cases, 2 = protocols, 3 = revision
     */
    private int typeOfExportedFiles = 0;
    
    private Text revision;

    /**
     * Creates an export wizard page. If the current resource selection is not
     * empty then it will be used as the initial collection of resources
     * selected for export.
     * 
     * @param pageName
     *            the name of the page
     * @param selection
     *            {@link IStructuredSelection} of {@link IResource}
     * @see IDE#computeSelectedResources(IStructuredSelection)
     */
    protected ExportWizardPage(final String pageName,
	    final IStructuredSelection selection) {
	super(pageName);
	initialResourceSelection = selection;
    }

    /**
     * The <code>addToHierarchyToCheckedStore</code> implementation of this
     * <code>WizardDataTransferPage</code> method returns <code>false</code>.
     * Subclasses may override this method.
     */
    @Override
    protected boolean allowNewContainerName() {
	return false;
    }

    /**
     * Creates a new button with the given id.
     * <p>
     * The <code>Dialog</code> implementation of this framework method creates a
     * standard push button, registers for selection events including button
     * presses and registers default buttons with its shell. The button id is
     * stored as the buttons client data. Note that the parent's layout is
     * assumed to be a GridLayout and the number of columns in this layout is
     * incremented. Subclasses may override.
     * </p>
     * 
     * @param parent
     *            the parent composite
     * @param id
     *            the id of the button (see <code>IDialogConstants.*_ID</code>
     *            constants for standard dialog button ids)
     * @param label
     *            the label from the button
     * @param defaultButton
     *            <code>true</code> if the button is to be the default button,
     *            and <code>false</code> otherwise
     */
    protected Button createButton(final Composite parent, final int id,
	    final String label, final boolean defaultButton) {
	// increment the number of columns in the button bar
	((GridLayout) parent.getLayout()).numColumns++;

	final Button button = new Button(parent, SWT.PUSH);

	final GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
	button.setLayoutData(buttonData);

	button.setData(Integer.valueOf(id));
	button.setText(label);
	button.setFont(parent.getFont());

	if (defaultButton) {
	    final Shell shell = parent.getShell();
	    if (shell != null) {
		shell.setDefaultButton(button);
	    }
	    button.setFocus();
	}
	button.setFont(parent.getFont());
	setButtonLayoutData(button);
	return button;
    }

    /**
     * Creates the buttons for selecting specific types or selecting all or none
     * of the elements.
     * 
     * @param parent
     *            the parent control
     */
    protected final void createButtonsGroup(final Composite parent) {

	final Font font = parent.getFont();

	// top level group
	final Composite buttonComposite = new Composite(parent, SWT.NONE);
	buttonComposite.setFont(parent.getFont());

	final GridLayout layout = new GridLayout();
	layout.numColumns = 3;
	layout.makeColumnsEqualWidth = true;
	buttonComposite.setLayout(layout);
	buttonComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
		| GridData.HORIZONTAL_ALIGN_FILL));

	// types edit button
	// Button selectTypesButton = createButton(buttonComposite,
	// IDialogConstants.SELECT_TYPES_ID, SELECT_TYPES_TITLE, false);
	//
	// SelectionListener listener = new SelectionAdapter() {
	// public void widgetSelected(SelectionEvent e) {
	// handleTypesEditButtonPressed();
	// }
	// };
	// selectTypesButton.addSelectionListener(listener);
	// selectTypesButton.setFont(font);
	// setButtonLayoutData(selectTypesButton);

	final Button selectButton = createButton(buttonComposite,
		IDialogConstants.SELECT_ALL_ID, SELECT_ALL_TITLE, false);

	SelectionListener listener = new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		resourceGroup.setAllSelections(true);
	    }
	};
	selectButton.addSelectionListener(listener);
	selectButton.setFont(font);
	setButtonLayoutData(selectButton);

	final Button deselectButton = createButton(buttonComposite,
		IDialogConstants.DESELECT_ALL_ID, DESELECT_ALL_TITLE, false);

	listener = new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		resourceGroup.setAllSelections(false);
	    }
	};
	deselectButton.addSelectionListener(listener);
	deselectButton.setFont(font);
	setButtonLayoutData(deselectButton);

    }

    /**
     * (non-) Method declared on IDialogPage.
     */
    @Override
    public void createControl(final Composite parent) {

	initializeDialogUnits(parent);

	final Composite composite = new Composite(parent, SWT.NULL);
	composite.setLayout(new GridLayout());
	composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
		| GridData.HORIZONTAL_ALIGN_FILL));
	composite.setFont(parent.getFont());

	createResourcesGroup(composite);
	createButtonsGroup(composite);

	createDestinationGroup(composite);

	createOptionsGroup(composite);

	restoreResourceSpecificationWidgetValues(); // ie.- local
	restoreWidgetValues(); // ie.- subclass hook
	if (initialResourceSelection != null) {
	    setupBasedOnInitialSelections();
	}

	updateWidgetEnablements();
	setPageComplete(determinePageCompletion());
	setErrorMessage(null); // should not initially have error message

	setControl(composite);
    }

    /**
     * Creates the export destination specification visual components.
     * <p>
     * Subclasses must implement this method.
     * </p>
     * 
     * @param parent
     *            the parent control
     */
    protected abstract void createDestinationGroup(Composite parent);

    /**
     * Creates the checkbox tree and list for selecting resources.
     * 
     * @param parent
     *            the parent control
     */
    protected final void createResourcesGroup(final Composite parent) {

	// create the input element, which has the root resource
	// as its only child
	final List<IProject> input = new ArrayList<IProject>();
	final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
		.getProjects();
	for (int i = 0; i < projects.length; i++) {

	    if (projects[i].isOpen()) {
		input.add(projects[i]);
	    }
	}

	resourceGroup = new ResourceTreeAndListGroup(parent, input,
		getResourceProvider(IResource.FOLDER | IResource.PROJECT),
		WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
		getResourceProvider(IResource.FILE),
		WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
		SWT.NONE, DialogUtil.inRegularFontMode(parent));

	final ICheckStateListener listener = new ICheckStateListener() {
	    @Override
	    public void checkStateChanged(final CheckStateChangedEvent event) {
		updateWidgetEnablements();
	    }
	};

	resourceGroup.addCheckStateListener(listener);
    }

    /*
     * @see WizardDataTransferPage.getErrorDialogTitle()
     */
    @Override
    protected String getErrorDialogTitle() {
	return ""; //$NON-NLS-1$
    }

    /**
     * Obsolete method. This was implemented to handle the case where
     * ensureLocal() needed to be called but it doesn't use it any longer.
     * 
     * @deprecated Only retained for backwards compatibility.
     */
    @Deprecated
    protected boolean ensureResourcesLocal(final List<?> resources) {
	return true;
    }

    /**
     * Returns a new subcollection containing only those resources which are not
     * local.
     * 
     * @param originalList
     *            the original list of resources (element type:
     *            <code>IResource</code>)
     * @return the new list of non-local resources (element type:
     *         <code>IResource</code>)
     */
    @SuppressWarnings("deprecation")
    protected List<IResource> extractNonLocalResources(
	    final List<?> originalList) {
	final Vector<IResource> result = new Vector<IResource>(
		originalList.size());
	final Iterator<?> resourcesEnum = originalList.iterator();

	while (resourcesEnum.hasNext()) {
	    final IResource currentResource = (IResource) resourcesEnum.next();
	    if (!currentResource.isLocal(IResource.DEPTH_ZERO)) {
		result.addElement(currentResource);
	    }
	}

	return result;
    }

    /**
     * Returns a content provider for <code>IResource</code>s that returns only
     * children of the given resource type.
     */
    private ITreeContentProvider getResourceProvider(final int resourceType) {
	return new WorkbenchContentProvider() {
	    @Override
	    public Object[] getChildren(final Object o) {
		if (o instanceof IContainer) {
		    IResource[] members = null;
		    try {
			members = ((IContainer) o).members();
		    } catch (final CoreException e) {
			// just return an empty set of children
			return new Object[0];
		    }

		    // filter out the desired resource types
		    final ArrayList<IResource> results = new ArrayList<IResource>();
		    for (int i = 0; i < members.length; i++) {
			// And the test bits with the resource types to see if
			// they are what we want
			if ((members[i].getType() & resourceType) > 0) {
			    results.add(members[i]);
			}
		    }
		    return results.toArray();
		}
		// input element case
		if (o instanceof ArrayList) {
		    return ((ArrayList<?>) o).toArray();
		}
		return new Object[0];
	    }
	};
    }

    /**
     * Returns this page's collection of currently-specified resources to be
     * exported. This is the primary resource selection facility accessor for
     * subclasses.
     * 
     * @return a collection of resources currently selected for export (element
     *         type: <code>IResource</code>)
     */
    protected List getSelectedResources() {
	final Iterator resourcesToExportIterator = getSelectedResourcesIterator();
	final List resourcesToExport = new ArrayList<Object>();
	while (resourcesToExportIterator.hasNext()) {
	    resourcesToExport.add(resourcesToExportIterator.next());
	}
	return resourcesToExport;
    }

    /**
     * Returns this page's collection of currently-specified resources to be
     * exported. This is the primary resource selection facility accessor for
     * subclasses.
     * 
     * @return an iterator over the collection of resources currently selected
     *         for export (element type: <code>IResource</code>). This will
     *         include white checked folders and individually checked files.
     */
    protected Iterator<?> getSelectedResourcesIterator() {
	return resourceGroup.getAllCheckedListItems().iterator();
    }

    /**
     * Returns the resource extensions currently specified to be exported.
     * 
     * @return the resource extensions currently specified to be exported
     *         (element type: <code>String</code>)
     */
    protected List<Object> getTypesToExport() {

	return selectedTypes;
    }

    /**
     * Returns this page's collection of currently-specified resources to be
     * exported. This returns both folders and files - for just the files use
     * getSelectedResources.
     * 
     * @return a collection of resources currently selected for export (element
     *         type: <code>IResource</code>)
     */
    protected List<?> getWhiteCheckedResources() {

	return resourceGroup.getAllWhiteCheckedItems();
    }

    /**
     * Queries the user for the types of resources to be exported and selects
     * them in the checkbox group.
     */
    protected void handleTypesEditButtonPressed() {
	final Object[] newSelectedTypes = queryResourceTypesToExport();

	if (newSelectedTypes != null) { // ie.- did not press Cancel
	    selectedTypes = new ArrayList<Object>(newSelectedTypes.length);
	    for (int i = 0; i < newSelectedTypes.length; i++) {
		selectedTypes.add(newSelectedTypes[i]);
	    }
	    setupSelectionsBasedOnSelectedTypes();
	}

    }

    /**
     * Returns whether the extension of the given resource name is an extension
     * that has been specified for export by the user.
     * 
     * @param resourceName
     *            the resource name
     * @return <code>true</code> if the resource name is suitable for export
     *         based upon its extension
     */
    protected boolean hasExportableExtension(final String resourceName) {
	if (selectedTypes == null) {
	    return true;
	}

	final int separatorIndex = resourceName.lastIndexOf(':'); //$NON-NLS-1$
	if (separatorIndex == -1) {
	    return false;
	}

	final String extension = resourceName.substring(separatorIndex + 1);

	final Iterator<Object> it = selectedTypes.iterator();
	while (it.hasNext()) {
	    if (extension.equalsIgnoreCase((String) it.next())) {
		return true;
	    }
	}

	return false;
    }

    /**
     * Persists additional setting that are to be restored in the next instance
     * of this page.
     * <p>
     * The <code>WizardImportPage</code> implementation of this method does
     * nothing. Subclasses may extend to persist additional settings.
     * </p>
     */
    protected void internalSaveWidgetValues() {
    }

    /**
     * Queries the user for the resource types that are to be exported and
     * returns these types as an array.
     * 
     * @return the resource types selected for export (element type:
     *         <code>String</code>), or <code>null</code> if the user canceled
     *         the selection
     */
    protected Object[] queryResourceTypesToExport() {

	final TypeFilteringDialog dialog = new TypeFilteringDialog(
		getContainer().getShell(), getTypesToExport());

	dialog.open();

	return dialog.getResult();
    }

    /**
     * Restores resource specification control settings that were persisted in
     * the previous instance of this page. Subclasses wishing to restore
     * persisted values for their controls may extend.
     */
    protected void restoreResourceSpecificationWidgetValues() {
    }

    /**
     * Persists resource specification control setting that are to be restored
     * in the next instance of this page. Subclasses wishing to persist
     * additional setting for their controls should extend hook method
     * <code>internalSaveWidgetValues</code>.
     */
    @Override
    protected void saveWidgetValues() {

	// allow subclasses to save values
	internalSaveWidgetValues();

    }

    /**
     * Set the initial selections in the resource group.
     */
    protected void setupBasedOnInitialSelections() {

	final Iterator<?> it = initialResourceSelection.iterator();
	while (it.hasNext()) {
	    final Object obj = it.next();
	    if (obj instanceof TSMResource) {
		return;
	    }
	    final IResource currentResource = (IResource) obj;
	    if (currentResource.getType() == IResource.FILE) {
		resourceGroup.initialCheckListItem(currentResource);
	    } else {
		resourceGroup.initialCheckTreeItem(currentResource);
	    }
	}
    }

    /**
     * Update the tree to only select those elements that match the selected
     * types
     */
    private void setupSelectionsBasedOnSelectedTypes() {

	final Runnable runnable = new Runnable() {
	    @Override
	    public void run() {
		final Map<IContainer, List<IResource>> selectionMap = new Hashtable<IContainer, List<IResource>>();
		// Only get the white selected ones
		final Iterator<?> resourceIterator = resourceGroup
			.getAllWhiteCheckedItems().iterator();
		while (resourceIterator.hasNext()) {
		    // handle the files here - white checked containers require
		    // recursion
		    final IResource resource = (IResource) resourceIterator
			    .next();
		    if (resource.getType() == IResource.FILE) {
			if (hasExportableExtension(resource.getName())) {
			    List<IResource> resourceList = new ArrayList<IResource>();
			    final IContainer parent = resource.getParent();
			    if (selectionMap.containsKey(parent)) {
				resourceList = selectionMap.get(parent);
			    }
			    resourceList.add(resource);
			    selectionMap.put(parent, resourceList);
			}
		    } else {
			setupSelectionsBasedOnSelectedTypes(selectionMap,
				(IContainer) resource);
		    }
		}
		resourceGroup.updateSelections(selectionMap);
	    }
	};

	BusyIndicator.showWhile(getShell().getDisplay(), runnable);

    }

    /**
     * Set up the selection values for the resources and put them in the
     * selectionMap. If a resource is a file see if it matches one of the
     * selected extensions. If not then check the children.
     */
    private void setupSelectionsBasedOnSelectedTypes(
	    final Map<IContainer, List<IResource>> selectionMap,
	    final IContainer parent) {

	final List<IResource> selections = new ArrayList<IResource>();
	IResource[] resources;
	boolean hasFiles = false;

	try {
	    resources = parent.members();
	} catch (final CoreException exception) {
	    // Just return if we can't get any info
	    return;
	}

	for (int i = 0; i < resources.length; i++) {
	    final IResource resource = resources[i];
	    if (resource.getType() == IResource.FILE) {
		if (hasExportableExtension(resource.getName())) {
		    hasFiles = true;
		    selections.add(resource);
		}
	    } else {
		setupSelectionsBasedOnSelectedTypes(selectionMap,
			(IContainer) resource);
	    }
	}

	// Only add it to the list if there are files in this folder
	if (hasFiles) {
	    selectionMap.put(parent, selections);
	}
    }

    /**
     * Save any editors that the user wants to save before export.
     * 
     * @return boolean if the save was successful.
     */
    protected boolean saveDirtyEditors() {
	return IDEWorkbenchPlugin.getDefault().getWorkbench()
		.saveAllEditors(true);
    }

    /**
     * Check if widgets are enabled or disabled by a change in the dialog.
     */
    @Override
    protected void updateWidgetEnablements() {

	final boolean pageComplete = determinePageCompletion();
	setPageComplete(pageComplete);
	if (pageComplete) {
	    setMessage(null);
	}
	super.updateWidgetEnablements();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.dialogs.WizardDataTransferPage#createOptionsGroupButtons
     * (org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createOptionsGroupButtons(final Group parent) {
	// top level group
	final Group options = new Group(parent, SWT.RADIO);
	options.setFont(parent.getFont());

	final GridLayout layout = new GridLayout();
	layout.numColumns = 1;
	layout.makeColumnsEqualWidth = true;
	options.setLayout(layout);
	options.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
		| GridData.HORIZONTAL_ALIGN_FILL));

	// one or many files
	Label label = new Label(options, SWT.LEFT);
	label.setText(IDEWorkbenchMessages.ExportWizardPage_5);

	final Button oneFileButton = new Button(options, SWT.RADIO);
	oneFileButton.setText(net.sourceforge.tsmtest.IDEWorkbenchMessages.ExportWizardPage_0);
	oneFileButton.setSelection(true);
	oneFileButton.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		if (oneFileButton.getSelection()) {
		    setExportType(ExportType.ONE_FILE);
		} else {
		    setExportType(ExportType.MULTIPLE_FILES);
		}
		setErrorMessage(null);
	    }

	    @Override
	    public void widgetDefaultSelected(final SelectionEvent e) {
		if (oneFileButton.getSelection()) {
		    setExportType(ExportType.ONE_FILE);
		} else {
		    setExportType(ExportType.MULTIPLE_FILES);
		}
		setErrorMessage(null);
	    }

	});

	final Button oneFolder = new Button(options, SWT.RADIO);
	oneFolder
		.setText(net.sourceforge.tsmtest.IDEWorkbenchMessages.ExportWizardPage_1);
	oneFolder.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		if (oneFileButton.getSelection()) {
		    setExportType(ExportType.ONE_FILE);
		} else {
		    setExportType(ExportType.MULTIPLE_FILES);
		}
		setErrorMessage(null);
	    }

	    @Override
	    public void widgetDefaultSelected(final SelectionEvent e) {
		if (oneFileButton.getSelection()) {
		    setExportType(ExportType.ONE_FILE);
		} else {
		    setExportType(ExportType.MULTIPLE_FILES);
		}
		setErrorMessage(null);
	    }

	});

	// which files
	final Group exportGroup = new Group(parent, SWT.None);
	exportGroup.setFont(parent.getFont());

	final GridLayout layout2 = new GridLayout();
	layout2.numColumns = 1;
	layout2.makeColumnsEqualWidth = true;
	exportGroup.setLayout(layout2);
	exportGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
		| GridData.HORIZONTAL_ALIGN_FILL));

	Label label2 = new Label(exportGroup, SWT.LEFT);
	label2.setText(IDEWorkbenchMessages.ExportWizardPage_6);

	final Button exportAll = new Button(exportGroup, SWT.RADIO);
	exportAll.setText(IDEWorkbenchMessages.ExportWizardPage_7);
	exportAll.setSelection(true);
	exportAll.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		typeOfExportedFiles = 0;
	    }

	    @Override
	    public void widgetDefaultSelected(final SelectionEvent e) {
		typeOfExportedFiles = 0;
	    }

	});

	final Button exportTestCases = new Button(exportGroup, SWT.RADIO);
	exportTestCases.setText(IDEWorkbenchMessages.ExportWizardPage_8);
	exportTestCases.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		typeOfExportedFiles = 1;
	    }

	    @Override
	    public void widgetDefaultSelected(final SelectionEvent e) {
		typeOfExportedFiles = 1;
	    }

	});

	final Button exportAllProtocols = new Button(exportGroup, SWT.RADIO);
	exportAllProtocols.setText(IDEWorkbenchMessages.ExportWizardPage_9);
	exportAllProtocols.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		typeOfExportedFiles = 2;
	    }

	    @Override
	    public void widgetDefaultSelected(final SelectionEvent e) {
		typeOfExportedFiles = 2;
	    }

	});
	
	final Button exportRevisionProtocols = new Button(exportGroup, SWT.RADIO);
	exportRevisionProtocols.setText(IDEWorkbenchMessages.ExportWizardPage_10);
	exportRevisionProtocols.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		typeOfExportedFiles = 3;
	    }

	    @Override
	    public void widgetDefaultSelected(final SelectionEvent e) {
		typeOfExportedFiles = 3;
	    }

	});
	
	revision = new Text(exportGroup, SWT.BORDER);
    }

    /**
     * @return The ExportType that was selected.
     */
    public ExportType getExportType() {
	return exportType;
    }

    /**
     * Sets the ExportType
     * @param exportType
     */
    private void setExportType(ExportType exportType) {
	this.exportType = exportType;
    }

    /**
     * Getter for the type of exported files.
     * @return 0 = all files, 1 = test cases, 2 = protocols, 3 = revision
     */
    protected int getTypeOfExportFiles() {
	return typeOfExportedFiles;
    }
    
    /**
     * @return The revision as a string.
     */
    public String getRevision(){
	return revision.getText();
    }

}

// sRead more:
// http://kickjava.com/src/org/eclipse/ui/dialogs/WizardExportResourcesPage.java.htm#ixzz2F1KdHBux
