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

import net.sourceforge.tsmtest.Messages;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * This class wirtes a different id for every test case
 * 
 * @author Verena Käfer
 * 
 */
class FooterOneFile extends PdfPageEventHelper {
    int pagenumber;
    long id;
    private static Font normalFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

    /**
     * @param id
     *            The new id
     */
    public void setId(long id) {
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
	// not appendix
	if ((id != -1)) {
	    Rectangle rect = writer.getBoxSize("art"); //$NON-NLS-1$
	    ColumnText.showTextAligned(writer.getDirectContent(),
		    Element.ALIGN_CENTER,
		    new Phrase(String.format(Messages.FooterOneFile_0, pagenumber)),
		    (rect.getLeft() + rect.getRight()) / 2,
		    rect.getBottom() - 18, 0);
//	    ColumnText.showTextAligned(writer.getDirectContent(),
//		    Element.ALIGN_CENTER, new Phrase(
//			    String.format("ID %d", id), normalFont), rect //$NON-NLS-1$
//			    .getLeft(), rect.getBottom() - 18, 0);
	} else {
	    Rectangle rect = writer.getBoxSize("art"); //$NON-NLS-1$
	    ColumnText.showTextAligned(writer.getDirectContent(),
		    Element.ALIGN_CENTER,
		    new Phrase(String.format("Page %d", pagenumber)), //$NON-NLS-1$
		    (rect.getLeft() + rect.getRight()) / 2,
		    rect.getBottom() - 18, 0);
	}
    }
}