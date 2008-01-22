/**
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
 */
package org.jdesktop.swingx.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.test.DateSelectionReport;

/**
 * Tests for the DefaultDateSelectionModel.
 * 
 * Moved from swingx to calendar package as of version 1.15
 */
public class SingleDaySelectionModelTest extends TestCase {
    private static final Logger LOG = Logger
            .getLogger(SingleDaySelectionModelTest.class.getName());
    private DateSelectionModel model;
    private Calendar calendar;

    public void testNormalizedDateCloned() {
        Date date = calendar.getTime();
        Date normalized = model.getNormalizedDate(date);
        assertEquals(date, normalized);
        assertNotSame(date, normalized);
    }
    /**
     * Always single selection by definition of SingleDateSelectionModel.
     */
    public void testSelectionMode() {
        // initial
        assertEquals(SelectionMode.SINGLE_SELECTION, model.getSelectionMode());
        for (SelectionMode mode : SelectionMode.values()) {
            model.setSelectionMode(mode);
            assertEquals(SelectionMode.SINGLE_SELECTION, model.getSelectionMode());
        }
    }

    /**
     * 
     */
    public void testIsSelectedAllInSameDay() {
       model.setSelectionInterval(today, today);
       // sanity: today itself
       assertTrue(model.isSelected(today));
       assertTrue("every date contained in today must be regarded as selected - startofday", 
               model.isSelected(startOfDay(today)));
       assertTrue("every date contained in today must be regarded as selected - endofday", 
               model.isSelected(endOfDay(today)));
    }

    
   /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test setSelection with single mode
     */
    public void testSelectionSetNotFireIfSameSingle() {
        final Date date = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
        model.setSelectionInterval(date, date);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(date, date);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }

    /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test addSelection with single mode
     */
    public void testSelectionAddNotFireIfSameSingle() {
        final Date date = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
        model.setSelectionInterval(date, date);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.addSelectionInterval(date, date);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }

 
    /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test setSelection with single interval mode
     */
    public void testSelectionSetNotFireIfSameSingleInterval() {
        final Date date = calendar.getTime();
        calendar.add(Calendar.DATE, 2);
        final Date end = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION);
        model.setSelectionInterval(date, end);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(date, end);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }

    /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test addSelection with single interval mode
     */
    public void testSelectionAddNotFireIfSameSingleInterval() {
        final Date date = calendar.getTime();
        calendar.add(Calendar.DATE, 2);
        final Date end = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION);
        model.setSelectionInterval(date, end);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.addSelectionInterval(date, end);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }

    /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test setSelection with multiple interval mode
     */
    public void testSelectionSetNotFireIfSameMultipleInterval() {
        final Date date = calendar.getTime();
        calendar.add(Calendar.DATE, 2);
        final Date end = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        model.setSelectionInterval(date, end);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(date, end);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }

    /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test addSelection with multiple interval mode
     */
    public void testSelectionAddNotFireIfSameMultipeInterval() {
        final Date date = calendar.getTime();
        calendar.add(Calendar.DATE, 2);
        final Date end = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        model.setSelectionInterval(date, end);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.addSelectionInterval(date, end);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }
    /**
     * related to #625-swingx: DateSelectionModel must not fire on clearing empty selection.
     */
    public void testDateSelectionClearSelectionNotFireIfUnselected() {
        // sanity
        assertTrue(model.isSelectionEmpty());
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.clearSelection();
        assertEquals("selection must not fire on clearing empty selection",
                0,
                report.getEventCount());
    }
    
    /**
     * related to #625-swingx: DateSelectionModel must fire SELECTION_CLEARED if 
     * had selection.
     * Testing here for sanity reasons ... be sure we didn't prevent the firing
     * altogether while changing.
     */
    public void testDateSelectionClearSelectionFireIfSelected() {
        Date date = new Date();
        model.setSelectionInterval(date, date);
        // sanity
        assertFalse(model.isSelectionEmpty());
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.clearSelection();
        assertEquals("selection must fire on clearing selection",
                1,
                report.getEventCount());
        assertEquals("event type must be SELECTION_CLEARED",
                DateSelectionEvent.EventType.SELECTION_CLEARED,
                report.getLastEventType());
    }
    
    /**
     * related to #625-swingx: DateSelectionModel must not fire on clearing empty selection.
     */
    public void testDateSelectionSetSelectionNotFireIfSelected() {
        Date date = new Date();
        model.setSelectionInterval(date, date);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(date, date);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }
    
    /**
     * related to #625-swingx: DateSelectionModel must fire SELECTION_CLEARED if 
     * had selection.
     * Testing here for sanity reasons ... be sure we didn't prevent the firing
     * altogether while changing.
     */
    public void testDateSelectionSetSelectionFire() {
        model.setSelectionInterval(today, today);
        // sanity
        assertTrue(model.isSelected(today));
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(tomorrow, tomorrow);
        assertEquals("selection must fire on selection",
                1,
                report.getEventCount());
        assertEquals("event type must be DATES_SET",
                DateSelectionEvent.EventType.DATES_SET,
                report.getLastEventType());
    }
    


    /**
     * adding api: adjusting
     *
     */
    public void testEventsCarryAdjustingFlagTrue() {
        Date date = calendar.getTime();
        model.setAdjusting(true);
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(date, date);
        assertEquals(model.isAdjusting(), report.getLastEvent().isAdjusting());
        // sanity: revert 
        model.setAdjusting(false);
        report.clear();
        model.removeSelectionInterval(date, date);
        assertEquals(model.isAdjusting(), report.getLastEvent().isAdjusting());
        
    }

    /**
     * adding api: adjusting
     *
     */
    public void testEventsCarryAdjustingFlagFalse() {
        Date date = calendar.getTime();
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(date, date);
        assertEquals(model.isAdjusting(), report.getLastEvent().isAdjusting());
    }
    
    /**
     * adding api: adjusting.
     *
     */
    public void testAdjusting() {
        // default value
        assertFalse(model.isAdjusting());
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        // set adjusting
        model.setAdjusting(true);
        assertTrue("model must be adjusting", model.isAdjusting());
        assertEquals(1, report.getEventCount());
        assertEquals(DateSelectionEvent.EventType.ADJUSTING_STARTED, 
                report.getLastEventType());
        // next round - reset to default adjusting
        report.clear();
        model.setAdjusting(false);
        assertFalse("model must not be adjusting", model.isAdjusting());
        assertEquals(1, report.getEventCount());
        assertEquals(DateSelectionEvent.EventType.ADJUSTING_STOPPED, 
                report.getLastEventType());
        
    }
    

    /**
     * respect both bounds - same day: single selection allowed.
     *
     */
    public void testBothBoundsSame() {
        model.setLowerBound(endOfDay(today));
        model.setUpperBound(startOfDay(today));
        model.setSelectionInterval(today, today);
        assertEquals("selected bounds", today, 
                model.getSelection().first());
    }

    /**
     * respect both bounds - overlapping: no selection.
     *
     */
    public void testBothBoundsOverlap() {
        model.setLowerBound(startOfDay(today));
        model.setUpperBound(endOfDay(yesterday));
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(today, today);
        assertEquals("selection must be empty", 0, model.getSelection().size());
        assertEquals("no event fired", 0, report.getEventCount());
    }
    
   /**
     *  respect lower bound - the day before is
     *  an invalid selection.
     *
     */
    public void testLowerBoundPast() {
        model.setLowerBound(today);
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(yesterday, yesterday);
        assertEquals("selection must be empty", 0, model.getSelection().size());
        assertEquals("no event fired", 0, report.getEventCount());
    }
    
    /**
     *  respect lower bound - the bound itself 
     *  a valid selection.
     *
     */
    public void testLowerBound() {
        model.setLowerBound(today);
        // the bound itself is allowed
        model.setSelectionInterval(today, today);
        assertEquals("selected upper bound", model.getLowerBound(), 
                model.getSelection().first());
    }
    
    /**
     *  respect lower bound - the bound itself 
     *  a valid selection.
     *
     */
    public void testLowerBoundSameDay() {
        model.setLowerBound(endOfDay(today));
        // the bound itself is allowed
        model.setSelectionInterval(today, today);
        assertTrue("same day as lower bound is allowed", model.isSelected(today));
    }
    /**
     *  respect upper bound - the day after is
     *  an invalid selection.
     *
     */
    public void testUpperBoundFuture() {
        model.setUpperBound(today);
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(tomorrow, tomorrow);
        assertTrue("selection must be empty", model.isSelectionEmpty());
        assertEquals("no event fired", 0, report.getEventCount());
    }
 
    /**
     * Remove the upper bound constraint
     */
    public void testUpperBoundRemove() {
        model.setUpperBound(today);
        model.setUpperBound(null);
        model.setSelectionInterval(tomorrow, tomorrow);
        assertTrue("tomorrow must be selected after removing upper bound ", 
                model.isSelected(tomorrow));
    }

    /**
     *  respect upper bound - the bound itself 
     *  a valid selection.
     *
     */
    public void testUpperBound() {
        model.setUpperBound(today);
        // the bound itself is allowed
        model.setSelectionInterval(today, today);
        assertEquals("selected upper bound", model.getUpperBound(), 
                model.getSelection().first());
    }
    
    /**
     *  respect upper bound - the bound itself 
     *  a valid selection.
     *
     */
    public void testUpperBoundSameDay() {
        model.setUpperBound(startOfDay(today));
        // the bound itself is allowed
        model.setSelectionInterval(today, today);
        assertTrue("same day as upper bound is allowed", model.isSelected(today));
    }
    /**
     * first set the unselectables then set the selection to an unselectable.
     */
    public void testUnselectableDates() {
        SortedSet<Date> unselectableDates = new TreeSet<Date>();
        unselectableDates.add(today);
        model.setUnselectableDates(unselectableDates);
        model.setSelectionInterval(today, today);
        assertTrue("selection must be empty", model.isSelectionEmpty());
    }
    
    /**
     * Set unselectable and test that all dates of the day are unselectable.
     */
    public void testUnselectableDatesCompleteDay() {
        SortedSet<Date> unselectableDates = new TreeSet<Date>();
        unselectableDates.add(today);
        model.setUnselectableDates(unselectableDates);
        // all dates in today must be rejected
        assertTrue(model.isUnselectableDate(today));
        assertTrue(model.isUnselectableDate(startOfDay(today)));
        assertTrue(model.isUnselectableDate(endOfDay(today)));
    }
    /**
     * null unselectables not allowed.
     */
    public void testUnselectableDatesNull() {
        try {
            model.setUnselectableDates(null);
            fail("must fail with null set of unselectables");
        } catch (RuntimeException e) {
            // expected
            LOG.info("got NPE as expected - how to test fail-fast?");
        }
    }

    /**
     * test the only mode we have: set single, set interval, add all must
     * result in the same: set selection to the startDate.
     */
    public void testSingleSelection() {
        model.setSelectionInterval(today, today);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(1 == selection.size());
        assertTrue(today.equals(selection.first()));

        model.setSelectionInterval(today, tomorrow);
        selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(1 == selection.size());
        assertTrue(today.equals(selection.first()));

        model.addSelectionInterval(tomorrow, tomorrow);
        selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(1 == selection.size());
        assertTrue(tomorrow.equals(selection.first()));
    }

    
    public void testUnselectableDatesSetClearsSelection() {
        // Make sure the unselectable dates returns an empty set if it hasn't been
        // used.
        SortedSet<Date> unselectableDates = model.getUnselectableDates();
        assertTrue(unselectableDates.isEmpty());

        model.setSelectionInterval(today, today);

        unselectableDates = new TreeSet<Date>();
        unselectableDates.add(startOfDay(today));
        unselectableDates.add(startOfDay(yesterday));
        unselectableDates.add(startOfDay(tomorrow));
        model.setUnselectableDates(unselectableDates);
        // Make sure the unselectable dates is the same as what we set.
        SortedSet<Date> result = model.getUnselectableDates();
        assertEquals(unselectableDates, result);

        assertTrue("today must be removed", model.isSelectionEmpty());

    }

    
    // pre-defined reference dates - all relative to current date at around 5 am
    
    private Date today;
    private Date tomorrow;
    @SuppressWarnings("unused")
    private Date afterTomorrow;
    private Date yesterday;

    @Override
    public void setUp() {
        model = new SingleDaySelectionModel();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        today = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        yesterday = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        tomorrow = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        afterTomorrow = calendar.getTime();
        
        calendar.setTime(today);

    }
    private Date startOfDay(Date date) {
        calendar.setTime(date);
        CalendarUtils.startOfDay(calendar);
        return calendar.getTime();
    }
 
    private Date endOfDay(Date date) {
        calendar.setTime(date);
        CalendarUtils.endOfDay(calendar);
        return calendar.getTime();
    }

}