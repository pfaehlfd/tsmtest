/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 * 	Wolfgang Kraus - fixed disappearing contents of the navigator
 * 	Jenny Krüwald - created the filterview
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel.providers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.datamodel.TSMProject;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Albert Flaig
 *
 */
public class TSMHierarchyContentProvider implements ITreeContentProvider {

    public final static TSMHierarchyContentProvider DEFAULT = new TSMHierarchyContentProvider();

    @Override
    public void dispose() {
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
	if (parentElement instanceof TSMContainer) {
	    final TSMContainer container = (TSMContainer) parentElement;
	    final List<TSMResource> children = new ArrayList<TSMResource>();
	    for (final TSMResource child : container.getChildren()) {
		// Dont show the images folder
		if (child instanceof TSMPackage
			&& child.getName().equals(
				DataModelTypes.imageFolderName)) {
		    continue;
		}
		// Only show TSMReport if it has no Test Case
		if (child instanceof TSMReport
			&& ((TSMReport) child).getTestCase() != null) {
		    continue;
		}
//		if (isFiltered(child)) {
		    children.add(child);
//		}
	    }
	    return (children.toArray());
	} else if (parentElement instanceof TSMTestCase) {
	    final List<TSMResource> children = new ArrayList<TSMResource>();
	    for (final TSMResource child : ((TSMTestCase) parentElement)
		    .getReports()) {
//		if (isFiltered(child)) {
		    children.add(child);
//		}
	    }
	    return (children.toArray());
	} else if (parentElement instanceof TSMRootElement) {
	    final List<TSMResource> children = new ArrayList<TSMResource>();
	    for (final TSMResource child : ((TSMRootElement) parentElement)
		    .getChildren()) {
//		if (isFiltered(child)) {
		    children.add(child);
//		}
	    }
	    return (children.toArray());
	}
	return new Object[0];
    }

    @Override
    public Object[] getElements(final Object inputElement) {
	return getChildren(inputElement);
    }

    @Override
    public Object getParent(final Object element) {
	if (element instanceof TSMPackage) {
	    return ((TSMPackage) element).getParent();
	} else if (element instanceof TSMTestCase) {
	    return ((TSMTestCase) element).getParent();
	} else if (element instanceof TSMReport) {
	    return ((TSMReport) element).getTestCase();
	} else if (element instanceof TSMProject) {
	    return TSMRootElement.getInstance();
	}
	return null;
    }

    @Override
    public boolean hasChildren(final Object element) {
	return getChildren(element).length != 0;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput,
	    final Object newInput) {
    }

//    private boolean isFiltered(final TSMResource child) {
//	if (child instanceof TSMTestCase
//		&& FilterModel.getInstance().isTestCases()) {
//	    final TSMTestCase testCase = (TSMTestCase) child;
//	    if (FilterModel.getInstance().isHigh()
//		    || FilterModel.getInstance().isMedium()
//		    || FilterModel.getInstance().isLow()) {
//		switch (testCase.getData().getPriority()) {
//		case high:
//		    if (!FilterModel.getInstance().isHigh()) {
//			return false;
//		    }
//		    break;
//		case medium:
//		    if (!FilterModel.getInstance().isMedium()) {
//			return false;
//		    }
//		    break;
//		case low:
//		    if (!FilterModel.getInstance().isLow()) {
//			return false;
//		    }
//		    break;
//		}
//	    }
//
//	    if (FilterModel.getInstance().isNotExecuted()
//		    && testCase.getData().getNumberOfExecutions() != 0) {
//		return false;
//	    }
//
//	    if (FilterModel.getInstance().isUnassigned()
//		    && !testCase.getData().getAssignedTo().isEmpty()) {
//		return false;
//	    }
//	    if (!testCase
//		    .getName()
//		    .toLowerCase()
//		    .contains(FilterModel.getInstance().getName().toLowerCase())) {
//		return false;
//	    }
//
//	    if (!testCase
//		    .getData()
//		    .getAuthor()
//		    .toLowerCase()
//		    .contains(
//			    FilterModel.getInstance().getCreator()
//				    .toLowerCase())) {
//		return false;
//	    }
//	    if (!isSameDay(FilterModel.getInstance().getLastExecution(),
//		    testCase.getData().getLastExecution())
//		    || (testCase.getData().getLastExecution() == null && FilterModel
//			    .getInstance().getLastExecution() != null)) {
//		return false;
//	    }
//
//	    if (!isSameDay(FilterModel.getInstance().getCreationTime(),
//		    testCase.getData().getCreationDate())) {
//		return false;
//	    }
//	    if (!isSameDay(FilterModel.getInstance().getLastChange(), testCase
//		    .getData().getLastChangedOn())) {
//		return false;
//	    }
//
//	    return true;
//	} else if (child instanceof TSMReport
//		&& !FilterModel.getInstance().isTestCases()) {
//	    final TSMReport report = (TSMReport) child;
//	    if (FilterModel.getInstance().isPassed()
//		    || FilterModel.getInstance().isFailed()
//		    || FilterModel.getInstance().isPassedWithAnnotation()
//		    || FilterModel.getInstance().isNotExecuted()) {
//		switch (report.getData().getStatus()) {
//		case passed:
//		    if (!FilterModel.getInstance().isPassed()) {
//			return false;
//		    }
//		    break;
//		case passedWithAnnotation:
//		    if (!FilterModel.getInstance().isPassedWithAnnotation()) {
//			return false;
//		    }
//		    break;
//		case failed:
//		    if (!FilterModel.getInstance().isFailed()) {
//			return false;
//		    }
//		    break;
//		case notExecuted:
//		    if (!FilterModel.getInstance().isNotExecuted()) {
//			return false;
//		    }
//		    break;
//		}
//	    }
//
//	    if (FilterModel.getInstance().isUnassigned()
//		    && !report.getData().getAssignedTo().isEmpty()) {
//		return false;
//	    }
//	    if (!report
//		    .getName()
//		    .toLowerCase()
//		    .contains(FilterModel.getInstance().getName().toLowerCase())) {
//		return false;
//	    }
//
//	    if (!report
//		    .getData()
//		    .getAssignedTo()
//		    .toLowerCase()
//		    .contains(
//			    FilterModel.getInstance().getCreator()
//				    .toLowerCase())) {
//		return false;
//	    }
//
//	    if (!isSameDay(FilterModel.getInstance().getCreationTime(), report
//		    .getData().getCreationDate())) {
//		return false;
//	    }
//
//	    return true;
//	} else {
//	    if (child instanceof TSMTestCase
//		    && !FilterModel.getInstance().isTestCases()) {
//		if (getChildren(child).length == 0) {
//		    return false;
//		}
//	    }
//	    return true;
//	}
//    }

    private boolean isSameDay(final Date date1, final Date date2) {
	if (date1 == null || date2 == null) {
	    return true;
	}
	final Calendar cal1 = Calendar.getInstance();
	cal1.setTime(date1);
	final Calendar cal2 = Calendar.getInstance();
	cal2.setTime(date2);
	if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
		&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
		&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
	    return true;
	} else {
	    return false;
	}

    }

}
