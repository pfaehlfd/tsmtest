 /*******************************************************************************
 * Copyright (c) 2012-2013 Wolfgang Kraus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wolfgang Kraus - initial version
 *    Albert Flaig - added image path to xml file
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.runtest.view;

import java.awt.MouseInfo;
import java.awt.PointerInfo;

import net.sourceforge.tsmtest.gui.richtexteditor.RichText;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple Listener to create a fake tooltip in a RichText widget
 * 
 * @author Wolfgang Kraus
 * 
 */
public class ToolTipListener implements Listener {
    private Shell tip = null;
    private String htmlToolTip;
    private Shell displayShell;
    private Point displaySize = new Point(400, 200);
    private String project;

    @Override
    public void handleEvent(final Event event) {
	RichText richTextDesc = null;
	/*
	 * We fall through as the different actions should trigger the same
	 * response
	 */
	switch (event.type) {
	case SWT.Dispose:
	case SWT.KeyDown:
	case SWT.MouseExit: {
	    if (tip == null) {
		break;
	    }
	    tip.dispose();
	    tip = null;
	    richTextDesc = null;
	    break;
	}
	case SWT.MouseEnter:
	case SWT.MouseHover: {
	    if (tip != null && !tip.isDisposed()) {
		return;
	    }
	    tip = new Shell(displayShell, SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
	    tip.setLayout(new FillLayout());
	    final PointerInfo p = MouseInfo.getPointerInfo();
	    tip.setLocation(p.getLocation().x - 310, p.getLocation().y + 20);
	    richTextDesc = new RichText(tip, SWT.NONE);
	    displaySize = richTextDesc.computeSize(displaySize.x, SWT.DEFAULT);
	    displaySize.y = Math.max(displaySize.y + 20, 200);
	    richTextDesc.setEnabled(false);
	    richTextDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
		    1));
	    tip.setSize(displaySize);
	    tip.open();
	    try {
		richTextDesc.setProjectName(project);
		richTextDesc.setFormattedText(htmlToolTip);
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	    displaySize = richTextDesc.computeSize(displaySize.x, SWT.DEFAULT);
	    displaySize.y = Math.max(displaySize.y + 20, 200);
	    tip.setSize(displaySize);
	}
	case SWT.MouseDown: {
	    displaySize = richTextDesc.computeSize(displaySize.x, SWT.DEFAULT);
	    displaySize.y = Math.max(displaySize.y + 20, 200);
	    tip.setSize(displaySize);

	}
	}
    }

    public void setHtmlToolTip(final String project, final String htmlToolTip) {
	// Helping gc
	this.htmlToolTip = null;
	this.htmlToolTip = htmlToolTip;
	this.project = project;
    }

    public void setDisplayShell(final Shell displayShell) {
	// Helping gc
	this.displayShell = null;
	this.displayShell = displayShell;
    }

    public Point getDisplaySize() {
	return displaySize;
    }

    public void setDisplaySize(final Point displaySize) {
	// Helping gc
	this.displaySize = null;
	this.displaySize = displaySize;
    }

    public void setDisplaySize(final int x, final int y) {
	// Helping gc
	displaySize = null;
	displaySize = new Point(x, y);
    }

}
