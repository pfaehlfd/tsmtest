/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Albert Flaig - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel.descriptors;

import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;

/**
 * @author Albert Flaig
 *
 */
public interface ITestStepDescriptor {
    /**
     * @return The action as a rich text that the tester should execute.
     */
    public String getExpectedResult();
    /**
     * @return The real result that was entered by the tester during the execution.
     */
    public String getRealResult();
    /**
     * @return The rich text description of the test step.
     */
    public String getActionRichText();
    /**
     * @return The status of the test step.
     */
    /**
     * Gets the status of the test step.
     * @return The status of the test step.
     */
    public StatusType getStatus();
}
