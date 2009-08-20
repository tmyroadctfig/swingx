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

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.sort.SortManager.ModelChange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class SortManagerTest extends InteractiveTestCase {

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
}
