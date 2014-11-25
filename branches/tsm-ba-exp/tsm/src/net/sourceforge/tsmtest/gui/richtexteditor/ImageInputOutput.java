/*******************************************************************************
 * Copyright (c) 2012-2013 Daniel Hertl.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Hertl - initial version
 *    Albert Flaig - some fixes
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import java.io.IOException;
import java.util.ArrayList;

import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.io.vcs.settings.VCSSettings;
import net.sourceforge.tsmtest.io.vcs.svn.SubversionWrapper;
import net.sourceforge.tsmtest.Activator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

/**
 * This class provides a mechanism to safe Images which are added to testcases
 * into the workspace
 * 
 * @author Daniel Hertl
 * 
 */
public final class ImageInputOutput {
    private String[] imagePaths;

    private ImageData imageDataToSave;

    private final ArrayList<String> tempImagePaths = new ArrayList<String>();
    private final ArrayList<String> tempImageFilenames = new ArrayList<String>();

    public static ImageInputOutput createHandler(final String[] paths) {
	return new ImageInputOutput(paths);
    }

    public static ImageInputOutput createHandler(final ImageData img) {
	return new ImageInputOutput(img);
    }

    /**
     * Constructor
     * 
     * @param paths
     *            the paths of the files you want to save
     */
    private ImageInputOutput(final String[] paths) {
	imagePaths = paths;
    }

    /**
     * Constructor
     * 
     * @param paths
     *            the paths of the files you want to save
     */
    private ImageInputOutput(final ImageData img) {
	imageDataToSave = img;
    }

    /**
     * Saves Images to the given path
     * 
     * @param projectName
     *            the project under which the image is going to be saved.
     * @throws IOException
     * @return the relative path to the workspace root to the new image.
     */
    public void saveImage(final String projectName) throws IOException {
	ImageLoader il;
	il = new ImageLoader();
	String absolutePath;
	String relativePath;
	String filename;
	if (imageDataToSave != null) {
	    il.data = new ImageData[] { imageDataToSave };
	    filename = "Image_" + System.currentTimeMillis() + ".png";
	    relativePath = "/" + projectName + "/"
		    + DataModelTypes.imageFolderName + "/" + filename;
	    absolutePath = ResourcesPlugin.getWorkspace().getRoot()
		    .getLocation().toString()
		    + relativePath;
	    il.save(absolutePath, SWT.IMAGE_PNG);
	    if (VCSSettings.isSubversionSupportEnabled()) {
		SubversionWrapper.addForCommit(absolutePath);
		SubversionWrapper.commit(absolutePath);
	    }
	    tempImagePaths.add(absolutePath);
	    tempImageFilenames.add(filename);

	} else if (imagePaths != null) {
	    // String [] result = null;
	    for (int i = 0; i < imagePaths.length; i++) {
		// result = imagePaths[i].split(".");

		if (imagePaths[i].endsWith(".jpeg")
			|| imagePaths[i].endsWith(".jpg")) {

		    il.data = new ImageData[] { new ImageData(imagePaths[i]) };
		    filename = "Image_" + System.currentTimeMillis() + ".jpg";
		    relativePath = "/" + projectName + "/"
			    + DataModelTypes.imageFolderName + "/" + filename;
		    absolutePath = ResourcesPlugin.getWorkspace().getRoot()
			    .getLocation().toString()
			    + relativePath;
		    il.save(absolutePath, SWT.IMAGE_JPEG);
		    SubversionWrapper.addForCommit(absolutePath);
		    SubversionWrapper.commit(absolutePath);
		    tempImagePaths.add(absolutePath);
		    tempImageFilenames.add(filename);
		} else if (imagePaths[i].endsWith(".png")) {

		    il.data = new ImageData[] { new ImageData(imagePaths[i]) };
		    filename = "Image_" + System.currentTimeMillis() + ".png";
		    relativePath = "/" + projectName + "/"
			    + DataModelTypes.imageFolderName + "/" + filename;
		    absolutePath = ResourcesPlugin.getWorkspace().getRoot()
			    .getLocation().toString()
			    + relativePath;
		    il.save(absolutePath, SWT.IMAGE_PNG);
		    SubversionWrapper.addForCommit(absolutePath);
		    SubversionWrapper.commit(absolutePath);
		    tempImagePaths.add(absolutePath);
		    tempImageFilenames.add(filename);
		} else if (imagePaths[i].endsWith(".gif")) {

		    il.data = new ImageData[] { new ImageData(imagePaths[i]) };
		    filename = "Image_" + System.currentTimeMillis() + ".gif";
		    relativePath = "/" + projectName + "/"
			    + DataModelTypes.imageFolderName + "/" + filename;
		    absolutePath = ResourcesPlugin.getWorkspace().getRoot()
			    .getLocation().toString()
			    + relativePath;
		    il.save(absolutePath, SWT.IMAGE_GIF);
		    SubversionWrapper.addForCommit(absolutePath);
		    SubversionWrapper.commit(absolutePath);
		    tempImagePaths.add(absolutePath);
		    tempImageFilenames.add(filename);
		}
		// TODO Call the thing again in the paste method
		// TODO implement getSources

	    }
	}
    }

    /**
     * Gets the sources of the latest added images
     * 
     * @return Images sources
     */
    public String[] getTempImageSrc() {
	return tempImagePaths.toArray(new String[tempImagePaths.size()]);
    }

    /**
     * Gets the relative sources of the latest added images
     * 
     * @return Images sources
     */
    public String[] getTempImageFilenames() {
	final String[] tempSrc = tempImageFilenames
		.toArray(new String[tempImageFilenames.size()]);
	return tempSrc;
    }
}
