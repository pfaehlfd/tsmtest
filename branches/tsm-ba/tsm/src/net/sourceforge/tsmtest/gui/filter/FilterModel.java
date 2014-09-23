 /*******************************************************************************
 * Copyright (c) 2012-2013 Jenny Krüwald.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jenny Krüwald - initial version
 *    Tobias Hirning - code cleanup
 *******************************************************************************/ 
package net.sourceforge.tsmtest.gui.filter;

import java.util.Date;

import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;

/**
 * Loads all resources in the workspace when needed and holds them in its
 * singleton.
 * 
 * @author Jenny Krüwald
 */

public final class FilterModel {
    /**
     * Instance of the FilterModel.
     */
    private static volatile FilterModel instance;
    private boolean unassigned;
    private String name = "";
    private String creator = "";
    private Date lastExecution;
    private Date creationTime;
    private Date lastChange;
    private boolean high;
    private boolean medium;
    private boolean low;
    private boolean passed;
    private boolean passedWithAnnotation;
    private boolean failed;
    private boolean notExecuted;
    
    private final FilterManager filterManager = FilterManager.getInstance();

    /**
     * FilterModel singleton.
     * 
     * @return instance of FilterModel.
     */
    public static FilterModel getInstance() {
	if (instance == null) {
	    instance = new FilterModel();
	}
	return instance;
    }

    private FilterModel() {
    }

    public boolean isUnassigned() {
	return unassigned;
    }

    public String getName() {
	return name;
    }

    public String getCreator() {
	return creator;
    }

    public Date getLastExecution() {
	return lastExecution;
    }

    public Date getCreationTime() {
	return creationTime;
    }

    public Date getLastChange() {
	return lastChange;
    }

    public boolean isPriorityHigh() {
	return high;
    }

    public boolean isPriorityMedium() {
	return medium;
    }

    public boolean isPriorityLow() {
	return low;
    }

    public boolean isStatusPassed() {
	return passed;
    }

    public boolean isStatusPassedWithAnnotation() {
	return passedWithAnnotation;
    }

    public boolean isStatusFailed() {
	return failed;
    }

    public boolean isStatusNotExecuted() {
	return notExecuted;
    }

    /**
     * Whether to filter for a test case or a report.
     */
    private boolean filterForTestCases;

    /**
     * @return true if we want to filter for test cases, false if we want to filter for protocols.
     */
    public boolean filterForTestCases() {
	return filterForTestCases;
    }

    /**
     * @param priority
     * @param checked indicates if given priority is selected.
     */
    public void setPriority(PriorityType priority, boolean checked) {	
	switch (priority) {
	case high:
	    this.high = checked;
	    break;
	case medium:
	    this.medium = checked;
	    break;
	case low:
	    this.low = checked;
	    break;
	}
	filterManager.invoke();
    }

    /**
     * @param status
     * @param checked indicates if given status is selected.
     */
    public void setStatus(StatusType status, boolean checked) {
	switch (status) {
	case passed:
	    this.passed = checked;
	    break;
	case passedWithAnnotation:
	    this.passedWithAnnotation = checked;
	    break;
	case failed:
	    this.failed = checked;
	    break;
	case notExecuted:
	    this.notExecuted = checked;
	    break;
	}
	filterManager.invoke();
    }

    /**
     * @param checked indicates if filter "unassigned test case" is selected.
     */
    public void setUnassigned(boolean checked) {
	this.unassigned = checked;
	filterManager.invoke();
    }

    /**
     * @param name the name to be filtered for.
     */
    public void setName(String name) {
	this.name = name;
	filterManager.invoke();
    }

    /**
     * @param creatorName the name of the creator to be filtered for.
     */
    public void setCreator(String creatorName) {
	this.creator = creatorName;
	filterManager.invoke();
    }

    /**
     * @param lastExecution the date of the last execution to be filtered for.
     */
    public void setLastExecution(Date lastExecution) {
	this.lastExecution = lastExecution;
	filterManager.invoke();
    }

    /**
     * @param creationTime the creation time to be filtered for.
     */
    public void setCreationTime(Date creationTime) {
	this.creationTime = creationTime;
	filterManager.invoke();

    }

    /**
     * @param lastChangedDate the date of the last change to be filtered for.
     */
    public void setLastChange(Date lastChangedDate) {
	this.lastChange = lastChangedDate;
	filterManager.invoke();

    }

    /**
     * @param checked indicates if filter is set to "test cases".
     */
    public void setFilterForTestCases(boolean checked) {
	this.filterForTestCases = checked;
	filterManager.invoke();
    }

    /**
     * Reset all filter settings to default.
     */
    public void reset() {
	creationTime = null;
	creator = "";
	failed = false;
	high = false;
	lastChange = null;
	lastExecution = null;
	low = false;
	medium = false;
	name = "";
	notExecuted = false;
	passed = false;
	passedWithAnnotation = false;
	filterForTestCases = true;
	unassigned = false;
	
	//Propagate changes to model.
	filterManager.invoke();
    }
}
