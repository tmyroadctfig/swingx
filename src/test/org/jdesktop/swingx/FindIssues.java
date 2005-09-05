/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.text.BadLocationException;

/**
 * Exposing open issues in Searchable implementations.
 * 
 * @author Jeanette Winzenburg
 */
public class FindIssues extends FindTest {
    
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
