 /*******************************************************************************
 * Copyright (c) 2012-2013 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Hirning - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.junit.datamodel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Tobias Hirning
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ TestCaseDescriptorTest.class, TestStepDescriptorTest.class, TestCaseTest.class })
public class AllTests {

}
