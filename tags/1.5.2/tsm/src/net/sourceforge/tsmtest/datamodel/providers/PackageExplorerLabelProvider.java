/*******************************************************************************
 * Copyright (c) 2012-2013 Wolfgang Kraus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Wolfgang Kraus - initial version
 * 	Tobias Hirning - fixed a IndexOutOfBounds exception
 * 	Albert Flaig - data model cleanup and custom navigator
 * 	Verena Käfer - i18n
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel.providers;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.datamodel.TSMProject;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.gui.ResourceManager;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Albert Flaig
 *
 */
public class PackageExplorerLabelProvider extends LabelProvider implements
	ILabelDecorator {
    public static final String ID = "net.sourceforge.tsmtest.datamodel.providers.PackageExplorerLabelProvider"; //$NON-NLS-1$

    @Override
    public Image decorateImage(final Image image, final Object element) {
	if (element == null) {
	    return image;
	}
	final TSMResource res = DataModel.getInstance().convertToTSMResource(
		element);
	if (res instanceof TSMTestCase) {
	    return ResourceManager.getImgTSMTestCase();
	} else if (res instanceof TSMReport) {
	    return ResourceManager.getImgTSMReport();
	} else if (res instanceof TSMPackage) {
	    return ResourceManager.getImgTSMPackage();
	} else if (res instanceof TSMProject) {
	    return ResourceManager.getImgTSMProject();
	}
	return image;
    }

    @Override
    public String decorateText(final String text, final Object element) {
	final TSMResource res = DataModel.getInstance().convertToTSMResource(
		element);
	if (res instanceof TSMTestCase) {
	    return res.getName() + Messages.PackageExplorerLabelProvider_1;
	}
	if (res instanceof TSMReport) {
	    final TSMReport report = (TSMReport) res;
	    return report.getName() + Messages.PackageExplorerLabelProvider_2
		    + report.getData().getRevisionNumber();
	    // + " (" + report.getData().getLastExecution() + ")";
	}
	return text;
    }
}
