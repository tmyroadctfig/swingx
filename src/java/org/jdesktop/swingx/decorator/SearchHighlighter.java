/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.util.regex.Pattern;

/**
 * @author Jeanette Winzenburg
 */
public class SearchHighlighter extends PatternHighlighter {
    /** the row to highlight. -1 means all */
    int highlightRow;
    private boolean enableHighlight;
    private static final String ALL = ".*";
    
    public SearchHighlighter() {
        this(Color.YELLOW.brighter(), null);
    }
    
    public SearchHighlighter(Color background, Color foreground) {
        super(background, foreground, null, 0, -1);
        setHighlightRow(-1);
    }

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
        // use one highlighter for all columns
        int testColumnV = adapter.column;
        if (testColumn >= 0) {
            testColumnV = adapter.modelToView(testColumn);
        }
        Object  value = adapter.getFilteredValueAt(adapter.row, testColumnV);
        if (value == null) {
            return false;
        }
        else {
            // this is a hack to make the Highlighter matching behave
            // consistently with Table matching: brute force find.
            // The correct thing to do would be to 
            // make JXTable respect the pattern.
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

    public void setHighlightCell(int row, int modelColumn) {
        this.testColumn = modelColumn;
        this.highlightColumn = modelColumn;
        this.highlightRow = row;
        setEnabled(true);
    }

}
