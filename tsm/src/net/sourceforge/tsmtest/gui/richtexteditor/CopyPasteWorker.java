 /*******************************************************************************
 * Copyright (c) 2012-2013 Daniel Hertl.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Hertl - initial version
 *    Wolfgang Kraus - some fixes
 *    Florian Krüger - bugfix
 *    Albert Flaig - added image name to xml
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.SAXException;

/**
 * 
 * @author Daniel Hertl
 * 
 */
public final class CopyPasteWorker {

    // the clipboard which is used
    private final Clipboard clipboard;

    // the current copied or cutted styles
    private StyleRange[] copiedStyles;

    // the transfer needed by the clipboard
    private Transfer[] transfer;

    // the singleton instance
    private static CopyPasteWorker copyPasteWorker;

    // saves the start positions of the style ranges to add the paste
    // mouse position later on
    private int[] startRange;
    private Display display;

    // ArrayList to temporary save the copied pictures
    private ArrayList<TsmStyledTextImage> tempImages = new ArrayList<TsmStyledTextImage>();

    // flag to characterize second paste action without coping a new selection
    private boolean thirdCopy = false;

    // relative ImageOffset of a selection
    private ArrayList<Integer> relImgOffset;

    private CopyPasteWorker() {

	// // transfer which can take html text or simple string
	// transfer = new Transfer[] { HTMLTransfer.getInstance(),
	// TextTransfer.getInstance(), ImageTransfer.getInstance() };

	final Display display = Display.getCurrent();
	clipboard = new Clipboard(display);
    }

    /**
     * gets the singleton instance of this CopyPasteWorker
     * 
     * @return copyPasteWorker
     */
    public static CopyPasteWorker getInstance() {
	if (copyPasteWorker == null) {
	    copyPasteWorker = new CopyPasteWorker();
	}
	return copyPasteWorker;
    }

    /**
     * Copies the selected text in the focused TsmStyledText Component within
     * style ranges and images
     * 
     * @param textToCopy
     *            the selected string
     * @param styles
     *            the style ranges in the selection
     * @param selStart
     *            the start of the selection
     * @param styledText
     *            the focused styledText component
     */
    public void copyStyledText(final String textToCopy,
	    final StyleRange[] styles, final int selStart,
	    final TsmStyledText styledText) {
	// the string which is currently copied
	String copiedString;
	int index = 0;

	// the position where the selection of text starts
	int startSelection;
	startSelection = selStart;
	copiedString = textToCopy;
	relImgOffset = styledText.getRelativeImageOffsets(textToCopy.length(),
		startSelection, styledText);

	// delete the images which are contained in the list of earlier copy
	// actions
	tempImages.clear();
	thirdCopy = false;

	// add images which the style text component contains to the list of
	// temporary images
	for (final TsmStyledTextImage image : styledText.getImages()) {
	    if (image.getOffset() >= selStart
		    && image.getOffset() <= selStart + textToCopy.length()) {

		// new Image(Display.getCurrent(), image.getSrc())
		final TsmStyledTextImage tImage = new TsmStyledTextImage(
			new Image(styledText.getDisplay(), image.getSrc()),
			styledText.getProjectName(), image.getFilename(),
			startSelection + relImgOffset.get(index),
			image.getScaleFactor());

		tempImages.add(tImage);
		index++;
	    }

	}

	copiedStyles = styles;

	// set the start position of the copied styles relativly to the selection
	for (int i = 0; i < copiedStyles.length; i++) {
	    copiedStyles[i].start = copiedStyles[i].start - selStart;
	}

	// saves the start positions relativly to the selection in an array
	startRange = new int[copiedStyles.length];
	for (int i = 0; i < startRange.length; i++) {
	    startRange[i] = copiedStyles[i].start;
	}

	// Html text Parser
	final HTMLParser htmlp = new HTMLParser(styledText);

	// translates the copied string and the appropriate style ranges into
	// HTML text
	copiedString = htmlp.getHTMLText(copiedString, copiedStyles);

	// The input data for the setContents method of the clipboard
	final String[] data = new String[] { copiedString, textToCopy };

	clipboard.setContents(data, new Transfer[] {
		HTMLTransfer.getInstance(), TextTransfer.getInstance() });

	for (int i = 0; i < copiedStyles.length; i++) {
	    startRange[i] = copiedStyles[i].start;
	}

    }

    /**
     * Pastes the current copied text within styles and images
     * 
     * @param styledText
     *            the styledText component
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void pasteStyledText(final TsmStyledText styledText)
	    throws ParserConfigurationException, SAXException, IOException {

	// Delete the selected Text
	if (!styledText.getSelectionText().isEmpty()) {
	    final Point selP = styledText.getSelectionRange();
	    styledText.replaceTextRange(selP.x, selP.y, "");
	}

	// read the text out of the clipboard
	final String cbHtmlTransfer = (String) clipboard
		.getContents(HTMLTransfer.getInstance());

	final String cbTextTransfer = (String) clipboard
		.getContents(HTMLTransfer.getInstance());

	final ImageData cbImageTransfer = (ImageData) clipboard
		.getContents(ImageTransfer.getInstance());

	final String[] cbFileTransfer = (String[]) clipboard
		.getContents(FileTransfer.getInstance());

	if (cbHtmlTransfer != null) {
	    try {
		insertTsmHtmlText(styledText, cbHtmlTransfer);
	    } catch (final Exception e) {
		// TODO Da w�re ein Exception Dialog ganz nett
		styledText.replaceTextRange(styledText.getCaretOffset(), 0,
			cbTextTransfer);
		// setting the cursor position
		styledText.setCaretOffset(styledText.getCaretOffset()
			+ cbTextTransfer.length());
	    }
	} else if (cbTextTransfer != null) {
	    styledText.replaceTextRange(styledText.getCaretOffset(), 0,
		    cbTextTransfer);
	    // setting the cursor position
	    styledText.setCaretOffset(styledText.getCaretOffset()
		    + cbTextTransfer.length());

	} else if (cbImageTransfer != null) {
	    final ImageInputOutput iIO = ImageInputOutput
		    .createHandler(cbImageTransfer);
	    iIO.saveImage(styledText.getProjectName());
	    for (int i = 0; i < iIO.getTempImageSrc().length; i++) {
		final int offset = styledText.getCaretOffset();
		styledText.replaceTextRange(offset, 0, "\uFFFC");
		final TsmStyledTextImage tImg = new TsmStyledTextImage(
			new Image(display, iIO.getTempImageSrc()[i]),
			styledText.getProjectName(),
			iIO.getTempImageFilenames()[i], offset);
		styledText.addImage(tImg);
		styledText.setStyleRange(tImg);
	    }

	} else if (cbFileTransfer != null) {
	    final ImageInputOutput iIO = ImageInputOutput
		    .createHandler(cbFileTransfer);
	    iIO.saveImage(styledText.getProjectName());
	    for (int i = 0; i < iIO.getTempImageSrc().length; i++) {
		final int offset = styledText.getCaretOffset() + i;
		styledText.replaceTextRange(offset, 0, "\uFFFC");
		final TsmStyledTextImage tImg = new TsmStyledTextImage(
			new Image(display, iIO.getTempImageSrc()[i]),
			styledText.getProjectName(),
			iIO.getTempImageFilenames()[i], offset);
		styledText.addImage(tImg);
		styledText.setStyleRange(tImg);

	    }
	}

	// TODO If outcoming clipboard transfers are coming in, treat HTML
	// content and none HTML content

    }

    private void addCopiedImages(final TsmStyledText styledText) {
	int index = 0;
	// insert all images which had been copied
	for (final TsmStyledTextImage image : tempImages) {

	    // if text will be paste the first time
	    if (!thirdCopy) {
		// sets the new offset position of the image to add

		image.setOffset(relImgOffset.get(index)
			+ styledText.getCaretOffset());

		// add the image to the styledText component

		styledText.addImage(image);
		thirdCopy = true;
		index++;
	    } else {
		// creating a new instance of the copied pictures
		final TsmStyledTextImage tImage = new TsmStyledTextImage(
			new Image(styledText.getDisplay(), image.getSrc()),
			styledText.getProjectName(), image.getFilename(),
			relImgOffset.get(index) + styledText.getCaretOffset(),
			image.getScaleFactor());

		// adding these pictures to the styled Text component

		styledText.addImage(tImage);
		index++;
	    }
	}
    }

    private void insertTsmHtmlText(final TsmStyledText styledText,
	    final String clipboardText) throws Exception {

	// the Parser which interprets the HTML Text
	final RichTextParser parser = RichTextParser.parse(clipboardText,
		display, styledText.getProjectName());

	// adding the relative start position of the copied style ranges
	// to the caret position
	for (int i = 0; i < copiedStyles.length; i++) {
	    copiedStyles[i].start = startRange[i] + styledText.getCaretOffset();
	}

	// fill in the copied text at caret position
	styledText.replaceTextRange(styledText.getCaretOffset(), 0,
		parser.getText());

	// replace the styles at the range of the copied text
	styledText.replaceStyleRanges(styledText.getCaretOffset(), parser
		.getText().length(), copiedStyles);

	addCopiedImages(styledText);

	// setting the cursor position
	styledText.setCaretOffset(styledText.getCaretOffset()
		+ parser.getText().length());
    }

}
