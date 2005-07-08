/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.util.AncientSwingTeam;

public class FilterTest extends InteractiveTestCase {

    public FilterTest() {
        super("FilterTest");
    }
    
    private TableModel tableModel;
    private ComponentAdapter directModelAdapter;

    /**
     * early binding of pipeline to filters.
     *
     */
    public void testDirectComponentAdapterAccess() {
        FilterPipeline pipeline = createPipeline();
        pipeline.assign(directModelAdapter);
        pipeline.flush();
        assertTrue("pipeline must have filtered values", pipeline.getOutputSize() < directModelAdapter.getRowCount());
        List lastNames = new ArrayList();
        List colors = new ArrayList();
        List numbers = new ArrayList();
        for (int i = 0; i < pipeline.getOutputSize(); i++) {
            lastNames.add(pipeline.getValueAt(i, 1));
            colors.add(pipeline.getValueAt(i, 2));
            numbers.add(pipeline.getValueAt(i, 3));
        }
        System.out.println(lastNames);
        System.out.println(colors);
        System.out.println(numbers);
    }

    private FilterPipeline createPipeline() {
        Filter filterZero = new PatternFilter(".*e.*", 0, 0);
        Filter filterTwo = new PatternFilter(".*e.*", 0, 2); 
        Sorter sorter = new ShuttleSorter();
        Filter[] filters = new Filter[] {filterZero, filterTwo, sorter};
        FilterPipeline pipeline = new FilterPipeline(filters);
        return pipeline;
    }
    /**
     * early binding of pipeline to filters.
     *
     */
    public void testDirectComponentAdapter() {
        Filter filterZero = new PatternFilter(".*e.*", 0, 0);
        Filter filterTwo = new PatternFilter(".*e.*", 0, 2); 
        Sorter sorter = new ShuttleSorter();
        Filter[] filters = new Filter[] {filterZero, filterTwo, sorter};
        FilterPipeline pipeline = new FilterPipeline(filters);
        assertOrder(filterZero, filters);
        assertEquals("assigned to pipeline", pipeline, filterZero.getPipeline());
        pipeline.assign(directModelAdapter);
        pipeline.flush();
        assertTrue("pipeline must have filtered values", pipeline.getOutputSize() < directModelAdapter.getRowCount());
      // 8 rows left with filterZero, 5 rows left with both
      //   assertEquals("quickly see the filtered count", directModelAdapter.getRowCount(), pipeline.getOutputSize());
    }
    
    public void testSorterOrder() {
        Sorter sorter = new ShuttleSorter(); 
        assertEquals("order < 0", -1, sorter.order);
        Filter[] filters = new Filter[] {sorter, new ShuttleSorter(2, true)};
        FilterPipeline pipeline = new FilterPipeline(filters);
        // JW: pipeline inverts order of sorter - why?
        assertOrders(filters);
    }

    /**
     * FilterPipeline allows maximal one sorter per column. 
     *
     */
    public void testDuplicatedSortColumnException() {
        Filter[] filters = new Filter[] {new ShuttleSorter(), new ShuttleSorter()};
        try {
            FilterPipeline pipeline = new FilterPipeline(filters);
            fail("trying to sort one column more than once must throw IllegalArgumentException");
        
        } catch (IllegalArgumentException e) {    
            // that's what we expect
        } catch (Exception e) {
            fail("trying to sort one column more than once must throw IllegalArgumentException" +
                    "instead of " + e);
        }
    }
    
    public void testAssignFilterPipelineBoundFilterException() {
        Filter filter = new PatternFilter(".*s.*", 0, 0);
        assertEquals("order < 0", -1, filter.order);
        Filter[] filters = new Filter[] {filter};
        FilterPipeline pipeline = new FilterPipeline(filters);
        assertOrder(filter, filters);
        try {
            new FilterPipeline(filters);
            fail("sharing filters are not allowed - must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // that's what we expect
        } catch (Exception e) {
            fail("exception must be illegalArgument instead of " + e);
        }
    }

    /**
     * early binding of pipeline to filters.
     *
     */
    public void testAssignFilterPipeline() {
        Filter filter = new PatternFilter(".*s.*", 0, 0);
        assertEquals("order < 0", -1, filter.order);
        Filter[] filters = new Filter[] {filter};
        FilterPipeline pipeline = new FilterPipeline(filters);
        assertOrder(filter, filters);
        assertEquals("assigned to pipeline", pipeline, filter.getPipeline());
        JXTable table = new JXTable(tableModel);
        table.setFilters(pipeline);
        assertEquals("assigned to table's componentadapter", table, filter.adapter.getComponent());
    }

    private void assertOrder(Filter filter, Filter[] filters) {
        int position = getFilterPosition(filter, filters);
        assertEquals("order equals position in array", position, filter.order);
    }

    private void assertOrders(Filter[] filters) {
        for (int i = 0; i < filters.length; i++) {
            assertEquals("order must be equal to filter position", i, filters[i].order);
        }
    }
    private int getFilterPosition(Filter filter, Filter[] filters) {
        int position = -1;
        for (int i = 0; i < filters.length; i++) {
            if (filters[i].equals(filter)) {
                position = i;
                return i;
            }
        }
        return -1;
    }
    
    /**
     * This is a test to ensure that the example in the javadoc actually works.
     * if the javadoc example changes, then those changes should be pasted here.
     */
    public void testJavaDocExample() {

        Filter[] filters = new Filter[] {
            new PatternFilter("S.*", 0, 1), // regex, matchflags, column
            new ShuttleSorter(1, false),   // column 1, descending
            new ShuttleSorter(0, true),    // column 0, ascending
        };
        FilterPipeline pipeline = new FilterPipeline(filters);
        JXTable table = new JXTable();
        table.setFilters(pipeline);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        tableModel = new AncientSwingTeam();
        directModelAdapter = new DirectModelAdapter(tableModel);
     }

    public class DirectModelAdapter extends ComponentAdapter {

        private TableModel tableModel;

        public DirectModelAdapter(TableModel tableModel) {
            super(null);
            this.tableModel = tableModel;
        }

        public int getColumnCount() {
            return tableModel.getColumnCount();
        }
        public int getRowCount() {
            return tableModel.getRowCount();
        }
        public String getColumnName(int columnIndex) {
            return tableModel.getColumnName(columnIndex);
        }

        public String getColumnIdentifier(int columnIndex) {
            return getColumnName(columnIndex);
        }

        public Object getValueAt(int row, int column) {
            return tableModel.getValueAt(row, column);
        }

        public Object getFilteredValueAt(int row, int column) {
            return null;
        }

        public void setValueAt(Object aValue, int row, int column) {
            tableModel.setValueAt(aValue, row, column);

        }

        public boolean isCellEditable(int row, int column) {
            return tableModel.isCellEditable(row, column);
        }

        public boolean hasFocus() {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isSelected() {
            // TODO Auto-generated method stub
            return false;
        }

    }
    /** 
     * just to see the filtering effects...
     * 
     */
    public void interactiveTestColumnControlAndFilters() {
        final JXTable table = new JXTable(tableModel);
        table.setColumnControlVisible(true);
//        table.setFilters(createPipeline());
        Action toggleFilter = new AbstractAction("Toggle Filters") {
            
            public void actionPerformed(ActionEvent e) {
                if (table.getFilters() != null) {
                    table.setFilters(null);
                } else {
                    table.setFilters(createPipeline());

                }
                
            }
            
        };
        toggleFilter.putValue(Action.SHORT_DESCRIPTION, "filtering first column - problem if invisible ");
        JFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl and Filters");
        addAction(frame, toggleFilter);
        frame.setVisible(true);
    }

    public static void main(String args[]) {
        setSystemLF(true);
        FilterTest test = new FilterTest();
        try {
           test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

}
