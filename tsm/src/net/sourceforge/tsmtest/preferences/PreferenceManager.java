 /*******************************************************************************
 * Copyright (c) 2012-2013 Jenny Krüwald.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jenny Krüwald - initial version
 *    Albert Flaig - some fixes
 *******************************************************************************/
package net.sourceforge.tsmtest.preferences;

import java.util.ArrayList;

import net.sourceforge.tsmtest.Activator;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Manages PreferenceChanges. <br>
 * Just use <code>PreferenceManager.instance.register(this);</code> to start
 * getting preference change events and<br>
 * <code>PreferenceManager.instance.unregister(this);</code> when it is not
 * needed anymore. Remember to let your class implement
 * {@link PreferenceListener}.
 * 
 * @author Jenny Krüwald
 * 
 */
public class PreferenceManager {
    /**
     * Returns the Singleton of this class.
     */
    public static final PreferenceManager instance = new PreferenceManager();

    private ArrayList<PreferenceListener> listeners = new ArrayList<PreferenceListener>();

    private IPropertyChangeListener changeListener = new IPropertyChangeListener() {
	@Override
	public void propertyChange(PropertyChangeEvent event) {
	    if (event.getProperty().startsWith("tsm.")) {
		PreferenceModel model = new PreferenceModel();
		if (event.getNewValue().equals("choice1")) {
		    model.setRole(PreferenceConstants.ROLE_TESTER);
		}
		else {
		    model.setRole(PreferenceConstants.ROLE_TEST_MANAGER);
		}
		changePreference(model);
	    }
	}
    };
    /**
     * The current preferences.
     */
    private PreferenceModel preferenceModel = loadPreferences();

    public PreferenceManager() {
	
    }
    
    /**
     * Register for Preferences changes. Should be called to start getting
     * preferences change events.
     * 
     * @param listener
     *            the object which receives preference updates
     * @usage <code>PreferenceManager.instance.register(this);</code>
     */
    public void register(PreferenceListener listener) {
	if (listeners.isEmpty()) {
	    Activator.getDefault().getPreferenceStore()
		    .addPropertyChangeListener(changeListener);
	}
	listeners.add((PreferenceListener) listener);
    }

    private PreferenceModel loadPreferences() {
	// TODO das aktuelle preferenceModel muss aus den Einstellungen geladen werden.
	PreferenceModel pm = new PreferenceModel();
	pm.setRole(PreferenceConstants.ROLE_TEST_MANAGER);
	return pm;
    }

    /**
     * Unregister for Preference changes. Should be called when preferences
     * updates are not needed anymore.
     * 
     * @param listener
     *            the object which receives selection updates
     * @usage <code>PreferenceManager.instance.unregister(this);</code>
     */
    public void unregister(PreferenceListener listener) {
	listeners.remove((PreferenceListener) listener);
	if (listeners.isEmpty()) {
	    Activator.getDefault().getPreferenceStore()
		    .removePropertyChangeListener(changeListener);
	}
    }

    /**
     * Changes the current preference and notifies each listener.
     * 
     * @param preferenceModel
     */
    private void changePreference(PreferenceModel preferenceModel) {
	// Update the property tester
	PreferencePropertyTester.refresh();
	this.preferenceModel = preferenceModel;
	for (PreferenceListener listener : listeners) {
	    listener.preferenceChanged(preferenceModel);
	}
    }

    /**
     * @return the current preferences.
     */
    public PreferenceModel getPreferences() {
	return preferenceModel;
    }

    /**
     * Must be implemented to receive preference updates.
     */
    public interface PreferenceListener {
	/**
	 * @param preferenceModel
	 *            stores the preferences.
	 */
	public void preferenceChanged(PreferenceModel preferenceModel);
    }

    /**
     * This class stores the preferences in form of attributes for easy handling.
     */
    public class PreferenceModel {
	private int role;

	public int getRole() {
	    return role;
	}

	void setRole(int role) {
	    this.role = role;
	}
    }

}