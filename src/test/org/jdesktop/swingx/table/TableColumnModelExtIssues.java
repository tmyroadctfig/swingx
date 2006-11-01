/*
 * Created on 24.08.2006
 *
 */
package org.jdesktop.swingx.table;

import org.jdesktop.swingx.test.ColumnModelReport;

public class TableColumnModelExtIssues extends TableColumnModelTest {
    /**
     * Issue #369-swingx: properties of hidden columns are not fired. <p>
     * make sure that property changes in hidden columns are routed to the
     * TableColumnModelExtListener
     *
     */
    public void testHiddenTableColumnPropertyNotification() {
        TableColumnModelExt columnModel = createColumnModel(COLUMN_COUNT);
        Object identifier = "0";
        columnModel.getColumnExt(identifier).setVisible(false);
        // sanity...
        assertNotNull(columnModel.getColumnExt(identifier));
        String title = columnModel.getColumnExt(identifier).getTitle() + "changed";
        ColumnModelReport report = new ColumnModelReport();
        columnModel.addColumnModelListener(report);
        columnModel.getColumnExt(identifier).setTitle(title);
        assertEquals(1, report.getColumnPropertyEventCount());
    }
    

}
