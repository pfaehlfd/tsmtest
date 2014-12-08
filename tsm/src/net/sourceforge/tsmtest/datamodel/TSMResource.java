/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Tobias Hirning - some refactoring
 * 	Wolfgang Kraus - added support for the graph
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel;

import net.sourceforge.tsmtest.gui.ResourceManager;

import org.eclipse.swt.graphics.Image;

/**
 * @author Albert Flaig
 *
 */
public abstract class TSMResource {

    private String name;
    private TSMContainer parent;

    protected TSMResource(final String name, final TSMContainer parent) {
	this.name = name;
	if (parent != null) {
	    parent.add(this);
	}
	this.parent = parent;
    }

    /**
     * @return the path to the resource relative to the workspace (e.g.
     *         "<i>Project</i>/<i>Package</i>")
     */
    public String getPath() {
	final TSMContainer parent = getParent();
	if (parent != null) {
	    return parent.getPath() + "/" + parent.getName();
	}
	return "";
    }

    /**
     * 
     * @return The parent or <code>null</code> if it's a Project
     */
    public TSMContainer getParent() {
	return parent;
    }

    /**
     * @return The TSMProject of this resource or null if it's not inside a
     *         TSMProject
     */
    public TSMProject getProject() {
	TSMContainer parent = getParent();
	while (parent != null) {
	    if (parent instanceof TSMProject) {
		return (TSMProject) parent;
	    }
	    parent = parent.getParent();
	}
	return null;
    }

    /**
     * @return The name of the resource.
     */
    public String getName() {
	return name;
    }

    public String getExtension() {
	return "";
    }

    public void rename(final String newName) throws DataModelException {
	DataModel.getInstance().rename(this, newName);
    }

    public void delete() throws DataModelException {
	DataModel.getInstance().delete(this);
    }

    protected void setName(final String newName) {
	name = newName;
    }

    public void move(final TSMContainer newContainer) {
	// FIXME Update in data model
	parent.remove(this);
	newContainer.add(this);
	parent = newContainer;
    }

    public boolean exists() {
	return DataModel.getInstance().exists(this);
    }

    public static Image getIcon() {
	return ResourceManager.getImgFile();
    }

    @Override
    public boolean equals(final Object obj) {
	if (obj instanceof TSMResource) {
	    final TSMResource other = (TSMResource) obj;
	    if (getName().equals(other.getName())) {
		if (getParent() == null && other.getParent() == null) {
		    return true;
		} else if (getParent() == null || other.getParent() == null) {
		    return false;
		} else {
		    return other.getParent().equals(getParent());
		}
	    }
	}
	return false;
    }

    @Override
    public int hashCode() {
	return (getName() + (parent != null ? parent.hashCode() : ""))
		.hashCode();
    }

}
