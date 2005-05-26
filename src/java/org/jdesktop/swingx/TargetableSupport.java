/*
 * TargetableSupport.java
 *
 * Created on May 26, 2005, 9:33 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;

/**
 *
 * @author Richard
 */
public class TargetableSupport {
    private JComponent component;
    
    /** Creates a new instance of TargetableSupport */
    public TargetableSupport(JComponent component) {
        this.component = component;
    }
    
    public boolean doCommand(Object command, Object value) {
        // Look at the internal component first.
        ActionMap map = component.getActionMap();
        Action action = map.get(command);

        if (action != null) {
            if (value instanceof ActionEvent) {
                action.actionPerformed( (ActionEvent) value);
            }
            else {
                // XXX should the value represent the event source?
                action.actionPerformed(new ActionEvent(value, 0,
                    command.toString()));
            }
            return true;
        }
        return false;
    }

    public Object[] getCommands() {
        ActionMap map = component.getActionMap();
        return map.keys();
        // TODO: Should Return the keys the current JNComponent as well.
    }

    public boolean hasCommand(Object command) {
        Object[] commands = getCommands();
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].equals(command)) {
                return true;
            }
        }
        return false;
    }
}
