/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.decorator;

import javax.swing.table.DefaultTableModel;

/**
 * @author Jeanette Winzenburg
 */
public class FilterIssues extends FilterTest {

    /**
     * Issue #45-swingx:
     * interpose should throw if trying to interpose to a pipeline
     * with a differnt ComponentAdapter.
     *
     */
    public void testInterposeDiffComponentAdapter() {
        int sortColumn = 0;
        Filter[] filters = new Filter[] {};
        FilterPipeline pipeline = new FilterPipeline(filters);
        pipeline.assign(directModelAdapter);
        pipeline.flush();
        Object value = pipeline.getValueAt(0, sortColumn);
        Object lastValue = pipeline.getValueAt(pipeline.getOutputSize() - 1, sortColumn);
        Sorter sorter = new ShuttleSorter();
        try {
            sorter.interpose(pipeline, new DirectModelAdapter(new DefaultTableModel(10, 5)), null);
            fail("interposing with a different adapter must throw an IllegalargumentException");
        } catch (IllegalArgumentException ex) {
            
        } catch (Exception e) {
            fail("interposing with a different adapter must throw an " +
                    "IllegalargumentException instead of " + e);
        }
//        assertEquals("value must be unchanged by interactive sorter", value, sorter.getValueAt(0, sortColumn));
//        sorter.setAscending(false);
//        assertEquals("first value must be old last", lastValue, sorter.getValueAt(0, sortColumn));
        
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
     */
    public void testEmptyPipeline() {
        int sortColumn = 0;
        Filter[] filters = new Filter[] {};
        FilterPipeline pipeline = new FilterPipeline(filters);
        pipeline.assign(directModelAdapter);
//        pipeline.flush();
        assertEquals("size must be number of rows in adapter", 
                directModelAdapter.getRowCount(), pipeline.getOutputSize());
        Object value = pipeline.getValueAt(0, sortColumn);
        assertEquals(directModelAdapter.getValueAt(0, sortColumn), value);
    }
 
    
    public void testSorterInEmptyPipeline() {
        int sortColumn = 0;
        Filter[] sorters = new Filter[] {new ShuttleSorter()};
        FilterPipeline sortedPipeline = new FilterPipeline(sorters);
        sortedPipeline.assign(directModelAdapter);
        sortedPipeline.flush();
        Object sortedValue = sortedPipeline.getValueAt(0, sortColumn);
        Filter[] filters = new Filter[] {};
        FilterPipeline pipeline = new FilterPipeline(filters);
        pipeline.assign(directModelAdapter);
     //   assert
        Sorter sorter = new ShuttleSorter();
        pipeline.setSorter(sorter);
        assertEquals(sortedValue, sorter.getValueAt(0, sortColumn));
        
    }
    /** does nothing currently. 
     * 
     *
     */
    public void testInterposeEmptyPipeline() {
        int sortColumn = 0;
        Filter[] filters = new Filter[] {};
        FilterPipeline pipeline = new FilterPipeline(filters);
        pipeline.assign(directModelAdapter);
        pipeline.flush();
        Object value = pipeline.getValueAt(0, sortColumn);
        Object lastValue = pipeline.getValueAt(pipeline.getOutputSize() - 1, sortColumn);
        // JW: Hmmm... need to test differently - the orig is unsorted
//        Sorter sorter = new ShuttleSorter();
//        sorter.interpose(pipeline, directModelAdapter, null);
//        assertEquals("value must be unchanged by interactive sorter", value, sorter.getValueAt(0, sortColumn));
//        sorter.setAscending(false);
//        assertEquals("first value must be old last", lastValue, sorter.getValueAt(0, sortColumn));
        
    }
      
    /**
     * what is the benefit of passing the old sorter?
     *
     */
      public void testInterposeWithOldSorter() {
//          int sortColumn = 0;
//          Filter[] filters = new Filter[] {createDefaultPatternFilter(sortColumn), new ShuttleSorter()};
//          FilterPipeline pipeline = new FilterPipeline(filters);
//          pipeline.assign(directModelAdapter);
//          pipeline.flush();
//          Object value = pipeline.getValueAt(0, sortColumn);
//          Object lastValue = pipeline.getValueAt(pipeline.getOutputSize() - 1, sortColumn);
//          Sorter sorter = new ShuttleSorter();
//          sorter.interpose(pipeline, directModelAdapter, null);
//          assertEquals("value must be unchanged by interactive sorter", value, sorter.getValueAt(0, sortColumn));
//          sorter.setAscending(false);
//          assertEquals("first value must be old last", lastValue, sorter.getValueAt(0, sortColumn));
//          Sorter newSorter = new ShuttleSorter();
//          newSorter.interpose(pipeline, directModelAdapter, sorter);
//          assertEquals("value must be unchanged by interactive sorter", value, newSorter.getValueAt(0, sortColumn));
          
          
      }

}
