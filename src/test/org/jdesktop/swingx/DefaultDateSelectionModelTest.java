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
package org.jdesktop.swingx;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;

/**
 * Tests for the DefaultDateSelectionModel
 */
public class DefaultDateSelectionModelTest extends TestCase {
    private DefaultDateSelectionModel model;

    @Override
    public void setUp() {
        model = new DefaultDateSelectionModel();
    }

    @Override
    public void tearDown() {

    }

    public void testSingleSelection() {
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
        Date today = new Date();
        model.setSelectionInterval(today, today);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(1 == selection.size());
        assertTrue(today.equals(selection.first()));

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.roll(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = cal.getTime();
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

    public void testSingleIntervalSelection() {
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION);
        Date startDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date endDate = cal.getTime();
        model.setSelectionInterval(startDate, endDate);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(startDate.equals(selection.first()));
        assertTrue(endDate.equals(selection.last()));

        cal.setTime(startDate);
        cal.roll(Calendar.MONTH, 1);
        Date startDateNextMonth = cal.getTime();
        model.addSelectionInterval(startDateNextMonth, startDateNextMonth);
        selection = model.getSelection();
        assertTrue(startDateNextMonth.equals(selection.first()));
        assertTrue(startDateNextMonth.equals(selection.last()));
    }

    public void testUnselctableDates() {
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
        Date today = new Date();
        model.setSelectionInterval(today, today);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(1 == selection.size());
        assertTrue(today.equals(selection.first()));

        model.setUnselectableDates(selection);
        SortedSet<Date> unselectableDates = model.getUnselectableDates();
        assertTrue(!selection.isEmpty());
        assertTrue(1 == selection.size());
        assertTrue(unselectableDates.contains(today));

        // Make sure setting the unselectable dates to include a selected date removes
        // it from the selected set.
        selection = model.getSelection();
        assertTrue(selection.isEmpty());
    }
}
