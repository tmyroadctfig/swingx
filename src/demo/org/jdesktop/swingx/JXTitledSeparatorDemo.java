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

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 *
 * @author rbair
 */
public class JXTitledSeparatorDemo extends JPanel {
    public JXTitledSeparatorDemo() {
        setLayout(new VerticalLayout(3));
        add(new JXTitledSeparator());
        add(new JXTitledSeparator()).setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JXTitledSeparator s = new JXTitledSeparator();
        s.setTitle("Custom Title");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setTitle("Custom Title");
        add(s);
        
        s = new JXTitledSeparator();
        s.setFont(new Font("Times New Roman", Font.ITALIC, 16));
        s.setTitle("Custom Font");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setFont(new Font("Times New Roman", Font.ITALIC, 16));
        s.setTitle("Custom Font");
        add(s);
        
        s = new JXTitledSeparator();
        s.setForeground(Color.BLUE.darker());
        s.setTitle("Custom Foreground");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setForeground(Color.BLUE.darker());
        s.setTitle("Custom Foreground");
        add(s);
        
        s = new JXTitledSeparator();
        s.setHorizontalAlignment(SwingConstants.CENTER);
        s.setTitle("Center Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setHorizontalAlignment(SwingConstants.CENTER);
        s.setTitle("Center Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setHorizontalAlignment(SwingConstants.TRAILING);
        s.setTitle("Trailing Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setHorizontalAlignment(SwingConstants.TRAILING);
        s.setTitle("Trailing Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setHorizontalAlignment(SwingConstants.LEADING);
        s.setTitle("Leading Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setHorizontalAlignment(SwingConstants.LEADING);
        s.setTitle("Leading Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setHorizontalAlignment(SwingConstants.LEFT);
        s.setTitle("Left Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setHorizontalAlignment(SwingConstants.LEFT);
        s.setTitle("Left Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setHorizontalAlignment(SwingConstants.RIGHT);
        s.setTitle("Right Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setHorizontalAlignment(SwingConstants.RIGHT);
        s.setTitle("Right Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setIcon(new ImageIcon(JXTitledSeparatorDemo.class.getResource("resources/green-orb.png")));
        s.setTitle("Custom Icon");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setIcon(new ImageIcon(JXTitledSeparatorDemo.class.getResource("resources/green-orb.png")));
        s.setTitle("Custom Icon");
        add(s);
        
        s = new JXTitledSeparator();
        s.setIcon(new ImageIcon(JXTitledSeparatorDemo.class.getResource("resources/green-orb.png")));
        s.setHorizontalTextPosition(SwingConstants.LEFT);
        s.setTitle("Custom Icon, LEFT Horizontal Text Position");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setIcon(new ImageIcon(JXTitledSeparatorDemo.class.getResource("resources/green-orb.png")));
        s.setHorizontalTextPosition(SwingConstants.LEFT);
        s.setTitle("Custom Icon, LEFT Horizontal Text Position");
        add(s);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setSize(400, 400);
                frame.add(new JXTitledSeparatorDemo());
                frame.setVisible(true);
            }
        });
    }
    
}
