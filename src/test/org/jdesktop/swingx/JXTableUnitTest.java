/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.PipelineListener;
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.table.ColumnHeaderRenderer;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.util.AncientSwingTeam;

public class JXTableUnitTest extends InteractiveTestCase {

    protected DynamicTableModel tableModel = null;
    protected TableModel sortableTableModel;
    
    public JXTableUnitTest() {
        super("JXTable unit test");
    }

    protected void setUp() throws Exception {
       super.setUp();
        // set loader priority to normal
        if (tableModel == null) {
            tableModel = new DynamicTableModel();
        }
        sortableTableModel = new AncientSwingTeam();
    }
    

    public void testComponentAdapterCoordinates() {
        
    }
    /**
     * Issue #196: backward search broken.
     *
     */
    public void testBackwardSearch() {
        JXTable table = new JXTable(createModel(0, 10));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        int found = table.search(Pattern.compile(lastName), -1, true);
        assertEquals(row, found);
    }

    /**
     * Issue #175: multiple registration as PipelineListener.
     * 
     *
     */
    public void testRegisterUniquePipelineListener() {
        JXTable table = new JXTable();
        PatternFilter noFilter = new PatternFilter(".*", 0, 1);
        table.setFilters(new FilterPipeline(new Filter[] {noFilter}));
        int listenerCount = table.getFilters().getPipelineListeners().length;
        PipelineListener l = table.getFilters().getPipelineListeners()[listenerCount - 1];
        // sanity assert
        assertEquals(1, listenerCount);
        assertEquals(table, l);
        table.setModel(createModel(0, 20));
        assertEquals("pipeline listener count must not change after setModel", listenerCount, table.getFilters().getPipelineListeners().length);
        
    }
    /**
     * Issue #174: componentAdapter.hasFocus() looks for anchor instead of lead.
     *
     */
    public void testLeadFocusCell() {
        final JXTable table = new JXTable();
        table.setModel(createModel(0, 10));
        // sort first column
//        table.setSorter(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        final int leadRow = table.getSelectionModel().getLeadSelectionIndex();
        int anchorRow = table.getSelectionModel().getAnchorSelectionIndex();
        table.addColumnSelectionInterval(0, 0);
        final int leadColumn = table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        int anchorColumn = table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
        assertEquals("lead must be last row", table.getRowCount() - 1, leadRow);
        assertEquals("anchor must be second last row", table.getRowCount() - 2, anchorRow);
        assertEquals("lead must be first column", 0, leadColumn);
        assertEquals("anchor must be first column", 0, anchorColumn);
        final JFrame frame = new JFrame();
        frame.add(table);
        frame.pack();
        frame.setVisible(true);
        table.requestFocus();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ComponentAdapter adapter = table.getComponentAdapter();
                adapter.row = leadRow;
                adapter.column = leadColumn;
                // difficult to test - hasFocus() implies that the table isFocusOwner()
                assertTrue("adapter must have focus for leadRow/Column: " + adapter.row + "/" + adapter.column, 
                        adapter.hasFocus());
                frame.dispose();

            }
        });
    }
    

    /**
     * Issue #173: 
     * ArrayIndexOOB if replacing model with one containing
     * fewer rows and the "excess" is selected.
     *
     */
    public void testSelectionAndToggleModel() {
        JXTable table = new JXTable();
        table.setModel(createModel(0, 10));
        // sort first column
        table.setSorter(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.setSorter(0);
        // set model with less rows
        table.setModel(createModel(0, 8));
        
    }
    
    /**
     * testing selection and adding rows.
     * 
     *
     */
    public void testSelectionAndAddRows() {
        JXTable table = new JXTable();
        DefaultTableModel model = createModel(0, 10);
        table.setModel(model);
        // sort first column
        table.setSorter(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.setSorter(0);
        
        Integer highestValue = new Integer(100);
        model.addRow(new Object[] { highestValue });
        assertEquals(highestValue, table.getValueAt(0, 0));
    }
    
    /**
     * Issue #16: removing row throws ArrayIndexOOB if
     * last row was selected
     *
     */
    public void testSelectionAndRemoveRows() {
        JXTable table = new JXTable();
        DefaultTableModel model = createModel(0, 10);
        table.setModel(model);
        // sort first column
        table.setSorter(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.setSorter(0);
        model.removeRow(0);
    }
    
    private DefaultTableModel createModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 5);
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }
    
    
    /**
     * Issue #171: row-coordinate not transformed in isCellEditable (sorting)
     *
     */
    public void testSortedEditability() {
        int rows = 2;
        RowObjectTableModel model = createRowObjectTableModel(rows);
        JXTable table = new JXTable(model);
        RowObject firstInModel = model.getRowObject(0);
        assertEquals("rowObject data must be equal", firstInModel.getData1(), table.getValueAt(0, 0));
        assertEquals("rowObject editability must be equal", firstInModel.isEditable(), table.isCellEditable(0, 0));
        // nothing changed
        table.setSorter(0);
        Object firstDataValueInTable = table.getValueAt(0,0);
        boolean firstEditableValueInTable = table.isCellEditable(0, 0);
        assertEquals("rowObject data must be equal", firstInModel.getData1(), table.getValueAt(0, 0));
        assertEquals("rowObject editability must be equal", firstInModel.isEditable(), table.isCellEditable(0, 0));
        // sanity assert: first and last have different values/editability
        assertTrue("lastValue different from first", firstDataValueInTable !=
                table.getValueAt(rows - 1, 0));
        assertTrue("lastEditability different from first", firstEditableValueInTable !=
            table.isCellEditable(rows - 1, 0));
        // reverse order
        table.setSorter(0);
        assertEquals("last row data must be equal to former first", firstDataValueInTable, 
                table.getValueAt(rows - 1, 0));
        assertEquals("last row editability must be equal to former first", firstEditableValueInTable, 
                table.isCellEditable(rows - 1, 0));
    }

    /**
     * Issue #171: row-coordinate not transformed in isCellEditable (filtering)
     *
     */
    public void testFilteredEditability() {
        int rows = 2;
        RowObjectTableModel model = createRowObjectTableModel(rows);
        JXTable table = new JXTable(model);
        // sanity assert
        for (int i = 0; i < table.getRowCount(); i++) {
            assertEquals("even/uneven rows must be editable/notEditable " + i,
                    i % 2 == 0, table.isCellEditable(i, 0));
        }
        // need to chain two filters (to reach the "else" block in
        // filter.isCellEditable()
        PatternFilter filter = new PatternFilter("NOT.*", 0, 1);
        PatternFilter noFilter = new PatternFilter(".*", 0, 1);

        table.setFilters(new FilterPipeline(new Filter[] {noFilter, filter}));
        assertEquals("row count is half", rows / 2, table.getRowCount());
        for (int i = 0; i < table.getRowCount(); i++) {
            assertFalse("all rows must be not-editable " + i, table.isCellEditable(i, 0));
            
        }
    }

//----------------------- test data for exposing #171 (Tim Dilks)
    
    /**
     * create test model - all cells in even rows are editable, 
     * in odd rows are not editable. 
     * @param rows the number of rows to create
     * @return
     */
    private RowObjectTableModel createRowObjectTableModel(int rows) {
        List rowObjects = new ArrayList();
        for (int i = 0; i < rows; i++) {
            rowObjects.add(new RowObject("somedata" + i, i % 2 == 0));
        }
        return new RowObjectTableModel(rowObjects);
    }

    /**
     * test object to map in test table model.
     */
    static class RowObject {

        private String data1;
        private boolean editable;

        public RowObject(String data1, boolean editable) {
            this.data1 = data1;
            this.editable = editable;
        }

        public String getData1() {
            return data1;
        }
      
        public boolean isEditable() {
            return editable;
        }

    }

    /** 
     *  test TableModel wrapping RowObject.
     */
    static class RowObjectTableModel extends AbstractTableModel {

        List data;

        public RowObjectTableModel(List data) {
            this.data = data;
        }

        public RowObject getRowObject(int row) {
            return (RowObject) data.get(row);
        }
        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return data.size();
        }

        public Object getValueAt(int row, int col) {
            RowObject object = getRowObject(row);
            switch (col) {
                case 0 :
                    return object.getData1();
                case 1 :
                    return object.isEditable() ? "EDITABLE" : "NOT EDITABLE";
                default :
                    return null;
            }
        }

        public boolean isCellEditable(int row, int col) {
            return getRowObject(row).isEditable();
        }
    }

    
//    public void testLinkCellType() {
//        JXTable table = new JXTable(tableModel);
//
//        assertEquals(Link.class,
//                     table.getColumnClass(DynamicTableModel.IDX_COL_LINK));
//
//        TableCellRenderer renderer = table.getCellRenderer(0,
//            DynamicTableModel.IDX_COL_LINK);
//
//        // XXX note: this should be a LinkCellRenderer LinkRenderer is not public.
//        //      assertEquals(renderer.getClass(),
//        //             org.jdesktop.swing.table.TableCellRenderers$LinkRenderer.class);
//    }

    /**
     * Issue #167: IllegalStateException if re-setting filter while
     * sorting.
     *
     */
    public void testToggleFiltersWhileSorting() {
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, "AA" },
                new Object[] { Boolean.FALSE, "AB" },
                new Object[] { Boolean.FALSE, "AC" },
                new Object[] { Boolean.TRUE, "BA" },
                new Object[] { Boolean.FALSE, "BB" },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        final JXTable table = new JXTable(rowData, columnNames);
        Filter filterA = new PatternFilter("A.*", Pattern.CASE_INSENSITIVE, 1);
        // simulates the sequence of user interaction as described in 
        // the original bug report in 
        // http://www.javadesktop.org/forums/thread.jspa?messageID=56285
        table.setFilters(new FilterPipeline(new Filter[] {filterA}));
        table.setSorter(1);
        Filter filterB = new PatternFilter(".*", Pattern.CASE_INSENSITIVE, 1);
        table.setFilters(new FilterPipeline(new Filter[] {filterB}));
        table.setSorter(1);
    }

    /**
     * Issue #167: IllegalStateException if re-setting filter while
     * sorting.
     * Another variant ...
     *
     */
    public void testToggleFiltersWhileSortingLonger() {
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, "AA" },
                new Object[] { Boolean.FALSE, "AB" },
                new Object[] { Boolean.FALSE, "AC" },
                new Object[] { Boolean.TRUE, "BA" },
                new Object[] { Boolean.FALSE, "BB" },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        final JXTable table = new JXTable(rowData, columnNames);
        // simulates the sequence of user interaction as described in 
        // the follow-up bug report in 
        // http://www.javadesktop.org/forums/thread.jspa?messageID=56285
        table.setFilters(createFilterPipeline(false, 1));
        table.setSorter(0);
        table.setSorter(1);
        table.setFilters(createFilterPipeline(true, 1));
        table.setFilters(createFilterPipeline(false, 1));
        table.setSorter(0);
    }
    
    private FilterPipeline createFilterPipeline(boolean matchAll, int col) {
        Filter filter;
        if (matchAll) {
            filter = new PatternFilter(".*", Pattern.CASE_INSENSITIVE, col);
        } else {
           filter = new PatternFilter("A.*", Pattern.CASE_INSENSITIVE, col);
        }
        return new FilterPipeline(new Filter[] {filter});
        
    }
    /**
     * Issue #125: setting filter to null doesn't clean up.
     * 
     * A visual consequence is that the hidden (by the old
     * filters) rows don't appear. A not-so visual consequence
     * is that the sorter is out of synch and accessing a row in
     * the region outside of the formerly filtered. 
     *
     */
    public void testRemoveFilterWhileSorting() {
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, "AA" },
                new Object[] { Boolean.FALSE, "AB" },
                new Object[] { Boolean.FALSE, "AC" },
                new Object[] { Boolean.TRUE, "BA" },
                new Object[] { Boolean.FALSE, "BB" },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        final JXTable table = new JXTable(rowData, columnNames);
        int rows = table.getRowCount();
        Filter filterA = new PatternFilter("A.*", Pattern.CASE_INSENSITIVE, 1);
        table.setFilters(new FilterPipeline(new Filter[] {filterA}));
        table.setSorter(1);
        table.setFilters(null);
        assertEquals("rowCount must be original", rows, table.getRowCount());
        table.getValueAt(rows - 1, 0);

    
    }   

    /**
     * Issue #134: JXTable - default renderers not loaded.
     * To fix the issue the JXTable internal renderers' access scope
     *  was changed to public. Note: if the JTable internal renderers
     * access scope were to be widened then this test has to be changed
     * (the comparing class are hardcoded).
     *
     */
    public void testLazyRenderersByClass() {
        JXTable table = new JXTable();
        assertEquals("default Boolean renderer", JXTable.BooleanRenderer.class, table.getDefaultRenderer(Boolean.class).getClass());
        assertEquals("default Number renderer", JXTable.NumberRenderer.class, table.getDefaultRenderer(Number.class).getClass());
        assertEquals("default Double renderer", JXTable.DoubleRenderer.class, table.getDefaultRenderer(Double.class).getClass());
        assertEquals("default Date renderer", JXTable.DateRenderer.class, table.getDefaultRenderer(Date.class).getClass());
// PENDING: jw - Link should be moved out of databinding
//        assertEquals("default Link renderer", JXTable.LinkRenderer.class, table.getDefaultRenderer(Link.class).getClass());
        assertEquals("default Icon renderer", JXTable.IconRenderer.class, table.getDefaultRenderer(Icon.class).getClass());
    }

    /** 
     * Issue #150: setting filters must not re-create columns.
     *
     */
    public void testTableColumnsWithFilters() {
        JXTable table = new JXTable(tableModel);
        assertEquals("table columns are equal to columns of model", 
                tableModel.getColumnCount(), table.getColumnCount());
        TableColumn column = table.getColumnExt(0);
        table.removeColumn(column);
        int columnCountAfterRemove = table.getColumnCount();
        assertEquals("table columns must be one less than columns of model",
                tableModel.getColumnCount() - 1, columnCountAfterRemove);
        table.setFilters(new FilterPipeline(new Filter[] {
                new ShuttleSorter(1, false), // column 1, descending
        }));
        assertEquals("table columns must be unchanged after setting filter",
                columnCountAfterRemove, table.getColumnCount());
        
    }
    

    /**
     * Issue #191: sorting and custom renderer
     * not reproducible ...
     *
     */
    public void interactiveTestCustomRendererSorting() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumn column = table.getColumn("No.");
        TableCellRenderer renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                value = "# " + value ;
                Component comp = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row,  col );
                return comp;
            }
        };
        column.setCellRenderer(renderer);
        JFrame frame = wrapWithScrollingInFrame(table, "RendererSortingTest");
        frame.setVisible(true);  // RG: Changed from deprecated method show();
    }

    /**
     * JW: hmm.... what are we testing here?
     *
     */
//    public void interactiveTestLinkCell() {
//        JXTable table = new JXTable(tableModel);
//        JFrame frame = wrapWithScrollingInFrame(table, "TableLinkCell Test");
//        frame.setVisible(true);  // RG: Changed from deprecated method show();
//    }

    /**
     */
    public void interactiveTestToggleSortingEnabled() {
        final JXTable table = new JXTable(sortableTableModel);
//        URL url = JXTableUnitTest.class.getResource("resources/stringdata.csv");
//        System.out.println("url="+url);
//        DefaultTableModelExt data = new DefaultTableModelExt(url);
//        TableModelExtTextLoader loader = (TableModelExtTextLoader)data.getLoader();
//        loader.setFirstRowHeader(true);
//        loader.setColumnDelimiter(",");
//        final JXTable table = new JXTable(data);
//        try {
//            data.startLoading();
//        }
//        catch (Exception ex) {
//            fail("Exception in data load: " + ex.getMessage());
//        }
        Action toggleSortableAction = new AbstractAction("Toggle Sortable") {

            public void actionPerformed(ActionEvent e) {
                table.setSortable(!table.isSortable());
                
            }
            
        };
        table.getActionMap().put("toggleSortable", toggleSortableAction);
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke("F6"), "toggleSortable");
        JFrame frame = wrapWithScrollingInFrame(table, "ToggleSortingEnabled Test (press F6 to toggle)");
        frame.setVisible(true);  // RG: Changed from deprecated method show();
        
    }
    public void interactiveTestTableSizing1() {
        JXTable table = new JXTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(tableModel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnExt columns[] = new TableColumnExt[tableModel.getColumnCount()];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new TableColumnExt(i);
            table.addColumn(columns[i]);
        }
        columns[0].setPrototypeValue(new Integer(0));
        columns[1].setPrototypeValue("Simple String Value");
        columns[2].setPrototypeValue(new Integer(1000));
        columns[3].setPrototypeValue(Boolean.TRUE);
        columns[4].setPrototypeValue(new Date(100));
        columns[5].setPrototypeValue(new Float(1.5));
//        columns[6].setPrototypeValue(new Link("Sun Micro", "_blank",
//                                              tableModel.linkURL));
        columns[7].setPrototypeValue(new Integer(3023));
        columns[8].setPrototypeValue("John Doh");
        columns[9].setPrototypeValue("23434 Testcase St");
        columns[10].setPrototypeValue(new Integer(33333));
        columns[11].setPrototypeValue(Boolean.FALSE);

        table.setVisibleRowCount(12);

        JFrame frame = wrapWithScrollingInFrame(table, "TableSizing1 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableSizing2() {
        JXTable table = new JXTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(tableModel);

        TableColumnExt columns[] = new TableColumnExt[6];
        int viewIndex = 0;
        for (int i = columns.length - 1; i >= 0; i--) {
            columns[viewIndex] = new TableColumnExt(i);
            table.addColumn(columns[viewIndex++]);
        }
        columns[5].setHeaderValue("String Value");
        columns[5].setPrototypeValue("9999");
        columns[4].setHeaderValue("String Value");
        columns[4].setPrototypeValue("Simple String Value");
        columns[3].setHeaderValue("Int Value");
        columns[3].setPrototypeValue(new Integer(1000));
        columns[2].setHeaderValue("Bool");
        columns[2].setPrototypeValue(Boolean.FALSE);
        //columns[2].setSortable(false);
        columns[1].setHeaderValue("Date");
        columns[1].setPrototypeValue(new Date(0));
        //columns[1].setSortable(false);
        columns[0].setHeaderValue("Float");
        columns[0].setPrototypeValue(new Float(5.5));

        table.setRowHeight(24);
        table.setRowMargin(2);
        JFrame frame = wrapWithScrollingInFrame(table, "TableSizing2 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableAlternateHighlighter1() {
        JXTable table = new JXTable(tableModel);
        table.setRowHeight(22);
        table.setRowMargin(1);

        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(0, true) // column 0, ascending
        }));

        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            AlternateRowHighlighter.
            linePrinter,
        }));

        JFrame frame = wrapWithScrollingInFrame(table, "TableAlternateRowHighlighter1 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableAlternateRowHighlighter2() {
        JXTable table = new JXTable(tableModel);
        table.setRowHeight(22);
        table.setRowMargin(1);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(1, false), // column 1, descending
        }));

        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            AlternateRowHighlighter.classicLinePrinter,
        }));

        JFrame frame = wrapWithScrollingInFrame(table, "TableAlternateRowHighlighter2 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableSorter1() {
        JXTable table = new JXTable(sortableTableModel);
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            AlternateRowHighlighter.notePadBackground,
        }));
        table.setBackground(new Color(0xFF, 0xFF, 0xCC)); // notepad
        table.setGridColor(Color.cyan.darker());
        table.setRowHeight(22);
        table.setRowMargin(1);
        table.setShowHorizontalLines(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(0, true), // column 0, ascending
                                            new ShuttleSorter(1, true), // column 1, ascending
        }));

        JFrame frame = wrapWithScrollingInFrame(table, "TableSorter1 col 0= asc, col 1 = asc");
        frame.setVisible(true);

    }

    public void interactiveTestTableSorter2() {
        JXTable table = new JXTable(sortableTableModel);
        table.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        table.setGridColor(Color.cyan.darker());
        table.setRowHeight(22);
        table.setRowMargin(1);
        table.setShowHorizontalLines(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(0, true), // column 0, ascending
                                            new ShuttleSorter(1, false), // column 1, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TableSorter2 col 0 = asc, col 1 = desc");
        frame.setVisible(true);
    }

    public void interactiveTestTableSorter3() {
        JXTable table = new JXTable(sortableTableModel);
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new Highlighter(Color.orange, null),
        }));
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(1, true), // column 1, ascending
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TableSorter3 col 1 = asc, col 0 = desc");
        frame.setVisible(true);
    }

    public void interactiveTestTableSorter4() {
        JXTable table = new JXTable(sortableTableModel);
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new Highlighter(Color.orange, null),
        }));
        table.setFilters(new FilterPipeline(new Filter[] {
                new ShuttleSorter(0, false), // column 0, descending
                                            new ShuttleSorter(1, true), // column 1, ascending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TableSorter4 col 0 = des, col 1 = asc");
        frame.setVisible(true);
    }
    public void interactiveTestTablePatternFilter1() {
        JXTable table = new JXTable(tableModel);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("A.*", 0, 1)
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter1 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter2() {
        JXTable table = new JXTable(tableModel);
        table.setIntercellSpacing(new Dimension(2, 2));
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("S.*", 0, 1),
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter2 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter3() {
        JXTable table = new JXTable(tableModel);
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("S.*", 0, 1),
                                            new ShuttleSorter(1, false), // column 1, descending
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter3 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter4() {
        JXTable table = new JXTable(tableModel);
        table.setIntercellSpacing(new Dimension(3, 3));
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("A.*", 0, 1),
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter4 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter5() {
        // **** IMPORTANT TEST CASE for interaction between ****
        // **** PatternFilter and PatternHighlighter!!! ****
        JXTable table = new JXTable(tableModel);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("S.*", 0, 1),
                                            new ShuttleSorter(0, false), // column 0, descending
                                            new ShuttleSorter(1, true), // column 1, ascending
                                            new ShuttleSorter(3, false), // column 3, descending
        }));
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new PatternHighlighter(null, Color.red, "S.*", 0, 1),
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter5 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableViewProperties() {
        JXTable table = new JXTable(tableModel);
        table.setIntercellSpacing(new Dimension(15, 15));
        table.setRowHeight(48);
        JFrame frame = wrapWithScrollingInFrame(table, "TableViewProperties Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternHighlighter() {
        JXTable table = new JXTable(tableModel);
        table.setIntercellSpacing(new Dimension(15, 15));
        table.setRowHeight(48);
        table.setRowHeight(0, 96);
        table.setShowGrid(true);
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new PatternHighlighter(null, Color.red, "A.*", 0, 1),
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternHighlighter Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableColumnProperties() {
        JXTable table = new JXTable();
        table.setModel(tableModel);

        table.getTableHeader().setBackground(Color.green);
        table.getTableHeader().setForeground(Color.magenta);
        table.getTableHeader().setFont(new Font("Serif", Font.PLAIN, 10));

        ColumnHeaderRenderer headerRenderer = ColumnHeaderRenderer.createColumnHeaderRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBackground(Color.blue);
        headerRenderer.setForeground(Color.yellow);
        headerRenderer.setIcon(new Icon() {
            public int getIconWidth() {
                return 12;
            }

            public int getIconHeight() {
                return 12;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(Color.red);
                g.fillOval(0, 0, 10, 10);
            }
        });
        headerRenderer.setIconTextGap(20);
        headerRenderer.setFont(new Font("Serif", Font.BOLD, 18));

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumnExt column = table.getColumnExt(i);
            if (i % 3 > 0) {
                column.setHeaderRenderer(headerRenderer);
            }
            if (i % 2 > 0) {
                TableCellRenderer cellRenderer =
                    table.getNewDefaultRenderer(table.getColumnClass(i));
                if (cellRenderer instanceof JLabel || cellRenderer instanceof AbstractButton) {
                    JLabel labelCellRenderer = (JLabel)cellRenderer;
                    labelCellRenderer.setBackground(Color.gray);
                    labelCellRenderer.setForeground(Color.red);
                    labelCellRenderer.setHorizontalAlignment(JLabel.CENTER);
                    column.setCellRenderer(cellRenderer);
                }
            }
        }

        JFrame frame = wrapWithScrollingInFrame(table, "TableColumnProperties Test");
        frame.setVisible(true);
    }

    public static class DynamicTableModel extends AbstractTableModel {
        private Object columnSamples[];
        private Object columnSamples2[];
        public URL linkURL;

        public static final int IDX_COL_LINK = 6;

        public DynamicTableModel() {
            try {
                linkURL = new URL("http://www.sun.com");
            }
            catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }

            columnSamples = new Object[12];
            columnSamples[0] = new Integer(0);
            columnSamples[1] = "Simple String Value";
            columnSamples[2] = new Integer(1000);
            columnSamples[3] = Boolean.TRUE;
            columnSamples[4] = new Date(100);
            columnSamples[5] = new Float(1.5);
            columnSamples[IDX_COL_LINK] = "dummy link replacement"; //new Link("Sun Micro", "_blank", linkURL);
            columnSamples[7] = new Integer(3023);
            columnSamples[8] = "John Doh";
            columnSamples[9] = "23434 Testcase St";
            columnSamples[10] = new Integer(33333);
            columnSamples[11] = Boolean.FALSE;

            columnSamples2 = new Object[12];
            columnSamples2[0] = new Integer(0);
            columnSamples2[1] = "Another String Value";
            columnSamples2[2] = new Integer(999);
            columnSamples2[3] = Boolean.FALSE;
            columnSamples2[4] = new Date(333);
            columnSamples2[5] = new Float(22.22);
            columnSamples2[IDX_COL_LINK] = "dummy link";//new Link("Sun Web", "new_frame", linkURL);
            columnSamples[7] = new Integer(5503);
            columnSamples[8] = "Jane Smith";
            columnSamples[9] = "2343 Table Blvd.";
            columnSamples[10] = new Integer(2);
            columnSamples[11] = Boolean.TRUE;

        }

        public DynamicTableModel(Object columnSamples[]) {
            this.columnSamples = columnSamples;
        }

        public Class getColumnClass(int column) {
            return columnSamples[column].getClass();
        }

        public int getRowCount() {
            return 1000;
        }

        public int getColumnCount() {
            return columnSamples.length;
        }

        public Object getValueAt(int row, int column) {
            Object value;
            if (row % 3 == 0) {
                value = columnSamples[column];
            }
            else {
                value = columnSamples2[column];
            }
            return column == 0 ? new Integer(row >> 3) :
                column == 3 ? new Boolean(row % 2 == 0) : value;
        }

        public boolean isCellEditable(int row, int column) {
            return (column == 1);
        }

        public void setValueAt(Object aValue, int row, int column) {
            if (column == 1) {
                if (row % 3 == 0) {
                    columnSamples[column] = aValue;
                }
                else {
                    columnSamples2[column] = aValue;
                }
            }
            this.fireTableDataChanged();
        }
    }

    public static void main(String args[]) {
        JXTableUnitTest test = new JXTableUnitTest();
        try {
          test.runInteractiveTests();
          //  test.runInteractiveTests("interactive.*Siz.*");
         //   test.runInteractiveTests("interactive.*Render.*");
         //   test.runInteractiveTests("interactive.*Toggle.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }
}
