/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.util.regex.Pattern;

/**
 * Convenience Highlighter to test and highlight cells in searching.
 *  
 * @author Jeanette Winzenburg
 */
public class SearchHighlighter extends PatternHighlighter {
    /** the row to highlight in view coordinates. -1 means all */
    int highlightRow;
    private boolean enableHighlight;
    private static final String ALL = ".*";
    
    /**
     * Instantiates a default SearchHighlighter. 
     * The default colors are Yellow background and null foreground.
     * The default matching state is 
     *  Pattern == null, flags = 0, tests all columns and all rows.
     * 
     */
    public SearchHighlighter() {
        this(Color.YELLOW.brighter(), null);
    }
    
    /**
     * Instantiates a default SearchHighlighter with background/foreground colors.
     * The default matching state is 
     *  Pattern == null, flags = 0, tests all columns and all rows.
     * 
     * @param background color of hightlight background
     * @param foreground color of highlight foreground
     */
    public SearchHighlighter(Color background, Color foreground) {
        super(background, foreground, null, 0, -1);
        setHighlightRow(-1);
    }

    /**
     * Toggle to enable/disable - if disabled never hightlights.
     * 
     * @param enableHighlight
     */
    public void setEnabled(boolean enableHighlight) {
        this.enableHighlight = enableHighlight;
        fireStateChanged();
    }
    
    protected boolean needsHighlight(ComponentAdapter adapter) {
        if (!isEnabled()) return false;
        if (highlightRow >= 0 && (adapter.row != highlightRow)) {
            return false;
        }
        return super.needsHighlight(adapter);
    }

    private boolean isEnabled() {
        Pattern pattern = getPattern();
        if (pattern == null || ALL.equals(pattern.pattern())) {
            return false;
        }
        return enableHighlight;
    }

    protected boolean test(ComponentAdapter adapter) {
        if (pattern == null) {
            return false;
        }
        int columnToTest = testColumn;
        // use one highlighter for all columns
        if (columnToTest < 0) {
            columnToTest = adapter.viewToModel(adapter.column);
        }
        Object  value = adapter.getFilteredValueAt(adapter.row, columnToTest);
        if (value == null) {
            return false;
        }
        else {
            boolean matches = pattern.matcher(value.toString()).find();
            return matches;
        }
    }

    /** set the row to match in test. 
     * - 1 means all.
     * @param row
     */
    public void setHighlightRow(int row) {
        highlightRow = row;
        fireStateChanged();
    }

    /** 
     * convenience method to test and highlight all rows/columns and 
     * enable.
     *
     */
    public void setHighlightAll() {
        setHighlightCell(-1, -1);
        
    }

    /**
     * Set's highlightRow to row, test- and highlight column = column
     * @param row
     * @param modelColumn
     */
    public void setHighlightCell(int row, int modelColumn) {
        this.testColumn = modelColumn;
        this.highlightColumn = modelColumn;
        this.highlightRow = row;
        setEnabled(true);
    }

}
