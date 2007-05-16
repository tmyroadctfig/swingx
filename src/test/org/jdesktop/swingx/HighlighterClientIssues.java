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
package org.jdesktop.swingx;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.test.PropertyChangeReport;

/**
 * Test to exposed known issues of <code>Highlighter</code> client
 * api in collection components.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterClientIssues extends InteractiveTestCase {

//------------------------ notification
    
    /**
     * PENDING: highlighters is property?
     */
    public void testSetHighlightersChangeEvent() {
        fail("missing test for change notification on setHighlighters");
        JXTable table = new JXTable();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Highlighter highlighter = new ColorHighlighter();
        table.setHighlighters(highlighter);
        assertTrue("table must have fired propertyChange for highlighters", report.hasEvents("highlighters"));
    }
    
    /**
     * PENDING: if/what to fire on add/remove
     */
    public void testAddHighlightersChangeEvent() {
        fail("missing test for change notification on add Highlighters");
        JXTable table = new JXTable();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Highlighter highlighter = new ColorHighlighter();
        table.addHighlighter(highlighter);
        assertTrue("table must have fired propertyChange for highlighters", report.hasEvents("highlighters"));
    }
    
    /**
     * PENDING: if/what to fire on add/remove
     */
    public void testRemoveHighlightersChangeEvent() {
        fail("missing test for change notification on remove Highlighters");
        JXTable table = new JXTable();
        Highlighter highlighter = new ColorHighlighter();
        table.setHighlighters(highlighter);
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.removeHighlighter(highlighter);
        assertTrue("table must have fired propertyChange for highlighters", report.hasEvents("highlighters"));
    }
    
//-------------------------- null and setHighlighters    
    /**
     * PENDING: Define how to handle setHighlighters(null).
     * Strictly: 
     */
    public void testSetHighlightersNull() {
        JXTable table = new JXTable();
        table.setHighlighters((Highlighter) null);
        assertEquals(0, table.getHighlighters().length);
    }

    /**
     * PENDING: Define how to handle setHighlighters(null).
     */
    public void testSetHighlightersWithCompoundNull() {
        JXTable table = new JXTable();
        table.setHighlighters((CompoundHighlighter) null);
        assertEquals(0, table.getHighlighters().length);
    }

    /**
     * PENDING: Define how to handle setHighlighters(null).
     */
    public void testSetHighlightersWithNullArray() {
        JXTable table = new JXTable();
        table.setHighlighters((Highlighter[]) null);
        assertEquals(0, table.getHighlighters().length);
    }

    /**
     * PENDING: Define how to handle setHighlighters(null).
     */
    public void testSetHighlightersEmptyArray() {
        JXTable table = new JXTable();
        table.setHighlighters(new Highlighter[] {});
        assertEquals(0, table.getHighlighters().length);
    }
    
    /**
     * PENDING: Define how to handle setHighlighters(null).
     */
    public void testSetHighlightersArrayNullElement() {
        JXTable table = new JXTable();
        table.setHighlighters(new Highlighter[] {null});
        assertEquals(0, table.getHighlighters().length);
    }
}
