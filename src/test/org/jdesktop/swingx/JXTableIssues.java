/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.util.regex.Pattern;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXTable;

/**
 * @author Jeanette Winzenburg
 */
public class JXTableIssues extends TestCase {

    private DefaultTableModel createModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 5);
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }
    
    /**
     * JW: Still needed? moved to main testCase?
     *
     */
    public void testNewRendererInstance() {
        JXTable table = new JXTable();
        TableCellRenderer newRenderer = table.getNewDefaultRenderer(Boolean.class);
        TableCellRenderer sharedRenderer = table.getDefaultRenderer(Boolean.class);
        assertNotNull(newRenderer);
        assertNotSame("new renderer must be different from shared", sharedRenderer, newRenderer);
        assertNotSame("new renderer must be different from object renderer", 
                table.getDefaultRenderer(Object.class), newRenderer);
    }

    /**
     * Issue #??: JXTable pattern search differs from 
     * PatternHighlighter/Filter.
     * 
     */
    public void testRespectPatternInSearch() {
        JXTable table = new JXTable(createModel(0, 11));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        int found = table.search(Pattern.compile(lastName), -1, false);
        assertEquals("found must be equal to row", row, found);
        found = table.search(Pattern.compile(lastName), found, false);
        assertEquals("search must fail", -1, found);
    }

    /**
     * Issue #??: JXTable pattern search differs from 
     * PatternHighlighter/Filter. 
     * 
     * Fixing the issue (respect the pattern as is by calling 
     * pattern.matcher().matches instead of the find()) must 
     * make sure that the search methods taking the string 
     * include wildcards.
     *
     *  Note: this method passes as long as the issue is not
     *  fixed!
     */
    public void testWildCardInSearchByString() {
        JXTable table = new JXTable(createModel(0, 11));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        int found = table.search(lastName, -1);
        assertEquals("found must be equal to row", row, found);
        found = table.search(lastName, found);
        assertEquals("search must succeed", 10, found);
    }


//    public static void main(String[] args) {
//        JXTableIssues issues = new JXTableIssues();
//        
//    }
}
