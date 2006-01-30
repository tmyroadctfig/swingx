/*
 * Created on 27.01.2006
 *
 */
package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;

import junit.framework.TestCase;

public class ActionTest extends TestCase {

    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     * sub-issue: ActionContainerFactory doesn't check if the action is
     * already synchronizing to the same button. 
     */
    public void testConfigureToggleWithSame() {
        AbstractActionExt extAction = new AbstractActionExt("dummy") {

            public void itemStateChanged(ItemEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        extAction.setStateAction();
        assertEquals(0, extAction.getPropertyChangeListeners().length);
        ActionContainerFactory factory = new ActionContainerFactory(null);
        JToggleButton checkBoxItem = new JToggleButton();
        factory.configureButton(checkBoxItem, extAction, null);
        // sanity: expect it to be 2 - one is the menuitem itself, another 
        // the TogglePCL registered by the ActionContainerFacory
        
        assertEquals(2, extAction.getPropertyChangeListeners().length);
        factory.configureButton(checkBoxItem, extAction, null);
        assertEquals(2, extAction.getPropertyChangeListeners().length);
        
    }

    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     * sub-issue: ActionContainerFactory registers the action multiple times to
     * the same button as ItemListener
     */
    public void testAddItemListenerToSame() {
        JToggleButton checkBoxItem = new JToggleButton();
        assertAddItemListenerToSame(checkBoxItem);
    }
    
    public void assertAddItemListenerToSame(JToggleButton checkBoxItem) {
        AbstractActionExt extAction = new AbstractActionExt("dummy") {

            public void itemStateChanged(ItemEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        extAction.setStateAction();
        ActionContainerFactory factory = new ActionContainerFactory(null);
        factory.configureButton(checkBoxItem, extAction, null);
        assertCountAsItemListener(1, extAction, checkBoxItem );
        factory.configureButton(checkBoxItem, extAction, null);
        assertCountAsItemListener(1, extAction, checkBoxItem );
      
    }

    private void assertCountAsItemListener(int expectedCount, ItemListener extAction, AbstractButton checkBoxItem) {
        int count = 0;
        ItemListener[] itemListeners = checkBoxItem.getItemListeners();
        for (int j = 0; j < itemListeners.length; j++) {
            if (extAction == itemListeners[j]) {
                count++;
            }
        }
        assertEquals("ItemListener registration count", expectedCount, count);
        
    }

    /**
     * Issue #4-swinglabs: infinite loop when setting long destricption.
     *
     */
    public void testLongDescriptionLoop() {
        AbstractActionExt action = new AbstractActionExt("looping") {

            public void itemStateChanged(ItemEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        
        action.setLongDescription("some");
    }
}
