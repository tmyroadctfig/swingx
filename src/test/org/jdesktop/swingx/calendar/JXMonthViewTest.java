/**
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

import junit.framework.TestCase;
import org.jdesktop.swingx.DateSelectionModel;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;

/**
 * Test case for <code>JXMonthView</code>
 *
 * @author Joshua Outwater
 */
public class JXMonthViewTest extends TestCase {

    public void setUp() {
    }

    public void teardown() {
    }

    public void testDefaultConstructor() {
        JXMonthView monthView = new JXMonthView();
        assertTrue(monthView.getSelection().isEmpty());
        assertTrue(JXMonthView.SelectionMode.SINGLE_SELECTION == monthView.getSelectionMode());
        assertTrue(Calendar.SUNDAY == monthView.getFirstDayOfWeek());
    }

    public void testNullSelection() {
        JXMonthView monthView = new JXMonthView();
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(selection.isEmpty());

        Date date = new Date();
        monthView.setSelectionInterval(date, date);
        selection = monthView.getSelection();
        assertTrue(1 == selection.size());
        assertTrue(date.equals(selection.first()));

        monthView.clearSelection();
        selection = monthView.getSelection();
        assertTrue(selection.isEmpty());
    }

    public void testNoSelectionMode() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(JXMonthView.SelectionMode.NO_SELECTION);

        Date date = new Date();
        monthView.setSelectionInterval(date, date);
        assertTrue(monthView.getSelection().isEmpty());
    }

    public void testSingleSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(JXMonthView.SelectionMode.SINGLE_SELECTION);

        Date today = new Date();
        monthView.setSelectionInterval(today, today);
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(1 == selection.size());
        assertTrue(today.equals(selection.first()));

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.roll(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = cal.getTime();
        monthView.setSelectionInterval(today, tomorrow);
        selection = monthView.getSelection();
        assertTrue(1 == selection.size());
        assertTrue(today.equals(selection.first()));
    }

    public void testSingleIntervalSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(JXMonthView.SelectionMode.SINGLE_INTERVAL_SELECTION);

        Date today = new Date();
        monthView.setSelectionInterval(today, today);
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(1 == selection.size());
        assertTrue(today.equals(selection.first()));

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.roll(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = cal.getTime();
        monthView.setSelectionInterval(today, tomorrow);
        selection = monthView.getSelection();
        assertTrue(2 == selection.size());
        assertTrue(today.equals(selection.first()));
        assertTrue(tomorrow.equals(selection.last()));
    }

    public void testWeekIntervalSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(JXMonthView.SelectionMode.WEEK_INTERVAL_SELECTION);

        // Use a known date that falls on a Sunday, which just happens to be my birthday.
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.MONTH, Calendar.APRIL);
        cal.set(Calendar.DAY_OF_MONTH, 9);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        Date endDate;
        cal.set(Calendar.DAY_OF_MONTH, 13);
        endDate = cal.getTime();

        monthView.setSelectionInterval(startDate, endDate);
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(startDate.equals(selection.first()));
        assertTrue(endDate.equals(selection.last()));

        cal.set(Calendar.DAY_OF_MONTH, 20);
        endDate = cal.getTime();
        monthView.setSelectionInterval(startDate, endDate);

        cal.set(Calendar.DAY_OF_MONTH, 22);
        endDate = cal.getTime();
        selection = monthView.getSelection();

        assertTrue(startDate.equals(selection.first()));
        assertTrue(endDate.equals((selection.last())));
    }

    public void testModelSelectionUpdate() {
        JXMonthView monthView = new JXMonthView();

        // The JXMonthView uses an underlying model mode of single selection when it is in no selection mode.
        monthView.setSelectionMode(JXMonthView.SelectionMode.NO_SELECTION);
        assertTrue(
                DateSelectionModel.SelectionMode.SINGLE_SELECTION == monthView.getSelectionModel().getSelectionMode());

        monthView.setSelectionMode(JXMonthView.SelectionMode.SINGLE_SELECTION);
        assertTrue(
                DateSelectionModel.SelectionMode.SINGLE_SELECTION == monthView.getSelectionModel().getSelectionMode());

        monthView.setSelectionMode(JXMonthView.SelectionMode.SINGLE_INTERVAL_SELECTION);
        assertTrue(
                DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION ==
                        monthView.getSelectionModel().getSelectionMode());

        // The JXMonthView uses an underlying model mode of single interval selection when it is in week selection mode.
        monthView.setSelectionMode(JXMonthView.SelectionMode.WEEK_INTERVAL_SELECTION);
        assertTrue(
                DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION ==
                        monthView.getSelectionModel().getSelectionMode());

        monthView.setSelectionMode(JXMonthView.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        assertTrue(
                DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION ==
                        monthView.getSelectionModel().getSelectionMode());

    }
}
