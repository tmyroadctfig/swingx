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
import java.util.regex.PatternSyntaxException;

/**
 * PatternHighlighter
 * 
 * @author Ramesh Gupta
 */
public class PatternHighlighter extends ConditionalHighlighter implements
        PatternMatcher {

    protected Pattern pattern = null;

    /**
     * Constructs a <code>PatternHighlighter</code> instance with no
     * background or foreground color and no pattern
     */
    public PatternHighlighter() {
        // default constructor
        this(null, null, null, 0, 0); // match flags = 0; test column = 0
    }

    /**
     * <p>
     * Constructs a <code>PatternHighlighter</code> instance with the
     * specified background and foreground colors that will be used to decorate
     * the renderer component for all cell in a row if and only if the specified
     * regExString defines a valid {@link java.util.regex.Pattern}, and the
     * value of the cell in the specified testColumn of that row matches that
     * pattern.
     * </p>
     * 
     * @param background
     *            background color for decorated cells, or null, if background
     *            should not be changed
     * @param foreground
     *            foreground color for decorated cells, or null, if foreground
     *            should not be changed
     * @param regExString
     *            the regular expression string to compile, or null to leave the
     *            pattern undefined
     * @param testColumn
     *            column to which the pattern matching test is applied; must be
     *            a valid column index in model coordinates
     * @throws java.util.regex.PatternSyntaxException
     *             if regExString is not null, but it does not define a valid
     *             {@link java.util.regex.Pattern}
     * @see java.util.regex.Pattern
     */
    public PatternHighlighter(Color background, Color foreground,
            String regExString, int matchFlags, int testColumn)
            throws PatternSyntaxException {
        this(background, foreground, regExString, matchFlags, testColumn, -1);
    }

    /**
     * <p>
     * Constructs a <code>PatternHighlighter</code> instance with the
     * specified background and foreground colors that will be used to decorate
     * the renderer component for a cell in the specified decorateColumn of any
     * row if and only if the specified regExString and matchFlags define a
     * valid {@link java.util.regex.Pattern}, and the value of the cell in the
     * specified testColumn of the same row matches that pattern.
     * </p>
     * 
     * @param background
     *            background color for decorated cells, or null, if background
     *            should not be changed
     * @param foreground
     *            foreground color for decorated cells, or null, if foreground
     *            should not be changed
     * @param regExString
     *            the regular expression string to compile, or null to leave the
     *            pattern undefined
     * @param matchFlags
     *            a bit mask that may include
     *            {@link java.util.regex.Pattern#CASE_INSENSITIVE},
     *            {@link java.util.regex.Pattern#MULTILINE},
     *            {@link java.util.regex.Pattern#DOTALL},
     *            {@link java.util.regex.Pattern#UNICODE_CASE}, and
     *            {@link java.util.regex.Pattern#CANON_EQ}
     * @param testColumn
     *            column to which the pattern matching test is applied; must be
     *            a valid column index in model coordinates
     * @param decorateColumn
     *            column to which decorator attributes will be applied; may be a
     *            valid column index in model coordinates, or -1 to indicate all
     *            columns
     * @throws java.util.regex.PatternSyntaxException
     *             if regExString is not null, but regExString and matchFlags do
     *             not define a valid {@link java.util.regex.Pattern}
     * @see java.util.regex.Pattern
     */
    public PatternHighlighter(Color background, Color foreground,
            String regExString, int matchFlags, int testColumn,
            int decorateColumn) throws PatternSyntaxException {
        super(background, foreground, testColumn, decorateColumn);
        setPattern(regExString, matchFlags);
    }

    /**
     * Tests whether the string representation of the value of the cell
     * identified by the specified adapter matches the pattern, if any, that is
     * set for this <code>PatternHighlighter</code>, and returns true if the
     * test succeeds; Otherwise, it returns false.
     * 
     * @param adapter
     *            the current cell rendering adapter
     * @return true if the test succeeds; false otherwise
     */
    protected boolean test(ComponentAdapter adapter) {
        if (pattern == null) {
            return false;
        }

        if (!adapter.isTestable(testColumn))
            return false;
        Object value = adapter.getFilteredValueAt(adapter.row, testColumn);

        if (value == null) {
            return false;
        } else {
            boolean matches = pattern.matcher(value.toString()).find();
            return matches;
        }
    }

    /**
     * Returns the pattern used by this cell decorator for matching against a
     * cell's value to determine if the conditions for cell decoration are met.
     * 
     * @return the pattern used by this cell decorator for matching
     * @see java.util.regex.Pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(String regularExpr, int matchFlags) {
        if ((regularExpr == null) || (regularExpr.length() == 0)) {
            regularExpr = ".*";
        }
        setPattern(Pattern.compile(regularExpr, matchFlags));
    }

    /**
     * Sets the pattern used by this cell decorator to match against a cell's
     * value to determine if the conditions for cell decoration are met.
     * 
     * @param pattern
     *            the pattern used by this cell decorator for matching
     * @see java.util.regex.Pattern
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
        fireStateChanged();
    }

}