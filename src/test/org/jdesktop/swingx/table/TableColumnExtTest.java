/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.table;

import junit.framework.TestCase;

/**
 * @author Jeanette Winzenburg
 */
public class TableColumnExtTest extends TestCase {

    /**
     * user friendly resizable flag.
     *
     */
    public void testResizable() {
        TableColumnExt column = new TableColumnExt(0);
        //sanity assert
        assertTrue("min < max", column.getMinWidth() < column.getMaxWidth());
        // sanity assert
        assertTrue("resizable default", column.getResizable());
        column.setMinWidth(column.getMaxWidth());
        assertFalse("must not be resizable with equal min-max", column.getResizable());
        TableColumnExt clone = (TableColumnExt) column.clone();
        // sanity
        assertEquals("min-max of clone", clone.getMinWidth(), clone.getMaxWidth());
        assertFalse("must not be resizable with equal min-max", clone.getResizable());
        clone.setMinWidth(0);
        //sanity assert
        assertTrue("min < max", clone.getMinWidth() < clone.getMaxWidth());
        assertTrue("cloned base resizable", clone.getResizable());
    }
}
