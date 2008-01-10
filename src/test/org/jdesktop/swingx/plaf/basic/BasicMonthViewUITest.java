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

import java.util.Calendar;
import java.util.logging.Logger;

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
