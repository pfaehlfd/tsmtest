/*******************************************************************************
 * Copyright (c) 2012-2013 Florian Krüger.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Florian Krüger - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * @author Florian Krüger
 *
 */
public class UndoRedoHandler {
    RichText rich;
    LinkedList<UndoRedoElement> undoRanges = new LinkedList<UndoRedoElement>();
    LinkedList<UndoRedoElement> redoRanges = new LinkedList<UndoRedoElement>();
    boolean doUndo = false;
    private UndoRedoElement lastUndo;
    private boolean doneRedo;

    public UndoRedoHandler(RichText rich) {
	this.rich = rich;
    }

    public void saveUndo() {
	if (!doUndo) {
	    String actualText = rich.getFormattedText();
	    int caretOffset = rich.getCaretOffset();
	    if (!rich.getStyledTextComponent().isFocusControl()
		    && caretOffset == 0)
		caretOffset = actualText.length();
	    undoRanges.push(new UndoRedoElement(actualText, caretOffset));
	}
    }

    public void undo() {
	doneRedo = true;
	String currentText = rich.getFormattedText();
	doUndo = true;
	if (undoRanges.size() > 0) {
	    UndoRedoElement undoElement = undoRanges.pop();
	    if (lastUndo != null && !currentText.equals(lastUndo.getPart())) {
		redoRanges.clear();
	    }
	    redoRanges.push(new UndoRedoElement(currentText, rich
		    .getCaretOffset()));

	    try {
		if (!currentText.equals(undoElement.getPart())) {
		    rich.setFormattedText(undoElement.getPart(), true);
		    rich.setCaretOffset(undoElement.getCaretPosition());
		}
	    } catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    lastUndo = undoElement;
	}
	doUndo = false;
    }

    public void redo() {
	lastUndo = null;
	doneRedo = true;
	doUndo = true;
	String currentText = rich.getFormattedText();
	if (redoRanges.size() > 0) {
	    UndoRedoElement redoElement = redoRanges.pop();
	    undoRanges.push(new UndoRedoElement(currentText, rich
		    .getCaretOffset()));

	    try {
		if (!currentText.equals(redoElement.getPart())) {

		    rich.setFormattedText(redoElement.getPart(), true);
		    rich.setCaretOffset(redoElement.getCaretPosition());
		}
	    } catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	doUndo = false;
    }

    /**
     * Class provides an undo redo element
     * 
     * @author kaisercanvas
     * 
     */
    public class UndoRedoElement {
	String editorPart;
	int caretPosition;

	public UndoRedoElement(String editorPart, int offset) {
	    this.editorPart = editorPart;
	    caretPosition = offset;
	}

	public int getCaretPosition() {
	    return caretPosition;
	}

	public String getPart() {
	    return editorPart;
	}
    }

    public boolean doneRedo() {
	// TODO Auto-generated method stub
	if (doneRedo == true) {
	    doneRedo = false;
	    return true;
	}
	return false;
    }
}
