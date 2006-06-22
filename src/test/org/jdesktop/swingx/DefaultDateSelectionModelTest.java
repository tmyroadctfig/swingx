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

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

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
        model.setSelectionMode(DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        Date today = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tPlus1 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tPlus2 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tPlus3 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tPlus4 = cal.getTime();

        model.setSelectionInterval(today, tPlus4);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(5 == selection.size());
        assertTrue(today.equals(selection.first()));
        assertTrue(tPlus4.equals(selection.last()));

        SortedSet<Date> unselectableDates = new TreeSet<Date>();
        unselectableDates.add(tPlus1);
        unselectableDates.add(tPlus3);
        model.setUnselectableDates(unselectableDates);

        // Make sure setting the unselectable dates to include a selected date removes
        // it from the selected set.
        selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(3 == selection.size());
        assertTrue(selection.contains(today));
        assertTrue(selection.contains(tPlus2));
        assertTrue(selection.contains(tPlus4));

        // Make sure the unselectable dates is the same as what we set.
        SortedSet<Date> result = model.getUnselectableDates();
        assertTrue(unselectableDates.equals(result));
    }
}