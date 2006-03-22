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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * <p>An ImagePainter subclass that provides convenience methods "setIcon" and
 * "getIcon" to use Icons (and ImageIcons) rather than Images directly. Because
 * this class uses "Icon" and because there is a "getImage" method from the
 * parent class, sometimes an intermediate BufferedImage will be created to
 * generate the image to return. This should be fairly performant, but will
 * of course have some overhead. To avoid this overhead, use an ImageIcon
 * where possible.</p>
 *
 * @author rbair
 */
public class IconPainter extends ImagePainter {
    /**
     * The icon to draw
     */
    private Icon icon;
    
    /**
     * Create a new IconPainter
     */
    public IconPainter() {
        super();
    }
    
    /**
     * Set the icon to use. This will fire property change notification not
     * only for the "icon" property, but also for the "image" property.
     *
     * @param icon the Icon to use
     */
    public void setIcon(Icon icon) {
        Icon old = getIcon();
        Image oldImage = getImage();
        this.icon = icon;
        firePropertyChange("icon", old, getIcon());
        firePropertyChange("image", oldImage, getImage());
    }
    
    /**
     * @return the Icon used by this painter
     */
    public Icon getIcon() {
        return icon;
    }
    
    /**
     * @return the image used for painting the background of this panel
     */
    public Image getImage() {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon)icon).getImage();
        } else if (icon == null) {
            return null;
        } else {
            //paint the icon into a buffered image
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            icon.paintIcon(new JComponent(){}, g2, 0, 0);
            g2.dispose();
            return image;
        }
    }
}
