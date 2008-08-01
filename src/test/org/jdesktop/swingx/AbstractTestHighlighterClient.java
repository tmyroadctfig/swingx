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

import junit.framework.TestCase;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;

/**
 * Contains tests around Highlighter client api for collection components.
 * <p>
 * 
 * The common public api:
 * 
 * <pre><code>
 *  void setHighlighters(Highlighter...)
 *  HighLighter[] getHighlighters()
 *  void addHighlighter(Highlighter)
 *  void removeHighlighter(Highlighter)
 *  updateUI()
 * </code></pre>
 * 
 * Subclasses testing concrete implementations must override the createHighlighterClient.
 * 
 * @author Jeanette Winzenburg
 */
public abstract class AbstractTestHighlighterClient extends TestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(AbstractTestHighlighterClient.class.getName());
    
    // ---- HighlighterClient
    

    /**
     * 
     * @return the concrete HighlighterClient to test
     */
    protected abstract HighlighterClient createHighlighterClient();
    
    /**
     * Test that the client is messaged on change to a managed Highlighter.
     */
    public void testUpdateUI() {
        HighlighterClient client = createHighlighterClient();
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

    public void testSetHighlighters() {
        HighlighterClient client = createHighlighterClient();
        Highlighter[] highlighters = new Highlighter[] {new ColorHighlighter(), new ColorHighlighter()};
        client.setHighlighters(highlighters);
        assertSameContent(highlighters, client.getHighlighters());
    }

    /**
     * Test property change event on setHighlighters for JXTable.
     */
    public void testSetHighlightersChangeEvent() {
        HighlighterClient client = createHighlighterClient();
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
    public void testSetHighlightersEmptyArray() {
        HighlighterClient client = createHighlighterClient();
        client.setHighlighters(new Highlighter[] {});
        assertEquals(0, client.getHighlighters().length);
    }

    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    public void testSetHighlightersNoArgument() {
        HighlighterClient client = createHighlighterClient();
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
    public void testSetHighlightersNullHighlighter() {
        try {
            createHighlighterClient().setHighlighters((Highlighter) null);
            fail("illegal to call setHighlighters(null)");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters for JXTable.
     * 
     * Here: null array
     */
    public void testSetHighlightersNullArray() {
        try {
            createHighlighterClient().setHighlighters((Highlighter[]) null);
            fail("illegal to call setHighlighters(null)");
            
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters for JXTable.
     * 
     * Here: null array element.
     */
    public void testSetHighlightersArrayNullElement() {
        try {
            createHighlighterClient().setHighlighters(new Highlighter[] {null});
            fail("illegal to call setHighlighters(null)");
            
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
    public void testRemoveHighlighterTable() {
        HighlighterClient client = createHighlighterClient();
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
     * Test property change event on removeHighlighter for JXTable.
     */
    public void testRemoveHighlightersChangeEvent() {
        HighlighterClient table = createHighlighterClient();
        Highlighter highlighter = new ColorHighlighter();
        table.setHighlighters(highlighter);
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Highlighter[] old = table.getHighlighters();
        table.removeHighlighter(highlighter);
        TestUtils.assertPropertyChangeEvent(report, "highlighters", old, table.getHighlighters());
    }

    /**
     * test if addHighlighter behaves as doc'ed for JXTable.
     *
     */
    public void testAddHighlighter() {
        HighlighterClient client = createHighlighterClient();
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
     * Test property change event on addHighlighter for JXTable.
     */
    public void testAddHighlighterChangeEvent() {
        HighlighterClient table = createHighlighterClient();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Highlighter[] old = table.getHighlighters();
        Highlighter highlighter = new ColorHighlighter();
        table.addHighlighter(highlighter);
        TestUtils.assertPropertyChangeEvent(report, "highlighters", old, table.getHighlighters());
    }

    /**
     * test choking on precondition failure (highlighter must not be null) for JTXTable.
     *
     */
    public void testAddNullHighlighter() {
        try {
            createHighlighterClient().addHighlighter(null);
            fail("adding a null highlighter must throw NPE");
        } catch (NullPointerException e) {
            // pass - this is what we expect
        } catch (Exception e) {
            fail("adding a null highlighter throws exception different " +
                        "from the expected NPE \n" + e);
        }
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
