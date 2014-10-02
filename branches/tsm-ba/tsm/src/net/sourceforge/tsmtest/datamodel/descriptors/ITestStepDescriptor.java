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
     * Gets the expected result of the test step.
     * @return The expected result of the test step.
     */
    public String getExpectedResult();
    /**
     * Gets the real result of the test step.
     * @return The real result of the test step.
     */
    public String getRealResult();
    /**
     * Gets the rich text description of the test step.
     * @return The description of the test step as rich text.
     */
    public String getRichTextDescription();
    /**
     * Gets the status of the test step.
     * @return The status of the test step.
     */
    public StatusType getStatus();
}
