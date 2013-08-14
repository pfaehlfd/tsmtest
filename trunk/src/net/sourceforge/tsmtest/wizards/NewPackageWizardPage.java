/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Leon J. Breedt - Added multiple folder creation support (in WizardNewFolderMainPage)
 *     Bernhard Wetzel - enhancements
 *     Verena Käfer - some fixes
 *     Tobias Hirning - some fixes, i18n
 *     Albert Flaig - some fixes
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;


import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Iterator;

import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ide.undo.CreateFolderOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.misc.ContainerSelectionGroup;
import org.eclipse.ui.internal.ide.misc.ResourceAndContainerGroup;

/**
 * Standard main page for a wizard that creates a folder resource.
 * <p>
 * This page may be used by clients as-is; it may be also be subclassed to suit.
 * </p>
 * <p>
 * Subclasses may extend
 * <ul>
 * </ul>
 * </p>
 * s
 */
@SuppressWarnings("restriction")
public class NewPackageWizardPage extends WizardPage implements Listener {
    private static final int SIZING_CONTAINER_GROUP_HEIGHT = 250;

    private final IStructuredSelection currentSelection;

    private IFolder newFolder;

    // link target location
    // TODO Findbugs says the variable is never written and has always the
    // default value.
    private URI linkTargetPath;

    // problem identifiers

    /**
     * Constant for no problem.
     */
    public static final int PROBLEM_NONE = 0;
    /**
     * Constant for empty resource.
     */
    public static final int PROBLEM_RESOURCE_EMPTY = 1;

    /**
     * Constant for resource already exists.
     */
    public static final int PROBLEM_RESOURCE_EXIST = 2;

    /**
     * Constant for invalid path.
     */
    public static final int PROBLEM_PATH_INVALID = 4;

    /**
     * Constant for empty container.
     */
    public static final int PROBLEM_CONTAINER_EMPTY = 5;

    /**
     * Constant for project does not exist.
     */
    public static final int PROBLEM_PROJECT_DOES_NOT_EXIST = 6;

    /**
     * Constant for invalid name.
     */
    public static final int PROBLEM_NAME_INVALID = 7;

    /**
     * Constant for path already occupied.
     */
    public static final int PROBLEM_PATH_OCCUPIED = 8;

    // the client to notify of changes
    private Listener client;

    // whether to allow existing resources
    private boolean allowExistingResources = false;

    // resource type (file, folder, project)
    private final String resourceType = ""; //$NON-NLS-1$

    // show closed projects in the tree, by default
    private final boolean showClosedProjects = true;

    // problem indicator
    private String problemMessage = "";//$NON-NLS-1$

    private int problemType = PROBLEM_NONE;

    // widgets
    private ContainerSelectionGroup containerGroup;

    private Text resourceNameField;

    /**
     * The resource extension for the resource name field.
     * 
     * @see ResourceAndContainerGroup#setResourceExtension(String)
     * @since 3.3
     */
    private String resourceExtension;

    // constants
    private static final int SIZING_TEXT_FIELD_WIDTH = 250;

    /**
     * Creates a new folder creation wizard page. If the initial resource
     * selection contains exactly one container resource then it will be used as
     * the default container resource.
     * 
     * @param pageName
     *            the name of the page
     * @param selection
     *            the current resource selection
     */
    public NewPackageWizardPage(final String pageName,
	    final IStructuredSelection selection) {
	super("newFolderPage1");//$NON-NLS-1$
	setTitle(pageName);
	setDescription(net.sourceforge.tsmtest.IDEWorkbenchMessages.NewPackageWizardPage_0);
	currentSelection = selection;
    }

    /**
     * (non-Javadoc) Method declared on IDialogPage.
     */
    @Override
    public void createControl(final Composite parent) {
	initializeDialogUnits(parent);
	// top level group
	final Composite composite = new Composite(parent, SWT.NONE);
	composite.setFont(parent.getFont());
	composite.setLayout(new GridLayout());
	composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
		| GridData.HORIZONTAL_ALIGN_FILL));

	// TODO some kind of help?
	// PlatformUI.getWorkbench().getHelpSystem()
	// .setHelp(composite, IIDEHelpContextIds.NEW_FOLDER_WIZARD_PAGE);
	createContents(composite,
		net.sourceforge.tsmtest.IDEWorkbenchMessages.NewPackageWizardPage_1,
		SIZING_CONTAINER_GROUP_HEIGHT);

	setAllowExistingResources(false);

	initializePage();
	validatePage();
	// Show description on opening
	setErrorMessage(null);
	setMessage(null);
	setControl(composite);
    }

    /**
     * Creates a folder resource handle for the folder with the given workspace
     * path. This method does not create the folder resource; this is the
     * responsibility of <code>createFolder</code>.
     * 
     * @param folderPath
     *            the path of the folder resource to create a handle for
     * @return the new folder resource handle
     * @see #createFolder
     */
    private IFolder createFolderHandle(final IPath folderPath) {
	return IDEWorkbenchPlugin.getPluginWorkspace().getRoot()
		.getFolder(folderPath);
    }

    public IPath getFolderPath() {
	return getContainerFullPath().append(getResource());
    }

    /**
     * Creates a new folder resource in the selected container and with the
     * selected name. Creates any missing resource containers along the path;
     * does nothing if the container resources already exist.
     * <p>
     * In normal usage, this method is invoked after the user has pressed Finish
     * on the wizard; the enablement of the Finish button implies that all
     * controls on this page currently contain valid values.
     * </p>
     * <p>
     * Note that this page caches the new folder once it has been successfully
     * created; subsequent invocations of this method will answer the same
     * folder resource without attempting to create it again.
     * </p>
     * <p>
     * This method should be called within a workspace modify operation since it
     * creates resources.
     * </p>
     * 
     * @return the created folder resource, or <code>null</code> if the folder
     *         was not created
     */
    public IFolder createNewFolder() {
	if (newFolder != null) {
	    return newFolder;
	}

	// create the new folder and cache it if successful
	final IPath containerPath = getContainerFullPath();
	final IPath newFolderPath = containerPath.append(getResource());
	final IFolder newFolderHandle = createFolderHandle(newFolderPath);

	final IRunnableWithProgress op = new IRunnableWithProgress() {
	    @Override
	    public void run(final IProgressMonitor monitor) {
		AbstractOperation op;
		op = new CreateFolderOperation(
			newFolderHandle,
			linkTargetPath,
			false,
			null,
			net.sourceforge.tsmtest.IDEWorkbenchMessages.NewPackageWizardPage_2);
		try {
		    // see bug
		    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
		    // directly execute the operation so that the undo state is
		    // not preserved. Making this undoable can result in
		    // accidental
		    // folder (and file) deletions.
		    op.execute(monitor,
			    WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
		} catch (final ExecutionException e) {
		    getContainer().getShell().getDisplay()
			    .syncExec(new Runnable() {
				@Override
				public void run() {
				    if (e.getCause() instanceof CoreException) {
					ErrorDialog
						.openError(
							getContainer()
								.getShell(), // Was
									     // Utilities.getFocusShell()
							IDEWorkbenchMessages.WizardNewFolderCreationPage_errorTitle,
							null, // no special
							      // message
							((CoreException) e
								.getCause())
								.getStatus());
				    } else {
					IDEWorkbenchPlugin
						.log(getClass(),
							"createNewFolder()", e.getCause()); //$NON-NLS-1$
					MessageDialog
						.openError(
							getContainer()
								.getShell(),
							IDEWorkbenchMessages.WizardNewFolderCreationPage_internalErrorTitle,
							NLS.bind(
								IDEWorkbenchMessages.WizardNewFolder_internalError,
								e.getCause()
									.getMessage()));
				    }
				}
			    });
		}
	    }
	};

	try {
	    getContainer().run(true, true, op);
	} catch (final InterruptedException e) {
	    return null;
	} catch (final InvocationTargetException e) {
	    // ExecutionExceptions are handled above, but unexpected runtime
	    // exceptions and errors may still occur.
	    IDEWorkbenchPlugin.log(getClass(),
		    "createNewFolder()", e.getTargetException()); //$NON-NLS-1$
	    MessageDialog
		    .open(MessageDialog.ERROR,
			    getContainer().getShell(),
			    IDEWorkbenchMessages.WizardNewFolderCreationPage_internalErrorTitle,
			    NLS.bind(
				    IDEWorkbenchMessages.WizardNewFolder_internalError,
				    e.getTargetException().getMessage()),
			    SWT.SHEET);
	    return null;
	}

	newFolder = newFolderHandle;

	return newFolder;
    }

    /**
     * The <code>WizardNewFolderCreationPage</code> implementation of this
     * <code>Listener</code> method handles all events and enablements for
     * controls on this page. Subclasses may extend.
     */
    @Override
    public void handleEvent(final Event ev) {
	validateControls();
	// TODO Findbugs says the variable is never written and has always the
	// default value.
	if (client != null) {
	    client.handleEvent(ev);
	}
	setPageComplete(validatePage());
    }

    /**
     * Initializes this page's controls.
     */
    private void initializePage() {
	final Iterator<?> it = currentSelection.iterator();
	if (it.hasNext()) {
	    final Object next = it.next();
	    IResource selectedResource = null;
	    if (next instanceof IResource) {
		selectedResource = (IResource) next;
	    } else if (next instanceof IAdaptable) {
		selectedResource = (IResource) ((IAdaptable) next)
			.getAdapter(IResource.class);
	    }
	    if (selectedResource != null) {
		if (selectedResource.getType() == IResource.FILE) {
		    selectedResource = selectedResource.getParent();
		}
		if (selectedResource.isAccessible()) {
		    setContainerFullPath(selectedResource.getFullPath());
		}
	    }
	}

	setPageComplete(false);
    }

    /*
     * @see DialogPage.setVisible(boolean)
     */
    @Override
    public void setVisible(final boolean visible) {
	super.setVisible(visible);
	if (visible) {
	    setFocus();
	}
    }

    /**
     * Returns whether this page's controls currently all contain valid values.
     * 
     * @return <code>true</code> if all controls are valid, and
     *         <code>false</code> if at least one is invalid
     */
    private boolean validatePage() {

	if (!areAllValuesValid()) {
	    // if blank name then fail silently
	    if (getProblemType() == ResourceAndContainerGroup.PROBLEM_RESOURCE_EMPTY
		    || getProblemType() == ResourceAndContainerGroup.PROBLEM_CONTAINER_EMPTY) {
		setMessage(getProblemMessage());
		setErrorMessage(null);
	    } else {
		setErrorMessage(getProblemMessage());
	    }
	    return false;
	}

	if (getResource().matches(".*[<>?|\".:_*].*") //$NON-NLS-1$
		|| getResource().matches(".*\\\\.*")) { //$NON-NLS-1$
	    setErrorMessage(DataModelException.NAME_CONTAINS_WRONG_CHARACTER);
	    return false;
	}
	setErrorMessage(null);
	setMessage(null);
	return true;
    }

    /**
     * Returns a boolean indicating whether all controls in this group contain
     * valid values.
     * 
     * @return boolean
     */
    public boolean areAllValuesValid() {
	return problemType == PROBLEM_NONE;
    }

    /**
     * Creates this object's visual components.
     * 
     * @param parent
     *            org.eclipse.swt.widgets.Composite
     * @param heightHint
     *            height hint for the container selection widget group
     */
    protected void createContents(final Composite parent,
	    final String resourceLabelString, final int heightHint) {

	final Font font = parent.getFont();
	// server name group
	final Composite composite = new Composite(parent, SWT.NONE);
	GridLayout layout = new GridLayout();
	layout.marginWidth = 0;
	layout.marginHeight = 0;
	composite.setLayout(layout);
	composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	composite.setFont(font);

	// container group
	if (heightHint == SWT.DEFAULT) {
	    containerGroup = new ContainerSelectionGroup(
		    composite,
		    this,
		    true,
		    net.sourceforge.tsmtest.IDEWorkbenchMessages.NewPackageWizardPage_5,
		    showClosedProjects);
	} else {
	    containerGroup = new ContainerSelectionGroup(
		    composite,
		    this,
		    true,
		    net.sourceforge.tsmtest.IDEWorkbenchMessages.NewPackageWizardPage_6,
		    showClosedProjects, heightHint, SIZING_TEXT_FIELD_WIDTH);
	}

	// resource name group
	final Composite nameGroup = new Composite(composite, SWT.NONE);
	layout = new GridLayout();
	layout.numColumns = 2;
	layout.marginWidth = 0;
	nameGroup.setLayout(layout);
	nameGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
		| GridData.GRAB_HORIZONTAL));
	nameGroup.setFont(font);

	final Label lblPackageName = new Label(nameGroup, SWT.NONE);
	lblPackageName
		.setText(net.sourceforge.tsmtest.IDEWorkbenchMessages.NewPackageWizardPage_7);
	lblPackageName.setFont(font);

	// resource name entry field
	resourceNameField = new Text(nameGroup, SWT.BORDER);
	resourceNameField.addListener(SWT.Modify, this);
	resourceNameField.setTextLimit(200);
	resourceNameField.addFocusListener(new FocusAdapter() {
	    @Override
	    public void focusLost(final FocusEvent e) {
		handleResourceNameFocusLostEvent();
	    }
	});
	final GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
		| GridData.GRAB_HORIZONTAL);
	data.widthHint = SIZING_TEXT_FIELD_WIDTH;
	resourceNameField.setLayoutData(data);
	resourceNameField.setFont(font);
	validateControls();
    }

    /**
     * Returns the path of the currently selected container or null if no
     * container has been selected. Note that the container may not exist yet if
     * the user entered a new container name in the field.
     * 
     * @return The path of the container, or <code>null</code>
     */
    public IPath getContainerFullPath() {
	return containerGroup.getContainerFullPath();
    }

    /**
     * Returns an error message indicating the current problem with the value of
     * a control in the group, or an empty message if all controls in the group
     * contain valid values.
     * 
     * @return java.lang.String
     */
    public String getProblemMessage() {
	return problemMessage;
    }

    /**
     * Returns the type of problem with the value of a control in the group.
     * 
     * @return one of the PROBLEM_* constants
     */
    public int getProblemType() {
	return problemType;
    }

    /**
     * Returns a string that is the name of the chosen resource, or an empty
     * string if no resource has been entered. <br>
     * <br>
     * The name will include the resource extension if the preconditions are
     * met.
     * 
     * @see ResourceAndContainerGroup#setResourceExtension(String)
     * 
     * @return The resource name
     * @since 3.3
     */
    public String getResource() {
	final String resource = resourceNameField.getText();
	if (useResourceExtension()) {
	    return resource + '.' + resourceExtension;
	}
	return resource;
    }

    /**
     * Returns the resource extension.
     * 
     * @return The resource extension or <code>null</code>.
     * @see ResourceAndContainerGroup#setResourceExtension(String)
     * @since 3.3
     */
    public String getResourceExtension() {
	return resourceExtension;
    }

    /**
     * Determines whether the resource extension should be added to the resource
     * name field. <br>
     * <br>
     * 
     * @see ResourceAndContainerGroup#setResourceExtension(String)
     * @return <code>true</code> if the preconditions are met; otherwise,
     *         <code>false</code>.
     * @since 3.3
     */
    private boolean useResourceExtension() {
	final String resource = resourceNameField.getText();
	if ((resourceExtension != null) && (resourceExtension.length() > 0)
		&& (resource.length() > 0)
		&& (resource.endsWith('.' + resourceExtension) == false)) {
	    return true;
	}
	return false;
    }

    /**
     * Handle the focus lost event from the resource name field. <br>
     * Adds the resource extension to the resource name field when it loses
     * focus (if the preconditions are met).
     * 
     * @see ResourceAndContainerGroup#setResourceExtension(String)
     * @since 3.3
     */
    private void handleResourceNameFocusLostEvent() {
	if (useResourceExtension()) {
	    setResource(resourceNameField.getText() + '.' + resourceExtension);
	}
    }

    /**
     * Sets the flag indicating whether existing resources are permitted.
     * 
     * @param value
     */
    public void setAllowExistingResources(final boolean value) {
	allowExistingResources = value;
    }

    /**
     * Sets the value of this page's container.
     * 
     * @param path
     *            Full path to the container.
     */
    public void setContainerFullPath(final IPath path) {
	IResource initial = ResourcesPlugin.getWorkspace().getRoot()
		.findMember(path);
	if (initial != null) {
	    if (!(initial instanceof IContainer)) {
		initial = initial.getParent();
	    }
	    containerGroup.setSelectedContainer((IContainer) initial);
	}
	validateControls();
    }

    /**
     * Gives focus to the resource name field and selects its contents
     */
    public void setFocus() {
	// select the whole resource name.
	resourceNameField.setSelection(0, resourceNameField.getText().length());
	resourceNameField.setFocus();
    }

    /**
     * Sets the value of this page's resource name.
     * 
     * @param value
     *            new value
     */
    public void setResource(final String value) {
	resourceNameField.setText(value);
	validateControls();
    }

    /**
     * Set the only file extension allowed for the resource name field. <br>
     * <br>
     * If a resource extension is specified, then it will always be appended
     * with a '.' to the text from the resource name field for validation when
     * the following conditions are met: <br>
     * <br>
     * (1) TSMResource extension length is greater than 0 <br>
     * (2) TSMResource name field text length is greater than 0 <br>
     * (3) TSMResource name field text does not already end with a '.' and the
     * resource extension specified (case sensitive) <br>
     * <br>
     * The resource extension will not be reflected in the actual resource name
     * field until the resource name field loses focus.
     * 
     * @param value
     *            The resource extension without the '.' prefix (e.g. 'java',
     *            'xml')
     * @since 3.3
     */
    public void setResourceExtension(final String value) {
	resourceExtension = value;
	validateControls();
    }

    /**
     * Returns a <code>boolean</code> indicating whether a container name
     * represents a valid container resource in the workbench. An error message
     * is stored for future reference if the name does not represent a valid
     * container.
     * 
     * @return <code>boolean</code> indicating validity of the container name
     */
    protected boolean validateContainer() {
	IPath path = containerGroup.getContainerFullPath();
	if (path == null) {
	    problemType = PROBLEM_CONTAINER_EMPTY;
	    problemMessage = IDEWorkbenchMessages.ResourceGroup_folderEmpty;
	    return false;
	}

	// TSM: Check for the images-folder
	if (path.segment(1) != null
		&& path.segment(1).equals(DataModelTypes.imageFolderName)) {
	    problemType = PROBLEM_PATH_INVALID;
	    problemMessage = net.sourceforge.tsmtest.IDEWorkbenchMessages.ContainerSelectionDialog_1;
	    return false;
	}

	final IWorkspace workspace = ResourcesPlugin.getWorkspace();
	final String projectName = path.segment(0);
	if (projectName == null
		|| !workspace.getRoot().getProject(projectName).exists()) {
	    problemType = PROBLEM_PROJECT_DOES_NOT_EXIST;
	    problemMessage = IDEWorkbenchMessages.ResourceGroup_noProject;
	    return false;
	}
	// path is invalid if any prefix is occupied by a file
	final IWorkspaceRoot root = workspace.getRoot();
	while (path.segmentCount() > 1) {
	    if (root.getFile(path).exists()) {
		problemType = PROBLEM_PATH_OCCUPIED;
		problemMessage = NLS.bind(
			IDEWorkbenchMessages.ResourceGroup_pathOccupied,
			path.makeRelative());
		return false;
	    }
	    path = path.removeLastSegments(1);
	}
	return true;
    }

    /**
     * Validates the values for each of the group's controls. If an invalid
     * value is found then a descriptive error message is stored for later
     * reference. Returns a boolean indicating the validity of all of the
     * controls in the group.
     */
    protected boolean validateControls() {
	// don't attempt to validate controls until they have been created
	if (containerGroup == null) {
	    return false;
	}
	problemType = PROBLEM_NONE;
	problemMessage = "";//$NON-NLS-1$

	if (!validateContainer() || !validateResourceName()) {
	    return false;
	}

	final IPath path = containerGroup.getContainerFullPath().append(
		getResource());
	return validateFullResourcePath(path);
    }

    /**
     * Returns a <code>boolean</code> indicating whether the specified resource
     * path represents a valid new resource in the workbench. An error message
     * is stored for future reference if the path does not represent a valid new
     * resource path.
     * 
     * @param resourcePath
     *            the path to validate
     * @return <code>boolean</code> indicating validity of the resource path
     */
    protected boolean validateFullResourcePath(final IPath resourcePath) {
	final IWorkspace workspace = ResourcesPlugin.getWorkspace();

	final IStatus result = workspace.validatePath(resourcePath.toString(),
		IResource.FOLDER);
	if (!result.isOK()) {
	    problemType = PROBLEM_PATH_INVALID;
	    problemMessage = result.getMessage();
	    return false;
	}

	if (!allowExistingResources
		&& (workspace.getRoot().getFolder(resourcePath).exists() || workspace
			.getRoot().getFile(resourcePath).exists())) {
	    problemType = PROBLEM_RESOURCE_EXIST;
	    problemMessage = NLS.bind(
		    IDEWorkbenchMessages.ResourceGroup_nameExists,
		    getResource());
	    return false;
	}
	return true;
    }

    /**
     * Returns a <code>boolean</code> indicating whether the resource name rep-
     * resents a valid resource name in the workbench. An error message is
     * stored for future reference if the name does not represent a valid
     * resource name.
     * 
     * @return <code>boolean</code> indicating validity of the resource name
     */
    protected boolean validateResourceName() {
	final String resourceName = getResource();

	if (resourceName.length() == 0) {
	    problemType = PROBLEM_RESOURCE_EMPTY;
	    problemMessage = NLS.bind(
		    IDEWorkbenchMessages.ResourceGroup_emptyName, resourceType);
	    return false;
	}

	if (!Path.ROOT.isValidPath(resourceName)) {
	    problemType = PROBLEM_NAME_INVALID;
	    problemMessage = NLS.bind(
		    IDEWorkbenchMessages.ResourceGroup_invalidFilename,
		    resourceName);
	    return false;
	}
	return true;
    }

    /**
     * Returns the flag indicating whether existing resources are permitted.
     * 
     * @return The allow existing resources flag.
     * @see ResourceAndContainerGroup#setAllowExistingResources(boolean)
     * @since 3.4
     */
    public boolean getAllowExistingResources() {
	return allowExistingResources;
    }

}
