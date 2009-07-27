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
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTableUnitTest.DynamicTableModel;
import org.jdesktop.swingx.JXTableUnitTest.RowObject;
import org.jdesktop.swingx.decorator.ComponentAdapterTest.JXTableT;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
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
            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*NPE.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void interactiveSortKeys() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        JXFrame frame = wrapWithScrollingInFrame(table, "sort cycles");
        Action three = new AbstractAction("three-cylce") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                table.getSortController().setSortOrderCycle(SortOrder.ASCENDING, SortOrder.DESCENDING, SortOrder.UNSORTED);
            }
        };
        addAction(frame, three);
        Action two = new AbstractAction("two-cylce") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                table.getSortController().setSortOrderCycle(SortOrder.ASCENDING, SortOrder.DESCENDING);
            }
        };
        addAction(frame, two);
        show(frame);
    }

//--------------- current re-introduce

    
    
///------------------------ still failing    
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
