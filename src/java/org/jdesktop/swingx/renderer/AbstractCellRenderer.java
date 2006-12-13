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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Abstract base class of all extended CellRenderers in SwingX.
 * <p>
 * 
 * PENDING: formulate constistently<p>
 * 
 * This is refactored from core <code>DefaultTableCellRenderer</code> to
 * delegate a performance-optimized label instead of subclassing.<p>
 * Extracted for consistency across different renderer types. Takes over all the
 * standard (LF) specific configuration of the rendering component. Concrete
 * renderers which choose to go from here should implement the appropriate
 * getXXCellRendererComponent to delegate to the configureYY methods.
 * 
 * <pre><code>
 * public Component getTableCellRendererComponent(JTable table, Object value,
 *         boolean isSelected, boolean hasFocus, int row, int column) {
 * 
 *     CellContext&lt;JTable&gt; context = getCellContext();
 *     context.install(table, value, row, column, isSelected, hasFocus,
 *             true, true);
 *     configureVisuals(context);
 *     configureContent(context);
 *     return rendererComponent;
 * }
 * 
 * </code></pre>
 * 
 * 
 * It's parameterized for both renderee (C) and rendering component(T).<p>
 * 
 * PENDING: extract super, parametrized in CellContext for implementation
 * of default list, tree renderers. This will further eleminate duplication
 * and enhance consistency of renderer behaviour.<p>
 * 
 * PENDING: really want to carry around the context as parameter in all methods?
 * They are meant to be used by subclasses exclusively. <p>
 * 
 * PENDING: where to put the UIResource? 
* 
 * @see CellContext
 * 
 * @author Jeanette Winzenburg
 * 
 * 
 */
public abstract class AbstractCellRenderer<T extends JComponent, C extends JComponent> {

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1,
            1);

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
     * Instantiates the renderer component to the type specified
     * by the subclass.
     * 
     * @see #createRendererComponent()
     */
    public AbstractCellRenderer() {
        rendererComponent = createRendererComponent();
    }

    //----------------- abstract methods    
    /**
     * Configures the renderering component's content from the
     * given cell context.
     * 
     * @param context the cell context to configure from
     * 
     */
    protected abstract void configureContent(CellContext<C> context);

    /**
     * Factory method to create and return the component to use for rendering.<p>
     * 
     * @return the component to use for rendering.
     */
    protected abstract T createRendererComponent();

    /**
     * Returns the cell context to use.
     * 
     * @return the cell context to use, guaranteed to be <code>not null</code>.
     */
    protected abstract CellContext<C> getCellContext();

    //------------------ public configuration

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
     * Sets the renderer's unselected-background color to the specified color.
     *
     * @param c set the background color to this value
     */
    public void setBackground(Color c) {
        unselectedBackground = c;
    }

    //---------------- subclass configuration    
    /**
     * Configures all visual state of the rendering component from the 
     * given cell context.
     * 
     * @param context the cell context to configure from.
     */
    protected void configureVisuals(CellContext<C> context) {
        configureState(context);
        configureColors(context);
        configureBorder(context);
    }

    /**
     * Configure "divers" visual state of the rendering component from
     * the given cell context. <p>
     * 
     * Here: synch <code>Font</code>, <code>ComponentOrientation</code> and 
     * <code>enabled</code> to context's 
     * component. 
     * 
     * PENDING: not fully defined - "divers" means everything that's not
     * colors or border.<p>
     * 
     * PENDING: doesn't check for null context component
     * 
     * @param context the cell context to configure from.
     */
    protected void configureState(CellContext<C> context) {
        rendererComponent.setFont(context.getComponent().getFont());
        rendererComponent.setEnabled(context.getComponent().isEnabled());
        rendererComponent.setComponentOrientation(context.getComponent()
                .getComponentOrientation());
    }

    /**
     * Configures colors of rendering component from the given cell context.
     * 
     * @param context the cell context to configure from.
     */
    protected void configureColors(CellContext<C> context) {
        if (context.isSelected()) {
            rendererComponent.setForeground(context.getSelectionForeground());
            rendererComponent.setBackground(context.getSelectionBackground());
        } else {
            rendererComponent.setForeground(getForeground(context));
            rendererComponent.setBackground(getBackground(context));
        }
    }

    /**
     * Configures the rendering component's border from the given cell context.<p>
     * 
     * @param context the cell context to configure from.
     */
    protected void configureBorder(CellContext<C> context) {
        if (context.isFocused()) {
            rendererComponent.setBorder(context.getFocusBorder());
        } else {
            rendererComponent.setBorder(getNoFocusBorder());
        }

    }

    /**
     * Returns the unselected foreground to use for the rendering 
     * component. <p>
     * 
     * Here: returns this renderer's unselected foreground is not null,
     * returns the foreground from the given context. In other words:
     * the renderer's foreground takes precedence if set.
     * 
     * @param context the cell context.
     * @return the unselected foreground.
     */
    protected Color getForeground(CellContext<C> context) {
        if (unselectedForeground != null)
            return unselectedForeground;
        return context.getForeground();
    }

    /**
     * Returns the unselected background to use for the rendering 
     * component. <p>
     * 
     * Here: returns this renderer's unselected background is not null,
     * returns the background from the given context. In other words:
     * the renderer's background takes precedence if set.
     * 
     * @param context the cell context.
     * @return the unselected background.
     */
    protected Color getBackground(CellContext<C> context) {
        if (unselectedBackground != null)
            return unselectedBackground;
        return context.getBackground();
    }

    /**
     * Returns a string representation of the content.<p>
     * 
     * PENDING: This is a first attempt - we need a consistent string representation
     * across all (new and old) theme: rendering, (pattern) filtering/highlighting,
     * searching, auto-complete, what else??   
     * 
     * @param context the cell context.
     * @return a appropriate string representation of the cell's content.
     */
    protected String getStringValue(CellContext<C> context) {
        return context.getValue() != null ? context.getValue().toString() : "";
    }

}