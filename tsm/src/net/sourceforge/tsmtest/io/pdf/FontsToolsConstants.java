package net.sourceforge.tsmtest.io.pdf;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.descriptors.ITestCaseDescriptor;

import com.itextpdf.text.Font;

/**
 * All available fonts for the export, some constants and some tool methods.
 * @author Tobias Hirning
 *
 */
public final class FontsToolsConstants {
    //Types of possible exports.
    public static enum ExportType { ONE_FILE, MULTIPLE_FILES, SPECIFIC_REVISIONS }
    // font sizes
    private static final int STANDARD_FONT_SIZE = 12;
    private static final int HEADER_FONT_SIZE = 18;
    private static final int SMALL_FONT_SIZE = 8;
    // five basic fonts
    static Font normalFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE, Font.NORMAL);
    static Font boldFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE, Font.BOLD);
    static Font italicFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE, Font.ITALIC);
    static Font underlineFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE,
	    Font.UNDERLINE);
    static Font strikeFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE,
	    Font.STRIKETHRU);

    // 2 mixed fonts
    static final Font boldItalicFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE,
	    Font.BOLDITALIC);
    static final Font boldUnderlineFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE,
	    Font.BOLD | Font.UNDERLINE);
    static final Font boldStrikeFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE, Font.BOLD
	    | Font.STRIKETHRU);
    static final Font italicUnderlineFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE,
	    Font.ITALIC | Font.UNDERLINE);
    static final Font italicStrikeFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE,
	    Font.ITALIC | Font.STRIKETHRU);
    static final Font underlineStrikeFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE,
	    Font.UNDERLINE | Font.STRIKETHRU);

    // 3 mixed fonts
    static final Font boldItalicUnderlineFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE,
	    Font.BOLD | Font.ITALIC | Font.UNDERLINE);
    static final Font boldItalicStrikeFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE,
	    Font.BOLD | Font.ITALIC | Font.STRIKETHRU);
    static final Font boldUnderlineStrikeFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE,
	    Font.BOLD | Font.UNDERLINE | Font.STRIKETHRU);
    static final Font italicUnderlineStrikeFont = new Font(Font.HELVETICA,
	    STANDARD_FONT_SIZE, Font.ITALIC | Font.UNDERLINE | Font.STRIKETHRU);

    // all fonts
    static final Font allFont = new Font(Font.HELVETICA, STANDARD_FONT_SIZE, Font.BOLD
	    | Font.ITALIC | Font.UNDERLINE | Font.STRIKETHRU);

    // header font
    static final Font bigBold = new Font(Font.HELVETICA, HEADER_FONT_SIZE, Font.BOLD);

    // captureFont
    static final Font smallFont = new Font(Font.HELVETICA, SMALL_FONT_SIZE);

    // margins
    static final float LEFT_MARGIN = 25;
    static final float RIGHT_MARGIN = 25;
    static final float TOP_MARGIN = 25;
    static final float BOTTOM_MARGIN = 60;

    //WIDTH_PERCENTAGE
    static final float WIDTH_PERCENTAGE = 100;

    /**
     * Determines the font depending on the active styles.
     * @param bold
     * @param italic
     * @param underline
     * @param strike
     * @return The font depending on what styles are true.
     */
    static Font getFont(boolean bold, boolean italic, boolean underline, boolean strike) {
	// one font
	if (!bold && !italic && !underline && !strike) {
	    return normalFont;
	} else if (bold && !italic && !underline && !strike) {
	    return boldFont;
	} else if (!bold && italic && !underline && !strike) {
	    return italicFont;
	} else if (!bold && !italic && underline && !strike) {
	    return underlineFont;
	} else if (!bold && !italic && !underline && strike) {
	    return strikeFont;
	}
	// two fonts
	else if (bold && italic && !underline && !strike) {
	    return boldItalicFont;
	} else if (bold && !italic && underline && !strike) {
	    return boldUnderlineFont;
	} else if (bold && !italic && !underline && strike) {
	    return boldStrikeFont;
	} else if (!bold && italic && underline && !strike) {
	    return italicUnderlineFont;
	} else if (!bold && italic && !underline && strike) {
	    return italicStrikeFont;
	} else if (!bold && !italic && underline && strike) {
	    return underlineStrikeFont;
	}
	// three fonts
	else if (bold && italic && underline && !strike) {
	    return boldItalicUnderlineFont;
	} else if (bold && italic && !underline && strike) {
	    return boldItalicStrikeFont;
	} else if (bold && !italic && underline && strike) {
	    return boldUnderlineStrikeFont;
	} else if (!bold && italic && underline && strike) {
	    return italicUnderlineStrikeFont;
	}
	// all fonts
	else if (bold && italic && underline && strike) {
	    return allFont;
	}
	return null;
    }
    
    /**
     * Gets the id for a TSMResource.
     * @param resource The resource you want the id from.
     * @return The id of the resource.
     */
    static long getId(TSMResource resource) {
	if (resource instanceof TSMReport) {
	    ITestCaseDescriptor protocol = ((TSMReport) resource).getData();
	    return protocol.getId();
	} else {
	    ITestCaseDescriptor testCase = ((TSMTestCase) resource).getData();
	    return testCase.getId();
	}
    }
    

    /**
     * @param monitor The monitor to be started.
     * @param pMainTaskName
     * @param taskAmount
     * @return a started monitor
     */
    public static IProgressMonitor startMonitor(IProgressMonitor monitor,
	    String pMainTaskName, int taskAmount) {
	IProgressMonitor newMonitor = monitor;
	if (newMonitor == null) {
	    newMonitor = new NullProgressMonitor();
	}
	newMonitor.beginTask(pMainTaskName == null ? "" : pMainTaskName,
		taskAmount);
	newMonitor.subTask(" ");
	return newMonitor;
    }

    /**
     * @param monitor
     * @param taskAmount
     * @return A sub monitor
     */
    public static IProgressMonitor getSubMonitor(IProgressMonitor monitor,
	    int taskAmount) {
	if (monitor == null) {
	    return new NullProgressMonitor();
	}
	if (monitor instanceof NullProgressMonitor) {
	    return monitor;
	}
	return new SubProgressMonitor(monitor, taskAmount,
		SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
    }
}
