/*******************************************************************************
 * Copyright (c) 2014 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 	Tobias Hirning - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.io.vcs.settings;

import java.io.File;

import net.sourceforge.tsmtest.Activator;
import net.sourceforge.tsmtest.preferences.PreferenceConstants;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Tobias Hirning
 * This class holds the settings for version control systems like paths to executable and environment settings.
 */
public class VCSSettings {
    public static enum VCS_INSTALL_STATUS {OK, NO_PATH, NO_FILE, NOT_EXECUTABLE};
    private VCSSettings() {
    }
    
    /**
     * @return True if the subversion path points to an available and executable file. False otherwise.
     */
    public static boolean isSubversionInstalledCorrectly() {
	return (checkSubversionInstallation() == VCS_INSTALL_STATUS.OK);
    }
    
    /**
     * @return true if user activated subversion support, false otherwise.
     */
    public static boolean isSubversionSupportEnabled() {
	return Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.FIELD_SUBVERSION_SUPPORT);
    }
    
    /**
     * @return The absolute path to the svn client executable as it is stored in the preference store.
     */
    public static String getSubversionPath() {
	return Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.FIELD_SUBVERSION_PATH);
    }

    /**
     * Checks whether the svn client installation is available and executable.
     * @return VCS_INSTALL_STATUS indicating whether and which error occurred.
     */
    private static VCS_INSTALL_STATUS checkSubversionInstallation() {
	//Get preference store
	IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	String subversionPath = store.getString("SUBVERSION_PATH");
	
	//Check whether the path from the preference store is valid.
	return checkInstallationPath(subversionPath);
    }
    
    /**
     * @param path The path to check.
     * @return VCS_INSTALL_STATUS indicating whether and which error occurred.
     */
    public static VCS_INSTALL_STATUS checkInstallationPath (String path) {	
	//Check if any path is given.
	if (path != null && !path.isEmpty()) {
	    File file = new File(path);
	    //Check if it is a file.
	    if (file.exists() && !file.isDirectory()) {
		//Check if it can be executed.
		if (file.canExecute()) {
		    return VCS_INSTALL_STATUS.OK;
		} else {
		    return VCS_INSTALL_STATUS.NOT_EXECUTABLE;
		}
	    } else {
		return VCS_INSTALL_STATUS.NO_FILE;
	    }
	}
	return VCS_INSTALL_STATUS.NO_PATH;
    }
    
}
