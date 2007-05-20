/**
 * 
 */
package org.jdesktop.swingx.test;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

/**
 *
 */
public class TreeTableUtils {
    private TreeTableUtils() {
        //does nothing
    }
    
    public static DefaultTreeTableModel convertDefaultTreeModel(DefaultTreeModel model) {
        DefaultTreeTableModel ttModel = new DefaultTreeTableModel();
        
        ttModel.setRoot(convertDefaultMutableTreeNode((DefaultMutableTreeNode) model.getRoot()));
        
        return ttModel;
    }
    
    private static DefaultMutableTreeTableNode convertDefaultMutableTreeNode(DefaultMutableTreeNode node) {
        DefaultMutableTreeTableNode ttNode = new DefaultMutableTreeTableNode(node.getUserObject());
        
        Enumeration<DefaultMutableTreeNode> children = node.children();
        
        while (children.hasMoreElements()) {
            ttNode.add(convertDefaultMutableTreeNode(children.nextElement()));
        }
        
        return ttNode;
    }
}
