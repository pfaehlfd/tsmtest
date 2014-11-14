/**
 * This class holds the settings for version control systems like paths to executable and environment settings.
 */
package net.sourceforge.tsmtest.io.vcs.settings;

import java.io.File;

import net.sourceforge.tsmtest.Activator;
import net.sourceforge.tsmtest.preferences.PreferencePage;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Tobias Hirning
 *
 */
public class VCSSettings {
    public static enum VCS_INSTALL_STATUS {OK, NO_PATH, NO_FILE, NOT_EXECUTABLE};
    private VCSSettings() {
    }
    
    /**
     * @return True if the subversion path points to an available and executable file. False otherwise.
     */
    public static boolean isSubversionInstalled() {
	if (checkSubversionInstallation() == VCS_INSTALL_STATUS.OK) {
	    return true;
	}
	return false;
    }
    
    /**
     * @return true if user activated subversion support, false otherwise.
     */
    public static boolean subversionSupportEnabled() {
	IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	String subversionPath = store.getString(PreferencePage.FIELD_SUBVERSION_PATH);
	
	//Check if path is valid.
	if (checkInstallationPath(subversionPath) == VCS_INSTALL_STATUS.OK) {
	    return true;
	} else {
	    return false;
	}
    }
    
    /**
     * @return The absolute path to the svn client executable as it is stored in the preference store.
     */
    public static String getSubversionPath() {
	IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	String subversionPath = store.getString(PreferencePage.FIELD_SUBVERSION_PATH);
	return subversionPath;
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
