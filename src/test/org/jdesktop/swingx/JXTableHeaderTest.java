/*
 * Created on 09.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.table.TableColumnExt;

public class JXTableHeaderTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTableHeaderTest.class
            .getName());
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
