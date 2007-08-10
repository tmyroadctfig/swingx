package org.jdesktop.swingx;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;


import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Node;

public class JXMultiSplitPaneVisualIssues extends InteractiveTestCase {


    private static void prepare(JFrame f) {
        prepare(f, MultiSplitLayout.parseModel("(ROW (COLUMN weight=0.5 left.top left.bottom)" + 
                " (LEAF name=middle)" + 
                " (COLUMN weight=0.5 right.top right.bottom))"));
        
    }
    
    private static void prepare(JFrame f, Node model) {
        final JXMultiSplitPane msp;
        JXPanel p = new JXPanel(new BorderLayout());
        JXHeader header = new JXHeader();
        header.setTitle("<html><B>JXMultiSplitPaneIssue</b>");
        header.setDescription("<html>This is a bit tricky ... To reproduce the issue move one of the dividers and then click reset. Expected behavior is that original layout is restored. Actual behavior (bug) is that only two cells stay displayed and no divider is visible.");
        p.add(header, BorderLayout.NORTH);
        msp = new JXMultiSplitPane();
        msp.setModel(model);
        p.add(msp);
        JButton green = new JButton("mid");
        green.setBackground(Color.GREEN);
        msp.add(green, "middle");
        msp.add(new JButton("l.t"), "left.top");
        msp.add(new JButton("l.b"), "left.bottom");
        msp.add(new JButton("r.t"), "right.top");
        msp.add(new JButton("r.b"), "right.bottom");
        f.add(p);
        JButton b = new JButton(new AbstractAction("Reset") {

            public void actionPerformed(ActionEvent e) {
                // just reset the model
                msp.setModel(MultiSplitLayout.parseModel("(ROW (COLUMN weight=0.5 left.top left.bottom)" + 
                " (LEAF name=middle)" + 
                " (COLUMN weight=0.5 right.top right.bottom))"));
                msp.revalidate();
                msp.repaint();
            }

        });
        f.add(b, BorderLayout.SOUTH);
        f.setPreferredSize(new Dimension(400, 400));
    }
    
    public void interactiveModelChange() {
        JFrame f = new JFrame();
        prepare(f);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JXMultiSplitPaneVisualIssues test = new JXMultiSplitPaneVisualIssues();
        try {
            test.runInteractiveTests();
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 
    }

}
