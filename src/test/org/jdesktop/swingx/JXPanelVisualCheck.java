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
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.painter.ImagePainter;

/**
 * Contains methods to visually test JXPanel.
 * 
 * @author Jeanette Winzenburg
 */
public class JXPanelVisualCheck extends InteractiveTestCase {

    public static void main(String args[]) {
//      setSystemLF(true);
      JXPanelVisualCheck test = new JXPanelVisualCheck();
      try {
         test.runInteractiveTests();
//         test.runInteractiveTests(".*List.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    /**
     * Issue #1187-swingx: default scrollable tracks property prevents scrolling.
     * Problem are the implementations of scrollableTracks: they unconditionally
     * return true, so forcing the viewport/scrollpane to the size of the contained
     * component.
     */
    public void interactiveScrolling() {
        JXPanel panel = new JXPanel(new BorderLayout());
        panel.add(new JXTable(100, 6));
        JXFrame frame = wrapInFrame(panel, "scrollbar must be showing");
        show(frame, 400, 400);
    }

    public void interactiveIconPainter() throws Exception {
        ImagePainter imagePainter = new ImagePainter(ImageIO.read(JXPanel.class.getResource("resources/images/kleopatra.jpg")));
        JXPanel panel = new JXPanel();
        panel.setBackgroundPainter(imagePainter);
//        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(200, 200));
        showWithScrollingInFrame(panel, "icon painter in jxpanel");
    }

    /**
     * do-nothing method - suppress warning if there are no other
     * test fixtures to run.
     *
     */
    public void testDummy() {
        
    }

}
