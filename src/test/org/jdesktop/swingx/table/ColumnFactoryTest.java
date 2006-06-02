/*
 * Created on 02.06.2006
 *
 */
package org.jdesktop.swingx.table;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;

public class ColumnFactoryTest extends InteractiveTestCase {

    /**
     * Issue #315-swingx: pack doesn't pass column index to headerRenderer.
     * This shows only if the renderer relies on the column index (default doesn't?) 
     */
    public void testPackColumnIndexToHeaderRenderer() {
        final int special = 1;
        TableCellRenderer renderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (column == special) {
                    value = "exxxxttteeeed" + value;
                }
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                        row, column);
                return this;
            }
            
        };
        JXTable table = new JXTable(1, 2);
        table.getTableHeader().setDefaultRenderer(renderer);
        TableColumnExt columnExt = table.getColumnExt(special);
        table.packAll();
        int realPrefWidth = 2 * 4 // magic value - JXTable's default margin, 
                                  //  needs to be made configurable, see Issue ?? 
            + renderer.getTableCellRendererComponent(table, 
                columnExt.getHeaderValue(), false, false, -1, special).getPreferredSize().width;
        assertEquals(realPrefWidth, columnExt.getPreferredWidth());
        
    }
}
