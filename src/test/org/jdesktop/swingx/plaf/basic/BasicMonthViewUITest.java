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
package org.jdesktop.swingx.plaf.basic;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXMonthView;

/**
 * Tests to expose known issues of BasicMonthViewUI.
 * 
 * @author Jeanette Winzenburg
 */
public class BasicMonthViewUITest extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger
            .getLogger(BasicMonthViewUITest.class.getName());

    public static void main(String[] args) {
//      setSystemLF(true);
      BasicMonthViewUITest  test = new BasicMonthViewUITest();
      try {
          test.runInteractiveTests();
//        test.runInteractiveTests(".*Simple.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    /**
     * Issue 711-swingx: today notify-only property.
     * 
     * the delegate makes an educated guess (until it gets notified next time)
     * of today. 
     */
    public void testTodayUpdate() {
        JXMonthView monthView = new JXMonthView(0);
        long first = ((BasicMonthViewUI) monthView.getUI()).getTodayInMillis();
        monthView.updateUI();
        assertEquals(first, ((BasicMonthViewUI) monthView.getUI()).getTodayInMillis());
    }

    /**
     * test localized month names.
     */
    public void testLocaleMonths() {
        Locale french = Locale.FRENCH;
        JXMonthView monthView = new JXMonthView(french);
        assertMonths(monthView, french);
        Locale german = Locale.GERMAN;
        monthView.setLocale(german);
        assertMonths(monthView, german);
    }

    private void assertMonths(JXMonthView monthView, Locale french) {
        // sanity
        assertEquals(french, monthView.getLocale());
        BasicMonthViewUI ui = (BasicMonthViewUI) monthView.getUI();
        String[] months = new DateFormatSymbols(french).getMonths();
        for (int i = 0; i < months.length; i++) {
            assertEquals(months[i], ui.monthsOfTheYear[i]);
        }
    }

    public void testCustomWeekdays() {
        String[] days = new String[] {"1", "2", "3", "4", "5", "6", "7"};
        UIManager.put("JXMonthView.daysOfTheWeek", days);
        JXMonthView monthView = new JXMonthView(Locale.GERMAN);
        try {
            assertWeekdays(monthView, days);
            monthView.setLocale(Locale.FRENCH);
            assertWeekdays(monthView, days);
        } finally {
            UIManager.put("JXMonthView.daysOfTheWeek", null);
        }
    }
    private void assertWeekdays(JXMonthView monthView, String[] weekdays) {
        // sanity
        for (int i = 0; i < 7; i++) {
            assertEquals(weekdays[i], monthView.getDaysOfTheWeek()[i]);
        }
    }
    /**
     * test localized weekday names.
     */
    public void testLocaleWeekdays() {
        Locale french = Locale.FRENCH;
        JXMonthView monthView = new JXMonthView(french);
        assertWeekdays(monthView, french);
        Locale german = Locale.GERMAN;
        monthView.setLocale(german);
        assertWeekdays(monthView, german);
    }

    private void assertWeekdays(JXMonthView monthView, Locale french) {
        // sanity
        assertEquals(french, monthView.getLocale());
        String[] weekdays =
            new DateFormatSymbols(french).getShortWeekdays();
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            assertEquals(weekdays[i], monthView.getDaysOfTheWeek()[i-1]);
        }
    }
    
    /**
     * Issue ??-swingx: zero millis are valid.
     * 
     * bad marker in ui-delegate ... but looks okay? 
     */
    public void testZeroFirstDisplayedDate() {
        JXMonthView monthView = new JXMonthView(0);
        long first = monthView.getUI().getLastDisplayedDate();
        monthView.updateUI();
        assertEquals(first, monthView.getUI().getLastDisplayedDate());
    }


    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that lastDisplayedDate is unchanged.
     */
    public void testUpdateUILast() {
        final JXMonthView monthView = new JXMonthView();
        long first = monthView.getUI().getLastDisplayedDate();
        monthView.updateUI();
        assertEquals(first, monthView.getUI().getLastDisplayedDate());
    };

    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that firstDisplayedDate is unchanged.
     */
    public void testUpdateUIFirstDate() {
        final JXMonthView monthView = new JXMonthView();
        long first = ((BasicMonthViewUI) monthView.getUI()).getFirstDisplayedDate();
        monthView.updateUI();
        assertEquals(first, ((BasicMonthViewUI) monthView.getUI()).getFirstDisplayedDate());
    };
    
    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that firstDisplayedYear is unchanged.
     */
    public void testUpdateUIFirstYear() {
        final JXMonthView monthView = new JXMonthView();
        long first = ((BasicMonthViewUI) monthView.getUI()).getFirstDisplayedYear();
        monthView.updateUI();
        assertEquals(first, ((BasicMonthViewUI) monthView.getUI()).getFirstDisplayedYear());
    };
    
    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that firstDisplayedMonth is unchanged.
     */
    public void testUpdateUIFirstMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 5);
        // need to instantiate with a month different from jan
        final JXMonthView monthView = new JXMonthView(cal.getTimeInMillis());
        long first = ((BasicMonthViewUI) monthView.getUI()).getFirstDisplayedMonth();
        monthView.updateUI();
        assertEquals(first, ((BasicMonthViewUI) monthView.getUI()).getFirstDisplayedMonth());
    };
    
}
