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
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;

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
    
    public void testNot() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        HighlightPredicate predicate = new NotHighlightPredicate(HighlightPredicate.NEVER);
        assertTrue(predicate.isHighlighted(allColored, adapter));
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
