package org.jdesktop.swingx;


import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class JXLabel2VisualTest extends InteractiveTestCase {

    public static void main(String[] args) throws Exception {
        // setSystemLF(true);
        JXLabel2VisualTest test = new JXLabel2VisualTest();
        
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * #swingx-680 Preferred size is not set when label is rotated.
     */
    public static void interactiveJXLabel() {

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