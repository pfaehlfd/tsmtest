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
package net.sourceforge.tsmtest.io.vcs.svn;

import java.io.IOException;
import java.util.List;

import net.sourceforge.tsmtest.io.vcs.settings.VCSSettings;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * @author Tobias Hirning
 * This class provides version control support for the workspace using Subversion from the command line.
 */
public final class SubversionWrapper {
    private static final Logger log = Logger.getLogger(SubversionWrapper.class);
    private SubversionWrapper() {
    }
    
    /**
     * Checks whether a given file or directory is under subversion control.
     * @param path The path to check.
     * @return True, if under version control. False otherwise.
     */
    public static final boolean isUnderVersionControl (String path) {
	try {
	    //Build the command: svn info <path>
	    ProcessBuilder processBuilder = new ProcessBuilder(VCSSettings.getSubversionPath(), "info", path);
	    Process process = processBuilder.start();
	    process.waitFor();

	    if (process.exitValue() != 0) {
		log.debug(path + " is not under version control.");
		return false;
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return true;
    }

    /**
     * Commits a given path to the repository using "svn commit".
     * @param path The path of the file or directory to commit.
     * @return The return code of the svn command.
     */
    public static final int commit(String path) {
	try {
	    //Build the command: svn commit <path> -mAuto commit of report.
	    //Don't put a space between the "-m" and the commit message and don't enclose it by quotation marks
	    //otherwise it won't work correctly.
	    ProcessBuilder processBuilder = new ProcessBuilder("svn", "commit", path, "-mAuto commit by TSM.");
	    Process process = processBuilder.start();
	    process.waitFor();
	   
	    //If return value is not equal than 0 the svn client has reported an error.
	    if (process.exitValue() != 0) {
		log.debug(path + " could not be committed.");
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return 0;
    }

    /**
     * Commits a given list of files to the repository using "svn commit".
     * The files have to be added first using {@link net.sourceforge.tsmtest.io.vcs.svn.SubversionWrapper#addForCommitVersion() 
     * addForCommitVersion()}
     * @param fileList The list of files to commit.
     * @return The return code of the svn command.
     */
    public static final int commitMultipleFiles(List<String> fileList) {
	//Build the command: svn commit -m "Auto commit of multiple files." <fileList>
	fileList.add(0, VCSSettings.getSubversionPath());
	fileList.add(1, "commit");
	//Don't put a space between the "-m" and the commit message and don't enclose it by quotation marks
	//otherwise it won't work correctly.
	fileList.add(2, "-mAuto commit of multiple files by TSM.");

	//Convert the list into an string array which is needed by the ProcessBuilder.
	String[] commandAndFileArray = new String[fileList.size()];
	for (int index = 0; index < fileList.size(); index++) {
	    commandAndFileArray[index] = fileList.get(index);
	}
	
	ProcessBuilder processBuilder = new ProcessBuilder(commandAndFileArray);
	try {
	    Process process = processBuilder.start();
	    process.waitFor();

	    //If return value is not equal than 0 the svn client has reported an error.
	    if (process.exitValue() != 0) {
		String path = "";
		for (int index = 2; index < fileList.size(); index++) {
		    path = fileList.get(index);
		    log.debug(path + " could not be committed.");
		}

	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return 0;
    }
    
    /**
     * Updates the given resource using "svn update".
     * @param updateResource The resource to update.
     * @return The return code of the svn command.
     */
    public static final boolean update(String updateResource) {
	//Build the command: svn update updateResource
	ProcessBuilder processBuilder = new ProcessBuilder(VCSSettings.getSubversionPath(), "update", updateResource);
	try {
	    Process process = processBuilder.start();
	    process.waitFor();

	    if (process.exitValue() != 0) {
		log.debug(updateResource + " could not be updated.");
		return false;
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return true;
    }
    
    /**
     * Updates the whole workspace using "svn update".
     * @return The return code of the svn command.
     */
    public static final boolean updateWorkspace() {
	//Build the command: svn update <workspace>
	ProcessBuilder processBuilder = new ProcessBuilder(VCSSettings.getSubversionPath(), "update", 
		ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
	try {
	    Process process = processBuilder.start();
	    process.waitFor();

	    if (process.exitValue() != 0) {
		log.debug("Workspace could not be updated.");
		return false;
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return true;
    }
    
    /**
     * Adds the file at the given path to version control.
     * @param path The path to the file to be added to version control.
     * @return 0 if no error occurred or if path is already under version control,
     * -1 if an IOException occurred, -2 if a InterruptedException occurred, otherwise the return value of the svn command.
     */
    public static final int addForCommit(String path) {
	//Check if path is already under version control.
	if (isUnderVersionControl(path)) {
	    return 0;
	}
	try {
	    ProcessBuilder processBuilder = new ProcessBuilder(VCSSettings.getSubversionPath(), "add", path);
	    Process process = processBuilder.start();
	    process.waitFor();

	    //If return value is not equal than 0 the svn client has reported an error.
	    if (process.exitValue() != 0) {
		log.debug(path + " could not be added.");
		return process.exitValue();
	    } else {
		//Everything was fine.
		return 0;
	    }
	    
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return -1;
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return -2;
	}
    }
    
    public static final int delete(String path) {
	//Check if path is under version control. If not we can not execute "svn delete".
	if (!isUnderVersionControl(path)) {
	    return -3;
	}
	try {
	    ProcessBuilder processBuilder = new ProcessBuilder(VCSSettings.getSubversionPath(), "delete", path);
	    Process process = processBuilder.start();
	    process.waitFor();

	    //If return value is not equal than 0 the svn client has reported an error.
	    if (process.exitValue() != 0) {
		log.debug(path + "could not be deleted.");
		return process.exitValue();
	    } else {
		return 0;
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return -1;
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return -2;
	}
    }
}
