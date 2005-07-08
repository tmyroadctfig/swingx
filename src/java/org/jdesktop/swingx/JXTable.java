/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
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
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
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
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PipelineEvent;
import org.jdesktop.swingx.decorator.PipelineListener;
import org.jdesktop.swingx.decorator.Sorter;
import org.jdesktop.swingx.icon.ColumnControlIcon;
import org.jdesktop.swingx.plaf.JXTableAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;


/**
 * <p>A JXTable is a JTable with built-in support for row sorting, filtering, and highlighting, column visibility
 * and a special popup control on the column header for quick access to table configuration.
 * You can instantiate a JXTable just as you would a JTable, using a TableModel. However, a JXTable automatically 
 * wraps TableColumns inside a TableColumnExt instance. TableColumnExt supports visibility, sortability, and 
 * prototype values for column sizing, none of which are available in TableColumn. You can retrieve the TableColumnExt
 * instance for a column using {@link #getColumnExt(Object)} or {@link #getColumnExt(int colnumber)}.
 *
 * <p>A JXTable is, by default, sortable by clicking on column headers; each subsequent click on a header reverses
 * the order of the sort, and a sort arrow icon is automatically drawn on the header. Sorting can be disabled using
 * {@link #setSortable(boolean)}. Sorting on columns is handled by a Sorter instance which contains a Comparator
 * used to compare values in two rows of a column. You can replace the Comparator for a given column by using
 * <code>getColumnExt("column").getSorter().setComparator(customComparator)</code>
 *
 * <p>Columns can be hidden or shown by setting the visible property on the TableColumnExt using 
 * {@link TableColumnExt#setVisible(boolean)}. Columns can also be shown or hidden from the column control popup.
 *
 * <p>The column control popup is triggered by an icon drawn to the far right of the column headers, above the
 * table's scrollbar (when installed in a JScrollPane). The popup allows the user to select which columns should
 * be shown or hidden, as well as to pack columns and turn on horizontal scrolling. To show or hide the column control,
 * use the {@link #setColumnControlVisible(boolean show)} method.
 *
 * <p>Rows can be filtered from a JXTable using a Filter class and a FilterPipeline. One assigns a FilterPipeline to 
 * the table using {@link #setFilters(FilterPipeline)}. Filtering hides, but does not delete or permanently remove rows 
 * from a JXTable. Filters are used to provide sorting to the table--rows are not removed, but the table is made
 * to believe rows in the model are in a sorted order.
 *
 * <p>One can automatically highlight certain rows in a JXTable by attaching Highlighters in the 
 * {@link #setHighlighters(HighlighterPipeline)} method. An example would be a Highlighter that colors alternate
 * rows in the table for readability; AlternateRowHighlighter does this. Again, like Filters, Highlighters can
 * be chained together in a HighlighterPipeline to achieve more interesting effects. 
 *
 * <p>You can resize all columns, selected columns, or a single column using the methods like {@link #packAll()}.
 * Packing combines several other aspects of a JXTable. If horizontal scrolling is enabled using 
 * {@link #setHorizontalScrollEnabled(boolean)}, then the scrollpane will allow the table to scroll right-left, and
 * columns will be sized to their preferred size. To control the preferred sizing of a column, you can provide
 * a prototype value for the column in the TableColumnExt using {@link TableColumnExt#setPrototypeValue(Object)}.
 * The prototype is used as an indicator of the preferred size of the column. This can be useful if some data in
 * a given column is very long, but where the resize algorithm would normally not pick this up.
 *
 * <p>Last, you can also provide searches on a JXTable using the search methods.
 *
 * @author Ramesh Gupta
 * @author Amy Fowler
 * @author Mark Davidson
 * @author Jeanette Winzenburg
 */
public class JXTable extends JTable implements Searchable {
    /** Constant string for horizontal scroll actions, used in JXTable's Action Map. */
    public static final String HORIZONTALSCROLL_ACTION_COMMAND = ColumnControlButton.COLUMN_CONTROL_MARKER + "horizontalScroll";

    /** Constant string for packing all columns, used in JXTable's Action Map. */
    public static final String PACKALL_ACTION_COMMAND = ColumnControlButton.COLUMN_CONTROL_MARKER + "packAll";

    /** Constant string for packing selected columns, used in JXTable's Action Map. */
    public static final String PACKSELECTED_ACTION_COMMAND = ColumnControlButton.COLUMN_CONTROL_MARKER + "packSelected";
    
    /** TODO */
    public static final String UIPREFIX = "JXTable.";
    
    static {
        // Hack: make sure the resource bundle is loaded
        LookAndFeelAddons.contribute(new JXTableAddon());
    }

    public static boolean TRACE = false;
  
    /** The sorter attached to this table for the column currently being sorted. */
    protected Sorter            sorter = null;
    
    /** The FilterPipeline for the table. */
    protected FilterPipeline        filters = null;

    /** The HighlighterPipeline for the table. */
    protected HighlighterPipeline   highlighters = null;

    // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
    private final ComponentAdapter dataAdapter = new TableAdapter(this);

    private boolean sortable = false;
    private int visibleRowCount = 18;

    /**
     * flag to indicate if the column control is visible.
     */
    private boolean columnControlVisible;
    /**
     * A button that allows the user to select which columns to display, and
     * which to hide
     */
    private JComponent columnControlButton;

    /**
     * ScrollPane's original vertical scroll policy.
     * If the columnControl is visible the policy is set to
     * ALWAYS.
     */
    private int verticalScrollPolicy;

    /**
     * Mouse/Motion/Listener keeping track of mouse moved in
     * cell coordinates.
     */
    private RolloverProducer rolloverProducer;

    /**
     * RolloverController: listens to cell over events and
     * repaints entered/exited rows.
     */
    private LinkController linkController;

    private int oldAutoResizeMode;
    protected boolean isXTableRowHeightSet;

    /** Instantiates a JXTable with a default table model, no data. */
    public JXTable() {
        init();
    }

    /** 
     * Instantiates a JXTable with a specific table model.
     * @param dm The model to use.
     */
    public JXTable(TableModel dm) {
        super(dm);
        init();
    }

    /** 
     * Instantiates a JXTable with a specific table model.
     * @param dm The model to use.
     */
    public JXTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        init();
    }

    /** 
     * Instantiates a JXTable with a specific table model, column model, and selection model.
     * @param dm The table model to use.
     * @param cm The colomn model to use.
     * @param sm The list selection model to use.
     */
    public JXTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        init();
    }

    /** 
     * Instantiates a JXTable for a given number of columns and rows.
     * @param numRows Count of rows to accomodate.
     * @param numColumns Count of columns to accomodate.
     */
    public JXTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        init();
    }

    /** 
     * Instantiates a JXTable with data in a vector or rows and column names.
     * @param rowData Row data, as a Vector of Objects.
     * @param columnNames Column names, as a Vector of Strings.
     */
    public JXTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
        init();
    }

    /** 
     * Instantiates a JXTable with data in a array or rows and column names.
     * @param rowData Row data, as a two-dimensional Array of Objects (by row, for column).
     * @param columnNames Column names, as a Array of Strings.
     */
    public JXTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        init();
    }

    /** Initializes the table for use. */
    protected void init() {
        setSortable(true);
        // Register the actions that this class can handle.
        ActionMap map = getActionMap();
        map.put("print", new Actions("print"));
        map.put("find", new Actions("find"));
        map.put(PACKALL_ACTION_COMMAND, createPackAllAction());//new Actions("packAll"));
        map.put(PACKSELECTED_ACTION_COMMAND, createPackSelectedAction());//new Actions("packSelected"));
        map.put(HORIZONTALSCROLL_ACTION_COMMAND, createHorizontalScrollAction());
        updateRowHeightUI(false);
    }

    /** Creates an Action for horizontal scrolling. */
    private Action createHorizontalScrollAction() {
        String actionName = getUIString(HORIZONTALSCROLL_ACTION_COMMAND);
        BoundAction action = new BoundAction(actionName, HORIZONTALSCROLL_ACTION_COMMAND);
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
    
    /**
     * This resizes all columns to fit the viewport; if horizontal scrolling
     * is enabled, all columns will get their preferred width. This can be
     * triggered by the "packAll" BoundAction on the table as well.
     */
    public void packAll() {
        packTable(getDefaultPackMargin());
    }

    /**
     * This resizes selected columns to fit the viewport; if horizontal scrolling
     * is enabled, selected columns will get their preferred width. This can be
     * triggered by the "packSelected" BoundAction on the table as well.
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
     * @param enabled If true, the scrollpane will allow the table to scroll
     * horizontally, and columns will resize to their preferred width. If false,
     * columns will resize to fit the viewport.
     */
    public void setHorizontalScrollEnabled(boolean enabled) {
        if (enabled == (isHorizontalScrollEnabled())) return;
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

    /** Notifies the table that a new column has been selected. */
    public void columnSelectionChanged(ListSelectionEvent e) {
        super.columnSelectionChanged(e);
        if (e.getValueIsAdjusting()) return;
        Action packSelected = getActionMap().get(PACKSELECTED_ACTION_COMMAND);
        if ((packSelected != null)) {// && (e.getSource() instanceof ListSelectionModel)){
           packSelected.setEnabled(!((ListSelectionModel) e.getSource()).isSelectionEmpty()); 
        }
    }

    /** ? */
    public void setAutoResizeMode(int mode) {
        super.setAutoResizeMode(mode);
        Action packSelected = getActionMap().get(HORIZONTALSCROLL_ACTION_COMMAND);
        if (packSelected instanceof BoundAction) {
           ((BoundAction) packSelected).setSelected(isHorizontalScrollEnabled());
        }
    }
    
    /**
     * Property to enable/disable rollover support. This can be enabled
     * to show "live" rollover behaviour, f.i. the cursor over LinkModel cells. 
     * Default is disabled. If using a RolloverHighlighter on the table, 
     * this should be set to true.
     *
     * @param rolloverEnabled
     */
    public void setRolloverEnabled(boolean rolloverEnabled) {
        boolean old = isRolloverEnabled();
        if (rolloverEnabled == old) return;
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
     * @return <code>true</code> if rollover is enabled
     */
    public boolean isRolloverEnabled() {
        return rolloverProducer != null;
    }

    /**
     * If the default editor for LinkModel.class is of type
     * LinkRenderer  enables link visiting with the given linkVisitor.
     * As a side-effect the rollover property is set to true.
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
    
    /**
     * overridden to addionally configure the upper right corner 
     * of an enclosing scrollpane with the ColumnControl.
     */
    protected void configureEnclosingScrollPane() {
        super.configureEnclosingScrollPane();
        configureColumnControl();
        configureViewportBackground();
    }

    /** 
     * set's the viewports background to this.background.
     * PENDING: need to repeat on background changes to this!
     *
     */
    protected void configureViewportBackground() {
        Container p = getParent();
        if (p instanceof JViewport) {
            p.setBackground(getBackground());
        }
    }

    /**
     * configure the upper right corner of an enclosing scrollpane
     * with/o the ColumnControl, depending on setting of 
     * columnControl visibility flag.
     *
     * PENDING: should choose corner depending on 
     * component orientation.
     */
    private void configureColumnControl() {
        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane)gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this) {
                    return;
                }
                if (isColumnControlVisible()) {
                    verticalScrollPolicy = scrollPane.getVerticalScrollBarPolicy();
                    scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, getColumnControl());
                    
                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                } else {
                    scrollPane.setVerticalScrollBarPolicy(verticalScrollPolicy == 0 ? 
                            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED : verticalScrollPolicy);
                    
                    try {
                        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, null);
                    }
                    catch (Exception ex) {
                        // Ignore spurious exception thrown by JScrollPane. This is a Swing bug!
                    }
                    
                }
            }
        }
    }

    /**
     * returns visibility flag of column control. <p>
     * 
     * Note: if the table is not inside a JScrollPane
     * the column control is not shown even if this returns true. 
     * In this case it's the responsibility of the client code 
     * to actually show it.
     * 
     * @return
     */
    public boolean isColumnControlVisible() {
        return columnControlVisible;
    }

    /**
     * returns the component for column control.
     * @return
     */
    public JComponent getColumnControl() {
        if (columnControlButton == null) {
            columnControlButton = new ColumnControlButton(this, new ColumnControlIcon());
        }
        return columnControlButton;
    }

    /**
     * bound property to flag visibility state of column control.
     * @param showColumnControl
     */
    public void setColumnControlVisible(boolean showColumnControl) {
        boolean old = columnControlVisible;
        this.columnControlVisible = showColumnControl;
        // JW: hacking issue #38(swingx) to initially add all columns
//        if (showColumnControl) {
//            getColumnControl();
//        }
        configureColumnControl();
        firePropertyChange("columnControlVisible", old, columnControlVisible);
    }
    
    /**
     * Returns a new instance of the default renderer for the specified class.
     * This differs from <code>getDefaultRenderer()</code> in that it returns a <b>new</b>
     * instance each time so that the renderer may be set and customized on
     * a particular column.
     *
     * @param columnClass Class of value being rendered
     * @return TableCellRenderer instance which renders values of the specified type
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

    /** ? */
    protected void createDefaultEditors() {
        super.createDefaultEditors();
        setLazyEditor(LinkModel.class, "org.jdesktop.swingx.LinkRenderer");
    }
    /**
     * Creates default cell renderers for objects, numbers, doubles, dates,
     * booleans, icons, and links.
     *
     */
    protected void createDefaultRenderers() {
        // super.createDefaultRenderers();
        // This duplicates JTable's functionality in order to make the renderers
        // available in getNewDefaultRenderer();  If JTable's renderers either
        // were public, or it provided a factory for *new* renderers, this would
        // not be needed

        defaultRenderersByColumnClass = new UIDefaults();

        // Objects
        setLazyRenderer(Object.class,
                        "javax.swing.table.DefaultTableCellRenderer");

        // Numbers
        setLazyRenderer(Number.class, "org.jdesktop.swingx.JXTable$NumberRenderer");

        // Doubles and Floats
        setLazyRenderer(Float.class, "org.jdesktop.swingx.JXTable$DoubleRenderer");
        setLazyRenderer(Double.class, "org.jdesktop.swingx.JXTable$DoubleRenderer");

        // Dates
        setLazyRenderer(Date.class, "org.jdesktop.swingx.JXTable$DateRenderer");

        // Icons and ImageIcons
        setLazyRenderer(Icon.class, "org.jdesktop.swingx.JXTable$IconRenderer");
        setLazyRenderer(ImageIcon.class, "org.jdesktop.swingx.JXTable$IconRenderer");

        // Booleans
        setLazyRenderer(Boolean.class, "org.jdesktop.swingx.JXTable$BooleanRenderer");

        // Other
        setLazyRenderer(LinkModel.class, "org.jdesktop.swingx.LinkRenderer");
    }

    
    /**
     * bug fix: super doesn't update all renderers/editors.
     */
    public void updateUI() {
        super.updateUI();
        // JW PENDING: update columnControl
        if (columnControlButton != null) {
            columnControlButton.updateUI();
        }
        for (Enumeration defaultEditors= defaultEditorsByColumnClass.elements(); defaultEditors.hasMoreElements();) {
          updateEditorUI(defaultEditors.nextElement());
        }
        
        for (Enumeration defaultRenderers = defaultRenderersByColumnClass.elements();defaultRenderers.hasMoreElements();) {
          updateRendererUI(defaultRenderers.nextElement());
        }
        Enumeration columns = getColumnModel().getColumns();
        if (getColumnModel() instanceof TableColumnModelExt) {
            columns = Collections.enumeration(((TableColumnModelExt) getColumnModel()).getAllColumns());
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
        if (respectRowSetFlag && isXTableRowHeightSet) return;
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
        
    }
    
    /**
     * this is a Hack: does not set the isXTableRowHeight flag.
     * @param rowHeight
     */
//    protected void setRowHeightInternally(int rowHeight) {
//        super.setRowHeight(rowHeight);
//    }

    private void updateEditorUI(Object value) {
        // maybe null or proxyValue
        if (!(value instanceof TableCellEditor)) return;
        // super handled this
        if ((value instanceof JComponent) || (value instanceof DefaultCellEditor)) return;
        // custom editors might balk about fake rows/columns
        try {
            Component comp = ((TableCellEditor) value).getTableCellEditorComponent(this, null, false, -1, -1);
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
        if (!(value instanceof TableCellRenderer)) return;
        // super handled this
        if (value instanceof JComponent) return;
        // custom editors might balk about fake rows/columns
        try {
            Component comp = ((TableCellRenderer) value).getTableCellRendererComponent(this, null, false, false, -1, -1);
            if (comp instanceof JComponent) {
                ((JComponent) comp).updateUI();
            }
        } catch (Exception e) {
            // ignore - can't do anything
        }
    }
    
    /**
     * A small class which dispatches actions.
     * TODO: Is there a way that we can make this static?
     * JW: I hate those if constructs... we are in OO-land!
     */
    private class Actions extends UIAction {
        Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
            if ("print".equals(getName())) {
                try {
                    print();
                }
                catch (PrinterException ex) {
                    //REMIND(aim): should invoke pluggable application error handler
                    ex.printStackTrace();
                }
            }
            else if ("find".equals(getName())) {
                find();
            } 
//            else if (PACKALL_ACTION_COMMAND.equals(getName())) {
//                packAll();
//            } else if (PACKSELECTED_ACTION_COMMAND.equals(getName())) {
//                packSelected();
//            }
        }


    }

    /** The JXFindDialog we open on find() */
    private JXFindDialog dialog = null;

    /** ? */
    private boolean automaticSortDisabled;

    /** Listens for changes from the filters. */
    private PipelineListener pipelineListener;

    /** Listens for changes from the highlighters. */
    private ChangeListener highlighterChangeListener;

    /** the factory to use for column creation and configuration. */
    private ColumnFactory columnFactory;

    /** Opens the JXFindDialog for the table. */
    private void find() {
        if (dialog == null) {
            dialog = new JXFindDialog(this);
        }
        dialog.setVisible(true);
    }

    /**
     * Sets &quot;sortable&quot; property indicating whether or not this table
         * supports sortable columns.  If <code>sortable</code> is <code>true</code>
     * then sorting will be enabled on all columns whose <code>sortable</code>
         * property is <code>true</code>.  If <code>sortable</code> is <code>false</code>
         * then sorting will be disabled for all columns, regardless of each column's
         * individual <code>sorting</code> property.  The default is <code>true</code>.
     * @see TableColumnExt#isSortable()
     * @param sortable boolean indicating whether or not this table supports
     *        sortable columns
     */
    public void setSortable(boolean sortable) {
        if (sortable == isSortable()) return;
        this.sortable = sortable;
        firePropertyChange("sortable", !sortable, sortable);
        //JW @todo: this is a hack!
        if (sorter != null) {
           updateOnFilterContentChanged();
        }

    }

    /** Returns true if the table is sortable. */
    public boolean isSortable() {
        return sortable;
    }


//    public void setAutomaticSort(boolean automaticEnabled) {
//        this.automaticSortDisabled = !automaticEnabled;
//
//    }
//
//    public boolean isAutomaticSort() {
//        return !automaticSortDisabled;
//    }

    /** ? */
    public void tableChanged(TableModelEvent e) {
        Selection   selection = new Selection(this);
        if (filters != null) {
            filters.flush();    // will call contentsChanged()
        }
        else if (sorter != null) {
            sorter.refresh();
//            if (isAutomaticSort()) {
//                sorter.refresh();
//            }
        }

        super.tableChanged(e);
        restoreSelection(selection);
    }

    /** ? */
    protected void updateOnFilterContentChanged() {
        removeSorter();
        clearSelection();
        // Force private rowModel in JTable to null;
        boolean heightSet = isXTableRowHeightSet;
        setRowHeight(getRowHeight()); // Ugly!
        isXTableRowHeightSet = heightSet;
        revalidate();
        repaint();
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
    
    /** Returns the row count in the table; if filters are applied, this is the filtered row count. */
	@Override
    public int getRowCount() {
        // RG: If there are no filters, call superclass version rather than accessing model directly
        return filters == null ? super.getRowCount() : filters.getOutputSize();
    }

    /**
     * workaround bug in JTable. 
     * (Bug Parade ID #6291631 - negative y is mapped to row 0).
     */
    public int rowAtPoint(Point point) {
        if (point.y < 0) return -1;
        return super.rowAtPoint(point);
    }
    
	public boolean isHierarchical(int column) {
		return false;
	}

    /**
     * Convert row index from view coordinates to model coordinates
     * accounting for the presence of sorters and filters.
     *
     * @param row row index in view coordinates
     * @return row index in model coordinates
     */
    public int convertRowIndexToModel(int row) {
        if (sorter == null) {
            if (filters == null) {
                return row;
            }
            else {
                // delegate conversion to the filters pipeline
                return filters.convertRowIndexToModel(row);
            }
        }
        else {
            // after performing its own conversion, the sorter
            // delegates the conversion to the filters pipeline, if any
            return sorter.convertRowIndexToModel(row);
        }
    }

    /**
     * Convert row index from model coordinates to view coordinates
     * accounting for the presence of sorters and filters.
     *
     * @param row row index in model coordinates
     * @return row index in view coordinates
     */
    public int convertRowIndexToView(int row) {
        if (sorter == null) {
            if (filters == null) {
                return row;
            }
            else {
                // delegate conversion to the filters pipeline
                return filters.convertRowIndexToView(row);
            }
        }
        else {
            // before performing its own conversion, the sorter
            // delegates the conversion to the filters pipeline, if any
            return sorter.convertRowIndexToView(row);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getValueAt(int row, int column) {
        if (sorter == null) {       // have interactive sorter?
            if (filters == null) {  // have filter pipeline?
                // superclass will call convertColumnIndexToModel
                return super.getValueAt(row, column);   // unsorted, unfiltered
            }
            else {  // filtered
                return filters.getValueAt(row, convertColumnIndexToModel(column));
            }
        }
        else {  // interactively sorted, and potentially filtered
            return sorter.getValueAt(row, convertColumnIndexToModel(column));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(Object aValue, int row, int column) {
        if (sorter == null) {
            if (filters == null) {
                super.setValueAt(aValue, row, column);
            }
            else {
                filters.setValueAt(aValue, row, convertColumnIndexToModel(column));
            }
        }
        else {
            sorter.setValueAt(aValue, row, convertColumnIndexToModel(column));
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(int row, int column) {
        if (sorter == null) {
            if (filters == null) {
                return super.isCellEditable(row, column);
            }
            else {
                return filters.isCellEditable(row, convertColumnIndexToModel(column));
            }
        }
        else {
            return sorter.isCellEditable(row, convertColumnIndexToModel(column));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setModel(TableModel newModel) {
        //JW: need to clear here because super.setModel
        // calls tableChanged...
        // fixing #173
        clearSelection();
        super.setModel(newModel);
        use(filters);
    }


    /** ? */
    protected JTableHeader createDefaultTableHeader() {
        return new JXTableHeader(columnModel);
    }

    /** ? */
    protected TableColumnModel createDefaultColumnModel() {
        return new DefaultTableColumnModelExt();
    }

    /** ? */
    private void restoreSelection(Selection selection) {
        clearSelection();   // call overridden version

        // RG: calculate rowCount once, not inside a loop
        final   int rowCount = getModel().getRowCount();

        for (int i = 0; i < selection.selected.length; i++) {
            // JW: make sure we convert valid row indices (in model coordinates) only
            // fix #16
            int selected = selection.selected[i];
            if ((selected != selection.lead) && (selected < rowCount)) {
                int index = convertRowIndexToView(selection.selected[i]);
                selectionModel.addSelectionInterval(index, index);
            }
        }

        // JW: make sure we convert valid row indices (in model coordinates) only
        // fix #16
        if ((selection.lead >= 0) && (selection.lead < rowCount)) {
            selection.lead = convertRowIndexToView(selection.lead);
            selectionModel.addSelectionInterval(selection.lead, selection.lead);
        }
    }

    /** Returns the FilterPipeline for the table, null if none. */
    public FilterPipeline getFilters() {
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
            }
            pipeline.flush();
        }
    }

    /**
     *
     * @param pipeline must be != null
     * @return true is not yet used in this JXTable, false otherwise
     */
    private boolean initialUse(FilterPipeline pipeline) {
        if (pipelineListener == null) return true;
        PipelineListener[] l = pipeline.getPipelineListeners();
        for (int i = 0; i < l.length; i++) {
            if (pipelineListener.equals(l[i])) return false;
        }
        return true;
    }

    /** Sets the FilterPipeline for filtering table rows. */
    public void setFilters(FilterPipeline pipeline) {
        unsetFilters();
        doSetFilters(pipeline);
    }

    /** ? */
    private void unsetFilters() {
        if (filters == null) return;
        // fix#125: cleanup old filters
        filters.removePipelineListener(pipelineListener);
        // hacking around -
        //brute force update of sorter by removing
        pipelineListener.contentsChanged(null);
    }

    /** ? */
    private void doSetFilters(FilterPipeline pipeline) {
        filters = pipeline;
        use(filters);
    }

    /** Returns the HighlighterPipeline assigned to the table, null if none. */
    public HighlighterPipeline getHighlighters() {
        return highlighters;
    }

    /** Assigns a HighlighterPipeline to the table. 
     *  bound property.
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
     * returns the ChangeListener to use with highlighters. 
     * Creates one if necessary.
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

    /** ? */
    private void removeSorter() {
        sorter = null;
        getTableHeader().repaint();
    }


    /*
     * Used by headerListener
     */
    protected void resetSorter() {
        if (sorter != null) {
            Selection selection = new Selection(this);
            removeSorter();
            restoreSelection(selection);
        }
    }

    /** 
     * returns the sorter for the column in view coordinates.
     * 
     */
    private Sorter refreshSorter(int columnIndex) {
        TableColumn col = getColumnModel().getColumn(columnIndex);
        if (col instanceof TableColumnExt) {
            TableColumnExt column = (TableColumnExt) col;
            Sorter  newSorter = column.getSorter();
            if (newSorter != null) {
                // JW: hacking around #167: un-assign from filters
                // this should be done somewhere else!
                // fixed in Sorter
//                newSorter.interpose(null, getComponentAdapter(), null);
                // filter pipeline may be null!
                newSorter.interpose(filters, getComponentAdapter(), sorter);  // refresh
                return newSorter;
            }
        }
        return sorter;
    }

    /*
     * Used by headerListener.
     * 
     * request to sort the column at columnIndex in view coordinates.
     * if there is already an interactive sorter for this column
     * it's sort order is reversed. Otherwise the columns sorter is
     * used as is.
     * 
     */
    protected void setSorter(int columnIndex) {
        if (!isSortable()) return;
        Selection   selection = new Selection(this);
        if (sorter == null) {
            sorter = refreshSorter(columnIndex);    // create and refresh
        }
        else {
            int modelColumnIndex = convertColumnIndexToModel(columnIndex);
            if (sorter.getColumnIndex() == modelColumnIndex) {
                sorter.toggle();
            }
            else {
                sorter = refreshSorter(columnIndex);    // create and refresh
            }
        }
        restoreSelection(selection);
    }

    /*
     * Used by ColumnHeaderRenderer.getTableCellRendererComponent()
     */
    public Sorter getSorter(int columnIndex) {
        return sorter == null ? null :
            sorter.getColumnIndex() == convertColumnIndexToModel(columnIndex) ?
            sorter : null;
    }

    /**
     * Remove all columns
     */
    protected void removeColumns() {
        /** @todo promote this method to superclass, and
         * change createDefaultColumnsFromModel() to call this method */
        TableColumnModel cm = getColumnModel();
        while (cm.getColumnCount() > 0) {
            cm.removeColumn(cm.getColumn(0));
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
     * returns a list of TableColumns including hidden if the parameter
     * is set to true.
     * 
     * @param includeHidden
     * @return
     */
    public List getColumns(boolean includeHidden) {
        if (includeHidden && (getColumnModel() instanceof TableColumnModelExt)) {
            return ((TableColumnModelExt) getColumnModel()).getColumns(includeHidden);
        }
        return getColumns();
    }

    public int getColumnCount(boolean includeHidden) {
        if (getColumnModel() instanceof TableColumnModelExt) {
            return ((TableColumnModelExt) getColumnModel()).getColumnCount(includeHidden);
        }
        return getColumnCount();
    }
    /**
     * reorders the columns in the sequence given array.
     * Logical names that do not correspond to any column in the
     * model will be ignored.
     * Columns with logical names not contained are added at the end.
     * @param columnNames array of logical column names
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
     * Returns the <code>TableColumnExt</code> object for the column in the table
     * whose identifier is equal to <code>identifier</code>, when compared using
     * <code>equals</code>. The returned TableColumn is guaranteed to be part of 
     * the current ColumnModel but may be hidden, that is 
     * <pre> <code>
     *     TableColumnExt column = table.getColumnExt(id);
     *     if (column != null) {
     *         int viewIndex = table.convertColumnIndexToView(column.getModelIndex());
     *         assertEquals(column.isVisible(), viewIndex >= 0);
     *     }
     * </code> </pre>
     *
     * @param   identifier                      the identifier object
     *
     * @return  the <code>TableColumnExt</code> object that matches the identifier or 
     *   null if none is found.
     */
    public TableColumnExt getColumnExt(Object identifier) {
        if (getColumnModel() instanceof TableColumnModelExt) {
            return ((TableColumnModelExt) getColumnModel()).getColumnExt(identifier);
        } else {
            // pending: not tested!
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
     * Returns the <code>TableColumnExt</code> object for the column in the table
     * whose column index is equal to <code>viewColumnIndex</code>
     *
     * @param  viewColumnIndex index of the column with the object in question
     *
     * @return  the <code>TableColumnExt</code> object that matches the column index
     * @exception IllegalArgumentException if no <code>TableColumn</code> has this identifier
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
            int modelColumnCount = model.getColumnCount();
            TableColumn newColumns[] = new TableColumn[modelColumnCount];
            for (int i = 0; i < newColumns.length; i++) {
                newColumns[i] = createAndConfigureColumn(model, i);
            }

            // Remove any current columns
            TableColumnModel columnModel = getColumnModel();
            while (columnModel.getColumnCount() > 0) {
                columnModel.removeColumn(columnModel.getColumn(0));
            }

            // Now add the new columns to the column model
            for (int i = 0; i < newColumns.length; i++) {
                addColumn(newColumns[i]);
            }
        }
    }

//------------------------- start of (meta)Data-aware code

    protected TableColumn createAndConfigureColumn(TableModel model, int modelColumn) {
//        TableColumn column = createColumn(modelColumn);
//        if (model instanceof MetaDataProvider) {
//            MetaDataProvider provider = (MetaDataProvider) model;
//            MetaData metaData = provider.getMetaData(provider.getFieldNames()[modelColumn]);
//            configureColumn(column, metaData);
//        }
//        column.setHeaderValue(model.getColumnName(modelColumn));
        return getColumnFactory().createAndConfigureTableColumn(model, modelColumn);
    }

    private ColumnFactory getColumnFactory() {
        if (columnFactory == null) {
            columnFactory = ColumnFactory.getInstance();
        }
        return columnFactory;
    }

//    /**
//     * set column properties from MetaData. <p>
//     * Experimental, will be moved somewhere else when
//     * going for a data-unaware swingx layer.<p>
//     *
//     * Note: the column must not be assumed to be already
//     * added to the columnModel nor to have any relation
//     * to the current tableModel!.
//     *
//     * @param column
//     * @param metaData
//     */
//    public void configureColumn(TableColumn column, MetaData metaData) {
//        column.setIdentifier(metaData.getName());
//        column.setHeaderValue(metaData.getLabel());
//        if (column instanceof TableColumnExt) {
//            TableColumnExt columnExt = (TableColumnExt) column;
//            if (metaData.getElementClass() == String.class) {
//
////                if (metaData.getDisplayWidth() > 0) {
////                    StringBuffer buf = new StringBuffer(metaData.getDisplayWidth());
////                    for (int i = 0; i < metaData.getDisplayWidth(); i++) {
////                        buf.append("r");
////
////                    }
////                    columnExt.setPrototypeValue(buf.toString());
////                }
//
//                columnExt.putClientProperty(TableColumnExt.SORTER_COMPARATOR,
//                        Collator.getInstance());
//            }
//        }
//    }

//    /**
//     * Returns the default table model object, which is
//     * a <code>DefaultTableModel</code>.  A subclass can override this
//     * method to return a different table model object.
//     *
//     * @return the default table model object
//     * @see DefaultTableColumnModelExt
//     */
//    protected TableModel createDefaultDataModel() {
//        return new DefaultTableModelExt();
//    }
//-------------------- end of (meta)Data-aware code



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
         * @param value margin between columns; must be greater than or equal to zero.
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

    /**
     * Returns the decorated <code>Component</code> used as a stamp to render
     * the specified cell. Overrides superclass version to provide support for
     * cell decorators.
     *
     * @param renderer the <code>TableCellRenderer</code> to prepare
     * @param row the row of the cell to render, where 0 is the first row
         * @param column the column of the cell to render, where 0 is the first column
     * @return the decorated <code>Component</code> used as a stamp to render the specified cell
     * @see org.jdesktop.swingx.decorator.Highlighter
     */
    public Component prepareRenderer(TableCellRenderer renderer, int row,
                                     int column) {
        Component stamp = super.prepareRenderer(renderer, row, column);
        if (highlighters == null) {
            return stamp;   // no need to decorate renderer with highlighters
        }
        else {
            // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
            ComponentAdapter    adapter = getComponentAdapter();
            adapter.row = row;
            adapter.column = column;
            return highlighters.apply(stamp, adapter);
        }
    }

    protected ComponentAdapter getComponentAdapter() {
        // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
        return dataAdapter;
    }

    /** 
     * Performs a search across the table using String that represents a regex pattern;
     * {@link java.util.regex.Pattern}. All columns and all rows are searched; the row id
     * of the first match is returned. 
     */
    public int search(String searchString) {
        return search(searchString, -1);
    }

    /** 
     * Performs a search on a column using String that represents a regex pattern;
     * {@link java.util.regex.Pattern}. The specified column searched; the row id
     * of the first match is returned. 
     */
    public int search(String searchString, int columnIndex) {
        Pattern pattern = null;
        if (searchString != null) {
            return search(Pattern.compile(searchString, 0), columnIndex);
        }
        return -1;
    }

    /** 
     * Performs a search across the table using a {@link java.util.regex.Pattern}. 
     * All columns and all rows are searched; the row id
     * of the first match is returned. 
     */
    public int search(Pattern pattern) {
        return search(pattern, -1);
    }

    /** 
     * Performs a search across the table using a {@link java.util.regex.Pattern}. 
     * starting at a given row. All columns and all rows are searched; the row id
     * of the first match is returned. 
     */
    public int search(Pattern pattern, int startIndex) {
        return search(pattern, startIndex, false);
    }

    // Save the last column with the match.
    private int lastCol = 0;

    /**
     * Performs a search across the table using a {@link java.util.regex.Pattern}. 
     * starting at a given row. All columns and all rows are searched; the row id
     * of the first match is returned. 
     *
     * @param startIndex row to start search
     * @param backwards whether to start at the last row and search up to the first.
     * @return row with a match.
     */
    public int search(Pattern pattern, int startIndex, boolean backwards) {
        if (pattern == null) {
            lastCol = 0;
            return -1;
        }
        int rows = getRowCount();
        int endCol = getColumnCount();

        int matchRow = -1;

        if (backwards == true) {
            if (startIndex < 0)
                startIndex = rows;
            int startRow = startIndex - 1;
            for (int r = startRow; r >= 0 && matchRow == -1; r--) {
                for (int c = endCol - 1; c >= 0; c--) {
                    Object value = getValueAt(r, c);
                    if ((value != null) &&
                    // JW: differs from PatternHighlighter/Filter
                        pattern.matcher(value.toString()).find()) {
                        // pattern.matcher(value.toString()).matches()) {
                        changeSelection(r, c, false, false);
                        matchRow = r;
                        lastCol = c;
                        break; // No need to search other columns
                    }
                }
                if (matchRow == -1) {
                    lastCol = endCol;
                }
            }
        } else {
            int startRow = startIndex + 1;
            for (int r = startRow; r < rows && matchRow == -1; r++) {
                for (int c = lastCol; c < endCol; c++) {
                    Object value = getValueAt(r, c);
                    if ((value != null) &&
                    // JW: differs from PatternHighlighter/Filter
                            pattern.matcher(value.toString()).find()) {
                        // pattern.matcher(value.toString()).matches()) {
                        changeSelection(r, c, false, false);
                        matchRow = r;
                        lastCol = c;
                        break; // No need to search other columns
                    }
                }
                if (matchRow == -1) {
                    lastCol = 0;
                }
            }
        }

        if (matchRow != -1) {
            Object viewport = getParent();
            if (viewport instanceof JViewport) {
                Rectangle rect = getCellRect(getSelectedRow(), 0, true);
                Point pt = ((JViewport) viewport).getViewPosition();
                rect.setLocation(rect.x - pt.x, rect.y - pt.y);
                ((JViewport) viewport).scrollRectToVisible(rect);
            }
        }
        return matchRow;
    }
    
//    public int search(Pattern pattern, int startIndex, boolean backwards) {
//        if (pattern == null) {
//            lastCol = 0;
//            return -1;
//        }
//        int rows = getRowCount();
//        int endCol = getColumnCount();
//
//        int startRow = startIndex + 1;
//        int matchRow = -1;
//
//        if (backwards == true) {
//            for (int r = startRow; r >= 0 && matchRow == -1; r--) {
//                for (int c = endCol; c >= 0; c--) {
//                    Object value = getValueAt(r, c);
//                    if ( (value != null) &&
//                        pattern.matcher(value.toString()).find()) {
//                        changeSelection(r, c, false, false);
//                        matchRow = r;
//                        lastCol = c;
//                        break; // No need to search other columns
//                    }
//                }
//                if (matchRow == -1) {
//                    lastCol = endCol;
//                }
//            }
//        }
//        else {
//            for (int r = startRow; r < rows && matchRow == -1; r++) {
//                for (int c = lastCol; c < endCol; c++) {
//                    Object value = getValueAt(r, c);
//                    if ( (value != null) &&
//                        pattern.matcher(value.toString()).find()) {
//                        changeSelection(r, c, false, false);
//                        matchRow = r;
//                        lastCol = c;
//                        break; // No need to search other columns
//                    }
//                }
//                if (matchRow == -1) {
//                    lastCol = 0;
//                }
//            }
//        }
//
//        if (matchRow != -1) {
//            Object viewport = getParent();
//            if (viewport instanceof JViewport) {
//                Rectangle rect = getCellRect(getSelectedRow(), 0, true);
//                Point pt = ( (JViewport) viewport).getViewPosition();
//                rect.setLocation(rect.x - pt.x, rect.y - pt.y);
//                ( (JViewport) viewport).scrollRectToVisible(rect);
//            }
//        }
//        return matchRow;
//    }

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
            //remind(aim): height is still off...???
            int rowCount = getVisibleRowCount();
            prefSize.height = rowCount * getRowHeight() +
                (header != null? header.getPreferredSize().height : 0);
            setPreferredScrollableViewportSize(prefSize);
        }
        return prefSize;
    }

//---------------------------- support to "pack" columns to header/cell content width    
    /**
     * Packs all the columns to their optimal size. Works best with
     * auto resizing turned off. 
     * 
     * Contributed by M. Hillary (Issue #60)
     * 
     * @param margin the margin to apply to each column.
     */
    public void packTable(int margin) {
        for (int c = 0; c < getColumnCount(); c++)
            packColumn(c, margin, -1 );
    }
    
    /**
     * Packs an indivudal column in the table.
     * Contributed by M. Hillary (Issue #60)
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
     * maximum witdth. If maximun is -1 then the column is made as wide
     * as it needs.
     * Contributed by M. Hillary (Issue #60)
     * 
     * @param column The Column index to pack in View Coordinates
     * @param margin The margin to apply to the column
     * @param max The maximum width the column can be resized to. -1 mean any size.
     */
    public void packColumn(int column, int margin, int max) {
        getColumnFactory().packColumn(this, getColumnExt(column), margin, max);
    }
    
    /**
     * Initialize the preferredWidth of the specified column based on the
     * column's prototypeValue property.  If the column is not an
     * instance of <code>TableColumnExt</code> or prototypeValue is <code>null</code>
     * then the preferredWidth is left unmodified.
     * @see org.jdesktop.swingx.table.TableColumnExt#setPrototypeValue
     * @param column TableColumn object representing view column
     */
    protected void initializeColumnPreferredWidth(TableColumn column) {
        if (column instanceof TableColumnExt) {
            getColumnFactory().configureColumnWidths(this, (TableColumnExt) column);
        }
    }


    protected static class TableAdapter extends ComponentAdapter {
        private final JXTable table;

        /**
         * Constructs a <code>TableDataAdapter</code> for the specified
         * target component.
         *
         * @param component the target component
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

        /**
         * {@inheritDoc}
         */
        public boolean hasFocus() {
        
            boolean rowIsLead = (table.getSelectionModel().
                                   getLeadSelectionIndex() == row);
            boolean colIsLead =
                (table.getColumnModel().getSelectionModel().
                 getLeadSelectionIndex() ==
                 column);
            return table.isFocusOwner() && (rowIsLead && colIsLead);

        }

        public String getColumnName(int columnIndex) {
            TableColumnModel    columnModel = table.getColumnModel();
            if (columnModel == null){
                return "Column " + columnIndex;
            }
            TableColumn         column = columnModel.getColumn(columnIndex);

            return column == null ? "" : column.getHeaderValue().toString();
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

        public Object getFilteredValueAt(int row, int column) {
            return table.getValueAt(row, column);   // in view coordinates
        }

        public void setValueAt(Object aValue, int row, int column) {
            // RG: eliminate superfluous back-and-forth conversions
            table.getModel().setValueAt(aValue, row, column);
        }

        public boolean isCellEditable(int row, int column) {
            // RG: eliminate superfluous back-and-forth conversions
            return table.getModel().isCellEditable(row, column);
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
        public int modelToView(int columnIndex) {
            return table.convertColumnIndexToView(columnIndex);
        }

        /**
         * {@inheritDoc}
         */
        public int viewToModel(int columnIndex) {
            return table.convertColumnIndexToModel(columnIndex);
        }

        public String getColumnIdentifier(int columnIndex) {
            Object identifier = table.getColumnExt(columnIndex).getIdentifier();
            return identifier != null ? identifier.toString() : null;
        }

    }

    private static class Selection {
        protected   final int[] selected;   // used ONLY within save/restoreSelection();
        protected   int     lead = -1;
        protected Selection(JXTable table) {
            selected = table.getSelectedRows(); // in view coordinates
            for (int i = 0; i < selected.length; i++) {
                selected[i] = table.convertRowIndexToModel(selected[i]);    // model coordinates
            }

            if (selected.length > 0) {
                // convert lead selection index to model coordinates
                lead = table.convertRowIndexToModel(
                    table.getSelectionModel().getLeadSelectionIndex());
            }
        }
    }

    /*
     * Default Type-based Renderers:
     * JTable's default table cell renderer classes are private and
     * JTable:getDefaultRenderer() returns a *shared* cell renderer instance,
     * thus there is no way for us to instantiate a new instance of one of its
     * default renderers.  So, we must replicate the default renderer classes
     * here so that we can instantiate them when we need to create renderers
     * to be set on specific columns.
     */
    public static class NumberRenderer extends DefaultTableCellRenderer {
        public NumberRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT);
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
            setText( (value == null) ? "" : formatter.format(value));
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
            setText( (value == null) ? "" : formatter.format(value));
        }
    }

    public static class IconRenderer extends DefaultTableCellRenderer {
        public IconRenderer() {
            super();
            setHorizontalAlignment(JLabel.CENTER);
        }

        public void setValue(Object value) {
            setIcon( (value instanceof Icon) ? (Icon) value : null);
        }
    }

    public static class BooleanRenderer extends JCheckBox
        implements TableCellRenderer {
        public BooleanRenderer() {
            super();
            setHorizontalAlignment(JLabel.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            }
            else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            setSelected( (value != null && ( (Boolean) value).booleanValue()));
            return this;
        }
    }


//    /**
//     * Renders a LinkModel type the link in the table column
//     */
//    public static class LinkRenderer extends DefaultTableCellRenderer {
//
//        // Should have a way of setting these statically
//        private static Color colorLive = new Color(0, 0, 238);
//        private static Color colorVisited = new Color(82, 24, 139);
//
//        public void setValue(Object value) {
//            if (value != null && value instanceof LinkModel) {
//                LinkModel link = (LinkModel) value;
//
//                setText(link.getText());
//                setToolTipText(link.getURL().toString());
//
//                if (link.getVisited()) {
//                    setForeground(colorVisited);
//                }
//                else {
//                    setForeground(colorLive);
//                }
//            }
//            else {
//                super.setValue(value != null ? value.toString() : "");
//            }
//        }
//
//        public void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            if (!getText().equals("")) {
//                // Render an underline. A really smart person
//                // would actually render an underline font but
//                // that's too much for my little brain.
//                Rectangle rect = PaintUtils.getTextBounds(g, this);
//
//                FontMetrics fm = g.getFontMetrics();
//                int descent = fm.getDescent();
//
//                //REMIND(aim): should we be basing the underline on
//                //the font's baseline instead of the text bounds?
//
//                g.drawLine(rect.x, (rect.y + rect.height) - descent + 1,
//                           rect.x + rect.width,
//                           (rect.y + rect.height) - descent + 1);
//            }
//        }
//    }
}
