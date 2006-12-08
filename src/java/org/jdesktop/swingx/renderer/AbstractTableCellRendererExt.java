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


import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.*;

import java.awt.Component;
import java.awt.Color;
import java.awt.Rectangle;

import java.io.Serializable;


/**
 * The standard class for rendering (displaying) individual cells
 * in a <code>JTable</code>.
 * <p>
 *
 * This is refactored from core <code>DefaultTableCellRenderer</code> to
 * a performance-optimized label instead of subclassing.
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
public abstract class AbstractTableCellRendererExt<T extends JComponent> 
    implements TableCellRenderer, Serializable
{

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1); 
    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

    // We need a place to store the color the JLabel should be returned 
    // to after its foreground and background colors have been set 
    // to the selection background color. 
    // These ivars will be made protected when their names are finalized. 
    private Color unselectedForeground; 
    private Color unselectedBackground; 

    protected T rendererLabel;
    
    /**
     * Creates a default table cell renderer.
     */
    public AbstractTableCellRendererExt() {
        rendererLabel = createRendererComponent();
//        setBorder(getNoFocusBorder());
    }
    /**
     * Sets the <code>String</code> object for the cell being rendered to
     * <code>value</code>.
     * 
     * @param value  the string value for this cell; if value is
     *          <code>null</code> it sets the text value to an empty string
     * @see JLabel#setText
     * 
     */
    protected abstract void setValue(Object value);
    
//    {
//        rendererLabel.setText((value == null) ? "" : value.toString());
//    }

    /**
     * Creates and returns the label to use for rendering.<p>
     * Here: returns a performance-optimized JXRendererLabel.
     * 
     * @return the label to use for rendering.
     */
    protected abstract T createRendererComponent();

    private static Border getNoFocusBorder() {
        if (System.getSecurityManager() != null) {
            return SAFE_NO_FOCUS_BORDER;
        } else {
            return noFocusBorder;
        }
    }

    /**
     * Overrides <code>JComponent.setForeground</code> to assign
     * the unselected-foreground color to the specified color.
     * 
     * @param c set the foreground color to this value
     */
    public void setForeground(Color c) {
        unselectedForeground = c; 
    }
    
    /**
     * Overrides <code>JComponent.setBackground</code> to assign
     * the unselected-background color to the specified color.
     *
     * @param c set the background color to this value
     */
    public void setBackground(Color c) {
        unselectedBackground = c; 
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
    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, int column) {

        setValue(value); 
        configureVisuals(table, value, isSelected, hasFocus, row, column);
        return rendererLabel;
    }

    protected void configureVisuals(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        configureSelectionDependentColors(table, isSelected);
        configureFromTable(table);
        configureFocusDependentState(table, isSelected, hasFocus, row, column);
    }

    protected void configureFromTable(JTable table) {
        rendererLabel.setFont(table.getFont());
        rendererLabel.setEnabled(table.isEnabled());
        rendererLabel.setComponentOrientation(table.getComponentOrientation());
    }

    protected void configureFocusDependentState(JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
        if (hasFocus) {
            Border border = null;
            if (isSelected) {
                border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            rendererLabel.setBorder(border);

            if (!isSelected && table.isCellEditable(row, column)) {
                Color col;
                col = UIManager.getColor("Table.focusCellForeground");
                if (col != null) {
                    rendererLabel.setForeground(col);
                }
                col = UIManager.getColor("Table.focusCellBackground");
                if (col != null) {
                    rendererLabel.setBackground(col);
                }
            }
        } else {
            rendererLabel.setBorder(getNoFocusBorder());
        }
    }

    protected void configureSelectionDependentColors(JTable table, boolean isSelected) {
        if (isSelected) {
           rendererLabel.setForeground(table.getSelectionForeground());
           rendererLabel.setBackground(table.getSelectionBackground());
        }
        else {
            rendererLabel.setForeground((unselectedForeground != null) ? unselectedForeground 
                                                               : table.getForeground());
            rendererLabel.setBackground((unselectedBackground != null) ? unselectedBackground 
                                                               : table.getBackground());
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


