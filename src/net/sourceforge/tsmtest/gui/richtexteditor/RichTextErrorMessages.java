/*******************************************************************************
 * Copyright (c) 2012-2013 Daniel Hertl.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Hertl - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.richtexteditor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Daniel Hertl
 *
 */
public class RichTextErrorMessages extends ErrorDialog{

    public RichTextErrorMessages(Shell parentShell, String dialogTitle,
	    String message, IStatus status, int displayMask) {
	super(parentShell, dialogTitle, message, status, displayMask);
	// TODO Auto-generated constructor stub
    }

}
