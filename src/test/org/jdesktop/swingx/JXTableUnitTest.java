/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.SizeSequence;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PipelineListener;
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.decorator.Sorter;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.util.AncientSwingTeam;

/**
* Split from old JXTableUnitTest - contains unit test
* methods only.
* 
*/
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

    public void testIndividualRowHeightAfterSetModel() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeight(0, 25);
        table.setModel(sortableTableModel);
        assertEquals("individual rowheight must be reset", 
                table.getRowHeight(), table.getRowHeight(0));
        
    }
    public void testIndividualRowHeight() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeight(0, 25);
        assertEquals(25, table.getRowHeight(0));
        assertEquals(table.getRowHeight(), table.getRowHeight(1));
        table.getFilters().setSorter(new ShuttleSorter(0, false));
        assertEquals(table.getRowHeight(), table.getRowHeight(1));
        assertEquals(25, table.getRowHeight(table.getRowCount() - 1));
        table.setRowHeight(table.getRowHeight());
        assertEquals(table.getRowHeight(), table.getRowHeight(table.getRowCount() - 1));
    }
    
    public void testResetIndividualRowHeight() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeight(0, 25);
        table.getFilters().setSorter(new ShuttleSorter(0, false));
        assertEquals("individual row height must be moved to last row", 
                25, table.getRowHeight(table.getRowCount() - 1));
        // reset
        table.setRowHeight(table.getRowHeight());
        assertEquals("individual row height must be reset", 
                table.getRowHeight(), table.getRowHeight(table.getRowCount() - 1));
    }
    
    public void testRowModelAccess() {
        JXTable table = new JXTable(sortableTableModel);
        table.setRowHeight(0, 25);
        SizeSequence sizing = table.getSuperRowModel();
        assertNotNull(sizing);
    }

    /**
     * Issue #64-swingx: setFilters(null) throws NPE if has selection.
     *
     */
    public void testSetNullFilters() {
        JXTable table = new JXTable(sortableTableModel);
        table.setRowSelectionInterval(0, 0);
        table.setFilters(null);
        assertEquals("selected row must be unchanged", 0, table.getSelectedRow());
    }
    
    /**
     * Issue #119: Exception if sorter on last column and setting
     * model with fewer columns.
     * 
     * JW: related to #53-swingx - sorter not removed on column removed. 
     *
     */
    public void testInteractiveSorterOnModelChange() {
        JXTable table = new JXTable(sortableTableModel);
        int columnCount = table.getColumnCount();
        table.setSorter(columnCount - 1);
        table.setModel(new DefaultTableModel(10, columnCount - 1));
        assertEquals(null, table.getFilters().getSorter());
    }
    /**
     * sanity testing while refactoring support
     * for interactive sorter.
     *
     */
    public void testSorterToPipeline() {
        JXTable table = new JXTable(sortableTableModel);
        table.setSorter(0);
        TableColumnExt columnX = table.getColumnExt(0);
        assertEquals("interactive sorter must be same as sorter in column", 
                columnX.getSorter(), table.getFilters().getSorter());
        table.resetSorter();
        assertEquals("interactive sorter must be null", null, table.getFilters().getSorter());
    }
    
    /**
     * Issue #53-swingx: interactive sorter not removed if column removed.
     *
     */
    public void testSorterAfterColumnRemoved() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.setSorter(0);
        table.removeColumn(columnX);
        assertEquals("sorter must be removed when column removed", null, table.getFilters().getSorter());
        
    }
    
    /**
     * interactive sorter must be active if column is hidden.
     *
     */
    public void testSorterAfterColumnHidden() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.setSorter(0);
        columnX.setVisible(false);
        assertEquals("interactive sorter must be same as sorter in column", 
                columnX.getSorter(), table.getFilters().getSorter());
        
    }
    
    /**
     * Issue #54-swingx: hidden columns not removed.
     * 
     * NOTE: this is testing internal behaviour - don't!
     * it bubbles up to public behaviour in setModel(), 
     * check if anywhere else?
     *
     */
    public void testRemoveAllColumns() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setVisible(false);
        table.removeColumns();
        assertEquals("all columns must have been removed", 
                table.getColumnCount(), table.getColumnCount(true));
    }
    
    /**
     * Issue #54: hidden columns not removed on setModel.
     *
     */
    public void testRemoveAllColumsAfterModelChanged() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setVisible(false);
        table.setModel(new DefaultTableModel());
        assertEquals("all columns must have been removed", 
                table.getColumnCount(), table.getColumnCount(true));
        assertEquals("sorter must be removed when column removed", null, table.getFilters().getSorter());
    }
    
    /**
     * testing contract of getColumnExt.
     *
     */
    public void testColumnExt() {
        JXTable table = new JXTable(sortableTableModel);
        /// arrgghhh... autoboxing ?
//        Object zeroName = table.getColumn(0).getIdentifier();
        Object zeroName = table.getColumnModel().getColumn(0).getIdentifier();
        Object oneName = table.getColumnModel().getColumn(1).getIdentifier();
        TableColumn column = table.getColumn(zeroName);
        ((TableColumnExt) column).setVisible(false);
        try {
            // access the invisible column by the inherited method
            table.getColumn(zeroName);
            fail("table.getColumn(identifier) guarantees to fail if identifier "
                    + "is unknown or column is hidden");
        } catch (Exception e) {
            // this is what we expect
        }
        // access the invisible column by new method
        TableColumnExt columnZero = table.getColumnExt(zeroName);
        // sanity..
        assertNotNull(columnZero);
        int viewIndexZero = table.convertColumnIndexToView(columnZero
                .getModelIndex());
        assertTrue("viewIndex must be negative for invisible", viewIndexZero < 0);
        // a different way to state the same
        assertEquals(columnZero.isVisible(), viewIndexZero >= 0);
        TableColumnExt columnOne = table.getColumnExt(oneName);
        // sanity..
        assertNotNull(columnOne);
        int viewIndexOne = table.convertColumnIndexToView(columnOne
                .getModelIndex());
        assertTrue("viewIndex must be positive for visible", viewIndexOne >= 0);
        assertEquals(columnOne.isVisible(), viewIndexOne >= 0);
    }
    /**
     * Issue #189, #214: Sorter fails if content is comparable with mixed types
     * 
     */
    public void testMixedComparableTypes() {
        
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, new Integer(2) },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        DefaultTableModel model =  new DefaultTableModel(rowData, columnNames);
        final JXTable table = new JXTable(model);
        table.setSorter(1);
    }   
    
    /**
     * Issue #189, #214: Sorter fails if content is 
     * mixed comparable/not comparable
     *
     */
    public void testMixedComparableTypesWithNonComparable() {
        
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, new Integer(2) },
                new Object[] { Boolean.TRUE, new Object() } };
        String[] columnNames = new String[] { "Critical", "Task" };
        DefaultTableModel model =  new DefaultTableModel(rowData, columnNames);
        final JXTable table = new JXTable(model);
        table.setSorter(1);
    }   

    /**
     * Issue #196: backward search broken.
     *
     */
    public void testBackwardSearch() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        int found = table.search(Pattern.compile(lastName), -1, true);
        assertEquals(row, found);
    }

    /**
     * Issue #187: filter update removes interactive sorter.
     *
     */
    public void testFilterUpdateKeepsSorter() {
        int rowCount = 20;
        int firstValue = 0;
        JXTable table = new JXTable(createAscendingModel(firstValue, rowCount));
        table.setSorter(0);
        // sort descending
        table.setSorter(0);
        Object value = table.getValueAt(0, 0);
        assertEquals("highest value", value, firstValue + rowCount - 1);
        PatternFilter filter = new PatternFilter(".*", 0, 0);
        // set a filter
        table.setFilters(new FilterPipeline(new Filter[] {filter}));
        assertEquals("highest value unchanged", value, table.getValueAt(0, 0 ));
        // update the filter
        filter.setPattern("1.*", 0);
        assertTrue("sorter must be active", 
                ((Integer) table.getValueAt(0, 0)).intValue() > ((Integer) table.getValueAt(1, 0)));
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
        // sanity assert - no longer 1: Selection adds listener as well
        // no longer 2: Rowsizing adds listener
        // no longer sane - remove!!
         assertEquals(3, listenerCount);
        // JW: no longer valid assumption - the pipelineListener now is an 
        // implementation detail of JXTable
 //       assertEquals(table, l);
        table.setModel(createAscendingModel(0, 20));
        assertEquals("pipeline listener count must not change after setModel", listenerCount, table.getFilters().getPipelineListeners().length);
        
    }
    /**
     * Issue #174: componentAdapter.hasFocus() looks for anchor instead of lead.
     *
     */
    public void testLeadFocusCell() {
        final JXTable table = new JXTable();
        table.setModel(createAscendingModel(0, 10));
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
     * Issue #33-swingx: selection not restored after refresh of interactive sorter.
     *
     */
    public void testSelectionOnSorterRefresh() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setSorter(0);
        Sorter sorter = table.getSorter(0);
        // sanity assert
        assertTrue(sorter.isAscending());
        // select the first row
        table.setRowSelectionInterval(0, 0);
        sorter.setAscending(false);
        assertEquals("last row must be selected", table.getRowCount() - 1, table.getSelectedRow());
    }
    /**
     * Issue #173: 
     * ArrayIndexOOB if replacing model with one containing
     * fewer rows and the "excess" is selected.
     *
     */
    public void testSelectionAndToggleModel() {
        JXTable table = new JXTable();
        table.setModel(createAscendingModel(0, 10));
        // sort first column
        table.setSorter(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.setSorter(0);
        // set model with less rows
        table.setModel(createAscendingModel(0, 8));
        
    }
    
    /**
     * testing selection and adding rows.
     * 
     *
     */
    public void testSelectionAndAddRows() {
        JXTable table = new JXTable();
        DefaultTableModel model = createAscendingModel(0, 10);
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
        DefaultTableModel model = createAscendingModel(0, 10);
        table.setModel(model);
        // sort first column
        table.setSorter(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.setSorter(0);
        model.removeRow(0);
    }

    public void testDeleteRowAboveIndividualRowHeight() {
        DefaultTableModel model = createAscendingModel(0, 10);
        JXTable table = new JXTable(model);
        int selectedRow = table.getRowCount() - 1;
        table.setRowHeight(selectedRow, 25);
        table.setSorter(0);
        assertEquals("last row is individual", 25, table.getRowHeight(selectedRow));
        model.removeRow(0);
        assertEquals("last row is individual", 25, table.getRowHeight(selectedRow - 1));
        
    }
    /**
     * Issue #223
     * 
     * test if selection is updated on remove row above selection.
     */
    public void testDeleteRowAboveSelection() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline
        table.setSorter(0);
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.removeRow(0);
        assertEquals("last row must still be selected after remove be selected", table.getRowCount() - 1, table.getSelectedRow());
        
    }

    /**
     * Issue #223
     * 
     * test if selection is updated on add row above selection.
     */
    public void testAddRowAboveSelection() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.insertRow(0, new Object[table.getColumnCount()]);
        assertEquals("last row must still be selected after add above", table.getRowCount() - 1, table.getSelectedRow());
    }

    public void testAddRowAboveIndividualRowHeigh() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowHeight(selectedRow, 25);
        assertEquals("last row must have indy rowheight", 25, table.getRowHeight(selectedRow));
        ascendingModel.insertRow(0, new Object[table.getColumnCount()]);
        assertEquals("last row must still have indy rowheight after add above", 25, table.getRowHeight(selectedRow + 1));
    }

    /**
     * Issue #223
     * test if selection is updated on add row above selection.
     *
     */
    public void testAddRowAboveSelectionInvertedOrder() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        // select the last row in view coordinates
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline - ascending, no change
        table.setSorter(0);
        // revert order 
        table.setSorter(0);
        assertEquals("first row must be selected", 0, table.getSelectedRow());
        // remove row in model coordinates
        Object[] row = new Integer[table.getColumnCount()];
        // insert high value
        row[0] = new Integer(100);
        ascendingModel.addRow(row);
        // selection must be moved one below
        assertEquals("selection must be incremented by one ", 1, table.getSelectedRow());
        
    }

    public void testAddRowAboveIndividualRowHeightInvertedOrder() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        // select the last row in view coordinates
        int selectedRow = table.getRowCount() - 1;
        table.setRowHeight(selectedRow, 25);
        // set a pipeline - ascending, no change
        table.setSorter(0);
        // revert order 
        table.setSorter(0);
        assertEquals("first row must have indy rowheight", 25, table.getRowHeight(0));
        // remove row in model coordinates
        Object[] row = new Integer[table.getColumnCount()];
        // insert high value
        row[0] = new Integer(100);
        ascendingModel.addRow(row);
        // selection must be moved one below
        assertEquals("row with indy height must be incremented by one ", 25, table.getRowHeight(1));
        
    }

    
    /**
     * Issue #223
     * test if selection is updated on remove row above selection.
     *
     */
    public void testDeleteRowAboveSelectionInvertedOrder() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        // select the last row in view coordinates
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline - ascending, no change
        table.setSorter(0);
        // revert order 
        table.setSorter(0);
        assertEquals("first row must be selected", 0, table.getSelectedRow());
        // remove row in model coordinates
        ascendingModel.removeRow(0);
        assertEquals("first row must still be selected after remove ", 0, table.getSelectedRow());
        
    }
    /**
     * Issue #223
     * test if selection is kept if row below selection is removed.
     *
     */
    public void testDeleteRowBelowSelection() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline
        table.setSorter(0);
        // revert order - fails... track down
//        table.setSorter(0);
        assertEquals("first row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.removeRow(selectedRow + 1);
        assertEquals("first row must still be selected after remove", selectedRow, table.getSelectedRow());
        
    }

    /**
     * Issue #223
     * test if selection is kept if row below selection is removed.
     *
     */
    public void testDeleteRowBelowSelectionInvertedOrder() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline
        table.setSorter(0);
        // revert order - fails... track down
        table.setSorter(0);
        assertEquals("last row must be selected", table.getRowCount() - 1, table.getSelectedRow());
        ascendingModel.removeRow(selectedRow + 1);
        assertEquals("last row must still be selected after remove", table.getRowCount() - 1, table.getSelectedRow());
        
    }
    /**
     * Issue #223
     * test if selection is kept if row in selection is removed.
     *
     */
    public void testDeleteLastRowInSelection() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        int lastSelectedRow = 1;
        table.setRowSelectionInterval(selectedRow, lastSelectedRow);
        // set a pipeline
        table.setSorter(0);
        int[] selectedRows = table.getSelectedRows();
        for (int i = selectedRow; i <= lastSelectedRow; i++) {
            assertEquals("row must be selected " + i, i, selectedRows[i]);
            
        }
        ascendingModel.removeRow(lastSelectedRow);
        int[] selectedRowsAfter = table.getSelectedRows();
        for (int i = selectedRow; i < lastSelectedRow; i++) {
            assertEquals("row must be selected " + i, i, selectedRowsAfter[i]);
            
        }
        assertFalse("removed row must not be selected " + lastSelectedRow, table.isRowSelected(lastSelectedRow));
        
    }

    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first column
     * starting from startRow.
     * @param startRow the value of the first row
     * @param count the number of rows
     * @return
     */
    private DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 4) {
            public Class getColumnClass(int column) {
                return column == 0 ? Integer.class : super.getColumnClass(column);
            }
        };
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
        assertEquals("default LinkModel renderer", LinkRenderer.class, table.getDefaultRenderer(LinkModel.class).getClass());
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
    
//-------------------------- tests for moving column control into swingx

    
    /**
     * hmm... sporadic ArrayIndexOOB after sequence:
     * 
     * filter(column), sort(column), hide(column), setFilter(null)
     *
     */
    public void testColumnControlAndFilters() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setColumnControlVisible(true);
        Filter filter = new PatternFilter(".*e.*", 0, 0);
        table.setFilters(new FilterPipeline(new Filter[] {filter}));
        // needed to make public in JXTable for testing
        //   table.getTable().setSorter(0);
        table.getColumnExt(0).setVisible(false);
        table.setFilters(null);

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
            columnSamples[IDX_COL_LINK] = new LinkModel("Sun Micro", "_blank", linkURL);
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
            columnSamples2[IDX_COL_LINK] = new LinkModel("Sun Web", "new_frame", linkURL);
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
            return 50;
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
}
