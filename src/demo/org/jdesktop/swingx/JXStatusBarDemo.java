/*
 * JXHeaderDemo.java
 *
 * Created on November 20, 2006, 3:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 *
 * @author richardallenbair
 */
public class JXStatusBarDemo {
    private JPanel panel;
    
    public JXStatusBarDemo() {
        panel = new JPanel();
        panel.setLayout(new VerticalLayout(3));
        
        //create the status bar
        JXStatusBar bar = new JXStatusBar();
        
        //create and add the message label
        JLabel messageLabel = new JLabel("Ready");
        bar.add(messageLabel, JXStatusBar.Constraint.ResizeBehavior.FILL);
        
        //create and add the mouse position indicator
        final javax.swing.JLabel mousePositionLabel = new javax.swing.JLabel("230, 320");
        mousePositionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mousePositionLabel.setPreferredSize(new Dimension(80, mousePositionLabel.getPreferredSize().height));
        bar.add(mousePositionLabel);
        
        //create and add the caps lock indicator
        final JLabel capslockLabel = new JLabel("Authenticated");
        capslockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bar.add(capslockLabel);
        
        //create and add the shift indicator
        final JLabel shiftLabel = new JLabel("Power User");
        shiftLabel.setPreferredSize(capslockLabel.getPreferredSize());
        shiftLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bar.add(shiftLabel);
        
        //create and add the progress bar
        JProgressBar progress = new JProgressBar();
        bar.add(progress);
        progress.setIndeterminate(true);
        
        panel.add(bar);
        
        //Add a second bar
        bar = new JXStatusBar();
        JComboBox combo1 = new JComboBox(new String[]{"AA", "BB", "CC"});
        
        bar.add(new JLabel("Fill portion"), JXStatusBar.Constraint.ResizeBehavior.FILL);
        bar.add(combo1);
        bar.add(new JLabel("jabcdefghijklm"));
        bar.add(new JLabel("gnopqrstuvwxyz"));
        
        panel.add(bar);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(400, 400);
        frame.add(new JXStatusBarDemo().panel);
        frame.setVisible(true);
    }
    
}
