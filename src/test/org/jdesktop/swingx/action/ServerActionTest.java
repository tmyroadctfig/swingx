/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.action;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class ServerActionTest extends TestCase {

    /**
     * Issue #206-swingx: NPE in addHeader.
     *
     */
    @Test
    public void testNPEAddHeader() {
       ServerAction action = new ServerAction();
       action.addHeader("key", "value");
    }
}
