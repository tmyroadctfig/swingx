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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdesktop.swingx.image.FastBlurFilter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * Visual tests of JXButton issues.
 * @author rah003
 *
 */
public class JXButtonVisualCheck extends InteractiveTestCase {

    /**
     * Test for issue #761.
     */
    public void interactiveButton() {
        final JFrame f = new JFrame();
    	JPanel control = new JPanel();
        JButton b = new JButton("Start");
        control.add(b);
        f.add(control, BorderLayout.SOUTH);
        f.setPreferredSize(new Dimension(400, 400));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    /**
     * Test for issue 849
     */
    public void interactiveActionButton() {
        AbstractAction action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //do nothing
            }
        };
        action.putValue(Action.NAME, "My Action");
        action.setEnabled(true);
        final JFrame f = new JFrame();
        f.setSize(300, 200);
        JPanel jContentPane = new JPanel();
        jContentPane.setLayout(new BorderLayout());
        jContentPane.add(new JButton(action), BorderLayout.WEST); // Generated
        jContentPane.add(new JXButton(action), BorderLayout.EAST);
        f.setContentPane(jContentPane);
        f.setTitle("JFrame");
        f.setVisible(true);
    }
    
    /**
     * SwingX Issue 1158
     */
    public void interactiveStatusBarCheck() {
        final JXButton button = new JXButton("Sample");
        MattePainter p = new MattePainter(PaintUtils.BLUE_EXPERIENCE, true);
        button.setForegroundPainter(p);
        BufferedImage im;
        try {
            im = ImageIO.read(JXButton.class.getResource("plaf/basic/resources/error16.png"));
        } catch (IOException ignore) {
            System.out.println(ignore);
            im = null;
        }
        button.setIcon(new ImageIcon(im));
        
        JXFrame frame = wrapInFrame(button, "Painter testing");
        frame.setStatusBar(new JXStatusBar());
        show(frame);
    }
    
    public void interactiveForegroundCheck() {
        final JXButton button = new JXButton("Sample");
//        MattePainter p = new MattePainter(PaintUtils.AERITH, true);
        final MattePainter p = new MattePainter(PaintUtils.BLUE_EXPERIENCE, true);
        p.setFilters(new FastBlurFilter());
        button.setForegroundPainter(p);
        button.addActionListener(new ActionListener(){
            private String[] values = new String[] {"Hello", "Goodbye", "SwingLabs", "Turkey Bowl"};
            private int index = 1;
            public void actionPerformed(ActionEvent ae) {
                button.setText(values[index]);
                index++;
                if (index >= values.length) {
                    index = 0;
                }
            }
        });
        BufferedImage im;
        try {
            im = ImageIO.read(JXButton.class.getResource("plaf/basic/resources/error16.png"));
        } catch (IOException ignore) {
            System.out.println(ignore);
            im = null;
        }
        button.setIcon(new ImageIcon(im));
        button.addMouseListener(new MouseAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                p.setFilters((BufferedImageOp[]) null);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseExited(MouseEvent e) {
                p.setFilters(new FastBlurFilter());
            }
            
        });
        
        showInFrame(button, "Painter testing");
    }

    public void interactiveBackgroundCheck() {
        final JXButton button = new JXButton("Sample");
        MattePainter p = new MattePainter(PaintUtils.AERITH, true);
        button.setBackgroundPainter(p);
        button.addActionListener(new ActionListener(){
            private String[] values = new String[] {"Hello", "Goodbye", "SwingLabs", "Turkey Bowl"};
            private int index = 1;
            public void actionPerformed(ActionEvent ae) {
                button.setText(values[index]);
                index++;
                if (index >= values.length) {
                    index = 0;
                }
            }
        });
        BufferedImage im;
        try {
            im = ImageIO.read(JXButton.class.getResource("plaf/basic/resources/error16.png"));
        } catch (IOException ignore) {
            System.out.println(ignore);
            im = null;
        }
        button.setIcon(new ImageIcon(im));
        
        showInFrame(button, "Painter testing");
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        JXButtonVisualCheck test = new JXButtonVisualCheck();
        try {
            test.runInteractiveTests("interactiveStatusBarCheck");
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          }
    }

    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
