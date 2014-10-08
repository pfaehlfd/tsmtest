 /*******************************************************************************
 * Copyright (c) 2012-2013 Bernhard Wetzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bernhard Wetzel - initial version
 *    Tobias Hirning - code cleanup
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.overview.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.descriptors.ITestCaseDescriptor;
import net.sourceforge.tsmtest.gui.SashManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Bernhard Wetzel
 *
 */
public class OverviewStepSash {
    private SashManager<TSMTestCase> tableManager;
    private static final int POSDESC = 0;
    private static final int POSDURATION = 1;

    private final Color red;
    private final Color green;
    private final Color yellow;
    private final Color grey;
    private final Color white;

    public OverviewStepSash(Composite parent) {
	tableManager = new SashManager<TSMTestCase>(parent) {
	    @Override
	    protected TSMTestCase createDefaultData() {
		return null;
	    }
	};

	// Testcase name and estimated time are always displayed
	tableManager.addColumn(createLabelColumn(tableManager),
		Messages.OverviewStepSash_0, 10, false, false, null);
	tableManager.addColumn(createLabelColumn(tableManager),
		Messages.OverviewStepSash_1, 10, false, false, null);
	parent.addDisposeListener(new DisposeListener() {

	    @Override
	    public void widgetDisposed(DisposeEvent e) {
		tableManager = null;
		dispose();
	    }
	});
	// Creating colors
	red = new Color(parent.getDisplay(), 255, 50, 50);
	green = new Color(parent.getDisplay(), 60, 160, 60);
	yellow = new Color(parent.getDisplay(), 245, 245, 0);
	grey = new Color(parent.getDisplay(), 220, 220, 220);
	white = new Color(parent.getDisplay(), 255, 255, 255);

    }

    /**
     * Disposes all colors
     */
    public void dispose() {
	red.dispose();
	green.dispose();
	yellow.dispose();
	grey.dispose();
	white.dispose();
    }

    /**
     * Sets the layout data of the tableManager
     * 
     * @param layout
     *            layout data
     */
    public void setLayoutData(Object layout) {
	tableManager.setLayoutData(layout);
    }

    /**
     * Returns the sashManager
     * 
     * @return instance of sashManager
     */
    public SashManager<TSMTestCase> getSashManager() {
	return tableManager;
    }

    /**
     * Set the content of the sashform
     * 
     * @param tsmTestCases
     *            rows to be displayed
     */
    public void initSteps(List<TSMTestCase> tsmTestCases) {
	tableManager.setContent(tsmTestCases);
    }

    /**
     * Creates a new label column in the sash form
     * @param Column 
     * 
     * @param SashManager
     *            <TSMTestCase> the instance of the sashManager
     */
    private SashManager<TSMTestCase>.SashManagerColumn<Label> createLabelColumn(
	    SashManager<TSMTestCase> tableManager) {
	return tableManager.new SashManagerColumn<Label>() {
	    private int height = 50;
	    /**
	     * Creates a new Label
	     * 
	     * @param parent
	     *            composite to be displayed in
	     * @param ModifyListener
	     *            ModifyListener (unused)
	     */
	    @Override
	    public Label create(Composite parent, ModifyListener heightListener) {
		Label label = new Label(parent, SWT.CENTER);
		Color white = new Color(null, 255, 255, 255);
		label.setBackground(white);
		white.dispose();
		return label;
	    }

	    /**
	     * Returns the height
	     * 
	     * @return returns the height of the label
	     */
	    @Override
	    protected int getHeight(Label widget) {
		return height;
	    }

	    /**
	     * Sets the height
	     * @param height
	     */
	    public void setHeight(int height) {
		this.height = height;
	    }

	    /**
	     * Sets up the current widget
	     * 
	     * @param widget
	     * @param data
	     * @param row
	     * @param column
	     */
	    @Override
	    public void renderTC(Label widget, TSMTestCase data, int row,
		    int column) {
		if (data == null) {
		    return;
		}
		// Need only the latest report of each revision
		TSMReport[] reports = getOnlyLastReport(data);
		ITestCaseDescriptor tcdata = data.getData();
		// First position is name
		if (column == POSDESC) {
		    widget.setText(data.getName());
		    String lastChanged = Messages.OverviewStepSash_2
			    + tcdata.getLastChangedOn().toString();
		    if (lastChanged.isEmpty()
			    || lastChanged.matches("<html><p></p></html>")) { //$NON-NLS-1$
			lastChanged = Messages.OverviewStepSash_3;
		    }
		    widget.setToolTipText(lastChanged);
		    // Second position is estimated time
		} else if (column == POSDURATION) {
		    widget.setText(tcdata.getExpectedDuration() + " h");
		    // rest revisions
		} else {
		    // getting the revision that should be displayed
		    Integer currentRev = Integer.parseInt(getColumnNames().get(
			    column));
		    // check each report if it is the corresponding
		    for (TSMReport currentReport : reports) {
			if (currentReport == null) {
			    continue;
			}
			if (currentReport.getData().getRevisionNumber() == currentRev) {
			    String realDuration = currentReport.getData().getRealDuration();
			    if (!(realDuration.isEmpty())) {
				realDuration = " ("	+ realDuration + ")";
			    }
			    String status =Messages.OverviewStepSash_8;
			    // colour bg + set text like status
			    switch (currentReport.getData().getStatus()) {
			    case failed:
				widget.setBackground(red);
				status = Messages.ClickableImage_4;
				break;
			    case notExecuted:
				widget.setBackground(grey);
				status = Messages.ClickableImage_1;
				break;
			    case passed:
				widget.setBackground(green);
				status = Messages.ClickableImage_2;
				break;
			    case passedWithAnnotation:
				widget.setBackground(yellow);
				status = Messages.ClickableImage_3;
				break;
			    default:
				break;
			    }
			    // sets status, real duration + tester
			    widget.setText(status
				    + "\n" + realDuration + "\n" + Messages.OverviewStepSash_5
				    + currentReport.getData().getAssignedTo());
			    return;
			}
		    }
		    // if no report was found
		    widget.setText(Messages.OverviewStepSash_6);
		    widget.setBackground(white);
		    return;
		}
	    }

	    @Override
	    protected void renderCustom(Label widget, String[] data, int row,
		    int column) {
		setHeight(30);
		if (data == null) {
		    return;
		}
		widget.setText(data[column]);
		if (column > 0 && column < 4) {
		    widget.setToolTipText(Messages.OverviewStepSash_7);
		}
	    }
	};
    }

    /**
     * Goes through all reports of the testcase and returns the latest report of
     * each revision
     * 
     * @param testcase
     *            testcase that is scanned
     * @return Array of reports with the latest report of each revision ordered
     *         from top to down
     */
    protected TSMReport[] getOnlyLastReport(TSMTestCase testcase) {
	// Getting all revisions + 2 "-1" at the beginning to equal columns
	ArrayList<Integer> revisions = getRevisions(testcase.getReports());
	// Getting all reports
	Collection<TSMReport> reps = testcase.getReports();
	// Collector for the accepted reps
	TSMReport[] newReps = new TSMReport[revisions.size()];
	// Check for latest report
	Date[] latest = new Date[revisions.size()];
	// We display newest first
	Collections.reverse(revisions);
	for (TSMReport currentReport : reps) {
	    //Checks if latest report of its revisions
	    //Saved if true
	    if (latest[revisions.indexOf(currentReport.getData().getRevisionNumber())] == null
		    || latest[revisions
			    .indexOf(currentReport.getData().getRevisionNumber())].before(currentReport
			    .getData().getLastExecution())) {
		newReps[revisions.indexOf(currentReport.getData().getRevisionNumber())] = currentReport;
		latest[revisions.indexOf(currentReport.getData().getRevisionNumber())] = currentReport
			.getData().getLastExecution();
	    }
	}
	return newReps;
    }

    /**
     * Returns all revisions of the give reports add 2 dummys at the beginning
     * @param reports
     * 		scanned reports
     * @return ArrayList<Integer>
     * 		List of revisions
     */
    public ArrayList<Integer> getRevisions(Collection<TSMReport> reports) {
	ArrayList<Integer> revisions = new ArrayList<Integer>();
	// for column name / est
	revisions.add(-1);
	revisions.add(-1);
	for (TSMReport rep : reports) {
	    if (!(revisions.contains(rep.getData().getRevisionNumber()))) {
		revisions.add(rep.getData().getRevisionNumber());
	    }
	}
	return revisions;
    }
    /**
     * Returns the content of the sashManager
     * @return	List<TSMTestCase>
     * 			List of testcases
     */
    public List<TSMTestCase> getAllSteps() {
	return tableManager.getContent();
    }
    /**
     * Returns a list with the test of the label-headers of each column
     * @return ArrayList<String>
     * 			List of columnnames
     */
    public ArrayList<String> getColumnNames() {
	return tableManager.getColumnListNames();
    }

    /**
     * Adds a column to the sashform
     * @param revision Name of the column
     * @param tooltip Text for the tooltip
     */
    public void addColumn(String revision, String tooltip) {
	tableManager.addColumn(createLabelColumn(tableManager), revision, revision.length()+10,
		false, false, tooltip);
    }
    /**
     * Removes a column
     * @param revision Index of the column that is to be deleted
     */
    public void removeColumn(int revision) {
	tableManager.removeColumn(revision);
    }
    /**
     * Removes all columns except the first two.
     */
    public void removeAllColumns() {
	tableManager.removeAllColumns();
    }

    public void initSteps(ArrayList<String[]> revisions) {
	tableManager.setContent(revisions);
    }
}
