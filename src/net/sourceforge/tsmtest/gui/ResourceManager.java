 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Tobias Hirning - some refactoring, i18n
 *    Florian Krüger - some enhancements
 *    Jenny Krüwald - added other icon sizes
 *******************************************************************************/
package net.sourceforge.tsmtest.gui;

import java.net.URL;

import net.sourceforge.tsmtest.Activator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

/**
 * @author Albert Flaig
 *
 */
public class ResourceManager {
    private static Image imgGreen;
    private static Image imgRed;
    private static Image imgOrange;
    private static Image imgGray;
    private static Image imgAccept;
    private static Image imgInformation;
    private static Image imgCross;
    private static Image imgLeft;
    private static Image imgRight;
    private static Image imgDown;
    private static Image imgTSMTestCase;
    private static Image imgTSMReport;
    private static Image imgTSMReportGreen;
    private static Image imgTSMReportYellow;
    private static Image imgTSMReportRed;
    private static Image imgTSMReportGray;
    private static Image imgTSMPackage;
    private static Image imgTSMProject;
    private static Image imgProject;
    private static Image imgFolder;
    private static Image imgFile;
    private static Image imgQuickview;
    private static Image imgPause;
    private static Image imgStart;
    private static Image imgArrowUp;
    private static Image imgArrowDown;
    private static Image imgCrossGrey;
    private static Image imgFullResize;
    private static Image imgFullResizeHover;

    public static URL getURL(final String path) {
	final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
	return FileLocator.find(bundle, new Path(path), null);
    }

    public static Image getImgGreen() {
	if (imgGreen == null) {
	    imgGreen = Activator.getImageDescriptor(getPathGreen())
		    .createImage();
	}
	return imgGreen;
    }

    public static Image getImgRed() {
	if (imgRed == null) {
	    imgRed = Activator.getImageDescriptor(getPathRed()).createImage();
	}
	return imgRed;
    }

    public static Image getImgOrange() {
	if (imgOrange == null) {
	    imgOrange = Activator.getImageDescriptor(getPathOrange())
		    .createImage();
	}
	return imgOrange;
    }

    public static Image getImgGray() {
	if (imgGray == null) {
	    imgGray = Activator.getImageDescriptor(getPathGray()).createImage();
	}
	return imgGray;
    }

    public static Image getImgAccept() {
	if (imgAccept == null) {
	    imgAccept = Activator.getImageDescriptor(getPathAccept())
		    .createImage();
	}
	return imgAccept;
    }

    public static Image getImgInformation() {
	if (imgInformation == null) {
	    imgInformation = Activator.getImageDescriptor(getPathInformation())
		    .createImage();
	}
	return imgInformation;
    }

    public static Image getImgCross() {
	if (imgCross == null) {
	    imgCross = Activator.getImageDescriptor(getPathCross())
		    .createImage();
	}
	return imgCross;
    }

    public static Image getImgLeft() {
	if (imgLeft == null) {
	    imgLeft = Activator.getImageDescriptor(getPathLeft()).createImage();
	}
	return imgLeft;
    }

    public static Image getImgRight() {
	if (imgRight == null) {
	    imgRight = Activator.getImageDescriptor(getPathRight())
		    .createImage();
	}
	return imgRight;
    }

    public static Image getImgDown() {
	if (imgDown == null) {
	    imgDown = Activator.getImageDescriptor(getPathDown()).createImage();
	}
	return imgDown;
    }

    public static Image getImgFile() {
	if (imgFile == null) {
	    imgFile = Activator.getImageDescriptor(getPathFile()).createImage();
	}
	return imgFile;
    }

    public static Image getImgFolder() {
	if (imgFolder == null) {
	    imgFolder = Activator.getImageDescriptor(getPathFolder())
		    .createImage();
	}
	return imgFolder;
    }

    public static Image getImgProject() {
	if (imgProject == null) {
	    imgProject = Activator.getImageDescriptor(getPathProject())
		    .createImage();
	}
	return imgProject;
    }

    public static Image getImgTSMReport() {
	if (imgTSMReport == null) {
	    imgTSMReport = Activator.getImageDescriptor(getPathTSMReport())
		    .createImage();
	}
	return imgTSMReport;
    }

    public static Image getImgTSMReportGreen() {
	if (imgTSMReportGreen == null) {
	    imgTSMReportGreen = Activator.getImageDescriptor(
		    getPathTSMReportGreen()).createImage();
	}
	return imgTSMReportGreen;
    }

    public static Image getImgTSMReportYellow() {
	if (imgTSMReportYellow == null) {
	    imgTSMReportYellow = Activator.getImageDescriptor(
		    getPathTSMReportYellow()).createImage();
	}
	return imgTSMReportYellow;
    }

    public static Image getImgTSMReportRed() {
	if (imgTSMReportRed == null) {
	    imgTSMReportRed = Activator.getImageDescriptor(
		    getPathTSMReportRed()).createImage();
	}
	return imgTSMReportRed;
    }

    public static Image getImgTSMReportGray() {
	if (imgTSMReportGray == null) {
	    imgTSMReportGray = Activator.getImageDescriptor(
		    getPathTSMReportGray()).createImage();
	}
	return imgTSMReportGray;
    }

    public static Image getImgTSMProject() {
	if (imgTSMProject == null) {
	    imgTSMProject = Activator.getImageDescriptor(getPathTSMProject())
		    .createImage();
	}
	return imgTSMProject;
    }

    public static Image getImgTSMPackage() {
	if (imgTSMPackage == null) {
	    imgTSMPackage = Activator.getImageDescriptor(getPathTSMPackage())
		    .createImage();
	}
	return imgTSMPackage;
    }

    public static Image getImgTSMTestCase() {
	if (imgTSMTestCase == null) {
	    imgTSMTestCase = Activator.getImageDescriptor(getPathTSMTestCase())
		    .createImage();
	}
	return imgTSMTestCase;
    }

    public static Image getImgQuickview() {
	if (imgQuickview == null) {
	    imgQuickview = Activator.getImageDescriptor(getPathQuickview())
		    .createImage();
	}
	return imgQuickview;
    }

    public static Image getImgPause() {
	if (imgPause == null) {
	    imgPause = Activator.getImageDescriptor(getPathPause())
		    .createImage();
	}
	return imgPause;
    }

    public static Image getImgStart() {
	if (imgStart == null) {
	    imgStart = Activator.getImageDescriptor(getPathStart())
		    .createImage();
	}
	return imgStart;
    }

    public static Image getImgArrowUp() {
	if (imgArrowUp == null) {
	    imgArrowUp = Activator.getImageDescriptor(getPathArrowUp())
		    .createImage();
	}
	return imgArrowUp;
    }

    public static Image getImgArrowDown() {
	if (imgArrowDown == null) {
	    imgArrowDown = Activator.getImageDescriptor(getPathArrowDown())
		    .createImage();
	}
	return imgArrowDown;
    }

    public static Image getImgCrossGrey() {
	if (imgCrossGrey == null) {
	    imgCrossGrey = Activator.getImageDescriptor(getPathCrossGrey())
		    .createImage();
	}
	return imgCrossGrey;
    }

    public static Image getImgFullResize() {
	if (imgFullResize == null) {
	    imgFullResize = Activator.getImageDescriptor(getFullResize())
		    .createImage();
	}
	return imgFullResize;
    }

    public static Image getImgFullResizeHover() {
	if (imgFullResizeHover == null) {
	    imgFullResizeHover = Activator.getImageDescriptor(
		    getFullResizeHover()).createImage();
	}
	return imgFullResizeHover;
    }

    // ------------ Paths -----------------

    public static String getFullResize() {
	return "icons/fullResize.png";
    }

    public static String getFullResizeHover() {
	return "icons/fullResizeHover.png";
    }

    public static String getPathGreen() {
	return "icons/green.png";
    }

    public static String getPathRed() {
	return "icons/red.png";
    }

    public static String getPathOrange() {
	return "icons/orange.png";
    }

    public static String getPathGray() {
	return "icons/gray.png";
    }

    public static String getPathAccept() {
	return "icons/accept.png";
    }

    public static String getPathInformation() {
	return "icons/information.png";
    }

    public static String getPathCross() {
	return "icons/cross.png";
    }

    public static String getPathLeft() {
	return "icons/arrow_left.png";
    }

    public static String getPathRight() {
	return "icons/arrow_right.png";
    }

    public static String getPathDown() {
	return "icons/arrow_down.png";
    }

    public static String getPathFile() {
	return "icons/testcase_TSM.png";
    }

    public static String getPathFolder() {
	return "icons/folder_default.png";
    }

    public static String getPathProject() {
	return "icons/project_TSM.png";
    }

    public static String getPathTSMPackage() {
	return "icons/package.png";
    }

    public static String getPathTSMProject() {
	// TODO
	return getPathProject();
    }

    public static String getPathTSMTestCase() {
	return "icons/testcase_TSM.png";
    }

    public static String getPathTSMReport() {
	return "icons/report.png";
    }

    public static String getPathTSMReportGreen() {
	return "icons/reportGreen.png";
    }

    public static String getPathTSMReportYellow() {
	return "icons/reportYellow.png";
    }

    public static String getPathTSMReportRed() {
	return "icons/reportRed.png";
    }

    public static String getPathTSMReportGray() {
	return "icons/reportGray.png";
    }

    public static String getPathQuickview() {
	return "icons/quickview.png";
    }

    public static String getPathPause() {
	return "icons/pause.png";
    }

    public static String getPathStart() {
	return "icons/start.png";
    }

    public static String getPathArrowUp() {
	return "icons/arrow-up.png";
    }

    public static String getPathArrowDown() {
	return "icons/arrow-down.png";
    }

    public static String getPathCrossGrey() {
	return "icons/cross-grey.png";
    }
}
