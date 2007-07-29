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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.DateSelectionListener;
import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.calendar.JXMonthView.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionEvent;

/**
 * Test to expose known issues with JXMonthView.
 * 
 * @author Jeanette Winzenburg
 */
public class JXMonthViewVisualCheck extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXMonthViewVisualCheck.class
            .getName());
    public static void main(String[] args) {
//      setSystemLF(true);
      JXMonthViewVisualCheck  test = new JXMonthViewVisualCheck();
      try {
          test.runInteractiveTests();
//        test.runInteractiveTests(".*Move.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }


    /**
     * Issue #563-swingx: arrow keys active even if not focused.
     * focus the button and use the arrow keys: selection moves.
     * Reason was that the WHEN_IN_FOCUSED_WINDOW key bindings
     * were always installed. 
     * 
     * Fixed by dynamically bind/unbind component input map bindings
     * based on the JXMonthView's componentInputMapEnabled property.
     *
     */
    public void interactiveMistargetedKeyStrokes() {
        JXMonthView month = new JXMonthView();
        JComponent panel = new JPanel();
        panel.add(new JButton("something to focus"));
        panel.add(month);
        showInFrame(panel, "default - for debugging only");
    }
    
    /**
     * Issue #563-swingx: arrow keys active even if not focused.
     * focus the button and use the arrow keys: selection moves.
     *
     * Fixed by dynamically bind/unbind component input map bindings
     * based on the JXMonthView's componentInputMapEnabled property.
     */
    public void interactiveMistargetedKeyStrokesPicker() {
        JXMonthView month = new JXMonthView();
        JComponent panel = new JPanel();
        JXDatePicker button = new JXDatePicker();
        panel.add(button);
        panel.add(month);
        showInFrame(panel, "default - for debugging only");
    }
    
    /**
     * Informally testing adjusting property on mouse events.
     * 
     * Hmm .. not formally testable without mocks/ui unit tests?
     *
     */
    public void interactiveAdjustingOnMouse() {
        final JXMonthView month = new JXMonthView();
        // we rely on being notified after the ui delegate ... brittle.
        MouseAdapter m = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                LOG.info("pressed - expect true " + month.getSelectionModel().isAdjusting());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                LOG.info("released - expect false" + month.getSelectionModel().isAdjusting());
            }
            
        };
        month.addMouseListener(m);
        showInFrame(month, "Mouse and adjusting - state on pressed/released");
    }

    /**
     * Issue #555-swingx: multiple selection with keyboard not working
     * Happens for standalone, okay for monthview in popup.
     * 
     * Fixed as a side-effect of cleanup of input map bindings. 
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
     * Issue #??-swingx: esc/enter does not always fire actionEvent.
     * 
     * Fixed: committing/canceling user gestures always fire.
     * 
     * Open: mouse-gestures?
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
        // JW: this picker comes up with today - should have taken the
        // empty selection (which it does the unit test)
        picker.setMonthView(intervalForPicker);
        
        JComponent comp = new JPanel();
        comp.add(monthView);
        comp.add(interval);
        comp.add(multiple);
        comp.add(picker);
        JXFrame frame = showInFrame(comp, "events from monthView");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");

    }
    
//----------------------
    

}
