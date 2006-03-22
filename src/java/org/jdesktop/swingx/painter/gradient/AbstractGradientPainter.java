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

package org.jdesktop.swingx.painter.gradient;

import java.awt.Graphics2D;
import java.awt.Paint;
import javax.swing.JComponent;
import org.jdesktop.swingx.painter.AbstractPainter;

/**
 * <p>An abstract base class from which the various gradient oriented painter
 * classes extend. Gradient based painters perform an important task beyond what
 * a MattePainter with a GradientPaint will do -- they resize the gradient to
 * fit the Component regardless of its dimensions.</p>
 *
 * <p>AbstractGradientPainter has a resize property of type Resize that specifies
 * whether the gradient should be resized horizontally, vertically, in both directions
 * or not at all (in which case you should really probably be using MattePainter).
 * Subclasses must implement calculateSizedPaint to return a new gradient paint
 * instance that is resized in the proper ways. If no Paint is returned from
 * this method call, then nothing is painted.</p>
 *
 * <p>By default, the resize property is set to BOTH.</p>
 *
 * <p>In order for resizing to work properly with GradientPaint, LinearGradientPaint,
 * and RadialGradientPaint, it is necessary that the various control points used in
 * these paints be specified in such a manner that they can be reliably resized.
 * For example, BasicGradientPainter takes GradientPaints who's point1 and point2
 * properties are specified between 0 and 1, representing at what percentage of
 * the distance from the origin the gradient begins and ends. Thus, if I created
 * a GradientPaint like this:
 * <pre><code>
 *  GradientPaint gp = new GradientPaint(
 *      new Point2D.Double(.2d, 0),
 *      Color.BLUE,
 *      new Point2D.Double(.8d, 0),
 *      Color.WHITE);
 * </code></pre>
 * 
 * then when painted, the gradient will start with a BLUE at 20% of the width of
 * the component, and finish with WHITE at 80% of the width of the component.</p>
 *
 * <p>See the various subclasses for more detailed examples.</p>
 *
 * @author rbair
 */
public abstract class AbstractGradientPainter extends AbstractPainter {
    /**
     * Indicates in which direction (vertical, horizontal, both, none) to 
     * resize the gradient paint prior to painting
     */
    private Resize resize = Resize.BOTH;
    
    /**
     * Creates a new instance of AbstractGradientPainter
     */
    public AbstractGradientPainter() {
    }
    
    /**
     * @inheritDoc
     */
    public void paint(Graphics2D g, JComponent component) {
        saveState(g);
        
        Paint p = calculateSizedPaint(component.getWidth(), component.getHeight());
        if (p != null) {
            g.setPaint(p);
            g.fillRect(0, 0, component.getWidth(), component.getHeight());
        }
        
        restoreState(g);
    }

    /**
     * @return a calculated Paint that fits within the given width/height. May
     *         be null.
     * 
     * @param width the width used to calculate the new paint size
     * @param height the height used to calculate the new paint size
     */
    protected abstract Paint calculateSizedPaint(int width, int height);

    /**
     * @return a value indicating how the paint will be resized to fit the
     *         component dimensions. Never returns null.
     */
    public Resize getResize() {
        return resize;
    }
    
    /**
     * Specifies how the paint will be resized based on the component dimensions.
     *
     * @param resize the new Resize value. May be null. If null, set to Resize.BOTH.
     */
    public void setResize(Resize resize) {
        Resize old = getResize();
        boolean rh = isResizeHorizontal();
        boolean rv = isResizeVertical();
        
        this.resize = resize == null ? Resize.BOTH : resize;
        
        firePropertyChange("resize", old, getResize());
        firePropertyChange("resizeHorizontal", rh, isResizeHorizontal());
        firePropertyChange("resizeVertical", rv, isResizeVertical());
    }
    
    /**
     * @return true if getResize() returns BOTH or HORIZONTAL.
     */
    protected boolean isResizeHorizontal() {
        Resize r = getResize();
        return r == Resize.BOTH || r == Resize.HORIZONTAL;
    }

    /**
     * @return true if getResize() returns BOTH or VERTICAL.
     */
    protected boolean isResizeVertical() {
        Resize r = getResize();
        return r == Resize.BOTH || r == Resize.VERTICAL;
    }
}
