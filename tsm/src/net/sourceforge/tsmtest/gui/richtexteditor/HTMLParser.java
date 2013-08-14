/*******************************************************************************
 * Copyright (c) 2012-2013 Daniel Hertl.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Hertl - initial version
 *    Florian Krüger - some changes
 *    Tobias Hirning - code cleanup
 *    Wolfgang Kraus - fixed handling of escape characters
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

/**
 * This class provides an interpreter for the CopyPasteWorker. This interpreter will create HTML text. 
 * It need style ranges and text.
 * 
 * @author Daniel Hertl
 *
 */
public class HTMLParser {
    
    //TsmStyledText component
    private TsmStyledText stTemp;
    
    //TsmStyledText component need to buffer styled text
    private TsmStyledText styledText;
    
    //text of styledText component without any style ranges
    private String plainText;

    /**
     * Constructor to create a new instance of HTMLParser
     * 
     * @param styledText a TsmStyledText component
     */
    public HTMLParser(TsmStyledText styledText) {
	this.styledText = styledText;
    }

    /**
     * Creates a HTML text of plain text and style ranges
     * 
     * @param plainText is the text which shell be edited with the styles
     * @param ranges the styles which are used
     * @return the HTML text
     */
    public String getHTMLText(String plainText, StyleRange[] ranges) {
	// plainText = styledText.getText();
	// replace forbidden characters
	for (String[] replace : RichText.escapes) {
	    plainText = plainText.replace(replace[0], replace[1]);
	}

	//TODO Singleton f�r diesen StyledTextEditor
	//creates a new TsmStyledText to work with the text and the styles without changing the contents of the original
	initializeTempStyledText();
	
	//set the text and the ranges
	stTemp.setText(plainText);
	stTemp.setStyleRanges(ranges);

	//String builder to add the single parts of the new html string
	RichStringBuilder builder = new RichStringBuilder();
	
	//first add the html tag
	builder.append("<html>");
	
	//counts the linebreaks and inserts the line length within the line separators into the integer array
	Integer[] lineBreaks = getLineBreaks(plainText);

	//length of line
	int brIdx = 0;
	
	//start of the line
	int start = 0;
	int end = (lineBreaks.length > brIdx ? lineBreaks[brIdx++] : plainText
		.length());

	//from start of line to end of line
	while (start < end) {
	    
	    //add paragraph
	    builder.startParagraph();

	    //get all styles of start to end
	    StyleRange[] lineRanges = stTemp.getStyleRanges(start,
		    (end - start) - stTemp.getLineDelimiter().length());

	    // Text between startTAG and first other TAG
	    if (lineRanges != null && lineRanges.length > 0) {
		for (int i = 0; i < lineRanges.length; i++) {
		    if (start < lineRanges[i].start) {
			builder.append(plainText.substring(start,
				lineRanges[i].start));
		    }

		    //translate the filtered styles in the current line to FontStyles
		    List<FontStyle> styles = translateStyle(lineRanges[i]);
		    
		    // add the start tags to the builder
		    builder.startFontStyles(styles.toArray(new FontStyle[styles
			    .size()]));
		    
		    //add the text between the tags to the buffer
		    builder.append(plainText.substring(lineRanges[i].start,
			    lineRanges[i].start + lineRanges[i].length));
		    
		    //add the end tags to the buffer
		    builder.endFontStyles(styles.size());
		    
		    //start is new set to next style range start
		    start = (lineRanges[i].start + lineRanges[i].length);
		}
	    }
	    
	    //if there are no more style ranges in this line
	    if (start < end) {
		
		// add text to builder
		builder.append(plainText.substring(start, end
			- styledText.getLineDelimiter().length()));
	    }
	    
	    //set the new start to the and of the last worked line
	    start = end;
	    
	    // end will be the end of the next line
	    end = (lineBreaks.length > brIdx ? lineBreaks[brIdx++] : plainText
		    .length());
	    
	    //add end paragraph
	    builder.endParagraph();
	}
	
	//add html tag
	builder.append("</html>");
	
	//create string out of the builder
	String formString = builder.toString();
	
	//change characteristic symbol to image tag
	for (TsmStyledTextImage image : styledText.getImages()) {
	    formString = formString.replaceFirst("\uFFFC", image.getHtmlTag());
	}

	return formString;
    }

    private void initializeTempStyledText() {
	//TODO Singleton f�r diesen StyledTextEditor
	stTemp = new TsmStyledText(styledText, SWT.None, null);
	
    }

    //get the count and line length of the line breaks
    private Integer[] getLineBreaks(String plainText) {
	
	
	List<Integer> list = new ArrayList<Integer>();
	int linelength = 0;
	int counter = 0;

	boolean noMore = false;
	for (int i = 0; !noMore;) {
	    int length;
	    if ((length = plainText.substring(i).indexOf(
		    System.getProperty("line.separator"))) != -1) {
		counter++;
		i += length + System.getProperty("line.separator").length();
		list.add(i);
	    } else {
		noMore = true;
		if (i < plainText.length()) {
		    list.add(plainText.length() + System.getProperty("line.separator").length());
		}
	    }
	}

	// Collections.sort(list);
	return list.toArray(new Integer[list.size()]);
    }

    private List<FontStyle> translateStyle(StyleRange range) {
	List<FontStyle> list = new ArrayList<FontStyle>();

	if ((range.fontStyle & SWT.BOLD) != 0) {
	    list.add(FontStyle.BOLD);
	}
	if ((range.fontStyle & SWT.ITALIC) != 0) {
	    list.add(FontStyle.ITALIC);
	}
	if (range.strikeout) {
	    list.add(FontStyle.STRIKE_THROUGH);
	}
	if (range.underline) {
	    list.add(FontStyle.UNDERLINE);
	}

	return list;
    }
    

}
