/*
 * Created on 12.07.2005
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterTest.DirectModelAdapter;
import org.jdesktop.swingx.util.AncientSwingTeam;

/**
 * @author Jeanette Winzenburg
 */
public class SelectionIssues extends InteractiveTestCase {
    public SelectionIssues() {
        super("SelectionTest");
    }
    
    private TableModel ascendingModel;
    protected ComponentAdapter ascendingModelAdapter;

    public void testSelectionNullPipeline() {
        ListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.setSelectionInterval(0, 0);
        Selection selection = new Selection(null, selectionModel);
        selection.restoreSelection();
        assertTrue("selection must be retained", selectionModel.isSelectedIndex(0));
    }
    
    public void testSelectionSetPipeline() {
        ListSelectionModel selectionModel = new DefaultListSelectionModel();
        FilterPipeline pipeline = new FilterPipeline();
        // descending sorter
        pipeline.setSorter(new ShuttleSorter(0, false));
        pipeline.assign(ascendingModelAdapter);
        pipeline.flush();
        // select first in model coordinates
        int index = 0;
        selectionModel.setSelectionInterval(index, index);
        Selection selection = new Selection(null, selectionModel);
        selection.setFilters(pipeline);
//        selection.restoreSelection();
        assertEquals("view selection must be last", ascendingModelAdapter.getRowCount() - 1, 
                selectionModel.getMinSelectionIndex());
        
    }
    
    public void testKeepSelectionOnFilterChange() {
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        table.setRowSelectionInterval(selectedRow, 0);
        // sanity assert
        assertEquals(selectedRow, table.getSelectedRow());
        PatternFilter filter = new PatternFilter(".*", 0, 0);
        table.setFilters(new FilterPipeline(new Filter[] {filter}));
        assertEquals("table must keep selection after setting filter", selectedRow, table.getSelectedRow());
    }

    
    
    protected void setUp() throws Exception {
        super.setUp();
        ascendingModel = createAscendingModel(0, 20);
        ascendingModelAdapter = new DirectModelAdapter(ascendingModel);
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
        DefaultTableModel model = new DefaultTableModel(count, 5);
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
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
     * just to see the filtering effects...
     * 
     */
    public void interactiveTestColumnControlAndFilters() {
        final JXTable table = new JXTable(new AncientSwingTeam());
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
        SelectionIssues test = new SelectionIssues();
        try {
           test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

}
