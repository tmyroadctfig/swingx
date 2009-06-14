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
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * @author joshy
 */
public class JXGradientChooserDemo {
    private JXGradientChooserDemo() {}

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        final JFrame frame = new JFrame("Gradient Picker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));

        JButton button = new JButton("Select Gradient");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color[] colors = { Color.blue, Color.black};
                float[] vals = {0.0f,1.0f};
                LinearGradientPaint paint = new LinearGradientPaint(0f,0f,10f,0f,vals,colors);
                MultipleGradientPaint grad = JXGradientChooser.showDialog(frame,"Pick a Gradient",paint);
                System.out.println("got: " + JXGradientChooser.toString(grad));
                //new LinearGradientPaint(pt1,pt2, new float[]{0f,1f}, new Color[]{ new Color(0,0,0,0), new Color(255,255,255,255)});
                StringBuffer sb = new StringBuffer();
                sb.append("new LinearGradientPaint(");
                sb.append("new Point(0,0),");
                sb.append("new Point(1,0),");
                sb.append("new float[] {");

                for(int i=0; i<grad.getFractions().length; i++) {
                    float f = grad.getFractions()[i];
                    sb.append(f+"f");
                    if(i < grad.getFractions().length-1) {
                        sb.append(",");
                    }
                }
                sb.append("},");
                sb.append("new Color[] {");
                for(int i=0; i<grad.getColors().length; i++) {
                    Color c= grad.getColors()[i];
                    sb.append("new Color(");
                    sb.append(c.getRed()+","+c.getGreen()+","+c.getBlue()+","+c.getAlpha());
                    sb.append(")");
                    if(i < grad.getColors().length-1) {
                        sb.append(",");
                    }
                }
                sb.append("}");
                sb.append(");");
                p(sb.toString());
            }
            public void p(String s) {
                System.out.println(s);
            }
        });
        frame.add(button);
        frame.pack();
        frame.setVisible(true);
    }

}
