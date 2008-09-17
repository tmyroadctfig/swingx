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
package org.jdesktop.swingx.table;

import java.awt.BorderLayout;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.JTextField;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;

/**
 * Test ColumnHeaderRenderer.
 * 
 * @author Jeanette Winzenburg
 */
public class ColumnHeaderRendererTest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(ColumnHeaderRendererTest.class.getName());

    public static void main(String args[]) {
        ColumnHeaderRendererTest test = new ColumnHeaderRendererTest();
        setSystemLF(true);
        try {
          test.runInteractiveTests();
//          test.runInteractiveTests(".*Create.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }
    


    /**
     * hack around big vista border.
     *  
     */
    public void interactiveVistaTableHeader() {
        final JTextField field = new JTextField();
        // sanity: custom borders untouched
//        Border customBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
//        UIManager.put(ColumnHeaderRenderer.VISTA_BORDER_HACK, customBorder);
        JXTable xTable = new JXTable(10, 3);
        JTable table = new JTable(xTable.getModel());
        JXFrame frame = wrapWithScrollingInFrame(xTable, table, "JXTable <-> JTable: compare header height");
        frame.add(field, BorderLayout.SOUTH);
        show(frame);
    }

    /**
     * Issue #540-swingx: sort icon in custom header renderer
     * not updated to ui. Reason was that the empty constructor
     * didn't load the ui-specific ion.
     *
     */
    public void interactiveHeaderRendererCreate() {
        JXTable table = new JXTable(10, 2);
        ColumnHeaderRenderer renderer = new ColumnHeaderRenderer();
        table.getColumnExt(1).setHeaderRenderer(renderer);
        showWithScrollingInFrame(table, "sortIcon in second column wrong");
    }
 
    public void testDummy() {
        // keep the test framework happy
    }
}
