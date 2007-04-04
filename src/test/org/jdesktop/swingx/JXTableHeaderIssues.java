/*
 * Created on 28.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTable;

import org.jdesktop.swingx.decorator.SortOrder;

public class JXTableHeaderIssues extends JXTableHeaderTest {
    public static void main(String args[]) {
        JXTableHeaderIssues test = new JXTableHeaderIssues();
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

    /**
     * Issue #??-swingx: table header disappears if all header values are
     * empty. 
     *
     */
    public void testHeaderSizeEmptyStringHeaderValue() {
        final String[] alternate = { 
                "", 
                "", 
                };
        JXTable xTable = new JXTable(10, 2);
        xTable.getColumn(0).setHeaderValue(alternate[0]);
        xTable.getColumn(1).setHeaderValue(alternate[1]);
        assertTrue("header height must be > 0", xTable.getTableHeader().getPreferredSize().height > 0);
    }

    /**
     * Issue #??-swingx: table header disappears if all header values are
     * empty. 
     *
     */
    public void testHeaderSizeNullHeaderValue() {
        final String[] alternate = { 
                null, 
                null, 
                };
        JXTable xTable = new JXTable(10, 2);
        xTable.getColumn(0).setHeaderValue(alternate[0]);
        xTable.getColumn(1).setHeaderValue(alternate[1]);
        assertTrue("header height must be > 0", xTable.getTableHeader().getPreferredSize().height > 0);
    }
    /**
     * Issue #??-swingx: table header disappears if all header values are
     * empty. 
     * 
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
     * Issue #281-swingx, Issue #334-swing: 
     * header should be auto-repainted on changes to
     * header title, value. Must update size if appropriate.
     * 
     * still not solved: core #4292511 - autowrapping is weird,
     * even with the swingx fix 
     *
     */
    public void interactiveUpdateHeaderAndSizeRequirements() {
        
        final String[] alternate = { 
//                "simple", 
                "<html><b>This is a test of a large label to see if it wraps </font></b>",
                "simple", 
                //                "<html><center>Line 1<br>Line 2</center></html>" 
                };
        final JXTable table = new JXTable(10, 2);
        table.getColumn(0).setHeaderValue(alternate[0]);
        table.getColumn(1).setHeaderValue(alternate[1]);
        
        JXFrame frame = wrapWithScrollingInFrame(table, "update header");
        Action action = new AbstractAction("update headervalue") {
            boolean first;
            public void actionPerformed(ActionEvent e) {
                table.getColumn(1).setHeaderValue(first ? alternate[0] : alternate[1]);
                first = !first;
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }

    /**
     * Issue 337-swingx: header heigth depends on sort icon (for ocean only?) 
     * NOTE: this seems to be independent of the tweaks to xTableHeaders
     *   prefSize.
     */
    public void testSortedPreferredHeight() {
        JXTable table = new JXTable(10, 2);
        JXTableHeader tableHeader = (JXTableHeader) table.getTableHeader();
        Dimension dim = tableHeader.getPreferredSize();
        table.setSortOrder(0, SortOrder.ASCENDING);
        assertEquals("Header pref height must be unchanged if sorted",
                dim.height, tableHeader.getPreferredSize().height);
    }

}
