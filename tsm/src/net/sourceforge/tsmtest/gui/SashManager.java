 /*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Albert Flaig - initial version
 *    Wolfgang Kraus - various fixes
 *    Bernhard Wetzel - various fixes
 *******************************************************************************/
package net.sourceforge.tsmtest.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sourceforge.tsmtest.SWTUtils;
import net.sourceforge.tsmtest.gui.sashform.CustomSashForm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Albert Flaig, Wolfgang Kraus
 */
public class SashManager<T> {
    private final List<SashManagerColumn<? extends Control>> columnList = new ArrayList<SashManagerColumn<? extends Control>>();
    private final LinkedList<SashManagerRow> rowList = new LinkedList<SashManagerRow>();
    private CustomSashForm sashHeader;
    private CustomSashForm sashBody;
    private ScrolledComposite scrolledComposite;
    /**
     * Used to control the speed of the mousewheel-listener
     */
    private static int scrollspeed = 12;
    private ModifyListener heightListener;

    private Composite composite;
    private boolean disposed = false;

    /**
     * Getter for scrollspeed.
     * 
     * @return The current scrollspeed.
     */
    public static int getScrollSpeed() {
	return scrollspeed;
    }

    /**
     * Setter for scrollspeed.
     * 
     * @param newSpeed The new scrollspeed.
     */
    public synchronized static void setScrollSpeed(int newSpeed) {
	scrollspeed = newSpeed;
    }

    /**
     * Constructs a new instance of this class given its parent.
     * 
     * @param parent
     *            a composite control which will be the parent of the new
     *            instance (cannot be null)
     */
    public SashManager(Composite parent) {

	composite = new Group(parent, SWT.NO_MERGE_PAINTS
		| SWT.NO_REDRAW_RESIZE);
	composite.setLayout(new GridLayout(1, false));
	composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
		1));

	sashHeader = new CustomSashForm(composite, SWT.HORIZONTAL);
	sashHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 1, 1));

	scrolledComposite = new ScrolledComposite(composite, SWT.V_SCROLL
		| SWT.BORDER);
	scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
		true, 1, 1));
	scrolledComposite.setExpandHorizontal(true);
	scrolledComposite.setExpandVertical(true);
	scrolledComposite.setAlwaysShowScrollBars(true);

	MouseWheelListener wheelListener = new MouseWheelListener() {
	    /**
	     * handles the mousewheel
	     */
	    public void mouseScrolled(final MouseEvent mouseEvent) {
		ScrolledComposite scrolledComposite = (ScrolledComposite) mouseEvent.getSource();
		Point point = scrolledComposite.getOrigin();
		point.y -= mouseEvent.count * scrollspeed;
		scrolledComposite.setOrigin(point);
	    }
	};

	scrolledComposite.addMouseWheelListener(wheelListener);

	sashBody = new CustomSashForm(scrolledComposite, SWT.VERTICAL
		| SWT.NO_REDRAW_RESIZE);
	sashBody.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

	scrolledComposite.setMinSize(sashBody.getSashWidth(), 600);
	scrolledComposite.setContent(sashBody);

	parent.addControlListener(new ControlAdapter() {
	    @Override
	    public void controlResized(ControlEvent e) {
		resize(false, null);
	    }
	});

	heightListener = new ModifyListener() {
	    @Override
	    public void modifyText(ModifyEvent modifyEvent) {
		SashManager<?>.SashManagerRow row = null;
		if (modifyEvent.getSource() instanceof Control) {
		    Control control = (Control) modifyEvent.getSource();
		    if (control.getParent() instanceof SashManager.SashManagerRow) {
			row = (SashManager<?>.SashManagerRow) control.getParent();
		    } else if (control.getParent().getParent() instanceof SashManager.SashManagerRow) {
			row = (SashManager<?>.SashManagerRow) control.getParent()
				.getParent();
		    }
		}
		resize(false, row);
	    }
	};
    }

    /**
     * No updates for sashcells are made from this point on.
     */
    public void dispose() {
	disposed = true;
    }

    /**
     * Used to create Default data
     * 
     * @return null
     */
    protected T createDefaultData() {
	return null;
    }

    /**
     * Setter for layout
     * 
     * @param layout
     */
    public void setLayoutData(Object layout) {
	composite.setLayoutData(layout);
    }

    /**
     * Adds the given column to the Sashform
     * 
     * @param sashManagerColumn
     *            the column to be added to the list
     * @param title Title of the column.
     * @param width
     * @param fixedWidth
     * @param traversable
     * @param tooltip Text of the tooltip.
     */
    public void addColumn(SashManagerColumn<?> sashManagerColumn, String title,
	    int width, boolean fixedWidth, boolean traversable, String tooltip) {
	columnList.add(sashManagerColumn);
	sashManagerColumn.setup(title, width, fixedWidth, traversable, tooltip);
    }

    /**
     * Disposes and removes the column at the specific index
     * 
     * @param index
     *            index to be disposed
     */
    public void removeColumn(int index) {
	columnList.get(index).dispose();
	columnList.remove(index);
    }

    /**
     * Sets the content of the sashform with the given testcases.
     * 
     * @param objects
     */
    public void setContent(List<T> objects) {
	if (objects == null) {
	    return;
	}
	// Clear all
	while (!(rowList.isEmpty())) {
	    SashManagerRow sashManagerRow = rowList.getFirst();
	    rowList.remove(sashManagerRow);
	    sashManagerRow.dispose();
	}
	// Add sashes
	this.addSashesAtTop(objects.size());
	// Set the content of the sashes
	for (int i = 0; i < objects.size(); i++) {
	    SashManagerRow sashManagerRow = rowList.get(i);
	    T ts = objects.get(i);
	    sashManagerRow.setInput(ts);
	}
	for (int i = 0; i < rowList.size(); i++) {
	    SashManagerRow sashManagerRow = rowList.get(i);
	    sashManagerRow.render(i);
	}
	// Update the labels
	Display.getCurrent().syncExec(new Runnable() {
	    public void run() {
		updateLabels(false, null);
		// setting the focus to avoid some strange shifting to
		// the project explorer
		if (isDisposed()) {
		    return;
		}
		scrolledComposite.setFocus();

	    }
	});
    }

    /**
     * Sets the content of the sashform with the given strings.
     * 
     * @param objects
     */
    public void setContent(ArrayList<String[]> revisions) {
	if (revisions.size() == 0) {
	    return;
	}
	// Clear all
	while (!(rowList.isEmpty())) {
	    SashManagerRow sashManagerRow = rowList.getFirst();
	    rowList.remove(sashManagerRow);
	    sashManagerRow.dispose();
	}
	// Add sashes
	this.addSashesAtTop(revisions.size());
	// Set the content of the sashes
	for (int i = 0; i < revisions.size(); i++) {
	    SashManagerRow sashManagerRow = rowList.get(i);
	    sashManagerRow.setInput(revisions.get(i));
	}
	for (int i = 0; i < rowList.size(); i++) {
	    SashManagerRow sashManagerRow = rowList.get(i);
	    sashManagerRow.render(i);
	}
	// Update the labels
	Display.getCurrent().asyncExec(new Runnable() {
	    public void run() {
		if (disposed) {
		    return;
		}
		updateLabels(false, null);
		// setting the focus to avoid some strange shifting to
		// the project explorer
		scrolledComposite.setFocus();

	    }
	});
    }

    /**
     * @return the Content
     */
    public List<T> getContent() {
	List<T> content = new ArrayList<T>();
	for (int i = 0; i < rowList.size(); i++) {
	    SashManagerRow sashManagerRow = rowList.get(i);
	    content.add(sashManagerRow.getContent(i));
	}
	return content;
    }

    /**
     * Getter for stepSize.
     * 
     * @return The size of the rowList.
     */
    public int getStepSize() {
	return rowList.size();
    }

    /**
     * Deletes the given row.
     * 
     * @param row The number of the row to be deleted.
     */
    public void deleteRow(int row) {
	SashManagerRow sashRow = rowList.remove(row);
	sashRow.dispose();
	row = Math.min(row + 1, rowList.size() - 1);
	if (!rowList.isEmpty()) {
	    sashRow = rowList.get(row);
	}
	updateLabels(true, sashRow);
	rowNumberChanged(rowList.size());
    }

    /**
     * Adds num SashForms at the top
     * 
     * @param num
     *            to add
     */
    public void addSashesAtTop(int num) {
	addSashes(num, 0);
    }

    /**
     * Adds num SashForms at the bottom
     * 
     * @param num
     *            to add
     */
    public void addSashesAtBottom(int num) {
	addSashes(num, rowList.size());
    }

    /**
     * Inserts num SashForms at the given index
     * 
     * @param num
     *            to add
     * @param index
     *            starting index for adding
     */
    public void addSashes(int num, int index) {
	SashManagerRow sashManagerRow = null;
	for (int i = 0; i < num; i++) {
	    sashManagerRow = new SashManagerRow(sashBody, SWT.HORIZONTAL);
	    sashManagerRow.setup();
	    rowList.add(index + i, sashManagerRow);
	}
	updateLabels(false, sashManagerRow);
	rowNumberChanged(rowList.size());
    }

    private void rowNumberChanged(int num) {
	for (int i = 0; i < num; i++) {
	    SashManagerRow sashManagerRow = rowList.get(i);
	    sashManagerRow.rowNumberChanged(num, i);
	}
    }

    /**
     * Scrolls to the given row
     * 
     * @param sashManagerRow
     */
    private void scrollToVisible(SashManager<?>.SashManagerRow sashManagerRow) {
	int absolutePosition = sashManagerRow.getLocation().y;
	int relativePosition = absolutePosition
		- scrolledComposite.getOrigin().y;
	int height = sashManagerRow.getMaxHeight();
	Rectangle area = scrolledComposite.getClientArea();
	Point origin = scrolledComposite.getOrigin();
	if (relativePosition < 0) {
	    origin.y = Math.max(0, origin.y + relativePosition);
	} else {
	    origin.y = Math.max(0, relativePosition + height - area.height);
	}
	scrolledComposite.setOrigin(origin);
    }

    /**
     * Scrolls the composite to top row
     */
    public void scrollToTop() {
	scrolledComposite.setOrigin(0, 0);
    }

    /**
     * Resizes the component.
     * 
     * @param force
     *            layout even if no sashform changed height
     * @param scrollToRow
     */
    private void resize(boolean force, SashManager<?>.SashManagerRow scrollToRow) {
	int height = 0;

	int[] sashWidths = new int[columnList.size()];
	for (int i = 0; i < columnList.size(); i++) {
	    sashWidths[i] = columnList.get(i).getWidth();
	}

	sashHeader.setWeights(sashWidths);

	int[] sashHeights = new int[rowList.size()];

	// Getting the current height and check for changes
	for (int i = 0; i < rowList.size(); i++) {
	    SashManagerRow sashManagerRow = rowList.get(i);

	    sashManagerRow.setWeights(sashWidths);

	    sashHeights[i] = Math.max(30, sashManagerRow.getMaxHeight());
	    height += sashHeights[i];

	    force = force || sashManagerRow.isHeightChanged();
	    sashManagerRow.setHeightChanged(false);
	}
	// We only need to layout if sth. was changed or it is forced
	if (force) {
	    Point size = scrolledComposite.getSize();
	    size.y = height;
	    sashBody.setWeights(sashHeights);
	    scrolledComposite.setMinSize(size);
	    scrolledComposite.setSize(size);
	    scrolledComposite.getParent().layout(true, true);
	    if (scrollToRow != null) {
		scrollToVisible(scrollToRow);
	    }
	    scrolledComposite.redraw();
	    scrolledComposite.update();
	}
    }

    /**
     * Updates the labels of our SashForms, updates their position and the size
     * of the ScrolledComosite
     * 
     * @param resizeForce layout even if no sashform changed height.
     * @param updateRow row which labels should be updated.
     */
    private void updateLabels(boolean resizeForce, SashManagerRow updateRow) {
	if (isDisposed()) {
	    return;
	}

	if (rowList.isEmpty()) {
	    return;
	}

	for (int i = 0; i < rowList.size(); i++) {
	    SashManagerRow sashManagerRow = rowList.get(i);
	    sashManagerRow.rowIndexChanged(i);
	    if (i >= 1) {
		sashManagerRow.moveBelow(rowList.get(i - 1));
	    }
	}
	resize(resizeForce, updateRow);
    }

    /**
     * Describes the element type in a particular column and how to render it.
     * 
     * @author Albert Flaig
     * @usage <pre>
     * TableManager&lt;TestStep&gt; tableManager = new TableManager&lt;TestStep&gt;(parent,
     * 	SWT.None);
     * tableManager.addColumn(tableManager.new TableManagerColumn&lt;Label&gt;() {
     *     &#064;Override
     *     public Label create(Table table) {
     * 	return new Label(table, SWT.NONE);
     *     }
     * 
     *     &#064;Override
     *     public void render(Label widget, TestStep data, int row, int column) {
     * 	widget.setText(data.getID() + &quot;&quot;);
     *     }
     * }, &quot;Status&quot;, 40, true);
     * </pre>
     */
    public abstract class SashManagerColumn<W extends Control> {
	private Label columnHeader;
	private int width;
	private boolean traversable;

	public boolean isTraversable() {
	    return traversable;
	}

	/**
	 * Create here a new widget of type Control and set its content in the
	 * render()-method.
	 * 
	 * @param table
	 *            when instantiating the widget, this is its parent
	 *            composite.<br>
	 *            e.g. <code>return new Label(table, SWT.NONE);</code>
	 * @return the newly created widget of type Control
	 */
	protected abstract W create(Composite parent,
		ModifyListener heightListener);

	/**
	 * Sets the needed attributes in order to display the correct data in a
	 * particular cell of the table.
	 * 
	 * @param widget
	 *            the widget to change the attributes
	 * @param data
	 *            in the current row
	 * @param row
	 *            the current row index
	 * @param column
	 *            the current column index
	 */
	protected abstract void renderTC(W widget, T data, int row, int column);

	/**
	 * Sets the needed attributes in order to display the correct data in a
	 * particular cell of the table.
	 * 
	 * @param widget
	 *            the widget to change the attributes
	 * @param data
	 *            in the current row
	 * @param row
	 *            the current row index
	 * @param column
	 *            the current column index
	 */
	protected abstract void renderCustom(W widget, String[] data, int row,
		int column);

	/**
	 * Sets up the columnheader.
	 * 
	 * @param title Title of the columm.
	 * @param width
	 * @param fixedWidth
	 * @param traversable
	 * @param tooltip Text of the tooltip.
	 */
	final void setup(String title, int width, boolean fixedWidth,
		boolean traversable, String tooltip) {
	    columnHeader = new Label(sashHeader, SWT.CENTER);
	    columnHeader.setText(title);
	    if (tooltip != null) {
		columnHeader.setToolTipText(tooltip);
	    }
	    Font font = columnHeader.getFont();
	    String name = "";
	    int height = 16;
	    int style = 0;
	    for (FontData data : font.getFontData()) {
		height = data.getHeight();
		style = data.getStyle() | SWT.BOLD;
		name = data.getName();
	    }
	    font = new Font(font.getDevice(), name, height, style);
	    columnHeader.setFont(font);
	    font.dispose();
	    columnHeader.setSize(width, 20);
	    this.traversable = traversable;
	    this.width = width;
	}

	/**
	 * Getter for the width.
	 * 
	 * @return
	 */
	final int getWidth() {
	    return width;
	}

	/**
	 * Getter for the height
	 * 
	 * @param widget
	 * @return
	 */
	protected int getHeight(W widget) {
	    return 0;
	}

	/**
	 * Disposes the header.
	 */
	final void dispose() {
	    columnHeader.dispose();
	}

	/**
	 * @param control
	 * @param data
	 * @param row
	 * @param column
	 * @return the data.
	 */
	protected T writeContentToData(W control, T data, int row, int column) {
	    return data;
	}

	protected void rowNumberChanged(W control, int num, int row) {
	    // to be overridden
	}

	protected void rowIndexChanged(W control, int row) {
	    // to be overridden
	}

	/**
	 * Returns the height of the control.
	 * 
	 * @param control
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final int getHeight2(Control control) {
	    return getHeight((W) control);
	}

	/**
	 * Renders the testcases.
	 * 
	 * @param control
	 *            widget of the testcase
	 * @param data
	 *            data of the testcase
	 * @param row
	 *            x position of the testcase
	 * @param column
	 *            y position of the testcase
	 */
	@SuppressWarnings("unchecked")
	final void render2(Control control, T data, int row, int column) {
	    Map<Integer, Listener[]> tempListeners = SWTUtils
		    .removeAllListeners(control);
	    renderTC((W) control, data, row, column);
	    SWTUtils.restoreAllListeners(control, tempListeners);
	}

	/**
	 * Renders the widgets
	 * 
	 * @param control
	 *            widget to be rendered
	 * @param data
	 *            data to be displayed
	 * @param row
	 *            x position of the testcase
	 * @param column
	 *            y position of the testcase
	 */
	@SuppressWarnings("unchecked")
	final void render3(Control control, String[] data, int row, int column) {
	    Map<Integer, Listener[]> tempListeners = SWTUtils
		    .removeAllListeners(control);
	    renderCustom((W) control, data, row, column);
	    SWTUtils.restoreAllListeners(control, tempListeners);
	}

	/**
	 * Returns the data.
	 * 
	 * @param control
	 * @param data
	 * @param row
	 * @param column
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final T writeContentToData2(Control control, T data, int row, int column) {
	    return writeContentToData((W) control, data, row, column);
	}

	/**
	 * does nothing
	 * 
	 * @param control
	 * @param num
	 * @param row
	 */
	@SuppressWarnings("unchecked")
	final void rowNumberChanged2(Control control, int num, int row) {
	    rowNumberChanged((W) control, num, row);
	}

	/**
	 * does nothing
	 * 
	 * @param control
	 * @param row
	 */
	@SuppressWarnings("unchecked")
	final void rowIndexChanged2(Control control, int row) {
	    rowIndexChanged((W) control, row);
	}
    }

    final public class SashManagerRow extends CustomSashForm {
	private String[] data;
	private T tcData;
	private boolean wasHeightChanged;
	private List<Control> widgets = new ArrayList<Control>();
	private int oldMaxHeight;

	/**
	 * Creates a new row.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT-Style
	 */
	SashManagerRow(Composite parent, int style) {
	    super(parent, style);
	}

	/**
	 * 
	 * @param num
	 * @param row
	 */
	void rowNumberChanged(int num, int row) {
	    for (int index = 0; index < columnList.size(); index++) {
		SashManagerColumn<? extends Control> column = columnList
			.get(index);
		column.rowNumberChanged2(widgets.get(index), num, row);
	    }
	}

	/**
	 * @param row number of the row which index changed.
	 */
	void rowIndexChanged(int row) {
	    for (int index = 0; index < columnList.size(); index++) {
		SashManagerColumn<? extends Control> column = columnList
			.get(index);
		column.rowIndexChanged2(widgets.get(index), row);
	    }
	}

	/**
	 * @return The biggest height of a column component
	 */
	int getMaxHeight() {
	    Collection<Integer> columnHeights = new ArrayList<Integer>();
	    for (int index = 0; index < columnList.size(); index++) {
		SashManagerColumn<? extends Control> column = columnList
			.get(index);
		columnHeights.add(column.getHeight2(widgets.get(index)));
	    }
	    int maxHeight = Collections.max(columnHeights);
	    if (oldMaxHeight != maxHeight) {
		oldMaxHeight = maxHeight;
		setHeightChanged(true);
	    }
	    return maxHeight;
	}

	/**
	 * @return whether the amount of lines was changed between the last two
	 *         calls of getMaxHeight()
	 */
	boolean isHeightChanged() {
	    return wasHeightChanged;
	}

	void setHeightChanged(boolean change) {
	    wasHeightChanged = change;
	}

	/**
	* Renders a row.
	* @param row The number of the row to be rendered.
	*/
	void render(int row) {
	    if (tcData == null) {
		if (data == null) {
		    return;
		} else {
		    for (int index = 0; index < columnList.size(); index++) {
			SashManagerColumn<? extends Control> column = columnList
				.get(index);
			column.render3(widgets.get(index), data, row, index);
		    }
		}
	    } else {
		for (int index = 0; index < columnList.size(); index++) {
		    SashManagerColumn<? extends Control> column = columnList
			    .get(index);
		    column.render2(widgets.get(index), tcData, row, index);
		}
	    }
	}

	/**
	* Gets the content of a given row.
	* @param row The row to get the content from.
	* @return The content of the row.
	*/
	void setup() {
	    tcData = createDefaultData();
	    ArrayList<Control> tabList = new ArrayList<Control>();
	    for (int index = 0; index < columnList.size(); index++) {
		SashManagerColumn<? extends Control> column = columnList
			.get(index);
		Control widget = column.create(this, heightListener);
		if (column.isTraversable()) {
		    tabList.add(widget);
		}
		widgets.add(widget);
	    }
	    setTabList(tabList.toArray(new Control[0]));
	}

	void setInput(T data) {
	    this.tcData = data;
	}

	void setInput(String[] data) {
	    this.data = data;
	}

	/**
	* Gets the name of all columns of the SashManager.
	* @return An array with the name of all columns.
	*/
	T getContent(int row) {
	    for (int index = 0; index < columnList.size(); index++) {
		SashManagerColumn<? extends Control> column = columnList
			.get(index);
		tcData = column.writeContentToData2(widgets.get(index), tcData,
			row, index);
	    }
	    return tcData;
	}
    }

    public ArrayList<String> getColumnListNames() {
	ArrayList<String> headerNames = new ArrayList<String>();
	for (SashManagerColumn<? extends Control> column : columnList) {
	    headerNames.add(column.columnHeader.getText());
	}
	return headerNames;
    }

    /**
    * Removes all columns except the first two.
    */
    public void removeAllColumns() {
	while (columnList.size() > 2) {
	    columnList.get(2).dispose();
	    columnList.remove(2);
	}
    }

    /**
    * @return True if SashManager is disposed.
    */
    public boolean isDisposed() {
	return disposed;
    }
}
