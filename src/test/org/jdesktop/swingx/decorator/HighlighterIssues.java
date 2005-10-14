/*
 * Created on 14.10.2005
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;

import javax.swing.JLabel;

public class HighlighterIssues extends HighlighterTest {

    /**
     * Issue #??-swingx: Highlighters always change the selection color.
     */
    public void testUnselectedDoNothingHighlighter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        emptyHighlighter.highlight(allColored, adapter);
        assertEquals("default highlighter must not change foreground", foreground, allColored.getForeground());
        assertEquals("default highlighter must not change background", background, allColored.getBackground());
    }

}
