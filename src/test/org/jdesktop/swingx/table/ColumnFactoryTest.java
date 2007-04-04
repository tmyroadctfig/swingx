/*
 * Created on 02.06.2006
 *
 */
package org.jdesktop.swingx.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;

public class ColumnFactoryTest extends InteractiveTestCase {

    /**
     * Issue #470-swingx: added getRowCount(table)
     *
     */
    public void testEncapsulateRowCount() {
        final int cutOffRowCount = 10;
        ColumnFactory factory = new ColumnFactory() {
           @Override
           protected int getRowCount(JXTable table) {
               return cutOffRowCount;
           }
        };
        DefaultTableModel model = new DefaultTableModel(cutOffRowCount * 2, 2) {

            @Override
            public Object getValueAt(int row, int column) {
                if (row >= cutOffRowCount) {
                    throw new IllegalArgumentException("Illegal access to cutoff rows");
                }
                return super.getValueAt(row, column);
            }
             
        };
        JXTable table = new JXTable();
        table.setColumnFactory(factory);
        
        table.setModel(model);
        factory.packColumn(table, table.getColumnExt(0), -1, -1);
        
        
    }
    /**
     * Issue #315-swingx: pack doesn't pass column index to headerRenderer.
     * This bug shows only if the renderer relies on the column index (default doesn't). 
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

    
    /**
     * Issue #266-swingx: support customization of pack margin.
     * 
     * added property to ColumnFactory. 
     *  
     */
    public void testPackMargin() {
        final int special = 1;
        JXTable table = new JXTable(1, 2);
        ColumnFactory factory = new ColumnFactory();
        table.setColumnFactory(factory);
        table.setValueAt("something that's wider than 75", 0, special);
        TableColumnExt columnExt = table.getColumnExt(special);
        table.packAll();
        TableCellRenderer renderer = table.getCellRenderer(0, special);
        Component comp = table.prepareRenderer(renderer, 0, special);
        int realPrefWidth = 2 * factory.getDefaultPackMargin() // magic value - JXTable's default margin, 
                                  //  needs to be made configurable, see Issue 266 
            + comp.getPreferredSize().width;
        // sanity - default margin kicks in
        assertEquals(realPrefWidth, columnExt.getPreferredWidth());
        
        int margin = 10;
        factory.setDefaultPackMargin(margin);
        table.packAll();
        table.prepareRenderer(renderer, 0, special);
        int otherPrefWidth = 2 * margin + comp.getPreferredSize().width;
        assertEquals(otherPrefWidth, columnExt.getPreferredWidth());
        
        
    }

}
