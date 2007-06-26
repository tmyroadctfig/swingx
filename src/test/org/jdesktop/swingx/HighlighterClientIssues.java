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

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

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
    public static void main(String[] args) throws Exception {
//      setSystemLF(true);
      HighlighterClientIssues test = new HighlighterClientIssues();
      try {
          test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Table.*");
//          test.runInteractiveTests("interactive.*List.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
  }

    /**
     * Highlighters in JXTable must be kept on moving 
     * the table to different container. This is a sanity test -
     * failed in early stages of fixing #519-swing: memory leak
     * with shared highlighters.
     *
     */
    public void interactiveMemoryLeak() {
        final ColorHighlighter shared = new ColorHighlighter(Color.RED, null);
        JXTable first = new JXTable(10, 3);
        first.addHighlighter(shared);
        final JXTable second = new JXTable(10, 2);
        second.setName("second");
        second.addHighlighter(shared);
        JXFrame firstFrame = wrapWithScrollingInFrame(first, "control");
        
        final JXFrame secondFrame = wrapWithScrollingInFrame(second, "dependent, don't close directly");
        Action close = new AbstractAction("close second") {

            public void actionPerformed(ActionEvent e) {
                secondFrame.dispose();
                setEnabled(false);
            }
            
        };
        Action open = new AbstractAction("open second") {

            public void actionPerformed(ActionEvent e) {
                JXFrame newFrame = wrapWithScrollingInFrame(second, "newly created");
                newFrame.setVisible(true);
            }
            
        };
        Action color = new AbstractAction("toggle color") {

            public void actionPerformed(ActionEvent e) {
                shared.setBackground(Color.YELLOW);
                
            }
            
        };
        addAction(firstFrame, close);
        addAction(firstFrame, open);
        addAction(firstFrame, color);
        firstFrame.setVisible(true);
        secondFrame.setVisible(true);
    }
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
