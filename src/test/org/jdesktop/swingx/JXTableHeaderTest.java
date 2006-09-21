/*
 * Created on 09.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.table.TableColumnExt;

public class JXTableHeaderTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTableHeaderTest.class
            .getName());

    /**
     * Issue #390-swingx: JXTableHeader: throws AIOOB on removing dragged column.
     * Test that getDraggedColumn is null if removed.
     * 
     * Problem was reported on mac:
     * http://forums.java.net/jive/thread.jspa?threadID=18368&tstart=0
     * when hiding column while drag(?) is in process.
     * 
     *
     */
    public void testDraggedColumnRemoved() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.getTableHeader().setDraggedColumn(columnExt);
        // sanity assert
        assertEquals(columnExt, table.getTableHeader().getDraggedColumn());
        table.getColumnModel().removeColumn(columnExt);
        assertNull("draggedColumn must be null if removed", table.getTableHeader().getDraggedColumn());
    }
    
    /**
     * Issue #390-swingx: JXTableHeader: throws AIOOB on removing dragged column.
     * Test that getDraggedColumn is visible or null.
     * 
     * Problem was reported on mac:
     * http://forums.java.net/jive/thread.jspa?threadID=18368&tstart=0
     * when hiding column while drag(?) is in process.
     */
    public void testDraggedColumnVisible() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.getTableHeader().setDraggedColumn(columnExt);
        // sanity assert
        assertEquals(columnExt, table.getTableHeader().getDraggedColumn());
        columnExt.setVisible(false);
        assertNull("dragged column must visible or null", table.getTableHeader().getDraggedColumn());
    }
    
    
    /**
     * Issue 334-swingx: BasicTableHeaderUI.getPrefSize doesn't respect 
     *   all renderere's size requirements.
     *
     */
    public void testPreferredHeight() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(1);
        columnExt.setTitle("<html><center>Line 1<br>Line 2</center></html>");
        JXTableHeader tableHeader = (JXTableHeader) table.getTableHeader();
        TableCellRenderer renderer = tableHeader.getCellRenderer(1);
        Component comp = renderer.getTableCellRendererComponent(table, 
                columnExt.getHeaderValue(), false, false, -1, 1);
        Dimension prefRendererSize = comp.getPreferredSize();
        assertEquals("Header pref height must respect renderer",
                prefRendererSize.height, tableHeader.getPreferredSize().height);
    }
    

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
    
    /**
     * #212-swingx: second last column cannot be set to invisible programatically.
     * 
     * One reason for the "trick" of reselecting the last is that 
     * the header and with it the columnControl vanishes if there is 
     * no visible column.
     * 
     * 
     *
     */
    public void testHeaderVisibleWithoutColumns() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run headerVisible - headless environment");
            return;
        }
        JXTable table = new JXTable();
        table.setColumnControlVisible(true);
        wrapWithScrollingInFrame(table, "");
        assertEquals("headerHeight must be > 0", 16, table.getTableHeader().getHeight());
        assertEquals("headerWidth must be table width", 
                table.getWidth(), table.getTableHeader().getWidth());
        
    }
    
    /**
     * #212-swingx: second last column cannot be set to invisible programatically.
     * 
     * One reason for the "trick" of reselecting the last is that 
     * the header and with it the columnControl vanishes if there is 
     * no visible column.
     * 
     * 
     *
     */
    public void testHeaderVisibleWithColumns() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run headerVisible - headless environment");
            return;
        }
        JXTable table = new JXTable(10, 2);
        table.setColumnControlVisible(true);
        wrapWithScrollingInFrame(table, "");
        assertEquals("headerHeight must be > 0", 16, table.getTableHeader().getHeight());
        table.setModel(new DefaultTableModel());
        assertEquals("headerHeight must be > 0", 16, table.getTableHeader().getHeight());
        
    }
    

//--------------------------------- visual checks
    /**
     * Issue #390-swingx: JXTableHeader: throws AIOOB on removing dragged column.
     * 
     */
    public void interactiveDraggedColumnRemoved() {
        final JXTable table = new JXTable(10, 5);
        Action deleteColumn = new AbstractAction("deleteCurrentColumn") {

            public void actionPerformed(ActionEvent e) {
                TableColumn column = table.getTableHeader().getDraggedColumn();
                if (column == null) return;
                table.getColumnModel().removeColumn(column);
            }
            
        };
        KeyStroke keyStroke = KeyStroke.getKeyStroke("F1");
        table.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "f1");
        table.getActionMap().put("f1", deleteColumn);
        JXFrame frame = wrapWithScrollingInFrame(table, "Remove dragged column with F1");
        frame.setVisible(true);
    }
    /**
     * Visual demo that header is always visible.
     */
    public void interactiveHeaderVisible() {
        final JXTable table = new JXTable();
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "header always visible");
        Action action = new AbstractAction("toggle model") {

            public void actionPerformed(ActionEvent e) {
                int columnCount = table.getColumnCount(true);
                table.setModel(columnCount > 0 ?
                        new DefaultTableModel() : new DefaultTableModel(10, 2));
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }
    public static void main(String args[]) {
        JXTableHeaderTest test = new JXTableHeaderTest();
        try {
          test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Siz.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

}
