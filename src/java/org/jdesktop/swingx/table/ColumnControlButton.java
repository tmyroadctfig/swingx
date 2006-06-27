/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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

package org.jdesktop.swingx.table;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;

/**
 * This class is installed in the trailing corner of the table and is a
 * control which allows for toggling the visibilty of individual columns.<p>
 * 
 * This class is responsible for handling/providing/updating the lists of 
 * actions and to keep all action's state in synch with Table-/Column state.
 * All (most?) visible behaviour of the popup is delegated to a DefaultControlPopup. 
 * 
 * 
 * TODO: the table reference is a potential leak
 * 
 * TODO: no need to extend JButton - use non-visual controller returning
 * a JComponent instead.
 * 
 */
public class ColumnControlButton extends JButton {

    /** exposed for testing. */
    protected ControlPopup popupMenu = null;
    /** the table which is controlled by this. */
    private JXTable table;
    /** a marker to auto-recognize actions which should be added to the popup */
    public static final String COLUMN_CONTROL_MARKER = "column.";
    /** the list of actions for column menuitems.*/
    private List<ColumnVisibilityAction> columnVisibilityActions;

    public ColumnControlButton(JXTable table, Icon icon) {
        super();
        init();
        setAction(createControlAction(icon));
        installTable(table);
    }

    public void updateUI() {
        super.updateUI();
        setMargin(new Insets(1, 2, 2, 1)); // Make this LAF-independent
        getControlPopup().updateUI();
    }

    /** 
     * Toggles the popup component's visibility. This method is
     * called by this control's default action. <p>
     * 
     * Here: delegates to getControlPopup().
     * 
     *
     */ 
    public void togglePopup() {
        getControlPopup().toggleVisibility(this);
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
        getControlPopup().applyComponentOrientation(o);
    }

   
//-------------------------- Action in synch with column properties
    /**
     * A specialized action which takes care of keeping in synch with
     * TableColumn state.
     * 
     * NOTE: client must call releaseColumn if this action is no longer needed!
     * 
     */
    public class ColumnVisibilityAction extends AbstractActionExt {

        private TableColumn column;

        private PropertyChangeListener columnListener;

        /** flag to distinguish selection changes triggered by
         *  column's property change from those triggered by
         *  user interaction. Hack around #212-swingx.
         */
        private boolean fromColumn;

        public ColumnVisibilityAction(TableColumn column) {
            super((String) null);
            setStateAction();
            installColumn(column);
        }

        /**
         * 
         * release listening to column. Client must call this method if the
         * action is no longer needed. After calling it the action must not be
         * used any longer.
         */
        public void releaseColumn() {
            column.removePropertyChangeListener(columnListener);
            column = null;
        }
        
        /**
         * overriden to disable if control is not applicable.
         */
        @Override
        public boolean isEnabled() {
            return super.isEnabled() && canControl(); 
        }
        
        private boolean canControl() {
            return (column instanceof TableColumnExt);
        }

        public void itemStateChanged(final ItemEvent e) {
            if (canControl()) {
                if ((e.getStateChange() == ItemEvent.DESELECTED)
                        //JW: guarding against 1 leads to #212-swingx: setting
                        // column visibility programatically fails if
                        // the current column is the second last visible
                        // guarding against 0 leads to hiding all columns
                        // by deselecting the menu item. 
                        && (table.getColumnCount() <= 1)
                        // JW Fixed #212: basically implemented Rob's idea to distinguish
                        // event sources instead of unconditionally reselect
                        // not entirely sure if the state transitions are completely
                        // defined but all related tests are passing now.
                        && !fromColumn) {
                    reselect();
                } else {
                    setSelected(e.getStateChange() == ItemEvent.SELECTED);
//                    ((TableColumnExt) column)
//                    .setVisible(e.getStateChange() == ItemEvent.SELECTED);
                }
            }
        }

        
        @Override
        public synchronized void setSelected(boolean newValue) {
            super.setSelected(newValue);
            if (canControl()) {
                ((TableColumnExt) column).setVisible(newValue);
            }
        }

        /**
         * do nothing. Synch is done in itemStateChanged.
         */
        public void actionPerformed(ActionEvent e) {

        }
        
        /**
         * synch from TableColumnExt.visible to selected.
         *
         */
        private void updateSelected() {
            boolean visible = true;
            if (canControl()) {
                visible = ((TableColumnExt) column).isVisible();
            }
            fromColumn = true;
            setSelected(visible);
            fromColumn = false;
        }

        /**
         * enforce selected == true. Called if user interaction
         * tried to de-select the last single visible column.
         *
         */
        private void reselect() {
            firePropertyChange("selected", null, Boolean.TRUE);
        }

        // -------------- init
        private void installColumn(TableColumn column) {
            this.column = column;
            column.addPropertyChangeListener(getColumnListener());
            setName(String.valueOf(column.getHeaderValue()));
            setActionCommand(column.getIdentifier());
            updateSelected();
        }

        private PropertyChangeListener getColumnListener() {
            if (columnListener == null) {
                columnListener = createPropertyChangeListener();
            }
            return columnListener;
        }

        private PropertyChangeListener createPropertyChangeListener() {
            PropertyChangeListener l = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    if ("visible".equals(evt.getPropertyName())) {
                        updateSelected();
                    } else if ("headerValue".equals(evt.getPropertyName())) {
                        setName(String.valueOf(evt.getNewValue()));
                    }
                }

            };
            return l;
        }

    }

    // ---------------------- the popup

    /**
     * A default implementation of ControlPopup, using a JPopupMenu with 
     * MenuItems corresponding to the Actions as provided by the ColumnControlButton.
     * 
     * 
     */
    public class DefaultControlPopup implements ControlPopup {
        private JPopupMenu popupMenu;

        //------------------ public methods to control visibility status
        
        /** 
         * @inheritDoc
         * 
         */
        public void updateUI() {
            // that's not enough - the individual items still have the
            // appearance of the old ui
            getPopupMenu().updateUI();
            for (int i = 0; i < getPopupMenu().getComponentCount(); i++) {
                Component comp = getPopupMenu().getComponent(i);
                if (comp instanceof JComponent) {
                   ((JComponent) comp).updateUI(); 
                }
            }
        }

        /** 
         * @inheritDoc
         * 
         */
        public void toggleVisibility(JComponent owner) {
            JPopupMenu popupMenu = getPopupMenu();
            if (popupMenu.isVisible()) {
                popupMenu.setVisible(false);
            } else if (popupMenu.getComponentCount() > 0) {
                Dimension buttonSize = owner.getSize();
                int xPos = owner.getComponentOrientation().isLeftToRight() ? buttonSize.width
                        - popupMenu.getPreferredSize().width
                        : 0;
                popupMenu.show(owner, xPos, buttonSize.height);
            }

        }

        /** 
         * @inheritDoc
         * 
         */
        public void applyComponentOrientation(ComponentOrientation o) {
            getPopupMenu().applyComponentOrientation(o);

        }

        //-------------------- public methods to manipulate popup contents.
        
        /** 
         * @inheritDoc
         * 
         */
        public void removeAll() {
            getPopupMenu().removeAll();
        }


        /** 
         * @inheritDoc
         * 
         */
        public void addVisibilityActionItems(
                List<ColumnVisibilityAction> actions) {
            addItems(new ArrayList<Action>(actions));

        }


        /** 
         * @inheritDoc
         * 
         */
        public void addAdditionalActionItems(List<Action> actions) {
            if (actions.size() == 0)
                return;
            // JW: this is a reference to the enclosing class 
            // prevents to make this implementation static
            // Hmmm...any way around?
            if (canControl()) {
                addSeparator();
            }
            addItems(actions);
        }
        
        //--------------------------- internal helpers to manipulate popups content
        
        /**
         * Here: creates and adds a menuItem to the popup for every 
         * Action in the list. Does nothing if 
         * if the list is empty.
         * 
         * PRE: actions != null.
         * 
         * @param actions a list containing the actions to add to the popup.
         *        Must not be null.
         * 
         */
        protected void addItems(List<Action> actions) {
            ActionContainerFactory factory = new ActionContainerFactory(null);
            for (Action action : actions) {
                addItem(factory.createMenuItem(action));
            }

        }
        
        /**
         * adds a separator to the popup.
         *
         */
        protected void addSeparator() {
            getPopupMenu().addSeparator();
        }

        /**
         * 
         * @param item the menuItem to add to the popup.
         */
        protected void addItem(JMenuItem item) {
            getPopupMenu().add(item);
        }

        /**
         * 
         * @return the popupMenu to add menuitems. Guaranteed to be != null.
         */
        protected JPopupMenu getPopupMenu() {
            if (popupMenu == null) {
                popupMenu = new JPopupMenu();
            }
            return popupMenu;
        }

    }


    /**
     * 
     * @return the DefaultControlPopup for showing the items.
     */
    protected ControlPopup getControlPopup() {
        if (popupMenu == null) {
            popupMenu = createControlPopup();
        }
        return popupMenu;
    }

    /**
     * Factory method to return a DefaultControlPopup.
     * Subclasses can override to hook custom implementations.
     * 
     * @return the DefaultControlPopup used.
     */
    protected ControlPopup createControlPopup() {
        return new DefaultControlPopup();
    }


//-------------------------- updates from table propertyChangelistnere
    
    /**
     * adjust internal state to after table's column model property has changed.
     * Handles cleanup of listeners to the old/new columnModel (listens to the
     * new only if we can control column visibility) and content of popupMenu.
     * 
     * @param oldModel the old ColumnModel we had been listening to.
     */
    protected void updateFromColumnModelChange(TableColumnModel oldModel) {
        if (oldModel != null) {
            oldModel.removeColumnModelListener(columnModelListener);
        }
        populatePopup();
        if (canControl()) {
            table.getColumnModel().addColumnModelListener(columnModelListener);
        }
    }
    
    /**
     * Synchs this button's enabled with table's enabled.
     *
     */
    protected void updateFromTableEnabledChanged() {
        getAction().setEnabled(table.isEnabled());
        
    }
    /**
     * Method to check if we can control column visibility POST: if true we can
     * be sure to have an extended TableColumnModel
     * 
     * @return boolean to indicate if controlling the visibility state is
     *   possible. 
     */
    protected boolean canControl() {
        return table.getColumnModel() instanceof TableColumnModelExt;
    }
 
//  ------------------------ updating the popupMenu
    /**
     * Populates the popup from scratch.
     * 
     * If applicable, creates and adds column visibility actions. Always adds
     * additional actions.
     */
    protected void populatePopup() {
        clearAll();
        if (canControl()) {
            createVisibilityActions();
            addVisibilityActionItems();
        }
        addAdditionalActionItems();
    }

    /**
     * 
     * removes all components from the popup, making sure to release all
     * columnVisibility actions.
     * 
     */
    protected void clearAll() {
        clearColumnVisibilityActions();
        getControlPopup().removeAll();
    }


    /**
     * release actions and clear list of actions.
     * 
     */
    protected void clearColumnVisibilityActions() {
        if (columnVisibilityActions == null)
            return;
        for (ColumnVisibilityAction action : columnVisibilityActions) {
            action.releaseColumn();
        }
        columnVisibilityActions.clear();
    }

   
    /**
     * Adds visibility actions into the popup view.
     * 
     * Here: delegates the list of actions to the DefaultControlPopup.
     * <p>
     * PRE: columnVisibilityActions populated before calling this.
     * 
     */
    protected void addVisibilityActionItems() {
        getControlPopup().addVisibilityActionItems(
                Collections.unmodifiableList(getColumnVisibilityActions()));
    }

    /**
     * Adds additional actions to the popup.
     * Here: delegates the list of actions as returned by #getAdditionalActions() 
     *   to the DefaultControlPopup. 
     * Does nothing if #getColumnActions() is empty.
     * 
     */
    protected void addAdditionalActionItems() {
        getControlPopup().addAdditionalActionItems(
                Collections.unmodifiableList(getAdditionalActions()));
    }


    /**
     * creates and adds a ColumnVisiblityAction for every column that should be
     * togglable via the column control. Here: all actions currently in the 
     * the table. This includes both visible and invisible columns.
     * 
     * pre: canControl()
     * 
     */
    protected void createVisibilityActions() {
        List<TableColumn> columns = table.getColumns(true);
        for (TableColumn column : columns) {
            ColumnVisibilityAction action = new ColumnVisibilityAction(column);
            getColumnVisibilityActions().add(action);
        }

    }

    /**
     * Lazyly creates and returns the List of visibility actions.
     * 
     * @return the list of visibility actions, guaranteed to be != null.
     */
    protected List<ColumnVisibilityAction> getColumnVisibilityActions() {
        if (columnVisibilityActions == null) {
            columnVisibilityActions = new ArrayList<ColumnVisibilityAction>();
        }
        return columnVisibilityActions;
    }


    /**
     * creates and returns a list of additional Actions to add to the popup.
     * Here: the actions are looked up in the table's actionMap according
     * to the keys as returned from #getColumnControlActionKeys();
     * 
     * @return a list containing all additional actions to include into the popup.
     */
    protected List<Action> getAdditionalActions() {
        Object[] actionKeys = getColumnControlActionKeys();
        List<Action> actions = new ArrayList<Action>();
        for (int i = 0; i < actionKeys.length; i++) {
            actions.add(table.getActionMap().get(actionKeys[i]));
        }
        return actions;
    }

    /**
     * Looks up and returns action keys to access actions in the 
     * table's actionMap which should be included into the popup.
     * 
     * Here: all keys with isColumnControlActionKey(key). 
     * 
     * @return the action keys of table's actionMap entries whose
     *   action should be included into the popup.
     */
    protected Object[] getColumnControlActionKeys() {
        Object[] allKeys = table.getActionMap().allKeys();
        List columnKeys = new ArrayList();
        for (int i = 0; i < allKeys.length; i++) {
            if (isColumnControlActionKey(allKeys[i])) {
                columnKeys.add(allKeys[i]);
            }
        }
        
        Object[] actionKeys = columnKeys.toArray();
        Arrays.sort(actionKeys);
        return actionKeys;
    }

    /**
     * Here: true if a String key starts with #COLUMN_CONTROL_MARKER.
     * 
     * @param actionKey a key in the table's actionMap.
     * @return a boolean to indicate whether the given actionKey maps to
     *    an action which should be included into the popup.
     *    
     */
    protected boolean isColumnControlActionKey(Object actionKey) {
        return (actionKey instanceof String) && 
            ((String) actionKey).startsWith(COLUMN_CONTROL_MARKER);
    }


    //--------------------------- init

    private void installTable(JXTable table) {
        this.table = table;
        table.addPropertyChangeListener(columnModelChangeListener);
        updateFromColumnModelChange(null);
        updateFromTableEnabledChanged();
    }


    /**
     * Initialize the column control button's gui
     */
    private void init() {
        setFocusPainted(false);
        setFocusable(false);
        // this is a trick to get hold of the client prop which
        // prevents closing of the popup
        JComboBox box = new JComboBox();
        Object preventHide = box.getClientProperty("doNotCancelPopup");
        putClientProperty("doNotCancelPopup", preventHide);
    }


    /** 
     * Creates and returns the default action for this button.
     * 
     * @param icon the Icon to use in the action.
     * @return the default action.
     */
    private Action createControlAction(Icon icon) {
        Action control = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                togglePopup();
            }

        };
        control.putValue(Action.SMALL_ICON, icon);
        return control;
    }
    
    // -------------------------------- listeners

    // TODO JW - create lazily
    private PropertyChangeListener columnModelChangeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if ("columnModel".equals(evt.getPropertyName())) {
                updateFromColumnModelChange((TableColumnModel) evt.getOldValue());
            } else if ("enabled".equals(evt.getPropertyName())) {
                updateFromTableEnabledChanged();
            }
        }
    };

    // TODO JW - create lazily
    private TableColumnModelListener columnModelListener = new TableColumnModelListener() {
        /** Tells listeners that a column was added to the model. */
        public void columnAdded(TableColumnModelEvent e) {
            // quickfix for #192
            if (!isVisibilityChange(e, true)) {
                populatePopup();
            }
        }

        /** Tells listeners that a column was removed from the model. */
        public void columnRemoved(TableColumnModelEvent e) {
            if (!isVisibilityChange(e, false)) {
                populatePopup();
            }
        }

        /**
         * check if the add/remove event is triggered by a move to/from the
         * invisible columns.
         * 
         * PRE: the event must be received in columnAdded/Removed.
         * 
         * @param e
         *            the received event
         * @param added
         *            if true the event is assumed to be received via
         *            columnAdded, otherwise via columnRemoved.
         * @return
         */
        private boolean isVisibilityChange(TableColumnModelEvent e,
                boolean added) {
            // can't tell
            if (!(e.getSource() instanceof DefaultTableColumnModelExt))
                return false;
            DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) e
                    .getSource();
            if (added) {
                return model.isAddedFromInvisibleEvent(e.getToIndex());
            } else {
                return model.isRemovedToInvisibleEvent(e.getFromIndex());
            }
        }

        /** Tells listeners that a column was repositioned. */
        public void columnMoved(TableColumnModelEvent e) {
        }

        /** Tells listeners that a column was moved due to a margin change. */
        public void columnMarginChanged(ChangeEvent e) {
        }

        /**
         * Tells listeners that the selection model of the TableColumnModel
         * changed.
         */
        public void columnSelectionChanged(ListSelectionEvent e) {
        }
    };



} // end class ColumnControlButton
