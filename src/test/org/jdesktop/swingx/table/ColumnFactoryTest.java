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
import org.jdesktop.swingx.util.PropertyChangeReport;

public class ColumnFactoryTest extends InteractiveTestCase {

    /**
     * test per-table ColumnFactory: bound property, reset to shared.
     * 
     * TODO: move to JXTableTest.
     *
     */
    public void testTableSetColumnFactory() {
        JXTable table = new JXTable();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        ColumnFactory factory = createCustomColumnFactory();
        table.setColumnFactory(factory);
        assertEquals(1, report.getEventCount());
        assertTrue(report.hasEvents("columnFactory"));
        assertSame(factory, report.getLastNewValue("columnFactory"));
        assertSame(ColumnFactory.getInstance(), report.getLastOldValue("columnFactory"));
        report.clear();
        table.setColumnFactory(null);
        assertEquals(1, report.getEventCount());
        assertTrue(report.hasEvents("columnFactory"));
        assertSame(factory, report.getLastOldValue("columnFactory"));
        assertSame(ColumnFactory.getInstance(), report.getLastNewValue("columnFactory"));
    }
    
    /**
     * test per-table ColumnFactory: use individual.
     * 
     * TODO: move to JXTableTest.
     *
     */
    public void testTableUseCustomColumnFactory() {
        JXTable table = new JXTable();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        ColumnFactory factory = createCustomColumnFactory();
        table.setColumnFactory(factory);
        // sanity...
        assertSame(factory, report.getLastNewValue("columnFactory"));
        table.setModel(new DefaultTableModel(2, 5));
        assertEquals(String.valueOf(0), table.getColumnExt(0).getTitle());
    }
    
    /**
     * create and return a custom columnFactory for testing: 
     * set's column title to modelIndex.
     * 
     * @return the custom ColumnFactory.
     */
    protected ColumnFactory createCustomColumnFactory() {
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model,
                    TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                columnExt.setTitle(String.valueOf(columnExt.getModelIndex()));
            }

        };
        return factory;
        
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
}
