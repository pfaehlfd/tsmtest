 /*******************************************************************************
 * Copyright (c) 2011 Robert Wloch.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Wloch - initial version
 *    Albert Flaig - enhancements
 *    Bernhard Wetzel - fix, added comments
 *    Daniel Hertl - enhancements
 *    Tobias Hirning - refactoring, i18n
 *******************************************************************************/
package net.sourceforge.tsmtest;

import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.preferences.PreferenceConstants;
import net.sourceforge.tsmtest.preferences.PreferenceManager;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

/**
 * @author Albert Flaig
 * @see {@link http://www.robertwloch.net/2011/01/eclipse-tips-tricks-property-testers-with-command-core-expressions/}
 */
public class TSMPropertyTester extends PropertyTester {
    public static final String PROPERTY_IS_TESTER = "isTester"; //$NON-NLS-1$
    public static final String PROPERTY_IS_TEST_MANAGER = "isTestManager"; //$NON-NLS-1$
    public static final String PROPERTY_IS_SELECTION_TEST_CASE = "isSelectionTestCase"; //$NON-NLS-1$
    public static final String PROPERTY_NAMESPACE = "net.sourceforge.tsmtest"; //$NON-NLS-1$

    public TSMPropertyTester() {
	
    }
    /*
     * (non-Javadoc)
     * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
     * checking Property type for (Tester,Test manager, Test case)
     */
    @Override
    public boolean test(Object receiver, String property, Object[] args,
	    Object expectedValue) {
	if (PROPERTY_IS_TESTER.equals(property)) {
	    return PreferenceManager.getInstance().getPreferences().getRole() == PreferenceConstants.ROLE_TESTER;
	}
	if (PROPERTY_IS_TEST_MANAGER.equals(property)) {
	    return PreferenceManager.getInstance().getPreferences().getRole() == PreferenceConstants.ROLE_TEST_MANAGER;
	}
	if (PROPERTY_IS_SELECTION_TEST_CASE.equals(property)) {
	    return (SelectionManager.getInstance().getSelection().getFirstFile() instanceof TSMTestCase);
	}
	return false;
    }
    
    /**
     * Refreshes the property state to disable/enable the rights of test manager
     */
    public static void refresh() {
	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	IEvaluationService evaluationService = (IEvaluationService) window.getService(IEvaluationService.class);
	if (evaluationService != null) {
		evaluationService.requestEvaluation(PROPERTY_NAMESPACE + "." + PROPERTY_IS_TESTER); //$NON-NLS-1$
		evaluationService.requestEvaluation(PROPERTY_NAMESPACE + "." + PROPERTY_IS_TEST_MANAGER); //$NON-NLS-1$
		evaluationService.requestEvaluation(PROPERTY_NAMESPACE + "." + PROPERTY_IS_SELECTION_TEST_CASE); //$NON-NLS-1$
	}
    }
}
