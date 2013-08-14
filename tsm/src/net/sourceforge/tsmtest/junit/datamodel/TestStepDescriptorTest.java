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

import static org.junit.Assert.assertEquals;

import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;

import org.junit.Test;

/**
 * @author Tobias Hirning
 * 
 */
public class TestStepDescriptorTest {

    /**
     * Test method for
     * {@link net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor#getRichTextDescription()}
     * .
     */
    @Test
    public final void testGetRichTextDescription() {
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();

	assertEquals("", testTestStepDescriptor.getRichTextDescription());
	testTestStepDescriptor.setRichTextDescription("äöüßÄÖÜ xy");
	assertEquals("äöüßÄÖÜ xy",
		testTestStepDescriptor.getRichTextDescription());
    }

    /**
     * Test method for
     * {@link net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor#setRichTextDescription(java.lang.String)}
     * .
     */
    @Test
    public final void testSetRichTextDescription() {
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();

	testTestStepDescriptor.setRichTextDescription("äöüßÄÖÜ xy");
	assertEquals("äöüßÄÖÜ xy",
		testTestStepDescriptor.getRichTextDescription());
    }

    /**
     * Test method for
     * {@link net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor#getExpectedResult()}
     * .
     */
    @Test
    public final void testGetExpectedResult() {
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();

	assertEquals("", testTestStepDescriptor.getExpectedResult());
	testTestStepDescriptor.setExpectedResult("äöüßÄÖÜ xy");
	assertEquals("äöüßÄÖÜ xy", testTestStepDescriptor.getExpectedResult());
    }

    /**
     * Test method for
     * {@link net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor#setExpectedResult(java.lang.String)}
     * .
     */
    @Test
    public final void testSetExpectedResult() {
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();

	testTestStepDescriptor.setExpectedResult("äöüßÄÖÜ xy");
	assertEquals("äöüßÄÖÜ xy", testTestStepDescriptor.getExpectedResult());
    }

    /**
     * Test method for
     * {@link net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor#getRealResult()}
     * .
     */
    @Test
    public final void testGetRealResult() {
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();

	assertEquals("", testTestStepDescriptor.getRealResult());
	testTestStepDescriptor.setRealResult("äöüßÄÖÜ xy");
	assertEquals("äöüßÄÖÜ xy", testTestStepDescriptor.getRealResult());
    }

    /**
     * Test method for
     * {@link net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor#setRealResult(java.lang.String)}
     * .
     */
    @Test
    public final void testSetRealResult() {
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();

	testTestStepDescriptor.setRealResult("äöüßÄÖÜ xy");
	assertEquals("äöüßÄÖÜ xy", testTestStepDescriptor.getRealResult());
    }

    /**
     * Test method for
     * {@link net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor#getStatus()}
     * .
     */
    @Test
    public final void testGetStatus() {
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();

	assertEquals(DataModelTypes.StatusType.notExecuted,
		testTestStepDescriptor.getStatus());
    }

    /**
     * Test method for
     * {@link net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor#setStatus(net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType)}
     * .
     */
    @Test
    public final void testSetStatus() {
	TestStepDescriptor testTestStepDescriptor = new TestStepDescriptor();

	assertEquals(DataModelTypes.StatusType.notExecuted,
		testTestStepDescriptor.getStatus());
	testTestStepDescriptor.setStatus(DataModelTypes.StatusType.failed);
	assertEquals(DataModelTypes.StatusType.failed,
		testTestStepDescriptor.getStatus());
	testTestStepDescriptor.setStatus(DataModelTypes.StatusType.passed);
	assertEquals(DataModelTypes.StatusType.passed,
		testTestStepDescriptor.getStatus());
	testTestStepDescriptor
		.setStatus(DataModelTypes.StatusType.passedWithAnnotation);
	assertEquals(DataModelTypes.StatusType.passedWithAnnotation,
		testTestStepDescriptor.getStatus());
	testTestStepDescriptor.setStatus(DataModelTypes.StatusType.notExecuted);
	assertEquals(DataModelTypes.StatusType.notExecuted,
		testTestStepDescriptor.getStatus());
    }

    /**
     * Test method for
     * {@link net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor#clone()}.
     */
    @Test
    public final void testClone() {
	// TODO
    }

}
