/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.Color;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXTable;

public class FilterTest extends TestCase {

    /**
     * This is a test to ensure that the example in the javadoc actually works.
     * if the javadoc example changes, then those changes should be pasted here.
     */
    public void testJavaDocExample() {

        Filter[] filters = new Filter[] {
            new PatternFilter("S.*", 0, 1), // regex, matchflags, column
            new ShuttleSorter(1, false),   // column 1, descending
            new ShuttleSorter(0, true),    // column 0, ascending
        };
        FilterPipeline pipeline = new FilterPipeline(filters);
        JXTable table = new JXTable();
        table.setFilters(pipeline);
    }
}
