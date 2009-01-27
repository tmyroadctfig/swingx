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
package org.jdesktop.swingx;


import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;


import junit.framework.TestCase;

/**
 * Unit test for <code>JXButton</code>.
 * <p>
 * 
 * All test methods in this class are expected to pass. 
 * 
 * @author rah003
 */
@RunWith(JUnit4.class)
public class JXButtonTest extends TestCase {

    /**
     * Issue #621-swingx: JXButton shares default fg/bg painters between instances.
     *
     */
    @Test
    public void testNonStaticPainters() {
        JXButton b1 = new JXButton();
        JXButton b2 = new JXButton();
    	assertNotSame("the painters must be unique", 
                b1.getForegroundPainter(), b2.getForegroundPainter());
    	assertNotSame("the painters must be unique", 
                b1.getBackgroundPainter(), b2.getBackgroundPainter());
    }

 }
