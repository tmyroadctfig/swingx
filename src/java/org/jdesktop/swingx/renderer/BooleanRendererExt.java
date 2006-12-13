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

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * Implementation of AbstractTableCellRendererExt for boolean values. Uses 
 * a JCheckBox for renderering.
 * 
 * @author Jeanette Winzenburg
 */
public class BooleanRendererExt extends AbstractTableCellRendererExt<AbstractButton> {

    
    /**
     * {@inheritDoc}<p>
     * Here: returns a base-configured (centered, borderpainted) 
     * JCheckBox as rendering component.<p>
     * 
     * PENDING: config should happen elsewhere?
     */
    @Override
    protected AbstractButton createRendererComponent() {
        JCheckBox box = new JCheckBox();
        box.setHorizontalAlignment(JLabel.CENTER);
        box.setBorderPainted(true);
        return  box;
    }

    

    /**
     * {@inheritDoc} <p>
     * Here: set's the checkbox' selected property to true if
     * the context's value is a boolean true, false otherwise.
     * 
     * @see #getStringValue(CellContext)
     */
    @Override
    protected void configureContent(CellContext<JTable> context) {
        boolean selected = Boolean.TRUE.equals(context.getValue());
        rendererComponent.setSelected(selected);
        
    }


}
