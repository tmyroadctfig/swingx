/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTableUnitTest.DynamicTableModel;
import org.jdesktop.swingx.JXTableUnitTest.RowObject;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ComponentAdapterTest.JXTableT;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class JXTableSortRevamp extends InteractiveTestCase {

    protected DynamicTableModel tableModel = null;
    protected TableModel sortableTableModel;

    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;
    // stored ui properties to reset in teardown
    private Object uiTableRowHeight;


    /**
     * A custom StringValue for Color. Maps to a string composed of the
     * prefix "R/G/B: " and the Color's rgb value.
     */
    private StringValue sv;

    @Before
       public void setUpJu4() throws Exception {
        // just a little conflict between ant and maven builds
        // junit4 @before methods needs to be public, while
        // junit3 setUp() inherited from super is protected
      this.setUp();
    }
    
    @Override
    protected void setUp() throws Exception {
       super.setUp();
        // set loader priority to normal
        if (tableModel == null) {
            tableModel = new DynamicTableModel();
        }
        sortableTableModel = new AncientSwingTeam();
        // make sure we have the same default for each test
        defaultToSystemLF = true;
        setSystemLF(defaultToSystemLF);
        uiTableRowHeight = UIManager.get("JXTable.rowHeight");
        sv = createColorStringValue();
    }

    
    @Override
    @After
       public void tearDown() throws Exception {
        UIManager.put("JXTable.rowHeight", uiTableRowHeight);
        super.tearDown();
    }

    
    public static void main(String[] args) {
        JXTableSortRevamp test = new JXTableSortRevamp();
        try {
//            test.runInteractiveTests();
            test.runInteractiveTests("interactive.*NPE.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Issue #??- ComponentAdapter's default implementation does not
     *    return the value at the adapter's view state.
     * 
     * Clarified the documentation: the assumption for the base implementation
     * is that model coordinates == view coordinates, that is it's up to 
     * subclasses to implement the model correctly if they support different
     * coordinate systems.
     *
     */
    @Test
    public void testComponentAdapterCoordinatesDocumentation() {
        fail("JXTable - swingx filtering/sorting disabled");
        final JXTable table = new JXTable(createAscendingModel(0, 10));
        Object originalFirstRowValue = table.getValueAt(0,0);
        Object originalLastRowValue = table.getValueAt(table.getRowCount() - 1, 0);
        assertEquals("view row coordinate equals model row coordinate", 
                table.getModel().getValueAt(0, 0), originalFirstRowValue);
        // sort first column - actually does not change anything order 
        table.toggleSortOrder(0);
        // sanity asssert
        assertEquals("view order must be unchanged ", 
                table.getValueAt(0, 0), originalFirstRowValue);
        // invert sort
        table.toggleSortOrder(0);
        // sanity assert
        assertEquals("view order must be reversed changed ", 
                table.getValueAt(0, 0), originalLastRowValue);
        ComponentAdapter adapter = new ComponentAdapter(table) {

            @Override
            public Object getColumnIdentifierAt(int columnIndex) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getColumnIndex(Object identifier) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Object getFilteredValueAt(int row, int column) {
                return getValueAt(table.convertRowIndexToModel(row), column);
            }

            @Override
            public Object getValueAt(int row, int column) {
                return table.getModel().getValueAt(row, column);
            }
            
            @Override
            public Object getValue() {
                return getValueAt(table.convertRowIndexToModel(row), viewToModel(column));
            }

            @Override
            public boolean hasFocus() {
                return false;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public boolean isEditable() {
                return false;
            }
            
            @Override
            public boolean isSelected() {
                return false;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
              
            }
            
        };
        assertEquals("adapter filteredValue expects row view coordinates", 
                table.getValueAt(0, 0), adapter.getFilteredValueAt(0, 0));
        // adapter coordinates are view coordinates
        adapter.row = 0;
        adapter.column = 0;
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                table.getValueAt(0, 0), adapter.getValue());
        
        assertEquals(adapter.getFilteredValueAt(0, adapter.getColumnCount() -1), 
                adapter.getValue(adapter.getColumnCount()-1));
        
        
    }
    /**
     * Test assumptions of accessing table model/view values through
     * the table's componentAdapter.
     * 
     * PENDING JW: revisit - diff from above method?
     */
    @Test
    public void testComponentAdapterCoordinates() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 10));
        Object originalFirstRowValue = table.getValueAt(0,0);
        Object originalLastRowValue = table.getValueAt(table.getRowCount() - 1, 0);
        assertEquals("view row coordinate equals model row coordinate", 
                table.getModel().getValueAt(0, 0), originalFirstRowValue);
        // sort first column - actually does not change anything order 
        table.toggleSortOrder(0);
        // sanity asssert
        assertEquals("view order must be unchanged ", 
                table.getValueAt(0, 0), originalFirstRowValue);
        // invert sort
        table.toggleSortOrder(0);
        // sanity assert
        assertEquals("view order must be reversed changed ", 
                table.getValueAt(0, 0), originalLastRowValue);
        ComponentAdapter adapter = table.getComponentAdapter();
        assertEquals("adapter filteredValue expects row view coordinates", 
                table.getValueAt(0, 0), adapter.getFilteredValueAt(0, 0));
        // adapter coordinates are view coordinates
        adapter.row = 0;
        adapter.column = 0;
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                table.getValueAt(0, 0), adapter.getValue());
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                table.getValueAt(0, 0), adapter.getValue(0));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test PatternFilter uses getStringXX
     */
    @Test
    public void testTableGetStringUsedInPatternFilter() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
//        PatternFilter filter = new PatternFilter("R/G/B: -2", 0, 2);
//        table.setFilters(new FilterPipeline(filter));
        assertTrue(table.getRowCount() > 0);
        assertEquals(sv.getString(table.getValueAt(0, 2)), table.getStringAt(0, 2));
    }




    /**
     * Issue #924-swingx: problems indy rowheight and filters.
     * 
     * ArrayIndexOutOfBounds on insert. 
     *
     */
    @Test
    public void testIndividualRowHeightAndFilterInsert() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 50));
        table.setRowHeight(1, 100);
//        final FilterPipeline filterPipeline = new FilterPipeline(new PatternFilter("[123]",0,0));
//        table.setFilters(filterPipeline);
        // sanity
        assertEquals(1, table.getValueAt(0, 0));
        ((DefaultTableModel) table.getModel()).addRow(new Object[] {1, null, null, null});
    }

    /**
     * Issue #924-swingx: problems indy rowheight and filters.
     * 
     * ArrayIndexOutOfBounds on remove. 
     *
     */
    @Test
    public void testIndividualRowHeightAndFilterRemove() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 50));
//        table.setRowHeight(1, 100);
//        final FilterPipeline filterPipeline = new FilterPipeline(new PatternFilter("[123]",0,0));
//        table.setFilters(filterPipeline);
        // sanity
        assertEquals(1, table.getValueAt(0, 0));
        ((DefaultTableModel) table.getModel()).removeRow(table.getModel().getRowCount() - 1);
    }

    /**
     * Issue #530-swingx: problems indy rowheight and filters
     *
     */
    @Test
    public void testIndividualRowHeightAndFilter() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 50));
        table.setRowHeight(1, 100);
//        final FilterPipeline filterPipeline = new FilterPipeline(new PatternFilter("[123]",0,0));
//        table.setFilters(filterPipeline);
        // sanity
        assertEquals(1, table.getValueAt(0, 0));
        assertEquals(100, table.getRowHeight(0));
    }

    /**
     * Issue 373-swingx: table must unsort column on sortable change.
     * 
     * Here we test if switching sortable to false on the sorted column resets
     * the sorting, special case hidden column. This fails because columnModel
     * doesn't fire property change events for hidden columns (see Issue
     * #369-swingx).
     * 
     */
    @Test
    public void testTableUnsortedColumnOnHiddenColumnSortableChange() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        Object identifier = columnExt.getIdentifier();
        table.toggleSortOrder(identifier);
//        assertTrue(table.getSortOrder(identifier).isSorted());
//        columnExt.setVisible(false);
//        assertTrue(table.getSortOrder(identifier).isSorted());
//        columnExt.setSortable(false);
//        assertFalse("table must have unsorted column on sortable change", table
//                .getSortOrder(identifier).isSorted());
    }


    /**
     * Issue 373-swingx: table must unsort column on sortable change.
     *
     * Here we test if switching sortable to false on the sorted column
     * resets the sorting.
     * 
     */
    @Test
    public void testTableUnsortedColumnOnColumnSortableChange() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
//        table.toggleSortOrder(0);
//        assertTrue(table.getSortOrder(0).isSorted());
//        columnExt.setSortable(false);
//        assertFalse("table must have unsorted column on sortable change", 
//                table.getSortOrder(0).isSorted());
    }

    /**
     * Issue 373-swingx: table must unsort column on sortable change.
     *
     * Here we test if switching sortable to false on unsorted column has
     * no effect.
     */
    @Test
    public void testTableSortedColumnOnNotSortedColumnSortableChange() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(10, 2);
        int unsortedColumn = 1;
        TableColumnExt columnExt = table.getColumnExt(unsortedColumn);
        table.toggleSortOrder(0);
//        assertTrue(table.getSortOrder(0).isSorted());
//        columnExt.setSortable(false);
//        assertTrue("table must keep sortorder on unsorted column sortable change", 
//                table.getSortOrder(0).isSorted());
    }



    /**
     * programmatic sorting of hidden column (through table api).
     * 
     */
    @Test
    public void testSetSortOrderHiddenColumn() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(new AncientSwingTeam());
        Object identifier = "Last Name";
        TableColumnExt columnExt = table.getColumnExt(identifier);
        columnExt.setVisible(false);
        table.setSortOrder(identifier, SortOrder.ASCENDING);
        assertEquals("sorted column must be at " + identifier, columnExt, table.getSortedColumn());
        assertEquals("column must be sorted after setting sortOrder on " + identifier, SortOrder.ASCENDING, table.getSortOrder(identifier));
        Object otherIdentifier = "First Name";
        table.setSortOrder(otherIdentifier, SortOrder.UNSORTED);
        assertNull("table must be unsorted after resetting sortOrder on " + otherIdentifier,
                table.getSortedColumn());
    }

    /**
     * added xtable.setSortOrder(Object, SortOrder)
     * 
     */
    @Test
    public void testSetSortOrderByIdentifier() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(new AncientSwingTeam());
        Object identifier = "Last Name";
        TableColumnExt columnExt = table.getColumnExt(identifier);
        table.setSortOrder(identifier, SortOrder.ASCENDING);
        assertEquals("sorted column must be at " + identifier, columnExt, table.getSortedColumn());
        assertEquals("column must be sorted after setting sortOrder on " + identifier, SortOrder.ASCENDING, table.getSortOrder(identifier));
        Object otherIdentifier = "First Name";
        table.setSortOrder(otherIdentifier, SortOrder.UNSORTED);
        assertNull("table must be unsorted after resetting sortOrder on " + otherIdentifier,
                table.getSortedColumn());
    }
    
    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    @Test
    public void testSetSortOrderByIdentifierColumnNotSortable() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(new AncientSwingTeam());
        Object identifier = "Last Name";
        TableColumnExt columnX = table.getColumnExt(identifier);
        //  make column not sortable.
        columnX.setSortable(false);
        table.setSortOrder(identifier, SortOrder.ASCENDING);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(0));
    }

    /**
     * testing new sorter api: 
     * toggleSortOrder(Object), resetSortOrder.
     *
     */
    @Test
    public void testToggleSortOrderByIdentifier() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(sortableTableModel);
        Object firstColumn = "First Name";
        Object secondColumn = "Last Name";
        assertSame(SortOrder.UNSORTED, table.getSortOrder(secondColumn));
        table.toggleSortOrder(firstColumn);
        assertSame(SortOrder.ASCENDING, table.getSortOrder(firstColumn));
        // sanity: other columns uneffected
        assertSame(SortOrder.UNSORTED, table.getSortOrder(secondColumn));
        table.toggleSortOrder(firstColumn);
        assertSame(SortOrder.DESCENDING, table.getSortOrder(firstColumn));
        table.resetSortOrder();
        assertSame(SortOrder.UNSORTED, table.getSortOrder(firstColumn));
    }

    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    @Test
    public void testToggleSortOrderByIdentifierColumnNotSortable() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(new AncientSwingTeam());
        Object identifier = "Last Name";
        TableColumnExt columnX = table.getColumnExt(identifier);
        // old way: make column not sortable.
        columnX.setSortable(false);
        table.toggleSortOrder(identifier);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(identifier));
       
    }

    
    /**
     * added xtable.setSortOrder(int, SortOrder)
     * 
     */
    @Test
    public void testSetSortOrder() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(new AncientSwingTeam());
        int col = 0;
        TableColumnExt columnExt = table.getColumnExt(col);
        table.setSortOrder(col, SortOrder.ASCENDING);
        assertEquals("sorted column must be at " + col, columnExt, table.getSortedColumn());
        assertEquals("column must be sorted after setting sortOrder on " + col, SortOrder.ASCENDING, table.getSortOrder(col));
        int otherColumn = col + 1;
        table.setSortOrder(otherColumn, SortOrder.UNSORTED);
        assertNull("table must be unsorted after resetting sortOrder on " + otherColumn,
                table.getSortedColumn());
    }
    
    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    @Test
    public void testSetSortOrderColumnNotSortable() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnX = table.getColumnExt(0);
        // old way: make column not sortable.
        columnX.setSortable(false);
        table.setSortOrder(0, SortOrder.ASCENDING);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(0));
       
    }

    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    @Test
    public void testToggleSortOrderColumnNotSortable() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnX = table.getColumnExt(0);
        // old way: make column not sortable.
        columnX.setSortable(false);
        table.toggleSortOrder(0);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(0));
       
    }
   
    
    /**
     * JXTable has responsibility to guarantee usage of 
     * TableColumnExt comparator.
     * 
     */
    @Test
    public void testComparatorToPipeline() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setComparator(Collator.getInstance());
        table.toggleSortOrder(0);
//        SortKey sortKey = SortKey.getFirstSortKeyForColumn(table.getFilters().getSortController().getSortKeys(), 0);
//        assertNotNull(sortKey);
//        assertEquals(columnX.getComparator(), sortKey.getComparator());
    }

    /**
     * resetSortOrders didn't check for tableHeader != null.
     * Didn't show up before new sorter api because method was protected and 
     * only called from JXTableHeader.
     *
     */
    @Test
    public void testResetSortOrderNPE() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(sortableTableModel);
        table.setTableHeader(null);
        table.resetSortOrder();
    }

    /**
     * testing new sorter api: 
     * getSortOrder(int), toggleSortOrder(int), resetSortOrder().
     *
     */
    @Test
    public void testToggleSortOrder() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(sortableTableModel);
        assertSame(SortOrder.UNSORTED, table.getSortOrder(0));
        table.toggleSortOrder(0);
        assertSame(SortOrder.ASCENDING, table.getSortOrder(0));
        // sanity: other columns uneffected
        assertSame(SortOrder.UNSORTED, table.getSortOrder(1));
        table.toggleSortOrder(0);
        assertSame(SortOrder.DESCENDING, table.getSortOrder(0));
        table.resetSortOrder();
        assertSame(SortOrder.UNSORTED, table.getSortOrder(0));
    }
    

    /**
     * Issue #232-swingx: selection not kept if selectionModel had been changed.
     *
     */
    @Test
    public void testSelectionMapperUpdatedOnSelectionModelChange() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable();
        ListSelectionModel model = new DefaultListSelectionModel();
        table.setSelectionModel(model);
//        assertEquals(model, table.getSelectionMapper().getViewSelectionModel());
    }

    /**
     * 
     * Issue #173-swingx.
     * 
     * table.setFilters() leads to selectionListener
     * notification while internal table state not yet stable.
     * 
     * example (second one, from Nicola):
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    @Test
    public void testSelectionListenerNotification() {
        fail("JXTable - swingx filtering/sorting disabled");
        final JXTable table = new JXTable(createAscendingModel(0, 20));
        final int modelRow = 0;
        // set a selection 
        table.setRowSelectionInterval(modelRow, modelRow);
        ListSelectionListener l = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int viewRow = table.getSelectedRow(); 
                assertTrue("view index visible", viewRow >= 0);
                // JW: the following checks if the reverse conversion succeeds
                table.convertRowIndexToModel(viewRow);
                
            }
            
        };
        table.getSelectionModel().addListSelectionListener(l);
//        table.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
    }

    
    /**
     * 
     * Issue #172-swingx.
     * 
     * The sequence: clearSelection() - setFilter - setRowSelectionInterval
     * throws Exception.
     * 
     * example (first, from Diego):
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    @Test
    public void testClearSelectionAndFilter() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 20));
        int modelRow = table.getRowCount() - 1;
        // set a selection near the end - will be invalid after filtering
        table.setRowSelectionInterval(modelRow, modelRow);
        table.clearSelection();
//        table.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("9", 0, 0) }));
        int viewRow = table.convertRowIndexToView(modelRow);
        assertTrue("view index visible", viewRow >= 0);
        table.setRowSelectionInterval(viewRow, viewRow);
    }

    /**
     * 
     * Issue #172-swingx.
     * 
     * The sequence:  setFilter - clearSelection() - setRowSelectionInterval
     * is okay. 
     * 
     * Looks like in SelectionMapper.setPipeline needs to check for empty 
     * selection in view selectionModel and update the anchor/lead (in 
     * the view selection) to valid values! 
     * Now done in SelectionMapper.clearViewSelection, which fixes this test.
     * 
     * example (first, from Diego):
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    @Test
    public void testFilterAndClearSelection() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 20));
        int modelRow = table.getRowCount() - 1;
        // set a selection near the end - will be invalid after filtering
        table.setRowSelectionInterval(modelRow, modelRow);
//        table.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("9", 0, 0) }));
        table.clearSelection();
        int viewRow = table.convertRowIndexToView(modelRow);
        assertTrue("view index visible", viewRow >= 0);
        table.setRowSelectionInterval(viewRow, viewRow);
    }
    /**
     * 
     * Issue #172-swingx. 
     * 
     * 
     * reported exception if row removed (Ray, at the end of)
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    @Test
    public void testSelectionAndRemoveRowOfMisbehavingModel() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public void fireTableRowsDeleted(int firstRow, int lastRow) {
                fireTableStructureChanged();
            }
            
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        JXTable table = new JXTable(model);
        int modelRow = table.getRowCount() - 1;
        table.toggleSortOrder(0);
        // set a selection near the end - will be invalid after filtering
        table.setRowSelectionInterval(modelRow, modelRow);
        model.removeRow(modelRow);
        int lastRow = table.getModel().getRowCount() - 1;
        int viewRow = table.convertRowIndexToView(lastRow);
        assertTrue("view index visible", viewRow >= 0);
        table.setRowSelectionInterval(viewRow, viewRow);
    }


    
    /**
     * 
     * Issue #172-swingx. 
     * 
     * 
     * reported exception if row removed (Ray, at the end of)
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    @Test
    public void testSelectionAndRemoveRowOfMisbehavingModelRay() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public void fireTableRowsDeleted(int firstRow, int lastRow) {
                fireTableStructureChanged();
            }
            
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        JXTable table = new JXTable(model);
        int modelRow = table.getRowCount() - 1;
//        Filter[] filters = new Filter[] {new ShuttleSorter(0, true)};
//        FilterPipeline filterPipe = new FilterPipeline(filters);
//        table.setFilters(filterPipe);        
        // set a selection near the end - will be invalid after filtering
        table.setRowSelectionInterval(modelRow, modelRow);
        model.removeRow(modelRow);
        int lastRow = table.getModel().getRowCount() - 1;
        int viewRow = table.convertRowIndexToView(lastRow);
        // JW: here's the problem - the anchor of the selectionModel is not updated correctly
        // after removing the last model row
        // not longer valid (as of 50u6)
//        assertEquals("anchor must be last", lastRow, table.getSelectionModel().getAnchorSelectionIndex());
        assertTrue("view index visible", viewRow >= 0);
        assertEquals("view index is last", viewRow, lastRow);
        table.setRowSelectionInterval(viewRow, viewRow);
    }



    /**
     * Issue #167-swingx: table looses individual row height 
     * on update.
     * 
     * This happened if the indy row is filtered and the selection is empty - 
     * updateSelectionAndRowHeight case analysis was incomplete. Fixed.
     *
     */
    @Test
    public void testKeepRowHeightOnUpdateAndEmptySelection() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(10, 3);
        table.setRowHeight(0, 25);
        // sanity assert
        assertEquals(25, table.getRowHeight(0));
        // setting an arbitrary value
        table.setValueAt("dummy", 1, 0);
        assertEquals(25, table.getRowHeight(0));
        // filter to leave only the row with the value set
//        table.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("d", 0, 0)}));
//        assertEquals(1, table.getRowCount());
//        // setting an arbitrary value in the visible rows
//        table.setValueAt("otherdummy", 0, 1);
//        // reset filter to show all
//        table.setFilters(null);
//        assertEquals(25, table.getRowHeight(0));
        
        
    }
    

    @Test
    public void testIndividualRowHeight() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeight(0, 25);
        assertEquals(25, table.getRowHeight(0));
        assertEquals(table.getRowHeight(), table.getRowHeight(1));
//        table.getFilters().getSortController().setSortKeys
//            (Collections.singletonList(
//                new SortKey(SortOrder.DESCENDING, 0)));
        assertEquals(table.getRowHeight(), table.getRowHeight(1));
        assertEquals(25, table.getRowHeight(table.getRowCount() - 1));
        table.setRowHeight(table.getRowHeight());
        assertEquals(table.getRowHeight(), table.getRowHeight(table.getRowCount() - 1));
    }
    

    @Test
    public void testResetIndividualRowHeight() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeight(0, 25);
//        table.getFilters().getSortController().setSortKeys
//            (Collections.singletonList(
//                new SortKey(SortOrder.DESCENDING, 0)));
//        assertEquals("individual row height must be moved to last row", 
//                25, table.getRowHeight(table.getRowCount() - 1));
        // reset
        table.setRowHeight(table.getRowHeight());
        assertEquals("individual row height must be reset", 
                table.getRowHeight(), table.getRowHeight(table.getRowCount() - 1));
    }
    /**
     * Issue #64-swingx: setFilters(null) throws NPE if has selection.
     *
     */
    @Test
    public void testSetNullFilters() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(sortableTableModel);
        table.setRowSelectionInterval(0, 0);
//        table.setFilters(null);
        assertEquals("selected row must be unchanged", 0, table.getSelectedRow());
    }

    /**
     * Issue #119: Exception if sorter on last column and setting
     * model with fewer columns.
     * 
     * JW: related to #53-swingx - sorter not removed on column removed. 
     * 
     * PatternFilter does not throw - checks with modelToView if the 
     * column is visible and returns false match if not. Hmm...
     * 
     * 
     */
    @Test
    public void testFilterInChainOnModelChange() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 10, 5, true));
        int columnCount = table.getColumnCount();
        assertEquals(5, columnCount);
//        Filter filter = new PatternFilter(".*", 0, columnCount - 1);
//        FilterPipeline pipeline = new FilterPipeline(new Filter[] {filter});
//        table.setFilters(pipeline);
//        assertEquals(10, pipeline.getOutputSize());
//        table.setModel(new DefaultTableModel(10, columnCount - 1));
    }
    
    /**
     * Issue #119: Exception if sorter on last column and setting
     * model with fewer columns.
     * 
     * 
     * JW: related to #53-swingx - sorter not removed on column removed. 
     * 
     * Similar if sorter in filter pipeline -- absolutely need mutable
     * pipeline!!
     * Filed the latter part as Issue #55-swingx 
     *
     */
    @Test
    public void testSorterInChainOnModelChange() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(new DefaultTableModel(10, 5));
        int columnCount = table.getColumnCount();
//        Sorter sorter = new ShuttleSorter(columnCount - 1, false);
//        FilterPipeline pipeline = new FilterPipeline(new Filter[] {sorter});
//        table.setFilters(pipeline);
        table.setModel(new DefaultTableModel(10, columnCount - 1));
    }
    

    /**
     * Issue #119: Exception if sorter on last column and setting
     * model with fewer columns.
     * 
     * JW: related to #53-swingx - sorter not removed on column removed. 
     *
     */
    @Test
    public void testInteractiveSorterOnModelChange() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(sortableTableModel);
        int columnCount = table.getColumnCount();
        table.toggleSortOrder(columnCount - 1);
        table.setModel(new DefaultTableModel(10, columnCount - 1));
//        assertTrue(table.getFilters().getSortController().getSortKeys().isEmpty());
    }
    
    /**
     * add api to access the sorted column.
     *
     */
    @Test
    public void testSortedColumn() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
        TableColumn sortedColumn = table.getSortedColumn();
        assertEquals(columnX, sortedColumn);
        
    }

    /**
     * Issue #53-swingx: interactive sorter not removed if column removed.
     *
     */
    @Test
    public void testSorterAfterColumnRemoved() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
        table.removeColumn(columnX);
//        assertTrue("sorter must be removed when column removed", 
//                table.getFilters().getSortController().getSortKeys().isEmpty());
        
    }
    
    /**
     * interactive sorter must be active if column is hidden.
     * THINK: no longer valid... check sortkeys instead?
     */
    @Test
    public void testSorterAfterColumnHidden() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
//        List<? extends SortKey> sortKeys = table.getFilters().getSortController().getSortKeys();
//        columnX.setVisible(false);
//        assertEquals("interactive sorter must be same as sorter in column", 
//                sortKeys, table.getFilters().getSortController().getSortKeys());
    }
    
    /**
     * Issue #54: hidden columns not removed on setModel.
     *
     */
    @Test
    public void testRemoveAllColumsAfterModelChanged() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setVisible(false);
        table.setModel(new DefaultTableModel());
        assertEquals("all columns must have been removed", 0, table.getColumnCount(true));
        assertEquals("all columns must have been removed", 
                table.getColumnCount(), table.getColumnCount(true));
//        assertTrue("sorter must be removed when column removed",
//                table.getFilters().getSortController().getSortKeys().isEmpty());
    }
    /**
     * Issue #187: filter update removes interactive sorter.
     *
     */
    @Test
    public void testFilterUpdateKeepsSorter() {
        fail("JXTable - swingx filtering/sorting disabled");
        int rowCount = 20;
        int firstValue = 0;
        JXTable table = new JXTable(createAscendingModel(firstValue, rowCount));
        table.toggleSortOrder(0);
        // sort descending
        table.toggleSortOrder(0);
        Object value = table.getValueAt(0, 0);
        assertEquals("highest value", value, firstValue + rowCount - 1);
//        PatternFilter filter = new PatternFilter(".*", 0, 0);
//        // set a filter
//        table.setFilters(new FilterPipeline(new Filter[] {filter}));
//        assertEquals("highest value unchanged", value, table.getValueAt(0, 0 ));
//        // update the filter
//        filter.setPattern("^1", 0);
        assertTrue("sorter must be active", 
                ((Integer) table.getValueAt(0, 0)).intValue() > ((Integer) table.getValueAt(1, 0)));
    }
    
    /**
     * Issue #175: multiple registration as PipelineListener.
     * 
     *
     */
    @Test
    public void testRegisterUniquePipelineListener() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable();
//        PatternFilter noFilter = new PatternFilter(".*", 0, 1);
//        table.setFilters(new FilterPipeline(new Filter[] {noFilter}));
//        int listenerCount = table.getFilters().getPipelineListeners().length;
//        table.setModel(createAscendingModel(0, 20));
//        assertEquals("pipeline listener count must not change after setModel", listenerCount, table.getFilters().getPipelineListeners().length);
        
    }

    /**
     * Issue #33-swingx: selection not restored after refresh of interactive sorter.
     *
     *  adjusted to new JXTable sorter api (after the source tag jw_before_rowsorter)
     *  
     */
    @Test
    public void testSelectionOnSorterRefresh() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.toggleSortOrder(0);
        SortOrder sortOrder = table.getSortOrder(0);
        // sanity assert
//        assertTrue(sortOrder.isAscending());
        // select the first row
        table.setRowSelectionInterval(0, 0);
        // reverse sortorder
        table.toggleSortOrder(0);
        assertEquals("last row must be selected", table.getRowCount() - 1, table.getSelectedRow());
    }

    /**
     * Issue #173: 
     * ArrayIndexOOB if replacing model with one containing
     * fewer rows and the "excess" is selected.
     *
     */
    @Test
    public void testSelectionAndToggleModel() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable();
        table.setModel(createAscendingModel(0, 10));
        // sort first column
        table.toggleSortOrder(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.toggleSortOrder(0);
        // set model with less rows
        table.setModel(createAscendingModel(0, 8));
        
    }
    
    /**
     * testing selection and adding rows.
     * 
     *
     */
    @Test
    public void testSelectionAndAddRows() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable();
        DefaultTableModel model = createAscendingModel(0, 10);
        table.setModel(model);
        // sort first column
        table.toggleSortOrder(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.toggleSortOrder(0);
        
        Integer highestValue = new Integer(100);
        model.addRow(new Object[] { highestValue });
        assertEquals(highestValue, table.getValueAt(0, 0));
    }

    /**
     * Issue #??: removing row throws ArrayIndexOOB on selection
     *
     */
    @Test
    public void testSelectionRemoveRowsReselect() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable();
        DefaultTableModel model = createAscendingModel(0, 10);
        table.setModel(model);
        // sort first column
        table.toggleSortOrder(0);
        // invert sort
        table.toggleSortOrder(0);
        // select last row
        int modelLast = table.getRowCount() - 1;
        table.setRowSelectionInterval(modelLast, modelLast);
        model.removeRow(table.convertRowIndexToModel(modelLast));
        table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
    }

    
    /**
     * Issue #16: removing row throws ArrayIndexOOB if
     * last row was selected
     *
     */
    @Test
    public void testSelectionAndRemoveRows() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable();
        DefaultTableModel model = createAscendingModel(0, 10);
        table.setModel(model);
        // sort first column
        table.toggleSortOrder(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.toggleSortOrder(0);
        model.removeRow(0);
    }

    @Test
    public void testDeleteRowAboveIndividualRowHeight() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel model = createAscendingModel(0, 10);
        JXTable table = new JXTable(model);
        int selectedRow = table.getRowCount() - 1;
        table.setRowHeight(selectedRow, 25);
        table.toggleSortOrder(0);
        assertEquals("last row is individual", 25, table.getRowHeight(selectedRow));
        model.removeRow(0);
        assertEquals("last row is individual", 25, table.getRowHeight(selectedRow - 1));
        
    }

    /**
     * Issue #223 - part d)
     * 
     * test if selection is cleared after receiving a dataChanged.
     * Need to specify behaviour: lead/anchor of selectionModel are 
     * not changed in clearSelection(). So modelSelection has old 
     * lead which is mapped as a selection in the view (may be out-of 
     * range). Hmmm...
     * 
     */
    @Test
    public void testSelectionAfterDataChanged() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel ascendingModel = createAscendingModel(0, 20, 5, false);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // sanity
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.fireTableDataChanged();
        assertEquals("selection must be cleared", -1, table.getSelectedRow());
    }

    /**
     * Issue #223 - part d)
     * 
     * test if selection is cleared after receiving a dataChanged.
     * 
     */
    @Test
    public void testCoreTableSelectionAfterDataChanged() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel ascendingModel = createAscendingModel(0, 20, 5, false);
        JTable table = new JTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // sanity
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.fireTableDataChanged();
        assertEquals("selection must be cleared", -1, table.getSelectedRow());
        
    }
    
    /**
     * Issue #223
     * 
     * test if selection is updated on remove row above selection.
     */
    @Test
    public void testDeleteRowAboveSelection() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline
        table.toggleSortOrder(0);
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.removeRow(0);
        assertEquals("last row must still be selected after remove be selected", table.getRowCount() - 1, table.getSelectedRow());
        
    }

    /**
     * Issue #223
     * 
     * test if selection is updated on add row above selection.
     */
    @Test
    public void testAddRowAboveSelection() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.insertRow(0, new Object[table.getColumnCount()]);
        assertEquals("last row must still be selected after add above", table.getRowCount() - 1, table.getSelectedRow());
    }


    @Test
    public void testAddRowAboveIndividualRowHeigh() {
        fail("JXTable - swingx filtering/sorting disabled");
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
    @Test
    public void testAddRowAboveSelectionInvertedOrder() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        // select the last row in view coordinates
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline - ascending, no change
        table.toggleSortOrder(0);
        // revert order 
        table.toggleSortOrder(0);
        assertEquals("first row must be selected", 0, table.getSelectedRow());
        // remove row in model coordinates
        Object[] row = new Integer[table.getColumnCount()];
        // insert high value
        row[0] = new Integer(100);
        ascendingModel.addRow(row);
        // selection must be moved one below
        assertEquals("selection must be incremented by one ", 1, table.getSelectedRow());
        
    }

    @Test
    public void testAddRowAboveIndividualRowHeightInvertedOrder() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        // select the last row in view coordinates
        int selectedRow = table.getRowCount() - 1;
        table.setRowHeight(selectedRow, 25);
        // set a pipeline - ascending, no change
        table.toggleSortOrder(0);
        // revert order 
        table.toggleSortOrder(0);
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
    @Test
    public void testDeleteRowAboveSelectionInvertedOrder() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        // select the last row in view coordinates
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline - ascending, no change
        table.toggleSortOrder(0);
        // revert order 
        table.toggleSortOrder(0);
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
    @Test
    public void testDeleteRowBelowSelection() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // sort ascending 
        table.toggleSortOrder(0);
        assertEquals("first row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.removeRow(selectedRow + 1);
        assertEquals("first row must still be selected after remove", selectedRow, table.getSelectedRow());
    }

    /**
     * Issue #223
     * test if selection is kept if row below selection is removed.
     *
     */
    @Test
    public void testDeleteRowBelowSelectionInvertedOrder() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // sort ascending
        table.toggleSortOrder(0);
        // revert order 
        table.toggleSortOrder(0);
        assertEquals("last row must be selected", table.getRowCount() - 1, table.getSelectedRow());
        ascendingModel.removeRow(selectedRow + 1);
        assertEquals("last row must still be selected after remove", table.getRowCount() - 1, table.getSelectedRow());
        
    }

    /**
     * Issue #223
     * test if selection is kept if row in selection is removed.
     *
     */
    @Test
    public void testDeleteLastRowInSelection() {
        fail("JXTable - swingx filtering/sorting disabled");
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        int lastSelectedRow = 1;
        table.setRowSelectionInterval(selectedRow, lastSelectedRow);
        // set a pipeline
        table.toggleSortOrder(0);
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
     * quick check if overriding sortOnChange prevents auto-resort.
     *
     */
    @Test
    public void testSortOnChange() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 10)) {

            @Override
            protected boolean shouldSortOnChange(TableModelEvent e) {
                if (isUpdate(e)) {
                    return false;
                }
                return super.shouldSortOnChange(e);
            }
            
        };
        // sort ascending
        table.toggleSortOrder(0);
        Integer first = (Integer) table.getValueAt(0, 0);
        Integer second = (Integer) table.getValueAt(1, 0);
        // sanity
        assertTrue(first.intValue() < second.intValue());
        int high = first.intValue() + 100;
        // set a high value
        table.setValueAt(high, 0, 0);
        assertEquals("sort should not update after", high, table.getValueAt(0, 0));
    }
    

    /**
     * check if setting to false really disables sortability.
     *
     */
    @Test
    public void testSortable() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(createAscendingModel(0, 10));
        boolean sortable = table.isSortable();
        // sanity assert: sortable defaults to true
        assertTrue("JXTable sortable defaults to true", sortable);
        table.toggleSortOrder(0);
        Object first = table.getValueAt(0, 0);
        table.setSortable(false);
        assertFalse(table.isSortable());
        // reverse the sorting order on first column
        table.toggleSortOrder(0);
        assertEquals("sorting on a non-sortable table must do nothing", first, table.getValueAt(0, 0));
    }
    
    /**
     * Issue #171: row-coordinate not transformed in isCellEditable (sorting)
     *
     */
    @Test
    public void testSortedEditability() {
        fail("JXTable - swingx filtering/sorting disabled");
        int rows = 2;
        RowObjectTableModel model = createRowObjectTableModel(rows);
        JXTable table = new JXTable(model);
        RowObject firstInModel = model.getRowObject(0);
        assertEquals("rowObject data must be equal", firstInModel.getData1(), table.getValueAt(0, 0));
        assertEquals("rowObject editability must be equal", firstInModel.isEditable(), table.isCellEditable(0, 0));
        // nothing changed
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
        assertEquals("last row data must be equal to former first", firstDataValueInTable, 
                table.getValueAt(rows - 1, 0));
        assertEquals("last row editability must be equal to former first", firstEditableValueInTable, 
                table.isCellEditable(rows - 1, 0));
    }

    /**
     * Issue #171: row-coordinate not transformed in isCellEditable (filtering)
     *
     */
    @Test
    public void testFilteredEditability() {
        fail("JXTable - swingx filtering/sorting disabled");
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
//        PatternFilter filter = new PatternFilter("^NOT", 0, 1);
//        PatternFilter noFilter = new PatternFilter(".*", 0, 1);
//
//        table.setFilters(new FilterPipeline(new Filter[] {noFilter, filter}));
        assertEquals("row count is half", rows / 2, table.getRowCount());
        for (int i = 0; i < table.getRowCount(); i++) {
            assertFalse("all rows must be not-editable " + i, table.isCellEditable(i, 0));
            
        }
    }

    /**
     * Issue #167: IllegalStateException if re-setting filter while
     * sorting.
     *
     */
    @Test
    public void testToggleFiltersWhileSorting() {
        fail("JXTable - swingx filtering/sorting disabled");
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, "AA" },
                new Object[] { Boolean.FALSE, "AB" },
                new Object[] { Boolean.FALSE, "AC" },
                new Object[] { Boolean.TRUE, "BA" },
                new Object[] { Boolean.FALSE, "BB" },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        final JXTable table = new JXTable(rowData, columnNames);
//        Filter filterA = new PatternFilter("A.*", Pattern.CASE_INSENSITIVE, 1);
        // simulates the sequence of user interaction as described in 
        // the original bug report in 
        // http://www.javadesktop.org/forums/thread.jspa?messageID=56285
//        table.setFilters(createFilterPipeline(false, 1));//new FilterPipeline(new Filter[] {filterA}));
//        table.toggleSortOrder(1);
////        Filter filterB = new PatternFilter(".*", Pattern.CASE_INSENSITIVE, 1);
//        table.setFilters(createFilterPipeline(true, 1)); //new FilterPipeline(new Filter[] {filterB}));
//        table.toggleSortOrder(1);
    }

    /**
     * Issue #167: IllegalStateException if re-setting filter while
     * sorting.
     * Another variant ...
     *
     */
    @Test
    public void testToggleFiltersWhileSortingLonger() {
        fail("JXTable - swingx filtering/sorting disabled");
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
//        table.setFilters(createFilterPipeline(false, 1));
//        table.toggleSortOrder(0);
//        table.toggleSortOrder(1);
//        table.setFilters(createFilterPipeline(true, 1));
//        table.setFilters(createFilterPipeline(false, 1));
//        table.toggleSortOrder(0);
    }
    
//    private FilterPipeline createFilterPipeline(boolean matchAll, int col) {
////        RowSorterFilter filter = new RowSorterFilter();
////        if (matchAll) {
////            filter.setRowFilter(RowFilter.regexFilter(".*", col));
////            
////        } else {
////            filter.setRowFilter(RowFilter.regexFilter("A.*", col));
////        }
//        Filter filter;
//        if (matchAll) {
//            filter = new PatternFilter(".*", Pattern.CASE_INSENSITIVE, col);
//        } else {
//           filter = new PatternFilter("^A", Pattern.CASE_INSENSITIVE, col);
//        }
//        return new FilterPipeline(new Filter[] {filter});
//        
//    }

    /**
     * Issue #125: setting filter to null doesn't clean up.
     * 
     * A visual consequence is that the hidden (by the old
     * filters) rows don't appear. A not-so visual consequence
     * is that the sorter is out of synch and accessing a row in
     * the region outside of the formerly filtered. 
     *
     */
    @Test
    public void testRemoveFilterWhileSorting() {
        fail("JXTable - swingx filtering/sorting disabled");
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
//        Filter filterA = new PatternFilter("A.*", Pattern.CASE_INSENSITIVE, 1);
//        table.setFilters(createFilterPipeline(false, 1)); //new FilterPipeline(new Filter[] {filterA}));
//        table.toggleSortOrder(1);
//        table.setFilters(null);
//        assertEquals("rowCount must be original", rows, table.getRowCount());
        table.getValueAt(rows - 1, 0);

    
    }   

    /** 
     * Issue #150: setting filters must not re-create columns.
     *
     */
    @Test
    public void testTableColumnsWithFilters() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(tableModel);
        assertEquals("table columns are equal to columns of model", 
                tableModel.getColumnCount(), table.getColumnCount());
        TableColumn column = table.getColumnExt(0);
        table.removeColumn(column);
        int columnCountAfterRemove = table.getColumnCount();
        assertEquals("table columns must be one less than columns of model",
                tableModel.getColumnCount() - 1, columnCountAfterRemove);
//        table.setFilters(new FilterPipeline(new Filter[] {
//                new ShuttleSorter(1, false), // column 1, descending
//        }));
        assertEquals("table columns must be unchanged after setting filter",
                columnCountAfterRemove, table.getColumnCount());
        
    }

    /**
     * JXTable has responsibility to guarantee usage of 
     * TableColumnExt comparator and update the sort if
     * the columns comparator changes.
     * 
     */
    public void testComparatorToPipelineDynamic() {
        fail("JXTable - swingx filtering/sorting disabled");
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
        columnX.setComparator(Collator.getInstance());
        // invalid assumption .. only the comparator must be used.
//        assertEquals("interactive sorter must be same as sorter in column", 
//                columnX.getSorter(), table.getFilters().getSorter());
//        SortKey sortKey = SortKey.getFirstSortKeyForColumn(table.getFilters().getSortController().getSortKeys(), 0);
//        assertNotNull(sortKey);
//        assertEquals(columnX.getComparator(), sortKey.getComparator());
       
    }

//-------------------------- tests for moving column control into swingx

    
    /**
     * hmm... sporadic ArrayIndexOOB after sequence:
     * 
     * filter(column), sort(column), hide(column), setFilter(null)
     *
     */
    @Test
    public void testColumnControlAndFilters() {
        fail("JXTable - swingx filtering/sorting disabled");
        final JXTable table = new JXTable(sortableTableModel);
        table.setColumnControlVisible(true);
//        Filter filter = new PatternFilter("e", 0, 0);
//        table.setFilters(new FilterPipeline(new Filter[] {filter}));
//        // needed to make public in JXTable for testing
        //   table.getTable().setSorter(0);
        table.getColumnExt(0).setVisible(false);
//        table.setFilters(null);

    }

 
    //----------------------- interactive 
    
    public void interactiveTreeTableNPE() {
        JXTreeTable treeTable = new JXTreeTable(new FileSystemModel());
//        treeTable.expandAll();
        showWithScrollingInFrame(treeTable, "NPE");
    }
    
    /**
     * Issue #271-swingx: make sort triggering mouseEvents
     * customizable.
     * 
     * added SortGestureRecognizer.
     * 
     * No longer supported - need to re-think (maybe could push core addition?)
     * Anyway, re-opened the old issue.
     *
     */
    public void interactiveSortGestureRecognizer() {
//        final JXTable table = new JXTable(10, 2);
//        JXFrame frame = wrapWithScrollingInFrame(table, "Sort Gesture customization");
//        Action action = new AbstractAction("toggle default/custom recognizer") {
//            boolean hasCustom;
//            public void actionPerformed(ActionEvent e) {
//                SortGestureRecognizer recognizer = null;
//                if (!hasCustom) {
//                    hasCustom = !hasCustom;
//                    recognizer = new SortGestureRecognizer() {
//                        /**
//                         * allow double clicks to trigger a sort.
//                         */
//                        @Override
//                        public boolean isSortOrderGesture(MouseEvent e) {
//                            return e.getClickCount() <= 2;
//                        }
//
//                        /**
//                         * Disable reset gesture.
//                         */
//                        @Override
//                        public boolean isResetSortOrderGesture(MouseEvent e) {
//                            return false;
//                        }
//
//                        /**
//                         * ignore modifiers.
//                         */
//                        @Override
//                        public boolean isToggleSortOrderGesture(MouseEvent e) {
//                            return isSortOrderGesture(e);
//                        }
//                        
//                        
//                        
//                    };
//                }
//                ((JXTableHeader) table.getTableHeader()).setSortGestureRecognizer(recognizer);
//                
//            }
//            
//        };
//        addAction(frame, action);
//        frame.setVisible(true);
        
    }

    public void interactiveIndividualRowHeightAndFilter() {
        final JXTable table = new JXTable(createAscendingModel(0, 50));
        table.setRowHeight(1, 100);
//        final FilterPipeline filterPipeline = new FilterPipeline(new PatternFilter(".*1.*",0,0));
//        Action action = new AbstractAction("filter") {
//
//            public void actionPerformed(ActionEvent e) {
//                if (table.getFilters() == filterPipeline) {
//                    table.setFilters(null);
//                } else {
//                    table.setFilters(filterPipeline);
//                }
//            }
//            
//        };
//        JXFrame frame = wrapWithScrollingInFrame(table, "toggle filter and indi rowheight");
//        addAction(frame, action);
//        frame.setVisible(true);
    }
   
    /** 
     * Issue #??: Problems with filters and ColumnControl
     * 
     * - sporadic ArrayIndexOOB after sequence:
     * filter(column), sort(column), hide(column), setFilter(null)
     * 
     * - filtering invisible columns? Unclear state transitions.
     *
     */
    public void interactiveTestColumnControlAndFilters() {
        final JXTable table = new JXTable(sortableTableModel);
        // hmm bug regression with combos as editors - same in JTable
//        JComboBox box = new JComboBox(new Object[] {"one", "two", "three" });
//        box.setEditable(true);
//        table.getColumnExt(0).setCellEditor(new DefaultCellEditor(box));
        Action toggleFilter = new AbstractAction("Toggle Filter col. 0") {
            boolean hasFilters;
            public void actionPerformed(ActionEvent e) {
//                if (hasFilters) {
//                    table.setFilters(null);
//                } else {
//                    Filter filter = new PatternFilter("e", 0, 0);
//                    table.setFilters(new FilterPipeline(new Filter[] {filter}));
//
//                }
//                hasFilters = !hasFilters;
            }
            
        };
        toggleFilter.putValue(Action.SHORT_DESCRIPTION, "filtering first column - problem if invisible ");
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl and Filters");
        addAction(frame, toggleFilter);
        frame.setVisible(true);
    }
 
    /**
     * Issue #55-swingx: NPE on setModel if sorter in pipeline and new
     * model getColumnCount() < sorter.getColumnIndex().
     *
     */
    public void interactiveSetModelFilter() {
        final DefaultTableModel model = createAscendingModel(0, 20, 3, true);
        final AncientSwingTeam ancientSwingTeam = new AncientSwingTeam();
        final JXTable table = new JXTable(ancientSwingTeam);
//        Sorter sorter = new ShuttleSorter(3, false);
//        FilterPipeline pipeline = new FilterPipeline(new Filter[] {sorter});
//        table.setFilters(pipeline);
        table.setRowHeight(0, 25);
        Action action = new AbstractAction("toggleModel") {

            public void actionPerformed(ActionEvent e) {
                if (table.getModel() == ancientSwingTeam) {
                    table.setModel(model);
                } else {
                    table.setModel(ancientSwingTeam);
                }
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "model update?");
        addAction(frame, action);
        frame.setVisible(true);
    }
    

    /** 
     * @KEEP this is about testing Mustang sorting.
     */
    public void interactiveTestColumnControlAndFiltersRowSorter() {
//        final JXTable table = new JXTable(sortableTableModel);
//        // hmm bug regression with combos as editors - same in JTable
////        JComboBox box = new JComboBox(new Object[] {"one", "two", "three" });
////        box.setEditable(true);
////        table.getColumnExt(0).setCellEditor(new DefaultCellEditor(box));
//        Action toggleFilter = new AbstractAction("Toggle RowFilter -contains e- ") {
//            boolean hasFilters;
//            public void actionPerformed(ActionEvent e) {
//                if (hasFilters) {
//                    table.setFilters(null);
//                } else {
//                    RowSorterFilter filter = new RowSorterFilter();
//                    filter.setRowFilter(RowFilter.regexFilter(".*e.*", 0));
//                    table.setFilters(new FilterPipeline(new Filter[] {filter}));
//
//                }
//                hasFilters = !hasFilters;
//            }
//            
//        };
//        toggleFilter.putValue(Action.SHORT_DESCRIPTION, "filtering first column - problem if invisible ");
//        table.setColumnControlVisible(true);
//        JFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl and Filters");
//        addAction(frame, toggleFilter);
//        frame.setVisible(true);
    }
 
    
    /**
     * Example: how to implement a custom toggle sort cycle.
     * unsorted - ascending - descending - unsorted.
     */
    public void interactiveCustomToggleSortOrder() {
//        FilterPipeline myFilterPipeline = new CustomToggleSortOrderFP();
        JXTable table = new JXTable(sortableTableModel);
//        table.setFilters(myFilterPipeline);
        JXFrame frame = wrapWithScrollingInFrame(table, "Custom sort toggle");
        frame.setVisible(true);
        
    }
    /**
     * Example: how to implement a custom toggle sort cycle.
     * 
     */
//    public class CustomToggleSortOrderFP extends FilterPipeline {
//
//        public CustomToggleSortOrderFP() {
//            super();
//        }
//
//        public CustomToggleSortOrderFP(Filter[] inList) {
//            super(inList);
//        }
//
//        @Override
//        protected SortController createDefaultSortController() {
//            return new CustomSortController();
//        }
//        
//        protected class CustomSortController extends SorterBasedSortController {
//
//            @Override
//            public void toggleSortOrder(int column, Comparator comparator) {
//                Sorter currentSorter = getSorter();
//                if ((currentSorter != null) && 
//                     (currentSorter.getColumnIndex() == column) &&
//                     !currentSorter.isAscending()) {
//                    setSorter(null);
//                } else {
//                    super.toggleSortOrder(column, comparator);
//                } 
//            }
//            
//        }
//    };
//    



    //--------------------- factory

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

        @Override
        public boolean isCellEditable(int row, int col) {
            return getRowObject(row).isEditable();
        }
    }

    /**
     * create test model - all cells in even rows are editable, 
     * in odd rows are not editable. 
     * @param rows the number of rows to create
     * @return
     */
    private RowObjectTableModel createRowObjectTableModel(int rows) {
        List<RowObject> rowObjects = new ArrayList<RowObject>();
        for (int i = 0; i < rows; i++) {
            rowObjects.add(new RowObject("somedata" + i, i % 2 == 0));
        }
        return new RowObjectTableModel(rowObjects);
    }
    

    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first column
     * starting from startRow.
     * @param startRow the value of the first row
     * @param count the number of rows
     * @return
     */
    protected DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 4) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Integer.class : super.getColumnClass(column);
            }
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }

    
    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first/last column depending on fillLast
     * starting from startRow.
     * with columnCount columns
     * @param startRow the value of the first row
     * @param rowCount the number of rows
     * @param columnCount the number of columns
     * @param fillLast boolean to indicate whether to ill the value in the first
     *   or last column
     * @return a configured DefaultTableModel.
     */
    protected DefaultTableModel createAscendingModel(int startRow, final int rowCount, 
            final int columnCount, boolean fillLast) {
        DefaultTableModel model = new DefaultTableModel(rowCount, columnCount) {
            @Override
            public Class<?> getColumnClass(int column) {
                Object value = rowCount > 0 ? getValueAt(0, column) : null;
                return value != null ? value.getClass() : super.getColumnClass(column);
            }
        };
        int filledColumn = fillLast ? columnCount - 1 : 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, filledColumn);
        }
        return model;
    }
    
    /**
     * Creates and returns a StringValue which maps a Color to it's R/G/B rep, 
     * prepending "R/G/B: "
     * 
     * @return the StringValue for color.
     */
    private StringValue createColorStringValue() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        return sv;
    }

}
