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

    private Color red;
    private Color green;
    private Color yellow;
    private Color grey;
    private Color white;

    public OverviewStepSash(Composite parent) {
	tableManager = new SashManager<TSMTestCase>(parent) {
	    @Override
	    protected TSMTestCase createDefaultData() {
		return null;
	    }
	};

	// testcase name and est are always displayed
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
	// creating colors
	red = new Color(parent.getDisplay(), 255, 50, 50);
	green = new Color(parent.getDisplay(), 60, 160, 60);
	yellow = new Color(parent.getDisplay(), 245, 245, 0);
	grey = new Color(parent.getDisplay(), 220, 220, 220);
	white = new Color(parent.getDisplay(), 255, 255, 255);

    }

    /**
     * Disposes all colours
     */
    public void dispose() {
	red.dispose();
	green.dispose();
	yellow.dispose();
	grey.dispose();
    }

    /**
     * sets the layoutdata of the tableManager
     * 
     * @param layout
     *            layoutdata
     */
    public void setLayoutData(Object layout) {
	tableManager.setLayoutData(layout);
    }

    /**
     * returns the sashManager
     * 
     * @return instance of sashManager
     */
    public SashManager<TSMTestCase> getSashManager() {
	return tableManager;
    }

    /**
     * Set the content of the sashform
     * 
     * @param TSMTestCases
     *            rows to be displayed
     */
    public void initSteps(List<TSMTestCase> TSMTestCases) {
	tableManager.setContent(TSMTestCases);
    }

    /**
     * Creates a new Label column in the sash form
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
	     * creates a new Label
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
	     * @return returns 50
	     */
	    @Override
	    protected int getHeight(Label widget) {
		return height;
	    }
	    
	    /**
	     * Sets the height
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
		// need only the latest report of each revision
		TSMReport[] reps = getOnlyLastReport(data);
		ITestCaseDescriptor tcdata = data.getData();
		// first pos is name
		if (column == POSDESC) {
		    widget.setText(data.getName());
		    String lastChanged = Messages.OverviewStepSash_2
			    + tcdata.getLastChangedOn().toString();
		    if (lastChanged.isEmpty()
			    || lastChanged.matches("<html><p></p></html>")) { //$NON-NLS-1$
			lastChanged = Messages.OverviewStepSash_3;
		    }
		    widget.setToolTipText(lastChanged);
		    // second pos is est time
		} else if (column == POSDURATION) {
		    widget.setText(tcdata.getExpectedDuration() + " h");
		    // rest revisions
		} else {
		    // getting the revision that should be displayed
		    Integer currentRev = Integer.parseInt(getColumnNames().get(
			    column));
		    // check each report if it is the corresponding
		    for (TSMReport rep : reps) {
			if (rep == null) {
			    continue;
			}
			if (rep.getData().getRevisionNumber() == currentRev) {
			    String realDur = rep.getData().getRealDuration();
			    if (!(realDur.isEmpty())) {
				realDur = " ("	+ realDur + ")";
			    }
			    String status =Messages.OverviewStepSash_8;
			    // colour bg + set text like status
			    switch (rep.getData().getStatus()) {
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
				    + "\n" + realDur + "\n" + Messages.OverviewStepSash_5
				    + rep.getData().getAssignedTo());
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
     * @param data
     *            testcase that is scanned
     * @return Array of reports with the latest report of each revision ordered
     *         from top to down
     */
    protected TSMReport[] getOnlyLastReport(TSMTestCase data) {
	// getting all revisions + 2 "-1" at the beginning to equal columns
	ArrayList<Integer> revisions = getRevisions(data.getReports());
	// getting all reports
	Collection<TSMReport> reps = data.getReports();
	// collector for the acceptet reps
	TSMReport[] newReps = new TSMReport[revisions.size()];
	// check for latest report
	Date[] latest = new Date[revisions.size()];
	// we display newest first
	Collections.reverse(revisions);
	for (TSMReport r : reps) {
	    //checks if latest report of its revisions
	    //saved if true
	    if (latest[revisions.indexOf(r.getData().getRevisionNumber())] == null
		    || latest[revisions
			    .indexOf(r.getData().getRevisionNumber())].before(r
			    .getData().getLastExecution())) {
		newReps[revisions.indexOf(r.getData().getRevisionNumber())] = r;
		latest[revisions.indexOf(r.getData().getRevisionNumber())] = r
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
     * Returns a list with the test of the labelhaeders of each column
     * @return ArrayList<String>
     * 			List of columnnames
     */
    public ArrayList<String> getColumnNames() {
	return tableManager.getColumnListNames();
    }

    /**
     * adds a column to the sashform
     * 
     * @param revision
     * 		name of the column
     */
    public void addColumn(String revision, String tooltip) {
	tableManager.addColumn(createLabelColumn(tableManager), revision, revision.length()+10,
		false, false, tooltip);
    }
    /**
     * removes a column
     * @param revision
     * 		index of the column that is to be deleted
     */
    public void removeColumn(int revision) {
	tableManager.removeColumn(revision);
    }
    /**
     * removes a columns except the first 2
     */
    public void removeAllColumns() {
	tableManager.removeAllColumns();
    }

    public void initSteps(ArrayList<String[]> revisions) {
	tableManager.setContent(revisions);
    }
}
