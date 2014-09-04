/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel.providers;

import java.util.Collection;

import net.sourceforge.tsmtest.datamodel.TSMProject;

/**
 * @author Albert Flaig
 *
 */
public final class TSMRootElement {

    public static TSMRootElement getInstance() {
	return new TSMRootElement();
    }
    
    private TSMRootElement() {
	
    }

    public Object getParent() {
	return null;
    }
    
    public Collection<TSMProject> getChildren() {
	return TSMProject.list();
    }
}
