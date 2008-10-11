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
package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXFrame;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

/**
 * @author rbair
 */
@RunWith(JUnit4.class)
public class RichInteractiveTestCase extends TestCase {
    private boolean retVal = false;
    private boolean block = true;
    protected boolean showTest(final JComponent c, final String title, final String instructions) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    JXHeader h = new JXHeader();
                    h.setTitle(title);
                    h.setDescription(instructions);
                    final JXFrame frame = new JXFrame(title);
                    frame.setDefaultCloseOperation(JXFrame.DISPOSE_ON_CLOSE);
                    frame.add(c, BorderLayout.CENTER);
                    frame.add(h, BorderLayout.NORTH);
                    JButton failButton = new JButton("Fail");
                    failButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            frame.setVisible(false);
                            retVal = false;
                            block = false;
                        }
                    });
                    JButton passButton = new JButton("Pass");
                    passButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            frame.setVisible(false);
                            retVal = true;
                            block = false;
                        }
                    });

                    JPanel buttonPanel = new JPanel();
                    buttonPanel.add(passButton);
                    buttonPanel.add(failButton);
                    frame.add(buttonPanel, BorderLayout.SOUTH);
                    frame.addWindowListener(new WindowAdapter() {
                        public void windowClosed(WindowEvent windowEvent) {
                            block = false;
                        }
                    });

                    frame.setSize(400, 300);
                    frame.setStartPosition(JXFrame.StartPosition.CenterInScreen);
                    frame.setVisible(true);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            block = false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            block = false;
        }

        while (block) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {}
        }

        return retVal;
    }
    
    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    @Test
    public void testDummy() {
        
    }

}
