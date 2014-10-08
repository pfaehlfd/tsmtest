 /*******************************************************************************
 * Copyright (c) 2012-2013 Bernhard Wetzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bernhard Wetzel - initial version
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.overview.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 * A dialog to customize the overview
 * 
 * @author Bernhard Wetzel
 * 
 */
public class OverviewDialog extends InputDialog {
    public final static String ID = "net.sourceforge.tsmtest.gui.runtest.view.dialogruntest"; //$NON-NLS-1$s
    private ArrayList<Integer> revisionsDialog = new ArrayList<Integer>();
    private ArrayList<Button> revCheck = new ArrayList<Button>();
    private ArrayList<Integer> revTicks = new ArrayList<Integer>();

    /**
    * @param revisions The available revisions.
    * @param revisionsSelected Selected revisions.
    */
    public OverviewDialog(ArrayList<Integer> revisions, ArrayList<Integer> revisionsSelected) {

	super(null, Messages.OverviewDialog_1, null, "", null); //$NON-NLS-2$ //$NON-NLS-1$
	// We want a bigger window than the default size
	setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	revisionsDialog = revisions;
	revTicks = revisionsSelected;
    }

    /**
     * Creates the OK and Cancel Button
     * 
     * @param parent
     *            composite to be displayed in
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
	createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
		true);
	createButton(parent, IDialogConstants.CANCEL_ID,
		IDialogConstants.CANCEL_LABEL, false);

	Button ok = getButton(IDialogConstants.OK_ID);
	ok.setText(Messages.OverviewDialog_2);
	setButtonLayoutData(ok);

	Button cancel = getButton(IDialogConstants.CANCEL_ID);
	cancel.setText(Messages.OverviewDialog_3);
	setButtonLayoutData(cancel);
    }

    /**
     * Creates the main area of the dialog
     * 
     * @param parent
     *            the composite to be displayed in
     */
    @Override
    protected Control createDialogArea(Composite parent) {
	//main composite
	Composite comp = (Composite) super.createDialogArea(parent);
	// We have 2 Text-children that we don't need
	//see more below
	Control[] children = comp.getChildren();
	Group scrollGroup = new Group(parent, SWT.NO_MERGE_PAINTS);
	scrollGroup.setLayout(new GridLayout(1, false));
	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1,
		1);
	gd.heightHint = 300;
	scrollGroup.setLayoutData(gd);
	
	Label lblexplain = new Label(comp, SWT.FILL);
	lblexplain.setText(Messages.OverviewDialog_6);
	//Message if no revisions were found
	if (revisionsDialog.isEmpty()) {
	    Label noRev = new Label(comp, SWT.FILL);
	    noRev.setText(Messages.OverviewDialog_4);
	    
		for (Control currentControl : children) {
		    currentControl.moveBelow(null);
		    currentControl.setVisible(false);
		}
	    return comp;
	}
	//Revisions area
	ScrolledComposite scrollComp = new ScrolledComposite(scrollGroup, SWT.V_SCROLL
	        | SWT.BORDER);
	scrollComp.setAlwaysShowScrollBars(true);
	scrollComp.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true,
		true, 1, 1));
	
	Composite scrollContent = new Composite(scrollComp, SWT.NONE);

	//order from highest to lowest
	Collections.sort(revisionsDialog);
	Collections.reverse(revisionsDialog);
	int index = 0;
	int btnheight = 0;
	GC gc = new GC(lblexplain);
	int height = gc.getFontMetrics().getHeight();
	gc.dispose();
	while (index < revisionsDialog.size()) {
	    Button btn = new Button(scrollContent, SWT.CHECK);
	    setup(btn, revisionsDialog.get(index));
	    if (revTicks.contains(revisionsDialog.get(index))) {
		btn.setSelection(true);
	    }
	    btnheight += height+1;
	    revCheck.add(btn);
	    index++;
	}
	scrollContent.setLayout(parent.getLayout());
	scrollComp.setContent(scrollContent);
	scrollComp.setMinSize(300, btnheight);
	scrollComp.setExpandHorizontal(true);
	scrollComp.setExpandVertical(true);
	//hide and move unneeded children below
	for (Control currentControl : children) {
	    currentControl.moveBelow(null);
	    currentControl.setVisible(false);
	}
	return comp;
    }

    private void setup(Button btn, final int revision) {
	Collection<TSMTestCase> tcs = (Collection<TSMTestCase>) TSMTestCase.list();
	int counter = 0;
	//Go through all existing testcases checking the amount of testcase each revision has
	for (TSMTestCase tc : tcs) {
	    Collection<TSMReport> reps = (Collection<TSMReport>) tc
		    .getReports();
	    for (TSMReport rep : reps) {
		if (rep.getData().getRevisionNumber() == revision) {
		    counter++;
		    break;
		}
	    }
	}
	btn.setText(Messages.OverviewDialog_5 + " " + revision + " (" + counter
		+ ")");
	//add(remove) to (from) ticklist if selected (deselected)
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (revTicks.indexOf(revision) == -1) {
		    revTicks.add(revision);
		} else {
		    revTicks.remove(revTicks.indexOf(revision));
		}

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
		// do nothing
	    }
	});
    }

    @Override
    protected int getInputTextStyle() {
	return (SWT.MULTI | SWT.WRAP);
    }

    /**
     * Getter for the list with the ticks for the selected revisions.
     * @return An ArrayList of Integers with the number of the selected revisions.
     */
    public ArrayList<Integer> getRevTicks() {
        return revTicks;
    }

}
