/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Pluggable pattern filter.
 *
 * @author Ramesh Gupta
 */
public class PatternFilter extends Filter implements PatternMatcher {
    private ArrayList	toPrevious, fromPrevious;
    protected Pattern	pattern = null;

    public PatternFilter() {
        this(null, 0, 0);
    }

    public PatternFilter(String regularExpr, int matchFlags, int col) {
        super(col);
        setPattern(regularExpr, matchFlags);
    }

    protected void init() {
		toPrevious = new ArrayList();
        fromPrevious = new ArrayList();
    }

    public void setPattern(String regularExpr, int matchFlags) {
        if ((regularExpr == null) || (regularExpr.length() == 0)) {
            regularExpr = ".*";
        }
        setPattern(Pattern.compile(regularExpr, matchFlags));
    }

    /**
     * Sets the pattern used by this filter for matching.
     *
     * @param pattern the pattern used by this filter for matching
     * @see java.util.regex.Pattern
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
        refresh();
    }

    /**
     * Returns the pattern used by this filter for matching.
     *
     * @return the pattern used by this filter for matching
     * @see java.util.regex.Pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Resets the internal row mappings from this filter to the previous filter.
     */
    protected void reset() {
        toPrevious.clear();
        Integer	none = new Integer(-1);
        int inputSize = getInputSize();
        fromPrevious = new ArrayList(inputSize);
        for (int i = 0; i < inputSize; i++) {
            fromPrevious.add(i, none);
        }
    }

    /**
     * Generates the row mappings from the previous filter to this filter.
     */
    protected void generateMappingFromPrevious() {
        int	outputSize = toPrevious.size();
        for (int i = 0; i < outputSize; i++) {
            Integer	index = (Integer) toPrevious.get(i);
            fromPrevious.set(index.intValue(), new Integer(i));
        }
    }

    protected void filter() {
        if (pattern != null) {
            int inputSize = getInputSize();
            for (int i = 0; i < inputSize; i++) {
                if (test(i)) {
                    toPrevious.add(new Integer(i));
                }
            }
        }
    }

    public boolean test(int row) {
        if (pattern == null) {
            return false;
        }

        // If column index in view coordinates is negative, the column is hidden.
        if (adapter.modelToView(getColumnIndex()) < 0) {
            return false; // column is not being displayed; obviously no match!
        }

        Object	value = getInputValue(row, getColumnIndex());

        if (value == null) {
            return false;
        }
        else {
            boolean	matches = pattern.matcher(value.toString()).matches();
            return matches;
        }
    }

    public int getSize() {
        return toPrevious.size();
    }

    /**
     * Returns the row in this filter that maps to the specified row in the
     * previous filter. If there is no previous filter in the pipeline, this returns
     * the row in this filter that maps to the specified row in the data model.
     * This method is called from
     * {@link org.jdesktop.swing.decorator.Filter#convertRowIndexToView(int) convertRowIndexToView}
     *
     * @param row a row index in the previous filter's "view" of the data model
     * @return the row in this filter that maps to the specified row in
     * the previous filter
     */
    protected int translateFromPreviousFilter(int row) {
        return ((Integer) fromPrevious.get(row)).intValue();
    }

    /**
     * Returns the row in the previous filter that maps to the specified row in
     * this filter. If there is no previous filter in the pipeline, this returns
     * the row in the data model that maps to the specified row in this filter.
     * This method is called from
     * {@link org.jdesktop.swing.decorator.Filter#convertRowIndexToModel(int) convertRowIndexToModel}
     *
     * @param row a row index in this filter's "view" of the data model
     * @return the row in the previous filter that maps to the specified row in
     * this filter
     */
    protected int translateToPreviousFilter(int row) {
        return ((Integer) toPrevious.get(row)).intValue();
    }
}