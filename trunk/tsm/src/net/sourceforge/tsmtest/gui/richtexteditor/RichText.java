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
 *    Florian Krüger - various changes
 *    Albert Flaig - some fixes
 *    Wolfgang Kraus - some fixes
 *    Verena Käfer - some fixes
 *    Bernhard Wetzel - some fixes
 *    Tobias Hirning - some fixes
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.tsmtest.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.xml.sax.SAXException;

/**
 * This class provides a extended version of the SWT styled text component It
 * contains an context menu with some formatting actions like bold, italic,
 * strike through, underline and some editing options like copy, paste and cut.
 * 
 * @author Alonso Dominguez
 * @author Daniel Hertl
 * 
 */
public class RichText extends Composite {

    private String projectName;

    protected static final int INITIAL_WIDTH_IMAGE = 80;
    protected static final int MIN_WIDTH_IMAGE = 20;
    protected static final int MAX_WIDTH_IMAGE = 1600;

    protected static final int TSM_LINESEPERATOR_LENGTH = 1;

    // Allowed file extensions for the file dialog.
    private static final String fileExtensions[] = { "*.jpg;*.jpeg;*.png" }; //$NON-NLS-1$

    private static final String[] escapeAmpersAnd = { "&", "\uFFD3" }; //$NON-NLS-1$ //$NON-NLS-2$
    /**
     * Used for escaping, contains arrays of character - escape sequence
     */
    public static final String[][] escapes = { { "<", "\uFFF9" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { ">", "\uFFF8" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "$", "\uFFF7" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "#", "\uFFF6" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "{", "\uFFF5" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "[", "\uFFF4" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "]", "\uFFF3" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "}", "\uFFF2" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "\\", "\uFFF1" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "?", "\uFFF0" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { ":", "\uFFD9" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "=", "\uFFD8" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "@", "\uFFD7" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "~", "\uFFD6" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "^", "\uFFD5" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "`", "\uFFD4" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "%", "\uFFD2" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "§", "\uFFD1" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "\"", "\uFFD0" }, //$NON-NLS-1$ //$NON-NLS-2$
	    { "'", "\uFFCF" }, //$NON-NLS-1$ //$NON-NLS-2$
	    escapeAmpersAnd, };

    // used Style Ranges in this component
    private List<StyleRange> cachedStyles = Collections
	    .synchronizedList(new LinkedList<StyleRange>());

    // private ToolBar toolBar;
    private TsmStyledText styledText;
    private String plainText;
    private final CopyPasteWorker cpworker = CopyPasteWorker.getInstance();

    // private ToolItem boldBtn;
    // private ToolItem italicBtn;
    // private ToolItem strikeThroughBtn;
    // private ToolItem underlineBtn;
    // private ToolItem pasteBtn;
    // private ToolItem eraserBtn;

    private MenuItem mntmBold;

    private MenuItem mntmItalic;

    private MenuItem mntmUnderline;

    private MenuItem mntmStrikeThrough;

    private MenuItem mntmPaste;

    private MenuItem mntmClean;

    private boolean refreshFlag = false;

    private final Composite parent;

    private final Cursor oldCursor;

    private AtomicBoolean mouseDown;
    private Cursor resizeCursor;

    private Cursor handCursor;
    private TsmStyledTextImage hoverImage = null;
    private int maxCharacters = -1;
    private boolean editText;
    private Menu menu;
    private MouseAdapter updateStyleListener;
    private MenuItem mntmImage;
    private SelectionAdapter selectionListener;
    private boolean grabMaximumSize;
    private Color disabledTextColor;
    private final UndoRedoHandler undoRedoHandler = new UndoRedoHandler(this);
    private final List<ModifyListener> modifyListeners = new ArrayList<ModifyListener>();

    public void doDispose() {
	if (handCursor != null) {
	    handCursor.dispose();
	}
	if (resizeCursor != null) {
	    resizeCursor.dispose();
	}
	for (final TsmStyledTextImage img : styledText.getImages()) {
	    img.dispose();
	}
	if (disabledTextColor != null) {
	    disabledTextColor.dispose();
	}
	// We need to make sure that the os is windows otherwise the virtual
	// machine can crash.
	if (System.getProperty("os.name").contains("windows")) { //$NON-NLS-1$ //$NON-NLS-2$
	    super.dispose();
	}
    }

    public TsmStyledText getStyledTextComponent() {
	return styledText;
    }

    public RichText(final Composite parent, final int style) {
	// We must not have a scroll pane in our outer component
	super(parent, style & (~SWT.V_SCROLL));
	this.parent = parent;
	initComponents(style);
	addImageListener();
	addCharactersLeftListener();
	oldCursor = styledText.getCursor();
	grapMaxSize(true);
	addDisposeListener(new DisposeListener() {
	    @Override
	    public void widgetDisposed(final DisposeEvent e) {
		doDispose();
	    }
	});
    }

    /**
     * Images grab existing space if they are on full screen mode
     * 
     * @param grab
     */
    public void grapMaxSize(final boolean grab) {
	grabMaximumSize = grab;
    }

    /**
     * Sets the maximum amount of characters allowed at this editor
     * 
     * @param maxCharacters
     */
    public void setMaximalCharacters(final int maxCharacters) {
	this.maxCharacters = maxCharacters;
    }

    /**
     * use a verify listener to keep the offsets up to date
     * 
     * @param e
     */
    public void verifyImages(final VerifyEvent e) {
	if (!refreshFlag) {
	    final int start = e.start;
	    final int replaceCharCount = e.end - e.start;
	    final int newCharCount = e.text.length();
	    if (styledText.getImages().size() > 0) {
		final Iterator<TsmStyledTextImage> img = styledText.getImages()
			.iterator();
		while (img.hasNext()) {
		    final TsmStyledTextImage image = img.next();
		    int offset = image.getOffset();
		    if (start <= offset && offset < start + replaceCharCount
			    && replaceCharCount > 0) {
			// this image is being deleted from the text
			if (image.getImage() != null
				&& !image.getImage().isDisposed()) {
			    image.getImage().dispose();
			    img.remove();
			}
			offset = -1;
		    }
		    if (offset != -1 && offset >= start) {
			offset += newCharCount - replaceCharCount;
		    }
		    image.setOffset(offset);
		}
	    }
	}
    }

    /**
     * Add Character Listener which visualizes the characters left
     */
    private void addCharactersLeftListener() {
	styledText.addVerifyListener(new VerifyListener() {

	    private int undoCounter;
	    private boolean deleteCurrent;

	    @Override
	    public void verifyText(final VerifyEvent e) {
		editText = true;
		int deleteImage = -1;
		if (e.text.indexOf("\uFFFC") == -1) {
		    for (final Iterator<TsmStyledTextImage> it = styledText
			    .getImages().iterator(); it.hasNext();) {
			final TsmStyledTextImage img = it.next();

			if (img.isFullsizeOn()
				&& !e.text.equals(System
					.getProperty("line.separator"))) { //$NON-NLS-1$
			    if (e.start > img.getOffset()
				    + System.getProperty("line.separator") //$NON-NLS-1$
					    .length()) {
			    } else if ((e.start + e.text.length() < img
				    .getOffset()
				    - (System.getProperty("line.separator") //$NON-NLS-1$
					    .length() - 2) && e.text.length() > 0)
				    || (e.end > img.getOffset()
					    + System.getProperty(
						    "line.separator").length() && e.start
					    + e.text.length() <= img
					    .getOffset()
					    - System.getProperty(
						    "line.separator").length())) {
			    } else if (e.start == img.getOffset() + 1
				    || e.end == img.getOffset()) {
				if (e.text.isEmpty() && e.end - e.start > 0) {
				    deleteImage = img.getOffset();
				}
				if (!e.text.equals(System
					.getProperty("line.separator"))) { //$NON-NLS-1$
				    e.doit = false;
				}
			    } else if (e.text.isEmpty() && e.end - e.start > 0) {
				// deleteImage = img.getOffset();
			    } else {
				e.doit = false;
			    }
			} else if (!e.text.equals(" ") //$NON-NLS-1$
				&& !img.isGeneratingFullSizeMode()
				&& e.start <= img.getOffset()
				&& e.text.length() + e.start > img.getOffset()) {
			    styledText.replaceTextRange(e.start, 0, " "); //$NON-NLS-1$
			}
		    }
		}
		if (deleteImage != -1) {
		    styledText
			    .replaceTextRange(
				    deleteImage
					    - System.getProperty(
						    "line.separator")
						    .length(), 1 + 2 * System
						    .getProperty("line.separator").length(), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    e.doit = false;
		}
		if (maxCharacters >= 0) {
		    final int currentLength = styledText.getText().length();
		    if (currentLength + e.text.length() > maxCharacters) {
			e.doit = false;
		    }
		    styledText.redraw();
		}
		if (e.doit) {
		    verifyImages(e);
		}
		
		if (undoRedoHandler.doneRedo()) {
		    undoRedoHandler.saveUndo();
		} else if (e.start - e.end < 0 && !deleteCurrent) {
		    deleteCurrent = true;
		    undoRedoHandler.saveUndo();
		} else {
		    if (deleteCurrent && e.text.length() > 0) {
			deleteCurrent = false;
			undoRedoHandler.saveUndo();
		    } else if (e.text.indexOf(' ') != -1) {
			undoCounter++;
			if (undoCounter == 4) {
			    undoRedoHandler.saveUndo();
			    undoCounter = 0;
			}
		    } else if (e.text.equals(System
			    .getProperty("line.separator"))) {
			undoRedoHandler.saveUndo();
		    }
		}
	    }
	});

	styledText.addFocusListener(new FocusListener() {

	    @Override
	    public void focusLost(final FocusEvent e) {
		styledText.redraw();
	    }

	    @Override
	    public void focusGained(final FocusEvent e) {
		styledText.redraw();
	    }
	});
	styledText.addPaintListener(new PaintListener() {

	    @Override
	    public void paintControl(final PaintEvent e) {
		if (editText && styledText.isFocusControl()
			&& maxCharacters != -1) {
		    final String alert = styledText.getText().length() + "/" //$NON-NLS-1$
			    + maxCharacters;
		    if (maxCharacters != styledText.getText().length()) {
			e.gc.setBackground(new Color(e.gc.getDevice(), 200,
				200, 200));
		    } else {
			e.gc.setBackground(new Color(e.gc.getDevice(), 200,
				100, 100));
		    }

		    final int x = styledText.getBounds().x
			    + styledText.getBounds().width
			    - (alert.length() * 6 + 4);
		    final int y = (styledText.getBounds().y);

		    if (new Rectangle(x - 6, y, alert.length() * (6 + 1), 14)
			    .contains(styledText.getCaret().getLocation())) {
			e.gc.drawText(styledText.getText().length() + "/" //$NON-NLS-1$
				+ maxCharacters, x, y + 20);
		    } else {
			e.gc.drawText(styledText.getText().length() + "/" //$NON-NLS-1$
				+ maxCharacters, x, y);
		    }
		}
	    }
	});
    }

    private void addImage(final TsmStyledTextImage image) {
	styledText.setStyleRange(image);
    }

    private void addImageListener() {

	styledText.addMouseListener(new MouseListener() {

	    @Override
	    public void mouseUp(final MouseEvent e) {
		if (hoverImage != null) {
		    hoverImage.stopResize();
		    for (final ModifyListener listener : modifyListeners) {
			final Event tempEvent = new Event();
			tempEvent.widget = RichText.this;
			listener.modifyText(new ModifyEvent(tempEvent));
		    }
		}
	    }

	    @Override
	    public void mouseDown(final MouseEvent e) {
		if (hoverImage != null && hoverImage.getOverResize()) {
		    hoverImage.startResize();
		} else if (hoverImage != null && hoverImage.getOverFillSize()) {
		    hoverImage.doFullSize(styledText, grabMaximumSize);
		} else if (hoverImage != null) {
		    new TsmImageFullScreen(hoverImage);
		}
		parent.redraw();
	    }

	    @Override
	    public void mouseDoubleClick(final MouseEvent e) {
	    }
	});

	styledText.addMouseMoveListener(new MouseMoveListener() {

	    @Override
	    public void mouseMove(final MouseEvent e) {
		boolean hover = false;
		for (final TsmStyledTextImage image : styledText.getImages()) {
		    if (image.getBounds() != null
			    && (image.getBounds().contains(e.x, e.y) || image
				    .isResizeOn())) {
			// Mouse is over image
			hover = true;
			if (image.isResizeOn()) {
			    image.setResizePoint(new Point(e.x, e.y));
			}
			if (image.getResizeBounds() != null
				&& image.getResizeBounds().contains(e.x, e.y)) {
			    // mouse is over the resizer
			    resizeCursor = new Cursor(Display.getCurrent(),
				    SWT.CURSOR_SIZESE);
			    styledText.setCursor(resizeCursor);
			    resizeCursor.dispose();
			    image.setOverResize(true);
			} else if (image.getFullSizeBounds() != null
				&& image.getFullSizeBounds().contains(e.x, e.y)) {
			    // mouse is over the fill resize icon
			    image.setOverFillSize(true);
			    styledText.setCursor(handCursor);
			} else {
			    // mouse is just over the image
			    resizeCursor = new Cursor(Display.getCurrent(),
				    SWT.CURSOR_SIZESE);
			    styledText.setCursor(handCursor);
			    resizeCursor.dispose();
			    image.setOverResize(false);
			    image.setOverFillSize(false);
			}
			hoverImage = image;
			image.paintResizeMode(true);
			styledText.redraw();
		    }
		}
		if (!hover) {
		    styledText.setCursor(oldCursor);
		    if (hoverImage != null) {
			hoverImage.setOverResize(false);
			hoverImage.paintResizeMode(false);
			styledText.redraw();
			hoverImage = null;
		    }
		}

	    }
	});

	styledText.addPaintObjectListener(new PaintObjectListener() {
	    private boolean firstView = true;

	    @Override
	    public void paintObject(final PaintObjectEvent event) {
		for (final TsmStyledTextImage timage : styledText.getImages()) {
		    // paint images
		    timage.paintImage(event);
		}
		if (firstView) {
		    styledText.update();
		    styledText.redraw();
		    firstView = false;
		}
	    }
	});
    }

    public void addCaretListener(final CaretListener listener) {
	styledText.addCaretListener(listener);
    }

    public void removeCaretListener(final CaretListener listener) {
	styledText.removeCaretListener(listener);
    }

    public void addExtendedModifyListener(final ExtendedModifyListener listener) {
	styledText.addExtendedModifyListener(listener);
    }

    public void removeExtendedModifyListener(
	    final ExtendedModifyListener listener) {
	styledText.removeExtendedModifyListener(listener);
    }

    public void addModifyListener(final ModifyListener listener) {
	styledText.addModifyListener(listener);
	modifyListeners.add(listener);
    }

    public void removeModifyListener(final ModifyListener listener) {
	styledText.removeModifyListener(listener);
	modifyListeners.remove(listener);
    }

    public void addVerifyKeyListener(final VerifyKeyListener listener) {
	styledText.addVerifyKeyListener(listener);
    }

    public void removeVerifyKeyListener(final VerifyKeyListener listener) {
	styledText.removeVerifyKeyListener(listener);
    }

    public void addVerifyListener(final VerifyListener listener) {
	styledText.addVerifyListener(listener);
    }

    public void addSelectionListener(final SelectionListener listener) {
	styledText.addSelectionListener(listener);
    }

    public void removeVerifyListener(final VerifyListener listener) {
	styledText.removeVerifyListener(listener);
    }

    @Override
    public void addMouseMoveListener(final MouseMoveListener listener) {
	styledText.addMouseMoveListener(listener);
    }

    @Override
    public void removeMouseMoveListener(final MouseMoveListener listener) {
	styledText.removeMouseMoveListener(listener);
    }

    public int getCharCount() {
	return styledText.getCharCount();
    }

    public Caret getCaret() {
	return styledText.getCaret();
    }

    public int getCaretOffset() {
	return styledText.getCaretOffset();
    }

    public void setCaretOffset(final int offset) {
	styledText.setCaretOffset(offset);
    }

    /**
     * Obtain an HTML formatted text from the component contents
     * 
     * @return an HTML formatted text
     */
    public String getFormattedText() {
	plainText = styledText.getText();
	if (plainText.isEmpty()) {
	    return "<html><p></p></html>"; //$NON-NLS-1$
	}
	// replace forbidden characters
	for (final String[] replace : escapes) {
	    plainText = plainText.replace(replace[0], replace[1]);
	}

	final RichStringBuilder builder = new RichStringBuilder();
	builder.append("<html>"); //$NON-NLS-1$
	final Integer[] lineBreaks = getLineBreaks();

	int brIdx = 0;
	int start = 0;
	int end = (lineBreaks.length > brIdx ? lineBreaks[brIdx++] : plainText
		.length());

	// for (int i = 0; i < lineBreaks.length; i++) {
	// System.out.println("Gib mmir mal die Inzizes: " + lineBreaks[i]);
	// }

	while (start < end) {
	    builder.startParagraph();
	    // System.out.println("StringBuilder Part2 : " +
	    // builder.toString());
	    final StyleRange[] ranges = styledText.getStyleRanges(start,
		    (end - start) - styledText.getLineDelimiter().length());
	    // for (int i = 0; i < ranges.length; i++) {
	    // System.out.println("Die kopierten Ranges: " + ranges[i]);
	    // }
	    // Text between startTAG and first other TAG
	    if (ranges != null && ranges.length > 0) {
		for (int i = 0; i < ranges.length; i++) {
		    if (start < ranges[i].start) {
			builder.append(plainText.substring(start,
				ranges[i].start));
			// System.out.println("StringBuilder Part3 : "
			// + builder.toString());
		    }

		    final List<FontStyle> styles = translateStyle(ranges[i]);
		    builder.startFontStyles(styles.toArray(new FontStyle[styles
			    .size()]));
		    builder.append(plainText.substring(ranges[i].start,
			    ranges[i].start + ranges[i].length));
		    // System.out.println("StringBuilder Part4 : "
		    // + builder.toString());
		    builder.endFontStyles(styles.size());
		    // System.out.println("StringBuilder Part5 : "
		    // + builder.toString());

		    start = (ranges[i].start + ranges[i].length);
		    // System.out.println("Start " + start + "| Ende : " + end);
		}
	    }
	    if (start < end) {
		builder.append(plainText.substring(start, end
			- styledText.getLineDelimiter().length()));
		// System.out.println("StringBuilder Part6 : "
		// + builder.toString());
	    }
	    start = end;
	    end = (lineBreaks.length > brIdx ? lineBreaks[brIdx++] : plainText
		    .length());
	    builder.endParagraph();
	    // System.out.println("StringBuilder Part7 : " +
	    // builder.toString());
	}
	builder.append("</html>"); //$NON-NLS-1$
	// System.out.println("StringBuilder Part8 : " + builder.toString());
	String formString = builder.toString();

	for (final TsmStyledTextImage image : styledText.getImages()) {
	    formString = formString.replaceFirst("\uFFFC", image.getHtmlTag()); //$NON-NLS-1$
	}
	return formString;
    }

    public void setFormattedText(String text)
	    throws ParserConfigurationException, SAXException, IOException {
	text = text.replace(escapeAmpersAnd[0], escapeAmpersAnd[1]);
	// text = text.replace(System.getProperty("line.separator"), "");
	final RichTextParser parser = RichTextParser.parse(text, getDisplay(),
		getProjectName());
	styledText.setImages(parser.getImages());
	refreshFlag = true;
	styledText.setText(parser.getText());
	styledText.setStyleRanges(parser.getStyleRanges());
	refreshFlag = false;
	undoRedoHandler.saveUndo();
    }

    public void setFormattedText(String text, final boolean isUndoRedo)
	    throws ParserConfigurationException, SAXException, IOException {
	text = text.replace(escapeAmpersAnd[0], escapeAmpersAnd[1]);
	// text = text.replace(System.getProperty("line.separator"), "");
	final RichTextParser parser = RichTextParser.parse(text, getDisplay(),
		getProjectName());
	styledText.setImages(parser.getImages());
	refreshFlag = true;
	styledText.setText(parser.getText());
	styledText.setStyleRanges(parser.getStyleRanges());
	refreshFlag = false;
	if (!isUndoRedo) {
	    undoRedoHandler.saveUndo();
	}
    }

    public int getLineAtOffset(final int offset) {
	return styledText.getLineAtOffset(offset);
    }

    public int getLineCount() {
	return styledText.getLineCount();
    }

    public int getLineSpacing() {
	return styledText.getLineSpacing();
    }

    public String getText() {
	return styledText.getText();
    }

    public void setEditable(final boolean editable) {
	// TODO real implementation
	setEnabled(false);
    }

    public boolean isEditable() {
	// TODO return value of real implementation
	return isEnabled();
    }

    protected void applyFontStyleToSelection(final FontStyle style) {
	final Point sel = styledText.getSelectionRange();
	if ((sel == null) || (sel.y == 0)) {
	    return;
	}

	StyleRange newStyle;
	for (int i = sel.x; i < (sel.x + sel.y); i++) {
	    final StyleRange range = styledText.getStyleRangeAtOffset(i);
	    if (range != null) {
		newStyle = (StyleRange) range.clone();
		newStyle.start = i;
		newStyle.length = 1;
	    } else {
		newStyle = new StyleRange(i, 1, null, null, SWT.NORMAL);
	    }

	    switch (style) {
	    case BOLD:
		newStyle.fontStyle ^= SWT.BOLD;
		break;
	    case ITALIC:
		newStyle.fontStyle ^= SWT.ITALIC;
		break;
	    case STRIKE_THROUGH:
		newStyle.strikeout = !newStyle.strikeout;
		break;
	    case UNDERLINE:
		newStyle.underline = !newStyle.underline;
		break;
	    default:
		break;
	    }

	    styledText.setStyleRange(newStyle);

	}

	styledText.setSelectionRange(sel.x + sel.y, 0);
    }

    /**
     * Clear all styled data
     */
    protected void clearStylesFromSelection() {
	final Point sel = styledText.getSelectionRange();
	if ((sel != null) && (sel.y != 0)) {
	    final StyleRange style = new StyleRange(sel.x, sel.y, null, null,
		    SWT.NORMAL);
	    styledText.setStyleRange(style);
	    // Add images again
	    for (final TsmStyledTextImage image : styledText.getImages()) {
		if (image.getOffset() >= sel.x
			&& image.getOffset() <= sel.x + sel.y) {
		    // restore style
		    addImage(image);
		}
	    }
	}
	// TODO Findbugs says the variable sel can be null.
	styledText.setSelectionRange(sel.x + sel.y, 0);
    }

    /*
     * returns an Integer array. The index of the Array is the line number, the
     * related value is the Index of the line delimiter
     */
    private Integer[] getLineBreaks() {
	final List<Integer> list = new ArrayList<Integer>();
	int linelength = 0;

	for (int i = 0; i < styledText.getLineCount(); i++) {
	    linelength += styledText.getLine(i).length()
		    + styledText.getLineDelimiter().length();
	    list.add(linelength);

	}

	if (list.size() >= 2) {
	    if ((list.get(list.size() - 1)) - list.get(list.size() - 2) == styledText
		    .getLineDelimiter().length()) {
		list.remove(list.size() - 1);
	    }
	}
	Collections.sort(list);
	return list.toArray(new Integer[list.size()]);
    }

    protected void handleCutCopy() {
	// Save the cut/copied style info so that during paste we will maintain
	// the style information. Cut/copied text is put in the clipboard in
	// RTF format, but is not pasted in RTF format. The other way to
	// handle the pasting of styles would be to access the Clipboard
	// directly and
	// parse the RTF text.
	cachedStyles = Collections
		.synchronizedList(new LinkedList<StyleRange>());

	final Point sel = styledText.getSelectionRange();

	final int startX = sel.x;
	for (int i = sel.x; i <= sel.x + sel.y - 1; i++) {
	    final StyleRange style = styledText.getStyleRangeAtOffset(i);
	    if (style != null) {
		style.start = style.start - startX;
		if (!cachedStyles.isEmpty()) {
		    final StyleRange lastStyle = cachedStyles.get(cachedStyles
			    .size() - 1);
		    if (lastStyle.similarTo(style)
			    && lastStyle.start + lastStyle.length == style.start) {
			lastStyle.length++;
		    } else {
			cachedStyles.add(style);
		    }
		} else {
		    cachedStyles.add(style);
		}
	    }
	}
	mntmPaste.setEnabled(true);
    }

    private void handleExtendedModified(final ExtendedModifyEvent event) {
	if (event.length == 0) {
	    return;
	}

	StyleRange style;
	if (event.length == 1
		|| styledText.getTextRange(event.start, event.length).equals(
			styledText.getLineDelimiter())) {
	    // Have the new text take on the style of the text to its right
	    // (during
	    // typing) if no style information is active.
	    final int caretOffset = styledText.getCaretOffset();
	    style = null;
	    if (caretOffset < styledText.getCharCount()) {
		style = styledText.getStyleRangeAtOffset(caretOffset);
	    }
	    if (style != null) {
		style = (StyleRange) style.clone();
		style.start = event.start;
		style.length = event.length;
	    } else {
		style = new StyleRange(event.start, event.length, null, null,
			SWT.NORMAL);
	    }
	    if (mntmBold.getSelection()) {
		style.fontStyle |= SWT.BOLD;
	    }
	    if (mntmItalic.getSelection()) {
		style.fontStyle |= SWT.ITALIC;
	    }
	    style.underline = mntmUnderline.getSelection();
	    style.strikeout = mntmStrikeThrough.getSelection();
	    if (!style.isUnstyled()) {
		styledText.setStyleRange(style);
		// styledText.setTextRange(
		// styledText.getText().substring(event.start,
		// event.start + event.length), event.start);
		// styledText.bundleStyles();
	    }
	} else {
	    // paste occurring, have text take on the styles it had when it was
	    // cut/copied
	    for (int i = 0; i < cachedStyles.size(); i++) {
		style = cachedStyles.get(i);
		final StyleRange newStyle = (StyleRange) style.clone();
		newStyle.start = style.start + event.start;
		styledText.setStyleRange(newStyle);
	    }
	}

    }

    private void handleTextSelected(final SelectionEvent event) {
	final Point sel = styledText.getSelectionRange();

	if ((sel != null) && (sel.y != 0)) {
	    final StyleRange[] styles = styledText.getStyleRanges(sel.x, sel.y);
	    mntmClean.setEnabled((styles != null) && (styles.length > 0));
	} else {
	    mntmClean.setEnabled(false);
	}

    }

    private void handleKeyReleased(final KeyEvent event) {
	if ((event.keyCode == SWT.ARROW_LEFT)
		|| (event.keyCode == SWT.ARROW_UP)
		|| (event.keyCode == SWT.ARROW_RIGHT)
		|| (event.keyCode == SWT.ARROW_DOWN)) {
	    updateStyleButtons();
	}
    }

    private void updateStyleButtons() {
	final int caretOffset = styledText.getCaretOffset();
	StyleRange style = null;
	if (caretOffset >= 0 && caretOffset < styledText.getCharCount()) {
	    style = styledText.getStyleRangeAtOffset(caretOffset);
	}

	if (style != null) {
	    mntmBold.setSelection((style.fontStyle & SWT.BOLD) != 0);
	    mntmItalic.setSelection((style.fontStyle & SWT.ITALIC) != 0);
	    mntmUnderline.setSelection(style.underline);
	    mntmStrikeThrough.setSelection(style.strikeout);
	} else {
	    mntmBold.setSelection(false);
	    mntmItalic.setSelection(false);
	    mntmUnderline.setSelection(false);
	    mntmStrikeThrough.setSelection(false);
	}
    }

    private void initComponents(final int style) {
	final GridLayout layout = new GridLayout();
	layout.numColumns = 1;
	layout.marginWidth = 0;
	layout.marginHeight = 0;
	setLayout(layout);

	styledText = new TsmStyledText(this, style, undoRedoHandler);
	styledText.setAlwaysShowScrollBars(false);
	styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
	styledText.addKeyListener(new KeyAdapter() {
	private boolean undoPressed = false;

	    @Override
	    public void keyPressed(final KeyEvent e) {
		switch (e.keyCode) {
		case SWT.CTRL:
		    undoPressed = true;
		    break;
		case SWT.F2:
		    // redo();
		    break;
		default:
		    // ignore everything else
		}
	    }

	    @Override
	    public void keyReleased(final KeyEvent e) {
		switch (e.keyCode) {
		case 122:
		    if (undoPressed) {
			styledText.undo();
			break;
		    }
		case 121:
		    if (undoPressed) {
			styledText.redo();
			break;
		    }
		    break;
		default:
		    undoPressed = false;
		    handleKeyReleased(e);
		}
	    }
	});

	styledText.addTraverseListener(new TraverseListener() {
	    @Override
	    public void keyTraversed(final TraverseEvent e) {
		if (e.detail == SWT.TRAVERSE_TAB_NEXT
			|| e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
		    e.doit = true;
		}
	    }
	});

	styledText.setWordWrap(true);

	menu = new Menu(styledText);
	styledText.setMenu(menu);

	mntmBold = new MenuItem(menu, SWT.CHECK);
	mntmBold.addSelectionListener(new FontStyleButtonListener(
		FontStyle.BOLD));
	mntmBold.setText(Messages.RichText_0);

	mntmItalic = new MenuItem(menu, SWT.CHECK);
	mntmItalic.addSelectionListener(new FontStyleButtonListener(
		FontStyle.ITALIC));
	mntmItalic.setText(Messages.RichText_1);

	mntmUnderline = new MenuItem(menu, SWT.CHECK);
	mntmUnderline.addSelectionListener(new FontStyleButtonListener(
		FontStyle.UNDERLINE));
	mntmUnderline.setText(Messages.RichText_2);

	mntmStrikeThrough = new MenuItem(menu, SWT.CHECK);
	mntmStrikeThrough.addSelectionListener(new FontStyleButtonListener(
		FontStyle.STRIKE_THROUGH));
	mntmStrikeThrough.setText(Messages.RichText_3);

	new MenuItem(menu, SWT.SEPARATOR);

	final MenuItem mntmCopy = new MenuItem(menu, SWT.PUSH);
	mntmCopy.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		handleCutCopy();
		styledText.copy();

	    }
	});
	mntmCopy.setText(Messages.RichText_4);

	mntmPaste = new MenuItem(menu, SWT.PUSH);
	mntmPaste.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		styledText.paste();
	    }
	});
	mntmPaste.setText(Messages.RichText_5);
	//
	// MenuItem mntmCut = new MenuItem(menu, SWT.PUSH);
	// mntmCut.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// handleCutCopy();
	// styledText.cut();
	// }
	// });
	// mntmCut.setText("Cut");
	//
	// new MenuItem(menu, SWT.SEPARATOR);

	mntmClean = new MenuItem(menu, SWT.PUSH);
	mntmClean.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent e) {
		clearStylesFromSelection();

		styledText.notifyListeners(SWT.Modify, new Event());

	    }
	});
	mntmClean.setText(Messages.RichText_6);
	styledText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(final KeyEvent e) {
		handleKeyReleased(e);
	    }
	});

	new MenuItem(menu, SWT.SEPARATOR);

	mntmImage = new MenuItem(menu, SWT.PUSH);
	mntmImage.setText(Messages.RichText_7);
	mntmImage.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent event) {
		final FileDialog dialog = new FileDialog(getShell());
		// Set the allowed file extensions.
		dialog.setFilterExtensions(fileExtensions);

		String filename = dialog.open();
		if (filename != null) {
		    filename = filename.replace("\\", "/");
		    filename = filename.replace("%20", " "); //$NON-NLS-1$ //$NON-NLS-2$
		    try {
			Image image = null;
			if (new File(filename).exists()) {
			    image = new Image(getDisplay(), filename);
			} else {
			    image = new Image(getDisplay(), new Rectangle(0, 0,
				    RichText.INITIAL_WIDTH_IMAGE,
				    RichText.INITIAL_WIDTH_IMAGE));
			    final GC gc = new GC(image);
			    gc.setBackground(new Color(getDisplay(), 200, 200,
				    200));
			    gc.drawRectangle(0, 0,
				    RichText.INITIAL_WIDTH_IMAGE - 1,
				    RichText.INITIAL_WIDTH_IMAGE - 1);
			    gc.fillRectangle(1, 1,
				    RichText.INITIAL_WIDTH_IMAGE - 2,
				    RichText.INITIAL_WIDTH_IMAGE - 2);
			    gc.drawLine(3, 3, RichText.INITIAL_WIDTH_IMAGE - 4,
				    RichText.INITIAL_WIDTH_IMAGE - 4);
			    gc.drawLine(RichText.INITIAL_WIDTH_IMAGE - 4, 3, 3,
				    RichText.INITIAL_WIDTH_IMAGE - 4);
			    gc.drawString("IMAGE", 2, //$NON-NLS-1$
				    (RichText.INITIAL_WIDTH_IMAGE / 2) - 7);
			    gc.dispose();
			}
			final int offset = styledText.getCaretOffset();
			// image alone in a line
			// styledText.replaceTextRange(offset, 0,
			// System.getProperty("line.separator"));
			// offset +=
			// System.getProperty("line.separator").length();
			styledText.replaceTextRange(offset, 0, "\uFFFC"); //$NON-NLS-1$
			// styledText.replaceTextRange(offset + 1, 0,
			// System.getProperty("line.separator"));
			int index = 0;
			while (index < styledText.getImages().size()) {
			    if (styledText.getImages().get(index).getOffset() == -1
				    && styledText.getImages().get(index) == null) {
				break;
			    }
			    index++;
			}

			final String[] s = new String[] { filename };

			final ImageInputOutput iIO = ImageInputOutput
				.createHandler(s);
			iIO.saveImage(projectName);

			if (index == styledText.getImages().size()) {
			    styledText.addImage(new TsmStyledTextImage(image,
				    projectName,
				    iIO.getTempImageFilenames()[0], offset));
			} else {
			    styledText.getImages().set(
				    index,
				    new TsmStyledTextImage(image, projectName,
					    iIO.getTempImageFilenames()[0],
					    offset));
			}
			addImage(styledText.getImages().get(
				styledText.getImages().size() - 1));
		    } catch (final Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	});

	styledText.addExtendedModifyListener(new ExtendedModifyListener() {
	    @Override
	    public void modifyText(final ExtendedModifyEvent event) {
		handleExtendedModified(event);
	    }
	});
	updateStyleListener = new MouseAdapter() {
	    @Override
	    public void mouseUp(final MouseEvent e) {
		updateStyleButtons();
	    }
	};
	styledText.addMouseListener(updateStyleListener);
	selectionListener = new SelectionAdapter() {
	    @Override
	    public void widgetSelected(final SelectionEvent event) {
		handleTextSelected(event);
	    }
	};
	styledText.addSelectionListener(selectionListener);
    }

    // private ToolBar createToolBar(Composite parent) {
    // ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
    //
    // boldBtn = new ToolItem(toolBar, SWT.CHECK);
    // // boldBtn.setImage(RichTextImages.IMG_BOLD);
    // boldBtn.setToolTipText(RichTextStrings.boldBtn_tooltipText);
    // boldBtn.addSelectionListener(
    // new FontStyleButtonListener(FontStyle.BOLD));
    //
    // italicBtn = new ToolItem(toolBar, SWT.CHECK);
    // // italicBtn.setImage(RichTextImages.IMG_ITALIC);
    // italicBtn.setToolTipText(RichTextStrings.italicBtn_tooltipText);
    // italicBtn.addSelectionListener(
    // new FontStyleButtonListener(FontStyle.ITALIC));
    //
    // underlineBtn = new ToolItem(toolBar, SWT.CHECK);
    // // underlineBtn.setImage(RichTextImages.IMG_UNDERLINE);
    // underlineBtn.setToolTipText(RichTextStrings.underlineBtn_tooltipText);
    // underlineBtn.addSelectionListener(
    // new FontStyleButtonListener(FontStyle.UNDERLINE));
    //
    // strikeThroughBtn = new ToolItem(toolBar, SWT.CHECK);
    // // strikeThroughBtn.setImage(RichTextImages.IMG_STRIKE_THROUGH);
    // strikeThroughBtn.setToolTipText(RichTextStrings.strikeThroughBtn_tooltipText);
    // strikeThroughBtn.addSelectionListener(
    // new FontStyleButtonListener(FontStyle.STRIKE_THROUGH));
    //
    // new ToolItem(toolBar, SWT.SEPARATOR);
    //
    // ToolItem cutBtn = new ToolItem(toolBar, SWT.PUSH);
    // // cutBtn.setImage(RichTextImages.IMG_CUT);
    // cutBtn.setToolTipText(RichTextStrings.cutBtn_tooltipText);
    // cutBtn.addSelectionListener(new SelectionAdapter() {
    // @Override
    // public void widgetSelected(SelectionEvent e) {
    // handleCutCopy();
    // styledText.cut();
    // }
    // });
    //
    // ToolItem copyBtn = new ToolItem(toolBar, SWT.PUSH);
    // // copyBtn.setImage(RichTextImages.IMG_COPY);
    // copyBtn.setToolTipText(RichTextStrings.copyBtn_tooltipText);
    // copyBtn.addSelectionListener(new SelectionAdapter() {
    // @Override
    // public void widgetSelected(SelectionEvent e) {
    // handleCutCopy();
    // styledText.copy();
    // }
    // });
    //
    // pasteBtn = new ToolItem(toolBar, SWT.PUSH);
    // pasteBtn.setEnabled(false);
    // // pasteBtn.setImage(RichTextImages.IMG_PASTE);
    // pasteBtn.setToolTipText(RichTextStrings.pasteBtn_tooltipText);
    // pasteBtn.addSelectionListener(new SelectionAdapter() {
    // @Override
    // public void widgetSelected(SelectionEvent e) {
    // styledText.paste();
    // }
    // });
    //
    // new ToolItem(toolBar, SWT.SEPARATOR);
    //
    // eraserBtn = new ToolItem(toolBar, SWT.PUSH);
    // eraserBtn.setEnabled(false);
    // // eraserBtn.setImage(RichTextImages.IMG_ERASER);
    // eraserBtn.setToolTipText(RichTextStrings.eraserBtn_tooltipText);
    // eraserBtn.addSelectionListener(new SelectionAdapter() {
    // @Override
    // public void widgetSelected(SelectionEvent e) {
    // clearStylesFromSelection();
    // }
    // });
    //
    // return toolBar;
    // }

    private List<FontStyle> translateStyle(final StyleRange range) {
	final List<FontStyle> list = new ArrayList<FontStyle>();

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

    private class FontStyleButtonListener extends SelectionAdapter {
	private final FontStyle style;

	public FontStyleButtonListener(final FontStyle style) {
	    this.style = style;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
	    final Point selection = styledText.getSelection();
	    applyFontStyleToSelection(style);
	    styledText.notifyListeners(SWT.Modify, new Event());
	    styledText.setSelection(selection);
	}
    }

    public void removeTextListeners() {
	styledText.removeMouseListener(updateStyleListener);
	styledText.removeSelectionListener(selectionListener);
	disabledTextColor = new Color(styledText.getDisplay(), 130, 130, 130);
	styledText.setForeground(disabledTextColor);
	mntmBold.setEnabled(false);
	mntmClean.setEnabled(false);
	mntmImage.setEnabled(false);
	mntmItalic.setEnabled(false);
	mntmPaste.setEnabled(false);
	mntmStrikeThrough.setEnabled(false);
	mntmUnderline.setEnabled(false);

    }

    public void setProjectName(final String project) {
	projectName = project;
	styledText.setProjectName(project);
    }

    public String getProjectName() {
	return projectName;
    }
}