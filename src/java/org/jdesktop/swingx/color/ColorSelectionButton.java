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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TODO may want to promote to a JXColorChooserButton
 *
 * @author joshy
 */
public class ColorSelectionButton extends JButton {
    
    JDialog dialog = null;
    private JColorChooser chooser = null;
    
    /** Creates a new instance of ColorSelectionButton */
    public ColorSelectionButton() {
        setBackground(Color.red);
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("new color selection button");
                if(dialog == null) {
                    dialog = JColorChooser.createDialog(
                            ColorSelectionButton.this,
                            "Choose a color",
                            true, 
                            getChooser(),
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
                    dialog.getContentPane().add(getChooser());
                    getChooser().getSelectionModel().addChangeListener(new ColorChangeListener(ColorSelectionButton.this));
                }
                dialog.setVisible(true);
                Color color = getChooser().getColor();
                
                if (color != null) {
                    setBackground(color);
                }
                
            }
        });
        this.setContentAreaFilled(false);
        this.setOpaque(false);
        
        try {
            colorwell = ImageIO.read(this.getClass().getResourceAsStream("colorwell.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    BufferedImage colorwell;
    
    private class ColorChangeListener implements ChangeListener {
        public ColorSelectionButton button;
        public ColorChangeListener(ColorSelectionButton button) {
            this.button = button;
        }
        public void stateChanged(ChangeEvent changeEvent) {
            System.out.println("color changed");
            button.setBackground(button.getChooser().getColor());
        }
    }

    public void paintComponent(Graphics g) {
        Insets ins = new Insets(5,5,5,5);        
        if(colorwell != null) {
            ColorUtil.tileStretchPaint(g, this, colorwell, ins);
        }
        
        // 0, 23, 255  = 235o, 100%, 100%
        // 31, 0, 204 =  249o, 100%,  80%
	g.setColor(ColorUtil.removeAlpha(getBackground()));
        g.fillRect(ins.left, ins.top, 
                    getWidth()  - ins.left - ins.right, 
                    getHeight() - ins.top - ins.bottom);
        g.setColor(ColorUtil.setBrightness(getBackground(),0.85f));
        g.drawRect(ins.left, ins.top,
                getWidth() - ins.left - ins.right - 1,
                getHeight() - ins.top - ins.bottom - 1);
        g.drawRect(ins.left + 1, ins.top + 1,
                getWidth() - ins.left - ins.right - 3,
                getHeight() - ins.top - ins.bottom - 3);
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Color Button Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.add(new ColorSelectionButton());
        panel.add(new JLabel("ColorSelectionButton test"));
        
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public JColorChooser getChooser() {
        if(chooser == null) {
            chooser = new JColorChooser();
        }
        return chooser;
    }
    
}
