/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.util.ChangeReport;

public class HighlighterTest extends TestCase {

    private Highlighter[] highlighters;
    private static final boolean UNSELECTED = false;
    private static final boolean SELECTED = true;
    private static final boolean FAIL_ALWAYS = false;
    private static final boolean PASS_ALWAYS = true;
    
    
    private JLabel backgroundNull ;
    private JLabel foregroundNull;

    protected void setUp() {
        highlighters = new Highlighter[] {
            new AlternateRowHighlighter(Color.white, new Color(0xF0, 0xF0, 0xE0), null),
            new PatternHighlighter(null, Color.red, "^s", 0, 0)
        };
        backgroundNull = new JLabel("test");
        backgroundNull.setForeground(Color.red);
        backgroundNull.setBackground(null);
        
        foregroundNull = new JLabel("test");
        foregroundNull.setForeground(null);
        foregroundNull.setBackground(Color.red);
    }

    protected void tearDown() {
        for (int i = 0; i < highlighters.length; i++) {
            highlighters[i] = null;
        }
        highlighters = null;
    }


    public void testAddToEmptyHighlighterPipeline() {
        HighlighterPipeline pipeline = new HighlighterPipeline(new Highlighter[] { });
        pipeline.addHighlighter(new Highlighter());
    }
    public void testRemoveFromEmptyHighlighterPipeline() {
        HighlighterPipeline pipeline = new HighlighterPipeline(new Highlighter[] { });
        pipeline.removeHighlighter(new Highlighter());
    }
    public void testApplyEmptyHighlighterPipeline() {
        HighlighterPipeline pipeline = new HighlighterPipeline(new Highlighter[] { });
        pipeline.apply(new JLabel(), createComponentAdapter(new JLabel(), false));
    }
//----------------- testing change notification of pipeline
    
    /**
     * @todo - how to handle same highlighter inserted more than once?
     */
    public void testHighlighterPipelineWithDuplicates() {
        
    }
    
    public void testHighlighterPipelineChange() {
        Highlighter highlighter = new Highlighter();
        HighlighterPipeline pipeline = new HighlighterPipeline();
        ChangeReport changeReport = new ChangeReport();
        pipeline.addChangeListener(changeReport);
        int count = changeReport.getEventCount();
        pipeline.addHighlighter(highlighter);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        assertPipelineChange(highlighter, pipeline, changeReport);
    }
    
    public void testHighlighterPipelineChangeConstructor() {
        Highlighter highlighter = new Highlighter();
        HighlighterPipeline pipeline = new HighlighterPipeline(new Highlighter[] { highlighter} );
        ChangeReport changeReport = new ChangeReport();
        pipeline.addChangeListener(changeReport);
        assertPipelineChange(highlighter, pipeline, changeReport);
 
    }
    private void assertPipelineChange(Highlighter highlighter, HighlighterPipeline pipeline, ChangeReport changeReport) {
        int count = changeReport.getEventCount();
        highlighter.setBackground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        assertEquals("event source must be pipeline", pipeline, changeReport.getLastEvent().getSource());
        pipeline.removeHighlighter(highlighter);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        pipeline.removeHighlighter(highlighter);
        assertEquals("event count must not be increased", count,  changeReport.getEventCount() );
        highlighter.setBackground(Color.BLUE);
        assertEquals("event count must not be increased", count,  changeReport.getEventCount() );
    }
    
//----------------- testing change notification Highlighter

    
    public void testHighlighterChange() {
        Highlighter highlighter = new Highlighter();
        ChangeReport changeReport = new ChangeReport();
        highlighter.addChangeListener(changeReport);
        assertBaseHighlighterChange(highlighter, changeReport);
    }

    private void assertBaseHighlighterChange(Highlighter highlighter, ChangeReport changeReport) {
        int count = changeReport.getEventCount();
        highlighter.setBackground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setForeground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setSelectedBackground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setSelectedForeground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
    }
    
    public void testAlternateRowHighlighterChange() {
        AlternateRowHighlighter highlighter = new AlternateRowHighlighter();
        ChangeReport changeReport = new ChangeReport();
        highlighter.addChangeListener(changeReport);
        assertBaseHighlighterChange(highlighter, changeReport);
        int count = changeReport.getEventCount();
        highlighter.setOddRowBackground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setEvenRowBackground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );

    }

    /**
     * @TODO
     */
    public void testColumnPropertyHighlighterChange() {
    }

    public void testConditionalHighlighterChange() {
        ConditionalHighlighter highlighter = new ConditionalHighlighter() {

            protected boolean test(ComponentAdapter adapter) {
                // TODO Auto-generated method stub
                return false;
            }
            
        };
        ChangeReport changeReport = new ChangeReport();
        highlighter.addChangeListener(changeReport);
        assertBaseHighlighterChange(highlighter, changeReport);
        assertBaseConditionalChange(highlighter, changeReport);

    }

    public void testPatternHighlighterChange() {
        PatternHighlighter highlighter = new PatternHighlighter();
        ChangeReport changeReport = new ChangeReport();
        highlighter.addChangeListener(changeReport);
        assertBaseHighlighterChange(highlighter, changeReport);
        assertBasePatternChange(highlighter, changeReport);
    }

    public void testSearchHighlighterChange() {
        SearchHighlighter highlighter = new SearchHighlighter();
        ChangeReport changeReport = new ChangeReport();
        highlighter.addChangeListener(changeReport);
        assertBaseHighlighterChange(highlighter, changeReport);
        assertBasePatternChange(highlighter, changeReport);
        assertBaseConditionalChange(highlighter, changeReport);
        int count = changeReport.getEventCount();
        highlighter.setEnabled(true);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setHighlightRow(3);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setHighlightAll();
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setHighlightCell(5, 7);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
    }
    
    private void assertBasePatternChange(PatternHighlighter highlighter, ChangeReport changeReport) {
        assertBaseConditionalChange(highlighter, changeReport);
        int count = changeReport.getEventCount();
        highlighter.setPattern("x", 0);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setPattern(Pattern.compile(".*", 0));
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
    }
    
    private void assertBaseConditionalChange(ConditionalHighlighter highlighter, ChangeReport changeReport) {
        int count = changeReport.getEventCount();
        highlighter.setHighlightColumnIndex(5);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setMask(255);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setTestColumnIndex(255);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
    }
    /**
     * This is a test to ensure that the example in the javadoc actually works.
     * if the javadoc example changes, then those changes should be pasted here.
     */
    public void testJavaDocExample() {
        Highlighter[]   highlighters = new Highlighter[] {
            new AlternateRowHighlighter(Color.white, new Color(0xF0, 0xF0, 0xE0), null),
            new PatternHighlighter(null, Color.red, "^s", 0, 0)
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
 
//---------------------- exposing highlighter probs with null component color
    
    public void testLabelSanity() {
        assertNull("foreground must be null", foregroundNull.getForeground());
        assertNotNull("background must not be null", foregroundNull.getBackground());
        assertNull("background must be null", backgroundNull.getBackground());
        assertNotNull("foreground must not be null", backgroundNull.getForeground());
    }
    
    /**
     * Issue #21: ConditionalHighligher throwing NPE in components 
     * with null color. This failed.
     * 
     * basically all the testNullXX test the same - they are 
     * permutations of the triple-state:
     * 
     *  comp background/foreground color null
     *  selected/unselected componentAdapter
     *  failing/passing test in conditionalHighlighter
     *  
     *  all passed except for those marked as having failed.
     *  
     * 
     *
     */
    public void testNullBackgroundOnConditional() {
        assertApply(backgroundNull, FAIL_ALWAYS, UNSELECTED);
    }
    /**
    * Issue #21: ConditionalHighligher throwing NPE in components 
    * with null color. This failed.
    */
    public void testNullForegroundOnConditional() {
        assertApply(foregroundNull, FAIL_ALWAYS, UNSELECTED);
    }
    
    /** 
     * this puzzled me because it did not fail! 
     * What happens is that first a selected color is set and
     * only afterwards the background/foreground masked, so
     * at the time of the mask the component has a color!
     *
     */
    public void testNullBackgroundOnConditionalSelected() {
        assertApply(backgroundNull, FAIL_ALWAYS, SELECTED);
    }

    /** 
     * this puzzled me because it did not fail! 
     * Currently does...
     */
    public void testNullForegroundOnConditionalSelected() {
        assertApply(foregroundNull, FAIL_ALWAYS, SELECTED);
    }
    
    public void testNullForegroundOnConditionalPass() {
        assertApply(foregroundNull, PASS_ALWAYS, UNSELECTED);
    }
    
    public void testNullBackgroundOnConditionalPass() {
        assertApply(backgroundNull, PASS_ALWAYS, UNSELECTED);
    }

    public void testNullForegroundOnConditionalPassSelected() {
        assertApply(foregroundNull, PASS_ALWAYS, SELECTED);
    }
    
    public void testNullBackgroundOnConditionalPassSelected() {
        assertApply(backgroundNull, PASS_ALWAYS, SELECTED);
    }

    private void assertApply(JLabel label, boolean testStatus, boolean selected) {
        assertApplyHighlightColors(Color.green, Color.magenta, label, testStatus, selected);
    }

    private void assertApplyNoColors(JLabel label, boolean testStatus, boolean selected) {
        assertApplyHighlightColors(null, null, label, testStatus, selected);
    }
    private void assertApplyHighlightColors(Color background, Color foreground, JLabel label, boolean testStatue, boolean selected) {
        testLabelSanity();
        HighlighterPipeline pipeline = createPipeline(background, foreground, testStatue);
        ComponentAdapter adapter = createComponentAdapter(label, selected);
        pipeline.apply(label, adapter);
    }

    //---------------------------------------------------
    
    private HighlighterPipeline createPipeline(Color background, Color foreground, boolean pass) {
        return new HighlighterPipeline(new Highlighter[] { createConditionalHighlighter(background, foreground, pass)});
    }

    private Highlighter createConditionalHighlighter(Color background, Color foreground, final boolean pass) {
        ConditionalHighlighter highlighter = new ConditionalHighlighter(background, foreground, 0, -1) {

            protected boolean test(ComponentAdapter adapter) {
                return pass;
            }
            
        };
        return highlighter;
    }

    public void testNullBackgroundOnHighlighter() {
        HighlighterPipeline pipeline =  new HighlighterPipeline(
                new Highlighter[] { new Highlighter(Color.orange, null), });
        ComponentAdapter adapter = createComponentAdapter(backgroundNull, false);
        pipeline.apply(backgroundNull, adapter);
    }
    
    public void testNullForegroundOnHighlighter() {
        JLabel label = new JLabel("test");
        HighlighterPipeline pipeline =  new HighlighterPipeline(
                new Highlighter[] { new Highlighter(null, null), });
        ComponentAdapter adapter = createComponentAdapter(label, false);
        label.setForeground(null);
        pipeline.apply(label, adapter);
    }

    private ComponentAdapter createComponentAdapter(final JLabel label, final boolean selected) {
        ComponentAdapter adapter = new ComponentAdapter(label) {

            public Object getValueAt(int row, int column) {
                return label.getText();
            }

            public Object getFilteredValueAt(int row, int column) {
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
                return selected;
            }

            public String getColumnName(int columnIndex) {
                return null;
            }

            public String getColumnIdentifier(int columnIndex) {
                return null;
            }
            
        };
        return adapter;
    }
}
