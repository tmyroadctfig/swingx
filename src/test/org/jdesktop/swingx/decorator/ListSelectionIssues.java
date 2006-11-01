/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.decorator;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import org.jdesktop.test.ListSelectionReport;

import junit.framework.TestCase;

/**
 * Test to understand behaviour/expose known issues of 
 * <code>ListSelectionModel</code>.
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class ListSelectionIssues extends TestCase {
    /**
     * sanity: understand DefaultListSelectionModel behaviour.
     * 
     * Is it allowed that event.getFirstIndex < 0? This happens in 
     * table.clearLeadAnchor
     *
     */
    public void testEventsONLeadAnchorAfterClearSelection() {
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        int selected = 5;
        selectionModel.setSelectionInterval(selected, selected);
        assertEquals(selected, selectionModel.getAnchorSelectionIndex());
        assertEquals(selected, selectionModel.getLeadSelectionIndex());
//        selectionModel.setLeadAnchorNotificationEnabled(false);
        ListSelectionReport report = new ListSelectionReport();
        selectionModel.addListSelectionListener(report);
        // following lines are copied from table.clearLeadAnchor()
//        selectionModel.setValueIsAdjusting(true);
        selectionModel.clearSelection();
        assertEquals(1, report.getEventCount());
        assertTrue(report.getLastEvent(false).getFirstIndex() >= 0);
        report.clear();
        selectionModel.setAnchorSelectionIndex(-1);
        assertEquals(1, report.getEventCount());
        assertTrue(report.getLastEvent(false).getFirstIndex() >= 0);
        report.clear();
        
        selectionModel.setLeadSelectionIndex(-1);
        assertEquals(1, report.getEventCount());
        assertTrue(report.getLastEvent(false).getFirstIndex() >= 0);
        report.clear();
    }


    /**
     * sanity: understand DefaultListSelectionModel behaviour.
     * 
     * Is it allowed that event.getFirstIndex < 0? This happens in 
     * table.clearLeadAnchor
     *
     */
    public void testEventONLeadAnchorAfterClearSelection() {
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        int selected = 5;
        selectionModel.setSelectionInterval(selected, selected);
        assertEquals(selected, selectionModel.getAnchorSelectionIndex());
        assertEquals(selected, selectionModel.getLeadSelectionIndex());
        selectionModel.setLeadAnchorNotificationEnabled(false);
        ListSelectionReport report = new ListSelectionReport();
        selectionModel.addListSelectionListener(report);
        // following lines are copied from table.clearLeadAnchor()
        selectionModel.setValueIsAdjusting(true);
        selectionModel.clearSelection();
        selectionModel.setAnchorSelectionIndex(-1);
        selectionModel.setLeadSelectionIndex(-1);
        assertEquals("", 0, report.getEventCount(true));
        selectionModel.setValueIsAdjusting(false);
        ListSelectionEvent event = report.getLastEvent(true);  
        assertEquals(5, event.getFirstIndex());
    }

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

}
