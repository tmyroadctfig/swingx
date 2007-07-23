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
package org.jdesktop.swingx.calendar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SortedSet;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.DateSelectionListener;
import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.JXMonthView.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.test.XTestUtils;

/**
 * Test to expose known issues with JXMonthView.
 * 
 * @author Jeanette Winzenburg
 */
public class JXMonthViewIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXMonthViewIssues.class
            .getName());
    public static void main(String[] args) {
//      setSystemLF(true);
      JXMonthViewIssues  test = new JXMonthViewIssues();
      try {
//          test.runInteractiveTests();
        test.runInteractiveTests(".*Simple.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    public void interactiveSimple() {
        JXMonthView month = new JXMonthView();
        showInFrame(month, "simple");
    }
    /**
     * Issue ??-swingx: multiple selection with keyboard not working
     * Happens for standalone, okay for monthview in popup.
     * 
     *
     */
    public void interactiveMultipleSelectionWithKeyboard() {
        JXMonthView interval = new JXMonthView();
        interval.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        JXMonthView multiple = new JXMonthView();
        multiple.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        // for comparison: single interval in popup is working
        JXDatePicker picker = new JXDatePicker();
        JXMonthView intervalForPicker = new JXMonthView();
        intervalForPicker.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        picker.setMonthView(intervalForPicker);
        
        JComponent comp = new JPanel();
        comp.add(interval);
        comp.add(multiple);
        comp.add(picker);
        showInFrame(comp, "select interval with keyboard");
        
    }
    /**
     * Issue
     * plus: 
     * trying to understand standalone MonthView events.
     * first is single selection, second is single interval, 
     * third multipleInterval.
     *
     */
    public void interactiveMonthViewEvents() {
        JXMonthView monthView = new JXMonthView();
        JXMonthView interval = new JXMonthView();
        interval.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        JXMonthView multiple = new JXMonthView();
        multiple.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action from: " + e.getSource().getClass().getName() + 
                        "\n" + e);
            }
            
        };
        monthView.addActionListener(l);
        interval.addActionListener(l);
        multiple.addActionListener(l);
        DateSelectionListener d = new DateSelectionListener() {

            public void valueChanged(DateSelectionEvent ev) {
                LOG.info("got selection from: " + ev.getSource().getClass().getName() + 
                        "\n" + ev);
            }
            
        };
        monthView.getSelectionModel().addDateSelectionListener(d);
        interval.getSelectionModel().addDateSelectionListener(d);
        multiple.getSelectionModel().addDateSelectionListener(d);
        
        JXDatePicker picker = new JXDatePicker();
        JXMonthView intervalForPicker = new JXMonthView();
        intervalForPicker.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        picker.setMonthView(intervalForPicker);
        
        JComponent comp = new JPanel();
        comp.add(monthView);
        comp.add(interval);
        comp.add(multiple);
        comp.add(picker);
        showInFrame(comp, "events from monthView");
    }
    
//----------------------
    
    /**
    *
    * Okay ... looks more like a confusing (me!) doc: the date
    * in the constructor is not the selection, but the date
    * to use for the first display. Hmm ...
    */
   public void testMonthViewInitialSelection() {
       JXMonthView monthView = new JXMonthView(new GregorianCalendar(2007, 6, 28).getTimeInMillis());
       SortedSet<Date> selection = monthView.getSelection();
       Date other = selection.isEmpty() ? null : selection.first();
       assertNotNull(other);
   }

   /**
    * 
    * no invariant for the monthView's calender
    * monthViewUI at some places restores to firstDisplayedDay, why?
    *
    */
   public void testCalendar() {
      JXMonthView monthView = new JXMonthView();
      assertEquals(1, monthView.getCalendar().get(Calendar.DATE));
      Date first = new Date(monthView.getFirstDisplayedDate());
      assertEquals(first, monthView.getCalendar().getTime());
      Date date = XTestUtils.getCleanedToday(10);
      monthView.addSelectionInterval(date , date);
      assertEquals(first, monthView.getCalendar().getTime());
      monthView.isSelectedDate(new Date().getTime());
      assertEquals(first, monthView.getCalendar().getTime());
   }

}
