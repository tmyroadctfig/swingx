/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SizeSequence;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.PipelineEvent;
import org.jdesktop.swingx.decorator.PipelineListener;
import org.jdesktop.swingx.decorator.RowSizing;
import org.jdesktop.swingx.decorator.SearchHighlighter;
import org.jdesktop.swingx.decorator.Selection;
import org.jdesktop.swingx.decorator.Sorter;
import org.jdesktop.swingx.icon.ColumnControlIcon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;

/**
 * <p>
 * A JXTable is a JTable with built-in support for row sorting, filtering, and
 * highlighting, column visibility and a special popup control on the column
 * header for quick access to table configuration. You can instantiate a JXTable
 * just as you would a JTable, using a TableModel. However, a JXTable
 * automatically wraps TableColumns inside a TableColumnExt instance.
 * TableColumnExt supports visibility, sortability, and prototype values for
 * column sizing, none of which are available in TableColumn. You can retrieve
 * the TableColumnExt instance for a column using {@link #getColumnExt(Object)}
 * or {@link #getColumnExt(int colnumber)}.
 * 
 * <p>
 * A JXTable is, by default, sortable by clicking on column headers; each
 * subsequent click on a header reverses the order of the sort, and a sort arrow
 * icon is automatically drawn on the header. Sorting can be disabled using
 * {@link #setSortable(boolean)}. Sorting on columns is handled by a Sorter
 * instance which contains a Comparator used to compare values in two rows of a
 * column. You can replace the Comparator for a given column by using
 * <code>getColumnExt("column").getSorter().setComparator(customComparator)</code>
 * 
 * <p>
 * Columns can be hidden or shown by setting the visible property on the
 * TableColumnExt using {@link TableColumnExt#setVisible(boolean)}. Columns can
 * also be shown or hidden from the column control popup.
 * 
 * <p>
 * The column control popup is triggered by an icon drawn to the far right of
 * the column headers, above the table's scrollbar (when installed in a
 * JScrollPane). The popup allows the user to select which columns should be
 * shown or hidden, as well as to pack columns and turn on horizontal scrolling.
 * To show or hide the column control, use the
 * {@link #setColumnControlVisible(boolean show)}method.
 * 
 * <p>
 * Rows can be filtered from a JXTable using a Filter class and a
 * FilterPipeline. One assigns a FilterPipeline to the table using
 * {@link #setFilters(FilterPipeline)}. Filtering hides, but does not delete or
 * permanently remove rows from a JXTable. Filters are used to provide sorting
 * to the table--rows are not removed, but the table is made to believe rows in
 * the model are in a sorted order.
 * 
 * <p>
 * One can automatically highlight certain rows in a JXTable by attaching
 * Highlighters in the {@link #setHighlighters(HighlighterPipeline)}method. An
 * example would be a Highlighter that colors alternate rows in the table for
 * readability; AlternateRowHighlighter does this. Again, like Filters,
 * Highlighters can be chained together in a HighlighterPipeline to achieve more
 * interesting effects.
 * 
 * <p>
 * You can resize all columns, selected columns, or a single column using the
 * methods like {@link #packAll()}. Packing combines several other aspects of a
 * JXTable. If horizontal scrolling is enabled using
 * {@link #setHorizontalScrollEnabled(boolean)}, then the scrollpane will allow
 * the table to scroll right-left, and columns will be sized to their preferred
 * size. To control the preferred sizing of a column, you can provide a
 * prototype value for the column in the TableColumnExt using
 * {@link TableColumnExt#setPrototypeValue(Object)}. The prototype is used as
 * an indicator of the preferred size of the column. This can be useful if some
 * data in a given column is very long, but where the resize algorithm would
 * normally not pick this up.
 * 
 * <p>
 * Last, you can also provide searches on a JXTable using the Searchable property.
 * 
 * <p>
 * Keys/Actions registered with this component:
 * 
 * <ul>
 * <li> "find" - open an appropriate search widget for searching cell content. The
 *   default action registeres itself with the SearchFactory as search target.
 * <li> "print" - print the table
 * <li> {@link JXTable#HORIZONTAL_ACTION_COMMAND} - toggle the horizontal scrollbar
 * <li> {@link JXTable#PACKSELECTED_ACTION_COMMAND} - resize the selected column to fit the widest
 *  cell content 
 * <li> {@link JXTable#PACKALL_ACTION_COMMAND} - resize all columns to fit the widest
 *  cell content in each column
 * 
 * </ul>
 * 
 * <p>
 * Key bindings.
 * 
 * <ul>
 * <li> "control F" - bound to actionKey "find".
 * </ul>
 * 
 * <p>
 * Client Properties.
 * 
 * <ul>
 * <li> {@link JXTable#MATCH_HIGHLIGHTER} - set to Boolean.TRUE to 
 *  use a SearchHighlighter to mark a cell as matching.
 * </ul>
 * 
 * @author Ramesh Gupta
 * @author Amy Fowler
 * @author Mark Davidson
 * @author Jeanette Winzenburg
 */
public class JXTable extends JTable { //implements Searchable {
    /**
     * Constant string for horizontal scroll actions, used in JXTable's Action
     * Map.
     */
    public static final String HORIZONTALSCROLL_ACTION_COMMAND = 
        ColumnControlButton.COLUMN_CONTROL_MARKER + "horizontalScroll";

    /** Constant string for packing all columns, used in JXTable's Action Map. */
    public static final String PACKALL_ACTION_COMMAND = 
        ColumnControlButton.COLUMN_CONTROL_MARKER + "packAll";

    /**
     * Constant string for packing selected columns, used in JXTable's Action
     * Map.
     */
    public static final String PACKSELECTED_ACTION_COMMAND = 
        ColumnControlButton.COLUMN_CONTROL_MARKER + "packSelected";

    /** The prefix marker to find component related properties in the resourcebundle. */
    public static final String UIPREFIX = "JXTable.";

    /** key for client property to use SearchHighlighter as match marker. */
    public static final String MATCH_HIGHLIGHTER = "match.highlighter";

    static {
        // Hack: make sure the resource bundle is loaded
        LookAndFeelAddons.getAddon();
    }

    /** The FilterPipeline for the table. */
    protected FilterPipeline filters = null;

    /** The HighlighterPipeline for the table. */
    protected HighlighterPipeline highlighters = null;

    /** The ComponentAdapter for model data access. */
    protected ComponentAdapter dataAdapter;

    /** The handler for mapping view/model coordinates of row selection. */
    private Selection selection;

    /** flag to indicate if table is interactively sortable. */
    private boolean sortable;

    /** future - enable/disable autosort on cell updates not used */
//    private boolean automaticSortDisabled;

    /** Listens for changes from the filters. */
    private PipelineListener pipelineListener;

    /** Listens for changes from the highlighters. */
    private ChangeListener highlighterChangeListener;

    /** the factory to use for column creation and configuration. */
    private ColumnFactory columnFactory;

    /** The default number of visible rows (in a ScrollPane). */
    private int visibleRowCount = 18;

    private RowSizing rowSizing;

    private Field rowModelField;

    private boolean rowHeightEnabled;

    /**
     * flag to indicate if the column control is visible.
     */
    private boolean columnControlVisible;
    /**
     * ScrollPane's original vertical scroll policy. If the columnControl is
     * visible the policy is set to ALWAYS.
     */
    private int verticalScrollPolicy;

    /**
     * A button that allows the user to select which columns to display, and
     * which to hide
     */
    private JComponent columnControlButton;

    /**
     * Mouse/Motion/Listener keeping track of mouse moved in cell coordinates.
     */
    private RolloverProducer rolloverProducer;

    /**
     * RolloverController: listens to cell over events and repaints
     * entered/exited rows.
     */
    private LinkController linkController;

    /** field to store the autoResizeMode while interactively setting 
     *  horizontal scrollbar to visible.
     */
    private int oldAutoResizeMode;

    /** temporary hack: rowheight will be internally adjusted to font size 
     *  on instantiation and in updateUI if 
     *  the height has not been set explicitly by the application.
     */
    protected boolean isXTableRowHeightSet;

    protected Searchable searchable;

    /** Instantiates a JXTable with a default table model, no data. */
    public JXTable() {
        init();
    }

    /**
     * Instantiates a JXTable with a specific table model.
     * 
     * @param dm
     *            The model to use.
     */
    public JXTable(TableModel dm) {
        super(dm);
        init();
    }

    /**
     * Instantiates a JXTable with a specific table model.
     * 
     * @param dm
     *            The model to use.
     */
    public JXTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        init();
    }

    /**
     * Instantiates a JXTable with a specific table model, column model, and
     * selection model.
     * 
     * @param dm
     *            The table model to use.
     * @param cm
     *            The colomn model to use.
     * @param sm
     *            The list selection model to use.
     */
    public JXTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        init();
    }

    /**
     * Instantiates a JXTable for a given number of columns and rows.
     * 
     * @param numRows
     *            Count of rows to accomodate.
     * @param numColumns
     *            Count of columns to accomodate.
     */
    public JXTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        init();
    }

    /**
     * Instantiates a JXTable with data in a vector or rows and column names.
     * 
     * @param rowData
     *            Row data, as a Vector of Objects.
     * @param columnNames
     *            Column names, as a Vector of Strings.
     */
    public JXTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
        init();
    }

    /**
     * Instantiates a JXTable with data in a array or rows and column names.
     * 
     * @param rowData
     *            Row data, as a two-dimensional Array of Objects (by row, for
     *            column).
     * @param columnNames
     *            Column names, as a Array of Strings.
     */
    public JXTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        init();
    }

    /** Initializes the table for use. */
    protected void init() {
        setSortable(true);
        // guarantee getFilters() to return != null
        setFilters(null);
        initActionsAndBindings();
        // instantiate row height depending on font size
        updateRowHeightUI(false);
    }

    /**
     * Property to enable/disable rollover support. This can be enabled to show
     * "live" rollover behaviour, f.i. the cursor over LinkModel cells. Default
     * is disabled. If using a RolloverHighlighter on the table, this should be
     * set to true.
     * 
     * @param rolloverEnabled
     */
    public void setRolloverEnabled(boolean rolloverEnabled) {
        boolean old = isRolloverEnabled();
        if (rolloverEnabled == old)
            return;
        if (rolloverEnabled) {
            rolloverProducer = new RolloverProducer();
            addMouseListener(rolloverProducer);
            addMouseMotionListener(rolloverProducer);
            linkController = new LinkController();
            addPropertyChangeListener(linkController);
        } else {
            removeMouseListener(rolloverProducer);
            removeMouseMotionListener(rolloverProducer);
            rolloverProducer = null;
            removePropertyChangeListener(linkController);
            linkController = null;
        }
        firePropertyChange("rolloverEnabled", old, isRolloverEnabled());
    }

    /**
     * Returns the rolloverEnabled property.
     * 
     * @return <code>true</code> if rollover is enabled
     */
    public boolean isRolloverEnabled() {
        return rolloverProducer != null;
    }

    /**
     * If the default editor for LinkModel.class is of type LinkRenderer enables
     * link visiting with the given linkVisitor. As a side-effect the rollover
     * property is set to true.
     * 
     * @param linkVisitor
     */
    public void setDefaultLinkVisitor(ActionListener linkVisitor) {
        TableCellEditor renderer = getDefaultEditor(LinkModel.class);
        if (renderer instanceof LinkRenderer) {
            ((LinkRenderer) renderer).setVisitingDelegate(linkVisitor);
        }
        setRolloverEnabled(true);
    }

    
//--------------------------------- ColumnControl
    
    /**
     * overridden to addionally configure the upper right corner of an enclosing
     * scrollpane with the ColumnControl.
     */
    protected void configureEnclosingScrollPane() {
        super.configureEnclosingScrollPane();
        configureColumnControl();
        configureViewportBackground();
    }

    /**
     * set's the viewports background to this.background. PENDING: need to
     * repeat on background changes to this!
     * 
     */
    protected void configureViewportBackground() {
        Container p = getParent();
        if (p instanceof JViewport) {
            p.setBackground(getBackground());
        }
    }

    /**
     * configure the upper right corner of an enclosing scrollpane with/o the
     * ColumnControl, depending on setting of columnControl visibility flag.
     * 
     * PENDING: should choose corner depending on component orientation.
     */
    private void configureColumnControl() {
        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this) {
                    return;
                }
                if (isColumnControlVisible()) {
                    verticalScrollPolicy = scrollPane
                            .getVerticalScrollBarPolicy();
                    scrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER,
                            getColumnControl());

                    scrollPane
                            .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                } else {
                    if (verticalScrollPolicy != 0) {
                        // Fix #155-swingx: reset only if we had force always before
                        // PENDING: JW - doesn't cope with dynamically changing the policy
                        // shouldn't be much of a problem because doesn't happen too often?? 
                        scrollPane.setVerticalScrollBarPolicy(verticalScrollPolicy);
                    }
                    try {
                        scrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER,
                                null);
                    } catch (Exception ex) {
                        // Ignore spurious exception thrown by JScrollPane. This
                        // is a Swing bug!
                    }

                }
            }
        }
    }

    
    @Override
    public void setComponentOrientation(ComponentOrientation o) {
        super.setComponentOrientation(o);
        configureColumnControl();
    }

    /**
     * returns visibility flag of column control.
     * <p>
     * 
     * Note: if the table is not inside a JScrollPane the column control is not
     * shown even if this returns true. In this case it's the responsibility of
     * the client code to actually show it.
     * 
     * @return
     */
    public boolean isColumnControlVisible() {
        return columnControlVisible;
    }

    /**
     * returns the component for column control.
     * 
     * @return
     */
    public JComponent getColumnControl() {
        if (columnControlButton == null) {
            columnControlButton = new ColumnControlButton(this,
                    new ColumnControlIcon());
        }
        return columnControlButton;
    }

    /**
     * bound property to flag visibility state of column control.
     * 
     * @param showColumnControl
     */
    public void setColumnControlVisible(boolean showColumnControl) {
        boolean old = columnControlVisible;
        this.columnControlVisible = showColumnControl;
        configureColumnControl();
        firePropertyChange("columnControlVisible", old, columnControlVisible);
    }

    
//--------------------- actions
    
    /**
     * A small class which dispatches actions. TODO: Is there a way that we can
     * make this static? JW: I hate those if constructs... we are in OO-land!
     */
    private class Actions extends UIAction {
        Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
            if ("print".equals(getName())) {
                try {
                    print();
                } catch (PrinterException ex) {
                    // REMIND(aim): should invoke pluggable application error
                    // handler
                    ex.printStackTrace();
                }
            } else if ("find".equals(getName())) {
                find();
            }
        }

    }


    private void initActionsAndBindings() {
        // Register the actions that this class can handle.
        ActionMap map = getActionMap();
        map.put("print", new Actions("print"));
        map.put("find", new Actions("find"));
        map.put(PACKALL_ACTION_COMMAND, createPackAllAction());
        map.put(PACKSELECTED_ACTION_COMMAND, createPackSelectedAction());
        map.put(HORIZONTALSCROLL_ACTION_COMMAND, createHorizontalScrollAction());
        // this should be handled by the LF!
        KeyStroke findStroke = KeyStroke.getKeyStroke("control F");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(findStroke, "find");
    }

    /** Creates an Action for horizontal scrolling. */
    private Action createHorizontalScrollAction() {
        String actionName = getUIString(HORIZONTALSCROLL_ACTION_COMMAND);
        BoundAction action = new BoundAction(actionName,
                HORIZONTALSCROLL_ACTION_COMMAND);
        action.setStateAction();
        action.registerCallback(this, "setHorizontalScrollEnabled");
        action.setSelected(isHorizontalScrollEnabled());
        return action;
    }

    private String getUIString(String key) {
        String text = UIManager.getString(UIPREFIX + key);
        return text != null ? text : key;
    }

    /** Creates an Action for packing selected columns. */
    private Action createPackSelectedAction() {
        String text = getUIString(PACKSELECTED_ACTION_COMMAND);
        BoundAction action = new BoundAction(text, PACKSELECTED_ACTION_COMMAND);
        action.registerCallback(this, "packSelected");
        action.setEnabled(getSelectedColumnCount() > 0);
        return action;
    }

    /** Creates an Action for packing all columns. */
    private Action createPackAllAction() {
        String text = getUIString(PACKALL_ACTION_COMMAND);
        BoundAction action = new BoundAction(text, PACKALL_ACTION_COMMAND);
        action.registerCallback(this, "packAll");
        return action;
    }

    
//------------------ bound action callback methods
    
    /**
     * This resizes all columns to fit the viewport; if horizontal scrolling is
     * enabled, all columns will get their preferred width. This can be
     * triggered by the "packAll" BoundAction on the table as well.
     */
    public void packAll() {
        packTable(getDefaultPackMargin());
    }

    /**
     * This resizes selected columns to fit the viewport; if horizontal
     * scrolling is enabled, selected columns will get their preferred width.
     * This can be triggered by the "packSelected" BoundAction on the table as
     * well.
     */
    public void packSelected() {
        int selected = getSelectedColumn();
        if (selected >= 0) {
            packColumn(selected, getDefaultPackMargin());
        }
    }

    /**
     * Controls horizontal scrolling in the viewport, and works in coordination
     * with column sizing.
     * 
     * @param enabled
     *            If true, the scrollpane will allow the table to scroll
     *            horizontally, and columns will resize to their preferred
     *            width. If false, columns will resize to fit the viewport.
     */
    public void setHorizontalScrollEnabled(boolean enabled) {
        if (enabled == (isHorizontalScrollEnabled()))
            return;
        if (enabled) {
            oldAutoResizeMode = getAutoResizeMode();
            setAutoResizeMode(AUTO_RESIZE_OFF);
        } else {
            setAutoResizeMode(oldAutoResizeMode);
        }
    }

    /** Returns the current setting for horizontal scrolling. */
    private boolean isHorizontalScrollEnabled() {
        return getAutoResizeMode() == AUTO_RESIZE_OFF;
    }

    /** Returns the default margin for packing columns. */
    private int getDefaultPackMargin() {
        return 4;
    }

    /** Notifies the table that a new column has been selected. 
     *  overridden to update the enabled state of the packSelected
     *  action.
     */
    public void columnSelectionChanged(ListSelectionEvent e) {
        super.columnSelectionChanged(e);
        if (e.getValueIsAdjusting())
            return;
        Action packSelected = getActionMap().get(PACKSELECTED_ACTION_COMMAND);
        if ((packSelected != null)) {// && (e.getSource() instanceof
                                        // ListSelectionModel)){
            packSelected.setEnabled(!((ListSelectionModel) e.getSource())
                    .isSelectionEmpty());
        }
    }

    /** 
     * overridden to update the show horizontal scrollbar action's
     * selected state. 
     */
    public void setAutoResizeMode(int mode) {
        super.setAutoResizeMode(mode);
        Action showHorizontal = getActionMap().get(
                HORIZONTALSCROLL_ACTION_COMMAND);
        if (showHorizontal instanceof BoundAction) {
            ((BoundAction) showHorizontal)
                    .setSelected(isHorizontalScrollEnabled());
        }
    }


//------------------------ override super because of filter-awareness
    
    /**
     * Returns the row count in the table; if filters are applied, this is the
     * filtered row count.
     */
    @Override public int getRowCount() {
        // RG: If there are no filters, call superclass version rather than
        // accessing model directly
        return filters == null ?
//        return ((filters == null) || !filters.isAssigned()) ? 
                super.getRowCount() : filters.getOutputSize();
    }

    public boolean isHierarchical(int column) {
        return false;
    }

    /**
     * Convert row index from view coordinates to model coordinates accounting
     * for the presence of sorters and filters.
     * 
     * @param row
     *            row index in view coordinates
     * @return row index in model coordinates
     */
    public int convertRowIndexToModel(int row) {
        return getFilters().convertRowIndexToModel(row);
    }

    /**
     * Convert row index from model coordinates to view coordinates accounting
     * for the presence of sorters and filters.
     * 
     * @param row
     *            row index in model coordinates
     * @return row index in view coordinates
     */
    public int convertRowIndexToView(int row) {
        return getFilters().convertRowIndexToView(row);
    }

    /**
     * {@inheritDoc}
     */
    public Object getValueAt(int row, int column) {
        return getModel().getValueAt(convertRowIndexToModel(row), 
                convertColumnIndexToModel(column));
//        return getFilters().getValueAt(row, convertColumnIndexToModel(column));
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(Object aValue, int row, int column) {
        getModel().setValueAt(aValue, convertRowIndexToModel(row),
                convertColumnIndexToModel(column));
//        getFilters().setValueAt(aValue, row, convertColumnIndexToModel(column));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(int row, int column) {
        return getModel().isCellEditable(convertRowIndexToModel(row),
                convertColumnIndexToModel(column));
//        return getFilters().isCellEditable(row,
//                convertColumnIndexToModel(column));
    }

    /**
     * {@inheritDoc}
     */
    public void setModel(TableModel newModel) {
        // JW: need to clear here because super.setModel
        // calls tableChanged...
        // fixing #173
//        clearSelection();
        getSelection().lock();
        super.setModel(newModel);
        // JW: PENDING - needs cleanup, probably much simpler now...
        // not needed because called in tableChanged
//        use(filters);
    }

    /** ? */
    public void tableChanged(TableModelEvent e) {
        // make Selection deaf ... super doesn't know about row
        // mapping and sets rowSelection in model coordinates
        // causing complete confusion.
        getSelection().lock();
        super.tableChanged(e);
        updateSelectionAndRowModel(e);
        use(filters);
        // JW: this is for the sake of the very first call to setModel, done in
        // super on instantiation - at that time filters cannot be set
        // because they will be re-initialized to null 
        // ... arrrgggg
//        if (filters != null) {
//            filters.flush(); // will call updateOnFilterContentChanged()
//        } else {
//            // not really needed... we reach this branch only on the
//            // very first super.setModel()
//            getSelection().restoreSelection();
//        }
    }

    /**
     * reset model selection coordinates in Selection after
     * model events.
     * 
     * @param e
     */
    private void updateSelectionAndRowModel(TableModelEvent e) {
        // c&p from JTable
        // still missing: checkLeadAnchor
        if (e.getType() == TableModelEvent.INSERT) {
            int start = e.getFirstRow();
            int end = e.getLastRow();
            if (start < 0) {
                start = 0;
            }
            if (end < 0) {
                end = getModel().getRowCount() - 1;
            }

            // Adjust the selection to account for the new rows.
            int length = end - start + 1;
            getSelection().insertIndexInterval(start, length, true);
            getRowSizing().insertIndexInterval(start, length, getRowHeight());

        } else if (e.getType() == TableModelEvent.DELETE) {
            int start = e.getFirstRow();
            int end = e.getLastRow();
            if (start < 0) {
                start = 0;
            }
            if (end < 0) {
                end = getModel().getRowCount() - 1;
            }

            int deletedCount = end - start + 1;
            int previousRowCount = getModel().getRowCount() + deletedCount;
            // Adjust the selection to account for the new rows
            getSelection().removeIndexInterval(start, end);
            getRowSizing().removeIndexInterval(start, deletedCount);

        } else if (getSelectionModel().isSelectionEmpty()) {
            // possibly got a dataChanged or structureChanged
            // super will have cleared selection
            getSelection().clearModelSelection();
            getRowSizing().clearModelSizes();
            getRowSizing().setViewSizeSequence(getSuperRowModel(), getRowHeight());
        }

    }
    
    private Selection getSelection() {
        if (selection == null) {
            selection = new Selection(filters, getSelectionModel());
        }
        return selection;
    }


//----------------------------- filters
    
    /** Returns the FilterPipeline for the table. */
    public FilterPipeline getFilters() {
        // PENDING: this is guaranteed to be != null because
        // init calls setFilters(null) which enforces an empty
        // pipeline
        return filters;
    }

    /**
     * setModel() and setFilters() may be called in either order.
     * 
     * @param pipeline
     */
    private void use(FilterPipeline pipeline) {
        if (pipeline != null) {
            // check JW: adding listener multiple times (after setModel)?
            if (initialUse(pipeline)) {
                pipeline.addPipelineListener(getFilterPipelineListener());
                pipeline.assign(getComponentAdapter());
            } else {
                pipeline.flush();
            }
        }
    }

    /**
     * @return true is not yet used in this JXTable, false otherwise
     */
    private boolean initialUse(FilterPipeline pipeline) {
        if (pipelineListener == null) return true;
        PipelineListener[] l = pipeline.getPipelineListeners();
        for (int i = 0; i < l.length; i++) {
            if (pipelineListener.equals(l[i]))
                return false;
        }
        return true;
    }

    /** Sets the FilterPipeline for filtering table rows. */
    public void setFilters(FilterPipeline pipeline) {
        FilterPipeline old = getFilters();
        Sorter sorter = null;
        if (old != null) {
            old.removePipelineListener(pipelineListener);
            sorter = old.getSorter();
        }
        if (pipeline == null) {
            pipeline = new FilterPipeline();
        }
        filters = pipeline;
        filters.setSorter(sorter);
        getSelection().setFilters(filters);
        getRowSizing().setFilters(filters);
        use(filters);
    }


    /** returns the listener for changes in filters. */
    protected PipelineListener getFilterPipelineListener() {
        if (pipelineListener == null) {
            pipelineListener = createPipelineListener();
        }
        return pipelineListener;
    }

    /** creates the listener for changes in filters. */
    protected PipelineListener createPipelineListener() {
        PipelineListener l = new PipelineListener() {
            public void contentsChanged(PipelineEvent e) {
                updateOnFilterContentChanged();
            }
        };
        return l;
    }


    /** 
     * method called on change notification from filterpipeline.
     */
    protected void updateOnFilterContentChanged() {
        // Force private rowModel in JTable to null;
//        adminSetRowHeight(getRowHeight());
        revalidate();
        repaint();
    }


//-------------------------------- sorting 

    /**
     * Sets &quot;sortable&quot; property indicating whether or not this table
     * supports sortable columns. If <code>sortable</code> is
     * <code>true</code> then sorting will be enabled on all columns whose
     * <code>sortable</code> property is <code>true</code>. If
     * <code>sortable</code> is <code>false</code> then sorting will be
     * disabled for all columns, regardless of each column's individual
     * <code>sorting</code> property. The default is <code>true</code>.
     * 
     * @see TableColumnExt#isSortable()
     * @param sortable
     *            boolean indicating whether or not this table supports sortable
     *            columns
     */
    public void setSortable(boolean sortable) {
        if (sortable == isSortable())
            return;
        this.sortable = sortable;
        if (!isSortable()) resetSorter();
        firePropertyChange("sortable", !sortable, sortable);
        // JW @todo: this is a hack!
        // check if the sortable/not sortable toggling still works with the
        // sorter in pipeline
//        if (getInteractiveSorter() != null) {
//            updateOnFilterContentChanged();
//        }
//
    }

    /** Returns true if the table is sortable. */
    public boolean isSortable() {
        return sortable;
    }

    // public void setAutomaticSort(boolean automaticEnabled) {
    // this.automaticSortDisabled = !automaticEnabled;
    //
    // }
    //
    // public boolean isAutomaticSort() {
    // return !automaticSortDisabled;
    // }

    private void setInteractiveSorter(Sorter sorter) {
        // this check is for the sake of the very first call after instantiation
        if (filters == null)
            return;
        getFilters().setSorter(sorter);

    }

    private Sorter getInteractiveSorter() {
        // this check is for the sake of the very first call after instantiation
        if (filters == null)
            return null;
        return getFilters().getSorter();
    }

    /**
     * Removes the interactive sorter.
     * Used by headerListener.
     * 
     */
    protected void resetSorter() {
        // JW PENDING: think about notification instead of manual repaint.
        setInteractiveSorter(null);
        getTableHeader().repaint();
    }

    public void columnRemoved(TableColumnModelEvent e) {
        // old problem: need access to removed column
        // to get hold of removed modelIndex
        // to remove interactive sorter if any
        // no way
        // int modelIndex = convertColumnIndexToModel(e.getFromIndex());
        updateSorterAfterColumnRemoved();
        super.columnRemoved(e);
    }

    /**
     * guarantee that the interactive sorter is removed if its column
     * is removed.
     * 
     */
    private void updateSorterAfterColumnRemoved() {
        // bloody hack: get sorter and check if there's a column with it
        // available
        Sorter sorter = getInteractiveSorter();
        if (sorter != null) {
            int sorterColumn = sorter.getColumnIndex();
            List columns = getColumns(true);
            for (Iterator iter = columns.iterator(); iter.hasNext();) {
                TableColumn column = (TableColumn) iter.next();
                if (column.getModelIndex() == sorterColumn)
                    return;
            }
            // didn't find a column with the sorter's index - remove
            resetSorter();
        }
    }

    /**
     * 
     * request to sort the column at columnIndex in view coordinates. if there
     * is already an interactive sorter for this column it's sort order is
     * reversed. Otherwise the columns sorter is used as is.
     * Used by headerListener.
     * 
     */
    protected void setSorter(int columnIndex) {
        if (!isSortable())
            return;
        Sorter sorter = getInteractiveSorter();

        if ((sorter != null)
            && (sorter.getColumnIndex() == convertColumnIndexToModel(columnIndex))) {
            sorter.toggle();
        } else {
            TableColumnExt column = getColumnExt(columnIndex);
            getFilters().setSorter(column != null ? column.getSorter() : null);
        }
    }

    /**
     * Returns the interactive sorter if it is set from the given column.
     * Used by ColumnHeaderRenderer.getTableCellRendererComponent().
     * 
     * @param columnIndex the column index in view coordinates.
     * @return the interactive sorter if matches the column or null.
     */
    public Sorter getSorter(int columnIndex) {
        Sorter sorter = getInteractiveSorter();

        return sorter == null ? null
                : sorter.getColumnIndex() == convertColumnIndexToModel(columnIndex) ? sorter
                        : null;
    }

    
//---------------------- enhanced TableColumn/Model support    
    /**
     * Remove all columns, make sure to include hidden.
     * 
     */
    protected void removeColumns() {
        /**
         * @todo promote this method to superclass, and change
         *       createDefaultColumnsFromModel() to call this method
         */
        List columns = getColumns(true);
        for (Iterator iter = columns.iterator(); iter.hasNext();) {
            getColumnModel().removeColumn((TableColumn) iter.next());

        }
    }

    /**
     * returns a list of all visible TableColumns.
     * 
     * @return
     */
    public List getColumns() {
        return Collections.list(getColumnModel().getColumns());
    }

    /**
     * returns a list of TableColumns including hidden if the parameter is set
     * to true.
     * 
     * @param includeHidden
     * @return
     */
    public List getColumns(boolean includeHidden) {
        if (includeHidden && (getColumnModel() instanceof TableColumnModelExt)) {
            return ((TableColumnModelExt) getColumnModel())
                    .getColumns(includeHidden);
        }
        return getColumns();
    }

    /**
     * returns the number of TableColumns including hidden if the parameter is set 
     * to true.
     * 
     * @param includeHidden
     * @return
     */
    public int getColumnCount(boolean includeHidden) {
        if (getColumnModel() instanceof TableColumnModelExt) {
            return ((TableColumnModelExt) getColumnModel())
                    .getColumnCount(includeHidden);
        }
        return getColumnCount();
    }

    /**
     * reorders the columns in the sequence given array. Logical names that do
     * not correspond to any column in the model will be ignored. Columns with
     * logical names not contained are added at the end.
     * 
     * @param columnNames
     *            array of logical column names
     */
    public void setColumnSequence(Object[] identifiers) {
        List columns = getColumns(true);
        Map map = new HashMap();
        for (Iterator iter = columns.iterator(); iter.hasNext();) {
            // PENDING: handle duplicate identifiers ...
            TableColumn column = (TableColumn) iter.next();
            map.put(column.getIdentifier(), column);
            getColumnModel().removeColumn(column);
        }
        for (int i = 0; i < identifiers.length; i++) {
            TableColumn column = (TableColumn) map.get(identifiers[i]);
            if (column != null) {
                getColumnModel().addColumn(column);
                columns.remove(column);
            }
        }
        for (Iterator iter = columns.iterator(); iter.hasNext();) {
            TableColumn column = (TableColumn) iter.next();
            getColumnModel().addColumn(column);
        }
    }

    /**
     * Returns the <code>TableColumnExt</code> object for the column in the
     * table whose identifier is equal to <code>identifier</code>, when
     * compared using <code>equals</code>. The returned TableColumn is
     * guaranteed to be part of the current ColumnModel but may be hidden, that
     * is
     * 
     * <pre> <code>
     * TableColumnExt column = table.getColumnExt(id);
     * if (column != null) {
     *     int viewIndex = table.convertColumnIndexToView(column.getModelIndex());
     *     assertEquals(column.isVisible(), viewIndex &gt;= 0);
     * }
     * </code> </pre>
     * 
     * @param identifier
     *            the identifier object
     * 
     * @return the <code>TableColumnExt</code> object that matches the
     *         identifier or null if none is found.
     */
    public TableColumnExt getColumnExt(Object identifier) {
        if (getColumnModel() instanceof TableColumnModelExt) {
            return ((TableColumnModelExt) getColumnModel())
                    .getColumnExt(identifier);
        } else {
            // PENDING: not tested!
            try {
                TableColumn column = getColumn(identifier);
                if (column instanceof TableColumnExt) {
                    return (TableColumnExt) column;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return null;
    }

    /**
     * Returns the <code>TableColumnExt</code> object for the column in the
     * table whose column index is equal to <code>viewColumnIndex</code>
     * 
     * @param viewColumnIndex
     *            index of the column with the object in question
     * 
     * @return the <code>TableColumnExt</code> object that matches the column
     *         index
     * @exception IllegalArgumentException
     *                if no <code>TableColumn</code> has this identifier
     */
    public TableColumnExt getColumnExt(int viewColumnIndex) {
        return (TableColumnExt) getColumnModel().getColumn(viewColumnIndex);
    }

    public void createDefaultColumnsFromModel() {
        TableModel model = getModel();
        if (model != null) {
            // Create new columns from the data model info
            // Note: it's critical to create the new columns before
            // deleting the old ones. Why?
            // JW PENDING: the reason is somewhere in the early forums - search!
            int modelColumnCount = model.getColumnCount();
            TableColumn newColumns[] = new TableColumn[modelColumnCount];
            for (int i = 0; i < newColumns.length; i++) {
                newColumns[i] = createAndConfigureColumn(model, i);
            }
            // Remove any current columns
            removeColumns();
            // Now add the new columns to the column model
            for (int i = 0; i < newColumns.length; i++) {
                addColumn(newColumns[i]);
            }
        }
    }


    protected TableColumn createAndConfigureColumn(TableModel model,
            int modelColumn) {
        return getColumnFactory().createAndConfigureTableColumn(model,
                modelColumn);
    }

    protected ColumnFactory getColumnFactory() {
        if (columnFactory == null) {
            columnFactory = ColumnFactory.getInstance();
        }
        return columnFactory;
    }



    
//----------------------- delegating methods?? from super    
    /**
     * Returns the margin between columns.
     * 
     * @return the margin between columns
     */
    public int getColumnMargin() {
        return getColumnModel().getColumnMargin();
    }

    /**
     * Sets the margin between columns.
     * 
     * @param value
     *            margin between columns; must be greater than or equal to zero.
     */
    public void setColumnMargin(int value) {
        getColumnModel().setColumnMargin(value);
    }

    /**
     * Returns the selection mode used by this table's selection model.
     * 
     * @return the selection mode used by this table's selection model
     */
    public int getSelectionMode() {
        return getSelectionModel().getSelectionMode();
    }

//----------------------- Search support 


    /** Opens the find widget for the table. */
    private void find() {
        SearchFactory.getInstance().showFindInput(this, getSearchable());
//        if (dialog == null) {
//            dialog = new JXFindDialog();
//        }
//        dialog.setSearchable(this);
//        dialog.setVisible(true);
    }

    /**
     * 
     * @returns a not-null Searchable for this editor.  
     */
    public Searchable getSearchable() {
        if (searchable == null) {
            searchable = new TableSearchable();
        }
        return searchable;
    }

    /**
     * sets the Searchable for this editor. If null, a default 
     * searchable will be used.
     * 
     * @param searchable
     */
    public void setSearchable(Searchable searchable) {
        this.searchable = searchable;
    }

    public class TableSearchable implements Searchable {

        /**
         * Performs a search across the table using String that represents a
         * regex pattern; {@link java.util.regex.Pattern}. All columns and all
         * rows are searched; the row id of the first match is returned.
         */
        public int search(String searchString) {
            return search(searchString, -1);
        }

        /**
         * Performs a search on a column using String that represents a regex
         * pattern; {@link java.util.regex.Pattern}. The specified column
         * searched; the row id of the first match is returned.
         */
        public int search(String searchString, int columnIndex) {
            if (searchString != null) {
                return search(Pattern.compile(searchString, 0), columnIndex);
            }
            return -1;
        }

        /**
         * Performs a search across the table using a
         * {@link java.util.regex.Pattern}. All columns and all rows are
         * searched; the row id of the first match is returned.
         */
        public int search(Pattern pattern) {
            return search(pattern, -1);
        }

        /**
         * Performs a search across the table using a
         * {@link java.util.regex.Pattern}. starting at a given row. All
         * columns and all rows are searched; the row id of the first match is
         * returned.
         */
        public int search(Pattern pattern, int startIndex) {
            return search(pattern, startIndex, false);
        }

        // Save the last column with the match.
        // TODO (JW) - lastCol should either be a valid column index or < 0
        private int lastFoundColumn = -1;
        private int lastFoundRow = -1;
        private MatchResult lastMatchResult;
        private SearchHighlighter searchHighlighter;

        /**
         * Performs a search across the table using a
         * {@link java.util.regex.Pattern}. starting at a given row. All
         * columns and all rows are searched; the row id of the first match is
         * returned.
         * 
         * @param startIndex
         *            row to start search
         * @param backwards
         *            whether to start at the last row and search up to the
         *            first.
         * @return row with a match.
         */
        public int search(Pattern pattern, int startIndex, boolean backwards) {

            int matchingRow = searchMatchingRow(pattern, startIndex, backwards);
            moveMatchMarker(pattern, matchingRow, lastFoundColumn);
            return matchingRow;

        }


        /**
         * returns/updates the matching row/column indices.
         * 
         * @param pattern
         * @param startIndex
         * @param backwards
         * @return
         */
        protected int searchMatchingRow(Pattern pattern, int startIndex, boolean backwards) {
            if (pattern == null) {
                updateStateAfterNotFound();
                return lastFoundRow;
            }
            
//            int start = startIndex;
            if (maybeExtendedMatch(startIndex)) {
                if (foundExtendedMatch(pattern, startIndex)) {
                    return lastFoundRow;
                }
//                start++;
            }
            if (backwards) {
                int matchRow = -1;
                if (startIndex < 0)
                    startIndex = getRowCount();
                int startRow = startIndex - 1;
                for (int r = startRow; r >= 0 && matchRow == -1; r--) {
                    matchRow = findMatchBackwardsInRow(pattern, r);
                }
            } else {
                int matchRow = -1;
                int startRow = startIndex + 1;
                for (int r = startRow; r < getRowCount() && matchRow == -1; r++) {
                    matchRow = findMatchForwardInRow(pattern, r);
                }
            }
            return lastFoundRow;
        }
        
        private boolean foundExtendedMatch(Pattern pattern, int start) {
            boolean foundExtended = false;
            Object value = getValueAt(start, lastFoundColumn);
            if (value != null) {
                Matcher matcher = pattern.matcher(value.toString());
                if (matcher.find()) {
                    MatchResult result = matcher.toMatchResult();
                    if ((result.start() == lastMatchResult.start()) &&
                            !result.group().equals(lastMatchResult.group())) {
                        updateStateAfterFound(matcher, start, lastFoundColumn);
                        foundExtended = true;
                    }
                 }
            }
            return foundExtended;
        }

        /**
         * Checks if the startIndex is a candidate for trying a re-match.
         * 
         * 
         * @param startIndex
         * @return true if the startIndex should be re-matched, false if not.
         */
        private boolean maybeExtendedMatch(final int startIndex) {
            return (startIndex >= 0) && (startIndex == lastFoundRow) && 
               (lastFoundColumn >= 0);
        }

        /**
         * @param pattern
         * @param matchRow
         * @param row
         * @return
         */
        private int findMatchForwardInRow(Pattern pattern, int row) {
            if (lastFoundColumn < 0) lastFoundColumn = 0;
            for (int column = lastFoundColumn; column < getColumnCount(); column++) {
                Object value = getValueAt(row, column);
                if (value != null) {
                    Matcher matcher = pattern.matcher(value.toString());
                    if (matcher.find()) {
                        updateStateAfterFound(matcher, row, column);
                        return row;
                    }
                }
            }
            updateStateAfterNotFound();
            return -1;
        }

       /**
         * @param pattern
         * @param row
         * @return
         */
        private int findMatchBackwardsInRow(Pattern pattern, int row) {
            if (lastFoundColumn < 0) lastFoundColumn = getColumnCount() - 1;
            for (int column = lastFoundColumn; column >= 0; column--) {
                Object value = getValueAt(row, column);
                if (value != null) {
                    Matcher matcher = pattern.matcher(value.toString());
                    if (matcher.find()) {
                        updateStateAfterFound(matcher, row, column);
                        return row;
                    }
                }
            }
            updateStateAfterNotFound();
            return -1;
        }

        /**
         * @param matcher
         * @param r
         * @param c
         */
        private void updateStateAfterFound(Matcher matcher, int r, int c) {
            lastMatchResult = matcher.toMatchResult();
            lastFoundRow = r;
            lastFoundColumn = c;
        }

         /**
         * 
         */
        protected void updateStateAfterNotFound() {
            lastFoundColumn = -1;
            lastFoundRow = -1;
            lastMatchResult = null;
        }
        
        protected void moveMatchMarker(Pattern pattern, int row, int column) {
            if (markByHighlighter()) {
                Rectangle cellRect = getCellRect(row, column, true);
                if (cellRect != null) {
                    scrollRectToVisible(cellRect);
                }
                 ensureInsertedSearchHighlighters();
                 // TODO (JW) - cleanup SearchHighlighter state management
                 if ((row >= 0) && (column >= 0)) { 
                     getSearchHighlighter().setPattern(pattern);
                     int modelColumn = convertColumnIndexToModel(column);
                     getSearchHighlighter().setHighlightCell(row, modelColumn);
                 } else {
                     getSearchHighlighter().setPattern(null);
                 }
            } else { // use selection
                changeSelection(row, column, false, false);
                if (!getAutoscrolls()) {
                    // scrolling not handled by moving selection
                    Rectangle cellRect = getCellRect(row, column, true);
                    if (cellRect != null) {
                        scrollRectToVisible(cellRect);
                    }
                }
            }
        }

        private boolean markByHighlighter() {
            return Boolean.TRUE.equals(getClientProperty(MATCH_HIGHLIGHTER));
        }

        private SearchHighlighter getSearchHighlighter() {
            if (searchHighlighter == null) {
               searchHighlighter = createSearchHighlighter();
            }
            return searchHighlighter;
        }

        private void ensureInsertedSearchHighlighters() {
            if (getHighlighters() == null) {
                setHighlighters(new HighlighterPipeline(new Highlighter[] {getSearchHighlighter()}));
            } else if (!isInPipeline(getSearchHighlighter())){
                getHighlighters().addHighlighter(getSearchHighlighter());
            }
        }

        private boolean isInPipeline(PatternHighlighter searchHighlighter) {
            Highlighter[] inPipeline = getHighlighters().getHighlighters();
            for (int i = 0; i < inPipeline.length; i++) {
                if (searchHighlighter.equals(inPipeline[i])) return true;
            }
            return false;
        }

        protected SearchHighlighter createSearchHighlighter() {
            return new SearchHighlighter();
        }
        


    }
//-------------------------------- sizing support
    
    /** ? */
    public void setVisibleRowCount(int visibleRowCount) {
        this.visibleRowCount = visibleRowCount;
    }

    /** ? */
    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    public Dimension getPreferredScrollableViewportSize() {
        Dimension prefSize = super.getPreferredScrollableViewportSize();

        // JTable hardcodes this to 450 X 400, so we'll calculate it
        // based on the preferred widths of the columns and the
        // visibleRowCount property instead...

        if (prefSize.getWidth() == 450 && prefSize.getHeight() == 400) {
            TableColumnModel columnModel = getColumnModel();
            int columnCount = columnModel.getColumnCount();

            int w = 0;
            for (int i = 0; i < columnCount; i++) {
                TableColumn column = columnModel.getColumn(i);
                initializeColumnPreferredWidth(column);
                w += column.getPreferredWidth();
            }
            prefSize.width = w;
            JTableHeader header = getTableHeader();
            // remind(aim): height is still off...???
            int rowCount = getVisibleRowCount();
            prefSize.height = rowCount * getRowHeight()
                    + (header != null ? header.getPreferredSize().height : 0);
            setPreferredScrollableViewportSize(prefSize);
        }
        return prefSize;
    }

    /**
     * Packs all the columns to their optimal size. Works best with auto
     * resizing turned off.
     * 
     * Contributed by M. Hillary (Issue #60)
     * 
     * @param margin
     *            the margin to apply to each column.
     */
    public void packTable(int margin) {
        for (int c = 0; c < getColumnCount(); c++)
            packColumn(c, margin, -1);
    }

    /**
     * Packs an indivudal column in the table. Contributed by M. Hillary (Issue
     * #60)
     * 
     * @param column
     *            The Column index to pack in View Coordinates
     * @param margin
     *            The Margin to apply to the column width.
     */
    public void packColumn(int column, int margin) {
        packColumn(column, margin, -1);
    }

    /**
     * Packs an indivual column in the table to less than or equal to the
     * maximum witdth. If maximun is -1 then the column is made as wide as it
     * needs. Contributed by M. Hillary (Issue #60)
     * 
     * @param column
     *            The Column index to pack in View Coordinates
     * @param margin
     *            The margin to apply to the column
     * @param max
     *            The maximum width the column can be resized to. -1 mean any
     *            size.
     */
    public void packColumn(int column, int margin, int max) {
        getColumnFactory().packColumn(this, getColumnExt(column), margin, max);
    }

    /**
     * Initialize the preferredWidth of the specified column based on the
     * column's prototypeValue property. If the column is not an instance of
     * <code>TableColumnExt</code> or prototypeValue is <code>null</code>
     * then the preferredWidth is left unmodified.
     * 
     * @see org.jdesktop.swingx.table.TableColumnExt#setPrototypeValue
     * @param column
     *            TableColumn object representing view column
     */
    protected void initializeColumnPreferredWidth(TableColumn column) {
        if (column instanceof TableColumnExt) {
            getColumnFactory().configureColumnWidths(this,
                    (TableColumnExt) column);
        }
    }

    
//----------------------------------- uniform data model access
    
    protected ComponentAdapter getComponentAdapter() {
        if (dataAdapter == null) {
            dataAdapter = new TableAdapter(this);
        }
        return dataAdapter;
    }

    
    protected static class TableAdapter extends ComponentAdapter {
        private final JXTable table;

        /**
         * Constructs a <code>TableDataAdapter</code> for the specified target
         * component.
         * 
         * @param component
         *            the target component
         */
        public TableAdapter(JXTable component) {
            super(component);
            table = component;
        }

        /**
         * Typesafe accessor for the target component.
         * 
         * @return the target component as a {@link javax.swing.JTable}
         */
        public JXTable getTable() {
            return table;
        }


        public String getColumnName(int columnIndex) {
//            TableColumnModel columnModel = table.getColumnModel();
//            if (columnModel == null) {
//                return "Column " + columnIndex;
//            }
//            int viewColumn = modelToView(columnIndex);
//            TableColumn column = null;
//            if (viewColumn >= 0) {
//                column = columnModel.getColumn(viewColumn);
//            }
            TableColumn column = getColumnByModelIndex(columnIndex);
            return column == null ? "" : column.getHeaderValue().toString();
        }

        protected TableColumn getColumnByModelIndex(int modelColumn) {
            List columns = table.getColumns(true);
            for (Iterator iter = columns.iterator(); iter.hasNext();) {
                TableColumn column = (TableColumn) iter.next();
                if (column.getModelIndex() == modelColumn) {
                    return column;
                }
            }
            return null;
        }

        
        public String getColumnIdentifier(int columnIndex) {
            
            TableColumn column = getColumnByModelIndex(columnIndex);
//            int viewColumn = modelToView(columnIndex);
//            if (viewColumn >= 0) {
//                table.getColumnExt(viewColumn).getIdentifier();
//            }
            Object identifier = column != null ? column.getIdentifier() : null;
            return identifier != null ? identifier.toString() : null;
        }
        
        public int getColumnCount() {
            return table.getModel().getColumnCount();
        }

        public int getRowCount() {
            return table.getModel().getRowCount();
        }

        /**
         * {@inheritDoc}
         */
        public Object getValueAt(int row, int column) {
            // RG: eliminate superfluous back-and-forth conversions
            return table.getModel().getValueAt(row, column);
        }

        public void setValueAt(Object aValue, int row, int column) {
            // RG: eliminate superfluous back-and-forth conversions
            table.getModel().setValueAt(aValue, row, column);
        }

        public boolean isCellEditable(int row, int column) {
            // RG: eliminate superfluous back-and-forth conversions
            return table.getModel().isCellEditable(row, column);
        }

        
        
        public boolean isTestable(int column) {
            return getColumnByModelIndex(column) != null;
        }
//-------------------------- accessing view state/values
        
        public Object getFilteredValueAt(int row, int column) {
            return table.getValueAt(row, modelToView(column)); // in view coordinates
        }

        /**
         * {@inheritDoc}
         */
        public boolean isSelected() {
            return table.isCellSelected(row, column);
        }
        /**
         * {@inheritDoc}
         */
        public boolean hasFocus() {
            boolean rowIsLead = (table.getSelectionModel()
                    .getLeadSelectionIndex() == row);
            boolean colIsLead = (table.getColumnModel().getSelectionModel()
                    .getLeadSelectionIndex() == column);
            return table.isFocusOwner() && (rowIsLead && colIsLead);
        }

        /**
         * {@inheritDoc}
         */
        public int modelToView(int columnIndex) {
            return table.convertColumnIndexToView(columnIndex);
        }

        /**
         * {@inheritDoc}
         */
        public int viewToModel(int columnIndex) {
            return table.convertColumnIndexToModel(columnIndex);
        }


    }

 
//--------------------- managing renderers/editors
    
    /** Returns the HighlighterPipeline assigned to the table, null if none. */
    public HighlighterPipeline getHighlighters() {
        return highlighters;
    }

    /**
     * Assigns a HighlighterPipeline to the table. bound property.
     */
    public void setHighlighters(HighlighterPipeline pipeline) {
        HighlighterPipeline old = getHighlighters();
        if (old != null) {
            old.removeChangeListener(getHighlighterChangeListener());
        }
        highlighters = pipeline;
        if (highlighters != null) {
            highlighters.addChangeListener(getHighlighterChangeListener());
        }
        firePropertyChange("highlighters", old, getHighlighters());
    }

    /**
     * returns the ChangeListener to use with highlighters. Creates one if
     * necessary.
     * 
     * @return != null
     */
    private ChangeListener getHighlighterChangeListener() {
        if (highlighterChangeListener == null) {
            highlighterChangeListener = new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    repaint();

                }

            };
        }
        return highlighterChangeListener;
    }


    
    /**
     * Returns the decorated <code>Component</code> used as a stamp to render
     * the specified cell. Overrides superclass version to provide support for
     * cell decorators.
     * 
     * @param renderer
     *            the <code>TableCellRenderer</code> to prepare
     * @param row
     *            the row of the cell to render, where 0 is the first row
     * @param column
     *            the column of the cell to render, where 0 is the first column
     * @return the decorated <code>Component</code> used as a stamp to render
     *         the specified cell
     * @see org.jdesktop.swingx.decorator.Highlighter
     */
    public Component prepareRenderer(TableCellRenderer renderer, int row,
            int column) {
        // JW PENDING: testing cadavers ... check if still needed  
//        Object value = getValueAt(row, column);
//        Class columnClass = getColumnClass(column);
//        boolean typeclash = !columnClass.isInstance(value);
//        TableColumn tableColumn = getColumnModel().getColumn(column);
//        getColumnClass(column);
        Component stamp = super.prepareRenderer(renderer, row, column);
        adjustComponentOrientation(stamp);
        if (highlighters == null) {
            return stamp; // no need to decorate renderer with highlighters
        } else {
            // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
            ComponentAdapter adapter = getComponentAdapter();
            adapter.row = row;
            adapter.column = column;
            return highlighters.apply(stamp, adapter);
        }
    }

    

    private void adjustComponentOrientation(Component stamp) {
        if (stamp.getComponentOrientation().equals(getComponentOrientation())) return;
        stamp.applyComponentOrientation(getComponentOrientation());
        
    }

    /**
     * Returns a new instance of the default renderer for the specified class.
     * This differs from <code>getDefaultRenderer()</code> in that it returns
     * a <b>new </b> instance each time so that the renderer may be set and
     * customized on a particular column.
     * 
     * @param columnClass
     *            Class of value being rendered
     * @return TableCellRenderer instance which renders values of the specified
     *         type
     */
    public TableCellRenderer getNewDefaultRenderer(Class columnClass) {
        TableCellRenderer renderer = getDefaultRenderer(columnClass);
        if (renderer != null) {
            try {
                return (TableCellRenderer) renderer.getClass().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** ? */
    protected void createDefaultEditors() {
        super.createDefaultEditors();
        setLazyEditor(LinkModel.class, "org.jdesktop.swingx.LinkRenderer");
    }

    /**
     * Creates default cell renderers for objects, numbers, doubles, dates,
     * booleans, icons, and links.
     * THINK: delegate to TableCellRenderers?
     * 
     */
    protected void createDefaultRenderers() {
        // super.createDefaultRenderers();
        // This duplicates JTable's functionality in order to make the renderers
        // available in getNewDefaultRenderer(); If JTable's renderers either
        // were public, or it provided a factory for *new* renderers, this would
        // not be needed

        defaultRenderersByColumnClass = new UIDefaults();

        // Objects
        setLazyRenderer(Object.class,
                "javax.swing.table.DefaultTableCellRenderer");

        // Numbers
        setLazyRenderer(Number.class,
                "org.jdesktop.swingx.JXTable$NumberRenderer");

        // Doubles and Floats
        setLazyRenderer(Float.class,
                "org.jdesktop.swingx.JXTable$DoubleRenderer");
        setLazyRenderer(Double.class,
                "org.jdesktop.swingx.JXTable$DoubleRenderer");

        // Dates
        setLazyRenderer(Date.class, "org.jdesktop.swingx.JXTable$DateRenderer");

        // Icons and ImageIcons
        setLazyRenderer(Icon.class, "org.jdesktop.swingx.JXTable$IconRenderer");
        setLazyRenderer(ImageIcon.class,
                "org.jdesktop.swingx.JXTable$IconRenderer");

        // Booleans
        setLazyRenderer(Boolean.class,
                "org.jdesktop.swingx.JXTable$BooleanRenderer");

        // Other
        setLazyRenderer(LinkModel.class, "org.jdesktop.swingx.LinkRenderer");
    }


    /** ? */
    private void setLazyValue(Hashtable h, Class c, String s) {
        h.put(c, new UIDefaults.ProxyLazyValue(s));
    }

    /** ? */
    private void setLazyRenderer(Class c, String s) {
        setLazyValue(defaultRenderersByColumnClass, c, s);
    }

    /** ? */
    private void setLazyEditor(Class c, String s) {
        setLazyValue(defaultEditorsByColumnClass, c, s);
    }

    /*
     * Default Type-based Renderers: JTable's default table cell renderer
     * classes are private and JTable:getDefaultRenderer() returns a *shared*
     * cell renderer instance, thus there is no way for us to instantiate a new
     * instance of one of its default renderers. So, we must replicate the
     * default renderer classes here so that we can instantiate them when we
     * need to create renderers to be set on specific columns.
     */
    public static class NumberRenderer extends DefaultTableCellRenderer {
        public NumberRenderer() {
            super();
            setHorizontalAlignment(JLabel.TRAILING);
        }
    }

    public static class DoubleRenderer extends NumberRenderer {
        NumberFormat formatter;

        public DoubleRenderer() {
            super();
        }

        public void setValue(Object value) {
            if (formatter == null) {
                formatter = NumberFormat.getInstance();
            }
            setText((value == null) ? "" : formatter.format(value));
        }
    }

    public static class DateRenderer extends DefaultTableCellRenderer {
        DateFormat formatter;

        public DateRenderer() {
            super();
        }

        public void setValue(Object value) {
            if (formatter == null) {
                formatter = DateFormat.getDateInstance();
            }
            setText((value == null) ? "" : formatter.format(value));
        }
    }

    public static class IconRenderer extends DefaultTableCellRenderer {
        public IconRenderer() {
            super();
            setHorizontalAlignment(JLabel.CENTER);
        }

        public void setValue(Object value) {
            setIcon((value instanceof Icon) ? (Icon) value : null);
        }
    }

    public static class BooleanRenderer extends JCheckBox implements
            TableCellRenderer {
        public BooleanRenderer() {
            super();
            setHorizontalAlignment(JLabel.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            setSelected((value != null && ((Boolean) value).booleanValue()));
            return this;
        }
    }

//---------------------------- updateUI support
    
    /**
     * bug fix: super doesn't update all renderers/editors.
     */
    public void updateUI() {
        super.updateUI();
        // JW PENDING: update columnControl
        if (columnControlButton != null) {
            columnControlButton.updateUI();
        }
        for (Enumeration defaultEditors = defaultEditorsByColumnClass
                .elements(); defaultEditors.hasMoreElements();) {
            updateEditorUI(defaultEditors.nextElement());
        }

        for (Enumeration defaultRenderers = defaultRenderersByColumnClass
                .elements(); defaultRenderers.hasMoreElements();) {
            updateRendererUI(defaultRenderers.nextElement());
        }
        Enumeration columns = getColumnModel().getColumns();
        if (getColumnModel() instanceof TableColumnModelExt) {
            columns = Collections
                    .enumeration(((TableColumnModelExt) getColumnModel())
                            .getAllColumns());
        }
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            updateEditorUI(column.getCellEditor());
            updateRendererUI(column.getCellRenderer());
            updateRendererUI(column.getHeaderRenderer());
        }
        updateRowHeightUI(true);
        configureViewportBackground();
    }

    /** ? */
    private void updateRowHeightUI(boolean respectRowSetFlag) {
        if (respectRowSetFlag && isXTableRowHeightSet)
            return;
        int minimumSize = getFont().getSize() + 6;
        int uiSize = UIManager.getInt(UIPREFIX + "rowHeight");
        setRowHeight(Math.max(minimumSize, uiSize != 0 ? uiSize : 18));
        isXTableRowHeightSet = false;
    }

    /** Changes the row height for all rows in the table. */
    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
        if (rowHeight > 0) {
            isXTableRowHeightSet = true;
        }
        getRowSizing().setViewSizeSequence(getSuperRowModel(), getRowHeight());

    }

    
    public void setRowHeight(int row, int rowHeight) {
        if (!isRowHeightEnabled()) return;
        super.setRowHeight(row, rowHeight);
        getRowSizing().setViewSizeSequence(getSuperRowModel(), getRowHeight());
        resizeAndRepaint();
    }

    /**
     * sets enabled state of individual rowHeight support. The default 
     * is false.
     * Enabling the support envolves reflective access
     * to super's private field rowModel which may fail due to security
     * issues. If failing the support is not enabled.
     * 
     * PENDING: should we throw an Exception if the enabled fails? 
     * Or silently fail - depends on runtime context, 
     * can't do anything about it.
     * 
     * @param enabled
     */
    public void setRowHeightEnabled(boolean enabled) {
        boolean old = isRowHeightEnabled();
        if (old == enabled) return;
        if (enabled && !canEnableRowHeight()) return;
        rowHeightEnabled = enabled;
        if (!enabled) {
            adminSetRowHeight(getRowHeight());
        }
//        getRowSizing().setViewSizeSequence(getSuperRowModel(), getRowHeight());
        firePropertyChange("rowHeightEnabled", old, rowHeightEnabled);
    }
    
    private boolean canEnableRowHeight() {
        return getRowModelField() != null;
    }

    public boolean isRowHeightEnabled() {
        return rowHeightEnabled;
    }

    private SizeSequence getSuperRowModel() {
        try {
            Field field = getRowModelField();
            if (field != null) {
                return (SizeSequence) field.get(this);
            }
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return
     * @throws NoSuchFieldException
     */
    private Field getRowModelField() {
        if (rowModelField == null) {
            try {
                rowModelField = JTable.class.getDeclaredField("rowModel");
                rowModelField.setAccessible(true);
            } catch (SecurityException e) {
                rowModelField = null;
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return rowModelField;
    }
    
    /**
     * 
     * @return
     */
    protected RowSizing getRowSizing() {
        if (rowSizing == null) {
            rowSizing = new RowSizing(filters);
        }
        return rowSizing;
    }

    /**
     * calling setRowHeight for internal reasons.
     * Keeps the isXTableRowHeight unchanged.
     */
    protected void adminSetRowHeight(int rowHeight) {
        boolean heightSet = isXTableRowHeightSet;
        setRowHeight(rowHeight); 
        isXTableRowHeightSet = heightSet;
    }


    private void updateEditorUI(Object value) {
        // maybe null or proxyValue
        if (!(value instanceof TableCellEditor))
            return;
        // super handled this
        if ((value instanceof JComponent)
                || (value instanceof DefaultCellEditor))
            return;
        // custom editors might balk about fake rows/columns
        try {
            Component comp = ((TableCellEditor) value)
                    .getTableCellEditorComponent(this, null, false, -1, -1);
            if (comp instanceof JComponent) {
                ((JComponent) comp).updateUI();
            }
        } catch (Exception e) {
            // ignore - can't do anything
        }
    }

    /** ? */
    private void updateRendererUI(Object value) {
        // maybe null or proxyValue
        if (!(value instanceof TableCellRenderer))
            return;
        // super handled this
        if (value instanceof JComponent)
            return;
        // custom editors might balk about fake rows/columns
        try {
            Component comp = ((TableCellRenderer) value)
                    .getTableCellRendererComponent(this, null, false, false,
                            -1, -1);
            if (comp instanceof JComponent) {
                ((JComponent) comp).updateUI();
            }
        } catch (Exception e) {
            // ignore - can't do anything
        }
    }


    
//---------------------------- overriding super factory methods and buggy
    /**
     * workaround bug in JTable. (Bug Parade ID #6291631 - negative y is mapped
     * to row 0).
     */
    public int rowAtPoint(Point point) {
        if (point.y < 0)
            return -1;
        return super.rowAtPoint(point);
    }

    
    /** ? */
    protected JTableHeader createDefaultTableHeader() {
        return new JXTableHeader(columnModel);
    }

    /** ? */
    protected TableColumnModel createDefaultColumnModel() {
        return new DefaultTableColumnModelExt();
    }

    // /**
    // * Returns the default table model object, which is
    // * a <code>DefaultTableModel</code>. A subclass can override this
    // * method to return a different table model object.
    // *
    // * @return the default table model object
    // * @see DefaultTableColumnModelExt
    // */
    // protected TableModel createDefaultDataModel() {
    // return new DefaultTableModelExt();
    // }

    
    
}
