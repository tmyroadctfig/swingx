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
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;


import org.jdesktop.swingx.JXHeader;

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
        action.putValue(action.NAME, "My Action");
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
     * @param args
     */
    public static void main(String[] args) {
        JXButtonVisualCheck test = new JXButtonVisualCheck();
        try {
            test.runInteractiveTests();
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
