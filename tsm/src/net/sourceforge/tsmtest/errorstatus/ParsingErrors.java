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
package net.sourceforge.tsmtest.errorstatus;

import org.eclipse.core.runtime.IStatus;

public class ParsingErrors implements IStatus{

    @Override
    public IStatus[] getChildren() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int getCode() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public Throwable getException() {
	
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getMessage() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getPlugin() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int getSeverity() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public boolean isMultiStatus() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean isOK() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean matches(int severityMask) {
	// TODO Auto-generated method stub
	return false;
    }

}
