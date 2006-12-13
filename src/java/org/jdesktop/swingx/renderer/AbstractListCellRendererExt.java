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
package org.jdesktop.swingx.renderer;


import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;


/**
 * The standard class for rendering (displaying) individual cells
 * in a <code>JList</code>.
 * <p>
 *
 * This is refactored from core <code>DefaultListCellRenderer</code> to
 * delegate a performance-optimized label instead of subclassing.<p>
 * 
 * PENDING: really want to carry around the context as parameter in all methods?
 * They are meant to be used by subclasses exclusively. 
 * 
 *
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.42 10/31/05
 * @author Philip Milne 
 * @author Jeanette Winzenburg
 * 
 * @see JXRendererLabel
 * @see JTable
 * 
 */
public abstract class AbstractListCellRendererExt<T extends JComponent> extends AbstractCellRenderer<T, JList> 
    implements ListCellRenderer, Serializable
{

    /**
     * Creates a default table cell renderer.
     */
    public AbstractListCellRendererExt() {
        rendererComponent = createRendererComponent();
        
    }
    // implements javax.swing.table.TableCellRenderer
    /**
     *
     * Returns the default table cell renderer.
     *
     * @param table  the <code>JTable</code>
     * @param value  the value to assign to the cell at
     *                  <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getListCellRendererComponent(JList table, Object value, int row,
                          boolean isSelected, boolean hasFocus) {

        CellContext<JList> context = getCellContext();
        context.installContext(table, value, row, 0, isSelected, hasFocus, true, true);
        configureVisuals(context);
        configureContent(context); 
        return rendererComponent;
    }
    
    @Override
    protected CellContext<JList> getCellContext() {
        if (cellContext == null) {
            cellContext = new ListCellContext();
        }
        return cellContext;
    }

    /**
     * Table specific cellContext.
     */
    public static class ListCellContext extends CellContext<JList> {


        @Override
        protected Color getSelectionBackground() {
            return getComponent() != null ? getComponent().getSelectionBackground() : null;
        }

        @Override
        protected Color getSelectionForeground() {
            return getComponent() != null ? getComponent().getSelectionForeground() : null;
        }

        @Override
        protected String getUIPrefix() {
            return "List.";
        }
        
        
        
    }
    /**
     * A subclass of <code>DefaultTableCellRenderer</code> that
     * implements <code>UIResource</code>.
     * <code>DefaultTableCellRenderer</code> doesn't implement
     * <code>UIResource</code>
     * directly so that applications can safely override the
     * <code>cellRenderer</code> property with
     * <code>DefaultTableCellRenderer</code> subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
//    public static class UIResource extends AbstractTableCellRendererExt 
//        implements javax.swing.plaf.UIResource
//    {
//    }

}


