/*
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jdesktop.swingx.JXMonthView.SelectionMode;
import org.jdesktop.swingx.calendar.DateUtils;

/**
 * Simple tests to ensure that the {@code JXDatePicker} can be instantiated and
 * displayed.<p>
 * 
 * JW: being lazy - added visuals for <code>JXMonthView</code> as well.
 * 
 * @author Karl Schaefer
 */
public class JXDatePickerVisualCheck extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger
            .getLogger(JXDatePickerVisualCheck.class.getName());
    @SuppressWarnings("unused")
    private Calendar calendar;
    /** flag to decide if the menubar should be created */
    private boolean showMenu;

    public JXDatePickerVisualCheck() {
        super("JXDatePicker Test");
    }

    public static void main(String[] args) throws Exception {
         setSystemLF(true);
        JXDatePickerVisualCheck test = new JXDatePickerVisualCheck();
        
        try {
//            test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Locale.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue #665-swingx: make JXDatePicker Locale-aware.
     * 
     * Tests reaction to default locales set via both JComponent.setDefault and
     * Locale.setDefault. Going that way, catches the locales fine.
     * 
     * PENDING: Issue #681-swingx - the first row of days in the monthview
     * overlaps with the day names for locales which have the monday as the 
     * first day of week. Here is okay, happens only if Locale is given in 
     * constructor.
     */
    public void interactiveLocaleDefault() {
        JComponent comp = new JPanel();
        Locale old = addDatePickerWithLocale(comp, Locale.UK);
        addDatePickerWithLocale(comp, Locale.FRANCE);
        addDatePickerWithLocale(comp, Locale.US);
        addDatePickerWithLocale(comp, Locale.GERMAN);
        addDatePickerWithLocale(comp, Locale.ITALIAN);
        showInFrame(comp, "Localized DatePicker");
        setLocale(old);
    }

    private Locale addDatePickerWithLocale(JComponent comp, Locale uk) {
        Locale old = setLocale(uk);
        JXDatePicker datePicker = new JXDatePicker();
        comp.add(new JLabel(uk.getDisplayName()));
        comp.add(datePicker);
        return old;
    }

    private Locale setLocale(Locale locale) {
        Locale old = JComponent.getDefaultLocale();
        JComponent.setDefaultLocale(locale);
        Locale.setDefault(locale);
        return old;
    }


    /**
     * Issue #572-swingx: monthView must show linkDate on empty selection.
     *
     * add month to linkDate, popup must show month containing link date.
     * Note: client code using the link date for something different than today
     * must take care to update the message format (PENDING: is that possible?)
     */
    public void interactiveLinkDate() {
        final JXDatePicker picker = new JXDatePicker();
        picker.setDate(null);
        long linkDate = picker.getLinkDate();
        // add two months and set as new link date
        long nextDate = DateUtils.getNextMonth(DateUtils.getNextMonth(linkDate));
        picker.setLinkDate(nextDate);
        Action action = new AbstractAction("next linkdate month") {

            public void actionPerformed(ActionEvent e) {
                long linkDate = picker.getLinkDate();
                long nextDate = DateUtils.getNextMonth(DateUtils.getNextMonth(linkDate));
                picker.setLinkDate(nextDate);
                
            }
            
        };
        JXFrame frame = wrapInFrame(picker, "null selection and linkdate");
        addAction(frame, action);
        addMessage(frame, "incr linkDate and open popup: must show new linkMonth");
        frame.pack();
        frame.setVisible(true);
    }
    /**
     * Issue #577-swingx: JXDatePicker focus cleanup.
     * Before open: picker's editor should be focused.
     * After commit/cancel in popup: picker's editor should be focused.
     */
    public void interactiveFocusOnTogglePopup() {
        JXDatePicker picker = new JXDatePicker();
        final Action togglePopup = picker.getActionMap().get("TOGGLE_POPUP");
        Action toggle = new AbstractAction("togglePopup") {

            public void actionPerformed(ActionEvent e) {
                togglePopup.actionPerformed(null);
            }
            
        };
        JComboBox box = new JComboBox(new String[] {"one", "twos"});
//        box.setEditable(true);
        JComponent panel = new JPanel();
        panel.add(box);
        panel.add(picker);
        JXFrame frame = showInFrame(panel, "Focus on editor");
        addAction(frame, toggle);
        frame.pack();
    }

    /**
     * Issue #566-swingx: JXRootPane eats picker's popup esc.
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
        showInFrame(panel, "Escape key");
    }
    

    /**
     * visual check that toggling the panel adds/removes it
     * and installs the keybindings.
     *
     */
    public void interactiveLinkPanelSet() {
        final JXDatePicker picker = new JXDatePicker();
        final JPanel panel = picker.getLinkPanel();
        // initial null okay
        JXFrame frame = showInFrame(picker, "null panel");
        Action toggleLinkPanel = new AbstractAction("toggleLinkPanel") {

            public void actionPerformed(ActionEvent e) {
                boolean hasLinkPanel = picker.getLinkPanel() != null;
                picker.setLinkPanel(hasLinkPanel ? null : panel);
            }
            
        };
        addAction(frame, toggleLinkPanel);
        frame.pack();
    }
  
    /**
     * something weird's going on: the picker's date must be null
     * after setting a monthView with null selection. It is, until
     * shown?
     * Looks fixed during synch control cleanup in datePicker.
     */
    public void interactiveShowPickerSetMonthNull() {
        JXDatePicker picker = new JXDatePicker();
        JXMonthView intervalForPicker = new JXMonthView();
        intervalForPicker.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        picker.setMonthView(intervalForPicker);
        LOG.info("picker date before showing " + picker.getDate());
        assertNull(picker.getDate());
        showInFrame(picker, "initial null date");
        LOG.info("picker date after showing " + picker.getDate());
        assertNull(picker.getDate());

    }
    /**
     * Issue #235-swingx: action events
     * 
     * Compare textfield, formatted, picker, combo after keyboard.
     * 
     * TextField
     * - simple field fires on enter always
     * - formatted fire on enter if value had been edited
     *
     * ComboBox
     * - fires on enter always
     * - fires on click in dropdown
     * 
     * Calendar widgets after cleanup: 
     * 
     * Picker
     * - fires "datePickerCommit" on click (actually released) into monthView
     * - fires "datePickerCommit"/-"Cancel" on enter/escape, both in input field
     * and if popup is open
     * 
     * MonthView
     * - fires "monthViewCommit" on click (actually released)
     * - fires "monthViewCommit"/-"Cancel" on enter/esc 
     * 
     * 
     */
    public void interactiveActionEvent() {
        JXDatePicker picker = new JXDatePicker();
        JTextField simpleField = new JTextField("simple field");
        JFormattedTextField textField = new JFormattedTextField(DateFormat.getDateInstance());
        textField.setValue(new Date());
        JComboBox box = new JComboBox(new Object[] {"one", "two", "three"});
        box.setEditable(true);
        
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action from: " + e.getSource().getClass().getName() + 
                        "\n" + e);
            }
            
        };
        simpleField.addActionListener(l);
        textField.addActionListener(l);
        picker.addActionListener(l);
//        picker.getMonthView().addActionListener(l);
        box.addActionListener(l);
        JPanel panel = new JPanel();
        panel.add(simpleField);
        panel.add(textField);
        panel.add(picker);
        panel.add(box);
        
        JXFrame frame = showInFrame(panel, "trace action events: keyboard/mouse");
        frame.pack();
    }

    /**
     * Issue #235-swingx: action events
     * 
     * Compare textfield, formatted, picker and combo: programatic change.
     * - only combo fires
     * 
     */
    public void interactiveActionEventSetValue() {
        final JXDatePicker picker = new JXDatePicker();
//        picker.setDate(null);
        final JTextField simpleField = new JTextField("simple field");
        final JFormattedTextField textField = new JFormattedTextField(DateFormat.getDateInstance());
        textField.setValue(new Date());
        final JComboBox box = new JComboBox(new Object[] {"one", "two", "three"});
        box.setEditable(true);
        
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action from: " + e.getSource().getClass().getName() + 
                        "\n" + e);
            }
            
        };
        simpleField.addActionListener(l);
        textField.addActionListener(l);
        picker.addActionListener(l);
        picker.getMonthView().addActionListener(l);
        box.addActionListener(l);
        Action action = new AbstractAction("set new value") {
            int dayToAdd = 1;
            public void actionPerformed(ActionEvent e) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, dayToAdd++);
                Date date = cal.getTime();
                String text = DateFormat.getDateInstance().format(date);
                simpleField.setText(text);
                textField.setValue(date);
                picker.setDate(date);
                box.setSelectedItem(text);
            }
            
        };
        
        JPanel panel = new JPanel();
        panel.add(simpleField);
        panel.add(textField);
        panel.add(picker);
        panel.add(box);
        
        JXFrame frame = showInFrame(panel, "trace action events: programmatic change");
        addAction(frame, action);
        frame.pack();
    }


    /**
     * Issue #99-swingx: null date and opening popup forces selection.
     * Status? Looks fixed..
     * 
     * Sizing issue if init with null date
     */
    public void interactiveNullDate() {
        JXDatePicker picker = new JXDatePicker();
        picker.setDate(null);
        JPanel panel = new JPanel();
        panel.add(picker);
        showInFrame(panel, "null date");
    }

    /**
     * Issue #426-swingx: NPE on traversing 
     * 
     * example from bug report
     *
     */
    public void interactiveMonthViewTravers() {
        JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        JFrame frame = wrapInFrame(monthView, "show month view - travers");
        frame.pack();
        frame.setVisible(true);
        
    }
    
    public void interactiveDatePickerDisplay() {
        JXDatePicker datePicker = new JXDatePicker();
        JFrame frame = wrapInFrame(datePicker, "show date picker");
        frame.pack();
        frame.setVisible(true);
    }
    

    
    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }

    private static class SetPlafAction extends AbstractAction {
        private String plaf;
        
        public SetPlafAction(String name, String plaf) {
            super(name);
            this.plaf = plaf;
        }
        
        /**
         * {@inheritDoc}
         */
        public void actionPerformed(ActionEvent e) {
            try {
                Component c = (Component) e.getSource();
                Window w = null;
                
                for (Container p = c.getParent(); p != null; p = p instanceof JPopupMenu ? (Container) ((JPopupMenu) p)
                        .getInvoker() : p.getParent()) {
                    if (p instanceof Window) {
                        w = (Window) p;
                    }
                }
                
                UIManager.setLookAndFeel(plaf);
                SwingUtilities.updateComponentTreeUI(w);
                w.pack();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (UnsupportedLookAndFeelException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    private JMenuBar createMenuBar() {
        LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Set L&F");
        
        for (LookAndFeelInfo info : plafs) {
            menu.add(new SetPlafAction(info.getName(), info.getClassName()));
        }
        
        bar.add(menu);
        
        return bar;
    }
    
    public JXFrame wrapInFrame(JComponent component, String title) {
        JXFrame frame = super.wrapInFrame(component, title);
        if (showMenu) {
            frame.setJMenuBar(createMenuBar());
        }
        return frame;
    }
    


    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }
    
}
