 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Verena Käfer - added lastChangedOn attribute
 *    Bernhard Wetzel - added revision numbers to execution
 *******************************************************************************/ 
package net.sourceforge.tsmtest.datamodel.descriptors;

import java.util.Date;
import java.util.List;

import net.sourceforge.tsmtest.datamodel.DataModelTypes.PriorityType;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;

/**
 * @author Albert Flaig
 *
 */
public interface ITestCaseDescriptor {
    public long getId();
    /**
     * Gets the tester for a test case.
     * @return The name of the assigned tester.
     */
    public String getAssignedTo();
    public String getAuthor();
    public Date getCreationDate();
    public String getExpectedDuration();
    public Date getLastExecution();
    public int getNumberOfExecutions();
    public int getNumberOfFailures();
    public PriorityType getPriority();
    public String getRealDuration();
    public String getRichTextPrecondition();
    public String getRichTextResult();
    public String getShortDescription();
    public StatusType getStatus();
    public List<TestStepDescriptor> getSteps();
    public Date getLastChangedOn();
    public int getRevisionNumber();
    public String getVersion();
}
