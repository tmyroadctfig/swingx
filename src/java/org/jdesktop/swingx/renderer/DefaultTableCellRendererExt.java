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
import javax.swing.JTable;

/**
 * Default implementation of AbstractTableCellRendererExt. Uses 
 * a JXRendererLabel for renderering.
 * 
 * @author Jeanette Winzenburg
 */
public class DefaultTableCellRendererExt extends AbstractTableCellRendererExt<JLabel> {


    /**
     * {@inheritDoc}<p>
     * Here: returns a performance-optimized JXRendererLabel as rendering component.
     */
    @Override
    protected JLabel createRendererComponent() {
        return new RendererLabel();
    }

    /**
     * {@inheritDoc} <p>
     * Here: set's the rendering label's text property to the standard 
     * string representation.
     * 
     * @see #getStringValue(CellContext)
     */
    @Override
    protected void configureContent(CellContext<JTable> context) {
        rendererComponent.setText(getStringValue(context));
    }

}
