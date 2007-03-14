/*
 * JXBoxPanel.java
 *
 * Created on March 5, 2007, 9:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.border.Border;
import org.jdesktop.swingx.painter.MattePainter;

/**
 *
 * @author joshy
 */
public class JXBoxPanel extends JXPanel {

    private Insets margins = new JXInsets();
    private Insets padding = new JXInsets();
    
    /** Creates a new instance of JXBoxPanel */
    public JXBoxPanel() {
        this.setMargins(new JXInsets(10));
        this.setBorder(BorderFactory.createLineBorder(Color.RED,10));
        this.setPadding(new JXInsets(10));
        this.setBackgroundPainter(new MattePainter(Color.GREEN));
    }
    
    public Insets getInsets() {
        return new JXInsets(margins,super.getInsets(),padding);
    }

    protected void paintBorder(Graphics g) {
        Border border = getBorder();
        if (border != null) {
            border.paintBorder(this, g, margins.left, margins.top, 
                    getWidth()-margins.left-margins.right, 
                    getHeight()-margins.top-margins.bottom);
        }
    }

    /** Overridden to provide painter support with a CSS box model
     */
    protected void paintComponent(Graphics g) {
        if(getBackgroundPainter() != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            // account for the insets
            Insets ins = this.getInsets();
            
            //josh: the background painter only fills the padding and
            //border area, or just padding? must look at the css specs
            ins = new Insets(ins.top-padding.top, ins.left - padding.left,
                    ins.bottom - padding.bottom, ins.right - padding.right);
            g2.translate(ins.left, ins.top);
            getBackgroundPainter().paint(g2, this,
                    this.getWidth()  - ins.left - ins.right,
                    this.getHeight() - ins.top  - ins.bottom);
            g2.dispose();
        } else {
            super.paintComponent(g);
        }
    }

    public void setMargins(Insets margins) {
        this.margins = margins;
    }

    private void setPadding(Insets padding) {
        this.padding = padding;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JXBoxPanel panel = new JXBoxPanel();
        panel.setPadding(new JXInsets(0));
        JButton button = new JButton("My Button");
        panel.setLayout(new BorderLayout());
        panel.add(button,"Center");
        frame.add(panel);
        frame.pack();
        frame.setSize(200,200);
        frame.setVisible(true);
    }
}
