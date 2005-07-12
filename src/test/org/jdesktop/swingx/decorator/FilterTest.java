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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.util.AncientSwingTeam;

public class FilterTest extends InteractiveTestCase {

    public FilterTest() {
        super("FilterTest");
    }
    
    private TableModel tableModel;
    protected ComponentAdapter directModelAdapter;

    /**
     * Issue #45-swingx:
     * interpose should throw if trying to interpose to a pipeline
     * with a differnt ComponentAdapter.
     *
     */
    public void testSetSorterDiffComponentAdapter() {
        int sortColumn = 0;
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
        pipeline.flush();
        Object value = pipeline.getValueAt(0, sortColumn);
        Object lastValue = pipeline.getValueAt(pipeline.getOutputSize() - 1, sortColumn);
        Sorter sorter = new ShuttleSorter();
        sorter.assign(new DirectModelAdapter(new DefaultTableModel(10, 5)));
        try {
            pipeline.setSorter(sorter);
            fail("interposing with a different adapter must throw an IllegalStateException");
        } catch (IllegalStateException ex) {
            
        } catch (Exception e) {
            fail("interposing with a different adapter must throw an " +
                    "IllegalStatetException instead of " + e);
        }
        
    }

    /**
     * Issue #45-swingx:
     * interpose should throw if trying to interpose to a pipeline
     * with a differnt ComponentAdapter.
     *
     */
    public void testInterposeDiffComponentAdapter() {
        int sortColumn = 0;
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
        pipeline.flush();
        Object value = pipeline.getValueAt(0, sortColumn);
        Object lastValue = pipeline.getValueAt(pipeline.getOutputSize() - 1, sortColumn);
        Sorter sorter = new ShuttleSorter();
        sorter.assign(new DirectModelAdapter(new DefaultTableModel(10, 5)));
        try {
            // PENDING: deprecate interpose ... delegates to pipeline anyway
            sorter.interpose(pipeline, new DirectModelAdapter(new DefaultTableModel(10, 5)), null);
            fail("interposing with a different adapter must throw an IllegalStateException");
        } catch (IllegalStateException ex) {
            
        } catch (Exception e) {
            fail("interposing with a different adapter must throw an " +
                    "IllegalStateException instead of " + e);
        }
        
    }

    /**
     * 
     * Issue #46-swingx:
     * 
     * need to clarify the behaviour of an empty pipeline.
     * I would expect 0 filters to result in an open pipeline 
     * (nothing filtered). The implementation treats this case as a
     * closed pipeline (everything filtered). 
     * 
     * Arguably it could be decided either way, but returning a
     * outputsize > 0 and null instead of the adapter value for 
     * all rows is a bug.
     * 
     * Fixed by adding an pass-all filter internally.
     *
     */
    public void testEmptyPipeline() {
        int sortColumn = 0;
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
//        pipeline.flush();
        assertEquals("size must be number of rows in adapter", 
                directModelAdapter.getRowCount(), pipeline.getOutputSize());
        Object value = pipeline.getValueAt(0, sortColumn);
        assertEquals(directModelAdapter.getValueAt(0, sortColumn), value);
    }
 
    /**
     * sorter in empty pipeline must behave in the same way as
     * an identical sorter in the pipeline's filter chain.
     *
     */
    public void testSorterInEmptyPipeline() {
        int sortColumn = 0;
        // prepare the reference pipeline
        Filter[] sorters = new Filter[] {new ShuttleSorter()};
        FilterPipeline sortedPipeline = new FilterPipeline(sorters);
        sortedPipeline.assign(directModelAdapter);
        sortedPipeline.flush();
        Object sortedValue = sortedPipeline.getValueAt(0, sortColumn);
        // prepare the empty pipeline with associated sorter
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
        Sorter sorter = new ShuttleSorter();
        pipeline.setSorter(sorter);
        assertEquals("sorted values must be equal", sortedValue, pipeline.getValueAt(0, sortColumn));
        
    }
    
    /**
     * stand-alone sorter must behave in the same way as 
     * an identical sorter in a pipeline's filter chain.
     *
     */
    public void testSorterInterposeNullPipeline() {
        int sortColumn = 0;
        // prepare the reference pipeline
        Filter[] sorters = new Filter[] {new ShuttleSorter()};
        FilterPipeline sortedPipeline = new FilterPipeline(sorters);
        sortedPipeline.assign(directModelAdapter);
        sortedPipeline.flush();
        Object sortedValue = sortedPipeline.getValueAt(0, sortColumn);
        // prepare the stand-alone sorter
        Sorter sorter = new ShuttleSorter();
        sorter.interpose(null, directModelAdapter, null);
        assertEquals(sortedValue, sorter.getValueAt(0, sortColumn));
    }
   
    /**
     * sorter.getValueAt must be same as pipeline.getValueAt.
     *
     */
    public void testSorterInPipeline() {
        Filter filter = createDefaultPatternFilter(0);
        FilterPipeline pipeline = new FilterPipeline(new Filter[] { filter });
        pipeline.assign(directModelAdapter);
        // JW PENDING: remove necessity to explicitly flush...
        pipeline.flush();
        Object value = pipeline.getValueAt(0, 0);
        Sorter sorter = new ShuttleSorter();
        pipeline.setSorter(sorter);
        assertEquals("value access via sorter must return the same as via pipeline", 
                pipeline.getValueAt(0, 0), sorter.getValueAt(0,0));
        
    }
    
    
    /**
     * cause for #167: sorter does not release pipeline if
     * moved to a new one.
     *
     */
    public void testInterpose() {
        int sortColumn = 0;
        Filter filter = new PatternFilter(".*s.*", 0, sortColumn);
        Filter[] filters = new Filter[] {filter, new ShuttleSorter()};
        FilterPipeline pipeline = new FilterPipeline(filters);
        pipeline.assign(directModelAdapter);
        pipeline.flush();
        Object value = pipeline.getValueAt(0, sortColumn);
        Object lastValue = pipeline.getValueAt(pipeline.getOutputSize() - 1, sortColumn);
        Sorter sorter = new ShuttleSorter();
        sorter.interpose(pipeline, directModelAdapter, null);
        assertEquals("value must be unchanged by interactive sorter", value, sorter.getValueAt(0, sortColumn));
        sorter.setAscending(false);
        assertEquals("first value must be old last", lastValue, sorter.getValueAt(0, sortColumn));

        Filter other = new PatternFilter();
        Filter[] otherFilters = new Filter[] {other};
        FilterPipeline otherPipeline = new FilterPipeline(otherFilters);
        otherPipeline.assign(directModelAdapter);
        otherPipeline.flush();
        // the interactive sorter is flexible - can be moved from one 
        // pipeline to the other
        sorter.interpose(otherPipeline, directModelAdapter, null);
        
    }
    
    /**
     * unassigned filter/-pipeline must have size 0.
     *
     */
    public void testUnassignedFilter() {
        Filter filter = createDefaultPatternFilter(0);
        assertEquals(0, filter.getSize());
        Filter[] filters = new Filter[] {filter};
        FilterPipeline pipeline = new FilterPipeline(filters);
        assertEquals(0, pipeline.getOutputSize());
    }
    /**
     * test paranoia
     *
     */
    public void testDirectComponentAdapterAccess() {
        FilterPipeline pipeline = createPipeline();
        pipeline.assign(directModelAdapter);
        pipeline.flush();
        assertTrue("pipeline must have filtered values", pipeline.getOutputSize() < directModelAdapter.getRowCount());
//        List lastNames = new ArrayList();
//        List colors = new ArrayList();
//        List numbers = new ArrayList();
//        for (int i = 0; i < pipeline.getOutputSize(); i++) {
//            lastNames.add(pipeline.getValueAt(i, 1));
//            colors.add(pipeline.getValueAt(i, 2));
//            numbers.add(pipeline.getValueAt(i, 3));
//        }
//        System.out.println(lastNames);
//        System.out.println(colors);
//        System.out.println(numbers);
    }
    
//    [Kloba, Moore, Saab, Korn, Walker]
//     [Yellow, Green, Red, Purple, Phthalo Blue]
//     [14, 88, 4, 12, 4]
//
    /**
     * order of filters must be retained.
     *
     */
    public void testFilterOrder() {
        Filter filterZero = createDefaultPatternFilter(0);
        Filter filterTwo = createDefaultPatternFilter(2); 
        Sorter sorter = new ShuttleSorter();
        assertEquals("order < 0", -1, sorter.order);
        Filter[] filters = new Filter[] {filterZero, filterTwo, sorter};
        FilterPipeline pipeline = new FilterPipeline(filters);
        assertOrder(filterZero, filters);
    }
    
    /**
     * order of filters must be retained.
     *
     */
    public void testSorterOrder() {
        Sorter sorter = new ShuttleSorter(); 
        assertEquals("order < 0", -1, sorter.order);
        Filter[] filters = new Filter[] {sorter, new ShuttleSorter(2, true)};
        FilterPipeline pipeline = new FilterPipeline(filters);
        // JW: pipeline inverts order of sorter - why?
       // assertOrders(filters);
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
    
    /**
     * A filter can be bound to maximally one pipeline.
     */
    public void testAssignFilterPipelineBoundFilterException() {
        Filter filter = createDefaultPatternFilter(0);
        assertEquals("order < 0", -1, filter.order);
        Filter[] filters = new Filter[] {filter};
        FilterPipeline pipeline = new FilterPipeline(filters);
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
        Filter filter = createDefaultPatternFilter(0);
        Filter[] filters = new Filter[] {filter};
        FilterPipeline pipeline = new FilterPipeline(filters);
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
     * returns a pipeline with two default patternfilters on
     * column 0, 2 and a sorter on column 0.
     */
    private FilterPipeline createPipeline() {
        Filter filterZero = createDefaultPatternFilter(0);
        Filter filterTwo = createDefaultPatternFilter(2); 
        Sorter sorter = new ShuttleSorter();
        Filter[] filters = new Filter[] {filterZero, filterTwo, sorter};
        FilterPipeline pipeline = new FilterPipeline(filters);
        return pipeline;
    }

    /** returns a PatternFilter for occurences of "e" in column.
     * 
     * @param column
     * @return
     */
    protected Filter createDefaultPatternFilter(int column) {
        Filter filterZero = new PatternFilter(".*e.*", 0, column);
        return filterZero;
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

    /**
     * ComponentAdapter directly on top of a TableModel.
     */
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

        public void refresh() {
            // do nothing
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
            boolean hasFilters;
            public void actionPerformed(ActionEvent e) {
                if (hasFilters) {
                    table.setFilters(null);
                } else {
                    table.setFilters(createPipeline());
//                    FilterPipeline pipeline = new FilterPipeline(new Filter[] {});
//                    table.setFilters(pipeline);
                }
                hasFilters = !hasFilters;
                
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
