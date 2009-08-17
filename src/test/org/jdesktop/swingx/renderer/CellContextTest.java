/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.renderer;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.InteractiveTestCase;
import org.junit.Test;

/**
 * CellContext related tests.<p>
 * 
 * PENDING JW: should move all bare context related tests here - currently they are spread
 * across the rendererxxtests, hard to find.
 * 
 * @author Jeanette Winzenburg
 */
public class CellContextTest extends InteractiveTestCase {

    /**
     * Issue #1151-swingx: Nimbus border not used in renderer. 
     * @throws Exception 
     */
    @Test
    public void testListContextNoFocusBorder() throws Exception {
        assertNimbusNoFocusBorder(new ListCellContext());
    }
    /**
     * Issue #1151-swingx: Nimbus border not used in renderer. 
     * @throws Exception 
     */
    @Test
    public void testTableContextNoFocusBorder() throws Exception {
        assertNimbusNoFocusBorder(new TableCellContext());
    }
    
    /**
     * Issue #1151-swingx: Nimbus border not used. 
     * @throws Exception 
     */
    @Test
    public void assertNimbusNoFocusBorder(CellContext context) throws Exception {
        LookAndFeel lf = UIManager.getLookAndFeel();
        try {
            setLookAndFeel("Nimbus");
            context.installState(null, -1, -1, false, false, false, false);
            assertEquals(UIManager.getBorder(context.getUIPrefix() + "cellNoFocusBorder"), context.getBorder());
        } finally {
            UIManager.setLookAndFeel(lf);
        }
    }
}
