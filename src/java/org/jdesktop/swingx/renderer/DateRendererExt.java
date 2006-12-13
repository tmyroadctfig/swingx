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

import java.text.DateFormat;

import javax.swing.JTable;

/**
 * Implementation of AbstractTableCellRendererExt for Date values. Uses 
 * a performance-optimized JXRendererLabel for renderering. Formats the
 * Date to a Formatter.
 * 
 * @author Jeanette Winzenburg
 */
public class DateRendererExt extends DefaultTableCellRendererExt {
    private final DateFormat formatter;

    public DateRendererExt() {
        this(null);
    }

    public DateRendererExt(DateFormat formatter) {
        if (formatter == null) {
            formatter = DateFormat.getDateInstance();
        }
        this.formatter = formatter;
    }

    /**
     * {@inheritDoc} <p>
     * Here: returns the string representation as formatted by the renderer's
     * dateFormatter.<p>
     * 
     * PENDING: think about moving up in the hierarchy?
     * 
     */
    @Override
    protected String getStringValue(CellContext<JTable> context) {
        return context.getValue() != null ? formatter.format(context.getValue()) : "";
    }


}
