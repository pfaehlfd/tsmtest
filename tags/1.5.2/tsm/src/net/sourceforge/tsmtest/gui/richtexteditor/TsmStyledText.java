/*******************************************************************************
 * Copyright (c) 2012-2013 Florian Krüger.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Florian Krüger - initial version
 *    Daniel Hertl - various changes
 *    Wolfgang Kraus - added cut operation
 *    Albert Flaig - added image name to xml
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Class provides a special StyledText Component Component offers functions to
 * undo and redo actions
 * 
 * @author Florian Krüger
 * 
 */
public class TsmStyledText extends StyledText {
    public static final int SINGLE_TEXT = 0;
    public static final int SINGLE_STYLE = 1;
    public static final int APPEND_TEXT = 2;
    public static final int APPEND_STYLE = 3;
    public static final int SINGLE_IMAGE = 4;
    private final CopyPasteWorker cpWorker = CopyPasteWorker.getInstance();

    /**
     * The contained TsmImages in the appropriate TsmStyledText instance
     */
    private List<TsmStyledTextImage> images = Collections
	    .synchronizedList(new ArrayList<TsmStyledTextImage>());

    /**
     * Sets the List of TsmImages in the appropriate TsmStyledText instance
     * 
     * @param images
     */
    public void setImages(final List<TsmStyledTextImage> images) {
	this.images = Collections.synchronizedList(images);
    }

    /**
     * Adds a single TsmImage to the appropriate TsmStyledText instance
     * 
     * @param image
     */
    public void addImage(final TsmStyledTextImage image) {
	images.add(image);
    }

    /**
     * Gets all containing TsmImages of the appropriate TsmStyledText instance
     * 
     * @return
     */
    public List<TsmStyledTextImage> getImages() {
	return images;
    }

    private UndoRedoHandler undoHelper;
    private RichText relatedRichText;
    private String projectPath;
    private String testcaseData;

    /**
     * Create an Instacne of this class
     * 
     * @param parent
     *            parent Composite
     * @param style
     *            style
     * @param undoRedoHandler
     */
    public TsmStyledText(final Composite parent, final int style,
	    final UndoRedoHandler undoRedoHandler) {
	super(parent, style);
	undoHelper = undoRedoHandler;
    }

    public void setStyleRange(final TsmStyledTextImage image) {
	super.setStyleRange(image.getStyle());
    }

    /**
     * undo the last action
     */
    public void undo() {
	undoHelper.undo();
    }

    public void redo() {
	undoHelper.redo();
    }

    /**
     * Gets the selected text of the focused TsmStyledText instance and
     * transfers it to the CopyPasteWorker
     */
    @Override
    public void copy() {
	final Point sel = getSelection();
	// System.out.println("x  : "+sel.x+" y :"+sel.y);
	final String selectedText = this.getText().substring(sel.x, sel.y);
	cpWorker.copyStyledText(selectedText,
		this.getStyleRanges(sel.x, sel.y - sel.x), sel.x, this);

    }

    /**
     * Pastes the styled text which is generated in the CopyPasteWorker
     */
    @Override
    public void paste() {
	try {
	    cpWorker.pasteStyledText(this);
	} catch (final ParserConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (final SAXException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Call the TsmStyledText copy method and cuts the selected text out of the
     * focused TsmStyledText instance
     */
    @Override
    public void cut() {
	copy();
	final Point sel = getSelection();
	replaceTextRange(sel.x, sel.y - sel.x, "");
    }

    public ArrayList<Integer> getRelativeImageOffsets(final int selLength,
	    final int selStart, final TsmStyledText styledText) {
	final int index = 0;
	final ArrayList<Integer> imgOffsets = new ArrayList<Integer>();

	for (final TsmStyledTextImage image : getImages()) {
	    if (image.getOffset() >= selStart
		    && image.getOffset() <= selStart + selLength) {
		imgOffsets.add(image.getOffset());
	    }

	}

	for (int i = 0; i < imgOffsets.size(); i++) {
	    imgOffsets.set(i, imgOffsets.get(i) - selStart);
	}

	return imgOffsets;
    }

    public void setRelatedRichTextComponent(final RichText richText) {
	relatedRichText = richText;
    }

    public String getProjectName() {
	return testcaseData;
    }

    public void setProjectName(final String projectName) {
	testcaseData = projectName;
    }

}
