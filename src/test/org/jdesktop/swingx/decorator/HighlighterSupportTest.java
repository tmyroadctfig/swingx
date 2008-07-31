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
package org.jdesktop.swingx.decorator;



import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.UIManager;

import junit.framework.TestCase;

/**
 * Unit tests for HighlighterSupport.
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterSupportTest extends TestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(HighlighterSupportTest.class.getName());
    /**
     * Test instantiation - must fire NPE on null client.
     */
    public void testHighlighterSupportNullClient() {
        try {
            new HighlighterSupport(null);
            fail("HighlighterSupport constructor must not accept null client");
        } catch (NullPointerException e) {
            // expected
        }
        
    }

    /**
     * Test that the client is messaged on change to a managed Highlighter.
     */
    public void testCallBackOnChange() {
        HighlighterClient client = new HighlighterClient();
        HighlighterSupport support = new HighlighterSupport(client);
        ColorHighlighter colorHighlighter = new ColorHighlighter();
        support.addHighlighter(colorHighlighter);
        colorHighlighter.setBackground(Color.RED);
        assertEquals(1, client.getCallCount());
    }
    
 
    /**
     * Test that the client is messaged on change to a managed Highlighter.
     */
    public void testUpdateUI() {
        HighlighterClient client = new HighlighterClient();
        HighlighterSupport support = new HighlighterSupport(client);
        // force loading of striping colors
        ColorHighlighter colorHighlighter = (ColorHighlighter) HighlighterFactory.createSimpleStriping();
        Color uiColor = UIManager.getColor("UIColorHighlighter.stripingBackground");
        if (uiColor == null) {
            LOG.info("cannot run test - no ui striping color");
            return;
        }
        assertSame("sanity", uiColor, colorHighlighter.getBackground());
        support.addHighlighter(colorHighlighter);
        Color changedUIColor = Color.RED;
        UIManager.put("UIColorHighlighter.stripingBackground", changedUIColor);
        support.updateUI();
        try {
            assertSame("support must update ui color", changedUIColor, colorHighlighter.getBackground());
        } finally {
            UIManager.put("UIColorHighlighter.stripingBackground", uiColor);
        }
        
    }

    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    public void testTableSetHighlightersReset() {
        JComponent client = new HighlighterClient();
        HighlighterSupport support = new HighlighterSupport(client);
        support.addHighlighter(new ColorHighlighter());
        // sanity
        assertEquals(1, support.getHighlighters().length);
        support.setHighlighters();
        assertEquals(0, support.getHighlighters().length);
    }

    /**
     * Sanity: handles empty array.
     */
    public void testSetHighlightersEmptyArray() {
        JComponent client = new HighlighterClient();
        HighlighterSupport support = new HighlighterSupport(client);
        support.setHighlighters(new Highlighter[] {});
        assertEquals(0, support.getHighlighters().length);
    }

    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
    public void testTableRemoveHighlighter() {
        JComponent client = new HighlighterClient();
        HighlighterSupport support = new HighlighterSupport(client);
        // test cope with null
        support.removeHighlighter(null);
        Highlighter presetHighlighter = new ColorHighlighter();
        support.setHighlighters(presetHighlighter);
        Highlighter[] highlighters = support.getHighlighters();
        // sanity
        assertEquals(1, highlighters.length);
        // remove uncontained
        support.removeHighlighter(new ColorHighlighter());
        // assert no change
        assertSameContent(highlighters, support.getHighlighters());
        support.removeHighlighter(presetHighlighter);
        assertEquals(0, support.getHighlighters().length);
    }
    

    /**
     * test choking on precondition failure (highlighter must not be null).
     *
     */
    public void testTableAddNullHighlighter() {
        JComponent client = new HighlighterClient();
        HighlighterSupport support = new HighlighterSupport(client);
        try {
            support.addHighlighter(null);
            fail("adding a null highlighter must throw NPE");
        } catch (NullPointerException e) {
            // pass - this is what we expect
        } catch (Exception e) {
            fail("adding a null highlighter throws exception different " +
                        "from the expected NPE \n" + e);
        }
    }
    /**
     * test if addHighlighter behaves as doc'ed.
     *
     */
    public void testTableAddHighlighter() {
        JComponent client = new HighlighterClient();
        HighlighterSupport support = new HighlighterSupport(client);
        Highlighter presetHighlighter = new ColorHighlighter();
        // add the first
        support.addHighlighter(presetHighlighter);
        // assert that it is added
        assertEquals(1, support.getHighlighters().length);
        assertAsLast(support.getHighlighters(), presetHighlighter);
        Highlighter highlighter = new ColorHighlighter();
        // add the second
        support.addHighlighter(highlighter);
        assertEquals(2, support.getHighlighters().length);
        // assert that it is appended
        assertAsLast(support.getHighlighters(), highlighter);
    }


    /**
     * Same content in both.
     * @param highlighters
     * @param highlighters2
     */
    private void assertSameContent(Highlighter[] highlighters, Highlighter[] highlighters2) {
        assertEquals(highlighters.length, highlighters2.length);
        for (int i = 0; i < highlighters.length; i++) {
            assertSame("must contain same element", highlighters[i], highlighters2[i]);
        }
    }
    
    /**
     * Last in list.
     * 
     * @param highlighters
     * @param highlighter
     */
    private void assertAsLast(Highlighter[] highlighters, Highlighter highlighter) {
        assertTrue("pipeline must not be empty", highlighters.length > 0);
        assertSame("highlighter must be added as last", highlighter, highlighters[highlighters.length - 1]);
    }

    /**
     * Stand-in for testing
     */
    public static class HighlighterClient extends JComponent {
        
        private int callCount;

        @Override
        public void repaint() {
            callCount++;
        }
        
        public int getCallCount() {
            return callCount;
        }
        
        public void clear() {
            callCount = 0;
        }
    }
}
