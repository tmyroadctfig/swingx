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
 * NOTE: there's a generics problem in createDefaultRenderingController. Eclipse is
 * happy but the standard compiler barks. So it returns null for now!
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
     * Instantiates a RendererController with the given component controller
     * @param componentController the component controller to configure, must not be null
     */
    public RendererController(RenderingComponentController<T> componentController) {
        setComponentController(componentController);
    }


    /**
     * The component's wrapper to use.
     * 
     * @param componentContext the componentContext to set
     */
    protected void setComponentController(RenderingComponentController<T> componentContext) {
        this.componentContext = componentContext;
    }


    /**
     * @return the componentContext
     */
    protected RenderingComponentController<T> getComponentController() {
        return componentContext;
    }
    

    /**
     * @return the rendererComponent
     */
    protected T getRendererComponent() {
        return getComponentController().rendererComponent;
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

    /**
     * Configure the rendering component from the given cell context.
     * Here: handles the visuals itself, 
     * delegates the content config to the component controller.
     * 
     * @param context the cell's context in the renderee.
     */
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
     * Configures the renderering component's content from the
     * given cell context.
     * 
     * @param cellContext the cell context to configure from
     * 
     */
    protected void configureContent(CellContext<C> cellContext) {
        getComponentController().configureContent(cellContext);
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
     * Here: delegates to the component controller.
     * 
     * PENDING: This is a first attempt - we need a consistent string representation
     * across all (new and old) theme: rendering, (pattern) filtering/highlighting,
     * searching, auto-complete, what else??   
     * 
     * @param context the cell context.
     * @return a appropriate string representation of the cell's content.
     */
    protected String getStringValue(CellContext<C> context) {
        return getComponentController().getStringValue(context);
    }

//--------------------- RolloverRenderer    
    
    /**
     * {@inheritDoc}
     */
    public void doClick() {
        if (isEnabled()) {
            ((RolloverRenderer) getComponentController()).doClick(); 
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return (getComponentController() instanceof RolloverRenderer) && 
           ((RolloverRenderer) getComponentController()).isEnabled();
    }

}