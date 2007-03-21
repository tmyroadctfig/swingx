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

import java.awt.Font;
import javax.swing.*;

/**
 *
 * @author rbair
 */
public class JXHeaderDemo extends JPanel {
    public JXHeaderDemo() {
        setLayout(new VerticalLayout(3));
        add(new JXHeader());
        
        JXHeader header = new JXHeader("A Custom JXHeader",
                "This JXHeader sets a custom title, an icon, and a custom description.\n" +
                "Multiple lines can be specified by using a \\n control character.\n" +
                "You may even specify more than two lines using this technique.",
                new ImageIcon(JXHeaderDemo.class.getResource("resources/header-image.png")));
        add(header);
        
        header = new JXHeader("This JXHeader uses a different font",
                "By calling setFont(), I can replace the font used by this\n" +
                "JXHeader. Note, however, that it affects both the title and" +
                "the description.", null);
        header.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        add(header);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setSize(400, 400);
                frame.add(new JXHeaderDemo());
                frame.setVisible(true);
            }
        });
    }
    
}
