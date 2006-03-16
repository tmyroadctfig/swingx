/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.event.ActionEvent;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.util.AncientSwingTeam;
import org.jdesktop.swingx.util.PipelineReport;

public class FilterTest extends InteractiveTestCase {

    public FilterTest() {
        super("FilterTest");
    }
    
    private TableModel tableModel;
    protected ComponentAdapter directModelAdapter;
    private PipelineReport pipelineReport;

//----------------------- testing sorter notification
//----------------------- will happen only if in pipeline so we do it here 
    /**
     * test notification from sorter after setSortkey.
     * Guarantee refresh notification with same direction, columnIndex
     * but different Comparator.
     */
    public void testSorterSortKeyComparatorRefresh() {
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
        // create a sorter for column 0, ascending, 
        // without explicit comparator
        Sorter sorter = new ShuttleSorter();
        pipeline.setSorter(sorter);
        pipeline.addPipelineListener(pipelineReport);
        // create sortKey with other sort direction on column
        SortKey sortKey = new SortKey(SortOrder.ASCENDING, 
                sorter.getColumnIndex(), Collator.getInstance());
        sorter.setSortKey(sortKey);
        // sanity: sorter and sortKey synched
        SorterTest.assertSorterSortKeySynched(sortKey, sorter);
        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.CONTENTS_CHANGED));
    }

    /**
     * test notification from sorter after setSortkey.
     * Guarantee refresh notification with same columnIndex.
     */
    public void testSorterSortKeyRefresh() {
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
        // create a sorter for column 0, ascending, 
        // without explicit comparator
        Sorter sorter = new ShuttleSorter();
        pipeline.setSorter(sorter);
        pipeline.addPipelineListener(pipelineReport);
        // create sortKey with other sort direction on column
        SortKey sortKey = new SortKey(SortOrder.DESCENDING, sorter.getColumnIndex());
        sorter.setSortKey(sortKey);
        // sanity: sorter and sortKey synched
        SorterTest.assertSorterSortKeySynched(sortKey, sorter);
        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.CONTENTS_CHANGED));
    }

    /**
     * test notification from sorter after setSortkey.
     * Guarantee exactly one refresh notification.
     */
    public void testSorterSortKeyOneRefresh() {
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
        // create a sorter for column 0, ascending, 
        // without explicit comparator
        Sorter sorter = new ShuttleSorter();
        pipeline.setSorter(sorter);
        pipeline.addPipelineListener(pipelineReport);
        // create sortKey with other sort direction on column
        SortKey sortKey = new SortKey(SortOrder.DESCENDING, sorter.getColumnIndex() +1);
        sorter.setSortKey(sortKey);
        // sanity: sorter and sortKey synched
        SorterTest.assertSorterSortKeySynched(sortKey, sorter);
        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.CONTENTS_CHANGED));
    }
    /**
     * test notification from sorter after setSortkey.
     * Guarantee no refresh notification on same.
     */
    public void testSorterSortKeyNoRefresh() {
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
        // create a sorter for column 0, ascending, 
        // without explicit comparator
        Comparator comparator = Collator.getInstance();
        Sorter sorter = new ShuttleSorter(0, true, comparator);
        pipeline.setSorter(sorter);
        pipeline.addPipelineListener(pipelineReport);
        // create sortKey with other sort direction on column
        SortKey sortKey = new SortKey(SortOrder.ASCENDING, 
                sorter.getColumnIndex(), sorter.getComparator());
        sorter.setSortKey(sortKey);
        // sanity: sorter and sortKey synched
        SorterTest.assertSorterSortKeySynched(sortKey, sorter);
        assertFalse("sorter must not refresh without state change", pipelineReport.hasEvents());
    }

//---------------------------------SortController/Sorter in FilterPipeline
 
    public void testSortControllerToggleWithComparator() {
        FilterPipeline pipeline = createAssignedPipeline(true);
        SortController controller = pipeline.getSortController();
//        controller.toggleSortOrder(0, Collator.getInstance());
    }
    
    /**
     * creates and returns a FilterPipeline assigned to directModelAdapter.
     * Registers pipelineReport if withReport.
     * 
     * @param withReport flag to indicate if pipelineReport should be registered.
     * @return
     */
    private FilterPipeline createAssignedPipeline(boolean withReport) {
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
        if (withReport) {
            pipeline.addPipelineListener(pipelineReport);
        }
        return pipeline;
    }

    /**
     * Guarantee that Pipeline's Sorter and SortController are in synch
     * after setting properties of SortController.
     *
     */
    public void testSortControllerToggleUpdatesSameSorter() {
        FilterPipeline pipeline = createAssignedPipeline(false);
        int column = 2;
        pipeline.setSorter(new ShuttleSorter(column, false));
        Sorter sorter = pipeline.getSorter();
        pipeline.addPipelineListener(pipelineReport);
        SortController controller = pipeline.getSortController();
        controller.toggleSortOrder(column);
        assertFalse("toggleSortOrder must have initialized sortKey", controller.getSortKeys().isEmpty());
        // we assume that there's exactly one sortkey created!
        SortKey sortKey = controller.getSortKeys().get(0);
        assertTrue(pipeline.getSorter().isAscending());
        assertEquals(column, sortKey.getColumn());
        assertSame(sorter, pipeline.getSorter());
        SorterTest.assertSorterSortKeySynched(sortKey, pipeline.getSorter());
        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.CONTENTS_CHANGED));
    }

    /**
     * Guarantee that Pipeline's Sorter and SortController are in synch
     * after setting properties of SortController.
     *
     */
    public void testSortControllerToggleUpdatesSorter() {
        FilterPipeline pipeline = createAssignedPipeline(false);
        int column = 2;
        pipeline.setSorter(new ShuttleSorter(column, false));
        pipeline.addPipelineListener(pipelineReport);
        SortController controller = pipeline.getSortController();
        int newColumn = column - 1;
        controller.toggleSortOrder(newColumn);
        assertFalse("toggleSortOrder must have initialized sortKey", controller.getSortKeys().isEmpty());
        // we assume that there's exactly one sortkey created!
        SortKey sortKey = controller.getSortKeys().get(0);
        assertTrue(pipeline.getSorter().isAscending());
        SorterTest.assertSorterSortKeySynched(sortKey, pipeline.getSorter());
        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.CONTENTS_CHANGED));
    }

    /**
     * Guarantee that Pipeline's Sorter is correctly initialized 
     * after setting properties of SortController.
     *
     */
    public void testSortControllerToggleInitSorter() {
        FilterPipeline pipeline = createAssignedPipeline(true);
        int column = 2;
        SortController controller = pipeline.getSortController();
        controller.toggleSortOrder(column);
        assertFalse("toggleSortOrder must have initialized sortKey", controller.getSortKeys().isEmpty());
        // we assume that there's exactly one sortkey created!
        SortKey sortKey = controller.getSortKeys().get(0);
        SorterTest.assertSorterSortKeySynched(sortKey, pipeline.getSorter());
        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.CONTENTS_CHANGED));
    }

    /**
     * Guarantee that Pipeline's Sorter and SortController are in synch
     * after setting properties of SortController.
     *
     */
    public void testSortControllerResetRemovesSorter() {
        FilterPipeline pipeline = createAssignedPipeline(false);
        int column = 2;
        pipeline.setSorter(new ShuttleSorter(column, true));
        SortController controller = pipeline.getSortController();
        controller.setSortKeys(Collections.EMPTY_LIST);
        assertNull(pipeline.getSorter());
    }
    
    /**
     * Guarantee that Pipeline's Sorter and SortController are in synch
     * after setting properties of SortController.
     *
     */
    public void testSortControllerSortKeysUpdatesSameSorter() {
        FilterPipeline pipeline = createAssignedPipeline(false);
        pipeline.setSorter(new ShuttleSorter());
        Sorter sorter = pipeline.getSorter();
        pipeline.addPipelineListener(pipelineReport);
        SortController controller = pipeline.getSortController();
        SortKey sortKey = new SortKey(SortOrder.DESCENDING, sorter.getColumnIndex());
        controller.setSortKeys(Collections.singletonList(sortKey));
        assertSame(sorter, pipeline.getSorter());
        SorterTest.assertSorterSortKeySynched(sortKey, pipeline.getSorter());
        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.CONTENTS_CHANGED));
    }
    /**
     * Guarantee that Pipeline's Sorter and SortController are in synch
     * after setting properties of SortController.
     *
     */
    public void testSortControllerSortKeysUpdatesSorter() {
        FilterPipeline pipeline = createAssignedPipeline(false);
        int column = 2;
        pipeline.setSorter(new ShuttleSorter(column, true));
        pipeline.addPipelineListener(pipelineReport);
        SortController controller = pipeline.getSortController();
        int newColumn = column - 1;
        SortKey sortKey = new SortKey(SortOrder.DESCENDING, newColumn);
        controller.setSortKeys(Collections.singletonList(sortKey));
        SorterTest.assertSorterSortKeySynched(sortKey, pipeline.getSorter());
        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.CONTENTS_CHANGED));
    }
    
    /**
     * Guarantee that Pipeline's Sorter is correctly initialized 
     * after setting properties of SortController.
     *
     */
    public void testSortControllerSortKeysInitSorter() {
        FilterPipeline pipeline = createAssignedPipeline(true);
        int column = 2;
        SortController controller = pipeline.getSortController();
        SortKey sortKey = new SortKey(SortOrder.DESCENDING, column);
        controller.setSortKeys(Collections.singletonList(sortKey));
        SorterTest.assertSorterSortKeySynched(sortKey, pipeline.getSorter());
        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.CONTENTS_CHANGED));
    }
    /**
     * initial addition of SortController 
     * (== basically renamed Jesse's RowSorter).
     * no active sorter.
     */
    public void testSortControllerWithoutSorter() {
        FilterPipeline pipeline = createAssignedPipeline(false);
        SortController controller = pipeline.getSortController();
        assertNotNull(controller);
        // test all method if nothing is sorted
        assertEquals(SortOrder.UNSORTED, controller.getSortOrder(0));
        assertNotNull(controller.getSortKeys());
    }
    
    /**
     * initial addition of SortController 
     * Guarantee that SortController getters are in synch with Sorter.
     */
    public void testSortControllerWithSorter() {
        FilterPipeline pipeline = createAssignedPipeline(false);
        int column = 2;
        pipeline.setSorter(new ShuttleSorter(column, true));
        SortController controller = pipeline.getSortController();
        assertNotNull(controller);
        // test all method if sorter ascending sorter on column 
        assertEquals(SortOrder.ASCENDING, controller.getSortOrder(column));
        List<? extends SortKey> sortKeys = controller.getSortKeys();
        assertNotNull(sortKeys);
        assertEquals(1, sortKeys.size());
        SortKey sortKey = sortKeys.get(0);
        assertEquals(SortOrder.ASCENDING, sortKey.getSortOrder());
        assertEquals(column, sortKey.getColumn());
        // sanity: doesn't effect unsorted column
        assertEquals(SortOrder.UNSORTED, controller.getSortOrder(column - 1));
    }
    
    public void testSortOrderChangedEvent() {
        FilterPipeline pipeline = createAssignedPipeline(true);
        
        pipeline.setSorter(new ShuttleSorter());
        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.CONTENTS_CHANGED));
        // expect 2 events: one for sortOrderChanged, one for contentsChanged
        // not yet implemented - has implications on other tests, so go for
        // one type of events only ... 
//        assertEquals(1, pipelineReport.getEventCount(PipelineEvent.SORT_ORDER_CHANGED));
//        PipelineEvent event = pipelineReport.getLastEvent(PipelineEvent.SORT_ORDER_CHANGED);
//        assertEquals(PipelineEvent.SORT_ORDER_CHANGED, event.getType());
    }

//----------------------------------
    
    /**
     * reported on swingx-dev mailing list:
     * chained filters must AND - as they did. 
     * Currently (10/2005) they OR ?.
     * 
     * Hmm, can't reproduce - JW.
     */
    public void testAndFilter() {
        PatternFilter first = new PatternFilter("a", 0, 0);
        PatternFilter second = new PatternFilter("b", 0, 1);
        FilterPipeline pipeline = new FilterPipeline(new Filter[] {first, second});
        pipeline.assign(directModelAdapter);
        assertTrue(pipeline.getOutputSize() > 0);
        for (int i = 0; i < pipeline.getOutputSize(); i++) {
            boolean firstMatch = first.getPattern().matcher(pipeline.getValueAt(i, 0).toString()).find();
            boolean secondMatch = second.getPattern().matcher(pipeline.getValueAt(i, 1).toString()).find();
            assertTrue(firstMatch);
            assertEquals("both matchers must find", firstMatch, secondMatch); 
        }
        
    }
    
    /**
     * Issue ??-swingx
     * pipeline should auto-flush on assigning adapter.
     *
     */
    public void testFlushOnAssign() {
        Filter filter = new PatternFilter(".*", 0, 0);
        FilterPipeline pipeline = new FilterPipeline(new Filter[] { filter });
        pipeline.assign(directModelAdapter);
        assertEquals("pipeline output size must be model count", 
                directModelAdapter.getRowCount(), pipeline.getOutputSize());
        // JW PENDING: remove necessity to explicitly flush...
        Object value = pipeline.getValueAt(0, 0);
        assertEquals("value access via sorter must return the same as via pipeline", 
               value, pipeline.getValueAt(0, 0));
        
    }
    /**
     * test notification on setSorter: must fire on change only.
     *
     */
    public void testPipelineEventOnSameSorter() {
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
        pipeline.addPipelineListener(pipelineReport);
        Sorter sorter = new ShuttleSorter();
        pipeline.setSorter(sorter);
        assertEquals("pipeline must have fired on setSorter", 1, pipelineReport.getEventCount());
        pipelineReport.clear();
        pipeline.setSorter(sorter);
        assertEquals("pipeline must not have fired on same setSorter", 0, pipelineReport.getEventCount());
        
    }

    /**
     * test notification on setSorter.
     *
     */
    public void testPipelineEventOnSetSorter() {
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
        pipeline.addPipelineListener(pipelineReport);
        pipeline.setSorter(new ShuttleSorter());
        assertEquals("pipeline must have fired on setSorter", 1, pipelineReport.getEventCount());
        pipelineReport.clear();
        pipeline.setSorter(null);
        assertEquals("pipeline must have fired on setSorter null", 1, pipelineReport.getEventCount());
        
    }

    /**
     * test notification on setSorter: must fire if assigned only.
     *
     */
    public void testPipelineEventOnSetSorterUnassigned() {
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.addPipelineListener(pipelineReport);
        pipeline.setSorter(new ShuttleSorter());
        assertEquals("pipeline must not fire if unassigned", 0, pipelineReport.getEventCount());
        pipelineReport.clear();
        pipeline.setSorter(null);
        assertEquals("pipeline must not fire if unassigned", 0, pipelineReport.getEventCount());
        
    }
    
    /**
     * Issue #45-swingx:
     * interpose should throw if trying to interpose to a pipeline
     * with a differnt ComponentAdapter.
     *
     */
    public void testSetSorterDiffComponentAdapter() {
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
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
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.assign(directModelAdapter);
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
        Filter filter = new PatternFilter("s", 0, sortColumn);
        Filter[] filters = new Filter[] {filter, new ShuttleSorter()};
        FilterPipeline pipeline = new FilterPipeline(filters);
        pipeline.assign(directModelAdapter);
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

    public void testUnassignedEmptyFilter() {
        FilterPipeline filters = new FilterPipeline();
        assertEquals(0, filters.getOutputSize());
    }

    /**
     * JW: test paranoia?
     *
     */
    public void testDirectComponentAdapterAccess() {
        FilterPipeline pipeline = createPipeline();
        pipeline.assign(directModelAdapter);
        assertTrue("pipeline must have filtered values", pipeline.getOutputSize() < directModelAdapter.getRowCount());
    }
    
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
        new FilterPipeline(filters);
        assertOrder(filterZero, filters);
    }
    

    /**
     * FilterPipeline allows maximal one sorter per column. 
     *
     */
    public void testDuplicatedSortColumnException() {
        Filter[] filters = new Filter[] {new ShuttleSorter(), new ShuttleSorter()};
        try {
            new FilterPipeline(filters);
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
        new FilterPipeline(filters);
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

    protected void assertOrders(Filter[] filters) {
        for (int i = 0; i < filters.length; i++) {
            assertEquals("order must be equal to filter position", i, filters[i].order);
        }
    }
    private int getFilterPosition(Filter filter, Filter[] filters) {
        for (int i = 0; i < filters.length; i++) {
            if (filters[i].equals(filter)) {
                return i;
            }
        }
        return -1;
    }
    /**
     * returns a pipeline with two default patternfilters on
     * column 0, 2 and an ascending sorter on column 0.
     */
    protected FilterPipeline createPipeline() {
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
        Filter filterZero = new PatternFilter("e", 0, column);
        return filterZero;
    }
    
    /**
     * This is a test to ensure that the example in the javadoc actually works.
     * if the javadoc example changes, then those changes should be pasted here.
     */
    public void testJavaDocExample() {

        Filter[] filters = new Filter[] {
            new PatternFilter("^S", 0, 1), // regex, matchflags, column
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
        pipelineReport = new PipelineReport();
     }

    /**
     * ComponentAdapter directly on top of a TableModel.
     */
    public static class DirectModelAdapter extends ComponentAdapter {

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
            return false;
        }

        public boolean isSelected() {
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
        Action toggleFilter = new AbstractAction("Toggle Filters") {
            boolean hasFilters;
            public void actionPerformed(ActionEvent e) {
                if (hasFilters) {
                    table.setFilters(null);
                } else {
                    table.setFilters(createPipeline());
                }
                hasFilters = !hasFilters;
                
            }
            
        };
        toggleFilter.putValue(Action.SHORT_DESCRIPTION, "filtering first column - problem if invisible ");
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl and Filters");
        addAction(frame, toggleFilter);
        frame.setVisible(true);
    }

    /** 
     * just to see the filtering effects...
     * 
     */
    public void interactiveTestAndFilter() {
        final JXTable table = new JXTable(tableModel);
        table.setColumnControlVisible(true);
        Action toggleFilter = new AbstractAction("Toggle Filters") {
            boolean hasFilters;
            public void actionPerformed(ActionEvent e) {
                if (hasFilters) {
                    table.setFilters(null);
                } else {
                    PatternFilter first = new PatternFilter("a", 0, 0);
                    PatternFilter second = new PatternFilter("b", 0, 1);
                    FilterPipeline pipeline = new FilterPipeline(new Filter[] {first, second});
                    table.setFilters(pipeline);
                }
                hasFilters = !hasFilters;
                
            }
            
        };
        toggleFilter.putValue(Action.SHORT_DESCRIPTION, 
                "Filtered rows: col(0) contains 'a' AND col(1) contains 'b'");
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl and Filters");
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
