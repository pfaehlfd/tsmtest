 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Tobias Hirning - some refactoring, some fixes
 *    Verena Käfer - bugfix
 *    Bernhard Wetzel - some fixes
 *    Wolfgang Kraus - various fixes
 *    
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.report;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.SWTUtils;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.MultiPageEditorPartInput;
import net.sourceforge.tsmtest.datamodel.ResourceEditorInput;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.AbstractDataModel.DataModelObservable;
import net.sourceforge.tsmtest.gui.IBreadCrumbListener;
import net.sourceforge.tsmtest.gui.ResourceManager;
import net.sourceforge.tsmtest.gui.TSMBreadcrumbViewer;
import net.sourceforge.tsmtest.gui.newtestcase.view.ViewTestCase;
import net.sourceforge.tsmtest.gui.richtexteditor.RichText;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;

/**
 * @author Albert Flaig
 *
 */
public class ViewReport extends MultiPageEditorPartInput implements
	DataModelObservable {

    public static final String ID = "net.sourceforge.tsmtest.gui.report"; //$NON-NLS-1$
    private RichText browserFinalResult;
    private ReportStepSash tableManager;
    private TSMReport input;
    private Text testCaseLabel;
    private Text executedLabel;
    private Text revisionLabel;
    private Text durationLabel;
    private Text executorLabel;
    private RichText shortDescription;
    private RichText precondition;
    private Label testCaseStatus;
    private Text testCaseDeprecatedText;
    private TSMBreadcrumbViewer viewer;
    private Label testCaseDeprecatedLabel;

    private void createPage0() {
	final Composite parent = new Composite(getContainer(), SWT.NONE);
	final int columns = 5;
	parent.setLayout(new GridLayout(columns, false));

	viewer = new TSMBreadcrumbViewer(parent, SWT.NONE);
	viewer.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false,
		columns, 1));
	viewer.setInput(input);
	viewer.addBreadCrumbListener(new IBreadCrumbListener() {
	    @Override
	    public boolean selectionChanged(final TSMReport report) {
		try {
		    ViewReport.openGUI(report);
		    return true;
		} catch (final PartInitException e) {
		    e.printStackTrace();
		    return false;
		}
	    }

	    @Override
	    public boolean selectionChanged(final TSMTestCase testCase) {
		try {
		    ViewTestCase.openGUI(testCase);
		    return true;
		} catch (final PartInitException e) {
		    e.printStackTrace();
		    return false;
		}
	    }
	});

	Composite site = new Composite(parent, SWT.NONE);
	site.setLayout(new GridLayout(3, false));
	site.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
	testCaseStatus = new Label(site, SWT.NONE);
	testCaseStatus.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
		false, 1, 2));
	testCaseLabel = new Text(site, SWT.BACKGROUND);
	testCaseLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		false, 2, 1));
	testCaseLabel.setEditable(false);
	testCaseDeprecatedText = new Text(site, SWT.BACKGROUND);
	testCaseDeprecatedText.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
		false, false, 1, 1));
	testCaseDeprecatedText.setEditable(false);
	testCaseDeprecatedLabel = new Label(site, SWT.NONE);
	testCaseDeprecatedLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
		true, false, 1, 1));

	site = new Composite(parent, SWT.NONE);
	site.setLayout(new GridLayout(2, false));
	site.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
	new Label(site, SWT.NONE).setText(Messages.ViewReport_1);
	executorLabel = new Text(site, SWT.BACKGROUND);
	executorLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
		false, 1, 1));
	executorLabel.setEditable(false);

	site = new Composite(parent, SWT.NONE);
	site.setLayout(new GridLayout(2, false));
	site.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
	new Label(site, SWT.NONE).setText(Messages.ViewReport_2);
	durationLabel = new Text(site, SWT.BACKGROUND);
	durationLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
		false, 1, 1));
	durationLabel.setEditable(false);

	site = new Composite(parent, SWT.NONE);
	site.setLayout(new GridLayout(2, false));
	site.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
	new Label(site, SWT.NONE).setText(Messages.ViewReport_3);
	executedLabel = new Text(site, SWT.BACKGROUND);
	executedLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
		false, 1, 1));
	executedLabel.setEditable(false);

	site = new Composite(parent, SWT.NONE);
	site.setLayout(new GridLayout(2, false));
	site.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
	new Label(site, SWT.NONE).setText(Messages.ViewReport_10);
	revisionLabel = new Text(site, SWT.BACKGROUND);
	revisionLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
		false, 1, 1));
	revisionLabel.setEditable(false);

	site = new Composite(parent, SWT.NONE);
	site.setLayout(new GridLayout(2, false));
	site.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false,
		columns, 1));
	Group group = new Group(site, SWT.NONE);
	group.setText(Messages.ViewReport_4);
	group.setLayout(new GridLayout(1, false));
	group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
	GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);
	gd.heightHint = 100;
	shortDescription = new RichText(group, SWT.V_SCROLL | SWT.READ_ONLY);
	shortDescription.setLayoutData(gd);
	shortDescription.removeTextListeners();

	group = new Group(site, SWT.NONE);
	group.setText(Messages.ViewReport_5);
	group.setLayout(new GridLayout(1, false));
	group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
	gd = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);
	gd.heightHint = 100;
	precondition = new RichText(group, SWT.V_SCROLL | SWT.READ_ONLY);
	precondition.setLayoutData(gd);
	precondition.removeTextListeners();

	tableManager = new ReportStepSash(parent);
	tableManager.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true,
		columns, 1));

	final int index = addPage(parent);
	setPageText(index, Messages.ViewReport_6);

	// Track dataModel changes
	DataModel.getInstance().register(this);
    }

    private void createPage1() {
	final Composite parent = new Composite(getContainer(), SWT.NONE);

	parent.setLayout(new GridLayout(1, false));
	new Label(parent, SWT.NONE).setText(Messages.ViewReport_7);
	browserFinalResult = new RichText(parent, SWT.BORDER | SWT.READ_ONLY
		| SWT.V_SCROLL);
	browserFinalResult.removeTextListeners();
	browserFinalResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		true, 1, 1));

	final int index = addPage(parent);
	setPageText(index, Messages.ViewReport_8);
    }

    private void setupModel() {
	setPartName(input.getName());

	final TSMTestCase testCase = input.getTestCase();
	if (testCase == null) {
	    testCaseLabel.setText(Messages.ViewReport_9);
	    Font font = testCaseLabel.getFont();
	    String name = ""; //$NON-NLS-1$
	    int height = 16;
	    int style = 0;
	    for (final FontData data : font.getFontData()) {
		height = data.getHeight();
		style = data.getStyle() | SWT.ITALIC;
		name = data.getName();
	    }
	    font = new Font(font.getDevice(), name, height, style);
	    testCaseLabel.setFont(font);
	} else {
	    testCaseLabel.setText(testCase.getName());
	    Font font = testCaseLabel.getFont();
	    String name = ""; //$NON-NLS-1$
	    int height = 16;
	    int style = 0;
	    for (final FontData data : font.getFontData()) {
		height = data.getHeight();
		style = data.getStyle() | SWT.BOLD;
		name = data.getName();
	    }
	    font = new Font(font.getDevice(), name, height, style);
	    testCaseLabel.setFont(font);
	}
	if (input.isDeprecated()) {
	    Font font = testCaseDeprecatedText.getFont();
	    String name = ""; //$NON-NLS-1$
	    int height = 16;
	    int style = 0;
	    for (final FontData data : font.getFontData()) {
		height = data.getHeight();
		style = data.getStyle() | SWT.ITALIC;
		name = data.getName();
	    }
	    font = new Font(font.getDevice(), name, height, style);
	    testCaseDeprecatedText.setFont(font);
	    final Color red = new Color(font.getDevice(), 255, 0, 0);
	    testCaseDeprecatedText.setForeground(red);
	    red.dispose();
	    testCaseDeprecatedText.setText(Messages.ViewReport_12);
	    testCaseDeprecatedText.setToolTipText(Messages.ViewReport_13);
	    testCaseDeprecatedLabel.setToolTipText(Messages.ViewReport_13);
	} else {
	    Font font = testCaseDeprecatedText.getFont();
	    String name = ""; //$NON-NLS-1$
	    int height = 16;
	    int style = 0;
	    for (final FontData data : font.getFontData()) {
		height = data.getHeight();
		style = data.getStyle() | SWT.ITALIC;
		name = data.getName();
	    }
	    font = new Font(font.getDevice(), name, height, style);
	    testCaseDeprecatedText.setFont(font);
	    final Color green = new Color(font.getDevice(), 0, 150, 0);
	    testCaseDeprecatedText.setForeground(green);
	    green.dispose();
	    testCaseDeprecatedText.setText(Messages.ViewReport_15);
	    testCaseDeprecatedText.setToolTipText(Messages.ViewReport_16);
	    testCaseDeprecatedLabel.setToolTipText(Messages.ViewReport_16);
	}
	testCaseDeprecatedLabel.setImage(ResourceManager.getImgInformation());

	switch (input.getData().getStatus()) {
	case failed:
	    testCaseStatus.setImage(ResourceManager.getImgRed());
	    testCaseStatus.setToolTipText(Messages.ViewReport_18);
	    break;
	case passedWithAnnotation:
	    testCaseStatus.setImage(ResourceManager.getImgOrange());
	    testCaseStatus.setToolTipText(Messages.ViewReport_19);
	    break;
	case passed:
	    testCaseStatus.setImage(ResourceManager.getImgGreen());
	    testCaseStatus.setToolTipText(Messages.ViewReport_20);
	    break;
	case notExecuted:
	    testCaseStatus.setImage(ResourceManager.getImgGray());
	    testCaseStatus.setToolTipText(Messages.ViewReport_21);
	    break;
	}
	executorLabel.setText(input.getData().getAssignedTo());
	durationLabel.setText(input.getData().getRealDuration());
	revisionLabel.setText(input.getData().getRevisionNumber() + "");
	if (input.getData().getLastExecution() == null) {
	    executedLabel.setText(Messages.ViewReport_22);
	} else {
	    executedLabel.setText(DataModelTypes.dateFormat.format(input
		    .getData().getLastExecution()));
	}
	tableManager.setProjectName(input.getProject().getName());
	tableManager.setContent(input.getData().getSteps());
	try {
	    browserFinalResult.setProjectName(input.getProject().getName());
	    browserFinalResult.setFormattedText(input.getData()
		    .getRichTextResult());
	    precondition.setProjectName(input.getProject().getName());
	    precondition.setFormattedText(input.getData()
		    .getRichTextPrecondition());
	    shortDescription.setProjectName(input.getProject().getName());
	    shortDescription.setFormattedText(input.getData()
		    .getShortDescription());
	} catch (final ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (final SAXException e) {
	    e.printStackTrace();
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void setFocus() {
	testCaseLabel.setFocus();
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isDirty() {
	return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
	return false;
    }

    @Override
    protected void createPages() {
	createPage0();
	createPage1();
	setupModel();
    }

    @Override
    protected void getInput(final TSMTestCase input) {
	// Not needed
    }

    @Override
    protected void getInput(final TSMReport input) {
	this.input = input;
    }

    public static IEditorPart openGUI(final TSMReport input)
	    throws PartInitException {
	final IWorkbenchPage page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();
	IEditorPart editor = SWTUtils.findOpenEditor(input);
	if (editor == null) {
	    editor = page.openEditor(input != null ? new ResourceEditorInput(
		    input) : null, ID);
	} else {
	    page.activate(editor);
	}
	return editor;
    }

    @Override
    public TSMTestCase getTestCaseInput() {
	// not needed
	return null;
    }

    @Override
    public TSMReport getReportInput() {
	return input;
    }

    @Override
    public void dataModelChanged() {
	if (input == null) {
	    getEditorSite().getPage().closeEditor(ViewReport.this, false);
	} else {
	    // update the report
	    input = input.getNewestVersion();
	    Display.getDefault().asyncExec(new Runnable() {
		@Override
		public void run() {
		    if (!testCaseLabel.isDisposed()) {
			if (input != null) {
			    setupModel();
			}
		    }
		}
	    });
	}
    }
}
