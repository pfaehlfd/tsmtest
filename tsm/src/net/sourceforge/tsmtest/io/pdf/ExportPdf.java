/*******************************************************************************
 * Copyright (c) 2012-2013 Verena Käfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 	Verena Käfer - initial version
 * 	Wolfgang Kraus - some fixes
 * 	Albert Flaig - new data model
 * 	Tobias Hirning - i18n and refactoring
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.io.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tsmtest.Messages;
import net.sourceforge.tsmtest.datamodel.DataModelTypes;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.descriptors.ITestCaseDescriptor;
import net.sourceforge.tsmtest.datamodel.descriptors.TestStepDescriptor;
import net.sourceforge.tsmtest.gui.ResourceManager;
import net.sourceforge.tsmtest.gui.richtexteditor.RichText;
import net.sourceforge.tsmtest.io.pdf.FontsToolsConstants.ExportType;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author Verena Käfer
 * 
 *         This class creates a PDF of a given TSMReport.
 * 
 */
public class ExportPdf {
    private static boolean bold;
    private static boolean italic;
    private static boolean underline;
    private static boolean strike;
    
    private static PdfWriter writer;

    private static List<Image> imageList = new ArrayList<Image>();
    private static List<String> nameList = new ArrayList<String>();
    private static int counterForImages = 1;

    private static boolean isTestStep = false;


    public ExportPdf() {

    }

    /**
     * @param newList The IFile of the exported protocol or test case.
     * @param path The path where it should be saved.
     * @param exportType The type of export (one file, multiple files, one test case with all revisions).
     * @param monitor
     * @throws DocumentException
     * @throws IOException
     */
    public static void print(List<TSMResource> newList, String path,
	    ExportType exportType, IProgressMonitor monitor)
	    throws DocumentException, IOException {
	IProgressMonitor progressMonitor = FontsToolsConstants.startMonitor(monitor, "document", 1);
	if (!progressMonitor.isCanceled()) {
	    //Export in one file.
	    if (exportType == ExportType.ONE_FILE) {
		counterForImages = 1;
		Document document = new Document();
		document.setPageSize(PageSize.A4);
		document.setMargins(FontsToolsConstants.LEFT_MARGIN, FontsToolsConstants.RIGHT_MARGIN, FontsToolsConstants.TOP_MARGIN, FontsToolsConstants.BOTTOM_MARGIN);
		// reset list
		imageList = new ArrayList<Image>();
		nameList = new ArrayList<String>();
		String fileName;

		//Check for correct filename extension.
		if (path.endsWith(".pdf")) { //$NON-NLS-1$
		    fileName = path;
		} else {
		    fileName = path + ".pdf"; //$NON-NLS-1$
		}
		writer = PdfWriter.getInstance(document, new FileOutputStream(
			fileName));
		FooterOneFile footer = new FooterOneFile();
		writer.setBoxSize("art", new Rectangle(36, 54, 559, 788)); //$NON-NLS-1$
		writer.setPageEvent(footer);

		document.open();
		addContentOneFile(document, newList, footer,
			FontsToolsConstants.getSubMonitor(monitor, 1));
		document.close();
	    } 
	    //Export in multiple files.
	    else if (exportType == ExportType.ONE_FILE){
		// go through the list and create one document for each entry
		for (TSMResource currentResource : newList) {
		    counterForImages = 1;
		    Document document = new Document();
		    document.setPageSize(PageSize.A4);
		    document.setMargins(FontsToolsConstants.LEFT_MARGIN, FontsToolsConstants.RIGHT_MARGIN, FontsToolsConstants.TOP_MARGIN, FontsToolsConstants.BOTTOM_MARGIN);
		    // reset list
		    imageList = new ArrayList<Image>();
		    nameList = new ArrayList<String>();

		    String fileName = path + currentResource.getPath() + "/" + currentResource.getName() //$NON-NLS-1$
			    + ".pdf"; //$NON-NLS-1$

		    File destinationFolder = new File(path + currentResource.getPath());

		    //If export folder does not exist create it.
		    if (!destinationFolder.exists()) {
			if (!destinationFolder.mkdirs()) {
			    throw new IOException(Messages.ExportPdf_0);
			}
		    }
		    destinationFolder.setWritable(true);
		    destinationFolder.setReadable(true);

		    java.io.FileOutputStream fos = new java.io.FileOutputStream(
			    fileName);

		    writer = PdfWriter.getInstance(document, fos);

		    Footer footer = null;
		    if (currentResource instanceof TSMTestCase) {
			ITestCaseDescriptor t = ((TSMTestCase) currentResource).getData();
			footer = new Footer(t.getId());
		    } else {
			ITestCaseDescriptor t = ((TSMReport) currentResource).getData();
			footer = new Footer(t.getId());
		    }

		    writer.setBoxSize("art", new Rectangle(36, 54, 559, 788)); //$NON-NLS-1$
		    writer.setPageEvent(footer);

		    document.open();
		    addMetaData(document, currentResource);
		    addContent(document, currentResource, FontsToolsConstants.getSubMonitor(monitor, 1));
		    document.close();
		}

	    }
	    progressMonitor.done();
	} else {
	    progressMonitor.done();
	}
    }
    
    /**
     * This method adds the chapters if all test cases are exported in one file
     * 
     * @param document
     *            The whole document
     * @param newList
     *            The list of the files to export
     * @param footer
     *            The footer
     * @param monitor
     * @throws MalformedURLException
     * @throws DocumentException
     * @throws IOException
     */
    private static void addContentOneFile(Document document,
	    List<TSMResource> newList, FooterOneFile footer,
	    IProgressMonitor monitor) throws MalformedURLException,
	    DocumentException, IOException {
	IProgressMonitor progressMonitor = FontsToolsConstants.startMonitor(monitor, " -> content", 1);
	int chapterNumber = 1;
	Chapter chapter = null;
	// go through all files
	for (int i = 0; i < newList.size(); i++) {
	    if (!progressMonitor.isCanceled()) {
		TSMResource file = newList.get(i);
		footer.setId(FontsToolsConstants.getId(file));
		// first element has a new chapter
		if (i == 0) {
		    Anchor anchor = new Anchor(file.getProject().getName(),
			    FontsToolsConstants.BIG_BOLD);
		    anchor.setName(file.getProject().getName());
		    // Second parameter is the number of the chapter
		    chapter = new Chapter(new Paragraph(anchor), chapterNumber);
		    chapterNumber++;

		    Paragraph newLineParagraph = new Paragraph();
		    addEmptyLine(newLineParagraph, 1);
		    chapter.add(newLineParagraph);

		    Paragraph entryParagraph = new Paragraph(file.getName());
		    createEntries(document, entryParagraph, file, footer,
			    FontsToolsConstants.getSubMonitor(monitor, 1));
		    chapter.addSection(entryParagraph);
		} else {
		    // if same project it is the same chapter
		    if (file.getProject().equals(
			    newList.get(i - 1).getProject())) {
			chapter.newPage();
			Paragraph entryParagraph = new Paragraph(file.getName());
			createEntries(document, entryParagraph, file, footer,
				FontsToolsConstants.getSubMonitor(monitor, 1));
			chapter.addSection(entryParagraph);
		    } else {
			// Add the old chapter before creating a new one
			document.add(chapter);
			Anchor anchor = new Anchor(file.getProject().getName(),
				FontsToolsConstants.BIG_BOLD);
			anchor.setName(file.getProject().getName());
			// Second parameter is the number of the chapter
			chapter = new Chapter(new Paragraph(anchor),
				chapterNumber);
			chapterNumber++;
			Paragraph entryParagraph = new Paragraph(file.getName());
			createEntries(document, entryParagraph, file, footer,
				FontsToolsConstants.getSubMonitor(monitor, 1));
			chapter.addSection(entryParagraph);
		    }
		}
	    }
	}
	    // add last chapter
	    document.add(chapter);
	// only appendix if images exist
	if (!imageList.isEmpty()) {

	    Chunk newPage = new Chunk();
	    newPage.setNewPage();

	    // go through all images and add them in an appendix
	    float height = document.getPageSize().getHeight()
		    - document.bottomMargin() - document.topMargin() - 15;
	    float width = document.getPageSize().getWidth()
		    - document.rightMargin() - document.leftMargin();

	    PdfPTable imageTable = new PdfPTable(1);
	    imageTable.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
	    Phrase appendix = new Phrase(Messages.ExportPdf_25, FontsToolsConstants.BIG_BOLD);
	    Paragraph emptyLine = new Paragraph();
	    addEmptyLine(emptyLine, 2);
	    appendix.add(emptyLine);

	    int counter = 1;

	    for (int j = 0; j < imageList.size(); j++) {
		Image i = imageList.get(j);
		// image wider or longer than page
		if (i.getWidth() > width || i.getHeight() > height) {
		    i.scaleToFit(width, height);
		}

		float availableSpace = writer.getVerticalPosition(false)
			- document.bottomMargin();

		// image scaled down but not enough space
		if (i.getHeight() > height && i.getHeight() > availableSpace) {
		    appendix.add(newPage);
		    // not scaled but still too big
		} else if (i.getHeight() > availableSpace) {
		    appendix.add(newPage);
		}
		Chunk imageChunk = new Chunk(i, 0, 0, true);
		Anchor target = new Anchor(imageChunk);
		target.setName(Integer.toString(counter));
		target.setReference("#back" + Integer.toString(counter)); //$NON-NLS-1$

		Paragraph p = new Paragraph();
		p.add(target);

		// add image
		appendix.add(p);

		// empty line
		appendix.add(emptyLine);
		appendix.add(new Paragraph(nameList.get(j), //$NON-NLS-1$
			FontsToolsConstants.SMALL_FONT));
		appendix.add(emptyLine);
		counter++;
	    }
	    // new page

	    PdfPCell appendixCell = new PdfPCell(appendix);
	    appendixCell.setBorder(Rectangle.NO_BORDER);
	    imageTable.addCell(appendixCell);
	    document.add(imageTable);
	}
	progressMonitor.done();
    }

    /**
     * The method adds meta data to the PDF.
     * 
     * @param document
     *            The document the PDF will be created from. To this document
     *            the content will be added.
     * 
     * @param file
     *            The IFile of the protocol or test case
     */
    private static void addMetaData(Document document, TSMResource file) {
	// protocol
	if (file instanceof TSMReport) {
	    ITestCaseDescriptor protocol = ((TSMReport) file).getData();
	    document.addTitle(file.getName());
	    document.addSubject(Messages.ExportPdf_13);
	    document.addKeywords(Messages.ExportPdf_14);
	    document.addAuthor(protocol.getAuthor());
	    // test case
	} else if (file instanceof TSMTestCase) {
	    ITestCaseDescriptor testCase = ((TSMTestCase) file).getData();
	    document.addTitle(file.getName());
	    document.addSubject(Messages.ExportPdf_15);
	    document.addKeywords(Messages.ExportPdf_16);
	    document.addAuthor(testCase.getAuthor());
	}
    }

    /**
     * @param document The document the content will be added to.
     * @param file The IFile of the protocol or test case.
     * @param monitor The progress monitor.
     * @throws DocumentException
     * @throws IOException
     */
    private static void addContent(Document document, TSMResource file,
	    IProgressMonitor monitor) throws DocumentException, IOException {
	IProgressMonitor progressMonitor = FontsToolsConstants.startMonitor(monitor, " -> " + file.getName(), 1);
	if (!progressMonitor.isCanceled()) {
	    // protocol
	    if (file instanceof TSMReport) {
		ITestCaseDescriptor protocol = ((TSMReport) file).getData();
		Anchor anchor = new Anchor(file.getName(), FontsToolsConstants.BIG_BOLD);
		anchor.setName(file.getName());

		// Second parameter is the number of the chapter
		Section chapter = new Chapter(new Paragraph(anchor), 1);
		// number is not shown
		chapter.setNumberDepth(0);

		Paragraph emptyLine = new Paragraph();
		addEmptyLine(emptyLine, 1);
		chapter.add(emptyLine);

		// add content
		createContentTable(chapter, file, FontsToolsConstants.getSubMonitor(monitor, 1));

		PdfPTable shortDescriptionTable = new PdfPTable(1);
		shortDescriptionTable.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase shortDescriptionEntry = new Phrase();
		Paragraph shortDescription = new Paragraph(
			Messages.ExportPdf_17, FontsToolsConstants.boldFont);
		Paragraph shortDescriptionLine = new Paragraph("", FontsToolsConstants.normalFont); //$NON-NLS-1$
		addEmptyLine(shortDescriptionLine, 1);
		shortDescriptionEntry.add(shortDescription);
		shortDescriptionEntry.add(shortDescriptionLine);
		parse(shortDescriptionEntry, protocol.getShortDescription(),
			document, file);
		PdfPCell shortDescriptionCell = new PdfPCell(
			shortDescriptionEntry);
		shortDescriptionCell.setBorder(Rectangle.NO_BORDER);
		shortDescriptionTable.addCell(shortDescriptionCell);
		chapter.add(shortDescriptionTable);

		chapter.add(emptyLine);

		PdfPTable preconditionTable = new PdfPTable(1);
		preconditionTable.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase preconditionEntry = new Phrase();
		Paragraph precondition = new Paragraph(Messages.ExportPdf_19,
			FontsToolsConstants.boldFont);
		preconditionEntry.add(precondition);
		preconditionEntry.add(shortDescriptionLine);
		parse(preconditionEntry, protocol.getRichTextPrecondition(),
			document, file);
		PdfPCell preconditionCell = new PdfPCell(preconditionEntry);
		preconditionCell.setBorder(Rectangle.NO_BORDER);
		preconditionTable.addCell(preconditionCell);
		chapter.add(preconditionTable);

		chapter.add(emptyLine);
		chapter.add(emptyLine);

		createStepTable(chapter, file, document,
			FontsToolsConstants.getSubMonitor(monitor, 1));
		chapter.add(emptyLine);

		PdfPTable finalResultTable = new PdfPTable(1);
		finalResultTable.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase finalResultEntry = new Phrase();
		Paragraph finalResult = new Paragraph(Messages.ExportPdf_20,
			FontsToolsConstants.boldFont);
		finalResultEntry.add(finalResult);
		finalResultEntry.add(shortDescriptionLine);
		parse(finalResultEntry, protocol.getRichTextResult(), document,
			file);
		PdfPCell finalResultCell = new PdfPCell(finalResultEntry);
		finalResultCell.setBorder(Rectangle.NO_BORDER);
		finalResultTable.addCell(finalResultCell);
		chapter.add(finalResultTable);

		// Now add all this to the document
		document.add(chapter);
	    }
	    // test case
	    else if (file instanceof TSMTestCase) {
		ITestCaseDescriptor testCase = ((TSMTestCase) file).getData();
		Anchor anchor = new Anchor(file.getName(), FontsToolsConstants.BIG_BOLD);
		anchor.setName(file.getName());

		// Second parameter is the number of the chapter
		Section chapter = new Chapter(new Paragraph(anchor), 1);
		// number is not shown
		chapter.setNumberDepth(0);

		Paragraph empyLine = new Paragraph();
		addEmptyLine(empyLine, 1);
		chapter.add(empyLine);

		// add content
		createContentTable(chapter, file, FontsToolsConstants.getSubMonitor(monitor, 1));

		PdfPTable shortDescriptionTable = new PdfPTable(1);
		shortDescriptionTable.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase shortDescriptionEntry = new Phrase();
		Paragraph shortDescription = new Paragraph(
			Messages.ExportPdf_21, FontsToolsConstants.boldFont);
		shortDescriptionEntry.add(shortDescription);
		shortDescriptionEntry.add(empyLine);
		parse(shortDescriptionEntry, testCase.getShortDescription(),
			document, file);
		PdfPCell shortDescriptionCell = new PdfPCell(
			shortDescriptionEntry);
		shortDescriptionCell.setBorder(Rectangle.NO_BORDER);
		shortDescriptionTable.addCell(shortDescriptionCell);
		chapter.add(shortDescriptionTable);

		chapter.add(empyLine);

		PdfPTable preconditionTable = new PdfPTable(1);
		preconditionTable.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase preconditionEntry = new Phrase();
		Paragraph precondition = new Paragraph(Messages.ExportPdf_22,
			FontsToolsConstants.boldFont);
		preconditionEntry.add(precondition);
		preconditionEntry.add(empyLine);
		parse(preconditionEntry, testCase.getRichTextPrecondition(),
			document, file);
		PdfPCell preconditionCell = new PdfPCell(preconditionEntry);
		preconditionCell.setBorder(Rectangle.NO_BORDER);
		preconditionTable.addCell(preconditionCell);
		chapter.add(preconditionTable);

		chapter.add(empyLine);
		chapter.add(empyLine);

		createStepTable(chapter, file, document,
			FontsToolsConstants.getSubMonitor(monitor, 1));
		chapter.add(empyLine);

		Paragraph finalResult = new Paragraph(Messages.ExportPdf_23,
			FontsToolsConstants.boldFont);
		chapter.add(finalResult);

		PdfPTable finalResultTable = new PdfPTable(1);
		finalResultTable.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase line = new Phrase();

		parse(line, "", document, file); //$NON-NLS-1$
		PdfPCell finalResultCell = new PdfPCell(line);
		finalResultCell.setBorder(Rectangle.NO_BORDER);
		finalResultTable.addCell(finalResultCell);
		finalResultTable.addCell(finalResultCell);
		chapter.add(finalResultTable);

		// Now add all this to the document
		document.add(chapter);
	    }

	    // only appendix if images exist
	    if (!imageList.isEmpty()) {

		Chunk newPage = new Chunk();
		newPage.setNewPage();

		// go through all images and add them in an appendix
		float height = document.getPageSize().getHeight()
			- document.bottomMargin() - document.topMargin() - 15;
		float width = document.getPageSize().getWidth()
			- document.rightMargin() - document.leftMargin();

		PdfPTable imageTable = new PdfPTable(1);
		imageTable.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase appendix = new Phrase(Messages.ExportPdf_25, FontsToolsConstants.BIG_BOLD);
		Paragraph emptyLine = new Paragraph();
		addEmptyLine(emptyLine, 2);
		appendix.add(emptyLine);

		int counter = 1;

		for (int j = 0; j < imageList.size(); j++) {
		    Image i = imageList.get(j);
		    // image wider or longer than page
		    if (i.getWidth() > width || i.getHeight() > height) {
			i.scaleToFit(width, height);
		    }

		    float availableSpace = writer.getVerticalPosition(false)
			    - document.bottomMargin();

		    // image scaled down but not enough space
		    if (i.getHeight() > height
			    && i.getHeight() > availableSpace) {
			appendix.add(newPage);
			// not scaled but still too big
		    } else if (i.getHeight() > availableSpace) {
			appendix.add(newPage);
		    }
		    Chunk imageChunk = new Chunk(i, 0, 0, true);
		    Anchor target = new Anchor(imageChunk);
		    target.setName(Integer.toString(counter));
		    target.setReference("#back" + Integer.toString(counter)); //$NON-NLS-1$

		    Paragraph p = new Paragraph();
		    p.add(target);

		    // add image
		    appendix.add(p);

		    // empty line
		    appendix.add(emptyLine);
		    appendix.add(new Paragraph(nameList.get(j), //$NON-NLS-1$
			    FontsToolsConstants.SMALL_FONT));
		    appendix.add(emptyLine);
		    counter++;
		}
		// new page

		PdfPCell appendixCell = new PdfPCell(appendix);
		appendixCell.setBorder(Rectangle.NO_BORDER);
		imageTable.addCell(appendixCell);
		document.add(imageTable);
	    }
	    progressMonitor.done();
	} else {
	    progressMonitor.done();
	}
    }

    /**
     * @param section The section where the table is in.
     * @param file The file the data comes from.
     * @param monitor
     * @throws DocumentException
     * @throws MalformedURLException
     * @throws IOException
     */
    private static void createContentTable(Section section, TSMResource file,
	    IProgressMonitor monitor) throws DocumentException,
	    MalformedURLException, IOException {
	IProgressMonitor progressMonitor = FontsToolsConstants.startMonitor(monitor, " -> content", 1);
	if (!progressMonitor.isCanceled()) {
	    //date format from the data model.
	    final SimpleDateFormat dateFormat = DataModelTypes.getDateFormat();

	    if (file instanceof TSMReport) {
		ITestCaseDescriptor protocol = ((TSMReport) file).getData();
		PdfPTable table = new PdfPTable(2);

		table.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		table.setWidths(new float[] { 30, 70 });

		PdfPCell project = new PdfPCell(new Phrase(
			Messages.ExportPdf_28, FontsToolsConstants.boldFont));
		project.setBorder(Rectangle.NO_BORDER);
		table.addCell(project);
		PdfPCell projectEntry = new PdfPCell(new Phrase(file
			.getProject().getName()));
		projectEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(projectEntry);

		PdfPCell pack = new PdfPCell(new Phrase(Messages.ExportPdf_29,
			FontsToolsConstants.boldFont));
		pack.setBorder(Rectangle.NO_BORDER);
		table.addCell(pack);
		PdfPCell packageEntry;
		if (file.getProject().equals(file.getParent())) {
		    packageEntry = new PdfPCell(new Phrase("-")); //$NON-NLS-1$
		} else {
		    packageEntry = new PdfPCell(new Phrase(file.getParent()
			    .getName()));
		}
		packageEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(packageEntry);

		PdfPCell emptyCell = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		emptyCell.setBorder(Rectangle.NO_BORDER);
		table.addCell(emptyCell);
		table.addCell(emptyCell);

		PdfPCell priority = new PdfPCell(new Phrase(
			Messages.ExportPdf_33, FontsToolsConstants.boldFont));
		priority.setBorder(Rectangle.NO_BORDER);
		table.addCell(priority);
		PdfPCell priorityEntry = new PdfPCell(new Phrase(protocol
			.getPriority().toString()));
		priorityEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(priorityEntry);

		PdfPCell revision = new PdfPCell(new Phrase(
			Messages.ExportPdf_2, FontsToolsConstants.boldFont));
		revision.setBorder(Rectangle.NO_BORDER);
		table.addCell(revision);
		PdfPCell revisionEntry = new PdfPCell(new Phrase(
			protocol.getRevisionNumber() + ""));
		revisionEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(revisionEntry);

		PdfPCell expectedDuration = new PdfPCell(new Phrase(
			Messages.ExportPdf_34, FontsToolsConstants.boldFont));
		expectedDuration.setBorder(Rectangle.NO_BORDER);
		table.addCell(expectedDuration);
		PdfPCell expectedDurationEntry = new PdfPCell(new Phrase(
			protocol.getExpectedDuration()));
		expectedDurationEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(expectedDurationEntry);

		PdfPCell realDuration = new PdfPCell(new Phrase(
			Messages.ExportPdf_35, FontsToolsConstants.boldFont));
		realDuration.setBorder(Rectangle.NO_BORDER);
		table.addCell(realDuration);

		PdfPCell realDurationEntry = new PdfPCell(new Phrase(
			protocol.getRealDuration()));
		realDurationEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(realDurationEntry);

		PdfPCell creator = new PdfPCell(new Phrase(
			Messages.ExportPdf_36, FontsToolsConstants.boldFont));
		creator.setBorder(Rectangle.NO_BORDER);
		table.addCell(creator);
		PdfPCell creatorEntry = new PdfPCell(new Phrase(
			protocol.getAuthor()));
		creatorEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(creatorEntry);

		PdfPCell tester = new PdfPCell(new Phrase("Tester: ", FontsToolsConstants.boldFont));
		tester.setBorder(Rectangle.NO_BORDER);
		table.addCell(tester);
		PdfPCell testerEntry = new PdfPCell(new Phrase(
			protocol.getAssignedTo()));
		testerEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(testerEntry);

		PdfPCell executions = new PdfPCell(new Phrase(
			Messages.ExportPdf_37, FontsToolsConstants.boldFont));
		executions.setBorder(Rectangle.NO_BORDER);
		table.addCell(executions);
		PdfPCell executionsEntry = new PdfPCell(new Phrase(
			Integer.toString(protocol.getNumberOfExecutions())));
		executionsEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(executionsEntry);

		PdfPCell failures = new PdfPCell(new Phrase(
			Messages.ExportPdf_38, FontsToolsConstants.boldFont));
		failures.setBorder(Rectangle.NO_BORDER);
		table.addCell(failures);
		PdfPCell failuresEntry = new PdfPCell(new Phrase(
			Integer.toString(protocol.getNumberOfFailures())));
		failuresEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(failuresEntry);

		// PdfPCell lastExecution = new PdfPCell(new Phrase(
		// Messages.ExportPdf_39, boldFont));
		// lastExecution.setBorder(Rectangle.NO_BORDER);
		// table.addCell(lastExecution);
		//
		// PdfPCell lastExecutionEntry = new PdfPCell(new Phrase(
		// DataModelTypes.dateFormat.format(protocol
		// .getLastExecution())));
		// lastExecutionEntry.setBorder(Rectangle.NO_BORDER);
		// table.addCell(lastExecutionEntry);

		PdfPCell lastChanged = new PdfPCell(new Phrase(
			Messages.ExportPdf_40, FontsToolsConstants.boldFont));
		lastChanged.setBorder(Rectangle.NO_BORDER);
		table.addCell(lastChanged);
		PdfPCell lastChangedEntry = new PdfPCell(new Phrase(
			dateFormat.format(protocol
				.getLastChangedOn()), FontsToolsConstants.normalFont));
		lastChangedEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(lastChangedEntry);

		PdfPCell status = new PdfPCell(new Phrase(
			Messages.ExportPdf_41, FontsToolsConstants.boldFont));
		status.setBorder(Rectangle.NO_BORDER);
		table.addCell(status);
		if (protocol.getStatus() == DataModelTypes.StatusType.passed) {
		    Image image = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathAccept()));
		    image.scaleToFit(10, 10);
		    Paragraph statusEntry = new Paragraph();
		    statusEntry.add(new Phrase(Messages.ExportPdf_42));
		    statusEntry.add(new Chunk(image, -1f, 1f));
		    PdfPCell statusCell = new PdfPCell(statusEntry);
		    statusCell.setBorder(Rectangle.NO_BORDER);
		    statusCell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(statusCell);
		} else if (protocol.getStatus() == DataModelTypes.StatusType.passedWithAnnotation) {
		    Image image = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathInformation()));
		    image.scaleToFit(10, 10);
		    Paragraph statusEntry = new Paragraph();
		    statusEntry.add(new Phrase(Messages.ExportPdf_43));
		    statusEntry.add(new Chunk(image, -1f, 1f));
		    PdfPCell statusCell = new PdfPCell(statusEntry);
		    statusCell.setBorder(Rectangle.NO_BORDER);
		    statusCell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(statusCell);
		} else if (protocol.getStatus() == DataModelTypes.StatusType.failed) {
		    Image image = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathCross()));
		    image.scaleToFit(10, 10);
		    Paragraph statusEntry = new Paragraph();
		    statusEntry.add(new Phrase(Messages.ExportPdf_44 + "   ")); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
		    statusEntry.add(new Chunk(image, -1f, 1f));
		    PdfPCell statusCell = new PdfPCell(statusEntry);
		    statusCell.setBorder(Rectangle.NO_BORDER);
		    statusCell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(statusCell);
		} else {
		    Paragraph statusEntry = new Paragraph(""); //$NON-NLS-1$
		    PdfPCell statusCell = new PdfPCell(statusEntry);
		    statusCell.setBorder(Rectangle.NO_BORDER);
		    table.addCell(statusCell);

		}

		// line
		PdfPCell line = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		line.setBorder(Rectangle.BOTTOM);
		table.addCell(line);
		PdfPCell line2 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		line2.setBorder(Rectangle.BOTTOM);
		table.addCell(line2);

		PdfPCell line3 = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		line3.setBorder(Rectangle.TOP);
		table.addCell(line3);
		PdfPCell line4 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		line4.setBorder(Rectangle.TOP);
		table.addCell(line4);

		section.add(table);

		// test case
	    } else if (file instanceof TSMTestCase) {
		ITestCaseDescriptor testCase = ((TSMTestCase) file).getData();
		PdfPTable table = new PdfPTable(2);

		table.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		table.setWidths(new float[] { 30, 70 });

		PdfPCell c1 = new PdfPCell(new Phrase(Messages.ExportPdf_51,
			FontsToolsConstants.boldFont));
		c1.setBorder(Rectangle.NO_BORDER);
		table.addCell(c1);
		PdfPCell pdfCell2 = new PdfPCell(new Phrase(file.getProject()
			.getName()));
		pdfCell2.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell2);

		PdfPCell c3 = new PdfPCell(new Phrase(Messages.ExportPdf_52,
			FontsToolsConstants.boldFont));
		c3.setBorder(Rectangle.NO_BORDER);
		table.addCell(c3);
		PdfPCell pdfCell4;
		if (file.getProject().equals(file.getParent())) {
		    pdfCell4 = new PdfPCell(new Phrase("-")); //$NON-NLS-1$
		} else {
		    pdfCell4 = new PdfPCell(new Phrase(file.getParent().getName()));
		}
		pdfCell4.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell4);

		PdfPCell pdfCell5 = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		pdfCell5.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell5);
		PdfPCell pdfCell6 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		pdfCell6.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell6);

		PdfPCell pdfCell7 = new PdfPCell(new Phrase(Messages.ExportPdf_56,
			FontsToolsConstants.boldFont));
		pdfCell7.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell7);
		PdfPCell pdfCell8 = new PdfPCell(new Phrase(testCase.getPriority()
			.toString()));
		pdfCell8.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell8);

		PdfPCell pdfCell9 = new PdfPCell(new Phrase(Messages.ExportPdf_57,
			FontsToolsConstants.boldFont));
		pdfCell9.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell9);
		PdfPCell pdfCell10 = new PdfPCell(new Phrase(
			testCase.getExpectedDuration()));
		pdfCell10.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell10);

		PdfPCell c11 = new PdfPCell(new Phrase(Messages.ExportPdf_58,
			FontsToolsConstants.boldFont));
		c11.setBorder(Rectangle.NO_BORDER);
		table.addCell(c11);
		// System.out.println(testCase.getRealDuration());
		PdfPCell pdfCell12 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		pdfCell12.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell12);

		PdfPCell pdfCell13 = new PdfPCell(new Phrase(Messages.ExportPdf_60,
			FontsToolsConstants.boldFont));
		pdfCell13.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell13);
		PdfPCell pdfCell14 = new PdfPCell(new Phrase(testCase.getAuthor()));
		pdfCell14.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell14);

		PdfPCell c15 = new PdfPCell(new Phrase(Messages.ExportPdf_61,
			FontsToolsConstants.boldFont));
		c15.setBorder(Rectangle.NO_BORDER);
		table.addCell(c15);
		PdfPCell pdfCell16 = new PdfPCell(new Phrase(
			Integer.toString(testCase.getNumberOfExecutions())));
		pdfCell16.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell16);

		PdfPCell pdfCell17 = new PdfPCell(new Phrase(Messages.ExportPdf_62,
			FontsToolsConstants.boldFont));
		pdfCell17.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell17);
		PdfPCell pdfCell18 = new PdfPCell(new Phrase(
			Integer.toString(testCase.getNumberOfFailures())));
		pdfCell18.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell18);

		PdfPCell pdfCell19 = new PdfPCell(new Phrase(Messages.ExportPdf_63,
			FontsToolsConstants.boldFont));
		pdfCell19.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell19);

		PdfPCell pdfCell20;
		if (testCase.getLastExecution() == null) {
		    pdfCell20 = new PdfPCell(new Phrase(Messages.ExportPdf_64));
		} else {

		    pdfCell20 = new PdfPCell(new Phrase(
			    dateFormat.format(testCase
				    .getLastExecution())));
		}
		pdfCell20.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell20);

		PdfPCell pdfCell21 = new PdfPCell(new Phrase(Messages.ExportPdf_65,
			FontsToolsConstants.boldFont));
		pdfCell21.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell21);
		PdfPCell pdfCell22 = new PdfPCell(new Phrase(
			dateFormat.format(testCase
				.getLastChangedOn()), FontsToolsConstants.normalFont));
		pdfCell22.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell22);

		PdfPCell pdfCell25 = new PdfPCell(new Phrase(Messages.ExportPdf_66,
			FontsToolsConstants.boldFont));
		pdfCell25.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell25);
		if (testCase.getStatus() == DataModelTypes.StatusType.passed) {
		    Image imageAccept = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathAccept()));
		    imageAccept.scaleToFit(10, 10);
		    Paragraph paragraph = new Paragraph();
		    paragraph.add(new Phrase(Messages.ExportPdf_67));
		    paragraph.add(new Chunk(imageAccept, -1f, 1f));
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(cell);
		} else if (testCase.getStatus() == DataModelTypes.StatusType.passedWithAnnotation) {
		    Image imageInformation = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathInformation()));
		    imageInformation.scaleToFit(10, 10);
		    Paragraph paragraph = new Paragraph();
		    paragraph.add(new Phrase(Messages.ExportPdf_68));
		    paragraph.add(new Chunk(imageInformation, -1f, 1f));
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(cell);
		} else if (testCase.getStatus() == DataModelTypes.StatusType.failed) {
		    Image imageCross = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathCross()));
		    imageCross.scaleToFit(10, 10);
		    Paragraph paragraph = new Paragraph();
		    paragraph.add(new Phrase(Messages.ExportPdf_69 + "   ")); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
		    paragraph.add(new Chunk(imageCross, -1f, 1f));
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(cell);
		} else {
		    Paragraph paragraph = new Paragraph(""); //$NON-NLS-1$
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    table.addCell(cell);

		}

		// line
		PdfPCell pdfCell27 = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		pdfCell27.setBorder(Rectangle.BOTTOM);
		table.addCell(pdfCell27);
		PdfPCell pdfCell28 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		pdfCell28.setBorder(Rectangle.BOTTOM);
		table.addCell(pdfCell28);

		PdfPCell pdfCell29 = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		pdfCell29.setBorder(Rectangle.TOP);
		table.addCell(pdfCell29);
		PdfPCell pdfCell30 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		pdfCell30.setBorder(Rectangle.TOP);
		table.addCell(pdfCell30);

		section.add(table);
	    }
	    progressMonitor.done();
	} else {
	    progressMonitor.done();
	}
    }

    /**
     * @param section The section the table will be added to.
     * @param file The file the steps come from.
     * @param doc The whole document, needed for new page adding.
     * @throws DocumentException
     * @throws MalformedURLException
     * @throws IOException
     */
    private static void createStepTable(Section section, TSMResource file,
	    Document doc, IProgressMonitor monitor) throws DocumentException,
	    MalformedURLException, IOException {
	IProgressMonitor progressMonitor = FontsToolsConstants.startMonitor(monitor, " -> steps", 1);
	if (!progressMonitor.isCanceled()) {
	    isTestStep = true;
	    if (file instanceof TSMReport) {
		ITestCaseDescriptor protocol = ((TSMReport) file).getData();
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);

		PdfPCell c1 = new PdfPCell(new Phrase("#", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		float[] columnWidths = new float[] { 2f, 15f, 15f, 15f, 5f };
		table.setWidths(columnWidths);
		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_77, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_78, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_79, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_80, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		table.setHeaderRows(1);

		List<TestStepDescriptor> steps = protocol.getSteps();
		int counter = 1;
		for (TestStepDescriptor currentTestStepDescriptor : steps) {
		    table.addCell(Integer.toString(counter));
		    Phrase paragraph5 = new Phrase();
		    parse(paragraph5, currentTestStepDescriptor.getRichTextDescription(), doc, file);
		    table.addCell(paragraph5);
		    Phrase paragraph6 = new Phrase();
		    parse(paragraph6, currentTestStepDescriptor.getExpectedResult(), doc, file);
		    table.addCell(paragraph6);
		    Phrase paragraph7 = new Phrase();
		    parse(paragraph7, currentTestStepDescriptor.getRealResult(), doc, file);
		    table.addCell(paragraph7);
		    if (currentTestStepDescriptor.getStatus() == DataModelTypes.StatusType.passed) {
			Image i = Image.getInstance(ResourceManager
				.getURL(ResourceManager.getPathAccept()));
			i.scaleToFit(15, 15);
			PdfPCell cell = new PdfPCell(i, false);
			table.addCell(cell);
		    } else if (currentTestStepDescriptor.getStatus() == DataModelTypes.StatusType.passedWithAnnotation) {
			Image i = Image.getInstance(ResourceManager
				.getURL(ResourceManager.getPathInformation()));
			i.scaleToFit(15, 15);
			PdfPCell cell = new PdfPCell(i, false);
			table.addCell(cell);
		    } else if (currentTestStepDescriptor.getStatus() == DataModelTypes.StatusType.failed) {
			Image i = Image.getInstance(ResourceManager
				.getURL(ResourceManager.getPathCross()));
			i.scaleToFit(15, 15);
			PdfPCell cell = new PdfPCell(i, false);
			table.addCell(cell);
		    } else if (currentTestStepDescriptor.getStatus() == DataModelTypes.StatusType.notExecuted) {

			table.addCell(""); //$NON-NLS-1$
		    }
		    counter++;
		}

		section.add(table);

		// test case
	    } else if (file instanceof TSMTestCase) {
		ITestCaseDescriptor testCase = ((TSMTestCase) file).getData();
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);

		PdfPCell pdfCell1 = new PdfPCell(new Phrase("#", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		pdfCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pdfCell1);
		float[] columnWidths = new float[] { 2f, 15f, 15f, 15f, 5f };
		table.setWidths(columnWidths);
		pdfCell1 = new PdfPCell(new Phrase(Messages.ExportPdf_83, FontsToolsConstants.boldFont));
		pdfCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pdfCell1);

		pdfCell1 = new PdfPCell(new Phrase(Messages.ExportPdf_84, FontsToolsConstants.boldFont));
		pdfCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pdfCell1);

		pdfCell1 = new PdfPCell(new Phrase(Messages.ExportPdf_85, FontsToolsConstants.boldFont));
		pdfCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pdfCell1);

		pdfCell1 = new PdfPCell(new Phrase(Messages.ExportPdf_86, FontsToolsConstants.boldFont));
		pdfCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pdfCell1);

		table.setHeaderRows(1);

		List<TestStepDescriptor> steps = testCase.getSteps();
		int counter = 1;
		for (TestStepDescriptor currentTestStepDescriptor : steps) {
		    table.addCell(Integer.toString(counter));
		    Phrase paragraph5 = new Phrase();
		    parse(paragraph5, currentTestStepDescriptor.getRichTextDescription(), doc, file);
		    table.addCell(paragraph5);
		    Phrase paragraph6 = new Phrase();
		    parse(paragraph6, currentTestStepDescriptor.getExpectedResult(), doc, file);
		    table.addCell(paragraph6);
		    // for filling out
		    table.addCell(""); //$NON-NLS-1$
		    // no status
		    table.addCell(""); //$NON-NLS-1$

		    counter++;
		}

		section.add(table);
	    }
	    isTestStep = false;
	    progressMonitor.done();
	} else {
	    progressMonitor.done();
	}
    }

    /**
     * @param paragraph
     *            The paragraph where the empty lines are packed into.
     * @param number
     *            The amount of empty lines.
     */
    private static void addEmptyLine(Paragraph paragraph, int number) {
	for (int i = 0; i < number; i++) {
	    paragraph.add(new Paragraph(" ")); //$NON-NLS-1$
	}
    }

    /**
     * Parses the given phrase and replaces special characters and html tags.
     * @param phrase The phrase the text is in.
     * @param text The text to be parsed.
     * @param doc The whole document.
     * @param resource
     * @throws MalformedURLException
     * @throws IOException
     * @throws DocumentException
     */
    private static void parse(Phrase phrase, String text, Document doc,
	    TSMResource resource) throws MalformedURLException, IOException,
	    DocumentException {
	// split at every tag and check what tag it is. Change font accordingly.
	String[] parts = text.split("<"); //$NON-NLS-1$

	for (String currentString : parts) {
	    //Replace the special characters.
	    for (String[] replace : RichText.escapes) {
		currentString = currentString.replace(replace[1], replace[0]);
	    }

	    //Replace html tags.
	    if (currentString.startsWith("html>")) { //$NON-NLS-1$
		Paragraph p = new Paragraph("", FontsToolsConstants.normalFont); //$NON-NLS-1$
		phrase.add(p);
	    } else if (currentString.startsWith("p>")) { //$NON-NLS-1$
		Paragraph p = new Paragraph(currentString.substring(2, currentString.length()),
			FontsToolsConstants.normalFont);
		phrase.add(p);
	    } else if (currentString.startsWith("b>")) { //$NON-NLS-1$
		bold = true;
		Paragraph p = new Paragraph(currentString.substring(2, currentString.length()),
			FontsToolsConstants.getFont(bold, italic, underline, strike));
		phrase.add(p);
	    } else if (currentString.startsWith("i>")) { //$NON-NLS-1$
		italic = true;
		Paragraph p = new Paragraph(currentString.substring(2, currentString.length()),
			FontsToolsConstants.getFont(bold, italic, underline, strike));
		phrase.add(p);
	    } else if (currentString.startsWith("ins>")) { //$NON-NLS-1$
		underline = true;
		Paragraph p = new Paragraph(currentString.substring(4, currentString.length()),
			FontsToolsConstants.getFont(bold, italic, underline, strike));
		phrase.add(p);
	    } else if (currentString.startsWith("del>")) { //$NON-NLS-1$
		strike = true;
		Paragraph p = new Paragraph(currentString.substring(4, currentString.length()),
			FontsToolsConstants.getFont(bold, italic, underline, strike));
		phrase.add(p);
	    } else if (currentString.startsWith("/html>")) { //$NON-NLS-1$
		Paragraph p = new Paragraph("", FontsToolsConstants.normalFont); //$NON-NLS-1$
		phrase.add(p);
	    } else if (currentString.startsWith("/p>")) { //$NON-NLS-1$
		Paragraph paragraph4 = new Paragraph();
		addEmptyLine(paragraph4, 1);
		Paragraph p = new Paragraph("", FontsToolsConstants.normalFont); //$NON-NLS-1$
		phrase.add(paragraph4);
		phrase.add(p);
	    } else if (currentString.startsWith("/b>")) { //$NON-NLS-1$
		bold = false;
		Paragraph p = new Paragraph(currentString.substring(3, currentString.length()),
			FontsToolsConstants.getFont(bold, italic, underline, strike));
		phrase.add(p);
	    } else if (currentString.startsWith("/i>")) { //$NON-NLS-1$
		italic = false;
		Paragraph p = new Paragraph(currentString.substring(3, currentString.length()),
			FontsToolsConstants.getFont(bold, italic, underline, strike));
		phrase.add(p);
	    } else if (currentString.startsWith("/ins>")) { //$NON-NLS-1$
		underline = false;
		Paragraph p = new Paragraph(currentString.substring(5, currentString.length()),
			FontsToolsConstants.getFont(bold, italic, underline, strike));
		phrase.add(p);
	    } else if (currentString.startsWith("/del>")) { //$NON-NLS-1$
		strike = false;
		Paragraph p = new Paragraph(currentString.substring(5, currentString.length()),
			FontsToolsConstants.getFont(bold, italic, underline, strike));
		phrase.add(p);
	    }
	    // image
	    else if (currentString.startsWith("img")) { //$NON-NLS-1$
		String[] split = currentString.split("\""); //$NON-NLS-1$
		// split[1] is project + name

		String path = ResourcesPlugin.getWorkspace().getRoot()
			.getLocation().toString()
			+ "/"
			+ resource.getProject().getName()
			+ "/"
			+ DataModelTypes.imageFolderName + "/" + split[1];

		// String path = ResourcesPlugin.getWorkspace().getRoot()
		// .getLocation().toString()
		// + "/" + split[1];
		File file = new File(path);
		URL pathToImage = file.toURI().toURL();

		Image image = Image.getInstance(pathToImage);
		String name = file.getName();
		imageList.add(image);
		nameList.add(name);

		// document sizes
		float docHeight = doc.getPageSize().getHeight()
			- doc.bottomMargin() - doc.topMargin() - 15;
		float docWidth = doc.getPageSize().getWidth()
			- doc.rightMargin() - doc.leftMargin();
		float cellWidth = 150;

		// scale to cell/page width
		if (split[split.length - 2].equals("100%")) { //$NON-NLS-1$
		    if (isTestStep) {
			// image has to be scaled
			if (image.getWidth() > cellWidth) {
			    // scales to cell width
			    float scaleFactor = image.getHeight() / cellWidth;
			    image.scaleToFit(cellWidth, image.getHeight() * scaleFactor);
			}
		    } else {
			// scale to text width
			if (image.getWidth() > docWidth) {
			    float scaleFactor = image.getHeight() / docWidth;
			    image.scaleToFit(docWidth, image.getWidth() * scaleFactor);
			}
		    }

		} else {
		    // in table
		    if (isTestStep) {
			// check if image too big for cell
			// scale factor
			Float width = Float.valueOf(split[split.length - 2])
				* image.getWidth();
			Float height = Float.valueOf(split[split.length - 2])
				* image.getHeight();
			if (width > cellWidth) {
			    float scaleFactor = image.getWidth() / cellWidth;
			    image.scaleToFit(cellWidth, image.getHeight() * scaleFactor);
			} else {
			    // scale with factor
			    image.scaleToFit(width.intValue(), height.intValue());
			}
			// out of table
		    } else {
			// scale with factor
			Float width = Float.valueOf(split[split.length - 2])
				* image.getWidth();
			Float height = Float.valueOf(split[split.length - 2])
				* image.getHeight();

			// check if image bigger then page
			if (width > docWidth || height > docHeight) {
			    image.scaleToFit(docWidth, docHeight);
			} else {
			    image.scaleToFit(width.intValue(), height.intValue());
			}
		    }
		}

		Chunk c = new Chunk(image, 0, 0);

		Anchor anchor = new Anchor(c);
		anchor.setReference("#" + Integer.toString(counterForImages)); //$NON-NLS-1$
		anchor.setName("back" + Integer.toString(counterForImages)); //$NON-NLS-1$
		Paragraph p = new Paragraph();
		p.add(anchor);

		Paragraph paragraph3 = new Paragraph();
		addEmptyLine(paragraph3, 1);
		Paragraph paragraph4 = new Paragraph();
		addEmptyLine(paragraph4, 1);

		// image alone in an new line
		phrase.add(paragraph3);
		phrase.add(paragraph3);
		phrase.add(p);
		phrase.add(paragraph4);
		phrase.add(new Paragraph(name, FontsToolsConstants.SMALL_FONT));
		phrase.add(paragraph4);
		counterForImages++;
	    } else if (currentString.startsWith("/img>")) { //$NON-NLS-1$
		Paragraph p = new Paragraph(currentString.substring(5, currentString.length()));
		phrase.add(p);
	    } else {
		if (!currentString.equals("")) { //$NON-NLS-1$
		    Paragraph p = new Paragraph("<" + currentString); //$NON-NLS-1$
		    phrase.add(p);
		}
	    }
	}
    }

    private static void createEntries(Document document, Paragraph chapter,
	    TSMResource file, FooterOneFile event, IProgressMonitor monitor)
	    throws MalformedURLException, DocumentException, IOException {
	IProgressMonitor progressMonitor = FontsToolsConstants.startMonitor(monitor, " -> entries", 1);
	if (!progressMonitor.isCanceled()) {
	    // protocol
	    if (file instanceof TSMReport) {
		ITestCaseDescriptor protocol = ((TSMReport) file).getData();
		// add content
		createContentTableOne(chapter, file, FontsToolsConstants.getSubMonitor(monitor, 1));

		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase paragraph5 = new Phrase();
		Paragraph p2 = new Paragraph(Messages.ExportPdf_116, FontsToolsConstants.boldFont);
		Paragraph p = new Paragraph("", FontsToolsConstants.normalFont); //$NON-NLS-1$
		addEmptyLine(p, 1);
		paragraph5.add(p2);
		paragraph5.add(p);
		parse(paragraph5, protocol.getShortDescription(), document,
			file);
		PdfPCell pdfCell = new PdfPCell(paragraph5);
		pdfCell.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell);
		chapter.add(table);

		Paragraph paragraph8 = new Paragraph();
		addEmptyLine(paragraph8, 1);
		chapter.add(paragraph8);

		PdfPTable table2 = new PdfPTable(1);
		table2.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase paragraph7 = new Phrase();
		Paragraph p3 = new Paragraph(Messages.ExportPdf_118, FontsToolsConstants.boldFont);
		paragraph7.add(p3);
		paragraph7.add(p);
		parse(paragraph7, protocol.getRichTextPrecondition(), document,
			file);
		PdfPCell pdfCell2 = new PdfPCell(paragraph7);
		pdfCell2.setBorder(Rectangle.NO_BORDER);
		table2.addCell(pdfCell2);
		chapter.add(table2);

		Paragraph para = new Paragraph();
		addEmptyLine(para, 2);
		chapter.add(para);

		createStepTableOne(chapter, file, document,
			FontsToolsConstants.getSubMonitor(monitor, 1));
		Paragraph paragraph3 = new Paragraph();
		addEmptyLine(paragraph3, 1);
		chapter.add(paragraph3);

		PdfPTable table3 = new PdfPTable(1);
		table3.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase paragraph10 = new Phrase();
		Paragraph p4 = new Paragraph(Messages.ExportPdf_119, FontsToolsConstants.boldFont);
		paragraph10.add(p4);
		paragraph10.add(p);
		parse(paragraph10, protocol.getRichTextResult(), document, file);
		PdfPCell pdfCell3 = new PdfPCell(paragraph10);
		pdfCell3.setBorder(Rectangle.NO_BORDER);
		table3.addCell(pdfCell3);
		chapter.add(table3);

	    }
	    // test case
	    else if (file instanceof TSMTestCase) {
		ITestCaseDescriptor testCase = ((TSMTestCase) file).getData();
		// add content
		createContentTableOne(chapter, file, FontsToolsConstants.getSubMonitor(monitor, 1));

		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase paragraph5 = new Phrase();
		Paragraph paragraph2 = new Paragraph(Messages.ExportPdf_120, FontsToolsConstants.boldFont);
		paragraph5.add(paragraph2);
		Paragraph paragraph = new Paragraph();
		addEmptyLine(paragraph, 1);
		paragraph5.add(paragraph);
		parse(paragraph5, testCase.getShortDescription(), document,
			file);
		PdfPCell pdfCell = new PdfPCell(paragraph5);
		pdfCell.setBorder(Rectangle.NO_BORDER);
		table.addCell(pdfCell);
		chapter.add(table);

		Paragraph paragraph8 = new Paragraph();
		addEmptyLine(paragraph8, 1);
		chapter.add(paragraph8);

		PdfPTable table2 = new PdfPTable(1);
		table2.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase paragraph7 = new Phrase();
		Paragraph p3 = new Paragraph(Messages.ExportPdf_121, FontsToolsConstants.boldFont);
		paragraph7.add(p3);
		paragraph7.add(paragraph);
		parse(paragraph7, testCase.getRichTextPrecondition(), document,
			file);
		PdfPCell c2 = new PdfPCell(paragraph7);
		c2.setBorder(Rectangle.NO_BORDER);
		table2.addCell(c2);
		chapter.add(table2);

		Paragraph para = new Paragraph();
		addEmptyLine(para, 2);
		chapter.add(para);

		createStepTableOne(chapter, file, document,
			FontsToolsConstants.getSubMonitor(monitor, 1));
		Paragraph paragraph3 = new Paragraph();
		addEmptyLine(paragraph3, 1);
		chapter.add(paragraph3);

		Paragraph paragraph9 = new Paragraph(Messages.ExportPdf_122,
			FontsToolsConstants.boldFont);
		chapter.add(paragraph9);

		PdfPTable table3 = new PdfPTable(1);
		table3.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		Phrase paragraph10 = new Phrase();

		parse(paragraph10, "", document, file); //$NON-NLS-1$
		PdfPCell c3 = new PdfPCell(paragraph10);
		c3.setBorder(Rectangle.NO_BORDER);
		table3.addCell(c3);
		table3.addCell(c3);
		chapter.add(table3);

	    }
	    progressMonitor.done();
	} else {
	    progressMonitor.done();
	}
    }

    /**
     * @param chapter The section where the table is in.
     * @param file The file the data comes from.
     * @param monitor
     * @throws DocumentException
     * @throws MalformedURLException
     * @throws IOException
     */
    private static void createContentTableOne(Paragraph chapter,
	    TSMResource file, IProgressMonitor monitor)
	    throws DocumentException, MalformedURLException, IOException {
	IProgressMonitor progressMonitor = FontsToolsConstants.startMonitor(monitor, " -> table", 1);
	if (!progressMonitor.isCanceled()) {
	    //dateFormat form the data model.
	    final SimpleDateFormat dateFormat = DataModelTypes.getDateFormat();

	    if (file instanceof TSMReport) {
		ITestCaseDescriptor protocol = ((TSMReport) file).getData();
		PdfPTable table = new PdfPTable(2);

		table.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		table.setWidths(new float[] { 30, 70 });

		PdfPCell c17 = new PdfPCell(new Phrase("ID", FontsToolsConstants.SMALL_FONT)); //$NON-NLS-1$
		c17.setBorder(Rectangle.NO_BORDER);
		table.addCell(c17);
		PdfPCell c18 = new PdfPCell(new Phrase(String.valueOf(protocol
			.getId()), FontsToolsConstants.SMALL_FONT));
		c18.setBorder(Rectangle.NO_BORDER);
		table.addCell(c18);

		PdfPCell c3 = new PdfPCell(new Phrase(Messages.ExportPdf_124,
			FontsToolsConstants.boldFont));
		c3.setBorder(Rectangle.NO_BORDER);
		table.addCell(c3);
		PdfPCell c4;
		if (file.getProject().equals(file.getParent())) {
		    c4 = new PdfPCell(new Phrase("-")); //$NON-NLS-1$
		} else {
		    c4 = new PdfPCell(new Phrase(file.getParent().getName()));
		}
		c4.setBorder(Rectangle.NO_BORDER);
		table.addCell(c4);

		PdfPCell c5 = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		c5.setBorder(Rectangle.NO_BORDER);
		table.addCell(c5);
		PdfPCell c6 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		c6.setBorder(Rectangle.NO_BORDER);
		table.addCell(c6);
		PdfPCell c7 = new PdfPCell(new Phrase(Messages.ExportPdf_128,
			FontsToolsConstants.boldFont));
		c7.setBorder(Rectangle.NO_BORDER);
		table.addCell(c7);
		PdfPCell c8 = new PdfPCell(new Phrase(protocol.getPriority()
			.toString()));
		c8.setBorder(Rectangle.NO_BORDER);
		table.addCell(c8);

		PdfPCell c87 = new PdfPCell(new Phrase(Messages.ExportPdf_3,
			FontsToolsConstants.boldFont));
		c87.setBorder(Rectangle.NO_BORDER);
		table.addCell(c87);
		PdfPCell c88 = new PdfPCell(new Phrase(
			protocol.getRevisionNumber() + ""));
		c88.setBorder(Rectangle.NO_BORDER);
		table.addCell(c88);

		PdfPCell c9 = new PdfPCell(new Phrase(Messages.ExportPdf_129,
			FontsToolsConstants.boldFont));
		c9.setBorder(Rectangle.NO_BORDER);
		table.addCell(c9);
		PdfPCell c10 = new PdfPCell(new Phrase(
			protocol.getExpectedDuration()));
		c10.setBorder(Rectangle.NO_BORDER);
		table.addCell(c10);

		PdfPCell c11 = new PdfPCell(new Phrase(Messages.ExportPdf_130,
			FontsToolsConstants.boldFont));
		c11.setBorder(Rectangle.NO_BORDER);
		table.addCell(c11);

		PdfPCell c12 = new PdfPCell(new Phrase(
			protocol.getRealDuration()));
		c12.setBorder(Rectangle.NO_BORDER);
		table.addCell(c12);

		PdfPCell c13 = new PdfPCell(new Phrase(Messages.ExportPdf_131,
			FontsToolsConstants.boldFont));
		c13.setBorder(Rectangle.NO_BORDER);
		table.addCell(c13);
		PdfPCell c14 = new PdfPCell(new Phrase(protocol.getAuthor()));
		c14.setBorder(Rectangle.NO_BORDER);
		table.addCell(c14);

		PdfPCell tester = new PdfPCell(new Phrase("Tester: ", FontsToolsConstants.boldFont));
		tester.setBorder(Rectangle.NO_BORDER);
		table.addCell(tester);
		PdfPCell testerEntry = new PdfPCell(new Phrase(
			protocol.getAssignedTo()));
		testerEntry.setBorder(Rectangle.NO_BORDER);
		table.addCell(testerEntry);

		PdfPCell c15 = new PdfPCell(new Phrase(Messages.ExportPdf_132,
			FontsToolsConstants.boldFont));
		c15.setBorder(Rectangle.NO_BORDER);
		table.addCell(c15);
		PdfPCell c16 = new PdfPCell(new Phrase(
			Integer.toString(protocol.getNumberOfExecutions())));
		c16.setBorder(Rectangle.NO_BORDER);
		table.addCell(c16);

		PdfPCell c27 = new PdfPCell(new Phrase(Messages.ExportPdf_133,
			FontsToolsConstants.boldFont));
		c27.setBorder(Rectangle.NO_BORDER);
		table.addCell(c27);
		PdfPCell c28 = new PdfPCell(new Phrase(
			Integer.toString(protocol.getNumberOfFailures())));
		c28.setBorder(Rectangle.NO_BORDER);
		table.addCell(c28);

		// PdfPCell c19 = new PdfPCell(new
		// Phrase(Messages.ExportPdf_134,
		// boldFont));
		// c19.setBorder(Rectangle.NO_BORDER);
		// table.addCell(c19);

		// PdfPCell c20 = new PdfPCell(new Phrase(
		// DataModelTypes.dateFormat.format(protocol
		// .getLastExecution())));
		// c20.setBorder(Rectangle.NO_BORDER);
		// table.addCell(c20);

		PdfPCell c21 = new PdfPCell(new Phrase(Messages.ExportPdf_135,
			FontsToolsConstants.boldFont));
		c21.setBorder(Rectangle.NO_BORDER);
		table.addCell(c21);
		PdfPCell c22 = new PdfPCell(new Phrase(
			dateFormat.format(protocol
				.getLastChangedOn()), FontsToolsConstants.normalFont));
		c22.setBorder(Rectangle.NO_BORDER);
		table.addCell(c22);

		PdfPCell c25 = new PdfPCell(new Phrase(Messages.ExportPdf_136,
			FontsToolsConstants.boldFont));
		c25.setBorder(Rectangle.NO_BORDER);
		table.addCell(c25);
		if (protocol.getStatus() == DataModelTypes.StatusType.passed) {
		    Image imageAccept = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathAccept()));
		    imageAccept.scaleToFit(10, 10);
		    Paragraph paragraph = new Paragraph();
		    paragraph.add(new Phrase(Messages.ExportPdf_137));
		    paragraph.add(new Chunk(imageAccept, -1f, 1f));
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(cell);
		} else if (protocol.getStatus() == DataModelTypes.StatusType.passedWithAnnotation) {
		    Image imageInformation = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathInformation()));
		    imageInformation.scaleToFit(10, 10);
		    Paragraph paragraph = new Paragraph();
		    paragraph.add(new Phrase(Messages.ExportPdf_138));
		    paragraph.add(new Chunk(imageInformation, -1f, 1f));
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(cell);
		} else if (protocol.getStatus() == DataModelTypes.StatusType.failed) {
		    Image imageCross = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathCross()));
		    imageCross.scaleToFit(10, 10);
		    Paragraph paragraph = new Paragraph();
		    paragraph.add(new Phrase(Messages.ExportPdf_139 + "   ")); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
		    paragraph.add(new Chunk(imageCross, -1f, 1f));
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(cell);
		} else {
		    Paragraph paragraph = new Paragraph(""); //$NON-NLS-1$
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    table.addCell(cell);

		}

		// line
		PdfPCell c37 = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		c37.setBorder(Rectangle.BOTTOM);
		table.addCell(c37);
		PdfPCell c38 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		c38.setBorder(Rectangle.BOTTOM);
		table.addCell(c38);

		PdfPCell c29 = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		c29.setBorder(Rectangle.TOP);
		table.addCell(c29);
		PdfPCell c30 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		c30.setBorder(Rectangle.TOP);
		table.addCell(c30);

		chapter.add(table);

		// test case
	    } else if (file instanceof TSMTestCase) {
		ITestCaseDescriptor testCase = ((TSMTestCase) file).getData();
		PdfPTable table = new PdfPTable(2);

		table.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);
		table.setWidths(new float[] { 30, 70 });

		// PdfPCell c1 = new PdfPCell(new Phrase("Name:", boldFont));
		// c1.setBorder(Rectangle.NO_BORDER);
		// table.addCell(c1);
		// PdfPCell c2 = new PdfPCell(new Phrase(file.getName()));
		// c2.setBorder(Rectangle.NO_BORDER);
		// table.addCell(c2);

		PdfPCell c37 = new PdfPCell(new Phrase(Messages.ExportPdf_1,
			FontsToolsConstants.SMALL_FONT));
		c37.setBorder(Rectangle.NO_BORDER);
		table.addCell(c37);
		PdfPCell c38 = new PdfPCell(new Phrase(String.valueOf(testCase
			.getId()), FontsToolsConstants.SMALL_FONT));
		c38.setBorder(Rectangle.NO_BORDER);
		table.addCell(c38);

		PdfPCell c3 = new PdfPCell(new Phrase(Messages.ExportPdf_146,
			FontsToolsConstants.boldFont));
		c3.setBorder(Rectangle.NO_BORDER);
		table.addCell(c3);
		PdfPCell c4;
		if (file.getProject().equals(file.getParent())) {
		    c4 = new PdfPCell(new Phrase("-")); //$NON-NLS-1$
		} else {
		    c4 = new PdfPCell(new Phrase(file.getParent().getName()));
		}
		c4.setBorder(Rectangle.NO_BORDER);
		table.addCell(c4);

		PdfPCell c5 = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		c5.setBorder(Rectangle.NO_BORDER);
		table.addCell(c5);
		PdfPCell c6 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		c6.setBorder(Rectangle.NO_BORDER);
		table.addCell(c6);

		PdfPCell c7 = new PdfPCell(new Phrase(Messages.ExportPdf_150,
			FontsToolsConstants.boldFont));
		c7.setBorder(Rectangle.NO_BORDER);
		table.addCell(c7);
		PdfPCell c8 = new PdfPCell(new Phrase(testCase.getPriority()
			.toString()));
		c8.setBorder(Rectangle.NO_BORDER);
		table.addCell(c8);

		PdfPCell c9 = new PdfPCell(new Phrase(Messages.ExportPdf_151,
			FontsToolsConstants.boldFont));
		c9.setBorder(Rectangle.NO_BORDER);
		table.addCell(c9);
		PdfPCell c10 = new PdfPCell(new Phrase(
			testCase.getExpectedDuration()));
		c10.setBorder(Rectangle.NO_BORDER);
		table.addCell(c10);

		PdfPCell c11 = new PdfPCell(new Phrase(Messages.ExportPdf_152,
			FontsToolsConstants.boldFont));
		c11.setBorder(Rectangle.NO_BORDER);
		table.addCell(c11);
		// System.out.println(testCase.getRealDuration());
		PdfPCell c12 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		c12.setBorder(Rectangle.NO_BORDER);
		table.addCell(c12);

		PdfPCell c13 = new PdfPCell(new Phrase(Messages.ExportPdf_154,
			FontsToolsConstants.boldFont));
		c13.setBorder(Rectangle.NO_BORDER);
		table.addCell(c13);
		PdfPCell c14 = new PdfPCell(new Phrase(testCase.getAuthor()));
		c14.setBorder(Rectangle.NO_BORDER);
		table.addCell(c14);

		PdfPCell c15 = new PdfPCell(new Phrase(Messages.ExportPdf_155,
			FontsToolsConstants.boldFont));
		c15.setBorder(Rectangle.NO_BORDER);
		table.addCell(c15);
		PdfPCell c16 = new PdfPCell(new Phrase(
			Integer.toString(testCase.getNumberOfExecutions())));
		c16.setBorder(Rectangle.NO_BORDER);
		table.addCell(c16);

		PdfPCell c17 = new PdfPCell(new Phrase(Messages.ExportPdf_156,
			FontsToolsConstants.boldFont));
		c17.setBorder(Rectangle.NO_BORDER);
		table.addCell(c17);
		PdfPCell c18 = new PdfPCell(new Phrase(
			Integer.toString(testCase.getNumberOfFailures())));
		c18.setBorder(Rectangle.NO_BORDER);
		table.addCell(c18);

		PdfPCell c19 = new PdfPCell(new Phrase(Messages.ExportPdf_157,
			FontsToolsConstants.boldFont));
		c19.setBorder(Rectangle.NO_BORDER);
		table.addCell(c19);

		PdfPCell c20;
		if (testCase.getLastExecution() == null) {
		    c20 = new PdfPCell(new Phrase(Messages.ExportPdf_158));
		} else {

		    c20 = new PdfPCell(new Phrase(
			    dateFormat.format(testCase
				    .getLastExecution())));
		}
		c20.setBorder(Rectangle.NO_BORDER);
		table.addCell(c20);

		PdfPCell c21 = new PdfPCell(new Phrase(Messages.ExportPdf_159,
			FontsToolsConstants.boldFont));
		c21.setBorder(Rectangle.NO_BORDER);
		table.addCell(c21);
		PdfPCell c22 = new PdfPCell(new Phrase(
			dateFormat.format(testCase
				.getLastChangedOn()), FontsToolsConstants.normalFont));
		c22.setBorder(Rectangle.NO_BORDER);
		table.addCell(c22);

		PdfPCell c25 = new PdfPCell(new Phrase(Messages.ExportPdf_160,
			FontsToolsConstants.boldFont));
		c25.setBorder(Rectangle.NO_BORDER);
		table.addCell(c25);
		if (testCase.getStatus() == DataModelTypes.StatusType.passed) {
		    Image i = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathAccept()));
		    i.scaleToFit(10, 10);
		    Paragraph paragraph = new Paragraph();
		    paragraph.add(new Phrase(Messages.ExportPdf_161));
		    paragraph.add(new Chunk(i, -1f, 1f));
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(cell);
		} else if (testCase.getStatus() == DataModelTypes.StatusType.passedWithAnnotation) {
		    Image i = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathInformation()));
		    i.scaleToFit(10, 10);
		    Paragraph paragraph = new Paragraph();
		    paragraph.add(new Phrase(Messages.ExportPdf_162));
		    paragraph.add(new Chunk(i, -1f, 1f));
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(cell);
		} else if (testCase.getStatus() == DataModelTypes.StatusType.failed) {
		    Image i = Image.getInstance(ResourceManager
			    .getURL(ResourceManager.getPathCross()));
		    i.scaleToFit(10, 10);
		    Paragraph paragraph = new Paragraph();
		    paragraph.add(new Phrase(Messages.ExportPdf_163 + "   ")); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
		    paragraph.add(new Chunk(i, -1f, 1f));
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Rectangle.ALIGN_BOTTOM);
		    table.addCell(cell);
		} else {
		    Paragraph paragraph = new Paragraph(""); //$NON-NLS-1$
		    PdfPCell cell = new PdfPCell(paragraph);
		    cell.setBorder(Rectangle.NO_BORDER);
		    table.addCell(cell);

		}

		// line
		PdfPCell c27 = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		c27.setBorder(Rectangle.BOTTOM);
		table.addCell(c27);
		PdfPCell c28 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		c28.setBorder(Rectangle.BOTTOM);
		table.addCell(c28);

		PdfPCell c29 = new PdfPCell(new Phrase("", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		c29.setBorder(Rectangle.TOP);
		table.addCell(c29);
		PdfPCell c30 = new PdfPCell(new Phrase("")); //$NON-NLS-1$
		c30.setBorder(Rectangle.TOP);
		table.addCell(c30);

		chapter.add(table);
	    }
	    progressMonitor.done();
	} else {
	    progressMonitor.done();
	}
    }

    /**
     * @param chapter The section the table will be added to.
     * @param file The file the steps come from.
     * @param doc The whole document, needed for new page adding.
     * @param monitor
     * @throws DocumentException
     * @throws MalformedURLException
     * @throws IOException
     */
    private static void createStepTableOne(Paragraph chapter, TSMResource file,
	    Document doc, IProgressMonitor monitor) throws DocumentException,
	    MalformedURLException, IOException {
	IProgressMonitor progressMonitor = FontsToolsConstants.startMonitor(monitor, " -> steps", 1);
	if (!progressMonitor.isCanceled()) {
	    isTestStep = true;
	    if (file instanceof TSMReport) {
		ITestCaseDescriptor protocol = ((TSMReport) file).getData();
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);

		PdfPCell c1 = new PdfPCell(new Phrase("#", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		float[] columnWidths = new float[] { 2f, 15f, 15f, 15f, 5f };
		table.setWidths(columnWidths);
		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_171, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_172, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_173, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_174, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		table.setHeaderRows(1);

		List<TestStepDescriptor> steps = protocol.getSteps();
		int counter = 1;
		for (TestStepDescriptor s : steps) {
		    table.addCell(Integer.toString(counter));
		    Phrase paragraph5 = new Phrase();
		    parse(paragraph5, s.getRichTextDescription(), doc, file);
		    table.addCell(paragraph5);
		    Phrase paragraph6 = new Phrase();
		    parse(paragraph6, s.getExpectedResult(), doc, file);
		    table.addCell(paragraph6);
		    Phrase paragraph7 = new Phrase();
		    parse(paragraph7, s.getRealResult(), doc, file);
		    table.addCell(paragraph7);
		    if (s.getStatus() == DataModelTypes.StatusType.passed) {
			Image i = Image.getInstance(ResourceManager
				.getURL(ResourceManager.getPathAccept()));
			i.scaleToFit(15, 15);
			PdfPCell cell = new PdfPCell(i, false);
			table.addCell(cell);
		    } else if (s.getStatus() == DataModelTypes.StatusType.passedWithAnnotation) {
			Image i = Image.getInstance(ResourceManager
				.getURL(ResourceManager.getPathInformation()));
			i.scaleToFit(15, 15);
			PdfPCell cell = new PdfPCell(i, false);
			table.addCell(cell);
		    } else if (s.getStatus() == DataModelTypes.StatusType.failed) {
			Image i = Image.getInstance(ResourceManager
				.getURL(ResourceManager.getPathCross()));
			i.scaleToFit(15, 15);
			PdfPCell cell = new PdfPCell(i, false);
			table.addCell(cell);
		    } else if (s.getStatus() == DataModelTypes.StatusType.notExecuted) {

			table.addCell(""); //$NON-NLS-1$
		    }
		    counter++;
		}

		chapter.add(table);

		// test case
	    } else if (file instanceof TSMTestCase) {
		ITestCaseDescriptor testCase = ((TSMTestCase) file).getData();
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(FontsToolsConstants.WIDTH_PERCENTAGE);

		PdfPCell c1 = new PdfPCell(new Phrase("#", FontsToolsConstants.boldFont)); //$NON-NLS-1$
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		float[] columnWidths = new float[] { 2f, 15f, 15f, 15f, 5f };
		table.setWidths(columnWidths);
		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_177, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_178, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_179, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(Messages.ExportPdf_180, FontsToolsConstants.boldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		table.setHeaderRows(1);

		List<TestStepDescriptor> steps = testCase.getSteps();
		int counter = 1;
		for (TestStepDescriptor s : steps) {
		    table.addCell(Integer.toString(counter));
		    Phrase paragraph5 = new Phrase();
		    parse(paragraph5, s.getRichTextDescription(), doc, file);
		    table.addCell(paragraph5);
		    Phrase paragraph6 = new Phrase();
		    parse(paragraph6, s.getExpectedResult(), doc, file);
		    table.addCell(paragraph6);
		    // for filling out
		    table.addCell(""); //$NON-NLS-1$
		    // no status
		    table.addCell(""); //$NON-NLS-1$

		    counter++;
		}

		chapter.add(table);
	    }
	    isTestStep = false;
	    progressMonitor.done();
	} else {
	    progressMonitor.done();
	}
    }
}