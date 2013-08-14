/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Tobias Hirning - refactoring
 * 	Bernhard Wetzel - added comments
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Albert Flaig
 *
 */
public class ResourceEditorInput implements IEditorInput {

    protected TSMResource input;

    /**
     * Creates a <code>NullEditorInput</code> for the given editor reference.
     * 
     * @param editorReference
     *            the editor reference
     * @since 3.4
     */
    public ResourceEditorInput(TSMResource input) {
	this.input = input;
    }

    /**
     * returns the input of current instance
     * 
     * @return TSMResource
     */
    public TSMResource getInput() {
	return input;
    }

    /**
     * Always true
     */
    @Override
    public boolean exists() {
	return true;
    }

    /**
     * Returns the ImageDescriptor of a missing ImageDescriptor
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
	return ImageDescriptor.getMissingImageDescriptor();
    }

    /**
     * Returns the name of the current instance of input
     */
    @Override
    public String getName() {
	return input.getName();
    }

    /**
 * 
 */
    @Override
    public IPersistableElement getPersistable() {
	// FIXME
	return null;
    }

    /**
     * Returns empty String
     */
    @Override
    public String getToolTipText() {
	return "";
    }

    /**
     * Returns always null
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
	return null;
    }
}