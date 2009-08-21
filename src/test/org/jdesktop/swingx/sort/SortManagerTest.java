/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx.sort;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXEditorPaneTest;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.sort.SortManager.ModelChange;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for SortManager (mainly to understand).
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class SortManagerTest extends InteractiveTestCase {
    protected DefaultListModel ascendingListModel;
    private JXList list;
    private ListSortController<ListModel> controller;
    private SortManager sortManager;
    private int testRow;

    
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
        RowFilter<ListModel, Integer> filter = RowFilters.regexFilter("0", 0);
        ((DefaultRowSorter<ListModel, Integer>) list.getRowSorter()).setRowFilter(filter);
        assertEquals(2, list.getElementCount());
    }


    @Test
    public void testSelectionAfterSort() {
        // use the 2 to be sure the comparable is used
        list.setSelectedIndex(testRow);
        list.setSortOrder(SortOrder.DESCENDING);
        assertEquals("last row must be selected after sorting", 
                ascendingListModel.getSize() - (testRow + 1) , list.getSelectedIndex());
    }
    
    @Test
    public void testPrepareNull() {
        
    }
//------------------- ModelChange - temporary ...    
    @Test
    public void testModelAdded() {
        int first = 3;
        int last = 5;
        ListDataEvent e = new ListDataEvent(new DefaultListModel(), ListDataEvent.INTERVAL_ADDED, last, first);
        ModelChange change = new ModelChange(e);
        assertEquals(ListDataEvent.INTERVAL_ADDED, change.type);
        assertFalse(change.allRowsChanged);
        assertEquals(last-first +1, change.length);
        assertEquals(first, change.startModelIndex);
        assertEquals(last, change.endModelIndex);
    }
    @Test
    public void testModelRemoved() {
        int first = 3;
        int last = 5;
        ListDataEvent e = new ListDataEvent(new DefaultListModel(), ListDataEvent.INTERVAL_REMOVED, last, first);
        ModelChange change = new ModelChange(e);
        assertEquals(ListDataEvent.INTERVAL_REMOVED, change.type);
        assertFalse(change.allRowsChanged);
        assertEquals(last-first +1, change.length);
        assertEquals(first, change.startModelIndex);
        assertEquals(last, change.endModelIndex);
    }
    @Test
    public void testModelChanged() {
        int first = 3;
        int last = 5;
        ListDataEvent e = new ListDataEvent(new DefaultListModel(), ListDataEvent.CONTENTS_CHANGED, last, first);
        ModelChange change = new ModelChange(e);
        assertEquals(ListDataEvent.CONTENTS_CHANGED, change.type);
        assertFalse(change.allRowsChanged);
        assertEquals(last-first +1, change.length);
        assertEquals(first, change.startModelIndex);
        assertEquals(last, change.endModelIndex);
    }
    @Test
    public void testModelAllChanged() {
        int first = -1;
        int last = -1;
        ListDataEvent e = new ListDataEvent(new DefaultListModel(), ListDataEvent.CONTENTS_CHANGED, last, first);
        ModelChange change = new ModelChange(e);
        assertEquals(ListDataEvent.CONTENTS_CHANGED, change.type);
        assertTrue(change.allRowsChanged);
        assertEquals(last-first +1, change.length);
        assertEquals(0, change.startModelIndex);
        assertEquals(0, change.endModelIndex);
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

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ascendingListModel = createAscendingListModel(0, 20);
        list = new JXList(ascendingListModel);
        controller = new ListSortController<ListModel>(list.getModel());
        controller.setComparator(0, TableSortController.COMPARABLE_COMPARATOR);
        list.setRowSorter(controller);
        sortManager = new SortManager(controller, list);
        testRow = 2;
    }
    
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        sortManager.dispose();
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
