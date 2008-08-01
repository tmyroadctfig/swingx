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

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;

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
 *  updateUI()
 * </code></pre>
 * 
 * The asserXX private methods constitute the contract of a HighlighterClient. Each concrete
 * implementation needs to pass test methods which delegation to every of those.  
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterClientTest extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(HighlighterClientTest.class.getName());
    
    // ---- HighlighterClient
    
    
    //-------------- JXTable
    
    /**
     * Test that the client is messaged on change to a managed Highlighter.
     */
    private void assertUpdateUI(HighlighterClient client) {
        // force loading of striping colors
        ColorHighlighter colorHighlighter = (ColorHighlighter) HighlighterFactory.createSimpleStriping();
        Color uiColor = UIManager.getColor("UIColorHighlighter.stripingBackground");
        if (uiColor == null) {
            LOG.info("cannot run test - no ui striping color");
            return;
        }
        assertSame("sanity", uiColor, colorHighlighter.getBackground());
        client.addHighlighter(colorHighlighter);
        Color changedUIColor = Color.RED;
        UIManager.put("UIColorHighlighter.stripingBackground", changedUIColor);
        client.updateUI();
        try {
            assertSame("support must update ui color", changedUIColor, colorHighlighter.getBackground());
        } finally {
            UIManager.put("UIColorHighlighter.stripingBackground", uiColor);
        }
    }

    private void assertSetHighlighters(HighlighterClient client) {
        Highlighter[] highlighters = new Highlighter[] {new ColorHighlighter(), new ColorHighlighter()};
        client.setHighlighters(highlighters);
        assertSameContent(highlighters, client.getHighlighters());
    }
    
    /**
     * Test property change event on setHighlighters.
     */
    private void assertSetHighlightersChangeEvent(HighlighterClient client) {
        PropertyChangeReport report = new PropertyChangeReport();
        client.addPropertyChangeListener(report);
        Highlighter[] old = client.getHighlighters();
        Highlighter highlighter = new ColorHighlighter();
        client.setHighlighters(highlighter);
        TestUtils.assertPropertyChangeEvent(report, "highlighters", old, client.getHighlighters());
    }

    /**
     * Sanity: handles empty array.
     */
    private void assertSetHighlightersEmptyArray(HighlighterClient client) {
        client.setHighlighters(new Highlighter[] {});
        assertEquals(0, client.getHighlighters().length);
    }

    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    private void assertSetHighlightersNoArgument(HighlighterClient client) {
        client.addHighlighter(new ColorHighlighter());
        // sanity
        assertEquals(1, client.getHighlighters().length);
        client.setHighlighters();
        assertEquals(0, client.getHighlighters().length);
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters for JXTable.
     * 
     * Here: null highlighter.
     */
    private void assertSetHighlightersNullHighlighter(HighlighterClient client) {
        try {
            client.setHighlighters((Highlighter) null);
            fail("illegal to call setHighlighters(null)");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters.
     * 
     * Here: null array
     */
    private void assertSetHighlightersNullArray(HighlighterClient client) {
        try {
            client.setHighlighters((Highlighter[]) null);
            fail("illegal to call setHighlighters(null)");
            
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters.
     * Here: null array element.
     */
    private void assertSetHighlightersArrayNullElement(HighlighterClient client) {
        try {
            client.setHighlighters(new Highlighter[] {null});
            fail("illegal to call setHighlighters(null)");
            
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
    private void assertRemoveHighlighter(HighlighterClient client) {
        // test cope with null
        client.removeHighlighter(null);
        Highlighter presetHighlighter = new ColorHighlighter();
        client.setHighlighters(presetHighlighter);
        Highlighter[] highlighters = client.getHighlighters();
        // sanity
        assertEquals(1, highlighters.length);
        // remove uncontained
        client.removeHighlighter(new ColorHighlighter());
        // assert no change
        assertSameContent(highlighters, client.getHighlighters());
        client.removeHighlighter(presetHighlighter);
        assertEquals(0, client.getHighlighters().length);
    }

    /**
     * Test property change event on removeHighlighter.
     */
    private void assertRemoveHighlighterChangeEvent(HighlighterClient table) {
        Highlighter highlighter = new ColorHighlighter();
        table.setHighlighters(highlighter);
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Highlighter[] old = table.getHighlighters();
        table.removeHighlighter(highlighter);
        TestUtils.assertPropertyChangeEvent(report, "highlighters", old, table.getHighlighters());
    }

    /**
     * test choking on precondition failure (highlighter must not be null).
     *
     */
    private void assertAddHighlighterNullHighlighter(HighlighterClient client) {
        try {
            client.addHighlighter(null);
            fail("adding a null highlighter must throw NPE");
        } catch (NullPointerException e) {
            // pass - this is what we expect
        } catch (Exception e) {
            fail("adding a null highlighter throws exception different " +
                        "from the expected NPE \n" + e);
        }
    }

    private void assertAddHighlighter(HighlighterClient client) {
        Highlighter presetHighlighter = new ColorHighlighter();
        // add the first
        client.addHighlighter(presetHighlighter);
        // assert that it is added
        assertEquals(1, client.getHighlighters().length);
        assertAsLast(client.getHighlighters(), presetHighlighter);
        Highlighter highlighter = new ColorHighlighter();
        // add the second
        client.addHighlighter(highlighter);
        assertEquals(2, client.getHighlighters().length);
        // assert that it is appended
        assertAsLast(client.getHighlighters(), highlighter);
    }

    /**
     * Test property change event on addHighlighter.
     */
    private void assertAddHighlighterChangeEvent(HighlighterClient table) {
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Highlighter[] old = table.getHighlighters();
        Highlighter highlighter = new ColorHighlighter();
        table.addHighlighter(highlighter);
        TestUtils.assertPropertyChangeEvent(report, "highlighters", old, table.getHighlighters());
    }

    //-------------- test JXTable    
    /**
     * Test that the client is messaged on change to a managed Highlighter.
     */
    public void testUpdateUITable() {
        assertUpdateUI(createHighlighterClient(new JXTable()));
        
    }

    public void testSetHighlightersTable() {
        assertSetHighlighters(createHighlighterClient(new JXTable()));
    }

    /**
     * Test property change event on setHighlighters for JXTable.
     */
    public void testSetHighlightersChangeEventTable() {
        assertSetHighlightersChangeEvent(createHighlighterClient(new JXTable()));
    }

    /**
     * Sanity: handles empty array.
     */
    public void testSetHighlightersEmptyArray() {
        assertSetHighlightersEmptyArray(createHighlighterClient(new JXTable()));
    }

    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    public void testSetHighlightersNoArgumentTable() {
        assertSetHighlightersNoArgument(createHighlighterClient(new JXTable()));
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters for JXTable.
     * 
     * Here: null highlighter.
     */
    public void testSetHighlightersNullHighlighterTable() {
        assertSetHighlightersNullHighlighter(createHighlighterClient(new JXTable()));
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters for JXTable.
     * 
     * Here: null array
     */
    public void testSetHighlightersNullArrayTable() {
        assertSetHighlightersNullArray(createHighlighterClient(new JXTable()));
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters for JXTable.
     * 
     * Here: null array element.
     */
    public void testSetHighlightersArrayNullElementTable() {
        assertSetHighlightersArrayNullElement(createHighlighterClient(new JXTable()));
    }

    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
    public void testRemoveHighlighterTable() {
        assertRemoveHighlighter(createHighlighterClient(new JXTable()));
    }

    /**
     * Test property change event on removeHighlighter for JXTable.
     */
    public void testRemoveHighlightersChangeEventTable() {
        assertRemoveHighlighterChangeEvent(createHighlighterClient(new JXTable()));
    }

    /**
     * test if addHighlighter behaves as doc'ed for JXTable.
     *
     */
    public void testAddHighlighterTable() {
        assertAddHighlighter(createHighlighterClient(new JXTable()));
    }

    /**
     * Test property change event on addHighlighter for JXTable.
     */
    public void testAddHighlighterChangeEventTable() {
        assertAddHighlighterChangeEvent(createHighlighterClient(new JXTable()));
    }

    /**
     * test choking on precondition failure (highlighter must not be null) for JTXTable.
     *
     */
    public void testAddNullHighlighterTable() {
        assertAddHighlighterNullHighlighter(createHighlighterClient(new JXTable()));
    }

    //----------------------------- JXList
    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    public void testListSetHighlightersNoArgument() {
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

    
    private HighlighterClient createHighlighterClient(final JXTable table) {
        HighlighterClient client = new HighlighterClient() {

            public void addHighlighter(Highlighter highlighter) {
                table.addHighlighter(highlighter);
            }

            public void addPropertyChangeListener(PropertyChangeListener l) {
                table.addPropertyChangeListener(l);
            }

            public Highlighter[] getHighlighters() {
                return table.getHighlighters();
            }

            public void removeHighlighter(Highlighter highlighter) {
                table.removeHighlighter(highlighter);
            }

            public void removePropertyChangeListener(PropertyChangeListener l) {
                table.removePropertyChangeListener(l);
            }

            public void setHighlighters(Highlighter... highlighters) {
                table.setHighlighters(highlighters);
            }

            public void updateUI() {
                table.updateUI();
            }
            
        };
        return client;
    }
    
    public static interface HighlighterClient {

        /**
         * Sets the <code>Highlighter</code>s to this client, replacing any old settings.
         * No argument or an empty array removes all <code>Highlighter</code>s. <p>
         * 
         * This is a bound property.
         * 
         * @param highlighters zero or more not null highlighters to use for renderer decoration.
         * @throws NullPointerException if array is null or array contains null values.
         * 
         * @see #getHighlighters()
         * @see #addHighlighter(Highlighter)
         * @see #removeHighlighter(Highlighter)
         * 
         */
        void setHighlighters(Highlighter... highlighters);


        /**
         * Returns the <code>Highlighter</code>s used by this client.
         * Maybe empty, but guarantees to be never null.
         * 
         * @return the Highlighters used by this table, guaranteed to never null.
         * 
         * @see #setHighlighters(Highlighter[])
         */
        Highlighter[] getHighlighters();

        /**
         * Appends a <code>Highlighter</code> to the end of the list of used
         * <code>Highlighter</code>s. The argument must not be null. 
         * <p>
         * 
         * @param highlighter the <code>Highlighter</code> to add, must not be null.
         * @throws NullPointerException if <code>Highlighter</code> is null.
         * 
         * @see #removeHighlighter(Highlighter)
         * @see #setHighlighters(Highlighter[])
         */
        void addHighlighter(Highlighter highlighter);

        /**
         * Removes the given <code>Highlighter</code>. Does nothing if the
         * <code>Highlighter</code> is not contained.
         * 
         * @param highlighter the <code>Highlighter</code> to remove.
         * 
         * @see #addHighlighter(Highlighter)
         * @see #setHighlighters(Highlighter...)
         */
        void removeHighlighter(Highlighter highlighter);

        /**
         * Updates contained Highlighters on LAF changes.
         */
        void updateUI();
        
        /**
         * Adds a PropertyChangeListener which will be notified on changes of the 
         * "highlighters" property.
         * 
         * @param l the listener to add.
         */
        void addPropertyChangeListener(PropertyChangeListener l);

        /**
         * Removes the <code>PropertyChangeListener</code>. Does nothing if the
         * listener is not contained.
         * 
         * @param l the listener to remove. 
         */
        void removePropertyChangeListener(PropertyChangeListener l);
    }
}
