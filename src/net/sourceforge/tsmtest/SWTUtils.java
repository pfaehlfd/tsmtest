 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Bernhard Wetzel - added comments, fix
 *******************************************************************************/
package net.sourceforge.tsmtest;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.tsmtest.datamodel.EditorPartInput;
import net.sourceforge.tsmtest.datamodel.MultiPageEditorPartInput;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class SWTUtils {

    /**
     * Contains most known listener types. Some listener IDs have to be
     * represented as numbers because the constants are not visible.
     */
    public static final int[] listenerEventTypes = { 3007, 3011, SWT.Resize,
	    SWT.Move, SWT.Dispose, SWT.DragDetect, 3000, SWT.FocusIn,
	    SWT.FocusOut, SWT.Gesture, SWT.Help, SWT.KeyUp, SWT.KeyDown, 3001,
	    3002, SWT.MenuDetect, SWT.Modify, SWT.MouseDown, SWT.MouseUp,
	    SWT.MouseDoubleClick, SWT.MouseMove, SWT.MouseEnter, SWT.MouseExit,
	    SWT.MouseHover, SWT.MouseWheel, SWT.Paint, 3008, SWT.Selection,
	    SWT.Touch, SWT.Traverse, 3005, SWT.Verify, 3009, 3010 };

    /**
     * @param control
     *            to remove all listeners from
     * @return a map which contains all removed listeners and their type for
     *         later restoration with <code>restoreAllListeners()</code>
     */
    public static Map<Integer, Listener[]> removeAllListeners(
	    final Control control) {
	final Map<Integer, Listener[]> savedListeners = new HashMap<Integer, Listener[]>();
	// iterate through all listener types
	for (final int eventType : SWTUtils.listenerEventTypes) {
	    final Listener[] listeners = control.getListeners(eventType);
	    savedListeners.put(eventType, listeners);
	    for (final Listener listener : listeners) {
		control.removeListener(eventType, listener);
	    }
	}
	// return the removed listeners
	return savedListeners;
    }

    /**
     * Restore all listeners to this control which were removed earlier with
     * <code>removeAllListeners()</code>.
     * 
     * @param control
     *            The control to restore the listeners to.
     * @param listeners
     *            The map of listeners which were removed.
     */
    public static void restoreAllListeners(final Control control,
	    final Map<Integer, Listener[]> listeners) {
	for (final int eventType : listeners.keySet()) {
	    for (final Listener listener : listeners.get(eventType)) {
		control.addListener(eventType, listener);
	    }
	}
    }

    /**
     * returns the currently open Editor associated with the given test case
     * 
     * @param testCase
     * @return IEditorPart currently opened
     */
    public static IEditorPart findOpenEditor(final TSMTestCase testCase) {
	return findOpenEditor((TSMResource) testCase);
    }

    /**
     * returns the currently open Editor associated with the given protocol
     * 
     * @param testCase
     * @return IEditorPart currently opened
     */
    public static IEditorPart findOpenEditor(final TSMReport report) {
	return findOpenEditor((TSMResource) report);
    }

    /**
     * @param resource
     *            TSMResource which an editor has as input
     * @return IEditorPart which has the parameter as input or null if no such
     *         editor is open.
     */
    public static IEditorPart findOpenEditor(final TSMResource resource) {
	if (resource == null) {
	    return null;
	}
	final IEditorReference[] references = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage()
		.getEditorReferences();
	for (final IEditorReference ref : references) {
	    final IEditorPart editor = ref.getEditor(false);
	    if (editor instanceof EditorPartInput) {
		final EditorPartInput tsmEditor = (EditorPartInput) editor;
		if (tsmEditor.getTestCaseInput() != null
			&& tsmEditor.getTestCaseInput().equals(resource)) {
		    return editor;
		}
		if (tsmEditor.getReportInput() != null
			&& tsmEditor.getReportInput().equals(resource)) {
		    return editor;
		}
	    } else if (editor instanceof MultiPageEditorPartInput) {
		final MultiPageEditorPartInput tsmEditor = (MultiPageEditorPartInput) editor;
		if (tsmEditor.getTestCaseInput() != null
			&& tsmEditor.getTestCaseInput().equals(resource)) {
		    return editor;
		}
		if (tsmEditor.getReportInput() != null
			&& tsmEditor.getReportInput().equals(resource)) {
		    return editor;
		}
	    }
	}
	return null;
    }

    public static void openWizard(final String id) {
	// First see if this is a "new wizard".
	IWizardDescriptor descriptor = PlatformUI.getWorkbench()
		.getNewWizardRegistry().findWizard(id);
	// If not check if it is an "import wizard".
	if (descriptor == null) {
	    descriptor = PlatformUI.getWorkbench().getImportWizardRegistry()
		    .findWizard(id);
	}
	// Or maybe an export wizard
	if (descriptor == null) {
	    descriptor = PlatformUI.getWorkbench().getExportWizardRegistry()
		    .findWizard(id);
	}
	try {
	    // Then if we have a wizard, open it.
	    if (descriptor != null) {
		final IWizard wizard = descriptor.createWizard();
		final WizardDialog wd = new WizardDialog(Display.getDefault()
			.getActiveShell(), wizard);
		wd.setTitle(wizard.getWindowTitle());
		wd.open();
	    }
	} catch (final CoreException e) {
	    e.printStackTrace();
	}
    }
}
