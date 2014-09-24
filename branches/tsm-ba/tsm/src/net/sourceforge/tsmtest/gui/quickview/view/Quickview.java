 /*******************************************************************************
 * Copyright (c) 2012-2013 Wolfgang Kraus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wolfgang Kraus - initial version
 *    Verena Käfer - bugfix
 *    Tobias Hirning - i18n
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.quickview.view;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.datamodel.TSMProject;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionModel;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionObservable;
import net.sourceforge.tsmtest.datamodel.descriptors.ITestCaseDescriptor;
import net.sourceforge.tsmtest.gui.ResourceManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Wolfgang Kraus
 *
 */
public class Quickview extends ViewPart implements SelectionObservable {
    public static final String ID = "net.sourceforge.tsmtest.gui.quickview.view.quickview"; //$NON-NLS-1$
    private Group group;
    private Label lblNameHere;
    private Label lblModifyHere;
    private Label lblIdHere;
    private Label lblPriorityHere;
    private Label lblDurationHere;
    private Label lblCreatorHere;
    private Label lblNrExecutionsHere;
    private Label lblNrFailuresHere;
    private Label lblLastExecutionHere;
    private Label lblStateHere;
    private Composite mainComp;

    private Label lblId;
    private Label lblModify;
    private Label lblPriority;
    private Label lblDuration;
    private Label lblCreator;
    private Label lblNrOfExecutions;
    private Label lblNrOfFailures;
    private Label lblLastExecution;
    private Label lblNotAssigned;
    private Label lblNotAssignedHere;
    private Label lblName;
    private Label lblState;

    public Quickview() {
    }

    @Override
    public void createPartControl(Composite parent) {
	GridData gdStd = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
	gdStd.minimumWidth = 110;
	gdStd.widthHint = 110;

	mainComp = new Composite(parent, SWT.NONE);
	mainComp.setLayout(new GridLayout());

	group = new Group(mainComp, SWT.NONE);
	group.setLayout(new GridLayout(2, true));
	group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	lblName = new Label(group, SWT.NONE);
	lblName.setText(Messages.Quickview_1);
	lblName.setLayoutData(gdStd);
	
	lblNameHere = new Label(group, SWT.NONE);
	lblNameHere.setText(Messages.Quickview_2);
	lblNameHere.setLayoutData(gdStd);

	lblId = new Label(group, SWT.NONE);
	lblId.setText("ID:"); //$NON-NLS-1$
	lblId.setLayoutData(gdStd);

	lblIdHere = new Label(group, SWT.NONE);
	lblIdHere.setText(Messages.Quickview_4);
	lblIdHere.setLayoutData(gdStd);

	lblPriority = new Label(group, SWT.NONE);
	lblPriority.setText(Messages.Quickview_5);
	lblPriority.setLayoutData(gdStd);

	lblPriorityHere = new Label(group, SWT.NONE);
	lblPriorityHere.setText(Messages.Quickview_6);
	lblPriorityHere.setLayoutData(gdStd);

	lblDuration = new Label(group, SWT.NONE);
	lblDuration.setText(Messages.Quickview_7);
	lblDuration.setLayoutData(gdStd);

	lblDurationHere = new Label(group, SWT.NONE);
	lblDurationHere.setText(Messages.Quickview_8);
	lblDurationHere.setLayoutData(gdStd);

	lblCreator = new Label(group, SWT.NONE);
	lblCreator.setText(Messages.Quickview_9);
	lblCreator.setLayoutData(gdStd);

	lblCreatorHere = new Label(group, SWT.NONE);
	lblCreatorHere.setText(Messages.Quickview_10);
	lblCreatorHere.setLayoutData(gdStd);

	// Needed for an empty row in the view
	// new Label(group, SWT.NONE);
	// new Label(group, SWT.NONE);

	lblNrOfExecutions = new Label(group, SWT.NONE);
	lblNrOfExecutions.setText(Messages.Quickview_11);
	lblNrOfExecutions.setLayoutData(gdStd);

	lblNrExecutionsHere = new Label(group, SWT.NONE);
	lblNrExecutionsHere.setText(Messages.Quickview_12);
	lblNrExecutionsHere.setLayoutData(gdStd);

	lblNrOfFailures = new Label(group, SWT.NONE);
	lblNrOfFailures.setText(Messages.Quickview_13);
	lblNrOfFailures.setLayoutData(gdStd);

	lblNrFailuresHere = new Label(group, SWT.NONE);
	lblNrFailuresHere.setText(Messages.Quickview_14);
	lblNrFailuresHere.setLayoutData(gdStd);

	lblModify = new Label(group, SWT.NONE);
	lblModify.setText(Messages.Quickview_15);
	lblModify.setLayoutData(gdStd);

	lblModifyHere = new Label(group, SWT.NONE);
	lblModifyHere.setText(Messages.Quickview_16);
	lblModifyHere.setLayoutData(gdStd);

	lblLastExecution = new Label(group, SWT.NONE);
	lblLastExecution.setText(Messages.Quickview_17);
	lblLastExecution.setLayoutData(gdStd);

	lblLastExecutionHere = new Label(group, SWT.NONE);
	lblLastExecutionHere.setText(Messages.Quickview_18);
	lblLastExecutionHere.setLayoutData(gdStd);

	lblNotAssigned = new Label(group, SWT.NONE);
	lblNotAssigned.setLayoutData(gdStd);

	lblNotAssignedHere = new Label(group, SWT.NONE);
	lblNotAssignedHere.setLayoutData(gdStd);

	GridData gdState = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 2);
	gdState.minimumHeight = 60;
	gdState.heightHint = 60;
	gdState.minimumWidth = 110;
	gdState.widthHint = 110;
	lblState = new Label(group, SWT.NONE);
	lblState.setText(Messages.Quickview_19);
	lblState.setLayoutData(gdState);

	lblStateHere = new Label(group, SWT.NONE);
	lblStateHere.setText(Messages.Quickview_20);
	lblStateHere.setLayoutData(gdState);

	setNoTestCase();
	SelectionManager.instance.register(this);
	this.setTitleImage(ResourceManager.getImgQuickview());
    }

    @Override
    public void setFocus() {
	// not needed here
    }

    private void setNoTestCase() {
	group.setText(Messages.Quickview_21);

	lblNotAssigned.setVisible(false);
	lblNotAssignedHere.setVisible(false);
	lblId.setVisible(false);
	lblIdHere.setVisible(false);

	lblCreatorHere.setText("");
	lblDurationHere.setText(""); //$NON-NLS-1$
	lblNrExecutionsHere.setText(""); //$NON-NLS-1$
	lblIdHere.setText(""); //$NON-NLS-1$
	lblLastExecutionHere.setText(""); //$NON-NLS-1$
	lblNameHere.setText(""); //$NON-NLS-1$
	lblNrFailuresHere.setText(""); //$NON-NLS-1$
	lblPriorityHere.setText(""); //$NON-NLS-1$
	lblStateHere.setText(""); //$NON-NLS-1$
	lblModifyHere.setText(""); //$NON-NLS-1$
    }

    private void setTestCase(TSMTestCase t) {
	ITestCaseDescriptor tp = t.getData();

	lblName.setVisible(true);
	lblNameHere.setVisible(true);
	lblPriority.setVisible(true);
	lblPriorityHere.setVisible(true);
	lblDuration.setVisible(true);
	lblDurationHere.setVisible(true);
	lblState.setVisible(true);
	lblStateHere.setVisible(true);
	lblNotAssigned.setVisible(false);
	lblNotAssignedHere.setVisible(false);
	lblModify.setVisible(true);
	lblModifyHere.setVisible(true);
	
	lblId.setVisible(false);
	lblIdHere.setVisible(false);

	if (tp.getNumberOfExecutions() == 0) {
	    lblLastExecution.setVisible(false);
	    lblLastExecutionHere.setVisible(false);
	} else {
	    lblLastExecution.setVisible(true);
	    lblLastExecutionHere.setVisible(true);
	}

	lblName.setText(Messages.Quickview_32);
	lblId.setText("ID:"); //$NON-NLS-1$
	lblDuration.setText(Messages.Quickview_34);
	lblCreator.setText(Messages.Quickview_35);
	lblNrOfExecutions.setText(Messages.Quickview_36);
	lblNrOfFailures.setText(Messages.Quickview_37);
	lblLastExecution.setText(Messages.Quickview_38);
	lblPriority.setText(Messages.Quickview_39);
	lblModify.setText(Messages.Quickview_40);

	group.setText(Messages.Quickview_41);
	lblCreatorHere.setText(tp.getAuthor());
	lblDurationHere.setText(tp.getExpectedDuration());
	lblNrExecutionsHere.setText(tp.getNumberOfExecutions() + ""); //$NON-NLS-1$
	lblIdHere.setText(tp.getId() + ""); //$NON-NLS-1$
	if (tp.getLastExecution() == null) {
	    lblLastExecutionHere.setText(Messages.Quickview_44);
	} else {
	    lblLastExecutionHere.setText(DataModelTypes.getDateFormat().format(tp
		    .getLastExecution()));
	}
	lblNameHere.setText(t.getName());
	lblNrFailuresHere.setText(tp.getNumberOfFailures() + ""); //$NON-NLS-1$
	lblPriorityHere.setText(tp.getPriority().toString());
	lblModifyHere.setText(DataModelTypes.getDateFormat().format(tp
		.getLastChangedOn()));
	switch (tp.getStatus()) {
	case failed:
	    lblStateHere.setImage(ResourceManager.getImgRed());
	    break;
	case notExecuted:
	    lblStateHere.setImage(ResourceManager.getImgGray());
	    break;
	case passed:
	    lblStateHere.setImage(ResourceManager.getImgGreen());
	    break;
	case passedWithAnnotation:
	    lblStateHere.setImage(ResourceManager.getImgOrange());
	    break;
	default:
	    lblStateHere.setImage(ResourceManager.getImgGray());
	    break;

	}
	group.getParent().layout();

    }

    public void dispose() {
	SelectionManager.instance.unregister(this);
	super.dispose();
    }

    private void setProtocol(TSMReport file) {
	ITestCaseDescriptor tp = file.getData();

	lblLastExecution.setVisible(true);
	lblLastExecutionHere.setVisible(true);
	lblName.setVisible(true);
	lblNameHere.setVisible(true);
	lblPriority.setVisible(true);
	lblPriorityHere.setVisible(true);
	lblDuration.setVisible(true);
	lblDurationHere.setVisible(true);
	lblState.setVisible(true);
	lblStateHere.setVisible(true);
	lblModify.setVisible(true);
	lblModifyHere.setVisible(true);

	lblId.setVisible(false);
	lblIdHere.setVisible(false);
	lblNotAssigned.setVisible(false);
	lblNotAssignedHere.setVisible(false);

	lblName.setText(Messages.Quickview_46);
//	lblId.setText("ID:"); //$NON-NLS-1$
	lblDuration.setText(Messages.Quickview_48);
	lblCreator.setText(Messages.Quickview_49);
	lblNrOfExecutions.setText(Messages.Quickview_50);
	lblNrOfFailures.setText(Messages.Quickview_51);
	lblLastExecution.setText(Messages.Quickview_52);
	lblPriority.setText(Messages.Quickview_53);
	lblModify.setText(Messages.Quickview_54);

	group.setText(Messages.Quickview_55);

	lblCreatorHere.setText(tp.getAuthor());
	lblDurationHere.setText(tp.getRealDuration());
	lblNrExecutionsHere.setText(tp.getNumberOfExecutions() + ""); //$NON-NLS-1$
//	lblIdHere.setText(tp.getId() + ""); //$NON-NLS-1$
	if (tp.getLastExecution() == null) {
	    lblLastExecutionHere.setText(Messages.Quickview_58);
	} else {
	    lblLastExecutionHere.setText(DataModelTypes.getDateFormat().format(tp
		    .getLastExecution()));
	}
	lblNameHere.setText(file.getName());
	lblNrFailuresHere.setText(tp.getNumberOfFailures() + ""); //$NON-NLS-1$
	lblPriorityHere.setText(tp.getPriority().toString());
	lblModifyHere.setText(DataModelTypes.getDateFormat().format(tp
		.getLastChangedOn()));
	switch (tp.getStatus()) {
	case failed:
	    lblStateHere.setImage(ResourceManager.getImgRed());
	    break;
	case notExecuted:
	    lblStateHere.setImage(ResourceManager.getImgGray());
	    break;
	case passed:
	    lblStateHere.setImage(ResourceManager.getImgGreen());
	    break;
	case passedWithAnnotation:
	    lblStateHere.setImage(ResourceManager.getImgOrange());
	    break;
	default:
	    lblStateHere.setImage(ResourceManager.getImgGray());
	    break;

	}
	group.getParent().layout();
    }

    private void setMultipleDescriptors(List<ITestCaseDescriptor> files) {
	int passed = 0;
	int passedWithAnn = 0;
	int failed = 0;
	int notExe = 0;
	int notAssigned = 0;

	for (ITestCaseDescriptor tc : files) {
	    switch (tc.getStatus()) {
	    case failed:
		failed++;
		break;
	    case notExecuted:
		notExe++;
		break;
	    case passed:
		passed++;
		break;
	    case passedWithAnnotation:
		passedWithAnn++;
		break;
	    default:
		break;

	    }
	    if (tc.getAssignedTo().isEmpty()) {
		notAssigned++;
	    }
	}

	lblLastExecution.setVisible(true);
	lblLastExecutionHere.setVisible(true);
	lblName.setVisible(false);
	lblNameHere.setVisible(false);
	lblId.setVisible(false);
	lblIdHere.setVisible(false);
	lblPriority.setVisible(false);
	lblPriorityHere.setVisible(false);
	lblDuration.setVisible(false);
	lblDurationHere.setVisible(false);
	lblState.setVisible(false);
	lblStateHere.setVisible(false);
	lblNotAssigned.setVisible(true);
	lblNotAssignedHere.setVisible(true);
	lblModify.setVisible(false);
	lblModifyHere.setVisible(false);

	group.setText(Messages.Quickview_60 + files.size());

	lblCreator.setText(Messages.Quickview_61);
	lblCreatorHere.setText(passed + ""); //$NON-NLS-1$

	lblNrOfExecutions.setText(Messages.Quickview_63);
	lblNrExecutionsHere.setText(passedWithAnn + ""); //$NON-NLS-1$

	lblNrOfFailures.setText(Messages.Quickview_65);
	lblNrFailuresHere.setText(failed + ""); //$NON-NLS-1$

	lblLastExecution.setText(Messages.Quickview_67);
	lblLastExecutionHere.setText(notExe + ""); //$NON-NLS-1$

	lblNotAssigned.setText(Messages.Quickview_69);
	lblNotAssignedHere.setText(notAssigned + ""); //$NON-NLS-1$
    }

    private void setProtocols(List<TSMReport> files) {
	ArrayList<ITestCaseDescriptor> tcs = new ArrayList<ITestCaseDescriptor>(
		files.size());
	for (TSMReport f : files) {
	    tcs.add(f.getData());
	}
	setMultipleDescriptors(tcs);
    }

    private void setTestCases(List<TSMTestCase> files) {
	ArrayList<ITestCaseDescriptor> tcs = new ArrayList<ITestCaseDescriptor>(
		files.size());
	for (TSMTestCase f : files) {
	    tcs.add(f.getData());
	}
	setMultipleDescriptors(tcs);

    }

    private void setPackage(TSMPackage folder) {
	// setting the testcases
	setTestCases(folder.getTestCases());
	lblName.setVisible(true);
	lblNameHere.setVisible(true);
	lblId.setVisible(true);
	lblIdHere.setVisible(true);
	lblPriority.setVisible(true);
	lblPriorityHere.setVisible(true);

	group.setText(Messages.Quickview_71 + folder.getName());
	lblName.setText(Messages.Quickview_72);
	lblNameHere.setText(folder.getPackages().size() + ""); //$NON-NLS-1$
	lblId.setText(Messages.Quickview_74);
	lblIdHere.setText(folder.getTestCases().size() + ""); //$NON-NLS-1$
	lblPriority.setText(Messages.Quickview_0);
	lblPriorityHere.setText(folder.getReports().size() + ""); //$NON-NLS-1$

    }

    private void setProject(TSMProject file) {
	// setting the testcases
	setTestCases(file.getTestCases());
	lblName.setVisible(true);
	lblNameHere.setVisible(true);
	lblId.setVisible(true);
	lblIdHere.setVisible(true);
	lblPriority.setVisible(true);
	lblPriorityHere.setVisible(true);

	group.setText(Messages.Quickview_78 + file.getName());
	lblName.setText(Messages.Quickview_79);
	lblNameHere.setText(file.getPackages().size() + ""); //$NON-NLS-1$
	lblId.setText(Messages.Quickview_81);
	lblIdHere.setText(file.getTestCases().size() + ""); //$NON-NLS-1$
	lblPriority.setText(Messages.Quickview_83);
	lblPriorityHere.setText(file.getReports().size() + ""); //$NON-NLS-1$
    }

    @Override
    public void selectionChanged() {
	SelectionModel sm = SelectionManager.instance.getSelection();
	setNoTestCase();
	TSMResource file = sm.getFirstResource();
	if (file != null) {
	    if (sm.getAllFiles().size() > 1) {
		List<TSMTestCase> testcases = sm.getTestCases();
		if (testcases.size() > 1) {
		    setTestCases(testcases);
		    return;
		}
		List<TSMReport> reports = sm.getProtocols();
		if (reports.size() > 1) {
		    setProtocols(reports);
		    return;
		}

	    }
	    if (file instanceof TSMTestCase) {
		setTestCase((TSMTestCase) file);
	    }
	    else if (file instanceof TSMReport) {
		setProtocol((TSMReport) file);
	    }
	    else if (file instanceof TSMPackage) {
		setPackage((TSMPackage) file);
	    }
	    else if (file instanceof TSMProject) {
		setProject((TSMProject) file);
	    }
	}
    }

}
