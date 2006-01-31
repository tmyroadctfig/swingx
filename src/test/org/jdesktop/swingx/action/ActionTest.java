/*
 * Created on 27.01.2006
 *
 */
package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;

import junit.framework.TestCase;

public class ActionTest extends TestCase {
    private static final Logger LOG = Logger.getLogger(ActionTest.class
            .getName());

    

    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     * sub-issue: ActionContainerFactory doesn't check if the action is
     * already synchronizing to the same button. 
     */
    public void testToggleButtonConfigure() {
        // this should pass after giving the gc "reasonable" chance to
        // have collected the unreachable...
//        assertToggleButtonConfigure(new JToggleButton(), new JToggleButton());

    }

    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     * sub-issue: ActionContainerFactory doesn't check if the action is
     * already synchronizing to the same button. 
     */
    public void testToggleButtonConfigureToggleWithSame() {
        assertToggleButtonConfigureWithSame(new JToggleButton());
        assertToggleButtonConfigureWithSame(new JRadioButton());
        assertToggleButtonConfigureWithSame(new JCheckBox());
        assertToggleButtonConfigureWithSame(new JRadioButtonMenuItem());
        assertToggleButtonConfigureWithSame(new JCheckBoxMenuItem());

    }
    
    private void assertToggleButtonConfigureWithSame(AbstractButton checkBoxItem) {
        assertToggleButtonConfigure(checkBoxItem, checkBoxItem);
    }
    
    private void assertToggleButtonConfigure(AbstractButton first, AbstractButton second) {
        AbstractActionExt extAction = createStateAction();
        assertEquals(0, extAction.getPropertyChangeListeners().length);
        ActionContainerFactory factory = new ActionContainerFactory(null);
        factory.configureSelectableButton(first, extAction, null);
        // sanity: expect it to be 2 - one is the menuitem itself, another 
        // the TogglePCL registered by the ActionContainerFacory
        
        assertEquals(2, extAction.getPropertyChangeListeners().length);
        factory.configureSelectableButton(second, extAction, null);
        assertEquals(2, extAction.getPropertyChangeListeners().length);
        
    }

    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     * sub-issue: ActionContainerFactory registers the action multiple times to
     * the same button as ItemListener
     */
    public void testToggleButtonAddItemListenerToSame() {
        assertAddItemListenerToSame(new JToggleButton());
        assertAddItemListenerToSame(new JRadioButton());
        assertAddItemListenerToSame(new JCheckBox());
        assertAddItemListenerToSame(new JRadioButtonMenuItem());
        assertAddItemListenerToSame(new JCheckBoxMenuItem());
    }
    
    private void assertAddItemListenerToSame(AbstractButton checkBoxItem) {
        AbstractActionExt extAction = createStateAction();
        ActionContainerFactory factory = new ActionContainerFactory(null);
        factory.configureSelectableButton(checkBoxItem, extAction, null);
        assertCountAsItemListener(1, extAction, checkBoxItem );
        factory.configureSelectableButton(checkBoxItem, extAction, null);
        assertCountAsItemListener(1, extAction, checkBoxItem );
      
    }

    protected AbstractActionExt createStateAction() {
        AbstractActionExt extAction = new AbstractActionExt("dummy") {

            public void itemStateChanged(ItemEvent e) {
            }

            public void actionPerformed(ActionEvent e) {
            }
            
        };
        extAction.setStateAction();
        return extAction;
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
        AbstractActionExt action = createStateAction();
        action.setLongDescription("some");
    }
}
