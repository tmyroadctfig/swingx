/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Point;

import org.jdesktop.swingx.RolloverProducer;

/**
 * @author Jeanette Winzenburg
 */
public class RolloverHighlighter extends ConditionalHighlighter {

    public RolloverHighlighter(Color cellBackground, Color cellForeground) {
        super(cellBackground, cellForeground, -1, -1);
    }

    protected boolean test(ComponentAdapter adapter) {
        // JW: where to put constants? 
        // this is a back-reference into swingx simply to access
        // a string constant. Hmmm...
        Point p = (Point) adapter.getComponent().getClientProperty(
                RolloverProducer.ROLLOVER_KEY);
        return p != null &&  p.y == adapter.row;
    }

}
