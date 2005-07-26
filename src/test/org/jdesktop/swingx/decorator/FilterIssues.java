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


    public void testConvertRowIndicesInLastAndPipeline() {
        Filter filterZero = createDefaultPatternFilter(0);
        Filter filterTwo = createDefaultPatternFilter(2); 
        Sorter sorter = new ShuttleSorter(2, true);
        Filter[] filters = new Filter[] {filterZero, filterTwo, sorter};
        FilterPipeline pipeline = new FilterPipeline(filters);
        pipeline.assign(directModelAdapter);
        
    }
    /**
     * trying to find out how much truth is in the api doc
     * for the conversion methods.
     * 
     * As it looks: pipeline _really_ converts all the way between
     * model <--> view, filter converts part of it. Between model and
     * "view" up to the filter.
     *
     */
    public void testConvertRowIndicesToModel() {
        FilterPipeline pipeline = createPipeline();
        Filter intermediateSorter = pipeline.last();
        intermediateSorter.setColumnIndex(2);
        // model contains 21 rows
        pipeline.assign(directModelAdapter);
        // this is 5
        int lastViewRow = pipeline.getOutputSize() - 1;
        Object firstValue = pipeline.getValueAt(0, 0);
        Object lastValue = pipeline.getValueAt(lastViewRow, 0);
        // modelrow is 18
        int modelRow = pipeline.convertRowIndexToModel(lastViewRow);
        Filter intermediateFilter1 = pipeline.previous(intermediateSorter);
        Filter intermediateFilter2 = pipeline.previous(intermediateFilter1);
        // first filter has same coordinates as pipeline
        assertEquals(modelRow, intermediateSorter.convertRowIndexToModel(lastViewRow)); 
        // filter previous to sorter has same output as filter
        // not really: was a coincidence that un-/sorted coordiates 
        // on last entry are the same!!
        // so we forced it to be different with setting the column to something else!
        // JW PENDING: need a better test model!
        // fails because expects "view" coordinates in this filter's "view" system!
        assertEquals(modelRow, intermediateFilter1.convertRowIndexToModel(lastViewRow)); 
         assertEquals(modelRow, intermediateFilter2.convertRowIndexToModel(lastViewRow)); 
        
    }

    /**
     * trying to find out how much truth is in the api doc
     * for the conversion methods.
     * 
     * As it looks: pipeline _really_ converts all the way between
     * model <--> view, filter converts part of it (?)
     *
     */
    public void testConvertRowIndicesToView() {
        FilterPipeline pipeline = createPipeline();
        Filter intermediateSorter = pipeline.last();
        intermediateSorter.setColumnIndex(2);
        // model contains 21 rows
        pipeline.assign(directModelAdapter);
        // this is 5
        int lastViewRow = pipeline.getOutputSize() - 1;
        Object firstValue = pipeline.getValueAt(0, 0);
        Object lastValue = pipeline.getValueAt(lastViewRow, 0);
        // modelrow is 18
        int modelRow = pipeline.convertRowIndexToModel(lastViewRow);
        Filter intermediateFilter1 = pipeline.previous(intermediateSorter);
        Filter intermediateFilter2 = pipeline.previous(intermediateFilter1);

        // now the other way round
        assertEquals(lastViewRow, pipeline.convertRowIndexToView(modelRow));
        assertEquals(lastViewRow, intermediateSorter.convertRowIndexToView(modelRow));
        // each filter converts from "real" model to it's own "view"
        // so we fail here  
        assertEquals(lastViewRow, intermediateFilter1.convertRowIndexToView(modelRow));
        assertEquals(lastViewRow, intermediateFilter2.convertRowIndexToView(modelRow));
        
    }
    
    public void testGetValue() {
        FilterPipeline pipeline = createPipeline();
        pipeline.assign(directModelAdapter);
        int size = pipeline.getOutputSize();
        Object firstValue = pipeline.getValueAt(0, 0);
        Object lastValue = pipeline.getValueAt(size - 1, 0);
        Sorter sorter = new ShuttleSorter();
        // nothing changed - we have an ascending sorter on column 0 in the pipeline
        pipeline.setSorter(sorter);
        assertEquals(size, pipeline.getOutputSize());
        assertEquals(firstValue, pipeline.getValueAt(0, 0));
        // sorter has same coordinate system as pipeline
        assertEquals(firstValue, sorter.getValueAt(0, 0));
        assertEquals(lastValue, sorter.getValueAt(size - 1, 0));
        sorter.setAscending(false);
        assertEquals(lastValue, sorter.getValueAt(0, 0));
        assertEquals(firstValue, sorter.getValueAt(size - 1, 0));
        Filter intermediateSorter = pipeline.previous(sorter);
        Filter intermediateFilter1 = pipeline.previous(intermediateSorter);
        Filter intermediateFilter2 = pipeline.previous(intermediateFilter1);
        // sanity check - to assure that filter.getValueAt(...) takes
        // row indices in filter row-coordinates
        assertTrue(intermediateFilter2 instanceof PatternFilter);
        assertEquals(lastValue, intermediateFilter2.getValueAt(0, 0));
        assertEquals(firstValue, intermediateFilter2.getValueAt(size - 1, 0));
        
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
