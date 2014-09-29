package net.sourceforge.tsmtest.gui.graph.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import net.sourceforge.tsmtest.datamodel.DataModel;
import net.sourceforge.tsmtest.datamodel.DataModel.DataModelObservable;
import net.sourceforge.tsmtest.datamodel.SelectionManager;
import net.sourceforge.tsmtest.datamodel.TSMContainer;
import net.sourceforge.tsmtest.datamodel.TSMReport;
import net.sourceforge.tsmtest.datamodel.TSMTestCase;
import net.sourceforge.tsmtest.datamodel.SelectionManager.SelectionObservable;
import net.sourceforge.tsmtest.datamodel.providers.TSMHierarchyContentProvider;
import net.sourceforge.tsmtest.datamodel.providers.TSMRootElement;
import net.sourceforge.tsmtest.datamodel.TSMResource;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.*;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.algorithms.*;

public class GraphView extends ViewPart implements SelectionObservable,
	DataModelObservable {
    public static final String ID = "net.sourceforge.tsmtest.gui.graph.view.graphview";
    private GraphViewer viewer;

    @Override
    public void createPartControl(Composite parent) {
	viewer = new GraphViewer(parent, SWT.NONE);
	viewer.setContentProvider(TSMHierarchyContentProvider.DEFAULT);
	viewer.setLabelProvider(new GraphLabelProvider());
	TreeLayoutAlgorithm algo = new TreeLayoutAlgorithm();
	algo.setResizing(false);
	algo.setNodeSpace(new Dimension(120, 100));
	viewer.setLayoutAlgorithm(algo);
	viewer.setInput(TSMRootElement.getInstance());
	// Selection listener on graphConnect or GraphNode is not supported
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
	viewer.getGraphControl().addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		Graph g = (Graph) e.getSource();
		ArrayList<IResource> selections = new ArrayList<IResource>();
		for (Object obj : g.getSelection()) {
		    try {
			GraphNode node = (GraphNode) obj;
			TSMResource res = (TSMResource) node.getData();
			selections.add(getIResource(res));
		    } catch (ClassCastException ce) {
			// Edge selected
		    }

		}
		IViewReference[] views = PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getActivePage()
			.getViewReferences();
		for (IViewReference view : views) {
		    if ("org.eclipse.jdt.ui.PackageExplorer".equals(view
			    .getId())) {
			IViewPart pExplorer = view.getView(true);
			StructuredSelection ss = new StructuredSelection(
				selections);
			pExplorer.getViewSite().getSelectionProvider()
				.setSelection(ss);
		    } else if ("net.sourceforge.tsmtest.gui.navigator".equals(view.getId())){
			IViewPart pExplorer = view.getView(true);
			StructuredSelection ss = new StructuredSelection(
				selections);
			pExplorer.getViewSite().getSelectionProvider()
				.setSelection(ss);
		    }
		}
	    }

	});
	SelectionManager.getInstance().register(this);
	DataModel.getInstance().register(this);
    }

    @Override
    public void setFocus() {
    }
    
    public void dispose(){
	SelectionManager.getInstance().unregister(this);
	DataModel.getInstance().unregister(this);
	super.dispose();
    }

    private IResource getIResource(TSMResource res) {
	if (res instanceof TSMTestCase) {
	    return DataModel.getInstance().getResource((TSMTestCase) res);
	}
	if (res instanceof TSMReport) {
	    return DataModel.getInstance().getResource((TSMReport) res);
	}
	if (res instanceof TSMContainer) {
	    return DataModel.getInstance().getResource((TSMContainer) res);
	}

	return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void selectionChanged() {
	List<GraphNode> nodes = viewer.getGraphControl().getNodes();
	List<TSMResource> selected = SelectionManager.getInstance().getSelection()
		.getAllResources();
	GraphNode[] toSelect = new GraphNode[selected.size()];
	int i = 0;

	for (GraphNode gn : nodes) {
	    if (selected.contains(gn.getData())) {
		toSelect[i] = gn;
		++i;
	    }
	}
	viewer.getGraphControl().setSelection(toSelect);
    }

    @Override
    public void dataModelChanged() {
	PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	    @Override
	    public void run() {
		viewer.setInput(TSMRootElement.getInstance());
		viewer.refresh();
	    }
	});
    }

}
