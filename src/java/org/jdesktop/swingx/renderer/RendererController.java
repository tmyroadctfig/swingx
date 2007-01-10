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
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Encapsulates the default visual configuration of renderering components.
 * <p>
 * @author Jeanette Winzenburg
 * 
 */
public class RendererController<T extends JComponent> implements Serializable {

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1,
            1);

    private static Border getNoFocusBorder() {
        if (System.getSecurityManager() != null) {
            return SAFE_NO_FOCUS_BORDER;
        } else {
            return noFocusBorder;
        }
    }


    private Color unselectedForeground;

    private Color unselectedBackground;

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
     * @param renderingComponent TODO
     * @param context the cell context to configure from.
     */
    public void configureVisuals(T renderingComponent, CellContext context) {
        configureState(renderingComponent, context);
        configureColors(renderingComponent, context);
        configureBorder(renderingComponent, context);
        configurePainter(renderingComponent, context);
    }

    /**
     * @param renderingComponent TODO
     * @param context
     */
    protected void configurePainter(T renderingComponent, CellContext context) {
        if (renderingComponent instanceof PainterAware) {
            ((PainterAware) renderingComponent).setPainter(null);
        }
        
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
     * @param renderingComponent the component to configure
     * @param context the cell context to configure from.
     */
    protected void configureState(T renderingComponent, CellContext context) {
        renderingComponent.setFont(context.getComponent().getFont());
        renderingComponent.setEnabled(context.getComponent().isEnabled());
        renderingComponent.applyComponentOrientation(context.getComponent()
                .getComponentOrientation());
    }

    /**
     * Configures colors of rendering component from the given cell context.
     * 
     * @param context the cell context to configure from.
     */
    protected void configureColors(T renderingComponent, CellContext context) {
        if (context.isSelected()) {
            renderingComponent.setForeground(context.getSelectionForeground());
            renderingComponent.setBackground(context.getSelectionBackground());
        } else {
            renderingComponent.setForeground(getForeground(context));
            renderingComponent.setBackground(getBackground(context));
        }
        if (context.isFocused()) {
            configureFocusColors(renderingComponent, context);
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
    protected void configureFocusColors(T renderingComponent, CellContext context) {
        if (!context.isSelected() && context.isEditable()) {
            Color col = context.getFocusForeground();
            if (col != null) {
                renderingComponent.setForeground(col);
            }
            col = context.getFocusBackground();
            if (col != null) {
                renderingComponent.setBackground(col);
            }
        }
    }


    /**
     * Configures the rendering component's border from the given cell context.<p>
     * 
     * @param context the cell context to configure from.
     */
    protected void configureBorder(T renderingComponent, CellContext context) {
//        getRendererComponent().setBorder(context.getBorder());
        if (context.isFocused()) {
            renderingComponent.setBorder(context.getFocusBorder());
        } else {
            renderingComponent.setBorder(getNoFocusBorder());
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
    protected Color getForeground(CellContext context) {
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
    protected Color getBackground(CellContext context) {
        if (unselectedBackground != null)
            return unselectedBackground;
        return context.getBackground();
    }


//--------------------- RolloverRenderer    
    

}