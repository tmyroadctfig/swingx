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
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class CollapsibleAtWork {
  
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    JFrame frame = new JFrame("CollapsibleAtWork");
    frame.setSize(200, 400);

    StackedBox box = new StackedBox();    
    JScrollPane scrollPane = new JScrollPane(box);
    scrollPane.setBorder(null);
    frame.add(scrollPane, BorderLayout.CENTER);
        
    Border contentBorder = BorderFactory.createEmptyBorder(6, 8, 6, 8);

    // the control pane
    JToolBar controls = new JToolBar();
    controls.setFloatable(false);
    controls.setBorder(contentBorder);
    controls.setRollover(true);
    controls.setOpaque(false);
    
    JButton button = new JButton(UIManager.getIcon("InternalFrame.icon"));
    button.setOpaque(false);
    controls.add(button);
    button = new JButton(UIManager.getIcon("FileChooser.newFolderIcon"));
    button.setOpaque(false);
    controls.add(button);
    button = new JButton(UIManager.getIcon("FileChooser.upFolderIcon"));
    button.setOpaque(false);
    controls.add(button);
    box.addBox("Controls", controls);
    
    // the status pane
    JPanel status = new JPanel(new GridLayout(3, 2));
    status.setOpaque(false);
    status.setBorder(contentBorder);
    status.add(makeBold(new JLabel("Type:")));
    status.add(new JLabel("CPU"));
    status.add(makeBold(new JLabel("Configuration:")));
    status.add(new JLabel("Preset"));
    status.add(makeBold(new JLabel("Status:")));
    status.add(new JLabel("Running"));
    box.addBox("Status", status);
    
    // the profiling results
    JPanel profilingResults = new JPanel(new BorderLayout(3, 3));
    profilingResults.add("Center", new JScrollPane(new JTree()));
    profilingResults.setPreferredSize(new Dimension(200, 100));
    profilingResults.setBorder(contentBorder);
    box.addBox("Profiling Results", profilingResults);

    // the saved snapshots pane
    JPanel savedSnapshots = new JPanel(new BorderLayout(3, 3));
    savedSnapshots.setBorder(contentBorder);
    savedSnapshots.setOpaque(false);
    
    JComboBox combo = new JComboBox(new Object[]{" Java Demo"});
    savedSnapshots.add("North", combo);
    JList list = new JList(new Object[]{"<html><b>&nbsp;03:53:54 PM</b>"});
    list.setVisibleRowCount(5);
    savedSnapshots.add("Center", new JScrollPane(list));    
    box.addBox("Saved Snapshots", savedSnapshots);
        
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  static JLabel makeBold(JLabel label) {
    label.setFont(label.getFont().deriveFont(Font.BOLD));
    return label;    
  }
  
  
}
