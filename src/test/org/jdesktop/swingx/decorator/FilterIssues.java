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
