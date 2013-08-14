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
    public String getExpectedResult();
    public String getRealResult();
    public String getRichTextDescription();
    public StatusType getStatus();
}
