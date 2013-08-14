 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Wolfgang Kraus - some fixes
 *    Tobias Hirning - i18n
 *    Bernhard Wetzel - some fixes
 *******************************************************************************/
package net.sourceforge.tsmtest.gui.report;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;
import net.sourceforge.tsmtest.gui.ResourceManager;
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
 * @author Albert Flaig
 *
 */
public class ReportStepSash {
    private SashManager<TestStepDescriptor> tableManager;
    private String projectName;
    private static final int POSDESC = 1;
    private static final int POSEXP = 2;
    private static final int POSREAL = 3;

    /**
     * @param parent
     * @param projectName
     *            Needed for images of the richtext
     */
    public ReportStepSash(final Composite parent) {
	tableManager = new SashManager<TestStepDescriptor>(parent) {
	    @Override
	    protected TestStepDescriptor createDefaultData() {
		return new TestStepDescriptor();
	    }
	};

	tableManager.addColumn(createLabelColumn(tableManager), "#", //$NON-NLS-1$
		10, true, false, null);
	tableManager.addColumn(createTextColumn(tableManager, true),
		Messages.ReportStepSash_1, 100, false, true, null);
	tableManager.addColumn(createTextColumn(tableManager, true),
		Messages.ReportStepSash_2, 100, false, true, null);
	tableManager.addColumn(createTextColumn(tableManager, false),
		Messages.ReportStepSash_3, 100, false, true, null);
	tableManager.addColumn(createImageColumn(tableManager),
		Messages.ReportStepSash_4, 20, true, false, null);
    }

    public void setLayoutData(final Object layout) {
	tableManager.setLayoutData(layout);
    }

    public SashManager<TestStepDescriptor> getSashManager() {
	return tableManager;
    }

    public void setContent(final List<TestStepDescriptor> TestStepDescriptors) {
	tableManager.setContent(TestStepDescriptors);
    }

    private SashManager<TestStepDescriptor>.SashManagerColumn<RichText> createTextColumn(
	    final SashManager<TestStepDescriptor> tableManager,
	    final boolean change) {
	return tableManager.new SashManagerColumn<RichText>() {

	    @Override
	    public RichText create(final Composite parent,
		    final ModifyListener heightListener) {
		final RichText text = new RichText(parent, SWT.MULTI | SWT.WRAP
			| SWT.READ_ONLY);
		text.setProjectName(projectName);
		text.removeTextListeners();
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
			String real = data.getRealResult();
			if (real.length() < 13) {
			    real = "<html></html>"; //$NON-NLS-1$
			}
			widget.setFormattedText(real);

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

    private SashManager<TestStepDescriptor>.SashManagerColumn<Label> createImageColumn(
	    final SashManager<TestStepDescriptor> tableManager) {
	return tableManager.new SashManagerColumn<Label>() {
	    @Override
	    public Label create(final Composite parent,
		    final ModifyListener heightListener) {
		final Label image = new Label(parent, SWT.NONE);
		final Color white = new Color(null, 255, 255, 255);
		image.setBackground(white);
		white.dispose();

		return image;
	    }

	    @Override
	    protected int getHeight(final Label widget) {
		return 30;
	    }

	    @Override
	    public void renderTC(final Label widget,
		    final TestStepDescriptor data, final int row,
		    final int column) {
		switch (data.getStatus()) {
		case failed:
		    widget.setImage(ResourceManager.getImgRed());
		    break;
		case notExecuted:
		    widget.setImage(ResourceManager.getImgGray());
		    break;
		case passed:
		    widget.setImage(ResourceManager.getImgGreen());
		    break;
		case passedWithAnnotation:
		    widget.setImage(ResourceManager.getImgOrange());
		    break;
		default:
		    break;

		}
	    }

	    @Override
	    protected TestStepDescriptor writeContentToData(
		    final Label control, final TestStepDescriptor data,
		    final int row, final int column) {
		return data;
	    }

	    @Override
	    protected void renderCustom(final Label widget,
		    final String[] data, final int row, final int column) {
		// not needed

	    }

	};
    }

    private SashManager<TestStepDescriptor>.SashManagerColumn<Label> createLabelColumn(
	    final SashManager<TestStepDescriptor> tableManager) {
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

    public String getProjectName() {
	return projectName;
    }

    public void setProjectName(final String projectName) {
	this.projectName = projectName;
    }

    public List<TestStepDescriptor> getAllSteps() {
	return tableManager.getContent();
    }
}
