/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Component;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.test.ChangeReport;

/**
 * 
 * Tests for LegacyHighlighter and subclasses. 
 * 
 * @author Jeanette Winzenburg
 */
// this test is about the old style - turn off deprecation warnings
@SuppressWarnings("deprecation")
public class LegacyHighlighterTest extends InteractiveTestCase {

    protected Highlighter[] highlighters;
    protected static final boolean UNSELECTED = false;
    protected static final boolean SELECTED = true;
    protected static final boolean FAIL_ALWAYS = false;
    protected static final boolean PASS_ALWAYS = true;
    
    
    protected JLabel backgroundNull ;
    protected JLabel foregroundNull;
    protected JLabel allNull;
    protected JLabel allColored;
    
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;
    
    protected LegacyHighlighter emptyHighlighter;

    protected void setUp() {
        highlighters = new LegacyHighlighter[] {
            new AlternateRowHighlighter(Color.white, new Color(0xF0, 0xF0, 0xE0), null),
            new PatternHighlighter(null, foreground, "^s", 0, 0)
        };
        backgroundNull = new JLabel("test");
        backgroundNull.setForeground(foreground);
        backgroundNull.setBackground(null);
        
        foregroundNull = new JLabel("test");
        foregroundNull.setForeground(null);
        foregroundNull.setBackground(background);
        
        allNull = new JLabel("test");
        allNull.setForeground(null);
        allNull.setBackground(null);
        
        allColored = new JLabel("test");
        allColored.setForeground(foreground);
        allColored.setBackground(background);
        
        emptyHighlighter = new LegacyHighlighter();
    }

    protected void tearDown() {
        for (int i = 0; i < highlighters.length; i++) {
            highlighters[i] = null;
        }
        highlighters = null;
    }
//------------ sanity during overhaul
    
    @SuppressWarnings("deprecation")
    public void testPredefinedColors() {
        assertEquals(((AlternateRowHighlighter) AlternateRowHighlighter.beige).getEvenRowBackground()
                , HighlighterFactory.BEIGE);
        assertEquals(((AlternateRowHighlighter) AlternateRowHighlighter.genericGrey).getEvenRowBackground()
                , HighlighterFactory.GENERIC_GRAY);
        assertEquals(((AlternateRowHighlighter) AlternateRowHighlighter.classicLinePrinter).getEvenRowBackground()
                , HighlighterFactory.CLASSIC_LINE_PRINTER);
        assertEquals(((AlternateRowHighlighter) AlternateRowHighlighter.floralWhite).getEvenRowBackground()
                , HighlighterFactory.FLORAL_WHITE);
        assertEquals(AlternateRowHighlighter.ledgerBackground.getBackground()
                , HighlighterFactory.LEDGER);
        assertEquals(((AlternateRowHighlighter) AlternateRowHighlighter.linePrinter).getEvenRowBackground()
                , HighlighterFactory.LINE_PRINTER);
        assertEquals(AlternateRowHighlighter.notePadBackground.getBackground()
                , HighlighterFactory.NOTEPAD);
        assertEquals(((AlternateRowHighlighter) AlternateRowHighlighter.quickSilver).getEvenRowBackground()
                , HighlighterFactory.QUICKSILVER);
    }    
    
//----------------------- highlighter constructors, immutable highlighters
 
    /**
     * Test constructors of LegacyHighlighter.
     */
    public void testConstructors() {
        LegacyHighlighter empty = new LegacyHighlighter();
        assertColors(empty, null, null, null, null, false);
        LegacyHighlighter normal = new LegacyHighlighter(background, foreground);
        assertColors(normal, background, foreground, null, null, false);
        LegacyHighlighter normalImmutable = new LegacyHighlighter(background, foreground, true);
        assertColors(normalImmutable, background, foreground, null, null, true);
        Color selectedBackground = Color.YELLOW;
        Color selectedForeground = Color.BLACK;
        LegacyHighlighter full = new LegacyHighlighter(background, foreground, 
                selectedBackground , selectedForeground);
        assertColors(full, background, foreground, selectedBackground, selectedForeground, false);
        LegacyHighlighter fullImmutable = new LegacyHighlighter(background, foreground, 
                selectedBackground , selectedForeground, true);
        assertColors(fullImmutable, background, foreground, selectedBackground, selectedForeground, true);
        
    }
    
    public void testImmutable() {
        LegacyHighlighter immutable = new LegacyHighlighter(background, foreground, true);
        ChangeReport report = new ChangeReport();
        immutable.addChangeListener(report);
        assertEquals("no listeners", 0, immutable.getChangeListeners().length);
        immutable.setForeground(Color.BLACK);
        immutable.setBackground(Color.YELLOW);
        immutable.setSelectedForeground(Color.BLACK);
        immutable.setSelectedBackground(Color.YELLOW);
        // nothing changed
        assertColors(immutable, background, foreground, null, null, true);
    }
    
    public void testAlternateRowImmutable() {
        Color evenColor = Color.YELLOW;
        AlternateRowHighlighter immutable = new AlternateRowHighlighter(background, evenColor, foreground, true);
        assertColors(immutable, background, foreground, null, null, true);
        assertAlternateColors(immutable, background, evenColor);
        immutable.setOddRowBackground(Color.GRAY);
        immutable.setEvenRowBackground(Color.CYAN);
        assertColors(immutable, background, foreground, null, null, true);
        assertAlternateColors(immutable, background, evenColor);
        
    }

    /**
     * @param immutable
     * @param oddColor TODO
     * @param evenColor
     */
    private void assertAlternateColors(AlternateRowHighlighter immutable, Color oddColor, Color evenColor) {
        assertEquals("oddbackground", oddColor, immutable.getOddRowBackground());
        assertEquals("evenbackground", evenColor, immutable.getEvenRowBackground());
    }
    
    
    public void testImmutablePredefinedHighlighters() {
        assertTrue("ledger must be immutable", LegacyHighlighter.ledgerBackground.isImmutable());
        assertTrue("notepad must be immutable", LegacyHighlighter.notePadBackground.isImmutable());
        assertTrue("beige must be immutable", AlternateRowHighlighter.beige.isImmutable());
        assertTrue("floral must be immutable", AlternateRowHighlighter.floralWhite.isImmutable());
        assertTrue("lineprinter must be immutable", AlternateRowHighlighter.linePrinter.isImmutable());
        assertTrue("classiclineprinter must be immutable", AlternateRowHighlighter.classicLinePrinter.isImmutable());
        assertTrue("quickSilver must be immutable", AlternateRowHighlighter.quickSilver.isImmutable());
    }
    
    private void assertColors(LegacyHighlighter highlighter, Color background, Color foreground,
            Color  selectedBackground, Color selectedForeground, boolean immutable) {
        assertEquals("background", background, highlighter.getBackground());
        assertEquals("foreground", foreground, highlighter.getForeground());
        assertEquals("selectedbackground", selectedBackground, highlighter.getSelectedBackground());
        assertEquals("selectedForeground", selectedForeground, highlighter.getSelectedForeground());
        assertEquals("immutable", immutable, highlighter.isImmutable());
    }

    
//----------------- testing change notification LegacyHighlighter

    
    public void testHighlighterChange() {
        LegacyHighlighter highlighter = new LegacyHighlighter();
        ChangeReport changeReport = new ChangeReport();
        highlighter.addChangeListener(changeReport);
        assertBaseHighlighterChange(highlighter, changeReport);
    }

    private void assertBaseHighlighterChange(LegacyHighlighter highlighter, ChangeReport changeReport) {
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
        highlighter.setLinesPerGroup(5);
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

 
//---------------------- exposing highlighter probs with null component color
    
    public void testLabelSanity() {
        assertNull("foreground must be null", foregroundNull.getForeground());
        assertNotNull("background must not be null", foregroundNull.getBackground());
        assertNull("background must be null", backgroundNull.getBackground());
        assertNotNull("foreground must not be null", backgroundNull.getForeground());
        assertNull("foreground must be null", allNull.getForeground());
        assertNull("background must be null", allNull.getBackground());
        assertEquals(background, allColored.getBackground());
        assertEquals(foreground, allColored.getForeground());
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

    private void assertApplyHighlightColors(Color background, Color foreground, JLabel label, boolean testStatus, boolean selected) {
        Highlighter highlighter = createConditionalHighlighter(background, foreground, testStatus);
        ComponentAdapter adapter = createComponentAdapter(label, selected);
        Color labelForeground = label.getForeground();
        Color labelBackground = label.getBackground();
        highlighter.highlight(label, adapter);
        if (testStatus && !selected) {
            if (background == null) {
                assertEquals(labelBackground, label.getBackground());
            } else {
                assertEquals(background, label.getBackground());
            }
            if (foreground == null) {
                assertEquals(labelForeground, label.getForeground());
                
            } else {
                assertEquals(foreground, label.getForeground());
            }
        }
    }

    //---------------------------------------------------
    

    private Highlighter createConditionalHighlighter(Color background, Color foreground, final boolean pass) {
        ConditionalHighlighter highlighter = new ConditionalHighlighter(background, foreground, 0, -1) {

            protected boolean test(ComponentAdapter adapter) {
                return pass;
            }
            
        };
        return highlighter;
    }

    /**
     * does nothing?
     *
     */
    public void testNullBackgroundOnHighlighter() {
        CompoundHighlighter pipeline =  new CompoundHighlighter(
                new LegacyHighlighter(null, null));
        ComponentAdapter adapter = createComponentAdapter(backgroundNull, false);
        pipeline.highlight(backgroundNull, adapter);
    }
    
    /**
     * does nothing?
     *
     */
    public void testNullForegroundOnHighlighter() {
        CompoundHighlighter pipeline =  new CompoundHighlighter(
                new LegacyHighlighter(null, null));
        ComponentAdapter adapter = createComponentAdapter(foregroundNull, false);
        pipeline.highlight(foregroundNull, adapter);
    }

    
 
    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     * sanity test to see if non-selected colors are unchanged.
     */
    public void testUnSelectedDoNothingHighlighter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertApplied(emptyHighlighter, allColored, adapter);
    }
    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    public void testSelectedDoNothingHighlighter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertApplied(emptyHighlighter, allColored, adapter);
    }

    
    /**
     * running assertion for all highlighter colors, depending on selection of adapter and
     * colors set/not set in highlighter.
     * 
     * @param highlighter
     * @param label
     * @param adapter
     */
    protected void assertApplied(LegacyHighlighter highlighter, Component label, ComponentAdapter adapter) {
        Color labelForeground = label.getForeground();
        Color labelBackground = label.getBackground();
        highlighter.highlight(label, adapter);
        if (!adapter.isSelected()) {
            if (highlighter.getBackground() == null) {
                assertEquals("unselected: background must not be changed", labelBackground, label.getBackground());
            } else {
                assertEquals("unselected: background must be changed", highlighter.getBackground(), label.getBackground());
            }
            if (highlighter.getForeground() == null) {
                assertEquals("unselected: forground must not be changed", labelForeground, label.getForeground());
            } else {
                assertEquals("unselected: forground must be changed", highlighter.getForeground(), label.getForeground());
            }
        } else {
            if (highlighter.getSelectedBackground() == null) {
                assertEquals("selected: background must not be changed", labelBackground, label.getBackground());
            } else {
                assertEquals("selected: background must be changed", highlighter.getSelectedBackground(), label.getBackground());
            }
            if (highlighter.getSelectedForeground() == null) {
                assertEquals("selected: forground must not be changed", labelForeground, label.getForeground());
            } else {
                assertEquals("selected: forground must be changed", highlighter.getSelectedForeground(), label.getForeground());
            }
            
        }
    } 
    
    
    protected ComponentAdapter createComponentAdapter(final JLabel label, final boolean selected) {
        ComponentAdapter adapter = new ComponentAdapter(label) {

            public Object getValueAt(int row, int column) {
                return label.getText();
            }

            public Object getFilteredValueAt(int row, int column) {
                return getValueAt(row, column);
            }

            
            @Override
            public Object getValue() {
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

            public boolean isEditable() {
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
