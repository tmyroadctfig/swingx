/*
 * $Id: JXListTest.java 3199 2009-01-21 18:37:28Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.sort.ListSortController;
import org.jdesktop.swingx.sort.SortManager;
import org.jdesktop.test.ListDataReport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Subset of tests for currently disabled sorting. Obviously, they are failing.
 * Once re-added support, we'll need analogous tests.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class JXListSortRevamp extends InteractiveTestCase {

    protected ListModel listModel;
    protected DefaultListModel ascendingListModel;

    public static void main(String[] args) {
        JXListSortRevamp test = new JXListSortRevamp();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

//------------------ re-enable
    
    @Test
    public void interactiveRowSorter() {
        JXList list = new JXList(ascendingListModel);
        final ListSortController<ListModel> controller = new ListSortController<ListModel>(list.getModel());
        list.setRowSorter(controller);
        new SortManager(controller, list);
        Action sort = new AbstractAction("toggle sort") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.toggleSortOrder(0);
                
            }
        };
        JXFrame frame = showWithScrollingInFrame(list, "sort in rowSorter");
        addAction(frame, sort);
        show(frame);
    }
//-------------------- failing tests    
    /**
     * Issue #855-swingx: throws AIOOB on repeated remove/add.
     * Reason is that the lead/anchor is not removed in removeIndexInterval
     */
    @Test
    public void testAddRemoveSelect() {
        fail("list sorting/filtering disabled");
        DefaultListModel model = new DefaultListModel();
        model.addElement("something");
        JXList list = new JXList(model, true);
        list.setSortOrder(SortOrder.ASCENDING);
        list.setSelectedIndex(0);
        model.remove(0);
        assertTrue("sanity - empty selection after remove", list.isSelectionEmpty());
        model.addElement("element");
        assertTrue("sanity - empty selection re-adding", list.isSelectionEmpty());
        list.setSelectedIndex(0);
    }
    
    /**
     * Issue #855-swingx: throws AIOOB on repeated remove/add.
     * Reason is that the lead/anchor is not removed in removeIndexInterval.
     * 
     * Compare JXTable behaviour: doesn't blow. JXList probably does because of 
     * the necessary event mapping. Sequence of selection/pipeline induced 
     * cleanup is different.
     * 
     */
    @Test
    public void testAddRemoveSelectTable() {
        DefaultTableModel model = new DefaultTableModel(0, 1);
        model.addRow(new Object[] {"something"});
        JXTable list = new JXTable(model);
        list.setSortOrder(0, SortOrder.ASCENDING);
        list.setRowSelectionInterval(0, 0);
        model.removeRow(0);
        assertTrue("sanity - empty selection after remove", list.getSelectionModel().isSelectionEmpty());
        model.addRow(new Object[] {"something"});
        assertTrue("sanity - empty selection re-adding", list.getSelectionModel().isSelectionEmpty());
        list.setRowSelectionInterval(0, 0);
    }

    

    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * Here: test single item remove fires interval removed
     */
    @Test
    public void testListDataEventRemove() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove row 
        ascendingListModel.remove(0);
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type removed", 
                1, report.getRemovedEventCount());
        assertEquals("the index of removed item  ", 0, report.getLastEvent().getIndex0());
        
    }
    
    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * Here: test that a single item removal of a filtered element doesn't fire.
     */
    @Test
    public void testListDataEventRemoveSingleFiltered() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        // filter out the first 10
//        list.setFilters(new FilterPipeline(createNumberFilter(10, 100, true)));
        assertEquals(10, list.getElementCount());
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove a row which had been filtered 
        ascendingListModel.remove(0);
        assertEquals("list must not have fired event for filtered remove", 
                0, report.getEventCount());
    }
    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * Here: test that a removing a interval which is partly filtered maps
     *   correctly to the visible interval.
     *   
     */
    @Test
    public void testListDataEventRemoveCutIntervalFiltered() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        // filter out the first 10
//        list.setFilters(new FilterPipeline(createNumberFilter(10, 100, true)));
        assertEquals(10, list.getElementCount());
        assertEquals(10, list.getElementAt(0));
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove a row which had been filtered 
        ascendingListModel.removeRange(8, 12);
        assertEquals(7, ascendingListModel.get(7));
        assertEquals(13, ascendingListModel.get(8));
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type removed", 
                1, report.getRemovedEventCount());
        assertEquals("first removed index ", 0, report.getLastRemovedEvent().getIndex0());
        assertEquals("last removed index", 2, report.getLastRemovedEvent().getIndex1());
    }
    
    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * Here: a removing a interval including all unfiltered value
     *   
     */
    @Test
    public void testListDataEventRemoveIntervalFilteredAll() {
        fail("list sorting/filtering disabled");
      JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        // filter out the first 7 and last 7
//        list.setFilters(new FilterPipeline(createNumberFilter(8, 12, true)));
        assertEquals(5, list.getElementCount());
        assertEquals(8, list.getElementAt(0));
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove a row which had been filtered 
        ascendingListModel.removeRange(7, 12);
        assertEquals(0, list.getElementCount());
        assertEquals(14, ascendingListModel.getSize());
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type removed", 
                1, report.getRemovedEventCount());
        assertEquals("first removed index ", 0, report.getLastRemovedEvent().getIndex0());
        assertEquals("last removed index", 4, report.getLastRemovedEvent().getIndex1());
    }
    
    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * set single item fires contentsChanged with single index.
     */
    @Test
    public void testListDataEventSingleSet() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // sanity
        assertEquals(0, report.getEventCount());
        // remove row 
        ascendingListModel.setElementAt(-1, 0);
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type changed", 
                1, report.getChangedEventCount());
        assertEquals("changed index ", 0, report.getLastEvent().getIndex1());
        
    }
    
    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * insert fires intervalAdded.
     */
    @Test
    public void testListDataEventAdd() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove row 
        ascendingListModel.insertElementAt(-1, 0);
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type added", 
                1, report.getAddedEventCount());
        
    }


    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * event source is the wrapping list model.
     */
    @Test
    public void testListDataEventSource() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // some change
        ascendingListModel.insertElementAt(-1, 0);
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type changed", 
                list.getModel(), report.getLastEvent().getSource());
        
    }

    
    /**
     * Issue #477-swingx: list with filter not updated after setModel.
     * 
     * Reason is that there's no call to filter.flush in that path
     * of action. Probably due to mostly c&p from JXTable - which
     * always goes through tableChanged (which JList doesn't).
     * How to test?
     *
     */
    @Test
    public void testSetModelFlushFilter() {
        fail("list sorting/filtering disabled");
        final JXList list = new JXList();
//        list.setFilterEnabled(true);
//        PatternFilter filter = new PatternFilter(".*1.*", 0, 0);
//        final FilterPipeline pipeline = new FilterPipeline(filter);
//        final DefaultListModel model = new DefaultListModel();
//        for (int i = 0; i < 10; i++)
//            model.addElement("Element " + i);
//        list.setFilters(pipeline);
//        list.setModel(model);
//        assertEquals(1, list.getElementCount());
    }

    /**
     * Issue #477-swingx:
     * 
     * Selection must be cleared after setModel. This is from
     * super's contract.
     *
     */
    @Test
    public void testSetModelEmptySelection() {
        fail("list sorting/filtering disabled");
        final JXList list = new JXList(true);
        final DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 10; i++)
            model.addElement("Element " + i);
        list.setModel(model);
        int selection = 0;
        list.setSelectedIndex(selection);
//        PatternFilter filter = new PatternFilter(".*", 0, 0);
//        final FilterPipeline pipeline = new FilterPipeline(filter);
//        list.setFilters(pipeline);
        assertEquals("setting filters must keep selection", selection, list.getSelectedIndex());
        list.setModel(model);
        assertEquals(model.getSize(), list.getElementCount());
        assertTrue("setting model must clear selectioon", list.isSelectionEmpty());
    }
    
   
    /**
     * Test assumptions of accessing list model/view values through
     * the list's componentAdapter.
     * 
     * PENDING: the default's getValue() implementation is incorrect!
     *
     */
    @Test
    public void testComponentAdapterCoordinates() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        Object originalFirstRowValue = list.getElementAt(0);
        Object originalLastRowValue = list.getElementAt(list.getElementCount() - 1);
        assertEquals("view row coordinate equals model row coordinate", 
                list.getModel().getElementAt(0), originalFirstRowValue);
        // sort first column - actually does not change anything order 
        list.toggleSortOrder();
        // sanity asssert
        assertEquals("view order must be unchanged ", 
                list.getElementAt(0), originalFirstRowValue);
        // invert sort
        list.toggleSortOrder();
        // sanity assert
        assertEquals("view order must be reversed changed ", 
                list.getElementAt(0), originalLastRowValue);
        ComponentAdapter adapter = list.getComponentAdapter();
        assertEquals("adapter filteredValue expects row view coordinates", 
                list.getElementAt(0), adapter.getFilteredValueAt(0, 0));
        // adapter coordinates are view coordinates
        adapter.row = 0;
        adapter.column = 0;
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                list.getElementAt(0), adapter.getValue());
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                list.getElementAt(0), adapter.getValue(0));
    }
    

    /**
     * test filterEnabled property on initialization.
     *
     */
    @Test
    public void testConstructorFilterEnabled() {
        fail("list sorting/filtering disabled");
        // 
        assertFilterEnabled(new JXList(), false);
        assertFilterEnabled(new JXList(new DefaultListModel()), false);
        assertFilterEnabled(new JXList(new Vector<Object>()), false);
        assertFilterEnabled(new JXList(new Object[] { }), false);
        
        assertFilterEnabled(new JXList(false), false);
        assertFilterEnabled(new JXList(new DefaultListModel(), false), false);
        assertFilterEnabled(new JXList(new Vector<Object>(), false), false);
        assertFilterEnabled(new JXList(new Object[] { }, false), false);

        assertFilterEnabled(new JXList(true), true);
        assertFilterEnabled(new JXList(new DefaultListModel(), true), true);
        assertFilterEnabled(new JXList(new Vector<Object>(), true), true);
        assertFilterEnabled(new JXList(new Object[] { }, true), true);
    }
    
    private void assertFilterEnabled(JXList list, boolean b) {
        assertEquals(b, list.getAutoCreateRowSorter());
    }

    /**
     * added xtable.setSortOrder(int, SortOrder)
     * 
     */
    @Test
    public void testSetSortOrder() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        list.setSortOrder(SortOrder.ASCENDING);
        assertEquals("column must be sorted after setting sortOrder on ", SortOrder.ASCENDING, list.getSortOrder());
    }
    
    /**
     * JXList has responsibility to guarantee usage of 
     * its comparator: setComparator if already sorted.
     */
    @Test
    public void testDynamicComparatorToSortController() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(listModel, true);
        list.toggleSortOrder();
        list.setComparator(Collator.getInstance());
//        SortKey sortKey = SortKey.getFirstSortKeyForColumn(list.getFilters().getSortController().getSortKeys(), 0);
//        assertNotNull(sortKey);
//        assertEquals(list.getComparator(), sortKey.getComparator());
    }

    /**
     * JXList has responsibility to guarantee usage of 
     * its comparator: toggle.
     */
    @Test
    public void testToggleComparatorToSortController() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(listModel, true);
        list.setComparator(Collator.getInstance());
        list.toggleSortOrder();
//        SortKey sortKey = SortKey.getFirstSortKeyForColumn(list.getFilters().getSortController().getSortKeys(), 0);
//        assertNotNull(sortKey);
//        assertEquals(list.getComparator(), sortKey.getComparator());
    }

    /**
     * JXList has responsibility to guarantee usage of 
     * its comparator: set.
     */
    @Test
    public void testSetComparatorToSortController() {
        fail("list sorting/filtering disabled");
//        JXList list = new JXList(listModel, true);
//        list.setComparator(Collator.getInstance());
//        list.setSortOrder(SortOrder.DESCENDING);
//        SortKey sortKey = SortKey.getFirstSortKeyForColumn(list.getFilters().getSortController().getSortKeys(), 0);
//        assertNotNull(sortKey);
//        assertEquals(list.getComparator(), sortKey.getComparator());
    }
    
    /**
     * testing new sorter api: 
     * getSortOrder(), toggleSortOrder(), resetSortOrder().
     *
     */
    @Test
    public void testToggleSortOrder() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        assertSame(SortOrder.UNSORTED, list.getSortOrder());
        list.toggleSortOrder();
        assertSame(SortOrder.ASCENDING, list.getSortOrder());
        list.toggleSortOrder();
        assertSame(SortOrder.DESCENDING, list.getSortOrder());
        list.resetSortOrder();
        assertSame(SortOrder.UNSORTED, list.getSortOrder());
    }

    /**
     * prepare sort testing: internal probs with SortController?
     */
    @Test
    public void testSortController() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        assertNotNull("sortController must be initialized", list.getSortController());
    }
    
    /**
     * Issue #232-swingx: selection not kept if selectionModel had been changed.
     *
     */
    @Test
    public void testSelectionMapperUpdatedOnSelectionModelChange() {
        fail("list sorting/filtering disabled");
        JXList table = new JXList(true);
        // created lazily, to see the failure,
        // need to get hold before replacing list's selection
//        SelectionMapper mapper = table.getSelectionMapper();
//        ListSelectionModel model = new DefaultListSelectionModel();
//        table.setSelectionModel(model);
//        assertEquals(model, mapper.getViewSelectionModel());
    }

    /**
     * Issue #232-swingx: selection not kept if selectionModel had been changed.
     *
     *  PENDING: selectionMapper shouldn't be available if list not filterable? 
     */
    @Test
    public void testSelectionMapperFilterDisabled() {
        fail("list sorting/filtering disabled");
//        JXList table = new JXList();
//        // created lazily, need to get hold before replacing list's selection
//        SelectionMapper mapper = table.getSelectionMapper();
//        ListSelectionModel model = new DefaultListSelectionModel();
//        table.setSelectionModel(model);
//        assertEquals(model, mapper.getViewSelectionModel());
    }


    @Test
    public void testConvertToModelPreconditions() {
        fail("list sorting/filtering disabled");
        final JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
//        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
        try {
            list.convertIndexToModel(list.getElementCount());
            fail("accessing list out of range index must throw execption");
        } catch (IndexOutOfBoundsException ex) {
            // this is correct behaviour
        } catch (Exception ex) {
            fail("got " + ex);
        }
        
    }
 

    @Test
    public void testElementAtPreconditions() {
        fail("list sorting/filtering disabled");
        final JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
//        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
        try {
            list.getElementAt(list.getElementCount());
            fail("accessing list out of range index must throw execption");
        } catch (IndexOutOfBoundsException ex) {
            // this is correct behaviour
        } catch (Exception ex) {
            fail("got " + ex);
        }
        
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
        fail("list sorting/filtering disabled");
        final JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        final int modelRow = 0;
        // set a selection 
        list.setSelectedIndex(modelRow);
        ListSelectionListener l = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int viewRow = list.getSelectedIndex(); 
                assertEquals("view index visible", 0, viewRow);
                // JW: the following checks if the reverse conversion succeeds
                list.convertIndexToModel(viewRow);
                
            }
            
        };
        list.getSelectionModel().addListSelectionListener(l);
//        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
    }



    @Test
    public void testEmptyFilter() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel);
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
    }
    
    @Test
    public void testFilterEnabled() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        assertNotSame(ascendingListModel, list.getModel());
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }

    
    @Test
    public void testSortingFilterEnabled() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
//        FilterPipeline pipeline = list.getFilters();
//        assertNotNull(pipeline);
        list.setSortOrder(SortOrder.DESCENDING);
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(list.getElementCount() - 1));
        
    }
    
    @Test
    public void testSortingKeepsModelSelection() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        list.setSelectedIndex(0);
        list.setSortOrder(SortOrder.DESCENDING);
        assertEquals("last row must be selected after sorting", 
                ascendingListModel.getSize() - 1, list.getSelectedIndex());
    }

    /**
     * Issue #2-swinglabs: setting filter if not enabled throws exception on selection.
     * Reported by Kim.
     * 
     * Fix: should not accept filter if not enabled. 
     * PENDING JW: Doesn't? appears to be fixed? Check!  
     *  
     *
     */
    @Test
    public void testFilterDisabled() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList();
        list.setModel(ascendingListModel);
//        Filter[] filter = new Filter[] { new PatternFilter("1", 0, 0) };
//        try {
//            list.setFilters(new FilterPipeline(filter));
//            fail("setFilter must not be called if filters not enabled");
//        } catch (IllegalStateException e) {
//            // do nothing, this is the documented behaviour
//        } catch (Exception e) {
//            fail("unexpected exception type " + e);
//        }
    }

    
    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails after quick fix for #370-swingx. 
     *
     */
    @Test
    public void testSelectionAfterAddAtFirst() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        // selecte second row
        list.setSelectedIndex(0);
        Object oldFirst = list.getElementAt(0);
        Object newFirst = new Integer(-1);
        // add first 
        ascendingListModel.insertElementAt(newFirst, 0);
        // sanity
        assertEquals(newFirst, list.getElementAt(0));
        assertEquals(oldFirst, list.getElementAt(1));
        assertEquals("first row must be selected inserting new first", 
                0, list.getSelectedIndex());
    }

    /**
     * sanity test: compare table with list behaviour (#377-swingx)
     * 
     */
    @Test
    public void testSelectionAfterAddAtFirstCompareTable() {
        fail("list sorting/filtering disabled");
        DefaultTableModel ascendingModel = new DefaultTableModel(20, 2);
        JXTable table = new JXTable(ascendingModel);
        // select second row
        table.setRowSelectionInterval(0, 0);
        // remove first
        ascendingModel.addRow(new Object[]{"", ""});
        assertEquals("second row must be selected after adding new first", 
                0, table.getSelectedRow());
        
    }

    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails after quick fix for #370-swingx. 
     *
     */
    @Test
    public void testSelectionAfterAddAbove() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        // selecte second row
        list.setSelectedIndex(1);
        Object oldFirst = list.getElementAt(1);
        Object newFirst = new Integer(-1);
        // add first 
        ascendingListModel.insertElementAt(newFirst, 0);
        // sanity
        assertEquals(newFirst, list.getElementAt(0));
        assertEquals(oldFirst, list.getElementAt(2));
        assertEquals("second row must be selected inserting new first", 
                2, list.getSelectedIndex());
    }

    /**
     * Issue #223
     * test if selection is updated on add row above selection.
     *
     */
    @Test
    public void testAddRowAboveSelectionInvertedOrder() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        // select the last row in view coordinates
        int selectedRow = list.getElementCount() - 2;
        list.setSelectedIndex(selectedRow);
        // set a pipeline - ascending, no change
        list.toggleSortOrder();
        // revert order 
        list.toggleSortOrder();
        assertEquals("second row must be selected", 1, list.getSelectedIndex());
        // add row in model coordinates
        // insert high value
        Object row = new Integer(100);
        ascendingListModel.addElement(row);
        // selection must be moved one below
        assertEquals("selection must be incremented by one ", 2, list.getSelectedIndex());
        
    }

    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails after quick fix for #370-swingx. 
     *
     */
    @Test
    public void testSelectionAfterDeleteAbove() {
        fail("list sorting/filtering disabled");
        JXList list = new JXList(ascendingListModel, true);
        // selecte second row
        list.setSelectedIndex(1);
        // remove first 
        ascendingListModel.remove(0);
        assertEquals("first row must be selected removing old first", 
                0, list.getSelectedIndex());
        
    }
    /**
     * sanity test: compare table with list behaviour (#370-swingx)
     * 
     */
    @Test
    public void testSelectionAfterDeleteAboveCompareTable() {
        fail("list sorting/filtering disabled");
        DefaultTableModel ascendingModel = new DefaultTableModel(20, 2);
        JXTable table = new JXTable(ascendingModel);
        // select second row
        table.setRowSelectionInterval(1, 1);
        // remove first
        ascendingModel.removeRow(0);
        assertEquals("first row must be selected after removing old first", 
                0, table.getSelectedRow());
        
    }

// ------------ from XIssues, had been failing anyway
    
    /**
     * Issue #855-swingx: throws AIOOB on repeated remove/add.
     * Open question: should selectionMapper guard against invalid
     * selection indices from view selection? Currently it blows. 
     * Probably good because it's most certainly a programming error.
     */
    public void testInvalidViewSelect() {
        fail("list sorting/filtering disabled - but had been failing before");
        DefaultListModel model = new DefaultListModel();
        model.addElement("something");
        JXList list = new JXList(model, true);
        list.setSortOrder(SortOrder.ASCENDING);
        // list guards against invalid index
        list.setSelectedIndex(1);
        // selectionModel can't do anything (has no notion about size)
        // selectionMapper doesn't guard and blows on conversion - should it?
        list.getSelectionModel().setSelectionInterval(1, 1);
    }

    public void testConvertToViewPreconditions() {
        fail("list sorting/filtering disabled - but had been failing before");
        final JXList list = new JXList(ascendingListModel);
        // a side-effect of setFilterEnabled is to clear the selection!
        // this is done in JList.setModel(..) which is called when 
        // changing filterEnabled!
//        list.setFilterEnabled(true);
        assertEquals(20, list.getElementCount());
//        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
        try {
            list.convertIndexToView(ascendingListModel.getSize());
            fail("accessing list out of range index must throw execption");
        } catch (IndexOutOfBoundsException ex) {
            // this is correct behaviour
        } catch (Exception ex) {
            fail("got " + ex);
        }
        
    }

//--------------------- interactive
    

    /**
     * Issue #377-swingx: JXList (it's wrapping model) fires incorrect events.
     * 
     * 
     */
    public void interactiveFilterMutateModel() {
        final DefaultListModel model = createAscendingListModel(0, 5);
        // PENDING: currently not useful, sort disabled
        final JXList list = new JXList(model, true);
//        list.setFilters(new FilterPipeline(new PatternFilter()));
        JXFrame frame = wrapWithScrollingInFrame(list, "Mutate model with filter");
        Action addItem = new AbstractAction("add item") {

            public void actionPerformed(ActionEvent e) {
                int selected = list.getSelectedIndex();
                if (selected >= 0) {
                    selected = list.convertIndexToModel(selected);
                }
                if (selected > 0) {
                    model.add(selected - 1, model.getSize());
                } else {
                    model.addElement(model.getSize());
                }
                
            }
            
        };
        addAction(frame, addItem);
        Action removeItem = new AbstractAction("remove item") {

            public void actionPerformed(ActionEvent e) {
                int selected = list.getSelectedIndex();
                if (selected >= 0) {
                    selected = list.convertIndexToModel(selected);
                }
                if (selected > 0) {
                    model.remove(selected - 1);
                } 
                
            }
            
        };
        addAction(frame, removeItem);
        Action changeItem = new AbstractAction("change item") {

            public void actionPerformed(ActionEvent e) {
                int selected = list.getSelectedIndex();
                if (selected >= 0) {
                    selected = list.convertIndexToModel(selected);
                }
                if (selected > 0) {
                    int newValue = ((Integer) model.getElementAt(selected - 1)).intValue() + 10;
                    model.set(selected - 1, newValue);
                } 
                
            }
            
        };
        addAction(frame, changeItem);
        Action flush = new AbstractAction("toggle sort") {

            public void actionPerformed(ActionEvent e) {
                list.toggleSortOrder();
            }
            
        };
        addAction(frame, flush);
        show(frame);
    }
    

    public void interactiveTestSort() {
        // PENDING: currently not useful, sort disabled
        final JXList list = new JXList(listModel, true);
        JXFrame frame = wrapWithScrollingInFrame(list, "Toggle sorter");
        Action toggleSortOrder = new AbstractAction("Toggle Sort Order") {

            public void actionPerformed(ActionEvent e) {
                list.toggleSortOrder();
                
            }
            
        };
        addAction(frame, toggleSortOrder);
        Action resetSortOrder = new AbstractAction("Reset Sort Order") {

            public void actionPerformed(ActionEvent e) {
                list.resetSortOrder();
                
            }
            
        };
        addAction(frame, resetSortOrder);
        frame.setVisible(true);
        
    }

    
//-------------------- factory methods, setup    
    protected ListModel createListModel() {
        JXList list = new JXList();
        return new DefaultComboBoxModel(list.getActionMap().allKeys());
    }

    protected DefaultListModel createAscendingListModel(int startRow, int count) {
        DefaultListModel l = new DefaultListModel();
        for (int row = startRow; row < startRow  + count; row++) {
            l.addElement(new Integer(row));
        }
        return l;
    }
    protected DefaultListModel createListModelWithLinks() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 20; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a resource", null, url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }

    /**
     * Creates and returns a number filter, passing values which are numbers and
     * have int values inside or outside of the bounds (included), depending on the given 
     * flag.
     * 
     * @param lowerBound
     * @param upperBound
     * @param inside 
     * @return
     */
//    protected Filter createNumberFilter(final int lowerBound, final int upperBound, final boolean inside) {
//        PatternFilter f = new PatternFilter() {
//
//            @Override
//            public boolean test(int row) {
//                Object value = getInputValue(row, getColumnIndex());
//                if (!(value instanceof Number)) return false;
//                boolean isInside = ((Number) value).intValue() >= lowerBound 
//                    && ((Number) value).intValue() <= upperBound;
//                return inside ? isInside : !isInside;
//            }
//            
//        };
//        return f;
//    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        listModel = createListModel();
        ascendingListModel = createAscendingListModel(0, 20);
    }
    public JXListSortRevamp() {
        super("JXList Tests");
    }

    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }

}
