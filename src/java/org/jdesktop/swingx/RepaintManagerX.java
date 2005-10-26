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

package org.jdesktop.swingx;

import java.awt.Container;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 * This repaint manager is used by Swingx for translucency. The default implementation
 * of JXPanel (which supports translucency) will replace the current RepaintManager
 * with a RepaintManagerX *unless* the current RepaintManager is
 * tagged by the "TranslucentRepaintManager" interface. 
 * <p>TODO: Add this to the main documentation (make it visible) so that people
 * don't bump into it accidently and spend time debugging</p>
 *
 * @author zixle
 * @author rbair
 */
public class RepaintManagerX extends RepaintManager implements TranslucentRepaintManager {
    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        Rectangle dirtyRegion = getDirtyRegion(c);
        if (dirtyRegion.width == 0 && dirtyRegion.height == 0) {
            int lastDeltaX = c.getX();
            int lastDeltaY = c.getY();
            Container parent = c.getParent();
            while (parent instanceof JComponent) {
                if (!parent.isVisible() || (parent.getPeer() == null)) {
                    return;
                }
                if (parent instanceof JXPanel && ((JXPanel)parent).getAlpha() < 1f) {
                    x += lastDeltaX;
                    y += lastDeltaY;
                    lastDeltaX = lastDeltaY = 0;
                    c = (JComponent)parent;
                }
                lastDeltaX += parent.getX();
                lastDeltaY += parent.getY();
                parent = parent.getParent();
            }
        }
        super.addDirtyRegion(c, x, y, w, h);
    }
}