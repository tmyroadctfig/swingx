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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.sort.ListSortController;
import org.jdesktop.swingx.sort.ListSortUI;
import org.jdesktop.swingx.sort.RowFilters;
import org.jdesktop.swingx.sort.TableSortController;
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
    private ListSortController<ListModel> controller;
    private JXList list;
    private ListSortUI sortManager;

    public static void main(String[] args) {
        JXListSortRevamp test = new JXListSortRevamp();
        try {
//            test.runInteractiveTests();
            test.runInteractiveTests("interactive.*RowSorter.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

//------------------ re-enable
    
    public void interactiveRowSorter() {
        final JXList list = new JXList(ascendingListModel);
        final DefaultTableModel tableModel = new DefaultTableModel(list.getElementCount(), 1) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
            
        };
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i, i, 0);
        }
        final JXTable table = new JXTable(tableModel);
        final ListSortController<ListModel> controller = new ListSortController<ListModel>(list.getModel());
        list.setRowSorter(controller);
        controller.setComparator(0, TableSortController.COMPARABLE_COMPARATOR);
        new ListSortUI(list, controller);
        Action sort = new AbstractAction("toggle sort") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.toggleSortOrder(0);
                
            }
        };
        Action reset = new AbstractAction("reset") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.resetSortOrders();
                
            }
        };
        Action toggleFilter = new AbstractAction("toggle filter") {
            boolean hasFilter;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasFilter) {
                RowFilter<Object, Integer> filter = RowFilters.regexFilter("0", 0);
                    list.getSortController().setRowFilter(filter);
                    table.getSortController().setRowFilter(filter);
                } else {
                    list.getSortController().setRowFilter(null);
                    table.getSortController().setRowFilter(null);
                }
                hasFilter = !hasFilter;
            }
        };
        Action removeFirst = new AbstractAction("remove firstM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                ascendingListModel.remove(0);
                tableModel.removeRow(0);
                
            }
        };
        JXFrame frame = showWithScrollingInFrame(list, table, "sort in rowSorter");
        addAction(frame, sort);
        addAction(frame, reset);
        addAction(frame, toggleFilter);
        addAction(frame, removeFirst);
        show(frame);
    }

 
    //----------------- re-enabled functionality
    /**
     * Issue #477-swingx:
     * 
     * Selection must be cleared after setModel. This is from
     * super's contract.
     * 
     * Not yet ready: must update ListSortUI after model update. 
     *
     */
    @Test
    public void testSetModelEmptySelection() {
        fail("list sorting/filtering not yet completely enabled");
        final JXList list = new JXList(listModel, true);
        int selection = 0;
        list.setSelectedIndex(selection);
        list.setModel(ascendingListModel);
        assertTrue("setting model must clear selectioon", list.isSelectionEmpty());
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
    }
    
    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails because the ui-delegate has its hands in removing
     * selection after removed, that is they are doubly removed.
     *
     */
    @Test
    public void testSelectionAfterAddAbove() {
        // selecte second row
        list.setSelectedIndex(1);
        // remove first 
        ascendingListModel.insertElementAt(5, 0);
        assertEquals("selected must have moved after adding at start", 
                2, list.getSelectedIndex());
    }
    
    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails because the ui-delegate has its hands in removing
     * selection after removed, that is they are doubly removed.
     *
     */
    @Test
    public void testSelectionAfterDeleteAbove() {
        // selecte second row
        list.setSelectedIndex(1);
        // remove first 
        ascendingListModel.remove(0);
        assertEquals("first row must be selected removing old first", 
                0, list.getSelectedIndex());
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
        list = new JXList(ascendingListModel);
        controller = new ListSortController<ListModel>(list.getModel());
        controller.setComparator(0, TableSortController.COMPARABLE_COMPARATOR);
        list.setRowSorter(controller);
        sortManager = new ListSortUI(list, controller);

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
