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
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Contains unit tests for <code>ColumnFactory</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class ColumnFactoryTest extends InteractiveTestCase {

    /**
     * Issue ??: NPE in pack for null table header.
     *
     */
    public void testPackColumnNullHeader() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setTableHeader(null);
        table.packAll();
    }
    /**
     * test if max parameter is respected.
     *
     */
    public void testPackColumnWithMax() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnExt = table.getColumnExt(0);
        table.getColumnFactory().packColumn(table, columnExt, -1, -1);
        int prefWidth = columnExt.getPreferredWidth();
        assertTrue("sanity: ", prefWidth > 10);
        int max = prefWidth - 5;
        table.getColumnFactory().packColumn(table, columnExt, -1, max);
        assertEquals("pref width must be bounded by max", 
                max, columnExt.getPreferredWidth());
    }
    /**
     * packColumn can't handle hidden columns.
     *
     */
    public void testPackHiddenColumn() {
        JXTable table = new JXTable(10, 4);
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setVisible(false);
        try {
            table.getColumnFactory().packColumn(table, columnExt, -1, -1);
            fail("packColumn is doc'ed to not handle hidden columns");
        } catch (IllegalStateException e) {
            // expected
        }        
    }
    
    /**
     * test that configure throws exceptions as doc'ed.
     * Here: model index == negative
     *
     */
    public void testConfigureTableColumnDoc() {
        TableModel model = new DefaultTableModel(0, 4);
        TableColumnExt columnExt = new TableColumnExt(-1);
        try {
            ColumnFactory.getInstance().configureTableColumn(model, columnExt);
            fail("factory must throw on illegal column model index " + columnExt.getModelIndex());
        } catch (IllegalStateException e) {
            // nothing to do - that's the doc'ed behaviour
        }        
    }
    /**
     * test that configure throws exceptions as doc'ed.
     * Here: model index == getColumnCount
     *
     */
    public void testConfigureTableColumnExcessModelIndex() {
        TableModel model = new DefaultTableModel(0, 4);
        TableColumnExt columnExt = new TableColumnExt(model.getColumnCount());
        try {
            ColumnFactory.getInstance().configureTableColumn(model, columnExt);
            fail("factory must throw on illegal column model index " + columnExt.getModelIndex());
        } catch (IllegalStateException e) {
            // nothing to do - that's the doc'ed behaviour
        }        
    }
    /**
     * For completeness: formally test that app-wide factory 
     * is used by JXTable.
     *
     */
    public void testSetColumnFactory() {
        ColumnFactory myFactory = new ColumnFactory();
        ColumnFactory.setInstance(myFactory);
        JXTable table = new JXTable();
        assertSame(myFactory, table.getColumnFactory());
    }
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
