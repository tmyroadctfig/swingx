/*
 * Created on 12.07.2005
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterTest.DirectModelAdapter;
import org.jdesktop.swingx.util.AncientSwingTeam;

/**
 * @author Jeanette Winzenburg
 */
public class SelectionMapperTest extends InteractiveTestCase {
    public SelectionMapperTest() {
        super("SelectionMapperTest");
    }
    
    private TableModel ascendingModel;
    protected ComponentAdapter ascendingModelAdapter;

    /**
     * sanity: understand DefaultListSelectionModel behaviour.
     * 
     * behaviour: if any selected (==lead/anchor) and selection cleared then the
     * selection is empty and lead/anchor still on old value.
     * 
     *
     */
    public void testLeadAnchorAfterClearSelection() {
        ListSelectionModel viewSelectionModel = new DefaultListSelectionModel();
        int selected = 5;
        viewSelectionModel.setSelectionInterval(selected, selected);
        assertEquals(selected, viewSelectionModel.getAnchorSelectionIndex());
        assertEquals(selected, viewSelectionModel.getLeadSelectionIndex());
        viewSelectionModel.clearSelection();
        int anchor = selected;
        assertTrue(viewSelectionModel.isSelectionEmpty());
        assertEquals(anchor, viewSelectionModel.getAnchorSelectionIndex());
        assertEquals(anchor, viewSelectionModel.getLeadSelectionIndex());
        
    }

    /**
     * sanity: understand DefaultListSelectionModel behaviour.
     * 
     * behaviour: if "last" selected (==lead/anchor) and removed then the
     * selection is empty but lead/anchor are on the new "last" row.
     *
     */
    public void testLeadAnchorAfterRemove() {
        ListSelectionModel viewSelectionModel = new DefaultListSelectionModel();
        int selected = 5;
        viewSelectionModel.setSelectionInterval(selected, selected);
        assertEquals(selected, viewSelectionModel.getAnchorSelectionIndex());
        assertEquals(selected, viewSelectionModel.getLeadSelectionIndex());
        viewSelectionModel.removeIndexInterval(5, 5);
        int anchor = selected -1;
        assertTrue(viewSelectionModel.isSelectionEmpty());
        assertEquals(anchor, viewSelectionModel.getAnchorSelectionIndex());
        assertEquals(anchor, viewSelectionModel.getLeadSelectionIndex());
        
    }
    /**
     * Related to #186-swingx: Lead/anchor not correctly synched.
     *
     */
    public void testSynchLeadSelection() {
        ListSelectionModel viewSelectionModel = new DefaultListSelectionModel();
        int selected = 0;
        viewSelectionModel.setSelectionInterval(selected, selected);
        FilterPipeline pipeline =  null; //new FilterPipeline();
//        pipeline.assign(ascendingModelAdapter);
        DefaultSelectionMapper selectionMapper = new DefaultSelectionMapper(pipeline, viewSelectionModel);
        int anchor = selected;
        int lead = selected;
        assertAnchorLeadSynched(anchor, lead, viewSelectionModel, selectionMapper);
        anchor = 2;
        viewSelectionModel.setValueIsAdjusting(true);
        viewSelectionModel.setAnchorSelectionIndex(anchor);
        viewSelectionModel.setValueIsAdjusting(false);
//        pipeline.flush();
        assertAnchorLeadSynched(anchor, lead, viewSelectionModel, selectionMapper);
        
    }
    
    public void testAnchorLeadSelection() {
        ListSelectionModel viewSelectionModel = new DefaultListSelectionModel();
        int selected = 0;
        viewSelectionModel.setSelectionInterval(selected, selected);
        int anchor = selected;
        int lead = selected;
        assertAnchorLead(anchor, lead, viewSelectionModel);
        anchor = 2;
        viewSelectionModel.setValueIsAdjusting(true);
        viewSelectionModel.setAnchorSelectionIndex(anchor);
        viewSelectionModel.setValueIsAdjusting(false);
        assertAnchorLead(anchor, lead, viewSelectionModel);
        
    }
    private void assertAnchorLeadSynched(int anchor, int lead, ListSelectionModel viewSelection, DefaultSelectionMapper mapper) {
       assertAnchorLead(anchor, lead, viewSelection);
       assertAnchorLead(anchor, lead, mapper.modelSelection);
    }
    
    private void assertAnchorLead(int anchor, int lead, ListSelectionModel viewSelection) {
        assertEquals("anchor", anchor, viewSelection.getAnchorSelectionIndex());
        assertEquals("lead", lead, viewSelection.getLeadSelectionIndex());
        
    }
    public void testSelectionNullPipeline() {
        ListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.setSelectionInterval(0, 0);
        DefaultSelectionMapper selectionMapper = new DefaultSelectionMapper(null, selectionModel);
        selectionMapper.mapTowardsView();
        assertTrue("selection must be retained", selectionModel.isSelectedIndex(0));
    }
 
    public void testLeadSelectionSetPipeline() {
        ListSelectionModel selectionModel = new DefaultListSelectionModel();
        // select first in "model" coordinates
        int index0 = 0;
        int lead = 2;
        int index1 = 4;
        selectionModel.addSelectionInterval(index0, index0);
        selectionModel.addSelectionInterval(index1, index1);
        selectionModel.addSelectionInterval(lead, lead);
        assertEquals("lead is last selected", lead, selectionModel.getLeadSelectionIndex());
        FilterPipeline pipeline = new FilterPipeline();
        // descending sorter
        pipeline.setSorter(new ShuttleSorter(0, false));
        pipeline.assign(ascendingModelAdapter);
        new DefaultSelectionMapper(pipeline, selectionModel);
        assertEquals("lead selection must be last added", 
                pipeline.convertRowIndexToView(lead), 
                selectionModel.getLeadSelectionIndex());
    }

    public void testSelectionSetPipeline() {
        ListSelectionModel selectionModel = new DefaultListSelectionModel();
        FilterPipeline pipeline = new FilterPipeline();
        // descending sorter
        pipeline.setSorter(new ShuttleSorter(0, false));
        pipeline.assign(ascendingModelAdapter);
//        pipeline.flush();
        // select first in model coordinates
        int index = 0;
        selectionModel.setSelectionInterval(index, index);
        SelectionMapper selectionMapper = new DefaultSelectionMapper(null, selectionModel);
        selectionMapper.setFilters(pipeline);
//        selection.mapTowardsView();
        assertEquals("view selection must be last", ascendingModelAdapter.getRowCount() - 1, 
                selectionModel.getMinSelectionIndex());
        
    }


    /**
     * Issue #187: keep selection on filter change
     *
     */
    public void testKeepSelectionOnFilterChange() {
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // sanity assert
        assertEquals(selectedRow, table.getSelectedRow());
        PatternFilter filter = new PatternFilter(".*", 0, 0);
        table.setFilters(new FilterPipeline(new Filter[] {filter}));
        assertEquals(ascendingModel.getRowCount(), table.getRowCount());
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
     * @return table model
     */
    private DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 5) {
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
     * @return a PatternFilter for occurences of "e" in column
     */
    protected Filter createDefaultPatternFilter(int column) {
        Filter filterZero = new PatternFilter("e", 0, column);
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
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl and Filters");
        addAction(frame, toggleFilter);
        frame.setVisible(true);
    }

    public static void main(String args[]) {
        setSystemLF(true);
        SelectionMapperTest test = new SelectionMapperTest();
        try {
           test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

}
