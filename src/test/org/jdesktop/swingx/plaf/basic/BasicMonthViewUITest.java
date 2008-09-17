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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;

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
    @SuppressWarnings("unused")
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
     * Issue #750-swingx: use rendering to side-step antialiase probs.
     * 
     * Debugging ...
     */
    public void interactiveRenderingOn() {
        // force default loading
        new JXMonthView();
        // this is global state - uncomment for debug painting completely
//        UIManager.put("JXMonthView.trailingDayForeground", Color.YELLOW);
//        UIManager.put("JXMonthView.leadingDayForeground", Color.ORANGE);
//        UIManager.put("JXMonthView.weekOfTheYearForeground", Color.GREEN);
//        UIManager.put("JXMonthView.unselectableDayForeground", Color.MAGENTA);
        String frameTitle = "Debug painting: rendering on";
        showDebugMonthView(frameTitle, null);
    }

    /**
     * Issue #750-swingx: use rendering to side-step antialiase probs.
     * 
     * Debugging ...
     */
    public void interactiveRenderingOff() {
        String frameTitle = "Debug painting: rendering off";
        Boolean disableRendering = Boolean.TRUE;
        showDebugMonthView(frameTitle, disableRendering);
    }
    /**
     * @param frameTitle
     * @param disableRendering
     */
    private void showDebugMonthView(String frameTitle, Boolean disableRendering) {
        final JXMonthView monthView = new JXMonthView();
        monthView.putClientProperty("disableRendering", disableRendering);
        monthView.setDayForeground(Calendar.SUNDAY, Color.BLUE);
        monthView.setDaysOfTheWeekForeground(Color.RED);
        monthView.setFlaggedDayForeground(Color.CYAN);
        monthView.setSelectedBackground(Color.GRAY);
        monthView.setTodayBackground(Color.PINK);
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        monthView.setShowingLeadingDays(true);
        monthView.setShowingTrailingDays(true);
        monthView.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        monthView.setPreferredCols(2);
        monthView.setPreferredRows(2);
        final JXFrame frame = wrapInFrame(monthView, frameTitle);
        addComponentOrientationToggle(frame);
        final JXDatePicker picker = new JXDatePicker();
        picker.getMonthView().putClientProperty("disableRendering", disableRendering);
        picker.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JXDatePicker.CANCEL_KEY)) return;
                if (picker.getDate() == null) return;
                monthView.setFlaggedDates(picker.getDate());
            }
            
        });
        final JXDatePicker unselectable = new JXDatePicker();
        unselectable.getMonthView().putClientProperty("disableRendering", disableRendering);
        unselectable.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JXDatePicker.CANCEL_KEY)) return;
                if (unselectable.getDate() == null) return;
                monthView.setUnselectableDates(unselectable.getDate());
            }
            
        });
        JComponent pickers = Box.createHorizontalBox();
        pickers.add(new JLabel("Flagged: "));
        pickers.add(picker);
        pickers.add(new JLabel("Unselectable: "));
        pickers.add(unselectable);
        frame.add(pickers, BorderLayout.SOUTH);
        show(frame);
    }


    /**
     * Issue #736-swingx: monthView cannot cope with minimalDaysInFirstWeek.
     * 
     * Debugging ...
     */
    public void interactiveDayAt() {
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        monthView.setShowingLeadingDays(true);
        monthView.setShowingTrailingDays(true);
        monthView.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        monthView.setPreferredCols(2);
        monthView.setPreferredRows(2);
        final BasicMonthViewUI ui = ((BasicMonthViewUI) monthView.getUI());
        monthView.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
//                LOG.info("calendar grid" + ui.calendarGrid);
//                LOG.info("dayAt " + e.getPoint() + ": "
//                        + "\n" + monthView.getDayAtLocation(e.getX(), e.getY()));
//                Calendar monthAtLocation = ui.getMonthAtLocation(e.getX(), e.getY());
//                LOG.info("month start " + 
//                        (monthAtLocation != null ? monthAtLocation.getTime() : null));
                
//                Point p = ui.getMonthGridPositionAtLocation(e.getX(), e.getY());
//                LOG.info("month bounds from logical " + 
//                        p + " \n " +
//                        ui.getMonthBounds(p.y, p.x));
//                LOG.info("month bounds at location" + 
//                        ui.getMonthBoundsAtLocation(e.getX(), e.getY()));
                LOG.info("day grid position " + 
                        ui.getDayGridPositionAtLocation(e.getX(), e.getY()) 
                      + "\nday bounds " + 
                        ui.getDayBoundsAtLocation(e.getX(), e.getY()));
            }
            
        });
        Action action = new AbstractActionExt("toggle minimal") {

            public void actionPerformed(ActionEvent e) {
                int minimal = monthView.getSelectionModel().getMinimalDaysInFirstWeek();
                monthView.getSelectionModel().setMinimalDaysInFirstWeek(minimal > 1 ? 1 : 4);
            }
            
        };
        final JXFrame frame = wrapInFrame(monthView, "test mapping: printed on mouse release");
        addAction(frame, action);
        addComponentOrientationToggle(frame);
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
        monthView.setShowingLeadingDays(true);
        monthView.setShowingTrailingDays(true);
        monthView.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        Action action = new AbstractActionExt("toggle minimal") {

            public void actionPerformed(ActionEvent e) {
                int minimal = monthView.getSelectionModel().getMinimalDaysInFirstWeek();
                monthView.getSelectionModel().setMinimalDaysInFirstWeek(minimal > 1 ? 1 : 4);
            }
            
        };
        final JXFrame frame = wrapInFrame(monthView, "click unselectable fires ActionEvent");
        addAction(frame, action);
        addComponentOrientationToggle(frame);
        final JComboBox dayOfWeekComboBox = new JComboBox(new String[]{"Sunday", "Monday", "Tuesday",
                "Wednesday", "Thursday", "Friday", "Saturday"});
        dayOfWeekComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int selected = dayOfWeekComboBox.getSelectedIndex();
                monthView.setFirstDayOfWeek(selected + Calendar.SUNDAY);
                
            }
            
        });
        dayOfWeekComboBox.setSelectedIndex(monthView.getFirstDayOfWeek() - Calendar.SUNDAY);
        addStatusComponent(frame, dayOfWeekComboBox);
        frame.pack();
        frame.setVisible(true);
    }

//------------------------------

    /**
     * Test that day position in details day-of-week header returns null date
     */
    public void testDayInMonthForDayOfWeekHeader() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // get a date in the first month
        Date month = ui.getMonth(1, 0);
        assertEquals("header grid position must return null date", null, ui.getDayInMonth(month, -1, 6));
     }

    /**
     * Test that day position in details weekNumber header returns null date
     */
    public void testDayInMonthForWeekNumberHeader() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT, true);
        // get a date in the first month
        Date month = ui.getMonth(0, 0);
        assertEquals("header grid position must return null date", null, ui.getDayInMonth(month, 3, -1));
     }

    /**
     * Test contract of getDayInMonth: grid position of leading dates must be null.
     * Here: first month in grid.
     */
    public void testDayInMonthLeadingFirstMonth() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // get a date in the first month
        Date month = ui.getMonth(0, 0);
        assertEquals("leading date grid position must return null date", null, ui.getDayInMonth(month, 0, 0));
     }
    
    /**
     * Test contract of getDayInMonth: grid position of leading dates must be null.
     * Here: second month in grid.
     */
    public void testDayInMonthLeadingSecondMonth() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // get a date in the first month
        Date month = ui.getMonth(1, 0);
        // this fails because 
        assertEquals("leading date grid position must return null date", null, ui.getDayInMonth(month, 0, 0));
     }
    
    /**
     * Issue #787-swingx: hit detection of leading/trailing days.
     * Sanity: get day bounds for leading dates (here: second month) must succeed.
     */
    public void testDayBoundsAtLocationLeadingSecondMonth() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // second month - Mar 2008 has leading dates
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20 + ui.getMonthSize().width, 20);
        int firstDay = monthBounds.y + ui.getMonthHeaderHeight() + ui.getDaySize().height + 2;
        // second month should behave the same way as first 
        // first is outside and returns null.
        int row = ui.getDayGridPositionAtLocation(monthBounds.x + 2, firstDay).y;
        assertEquals(0, row);
        Rectangle dayBounds = ui.getDayBoundsAtLocation(monthBounds.x + 2, firstDay);
        assertNotNull(dayBounds);
    }

    /**
     * Issue #787-swingx: hit detection of leading/trailing days.
     * Sanity: get day bounds for leading dates (here: first month) must succeed.
     */
    public void testDayBoundsAtLocationLeadingFirstMonth() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // first month - Feb 2008 has leading dates
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        int firstDayY = monthBounds.y + ui.getMonthHeaderHeight() + ui.getDaySize().height + 2;
        int row = ui.getDayGridPositionAtLocation(monthBounds.x + 2, firstDayY).y;
        assertEquals(0, row);
        Rectangle dayBounds = ui.getDayBoundsAtLocation(monthBounds.x + 2, firstDayY);
        assertNotNull(dayBounds);
    }
    /**
     * Issue #787-swingx: hit detection of leading/trailing days.
     * Must be null (second month).
     */
    public void testDayAtLocationLeadingSecondMonth() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // second month - Mar 2008 has leading dates
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20 + ui.getMonthSize().width, 20);
        int firstDay = monthBounds.y + ui.getMonthHeaderHeight() + ui.getDaySize().height + 2;
        assertEquals("hit detection in leading date must return null", null, 
                ui.getDayAtLocation(monthBounds.x + 2, firstDay));
    }

    /**
     * Issue #787-swingx: hit detection of leading/trailing days.
     * Must be null (first month).
     */
    public void testDayAtLocationLeadingFirstMonth() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // first month - Feb 2008 has leading dates
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        int firstDayY = monthBounds.y + ui.getMonthHeaderHeight() + ui.getDaySize().height + 2;
        assertEquals("hit detection in leading date must return null", null, 
                ui.getDayAtLocation(monthBounds.x + 2, firstDayY));
    }

    /**
     * Test full circle: getDayBounds(Date)
     */
    public void testDayBoundsFromDate() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle bounds = ui.getMonthBoundsAtLocation(20, 20);
        Dimension daySize = ui.getDaySize();
        // first day column
        int locationX = bounds.x + 2;
        // second non-header row
        int locationY = bounds.y + ui.getMonthHeaderHeight() + 2 * daySize.height + 2;
        Rectangle dayBounds = ui.getDayBoundsAtLocation(locationX, locationY);
        Date date = ui.getDayAtLocation(locationX, locationY); 
        assertEquals(dayBounds, ui.getDayBounds(date));
     }


    /**
     * Test getDayGridPosition(Date) - 
     * here: had been incorrect calculation of first row.
     */
    public void testDateToGridPosition6Apr2008() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Calendar cal = ui.getCalendar();
        cal.set(2008, Calendar.APRIL, 6);
        // it's the last day of the first week
        assertEquals(6, ui.getDayGridPosition(cal.getTime()).x);
        assertEquals(0, ui.getDayGridPosition(cal.getTime()).y);
        
     }

    /**
    * Test getDayGridPosition(Date) - first complete row
    */
    public void testDateToGridPositionNonLeading() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // get a date in the first month
        Date month = ui.getMonth(0, 0);
        // second row - cant be a leading date
        assertDateToDayGrid(ui, month, 1, 0);
     }

    /**
    * Test getDayGridPosition(Date) - somewhere in the middle (being paranoid ;-)
     */
    public void testDateToGridPositionMiddle() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // get a date in the first month
        Date month = ui.getMonth(0, 0);
        assertDateToDayGrid(ui, month, 2, 4);
     }
    
    /**
     * Full cylce: use getDayInMonth(...) to get the day from the logical 
     * grid position and assert that the reverse getDayGridPosition returns the same
     * logical coordinates. The given coordinates must map to a day contained in the
     * month, that is >= 0 and not representing leading/trailing dates.
     * 
     * @param ui the ui to use (must be realized)
     * @param month the month containing the grid to test
     * @param dayRow the logical row coordinate of the day
     * @param dayColumn the logical column coordinate of the day
     */
    private void assertDateToDayGrid(BasicMonthViewUI ui, Date month,
            int dayRow, int dayColumn) {
        Date day = ui.getDayInMonth(month, dayRow, dayColumn);
        assertEquals(dayRow, ui.getDayGridPosition(day).y);
        assertEquals(dayColumn, ui.getDayGridPosition(day).x);
    }




    
    /**
     * Test full circle: getMonthGridPosition(Date) - had problems with first row?
     */
    public void testMonthGridPositionFirstRowFromDate() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        assertDateToMonthGrid(ui, 0, 1);
     }

    /**
     * Full cylce: use getMonth(...) to get the month from the logical 
     * grid position and assert that the reverse getMonthGridPosition returns the same
     * logical coordinates. 
     * 
     * @param ui the ui to use (must be realized)
     * @param row the row index of the month
     * @param column the column index of the month
     */
    private void assertDateToMonthGrid(BasicMonthViewUI ui, int row, int column) {
        // date of start of month from logical position
        Date month = ui.getMonth(row, column);
        assertEquals(row, ui.getMonthGridPosition(month).y);
        assertEquals(column, ui.getMonthGridPosition(month).x);
    }

    /**
     * Test full circle: getMonthGridPosition(Date) - had problems with first row?
     */
    public void testDateToMonthGridPositionFirst() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        assertDateToMonthGrid(ui, 0, 0);
     }
    /**
     * Test full circle: getMonthGridPosition(Date) - had problems with first row?
     */
    public void testDateToMonthGridPositionSecond() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        assertDateToMonthGrid(ui, 1, 0);
     }

    /**
     * Test full circle: getMonthGridPosition(Date)
     */
    public void testDateToMonthGridPositionLast() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        assertDateToMonthGrid(ui, 1, 1);
     }

    /**
     * Test full circle: getMonthBounds(Date)
     */
    public void testDateToMonthBounds() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // month bounds from logical position in second row, second column
        Rectangle bounds = ui.getMonthBounds(1, 1);
        // date of start of month from logical position
        Date month = ui.getMonth(1, 1);
        assertEquals(bounds, ui.getMonthBounds(month));
     }

    /**
     * Test  getMonthBounds(Date) for not visible dates are null.
     */
    public void testDateToMonthBoundsNotVisible() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // the ui's calendar is configured to the first displayed day
        Calendar uiCalendar = ui.getCalendar();
        int month = uiCalendar.get(Calendar.MONTH);
        CalendarUtils.startOfWeek(uiCalendar);
        assertFalse("sanity - we have leading dates in the month", month == uiCalendar.get(Calendar.MONTH));
        assertEquals("leading dates must return null bounds", 
                null, ui.getMonthBounds(uiCalendar.getTime()));
    }

    /**
     * Test  getDayBounds(Date)  for null date must throw NPE.
     */
    public void testMonthBoundsNullDate() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        try {
            ui.getMonthBounds(null);
            fail("date param null is not allowed - must fire NPE");
        } catch (NullPointerException ex) {
            // that's what we expect
        }
    }
    
    /**
     * Test  getDayBounds(Date) for leading dates are null.
     */
    public void testDateToDayBoundsLeadingDate() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // the ui's calendar is configured to the first displayed day
        Calendar uiCalendar = ui.getCalendar();
        int month = uiCalendar.get(Calendar.MONTH);
        CalendarUtils.startOfWeek(uiCalendar);
        assertFalse("sanity - we have leading dates in the month", month == uiCalendar.get(Calendar.MONTH));
        assertEquals("leading dates must return null bounds", 
                null, ui.getDayBounds(uiCalendar.getTime()));
    }

    /**
     * Test  getDayBounds(Date)  for null date must fire NPE.
     */
    public void testDayBoundsNullDate() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        try {
            ui.getDayBounds(null);
            fail("date param null is not allowed - must fire NPE");
        } catch (NullPointerException ex) {
            // that's what we expect
        }
    }
    /**
     * Issue #781-swingx: reverse coordinate transformation.
     * Here: expose sizes
     */
    public void testMonthSize() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        assertEquals(monthBounds.width, ui.getMonthSize().width);
        assertEquals(monthBounds.height, ui.getMonthSize().height);
    }
 
    /**
     * Issue #781-swingx: reverse coordinate transformation.
     * Here: expose sizes
     */
    public void testDaySize() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        Rectangle dayBounds = ui.getDayBoundsAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + ui.getMonthHeaderHeight() + 2); 
        assertEquals(dayBounds.width, ui.getDaySize().width);
        assertEquals(dayBounds.height, ui.getDaySize().height);
    }
    
    public void testMonthBoundsFromLogicalRToL() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        // second row, first column in absolute coordinates
        Rectangle monthBounds11 = ui.getMonthBoundsAtLocation(
                20, 
                monthBounds.y + 20 + monthBounds.height);
        // second row, second column in logical coordinates
        assertEquals(monthBounds11, ui.getMonthBounds(1, 1));
    }

    public void testMonthBoundsFromLogicalLToR() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        // second row, second column
        Rectangle monthBounds11 = ui.getMonthBoundsAtLocation(
                monthBounds.x + 20 + monthBounds.width, 
                monthBounds.y + 20 + monthBounds.height);
        // second row, second column in logical coordinates
        assertEquals(monthBounds11, ui.getMonthBounds(1, 1));
    }
    
    /**
     * Sanity test: inexpected pass if the realized frame isn't visible.
     */
    public void testFirstMonthLocation() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        assertEquals(monthBounds.getLocation(), ui.calendarGrid.getLocation());
        assertNull("no hit - bounds must be null", ui.getMonthBoundsAtLocation(19, 20));
    }
 
    
    /**
     * Test day at location
     */
    public void testDayAtLocationLToR() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        Rectangle dayBounds = ui.getDayBoundsAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + ui.getMonthHeaderHeight() + 2); 
        // first column in second non-header row
        Date date = ui.getDayAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + ui.getMonthHeaderHeight() + 2 * dayBounds.height + 2);
        // the ui's calendar is configured to the first displayed day
        Calendar uiCalendar = ui.getCalendar();
        uiCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        CalendarUtils.startOfWeek(uiCalendar);
        assertEquals("first logical column in LToR", uiCalendar.getTime(), date);
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
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        Rectangle dayBounds = ui.getDayBoundsAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + ui.getMonthHeaderHeight() + 2); 
        // first column in second non-header row
        Date date = ui.getDayAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + ui.getMonthHeaderHeight() + 2 * dayBounds.height + 2); 
        Date endOfWeek = CalendarUtils.endOfWeek(ui.getCalendar(), date);
        Calendar uiCalendar = ui.getCalendar(ui.getMonthAtLocation(20, 20));
        uiCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        CalendarUtils.endOfWeek(uiCalendar);
        assertEquals("first day in first week", uiCalendar.getTime(), endOfWeek); 
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
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        // same for LToR
        Date date = ui.getDayAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + ui.getMonthHeaderHeight() + 2); 
        assertNull("hitting days-of-week must return null calendar", date);
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
        // first column first row
        Rectangle dayBoundsRToL = uiRToL.getDayBoundsAtLocation(
                monthBounds.x + 2, monthBounds.y + uiRToL.getMonthHeaderHeight()); 
        // same for LToR
        Rectangle dayBoundsLToR = uiLToR.getDayBoundsAtLocation(
                monthBounds.x + 2, monthBounds.y + uiLToR.getMonthHeaderHeight()); 
        assertEquals("day bounds must be independent of orientation", 
                dayBoundsLToR, dayBoundsRToL);
        assertEquals(monthBounds.x, dayBoundsLToR.x);
        assertEquals(monthBounds.y + uiLToR.getMonthHeaderHeight(), dayBoundsLToR.y);
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
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        Rectangle dayBounds = ui.getDayBoundsAtLocation(
                monthBounds.x + 2, monthBounds.y + ui.getMonthHeaderHeight() +2); 

        // first column near bottom
        Point dayInGrid = ui.getDayGridPositionAtLocation(
                monthBounds.x + 2, 
                monthBounds.y + ui.getMonthHeaderHeight() + dayBounds.height + 2); 
       
        assertEquals("first row", 0, dayInGrid.y);
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
    public void testDayGridPositionMonthHeaderHitLToR() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        assertNull("hit in header must return null grid position", 
                ui.getDayGridPositionAtLocation(monthBounds.x + 2, monthBounds.y + 2));
    }
    
    /**
     * day grid returns null for hitting month header.
     */
    public void testDayGridPositionMonthHeaderHitRToL() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.RIGHT_TO_LEFT);
        Rectangle monthBounds = ui.getMonthBoundsAtLocation(20, 20);
        assertNull("hit in header must return null grid position", 
                ui.getDayGridPositionAtLocation(monthBounds.x + 2, monthBounds.y + 2));
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
        Date month = uiLToR.getMonth(0, 0);
        Calendar first = uiLToR.getCalendar();
        int monthField = first.get(Calendar.MONTH);
        first.setTime(month);
        assertEquals(monthField, first.get(Calendar.MONTH));
        Date monthRL = uiRToL.getMonth(0, 0);
        first.setTime(monthRL);
        assertEquals("logical coordinates must be independent of orientation",
                monthField, first.get(Calendar.MONTH));
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
     * coordinate mapping: monthBounds in pixel.
     * 
     */
    public void testMonthHeaderBoundsAtLocation() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        JXFrame frame = new JXFrame();
        frame.add(monthView);
        frame.pack();
        BasicMonthViewUI ui = (BasicMonthViewUI) monthView.getUI();
        Rectangle monthBoundsLToR = ui.getMonthHeaderBoundsAtLocation(20, 20);
        assertEquals("", ui.getMonthHeaderHeight(), monthBoundsLToR.height);
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
     * Returns the ui of a realized JXMonthView with
     * given componentOrientation and showingWeekNumbers flag.
     * It's prefColumns/Rows are set to 2. The first displayedDate is 
     * 20. Feb. 2008 (to have fixed leading/trailing dates)
     * 
     * The frame is packed and it's size extended by 40, 40 to
     * give a slight off-position (!= 0) of the months shown. 
     * 
     * 
     * 
     * NOTE: this must not be used in a headless environment.
     * 
     * @param co the componentOrientation to use
     * @return
     */
    private BasicMonthViewUI getRealizedMonthViewUI(ComponentOrientation co,
            boolean isShowingWeekNumbers) {
        JXMonthView monthView = new JXMonthView();
        monthView.setPreferredCols(2);
        monthView.setPreferredRows(2);
        monthView.setComponentOrientation(co);
        monthView.setShowingWeekNumber(isShowingWeekNumbers);
        Calendar calendar = monthView.getCalendar();
        calendar.set(2008, Calendar.FEBRUARY, 20);
        monthView.setFirstDisplayedDay(calendar.getTime());
        JXFrame frame = new JXFrame();
        frame.add(monthView);
        frame.pack();
        frame.setSize(frame.getWidth() + 40, frame.getHeight() + 40);
        frame.setVisible(true);
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
        Date date = monthView.getDayAtLocation(pref.width, pref.height);
        assertNotNull(date);
    }

    
    /**
     * Issue 711-swingx: today notify-only property.
     * Changed to read-only in monthView
     */
    public void testTodayUpdate() {
        JXMonthView monthView = new JXMonthView();
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
        JXMonthView monthView = new JXMonthView();
        Date first = monthView.getUI().getLastDisplayedDay();
        monthView.updateUI();
        assertEquals(first, monthView.getUI().getLastDisplayedDay());
    }


    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that lastDisplayedDate is unchanged.
     */
    public void testUpdateUILast() {
        final JXMonthView monthView = new JXMonthView();
        Date first = monthView.getUI().getLastDisplayedDay();
        monthView.updateUI();
        assertEquals(first, monthView.getUI().getLastDisplayedDay());
    };

    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that firstDisplayedDate is unchanged.
     */
    public void testUpdateUIFirstDate() {
        final JXMonthView monthView = new JXMonthView();
        Date first = ((BasicMonthViewUI) monthView.getUI()).getFirstDisplayedDay();
        monthView.updateUI();
        assertEquals(first, ((BasicMonthViewUI) monthView.getUI()).getFirstDisplayedDay());
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
        final JXMonthView monthView = new JXMonthView(cal.getTime());
        long first = ((BasicMonthViewUI) monthView.getUI()).getFirstDisplayedMonth();
        monthView.updateUI();
        assertEquals(first, ((BasicMonthViewUI) monthView.getUI()).getFirstDisplayedMonth());
    };
    

}
