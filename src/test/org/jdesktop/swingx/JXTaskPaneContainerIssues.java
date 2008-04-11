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
 *
 */
package org.jdesktop.swingx;

import javax.swing.plaf.UIResource;

import junit.framework.TestCase;

/**
 * 
 * @author Jeanette Winzenburg
 */
public class JXTaskPaneContainerIssues extends TestCase {

    /**
     * Issue #843-swingx: BasicTaskPaneContainerUI must respect custom Layout.
     */
    public void testRespectCustomLayoutGap() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        VerticalLayout layout = (VerticalLayout) container.getLayout();
        VerticalLayout custom = new VerticalLayout(layout.getGap() + 10);
        container.setLayout(custom);
        container.updateUI();
        assertEquals(custom.getGap(), ((VerticalLayout) container.getLayout()).getGap());
    }

    /**
     * Issue #843-swingx: BasicTaskPaneContainerUI must respect custom Layout.
     */
    public void testRespectCustomLayout() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        VerticalLayout layout = (VerticalLayout) container.getLayout();
        VerticalLayout custom = new VerticalLayout(layout.getGap() + 10);
        container.setLayout(custom);
        container.updateUI();
        assertSame(custom, container.getLayout());
    }
    
    /**
     * Issue #843-swingx: BasicTaskPaneContainerUI must respect custom Layout.
     */
    public void testLayoutUIResource() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        assertTrue(container.getLayout() instanceof UIResource);
    }
    
    /**
     * Empty test method to keep the test runner happy if we have no 
     * open issues.
     */
    public void testDummy() {     
    }
}
