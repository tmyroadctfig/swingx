package org.jdesktop.swingx.treetable;

import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


import junit.framework.TestCase;

/**
 *
 */
@RunWith(JUnit4.class)
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
    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
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
        
        Vector<String> names = new Vector<String>();
        names.add("A");
        
        model = new DefaultTreeTableModel(createTree(), names);
    }
    
    @Test
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
        
        try {
            model.getPathToRoot(null);
            fail("expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
    }
    
    @Test
    public void testModelGetValueAt() {
        //Test expected cases
        assertEquals(model.getValueAt(root, 0), "root");
        
        //Test boundary cases
        try {
            model.getValueAt(child1, model.getColumnCount());
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        try {
            model.getValueAt(grandchild4, -1);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
    }
    
    @Test
    public void testModelIsLeaf() {
        //asksAllowsChildren == false
        //Test exceptional cases
        try {
            model.isLeaf(null);
            
            fail("IllegalArgumentException is not thrown.");
        } catch (IllegalArgumentException e) {
            //test succeeded
        }
        
        try {
            model.isLeaf(new Object());
            
            fail("IllegalArgumentException is not thrown.");
        } catch (IllegalArgumentException e) {
            //test succeeded
        }
        
        //Test expected cases
        assertFalse(model.isLeaf(root));
        assertFalse(model.isLeaf(child2));
        assertTrue(model.isLeaf(grandchild3));
    }
    
    @Test
    public void testRemoveFromParent() {
    	try {
    		model.removeNodeFromParent(new DefaultMutableTreeTableNode());
    		fail("Expected IllegalArgumentException");
    	} catch (IllegalArgumentException e) {
    		//success
    	}
    	
    	try {
    		DefaultMutableTreeTableNode p = new DefaultMutableTreeTableNode();
    		DefaultMutableTreeTableNode c = new DefaultMutableTreeTableNode();
    		c.setParent(p);
    		
    		model.removeNodeFromParent(c);
    		
    		fail("Expected NullPointerException");
    	} catch (NullPointerException e) {
    		//success
    		//TODO does not seem like the correct exception
    	}
    	
    	TreeNode parent = grandchild6.getParent();
    	int count = parent.getChildCount();
    	model.removeNodeFromParent(grandchild6);
    	
    	assertNull(grandchild6.getParent());
    	assertEquals(parent.getChildCount(), count - 1);
    	
    	model.removeNodeFromParent(child1);
    	assertNull(child1.getParent());
    	
    	try {
    		model.removeNodeFromParent(root);
    		fail("Expected IllegalArgumentException");
    	} catch (IllegalArgumentException e) {
    		//success
    	}
    	
    	//TODO test removing already removed nodes?
    }
    
    @Test
    public void testSetRoot() {
    	assertEquals(model.getRoot(), root);
    	
    	DefaultMutableTreeTableNode newRoot = new DefaultMutableTreeTableNode("a new root");
    	model.setRoot(newRoot);
    	
    	assertEquals(model.getRoot(), newRoot);
    }
    
    //TODO test "fire" methods and reloads
}
