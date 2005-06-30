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
public class TableColumnExtIssues extends TestCase {

    /**
     * Client properties not preserved when cloning.
     *
     */
    public void testClientPropertyClone() {
        TableColumnExt column = new TableColumnExt(0);
        String key = "property";
        column.putClientProperty(key, new Object());
        TableColumnExt cloned = (TableColumnExt) column.clone();
        assertNotNull("client property must be in cloned", cloned.getClientProperty(key));
    }
    

}
