/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import javax.swing.JComponent;
import javax.swing.JScrollBar;

import org.jdesktop.swingx.util.Contract;

/**
 * Sizing hints for layout, useful f.i. in a Scrollable implementation.<p>
 * 
 * Inspired by <a href=
 * http://tips4java.wordpress.com/2009/12/20/scrollable-panel/> Rob Camick</a>.
 * 
 * PENDING JW: naming... suggestions?
 * 
 * @author Jeanette Winzenburg
 */
public enum ScrollableSizeHint {

    /**
     * Size should be unchanged.
     */
    NONE(false), 
    
    /**
     * Size should be ajusted to parent size. 
     */
    FIT(true), 
    
    /**
     * Width should be streched to parent width if smaller, unchanged otherwise.
     */
    HORIZONTAL_STRETCH(JScrollBar.HORIZONTAL) {
        /**
         * @param component
         * @return
         */
        @Override
        boolean isSmallerThanParent(JComponent component) {
            if (component.getParent() != null) {
                return component.getParent().getWidth() > 
                    component.getPreferredSize().width;
            }

            return false;
        }
         
    },
    
    /**
     * Width should be streched to parent height if smaller, unchanged otherwise.
     */
    VERTICAL_STRETCH(JScrollBar.VERTICAL) {
        /**
         * @param component
         * @return
         */
         @Override
        boolean isSmallerThanParent(JComponent component) {
            if (component.getParent() != null) {
                return component.getParent().getHeight() > 
                    component.getPreferredSize().height;
            }

            return false;
        }
        
    };
    
    final boolean tracks;
    final int orientation;
    
    ScrollableSizeHint(boolean track) {
        this(track, -1);
    }
    
    ScrollableSizeHint(int orientation) {
        this(false, orientation);
    }
    
    ScrollableSizeHint(boolean tracks, int orientation) {
        this.tracks = tracks;
        this.orientation = orientation;
        
    }
    
    /**
     * Returns a boolean indicating whether the component's size should be
     * adjusted to parent.
     *  
     * @param component the component resize, must not be null
     * @return a boolean indicating whether the component's size should be
     *    adjusted to parent
     *    
     * @throws NullPointerException if component is null   
     */
    public boolean getTracksParentSize(JComponent component) {
        Contract.asNotNull(component, "component must be not-null");
        if (orientation < 0) {
            return tracks;
        }
        return isSmallerThanParent(component);
    }

    /**
     * Returns a boolean indicating whether the hint can be used in 
     * horizontal orientation.
     * 
     * @return a boolean indicating whether the hint can be used in horizontal
     *   orientation. 
     */
    public boolean isHorizontalCompatible() {
        return (orientation < 0) ? true : JScrollBar.HORIZONTAL == orientation;
    }
    
    /**
     * Returns a boolean indicating whether the hint can be used in 
     * vertical orientation.
     * 
     * @return a boolean indicating whether the hint can be used in vertical
     *   orientation. 
     */
    public boolean isVerticalCompatible() {
        return (orientation < 0) ? true : JScrollBar.VERTICAL == orientation;
    }
    
    
    boolean isSmallerThanParent(JComponent component) {
        return tracks;
    }
    
}
