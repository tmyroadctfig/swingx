/*
 * Created on 31.01.2006
 *
 */
package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import org.jdesktop.test.PropertyChangeReport;

public class ActionIssues extends ActionTest {
    
    /**
     * core issue: 
     * set enabled via putValue leads to inconsistent state.
     *
     */
    public void testFireEnabled() {
        Action action = new AbstractAction("dummy") {

            public void actionPerformed(ActionEvent e) {
                // nothing to do
                
            }
            
        };
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("enabled".equals(evt.getPropertyName())) {
                    assertEquals(evt.getNewValue(), ((Action) evt.getSource()).isEnabled());
                }
                
            }
            
        };
        action.addPropertyChangeListener(l);
        action.putValue("enabled", false);
        
    }
    /**
     * core issue: 
     * set selected via putValue leads to inconsistent state.
     *
     */
    public void testFireSelected() {
        AbstractActionExt action = new AbstractActionExt("dummy") {

            public void actionPerformed(ActionEvent e) {
                // nothing to do
                
            }

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
    
    

}
