 /*******************************************************************************
 * Copyright (c) 2012-2013 Bernhard Wetzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bernhard Wetzel - initial version
 *    Verena Käfer - bugfix
 *    Albert Flaig - code cleanup
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.overview.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.SWTUtils;
import net.sourceforge.tsmtest.datamodel.MultiPageEditorPartInput;
import net.sourceforge.tsmtest.datamodel.ResourceEditorInput;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.datamodel.TSMProject;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionModel;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionObservable;
import net.sourceforge.tsmtest.gui.ResourceManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Bernhard Wetzel
 *
 */
public class Overview extends MultiPageEditorPartInput implements
	SelectionObservable {

    public static final String ID = "net.sourceforge.tsmtest.gui.overview.view.overview"; //$NON-NLS-1$
    private OverviewStepSash sashPage0;
    private OverviewStepSash sashPage1Revs;
    private ArrayList<TSMTestCase> input = new ArrayList<TSMTestCase>();
    private Label titel;
    private ArrayList<Integer> revisions;
    private ArrayList<Integer> revisionsSelected;

    /**
     * Constructs a new site
     */
    @Override
    public void init(final IEditorSite site, final IEditorInput input)
	    throws PartInitException {
	setSite(site);
	setInput(input);
	setPartName(Messages.Overview_8);
    }

    /**
     * Disposes the sashform and unregisters from SelectionListener
     */
    @Override
    public void dispose() {
	sashPage0.dispose();
	sashPage1Revs.dispose();
	SelectionManager.getInstance().unregister(this);
	// super.dispose();
    }

    /**
     * Reacts on a changed selection in the package explorer and sets new
     * content in the sashform
     */
    @Override
    public void selectionChanged() {
	final SelectionModel sm = SelectionManager.getInstance().getSelection();
	final ArrayList<TSMResource> file = sm.getAllResources();
	// if only reports are selected the revisions and corresponding
	// testcases are shown
	Boolean onlyRep = true;
	for (final TSMResource f : file) {
	    if (!(f instanceof TSMReport)) {
		onlyRep = false;
		break;
	    }
	}
	input.clear();
	if (onlyRep) {
	    for (final TSMResource f : file) {
		final int rev = ((TSMReport) f).getData().getRevisionNumber();
		sashPage0.removeAllColumns();
		sashPage0.addColumn(rev + "", null);
		final TSMTestCase tc = ((TSMReport) f).getTestCase();
		if (!(input.contains(tc))) {
		    input.add(tc);
		}
	    }
	} else {
	    // all selected resources and there children are scanned for
	    // testcases
	    for (final TSMResource res : file) {
		for (final TSMResource tc : getChildrens(res)) {
		    if (!(input.contains(tc))) {
			input.add((TSMTestCase) tc);
		    }
		}
	    }
	}
	// list of revisions of the testcases reports
	revisions.clear();
	for (final TSMTestCase tc : input) {
	    for (final TSMReport rep : tc.getReports()) {
		if (!(revisions.contains(rep.getData().getRevisionNumber()))) {
		    revisions.add(rep.getData().getRevisionNumber());
		}
	    }
	}
	// sorted and reversed because we display from highest to lowest
	Collections.sort(revisions);
	Collections.reverse(revisions);
	// setting the title of the sashform
	titel.setText(Messages.Overview_9);
	if (input.size() == 1) {
	    titel.setText(getTitleText(input.get(0)));
	    sashPage0.initSteps(input);
	} else {
	    titel.setText(Messages.Overview_8);
	    titel.update();
	    if (!(input.isEmpty())) {
		sashPage0.initSteps(input);
	    }
	}
    }

    /**
     * returns the childrens and childrens children (aso) from the given file
     * 
     * @param file
     * @return
     */
    private ArrayList<TSMTestCase> getChildrens(final TSMResource file) {
	final ArrayList<TSMTestCase> tcs = new ArrayList<TSMTestCase>();
	// check packages if project
	if (file instanceof TSMProject) {
	    final List<TSMResource> res = ((TSMProject) file).getChildren();
	    for (final TSMResource currentRessource : res) {
		if (currentRessource instanceof TSMPackage) {
		    tcs.addAll(getChildrens(currentRessource));
		} else {
		    if (currentRessource instanceof TSMTestCase) {
			tcs.add((TSMTestCase) currentRessource);
		    }
		}
	    }
	    return tcs;
	    // check subpackages if package
	} else if (file instanceof TSMPackage) {
	    final List<TSMResource> res = ((TSMPackage) file).getChildren();
	    for (final TSMResource currentRessource : res) {
		if (currentRessource instanceof TSMPackage) {
		    tcs.addAll(getChildrens(currentRessource));
		} else {
		    if (currentRessource instanceof TSMTestCase) {
			tcs.add((TSMTestCase) currentRessource);
		    }
		}
	    }
	    return tcs;
	    // add testcase
	} else if (file instanceof TSMTestCase) {
	    tcs.add((TSMTestCase) file);
	    return tcs;
	}
	return tcs;
    }

    @Override
    public void setFocus() {
	// nothing to be focused
    }

    /**
     * Opens the editor and sets input
     * 
     * @param sm
     *            input of the editor
     * @return the editor
     */
    public static Object openGUI(final SelectionModel sm) {
	final TSMResource input = sm.getFirstFile();
	final IWorkbenchPage page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();
	IEditorPart editor = SWTUtils.findOpenEditor(input);
	if (editor == null) {
	    try {
		editor = page.openEditor(new ResourceEditorInput(input), ID);
	    } catch (final PartInitException e) {
		e.printStackTrace();
	    }
	} else {
	    page.activate(editor);
	}
	return editor;
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
	// There is no save

    }

    @Override
    public void doSaveAs() {
	// There is no save

    }

    @Override
    public boolean isDirty() {
	// never gets Dirty
	return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
	// There is no save
	return false;
    }

    @Override
    protected void getInput(final TSMTestCase input) {
	// input is never getted
    }

    @Override
    protected void setEditorInput(final TSMReport input) {
	// input is never getted
    }

    @Override
    public TSMTestCase getTestCaseInput() {
	// input is never getted
	return null;
    }

    @Override
    public TSMReport getReportInput() {
	// input is never getted
	return null;
    }

    /**
     * Creates the pages of the editor and registers the Selectionlistener.
     */
    @Override
    protected void createPages() {
	createPage0();
	createPage1();
	SelectionManager.getInstance().register(this);
    }

    /**
     * Creates the overviewpage with the sashform
     */
    private void createPage0() {
	// parent composite
	Composite parent;
	parent = new Composite(getContainer(), SWT.NONE);
	parent.addDisposeListener(new DisposeListener() {

	    @Override
	    public void widgetDisposed(final DisposeEvent e) {
		sashPage0.getSashManager().dispose();
		dispose();
	    }
	});
	parent.setLayout(new GridLayout(1, false));
	final SelectionModel sm = SelectionManager.getInstance().getSelection();
	final ArrayList<TSMResource> file = sm.getAllResources();
	// if only reports are selected the revisions and corresponding
	// test cases are shown
	Boolean onlyRep = true;
	for (final TSMResource f : file) {
	    if (!(f instanceof TSMReport)) {
		onlyRep = false;
		break;
	    }
	}
	input.clear();
	final ArrayList<Integer> revs = new ArrayList<Integer>();
	if (onlyRep) {
	    for (final TSMResource f : file) {
		revs.add(((TSMReport) f).getData().getRevisionNumber());
		final TSMTestCase tc = ((TSMReport) f).getTestCase();
		if (!(input.contains(tc))) {
		    input.add(tc);
		}
	    }
	} else {
	    // all selected resources and there children are scanned for
	    // testcases
	    for (final TSMResource currentRessource : file) {
		for (final TSMResource currentTestcase : getChildrens(currentRessource)) {
		    if (!(input.contains(currentTestcase))) {
			input.add((TSMTestCase) currentTestcase);
		    }
		}
	    }
	}
	// toplevel composite divided into left and right
	final Composite topInfo = new Composite(parent, SWT.NONE);
	topInfo.setLayout(new GridLayout(3, false));
	// topleft
	final Composite topInfoLeft = new Composite(topInfo, SWT.FILL);
	topInfoLeft.setLayout(new FillLayout(SWT.VERTICAL));
	titel = new Label(topInfoLeft, SWT.FILL);
	if (file.isEmpty()) {
	    titel.setText(Messages.Overview_8);
	} else {
	    titel.setText(getTitleText(file.get(0)));
	}
	// topright
	final Group topInfoRight = new Group(topInfo, SWT.END);
	topInfoRight.setLayout(new GridLayout(5, false));
	// getting all revisions of the inputs reports
	revisions = new ArrayList<Integer>();
	for (final TSMTestCase currentTestcase : input) {
	    for (final TSMReport currentReport : currentTestcase.getReports()) {
		if (!(revisions.contains(currentReport.getData().getRevisionNumber()))) {
		    revisions.add(currentReport.getData().getRevisionNumber());
		}
	    }
	}
	Collections.sort(revisions);
	Collections.reverse(revisions);
	// viewing all revisions
	Button btnAddAll;
	btnAddAll = new Button(topInfoRight, SWT.PUSH);
	btnAddAll.setText(Messages.Overview_10);
	btnAddAll.addSelectionListener(new SelectionListener() {
	    /**
	     * removes all columns and adds all revisions the input contains
	     */
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		sashPage0.removeAllColumns();
		for (final int currentRevision : revisions) {
		    sashPage0.addColumn(currentRevision + "", null);
		}
		revisionsSelected = revisions;
		sashPage0.initSteps(input);
	    }

	    @Override
	    public void widgetDefaultSelected(final SelectionEvent e) {
		// do nothing

	    }
	});

	// opens dialog to select specific revisions
	Button btnCustom;
	btnCustom = new Button(topInfoRight, SWT.BUTTON1);
	btnCustom.setText(Messages.Overview_11);
	btnCustom.addSelectionListener(new SelectionListener() {
	    /**
	     * removes all columns and adds the selected
	     */
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		if (revisionsSelected == null) {
		    revisionsSelected = new ArrayList<Integer>();
		}
		final OverviewDialog dia = new OverviewDialog(
			new ArrayList<Integer>(revisions),
			new ArrayList<Integer>(revisionsSelected));
		if (dia.open() == Window.OK) {
		    sashPage0.removeAllColumns();
		    // add selected
		    revisionsSelected = dia.getRevTicks();
		    Collections.sort(revisionsSelected);
		    Collections.reverse(revisionsSelected);
		    for (final int currentRevision : revisionsSelected) {
			sashPage0.addColumn(currentRevision + "", null);
		    }
		    sashPage0.initSteps(input);
		}
	    }

	    @Override
	    public void widgetDefaultSelected(final SelectionEvent e) {
		// do nothing

	    }
	});
	// initialize sashform
	sashPage0 = new OverviewStepSash(parent);
	if (onlyRep) {
	    for (final int revisionColumn : revs) {
		sashPage0.addColumn(revisionColumn + "", null);
	    }
	}
	sashPage0.initSteps(input);
	final int index = addPage(parent);
	setPageText(index, Messages.Overview_2);
    }

    /**
     * Returns the text that will be display in the title
     * 
     * @param file
     *            file the titletext depends on
     * @return String new text of the title
     */
    private String getTitleText(final TSMResource file) {
	if (file instanceof TSMProject) {
	    input = (ArrayList<TSMTestCase>) ((TSMProject) file).getTestCases();
	    return (Messages.Overview_4 + file.getName());
	} else if (file instanceof TSMPackage) {
	    input = (ArrayList<TSMTestCase>) ((TSMPackage) file).getTestCases();
	    return (Messages.Overview_5 + file.getName());
	} else if (file instanceof TSMTestCase) {
	    input = SelectionManager.getInstance().getSelection().getTestCases();
	    return (Messages.Overview_6);
	}
	return (Messages.Overview_8);
    }

    /**
     * Creates the second page with the statistics
     */
    private void createPage1() {
	final Composite parent = new Composite(getContainer(), SWT.NONE);
	parent.setLayout(new GridLayout(1, false));
	parent.addDisposeListener(new DisposeListener() {

	    @Override
	    public void widgetDisposed(final DisposeEvent e) {
		sashPage1Revs.getSashManager().dispose();
		dispose();
	    }
	});
	// total statistics
	int high = 0;
	int medium = 0;
	int low = 0;
	for (final TSMTestCase currentTestcase : TSMTestCase.list()) {
	    if (currentTestcase.getData().getStatus().equals(StatusType.failed)
		    || currentTestcase.getData().getStatus().equals(StatusType.notExecuted)) {
		if (currentTestcase.getData().getPriority().equals(PriorityType.high)) {
		    high++;
		} else if (currentTestcase.getData().getPriority()
			.equals(PriorityType.medium)) {
		    medium++;
		} else {
		    low++;
		}
	    }
	}

	// Composites for total statistics
	final Group statGroup = new Group(parent, SWT.FILL);
	statGroup.setLayout(new GridLayout(6, false));
	final Composite col1 = new Composite(statGroup, SWT.FILL);
	col1.setLayout(new GridLayout());
	final Composite col2 = new Composite(statGroup, SWT.FILL);
	col2.setLayout(new GridLayout());
	final Composite col3 = new Composite(statGroup, SWT.FILL);
	col3.setLayout(new GridLayout());
	final Composite col4 = new Composite(statGroup, SWT.FILL);
	col4.setLayout(new GridLayout());
	final Composite col5 = new Composite(statGroup, SWT.FILL);
	col5.setLayout(new GridLayout());
	final Composite col6 = new Composite(statGroup, SWT.FILL);
	col6.setLayout(new GridLayout());

	final String[] time = getTimeSpentTotal();
	// col 1
	final Label lblTotalStatistic = new Label(col1, SWT.FILL);
	lblTotalStatistic.setText(Messages.Overview_12);
	final Label lblTimeSpent = new Label(col1, SWT.FILL);
	lblTimeSpent.setText(Messages.Overview_13);
	final Label lblNotPassedTC = new Label(col1, SWT.FILL);
	lblNotPassedTC.setText(Messages.Overview_19);

	// col 2
	final Label lblHighPrio = new Label(col2, SWT.FILL);
	lblHighPrio.setText(Messages.Overview_16);
	final Label lblHighPrioAmount = new Label(col2, SWT.FILL);
	lblHighPrioAmount.setText(high + "");
	final Label lblHighPrioTime = new Label(col2, SWT.FILL);
	lblHighPrioTime.setText(time[1]);

	// col 3
	final Label lblMiddlePrio = new Label(col3, SWT.FILL);
	lblMiddlePrio.setText(Messages.Overview_17);
	final Label lblMiddlePrioAmount = new Label(col3, SWT.FILL);
	lblMiddlePrioAmount.setText(medium + "");
	final Label lblMiddlePrioTime = new Label(col3, SWT.FILL);
	lblMiddlePrioTime.setText(time[2]);

	// col 4
	final Label lblLowPrio = new Label(col4, SWT.FILL);
	lblLowPrio.setText(Messages.Overview_18);
	final Label lblLowPrioAmount = new Label(col4, SWT.FILL);
	lblLowPrioAmount.setText(low + "");
	final Label lblLowPrioTime = new Label(col4, SWT.FILL);
	lblLowPrioTime.setText(time[3]);

	// col 5
	final Label lblTotal = new Label(col5, SWT.FILL);
	lblTotal.setText(Messages.Overview_21);
	final Label lblTotalPrioAmount = new Label(col5, SWT.FILL);
	lblTotalPrioAmount.setText((high + medium + low) + "");
	final Label lblTotalPrioTime = new Label(col5, SWT.FILL);
	lblTotalPrioTime.setText(time[4]);

	// col 6 = ample
	final Group ample = new Group(col6, SWT.NONE);
	final FillLayout fill = new FillLayout();
	fill.type = SWT.VERTICAL;
	ample.setLayout(fill);
	Image imagetop = null;
	Image imagemiddle = null;
	Image imagebot = null;
	Boolean green = true;
	if (high > 0) {
	    imagetop = ResourceManager.getImgRed();
	    green = false;
	} else {
	    imagetop = ResourceManager.getImgGray();
	}
	if (medium > 0 || low > 0 && green) {
	    imagemiddle = ResourceManager.getImgOrange();
	    green = false;
	} else {
	    imagemiddle = ResourceManager.getImgGray();
	}
	if (green) {
	    imagebot = ResourceManager.getImgGreen();
	} else {
	    imagebot = ResourceManager.getImgGray();
	}
	final Label topAmple = new Label(ample, SWT.NONE);
	topAmple.setImage(imagetop);
	final Label midAmple = new Label(ample, SWT.NONE);
	midAmple.setImage(imagemiddle);
	final Label botAmple = new Label(ample, SWT.NONE);
	botAmple.setImage(imagebot);
	// statistics by revision
	final Label statLbl = new Label(parent, SWT.LEFT);
	statLbl.setText(Messages.Overview_14);

	sashPage1Revs = new OverviewStepSash(parent);
	Boolean noEst = false;
	for (final TSMTestCase currentTestcase : TSMTestCase.list()) {
	    if (currentTestcase.getData().getExpectedDuration().equals("00:00")
		    || currentTestcase.getData().getExpectedDuration().equals("")) {
		noEst = true;
		break;
	    }
	}
	initColumnsRevs(sashPage1Revs, noEst);
	final ArrayList<String[]> revisionData = new ArrayList<String[]>();
	final ArrayList<Integer> allRevs = new ArrayList<Integer>();
	for (final TSMTestCase currentTestcase : TSMTestCase.list()) {
	    for (final TSMReport rep : currentTestcase.getReports()) {
		if (!(allRevs.contains(rep.getData().getRevisionNumber()))) {
		    allRevs.add(rep.getData().getRevisionNumber());
		}
	    }
	}
	// sorted backwards
	Collections.sort(allRevs);
	Collections.reverse(allRevs);
	for (final int currentRevision : allRevs) {
	    final String[] data = { currentRevision + "",
		    getPriorityTC(currentRevision, PriorityType.high),
		    getPriorityTC(currentRevision, PriorityType.medium),
		    getPriorityTC(currentRevision, PriorityType.low), getTimeSpent(currentRevision),
		    getTimeNeeded(currentRevision) };
	    revisionData.add(data);
	}
	sashPage1Revs.initSteps(revisionData);
	sashPage1Revs.getSashManager().scrollToTop();
	// indexing + pagetitel
	final int index = addPage(parent);
	setPageText(index, Messages.Overview_3);
    }

    private String getTimeNeeded(final int rev) {
	final Collection<TSMTestCase> testcases = TSMTestCase.list();
	int hours = 0;
	int minutes = 0;
	for (final TSMTestCase currentTestcase : testcases) {
	    if (getCurrentReport(currentTestcase, rev) == null) {
		final String[] duration = currentTestcase.getData().getExpectedDuration()
			.split(":");
		if (duration.length > 1) {
		    hours += Integer.parseInt(duration[0]);
		    minutes += Integer.parseInt(duration[1]);
		} else {
		    if (!(duration[0].equals(""))) {
			minutes += Integer.parseInt(duration[0]);
		    }
		}
	    }
	}
	if (minutes > 59) {
	    hours += minutes / 60;
	    minutes = minutes % 60;
	}
	return buildTime(hours, minutes, 0);
    }

    private String buildTime(final int hours, final int minutes,
	    final int seconds) {
	String returnString = hours + ":";
	if (minutes < 10) {
	    returnString += "0" + minutes;
	} else {
	    returnString += minutes;
	}
	returnString += ":";
	if (seconds < 10) {
	    returnString += "0" + seconds;
	} else {
	    returnString += seconds;
	}
	return returnString;
    }

    private String getTimeSpent(final int rev) {
	final Collection<TSMTestCase> testcases = TSMTestCase.list();
	int hours = 0;
	int minutes = 0;
	int seconds = 0;
	for (final TSMTestCase tc : testcases) {
	    final Collection<TSMReport> reports = tc.getReports();
	    for (final TSMReport currentReport : reports) {
		if (currentReport.getData().getRevisionNumber() == rev) {
		    final String duration = currentReport.getData().getRealDuration();
		    if (duration.length() > 5) {
			hours += Integer.parseInt(duration.substring(0,
				duration.indexOf(':')));
			minutes += Integer.parseInt(duration.substring(
				duration.indexOf(':') + 1,
				duration.lastIndexOf(':')));
			seconds += Integer.parseInt(duration.substring(duration
				.lastIndexOf(':') + 1));
		    } else if (duration.length() > 2) {
			minutes += Integer.parseInt(duration.substring(0,
				duration.indexOf(':')));
			seconds += Integer.parseInt(duration.substring(duration
				.indexOf(':')) + 1);
		    } else {
			seconds += Integer.parseInt(duration);
		    }
		}
	    }
	}
	if (seconds > 59) {
	    minutes += seconds / 60;
	    seconds = seconds % 60;
	}
	if (minutes > 59) {
	    hours += minutes / 60;
	    minutes = minutes % 60;
	}
	return buildTime(hours, minutes, seconds);
    }

    private String[] getTimeSpentTotal() {
	final Collection<TSMTestCase> testcases = TSMTestCase.list();
	int hours_high = 0;
	int minutes_high = 0;
	int seconds_high = 0;
	int hours_medium = 0;
	int minutes_medium = 0;
	int seconds_medium = 0;
	int hours_low = 0;
	int minutes_low = 0;
	int seconds_low = 0;

	for (final TSMTestCase currentTestcase : testcases) {
	    final Collection<TSMReport> reports = currentTestcase.getReports();
	    for (final TSMReport rep : reports) {
		if (currentTestcase.getData().getPriority().equals(PriorityType.high)) {
		    final String duration = rep.getData().getRealDuration();
		    if (duration.length() > 5) {
			hours_high += Integer.parseInt(duration.substring(0,
				duration.indexOf(':')));
			minutes_high += Integer.parseInt(duration.substring(
				duration.indexOf(':') + 1,
				duration.lastIndexOf(':')));
			seconds_high += Integer.parseInt(duration
				.substring(duration.lastIndexOf(':') + 1));
		    } else if (duration.length() > 2) {
			minutes_high += Integer.parseInt(duration.substring(0,
				duration.indexOf(':')));
			seconds_high += Integer.parseInt(duration
				.substring(duration.indexOf(':')) + 1);
		    } else {
			seconds_high += Integer.parseInt(duration);
		    }
		} else if (currentTestcase.getData().getPriority()
			.equals(PriorityType.medium)) {
		    final String duration = rep.getData().getRealDuration();
		    if (duration.length() > 5) {
			hours_medium += Integer.parseInt(duration.substring(0,
				duration.indexOf(':')));
			minutes_medium += Integer.parseInt(duration.substring(
				duration.indexOf(':') + 1,
				duration.lastIndexOf(':')));
			seconds_medium += Integer.parseInt(duration
				.substring(duration.lastIndexOf(':') + 1));
		    } else if (duration.length() > 2) {
			minutes_medium += Integer.parseInt(duration.substring(
				0, duration.indexOf(':')));
			seconds_medium += Integer.parseInt(duration
				.substring(duration.indexOf(':')) + 1);
		    } else {
			seconds_medium += Integer.parseInt(duration);
		    }
		} else {
		    final String duration = rep.getData().getRealDuration();
		    if (duration.length() > 5) {
			hours_low += Integer.parseInt(duration.substring(0,
				duration.indexOf(':')));
			minutes_low += Integer.parseInt(duration.substring(
				duration.indexOf(':') + 1,
				duration.lastIndexOf(':')));
			seconds_low += Integer.parseInt(duration
				.substring(duration.lastIndexOf(':') + 1));
		    } else if (duration.length() > 2) {
			minutes_low += Integer.parseInt(duration.substring(0,
				duration.indexOf(':')));
			seconds_low += Integer.parseInt(duration
				.substring(duration.indexOf(':')) + 1);
		    } else {
			seconds_low += Integer.parseInt(duration);
		    }
		}
	    }
	}
	if (seconds_high > 59) {
	    minutes_high += seconds_high / 60;
	    seconds_high = seconds_high % 60;
	}
	if (minutes_high > 59) {
	    hours_high += minutes_high / 60;
	    minutes_high = minutes_high % 60;
	}
	if (seconds_medium > 59) {
	    minutes_medium += seconds_medium / 60;
	    seconds_medium = seconds_medium % 60;
	}
	if (minutes_medium > 59) {
	    hours_medium += minutes_medium / 60;
	    minutes_medium = minutes_medium % 60;
	}
	if (seconds_low > 59) {
	    minutes_low += seconds_low / 60;
	    seconds_low = seconds_low % 60;
	}
	if (minutes_low > 59) {
	    hours_low += minutes_low / 60;
	    minutes_low = minutes_low % 60;
	}
	final String[] returnString = {
		Messages.Overview_19,
		buildTime(hours_high, minutes_high, seconds_high),
		buildTime(hours_medium, minutes_medium, seconds_medium),
		buildTime(hours_low, minutes_low, seconds_low),
		buildTime(hours_high + hours_medium + hours_low, minutes_high
			+ minutes_medium + minutes_low, seconds_high
			+ seconds_medium + seconds_low) };
	return returnString;
    }

    private String getPriorityTC(final int rev, final PriorityType prio) {
	final Collection<TSMTestCase> testcases = TSMTestCase.list();
	TSMReport report;
	int counter = 0;
	int counterPas = 0;
	for (final TSMTestCase currentTestcase : testcases) {
	    if (currentTestcase.getData().getPriority().equals(prio)) {
		report = getCurrentReport(currentTestcase, rev);
		if (report != null) {
		    if (report.getData().getStatus().equals(StatusType.passed)
			    || report.getData().getStatus()
				    .equals(StatusType.passedWithAnnotation)) {
			counterPas++;
		    }
		}
		counter++;
	    }
	}
	return counterPas + "/" + counter;
    }

    private void initColumnsRevs(final OverviewStepSash sash, final Boolean star) {
	sash.removeColumn(0);
	sash.removeColumn(0);
	sash.addColumn(Messages.Overview_15, null);
	sash.addColumn(Messages.Overview_16, null);
	sash.addColumn(Messages.Overview_17, null);
	sash.addColumn(Messages.Overview_18, null);
	if (star) {
	    sash.addColumn(Messages.Overview_19 + "*", Messages.Overview_23);
	    sash.addColumn(Messages.Overview_20 + "*", Messages.Overview_23);
	} else {
	    sash.addColumn(Messages.Overview_19, null);
	    sash.addColumn(Messages.Overview_20, null);
	}
    }

    /**
     * Goes through all reports of the testcase and returns the latest report of
     * each revision
     * 
     * @param testcase
     *            testcase that is scanned
     * @param rev FIXME For what is the parameter rev used?
     * @return Array of reports with the latest report of each revision ordered
     *         from top to down
     */
    protected TSMReport getCurrentReport(final TSMTestCase testcase, final int rev) {
	// getting all reports
	final Collection<TSMReport> reportsForTestcase = testcase.getReports();
	
	TSMReport report = null;
	// check for latest report
	Date latest = new Date();
	Boolean first = true;
	for (final TSMReport currentReport : reportsForTestcase) {
	    // check for right revision + latest date
	    if (currentReport.getData().getRevisionNumber() == rev) {
		if (first) {
		    report = currentReport;
		    latest = currentReport.getData().getLastExecution();
		    first = false;
		}
		if (latest == null
			|| latest.before(currentReport.getData().getLastExecution())) {
		    report = currentReport;
		    latest = currentReport.getData().getLastExecution();
		}
	    }
	}
	return report;
    }
}
