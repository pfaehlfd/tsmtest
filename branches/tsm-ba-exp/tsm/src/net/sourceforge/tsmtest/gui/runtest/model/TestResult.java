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
    private StatusType state;
    private boolean updateTime;
    
    public String getDescription() {
	return description;
    }
    public void setDescription(String description) {
	this.description = description;
    }
    public StatusType getState() {
	return state;
    }
    public void setState(StatusType state) {
	this.state = state;
    }
    public boolean isUpdateTime() {
	return updateTime;
    }
    public void setUpdateTime(boolean updateTime) {
	this.updateTime = updateTime;
    }
    
}
