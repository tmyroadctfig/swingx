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

package org.jdesktop.swingx.color;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;

/**
 * TODO may want to promote to a JXColorChooserButton
 *
 * @author joshy
 */
public class ColorSelectionButton extends JButton {
    
    JDialog dialog = null;
    JColorChooser chooser = null;
    
    /** Creates a new instance of ColorSelectionButton */
    public ColorSelectionButton() {
        setBackground(Color.red);
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if(dialog == null) {
                    chooser = new JColorChooser();
                    dialog = JColorChooser.createDialog(
                            ColorSelectionButton.this,
                            "Choose a color",
                            true,
                            chooser,
                            new ActionListener() {
                                public void actionPerformed(ActionEvent actionEvent) {
                                    //System.out.println("okay");
                                }
                            },
                            new ActionListener() {
                                public void actionPerformed(ActionEvent actionEvent) {
                                    //System.out.println("cancel");
                                }
                            }
                            );
                    dialog.getContentPane().add(chooser);
                }
                dialog.setVisible(true);
                Color color = chooser.getColor();
                
                if (color != null) {
                    setBackground(color);
                }
                
                
            }
        });
    }

    public void paintComponent(Graphics g) {
	g.setColor(ColorUtil.removeAlpha(getBackground()));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        g.setColor(Color.white);
        g.drawRect(1, 1, getWidth()-3, getHeight()-3);
        g.drawString(getText(),10,20);
    }
}
