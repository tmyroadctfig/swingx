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

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.CalendarUtils;

/**
 * Tests to expose known issues of BasicMonthViewUI.
 * 
 * @author Jeanette Winzenburg
 */
public class BasicMonthViewUITest extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger
            .getLogger(BasicMonthViewUITest.class.getName());

    // duplicate hard-coded monthViewUI values
    private static final int CALENDAR_SPACING = 10;
    
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
     * Issue #736-swingx: monthView cannot cope with minimalDaysInFirstWeek.
     * 
     */
    public void interactiveDayAt() {
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        monthView.setShowLeadingDates(true);
        monthView.setShowTrailingDates(true);
        monthView.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                LOG.info("dayAt " + e.getPoint() + ": "
                        + new Date(monthView.getDayAt(e.getX(), e.getY()))
                        + "\n" + monthView.getDayAtLocation(e.getX(), e.getY())
                                );
                LOG.info("month at " + 
                        ((BasicMonthViewUI) monthView.getUI()).getMonthAtLocation(e.getX(), e.getY()).getTime());
                
                LOG.info("month at " + 
                        ((BasicMonthViewUI) monthView.getUI()).getMonthBoundsAtLocation(e.getX(), e.getY()));
            }
            
        });
        Action action = new AbstractActionExt("toggle minimal") {

            public void actionPerformed(ActionEvent e) {
                int minimal = monthView.getSelectionModel().getMinimalDaysInFirstWeek();
                monthView.getSelectionModel().setMinimalDaysInFirstWeek(minimal > 1 ? 1 : 4);
            }
            
        };
        final JXFrame frame = wrapInFrame(monthView, "click day");
        addAction(frame, action);
        Action toggleComponentOrientation = new AbstractAction("toggle orientation") {

            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                }
                frame.getRootPane().revalidate();
                frame.invalidate();
                frame.validate();
                frame.repaint();
            }

        };
        addAction(frame, toggleComponentOrientation);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #736-swingx: monthView cannot cope with minimalDaysInFirstWeek.
     * 
     * Here: look at impact of forcing the minimalDays to a value different
     * from the calendar. Days must be displayed in starting from the 
     * first row under the days-of-week.
     * 
     * Not yet completely fixed: for very late firstDayOfWeek, the Jan is incompletely
     * painted for mininalDays > 1. Rare enough to ignore for now?
     */
    public void interactiveMinimalDaysInFirstWeek() {
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        monthView.setShowLeadingDates(true);
        monthView.setShowTrailingDates(true);
        Action action = new AbstractActionExt("toggle minimal") {

            public void actionPerformed(ActionEvent e) {
                int minimal = monthView.getSelectionModel().getMinimalDaysInFirstWeek();
                monthView.getSelectionModel().setMinimalDaysInFirstWeek(minimal > 1 ? 1 : 4);
            }
            
        };
        final JXFrame frame = wrapInFrame(monthView, "click unselectable fires ActionEvent");
        addAction(frame, action);
        Action toggleComponentOrientation = new AbstractAction("toggle orientation") {

            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                }
                frame.getRootPane().revalidate();
                frame.invalidate();
                frame.validate();
                frame.repaint();
            }

        };
        addAction(frame, toggleComponentOrientation);
        JXStatusBar bar = getStatusBar(frame);
        final JComboBox dayOfWeekComboBox = new JComboBox(new String[]{"Sunday", "Monday", "Tuesday",
                "Wednesday", "Thursday", "Friday", "Saturday"});
        dayOfWeekComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int selected = dayOfWeekComboBox.getSelectedIndex();
                monthView.setFirstDayOfWeek(selected + Calendar.SUNDAY);
                
            }
            
        });
        dayOfWeekComboBox.setSelectedIndex(monthView.getFirstDayOfWeek() - Calendar.SUNDAY);
        bar.add(dayOfWeekComboBox);
        frame.pack();
        frame.setVisible(true);
    }

//------------------------------
 
    
    /**
     * Test day at location
     */
    public void testDayAtLocationLToR() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiLToR = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBounds = uiLToR.getMonthBoundsAtLocation(20, 20);
        Rectangle dayBounds = uiLToR.getDayBoundsAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + uiLToR.getMonthHeaderHeight() + 2); 
        // first column in second non-header row
        Calendar calLToR = uiLToR.getDayAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + uiLToR.getMonthHeaderHeight() + 2 * dayBounds.height + 2); 
        CalendarUtils.startOfWeek(calLToR);
        Calendar uiCalendarL = uiLToR.getCalendar();
        uiCalendarL.add(Calendar.WEEK_OF_YEAR, 1);
        CalendarUtils.startOfWeek(uiCalendarL);
        assertEquals("first logical column in LToR", uiCalendarL.getTime(), calLToR.getTime());
     }

    /**
     * Test day at location: hitting days of week must return null.
     */
    public void testDayAtLocationDayHeaderNull() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiLToR = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBounds = uiLToR.getMonthBoundsAtLocation(20, 20);
        // same for LToR
        Calendar calLToR = uiLToR.getDayAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + uiLToR.getMonthHeaderHeight() + 2); 
        assertNull("hitting days-of-week must return null calendar", calLToR);
     }
    
    /**
     * Test day at location
     */
    public void testDayAtLocationRToL() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiRToL = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        Rectangle monthBounds = uiRToL.getMonthBoundsAtLocation(20, 20);
        Rectangle dayBounds = uiRToL.getDayBoundsAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + uiRToL.getMonthHeaderHeight() + 2); 
        // first column in second non-header row
        Calendar calRToL = uiRToL.getDayAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + uiRToL.getMonthHeaderHeight() + 2 * dayBounds.height + 2); 
        CalendarUtils.endOfWeek(calRToL);
        Calendar uiCalendar = uiRToL.getMonthAtLocation(20, 20);
        uiCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        CalendarUtils.endOfWeek(uiCalendar);
        assertEquals("first day in first week", uiCalendar.getTime(), calRToL.getTime()); 
     }

    public void testDayBounds() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiRToL = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        BasicMonthViewUI uiLToR = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBounds = uiRToL.getMonthBoundsAtLocation(20, 20);
        assertNull("hit in header must return null bounds", 
                uiRToL.getDayBoundsAtLocation(monthBounds.x + 2, monthBounds.y + 2));
        // first column near bottom
        Rectangle dayBoundsRToL = uiRToL.getDayBoundsAtLocation(
                monthBounds.x + 2, monthBounds.y + monthBounds.height - 20); 
        // same for LToR
        Rectangle dayBoundsLToR = uiLToR.getDayBoundsAtLocation(
                monthBounds.x + 2, monthBounds.y + monthBounds.height - 20); 
        assertEquals("day bounds must be independent of orientation", 
                dayBoundsLToR, dayBoundsRToL);
    }
 
    /**
     * days of week is mapped to row index -1.
     */
    public void testDayGridPositionColumnHeader() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiRToL = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        Rectangle monthBounds = uiRToL.getMonthBoundsAtLocation(20, 20);
        // first row below month header == days of week header 
        Point dayGridRToL = uiRToL.getDayGridPositionAtLocation(
                monthBounds.x + 2, monthBounds.y + uiRToL.getMonthHeaderHeight() + 2); 
        assertEquals("first row below header must be day column header", -1, dayGridRToL.y);
     }

    /**
     * day grid rows >= 0
     */
    public void testDayGridPositionRow() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiRToL = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        Rectangle monthBounds = uiRToL.getMonthBoundsAtLocation(20, 20);
        Rectangle dayBounds = uiRToL.getDayBoundsAtLocation(
                monthBounds.x + 2, monthBounds.y + uiRToL.getMonthHeaderHeight() +2); 

        // first column near bottom
        Point dayGridRToL = uiRToL.getDayGridPositionAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + uiRToL.getMonthHeaderHeight() + dayBounds.height + 2); 
       
        assertEquals("first row", 0, dayGridRToL.y);
     }
 
    /**
     * Screen location mapped to logical day columns.
     */
    public void testDayGridPositionColumn() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiRToL = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        BasicMonthViewUI uiLToR = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBounds = uiRToL.getMonthBoundsAtLocation(20, 20);
        // first column in first non-header row
        Point dayGridRToL = uiRToL.getDayGridPositionAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + uiRToL.getMonthHeaderHeight() + 2); 
        assertEquals("last logical column in RToL", JXMonthView.DAYS_IN_WEEK - 1, 
                dayGridRToL.x);
        // same for LToR
        Point dayGridLToR = uiLToR.getDayGridPositionAtLocation(
                monthBounds.x + 2, monthBounds.y + monthBounds.height - 20); 
        assertEquals("first logical column in LToR", 0, dayGridLToR.x);
     }

    public void testDayGridPositionWeekHeader() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiRToL = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT, true);
        BasicMonthViewUI uiLToR = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT, true);
        Rectangle monthBounds = uiRToL.getMonthBoundsAtLocation(20, 20);
        // first column near bottom
        Point dayGridRToL = uiRToL.getDayGridPositionAtLocation(
                monthBounds.x + monthBounds.width - 2, 
                monthBounds.y + uiRToL.getMonthHeaderHeight() + 2); 
        assertEquals("weeks of year column in RTL", -1, 
                dayGridRToL.x);
        // same for LToR
        Point dayGridLToR = uiLToR.getDayGridPositionAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + uiRToL.getMonthHeaderHeight() + 2); 
        assertEquals("first logical column in LToR", -1, dayGridLToR.x);
     }
    
    /**
     * day grid returns null for hitting month header.
     */
    public void testDayGridPositionMonthHeaderHit() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiRToL = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        BasicMonthViewUI uiLToR = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBounds = uiRToL.getMonthBoundsAtLocation(20, 20);
        assertNull("hit in header must return null grid position", 
                uiRToL.getDayGridPositionAtLocation(monthBounds.x + 2, monthBounds.y + 2));
        assertNull("hit in header must return null grid position", 
                uiLToR.getDayGridPositionAtLocation(monthBounds.x + 2, monthBounds.y + 2));
    }
    
    
    /**
     * coordinate mapping: get calendar from logical grid 
     *   coordinates.
     */
    public void testMonthFromGrid() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiRToL = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        BasicMonthViewUI uiLToR = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Calendar month = uiLToR.getMonth(0, 0);
        Calendar first = uiLToR.getCalendar();
        assertEquals(first.get(Calendar.MONTH), month.get(Calendar.MONTH));
        Calendar monthRL = uiRToL.getMonth(0, 0);
        assertEquals("logical coordinates must be independent of orientation",
                first.get(Calendar.MONTH), monthRL.get(Calendar.MONTH));
    }

    /**
     * coordinate mapping: logical grid coordinates.
     */
    public void testMonthGridPositionAtLocation() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiRToL = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        BasicMonthViewUI uiLToR = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Point gridPositionLToR = uiLToR.getMonthGridPositionAtLocation(20, 20);
        assertEquals(0, gridPositionLToR.x);
        Point gridPositionRToL = uiRToL.getMonthGridPositionAtLocation(20, 20);
        assertEquals(1, gridPositionRToL.x);
    }
    
    

    /**
     * coordinate mapping: monthBounds in pixel.
     * 
     */
    public void testMonthBoundsAtLocation() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI uiRToL = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        BasicMonthViewUI uiLToR = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBoundsRToL = uiRToL.getMonthBoundsAtLocation(20, 20);
        Rectangle monthBoundsLToR = uiLToR.getMonthBoundsAtLocation(20, 20);
        // bounds of first
        assertEquals("bounds of left-most month must be equal", 
                monthBoundsLToR, monthBoundsRToL);
        Rectangle monthBoundsTwoRToL = uiRToL.getMonthBoundsAtLocation(
                        monthBoundsRToL.width + 20, 20);
        Rectangle monthBoundsTwoLToR = uiRToL.getMonthBoundsAtLocation(
                monthBoundsLToR.width + 20, 20);
        assertEquals("bounds of right-most month must be equal", 
                monthBoundsTwoLToR, monthBoundsTwoRToL);
        
    }

    /**
     * Returns the ui of a realized JXMonthView with 2 columns and the 
     * given componentOrientation without showingWeekNumbers.
     * 
     * NOTE: this must not be used in a headless environment.
     * 
     * @param co
     * @return
     */
    private BasicMonthViewUI getRealizedMonthViewUI(ComponentOrientation co) {
        return getRealizedMonthViewUI(co, false);
    }

    /**
     * Returns the ui of a realized JXMonthView with 2 columns and the 
     * given componentOrientation and showingWeekNumbers flag.
     * 
     * NOTE: this must not be used in a headless environment.
     * 
     * @param co
     * @return
     */
    private BasicMonthViewUI getRealizedMonthViewUI(ComponentOrientation co,
            boolean isShowingWeekNumbers) {
        JXMonthView monthView = new JXMonthView();
        monthView.setPreferredCols(2);
        monthView.setComponentOrientation(co);
        monthView.setShowingWeekNumber(isShowingWeekNumbers);
        JXFrame frame = new JXFrame();
        frame.add(monthView);
        frame.pack();
        BasicMonthViewUI ui = (BasicMonthViewUI) monthView.getUI();
        return ui;
    }

    /**
     * cleanup date representation as long: new api getDayAtLocation. will
     * replace getDayAt which is deprecated as a first step.
     */
    @SuppressWarnings("deprecation")
    public void testGetDayAtLocation() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        JXMonthView monthView = new JXMonthView();
        monthView.getSelectionModel().setMinimalDaysInFirstWeek(1);
        JXFrame frame = new JXFrame();
        frame.add(monthView);
        frame.pack();
        Dimension pref = monthView.getPreferredSize();
        pref.width = pref.width / 2;
        pref.height = pref.height / 2;
        long dayLong = monthView.getDayAt(pref.width, pref.height);
        assertTrue(dayLong > 0);
        Calendar date = monthView.getDayAtLocation(pref.width, pref.height);
        assertNotNull(date);
        Calendar cal = monthView.getCalendar();
        cal.setTimeInMillis(dayLong);
        assertEquals(cal.getTime(), date.getTime());
        assertTrue(CalendarUtils.isSameDay(cal, date.getTime()));
        assertEquals(new Date(dayLong), date.getTime());
    }

    
    /**
     * Issue 711-swingx: today notify-only property.
     * Changed to read-only in monthView
     */
    public void testTodayUpdate() {
        JXMonthView monthView = new JXMonthView(0);
        Date first = ((BasicMonthViewUI) monthView.getUI()).getToday();
        monthView.updateUI();
        assertEquals(first, ((BasicMonthViewUI) monthView.getUI()).getToday());
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
