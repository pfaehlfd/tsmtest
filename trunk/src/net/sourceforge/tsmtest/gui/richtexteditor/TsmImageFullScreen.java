/*******************************************************************************
 * Copyright (c) 2012-2013 Florian Krüger.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Florian Krüger - initial version
 *    Wolfgang Kraus - bugfix
 *    Bernhard Wetzel - bugfix
 *    Albert Flaig - bugfix
 *    Verena Käfer - code cleanup
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Florian Krüger
 *
 */
public class TsmImageFullScreen {

    private TsmStyledTextImage timage;
    private Display display;
    private float scale = 1;
    private int x;
    private int y;
    private boolean buttonPressed;
    private Point dragPoint;

    public TsmImageFullScreen(TsmStyledTextImage timage) {
	this.timage = timage;
	run();
    }

    public void dispose() {
	timage.dispose();
	display.dispose();
    }

    public void run() {
	display = Display.getCurrent();
	Shell shell = new Shell(display);
	shell.setSize(200, 200);
	Monitor primary = display.getPrimaryMonitor();
	Rectangle bounds = primary.getBounds();
	shell.setText(timage.getSrc());
	// shell.set
	shell.open();
	shell.setLayout(new FillLayout());

	final Image image = timage.getImage();
	
	final Point origin = new Point(0, 0);
	final Canvas canvas = new Canvas(shell, SWT.NONE);
	canvas.addListener(SWT.Paint, new Listener() {
	    private Rectangle tempbounds;

	    public void handleEvent(Event e) {
		GC gc = e.gc;
		tempbounds = new Rectangle(origin.x, origin.y, (int) (image
			.getBounds().width * scale),
			(int) (image.getBounds().height * scale));
		// draw image
		gc.drawImage(image, 0, 0, image.getBounds().width,
			image.getBounds().height, origin.x, origin.y,
			tempbounds.width, tempbounds.height);
	    }
	});
	// check if image bigger than screen
	if(image.getBounds().width > bounds.width || image.getBounds().height > bounds.height) {
	    scale = Math.min(image.getBounds().width / bounds.width, image.getBounds().height / bounds.height);
	    canvas.redraw();
	}
	
	Rectangle rect1 = image.getBounds();
	Rectangle rectMax = display.getClientArea();
	Monitor[] list = display.getMonitors();
	if (list.length > 0) {
	    rectMax = list[0].getBounds();
	    //System.out.println(rectMax);
	}

	if (rectMax.width < rect1.width || rectMax.height < rect1.height) {
	    shell.setSize(Math.max(200, rectMax.width),
		    Math.max(150, rectMax.height));
	    x = bounds.x + (bounds.width - rectMax.width) / 2;
	    y = bounds.y + (bounds.height - rectMax.height) / 2;
	} else {
	    shell.setSize(Math.max(200, rect1.width),
		    Math.max(150, rect1.height));
	    x = bounds.x + (bounds.width - rectMax.width) / 2;
	    y = bounds.y + (bounds.height - rectMax.height) / 2;
	}

	shell.setLocation(x, y);

	shell.addMouseWheelListener(new MouseWheelListener() {
	    @Override
	    public void mouseScrolled(MouseEvent e) {
		if (scale < 1)
		    scale += (float) ((float) e.count / 24) * scale;
		else
		    scale += (float) ((float) e.count / ((float) 12 - ((float) scale / 3)));

		if (scale < 0.01)
		    scale = (float) 0.01;
		else if (scale > 30) {
		    scale = 30;
		}
		canvas.redraw();
	    }
	});
	canvas.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseUp(MouseEvent e) {
		if (e.button == 1)
		    buttonPressed = false;
	    }

	    @Override
	    public void mouseDown(MouseEvent e) {
		if (!buttonPressed) {
		    dragPoint = new Point(e.x - origin.x, e.y - origin.y);
		}
		if (e.button == 1)
		    buttonPressed = true;
	    }
	});
	canvas.addMouseMoveListener(new MouseMoveListener() {
	    @Override
	    public void mouseMove(MouseEvent e) {
		if (buttonPressed) {
		    int hSelection = dragPoint.x - e.x;
		    int vSelection = dragPoint.y - e.y;
		    int destX = -hSelection - origin.x;
		    int destY = -vSelection - origin.y;
		    Rectangle rect = image.getBounds();
		    canvas.scroll(destX, destY, 0, 0, rect.width, rect.height,
			    false);
		    origin.x = -hSelection;
		    origin.y = -vSelection;
		}
	    }
	});
	shell.open();
    }

}
