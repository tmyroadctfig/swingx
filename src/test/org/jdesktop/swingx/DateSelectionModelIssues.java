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
    @SuppressWarnings("unused")
    private Calendar calendar;

    
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
    
    @Override
    protected void setUp() throws Exception {
        model = new DefaultDateSelectionModel();
        calendar = Calendar.getInstance();
    }
    
    
}
