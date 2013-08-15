/*******************************************************************************
 * Copyright (c) 2012-2013 Florian Krüger.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Florian Krüger - initial version
 *    Wolfgang Kraus - some fixes
 *    Verena Käfer - some fixes
 *    Albert Flaig - added image name to xml
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.gui.ResourceManager;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Image Class is used to visualize images at the TsmStyledText Editor
 * 
 * @author Florian Krüger
 * 
 */
public class TsmStyledTextImage {

    private static final int MARGIN_RIGHT = 10;
    private Image image;
    private int offset;
    /**
     * The relative path to the image relative to the workspace root.
     */
    private final String imageFilename;
    private Rectangle bounds;
    private boolean paintResizeMode;
    private Rectangle resizeBounds;
    private boolean overResize;
    private boolean resizeMode;
    private Point resizePoint;
    private float scale = 0;
    private StyleRange imageStyle;
    private float minScale;
    private float maxScale;
    private Rectangle fillSizeBounds;
    private boolean overFillSize;
    private boolean fullSizeMode;
    private boolean generateFullSizeMode;
    private float saveScale;
    private boolean chanceFactor;
    private boolean grabMaximumSize;
    private boolean maxResizeFlag;
    private final String project;

    public String getProject() {
	return project;
    }

    public void dispose() {
	image.dispose();
    }

    /**
     * Constructs the class with an image and the source path to the image
     * 
     * @param image
     * @param project
     *            The TSMProject this image is saved in
     * @param imageFilename
     */
    public TsmStyledTextImage(final Image image, final String project,
	    final String imageFilename) {
	this(image, project, imageFilename, 0);
    }

    /**
     * Constructs the class with an image and the source path to the image and
     * an offset
     * 
     * @param image
     * @param project
     *            The TSMProject this image is saved in
     * @param imageFilename
     * @param offset
     */
    public TsmStyledTextImage(final Image image, final String project,
	    final String imageFilename, final int offset) {
	this(image, project, imageFilename, offset, 0);
    }

    /**
     * Constructs the class with an image and the source path to the image and
     * an offset and a scale of the image
     * 
     * @param image
     * @param project
     *            The TSMProject this image is saved in
     * @param imageFilename
     * @param offset
     * @param scale2
     */
    public TsmStyledTextImage(final Image image, final String project,
	    final String imageFilename, final int offset, final float scale2) {
	this.image = image;
	this.imageFilename = imageFilename;
	this.offset = offset;
	this.project = project;
	setScaleFactor(scale2);
	createStyle();
    }

    /**
     * Sets the offset of the image
     * 
     * @param offset
     */
    public void setOffset(final int offset) {
	this.offset = offset;
	imageStyle.start = offset;
    }

    /**
     * @return the absolute path where this image is stored at.
     */
    public String getSrc() {
	return ResourcesPlugin.getWorkspace().getRoot().getLocation() + "/"
		+ project + "/" + DataModelTypes.imageFolderName + "/"
		+ imageFilename;
    }

    /**
     * @return the filename of the source
     */
    public String getFilename() {
	return imageFilename;
    }

    /**
     * get current offset of the image
     * 
     * @return
     */
    public int getOffset() {
	return offset;
    }

    /**
     * get the image
     * 
     * @return
     */
    public Image getImage() {
	return image;
    }

    public Rectangle getBounds() {
	return bounds;
    }

    /**
     * Draws an Image into the styled text component
     * 
     * @param event
     */
    public void paintImage(final PaintObjectEvent event) {

	if (maxResizeFlag) {
	    int verticalBar = 10;
	    if (((TsmStyledText) event.getSource()).getVerticalBar() != null) {
		verticalBar += ((TsmStyledText) event.getSource())
			.getVerticalBar().getSize().x;
	    }
	    scale = (float) (((TsmStyledText) event.getSource()).getBounds().width
		    - verticalBar - MARGIN_RIGHT)
		    / image.getBounds().width;
	    if (scale > 1.0) {
		scale = 1;
	    }
	    maxResizeFlag = false;
	    fullSizeMode = true;
	    chanceFactor = true;
	}
	final StyleRange style = event.style;
	// System.out.println(style.start+" _-_-  "+offset);
	if (style.start == offset) {
	    final int x = event.x;
	    final int y = event.y + event.ascent - style.metrics.ascent;

	    if (scale == 0) {
		// initialize some values to calculate scale factor
		scale = (float) (RichText.INITIAL_WIDTH_IMAGE)
			/ image.getBounds().width;
		minScale = (float) RichText.MIN_WIDTH_IMAGE
			/ image.getBounds().width;
		if (minScale < scale) {
		    minScale = scale;
		}
		maxScale = (float) RichText.MAX_WIDTH_IMAGE
			/ image.getBounds().width;

	    }

	    if (isResizeOn() && resizePoint != null) {
		// Resize Image - calculate scale factor
		if (image.getBounds().width > RichText.INITIAL_WIDTH_IMAGE) {
		    scale = (float) (resizePoint.x - event.x)
			    / image.getBounds().width;
		}
		if (scale < minScale) {
		    scale = minScale;
		}
		if (scale > maxScale) {
		    scale = maxScale;
		}
	    }

	    // don't draw over the border
	    int verticalBar = 0;
	    if (((TsmStyledText) event.getSource()).getVerticalBar() != null) {
		verticalBar = ((TsmStyledText) event.getSource())
			.getVerticalBar().getSize().x;
	    }
	    if (x + image.getBounds().width * scale > ((TsmStyledText) event
		    .getSource()).getBounds().width
		    - MARGIN_RIGHT
		    - verticalBar) {
		final float tempscale = (float) (((TsmStyledText) event
			.getSource()).getBounds().width - verticalBar - MARGIN_RIGHT)
			/ image.getBounds().width;
		if (tempscale < scale) {
		    scale = tempscale;
		}
	    }

	    // calculate bounds
	    bounds = new Rectangle(x, y,
		    (int) (image.getBounds().width * scale),
		    (int) (image.getBounds().height * scale));

	    if (isResizeOn() || chanceFactor == true) {
		imageStyle.metrics = new GlyphMetrics((bounds.height), 0,
			(bounds.width));
		chanceFactor = false;
	    }

	    // draw image
	    // System.out.println("draw Image !");
	    event.gc.drawImage(image, 0, 0, image.getBounds().width,
		    image.getBounds().height, x, y, bounds.width, bounds.height);

	    // paint Resize Icon
	    if (paintResizeMode && !(fullSizeMode && grabMaximumSize)) {
		resizeBounds = new Rectangle(x + bounds.width - 9, y
			+ bounds.height - 9, 10, 10);
		Color c = new Color(image.getDevice(), 255, 255, 255);
		event.gc.setForeground(c);
		c.dispose();
		event.gc.fillRectangle(resizeBounds);
		c = new Color(image.getDevice(), 0, 0, 0);
		event.gc.setForeground(c);
		c.dispose();
		event.gc.drawRectangle(resizeBounds);

	    }
	    if (paintResizeMode) {
		fillSizeBounds = new Rectangle(x + bounds.width - 14, y - 1,
			14, 14);
		if (fullSizeMode) {
		    event.gc.drawImage(ResourceManager.getImgFullResizeHover(),
			    fillSizeBounds.x + 1, fillSizeBounds.y + 1);
		} else {
		    event.gc.drawImage(ResourceManager.getImgFullResize(),
			    fillSizeBounds.x + 1, fillSizeBounds.y + 1);
		}
		if (getOverFillSize()) {
		    event.gc.drawRectangle(fillSizeBounds);
		}
	    }
	}
    }

    /**
     * Sets the image resize mode. In this mode the image will continued drew in
     * the new size
     * 
     * @param b
     */
    public void paintResizeMode(final boolean b) {
	paintResizeMode = b;
    }

    /**
     * Gets the full size of the image element
     * 
     * @return
     */
    public Rectangle getFullSizeBounds() {
	return fillSizeBounds;
    }

    /**
     * Returns the bounds of the resize picker
     * 
     * @return the rectangle
     */
    public Rectangle getResizeBounds() {
	return resizeBounds;
    }

    /**
     * Set true if the mouse lies over the resize picker
     * 
     * @param b
     */
    public void setOverResize(final boolean b) {
	overResize = b;
    }

    /**
     * gets the over resize mode. If the overResize is true the mouse is focused
     * on the resize symbol
     * 
     * @return overSize
     */
    public boolean getOverResize() {
	return overResize;
    }

    /**
     * Sets true if the mouse lies over the fill size picker
     * 
     * @param b
     */
    public void setOverFillSize(final boolean b) {
	overFillSize = b;
    }

    /**
     * Gets the overFillSize mode. If the mouse is focused on the fillSize
     * Symbol overFillSize is true
     * 
     * @return overFillSize
     */
    public boolean getOverFillSize() {
	return overFillSize;
    }

    /**
     * Checks whether the resizeMode is enabled
     * 
     * @return resizeMode
     */
    public boolean isResizeOn() {
	return resizeMode;
    }

    /**
     * Checks whether the fullSizeMode is enabled
     * 
     * @return fullSizeMode
     */
    public boolean isFullsizeOn() {
	return fullSizeMode;
    }

    /**
     * Start to recognize Mouse Movements
     */
    public void startResize() {
	resizeMode = true;
    }

    /**
     * Stop to recognize Mouse Movements
     */
    public void stopResize() {
	resizeMode = false;
    }

    /**
     * Set the Point of the mouse to resize the image
     * 
     * @param point
     */
    public void setResizePoint(final Point point) {
	resizePoint = point;
    }

    /**
     * Creates the style for the image
     * 
     * @param style
     */
    private void createStyle() {
	final StyleRange style = new StyleRange();
	style.start = getOffset();
	style.length = 1;
	final Rectangle rect = image.getBounds();

	final float scale = getScaleFactor();
	style.metrics = new GlyphMetrics((int) (rect.height * scale), 0,
		(int) (rect.width * scale));
	imageStyle = style;
    }

    /**
     * Get Scale Factor
     * 
     * @return
     */
    public float getScaleFactor() {
	if (scale == 0) {
	    return (float) (RichText.INITIAL_WIDTH_IMAGE)
		    / image.getBounds().width;
	}
	return scale;
    }

    /**
     * Set Scale Factor
     * 
     * @param scale
     */
    public void setScaleFactor(final float scale) {
	this.scale = scale;
    }

    /**
     * returns true if the program tries to chance image to full size mode
     * 
     * @return
     */
    public boolean isGeneratingFullSizeMode() {
	return generateFullSizeMode;
    }

    /**
     * Image craps existing space
     * 
     * @param grabMaximumSize
     * @param b
     */
    public void doFullSize(final TsmStyledText styledText,
	    final boolean grabMaximumSize) {
	this.grabMaximumSize = grabMaximumSize;
	if (fullSizeMode == false) {
	    // make full Size
	    generateFullSizeMode = true;
	    styledText.replaceTextRange(offset, 0,
		    System.getProperty("line.separator"));
	    styledText.replaceTextRange(offset + 1, 0,
		    System.getProperty("line.separator"));
	    fullSizeMode = true;
	    generateFullSizeMode = false;

	    if (grabMaximumSize) {
		int verticalBar = 0;
		if (styledText.getVerticalBar() != null) {
		    verticalBar = styledText.getVerticalBar().getSize().x;
		}
		float scale = (float) (styledText.getBounds().width
			- verticalBar - MARGIN_RIGHT)
			/ image.getBounds().width;
		saveScale = getScaleFactor();
		if (scale > 1.0) {
		    scale = 1;
		}
		setScaleFactor(scale);
		chanceFactor = true;
	    }
	} else {
	    // don't make full Size
	    fullSizeMode = false;
	    styledText.replaceTextRange(
		    offset - System.getProperty("line.separator").length(),
		    System.getProperty("line.separator").length(), "");
	    styledText.replaceTextRange(offset + 1,
		    System.getProperty("line.separator").length(), "");
	    setScaleFactor(saveScale);
	    chanceFactor = true;
	}

	styledText.redraw();

    }

    /**
     * returns the style element of the string
     * 
     * @return
     */
    public StyleRange getStyle() {
	return imageStyle;
    }

    /**
     * returns the html tag of the string
     * 
     * @return html string for the Image Tag
     */
    public String getHtmlTag() {
	String srcStr;
	String html = "";
	srcStr = imageFilename;
	for (final String[] replace : RichText.escapes) {
	    srcStr = srcStr.replace(replace[0], replace[1]);
	}

	final String scaleFactorStr = isFullsizeOn() ? "100%" : ""
		+ getScaleFactor();
	html = "<img src=\"" + srcStr + "\" width=\"" + scaleFactorStr + "\">"
		+ "\uFFFB" + "</img>";
	return html;
    }

    /**
     * Sets the flag to compute the max width after first rendering
     * 
     * @param b
     */
    public void setMaxResizeFlag(final boolean b) {
	maxResizeFlag = true;
    }

    public void refreshImage(final Image img) {
	image = img;
    }
}
