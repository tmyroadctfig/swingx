/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.action;

import junit.framework.TestCase;

public class ServerActionTest extends TestCase {

    /**
     * Issue #206-swingx: NPE in addHeader.
     *
     */
    public void testNPEAddHeader() {
       ServerAction action = new ServerAction();
       action.addHeader("key", "value");
    }
}
