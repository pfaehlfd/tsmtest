 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Jenny Krüwald - enhancements
 *    Tobias Hirning - some refactoring, i18n
 *******************************************************************************/
package net.sourceforge.tsmtest.preferences;

import net.sourceforge.tsmtest.Activator;
import net.sourceforge.tsmtest.io.vcs.settings.VCSSettings;
import net.sourceforge.tsmtest.io.vcs.settings.VCSSettings.VCS_INSTALL_STATUS;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class PreferencePage extends FieldEditorPreferencePage implements
	IWorkbenchPreferencePage {
   
    FileFieldEditor subversionPathEditor;
    BooleanFieldEditor subversionSupportBooleanFieldEditor;

    public PreferencePage() {
	super(GRID);
	setPreferenceStore(Activator.getDefault().getPreferenceStore());
	setDescription(""); //$NON-NLS-1$
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    protected void createFieldEditors() {
	subversionSupportBooleanFieldEditor = new BooleanFieldEditor
		(PreferenceConstants.FIELD_SUBVERSION_SUPPORT, "Enable Subversion support", BooleanFieldEditor.DEFAULT, getFieldEditorParent());
	subversionSupportBooleanFieldEditor.setPreferenceStore(Activator.getDefault().getPreferenceStore());
	subversionSupportBooleanFieldEditor.load();

	subversionPathEditor = new FileFieldEditor("SubversionPathEditor", "Subversion client executable: ", true, 
		FileFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent());
	subversionPathEditor.setStringValue(VCSSettings.getSubversionPath());

	IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	subversionPathEditor.setEnabled(store.getBoolean(PreferenceConstants.FIELD_SUBVERSION_SUPPORT), getFieldEditorParent());
	subversionPathEditor.setPreferenceStore(Activator.getDefault().getPreferenceStore());

	addField(subversionSupportBooleanFieldEditor);
    	addField(subversionPathEditor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
	setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
     */
    @Override
    protected IPreferenceStore doGetPreferenceStore() {
	return Activator.getDefault().getPreferenceStore();
    }

    /**
     * FIXME
     */
    private void storeValues() {
	IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	store.setValue(PreferenceConstants.FIELD_SUBVERSION_SUPPORT, subversionSupportBooleanFieldEditor.getBooleanValue());
	store.setValue(PreferenceConstants.FIELD_SUBVERSION_PATH, subversionPathEditor.getStringValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#checkState()
     */
    @Override
    protected void checkState() {
	super.checkState();
	VCS_INSTALL_STATUS vcsStatus = VCSSettings.checkInstallationPath(subversionPathEditor.getStringValue());
	Boolean subversionSupport = subversionSupportBooleanFieldEditor.getBooleanValue();
	//Set error messages if subversion support is enabled and subversion path is invalid.
	if (subversionSupport) {
	    if (vcsStatus == VCS_INSTALL_STATUS.NO_FILE) {
		setErrorMessage("No file");
		setValid(false);
	    } else if (vcsStatus == VCS_INSTALL_STATUS.NO_PATH) {
		setErrorMessage("No path");
		setValid(false);
	    } else if (vcsStatus == VCS_INSTALL_STATUS.NOT_EXECUTABLE) {
		setErrorMessage("Not executable");
		setValid(false);
	    } else if (vcsStatus == VCS_INSTALL_STATUS.OK) {
		setErrorMessage(null);
		setValid(true);
	    }
	} else {
	    setErrorMessage(null);
	    setValid(true);
	}
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
	if ("field_editor_value".equalsIgnoreCase(event.getProperty())) {
	    Object eventNewValue = event.getNewValue();
	    if (eventNewValue instanceof Boolean) {
		Boolean enabled = (Boolean)eventNewValue;
		subversionPathEditor.setEnabled(enabled, getFieldEditorParent());
	    }
	}
	super.propertyChange(event);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performDefaults()
     */
    @Override
    public void performDefaults() {
	subversionPathEditor.loadDefault();
	subversionSupportBooleanFieldEditor.loadDefault();
	super.performDefaults();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
	//Save values.
	storeValues();
	return super.performOk();
    }
}