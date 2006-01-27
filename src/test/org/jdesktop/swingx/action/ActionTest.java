/*
 * Created on 27.01.2006
 *
 */
package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import junit.framework.TestCase;

public class ActionTest extends TestCase {

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
