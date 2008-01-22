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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DatePickerFormatter;
import org.jdesktop.swingx.calendar.DateSelectionModel;
import org.jdesktop.swingx.calendar.DateUtils;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;

/**
 * Known issues of <code>JXDatePicker</code> and picker related 
 * formats.
 * 
 * @author Jeanette Winzenburg
 */
public class JXDatePickerIssues extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXDatePickerIssues.class
            .getName());
    public static void main(String[] args) {
        setSystemLF(true);
        JXDatePickerIssues  test = new JXDatePickerIssues();
        try {
            test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*UpdateUI.*");
//          test.runInteractiveTests("interactive.*Visible.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }


    private Calendar calendar;

    /**
     * Compare picker and combo behaviour on toggle lf.
     * 
     * Not really comparable: the combo has complete control over the popup, re-creates
     * both popup and content on install.
     * 
     */
    public void interactiveUpdateUIPickerCompareCombo() {
        JXDatePicker picker = new JXDatePicker();
        JComboBox box = new JComboBox(new Object[] {"one", "twooooooo", "threeeeeeeeeeee", "GOOO!"});
        box.setEditable(true);
        JComponent comp = new JPanel();
        comp.add(picker);
        comp.add(box);
        JXFrame frame = wrapInFrame(comp, "compare combo <-> picker", true);
        frame.setVisible(true);
    }
    
    
    /**
     * Issue #709-swingx: DatePicker should have empty constructor which doesn't select.
     * 
     * Plus deprecate constructors with long - replace by Date parameters.
     * Deprecate other methods taking long.
     */
    public void interactiveNullDate() {
        JComponent comp = Box.createVerticalBox();
        comp.add(new JLabel("setDate(null)"));
        JXDatePicker picker = new JXDatePicker();
        picker.setDate(null);
        comp.add(picker);
        comp.add(new JLabel("initial -1"));
        comp.add(new JXDatePicker(-1));
        showInFrame(comp, "null date");
    }

    /**
     * Issue #606-swingx: keybindings in monthView popup not working 
     * in InternalFrame.
     * 
     * Looks like a problem with componentInputMaps in internalFrame:
     * the registered keys are eaten somewhere (f.i. bind f1 for left is okay)
     *  
     */
    public void interactiveKeyBindingInternalFrame() {
        JXDatePicker picker = new JXDatePicker();
        JInternalFrame iFrame = new JInternalFrame("iFrame", true, true, true, true) {

            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                    int condition, boolean pressed) {
                // hook for debugging
                return super.processKeyBinding(ks, e, condition, pressed);
            }
            
        };
        JComponent box = Box.createVerticalBox();
        box.add(picker);
        box.add(new JLabel("where????"));
        iFrame.getContentPane().add(box);
        iFrame.pack();
        iFrame.setVisible(true);
        JDesktopPane desktop = new JDesktopPane() {

            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                    int condition, boolean pressed) {
                // hook for debugging
                return super.processKeyBinding(ks, e, condition, pressed);
            }
            
        };
        desktop.add(iFrame);
        JInternalFrame other = new JInternalFrame("other", true, true, true, true);
        other.add(new JLabel("Dummy .... we want it!"));
        other.pack();
        other.setVisible(true);
        desktop.add(other);
        
        JXFrame frame = wrapInFrame(desktop, "InternalFrame keybinding");
        frame.setSize(400, 300);
        frame.setVisible(true);
    }
    
    /**
     * Issue #577-swingx: JXDatePicker focus cleanup.
     * 
     * PENDING JW: check if this is fixed!
     */
    public void interactiveFocusOnTogglePopup() {
        JXDatePicker picker = new JXDatePicker();
        FocusListener l = new FocusListener() {

            public void focusGained(FocusEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void focusLost(FocusEvent e) {
                if (e.isTemporary()) return;
                LOG.info("focus lost from editor");
            }};
        picker.getEditor().addFocusListener(l);    
        JComboBox box = new JComboBox(new String[] {"one", "twos"});
//        box.setEditable(true);
        JComponent panel = new JPanel();
        panel.add(box);
        panel.add(picker);
        JXFrame frame = showInFrame(panel, "FocusEvents on editor");
        frame.pack();
    }


    /**
     * Issue #725-swingx: review linkPanel/linkDate requirements.
     * 
     * link panel commit gesture (keystroke F5, double-click) must 
     * commit the date. 
     * 
     * Update the linkDate must 
     * - show in the LinkPanel. 
     * - open the popup in the month of the link date?
     *
     */
    public void interactiveCommitLinkPanelAction() {
        final JXDatePicker picker = new JXDatePicker();
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("received: " + e + 
                        "\n adjusting must be false: " 
                        + picker.getMonthView().getSelectionModel().isAdjusting());
                
            }
            
        };
        picker.addActionListener(l);
        JXFrame frame = showInFrame(picker, "double-click on linkpanel must commit");
        Action nextDate = new AbstractAction("change linkdate") {

            public void actionPerformed(ActionEvent e) {
                picker.setLinkDate(DateUtils.getNextMonth(picker.getLinkDate()));
                
            }
            
        };
        addAction(frame, nextDate);
        frame.pack();
    }
    
    /**
     * Issue #565-swingx: occasionally, the popup isn't closed. 
     * to reproduce: open the picker's popup then click into
     * the comboBox. All is well if click into the textfield.
     *
     */
    public void interactiveClosePopup() {
        final JXDatePicker picker = new JXDatePicker();
        JComboBox box = new JComboBox(new String[] {"one", "twos"});
        box.setEditable(true);
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("received: " + e + 
                        "\n and not adjusting: " 
                        + picker.getMonthView().getSelectionModel().isAdjusting());
                
            }
            
        };
        picker.addActionListener(l);
        box.addActionListener(l);
        JComponent panel = new JPanel();
        panel.add(picker);
        panel.add(box);
        panel.add(new JTextField("textfield - click into with open popup"));
        JXFrame frame = showInFrame(panel, "closed?");
        // check if it's a lightweight vs. heavyweight problem
        frame.setSize(new Dimension(frame.getSize().width, 400));
    }
    
    /**
     * compare JFormattedTextField and JXDatePicker pref.
     * date is slightly cut. Looks like an issue 
     * of the formattedTextField.
     */
    public void interactivePrefSize() {
        JXDatePicker picker = new JXDatePicker();
        JFormattedTextField field = new JFormattedTextField(new DatePickerFormatter());
        field.setValue(picker.getDate());
        JComponent panel = new JPanel();
        panel.add(picker);
        panel.add(field);
        showInFrame(panel, "compare pref width");
    }

    /**
     * visual testing of selection constraints: upper/lower bounds.
     * 
     * Issue #567-swingx:
     * clicking into a unselectable in the popup clears the
     * selection - should revert to the last valid selection.
     * PENDING: better control the bounds ... 
     */
    public void interactiveBoundsDatePickerClickUnselectable() {
        JXDatePicker picker = new JXDatePicker();
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        picker.getMonthView().setUpperBound(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, - 20);
        picker.getMonthView().setLowerBound(calendar.getTime());
        showInFrame(picker, "click unselectable clears date");
    }




    /**
     * Sanity during fix #705-swingx: JXMonthView must not scroll in layoutContainer.
     * 
     * Here we have a selected date way into the future, to be sure it had to scroll.
     */
    public void interactiveVisibleMonth() {
        calendar.set(2008, Calendar.JULY, 15);
        final JXDatePicker picker = new JXDatePicker();
        picker.setDate(calendar.getTime());
        JXFrame frame = wrapInFrame(picker, "sanity - monthview shows selected");
        Action toggleWrapper = new AbstractAction("open popup") {

            public void actionPerformed(ActionEvent e) {
                Action togglePopup = picker.getActionMap().get("TOGGLE_POPUP");
                togglePopup.actionPerformed(null);
                CalendarUtils.startOfMonth(calendar);
                assertEquals(calendar.getTime(), new Date(picker.getMonthView().getFirstDisplayedDate()));

                
            }
            
        };
        addAction(frame, toggleWrapper);
        frame.pack();
        frame.setVisible(true);
    }
//-------------------- unit tests
 
    DateFormat longFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    /**
     * Issue #568-swingx: picker must respect selection model (as of time fields) 
     */
    public void testSetDateKeepsTime() {
        JXDatePicker picker = new JXDatePicker();
        picker.setDate(null);
        DateSelectionModel selectionModel = picker.getMonthView().getSelectionModel();
        assertTrue(selectionModel instanceof SingleDaySelectionModel);
        Date date = new Date();
        selectionModel.setSelectionInterval(date, date);
        Date first = selectionModel.getSelection().first();
        assertEquals("formats diff: " + (date.getTime() - first.getTime())
                , date, first);
    }
    
    /**
     * Issue #572-swingx: monthView must show linkDate on empty selection.
     *
     * Definition of picker.linkDate vs. monthView.todayInMillis missing.
     * PENDING: back out - say linkDate == today, not mutable by client code
     * but fixed to system?
     */
    public void testLinkDate() {
        JXDatePicker picker = new JXDatePicker();
        picker.setDate(null);
        long linkDate = picker.getLinkDate();
        long firstDisplayedDate = picker.getMonthView().getFirstDisplayedDate();
        assertSameMonth(linkDate, firstDisplayedDate);
        long nextDate = DateUtils.getNextMonth(DateUtils.getNextMonth(linkDate));
        picker.setLinkDate(nextDate);
        assertSameMonth(nextDate, picker.getMonthView().getFirstDisplayedDate());
    }
    /**
     * @param linkDate
     * @param firstDisplayedDate
     */
    private void assertSameMonth(long linkDate, long firstDisplayedDate) {
        calendar.setTimeInMillis(linkDate);
        int linkMonth = calendar.get(Calendar.MONTH);
        calendar.setTimeInMillis(firstDisplayedDate);
        assertEquals(linkMonth, calendar.get(Calendar.MONTH));
        
    }

    /**
     * Issue #724-swingx: picker must notify about timezone changes.
     * Here: change the timezone on the monthView - can't guarantee the notification.
     * At least not without hacks...
     */
    public void testTimeZoneChangeNotificationChangeOnMonthView() {
        JXDatePicker picker = new JXDatePicker();
        TimeZone timeZone = picker.getTimeZone();
        TimeZone alternative = getSafeAlternativeTimeZone(timeZone);
        PropertyChangeReport report = new PropertyChangeReport();
        picker.addPropertyChangeListener(report);
        picker.getMonthView().setTimeZone(alternative);
        TestUtils.assertPropertyChangeEvent(report, "timeZone", timeZone, alternative, false);
    }
    
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
    

    /**
     * Returns a timezone different from the given.
     * @param defaultZone
     * @return
     */
    private TimeZone getSafeAlternativeTimeZone(TimeZone defaultZone) {
        TimeZone alternative = TimeZone.getTimeZone("GMT-6");
        // sanity
        assertNotNull(alternative);
        if (alternative.equals(defaultZone)) {
            alternative = TimeZone.getTimeZone("GMT-7");
            // paranoid ... but shit happens
            assertNotNull(alternative);
            assertFalse(alternative.equals(defaultZone));
        }
        return alternative;
    }



    

    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }
    
    
}
