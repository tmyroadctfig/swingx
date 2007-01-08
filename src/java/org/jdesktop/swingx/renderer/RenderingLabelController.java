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

import javax.swing.JLabel;

/**
 * A component controller which uses a JLabel. 
 * 
 * @author Jeanette Winzenburg
 */
public class RenderingLabelController extends RenderingComponentController<JLabel> {

    public RenderingLabelController() {
        this(null);
    }
    
    /**
     * @param converter
     */
    public RenderingLabelController(ToStringConverter converter) {
        super();
        setToStringConverter(converter);
    }

    /**
     * @param alignment
     */
    public RenderingLabelController(int alignment) {
        this();
        setHorizontalAlignment(alignment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JLabel createRendererComponent() {
        return new JRendererLabel();
    }

    /**
     * {@inheritDoc}
     * Here: sets the Label's horizontal alignment to the alignment as configured 
     * in the controller.
     */
    @Override
    protected void configureState(CellContext context) {
       rendererComponent.setHorizontalAlignment(getHorizontalAlignment());
    }

    /**
     * {@inheritDoc}
     * Here: sets the labels text property to the value as returned 
     * from the string representation.
     * 
     * @param the cellContext to use
     * 
     * @see #getStringValue(CellContext) 
     */
    @Override
    protected void format(CellContext context) {
        rendererComponent.setText(getStringValue(context));
    }

    
}
