 /*******************************************************************************
 * Copyright (c) 2012-2013 Wolfgang Kraus and Bernhard Wetzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wolfgang Kraus - initial version
 *    Bernhard Wetzel - initial version
 *    Tobias Hirning - some refactoring
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.runtest.model;

import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;
/**
 * An Editor to execute a TestCaseObject
 * 
 * @author Wolfgang Kraus
 * @author Bernhard Wetzel
 * 
 */
public class TestResult {
    private String description;
    private StatusType status;
    private String time;
    private boolean updateTime;
    
    public String getDescription() {
	return description;
    }
    public void setDescription(String description) {
	this.description = description;
    }
    /**
     * @return The status of the test case execution.
     */
    public StatusType getStatus() {
	return status;
    }
    /**
     * @param status Sets the status of the test case execution.
     */
    public void setStatus(StatusType status) {
	this.status = status;
    }
    /**
     * @return True, if the estimated time of the test case should be updated.
     */
    public boolean isUpdateTime() {
	return updateTime;
    }
    /**
     * @param updateTime Whether the estimated time of the test case should be updated.
     */
    public void setUpdateTime(boolean updateTime) {
	this.updateTime = updateTime;
    }
    
    /**
     * @return The duration of the test case execution.
     */
    public String getDuration() {
	return time;
    }
    
    /**
     * @param newDuration The new duration of the test case execution.
     */
    public void setDuration(String newDuration) {
	time = newDuration;
    }
    
}
