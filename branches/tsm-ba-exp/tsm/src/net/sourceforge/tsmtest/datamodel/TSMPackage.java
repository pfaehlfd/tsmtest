/*******************************************************************************
 * Copyright (c) 2012-2013 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Tobias Hirning - initial version
 * 	Verena Käfer - added methods for protocol support, some refactoring
 * 	Albert Flaig - data model refactoring, some fixes
 * 	Wolfgang Kraus - data model refactoring
 *
 *******************************************************************************/
/**
 * This class provides temporary package objects for the view.
 */
package net.sourceforge.tsmtest.datamodel;

import java.util.Collection;


/**
 * @author Albert Flaig
 *
 */
public class TSMPackage extends TSMContainer {

    protected TSMPackage(String name, TSMContainer parent) {
	super(name, parent);
    }
    
    public static Collection<TSMPackage> list() {
	return DataModel.getInstance().getPackages();
    }

    /**
     * Getter for the category for TSMViewerComparator.
     * @return The categories of the TSMPackage.
     */
    public static int getCategory() {
	return DataModelTypes.CATEGORY_TSMPACKAGE;
    }

}
