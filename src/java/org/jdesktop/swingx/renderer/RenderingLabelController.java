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

import java.text.Format;

import javax.swing.JLabel;

/**
 * A component controller which uses a JLabel. 
 * 
 * @author Jeanette Winzenburg
 */
public class RenderingLabelController extends RenderingComponentController<JLabel> {

    protected Format formatter;
 
    public RenderingLabelController() {
    }
    
    /**
     * The Format to use in content configuration. 
     * 
     * @param formatter the format to use.
     */
    public void setFormatter(Format formatter) {
        this.formatter = formatter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JLabel createRendererComponent() {
        return new JRendererLabel();
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to use the Format, if available. 
     * 
     */
    @Override
    protected String getStringValue(CellContext context) {
        if (formatter != null) {
            return context.getValue() != null ? formatter.format(context.getValue()) : "";
        }
        return super.getStringValue(context);
    }

    /**
     * {@inheritDoc}
     * Here: sets the Label's horizontal alignment to the alignment as configured 
     * in the controller.
     */
    @Override
    protected void configureState(CellContext context) {
       rendererComponent.setHorizontalAlignment(getAlignment());
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
