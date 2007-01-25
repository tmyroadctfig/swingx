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

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * A component provider which uses a JLabel. 
 * 
 * @author Jeanette Winzenburg
 */
public class LabelProvider extends ComponentProvider<JLabel> {

    /**
     * Instantiates a default component provider with LEADING
     * horizontal alignment and default to-String converter. <p> 
     *
     */
    public LabelProvider() {
        this(null);
    }
    
    /**
     * Instantiates a default component provider with LEADING
     * horizontal alignment and the given to-String converter. If 
     * the converter is null, the default TO_STRING is used. <p> 
     * 
     * @param converter the converter to use for mapping the cell value
     *   to a String representation.
     */
    public LabelProvider(StringValue converter) {
        super();
        setToStringConverter(converter);
    }

    /**
     * Instantiates a default component provider with the given 
     * horizontal alignment and default to-String converter. <p> 
     * 
     * @param alignment the horizontal alignment.
     */
    public LabelProvider(int alignment) {
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
     * Here: sets the labels text and icon property depending on the
     * type of the context's value. If the value is of <code>Icon</code> type, it's 
     * used as the label's icon and the text is set to empty. Otherwise,
     * the icon is set to null and the text is set to the value as returned
     * from getStringValue. <p>
     * 
     * Note: this is the behaviour as implemented in core default list renderer. 
     * It is different from core default table renderer which handles icons in
     * a subclass only. 
     * 
     * @param context the cellContext to use
     * 
     * @see #getStringValue(CellContext) 
     */
    @Override
    protected void format(CellContext context) {
        if (context.getValue() instanceof Icon) {
            rendererComponent.setIcon((Icon) context.getValue());
            rendererComponent.setText(getStringValue(null));
        } else {
            rendererComponent.setIcon(null);
            rendererComponent.setText(getStringValue(context));
        }
    }

    
}
