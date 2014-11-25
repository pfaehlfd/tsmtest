/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Verena Käfer - various fixes
 * 	Tobias Hirning - some fixes, i18n
 * 	Wolfgang Kraus - some fixes and data model refactoring
 * 	Bernhard Wetzel - added comments
 * 	Jenny Krüwald - changed use from List to Set
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.TSMImageContentFilter;
import net.sourceforge.tsmtest.datamodel.descriptors.ITestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestCaseDescriptor;
import net.sourceforge.tsmtest.io.vcs.settings.VCSSettings;
import net.sourceforge.tsmtest.io.vcs.svn.SubversionWrapper;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.internal.Workbench;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Loads all resources in the workspace when needed and holds them in its
 * singleton.
 * 
 * @author Albert Flaig
 */
@SuppressWarnings("restriction")
public final class DataModel extends AbstractDataModel implements
	IResourceChangeListener {
    /**
     * used for console logging
     */
    private static final Logger log = Logger.getLogger(DataModel.class);

    private static volatile DataModel instance;
    private final HashMap<IProject, TSMProject> projects = new HashMap<IProject, TSMProject>();
    private final HashMap<IFolder, TSMPackage> packages = new HashMap<IFolder, TSMPackage>();
    private final HashMap<IFile, TSMTestCase> testCases = new HashMap<IFile, TSMTestCase>();
    private final HashMap<Long, TSMTestCase> idToTestCase = new HashMap<Long, TSMTestCase>();
    private final HashMap<IFile, TSMReport> reports = new HashMap<IFile, TSMReport>();
    private final HashMap<Long, HashSet<TSMReport>> testCaseIdToReports = new HashMap<Long, HashSet<TSMReport>>();
    /**
     * The single purpose of this attribute is to keep track of reports when
     * they are moved/renamed. {@link TSMReport.find()}
     */
    private final HashMap<ITestCaseDescriptor, TSMReport> dataToReport = new HashMap<ITestCaseDescriptor, TSMReport>();
    /**
     * The array of observables to notify when a data model change occurs.
     */
    private final List<DataModelObservable> observables = Collections
	    .synchronizedList(new ArrayList<DataModelObservable>());

    private boolean resourcesLoaded = false;

    /**
     * creating new datamodel if none exists
     * 
     * @return instance of datamodel
     */
    public static AbstractDataModel getInstance() {
	if (instance == null) {
	    instance = new DataModel();
	}
	return instance;
    }

    /**
     * Refreshing Workspace on start and adding change listener
     */
    private DataModel() {
	// lookup all resources upon creation
	try {
	    reloadResources();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	// Afterwards the resource change listener should keep track of
	// resource changes
	ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
		IResourceChangeEvent.POST_CHANGE);
    }

    /**
     * clearing resources and getting all objects recursively
     */
    private void reloadResources() {
	projects.clear();
	packages.clear();
	testCases.clear();
	idToTestCase.clear();
	reports.clear();
	testCaseIdToReports.clear();
	dataToReport.clear();
	// Iterate through all projects, add them to the data model and get all
	// members of each container in the project recursively
	final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
		.getProjects();
	for (final IProject project : projects) {
	    if (project.isOpen()) {
		try {
		    if (project.hasNature(DataModelTypes.TSM_NATURE)) {
			if (VCSSettings.isSubversionSupportEnabled()) {
			    project.getLocation().toString();
			    SubversionWrapper.update(project.getLocation().toString());
			}
			final TSMProject tsmProject = new TSMProject(
				stripExtension(project.getName()));
			put(project, tsmProject);
			addResourcesRecursive(project, tsmProject);
		    }
		} catch (final CoreException e) {
		    // Dont do anything
		}
	    }
	}
	resourcesLoaded = true;
	log.debug(Messages.DataModel_0);
    }

    /**
     * adding listener to observer
     * 
     * @param listener
     */
    @Override
    public void register(final DataModelObservable listener) {
	observables.add(listener);
    }

    /**
     * removing listener to observer
     * 
     * @param listener
     */
    @Override
    public void unregister(final DataModelObservable listener) {
	observables.remove(listener);
    }

    /**
     * returns the packages matching the container checking for projects if no
     * packages were found
     * 
     * @return packages, projects
     */
    private TSMContainer findContainer(final IContainer container) {
	TSMContainer cont = packages.get(container);
	if (cont == null) {
	    cont = projects.get(container);
	}
	return cont;
    }

    @Override
    protected Collection<TSMProject> getProjects() {
	return projects.values();
    }

    /**
     * Returns all values of the package-map
     * 
     * @return packages
     */
    @Override
    protected Collection<TSMPackage> getPackages() {
	return packages.values();
    }

    /**
     * Returns all values of the testCases-map
     * 
     * @return test cases
     */
    @Override
    protected Collection<TSMTestCase> getTestCases() {
	return testCases.values();
    }

    /**
     * Returns all values of the idToTestCase-map
     * 
     * @return test case
     */
    @Override
    protected TSMTestCase getTestCaseById(final long id) {
	return idToTestCase.get(id);
    }

    /**
     * Returns all values of the reports-map
     * 
     * @return reports
     */
    @Override
    protected Collection<TSMReport> getReports() {
	return reports.values();
    }

    /**
    /* (non-Javadoc)
     * @see net.sourceforge.tsmtest.datamodel.AbstractDataModel#getReportOfTestCase(long)
     */
    @Override
    protected Collection<TSMReport> getReportOfTestCase(final long id) {
	final Collection<TSMReport> reports = testCaseIdToReports.get(id);
	if (reports == null) {
	    return new HashSet<TSMReport>();
	} else {
	    return reports;
	}
    }

    /**
     * getting the corresponding IPorject from the given project.
     * 
     * @param project
     * @return the IProject
     */
    private IProject getResource(final TSMProject project) {
	if (!resourcesLoaded) {
	    reloadResources();
	}
	for (final IProject res : projects.keySet()) {
	    if (projects.get(res).equals(project)) {
		return res;
	    }
	}
	return null;
    }

    /**
     * getting the corresponding IFile from the given test case
     * 
     * @param testCase
     * @return IFile
     */
    private IFile getResource(final TSMTestCase testCase) {
	if (!resourcesLoaded) {
	    reloadResources();
	}
	for (final IFile res : testCases.keySet()) {
	    if (testCases.get(res).equals(testCase)) {
		return res;
	    }
	}
	return null;
    }

    /**
     * getting the corresponding IFile from the given report
     * 
     * @param testCase
     * @return IFile
     */
    private IFile getResource(final TSMReport report) {
	if (!resourcesLoaded) {
	    reloadResources();
	}
	for (final IFile res : reports.keySet()) {
	    if (reports.get(res).equals(report)) {
		return res;
	    }
	}
	return null;
    }

    /**
     * getting the corresponding Folder from the given tsm package
     * 
     * @param testCase
     * @return IFolder
     */
    private IFolder getResource(final TSMPackage tsmPackage) {
	if (!resourcesLoaded) {
	    reloadResources();
	}
	for (final IFolder res : packages.keySet()) {
	    if (packages.get(res).equals(tsmPackage)) {
		return res;
	    }
	}
	return null;
    }

    private IResource getResource(final TSMResource resource) {
	if (!resourcesLoaded) {
	    reloadResources();
	}
	if (resource instanceof TSMContainer) {
	    return getResource((TSMContainer) resource);
	} else if (resource instanceof TSMTestCase) {
	    return getResource((TSMTestCase) resource);
	} else if (resource instanceof TSMReport) {
	    return getResource((TSMReport) resource);
	}
	return null;
    }

    /**
     * getting the corresponding IContainer from the given container
     * 
     * @param testCase
     * @return IContainer
     */
    private IContainer getResource(final TSMContainer container) {
	if (!resourcesLoaded) {
	    reloadResources();
	}
	if (container instanceof TSMProject) {
	    return getResource((TSMProject) container);
	} else if (container instanceof TSMPackage) {
	    return getResource((TSMPackage) container);
	}
	return null;
    }

    /**
     * checking if the given resource is in the project-, package-, test cases-
     * or reportlist
     * 
     * @param resource
     * @return boolean
     */
    @Override
    protected boolean exists(final TSMResource resource) {
	return getProjects().contains(resource)
		|| getPackages().contains(resource)
		|| getTestCases().contains(resource)
		|| getReports().contains(resource);
    }

    /**
     * adding a new project to the projects-map
     * 
     * @param project
     * @param tsmProject
     */
    private void put(final IProject project, final TSMProject tsmProject) {
	projects.put(project, tsmProject);
    }

    /**
     * adding a new package to the package-map
     * 
     * @param folder
     * @param tsmPackage
     */
    private void put(final IFolder folder, final TSMPackage tsmPackage) {
	packages.put(folder, tsmPackage);
    }

    /**
     * adding a new test case to the testCases- and the idToTestCase-map
     * 
     * @param file
     * @param testCase
     */
    private void put(final IFile file, final TSMTestCase testCase) {
	testCases.put(file, testCase);
	idToTestCase.put(testCase.getData().getId(), testCase);
    }

    /**
     * adding a new report to the reports- and the testCaseIdToReports-map
     * adding new HashSet to the testCaseIdToReports map if the id of the report
     * doesnt exist.
     * 
     * @param file
     * @param report
     */
    private void put(final IFile file, final TSMReport report) {
	reports.put(file, report);
	dataToReport.put(report.getData(), report);
	final long id = report.getData().getId();
	if (testCaseIdToReports.get(id) == null) {
	    testCaseIdToReports.put(id, new HashSet<TSMReport>());
	}
	testCaseIdToReports.get(id).add(report);
    }

    /**
     * Adding the tsmResource to the correct map by forwarding to the right
     * funtion
     * 
     * @param resource
     * @param tsmResource
     */
    private void put(final IResource resource, final TSMResource tsmResource) {
	if (tsmResource instanceof TSMProject) {
	    put((IProject) resource, (TSMProject) tsmResource);
	} else if (tsmResource instanceof TSMPackage) {
	    put((IFolder) resource, (TSMPackage) tsmResource);
	} else if (tsmResource instanceof TSMTestCase) {
	    put((IFile) resource, (TSMTestCase) tsmResource);
	} else if (tsmResource instanceof TSMReport) {
	    put((IFile) resource, (TSMReport) tsmResource);
	}
    }

    /**
     * Removes the resource from the data model. Checks the type of the resource
     * whether it is a project, a package, a test case or a test case protocol
     * and removes it from the list depending on the type.
     * 
     * @param resource
     *            to remove from the data model.
     */
    private void removeResource(final IResource resource) {
	if (resource instanceof IProject) {
	    final IProject project = (IProject) resource;
	    removeProject(project);
	} else if (resource instanceof IFolder) {
	    final IFolder folder = (IFolder) resource;
	    removePackage(folder);
	} else if (resource instanceof IFile) {
	    final IFile file = (IFile) resource;
	    if (getContentTypeId(file).equals(
		    DataModelTypes.CONTENT_TYPE_ID_TESTCASE)) {
		removeTestCase(file);
	    } else if (getContentTypeId(file).equals(
		    DataModelTypes.CONTENT_TYPE_ID_PROTOCOL)) {
		removeTestCaseReport(file);
	    }
	}
    }

    /**
     * deletes a project from the projects map
     */
    private void removeProject(final IProject project) {
	projects.remove(project);
    }

    /**
     * deletes a package from the packages map
     */
    private void removePackage(final IFolder folder) {
	packages.remove(folder);
    }

    /**
     * deletes a test case from the testCases map
     */
    private void removeTestCase(final IFile testCase) {
	final TSMTestCase tsmTestCase = testCases.remove(testCase);
	idToTestCase.remove(tsmTestCase.getData().getId());
    }

    /**
     * deletes a report from the reports map
     */
    private void removeTestCaseReport(final IFile testCaseProtocol) {
	final TSMReport report = reports.remove(testCaseProtocol);
	testCaseIdToReports.get(report.getData().getId()).remove(report);
    }

    /**
     * Recursively enters all {@link IContainer} in contained and returns all
     * {@link IResource} residing in each container.
     * 
     * @param container
     *            to search recursively for files in
     * @param tsmContainer
     */
    private void addResourcesRecursive(final IContainer container,
	    final TSMContainer tsmContainer) {
	try {
	    for (final IResource resource : container.members()) {
		if (!new TSMImageContentFilter().select(null, null, resource)) {
		    continue;
		}
		if (resource instanceof IFolder) {
		    final IFolder subFolder = (IFolder) resource;
		    final TSMContainer tsmSubPackage = addFolder(subFolder,
			    tsmContainer);
		    addResourcesRecursive(subFolder, tsmSubPackage);
		} else if (resource instanceof IFile) {
		    final IFile file = (IFile) resource;
		    addFile(file, tsmContainer);
		}
	    }
	} catch (final CoreException e) {
	    // TODO error message
	    e.printStackTrace();
	} catch (final JDOMException e) {
	    // TODO error message
	    e.printStackTrace();
	} catch (final IOException e) {
	    // TODO error message
	    e.printStackTrace();
	}
    }

    /**
     * Adds the resource to the data model. Checks the type of the resource
     * whether it is a project, a package, a test case or a test case Report and
     * adds it to the list depending on the type.
     * 
     * @param resource
     *            to add to the data model.
     */
    private TSMContainer addFolder(final IFolder folder,
	    final TSMContainer parent) {
	final TSMPackage tsmPackage = new TSMPackage(
		stripExtension(folder.getName()), parent);
	put(folder, tsmPackage);
	if (VCSSettings.isSubversionSupportEnabled()) {
	    SubversionWrapper.addForCommit(folder.getLocation().toString());
	    SubversionWrapper.commit(folder.getLocation().toString());
	}
	return tsmPackage;
    }

    /**
     * Converts the given File to test case / Report returning it
     * 
     * @param file
     * @param parent
     * @return test case / Report
     * @throws JDOMException
     * @throws IOException
     */
    private TSMResource addFile(final IFile file, final TSMContainer parent)
	    throws JDOMException, IOException {
	// checking if test case or Report
	if (getContentTypeId(file).equals(
		DataModelTypes.CONTENT_TYPE_ID_TESTCASE)) {

	    Reader reader;
	    try {
		final InputStream is = file.getContents();
		reader = new InputStreamReader(is, "UTF-8"); //$NON-NLS-1$
	    } catch (final CoreException e) {
		log.error(e.getStackTrace());
		return null;
	    }
	    final TestCaseDescriptor data = Load.loadTestCase(new SAXBuilder()
		    .build(reader));
	    reader.close();
	    final TSMTestCase testCase = new TSMTestCase(
		    stripExtension(file.getName()), parent, data);
	    // adding test case to the test case map
	    put(file, testCase);
	    return testCase;
	} else if (getContentTypeId(file).equals(
		DataModelTypes.CONTENT_TYPE_ID_PROTOCOL)) {
	    Reader reader;
	    try {
		final InputStream is = file.getContents();
		reader = new InputStreamReader(is, "UTF-8"); //$NON-NLS-1$
	    } catch (final CoreException e) {
		log.error(e.getStackTrace());
		return null;
	    }

	    final TestCaseDescriptor data = Load.loadProtocol(new SAXBuilder()
		    .build(reader));
	    reader.close();
	    final TSMReport testCaseProtocol = new TSMReport(
		    stripExtension(file.getName()), parent, data);
	    // adding Report to the Report map
	    put(file, testCaseProtocol);
	    return testCaseProtocol;
	}
	return null;
    }

    /**
     * fail-safe content type id request.
     * 
     * @param file
     * @return the content type id of the file or an empty String.
     */
    private String getContentTypeId(final IFile file) {
	try {
	    if (file.getContentDescription() != null
		    && file.getContentDescription().getContentType() != null) {
		return file.getContentDescription().getContentType().getId();
	    }
	} catch (final CoreException e) {
	}
	return ""; //$NON-NLS-1$
    }

    /**
     * Reloading the data model if the data got changed
     * 
     * @param event
     *            the change of the data
     */
    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
	if (event.getDelta() == null) {
	    return;
	}

	recursiveResourceDelta(event.getDelta(), new IResourceDeltaVisitor() {
	    @Override
	    public boolean visit(final IResourceDelta delta)
		    throws CoreException {
		if (!(delta.getResource() instanceof IFile)) {
		    return true;
		}
		final IFile file = (IFile) delta.getResource();
		// only when a new file is being created
		if (delta.getKind() != IResourceDelta.ADDED) {
		    return false;
		}
		// and it is not moved from somewhere
		if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
		    return false;
		}
		// only when test case
		if (!getContentTypeId(file).equals(
			DataModelTypes.CONTENT_TYPE_ID_TESTCASE)) {
		    return false;
		}
		// read this test case
		Reader reader;
		final TestCaseDescriptor data;
		try {
		    final InputStream is = file.getContents();
		    reader = new InputStreamReader(is, "UTF-8"); //$NON-NLS-1$
		    data = Load.loadTestCase(new SAXBuilder().build(reader));
		    reader.close();
		} catch (final JDOMException e1) {
		    log.error(e1.getStackTrace());
		    return false;
		} catch (final IOException e1) {
		    log.error(e1.getStackTrace());
		    return false;
		}
		// check if id already exists
		final TSMTestCase existingTestCase = idToTestCase.get(data
			.getId());
		if (existingTestCase != null) {
		    // yes -> the test case has probably been copied
		    if (data.equals(existingTestCase.getData())) {
			// it has been copied definitely -> create new id
			data.setId(TSMTestCase.generateID(Calendar
				.getInstance().getTime()));
			// and now the file has to be rewritten in another
			// thread
			// because the workspace is locked a this point.
			new Thread(new Runnable() {
			    @Override
			    public void run() {
				try {
				    final InputStream source = Save
					    .saveTestCase(data, file
						    .getProject().getName());
				    file.setContents(source, true, true, null);
				} catch (final CoreException e) {
				    log.error(Messages.DataModel_5
					    + e.getMessage());
				} catch (final DataModelException e) {
				    log.error(e.getStackTrace());
				}
			    }
			}).start();
		    }
		}
		return true;
	    }
	});

	// FIXME More efficient way of reloading the data model
	reloadResources();

	for (final Iterator<?> iterator = observables.iterator(); iterator.hasNext();) {
	    final DataModelObservable observable = (DataModelObservable) iterator
		    .next();
	    observable.dataModelChanged();
	}
    }

    /**
     * Visits the delta tree recursive and calls the visitor.visit() each time
     * the kind of the delta equals kind.
     */
    private void recursiveResourceDelta(final IResourceDelta delta,
	    final IResourceDeltaVisitor visitor) {
	try {
	    if (visitor.visit(delta)) {
		for (final IResourceDelta child : delta.getAffectedChildren()) {
		    recursiveResourceDelta(child, visitor);
		}
	    }
	} catch (final CoreException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Returns the corresponding TSMProject to the given IProject p
     * 
     * @param p
     *            IPproject to be loaded
     * @return TSMProject of the map
     */
    private TSMProject getProject(final IProject p) {
	return projects.get(p);
    }

    /**
     * Returns the corresponding TSMPackage to the given IFolder f
     * 
     * @param f
     *            IFolder to be loaded
     * @return TSMPackage of the map
     */
    private TSMPackage getPackage(final IFolder f) {
	return packages.get(f);
    }

    /**
     * Returns the corresponding TSMTestCase to the given IFile f
     * 
     * @param f
     *            IFile to be loaded
     * @return TSMTestCase of the map
     */
    private TSMTestCase getTestCase(final IFile f) {
	return testCases.get(f);
    }

    /**
     * Returns the corresponding TSMReport to the given IFile f
     * 
     * @param f
     *            IFile to be loaded
     * @return TSMReport of the map
     */
    private TSMReport getTestCaseProtocol(final IFile f) {
	return reports.get(f);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.tsmtest.datamodel.AbstractDataModel#convertToTSMResource(java.lang.Object)
     */
    @Override
    public TSMResource convertToTSMResource(final Object resource) {
	if (resource instanceof IProject) {
	    return getProject((IProject) resource);
	} else if (resource instanceof IFolder) {
	    return getPackage((IFolder) resource);
	} else if (resource instanceof IFile) {
	    final IFile file = (IFile) resource;
	    if (getContentTypeId(file).equals(
		    DataModelTypes.CONTENT_TYPE_ID_TESTCASE)) {
		return getTestCase(file);
	    } else if (getContentTypeId(file).equals(
		    DataModelTypes.CONTENT_TYPE_ID_PROTOCOL)) {
		return getTestCaseProtocol(file);
	    }
	}
	return null;
    }

    /**
     * Cuts of the file extension the the filename
     * 
     * @param filename
     *            filename with an extension
     * @return filename without the extension
     */
    private String stripExtension(final String filename) {
	final int extensionIndex = filename.lastIndexOf('.'); //$NON-NLS-1$
	if (extensionIndex == -1) {
	    return filename;
	}
	return filename.substring(0, extensionIndex);
    }

    /**
     * Changes the name of the given TSMResource to newName
     * 
     * @param tsmResource
     *            resource to be renamed
     * @param newName
     *            new name of the resource
     * @throws DataModelException
     */
    @Override
    protected void rename(final TSMResource tsmResource, final String newName)
	    throws DataModelException {
	internalRename(tsmResource, newName);
    }

    private IResource internalRename(final TSMResource tsmResource,
	    final String newName) throws DataModelException {
	IResource res = getResource(tsmResource);
	final String extension = tsmResource.getExtension();

	if (res != null) {
	    final IPath path = new Path(res.getParent().getFullPath()
		    .toString()
		    + "/" + newName + extension); //$NON-NLS-1$
	    if (res.getFullPath().equals(path)) {
		return res;
	    }
	    try {
		res.move(path, true, null);
		tsmResource.setName(newName);
		removeResource(res);
		// TODO reload is happening immediately. Check if returning new
		// IResource is really needed
		res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		put(res, tsmResource);

		//Subversion support
		if (VCSSettings.isSubversionSupportEnabled()) {
		    SubversionWrapper.addForCommit(res.getLocation().toString());
		    SubversionWrapper.commit(res.getLocation().toString());
		}
		return res;
	    } catch (final CoreException e) {
		log.error(Messages.DataModel_9 + e.getMessage());
		throw new DataModelException(
			DataModelException.RESOURCE_RENAME_ERROR);
	    }
	}
	log.error(Messages.DataModel_10
		+ DataModelException.RESOURCE_NOT_FOUND_ERROR);
	throw new DataModelException(
		DataModelException.RESOURCE_NOT_FOUND_ERROR);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.tsmtest.datamodel.AbstractDataModel#createProject(java.lang.String, org.eclipse.ui.IWorkingSet[])
     */
    @Override
    protected TSMProject createProject(final String name,
	    final IWorkingSet[] workingSets) throws DataModelException {
	DataModelException.verifyProject(name);

	final IProject project = ResourcesPlugin.getWorkspace().getRoot()
		.getProject(name);
	// create project
	try {
	    project.create(null);
	    if (workingSets != null) {
		Workbench.getInstance().getWorkingSetManager()
			.addToWorkingSets(project, workingSets);
	    }
	    // open project
	    project.open(null);
	    // add nature to project
	    final IProjectDescription description = project.getDescription();
	    final String[] oldIds = description.getNatureIds();
	    final String[] newIds = new String[oldIds.length + 1];
	    System.arraycopy(oldIds, 0, newIds, 0, oldIds.length);
	    newIds[oldIds.length] = "net.sourceforge.tsmtest.datamodel.TSMNature"; //$NON-NLS-1$
	    description.setNatureIds(newIds);
	    project.setDescription(description, null);
	    // set IProject attribute
	    final TSMProject tsmProject = new TSMProject(project.getName());
	    put(project, tsmProject);
	    //Subversion support
	    if (VCSSettings.isSubversionSupportEnabled()) {
		SubversionWrapper.addForCommit(project.getLocation().toString());
		SubversionWrapper.commit(project.getLocation().toString());
	    }
	    
	    return tsmProject;
	} catch (final CoreException e) {
	    log.error(Messages.DataModel_12 + e.getMessage());
	    throw new DataModelException(
		    DataModelException.PROJECT_CREATION_ERROR + "\n" //$NON-NLS-1$
			    + e.getMessage());
	} catch (final Exception e) {
	    log.error(Messages.DataModel_14 + e.getMessage());
	    throw new DataModelException(
		    DataModelException.PACKAGE_CREATION_ERROR + "\n" //$NON-NLS-1$
			    + e.getMessage());
	}
    }

    /**
     * Creates a new test case
     * 
     * @param name
     *            name of the new test case
     * @param testCase
     *            default data of the test case
     * @param container
     *            parent container of the test case
     * @return the created test case
     * @throws DataModelException
     */
    @Override
    protected TSMTestCase createTestCase(final String name,
	    final TestCaseDescriptor testCase, final TSMContainer container)
	    throws DataModelException {
	DataModelException.verifyTestCase(name, testCase);
	final IContainer iContainer = getResource(container);
	// create testCase
	try {
	    final InputStream source = Save.saveTestCase(testCase, container
		    .getProject().getName());
	    final IFile newFile = iContainer.getFile(new Path(name
		    + DataModelTypes.TSM_TEST_CASE_EXTENSION));
	    newFile.create(source, true, null);
	    final TSMTestCase tsmTestCase = new TSMTestCase(name, container,
		    testCase);
	    put(newFile, tsmTestCase);
	    if (VCSSettings.isSubversionSupportEnabled()) {
		SubversionWrapper.addForCommit(newFile.getLocation().toString());
		SubversionWrapper.commit(newFile.getLocation().toString());
	    }
	    return tsmTestCase;
	} catch (final CoreException e) {
	    log.error(Messages.DataModel_16 + e.getMessage());
	    throw new DataModelException(
		    DataModelException.TESTCASE_CREATION_ERROR + "\n" //$NON-NLS-1$
			    + e.getMessage(), e);
	} catch (final Exception e) {
	    log.error(Messages.DataModel_18 + e.getMessage());
	    throw new DataModelException(
		    DataModelException.PACKAGE_CREATION_ERROR + "\n" //$NON-NLS-1$
			    + e.getMessage());
	}
    }

    /**
     * Creates a new report
     * 
     * @param name
     *            name of the report
     * @param newTestCaseProtocol
     *            data of the report
     * @param testCase
     *            data of the test case
     * @return the created report
     * @throws DataModelException
     */
    @Override
    protected TSMReport createReport(final String name,
	    final TestCaseDescriptor newTestCaseProtocol,
	    final TSMTestCase testCase) throws DataModelException {
	DataModelException.verifyReport(name, newTestCaseProtocol);
	final IContainer iContainer = getResource(testCase).getParent();
	// create testCaseProtocol
	try {
	    final InputStream source = Save.saveTestCaseProtocol(
		    newTestCaseProtocol, testCase.getProject().getName());
	    final IFile newFile = iContainer.getFile(new Path(name
		    + DataModelTypes.TSM_REPORT_EXTENSION));
	    newFile.create(source, true, null);
	    final TSMReport tsmTestCaseProtocol = new TSMReport(name,
		    findContainer(iContainer), newTestCaseProtocol);
	    put(newFile, tsmTestCaseProtocol);

	    //Use subversion
	    if (VCSSettings.isSubversionSupportEnabled()) {
		    SubversionWrapper.addForCommit(newFile.getLocation().toString());
		    SubversionWrapper.addForCommit(getResource(testCase).getLocation().toString());
	    }
	    
	    return tsmTestCaseProtocol;
	} catch (final CoreException e) {
	    throw new DataModelException(
		    DataModelException.TESTCASEPROTOCOL_CREATION_ERROR + "\n" //$NON-NLS-1$
			    + e.getMessage());
	} catch (final Exception e) {
	    log.error(Messages.DataModel_21 + e.getMessage());
	    throw new DataModelException(
		    DataModelException.PACKAGE_CREATION_ERROR + "\n" //$NON-NLS-1$
			    + e.getMessage());
	}
    }

    /**
     * updates the report, renaming it if necessary
     * 
     * @param name
     *            new name if it should be changed otherwise same as
     *            testCaseProtocol.getName()
     * @param report
     *            old report
     * @param data
     *            the updated data
     * @return updated TSMReport
     * @throws DataModelException
     */
    @Override
    protected TSMReport updateTestCaseReport(final String name,
	    final TSMReport report, final TestCaseDescriptor data)
	    throws DataModelException {
	DataModelException.verifyTestCase(name, data);
	IFile file = getResource(report);
	if (!name.equals(report.getName())) {
	    // Name has changed -> rename file
	    file = (IFile) internalRename(report, name);
	}
	try {
	    final InputStream source = Save.saveTestCaseProtocol(data,
		    report.getName());
	    file.setContents(source, true, true, null);
	    report.setData(data);
	    // removeResource(file);
	    // put(file, testCaseProtocol);
	    return report;
	} catch (final CoreException e) {
	    // FIXME better Exception
	    log.error(Messages.DataModel_23 + e.getMessage());
	    throw new DataModelException(
		    DataModelException.TESTCASEPROTOCOL_CREATION_ERROR + "\n" //$NON-NLS-1$
			    + e.getMessage());
	}
    }

    /**
     * updates the test case, renaming it if necessary
     * 
     * @param name
     *            new name if it should be changed otherwise same as
     *            testCase.getName()
     * @param testCase
     *            old test case
     * @param data
     *            the updated data
     * @return updated TSMTestCase
     * @throws DataModelException
     */
    @Override
    protected TSMTestCase updateTestCase(final String name,
	    final TSMTestCase testCase, final TestCaseDescriptor data)
	    throws DataModelException {
	DataModelException.verifyTestCase(name, data);
	IFile file = getResource(testCase);
	if (!name.equals(testCase.getName())) {
	    // Name has changed -> rename file
	    file = (IFile) internalRename(testCase, name);
	}
	try {
	    final InputStream source = Save.saveTestCase(data,
		    testCase.getName());
	    file.setContents(source, true, true, null);
	    testCase.setData(data);
	    
	    final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
			.getProjects();
		for (final IProject project : projects) {
		    if (project.isOpen()) {
			try {
			    if (project.hasNature(DataModelTypes.TSM_NATURE)) {
				//Use subversion.
				if (VCSSettings.isSubversionSupportEnabled()) {
				    SubversionWrapper.commit(project.getLocation().toString());
				}
			    }
			} catch (final CoreException e) {
			    // Dont do anything
			}
		    }
		}
	    
	    // removeResource(file);
	    // put(file, testCase);
	    return testCase;
	} catch (final CoreException e) {
	    // FIXME better Exception
	    log.error(Messages.DataModel_25 + e.getMessage());
	    throw new DataModelException(
		    DataModelException.TESTCASE_CREATION_ERROR + "\n" //$NON-NLS-1$
			    + e.getMessage());
	}
    }

    /**
     * Creates a new package
     * 
     * @param name
     *            name of the package
     * @param container
     *            parent of the package
     * @return the created package
     * @throws DataModelException
     */
    @Override
    protected TSMPackage createPackage(final String name,
	    final TSMContainer container) throws DataModelException {
	DataModelException.verifyPackage(name);
	final IContainer iContainer = getResource(container);
	// create new package
	try {
	    final IFolder newFolder = iContainer.getFolder(new Path(name));
	    newFolder.create(true, true, null);
	    final TSMPackage tsmPackage = new TSMPackage(name, container);
	    put(newFolder, tsmPackage);
	    
	    return tsmPackage;
	} catch (final CoreException e) {
	    log.error(Messages.DataModel_27 + e.getMessage());
	    throw new DataModelException(
		    DataModelException.PACKAGE_CREATION_ERROR + "\n" //$NON-NLS-1$
			    + e.getMessage());
	} catch (final Exception e) {
	    log.error(Messages.DataModel_29 + e.getMessage());
	    throw new DataModelException(
		    DataModelException.PACKAGE_CREATION_ERROR + "\n" //$NON-NLS-1$
			    + e.getMessage());
	}
    }

    /**
     * Returns all test cases of the given container
     * 
     * @param tsmContainer
     *            contains the test cases
     * @return list of test cases
     */
    @Override
    protected List<TSMTestCase> getTestCases(final TSMContainer tsmContainer) {
	final List<TSMTestCase> tcs = new ArrayList<TSMTestCase>();
	for (final TSMTestCase tc : getTestCases()) {
	    if (tc.getParent().equals(tsmContainer)) {
		tcs.add(tc);
	    }
	}
	return tcs;
    }

    /**
     * Returns all report of the given container
     * 
     * @param tsmContainer
     *            contains the report
     * @return list of reports
     */
    @Override
    protected List<TSMReport> getReports(final TSMContainer tsmContainer) {
	final List<TSMReport> tcs = new ArrayList<TSMReport>();
	for (final TSMReport tc : getReports()) {
	    if (tc.getParent().equals(tsmContainer)) {
		tcs.add(tc);
	    }
	}
	return tcs;
    }

    /**
     * Returns all packages of the given container
     * 
     * @param tsmContainer
     *            contains the packages
     * @return list of packages
     */
    @Override
    protected List<TSMPackage> getPackages(final TSMContainer tsmContainer) {
	final List<TSMPackage> tcs = new ArrayList<TSMPackage>();
	for (final TSMPackage tc : getPackages()) {
	    if (tc.getParent().equals(tsmContainer)) {
		tcs.add(tc);
	    }
	}
	return tcs;
    }

    /**
     * Returns all objects of the given container
     * 
     * @param tsmContainer
     *            contains the objects
     * @return list containing all packages, test cases and reports of the
     *         container
     */
    @Override
    protected List<TSMResource> getChildren(final TSMContainer tsmContainer,
	    final boolean includeReports) {
	final List<TSMResource> tcs = new ArrayList<TSMResource>();
	final List<TSMResource> resources = new ArrayList<TSMResource>();
	resources.addAll(getPackages());
	resources.addAll(getTestCases());
	if (includeReports) {
	    resources.addAll(getReports());
	}
	for (final TSMResource tc : resources) {
	    if (tc.getParent().equals(tsmContainer)) {
		tcs.add(tc);
	    }
	}
	return tcs;
    }

    /**
     * Copies the image into the project it belongs to.
     * 
     * @param pathToImage
     * @param project
     * @param monitor
     * @return
     * @throws FileNotFoundException
     */
    private String copyImageToProject(final String pathToImage,
	    final String project) throws FileNotFoundException {
	// TODO Remove unused Method
	final IFolder folder = ResourcesPlugin.getWorkspace().getRoot()
		.getProject(project).getFolder(DataModelTypes.imageFolderName);
	try {
	    folder.create(true, true, null);
	} catch (final CoreException e) {
	    throw new FileNotFoundException(e.getMessage());
	}
	// find unused name in the image folder
	final File oldImage = new File(pathToImage);
	final IFile newImage = folder.getFile(oldImage.getName());
	int i = 2;
	while (newImage.exists()) {
	    folder.getFile(oldImage.getName() + "_" + i);
	    i++;
	}
	InputStream is;
	is = new FileInputStream(oldImage);
	try {
	    newImage.create(is, true, null);
	} catch (final CoreException e) {
	    throw new FileNotFoundException(e.getMessage());
	}
	return newImage.getProjectRelativePath().toString();
    }

    @Override
    public Set<String> getAllCreators() {
	final Set<String> testCaseCreators = new HashSet<String>();
	for (final TSMTestCase testCase : testCases.values()) {
	    testCaseCreators.add(testCase.getData().getAuthor());
	}
	return testCaseCreators;
    }

    @Override
    public Set<String> getAllTesters() {
	final Set<String> testCaseTesters = new HashSet<String>();
	for (final TSMReport testCase : reports.values()) {
	    testCaseTesters.add(testCase.getData().getAssignedTo());
	}
	return testCaseTesters;
    }

    @Override
    public void refresh() throws DataModelException {
	try {
	    setEnabled(false);
	    ResourcesPlugin.getWorkspace().getRoot()
		    .refreshLocal(IResource.DEPTH_INFINITE, null);
	    reloadResources();
	    for (final DataModelObservable observerable : observables) {
		observerable.dataModelChanged();
	    }
	} catch (final CoreException e) {
	    throw new DataModelException(e);
	} finally {
	    setEnabled(true);
	}
    }

    @Override
    protected void pasteFiles(final ArrayList<TSMResource> filesToCopy,
	    final TSMContainer destination, final boolean copy)
	    throws DataModelException {
	for (final TSMResource resource : filesToCopy) {
	    try {
		final IResource res = getResource(resource);
		final IResource container = getResource(destination);
		final String basePath = container.getFullPath() + "/"
			+ resource.getName().replaceAll(" \\(\\d*\\)", "");
		String path = basePath + resource.getExtension();
		// find empty place
		int id = 1;
		while (ResourcesPlugin.getWorkspace().getRoot()
			.findMember(path) != null) {
		    path = basePath + " (" + id + ")" + resource.getExtension();
		    id++;
		}
		if (copy) {
		    res.copy(new Path(path), true, null);
		} else {
		    res.move(new Path(path), true, null);
		}
	    } catch (final CoreException e) {
		throw new DataModelException(e);
	    }
	}
    }

    @Override
    protected TSMReport getReportByData(final ITestCaseDescriptor data) {
	return dataToReport.get(data);
    }

    @Override
    protected void delete(final TSMResource tsmResource)
	    throws DataModelException {
	try {
	    //For TSMProject we need the corresponding IProject to get the location.
	    if (tsmResource instanceof TSMProject) {
		IProject iProject = (IProject)DataModel.getInstance().getIProjectForTSMProject((TSMProject)tsmResource);
		//Use subversion.
		if (VCSSettings.isSubversionSupportEnabled()) {
		    SubversionWrapper.updateWorkspace();
		    SubversionWrapper.delete(iProject.getLocation().toString());
		    SubversionWrapper.commit(iProject.getLocation().toString());
		    getResource(tsmResource).delete(true, null);
		}
	    } else {
		getResource(tsmResource).delete(true, null);
		//Use subversion.
		if (VCSSettings.isSubversionSupportEnabled()) {
		    SubversionWrapper.updateWorkspace();
		    SubversionWrapper.delete(getResource(tsmResource).getLocation().toString());
		    SubversionWrapper.commit(getResource(tsmResource).getLocation().toString());
		}
	    }
	} catch (final CoreException e) {
	    throw new DataModelException(e);
	}
    }

    @Override
    public void setEnabled(final boolean enabled) {
	if (enabled) {
	    ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
		    IResourceChangeEvent.POST_CHANGE);
	} else {
	    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.tsmtest.datamodel.AbstractDataModel#getIProjectForTSMProject(net.sourceforge.tsmtest.datamodel.TSMProject)
     */
    public IProject getIProjectForTSMProject (TSMProject project) {
	for (IProject currentProject : projects.keySet()) {
	    if (currentProject.getName().equals(project.getName())) {
		return (IProject)currentProject;
	    }
	}
	return null;
    }
}
