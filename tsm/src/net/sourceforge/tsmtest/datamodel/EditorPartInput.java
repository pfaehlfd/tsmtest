/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Tobias Hirning - i18n
 * 	Bernhard Wetzel - added comments
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.SWTUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Albert Flaig
 */
public abstract class EditorPartInput extends EditorPart {

    /**
     * Initializes the editors and opens them with the given test case or report
     * input
     */
    @Override
    public void init(final IEditorSite site, final IEditorInput input)
	    throws PartInitException {
	TSMResource resource = null;
	if (input instanceof IFileEditorInput) {
	    final IFile file = ((IFileEditorInput) input).getFile();
	    resource = DataModel.getInstance().convertToTSMResource(file);
	} else if (input instanceof ResourceEditorInput) {
	    resource = ((ResourceEditorInput) input).getInput();
	}
	// TODO Error message
	if (resource == null) {
	    throw new PartInitException(Messages.EditorPartInput_0);
	}
	if (resource instanceof TSMTestCase) {
	    final TSMTestCase testCase = (TSMTestCase) resource;
	    if (SWTUtils.findOpenEditor(testCase) != null) {
		throw new PartInitException(Messages.EditorPartInput_1);
	    }
	    initInput(testCase);
	} else if (resource instanceof TSMReport) {
	    final TSMReport report = (TSMReport) resource;
	    if (SWTUtils.findOpenEditor(report) != null) {
		throw new PartInitException(Messages.EditorPartInput_2);
	    }
	    initInput(report);
	} else {
	    throw new PartInitException(Messages.EditorPartInput_3);
	}
	setSite(site);
	setInput(input);
    }

    /**
     * @param input
     *            for this editor. Can be <code>null</code>.
     */
    protected abstract void initInput(TSMTestCase input);

    /**
     * @param input
     *            for this editor. Can be <code>null</code>.
     */
    protected abstract void initInput(TSMReport input);

    /**
     * @return input of the test case
     */
    public abstract TSMTestCase getTestCaseInput();

    /**
     * @return input of the report
     */
    public abstract TSMReport getReportInput();
}
