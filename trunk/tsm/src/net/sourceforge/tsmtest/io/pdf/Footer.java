/*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 	Verena Käfer - initial version
 * 	Tobias Hirning - i18n
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.io.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author Verena Käfer
 * 
 *         This class makes a footer with the same id on each page
 */
class Footer extends PdfPageEventHelper {

    private int pagenumber;
    private long id;
    private static Font normalFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

    /**
     * @param id
     *            the id for the pages
     */
    public Footer(long id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(com.itextpdf.text
     * .pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onStartPage(PdfWriter writer, Document document) {
	pagenumber++;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(com.itextpdf.text.
     * pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onEndPage(PdfWriter writer, Document document) {
	Rectangle rect = writer.getBoxSize("art"); //$NON-NLS-1$
	ColumnText.showTextAligned(writer.getDirectContent(),
		Element.ALIGN_CENTER,
		new Phrase(String.format("Page %d", pagenumber)), //$NON-NLS-1$
		(rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18,
		0);
	ColumnText.showTextAligned(writer.getDirectContent(),
		Element.ALIGN_CENTER, new Phrase(String.format("ID %d", id), //$NON-NLS-1$
			normalFont), rect.getLeft(), rect.getBottom() - 18, 0);
    }
}