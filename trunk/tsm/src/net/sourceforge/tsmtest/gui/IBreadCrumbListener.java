 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.gui;

import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;

/**
 * @author Albert Flaig
 *
 */
public interface IBreadCrumbListener {
    /**
     * @param report
     * @return Whether the event should be consumed.
     */
    public boolean selectionChanged(TSMReport report);
    /**
     * @param testCase
     * @return Whether the event should be consumed.
     */
    public boolean selectionChanged(TSMTestCase testCase);
}
