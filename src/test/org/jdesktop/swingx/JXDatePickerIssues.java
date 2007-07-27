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
package org.jdesktop.swingx;

import java.util.Calendar;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

/**
 * Known issues of <code>JXDatePicker</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class JXDatePickerIssues extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXDatePickerIssues.class
            .getName());
    public static void main(String[] args) {
//        setSystemLF(true);
        JXDatePickerIssues  test = new JXDatePickerIssues();
        try {
            test.runInteractiveTests();
//          test.runInteractiveTests(".*Show.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }


    private Calendar calendar;

    /**
     * Issue ??-swingx: occasionally, the popup isn't closed. 
     * to reproduce: open the picker's popup then click into
     * the comboBox. All is well if click into the textfield.
     *
     */
    public void interactiveClosePopup() {
        JXDatePicker picker = new JXDatePicker();
        JComboBox box = new JComboBox(new String[] {"one", "twos"});
        box.setEditable(true);
        JComponent panel = new JPanel();
        panel.add(picker);
        panel.add(box);
        JXFrame frame = showInFrame(panel, "closed?");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");
    }
    
    /**
     * Issue ??-swingx: JXRootPane eats picker's popup esc.
     * to reproduce: open the picker's popup the press esc -
     * not closed. Same with combo is working.
     *
     */
    public void interactiveXRootPaneEatsEscape() {
        JXDatePicker picker = new JXDatePicker();
        JComboBox box = new JComboBox(new String[] {"one", "twos"});
        box.setEditable(true);
        JComponent panel = new JPanel();
        panel.add(picker);
        panel.add(box);
        @SuppressWarnings("unused")
        JXFrame frame = showInFrame(panel, "closed?");
        // JXRootPane eats esc 
//        frame.getRootPaneExt().getActionMap().remove("esc-action");
    }
    
    /**
     * compare JFormattedTextField and JXDatePicker pref.
     * date is slightly cut. Looks like an issue 
     * of the formattedTextField.
     */
    public void interactivePrefSize() {
//        ListSelectionModel l;
//        TreeSelectionModel t;
        JXDatePicker picker = new JXDatePicker();
        JFormattedTextField field = new JFormattedTextField(new JXDatePickerFormatter());
        field.setValue(picker.getDate());
        JComponent panel = new JPanel();
        panel.add(picker);
        panel.add(field);
        JXFrame frame = showInFrame(panel, "compare pref width");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");
    }

    /**
     * visual testing of selection constraints: upper/lower bounds.
     * 
     * Issue ??-swingx:
     * clicking into a unselectable in the popup clears the
     * selection - should revert to the last valid selection.
     */
    public void interactiveBounds() {
        JXDatePicker picker = new JXDatePicker();
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        // access the model directly requires to "clean" the date
        picker.getMonthView().setUpperBound(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, - 20);
        picker.getMonthView().setLowerBound(calendar.getTimeInMillis());
        JXFrame frame = showInFrame(picker, "lower/upper bounds");
        frame.pack();
    }


 
    
//-------------------- unit tests
    /**
     * test that selectionListener is uninstalled.
     * 
     * Hmm ... missing api or overshooting?
     */
    public void testSelectionListening() {
//        JXMonthView monthView = new JXMonthView();
//        int selectionListenerCount = monthView.getSelectionModel()).getListeners().length;
//        JXDatePicker picker = new JXDatePicker();
//        assertEquals("ui must have installed one listener", selectionListenerCount + 1, 
//                picker.getMonthView().getSelectionModel().getListeners().length);
//        picker.getUI().uninstallUI(picker);
//        assertEquals("", selectionListenerCount, 
//                picker.getMonthView().getSelectionModel().getListeners().length);
    }
    




    

    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }
    
    
}
