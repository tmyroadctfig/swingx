/*
 * Created on 31.01.2006
 *
 */
package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.SerializableSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ActionIssues extends ActionTest implements Serializable {
    
    /**
     * Issue #1364-swingx: AbstractActionExt - incorrect parameter type in setActionCommand
     */
    @Test
    public void testActionCommand() {
        AbstractActionExt action = new AbstractActionExt("something"){

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }};
        action.setActionCommand(new Object());
        new JButton(action);
    }

    /**
     * Issue #349-swingx: table not serializable
     * 
     *
     */
    @Test
    public void testSerializationBoundAction() {
        BoundAction action = new BoundAction("some");
        action.registerCallback(this, "testSerializationRolloverFalse");
        BoundAction serialized = SerializableSupport.serialize(action);
    }

    /**
     * core issue: 
     * set selected via putValue leads to inconsistent state.
     *
     */
    @Test
    public void testFireSelected() {
        AbstractActionExt action = new AbstractActionExt("dummy") {

            public void actionPerformed(ActionEvent e) {
                // nothing to do
                
            }

            @Override
            public void itemStateChanged(ItemEvent e) {
                // nothing to do
                
            }
            
        };
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("selected".equals(evt.getPropertyName())) {
                    assertEquals(evt.getNewValue(), ((AbstractActionExt) evt.getSource()).isSelected());
                }
                
            }
            
        };
        action.addPropertyChangeListener(l);
        action.putValue("selected", true);
        
    }
    //--------------------- core: selected is not a bean property
    
    /**
     * unexpected core behaviour: selected is not a bound property!
     * PENDING: is it in Mustang?
     */
    @Test
    public void testToggleButtonPropertyChangeSelected() {
        JToggleButton button = new JToggleButton();
        PropertyChangeReport report = new PropertyChangeReport();
        button.addPropertyChangeListener(report);
        boolean selected = button.isSelected();
        button.setSelected(!selected);
        // sanity...
        assertEquals(selected, !button.isSelected());
        assertEquals("must have one event for selected", 1, report.getEventCount("selected"));
    }
    
    @Test
    public void testCheckBoxPropertyChangeSelected() {
        JCheckBox button = new JCheckBox();
        PropertyChangeReport report = new PropertyChangeReport();
        button.addPropertyChangeListener(report);
        boolean selected = button.isSelected();
        button.setSelected(!selected);
        // sanity...
        assertEquals(selected, !button.isSelected());
        assertEquals("must have one event for selected", 1, report.getEventCount("selected"));
    }
    
    @Test
    public void testRadioButtonPropertyChangeSelected() {
        JRadioButton button = new JRadioButton();
        PropertyChangeReport report = new PropertyChangeReport();
        button.addPropertyChangeListener(report);
        boolean selected = button.isSelected();
        button.setSelected(!selected);
        // sanity...
        assertEquals(selected, !button.isSelected());
        assertEquals("must have one event for selected", 1, report.getEventCount("selected"));
    }
    
    @Test
    public void testCheckBoxMenuItemPropertyChangeSelected() {
        JMenuItem button = new JCheckBoxMenuItem();
        PropertyChangeReport report = new PropertyChangeReport();
        button.addPropertyChangeListener(report);
        boolean selected = button.isSelected();
        button.setSelected(!selected);
        // sanity...
        assertEquals(selected, !button.isSelected());
        assertEquals("must have one event for selected", 1, report.getEventCount("selected"));
    }
    
    /**
     * Template to try and test memory leaks (from Palantir blog).
     * TODO apply for listener problems
     */
    @Test
    public void testMemory() {
        //create test object
        Object testObject = new Object();
        // create queue and weak reference
        ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
        WeakReference<Object> ref = new WeakReference<Object>(testObject, queue);
        // set hard reference to null
        testObject = null;
//        force garbage collection
        System.gc();
        // soft reference should now be enqueued (no leak)
        assertTrue(ref.isEnqueued());
    }
    

}
