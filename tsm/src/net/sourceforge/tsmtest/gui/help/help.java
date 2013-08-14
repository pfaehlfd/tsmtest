 /*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Verena Käfer - initial version
 *    Tobias Hirning - disabled code
 *******************************************************************************/ 
package net.sourceforge.tsmtest.gui.help;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import net.sourceforge.tsmtest.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;



public class help extends AbstractHandler{
    public static final String ID = "net.sourceforge.tsmtest.gui.help.help";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
//	// TODO Auto-generated method stub
//	final Bundle manualBundle = Platform.getBundle(Activator.PLUGIN_ID);
//	URL manualUrl = manualBundle.getEntry("manual/Handbuch.pdf");
//	File file = null;
//	try {
//	    file = new File(FileLocator.resolve(manualUrl).toURI());
//	    Desktop.getDesktop().open(file);
//	} catch (IOException e) {
//	    // TODO Auto-generated catch block
//	    e.printStackTrace();
//	} catch (URISyntaxException e) {
//	    // TODO Auto-generated catch block
//	    e.printStackTrace();
//	}
	return null;
    }

}
