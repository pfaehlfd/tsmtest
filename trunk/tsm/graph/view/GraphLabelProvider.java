package net.sourceforge.tsmtest.gui.graph.view;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import net.sourceforge.tsmtest.datamodel.TSMPackage;
import net.sourceforge.tsmtest.datamodel.TSMProject;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.gui.ResourceManager;

public class GraphLabelProvider extends LabelProvider {
    public Image getImage(Object element) {
	if (element instanceof TSMTestCase) {
	    return ResourceManager.getImgTSMTestCase();
	}

	if (element instanceof TSMReport) {
	    return ResourceManager.getImgTSMReport();
	}

	if (element instanceof TSMProject) {
	    return ResourceManager.getImgTSMProject();
	}

	if (element instanceof TSMPackage) {
	    return ResourceManager.getImgTSMPackage();
	}

	return null;
    }

    public String getText(Object element) {
	if (element instanceof TSMResource) {
	    return ((TSMResource) element).getName();
	}

	return "";
    }

}
