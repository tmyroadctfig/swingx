/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.plaf;

import junit.framework.TestCase;

/**
 * This test ensures that all values that should be {@code UIResouce}s are.
 * 
 * @author Karl George Schaefer
 */
public class DefaultsListTest extends TestCase {
    /**
     * Ensure that "adding" a key with a {@code null} value removes the value
     * from the list.
     */
    public void testRemoval() {
        DefaultsList kvList = new DefaultsList();
        kvList.add("a", "b");
        
        Object[] arr = kvList.toArray();
        assertEquals(2, arr.length);
        assertEquals("a", arr[0]);
        assertEquals("b", arr[1]);
        
        kvList.add("a", null);
        assertEquals(0, kvList.toArray().length);
    }
}
