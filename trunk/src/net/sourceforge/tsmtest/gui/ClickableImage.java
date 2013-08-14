 /*******************************************************************************
 * Copyright (c) 2012-2013 Wolfgang Kraus and Bernhard Wetzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wolfgang Kraus - initial version
 *    Bernhard Wetzel - initial version
 *    Tobias Hirning - some refactoring, i18n
 *    Albert Flaig - some refactoring
 *******************************************************************************/
package net.sourceforge.tsmtest.gui;

import java.util.ArrayList;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Provides an image which changed when clicked
 * 
 * @author Wolfgang Kraus
 * @author Bernhard Wetzel
 * 
 */

public class ClickableImage extends Composite {
    public final static String ID = "net.sourceforge.tsmtest.gui.runtest.view.clickableimage"; //$NON-NLS-1$
    private Label imgLabel;
    private Label txtLabel;
    private int currentState = 0;
    private ArrayList<ModifyListener> listeners;

    
    private static StatusType[] states = { 
	StatusType.notExecuted,
	StatusType.passed, 
	StatusType.passedWithAnnotation,
	StatusType.failed 
	};
    
    private static String[] statesTxt = { 
	Messages.ClickableImage_1, 
	Messages.ClickableImage_2,
	Messages.ClickableImage_3, 
	Messages.ClickableImage_4 
	};

    /**
     * Creates a new Component with an image and a text label below
     * 
     * @param parent
     *            for the component
     * @param style
     *            for the component
     * @param clickable
     *            whether a click should change the image
     */
    public ClickableImage(Composite parent, int style, boolean clickable) {
	super(parent, style);
	setLayout(new GridLayout());
	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
	setLayoutData(gd);

	imgLabel = new Label(this, SWT.CENTER);
	imgLabel.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, true));
	Color white = new Color(null, 255, 255, 255);
	imgLabel.setBackground(white);


	txtLabel = new Label(this, SWT.CENTER);
	txtLabel.setBackground(white);
	txtLabel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true));
	
	white.dispose();

	if (clickable) {
	    MouseAdapter ma = new MouseAdapter() {
		@Override
		public void mouseDown(MouseEvent e) {
		    changeImage(true);
		}
	    };
	    imgLabel.addMouseListener(ma);
	    txtLabel.addMouseListener(ma);
	    this.addMouseListener(ma);
	}

	listeners = new ArrayList<ModifyListener>();
    }

    /**
     * Sets the Label, tooltips (image/composite) and updates the listener
     * 
     * @param inc
     * 		if true, next image will be displayed 
     */
    private void changeImage(boolean inc) {
	if (inc)
	    currentState = (currentState + 1) % 4;
	
	switch (states[currentState]){
	case failed:
	    imgLabel.setImage(ResourceManager.getImgRed());
	    break;
	case notExecuted:
	    imgLabel.setImage(ResourceManager.getImgGray());		    
	    break;
	case passed:
	    imgLabel.setImage(ResourceManager.getImgGreen());
	    break;
	case passedWithAnnotation:
	    imgLabel.setImage(ResourceManager.getImgOrange());
	    break;
	default:
	    break;
	
	}

	txtLabel.setText(statesTxt[currentState]);
	imgLabel.setToolTipText(statesTxt[currentState]);
	this.setToolTipText(statesTxt[currentState]);
	updateListeners();
    }

    /**
     * Sets the status to the specific type, invokes a change for those who
     * listen
     * 
     * @param st
     *            new status for the image
     */
    public void setStatus(StatusType st) {
	switch (st) {
	case failed:
	    currentState = 3;
	    changeImage(false);
	    break;
	case notExecuted:
	    currentState = 0;
	    changeImage(false);
	    break;
	case passed:
	    currentState = 1;
	    changeImage(false);
	    break;
	case passedWithAnnotation:
	    currentState = 2;
	    changeImage(false);
	    break;
	default:
	    break;

	}
    }

    /**
     * Adds the listener to the the list
     * 
     * @param listen
     *            to add
     */
    public void addModifyListener(ModifyListener listen) {
	if (listen == null) {
	    throw new NullPointerException(Messages.ClickableImage_5);
	}
	listeners.add(listen);
    }

    /**
     * Removed the given listener
     * 
     * @param listen
     *            to remove
     * @return whether the listener was removed
     */
    public boolean removeModifyListener(ModifyListener listen) {
	if (listen == null) {
	    throw new NullPointerException(Messages.ClickableImage_6);
	}
	return listeners.remove(listen);
    }

    /**
     * Causes all listeners to trigger
     */
    private void updateListeners() {
	for (ModifyListener m : listeners) {
	    m.modifyText(null);
	}
    }

    /**
     * @return the currently selected status as StatusType
     */
    public StatusType getStatus() {
	return states[currentState];
    }
}
