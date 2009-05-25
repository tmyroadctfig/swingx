/*
 * Created on 19.08.2005
 *
 */
package org.jdesktop.swingx.util;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.test.EDTRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EDTRunner.class)
public class WindowUtilsTest extends InteractiveTestCase {

    public static void main(String[] args) {
        WindowUtilsTest test = new WindowUtilsTest();
        try {
            test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*HighLighters");
        } catch (Exception ex) {

        }
    }

    public void interactiveMinimumWindowSize() {
        JPanel config = new JPanel();
        ((FlowLayout)config.getLayout()).setAlignment(FlowLayout.LEFT);
        final JTextField minW = new JTextField("800");
        minW.setColumns(4);
        minW.setHorizontalAlignment(JTextField.RIGHT);
        final JTextField minH = new JTextField("600");
        minH.setColumns(4);
        minH.setHorizontalAlignment(JTextField.RIGHT);
        config.add(new JLabel("Min. Width"));
        config.add(minW);
        config.add(new JLabel("Min. Height"));
        config.add(minH);

        final JXFrame minSizeFrame = wrapInFrame(config, "Minimum Size");
        Action apply = new AbstractAction("Apply") {
            public void actionPerformed(ActionEvent e) {
               int newW = new Integer(minW.getText()).intValue();
               int newH = new Integer(minH.getText()).intValue();
               WindowUtils.setMinimumSizeManager(minSizeFrame, newW, newH);
//               minSizeFrame.setSize(newW, newH);
            }
         };
         addAction(minSizeFrame, apply);
         minSizeFrame.setSize(400, 200);
         minSizeFrame.setVisible(true);

    }
    
    public void interactiveCenteringTest() {
        JFrame frame = new JFrame("I should be centered");
        frame.add(new JTable(5, 5));
        frame.pack();
        frame.setLocation(WindowUtils.getPointForCentering(frame));
        frame.setVisible(true);
    }
    
    /**
     * TODO formally test...
     *
     */
    @Test
    public void testDummy() {
        
    }
}
