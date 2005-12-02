/*
 * Created on 02.12.2005
 *
 */
package org.jdesktop.swingx.table;

import java.awt.Component;
import java.awt.GraphicsEnvironment;

import javax.swing.JMenuItem;

import org.jdesktop.swingx.JXTable;

public class ColumnControlButtonIssues extends ColumnControlButtonTest {

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
