/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.UIManager;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.decorator.HighlighterFactory.UIColorHighlighter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.renderer.JRendererLabel;
import org.jdesktop.test.ChangeReport;

/**
 * 
 * Tests for Highlighters after overhaul.
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(HighlighterTest.class
            .getName());
    
    protected JLabel backgroundNull ;
    protected JLabel foregroundNull;
    protected JLabel allNull;
    protected JRendererLabel allColored;
    
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;
    
    protected Color unselectedBackground = Color.CYAN;
    protected Color unselectedForeground = Color.GREEN;
    
    protected Color selectedBackground = Color.LIGHT_GRAY;
    protected Color selectedForeground = Color.MAGENTA;
    
    protected ColorHighlighter emptyHighlighter;
    // flag used in setup to explicitly choose LF
    protected boolean defaultToSystemLF;

    protected void setUp() {
        backgroundNull = new JLabel("test");
        backgroundNull.setForeground(foreground);
        backgroundNull.setBackground(null);
        
        foregroundNull = new JLabel("test");
        foregroundNull.setForeground(null);
        foregroundNull.setBackground(background);
        
        allNull = new JLabel("test");
        allNull.setForeground(null);
        allNull.setBackground(null);
        
        allColored = new JRendererLabel();
        allColored.setText("test");
        allColored.setForeground(foreground);
        allColored.setBackground(background);
        
        emptyHighlighter = new ColorHighlighter();
        // make sure we have the same default for each test
        defaultToSystemLF = false;
        setSystemLF(defaultToSystemLF);
    }

//-------------------PainterHighlighter
    
    public void testPainterHighlighterConstructors() {
        PainterHighlighter hl = new PainterHighlighter();
        assertEquals(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertNull(hl.getPainter());
        Painter mattePainter = new MattePainter();
        PainterHighlighter withPainter = new PainterHighlighter(mattePainter);
        assertEquals(HighlightPredicate.ALWAYS, withPainter.getHighlightPredicate());
        assertEquals(mattePainter, withPainter.getPainter());
        PainterHighlighter all = new PainterHighlighter(mattePainter, HighlightPredicate.NEVER);
        assertEquals(HighlightPredicate.NEVER, all.getHighlightPredicate());
        assertEquals(mattePainter, all.getPainter());
    }
    
    public void testPainterHighlighterSetPainterAndNotificatioon() {
        PainterHighlighter hl = new PainterHighlighter();
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        MattePainter mattePainter = new MattePainter();
        hl.setPainter(mattePainter);
        assertEquals(mattePainter, hl.getPainter());
        assertEquals(1, report.getEventCount());
     }
    
    public void testPainterHighlighterUsePainter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        MattePainter mattePainter = new MattePainter();
        PainterHighlighter hl = new PainterHighlighter(mattePainter);
        hl.highlight(allColored, adapter);
        // sanity 
        assertEquals(mattePainter, allColored.getPainter());
    }
    
    /**
     * 
     *
     */
    public void testPainterHighlighterNotUseNullPainter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        PainterHighlighter hl = new PainterHighlighter();
        MattePainter mattePainter = new MattePainter();
        allColored.setPainter(mattePainter);
        hl.highlight(allColored, adapter);
        // sanity 
        assertEquals(mattePainter, allColored.getPainter());
    }
//-------------------- factory
    
    /**
     * highlight every second 
     */
    public void testSimpleStriping() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        Highlighter h = HighlighterFactory.createSimpleStriping(unselectedBackground);
        h.highlight(allColored, adapter);
        // no effect on first row
        assertEquals(background, allColored.getBackground());
        adapter.row = 1;
        h.highlight(allColored, adapter);
        assertEquals(unselectedBackground, allColored.getBackground());
    }
    

    /**
     * highlight every second 
     */
    public void testAlternateStriping() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        Highlighter h = HighlighterFactory.createAlternateStriping(unselectedBackground, selectedBackground);
        h.highlight(allColored, adapter);
        // first color on first row
        assertEquals(unselectedBackground, allColored.getBackground());
        adapter.row = 1;
        h.highlight(allColored, adapter);
        // second color on second row
        assertEquals(selectedBackground, allColored.getBackground());
    }
    
    
//----------------------- Colorhighlighter constructors
 
    /**
     * Test constructor of ColorHighlighter.
     */
    public void testConstructors() {
        ColorHighlighter empty = new ColorHighlighter();
        assertColorsAndPredicate(empty, null, null, null, null, HighlightPredicate.ALWAYS);
        ColorHighlighter normal = new ColorHighlighter(background, foreground);
        assertColorsAndPredicate(normal, background, foreground, null, null, HighlightPredicate.ALWAYS);
        ColorHighlighter normalNever = new ColorHighlighter(background, foreground, 
                HighlightPredicate.NEVER);
        assertColorsAndPredicate(normalNever, background, foreground, null, null, HighlightPredicate.NEVER);
        ColorHighlighter full = new ColorHighlighter(background, foreground, 
                selectedBackground , selectedForeground);
        assertColorsAndPredicate(full, background, foreground, selectedBackground, selectedForeground, HighlightPredicate.ALWAYS);
        ColorHighlighter fullNever = new ColorHighlighter(background, foreground, 
                selectedBackground , selectedForeground, HighlightPredicate.NEVER);
        assertColorsAndPredicate(fullNever, background, foreground, selectedBackground, selectedForeground, HighlightPredicate.NEVER);
    }
    
    private void assertColorsAndPredicate(ColorHighlighter highlighter, Color background, Color foreground,
            Color  selectedBackground, Color selectedForeground, HighlightPredicate predicate) {
        assertEquals("background", background, highlighter.getBackground());
        assertEquals("foreground", foreground, highlighter.getForeground());
        assertEquals("selectedbackground", selectedBackground, highlighter.getSelectedBackground());
        assertEquals("selectedForeground", selectedForeground, highlighter.getSelectedForeground());
        assertEquals("predicate", predicate, highlighter.getHighlightPredicate());
    }
//----------------- testing change notification ColorHighlighter

    
    public void testHighlighterChange() {
        ColorHighlighter highlighter = new ColorHighlighter();
        ChangeReport changeReport = new ChangeReport();
        highlighter.addChangeListener(changeReport);
        assertBaseHighlighterChange(highlighter, changeReport);
    }

    private void assertBaseHighlighterChange(ColorHighlighter highlighter, ChangeReport changeReport) {
        int count = changeReport.getEventCount();
        highlighter.setBackground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setForeground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setSelectedBackground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setSelectedForeground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        highlighter.setHighlightPredicate(HighlightPredicate.NEVER);
        assertEquals("event count must be increased", ++count, changeReport.getEventCount());
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

    // --------------------- highlightPredicate
    
    /**
     * test predicate defaults to always
     */
    public void testDefaultPredicate() {
        ColorHighlighter highlighter = new ColorHighlighter();
        assertSame(HighlightPredicate.ALWAYS, highlighter.getHighlightPredicate());
    }
    
    /**
     * test highlight respects predicate never.
     *
     */
    public void testHighlightPredicate() {
        ColorHighlighter highlighter = new ColorHighlighter(unselectedBackground, 
                unselectedForeground);
        highlighter.setHighlightPredicate(HighlightPredicate.NEVER);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        highlighter.highlight(allColored, adapter);
        // assert unchanged colors
        assertEquals(foreground, allColored.getForeground());
        assertEquals(background, allColored.getBackground());
    }
    //------------------------- test highlight effects ColorHighlighter
    
    /**
     * Test ColorHighlighter decorate unselected cell.
     *
     */
    public void testApplyColorHighlighterUnselected() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        ColorHighlighter highlighter = new ColorHighlighter(unselectedBackground, 
                unselectedForeground, selectedBackground, selectedForeground);
        assertApplied(highlighter, allColored, adapter);
    }
    
    /**
     * Test ColorHighlighter decorate selected cell.
     *
     */
    public void testApplyColorHighlighterSelected() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        ColorHighlighter highlighter = new ColorHighlighter(unselectedBackground, 
                unselectedForeground, selectedBackground, selectedForeground);
        assertApplied(highlighter, allColored, adapter);
    }
    
    /**
     * test that same renderer is returned.
     *
     */
    public void testReturnHighlightedComponent() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        ColorHighlighter highlighter = new ColorHighlighter(unselectedBackground, 
                unselectedForeground);
        assertSame(allColored, highlighter.highlight(allColored, adapter));
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
    protected void assertApplied(ColorHighlighter highlighter, Component label, ComponentAdapter adapter) {
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

//------------------ UIDependent
    
    /**
     * test that the ui color highlighter comes up with 
     * the ui-setting.
     */
    public void testInitialUIColorHighlighter() {
        ColorHighlighter h = new UIColorHighlighter();
        Color uiBackground = h.getBackground();
        Color uiColor = UIManager.getColor("UIColorHighlighter.stripingBackground");
        if (uiColor == null) {
            LOG.info("cannot test initial ui striping color - UIManager has null");
        }
        assertEquals(uiColor, uiBackground);
    }
    
//-----------------------  CompoundHighlighter
    
    /**
     * there had been exceptions when adding/removing highlighters to/from
     * an initially empty pipeline. 
     */
    public void testAddToEmptyCompoundHiglighter() {
        CompoundHighlighter pipeline = new CompoundHighlighter();
        pipeline.addHighlighter(new ColorHighlighter());
    }
    public void testRemoveFromEmptyCompoundHighlighter() {
        CompoundHighlighter pipeline = new CompoundHighlighter();
        pipeline.removeHighlighter(new ColorHighlighter());
    }
    public void testApplyEmptyCompoundHighlighter() {
        CompoundHighlighter pipeline = new CompoundHighlighter();
        pipeline.highlight(new JLabel(), createComponentAdapter(new JLabel(), false));
    }

    /*
     */
    public void testAddRemoveHighlighter() {
        CompoundHighlighter pipeline = new CompoundHighlighter(
                new ColorHighlighter(Color.white, new Color(0xF0, 0xF0, 0xE0)),
                new ColorHighlighter(null, foreground)
                );

        ColorHighlighter hl = new ColorHighlighter(Color.blue, Color.red);
        ColorHighlighter hl2 = new ColorHighlighter(Color.white, Color.black);

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


    //----------------- testing change notification of pipeline
    
    /**
     * @todo - how to handle same highlighter inserted more than once?
     */
    public void testCompoundHighlighterWithDuplicates() {
        
    }
    
    /**
     * test doc'ed NPE when adding null Highlighter.
     *
     */
    public void testCompoundHighlighterAddNull() {
        CompoundHighlighter pipeline = new CompoundHighlighter();
        try {
            pipeline.addHighlighter(null);
            fail("compoundHighlighter must not accept null highlighter");
        } catch(NullPointerException ex) {
            
        }
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        // was added even with NPE.
        pipeline.highlight(allColored, adapter);
    }
    
    public void testCompoundHighlighterChange() {
        ColorHighlighter highlighter = new ColorHighlighter();
        CompoundHighlighter pipeline = new CompoundHighlighter();
        ChangeReport changeReport = new ChangeReport();
        pipeline.addChangeListener(changeReport);
        int count = changeReport.getEventCount();
        pipeline.addHighlighter(highlighter);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        assertCompoundHighlighterChange(highlighter, pipeline, changeReport);
    }
    
    public void testCompoundHighlighterChangeConstructor() {
        ColorHighlighter highlighter = new ColorHighlighter();
        CompoundHighlighter pipeline = new CompoundHighlighter(highlighter);
        ChangeReport changeReport = new ChangeReport();
        pipeline.addChangeListener(changeReport);
        assertCompoundHighlighterChange(highlighter, pipeline, changeReport);
 
    }
    private void assertCompoundHighlighterChange(ColorHighlighter highlighter, CompoundHighlighter pipeline, ChangeReport changeReport) {
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
 
    // --------------------- factory methods
    /**
     * Creates and returns a ComponentAdapter on the given 
     * label with the specified selection state.
     * 
     * @param label
     * @param selected
     * @return
     */
    protected ComponentAdapter createComponentAdapter(final JLabel label, final boolean selected) {
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
