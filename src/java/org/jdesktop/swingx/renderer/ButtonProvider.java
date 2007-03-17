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

/**
 * A component provider which uses a AbstractButton. 
 * 
 * @author Jeanette Winzenburg
 */
public class ButtonProvider extends ComponentProvider<AbstractButton> {

    private boolean borderPainted;

    public ButtonProvider() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setBorderPainted(true);
    }
    
    /**
     * {@inheritDoc} <p>
     * Overridden to set the button's selected state. It's set to true if the 
     * context's value equals Boolean.TRUE, false otherwise.
     */
    @Override
    protected void format(CellContext context) {
        boolean selected = Boolean.TRUE.equals(context.getValue());
        rendererComponent.setSelected(selected);
    }
    
    /**
     * {@inheritDoc}<p>
     * 
     * Here: set's the buttons horizontal alignment and borderpainted properties
     * to this controller's properties.
     */
    protected void configureState(CellContext context) {
        rendererComponent.setBorderPainted(borderPainted);
        rendererComponent.setHorizontalAlignment(getHorizontalAlignment());
    }

    /**
     * {@inheritDoc}<p>
     * Here: returns a JCheckBox as rendering component.<p>
     * 
     */
    @Override
    protected AbstractButton createRendererComponent() {
        return new JRendererCheckBox();
    }

    /**
     * @param b
     */
    protected void setBorderPainted(boolean b) {
        this.borderPainted = b;
        
    }

    

}
