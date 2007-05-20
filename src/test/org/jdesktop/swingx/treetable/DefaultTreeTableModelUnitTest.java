package org.jdesktop.swingx.treetable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import junit.framework.TestCase;

/**
 *
 */
public class DefaultTreeTableModelUnitTest extends TestCase {
    private DefaultTreeTableModel model;
    private DefaultMutableTreeTableNode root;
    private DefaultMutableTreeTableNode child1;
    private DefaultMutableTreeTableNode child2;
    private DefaultMutableTreeTableNode grandchild1;
    private DefaultMutableTreeTableNode grandchild2;
    private DefaultMutableTreeTableNode grandchild3;
    private DefaultMutableTreeTableNode grandchild4;
    private DefaultMutableTreeTableNode grandchild5;
    private DefaultMutableTreeTableNode grandchild6;
    
    private TreeTableNode createTree() {
        root = new DefaultMutableTreeTableNode("root");
        
        child1 = new DefaultMutableTreeTableNode("child1");
        grandchild1 = new DefaultMutableTreeTableNode("grandchild1");
        child1.add(grandchild1);
        grandchild2 = new DefaultMutableTreeTableNode("grandchild2");
        child1.add(grandchild2);
        grandchild3 = new DefaultMutableTreeTableNode("grandchild3");
        child1.add(grandchild3);
        root.add(child1);
        
        child2 = new DefaultMutableTreeTableNode("child2");
        grandchild4 = new DefaultMutableTreeTableNode("grandchild4");
        child2.add(grandchild4);
        grandchild5 = new DefaultMutableTreeTableNode("grandchild5");
        child2.add(grandchild5);
        grandchild6 = new DefaultMutableTreeTableNode("grandchild6");
        child2.add(grandchild6);
        root.add(child2);
        
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        model = new DefaultTreeTableModel(createTree());
    }
    
    public void testModelGetPathToRoot() {
        TreeNode[] testGroup1 = model.getPathToRoot(grandchild3);
        
        assertEquals(testGroup1[0], root);
        assertEquals(testGroup1[1], child1);
        assertEquals(testGroup1[2], grandchild3);
        
        TreeNode[] testGroup2 = model.getPathToRoot(child2);
        
        assertEquals(testGroup2[0], root);
        assertEquals(testGroup2[1], child2);
        
        TreeNode[] testGroup3 = model.getPathToRoot(root);
        
        assertEquals(testGroup3[0], root);
        
        TreeNode[] testGroup4 = model.getPathToRoot(null);
        
        assertEquals(testGroup4.length, 0);
        
    }
    
    public void testModelGetValueAt() {
        //Test expected cases
        assertEquals(model.getValueAt(root, 0), root + "@column " + 0);
        
        //Test boundary cases
        //TODO should we boundary check?  currently we don't
        assertEquals(model.getValueAt(child1, model.getColumnCount()), child1 + "@column " + model.getColumnCount());
        assertEquals(model.getValueAt(grandchild4, -1), grandchild4 + "@column " + -1);
    }
    
    public void testModelIsLeaf() {
        //asksAllowsChildren == false
        //Test exceptional cases
        try {
            model.isLeaf(null);
            
            fail("NullPointerException is not thrown.");
        } catch (NullPointerException e) {
            //test succeeded
        }
        
        //Test expected cases
        assertFalse(model.isLeaf(root));
        assertFalse(model.isLeaf(child2));
        assertTrue(model.isLeaf(grandchild3));
        
        //Test boundary cases
        assertTrue(model.isLeaf(new Object()));
    }
    
    //TODO test "fire" methods and reloads
}
