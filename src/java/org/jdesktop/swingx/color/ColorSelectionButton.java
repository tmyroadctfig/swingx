/*
 * ColorSelectionButton.java
 *
 * Created on March 13, 2006, 5:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
