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

import javax.swing.JComponent;

public abstract class RenderingComponentController<T extends JComponent> {
    protected T rendererComponent;
    
    public RenderingComponentController() {
        rendererComponent = createRendererComponent();
    }
    public T getRendererComponent() {
        return rendererComponent;
    }
    /**
     * Configures the renderering component's content from the
     * given cell context.
     * 
     * @param context the cell context to configure from
     * 
     */
    protected abstract void configureContent(CellContext context);

    /**
     * Factory method to create and return the component to use for rendering.<p>
     * 
     * @return the component to use for rendering.
     */
    protected abstract T createRendererComponent();


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
        return context.getValue() != null ? context.getValue().toString() : "";
    }

}