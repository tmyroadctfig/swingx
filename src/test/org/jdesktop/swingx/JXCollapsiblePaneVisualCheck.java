/*
 * $Id$
 *
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
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
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXCollapsiblePane.Direction;

/**
 * @author Karl George Schaefer
 *
 */
public class JXCollapsiblePaneVisualCheck extends InteractiveTestCase {
    /**
     * @param args
     */
    public static void main(String[] args) {
        JXCollapsiblePaneVisualCheck test = new JXCollapsiblePaneVisualCheck();
        try {
            test.runInteractiveTests();
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 
    }

    /**
     * SwingX 578: Ensure that the directions work correctly.
     */
    public void interactiveDirectionTest() {
    	JXCollapsiblePane north = new JXCollapsiblePane(Direction.UP);
    	JLabel label = new JLabel("<html>north1<br>north2<br>north3<br>north4<br>north5<br>north6</html>");
    	label.setHorizontalAlignment(SwingConstants.CENTER);
    	north.add(label);
    	JXCollapsiblePane south = new JXCollapsiblePane(Direction.DOWN);
    	label = new JLabel("<html>south1<br>south2<br>south3<br>south4<br>south5<br>south6</html>");
    	label.setHorizontalAlignment(SwingConstants.CENTER);
    	south.add(label);
    	JXCollapsiblePane west = new JXCollapsiblePane(Direction.LEFT);
    	west.add(new JLabel("west1west2west3west4west5west6"));
    	JXCollapsiblePane east = new JXCollapsiblePane(Direction.RIGHT);
    	east.add(new JLabel("east1east2east3east4east5east6"));
    	
    	JPanel panel = new JPanel(new GridLayout(2, 2));
    	JButton button = new JButton(north.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
    	button.setText("UP");
    	panel.add(button);
    	button = new JButton(south.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
    	button.setText("DOWN");
    	panel.add(button);
    	button = new JButton(west.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
    	button.setText("LEFT");
    	panel.add(button);
    	button = new JButton(east.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
    	button.setText("RIGHT");
    	panel.add(button);
    	
    	JFrame frame = wrapInFrame(panel, "Direction Animation Test");
    	frame.add(north, BorderLayout.NORTH);
    	frame.add(south, BorderLayout.SOUTH);
    	frame.add(west, BorderLayout.WEST);
    	frame.add(east, BorderLayout.EAST);
    	frame.pack();
    	frame.setVisible(true);
    }
    
    
    /**
     * Test case for bug 1076.
     */
    public void interactiveAnimationSizingTest() {
    	JPanel panel = new JPanel(new BorderLayout());
    	
    	final JXCollapsiblePane collPane = new JXCollapsiblePane();
    	collPane.setCollapsed(true);
    	collPane.add(new JLabel("hello!"));
    	collPane.setAnimated(false); // critical statement

    	panel.add(collPane, BorderLayout.NORTH);

		JButton button = new JButton("coll/exp");
		button.addActionListener(collPane.getActionMap().get(
				JXCollapsiblePane.TOGGLE_ACTION));

		panel.add(button, BorderLayout.CENTER);
		
		showInFrame(panel, "Animation Sizing Test");
    }
    
    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }
}
