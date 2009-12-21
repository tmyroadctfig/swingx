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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.action.AbstractActionExt;
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
        final JXPanel panel = new JXPanel(new BorderLayout());
        panel.add(createScrollableContent(20));
        JXFrame frame = wrapWithScrollingInFrame(panel, "scrollbar must be showing");
        Action toggleHeightTrack = new AbstractActionExt("track height: " + panel.getScrollableHeightHint()) {
            
            ScrollableSizeHint[] tracks = new ScrollableSizeHint[] {
                    ScrollableSizeHint.FIT
                    , ScrollableSizeHint.NONE
                    , ScrollableSizeHint.VERTICAL_STRETCH
            };
            int position;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                position++;
                if (position >= tracks.length) position = 0;
                panel.setScrollableHeightHint(tracks[position]);
                setName("track height: " + tracks[position]);
            }
        }; 
        addAction(frame, toggleHeightTrack);
        show(frame, 400, 400);
    }
    /**
     * @return
     */
    private JComponent createScrollableContent(int rows) {
        JPanel component = new JPanel();
        component.setPreferredSize(new Dimension(400, 400));
        component.setMinimumSize(new Dimension(200, 200));
        component.setMaximumSize(new Dimension(600, 600));
        component.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
        
//        JButton component = new JButton("gotcha!");
//        JXTable table = new JXTable(rows, 6);
//        for (int i = 0; i < table.getRowCount(); i++) {
//            table.setValueAt("row: " + i, i, 0);
//        }
        return component;
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
