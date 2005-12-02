/*
 * Created on 02.12.2005
 *
 */
package org.jdesktop.swingx.table;

import java.awt.GraphicsEnvironment;

import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;

public class ColumnControlButtonIssues extends ColumnControlButtonTest {

    /**
     * Issue #212-swingx: 
     * 
     * guarantee that at exactly one column is always.
     * 
     * Happens if a) column visibility is set after adding the table to a frame
     * and b) model.count = 2.
     *
     */
    public void testMinimumColumnCountOneAfterSetModel() {
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


}
