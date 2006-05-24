package org.jdesktop.swingx;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;

/**
 * Created by IntelliJ IDEA.
 * User: joutwate
 * Date: May 23, 2006
 * Time: 7:11:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateSelectionModelTest extends TestCase {
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
    }

    public void testSingleSelectin() {
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
        Date today = new Date();
        model.setSelectionInterval(today, today);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(selection.size() == 1);
        assertTrue(selection.first().equals(today));

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.roll(Calendar.DAY_OF_WEEK, 1);
        Date tomorrow = cal.getTime();
        model.setSelectionInterval(today, tomorrow);
        selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(selection.size() == 1);
        assertTrue(selection.first().equals(today));
    }
}
