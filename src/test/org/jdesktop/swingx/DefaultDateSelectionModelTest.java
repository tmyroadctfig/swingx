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

    public void testNoSelection() {
        assertTrue(model.getSelection().isEmpty());
        model.setSelectionMode(DateSelectionModel.SelectionMode.NO_SELECTION);
        Date today = new Date();
        model.setSelectionInterval(today, today);
        assertTrue(model.getSelection().isEmpty());

        model.addSelectionInterval(today, today);
        assertTrue(model.getSelection().isEmpty());
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
        cal.roll(Calendar.DAY_OF_MONTH, 5);
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

    public void testWeekIntervalSelection() {
        model.setSelectionMode(DateSelectionModel.SelectionMode.WEEK_INTERVAL_SELECTION);
        // Use known date that lands on a Sunday.  Which just happens to be my birthday.
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.MONTH, Calendar.APRIL);
        cal.set(Calendar.DAY_OF_MONTH, 9);

        // The DefaultDateSelectionModel requires that hour/minute/second/millisecond are set to 0.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 12);
        Date endDate = cal.getTime();
        model.setSelectionInterval(startDate, endDate);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(startDate.equals(selection.first()));
        assertTrue(endDate.equals(selection.last()));

        cal.set(Calendar.DAY_OF_MONTH, 16);
        endDate = cal.getTime();
        model.setSelectionInterval(startDate, endDate);
        selection = model.getSelection();
        cal.set(Calendar.DAY_OF_MONTH, 22);
        Date expectedEndDate = cal.getTime();
        assertTrue(expectedEndDate.equals(selection.last()));

    }
}
