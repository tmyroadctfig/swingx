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
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXCollapsiblePane.Direction;
import org.jdesktop.swingx.action.AbstractActionExt;

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
     * Issue #??-swingx: inconsistent change notification of "animatedState"
     * 
     * Depends on value of animated: fired only if true, nothing happens on false
     * 
     * 
     * Trying to resize a top-level window on collapsed state changes of a taskpane.
     * Need to listen to "animationState" which is fired when animation is complete.
     */
    public void interactiveDialogWithCollapsible() {
        JXTaskPane pane = new JXTaskPane();
        pane.setAnimated(false);
        pane.setTitle("Just a TaskPane with animated property false");
        final JXDialog dialog = new JXDialog(pane);
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("animationState".equals(evt.getPropertyName())) {
                    dialog.pack();
                }
            }
            
        };
        pane.addPropertyChangeListener(l);
        pane.add(new JLabel("to have some content"));
        dialog.setTitle("pack on expand/collapse");
        dialog.pack();
        dialog.setVisible(true);
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

		JButton button = new JButton("Toggle");
		button.addActionListener(collPane.getActionMap().get(
				JXCollapsiblePane.TOGGLE_ACTION));

		panel.add(button, BorderLayout.CENTER);
		
		showInFrame(panel, "Animation Sizing Test");
    }
    
    /**
     * Test case for bug 1087.  Hidden components should not have focus.
     */
    public void interactiveFocusTest() {
    	JPanel panel = new JPanel(new BorderLayout());
    	
    	final JXCollapsiblePane instance = new JXCollapsiblePane();
    	panel.add(instance, BorderLayout.SOUTH);
    	
        JButton paneButton = new JButton("I do nothing");
        paneButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
            	if (instance.isCollapsed()) {
            		fail("Why am i getting focus?!");
            	}
            }
        });
        instance.add(paneButton);
        
		JButton button = new JButton("Toggle");
		button.addActionListener(instance.getActionMap().get(
				JXCollapsiblePane.TOGGLE_ACTION));
		panel.add(button, BorderLayout.CENTER);
		
		showInFrame(panel, "Focus Test");
    }
    
    /**
     * Test case for bug 1181. scrollXXXToVisible does not work.
     */
    public void interactiveScrollToVisibleTest() {
    	JPanel panel = new JPanel(new BorderLayout());
    	
    	JXCollapsiblePane pane = new JXCollapsiblePane();
    	pane.setScrollableTracksViewportHeight(false);
		panel.add(new JScrollPane(pane), BorderLayout.SOUTH);
    	
    	final JXTree tree = new JXTree();
    	pane.add(tree);
    	
    	JXFindBar find = new JXFindBar(tree.getSearchable());
    	panel.add(find, BorderLayout.CENTER);
    	
    	showInFrame(panel, "Scroll Rect To Visible Test");
    	
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tree.expandAll();
			}
		});
    }

    /**
     * Issue #1181-swingx: scrollRectToVisible not effective if component in JXTaskPane.
     * Yet another one for the same issue - moved from TaskPaneIssues.
     * 
     * example adapted from tjwolf,
     * http://forums.java.net/jive/thread.jspa?threadID=66759&tstart=0
     */
    public void interactiveScrollRectToVisible() {
        final JXTree tree = new JXTree();
        tree.expandAll();
        JXTaskPane pane = new JXTaskPane();
        pane.add(tree);
        JComponent component = new JPanel(new BorderLayout());
        component.add(pane);
        JXFrame frame = wrapWithScrollingInFrame(component, "scroll to last row must work");
        Action action = new AbstractActionExt("scrollToLastRow") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.scrollRowToVisible(tree.getRowCount()- 1);
            }
        };
        addAction(frame, action);
        // small dimension, make sure there is something to scroll
        show(frame, 300, 200);
    }

    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }
}
