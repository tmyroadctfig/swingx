/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.ActionMap;
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
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PipelineEvent;
import org.jdesktop.swingx.decorator.PipelineListener;
import org.jdesktop.swingx.decorator.Sorter;
import org.jdesktop.swingx.icon.ColumnControlIcon;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.ColumnHeaderRenderer;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.util.Link;


/**
 * JXTable
 *
 * @author Ramesh Gupta
 * @author Amy Fowler
 * @author Mark Davidson
 * @author Jeanette Winzenburg
 */
public class JXTable extends JTable implements PipelineListener, Searchable {

public static boolean TRACE = false;
  
    protected Sorter            sorter = null;
    protected FilterPipeline        filters = null;
    protected HighlighterPipeline   highlighters = null;

    // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
    private final ComponentAdapter dataAdapter = new TableAdapter(this);

    // No need to define a separate JTableHeader subclass!
    // JW: on the contrary - should be moved to dedicated JTableHeader subclass
    // (never act on behalf of other components - except with good reason <g>)
    // there's some weirdness (caused by JTable) when overriding 
    // createDefaultTableHeader - check!
    private final static MouseAdapter   headerListener = new MouseAdapter() {
        // MouseAdapter must be stateless
        public void mouseClicked(MouseEvent e) {
            if (shouldIgnore(e)) return;
            if (isInResizeRegion(e)) {
                doResize(e);
                return;
            }
            JTableHeader    header = (JTableHeader) e.getSource();
            JXTable     table = (JXTable) header.getTable();
            if (!table.isSortable() || (e.getClickCount() != 1)) return;
            if ((e.getModifiersEx() & e.SHIFT_DOWN_MASK) == e.SHIFT_DOWN_MASK) {
                table.resetSorter();
            }
            else {

                int column = header.getColumnModel().getColumnIndexAtX(e.getX());
                if (column >= 0) {
                    table.setSorter(column);
                }
            }
            header.repaint();
        }

        private boolean shouldIgnore(MouseEvent e) {
            return !SwingUtilities.isLeftMouseButton(e);
                   // && table.isEnabled());
        }

        private void doResize(MouseEvent e) {
            if (e.getClickCount() < 2) return;
            JTableHeader header = (JTableHeader) e.getSource();
            if (header.getTable() instanceof JXTable) {
                int column = header.getColumnModel().getColumnIndexAtX(e.getX());
                if (column >= 0) {
                    ((JXTable) header.getTable()).packColumn(column, 5);
                }
            }
            
        }

        private boolean isInResizeRegion(MouseEvent e) {
            JTableHeader header = (JTableHeader) e.getSource();
            // JW: kind of a hack - there's no indication in the 
            // JTableHeader api to find if we are in the resizing
            // region before actually receiving a click
            // checked the header.resizingColumn should be set on
            // first click?
            // doesn't work probably because this listener is messaged before
            // ui-delegate listener
            // return header.getResizingColumn() != null;
            Cursor cursor = header.getCursor();
            boolean inResize = cursor != null ? 
                    (cursor.getType() == Cursor.E_RESIZE_CURSOR || cursor.getType() == Cursor.W_RESIZE_CURSOR ) :
                     false;   
            return inResize;
        }
    };

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

    public JXTable() {
        init();
    }

    public JXTable(TableModel dm) {
        super(dm);
        init();
    }

    public JXTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        init();
    }

    public JXTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        init();
    }

    public JXTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        init();
    }

    public JXTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
        init();
    }

    public JXTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        init();
    }

    protected void init() {
        setSortable(true);
        // Register the actions that this class can handle.
        ActionMap map = getActionMap();
        map.put("print", new Actions("print"));
        map.put("find", new Actions("find"));

        // Add a link handler to to the table.
        // XXX note: this listener represents overhead if no columns are links.
        // Beter to detect if the table has a link column and add the handler.
//        LinkHandler handler = new LinkHandler();
//        addMouseListener(handler);
//        addMouseMotionListener(handler);
        setDefaultEditor(Link.class, new LinkRenderer());
//        setRolloverEnabled(true);
    }

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

    public boolean isRolloverEnabled() {
        return rolloverProducer != null;
    }

    /**
     * overridden to addionally configure the upper right corner 
     * of an enclosing scrollpane with the ColumnControl.
     */
    protected void configureEnclosingScrollPane() {
        super.configureEnclosingScrollPane();
        configureColumnControl();
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
        configureColumnControl();
        firePropertyChange("hasColumnControl", old, columnControlVisible);
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

    private void setLazyValue(Hashtable h, Class c, String s) {
        h.put(c, new UIDefaults.ProxyLazyValue(s));
    }

    private void setLazyRenderer(Class c, String s) {
        setLazyValue(defaultRenderersByColumnClass, c, s);
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
        setLazyRenderer(Link.class, "org.jdesktop.swingx.LinkRenderer");
    }


    /**
     * A small class which dispatches actions.
     * TODO: Is there a way that we can make this static?
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
        }
    }

    private JXFindDialog dialog = null;
    private boolean automaticSortDisabled;

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
     * @see TableColumnExt#isSortable
     * @see TableColumnExt#setSortable
     * @param sortable boolean indicating whether or not this table supports
     *        sortable columns
     */
    public void setSortable(boolean sortable) {
        if (sortable == isSortable()) return;
        this.sortable = sortable;
        firePropertyChange("sortable", !sortable, sortable);
        //JW @todo: this is a hack!
        if (sorter != null) {
           contentsChanged(null);
        }

    }

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

    public void contentsChanged(PipelineEvent e) {
        removeSorter();
        clearSelection();

        // Force private rowModel in JTable to null;
        setRowHeight(getRowHeight());   // Ugly!

        revalidate();
        repaint();
    }

	@Override
    public int getRowCount() {
        // RG: If there are no filters, call superclass version rather than accessing model directly
        return filters == null ? super.getRowCount() : filters.getOutputSize();
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

    public void setModel(TableModel newModel) {
        //JW: need to clear here because super.setModel
        // calls tableChanged...
        // fixing #173
        clearSelection();
        super.setModel(newModel);
        use(filters);
    }


    /**
     * Adds private mouse listener to the table header (for sorting support)
     * before handing it off to the super class for processing.
     *
     * @param tableHeader
     */
    public void setTableHeader(JTableHeader tableHeader) {
        // This method is also called during construction of JTable
        if (tableHeader != null) {
            tableHeader.addMouseListener(headerListener);
//            tableHeader.setDefaultRenderer(
//                    new DelegatingHeaderRenderer((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()));
            tableHeader.setDefaultRenderer(ColumnHeaderRenderer.createColumnHeaderRenderer());
        }
        super.setTableHeader(tableHeader);
    }


//    protected JTableHeader createDefaultTableHeader() {
//        return new JXTableHeader(columnModel);
//    }


    protected TableColumnModel createDefaultColumnModel() {
        return new DefaultTableColumnModelExt();
    }

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
                pipeline.addPipelineListener(this);
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
        PipelineListener[] l = pipeline.getPipelineListeners();
        for (int i = 0; i < l.length; i++) {
            if (this.equals(l[i])) return false;
        }
        return true;
    }

    public void setFilters(FilterPipeline pipeline) {
        unsetFilters();
        doSetFilters(pipeline);
    }

    private void unsetFilters() {
        if (filters == null) return;
        // fix#125: cleanup old filters
        filters.removePipelineListener(this);
        // hacking around -
        //brute force update of sorter by removing
        contentsChanged(null);
    }

    private void doSetFilters(FilterPipeline pipeline) {
        filters = pipeline;
        use(filters);
    }

    public HighlighterPipeline getHighlighters() {
        return highlighters;
    }

    public void setHighlighters(HighlighterPipeline pipeline) {
        highlighters = pipeline;
    }

    private void removeSorter() {
//        // fixing #167: remove from pipeline
//        // moved to refreshSorter
//        if (sorter != null) {
//           sorter.interpose(null, getComponentAdapter(), null);
//        }
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

    private Sorter refreshSorter(int columnIndex) {
        TableColumn col = getColumnModel().getColumn(columnIndex);
        if (col instanceof TableColumnExt) {
            TableColumnExt column = (TableColumnExt) col;
            Sorter  newSorter = column.getSorter();
            if (newSorter != null) {
                // JW: hacking around #167: un-assign from filters
                // this should be done somewhere else!
                newSorter.interpose(null, getComponentAdapter(), null);
                // filter pipeline may be null!
                newSorter.interpose(filters, getComponentAdapter(), sorter);  // refresh
                return newSorter;
            }
        }
        return sorter;
    }

    /*
     * Used by headerListener
     */
    protected void setSorter(int columnIndex) {
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

    public List getColumns() {
        return null; /** @todo Implement this */
    }

    /**
     * Returns the <code>TableColumn</code> object for the column in the table
     * whose identifier is equal to <code>identifier</code>, when compared using
     * <code>equals</code>.
     *
     * @return  the <code>TableColumn</code> object that matches the identifier
     * @exception IllegalArgumentException
     *    if <code>identifier</code> is <code>null</code>
     *    or no <code>TableColumn</code> has this identifier
     *
     * @param   identifier                      the identifier object
     */

    public TableColumnExt getColumnExt(Object identifier) {
        return (TableColumnExt)super.getColumn(identifier);
    }

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
        TableColumn column = createColumn(modelColumn);
//        if (model instanceof MetaDataProvider) {
//            MetaDataProvider provider = (MetaDataProvider) model;
//            MetaData metaData = provider.getMetaData(provider.getFieldNames()[modelColumn]);
//            configureColumn(column, metaData);
//        }
        column.setHeaderValue(model.getColumnName(modelColumn));
        return column;
    }

    /**
     * set column properties from MetaData. <p>
     * Experimental, will be moved somewhere else when
     * going for a data-unaware swingx layer.<p>
     *
     * Note: the column must not be assumed to be already
     * added to the columnModel nor to have any relation
     * to the current tableModel!.
     *
     * @param column
     * @param metaData
     */
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

    /**
     * Returns the default table model object, which is
     * a <code>DefaultTableModel</code>.  A subclass can override this
     * method to return a different table model object.
     *
     * @return the default table model object
     * @see org.jdesktop.swing.table.DefaultTableModelExt
     */
//    protected TableModel createDefaultDataModel() {
//        return new DefaultTableModelExt();
//    }
//-------------------- end of (meta)Data-aware code

    protected TableColumn createColumn(int modelIndex) {
        return new TableColumnExt(modelIndex);
    }


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
     * @see org.jdesktop.swing.decorator.Highlighter
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

    public int search(String searchString) {
        return search(searchString, -1);
    }

    public int search(String searchString, int columnIndex) {
        Pattern pattern = null;
        if (searchString != null) {
            return search(Pattern.compile(searchString, 0), columnIndex);
        }
        return -1;
    }

    public int search(Pattern pattern) {
        return search(pattern, -1);
    }

    public int search(Pattern pattern, int startIndex) {
        return search(pattern, startIndex, false);
    }

    // Save the last column with the match.
    private int lastCol = 0;

    /**
     * @param startIndex row to start search
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

    public void setVisibleRowCount(int visibleRowCount) {
        this.visibleRowCount = visibleRowCount;
    }

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
        TableColumnModel colModel = getColumnModel();
        TableColumn col = colModel.getColumn(column);

        /* Get width of column header */
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) 
            renderer = getTableHeader().getDefaultRenderer();
        
        int width = 0;
        
        Component comp = renderer.getTableCellRendererComponent(this, col
                .getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;
        
        if(getRowCount() > 0)
            renderer = getCellRenderer(0, column);
        for (int r = 0; r < getRowCount(); r++) {
            comp = renderer.getTableCellRendererComponent(this, getValueAt(r,
                    column), false, false, r, column);
            width = Math.max(width, comp.getPreferredSize().width);
        }
        width += 2 * margin;

        /* Check if the width exceeds the max */
        if( max != -1 && width > max )
            width = max;
        
        col.setPreferredWidth(width);
    }
    
    /**
     * Initialize the preferredWidth of the specified column based on the
     * column's prototypeValue property.  If the column is not an
     * instance of <code>TableColumnExt</code> or prototypeValue is <code>null</code>
     * then the preferredWidth is left unmodified.
     * @see org.jdesktop.swing.table.TableColumnExt#setPrototypeValue
     * @param column TableColumn object representing view column
     */
    protected void initializeColumnPreferredWidth(TableColumn column) {
        if (column instanceof TableColumnExt) {
            Dimension cellSpacing = getIntercellSpacing();
            TableColumnExt columnx = (TableColumnExt) column;
 //           if (columnx.isVisible()) {
                Object prototypeValue = columnx.getPrototypeValue();
                if (prototypeValue != null) {
                    // calculate how much room the prototypeValue requires
                    TableCellRenderer renderer = getCellRenderer(0,
                        convertColumnIndexToView(columnx.getModelIndex()));
                    Component comp = renderer.getTableCellRendererComponent(this,
                        prototypeValue, false, false, 0, 0);
                    int prefWidth = comp.getPreferredSize().width + cellSpacing.width;

                    // now calculate how much room the column header wants
                    renderer = columnx.getHeaderRenderer();
                    if (renderer == null) {
                        JTableHeader header = getTableHeader();
                        if (header != null) {
                            renderer = header.getDefaultRenderer();
                        }
                    }
                    if (renderer != null) {
                        comp = renderer.getTableCellRendererComponent(this,
                                 columnx.getHeaderValue(), false, false, 0,
                                 convertColumnIndexToView(columnx.getModelIndex()));

                        prefWidth = Math.max(comp.getPreferredSize().width, prefWidth);
                    }
                    prefWidth += getColumnModel().getColumnMargin();
                    columnx.setPreferredWidth(prefWidth);
                }
//            } else {
//                columnx.setPreferredWidth(0);
//            }
        }
    }


    protected static class TableAdapter extends ComponentAdapter {
        private final JTable table;

        /**
         * Constructs a <code>TableDataAdapter</code> for the specified
         * target component.
         *
         * @param component the target component
         */
        public TableAdapter(JTable component) {
            super(component);
            table = component;
        }

        /**
         * Typesafe accessor for the target component.
         *
         * @return the target component as a {@link javax.swing.JTable}
         */
        public JTable getTable() {
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

    /**
     * Renders a Link type the link in the table column
     */
//    public static class LinkRenderer extends DefaultTableCellRenderer {
//
//        // Should have a way of setting these statically
//        private static Color colorLive = new Color(0, 0, 238);
//        private static Color colorVisited = new Color(82, 24, 139);
//
//        public void setValue(Object value) {
//            if (value != null && value instanceof Link) {
//                Link link = (Link) value;
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
