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

import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Wrapper around a component which is usable for rendering cells. Acts as
 * <code>Factory</code> for the component. Encapsulates content-related component 
 * configuration, delegates default visual component configuration to a 
 * <code>RendererController</code>.
 * 
 * @author Jeanette Winzenburg
 */
public abstract class RenderingComponentController<T extends JComponent> 
    implements Serializable {
    /** component to render with. */
    protected T rendererComponent;
    /** configurator of default visuals. */
    protected RendererController<T> rendererController;
    /** horizontal (text) alignment of component. PENDING: useful only for labels, buttons? */
    protected int alignment;
    /** the converter to use for string representation. */
    protected ToStringConverter formatter;
    
    public RenderingComponentController() {
        setHorizontalAlignment(JLabel.LEADING);
        setToStringConverter(null);
        rendererComponent = createRendererComponent();
        rendererController = createRendererController();
    }
    

    
    /**
     * Configures and returns an appropriate component to render a cell
     * in the given context.
     * 
     * @param context
     * @return a component to render a cell in the given context.
     */
    public T getRendererComponent(CellContext context) {
        rendererController.configureVisuals(rendererComponent, context);
        configureContent(context);
        return rendererComponent;
    }
    
    /**
     * Sets the horizontal alignment property to configure the component with.
     * Allowed values are those accepted by corresponding JLabel setter. The
     * default value is JLabel.LEADING.
     * 
     * @param alignment the horizontal alignment to use when configuring the
     *   rendering component.
     */
    public void setHorizontalAlignment(int alignment) {
       this.alignment = alignment; 
    }
    
    /**
     * Returns the horizontal alignment.
     * 
     * @return the horizontal component.
     * 
     * @see #setHorizontalAlignment(int)
     * 
     */
    public int getHorizontalAlignment() {
        return alignment;
    }

    /**
     * Sets the ToStringConverter to use. If the given converter is null,
     * uses the default to_string. 
     * 
     * @param formatter the format to use.
     */
    public void setToStringConverter(ToStringConverter formatter) {
        if (formatter == null) {
            formatter = ToStringConverter.TO_STRING;
        }
        this.formatter = formatter;
    }

    /**
     * Returns the converter to use for obtaining the String representation.
     * 
     * @return the ToStringConverter used by this controller, guaranteed to
     *   be not null.
     */
    public ToStringConverter getToStringConverter() {
        return formatter;
    }
    
    /**
     * Configures the renderering component's content and state from the
     * given cell context.
     * 
     * @param context the cell context to configure from
     * 
     * @see #configureState(CellContext)
     * @see #format(CellContext)
     */
    protected void configureContent(CellContext context) {
        configureState(context);
        format(context);
    }

    /**
     * Formats the renderering component's content from the
     * given cell context.
     * 
     * @param context the cell context to configure from
     */
    protected abstract void format(CellContext context);

    /**
     * Configures the renderering component's state from the
     * given cell context.
     * @param context
     */
    protected abstract void configureState(CellContext context); 

    /**
     * Factory method to create and return the component to use for rendering.<p>
     * 
     * @return the component to use for rendering.
     */
    protected abstract T createRendererComponent();

    /**
     * Creates and returns the RendererController used by this.
     * 
     * @return
     */
    protected RendererController<T> createRendererController() {
        return new RendererController<T>();
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
    protected String getStringValue(CellContext context) {
        return formatter.getStringValue(context.getValue());
    }

    /**
     * Intermediate exposure during refactoring...
     * 
     * @return the default visual configurator used by this.
     */
    protected RendererController<T> getRendererController() {
        return rendererController;
    }



}