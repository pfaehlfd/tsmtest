/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Tobias Hirning - refactoring, i18n	
 * 	Bernhard Wetzel - added comments
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel;

import net.sourceforge.tsmtest.Messages;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Albert Flaig
 */
public abstract class MultiPageEditorPartInput extends MultiPageEditorPart {
    
    /**
     * Checks the given input for report or test case and opens the editor
     * 
     * @param site
     *          IEditorSite of the corresponding input
     * @param input
     * 		test case or report to be opened
     */
    @Override
    public void init(IEditorSite site, IEditorInput input)
	    throws PartInitException {
	TSMResource resource = null;
	if (input instanceof IFileEditorInput) {
	    IFile file = ((IFileEditorInput) input).getFile();
	    resource = DataModel.getInstance().convertToTSMResource(file);
	    setInputWithNotify(new ResourceEditorInput(resource));
	} else if (input instanceof ResourceEditorInput) {
	    resource = ((ResourceEditorInput) input).getInput();
	}
	// TODO Error message
	if(resource == null)
	    throw new PartInitException(Messages.MultiPageEditorPartInput_0);
	if (resource instanceof TSMTestCase)
	    getInput((TSMTestCase) resource);
	else if (resource instanceof TSMReport)
	    getInput((TSMReport) resource);
	else
	    throw new PartInitException(Messages.MultiPageEditorPartInput_1);
	setSite(site);
	setInput(input);
    }

    /**
     * @param input
     *            for this editor. Can be <code>null</code>.
     */
    protected abstract void getInput(TSMTestCase input);

    /**
     * @param input
     *            for this editor. Can be <code>null</code>.
     */
    protected abstract void getInput(TSMReport input);
    
    public abstract TSMTestCase getTestCaseInput();

    public abstract TSMReport getReportInput();
}
