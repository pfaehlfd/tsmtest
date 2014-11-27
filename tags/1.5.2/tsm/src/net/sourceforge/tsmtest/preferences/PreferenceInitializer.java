 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Tobias Hirning - some refactoring
 *    Jenny Krüwald - enhancements
 *******************************************************************************/
package net.sourceforge.tsmtest.preferences;

import net.sourceforge.tsmtest.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Class used to initialize default preference values.
 * @author Albert Flaig
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_CHOICE, "choice2");
		store.setDefault(PreferenceConstants.FIELD_SUBVERSION_SUPPORT, false);
		store.setDefault(PreferenceConstants.FIELD_SUBVERSION_PATH, "/usr/bin/svn");
	}
}
