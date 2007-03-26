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
package org.jdesktop.swingx.painter;

import java.awt.*;
import java.awt.image.BufferedImageOp;

/**
 * A useful Painter implementation for Testing purposes
 * @author rbair
 */
class TestablePainter extends AbstractPainter {
    boolean painted = false;
    boolean configured = false;
    boolean configureCalledFirst = false;
    Object last;
    protected void doPaint(Graphics2D g, Object obj, int width, int height) {
        painted = true;
        last = obj;
    }

    protected void validate(Object object) {
        if (last != object) {
            clearCache();
            setDirty(true);
        }
    }

    protected void configureGraphics(Graphics2D g) {
        configured = true;
        configureCalledFirst = configured && !painted;
    }

    void reset() {
        painted = false;
        configured = false;
        setCacheable(false);
        clearCache();
        setFilters((BufferedImageOp[])null);
        setDirty(false);
    }
}
