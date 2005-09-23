/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.text.BadLocationException;

import org.jdesktop.swingx.JXTable.TableSearchable;

/**
 * Exposing open issues in Searchable implementations.
 * 
 * @author Jeanette Winzenburg
 */
public class FindIssues extends FindTest {

    /** 
     * test if internal state is reset to not found by
     * passing a null searchstring.
     *
     */
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
     * test incremental search in JXTable.
     *
     */
    public void testTableIncremental() {
        JXTable table = new JXTable(new TestTableModel());
        String firstSearchText = "on";
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        // make sure we had a match
        int foundIndex = table.getSearchable().search(model.getPattern(), -1);
        assertEquals("must return be found", 0, foundIndex);
        // extended searchstring
        String secondSearchText ="one";
        model.setRawText(secondSearchText);
        // start search with row >> getRowCount()
        int secondFoundIndex = table.getSearchable().search(model.getPattern(), foundIndex);
        assertEquals("must not be found", foundIndex, secondFoundIndex);
        
    }

    /** 
     * test if starting a search in a non-contained index results
     * in not-found state.
     *
     */
    public void testTableInvalidStartIndex() {
        JXTable table = new JXTable(new TestTableModel());
        String firstSearchText = "one";
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        // make sure we had a match
        int foundIndex = table.getSearchable().search(model.getPattern(), -1);
        assertEquals("must return be found", 0, foundIndex);
        // start search with row >> getRowCount()
        int notFoundIndex = table.getSearchable().search(model.getPattern(), table.getRowCount() * 5, false);
        assertEquals("must not be found", -1, notFoundIndex);
        
    }
    /**
     * test if search loops all columns of previous row (backwards search).
     * 
     * Hmm... not testable? 
     * Needed to widen access for lastFoundColumn.
     */
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
     * test if match in same row but different column is found in backwards
     * search.
     *
     */
    public void testTableFoundPreviousColumnInSameRow() {
        JXTable table = new JXTable(new TestTableModel());
        int lastColumn = table.getColumnCount() -1;
        int row = 90;
        String firstSearchText = table.getValueAt(row, lastColumn).toString();
        // need a pattern for backwards search
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        int foundIndex = table.getSearchable().search(model.getPattern(), -1, true);
        assertEquals("last line found", row, foundIndex);
        String secondSearchText = table.getValueAt(row, lastColumn - 1).toString();
        model.setRawText(secondSearchText);
        int secondFoundIndex = table.getSearchable().search(model.getPattern(), foundIndex, true);
        assertEquals("must find match in same row", foundIndex, secondFoundIndex);
    }

    
    /**
     * test if search loops all columns of next row.
     *
     * Hmm... not testable? 
     * Needed to widen access for lastFoundColumn.
     */
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
    /**
     * check if not-wrapping returns not found marker (-1).
     *
     */
    public void testTableNotFoundWithoutWrapping() {
        JXTable table = new JXTable(new TestTableModel());
        int row = 90;
        String searchText = table.getValueAt(row, 0).toString();
        int foundIndex = table.getSearchable().search(searchText);
        assertEquals("last line found", row, foundIndex);
        int notFoundIndex = table.getSearchable().search(searchText, foundIndex);
        assertEquals("nothing found after last line", -1, notFoundIndex);
    }
    
    /**
     * test if not-wrapping returns not found marker (-1) if the
     * last found position is the last row.
     *
     */
    public void testTableNotFoundLastRowWithoutWrapping() {
        JXTable table = new JXTable(new TestTableModel());
        int row = table.getRowCount() - 1;
        String searchText = table.getValueAt(row, 0).toString();
        int foundIndex = table.getSearchable().search(searchText);
        assertEquals("last line found", row, foundIndex);
        int notFoundIndex = table.getSearchable().search(searchText, foundIndex);
        assertEquals("nothing found after last line", -1, notFoundIndex);
    }

    /**
     * test if not-wrapping returns not found marker (-1) if the
     * last found position is the first row.
     *
     */
    public void testTableNotFoundFirstRowWithoutWrapping() {
        JXTable table = new JXTable(new TestTableModel());
        int row = 0;
        PatternModel model = new PatternModel();
        String searchText = table.getValueAt(row, 0).toString();
        model.setRawText(searchText);
        int foundIndex = table.getSearchable().search(model.getPattern(), row + 1, true);
        assertEquals("last line found", row, foundIndex);
        int notFoundIndex = table.getSearchable().search(model.getPattern(), foundIndex, true);
        assertEquals("nothing found after last line", -1, notFoundIndex);
    }

    /**
     * Issue #??-swingx: backwards search not implemented in JXEditorPane.
     *
     */
    public void testEditorBackwards() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        int first = 2;
        try {
            editor.getDocument().getText(first, editor.getDocument().getLength() - first);
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PatternModel model = new PatternModel();
        model.setRawText("fo");
        int foIndex = editor.getSearchable().search(model.getPattern(), text.length() - 1, true);
        assertEquals("found index must be last occurence", text.lastIndexOf("fo"), foIndex);
        
    }
    
    /**
     * Test incremental searching: index must be the same if
     * extended text still matches.
     *
     */
    public void testEditorIncremental() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        String search = "fo";
        int first = editor.getSearchable().search(search, 0);
        assertEquals(0, first);
        String searchExt = search + "u";
        int second = editor.getSearchable().search(searchExt, first);
        assertEquals("index must be same if extension matches", first, second);
    }
    
    
    /**
     * NPE on no-match?
     */
    public void testEditorIncrementalNotFound() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        String search = "ou";
        int first = editor.getSearchable().search(search, 0);
        assertEquals(1, first);
        String searchExt = search + "u";
        try {
            int second = editor.getSearchable().search(searchExt, first);
            assertEquals("not found", -1, second);
            
        } catch (NullPointerException npe) {
            fail("npe");
        }
    }

    /**
     * test that search moves forward.
     *
     */
    public void testEditorFindNext() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        String search = "fou";
        int first = editor.getSearchable().search(search, -1);
        assertEquals(0, first);
        int second = editor.getSearchable().search(search, first);
        assertEquals(4, second);
    }
    /**
     * Testing graceful handling of start index out-of range
     * of document size.
     *
     */
    public void testEditorTolerateExceedingStartIndex() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("fou four");
        try {
            int foIndex = editor.getSearchable().search("fo", 20);
            assertEquals(-1, foIndex);
            
        } catch (Exception ex) {
            fail("search must not throw if index out off range");
        }
    }

    public void testEditorEmptyDocument() {
        JXEditorPane editor = new JXEditorPane();
        int foIndex = editor.getSearchable().search("fo", -1);
        assertEquals(-1, foIndex);
        foIndex = editor.getSearchable().search("fo", 0);
        assertEquals(-1, foIndex);
    }
    
    /**
     * testing incremental search: 
     * must start search at given position (inclusive).
     * 
     * This implies that search(xx, -1) is equivalent to 
     * search(xx, 0) if the match is at position 0.
     *
     */
    public void testEditorBoundarySearchIndex() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("f");
       // can't test in one method - the searchable has internal state
        int startOff = editor.getSearchable().search("f", -1);
        assertEquals("must return first occurence if startIndex if off", 0, startOff);
        // sanity - must not find mismatch if longer
        int foIndex = editor.getSearchable().search("fo", -1);
        assertEquals("must not find exceeding text", -1, foIndex);
        foIndex = editor.getSearchable().search("f", 0);
        assertEquals("must return first occurence from startIndex inclusively",0 , foIndex);
    }
    /**
     * Issue #100-swingx: expect to return the start of the match.
     * Only then it's possible to implement a reasonably behaved
     * incremental search.
     *
     */
    public void testEditorFindMatchPosition() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("fou four");
        int foIndex = editor.getSearchable().search("fo", -1);
        assertEquals(0, foIndex);
    }
    
    /**
     * testing Searchable assumption along the lines of: 
     * found = searchable.search(text)
     * searchable.getValueAt(found).startsWith(text) or
     * searchable.getValueAt(found).contains(text)
     * 
     */
    public void testEditorFindMatch() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("fou four");
        int foIndex = editor.getSearchable().search("fo", -1);
        assertEquals("selected text must be equals to input", "fo", editor.getSelectedText());
        try {
            String textAt = editor.getText(foIndex, 2);
            assertEquals("fo", textAt);
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

}
