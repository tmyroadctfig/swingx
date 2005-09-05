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
    public void testEditorTolerateExceedingStartIndex() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("fou four");
        int foIndex = editor.getSearchable().search("fo", 20);
        assertEquals(-1, foIndex);
    }

    public void testEditorEmptyDocument() {
        JXEditorPane editor = new JXEditorPane();
        int foIndex = editor.getSearchable().search("fo", -1);
        assertEquals(-1, foIndex);
        foIndex = editor.getSearchable().search("fo", 0);
        assertEquals(-1, foIndex);
    }
    
    public void testEditorBoundarySearchIndex() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("f");
        int foIndex = editor.getSearchable().search("fo", -1);
        assertEquals(-1, foIndex);
        foIndex = editor.getSearchable().search("f", -1);
        assertEquals(0, foIndex);
        foIndex = editor.getSearchable().search("f", 0);
        assertEquals(0, foIndex);
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
