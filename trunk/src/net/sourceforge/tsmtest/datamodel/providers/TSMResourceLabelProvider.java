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
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.gui.ResourceManager;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Albert Flaig
 *
 */
public class TSMResourceLabelProvider extends ColumnLabelProvider {
    public final static TSMResourceLabelProvider DEFAULT = new TSMResourceLabelProvider();

    @Override
    public Image getImage(final Object element) {
	if (element == null) {
	    return null;
	} else if (element instanceof TSMTestCase) {
	    return ResourceManager.getImgTSMTestCase();
	} else if (element instanceof TSMReport) {
	    switch (((TSMReport) element).getData().getStatus()) {
	    case failed:
		return ResourceManager.getImgTSMReportRed();
	    case passed:
		return ResourceManager.getImgTSMReportGreen();
	    case passedWithAnnotation:
		return ResourceManager.getImgTSMReportYellow();
	    case notExecuted:
		return ResourceManager.getImgTSMReportGray();
	    }
	    return ResourceManager.getImgTSMReport();
	} else if (element instanceof TSMPackage) {
	    return ResourceManager.getImgTSMPackage();
	} else if (element instanceof TSMProject) {
	    return ResourceManager.getImgTSMProject();
	} else {
	    return ResourceManager.getImgFile();
	}
    }

    @Override
    public String getText(final Object element) {
	if (element instanceof TSMResource) {
	    return ((TSMResource) element).getName();
	}
	return super.getText(element);
    }

}
