/*
 * Created on 31.01.2006
 *
 */
package org.jdesktop.swingx.action;

import javax.swing.JToggleButton;

public class ActionIssues extends ActionTest {


    /**
     * test that configured button is kept in synch with
     *  maximal one action's selected state
     */
    public void testButtonSelectedOneSynchAction() {
        AbstractActionExt extAction = createStateAction();
        JToggleButton button = new JToggleButton();
        ActionContainerFactory factory = new ActionContainerFactory(null);
        factory.configureSelectableButton(button, extAction, null);
        // we are sure that the button selected is true (has dedicated test)
        // now configure it with a different action, unselected
        AbstractActionExt extActionB = createStateAction();
        factory.configureSelectableButton(button, extActionB, null);
        // invert the old action selected and assert that the change 
        // does not effect the taken up by the button
        extAction.setSelected(!extAction.isSelected());
        assertEquals("button selected must be uneffected by old action",
                extActionB.isSelected(), button.isSelected());
    }

    /**
     * test that configured button is no longer kept in
     * synch after setting the action to null.
     */
    public void testButtonSelectedReleasedSynchAction() {
        AbstractActionExt extAction = createStateAction();
        JToggleButton button = new JToggleButton();
        ActionContainerFactory factory = new ActionContainerFactory(null);
        factory.configureSelectableButton(button, extAction, null);
        // now we unconfigure it with a null action
        factory.configureSelectableButton(button, null, null);
        // invert the old action selected and assert that the change 
        // does not effect the taken up by the button
        boolean oldSelected = button.isSelected();
        extAction.setSelected(!extAction.isSelected());
        assertEquals("button selected must be uneffected by old action",
                oldSelected, button.isSelected());
    }

    /**
     * test that configured button is kept in synch with
     *  maximal one action's selected state
     */
    public void testButtonSelectedMaxOneSynchAction() {
        AbstractActionExt extAction = createStateAction();
        boolean actionSelected = true;
        extAction.setSelected(actionSelected);
        JToggleButton button = new JToggleButton();
        ActionContainerFactory factory = new ActionContainerFactory(null);
        factory.configureSelectableButton(button, extAction, null);
        // we are sure that the button selected is true (has dedicated test)
        // now configure it with a different action, unselected
        AbstractActionExt extActionB = createStateAction();
        factory.configureSelectableButton(button, extActionB, null);
        // sanity: the new action is not effected by the old
        // currently this may accidentally pass because the back direction isn't
        // synched!! 
        assertFalse(extActionB.isSelected());
        assertEquals("button selected must be initialized to new action",
                extActionB.isSelected(), button.isSelected());
        // invert the old action selected and assert that the change 
        // does not effect the taken up by the button
        extAction.setSelected(!actionSelected);
        // need to be done twice, the first toggle produces 
        extAction.setSelected(actionSelected);
        assertEquals("button selected must be uneffected by old action",
                extActionB.isSelected(), button.isSelected());
    }
    
    /**
     * test that configured button is kept in synch with
     *  action selected state and the other way round.
     * The direction from button to action is broken.
     */
    public void testButtonSelectedSynchAction() {
        AbstractActionExt extAction = createStateAction();
        boolean actionSelected = true;
        extAction.setSelected(actionSelected);
        JToggleButton button = new JToggleButton();
        ActionContainerFactory factory = new ActionContainerFactory(null);
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

    /**
     * test that button is configured with initial action selected state.
     *
     */
    public void testButtonSelectedInitialSynchAction() {
        AbstractActionExt extAction = createStateAction();
        boolean actionSelected = true;
        extAction.setSelected(actionSelected);
        JToggleButton button = new JToggleButton();
        boolean buttonSelected = button.isSelected();
        // sanity: different selected state
        assertTrue(actionSelected != buttonSelected);
        ActionContainerFactory factory = new ActionContainerFactory(null);
        factory.configureSelectableButton(button, extAction, null);
        assertEquals("action selection must be unchanged", actionSelected, extAction.isSelected());
        assertEquals("button selected must be initialized", actionSelected, button.isSelected());
    }
}
