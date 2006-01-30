/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.table;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;

public class ColumnControlButtonIssues extends ColumnControlButtonTest {
    private static final Logger LOG = Logger
            .getLogger(ColumnControlButtonIssues.class.getName());


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
        // this is testing the actual (combined) misbehaviour of ColumnControl
        // and ActionContainerFactory: on every column removed/columnAdded there's
        // a new menuItem created registering a listener without deregistering the old.
//        System.gc();
       assertEquals(initialPCLCount, extAction.getPropertyChangeListeners().length);
        
    }
    /**
     * Issue #212-swingx: 
     * 
     * guarantee that exactly one column is always visible.
     * 
     * Here we directly set the second last visible column to invisible. This 
     * fails if a) column visibility is set after adding the table to a frame
     * and b) model.count = 2.
     *
     */
    public void testSetSecondLastColumnToInvisible() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        final JXTable table = new JXTable(10, 2);
        table.setColumnControlVisible(true);
        wrapWithScrollingInFrame(table, "");
        table.getColumnExt(0).setVisible(false);
        assertEquals(1, table.getColumnCount());
    }

    /**
     * Issue #212-swingx: 
     * 
     * guarantee that exactly one column is always visible.
     * 
     * Here we deselect the menuitem.
     * 
     */
    public void testSetLastColumnToInvisible() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        final JXTable table = new JXTable(10, 1);
        table.setColumnControlVisible(true);
        wrapWithScrollingInFrame(table, "");
        ColumnControlButton columnControl = (ColumnControlButton) table.getColumnControl();
        Component[] items = columnControl.popupMenu.getComponents();
        ((JMenuItem) items[0]).setSelected(false);
        assertEquals(1, table.getColumnCount());
    }

}
