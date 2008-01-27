/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.calendar;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;

/**
 * Test DaySelectionModel.
 * 
 * @author Jeanette Winzenburg
 */
public class DaySelectionModelTest extends AbstractTestDateSelectionModel {

    /**
     * test the contract as doc'ed 
     */
    public void testNormalizedDateContract() {
        model.setSelectionInterval(today, today);
        assertEquals(model.getNormalizedDate(today), model.getSelection().first());
    }
    
    /**
     * Normalized must throw NPE if given date is null
     */
    public void testNormalizedDateNull() {
        try {
            model.getNormalizedDate(null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
    }

    /**
     * DaySelectionModel normalizes to start of day.
     */
    public void testNormalizedDateCloned() {
        assertEquals(startOfDay(today), model.getNormalizedDate(today));
        assertNotSame(startOfDay(today), model.getNormalizedDate(today));
    }


    /**
     * setSelectionInterval must throw NPE if given date is null
     */
    public void testSetIntervalNulls() {
        try {
            model.setSelectionInterval(null, null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
        
    }
    /**
     * setSelectionInterval must throw NPE if given date is null
     */
    public void testAddIntervalNulls() {
        try {
            model.addSelectionInterval(null, null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
        
    }
    
    /**
     * removeSelectionInterval must throw NPE if given date is null
     */
    public void testRemoveIntervalNulls() {
        try {
            model.removeSelectionInterval(null, null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
        
    }

    /**
     * expose more selection constraint methods in JXMonthView
     *
     */
    public void testUpperBound() {
        model.setUpperBound(today);
        assertEquals(startOfDay(today), model.getUpperBound());
        // remove again
        model.setUpperBound(null);
        assertEquals(null, model.getUpperBound());
    }
    
    /**
     * expose more selection constraint methods in JXMonthView
     *
     */
    public void testLowerBound() {
        model.setLowerBound(today);
        assertEquals(startOfDay(today), model.getLowerBound());
        // remove again
        model.setLowerBound(null);
        assertEquals(null, model.getLowerBound());
    }

    /**
     * test unselectable: use methods with Date.
     *
     */
    public void testUnselectableDate() {
        // initial
        assertFalse(model.isUnselectableDate(today));
        // set unselectable today
        SortedSet<Date> unselectables = new TreeSet<Date>();
        unselectables.add(today);
        model.setUnselectableDates(unselectables);
        assertTrue("raqw today must be unselectable", 
                model.isUnselectableDate(today));
        assertTrue("start of today must be unselectable", 
                model.isUnselectableDate(startOfDay(today)));
        assertTrue("end of today must be unselectable", 
                model.isUnselectableDate(endOfDay(today)));
        model.setUnselectableDates(new TreeSet<Date>());
        assertFalse(model.isUnselectableDate(today));
        assertFalse(model.isUnselectableDate(startOfDay(today)));
        assertFalse(model.isUnselectableDate(endOfDay(today)));
    }

    /**
     * Issue #494-swingx: JXMonthView changed all passed-in dates
     *
     */
    public void testCleanupCopyDate() {
        Date copy = new Date(today.getTime());
        model.setSelectionInterval(today, today);
        assertEquals("the date used for selection must be unchanged", copy, today);
    }
   
    public void testEmptySelectionInitial() {
        assertTrue(model.isSelectionEmpty());
        SortedSet<Date> selection = model.getSelection();
        assertTrue(selection.isEmpty());
    }
    
    public void testEmptySelectionClear() {
        model.setSelectionInterval(today, today);
        // sanity
        assertTrue(1 == model.getSelection().size());

        model.clearSelection();
        assertTrue(model.isSelectionEmpty());
        assertTrue(model.getSelection().isEmpty());
    }

    public void testSingleSelection() {
        model.setSelectionMode(SelectionMode.SINGLE_SELECTION);

        model.setSelectionInterval(today, today);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(1 == selection.size());
        assertEquals(startOfDay(today), selection.first());

        model.setSelectionInterval(today, afterTomorrow);
        selection = model.getSelection();
        assertTrue(1 == selection.size());
        assertEquals(startOfDay(today), selection.first());
    }
    
    public void testSingleIntervalSelection() {
        model.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);

        model.setSelectionInterval(today, today);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(1 == selection.size());
        assertEquals(startOfDay(today), selection.first());

        model.setSelectionInterval(today, tomorrow);
        
        selection = model.getSelection();
        assertEquals(2, selection.size());
        assertEquals(startOfDay(today), selection.first());
        assertEquals(startOfDay(tomorrow), selection.last());
    }

    public void testWeekIntervalSelection() {
        //TODO...
    }

    public void testMultipleIntervalSelection() {
        model.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);

        model.setSelectionInterval(yesterday, yesterday);
        model.addSelectionInterval(afterTomorrow, afterTomorrow);
        
        SortedSet<Date> selection = model.getSelection();
        assertEquals(2, selection.size());
        assertEquals(startOfDay(yesterday), selection.first());
        assertEquals(startOfDay(afterTomorrow), selection.last());
    }

    @Override
    protected void setUp() throws Exception {
        setUpCalendar();
        model = new DaySelectionModel();
    }

    
}
