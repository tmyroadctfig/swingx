/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.Color;

import javax.swing.JLabel;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXTable;

public class HighlighterTest extends TestCase {

    private Highlighter[] highlighters;

    protected void setUp() {
        highlighters = new Highlighter[] {
            new AlternateRowHighlighter(Color.white, new Color(0xF0, 0xF0, 0xE0), null),
            new PatternHighlighter(null, Color.red, "s.*", 0, 0)
        };
    }

    protected void tearDown() {
        for (int i = 0; i < highlighters.length; i++) {
            highlighters[i] = null;
        }
        highlighters = null;
    }

    /**
     * This is a test to ensure that the example in the javadoc actually works.
     * if the javadoc example changes, then those changes should be pasted here.
     */
    public void testJavaDocExample() {
        Highlighter[]   highlighters = new Highlighter[] {
            new AlternateRowHighlighter(Color.white, new Color(0xF0, 0xF0, 0xE0), null),
            new PatternHighlighter(null, Color.red, "s.*", 0, 0)
        };

        HighlighterPipeline highlighterPipeline = new HighlighterPipeline(highlighters);
        JXTable table = new JXTable();
        table.setHighlighters(highlighterPipeline);
    }

    /*
     */
    public void testAddRemoveHighlighter() {
        HighlighterPipeline pipeline = new HighlighterPipeline(highlighters);

        Highlighter hl = new PatternHighlighter(Color.blue, Color.red, "mark", 0, 0);
        Highlighter hl2 = new PatternHighlighter(Color.white, Color.black, "amy", 0, 0);

        // added highlighter should be appended
        pipeline.addHighlighter(hl);

        Highlighter[] hls = pipeline.getHighlighters();

        assertTrue(hls.length == 3);
        assertTrue(hls[2] == hl);

        // added highlighter should be prepended
        pipeline.addHighlighter(hl2, true);

        hls = pipeline.getHighlighters();

        assertTrue(hls.length == 4);
        assertTrue(hls[0] == hl2);

        // remove highlighter
        pipeline.removeHighlighter(hl);

        hls = pipeline.getHighlighters();
        assertTrue(hls.length == 3);
        for (int i = 0; i < hls.length; i++) {
            assertTrue(hls[i] != hl);
        }

        // remove another highligher
        pipeline.removeHighlighter(hl2);

        hls = pipeline.getHighlighters();
        assertTrue(hls.length == 2);
        for (int i = 0; i < hls.length; i++) {
            assertTrue(hls[i] != hl2);
        }
    }
    
    public void testNullForeground() {
        JLabel label = new JLabel("test");
        HighlighterPipeline pipeline = new HighlighterPipeline(new Highlighter[] {
                new PatternHighlighter(null, Color.red, "notfitting", 0, 1) });
        ComponentAdapter adapter = createComponentAdapter(label);
        label.setForeground(null);
        pipeline.apply(label, adapter);
    }

    private ComponentAdapter createComponentAdapter(final JLabel label) {
        ComponentAdapter adapter = new ComponentAdapter(label) {

            public Object getValueAt(int row, int column) {
                // TODO Auto-generated method stub
                return label.getText();
            }

            public Object getFilteredValueAt(int row, int column) {
                // TODO Auto-generated method stub
                return getValueAt(row, column);
            }

            public void setValueAt(Object aValue, int row, int column) {
                // TODO Auto-generated method stub
                
            }

            public boolean isCellEditable(int row, int column) {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean hasFocus() {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean isSelected() {
                // TODO Auto-generated method stub
                return false;
            }
            
        };
        return adapter;
    }
}
