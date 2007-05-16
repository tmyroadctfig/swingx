/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;

/**
 * Contains tests around Highlighter client api for collection components.
 * <p>
 * 
 * Common public api:
 * 
 * <pre><code>
 *  void setHighlighters(Highlighter...)
 *  HighLighter[] getHighlighters()
 *  void addHighlighter(Highlighter)
 *  void removeHighlighter(Highlighter)
 * </code></pre>
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterClientTest extends InteractiveTestCase {

    //-------------- JXTable
    
    
    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    public void testTableSetHighlightersReset() {
        JXTable table = new JXTable();
        table.addHighlighter(new ColorHighlighter());
        // sanity
        assertEquals(1, table.getHighlighters().length);
        table.setHighlighters();
        assertEquals(0, table.getHighlighters().length);
    }

    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
    public void testTableRemoveHighlighter() {
        JXTable table = new JXTable();
        // test cope with null
        table.removeHighlighter(null);
        Highlighter presetHighlighter = new ColorHighlighter();
        table.setHighlighters(presetHighlighter);
        Highlighter[] highlighters = table.getHighlighters();
        // sanity
        assertEquals(1, highlighters.length);
        // remove uncontained
        table.removeHighlighter(new ColorHighlighter());
        // assert no change
        assertSameContent(highlighters, table.getHighlighters());
        table.removeHighlighter(presetHighlighter);
        assertEquals(0, table.getHighlighters().length);
    }
    

    /**
     * test choking on precondition failure (highlighter must not be null).
     *
     */
    public void testTableAddNullHighlighter() {
        JXTable table = new JXTable();
        try {
            table.addHighlighter(null);
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
        JXTable table = new JXTable();
        Highlighter presetHighlighter = new ColorHighlighter();
        // add the first
        table.addHighlighter(presetHighlighter);
        // assert that it is added
        assertEquals(1, table.getHighlighters().length);
        assertAsLast(table.getHighlighters(), presetHighlighter);
        Highlighter highlighter = new ColorHighlighter();
        // add the second
        table.addHighlighter(highlighter);
        assertEquals(2, table.getHighlighters().length);
        // assert that it is appended
        assertAsLast(table.getHighlighters(), highlighter);
    }

//----------------------------- JXList
    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    public void testListSetHighlightersReset() {
        JXList table = new JXList();
        table.addHighlighter(new ColorHighlighter());
        // sanity
        assertEquals(1, table.getHighlighters().length);
        table.setHighlighters();
        assertEquals(0, table.getHighlighters().length);
    }

    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
    public void testListRemoveHighlighter() {
        JXList table = new JXList();
        // test cope with null
        table.removeHighlighter(null);
        Highlighter presetHighlighter = new ColorHighlighter();
        table.setHighlighters(presetHighlighter);
        Highlighter[] highlighters = table.getHighlighters();
        // sanity
        assertEquals(1, highlighters.length);
        // remove uncontained
        table.removeHighlighter(new ColorHighlighter());
        // assert no change
        assertSameContent(highlighters, table.getHighlighters());
        table.removeHighlighter(presetHighlighter);
        assertEquals(0, table.getHighlighters().length);
    }
    

    /**
     * test choking on precondition failure (highlighter must not be null).
     *
     */
    public void testListAddNullHighlighter() {
        JXList table = new JXList();
        try {
            table.addHighlighter(null);
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
    public void testListAddHighlighter() {
        JXList table = new JXList();
        Highlighter presetHighlighter = new ColorHighlighter();
        // add the first
        table.addHighlighter(presetHighlighter);
        // assert that it is added
        assertEquals(1, table.getHighlighters().length);
        assertAsLast(table.getHighlighters(), presetHighlighter);
        Highlighter highlighter = new ColorHighlighter();
        // add the second
        table.addHighlighter(highlighter);
        assertEquals(2, table.getHighlighters().length);
        // assert that it is appended
        assertAsLast(table.getHighlighters(), highlighter);
    }

  //--------------------- JXTree
    
    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    public void testTreeSetHighlightersReset() {
        JXTree table = new JXTree();
        table.addHighlighter(new ColorHighlighter());
        // sanity
        assertEquals(1, table.getHighlighters().length);
        table.setHighlighters();
        assertEquals(0, table.getHighlighters().length);
    }

    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
    public void testTreeRemoveHighlighter() {
        JXTree table = new JXTree();
        // test cope with null
        table.removeHighlighter(null);
        Highlighter presetHighlighter = new ColorHighlighter();
        table.setHighlighters(presetHighlighter);
        Highlighter[] highlighters = table.getHighlighters();
        // sanity
        assertEquals(1, highlighters.length);
        // remove uncontained
        table.removeHighlighter(new ColorHighlighter());
        // assert no change
        assertSameContent(highlighters, table.getHighlighters());
        table.removeHighlighter(presetHighlighter);
        assertEquals(0, table.getHighlighters().length);
    }
    

    /**
     * test choking on precondition failure (highlighter must not be null).
     *
     */
    public void testTreeAddNullHighlighter() {
        JXTree table = new JXTree();
        try {
            table.addHighlighter(null);
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
    public void testTreeAddHighlighter() {
        JXTree table = new JXTree();
        Highlighter presetHighlighter = new ColorHighlighter();
        // add the first
        table.addHighlighter(presetHighlighter);
        // assert that it is added
        assertEquals(1, table.getHighlighters().length);
        assertAsLast(table.getHighlighters(), presetHighlighter);
        Highlighter highlighter = new ColorHighlighter();
        // add the second
        table.addHighlighter(highlighter);
        assertEquals(2, table.getHighlighters().length);
        // assert that it is appended
        assertAsLast(table.getHighlighters(), highlighter);
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

}
