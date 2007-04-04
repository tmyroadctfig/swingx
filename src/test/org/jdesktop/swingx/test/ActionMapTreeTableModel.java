/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

/**
 * Convenience TreeTableModel for wrapping an ActionMap hierarchy.
 * 
 * @author Jeanette Winzenburg, Berlin
 */
public class ActionMapTreeTableModel extends DefaultTreeTableModel {

    public ActionMapTreeTableModel(JComponent comp) {
        super(createRootNodeExt(comp), false);
    }

    public Object getChild(Object parent, int index) {
        return ((ActionEntryNode) parent).getChildren().get(index);
    }

    public int getChildCount(Object parent) {
        return ((ActionEntryNode) parent).getChildren().size();
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Key Name";
        case 1:
            return "Action Name";
        case 2:
            return "Action Command";
        default:
            return "Column " + column;
        }
    }

    public Object getValueAt(Object node, int column) {
        ActionEntryNode actionNode = (ActionEntryNode) node;

        switch (column) {
        case 0:
            return actionNode.key;
        case 1:
            if (actionNode.isLeaf())
                return actionNode.getAction().getValue(Action.NAME);
            return null;
        case 2:
            if (actionNode.isLeaf())
                return actionNode.getAction().getValue(
                        Action.ACTION_COMMAND_KEY);
        // case 3:
        // return "Modification Date";
        default:
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private static ActionEntryNode createRootNodeExt(JComponent comp) {
        ActionMap map = comp.getActionMap();
        if (map == null)
            throw new IllegalArgumentException("Component must have ActionMap");
        List actionMaps = new ArrayList();
        actionMaps.add(map);
        while ((map = map.getParent()) != null) {
            actionMaps.add(0, map);
        }
        return createActionEntryNodes(actionMaps);
    }

    private static ActionEntryNode createActionEntryNodes(List actionMaps) {
        ActionMap topLevel = (ActionMap) actionMaps.get(0);
        ActionEntryNode mapRoot = new ActionEntryNode("topLevel", topLevel);
        ActionEntryNode current = mapRoot;
        for (int i = 1; i < actionMaps.size(); i++) {
            current = current.addActionMapAsChild("childMap " + i,
                    (ActionMap) actionMaps.get(i));
        }
        return mapRoot;
    }

    public static class ActionEntryNode extends DefaultMutableTreeNode {
        Object key;

        Action action;

        ActionMap actionMap;

        List<ActionEntryNode> children;

        @SuppressWarnings("unchecked")
        public ActionEntryNode(Object key, Action action) {
            super(action);
            this.key = key;
            this.action = action;
            children = Collections.EMPTY_LIST;
        }

        public ActionEntryNode(Object key, ActionMap map) {
            super(key);
            this.key = key;
            this.actionMap = map;
            children = new ArrayList<ActionEntryNode>();
            Object[] keys = map.keys();
            for (int i = 0; i < keys.length; i++) {
                children.add(new ActionEntryNode(keys[i], (Action) map
                        .get(keys[i])));
            }
        }

        /**
         * pre: !isLeaf
         * 
         * @param key
         * @param map
         */
        public ActionEntryNode addActionMapAsChild(Object key, ActionMap map) {
            ActionEntryNode actionEntryNode = new ActionEntryNode(key, map);
            getChildren().add(0, actionEntryNode);
            return actionEntryNode;
        }

        public List<ActionEntryNode> getChildren() {
            return children;
        }

        public boolean isLeaf() {
            return action != null;
        }

        public ActionMap getActionMap() {
            return actionMap;
        }

        public Action getAction() {
            return action;
        }

        public boolean getAllowsChildren() {
            return !isLeaf();
        }

        public String toString() {
            return key.toString();
        }
    }

}
