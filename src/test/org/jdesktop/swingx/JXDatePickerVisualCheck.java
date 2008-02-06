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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
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

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;

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
            test.runInteractiveTests("interactive.*Keep.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue #568-swingx: DatePicker must not reset time fields.
     * 
     * Behaviour defined by selection model of monthView. While the default 
     * (DaySelectionModel) normalizes the dates to the start of the day in the
     * model's calendar coordinates, a SingleDaySelectionModel keeps the date as-is.
     * For now, need to explicitly set. 
     */
    public void interactiveKeepTimeFields() {
        final JXDatePicker picker = new JXDatePicker();
        SingleDaySelectionModel selectionModel = new SingleDaySelectionModel();
        picker.getMonthView().setSelectionModel(selectionModel);
        picker.setDate(new Date());
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL);
        picker.setFormats(format);
        final JFormattedTextField field = new JFormattedTextField(format);
        field.setValue(picker.getDate());
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    field.setValue(evt.getNewValue());
                }
                
            }
            
        };
        picker.addPropertyChangeListener(l);
        Action setDate = new AbstractActionExt("set date") {

            public void actionPerformed(ActionEvent e) {
                picker.setDate(new Date());
                
            }
            
        };
        JComponent box = Box.createHorizontalBox();
        box.add(picker);
        box.add(field);
        JXFrame frame = wrapInFrame(box, "time fields");
        addAction(frame, setDate);
        frame.pack();
        frame.setVisible(true);
        
    }

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
                picker.updateUI();
                custom = !custom;
            }
            
        };
        addAction(frame, action);
        frame.pack();
    };
    
    
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
     * Here: instantiate the picker with a non-default locale. 
     */
    public void interactiveLocaleConstructor() {
        JComponent comp = new JPanel();
        addDatePickerWithLocaleConstructor(comp, Locale.US);
        addDatePickerWithLocaleConstructor(comp, Locale.UK);
        addDatePickerWithLocaleConstructor(comp, Locale.GERMAN);
        addDatePickerWithLocaleConstructor(comp, Locale.ITALIAN);
        showInFrame(comp, "Localized DatePicker: constructor");
    }

    private void addDatePickerWithLocaleConstructor(JComponent comp, Locale uk) {
        JXDatePicker datePicker = new JXDatePicker(uk);
        comp.add(new JLabel(uk.getDisplayName()));
        comp.add(datePicker);
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
        showInFrame(comp, "DatePicker takes default Locale");
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
        Action toggleLinkPanel = new AbstractAction("toggleLinkPanel <-> null") {

            public void actionPerformed(ActionEvent e) {
                boolean hasLinkPanel = picker.getLinkPanel() != null;
                picker.setLinkPanel(hasLinkPanel ? null : panel);
            }
            
        };
        addAction(frame, toggleLinkPanel);
        frame.pack();
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
        
        JXFrame frame = showInFrame(panel, "Compare action events: keyboard/mouse");
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
        
        JXFrame frame = showInFrame(panel, "Compare action events: programmatic change");
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
        showInFrame(picker, "null date in picker");
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
        assertNull(picker.getDate());
        showInFrame(picker, "empty selection in monthView");
        assertNull(picker.getDate());
    }
    
    public void interactiveDatePickerDisplay() {
        JXDatePicker datePicker = new JXDatePicker();
        showInFrame(datePicker, "show date picker");
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
