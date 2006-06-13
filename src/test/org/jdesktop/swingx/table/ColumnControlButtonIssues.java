/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.table;

import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;

public class ColumnControlButtonIssues extends ColumnControlButtonTest {
    private static final Logger LOG = Logger
            .getLogger(ColumnControlButtonIssues.class.getName());

    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     */
    public void testActionListenerCount() {
        JXTable table = new JXTable(10, 1);
        Action action = table.getActionMap().get(JXTable.HORIZONTALSCROLL_ACTION_COMMAND);
        if (!(action instanceof AbstractActionExt)) {
            LOG.info("cannot run testColumnActionListenerCount - action not of type AbstractAction");
            return;
        }
        AbstractActionExt extAction = (AbstractActionExt) action;
        assertTrue(extAction.isStateAction());
        assertEquals(0, extAction.getPropertyChangeListeners().length);
        AbstractButton menuItem = new JCheckBoxMenuItem();
        ActionContainerFactory factory = new ActionContainerFactory(null);
        factory.configureSelectableButton(menuItem, extAction, null);
        // sanity: here the action is bound to a menu item in the columnControl
        // should have one ad
        int initialPCLCount = extAction.getPropertyChangeListeners().length;
        // sanity: expect it to be 2 - one is the menuitem itself, another 
        // the TogglePCL registered by the ActionContainerFacory
        assertEquals(2, initialPCLCount);
        menuItem = new JToggleButton();
        factory.configureSelectableButton(menuItem, extAction, null);
        System.gc();
        PropertyChangeListener[] listeners = extAction.getPropertyChangeListeners();
        // testing this is not quite okay: probably the old buttons are not yet
        // gc'ed
       assertEquals(initialPCLCount, extAction.getPropertyChangeListeners().length);
        
    }
    
    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     */
    public void testColumnActionListenerCount() {
        JXTable table = new JXTable(10, 1);
        Action action = table.getActionMap().get(JXTable.HORIZONTALSCROLL_ACTION_COMMAND);
        if (!(action instanceof AbstractActionExt)) {
            LOG.info("cannot run testColumnActionListenerCount - action not of type AbstractAction");
            return;
        }
        AbstractActionExt extAction = (AbstractActionExt) action;
        assertTrue(extAction.isStateAction());
        assertEquals(0, extAction.getPropertyChangeListeners().length);
        table.setColumnControlVisible(true);
        // make sure the control is initialized (usually done lazyly on showing)
        table.getColumnControl();
        // sanity: here the action is bound to a menu item in the columnControl
        // should have one ad
        int initialPCLCount = extAction.getPropertyChangeListeners().length;
        // sanity: expect it to be 2 - one is the menuitem itself, another 
        // the TogglePCL registered by the ActionContainerFacory
        assertEquals(2, initialPCLCount);
        table.setModel(new DefaultTableModel(10, 1));
        System.gc();
        PropertyChangeListener[] listeners = extAction.getPropertyChangeListeners();
        // testing this is not quite okay: probably the old buttons are not yet
        // gc'ed
       assertEquals(initialPCLCount, extAction.getPropertyChangeListeners().length);
        
    }


}
