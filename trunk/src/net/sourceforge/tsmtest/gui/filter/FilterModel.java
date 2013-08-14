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

public class FilterModel {

    private static volatile FilterModel instance;

    /**
     * creating new filtermodel if none exists
     * 
     * @return instance of filtermodel
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

    public boolean isHigh() {
	return high;
    }

    public boolean isMedium() {
	return medium;
    }

    public boolean isLow() {
	return low;
    }

    public boolean isPassed() {
	return passed;
    }

    public boolean isPassedWithAnnotation() {
	return passedWithAnnotation;
    }

    public boolean isFailed() {
	return failed;
    }

    public boolean isNotExecuted() {
	return notExecuted;
    }

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
    /**
     * Whether to search for a test case or a report.
     */
    private boolean testCases;

    public boolean isTestCases() {
	return testCases;
    }

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
	FilterManager.instance.invoke();
    }

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
	FilterManager.instance.invoke();
    }

    public void setUnassigned(boolean checked) {
	this.unassigned = checked;
	FilterManager.instance.invoke();
    }

    public void setName(String text) {
	this.name = text;
	FilterManager.instance.invoke();
    }

    public void setCreator(String text) {
	this.creator = text;
	FilterManager.instance.invoke();
    }

    public void setLastExecution(Date time) {
	this.lastExecution = time;
	FilterManager.instance.invoke();
    }

    public void setCreationTime(Date time) {
	this.creationTime = time;
	FilterManager.instance.invoke();

    }

    public void setLastChange(Date time) {
	this.lastChange = time;
	FilterManager.instance.invoke();

    }

    public void setTestCases(boolean b) {
	this.testCases = b;
	FilterManager.instance.invoke();
    }

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
	testCases = true;
	unassigned = false;
	FilterManager.instance.invoke();
    }
}
