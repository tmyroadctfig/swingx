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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;

import org.jdesktop.swingx.action.AbstractActionExt;

/**
 *
 * Quick demo of the JXTaskPane
 *
 * @author rbair
 */
public class JXTaskPaneDemo extends JPanel {

    public JXTaskPaneDemo() {
        setLayout(new BorderLayout());
        
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        JXTaskPane taskPane = new JXTaskPane();
        taskPane.setTitle("TODO Tasks");
        taskPane.add(new TODOAction("Prepare slides for JavaPolis"));
        taskPane.add(new TODOAction("Buy Christmas presents"));
        taskPane.add(new TODOAction("Meet with Brian about SwingLabs"));
        container.add(taskPane);
        
        taskPane = new JXTaskPane();
        taskPane.setTitle("Key Dates");
        taskPane.add(new TODOAction("December 25"));
        taskPane.add(new TODOAction("January 1"));
        taskPane.add(new TODOAction("Febuary 14"));
        taskPane.add(new TODOAction("March 26"));
        container.add(taskPane);
        
        taskPane = new JXTaskPane();
        taskPane.setTitle("Notes");
        taskPane.add(new JScrollPane(new JTextArea(15, 20)));
        container.add(taskPane);
        
        add(new JScrollPane(container));
    }
    
    private static final class TODOAction extends AbstractActionExt {
        public TODOAction(String name) {
            setName(name);
        }
        public void actionPerformed(ActionEvent actionEvent) {}
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setSize(400, 400);
                frame.add(new JXTaskPaneDemo());
                frame.setVisible(true);
            }
        });
    }
    
}
