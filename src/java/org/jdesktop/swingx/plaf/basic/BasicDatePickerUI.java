/*
 * Copyright 2005 Sun Microsystems, Inc., 4150 Network Circle,
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
 */
package org.jdesktop.swingx.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;

import org.jdesktop.swingx.DateSelectionListener;
import org.jdesktop.swingx.DateSelectionModel;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXDatePickerFormatter;
import org.jdesktop.swingx.calendar.JXMonthView;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.plaf.DatePickerUI;

/**
 * @author Joshua Outwater
 */
public class BasicDatePickerUI extends DatePickerUI {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(BasicDatePickerUI.class
            .getName());
    
    protected JXDatePicker datePicker;
    private JButton popupButton;
    private BasicDatePickerPopup popup;
    private Handler handler;
    protected PropertyChangeListener propertyChangeListener;
    protected MouseListener mouseListener;
    protected MouseMotionListener mouseMotionListener;

    private PropertyChangeListener editorListener;

    private DateSelectionListener selectionListener;

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new BasicDatePickerUI();
    }

    @Override
    public void installUI(JComponent c) {
        datePicker = (JXDatePicker)c;
        datePicker.setLayout(createLayoutManager());
        installComponents();
        installDefaults();
        installKeyboardActions();
        installListeners();
    }

    @Override
    public void uninstallUI(JComponent c) {
        uninstallListeners();
        uninstallKeyboardActions();
        uninstallDefaults();
        uninstallComponents();
        datePicker.setLayout(null);
        datePicker = null;
    }

    protected void installComponents() {
        JFormattedTextField editor = datePicker.getEditor();
        if (editor == null || editor instanceof UIResource) {
            // we are not yet listening ...
            datePicker.setEditor(createEditor());
        }
        // JW: duplicating code: updateFromEditor does a similar job
        // albeit more complete job
        // but at this time the listeners are not yet ready ... hmm.
        datePicker.add(datePicker.getEditor());
        // #551-swingx: editor's value not updated after lf change
        datePicker.getEditor().setValue(datePicker.getDate());
        
        popupButton = createPopupButton();
        if (popupButton != null) {
            // this is a trick to get hold of the client prop which
            // prevents closing of the popup
            JComboBox box = new JComboBox();
            Object preventHide = box.getClientProperty("doNotCancelPopup");
            popupButton.putClientProperty("doNotCancelPopup", preventHide);
            datePicker.add(popupButton);
        }
    }

    protected void uninstallComponents() {
        JFormattedTextField editor = datePicker.getEditor();
        // JW: when can that be?
        if (editor != null) {
            // JW: shouldn't all listener dereg be done in uninstallListeners() ?
            // haha -- at this moment the handler is nulled and
            // we create a new one for removal!
//            editor.removePropertyChangeListener(getHandler());
            datePicker.remove(editor);
        }

        if (popupButton != null) {
            datePicker.remove(popupButton);
            popupButton = null;
        }
    }

    protected void installDefaults() {

    }

    protected void uninstallDefaults() {

    }

    protected void installKeyboardActions() {
        KeyStroke spaceKey =
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false);

        InputMap inputMap = popupButton.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(spaceKey, "TOGGLE_POPUP");

        ActionMap actionMap = popupButton.getActionMap();
        actionMap.put("TOGGLE_POPUP", new TogglePopupAction());
    }

    protected void uninstallKeyboardActions() {

    }

    protected void installListeners() {
        propertyChangeListener = createPropertyChangeListener();
        mouseListener = createMouseListener();
        mouseMotionListener = createMouseMotionListener();
        editorListener = createEditorListener();
        selectionListener = createSelectionListener();
        
        datePicker.addPropertyChangeListener(propertyChangeListener);
        
        
        if (popupButton != null) {
            // JW: which property do we want to monitor?
            popupButton.addPropertyChangeListener(propertyChangeListener);
            popupButton.addMouseListener(mouseListener);
            popupButton.addMouseMotionListener(mouseMotionListener);
        }
        updateEditorListeners(null);
        updateFromMonthViewChanged(null);

    }
    protected void uninstallListeners() {
        datePicker.removePropertyChangeListener(propertyChangeListener);
        // JW: when can that be null?
        if (datePicker.getEditor() != null) {
            datePicker.getEditor().removePropertyChangeListener(editorListener);
        }
        if (datePicker.getMonthView() != null) {
            datePicker.getMonthView().getSelectionModel().removeDateSelectionListener(selectionListener);
        }
        if (popupButton != null) {
            popupButton.removePropertyChangeListener(propertyChangeListener);
            popupButton.removeMouseListener(mouseListener);
            popupButton.removeMouseMotionListener(mouseMotionListener);
        }

        propertyChangeListener = null;
        mouseListener = null;
        mouseMotionListener = null;
        handler = null;
        editorListener = null;
        selectionListener = null;
    }

//------------------ listener creation
    /**
     * Creates and returns the listener for the dateSelection.
     * 
     * @return
     */
    protected DateSelectionListener createSelectionListener() {
        DateSelectionListener l = new DateSelectionListener() {

            public void valueChanged(DateSelectionEvent ev) {
                updateDateFromSelection(ev.getEventType(), ev.isAdjusting());
            }
            
        };
        return l;
    }

    /**
     * @return a propertyChangeListener dedicated to editor property changes
     */
    protected PropertyChangeListener createEditorListener() {
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("value".equals(evt.getPropertyName())) {
                    updateFromValueChanged((Date) evt.getOldValue(), (Date) evt.getNewValue());
                }
                
            }
            
        };
        return l;
    }


    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return getHandler();
    }

    protected LayoutManager createLayoutManager() {
        return getHandler();
    }

    protected MouseListener createMouseListener() {
        return getHandler();
    }

    protected MouseMotionListener createMouseMotionListener() {
        return getHandler();
    }

//---------------- component creation    
    /**
     * Creates the editor used to edit the date selection.  Subclasses should
     * override this method if they want to substitute in their own editor.
     *
     * @return an instance of a JFormattedTextField
     */
    protected JFormattedTextField createEditor() {
        JFormattedTextField f = new DefaultEditor(new JXDatePickerFormatter());
        f.setName("dateField");
        f.setColumns(UIManager.getInt("JXDatePicker.numColumns"));
        f.setBorder(UIManager.getBorder("JXDatePicker.border"));

        return f;
    }

    protected JButton createPopupButton() {
        JButton b = new JButton();
        b.setName("popupButton");
        b.setRolloverEnabled(false);
        b.setMargin(new Insets(0, 3, 0, 3));

        Icon icon = UIManager.getIcon("JXDatePicker.arrowDown.image");
        if (icon == null) {
            icon = (Icon)UIManager.get("Tree.expandedIcon");
        }
        b.setIcon(icon);

        return b;
    }

    private class DefaultEditor extends JFormattedTextField implements UIResource {
        public DefaultEditor(AbstractFormatter formatter) {
            super(formatter);
        }
    }

// ---------------- Layout    
    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension dim = getEditorPreferredSize();
        if (popupButton != null) {
            dim.width += popupButton.getPreferredSize().width;
        }
        Insets insets = datePicker.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        return (Dimension)dim.clone();
    }

    /**
     * Returns a preferred size for the editor. If the selected date
     * is null, returns a reasonable minimal width. <p>
     * 
     * PENDING: how to find the "reasonable" width is open to discussion.
     * This implementation creates another datePicker, feeds it with 
     * the formats and asks its prefWidth.
     * 
     * @return the editor's preferred size
     */
    private Dimension getEditorPreferredSize() {
        Dimension dim = datePicker.getEditor().getPreferredSize();
        if (datePicker.getDate() == null) {
            // the editor tends to collapsing for empty values
            // JW: better do this in a custom editor?
            JXDatePicker picker = new JXDatePicker();
            picker.setFormats(datePicker.getFormats());
            dim.width = picker.getEditor().getPreferredSize().width;
        }
        return dim;
    }

    @Override
    public int getBaseline(int width, int height) {
        JFormattedTextField editor = datePicker.getEditor();
        View rootView = editor.getUI().getRootView(editor);
        if (rootView.getViewCount() > 0) {
            Insets insets = editor.getInsets();
            Insets insetsOut = datePicker.getInsets();
            int nh = height - insets.top - insets.bottom
                    - insetsOut.top - insetsOut.bottom;
            int y = insets.top + insetsOut.top;
            View fieldView = rootView.getView(0);
            int vspan = (int) fieldView.getPreferredSpan(View.Y_AXIS);
            if (nh != vspan) {
                int slop = nh - vspan;
                y += slop / 2;
            }
            FontMetrics fm = editor.getFontMetrics(editor.getFont());
            y += fm.getAscent();
            return y;
        }
        return -1;
    }


//------------------------------- controller methods/classes 
    
    
    @Override
    public Date getSelectableDate(Date date) throws PropertyVetoException {
        Date cleaned = date != null ? cleanupDate(date) : null;
        if (equalsDate(cleaned, datePicker.getDate())) { 
            throw new PropertyVetoException("date not selectable", null);
        }
        if (cleaned == null) return cleaned;
        if (datePicker.getMonthView().isUnselectableDate(cleaned.getTime())) {
            throw new PropertyVetoException("date not selectable", null);
            
        }
        return cleaned;
    }

    // duplication!!
    private Date cleanupDate(Date date) {
        // only modify defensive copies
        return new Date(cleanupDate(date.getTime(), datePicker.getMonthView().getCalendar()));
    }
    // duplication!!
    private long cleanupDate(long date, Calendar cal) {
        cal.setTimeInMillis(date);
        // We only want to compare the day, month and year
        // so reset all other values to 0.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

   
    /**
     * Sets the editor value to the model's selectedDate.
     */
    private void updateEditorValue() {
        datePicker.getEditor().setValue(datePicker.getMonthView().getSelectedDate());
    }
    
    /**
     * @param date
     */
    protected void updateFromDateChanged() {
        datePicker.getEditor().setValue(datePicker.getDate());
    }

    /**
     * Updates date related properties in picker/monthView 
     * after a change in the editor's value. Reverts the 
     * value if the new date is unselectable.
     * 
     * @param oldDate the editor value before the change
     * @param newDate the editor value after the change
     */
    protected void updateFromValueChanged(Date oldDate, Date newDate) {
        if ((newDate != null) && datePicker.getMonthView().isUnselectableDate(newDate.getTime())) {
            revertValue(oldDate);
            return;
        }
        if (!equalsDate(newDate, datePicker.getMonthView().getSelectedDate())) {
            if (newDate == null) {
               datePicker.getMonthView().clearSelection();
            } else {
                datePicker.getMonthView().setSelectionInterval(newDate, newDate);
            }
        }
        datePicker.setDate(newDate);
        datePicker.postActionEvent();                
    }
    /**
     * @param date
     * @return
     */
    private boolean equalsDate(Date current, Date date) {
        if ((date == null) && (current == null)) {
            return true;
        }
        if ((date != null) && (date.equals(current))) {
           return true; 
        }
        return false;
    }

    /**
     * PENDING: currently this resets at once - but it's a no-no,
     * because it happens during notification
     * 
     * 
     * @param oldDate the old date to revert to
     */
    private void revertValue(Date oldDate) {
        datePicker.getEditor().setValue(oldDate);
    }

    /**
     * Updates date related properties picker/editor 
     * after a change in the monthView's
     * selection.
     * 
     * Here: does nothing if the change is intermediate.
     * 
     * @param eventType the type of the selection change
     * @param adjusting flag to indicate whether the the selection change
     *    is intermediate
     */
    protected void updateDateFromSelection(EventType eventType, boolean adjusting) {
        if (adjusting) return;
        
        updateEditorValue();
    }

    /**
     * Updates internals after the picker's monthView has changed. <p>
     * 
     * Cleans to popup.
     * 
     * @param oldMonthView the picker's monthView before the change,
     *   may be null.
     */
    protected void updateFromMonthViewChanged(JXMonthView oldMonthView) {
        popup = null;
        updateMonthViewListeners(oldMonthView);
        updateEditorValue();
    }


    /**
     * Updates internals after the picker's editor property 
     * has changed. <p>
     * 
     * Updates the picker's children. Removes the old editor and 
     * adds the new editor. Wires the editor listeners.
     * 
     * 
     * @param oldEditor the picker's editor before the change,
     *   may be null.
     */
    protected void updateFromEditorChanged(JFormattedTextField oldEditor) { 
        if (oldEditor != null) {
            datePicker.remove(oldEditor);
        }
        datePicker.add(datePicker.getEditor());
        updateEditorValue();
        updateEditorListeners(oldEditor);
        datePicker.revalidate();
    }


    /**
     * Updates internals after the selection model changed.
     * 
     * @param oldModel the model before the change.
     */
    protected void updateFromSelectionModelChanged(DateSelectionModel oldModel) {
        updateSelectionModelListeners(oldModel);
        updateEditorValue();
    }

    /**
     * Wires the picker's monthView related listening. Removes all
     * listeners from the given old view and adds the listeners to 
     * the current monthView. <p>
     * 
     * @param oldMonthView
     */
    protected void updateMonthViewListeners(JXMonthView oldMonthView) {
        DateSelectionModel model = null;
        if (oldMonthView != null) {
            oldMonthView.removePropertyChangeListener(propertyChangeListener);
            model = oldMonthView.getSelectionModel();
        }
        datePicker.getMonthView().addPropertyChangeListener(propertyChangeListener);
        updateSelectionModelListeners(model);
    }

    
    /**
     * Wires the picker's editor related listening. Removes the
     * listeners from the old editor and adds them to 
     * the new editor. <p>
     * 
     * @param oldEditor the pickers editor before the change
     */
    protected void updateEditorListeners(JFormattedTextField oldEditor) {
        if (oldEditor != null) {
            oldEditor.removePropertyChangeListener(editorListener);
        }
        datePicker.getEditor().addPropertyChangeListener(editorListener);
    }
    
    /**
     * Wires monthView's selection model listening. Removes the
     * selection listener from the old model and add to the new model.
     * 
     * @param oldModel the dateSelectionModel before the change, may be null.
     */
    protected void updateSelectionModelListeners(DateSelectionModel oldModel) {
        if (oldModel != null) {
            oldModel.removeDateSelectionListener(selectionListener);
        }
        datePicker.getMonthView().getSelectionModel()
            .addDateSelectionListener(selectionListener);
        
    }

    public void toggleShowPopup() {
        if (popup == null) {
            popup = new BasicDatePickerPopup();
            popup.setLightWeightPopupEnabled(datePicker.isLightWeightPopupEnabled());
        }

        if (!popup.isVisible()) {
            JXMonthView monthView = datePicker.getMonthView();
            SortedSet<Date> selection = monthView.getSelection();
            if (!selection.isEmpty()) {
                Date date = selection.first();
                monthView.setSelectionInterval(date, date);
                monthView.ensureDateVisible(date.getTime());
            } else {
                // JW: hmm .. this is interfering with the firstDayToShow property
                monthView.ensureDateVisible(System.currentTimeMillis());
            }
            popup.show(datePicker,
                    0, datePicker.getHeight());
        } else {
            popup.setVisible(false);
        }
    }
    /**
     * Action used to commit the current value in the JFormattedTextField.
     * This action is used by the keyboard bindings.
     */
    private class TogglePopupAction extends AbstractAction {
        public TogglePopupAction() {
            super("TogglePopup");
        }

        public void actionPerformed(ActionEvent ev) {
            toggleShowPopup();
        }
    }


    /**
     * Popup component that shows a JXMonthView component along with controlling
     * buttons to allow traversal of the months.  Upon selection of a date the
     * popup will automatically hide itself and enter the selection into the
     * editable field of the JXDatePicker.
     */
    protected class BasicDatePickerPopup extends JPopupMenu
            implements ActionListener {

        public BasicDatePickerPopup() {
            JXMonthView monthView = datePicker.getMonthView();
            monthView.setActionCommand("MONTH_VIEW");
            monthView.addActionListener(this);

            setLayout(new BorderLayout());
            add(monthView, BorderLayout.CENTER);
            JPanel linkPanel = datePicker.getLinkPanel();
            if (linkPanel != null) {
                add(linkPanel, BorderLayout.SOUTH);
            }
        }

        public void actionPerformed(ActionEvent ev) {
            String command = ev.getActionCommand();
            if ("MONTH_VIEW".equals(command)) {
                datePicker.getEditor().setValue(datePicker.getMonthView().getSelectedDate());
                setVisible(false);
            }
        }
    }


    private class Handler implements LayoutManager, MouseListener, MouseMotionListener,
            PropertyChangeListener {
        private boolean _forwardReleaseEvent = false;

        public void mouseClicked(MouseEvent ev) {
        }

        public void mousePressed(MouseEvent ev) {
            if (!datePicker.isEnabled()) {
                return;
            }
            // JW: most probably the intention was to commit
            // if the editor isEditable
//            if (datePicker.isEditable()) {
            // reverted to old behaviour (not commit on open) 
            // until state transitions are defined
              if (!datePicker.isEditable()) {
                JFormattedTextField editor = datePicker.getEditor();
                if (editor.isEditValid()) {
                    //noinspection EmptyCatchBlock
                    try {
                        editor.commitEdit();
                    } catch (java.text.ParseException ex) {
                    }
                }
            }
            toggleShowPopup();
        }

        public void mouseReleased(MouseEvent ev) {
            if (!datePicker.isEnabled() || !datePicker.isEditable()) {
                return;
            }

            // Retarget mouse event to the month view.
            if (_forwardReleaseEvent) {
                JXMonthView monthView = datePicker.getMonthView();
                ev = SwingUtilities.convertMouseEvent(popupButton, ev,
                        monthView);
                monthView.dispatchEvent(ev);
                _forwardReleaseEvent = false;
            }
        }

        public void mouseEntered(MouseEvent ev) {
        }

        public void mouseExited(MouseEvent ev) {
        }

        public void mouseDragged(MouseEvent ev) {
            if (!datePicker.isEnabled() || !datePicker.isEditable()) {
                return;
            }

            _forwardReleaseEvent = true;

            if (!popup.isShowing()) {
                return;
            }

            // Retarget mouse event to the month view.
            JXMonthView monthView = datePicker.getMonthView();
            ev = SwingUtilities.convertMouseEvent(popupButton, ev, monthView);
            monthView.dispatchEvent(ev);
        }

        public void mouseMoved(MouseEvent ev) {
        }


        public void propertyChange(PropertyChangeEvent e) {
            if (e.getSource() == datePicker) {
                datePickerPropertyChange(e);
            }
            if (e.getSource() == datePicker.getMonthView()) {
                montViewPropertyChange(e);
            }
            if (e.getSource() == popupButton) {
                buttonPropertyChange(e);
            }
            if ("value".equals(e.getPropertyName())) {
                throw new IllegalStateException(
                        "editor listening is moved to dedicated propertyChangeLisener");
            }
        }
        
        /**
         * @param e
         */
        private void datePickerPropertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            if ("date".equals(property)) {
                updateFromDateChanged();
            } else if ("enabled".equals(property)) {
                boolean isEnabled = datePicker.isEnabled();
                popupButton.setEnabled(isEnabled);
                datePicker.getEditor().setEnabled(isEnabled);
            } else if ("editable".equals(property)) {
                boolean isEditable = datePicker.isEditable();
                datePicker.getMonthView().setEnabled(isEditable);
                datePicker.getEditor().setEditable(isEditable);
            } else if (JComponent.TOOL_TIP_TEXT_KEY.equals(property)) {
                String tip = datePicker.getToolTipText();
                datePicker.getEditor().setToolTipText(tip);
                popupButton.setToolTipText(tip);
            } else if (JXDatePicker.MONTH_VIEW.equals(property)) {
                updateFromMonthViewChanged((JXMonthView) e.getOldValue());
            } else if (JXDatePicker.LINK_PANEL.equals(property)) {
                // If the popup is null we haven't shown it yet.
                JPanel linkPanel = datePicker.getLinkPanel();
                if (popup != null) {
                    popup.remove(linkPanel);
                    popup.add(linkPanel, BorderLayout.SOUTH);
                }
            } else if (JXDatePicker.EDITOR.equals(property)) {
                updateFromEditorChanged((JFormattedTextField) e.getOldValue());
            } else if ("componentOrientation".equals(property)) {
                datePicker.revalidate();
            } else if ("lightWeightPopupEnabled".equals(property)) {
                // Force recreation of the popup when this property changes.
                if (popup != null) {
                    popup.setVisible(false);
                }
                popup = null;
            }
            
        }

        /**
         * @param e
         */
        private void montViewPropertyChange(PropertyChangeEvent e) {
            if ("selectionModel".equals(e.getPropertyName())) {
                updateFromSelectionModelChanged((DateSelectionModel) e.getOldValue());
            }
            
        }

        private void buttonPropertyChange(PropertyChangeEvent e) {
        }

        public void addLayoutComponent(String name, Component comp) { }

        public void removeLayoutComponent(Component comp) { }

        public Dimension preferredLayoutSize(Container parent) {
            return parent.getPreferredSize();
        }

        public Dimension minimumLayoutSize(Container parent) {
            return parent.getMinimumSize();
        }

        public void layoutContainer(Container parent) {
            Insets insets = datePicker.getInsets();
            int width = datePicker.getWidth() - insets.left - insets.right;
            int height = datePicker.getHeight() - insets.top - insets.bottom;

            int popupButtonWidth = popupButton != null ? popupButton.getPreferredSize().width : 0;

            boolean ltr = datePicker.getComponentOrientation().isLeftToRight();

            datePicker.getEditor().setBounds(ltr ? insets.left : insets.left + popupButtonWidth,
                    insets.top,
                    width - popupButtonWidth,
                    height);

            if (popupButton != null) {
                popupButton.setBounds(ltr ? width - popupButtonWidth + insets.left : insets.left,
                        insets.top,
                        popupButtonWidth,
                        height);
            }
        }
    }




}
