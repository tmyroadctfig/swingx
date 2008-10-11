/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx.search;


import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.FindTest.TestTableModel;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


/**
 * Unit tests of search related services. <p>
 * 
 * PENDING JW: need to cleanup packeging - this here is a quick extract of swingx.FindTest 
 * of those methods which test internals. The internals are no longer visible in the
 * swingx package (which is good). Moving the complete FindXX tests didn't work out 
 * because some test internals of the swingx package. 
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class FindTest extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(FindTest.class.getName());
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // sanity: new instance for each test
        SearchFactory.setInstance(new SearchFactory());
    }

    /**
     * Issue #718-swingx: shared FindPanel not updated on LF change.
     * 
     * Here: check that containing dialog is disposed, old api (no boolean).
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testFindDialogDisposeDeprecated() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        JXFrame frame = new JXFrame();
        JXTable table = new JXTable();
        frame.add(table);
        JComponent findPanel = SearchFactory.getInstance().getSharedFindPanel();
        SearchFactory.getInstance().showFindDialog(table, table.getSearchable());
        Window window = SwingUtilities.getWindowAncestor(findPanel);
        assertSame(frame, window.getOwner());
        SearchFactory.getInstance().hideSharedFindPanel(true);
        assertFalse("window must not be displayable", window.isDisplayable());
        assertNull("findPanel must be unparented", findPanel.getParent());
    }
    
    /**
     * Issue #718-swingx: shared FindPanel not updated on LF change.
     * 
     * Here: check that containing dialog is disposed, new api with flag.
     */
    @Test
    public void testFindDialogDispose() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        JXFrame frame = new JXFrame();
        JXTable table = new JXTable();
        frame.add(table);
        JComponent findPanel = SearchFactory.getInstance().getSharedFindPanel();
        SearchFactory.getInstance().showFindDialog(table, table.getSearchable());
        Window window = SwingUtilities.getWindowAncestor(findPanel);
        assertSame(frame, window.getOwner());
        SearchFactory.getInstance().hideSharedFindPanel(true);
        assertFalse("window must not be displayable", window.isDisplayable());
        assertNull("findPanel must be unparented", findPanel.getParent());
    }

    /**
     * Issue #718-swingx: shared FindPanel not updated on LF change.
     * 
     * Here: check that containing dialog is not disposed.
     */
    @Test
    public void testFindDialogHide() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        JXFrame frame = new JXFrame();
        JXTable table = new JXTable();
        frame.add(table);
        JComponent findPanel = SearchFactory.getInstance().getSharedFindPanel();
        SearchFactory.getInstance().showFindDialog(table, table.getSearchable());
        Container parent = findPanel.getParent();
        Window window = SwingUtilities.getWindowAncestor(findPanel);
        assertSame(frame, window.getOwner());
        SearchFactory.getInstance().hideSharedFindPanel(false);
        assertFalse("window must not be visible", window.isVisible());
        assertSame("findPanel must parent must be unchanged", 
                parent, findPanel.getParent());
        assertTrue("window must be displayable", window.isDisplayable());
    }
    
    /** 
     * test if internal state is reset to not found by
     * passing a null searchstring.
     *
     */
    @Test
    public void testTableResetStateWithNullSearchString() {
        JXTable table = new JXTable(new TestTableModel());
        int row = 39;
        int firstColumn = 0;
        String firstSearchText = table.getValueAt(row, firstColumn).toString();
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        // initialize searchable to "found state"
        int foundIndex = table.getSearchable().search(model.getPattern(), -1);
        // sanity asserts
        int foundColumn = ((TableSearchable) table.getSearchable()).lastSearchResult.foundColumn;
        assertEquals("last line found", row, foundIndex);
        assertEquals("column must be updated", firstColumn, foundColumn);
        // search with null searchstring 
        int notFoundIndex =  table.getSearchable().search((String) null);
        assertEquals("nothing found", -1, notFoundIndex);
        assertEquals("column must be reset", -1, ((TableSearchable) table.getSearchable()).lastSearchResult.foundColumn);

    }

    /** 
     * test if internal state is reset to not found by
     * passing a empty (="") searchstring.
     *
     */
    @Test
    public void testTableResetStateWithEmptySearchString() {
        JXTable table = new JXTable(new TestTableModel());
        int row = 39;
        int firstColumn = 0;
        String firstSearchText = table.getValueAt(row, firstColumn).toString();
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        // initialize searchable to "found state"
        int foundIndex = table.getSearchable().search(model.getPattern(), -1);
        // sanity asserts
        int foundColumn = ((TableSearchable) table.getSearchable()).lastSearchResult.foundColumn;
        assertEquals("last line found", row, foundIndex);
        assertEquals("column must be updated", firstColumn, foundColumn);
        // search with null searchstring 
        int notFoundIndex =  table.getSearchable().search("");
        assertEquals("nothing found", -1, notFoundIndex);
        assertEquals("column must be reset", -1, ((TableSearchable) table.getSearchable()).lastSearchResult.foundColumn);

    }

    /**
     * test if search loops all columns of previous row (backwards search).
     * 
     * Hmm... not testable? 
     * Needed to widen access for lastFoundColumn.
     */
    @Test
    public void testTableFoundNextColumnInPreviousRow() {
        JXTable table = new JXTable(new TestTableModel());
        int lastColumn = table.getColumnCount() -1;
        int row = 39;
        int firstColumn = lastColumn - 1;
        String firstSearchText = table.getValueAt(row, firstColumn).toString();
        // need a pattern for backwards search
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        int foundIndex = table.getSearchable().search(model.getPattern(), -1, true);
        assertEquals("last line found", row, foundIndex);
        int foundColumn = ((TableSearchable) table.getSearchable()).lastSearchResult.foundColumn;
        assertEquals("column must be updated", firstColumn, foundColumn);
        // the last char(s) of all values is the row index
        // here we are searching for an entry in the next row relative to
        // the previous search and expect the match in the first column (index = 0);
        int previousRow = row -1;
        String secondSearchText = String.valueOf(previousRow);
        model.setRawText(secondSearchText);
        int secondFoundIndex = table.getSearchable().search(model.getPattern(), previousRow, true);
        // sanity assert
        assertEquals("must find match in same row", previousRow, secondFoundIndex);
        assertEquals("column must be updated", lastColumn, ((TableSearchable) table.getSearchable()).lastSearchResult.foundColumn);
        
    }

    /**
     * test if search loops all columns of next row.
     *
     * Hmm... not testable? 
     * Needed to widen access for lastFoundColumn.
     */
    @Test
    public void testTableFoundPreviousColumnInNextRow() {
        JXTable table = new JXTable(new TestTableModel());
        int row = 0;
        int firstColumn = 1;
        String firstSearchText = table.getValueAt(row, firstColumn).toString();
        int foundIndex = table.getSearchable().search(firstSearchText);
        assertEquals("last line found", row, foundIndex);
        int foundColumn = ((TableSearchable) table.getSearchable()).lastSearchResult.foundColumn;
        assertEquals("column must be updated", firstColumn, foundColumn);
        // the last char(s) of all values is the row index
        // here we are searching for an entry in the next row relative to
        // the previous search and expect the match in the first column (index = 0);
        int nextRow = row + 1;
        String secondSearchText = String.valueOf(nextRow);
        int secondFoundIndex = table.getSearchable().search(secondSearchText, nextRow);
        // sanity assert
        assertEquals("must find match in same row", nextRow, secondFoundIndex);
        assertEquals("column must be updated", 0, ((TableSearchable) table.getSearchable()).lastSearchResult.foundColumn);
        
    }

    /**
     * test if match in same row but different column is found in forward
     * search.
     *
     */
    @Test
    public void testTableFoundNextColumnInSameRow() {
        JXTable table = new JXTable(new TestTableModel());
        int row = 90;
        int firstColumn = 0;
        String firstSearchText = table.getValueAt(row, firstColumn).toString();
        int foundIndex = table.getSearchable().search(firstSearchText);
        assertEquals("last line found", row, foundIndex);
        String secondSearchText = table.getValueAt(row, firstColumn +1).toString();
        int secondFoundIndex = table.getSearchable().search(secondSearchText, foundIndex);
        assertEquals("must find match in same row", foundIndex, secondFoundIndex);
        
    }

}
