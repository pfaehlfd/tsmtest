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

import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.datamodel.TSMProject;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author Albert Flaig
 *
 */
public class TSMTooltipLabelProvider extends LabelProvider {
    public final static TSMTooltipLabelProvider DEFAULT = new TSMTooltipLabelProvider();

    @Override
    public String getText(final Object element) {
	if (element instanceof TSMTestCase) {
	    return "Test Case";
	} else if (element instanceof TSMReport) {
	    return "Report";
	} else if (element instanceof TSMProject) {
	    return "Project";
	} else if (element instanceof TSMPackage) {
	    return "Package";
	}
	return super.getText(element);
    }
}
