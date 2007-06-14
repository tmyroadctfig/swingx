/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Point;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.RolloverProducer;
import org.jdesktop.swingx.decorator.HighlightPredicate.AndHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.EqualsHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.OrHighlightPredicate;

/**
 * 
 * Tests for Highlighters after overhaul.
 * 
 * @author Jeanette Winzenburg
 */
public class HighlightPredicateTest extends InteractiveTestCase {

    
    protected JLabel backgroundNull ;
    protected JLabel foregroundNull;
    protected JLabel allNull;
    protected JLabel allColored;
    
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;
    
    protected Color unselectedBackground = Color.CYAN;
    protected Color unselectedForeground = Color.GREEN;
    
    protected Color selectedBackground = Color.LIGHT_GRAY;
    protected Color selectedForeground = Color.MAGENTA;
    
    protected ColorHighlighter emptyHighlighter;

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
        
        allColored = new JLabel("test");
        allColored.setForeground(foreground);
        allColored.setBackground(background);
        
        emptyHighlighter = new ColorHighlighter();
    }

    // ---------------- predefined predicate
    
    /**
     * Can't really test the unconditional predicates.
     *
     */
    public void testAlways() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertTrue(HighlightPredicate.ALWAYS.isHighlighted(allColored, adapter));
    }
    
    /**
     * Can't really test the unconditional predicates.
     *
     */
    public void testNever() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertFalse(HighlightPredicate.NEVER.isHighlighted(allColored, adapter));
    }
    
    /**
     * test the NOT predicate.
     *
     */
    public void testNot() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        HighlightPredicate notNever = new NotHighlightPredicate(HighlightPredicate.NEVER);
        assertTrue(notNever.isHighlighted(allColored, adapter));
        HighlightPredicate notAlways = new NotHighlightPredicate(HighlightPredicate.ALWAYS);
        assertFalse(notAlways.isHighlighted(allColored, adapter));
    }
    
    /**
     * test the OR predicate. Boring as it is, is it complete?
     *
     */
    public void testOr() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        HighlightPredicate oneTrue = new OrHighlightPredicate(HighlightPredicate.ALWAYS);
        assertTrue(oneTrue.isHighlighted(allColored, adapter));
        HighlightPredicate oneFalse = new OrHighlightPredicate(HighlightPredicate.NEVER);
        assertFalse(oneFalse.isHighlighted(allColored, adapter));
        HighlightPredicate oneFalseOneTrue = new OrHighlightPredicate(
                HighlightPredicate.NEVER, HighlightPredicate.ALWAYS);
        assertTrue(oneFalseOneTrue.isHighlighted(allColored, adapter));
        HighlightPredicate oneTrueOneFalse = new OrHighlightPredicate(
                HighlightPredicate.ALWAYS, HighlightPredicate.NEVER);
        assertTrue(oneTrueOneFalse.isHighlighted(allColored, adapter));
    }
    
    /**
     * Issue #5??-swingx: OrPredicate must throw if any of the parameters
     *   is null.
     *
     */
    public void testOrThrowsOnNullPredicates() {
        try {
            new OrHighlightPredicate((HighlightPredicate[]) null);
            fail("orPredicate constructor must throw IllegalArgumentEx on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }

        try {
            new OrHighlightPredicate(HighlightPredicate.ALWAYS, null);
            fail("orPredicate constructor must throw NullPointerException on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }
            

        try {
            new OrHighlightPredicate((HighlightPredicate) null);
            fail("orPredicate constructor must throw IllegalArgumentEx on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }
            
    }
    /**
     * test the AND predicate. Boring as it is, is it complete?
     *
     */
    public void testAnd() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        HighlightPredicate oneTrue = new AndHighlightPredicate(HighlightPredicate.ALWAYS);
        assertTrue(oneTrue.isHighlighted(allColored, adapter));
        HighlightPredicate oneFalse = new AndHighlightPredicate(HighlightPredicate.NEVER);
        assertFalse(oneFalse.isHighlighted(allColored, adapter));
        HighlightPredicate oneFalseOneTrue = new AndHighlightPredicate(
                HighlightPredicate.NEVER, HighlightPredicate.ALWAYS);
        assertFalse(oneFalseOneTrue.isHighlighted(allColored, adapter));
        HighlightPredicate oneTrueOneFalse = new AndHighlightPredicate(
                HighlightPredicate.ALWAYS, HighlightPredicate.NEVER);
        assertFalse(oneTrueOneFalse.isHighlighted(allColored, adapter));
    }
    
    public void testRolloverRow() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        // rollover and adapter at 0, 0
        int row = 0; 
        int col = 0;
        allColored.putClientProperty(RolloverProducer.ROLLOVER_KEY, new Point(row, col));
        assertTrue(HighlightPredicate.ROLLOVER_ROW.isHighlighted(allColored, adapter));
        // move adapter column in same row
        adapter.column = 3;
        assertTrue(HighlightPredicate.ROLLOVER_ROW.isHighlighted(allColored, adapter));
        // move adapter row 
        adapter.row = 1;
        assertFalse(HighlightPredicate.ROLLOVER_ROW.isHighlighted(allColored, adapter));
    }

//---------------- special predicates
    
    /**
     * test equals predicate with null value.
     */
    public void testEqualsNull() {
        HighlightPredicate predicate = new EqualsHighlightPredicate();
        allColored.setText(null);
        assertNull(allColored.getText());
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertTrue(predicate.isHighlighted(allColored, adapter));
        String text = "test";
        allColored.setText(text);
        assertEquals(text, allColored.getText());
        assertFalse(predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test equals predicate with not null value.
     *
     */
    public void testEqualsNotNull() {
        HighlightPredicate predicate = new EqualsHighlightPredicate(allColored
                .getText());
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertTrue(predicate.isHighlighted(allColored, adapter));
        allColored.setText(null);
        assertFalse(predicate.isHighlighted(allColored, adapter));
    }
    
    public void testColumn() {
        HighlightPredicate predicate = new ColumnHighlightPredicate(1);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("column 0 must not be highlighted", predicate.isHighlighted(allColored, adapter));
        adapter.column = 1;
        assertTrue("column 1 must be highlighted", predicate.isHighlighted(allColored, adapter));
    }

    public void testPattern() {
        // start with "t"
        Pattern pattern = Pattern.compile("^t", 0);
        int testColumn = 0;
        int decorateColumn = 0;
        HighlightPredicate predicate = new PatternPredicate(pattern, testColumn, decorateColumn);
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertEquals("predicate must have same result as matcher", pattern.matcher(allColored.getText()).find(), 
                  predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test match in all cells.
     *
     */
    public void testSearchHighlightAllMatches() {
        // start with "t"
        Pattern pattern = Pattern.compile("^t", 0);
        HighlightPredicate predicate = new SearchPredicate(pattern);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertEquals("predicate must have same result as matcher", pattern.matcher(allColored.getText()).find(), 
                  predicate.isHighlighted(allColored, adapter));
        adapter.row = 5;
        adapter.column = 10;
        assertEquals("predicate must have same result as matcher", pattern.matcher(allColored.getText()).find(), 
                predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test match limited by column.
     *
     */
    public void testSearchHighlightColumn() {
        // start with "t"
        Pattern pattern = Pattern.compile("^t", 0);
        int column = 2;
        HighlightPredicate predicate = new SearchPredicate(pattern, column);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("predicate must not match", 
                  predicate.isHighlighted(allColored, adapter));
        adapter.column = column;
        assertTrue("predicate must match", 
                predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test match limited by row.
     *
     */
    public void testSearchHighlightRow() {
        // start with "t"
        Pattern pattern = Pattern.compile("^t", 0);
        int row = 2;
        HighlightPredicate predicate = new SearchPredicate(pattern, row, -1);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("predicate must not match", 
                  predicate.isHighlighted(allColored, adapter));
        adapter.row = row;
        assertTrue("predicate must match", 
                predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test match limited by row and column.
     *
     */
    public void testSearchHighlightRowAndColumn() {
        // start with "t"
        Pattern pattern = Pattern.compile("^t", 0);
        int row = 2;
        int column = 2;
        HighlightPredicate predicate = new SearchPredicate(pattern, row, column);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("predicate must not match", 
                  predicate.isHighlighted(allColored, adapter));
        adapter.row = row;
        adapter.column = column;
        assertTrue("predicate must match", 
                predicate.isHighlighted(allColored, adapter));
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
