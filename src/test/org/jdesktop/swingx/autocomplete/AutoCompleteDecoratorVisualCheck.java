/**
 * 
 */
package org.jdesktop.swingx.autocomplete;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.jdesktop.swingx.InteractiveTestCase;

/**
 * @author Karl George Schaefer
 */
public class AutoCompleteDecoratorVisualCheck extends InteractiveTestCase {
    public static void main(String[] args) throws Exception {
        AutoCompleteDecoratorVisualCheck test = new AutoCompleteDecoratorVisualCheck();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }


    public void interactiveCompletionAtTopTest() {
        JComboBox combo = new JComboBox(new String[] {
                "A1", "A2", "A3", "A4", "A5",
                "B1", "B2", "B3", "B4", "B5",
                "C1", "C2", "C3", "C4", "C5",
                "D1", "D2", "D3", "D4", "D5",
        });
        
        AutoCompleteDecorator.decorate(combo);
        
        combo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                System.err.println(e);
                
                JComboBox box = (JComboBox) e.getSource();
                for (ItemListener l : box.getItemListeners()) {
                    System.err.println(l);
                }
            }
            
        });
        
        JFrame frame = wrapInFrame(combo, "show combo ");
        frame.setSize(200, 200);
        frame.setVisible(true);
    }
    
    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
