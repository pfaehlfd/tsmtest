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

import net.sourceforge.tsmtest.datamodel.DataModelTypes;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.w3c.dom.Node;

/**
 * @author Albert Flaig
 *
 */
public class TSMViewerComparator extends ViewerComparator {
    public final static TSMViewerComparator DEFAULT = new TSMViewerComparator();

    @Override
    public int category(final Object element) {
	return DataModelTypes.getCategory(element);
    }
    
    @Override
    public int compare(final Viewer viewer, final Object e1, final Object e2) {
	// compare categories
	final int cat1 = category(e1);
	final int cat2 = category(e2);
	if (cat1 != cat2) {
	    return cat1 - cat2;
	}

	// compare names
	if (e1 instanceof Node && e2 instanceof Node) {
	    final Node node1 = (Node) e1;
	    final Node node2 = (Node) e2;
	    if (node1.equals(node2)) {
		return 0;
	    }
	    return node1.getNodeName().compareToIgnoreCase(node2.getNodeName());
	}

	// default
	return super.compare(viewer, e1, e2);
    }
}
