/*
 * Created on 31.08.2005
 *
 */
package org.jdesktop.swingx;

import javax.swing.text.BadLocationException;

public class FindIssues extends FindTest {

    /**
     * Issue #??-swingx: expect to return the start of the match.
     * Only then it's possible to implement a reasonably behaved
     * incremental search.
     *
     */
    public void testEditorFindMatchPosition() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("fou four");
        int foIndex = editor.search("fo", -1);
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
        int foIndex = editor.search("fo", -1);
        assertTrue("fo".equals(editor.getSelectedText()));
        try {
            String textAt = editor.getText(foIndex, 2);
            assertEquals("fo", textAt);
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

}
