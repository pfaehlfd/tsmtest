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
 *******************************************************************************/
package net.sourceforge.tsmtest.preferences;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

/**
 * @author Albert Flaig
 * @see {@link http://www.robertwloch.net/2011/01/eclipse-tips-tricks-property-testers-with-command-core-expressions/}
 */
public class PreferencePropertyTester extends PropertyTester {
    public static final String PROPERTY_IS_TESTER = "isTester";
    public static final String PROPERTY_IS_TEST_MANAGER = "isTestManager";
    public static final String PROPERTY_NAMESPACE = "net.sourceforge.tsmtest.preferences";

    public PreferencePropertyTester() {
	
    }
    
    @Override
    public boolean test(Object receiver, String property, Object[] args,
	    Object expectedValue) {
	if (PROPERTY_IS_TESTER.equals(property)) {
	    return PreferenceManager.getInstance().getPreferences().getRole() == PreferenceConstants.ROLE_TESTER;
	}
	if (PROPERTY_IS_TEST_MANAGER.equals(property)) {
	    return PreferenceManager.getInstance().getPreferences().getRole() == PreferenceConstants.ROLE_TEST_MANAGER;
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
		evaluationService.requestEvaluation(PROPERTY_NAMESPACE + "." + PROPERTY_IS_TESTER);
		evaluationService.requestEvaluation(PROPERTY_NAMESPACE + "." + PROPERTY_IS_TEST_MANAGER);
	}
    }
}
