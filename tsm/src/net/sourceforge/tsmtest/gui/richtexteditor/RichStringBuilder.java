/*******************************************************************************
 * Copyright (c) 2012-2013 Alonso Dominguez.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alonso Dominguez - initial version
 *    Daniel Hertl - various changes
 *    Albert Flaig - some fixes
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import java.util.Stack;

/**
 * This class provides an String Builder to build an HTML String and work with it.
 * 
 * @author Alonso Dominguez
 * @author Daniel Hertl
 *
 */

public final class RichStringBuilder {
 
    public static final String LINE_DELIMITER = "<br/>";
    private StringBuilder builder;
    private Stack<FontStyle> fontStyleStack;
 
    /**
     * Constructor to create a new StringBuilder and a new Stack to save the Fontstyles
     */
    public RichStringBuilder() {
        builder = new StringBuilder();
        fontStyleStack = new Stack<FontStyle>();
    }
 
    /**
     * Adds text to string builder
     * @param text the text to append
     * @return the new RichStringbuilder
     */
    public RichStringBuilder append(String text) {
        builder.append(text);
        return this;
    }
 
    /**
     * Add line break
     * @return the new RichStringbuilder
     */
    public RichStringBuilder appendLineBreak() {
        builder.append(LINE_DELIMITER);
        return this;
    }
 
    /**
     * Add start html paragraph
     * @return the new RichStringbuilder
     */
    public RichStringBuilder startParagraph() {
        builder.append("<p>");
        return this;
    }
 
    /**
     * Add an start Html to the specific fontstyle
     * @param fontStyle the fontstyle you want to add the start tag
     * @return the new RichStringbuilder
     */
    public RichStringBuilder startFontStyle(FontStyle fontStyle) {
        fontStyleStack.push(fontStyle);
        internalStartFontStyle(fontStyle);
        return this;
    }
 
    /**
     * Add an array of start tags of fontstyles
     * 
     * @param fontStyles the array of fontstyles you want to add the start tags
     * @return the new RichStringbuilder
     */
    public RichStringBuilder startFontStyles(FontStyle... fontStyles) {
        for (FontStyle fs : fontStyles) {
            startFontStyle(fs);
        }
        return this;
    }
 
    /**
     * Adds end Html tags of the includes Fontstyles in the FontstyleStack
     * 
     * @param count how many end tags should be added to the StringBuilder
     * @return the new RichStringbuilder
     */
    public RichStringBuilder endFontStyles(int count) {
        for (int i = 0;i < count;i++) {
            endStyle();
        }
        return this;
    }
 
    /** 
     * Add end Html tags of the FontStyleStack at the top of the stack
     * 
     * @return
     */
    public RichStringBuilder endStyle() {
        if (fontStyleStack.size() > 0) {
            FontStyle style = fontStyleStack.pop();
            internalEndFontStyle(style);
        }
        return this;
    }
 
    /**
     * Add end Html Tag of an Paragraph
     * 
     * @return the new RichtStringBuilder
     */
    public RichStringBuilder endParagraph() {
        flushStyles();
        builder.append("</p>");
        return this;
    }
 
    /**
     * Add all end Html tags which are included in the stack at this moment
     */
    public void flushStyles() {
        while (fontStyleStack.size() > 0) {
            endStyle();
        }
    }
 
    @Override
    public boolean equals(Object o) {
	if (this == o) {
	    return true;
	}
	if (null == o) {
	    return false;
	}
	if (!(o instanceof RichStringBuilder)) {
	    return false;
	}
 
        return ((RichStringBuilder) o).builder.equals(builder);
    }
 
    @Override
    public int hashCode() {
        return builder.hashCode();
    }
 
    @Override
    public String toString() {
        return builder.toString();
    }
 
    private void internalStartFontStyle(FontStyle fontStyle) {
        switch (fontStyle) {
        case BOLD:
            builder.append("<b>");
                        break;
                case ITALIC:
                        builder.append("<i>");
                        break;
                case STRIKE_THROUGH:
                        builder.append("<del>");
                        break;
                case UNDERLINE:
                        builder.append("<ins>");
                        break;
	default:
	    break;
                }
        }
 
        private void internalEndFontStyle(FontStyle fontStyle) {
                switch (fontStyle) {
                case BOLD:
                        builder.append("</b>");
            break;
        case ITALIC:
            builder.append("</i>");
            break;
        case STRIKE_THROUGH:
            builder.append("</del>");
            break;
        case UNDERLINE:
            builder.append("</ins>");
            break;
		default:
		    break;
        }
    }
 
}