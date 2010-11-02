/*
 * Created on 09.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Interactive "test" methods for <code>JXTableHeader</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class JXTableHeaderVisualCheck extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(JXTableHeaderVisualCheck.class.getName());
    
    public static void main(String args[]) {
        JXTableHeaderVisualCheck test = new JXTableHeaderVisualCheck();
        try {
//          test.runInteractiveTests();
            test.runInteractiveTests("interactive.*DoubleSort.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

    /**
     * Issue #271-swingx: optionally support double sort on double click.
     * 
     */
    public void interactiveDoubleSort() {
        JXTable table = new JXTable(30, 5);
        showWithScrollingInFrame(table, "support double sort on double click");
    }
    
    /**
     * Issue #1225-swingx: JXTableHeader throws on rowSorters which are not
     *   of type SortController when resizing columns with mouse.
     */
    public void interactiveHeaderCoreRowSorter() {
        JXTable table = new JXTable(30, 5);
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(new TableRowSorter<TableModel>(table.getModel()));
        showWithScrollingInFrame(table, "core resize columns with mouse");
    }
    /**
     * Issue #683-swingx: Autoscroll if column dragged outside.
     * 
     */
    public void interactiveHeaderAutoScrollRToL() {
        JXTable table = new JXTable(20, 30);
        table.setColumnControlVisible(true);
        table.setVisibleColumnCount(6);
        assertTrue(table.getAutoscrolls());
        table.getTableHeader().setAutoscrolls(true);
        assertTrue(table.getTableHeader().getAutoscrolls());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JXFrame frame = showWithScrollingInFrame(table, "autoScroll column drag - RToL");
        frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }

    /**
     * Issue #683-swingx: Autoscroll if column dragged outside.
     * 
     */
    public void interactiveHeaderAutoScroll() {
        JXTable table = new JXTable(20, 30);
        table.setColumnControlVisible(true);
        table.setVisibleColumnCount(6);
        assertTrue(table.getAutoscrolls());
        table.getTableHeader().setAutoscrolls(true);
        assertTrue(table.getTableHeader().getAutoscrolls());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JXFrame frame = showWithScrollingInFrame(table, "autoScroll column drag - LToR");
        frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }
    
    /**
     * Issue #683-swingx: Autoscroll if column dragged outside.
     * Plain ol' JTable - core bug 6503981
     */
    public void interactiveHeaderAutoScrollCoreIssue() {
        JTable table = new JXTable(20, 30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JXFrame frame = showWithScrollingInFrame(table, "core bug 1.6 before u4 - scroll to last column and drag");
        frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        frame.setSize(400, 400);
    }
    
    /**
     * Issue #485-swingx: table header disappears if all header values are
     * empty. Compare core <--> JXTable
     * fixed for SwingX.
     *
     */
    public void interactiveHeaderSizeRequirements() {
        
        final String[] alternate = { 
                null, 
                null, 
                };
        final JTable table = new JTable(10, 2);
        table.getColumnModel().getColumn(0).setHeaderValue(alternate[0]);
        table.getColumnModel().getColumn(1).setHeaderValue(alternate[1]);
        
        JXTable xTable = new JXTable(10, 2);
        xTable.getColumn(0).setHeaderValue(alternate[0]);
        xTable.getColumn(1).setHeaderValue(alternate[1]);
        
        JXFrame frame = wrapWithScrollingInFrame(table, xTable, "header height empty (core - xtable)");
        frame.setVisible(true);
        
    }

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

    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
