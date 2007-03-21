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
 */

package org.jdesktop.swingx;

import java.awt.Dimension;
import javax.swing.*;

/**
 *
 * @author rbair
 */
public class JXStatusBarDemo extends JPanel {
    public JXStatusBarDemo() {
        setLayout(new VerticalLayout(3));
        
        //create the status bar
        JXStatusBar bar = new JXStatusBar();
        
        //create and add the message label
        JLabel messageLabel = new JLabel("Ready");
        bar.add(messageLabel, JXStatusBar.Constraint.ResizeBehavior.FILL);
        
        //create and add the mouse position indicator
        final javax.swing.JLabel mousePositionLabel = new javax.swing.JLabel("230, 320");
        mousePositionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mousePositionLabel.setPreferredSize(new Dimension(80, mousePositionLabel.getPreferredSize().height));
        bar.add(mousePositionLabel);
        
        //create and add the caps lock indicator
        final JLabel capslockLabel = new JLabel("Authenticated");
        capslockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bar.add(capslockLabel);
        
        //create and add the shift indicator
        final JLabel shiftLabel = new JLabel("Power User");
        shiftLabel.setPreferredSize(capslockLabel.getPreferredSize());
        shiftLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bar.add(shiftLabel);
        
        //create and add the progress bar
        JProgressBar progress = new JProgressBar();
        bar.add(progress);
        progress.setIndeterminate(true);
        
        add(bar);
        
        //Add a second bar
        bar = new JXStatusBar();
        JComboBox combo1 = new JComboBox(new String[]{"AA", "BB", "CC"});
        
        bar.add(new JLabel("Fill portion"), JXStatusBar.Constraint.ResizeBehavior.FILL);
        bar.add(combo1);
        bar.add(new JLabel("jabcdefghijklm"));
        bar.add(new JLabel("gnopqrstuvwxyz"));
        
        add(bar);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setSize(400, 400);
                frame.add(new JXStatusBarDemo());
                frame.setVisible(true);
            }
        });
    }
    
}
