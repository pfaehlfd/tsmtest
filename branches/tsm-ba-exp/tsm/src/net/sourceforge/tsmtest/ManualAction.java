 /*******************************************************************************
 * Copyright (c) 2012-2013 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Hirning - initial version
 *    Verena Käfer - removed non-working code
 *******************************************************************************/
package net.sourceforge.tsmtest;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Tobias Hirning
 * 
 */
public class ManualAction extends Action {

    /**
     * 
     */
    public ManualAction() {
    }

    /**
     * @param text
     */
    public ManualAction(String text) {
	super(text);
    }

    /**
     * @param text
     * @param image
     */
    public ManualAction(String text, ImageDescriptor image) {
	super(text, image);
    }

    /**
     * @param text
     * @param style
     */
    public ManualAction(String text, int style) {
	super(text, style);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
	
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#setToolTipText(java.lang.String)
     */
    public void setToolTipText(String toolTipText) {
	super.setToolTipText(toolTipText);
    }
}
