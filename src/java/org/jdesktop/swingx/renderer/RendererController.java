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

import org.jdesktop.swingx.RolloverRenderer;

/**
 * Encapsulates configuration of renderering components.
 * <p>
 * 
 * 
 * It's parameterized for both renderee (C) and rendering component(T).<p>
 * 
 * 
 * @author Jeanette Winzenburg
 * 
 * 
 */
public class RendererController<T extends JComponent, C extends JComponent> 
   implements RolloverRenderer {

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1,
            1);

    private Color unselectedForeground;

    private Color unselectedBackground;


    private RenderingComponentController<T> componentContext;
    
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
    public RendererController(RenderingComponentController<T> componentContext) {
        setComponentContext(componentContext);
    }

    /**
     * Configures the renderering component's content from the
     * given cell context.
     * 
     * @param context the cell context to configure from
     * 
     */
    protected void configureContent(CellContext<C> context) {
        getComponentContext().configureContent(context);
    }

    /**
     * @param componentContext the componentContext to set
     */
    protected void setComponentContext(RenderingComponentController<T> componentContext) {
        if (componentContext == null) {
            componentContext = createDefaultComponentContext();
        }
        this.componentContext = componentContext;
    }

    /**
     * @return
     */
    protected RenderingComponentController<T> createDefaultComponentContext() {
        return (RenderingComponentController<T>) new RenderingLabelController();
    }

    /**
     * @return the componentContext
     */
    protected RenderingComponentController<T> getComponentContext() {
        return componentContext;
    }
    

    /**
     * @return the rendererComponent
     */
    protected T getRendererComponent() {
        return getComponentContext().rendererComponent;
    }


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

    public void configure(CellContext<C> context) {
        configureVisuals(context);
        configureContent(context);
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
        getRendererComponent().setFont(context.getComponent().getFont());
        getRendererComponent().setEnabled(context.getComponent().isEnabled());
        getRendererComponent().setComponentOrientation(context.getComponent()
                .getComponentOrientation());
    }

    /**
     * Configures colors of rendering component from the given cell context.
     * 
     * @param context the cell context to configure from.
     */
    protected void configureColors(CellContext<C> context) {
        if (context.isSelected()) {
            getRendererComponent().setForeground(context.getSelectionForeground());
            getRendererComponent().setBackground(context.getSelectionBackground());
        } else {
            getRendererComponent().setForeground(getForeground(context));
            getRendererComponent().setBackground(getBackground(context));
        }
        if (context.isFocused()) {
            configureFocusColors(context);
        }
    }
    /**
     * Configures focus-related colors form given cell context.<p>
     * 
     * PENDING: move to context as well? - it's the only comp
     * with focus specifics? Problem is the parameter type...
     * 
     * @param context the cell context to configure from.
     */
    protected void configureFocusColors(CellContext<C> context) {
        if (!context.isSelected() && context.isEditable()) {
            Color col = context.getFocusForeground();
            if (col != null) {
                getRendererComponent().setForeground(col);
            }
            col = context.getFocusBackground();
            if (col != null) {
                getRendererComponent().setBackground(col);
            }
        }
    }


    /**
     * Configures the rendering component's border from the given cell context.<p>
     * 
     * @param context the cell context to configure from.
     */
    protected void configureBorder(CellContext<C> context) {
        if (context.isFocused()) {
            getRendererComponent().setBorder(context.getFocusBorder());
        } else {
            getRendererComponent().setBorder(getNoFocusBorder());
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
        return getComponentContext().getStringValue(context);
//        return context.getValue() != null ? context.getValue().toString() : "";
    }

//--------------------- RolloverRenderer    
    public void doClick() {
        if (isEnabled()) {
            ((RolloverRenderer) getComponentContext()).doClick(); 
        }
        
    }

    public boolean isEnabled() {
        return (getComponentContext() instanceof RolloverRenderer) && 
           ((RolloverRenderer) getComponentContext()).isEnabled();
    }

}