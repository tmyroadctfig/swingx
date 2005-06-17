/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.action.ActionContainerFactory;

/**
 * This class is installed in the upper right corner of the table and is a
 * control which allows for toggling the visibilty of individual columns.
 * 
 * TODO: the table reference is a potential leak
 */
public final class ColumnControlButton extends JButton {
    /**
     * Used to construct a button with a reasonable preferred size
     */
    public final static String TITLE = "x";

    /** exposed for testing. */
    protected JPopupMenu popupMenu = null;

    // private MouseAdapter mouseListener = new MouseAdapter() {
    // public void mousePressed(MouseEvent ev) {
    // showP();
    // }
    // };

    private JTable table;

    public static final String COLUMN_CONTROL_MARKER = "column.";

    public ColumnControlButton(JTable table, Icon icon) {
        super(icon);
        this.table = table;
        init();
    }

    public void updateUI() {
        super.updateUI();
        setMargin(new Insets(1, 2, 2, 1)); // Make this LAF-independent
        if (popupMenu != null) {
            // Hmm, not really working....
            popupMenu.updateUI();
        }
    }

    /**
     * Initialize the column control button's gui
     */
    private void init() {
        setFocusPainted(false);
        setFocusable(false);

        setEnabled(table.getColumnModel() instanceof TableColumnModelExt);

        // create the popup menu
        popupMenu = new JPopupMenu();
        // initially populate the menu
        populatePopupMenu();
        // attach a TableColumnModel listener so that if the column model
        // is replaced or changed the popup menu will be regenerated
        table.addPropertyChangeListener("columnModel",
                columnModelChangeListener);
        table.getColumnModel().addColumnModelListener(columnModelListener);
        addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showPopup();
                // patch for Issue #27(swingx) - incomplete because not
                // catching all possibilities to close the popup
//                if (opened) {
//                    opened = false;
//                    popupMenu.setVisible(opened);
//                } else {
//                    showPopup();
//                }

            }

        });

    }

    /**
     * Populates the popup menu based on the current columns in the
     * TableColumnModel for jxtable
     */
    protected void populatePopupMenu() {
        popupMenu.removeAll();
        // For each column in the view, add a JCheckBoxMenuItem to popup
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            // Create a new JCheckBoxMenuItem
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(column
                    .getHeaderValue().toString(), true);
            item.putClientProperty("column", column);
            // Attach column visibility action to each menu item
            item.addActionListener(columnVisibilityAction);
            popupMenu.add(item); // Add item to popup menu
        }
        addColumnActions();
    }

    private void addColumnActions() {
        Object[] actionKeys = table.getActionMap().allKeys();
        Arrays.sort(actionKeys);
        List actions = new ArrayList();
        for (int i = 0; i < actionKeys.length; i++) {
            if (isColumnControlAction(actionKeys[i])) {
                actions.add(table.getActionMap().get(actionKeys[i]));
            }
        }
        if (actions.size() == 0)
            return;
        popupMenu.addSeparator();
        ActionContainerFactory factory = new ActionContainerFactory(null);
        for (Iterator iter = actions.iterator(); iter.hasNext();) {
            Action action = (Action) iter.next();
            popupMenu.add(factory.createMenuItem(action));
        }
    }

    private boolean isColumnControlAction(Object object) {
        return String.valueOf(object).startsWith(COLUMN_CONTROL_MARKER);
    }

    protected void updateSelectionState() {
        Component[] menuItems = popupMenu.getComponents();
        for (int i = 0; i < menuItems.length; i++) {
            if (menuItems[i] instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) menuItems[i];
                TableColumn column = (TableColumn) menuItem
                        .getClientProperty("column");
                if (column instanceof TableColumnExt) {
                    menuItem.setSelected(((TableColumnExt) column).isVisible());
                }
            }
        }

    }

    private AbstractAction columnVisibilityAction = new AbstractAction() {
        public void actionPerformed(ActionEvent ev) {
            opened = false;
            JCheckBoxMenuItem item = (JCheckBoxMenuItem) ev.getSource();
            TableColumnExt column = (TableColumnExt) item
                    .getClientProperty("column");
            if (item.isSelected()) { // was not selected, but is now...
                column.setVisible(true);
                /** @todo Figure out how to restore column index */
            } else {
                // Remove the column unless it's the last one.
                TableColumnModelExt model = (TableColumnModelExt) table
                        .getColumnModel();
                if (model.getColumnCount() - 1 == 0) {
                    // reselect the checkbox because we cannot unselect the last
                    // one
                    item.setSelected(true);
                } else {
                    column.setVisible(false);
                }
            }
        }
    };

    private boolean opened;

    public void showPopup() {

        if (popupMenu.getComponentCount() > 0) {
            opened = true;
            Dimension buttonSize = getSize();
            popupMenu.show(this, buttonSize.width
                    - popupMenu.getPreferredSize().width, buttonSize.height);
        }
    }

    // -------------------------------- listeners

    private PropertyChangeListener columnModelChangeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            ((TableColumnModel) evt.getOldValue())
                    .removeColumnModelListener(columnModelListener);
            if (((TableColumnModel) evt.getNewValue()) instanceof TableColumnModelExt) {
                setEnabled(true);
                ((TableColumnModel) evt.getNewValue())
                        .addColumnModelListener(columnModelListener);
                populatePopupMenu();
            } else {
                setEnabled(false);
            }
        }
    };

    private TableColumnModelListener columnModelListener = new TableColumnModelListener() {
        /** Tells listeners that a column was added to the model. */
        public void columnAdded(TableColumnModelEvent e) {
            // quickfix for #192
            if (isVisibilityChange(e, true)) {
                updateSelectionState();
            } else {
                populatePopupMenu();
            }
        }

        /** Tells listeners that a column was removed from the model. */
        public void columnRemoved(TableColumnModelEvent e) {
            if (isVisibilityChange(e, false)) {
                // quickfix for #192
                updateSelectionState();
            } else {
                populatePopupMenu();
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
