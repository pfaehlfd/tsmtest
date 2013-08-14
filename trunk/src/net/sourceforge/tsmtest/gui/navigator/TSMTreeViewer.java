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
package net.sourceforge.tsmtest.gui.navigator;

import net.sourceforge.tsmtest.datamodel.DataModelException;
import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.providers.TSMHierarchyContentProvider;
import net.sourceforge.tsmtest.datamodel.providers.TSMResourceLabelProvider;
import net.sourceforge.tsmtest.datamodel.providers.TSMRootElement;
import net.sourceforge.tsmtest.gui.newtestcase.view.ViewTestCase;
import net.sourceforge.tsmtest.gui.report.ViewReport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;

public class TSMTreeViewer extends TreeViewer {

    public TSMTreeViewer(final Composite parent, final int style) {
	super(new Tree(parent, style));
	init();
    }

    private void init() {

	// TODO Different sorters for better usability
	setSorter(new ViewerSorter());
	setContentProvider(TSMHierarchyContentProvider.DEFAULT);
	setLabelProvider(TSMResourceLabelProvider.DEFAULT);
	setInput(TSMRootElement.getInstance());

	expandAll();
	addDoubleClickListener(new IDoubleClickListener() {
	    @Override
	    public void doubleClick(final DoubleClickEvent event) {
		final Object element = ((IStructuredSelection) event
			.getSelection()).getFirstElement();
		if (element != null) {
		    if (element instanceof TSMTestCase) {
			try {
			    ViewTestCase.openGUI((TSMTestCase) element);
			} catch (final PartInitException e) {
			    e.printStackTrace();
			    // Do Nothing
			}
		    }
		    if (element instanceof TSMReport) {
			try {
			    ViewReport.openGUI((TSMReport) element);
			} catch (final PartInitException e) {
			    e.printStackTrace();
			    // Do Nothing
			}
		    }
		    if (element instanceof TSMContainer) {
			if (getExpandedState(element)) {
			    collapseToLevel(element, 1);
			} else {
			    expandToLevel(element, 1);
			}
		    }
		}
	    }
	});

	setCellEditors(new CellEditor[] { new TextCellEditor(getTree()) });
	setColumnProperties(new String[] { "" });
	setCellModifier(new ICellModifier() {

	    @Override
	    public boolean canModify(final Object element, final String property) {
		return true;
	    }

	    @Override
	    public Object getValue(final Object element, final String property) {
		return ((TSMResource) element).getName();
	    }

	    @Override
	    public void modify(final Object element, final String property,
		    final Object value) {
		final TreeItem item = (TreeItem) element;
		try {
		    boolean isValid = true;
		    final String name = value.toString();
		    if (name.trim().equals("")) {
			isValid = false;
		    }
		    if (name.matches(".*[<>?|\".:_\\*/].*")
			    || name.matches(".*\\\\.*")) {
			isValid = false;
		    }
		    if (isValid) {
			((TSMResource) item.getData()).rename(name.trim());
			TSMTreeViewer.this.update(item.getData(), null);
		    }
		} catch (final DataModelException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	});

	TreeViewerEditor.create(this, new ColumnViewerEditorActivationStrategy(
		this) {
	    @Override
	    protected boolean isEditorActivationEvent(
		    final ColumnViewerEditorActivationEvent event) {
		return event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
	    }
	}, ColumnViewerEditor.DEFAULT);
    }

    public void editElement(final Object element) {
	editElement(element, 0);
    }

    public void addListener(final int eventType, final Listener listener) {
	getTree().addListener(eventType, listener);
    }

}
