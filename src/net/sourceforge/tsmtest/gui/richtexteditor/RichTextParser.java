/*******************************************************************************
 * Copyright (c) 2012-2013 Alonso Dominguez.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alonso Dominguez - initial version. 
 *    Original code source: http://www.codenibbles.com/blog/2012/03/21/richtext-editor-component-for-swt-based-applications/#.UNL4EaxN_IN
 *    Daniel Hertl - various changes
 *    Florian Krüger - various changes
 *    Wolfgang Kraus - some fixes
 *    Verena Käfer - some fixes
 *    Tobias Hirning - code cleanup
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sourceforge.tsmtest.datamodel.DataModelTypes;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class provides a parser for the TsmStyledText component.
 * 
 * The class works with html text and parses this into styled text. The html may
 * not contain all known html tags. Treated Html tags: - html - p - b - i - ins
 * - del - img
 * 
 * @author Alonso Dominguez
 * @author Daniel Hertl
 * 
 */
public final class RichTextParser {

    // A string array containing the characters which may not take place in the
    // styled text
    private static String[][] htmlEscapes = { { "%20", " " }, { "%23", "#" },
	    { "%7B", "{" }, { "%7D", "}" }, { "%7C", "|" }, { "%5C", "\\" },
	    { "%5E", "^" }, { "%7E", "~" }, { "%5B", "[" }, { "%5D", "]" },
	    { "%60", "`" }, { "%3F", "?" }, { "%3A", ":" }, { "%40", "@" },
	    { "%3D", "=" }, { "%26", "&" }, { "%24", "$" }, { "%25", "%" } };

    // the List of containing images in this instance
    private final List<TsmStyledTextImage> images = new ArrayList<TsmStyledTextImage>();

    // the image source strings
    private final String[] imagesSrc = new String[0];

    private final Display display;

    /**
     * Parses the given formated Text (html Text) by instanciate this class. In
     * the constructor the given html text will be parse by the SAXParser.
     * 
     * @param formattedText
     * @param display
     * @return a new instance of the RichTextParser
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static RichTextParser parse(final String formattedText,
	    final Display display, final String project)
	    throws ParserConfigurationException, SAXException, IOException {
	return new RichTextParser(formattedText, display, project);
    }

    // the StringBuilder which get continued the translated pieces of the parser
    private final StringBuilder text = new StringBuilder();

    // a list of containing StyleRanges in the instance of TsmStyledText the
    // formattedText comes from
    private final List<StyleRange> styleRanges = new ArrayList<StyleRange>();

    // the constructor to create the SAXParser and all components you need to
    // parse the given string
    private RichTextParser(String formattedText, final Display display2,
	    final String project) throws ParserConfigurationException,
	    SAXException, IOException {
	display = display2;

	// if the transfered text is empty you need to add start and end tag for
	// later actions
	if (formattedText.equals("")) {
	    formattedText = "<html></html>";
	}

	// StringReader the parse method need for executing
	final StringReader reader = new StringReader(formattedText);

	// Instanciating the SAXParser
	final SAXParserFactory factory = SAXParserFactory.newInstance();
	final SAXParser parser = factory.newSAXParser();

	// the implemented DefaultHanlder the parse method needs to translate
	// the given Html text
	final DefaultHandler handler = new RichTextContentHandler(project);
	parser.parse(new InputSource(reader), handler);
    }

    /**
     * Gets all containing TsmImages in this instance
     * 
     * @return the images
     */
    public List<TsmStyledTextImage> getImages() {
	return images;
    }

    /**
     * Get the TsmImage sources of the containing TsmImages in this instance
     * 
     * @return string array of the image sources
     */
    public String[] getImagesSrc() {
	return imagesSrc;
    }

    /**
     * creates a Sting of the StringBuilder
     * 
     * @return the sting
     */
    public String getText() {
	return text.toString().replaceAll("\uFFFB", "\uFFFC");
    }

    /**
     * creates an array of the given list of StyleRanges
     * 
     * @return the StyleRange array
     */
    public StyleRange[] getStyleRanges() {
	return styleRanges.toArray(new StyleRange[styleRanges.size()]);
    }

    private class RichTextContentHandler extends DefaultHandler {

	// A stack where all StyleRanges which were indentified as a Tag will be
	// push in
	private final Stack<List<FontStyle>> stylesStack = new Stack<List<FontStyle>>();

	// the string before an indentified end tag
	private String lastTextChunk = null;
	private int counter = 0;

	private final String project;

	public RichTextContentHandler(final String project) {
	    this.project = project;
	}

	@Override
	public void characters(final char[] ch, final int start,
		final int length) throws SAXException {
	    counter += length;
	    lastTextChunk = new String(ch, start, length);
	    // replace forbidden characters

	    for (final String[] replace : RichText.escapes) {
		lastTextChunk = lastTextChunk.replace(replace[1], replace[0]);
	    }

	}

	@Override
	public void endElement(final String uri, final String localName,
		final String qName) throws SAXException {
	    // If there is not any previous text chunk parsed then return
	    if (lastTextChunk == null) {
		if ("p".equals(qName)) {
		    // If there is a no text before an end tag it have to be a
		    // paragraph
		    counter += System.getProperty("line.separator").length();

		    // so add line.separator to Stringuilder
		    text.append(System.getProperty("line.separator"));

		} else if ("html".equals(qName)) {
		    // delete the last added line.separator in an other action
		    if (text.length() != 0) {
			final int start = text.lastIndexOf(System
				.getProperty("line.separator"));

			for (int i = 0; i < System
				.getProperty("line.separator").length(); i++) {
			    text.deleteCharAt(start);
			}
		    }

		}
		return;
	    }
	    // If the tag found is not a supported one then return
	    if (!"html".equals(qName) && !"p".equals(qName)
		    && !"b".equals(qName) && !"i".equals(qName)
		    && !"ins".equals(qName) && !"del".equals(qName)
		    && !"img".equals(qName)) {
		return;
	    }

	    // A list of nested temporary nested tags
	    final List<FontStyle> lastStyles = lastFontStyles(true);

	    if (lastStyles != null) {

		// apply the included FontStyles and transform the text in the
		// RichText into these Styles
		final StyleRange range = transform(lastStyles);

		// set the start and length of the transformed ranges
		range.start = currentIndex() + 1;
		range.length = lastTextChunk.length();
		styleRanges.add(range);
	    }

	    // add the text before the given end tag
	    text.append(lastTextChunk);

	    // If an p is found add an paragraph to the text
	    if ("p".equals(qName)) {
		counter += System.getProperty("line.separator").length();
		text.append(System.getProperty("line.separator"));
	    }

	    // delete the current lastTextChunk
	    lastTextChunk = null;
	}

	@Override
	public void startElement(final String uri, final String localName,
		final String qName, final Attributes atts) throws SAXException {
	    // If the tag found is not a supported one then return
	    if (!"html".equals(qName) && !"p".equals(qName)
		    && !"b".equals(qName) && !"i".equals(qName)
		    && !"ins".equals(qName) && !"del".equals(qName)
		    && !"img".equals(qName)) {
		return;
	    }

	    // An <img> start tag is found
	    if ("img".equals(qName)) {
		// Add Image
		String filename = null;
		float scale = 0;
		boolean isMaxW = false;
		final int offset = counter;

		// TODO Document this part of the code
		for (int i = 0; i < atts.getLength(); i++) {
		    if (atts.getLocalName(i).equals("src")) {
			filename = atts.getValue(i);
		    }
		    if (atts.getLocalName(i).equals("width")) {
			if (atts.getValue(i).equals("100%")) {
			    isMaxW = true;
			} else {
			    scale = Float.valueOf(atts.getValue(i));
			}
		    }
		}
		Image image = null;
		// replacing the html escape sequences
		for (final String[] replace : RichText.escapes) {
		    filename = filename.replace(replace[1], replace[0]);
		}
		final String absoluteFilePath = ResourcesPlugin.getWorkspace()
			.getRoot().getLocation().toString()
			+ "/"
			+ project
			+ "/"
			+ DataModelTypes.imageFolderName
			+ "/" + filename;
		final File f = new File(absoluteFilePath);
		if (f.exists()) {
		    image = new Image(display, absoluteFilePath);
		} else {
		    image = new Image(display, new Rectangle(0, 0,
			    RichText.INITIAL_WIDTH_IMAGE,
			    RichText.INITIAL_WIDTH_IMAGE));
		    final GC gc = new GC(image);
		    final Color c = new Color(display, 200, 200, 200);
		    gc.setBackground(c);
		    c.dispose();
		    gc.drawRectangle(0, 0, RichText.INITIAL_WIDTH_IMAGE - 1,
			    RichText.INITIAL_WIDTH_IMAGE - 1);
		    gc.fillRectangle(1, 1, RichText.INITIAL_WIDTH_IMAGE - 2,
			    RichText.INITIAL_WIDTH_IMAGE - 2);
		    gc.drawLine(3, 3, RichText.INITIAL_WIDTH_IMAGE - 4,
			    RichText.INITIAL_WIDTH_IMAGE - 4);
		    gc.drawLine(RichText.INITIAL_WIDTH_IMAGE - 4, 3, 3,
			    RichText.INITIAL_WIDTH_IMAGE - 4);
		    gc.drawString("IMAGE", 2,
			    (RichText.INITIAL_WIDTH_IMAGE / 2) - 7);
		    gc.dispose();
		}
		int index = 0;
		while (index < images.size()) {
		    if (images.get(index).getOffset() == -1
			    && images.get(index) == null) {
			break;
		    }
		    index++;
		}
		TsmStyledTextImage tempImage;
		if (isMaxW) {
		    tempImage = new TsmStyledTextImage(image, project,
			    filename, offset);
		} else {
		    tempImage = new TsmStyledTextImage(image, project,
			    filename, offset, scale);
		}
		if (isMaxW) {
		    tempImage.setMaxResizeFlag(true);
		}
		if (index == images.size()) {
		    images.add(tempImage);
		} else {
		    images.set(index, tempImage);
		}
	    }

	    List<FontStyle> lastStyles = lastFontStyles(false);

	    if (lastStyles == null) {
		lastStyles = new ArrayList<FontStyle>();
		stylesStack.add(lastStyles);
	    }

	    if (lastTextChunk != null) {
		if (lastStyles != null) {
		    final StyleRange range = transform(lastStyles);
		    range.start = currentIndex() + 1;
		    range.length = lastTextChunk.length();
		    styleRanges.add(range);
		}

		text.append(lastTextChunk);
		lastTextChunk = null;
	    }

	    if ("b".equals(qName)) {
		lastStyles.add(FontStyle.BOLD);
	    } else if ("i".equals(qName)) {
		lastStyles.add(FontStyle.ITALIC);
	    } else if ("ins".equals(qName)) {
		lastStyles.add(FontStyle.UNDERLINE);
	    } else if ("p".equals(qName)) {
		lastStyles.add(FontStyle.PARAGRAPH);
	    } else if ("html".equals(qName)) {
		lastStyles.add(FontStyle.STARTDOC);
	    } else if ("img".equals(qName)) {
		lastStyles.add(FontStyle.IMAGE);
	    } else {
		// TODO Findbugs says variable can be null.
		lastStyles.add(FontStyle.STRIKE_THROUGH);
	    }
	}

	private StyleRange transform(final List<FontStyle> styles) {
	    final StyleRange range = new StyleRange();
	    range.start = currentIndex() + 1;
	    range.length = lastTextChunk.length();

	    for (final FontStyle fs : styles) {
		if (FontStyle.IMAGE == fs) {
		    final TsmStyledTextImage tImg = images
			    .get(images.size() - 1);
		    return tImg.getStyle();

		} else if (FontStyle.BOLD == fs) {
		    if (range.fontStyle == SWT.ITALIC) {
			range.fontStyle = (SWT.BOLD | SWT.ITALIC);
		    } else {
			range.fontStyle = (SWT.BOLD);
		    }
		} else if (FontStyle.ITALIC == fs) {
		    if (range.fontStyle == SWT.BOLD) {
			range.fontStyle = (SWT.BOLD | SWT.ITALIC);
		    } else {
			range.fontStyle = (SWT.ITALIC);
		    }
		} else if (FontStyle.STRIKE_THROUGH == fs) {
		    range.strikeout = true;
		} else if (FontStyle.UNDERLINE == fs) {
		    range.underline = true;
		} else if (FontStyle.PARAGRAPH == fs) {

		} else if (FontStyle.STARTDOC == fs) {

		}
	    }
	    return range;
	}

	private List<FontStyle> lastFontStyles(final boolean remove) {
	    List<FontStyle> lastStyles = null;
	    if (stylesStack.size() > 0) {
		if (remove) {
		    lastStyles = stylesStack.pop();
		} else {
		    lastStyles = stylesStack.peek();
		}
	    }
	    return lastStyles;
	}

	private int currentIndex() {
	    return text.length() - 1;
	}

    }

}