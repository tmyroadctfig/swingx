/*
 * Created on 31.01.2006
 *
 */
package org.jdesktop.swingx.action;

import javax.swing.JToggleButton;

public class ActionIssues extends ActionTest {
    
    
    /**
     * Issue #255-swingx: probs in synch selectable button <--> action. 
     * 
     * test that configured button is kept in synch with
     *  action selected state and the other way round.
     * The direction from button to action is broken.
     */
    public void testButtonSelectedSynchAction() {
        AbstractActionExt extAction = createStateAction();
        boolean actionSelected = true;
        extAction.setSelected(actionSelected);
        JToggleButton button = new JToggleButton();
        factory.configureSelectableButton(button, extAction, null);
        // invert action selected and assert that the change is taken up
        // by the button
        extAction.setSelected(!actionSelected);
        assertEquals("button selected must be synched to action", 
                !actionSelected, button.isSelected());
        // reset button 
        button.setSelected(actionSelected);
        // sanity: the button did take the direct selection change
        assertEquals(actionSelected, button.isSelected());
        // assert that changed selected is taken up by action
        assertEquals("action selected must be synched to button", 
                actionSelected, extAction.isSelected());
    }

}
