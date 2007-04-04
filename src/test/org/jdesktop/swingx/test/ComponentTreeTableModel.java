/*
 * Created on 20.06.2005
 *
 */
package org.jdesktop.swingx.test;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

/**
 * A static snapshot of a container hierarchy.
 * 
 * NOTE: does not listen to any property changes of
 * contained components - cell updates are arbitrary on
 * repaint only!
 * 
 * @author Jeanette Winzenburg
 */
public class ComponentTreeTableModel extends AbstractTreeTableModel {


    public ComponentTreeTableModel(Container root) {
        super(root);
        setRoot(root);
    }

    public void setRoot(Container root) {
        if (root == null) {
            root = new JXFrame();
        }
        this.root = root;
        fireTreeStructureChanged(this, new Object[]{ root }, null, null);
    }
    
    //  ------------------TreeModel
    
    
    public Object getChild(Object parent, int index) {
        return ((Container) parent).getComponent(index);
    }
    
    public int getChildCount(Object parent) {
        return parent instanceof Container ? ((Container) parent).getComponentCount() : 0;
    }
    
    public int getIndexOfChild(Object parent, Object child) {
        Component[] children = ((Container) parent).getComponents();
        for (int i = 0; i < children.length; i++) {
            if (children[i].equals(child)) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    /**
     * This method is called by a standard JTree after
     * editing the cell.
     * 
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        Object node = path.getLastPathComponent();
        setValueAt(newValue, node, 0);
        
    }


    /**
     * This method is called by the "tree" part to render the 
     * hierarchical column.
     * 
     * @param node
     * @return
     */
    public String convertValueToText(Object node) {
        return String.valueOf(getValueAt(node, 0));
    }
    
//------------------ TreeTableModel    

    public Class getColumnClass(int column) {
        switch (column) {
        case 0:
            return hierarchicalColumnClass;
        case 1:
            return Class.class;
        case 2:
            return Dimension.class;
        default:
            return Object.class;
        }
    }
    
    public int getColumnCount() {
        return 3;
    }
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Name";
        case 1:
            return "Type";
        case 2:
            return "Size";
        default:
            return "Column " + column;
        }
    }
    
    public Object getValueAt(Object node, int column) {
        Component comp = (Component) node;
        switch (column) {
        case 0:
            return comp.getName();
        case 1:
            return comp.getClass();
        case 2:
            return comp.getSize();
        default:
            return null;
        }
    }

    
    @Override
    public boolean isCellEditable(Object node, int column) {
        // fake accessing the node: TreeModel can expect to 
        // be messaged with nodes that belong to the 
        // hierarchical structure 
        // introduced for testing #270-swingx: 
        // NPE for invisible rows (if parent is collapsed.
        ((Component) node).getName();
        return column == 0;
    }

    public void setValueAt(Object value, Object node, int column) {
        if (!isCellEditable(node, column)) return;
        Component comp = (Component) node;
        comp.setName(String.valueOf(value));
        nodeChanged(comp);
    }


    private void nodeChanged(Component comp) {
        TreePath path = getPathToRoot(comp);
        Object node = path.getLastPathComponent();
        TreePath parentPath = path.getParentPath();

        if (parentPath == null) {
            //todo....
           // fireChildrenChanged(path, null, null);
        } else {
            Object parent = parentPath.getLastPathComponent();

            fireTreeNodesChanged(this, new Object[] {parentPath} , 
                    new int[] { getIndexOfChild(parent, node) },
                    new Object[]  { node} );
        }
        
    }

    /**
     * exposed for test convenience.
     * 
     * @param node
     * @return
     */
    public TreePath getPathToRoot(Component node) {
        return new TreePath(getPathToRoot(node, 0));
    }

    private Component[] getPathToRoot(Component node, int depth) {
        Component[]              retNodes;
        // This method recurses, traversing towards the root in order
        // size the array. On the way back, it fills in the nodes,
        // starting from the root and working back to the original node.

        /* Check for null, in case someone passed in a null node, or
           they passed in an element that isn't rooted at root. */
        if(node == null) {
            if(depth == 0)
                return null;
            else
                retNodes = new Component[depth];
        }
        else {
            depth++;
            if(node == root)
                retNodes = new Component[depth];
            else
                retNodes = getPathToRoot(node.getParent(), depth);
            retNodes[retNodes.length - depth] = node;
        }
        return retNodes;
    }

    
}
