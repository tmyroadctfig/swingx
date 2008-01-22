package org.jdesktop.swingx;


import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class JXLabel2VisualTest implements Runnable{

    public static void main(String[] args) {
    SwingUtilities.invokeLater(new JXLabel2VisualTest());
    }

    /**
     * #swingx-680 Preferred size is not set when label is rotated.
     */
    public void run() {
    JFrame testFrame = new JFrame("JXLabel Test");
    testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JXLabel label = new JXLabel("This is some JXLabel text");
    label.setTextRotation(Math.PI/4);

    testFrame.setContentPane(label);
    testFrame.pack();
    testFrame.setLocationByPlatform(true);
    testFrame.setVisible(true);
    }

    }