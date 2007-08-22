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
import javax.swing.JLabel;

/**
 * A component provider which uses a AbstractButton. <p>
 * 
 * PENDING: rename ... this is actually a CheckBoxProvider.
 * 
 * @author Jeanette Winzenburg
 */
public class ButtonProvider extends ComponentProvider<AbstractButton> {

    private boolean borderPainted;
    /**
     * Instantiates a ButtonProvider with CENTER
     * horizontal alignment and default border painted. <p> 
     *
     */
    public ButtonProvider() {
        super(null, JLabel.CENTER);
        setBorderPainted(true);
    }
    
    /**
     * Returns the border painted flag.
     * @return the borderpainted flag to use on the checkbox.
     * @see #setBorderPainted(boolean)
     */
    public boolean isBorderPainted() {
        return borderPainted;
    }
    
    /**
     * Sets the border painted flag. the underlying checkbox
     * is configured with this value on every request.<p>
     * 
     * The default value is true.
     * 
     * @param borderPainted the borderPainted property to configure
     *   the underlying checkbox with.
     *   
     *  @see #isBorderPainted() 
     */
    public void setBorderPainted(boolean borderPainted) {
        this.borderPainted = borderPainted;
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
        rendererComponent.setBorderPainted(isBorderPainted());
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

    

}
