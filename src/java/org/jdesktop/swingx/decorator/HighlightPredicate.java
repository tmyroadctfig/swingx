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
package org.jdesktop.swingx.decorator;

import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdesktop.swingx.RolloverProducer;

/**
 * The predicate used by AbstractHighlighter to control 
 * highlight on/off.
 * 
 * @author Jeanette Winzenburg
 */
public interface HighlightPredicate {
    
    /**
     * Unconditional true.
     */
    public static final HighlightPredicate ALWAYS = new HighlightPredicate() {

        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return true;
        }
        
    };

    /**
     * Unconditional false.
     */
    public static final HighlightPredicate NEVER = new HighlightPredicate() {

        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return false;
        }
        
    };
    
    /**
     * Rollover
     */
    public static final HighlightPredicate ROLLOVER_ROW = new HighlightPredicate() {
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            // JW: where to put constants? 
            // this is a back-reference into swingx simply to access
            // a string constant. Hmmm...
            Point p = (Point) adapter.getComponent().getClientProperty(
                    RolloverProducer.ROLLOVER_KEY);
            return p != null &&  p.y == adapter.row;
        }
        
    };
    
    /**
     * Even rows.
     * 
     * PENDING: this is zero based (that is "really" even 0, 2, 4 ..), differing 
     * from the old AlternateRowHighlighter.
     * 
     */
    public static final HighlightPredicate EVEN = new HighlightPredicate() {

        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return adapter.row % 2 == 0;
        }
        
    };
    
    /**
     * Odd rows.
     * 
     * PENDING: this is zero based (that is 1, 3, 4 ..), differs from 
     * the old implementation which was one based?
     * 
     */
    public static final HighlightPredicate ODD = new HighlightPredicate() {

        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return !EVEN.isHighlighted(renderer, adapter);
        }
        
    };
    
    
    /**
     * Returns a boolean to indicate whether the component should be 
     * highlighted.
     * 
    * @param renderer the cell renderer component that is to be decorated
    * @param adapter the ComponentAdapter for this decorate operation
    * @return a boolean to indicate whether the component should be highlighted.
     */
    boolean isHighlighted(Component renderer, ComponentAdapter adapter);

    
//----------------- logical implementations
    
    public static class NotHighlightPredicate implements HighlightPredicate {
        
        private HighlightPredicate predicate;
        
        public NotHighlightPredicate(HighlightPredicate predicate) {
            if (predicate == null) 
                throw new NullPointerException("predicate must not be null");
            this.predicate = predicate;
        }
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return !predicate.isHighlighted(renderer, adapter);
        }
        
    }
    
    public static class AndHighlightPredicate implements HighlightPredicate {
        
        private List<HighlightPredicate> predicate;
        
        public AndHighlightPredicate(HighlightPredicate... predicate) {
            if (predicate == null) 
                throw new NullPointerException("predicate must not be null");
            this.predicate = Arrays.asList(predicate);
        }
        
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            for (HighlightPredicate hp : predicate) {
                if (!hp.isHighlighted(renderer, adapter)) return false;
            }
            return true;
        }
        
    }
    
    public static class RowGroupHighlightPredicate implements HighlightPredicate {

        private int linesPerGroup;

        public RowGroupHighlightPredicate(int linesPerGroup) {
            this.linesPerGroup = linesPerGroup;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return (adapter.row / linesPerGroup) % 2 == 1;
        }
        
    }
    
    /**
     * A HighlightPredicate based on column index.
     * 
     */
    public static class ColumnHighlightPredicate implements HighlightPredicate {
        List<Integer> columnList;
        
        /**
         * Instantiates a predicate which returns true for the
         * given columns in model coodinates.
         * 
         * @param columns the columns to highlight in model coordinates.
         */
        public ColumnHighlightPredicate(int... columns) {
            columnList = new ArrayList<Integer>();
            for (int i = 0; i < columns.length; i++) {
                columnList.add(columns[i]);
            }
        }
        
        /**
         * {@inheritDoc}
         * 
         * This implementation returns true if the adapters column
         * is contained in this predicates list.
         * 
         */
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            int modelIndex = adapter.viewToModel(adapter.column);
            return columnList.contains(modelIndex);
        }
        
    }
}
