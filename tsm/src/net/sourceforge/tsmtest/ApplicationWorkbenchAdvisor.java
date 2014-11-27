 /*******************************************************************************
 * Copyright (c) 2012-2013 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobbias Hirning - initial version
 *    Florian Krüger - enhancements
 *    Bernhard Wetzel - added comments
 *    Verena Käfer - fix
 *******************************************************************************/
package net.sourceforge.tsmtest;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
    	
    	/**
    	 * The workbench window advisor object is created in response to a workbench window being created 
    	 * (one per window), and is used to configure the window. 
    	 */
	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}
	/**
	 * Returns the Perspectives ID
	 */
	public String getInitialWindowPerspectiveId() {
		return net.sourceforge.tsmtest.Perspective.ID;
	} 
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
	 */
	public void initialize(IWorkbenchConfigurer configurer) {
	    super.initialize(configurer);
	    IDE.registerAdapters();
	}
}
