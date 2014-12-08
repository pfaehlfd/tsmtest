 /*******************************************************************************
 * Copyright (c) 2012-2013 Jenny Krüwald.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jenny Krüwald - initial version
 *    Tobias Hirning - code cleanup
 *******************************************************************************/ 
package net.sourceforge.tsmtest.gui.filter;

import java.util.ArrayList;

/**
 * 
 * @author Jenny Krüwald
 * 
 */
public final class FilterManager {
    /**
     * Instance of the FilterManager.
     */
    private static volatile FilterManager instance;

    private ArrayList<FilterListener> listeners = new ArrayList<FilterListener>();

    private FilterManager() {

    }
    
    /**
     * FilterManager singleton.
     * 
     * @return instance of FilterManager
     */
    public static FilterManager getInstance() {
	if (instance == null) {
	    instance = new FilterManager();
	}
	return instance;
    }

    /**
     * Register for Preferences changes. Should be called to start getting
     * preferences change events.
     * 
     * @param listener
     *            the object which receives preference updates
     * @usage <code>PreferenceManager.getInstance().register(this);</code>
     */
    public void register(FilterListener listener) {
	listeners.add((FilterListener) listener);
    }

    /**
     * Unregister for Preference changes. Should be called when preferences
     * updates are not needed anymore.
     * 
     * @param listener
     *            the object which receives selection updates
     * @usage <code>PreferenceManager.getInstance().unregister(this);</code>
     */
    public void unregister(FilterListener listener) {
	listeners.remove((FilterListener) listener);
    }

    /**
     * Must be implemented to receive preference updates.
     */
    public interface FilterListener {
	/**
	 * @param preferenceModel
	 *            stores the preferences.
	 */
	public void filterChanged();
    }

    /**
     * Call when model has changed.
     */
    public void invoke(){
	for(FilterListener listener : listeners) {
	    listener.filterChanged();
	}
    }
}