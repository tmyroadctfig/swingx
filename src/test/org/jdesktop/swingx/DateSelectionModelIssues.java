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

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdesktop.swingx.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.test.DateSelectionReport;

/**
 * Test to expose known Issues with <code>DateSelectionModel</code>
 *  and implementations.
 * 
 * @author Jeanette Winzenburg
 */
public class DateSelectionModelIssues extends InteractiveTestCase {

    private DateSelectionModel model;
    private Calendar calendar;

    /**
     * respect both bounds - 
     *
     * Both bounds same --> bound allowed.
     */
    public void testBothBoundsSame() {
        Date today = getCleanedToday();
        model.setLowerBound(today);
        model.setUpperBound(today);
        model.setSelectionInterval(today, today);
        assertEquals("selected bounds", today, 
                model.getSelection().first());
    }

    /**
     * respect both bounds - 
     *
     * Both bounds same --> bound allowed.
     */
    public void testBothBoundsOverlap() {
        Date today = getCleanedToday();
        model.setLowerBound(today);
        Date yesterday = getCleanedToday(-1);
        model.setUpperBound(yesterday);
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
        Date today = getCleanedToday();
        model.setLowerBound(today);
        Date yesterday = getCleanedToday(-1);
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
        Date today = getCleanedToday();
        model.setLowerBound(today);
        // the bound itself is allowed
        model.setSelectionInterval(today, today);
        assertEquals("selected upper bound", model.getLowerBound(), 
                model.getSelection().first());
    }
    /**
     *  respect upper bound - the day after is
     *  an invalid selection.
     *
     */
    public void testUpperBoundFuture() {
        Date today = getCleanedToday();
        model.setUpperBound(today);
        Date tomorrow = getCleanedToday(1);
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setSelectionInterval(tomorrow, tomorrow);
        assertEquals("selection must be empty", 0, model.getSelection().size());
        assertEquals("no event fired", 0, report.getEventCount());
    }
    
    /**
     *  respect upper bound - the bound itself 
     *  a valid selection.
     *
     */
    public void testUpperBound() {
        Date today = getCleanedToday();
        model.setUpperBound(today);
        // the bound itself is allowed
        model.setSelectionInterval(today, today);
        assertEquals("selected upper bound", model.getUpperBound(), 
                model.getSelection().first());
    }
    
    /**
     * NYI: respect unselectables on set/addSelection
     * first set the unselectables then set the selection to an unselectable.
     * NOTE: the other way round - adjust the selection on setUnselectable
     *   is implemented.
     */
    public void testUnselectableDates() {
        Date today = getCleanedToday();
        SortedSet<Date> unselectableDates = new TreeSet<Date>();
        unselectableDates.add(today);
        model.setUnselectableDates(unselectableDates);
        // sanity: 
        assertTrue(model.isUnselectableDate(today));
        model.setSelectionInterval(today, today);
        assertEquals("selection must be empty", 0, model.getSelection().size());
    }
    
    public void testUnselectableDatesCleanupOneRemovedEvent() {
        fail("TODO: test that we fire only one remove event");
    }
    
    /**
     * Issue ??-swingx: set/add/remove dates must cope with null or 
     *   document not to.
     * here: set, single selection
     */
    public void testNullDateSetSingle() {
        model.setSelectionInterval(null, null);
    }

    /**
     * Issue ??-swingx: set/add/remove dates must cope with null or 
     *   document not to.
     * here: set, single selection
     */
    public void testNullDateRemoveSingle() {
        model.removeSelectionInterval(null, null);
    }
    
    /**
     * Issue ??-swingx: set/add/remove dates must cope with null or 
     *   document not to.
     * here: set, single selection
     */
    public void testNullDateAddSingle() {
        model.addSelectionInterval(null, null);
    }

    /**
     * Event properties should be immutable.
     *
     */
    public void testEventImmutable() {
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        Date date = new Date();
        model.setSelectionInterval(date, date);
        assertEquals(1, report.getEventCount());
        DateSelectionEvent event = report.getLastEvent();
        // sanity
        assertEquals(date, event.getSelection().first());
        Date next = new Date();
        model.setSelectionInterval(next, next);
        assertSame(date, event.getSelection().first());
    }
    /**
     * 
     * @param days
     * @return the current day offset by days with all time elements 
     *   set to 0
     */
    private Date getCleanedToday(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
    /**
     * 
     * @return the current date with all time elements set to 0
     */
    private Date getCleanedToday() {
        return getCleanedDate(Calendar.getInstance());
    }
    
    /**
     * 
     * @param cal the calendar to clean
     * @return the calendar's date with all time elements set to 0
     */
    private Date getCleanedDate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    protected void setUp() throws Exception {
        model = new DefaultDateSelectionModel();
        calendar = Calendar.getInstance();
    }
    
    
}
