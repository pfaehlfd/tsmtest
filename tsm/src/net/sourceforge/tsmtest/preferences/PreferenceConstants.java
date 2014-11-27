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

/**
 * Constant definitions for plug-in preferences
 * @author Albert Flaig
 */
public class PreferenceConstants {
    public static final String P_CHOICE = "tsm.choicePreference";

    //Role constants.
    public static final int ROLE_TESTER = 0;
    public static final int ROLE_TEST_MANAGER = 1;

    //Subversion constants.
    public static final String FIELD_SUBVERSION_SUPPORT = "subversionSupport";
    public static final String FIELD_SUBVERSION_PATH = "subversionPath";
}
