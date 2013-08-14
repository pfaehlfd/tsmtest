 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Bernhard Wetzel - some fixes
 *    Florian Krüger - some fixes
 *    Wolfgang Kraus - added features, various fixes
 *    Daniel Hertl - fixed dirty value
 *    Verena Käfer - worked on lastChange attribute
 *    Tobias Hirning - some fixes, i18n
 *    
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.newtestcase.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.SWTUtils;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;
import net.sourceforge.tsmtest.gui.ResourceManager;
import net.sourceforge.tsmtest.gui.SashManager;
import net.sourceforge.tsmtest.gui.richtexteditor.RichText;
import net.sourceforge.tsmtest.gui.richtexteditor.TsmStyledText;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.xml.sax.SAXException;

/**
 * @author Albert Flaig
 *
 */
public class StepSash {
    private SashManager<TestStepDescriptor> tableManager;
    private final Logger log = Logger.getLogger(StepSash.class);
    private ModifyListener lastStepListener;
    private static final String strRowData = "rowData";
    private boolean initComplete = false;
    private TSMTestCase testcase;

    public StepSash(final Composite parent, final ModifyListener listen,
	    final TSMTestCase input) {
	testcase = input;
	tableManager = new SashManager<TestStepDescriptor>(parent) {
	    @Override
	    protected TestStepDescriptor createDefaultData() {
		return new TestStepDescriptor();
	    }
	};

	lastStepListener = new ModifyListener() {
	    @Override
	    public void modifyText(final ModifyEvent e) {
		if (!initComplete) {
		    return;
		}
		final Object src = e.getSource();
		if (src instanceof TsmStyledText) {
		    final Object pos = ((TsmStyledText) src).getParent()
			    .getData(strRowData);
		    final int rowPos = (Integer) pos;
		    if (rowPos + 1 == tableManager.getStepSize()) {
			tableManager.addSashesAtBottom(1);
		    }
		}
	    }

	};

	tableManager.addColumn(createLabelColumn(tableManager, listen), "#", //$NON-NLS-1$
		10, true, false, null);
	tableManager.addColumn(createTextColumn(tableManager, listen),
		Messages.StepSash_1, 100, false, true, null);
	tableManager.addColumn(createTextColumn(tableManager, listen),
		Messages.StepSash_2, 100, false, true, null);
	tableManager.addColumn(createButtonsColumn(tableManager, listen),
		Messages.StepSash_3, 40, true, false, null);
    }

    public void initCompleted() {
	initComplete = true;
    }

    private SashManager<TestStepDescriptor>.SashManagerColumn<Label> createLabelColumn(
	    final SashManager<TestStepDescriptor> tableManager,
	    final ModifyListener listen) {
	return tableManager.new SashManagerColumn<Label>() {

	    @Override
	    public Label create(final Composite parent,
		    final ModifyListener heightListener) {
		final Label label = new Label(parent, SWT.CENTER
			| SWT.BACKGROUND);
		final Color white = new Color(null, 255, 255, 255);
		label.setBackground(white);
		white.dispose();
		return label;
	    }

	    @Override
	    public void renderTC(final Label widget,
		    final TestStepDescriptor data, final int row,
		    final int column) {
		widget.setText((row + 1) + "."); //$NON-NLS-1$
	    }

	    @Override
	    protected void rowIndexChanged(final Label control, final int row) {
		control.setText((row + 1) + "."); //$NON-NLS-1$
	    }

	    @Override
	    protected void renderCustom(final Label widget,
		    final String[] data, final int row, final int column) {
		// not needed

	    }
	};
    }

    public void setLayoutData(final Object layout) {
	tableManager.setLayoutData(layout);
    }

    public SashManager<TestStepDescriptor> getSashManager() {
	return tableManager;
    }

    public void initSteps(final List<TestStepDescriptor> TestStepDescriptors) {
	final ArrayList<TestStepDescriptor> copy = new ArrayList<TestStepDescriptor>(
		TestStepDescriptors);
	copy.add(new TestStepDescriptor());

	tableManager.setContent(copy);
    }

    private SashManager<TestStepDescriptor>.SashManagerColumn<RichText> createTextColumn(
	    final SashManager<TestStepDescriptor> tableManager,
	    final ModifyListener listen) {
	return tableManager.new SashManagerColumn<RichText>() {

	    @Override
	    public RichText create(final Composite parent,
		    final ModifyListener heightListener) {
		final RichText text = new RichText(parent, SWT.MULTI | SWT.WRAP);
		text.setProjectName(testcase.getProject().getName());
		text.addModifyListener(heightListener);
		text.addModifyListener(lastStepListener);
		text.addMouseMoveListener(new MouseMoveListener() {
		    @Override
		    public void mouseMove(final MouseEvent x) {
			final Event e = new Event();
			e.widget = x.widget;
			e.display = x.display;
			e.time = x.time;
			e.data = x.data;
			heightListener.modifyText(new ModifyEvent(e));
		    }
		});
		text.addModifyListener(listen);
		return text;
	    }

	    @Override
	    protected void rowIndexChanged(final RichText control, final int row) {
		control.setData(strRowData, row);
	    }

	    @Override
	    public void renderTC(final RichText widget,
		    final TestStepDescriptor data, final int row,
		    final int column) {
		widget.setData(strRowData, row);
		try {
		    switch (column) {
		    case 0:
			widget.setFormattedText(row + ""); //$NON-NLS-1$
			break;
		    case 1:
			final Map<Integer, Listener[]> m = SWTUtils
				.removeAllListeners(widget);
			widget.setFormattedText(data.getRichTextDescription());
			SWTUtils.restoreAllListeners(widget, m);
			break;
		    case 2:
			final Map<Integer, Listener[]> m2 = SWTUtils
				.removeAllListeners(widget);
			widget.setFormattedText(data.getExpectedResult());
			SWTUtils.restoreAllListeners(widget, m2);
			break;
		    }
		} catch (final ParserConfigurationException e) {
		    // TODO
		} catch (final SAXException e) {
		} catch (final IOException e) {
		}

	    }

	    @Override
	    protected int getHeight(final RichText widget) {
		Point p = widget.getSize();
		p.x = Math.max(p.x, 200);
		p = widget.computeSize(p.x, SWT.DEFAULT);
		return p.y + 10 + widget.getLineCount() / 10;
	    }

	    @Override
	    protected TestStepDescriptor writeContentToData(
		    final RichText control, final TestStepDescriptor data,
		    final int row, final int column) {
		switch (column) {
		case 1:
		    data.setRichTextDescription(control.getFormattedText());
		    break;
		case 2:
		    data.setExpectedResult(control.getFormattedText());
		    break;
		}
		return data;
	    }

	    @Override
	    protected void renderCustom(final RichText widget,
		    final String[] data, final int row, final int column) {
		// not needed

	    }
	};
    }

    private SashManager<TestStepDescriptor>.SashManagerColumn<Composite> createButtonsColumn(
	    final SashManager<TestStepDescriptor> tableManager,
	    final ModifyListener listen) {
	final GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false,
		1, 1);

	return tableManager.new SashManagerColumn<Composite>() {
	    @Override
	    public Composite create(final Composite parent,
		    final ModifyListener heightListener) {
		final Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(3, false));
		c.setLayoutData(gd);
		final Color white = new Color(null, 255, 255, 255);
		c.setBackground(white);
		white.dispose();

		final Button btnTop = new Button(c, SWT.CENTER);
		// btnTop.setText("\u2191+");
		btnTop.setImage(ResourceManager.getImgArrowUp());
		btnTop.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(final SelectionEvent e) {
			addSashEvent(e, 0);
			final Event fireEvent = new Event();
			fireEvent.widget = e.widget;
			listen.modifyText(new ModifyEvent(fireEvent));
		    }
		});
		btnTop.setLayoutData(gd);
		btnTop.setData(c);

		final Button btnDel = new Button(c, SWT.CENTER);
		// btnDel.setText("\u2715");
		btnDel.setImage(ResourceManager.getImgCrossGrey());
		btnDel.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(final SelectionEvent e) {
			// TODO: cleaner implementation
			final Button b = (Button) e.getSource();
			final Composite mainComp = (Composite) b.getData();
			final Control[] controls = mainComp.getParent()
				.getChildren();
			boolean ask = false;
			for (final Control c : controls) {
			    if (c instanceof RichText) {
				final String s1 = ((RichText) c)
					.getFormattedText();
				final String s = s1.replaceAll("<.*?>", ""); //$NON-NLS-1$ //$NON-NLS-2$
				final boolean tmp = !(s.isEmpty());
				ask = ask || tmp;
			    }
			}
			if (ask) {
			    final MessageBox message = new MessageBox(Display
				    .getCurrent().getActiveShell(),
				    SWT.ICON_WARNING | SWT.YES | SWT.NO);
			    message.setMessage(Messages.StepSash_9);
			    message.setText(Messages.StepSash_10);
			    if (message.open() == SWT.YES) {
				addSashEvent(e, -1);
				final Event fireEvent = new Event();
				fireEvent.widget = e.widget;
				listen.modifyText(new ModifyEvent(fireEvent));
			    }
			} else {
			    addSashEvent(e, -1);
			    final Event fireEvent = new Event();
			    fireEvent.widget = e.widget;
			    listen.modifyText(new ModifyEvent(fireEvent));
			}
		    }
		});
		btnDel.setLayoutData(gd);
		btnDel.setData(c);

		final Button btnBottom = new Button(c, SWT.CENTER);
		// btnBottom.setText("+\u2193");
		btnBottom.setImage(ResourceManager.getImgArrowDown());
		btnBottom.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(final SelectionEvent e) {
			addSashEvent(e, 1);
			final Event fireEvent = new Event();
			fireEvent.widget = e.widget;
			listen.modifyText(new ModifyEvent(fireEvent));
		    }
		});
		btnBottom.setLayoutData(gd);
		btnBottom.setData(c);
		return c;
	    }

	    @Override
	    public void renderTC(final Composite widget,
		    final TestStepDescriptor data, final int row,
		    final int column) {
		// not needed
	    }

	    @Override
	    protected void rowIndexChanged(final Composite control,
		    final int row) {
		// remember row position
		control.setData(row);
	    }

	    /**
	     * Adds a sash or deletes the source
	     * 
	     * @param e
	     *            event from a button of a sash, needs to have the
	     *            button as source
	     * @param action
	     *            <code>-1</code> for deleting the source sash <br>
	     *            <code>0</code> to add a new one above the source <br>
	     *            <code>1</code> to add a new one below the source
	     */
	    private void addSashEvent(final SelectionEvent e, final int action) {
		final Button b = (Button) e.getSource();
		Composite s;
		final Object o = b.getData();
		if (o instanceof Composite) {
		    s = (Composite) o;
		    if (s.getData() instanceof Integer) {
			final int pos = (Integer) s.getData();
			if (action == -1) {
			    tableManager.deleteRow(pos);
			} else if (action == 0) {
			    tableManager.addSashes(1, pos);
			} else if (action == +1) {
			    tableManager.addSashes(1, pos + 1);
			}
		    } else {
			log.error(Messages.StepSash_11);
		    }
		} else {
		    log.error(Messages.StepSash_12);
		}
	    }

	    @Override
	    protected void rowNumberChanged(final Composite control,
		    final int num, final int row) {
		// TODO
		final Object data = control.getChildren()[1];
		if (data instanceof Button) {
		    final Button button = (Button) data;
		    if (num == 1 && row == 0) {
			button.setEnabled(false);
		    } else {
			button.setEnabled(true);
		    }
		}
	    }

	    @Override
	    protected void renderCustom(final Composite widget,
		    final String[] data, final int row, final int column) {
		// not needed

	    }
	};
    }

    public void addSashesAtTop(final int i) {
	tableManager.addSashesAtTop(i);
    }

    public List<TestStepDescriptor> getAllSteps() {
	return tableManager.getContent();
    }
}
