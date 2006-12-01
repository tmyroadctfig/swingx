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

import org.jdesktop.test.SerializableSupport;

/**
 * Test serializable of all SwingX components.
 * 
 * @author Jeanette Winzenburg
 */
public class SerializableTest extends InteractiveTestCase {

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * 
     */
    public void testTitledSeparator() {
        JXTitledSeparator component = new JXTitledSeparator();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }



    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * Note: this blows as soon as a JXTable is set!
     */
    public void testTableHeader() {
        JXTableHeader component = new JXTableHeader();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.
     * 
     * 
     */
    public void testRootPane() {
        JXRootPane component = new JXRootPane();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.
     * 
     * 
     */
    public void testRadioGroup() {
        JXRadioGroup component = new JXRadioGroup();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.
     * 
     * 
     */
    public void testPanel() {
        JXPanel component = new JXPanel();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.
     * 
     *
     */
    public void testHyperlink() {
        JXHyperlink component = new JXHyperlink();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

}
