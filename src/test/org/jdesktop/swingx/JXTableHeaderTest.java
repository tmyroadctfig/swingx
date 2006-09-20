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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.table.TableColumnExt;

public class JXTableHeaderTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTableHeaderTest.class
            .getName());

    /**
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
     * Characterization of header#isVisible(TableColumn).
     *
     * PENDING JW: should column be contained in model to be evaluated as
     *   visible in the header context? This is a bit moot, because needed
     *   mainly in context with the draggedColumn which is dirty anyway.
     */
    public void testColumnVisible() {
        JXTableHeader header = new JXTableHeader();
        assertFalse("null column must not be visible", header.isVisible(null));
        assertTrue("TableColumn must be visible", header.isVisible(new TableColumn()));
        TableColumnExt columnExt = new TableColumnExt();
        assertEquals("TableColumnExt visible property", 
                columnExt.isVisible(), header.isVisible(columnExt));
        columnExt.setVisible(!columnExt.isVisible());
        assertEquals("TableColumnExt visible property", 
                columnExt.isVisible(), header.isVisible(columnExt));
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
         //   test.runInteractiveTests("interactive.*Render.*");
         //   test.runInteractiveTests("interactive.*Toggle.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

}
