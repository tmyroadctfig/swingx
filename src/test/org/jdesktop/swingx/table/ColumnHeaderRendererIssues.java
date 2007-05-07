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

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableIssues;
import org.jdesktop.swingx.test.XTestUtils;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class ColumnHeaderRendererIssues extends InteractiveTestCase {
    public static void main(String args[]) {
        ColumnHeaderRendererIssues test = new ColumnHeaderRendererIssues();
        setSystemLF(true);
        try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

    /**
     * Issue #??-jdnc: configure header/column to show icon.
     * 
     * All:
     * don't handle header values of icon type by default
     * label properties not reset on each call (which produces
     * problems with the shared ui-provided renderer) 
     *  
     * Mustang:
     * duplicate sort icon (when using swingx sorting)
     * custom icon overpaints sort icon (when using 1.6 sorting) 
     */
    public void interactiveHeaderIcon() {
        final JXTable table = new JXTable();
//        table.setAutoCreateRowSorter(false);
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                if (columnExt.getModelIndex() == 1) {
                    // default can't cope
//                    columnExt.setHeaderValue(XTestUtils.loadDefaultIcon());
//                    columnExt.setHeaderValue(null);
                    // not working - column renderer doesn't reset shared renderers state!
                    ColumnHeaderRenderer renderer = new ColumnHeaderRenderer(table.getTableHeader());
                    renderer.setIcon(XTestUtils.loadDefaultIcon());
                    columnExt.setHeaderRenderer(renderer);
                }
            }
            
        };
        table.setColumnFactory(factory);
        table.setModel(new DefaultTableModel(new String[]{"first", "second", "third"}, 10));
        JScrollPane pane = new JScrollPane(table);
       table.setColumnControlVisible(true);
       final JXFrame frame = wrapInFrame(pane, "Icon on second header - SwingX sort icon");
       frame.setVisible(true);

    }
   
    /**
     * Issue #??-jdnc: configure header/column to show icon.
     * 
     * 
     * All:
     * don't handle header values of icon type by default
     * label properties not reset on each call (which produces
     * problems with the shared ui-provided renderer) 
     *  
     * Mustang:
     * duplicate sort icon (when using swingx sorting)
     * custom icon overpaints sort icon (when using 1.6 sorting) 
     * 
     * Note: this method makes sense only if 
     * compiled, run under 1.6 (and the Mustang api uncommented)
     */
    public void interactiveHeaderIconMustang() {
        final JXTable table = new JXTable();
        // un-comment to see Mustang effect 
//        table.setSortable(false);
//        table.setAutoCreateRowSorter(true);
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                if (columnExt.getModelIndex() == 1) {
                    // default can't cope
//                    columnExt.setHeaderValue(XTestUtils.loadDefaultIcon());
//                    columnExt.setHeaderValue(null);
                    // not working - column renderer doesn't reset shared renderers state!
                    ColumnHeaderRenderer renderer = new ColumnHeaderRenderer(table.getTableHeader());
                    renderer.setIcon(XTestUtils.loadDefaultIcon());
                    columnExt.setHeaderRenderer(renderer);
                }
            }
            
        };
        table.setColumnFactory(factory);
        table.setModel(new DefaultTableModel(new String[]{"first", "second", "third"}, 10));
        JScrollPane pane = new JScrollPane(table);
       table.setColumnControlVisible(true);
       final JXFrame frame = wrapInFrame(pane, "Icon on second header - Mustang sort icon");
       frame.setVisible(true);

    }
    /**
     * Issue #79-jdnc: leading/trailing (?) doesn't work.
     *
     */
    public void interactiveHeaderAlignment() {
        final String[] alignText = {"default", "center", "leading", "left", "trailing", "right"};
        final int[] align = {-1, JLabel.CENTER, JLabel.LEADING, JLabel.LEFT, JLabel.TRAILING, JLabel.RIGHT};
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                int columnIndex = columnExt.getModelIndex();
                columnExt.setHeaderValue(alignText[columnIndex]);
                ColumnHeaderRenderer renderer = ColumnHeaderRenderer.createColumnHeaderRenderer();
                if (align[columnIndex] >= 0) {
                   renderer.setHorizontalAlignment(align[columnIndex]); 
                }
                columnExt.setHeaderRenderer(renderer);
            }
            
        };
        final JXTable table = new JXTable();
        table.setColumnFactory(factory);
        table.setModel(new DefaultTableModel(10, alignText.length));
        JScrollPane pane = new JScrollPane(table);
       table.setColumnControlVisible(true);
       final JXFrame frame = wrapInFrame(pane, "RToL and column text alignment");
       Action toggleComponentOrientation = new AbstractAction("toggle orientation") {

           public void actionPerformed(ActionEvent e) {
               ComponentOrientation current = frame.getComponentOrientation();
               if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                   frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
               } else {
                   frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

               }

           }

       };
       addAction(frame, toggleComponentOrientation);
       frame.setVisible(true);

    }

}
