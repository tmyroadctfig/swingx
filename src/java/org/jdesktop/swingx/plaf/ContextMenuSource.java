/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.UIManager;


/**
 * @author Jeanette Winzenburg
 */
public abstract class ContextMenuSource {

    private Map<String, String> names;

    public abstract String[] getKeys();

    public String getName(String actionKey) {
        return getNames().get(actionKey);
    }

    public abstract void updateActionEnabled(JComponent component, ActionMap map);

    /**
     * returns an ActionMap for usage in default context menues.
     * @param component
     * @return
     */
    public ActionMap createActionMap(JComponent component) {
        ActionMap map = new ActionMap();
        String[] keys = getKeys();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) {
                Action action = createDelegateAction(component, keys[i]);
                if (action != null) {
                    map.put(keys[i], action);
                }
            }
        }
        return map;
    }

    protected Map<String, String> getNames() {
        if (names == null) {
            names = new HashMap<String, String>();
            initNames(names);
        }
        return names;
    }

    protected String getValue(String key, String defaultValue) {
        String value = UIManager.getString(getResourcePrefix() + key);
        return value != null ? value : defaultValue;
    }

    protected abstract void initNames(Map<String, String> names);
    
    protected abstract String getResourcePrefix();

    
//    private void addAction(ActionMap map, JComponent component,
//            String actionKey) {
//        Action action = createDelegateAction(component, actionKey);
//        map.put(actionKey, action);
//    }

    protected Action createDelegateAction(JComponent component,
            String actionKey) {
        Action action = component.getActionMap().get(actionKey);
        if (action != null) {
         return new DelegateAction(getName(actionKey),
                action, component);
        }
        return null;
    }

    public static class DelegateAction extends AbstractAction {

        private Action delegatee;
        private JComponent target;

        public DelegateAction(String name, Action delegatee, JComponent target) {
            super(name);
            this.delegatee = delegatee;
            this.target = target;
        }

        public void actionPerformed(ActionEvent e) {
            delegatee.actionPerformed(createActionEvent(e));
        }

        private ActionEvent createActionEvent(ActionEvent e) {
            if (target != null) {
                return new ActionEvent(target, e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
            }
            return e;
        }
    }

}
