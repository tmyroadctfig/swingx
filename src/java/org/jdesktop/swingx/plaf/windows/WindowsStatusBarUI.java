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

package org.jdesktop.swingx.plaf.windows;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXStatusBar.Constraint;
import org.jdesktop.swingx.plaf.basic.BasicStatusBarUI;

/**
 *
 * @author rbair
 */
public class WindowsStatusBarUI extends BasicStatusBarUI {
    private BufferedImage leftImage;
    private BufferedImage middleImage;
    private BufferedImage rightImage;
    
    /** Creates a new instance of BasicStatusBarUI */
    public WindowsStatusBarUI() {
        try {
            leftImage = ImageIO.read(getClass().getResource("resources/statusbar-left.png"));
            middleImage = ImageIO.read(getClass().getResource("resources/statusbar-middle.png"));
            rightImage = ImageIO.read(getClass().getResource("resources/statusbar-right.png"));
        } catch (Exception e) {
            //hmmmm... should log this I guess
        }
    }
    
    /**
     * Returns an instance of the UI delegate for the specified component.
     * Each subclass must provide its own static <code>createUI</code>
     * method that returns an instance of that UI delegate subclass.
     * If the UI delegate subclass is stateless, it may return an instance
     * that is shared by multiple components.  If the UI delegate is
     * stateful, then it should return a new instance per component.
     * The default implementation of this method throws an error, as it
     * should never be invoked.
     */
    public static ComponentUI createUI(JComponent c) {
        return new WindowsStatusBarUI();
    }	

    protected void paintBackground(Graphics2D g, JXStatusBar statusBar) {
        //if bidi, reverse the image painting order
        //TODO need to handle vertical stretching better
        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(leftImage, 0, 0, leftImage.getWidth(), statusBar.getHeight(), null);
        g2.drawImage(middleImage, leftImage.getWidth(), 0, statusBar.getWidth() - leftImage.getWidth() - rightImage.getWidth(), statusBar.getHeight(), null);
        g2.drawImage(rightImage, statusBar.getWidth() - rightImage.getWidth(), 0, rightImage.getWidth(), statusBar.getHeight(), null);
    }
}
