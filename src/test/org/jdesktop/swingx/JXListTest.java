/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.SelectionMapper;
import org.jdesktop.swingx.decorator.SortKey;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.test.util.ListDataReport;
import org.jdesktop.test.util.PropertyChangeReport;

/**
 * Testing JXList.
 * 
 * @author Jeanette Winzenburg
 */
public class JXListTest extends InteractiveTestCase {

    protected ListModel listModel;
    protected DefaultListModel ascendingListModel;

    /**
     * test exceptions on null data(model, vector, array).
     *
     */
    public void testNullData() {
        try {
            new JXList((ListModel) null);
            fail("JXList contructor must throw on null data");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (Exception e) {
            fail("unexpected exception type " + e);
        }
        
        try {
           new JXList((Vector) null);
            fail("JXList contructor must throw on null data");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (Exception e) {
            fail("unexpected exception type " + e);
        }
        
        try {
            new JXList((Object[]) null);
             fail("JXList contructor must throw on null data");
         } catch (IllegalArgumentException e) {
             // expected
         } catch (Exception e) {
             fail("unexpected exception type " + e);
         }
    }
    

    /**
     * test filterEnabled property on initialization.
     *
     */
    public void testConstructorFilterEnabled() {
        // 
        assertFilterEnabled(new JXList(), false);
        assertFilterEnabled(new JXList(new DefaultListModel()), false);
        assertFilterEnabled(new JXList(new Vector()), false);
        assertFilterEnabled(new JXList(new Object[] { }), false);
        
        assertFilterEnabled(new JXList(false), false);
        assertFilterEnabled(new JXList(new DefaultListModel(), false), false);
        assertFilterEnabled(new JXList(new Vector(), false), false);
        assertFilterEnabled(new JXList(new Object[] { }, false), false);

        assertFilterEnabled(new JXList(true), true);
        assertFilterEnabled(new JXList(new DefaultListModel(), true), true);
        assertFilterEnabled(new JXList(new Vector(), true), true);
        assertFilterEnabled(new JXList(new Object[] { }, true), true);
    }
    
    private void assertFilterEnabled(JXList list, boolean b) {
        assertEquals(b, list.isFilterEnabled());
    }

    /**
     * added xtable.setSortOrder(int, SortOrder)
     * 
     */
    public void testSetSortOrder() {
        JXList list = new JXList(ascendingListModel, true);
        list.setSortOrder(SortOrder.ASCENDING);
        assertEquals("column must be sorted after setting sortOrder on ", SortOrder.ASCENDING, list.getSortOrder());
    }
    
    /**
     * JXList has responsibility to guarantee usage of 
     * its comparator: setComparator if already sorted.
     */
    public void testDynamicComparatorToSortController() {
        JXList list = new JXList(listModel, true);
        list.toggleSortOrder();
        list.setComparator(Collator.getInstance());
        SortKey sortKey = SortKey.getFirstSortKeyForColumn(list.getFilters().getSortController().getSortKeys(), 0);
        assertNotNull(sortKey);
        assertEquals(list.getComparator(), sortKey.getComparator());
    }

    /**
     * JXList has responsibility to guarantee usage of 
     * its comparator: toggle.
     */
    public void testToggleComparatorToSortController() {
        JXList list = new JXList(listModel, true);
        list.setComparator(Collator.getInstance());
        list.toggleSortOrder();
        SortKey sortKey = SortKey.getFirstSortKeyForColumn(list.getFilters().getSortController().getSortKeys(), 0);
        assertNotNull(sortKey);
        assertEquals(list.getComparator(), sortKey.getComparator());
    }

    /**
     * JXList has responsibility to guarantee usage of 
     * its comparator: set.
     */
    public void testSetComparatorToSortController() {
        JXList list = new JXList(listModel, true);
        list.setComparator(Collator.getInstance());
        list.setSortOrder(SortOrder.DESCENDING);
        SortKey sortKey = SortKey.getFirstSortKeyForColumn(list.getFilters().getSortController().getSortKeys(), 0);
        assertNotNull(sortKey);
        assertEquals(list.getComparator(), sortKey.getComparator());
    }
    
    /**
     * add and test comparator property.
     * 
     */
    public void testComparator() {
        JXList list = new JXList();
        assertNull(list.getComparator());
        Collator comparator = Collator.getInstance();
        PropertyChangeReport report = new PropertyChangeReport();
        list.addPropertyChangeListener(report);
        list.setComparator(comparator);
        assertEquals(comparator, list.getComparator());
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("comparator"));
        
    }
    /**
     * testing new sorter api: 
     * getSortOrder(), toggleSortOrder(), resetSortOrder().
     *
     */
    public void testToggleSortOrder() {
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
    public void testSortController() {
        JXList list = new JXList(ascendingListModel, true);
        assertNotNull("sortController must be initialized", list.getSortController());
    }
    
    /**
     * Issue #232-swingx: selection not kept if selectionModel had been changed.
     *
     */
    public void testSelectionMapperUpdatedOnSelectionModelChange() {
        JXList table = new JXList(true);
        // created lazily, to see the failure,
        // need to get hold before replacing list's selection
        SelectionMapper mapper = table.getSelectionMapper();
        ListSelectionModel model = new DefaultListSelectionModel();
        table.setSelectionModel(model);
        assertEquals(model, mapper.getViewSelectionModel());
    }

    /**
     * Issue #232-swingx: selection not kept if selectionModel had been changed.
     *
     *  PENDING: selectionMapper shouldn't be available if list not filterable? 
     */
    public void testSelectionMapperFilterDisabled() {
        JXList table = new JXList();
        // created lazily, need to get hold before replacing list's selection
        SelectionMapper mapper = table.getSelectionMapper();
        ListSelectionModel model = new DefaultListSelectionModel();
        table.setSelectionModel(model);
        assertEquals(model, mapper.getViewSelectionModel());
    }

    /**
     * test if LinkController/executeButtonAction is properly registered/unregistered on
     * setRolloverEnabled.
     *
     */
    public void testLinkControllerListening() {
        JXList table = new JXList();
        table.setRolloverEnabled(true);
        assertNotNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.CLICKED_KEY));
        assertNotNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.ROLLOVER_KEY));
        assertNotNull("execute button action must be registered", table.getActionMap().get(JXList.EXECUTE_BUTTON_ACTIONCOMMAND));
        table.setRolloverEnabled(false);
        assertNull("LinkController must not be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.CLICKED_KEY ));
        assertNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.ROLLOVER_KEY));
        assertNull("execute button action must be de-registered", table.getActionMap().get(JXList.EXECUTE_BUTTON_ACTIONCOMMAND));
    }

    private PropertyChangeListener getLinkControllerAsPropertyChangeListener(JXList table, String propertyName) {
        PropertyChangeListener[] listeners = table.getPropertyChangeListeners(propertyName);
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof JXList.ListRolloverController) {
                return (JXList.ListRolloverController) listeners[i];
            }
        }
        return null;
    }

    public void testConvertToModelPreconditions() {
        final JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
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
 

    public void testElementAtPreconditions() {
        final JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
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
    public void testSelectionListenerNotification() {
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
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
    }


    /**
     * setFilterEnabled throws NPE if formerly had selection.
     * 
     *
     */
    public void testSetFilterEnabledWithSelection() {
        final JXList list = new JXList(ascendingListModel);
        assertEquals(20, list.getElementCount());
        final int modelRow = 0;
        // set a selection 
        list.setSelectedIndex(modelRow);
        list.setFilterEnabled(true);
        
    }

    public void testEmptyFilter() {
        JXList list = new JXList(ascendingListModel);
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
    }
    
    public void testFilterEnabled() {
        JXList list = new JXList(ascendingListModel, true);
        assertNotSame(ascendingListModel, list.getModel());
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }

    /**
     * Emergency break for #2-swinglabs: 
     * it's not allowed to reset filterEnabled property to false again.
     *
     */
    public void testFilterEnabledAndDisabled() {
        JXList list = new JXList(ascendingListModel, true);
        try {
            list.setFilterEnabled(false);
            fail("must not reset the filterEnabled property");
        } catch (IllegalStateException e) {
            // do nothing this is the exception we expect. 
        } catch (Exception e) { 
            fail("unexpected exception type" + e);
        }
//        assertSame(ascendingListModel, list.getModel());
//        assertEquals(ascendingListModel.getSize(), list.getElementCount());
//        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }
    
    public void testSortingFilterEnabled() {
        JXList list = new JXList(ascendingListModel, true);
        FilterPipeline pipeline = list.getFilters();
        assertNotNull(pipeline);
        list.setSortOrder(SortOrder.DESCENDING);
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(list.getElementCount() - 1));
        
    }
    
    public void testSortingKeepsModelSelection() {
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
    public void testFilterDisabled() {
        JXList list = new JXList();
        list.setModel(ascendingListModel);
        Filter[] filter = new Filter[] { new PatternFilter("1", 0, 0) };
        try {
            list.setFilters(new FilterPipeline(filter));
            fail("setFilter must not be called if filters not enabled");
        } catch (IllegalStateException e) {
            // do nothing, this is the documented behaviour
        } catch (Exception e) {
            fail("unexpected exception type " + e);
        }
    }

    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails after quick fix for #370-swingx. 
     *
     */
    public void testSelectionAfterDeleteAbove() {
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
    public void testSelectionAfterDeleteAboveCompareTable() {
        DefaultTableModel ascendingModel = new DefaultTableModel(20, 2);
        JXTable table = new JXTable(ascendingModel);
        // select second row
        table.setRowSelectionInterval(1, 1);
        // remove first
        ascendingModel.removeRow(0);
        assertEquals("first row must be selected after removing old first", 
                0, table.getSelectedRow());
        
    }

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

    protected void setUp() throws Exception {
        super.setUp();
        listModel = createListModel();
        ascendingListModel = createAscendingListModel(0, 20);
    }
    public JXListTest() {
        super("JXList Tests");
    }

    
}
