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
import java.awt.Component;

/**
 * HierarchicalColumnHighlighter
 *
 * @author Ramesh Gupta
 */
public class HierarchicalColumnHighlighter extends Highlighter {
    public HierarchicalColumnHighlighter() {
    }

    public HierarchicalColumnHighlighter(Color background, Color foreground) {
        super(background, foreground);
    }

    protected Color computeBackground(Component component, ComponentAdapter adapter) {
        if (adapter.isHierarchical()) {
            Color background = getBackground();
            Color seed = background == null ? component.getBackground() : background;
            seed = computeBackgroundSeed(seed);
            return adapter.isSelected() ? computeSelectedBackground(seed) : seed;
        }
        return null;    // don't change the background
    }

    public Color getForeground(Component component, ComponentAdapter adapter) {
        if (adapter.isHierarchical()) {
            if (getForeground() != null) {
                return super.computeForeground(component, adapter);
            }
        }
        return null;    // don't change the foreground
    }

    protected Color computeBackgroundSeed(Color seed) {
        return new Color(Math.max((int)(seed.getRed()  * 0.95), 0),
                         Math.max((int)(seed.getGreen()* 0.95), 0),
                         Math.max((int)(seed.getBlue() * 0.95), 0));
    }
}
