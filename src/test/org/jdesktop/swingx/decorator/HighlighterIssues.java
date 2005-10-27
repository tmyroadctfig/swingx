/*
 * Created on 14.10.2005
 *
 */
package org.jdesktop.swingx.decorator;


public class HighlighterIssues extends HighlighterTest {

    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    public void testSelectedDoNothingHighlighter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        emptyHighlighter.highlight(allColored, adapter);
        assertEquals("default highlighter must not change foreground", foreground, allColored.getForeground());
        assertEquals("default highlighter must not change background", background, allColored.getBackground());
    }

}
