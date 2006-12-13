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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.renderer.AbstractTableCellRendererExt.TableCellContext;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public abstract class AbstractCellRenderer<T extends JComponent, C extends JComponent> {

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
    private Color unselectedForeground;
    private Color unselectedBackground;
    protected T rendererComponent;
    protected CellContext<C> cellContext;

    private static Border getNoFocusBorder() {
        if (System.getSecurityManager() != null) {
            return SAFE_NO_FOCUS_BORDER;
        } else {
            return noFocusBorder;
        }
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

    /**
     * Creates and returns the label to use for rendering.<p>
     * Here: returns a performance-optimized JXRendererLabel.
     * 
     * @return the component to use for rendering.
     */
    protected abstract T createRendererComponent();

    /**
     * 
     */
    public AbstractCellRenderer() {
        super();
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

    protected void configureContent(CellContext<C> context) {
        setValue(context.getValue());
    }

    /**
     * @param context
     */
    protected void configureVisuals(CellContext<C> context) {
        configureState(context);
        configureColors(context);
        configureBorder(context);
    }

    /**
     * @param context
     */
    protected void configureState(CellContext<C> context) {
        rendererComponent.setFont(context.getComponent().getFont());
        rendererComponent.setEnabled(context.getComponent().isEnabled());
        rendererComponent.setComponentOrientation(context.getComponent().getComponentOrientation());
    }

    /**
     * @param context
     */
    protected void configureColors(CellContext<C> context) {
        if (context.isSelected()) {
            rendererComponent.setForeground(context.getSelectionForeground());
            rendererComponent.setBackground(context.getSelectionBackground());
         }
         else {
             rendererComponent.setForeground(getForeground(context));
             rendererComponent.setBackground(getBackground(context));
         }
        if (context.isFocused()) {
            configureFocusColors(context);
        }
    }

    protected void configureFocusColors(CellContext<C> context) {
        if (!context.isSelected() && context.isEditable()) {
            Color col = context.getFocusForeground();
            if (col != null) {
                rendererComponent.setForeground(col);
            }
            col = context.getFocusBackground();
            if (col != null) {
                rendererComponent.setBackground(col);
            }
        }
    }

    /**
     * @param context
     */
    protected void configureBorder(CellContext<C> context) {
        if (context.isFocused()) {
            rendererComponent.setBorder(context.getFocusBorder());
        } else {
            rendererComponent.setBorder(getNoFocusBorder());
        }
        
    }

    protected Color getForeground(CellContext<C> context) {
        if (unselectedForeground != null) return unselectedForeground;
        return context.getForeground();
    }

    protected Color getBackground(CellContext<C> context) {
        if (unselectedBackground != null) return unselectedBackground;
        return context.getBackground();
    }

    /**
     * 
     * @return the cell context to use, guaranteed to be <code>not null</code>.
     */
    protected abstract CellContext<C> getCellContext();

}