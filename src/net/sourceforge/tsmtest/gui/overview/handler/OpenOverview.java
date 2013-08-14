 /*******************************************************************************
 * Copyright (c) 2012-2013 Bernhard Wetzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bernhard Wetzel - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.overview.handler;

import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionModel;
import net.sourceforge.tsmtest.gui.overview.view.Overview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * @author Bernhard Wetzel
 *
 */
public class OpenOverview extends AbstractHandler {
    public static final String ID = "net.sourceforge.tsmtest.gui.overview.view.overview"; //$NON-NLS-1$

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	SelectionModel sm = SelectionManager.instance.getSelection();
	return Overview.openGUI(sm);
    }
    
}
