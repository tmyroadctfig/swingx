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
import java.util.Calendar;
import java.util.Locale;
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
import javax.swing.UIManager;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.DatePickerFormatter;
import org.jdesktop.swingx.calendar.DateUtils;
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
//            test.runInteractiveTests();
          test.runInteractiveTests("interactive.*UpdateUI.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }


    private Calendar calendar;

    /**
     * Issue #706-swingx: picker doesn't update monthView.
     * 
     */
    public void interactiveUpdateUIPickerMonthView() {
        final JXDatePicker picker = new JXDatePicker();
        JXFrame frame = showInFrame(picker, "picker update ui");
        Action action = new AbstractActionExt("toggleUI") {
            public void actionPerformed(ActionEvent e) {
                String uiClass = (String) UIManager.get(JXMonthView.uiClassID);
                boolean custom = uiClass.indexOf("Custom") > 0;
                if (!custom) {
                    UIManager.put(JXMonthView.uiClassID, "org.jdesktop.swingx.test.CustomMonthViewUI");
                } else {
                    UIManager.put(JXMonthView.uiClassID, null);
                }
//                picker.setMonthView(new JXMonthView());
                picker.updateUI();
                custom = !custom;
            }
            
        };
        addAction(frame, action);
        frame.pack();
    };
    
    /**
     * Issue #706-swingx: picker doesn't update monthView.
     * 
     */
    public void interactiveUpdateUIMonthView() {
        final JXMonthView monthView = new JXMonthView();
//        picker.setSelectedDate(new Date());
        final JXFrame frame = showInFrame(monthView, "MonthView update ui");
        Action action = new AbstractActionExt("toggleUI") {
            public void actionPerformed(ActionEvent e) {
                String uiClass = (String) UIManager.get(JXMonthView.uiClassID);
                boolean custom = uiClass.indexOf("Custom") > 0;
                if (!custom) {
                    UIManager.put(JXMonthView.uiClassID, "org.jdesktop.swingx.test.CustomMonthViewUI");
                } else {
                    UIManager.put(JXMonthView.uiClassID, null);
                }
                monthView.updateUI();
//                SwingUtilities.updateComponentTreeUI(frame);
                custom = !custom;
            }
            
        };
        addAction(frame, action);
        frame.pack();
    };
    
    /**
     * Issue #??-swingx: DatePicker should have empty constructor which doesn't select.
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
     * Issue #665-swingx: make JXDatePicker Locale-aware.
     * 
     * Here: instantiate the picker with a non-default locale. The 
     * LinkPanel is okay, if the UK locale is used _before_
     * the US locale (on a machine with default German). The other way 
     * round the messageFormat for the 
     * US linkPanel is German.
     */
    public void interactiveLocaleSet() {
        JComponent comp = new JPanel();
        comp.add(new JXDatePicker());
        addDatePickerWithLocaleSet(comp, Locale.US);
        addDatePickerWithLocaleSet(comp, Locale.UK);
        addDatePickerWithLocaleSet(comp, Locale.GERMAN);
        addDatePickerWithLocaleSet(comp, Locale.ITALIAN);
        showInFrame(comp, "Localized DatePicker: setLocale");
    }

    private void addDatePickerWithLocaleSet(JComponent comp, Locale uk) {
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setLocale(uk);
        comp.add(new JLabel(uk.getDisplayName()));
        comp.add(datePicker);
    }

    /**
     * Issue #665-swingx: make JXDatePicker Locale-aware.
     * 
     * Here: instantiate the picker with a non-default locale. The 
     * LinkPanel is okay, if the UK locale is used _before_
     * the US locale (on a machine with default German). The other way 
     * round the messageFormat for the 
     * US linkPanel is German.
     */
    public void interactiveLocaleConstructor() {
        JComponent comp = new JPanel();
        addDatePickerWithLocale(comp, Locale.US);
        addDatePickerWithLocale(comp, Locale.UK);
        addDatePickerWithLocale(comp, Locale.GERMAN);
        addDatePickerWithLocale(comp, Locale.ITALIAN);
        showInFrame(comp, "Localized DatePicker: constructor");
    }

    private void addDatePickerWithLocale(JComponent comp, Locale uk) {
        JXDatePicker datePicker = new JXDatePicker(uk);
        comp.add(new JLabel(uk.getDisplayName()));
        comp.add(datePicker);
    }

//    private Locale setLocale(Locale locale) {
//        Locale old = JComponent.getDefaultLocale();
//        JComponent.setDefaultLocale(locale);
//        Locale.setDefault(locale);
//        return old;
//    }

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
//        picker.setDate(null);
        // initially
//        picker.setLinkDate(DateUtils.getNextMonth(picker.getLinkDate()));
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
     * Issue #567-swingx: JXDatepicker - clicking on unselectable date clears
     * picker's selection.
     * 
     * Here: visualize JXMonthView's behaviour. It fires a commit ... probably the 
     * wrong thing to do?. 
     * PENDING: better control the bounds ... 
     * PENDING: move into monthView after rename
     */
    public void interactiveBoundsMonthViewClickUnselectable() {
        JXMonthView monthView = new JXMonthView();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 7);
        monthView.setLowerBound(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        monthView.setUpperBound(calendar.getTime());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action " + e);
                
            }
            
        };
        monthView.addActionListener(l);
        showInFrame(monthView, "click unselectable fires ActionEvent");
    }

    /**
     * Issue #657-swingx: JXMonthView - unintuitive week-wise navigation with bounds
     * 
     * In a month, keyboard navigation beyond the upper/lower bound is prevented.
     * There's a leak in the region of the leading/trailing dates 
     * when navigating week-wise. 
     * 
     * PENDING: move into monthView after rename
     */
    public void interactiveBoundsNavigateBeyond() {
        JXMonthView monthView = new JXMonthView();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        // access the model directly requires to "clean" the date
        monthView.setLowerBound(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 27);
        monthView.setUpperBound(calendar.getTime());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action " + e);
                
            }
            
        };
        monthView.addActionListener(l);
        showInFrame(monthView, "navigate beyond bounds");
    }

    
    /**
     * Issue #657-swingx: JXMonthView - unintuitive week-wise navigation with bounds
     * 
     * Can't navigate at all if today is beyound the bounds
     * PENDING: move into monthView after rename
     */
    public void interactiveBoundsNavigateLocked() {
        JXMonthView monthView = new JXMonthView();
        // same time as monthView's today
        Calendar calendar = Calendar.getInstance();
        // set upper bound a week before today, 
        // to block navigation into all directions
        calendar.add(Calendar.DAY_OF_MONTH, -8);
        monthView.setUpperBound(calendar.getTime());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action " + e);
                
            }
            
        };
        monthView.addActionListener(l);
        showInFrame(monthView, "navigate: locked for today beyond bounds");
    }

//-------------------- unit tests
 
    /**
     * Issue #??-swingx: picker must notify about timezone changes.
     * Can it guarantee the notification? Not really, if changed in the
     * underlying monthView? 
     */
    public void testTimeZoneChangeNotification() {
        JXDatePicker picker = new JXDatePicker();
        TimeZone timeZone = picker.getTimeZone();
        TimeZone alternative = getSafeAlternativeTimeZone(timeZone);
        PropertyChangeReport report = new PropertyChangeReport();
        picker.addPropertyChangeListener(report);
        picker.setTimeZone(alternative);
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
