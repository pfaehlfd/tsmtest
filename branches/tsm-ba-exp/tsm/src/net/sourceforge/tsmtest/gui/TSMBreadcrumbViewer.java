 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.gui;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.AbstractDataModel.DataModelObservable;
import net.sourceforge.tsmtest.datamodel.providers.TSMHierarchyContentProvider;
import net.sourceforge.tsmtest.datamodel.providers.TSMResourceLabelProvider;
import net.sourceforge.tsmtest.datamodel.providers.TSMTooltipLabelProvider;
import net.sourceforge.tsmtest.datamodel.providers.TSMViewerComparator;
import net.sourceforge.tsmtest.gui.breadcrumb.BreadcrumbViewer;
import net.sourceforge.tsmtest.gui.breadcrumb.IMenuSelectionListener;
import net.sourceforge.tsmtest.gui.breadcrumb.MenuSelectionEvent;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class TSMBreadcrumbViewer extends BreadcrumbViewer implements
	DataModelObservable {

    private final List<IBreadCrumbListener> listenerList = new ArrayList<IBreadCrumbListener>();

    public TSMBreadcrumbViewer(final Composite parent, final int style) {
	super(parent, style);

	setToolTipLabelProvider(TSMTooltipLabelProvider.DEFAULT);
	setContentProvider(TSMHierarchyContentProvider.DEFAULT);
	setLabelProvider(TSMResourceLabelProvider.DEFAULT);
	setComparator(TSMViewerComparator.DEFAULT);
	DataModel.getInstance().register(this);

	// set input to the drop down selection
	addMenuSelectionListener(new IMenuSelectionListener() {
	    @Override
	    public void menuSelect(final MenuSelectionEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) event
			.getSelection();
		if (selection.isEmpty()) {
		    setFocus();
		    return;
		}
		final Object selected = selection.getFirstElement();
		if (selected instanceof TSMReport) {
		    if (!fireBreadCrumbListenerEvent((TSMReport) selected)) {
			setInput(selection.getFirstElement());
			setSelection(selection);
			setFocus();
		    }
		}
		if (selected instanceof TSMTestCase) {
		    if (!fireBreadCrumbListenerEvent((TSMTestCase) selected)) {
			setInput(selection.getFirstElement());
			setSelection(selection);
			setFocus();
		    }
		}
	    }
	});
	addOpenListener(new IOpenListener() {
	    @Override
	    public void open(final OpenEvent event) {
		final Object element = ((IStructuredSelection) event
			.getSelection()).getFirstElement();
		if (element != null) {
		    if (element instanceof TSMReport) {
			if (!fireBreadCrumbListenerEvent((TSMReport) element)) {
			    setInput(element);
			}
		    } else if (element instanceof TSMTestCase) {
			if (!fireBreadCrumbListenerEvent((TSMTestCase) element)) {
			    setInput(element);
			}
		    }
		}
	    }
	});
	// addDoubleClickListener(new IDoubleClickListener() {
	// @Override
	// public void doubleClick(final DoubleClickEvent event) {
	// final Object element = ((IStructuredSelection) event
	// .getSelection()).getFirstElement();
	// if (element != null) {
	// if (element instanceof TSMTestCase) {
	// if (!fireBreadCrumbListenerEvent((TSMTestCase) element)) {
	// setInput(element);
	// }
	// }
	// }
	// }
	// });
    }

    /**
     * @param report
     * @return whether the event should be consumed.
     */
    private final boolean fireBreadCrumbListenerEvent(final TSMReport report) {
	boolean consume = false;
	for (final IBreadCrumbListener listener : listenerList) {
	    if (listener.selectionChanged(report)) {
		consume = true;
	    }
	}
	return consume;
    }

    /**
     * @param testCase
     * @return whether the event should be consumed.
     */
    private final boolean fireBreadCrumbListenerEvent(final TSMTestCase testCase) {
	boolean consume = false;
	for (final IBreadCrumbListener listener : listenerList) {
	    if (listener.selectionChanged(testCase)) {
		consume = true;
	    }
	}
	return consume;
    }

    public void addBreadCrumbListener(final IBreadCrumbListener listener) {
	listenerList.add(listener);
    }

    public void removeBreadCrumbListener(final IBreadCrumbListener listener) {
	listenerList.remove(listener);
    }

    public void setLayoutData(final Object layoutData) {
	getControl().setLayoutData(layoutData);
    }

    @Override
    protected void configureDropDownViewer(final TreeViewer viewer,
	    final Object input) {
	// copy values
	viewer.setContentProvider(getContentProvider());
	viewer.setLabelProvider(getLabelProvider());
	viewer.setComparator(getComparator());
	viewer.setComparer(getComparer());
	viewer.setFilters(getFilters());

	// set initial selection
	viewer.setSelection(getSelection());
    }

    @Override
    public void dataModelChanged() {
	Display.getDefault().syncExec(new Runnable() {
	    @Override
	    public void run() {
		// check if the current input has been changed.
		final Object input = getInput();
		if (input instanceof TSMTestCase) {
		    setInput(((TSMTestCase) input).getNewestVersion());
		} else if (input instanceof TSMReport) {
		    setInput(((TSMReport) input).getNewestVersion());
		}
	    }
	});
    }
}
