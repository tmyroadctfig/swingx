/*
 * Created on 19.08.2005
 *
 */
package org.jdesktop.swingx.util;

import javax.swing.JFrame;
import javax.swing.JTable;

import org.jdesktop.swingx.InteractiveTestCase;
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
