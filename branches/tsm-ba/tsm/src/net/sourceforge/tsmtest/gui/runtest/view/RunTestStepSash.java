 /*******************************************************************************
 * Copyright (c) 2012-2013 Wolfgang Kraus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wolfgang Kraus - initial version
 *    Albert Flaig - some fixes, data model refactoring
 *    Bernhard Wetzel - some enhancements
 *    Tobias Hirning - some fixes, i18n
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.runtest.view;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.DataModelTypes.StatusType;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;
import net.sourceforge.tsmtest.gui.ClickableImage;
import net.sourceforge.tsmtest.gui.SashManager;
import net.sourceforge.tsmtest.gui.richtexteditor.RichText;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.xml.sax.SAXException;

/**
 * @author Wolfgang Kraus
 *
 */
public class RunTestStepSash {
    private SashManager<TestStepDescriptor> tableManager;
    private static final int POSDESC = 1;
    private static final int POSEXP = 2;
    private static final int POSREAL = 3;
    private final boolean loadActual;
    TSMTestCase testcase;

    public RunTestStepSash(final Composite parent, final ModifyListener listen,
	    final ModifyListener lastChangedListener, final boolean load,
	    final TSMTestCase input) {
	testcase = input;
	tableManager = new SashManager<TestStepDescriptor>(parent) {
	    @Override
	    protected TestStepDescriptor createDefaultData() {
		return new TestStepDescriptor();
	    }
	};
	loadActual = load;

	tableManager.addColumn(createLabelColumn(tableManager, listen), "#", //$NON-NLS-1$
		10, true, false, null);
	tableManager.addColumn(
		createTextColumn(tableManager, listen, lastChangedListener,
			true), Messages.RunTestStepSash_1, 100, false, false,
		null);
	tableManager.addColumn(
		createTextColumn(tableManager, listen, lastChangedListener,
			true), Messages.RunTestStepSash_2, 100, false, false,
		null);
	tableManager.addColumn(
		createTextColumn(tableManager, listen, lastChangedListener,
			false), Messages.RunTestStepSash_3, 100, false, true,
		null);
	tableManager.addColumn(createImageColumn(tableManager, listen),
		Messages.RunTestStepSash_4, 50, true, false, null);
    }

    public void setLayoutData(final Object layout) {
	tableManager.setLayoutData(layout);
    }

    public SashManager<TestStepDescriptor> getSashManager() {
	return tableManager;
    }

    public void initSteps(final List<TestStepDescriptor> TestStepDescriptors) {
	tableManager.setContent(TestStepDescriptors);
    }

    private SashManager<TestStepDescriptor>.SashManagerColumn<RichText> createTextColumn(
	    final SashManager<TestStepDescriptor> tableManager,
	    final ModifyListener listen,
	    final ModifyListener lastChangedListener, final boolean change) {
	return tableManager.new SashManagerColumn<RichText>() {

	    @Override
	    public RichText create(final Composite parent,
		    final ModifyListener heightListener) {
		final RichText text = new RichText(parent, SWT.MULTI | SWT.WRAP);
		text.addModifyListener(heightListener);
		text.addModifyListener(listen);
		if (change) {
		    text.addModifyListener(lastChangedListener);
		}
		text.setProjectName(testcase.getProject().getName());
		return text;
	    }

	    @Override
	    public void renderTC(final RichText widget,
		    final TestStepDescriptor data, final int row,
		    final int column) {
		try {
		    switch (column) {
		    case POSDESC:
			String desc = data.getRichTextDescription();
			if (desc.length() < 13) {
			    desc = "<html></html>"; //$NON-NLS-1$
			}

			widget.setFormattedText(desc);
			break;
		    case POSEXP:
			String exp = data.getExpectedResult();
			if (exp.length() < 13) {
			    exp = "<html></html>"; //$NON-NLS-1$
			}

			widget.setFormattedText(exp);
			break;
		    case POSREAL:
			if (loadActual) {
			    String real = data.getRealResult();
			    if (real.length() < 13) {
				real = "<html></html>"; //$NON-NLS-1$
			    }
			    widget.setFormattedText(real);
			}
			break;
		    default:
			break;
		    }
		} catch (final ParserConfigurationException e) {
		    e.printStackTrace();
		} catch (final SAXException e) {
		    e.printStackTrace();
		} catch (final IOException e) {
		    e.printStackTrace();
		}

	    }

	    @Override
	    protected int getHeight(final RichText widget) {
		Point p = widget.getSize();
		p.x = Math.max(p.x, 150);
		p = widget.computeSize(p.x, SWT.DEFAULT);

		return p.y + 10 + widget.getLineCount() / 10;
	    }

	    @Override
	    protected TestStepDescriptor writeContentToData(
		    final RichText control, final TestStepDescriptor data,
		    final int row, final int column) {
		switch (column) {
		case POSDESC:
		    data.setRichTextDescription(control.getFormattedText());
		    break;
		case POSEXP:
		    data.setExpectedResult(control.getFormattedText());
		    break;
		case POSREAL:
		    data.setRealResult(control.getFormattedText());
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

    private SashManager<TestStepDescriptor>.SashManagerColumn<ClickableImage> createImageColumn(
	    final SashManager<TestStepDescriptor> tableManager,
	    final ModifyListener listen) {
	return tableManager.new SashManagerColumn<ClickableImage>() {
	    @Override
	    public ClickableImage create(final Composite parent,
		    final ModifyListener heightListener) {
		final ClickableImage image = new ClickableImage(parent,
			SWT.NONE, true);
		final Color white = new Color(null, 255, 255, 255);
		image.setBackground(white);
		white.dispose();
		image.setStatus(StatusType.notExecuted);
		image.addModifyListener(listen);
		return image;
	    }

	    @Override
	    protected int getHeight(final ClickableImage widget) {
		return 60;
	    }

	    @Override
	    public void renderTC(final ClickableImage widget,
		    final TestStepDescriptor data, final int row,
		    final int column) {
		if (loadActual) {
		    widget.setStatus(data.getStatus());
		}
	    }

	    @Override
	    protected TestStepDescriptor writeContentToData(
		    final ClickableImage control,
		    final TestStepDescriptor data, final int row,
		    final int column) {
		data.setStatus(control.getStatus());
		return data;
	    }

	    @Override
	    protected void renderCustom(final ClickableImage widget,
		    final String[] data, final int row, final int column) {
		// not needed

	    }

	};
    }

    private SashManager<TestStepDescriptor>.SashManagerColumn<Label> createLabelColumn(
	    final SashManager<TestStepDescriptor> tableManager,
	    final ModifyListener listen) {
	return tableManager.new SashManagerColumn<Label>() {

	    @Override
	    public Label create(final Composite parent,
		    final ModifyListener heightListener) {
		final Label label = new Label(parent, SWT.CENTER);
		final Color white = new Color(null, 255, 255, 255);
		label.setBackground(white);
		white.dispose();
		return label;
	    }

	    @Override
	    protected int getHeight(final Label widget) {

		return 20;
	    }

	    @Override
	    public void renderTC(final Label widget,
		    final TestStepDescriptor data, final int row,
		    final int column) {
		widget.setText((row + 1) + "."); //$NON-NLS-1$
	    }

	    @Override
	    protected void renderCustom(final Label widget,
		    final String[] data, final int row, final int column) {
		// not needed

	    }
	};
    }

    public List<TestStepDescriptor> getAllSteps() {
	return tableManager.getContent();
    }
}
