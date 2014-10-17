 /*******************************************************************************
 * Copyright (c) 2012-2014 Tobias Hirning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Hirning - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.wizards;

import java.util.BitSet;

/**
 * Singleton class that holds the chosen export filter settings.
 * @author Tobias Hirning
 *
 */
public final class ExportFilter {
    /**
     * Indicates if the filter is enabled.
     */
    private static boolean filterEnabled = false;
    
    /**
     * The bit set represents the chosen filter options.
     * Order is:
     * [0] - passed
     * [1] - passed with annotations
     * [2] - failed
     * [3] - not executed
     * [4] - low
     * [5] - medium
     * [6] - high
     */
    private static BitSet filterBitSet = new BitSet();    
    
    private ExportFilter() {
    }

    /**
     * @param exportPassed Whether test case protocols with status "passed" should be exported.
     */
    public static void setExportPassed(boolean exportPassed) {
        if (exportPassed) {
            filterBitSet.set(0);
        } else {
            filterBitSet.clear(0);
        }
    }

    /**
     * @param exportPassedWithAnnotations 
     * 		Whether test case protocols with status "passed with annotations" should be exported.
     */
    public static void setExportPassedWithAnnotations(boolean exportPassedWithAnnotations) {
	if (exportPassedWithAnnotations) {
	    filterBitSet.set(1);
	} else {
	    filterBitSet.clear(1);
	}
    }

    /**
     * @param exportFailed Whether test case protocols with status "failed" should be exported.
     */
    public static void setExportFailed(boolean exportFailed) {
        if (exportFailed) {
            filterBitSet.set(2);
        } else {
            filterBitSet.clear(2);
        }
    }

    /**
     * @param exportNotExecuted Whether test case protocols with status "not executed" should be exported.
     */
    public static void setExportNotExecuted(boolean exportNotExecuted) {
        if (exportNotExecuted) {
            filterBitSet.set(3);
        } else {
            filterBitSet.clear(3);
        }
    }

    /**
     * @param exportLow Whether test case protocols with low priority should be exported.
     */
    public static void setExportLow(boolean exportLow) {
	if(exportLow) {
	    filterBitSet.set(4);
	} else {
	    filterBitSet.clear(4);
	}
    }

    /**
     * @param exportMedium Whether test case protocols with medium priority should be exported.
     */
    public static void setExportMedium(boolean exportMedium) {
	if (exportMedium) {
	    filterBitSet.set(5);
	} else {
	    filterBitSet.clear(5);
	}
    }

    /**
     * @param exportHigh Whether test case protocols with high priority should be exported.
     */
    public static void setExportHigh(boolean exportHigh) {
        if (exportHigh) {
            filterBitSet.set(6);
        } else {
            filterBitSet.clear(6);
        }
    }

    /**
     * @return True, if the filter is enabled.
     */
    public static boolean getFilterEnabled() {
	return filterEnabled;
    }
    
    /**
     * @param enableFilter Whether the filter should be applied.
     */
    public static void setFilterEnabled(boolean enableFilter) {
	filterEnabled = enableFilter;
    }
    
    /**
     * @return The filter settings as a BitSet.
     */
    public static BitSet getFilterBitSet() {
	if(filterBitSet == null) {
	    filterBitSet = new BitSet();
	}
	return filterBitSet;
    }
    
    /**
     * Initializes the BitSet of the filter: All values are true, so that every report gets exported 
     * (which is the default setting in the gui).
     */
    public static void initializeFilterBitSet() {
	filterBitSet.set(0);
	filterBitSet.set(1);
	filterBitSet.set(2);
	filterBitSet.set(3);
	filterBitSet.set(4);
	filterBitSet.set(5);
	filterBitSet.set(6);
    }
}
