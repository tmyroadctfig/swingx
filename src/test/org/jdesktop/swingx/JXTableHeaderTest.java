/*
 * Created on 09.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.event.MouseEvent;

import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.swingx.table.TableColumnExt;

public class JXTableHeaderTest extends InteractiveTestCase {
    
    /**
     * test doc'ed xheader.getToolTipText(MouseEvent) behaviour.
     *
     */
    public void testColumnToolTip() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        JXTableHeader tableHeader = (JXTableHeader) table.getTableHeader();
        MouseEvent event = new MouseEvent(tableHeader, 0,
                  0, 0, 40, 5, 0, false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        String rendererToolTip = "rendererToolTip";
        renderer.setToolTipText(rendererToolTip);
        columnExt.setHeaderRenderer(renderer);
        assertEquals(rendererToolTip, tableHeader.getToolTipText(event));
        String columnToolTip = "columnToolTip";
        columnExt.setToolTipText(columnToolTip);
        assertEquals(columnToolTip, tableHeader.getToolTipText(event));

    }
}
