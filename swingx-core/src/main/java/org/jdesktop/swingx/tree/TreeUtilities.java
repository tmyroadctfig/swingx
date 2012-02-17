package org.jdesktop.swingx.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Vector;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * Contains convenience classes/methods for handling hierarchical Swing structures.
 * 
 * @author Jeanette Winzenburg, Berlin
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TreeUtilities {

    /**
     * An enumeration that is always empty. 
     */
    public static final Enumeration EMPTY_ENUMERATION
        = new Enumeration() {
            @Override
            public boolean hasMoreElements() { return false; }
            @Override
            public Object nextElement() {
                throw new NoSuchElementException("No more elements");
            }
    };

    /**
     * Implementation of a preorder traversal of a subtree in a TreeModel.
     */
    public static class PreorderModelEnumeration implements Enumeration {
        protected Deque<Enumeration> stack;
        protected TreeModel model;
        
        
        public PreorderModelEnumeration(TreeModel model) {
            this(model, model.getRoot());
        }
        
        public PreorderModelEnumeration(TreeModel model, Object node) {
            this.model = model;
            Vector v = new Vector(1);
            v.add(node);
            stack = new ArrayDeque<Enumeration>();
            stack.push(v.elements()); //children(model));
        }

        @Override
        public boolean hasMoreElements() {
            return (!stack.isEmpty() && stack.peek().hasMoreElements());
        }

        @Override
        public Object nextElement() {
            Enumeration enumer = stack.peek();
            Object  node = enumer.nextElement();
            Enumeration children = children(model, node);

            if (!enumer.hasMoreElements()) {
                stack.pop();
            }
            if (children.hasMoreElements()) {
                stack.push(children);
            }
            return node;
        }

    }  // End of class PreorderEnumeration

    
    /**
     * Implementation of a breadthFirst traversal of a subtree in a TreeModel.
     */
    public static class BreadthFirstModelEnumeration implements Enumeration {
        protected Queue<Enumeration> queue;
        private TreeModel model;
        
        public BreadthFirstModelEnumeration(TreeModel model) {
            this(model, model.getRoot());
        }
        
        public BreadthFirstModelEnumeration(TreeModel model, Object node) {
            this.model = model;
            // Vector is just used for getting an Enumeration easily
            Vector v = new Vector(1);
            v.addElement(node);    
            queue = new ArrayDeque<Enumeration>();
            queue.offer(v.elements());
        }
        
        @Override
        public boolean hasMoreElements() {
            return !queue.isEmpty() &&
                    queue.peek().hasMoreElements();
        }
        
        @Override
        public Object nextElement() {
            // look at head
            Enumeration enumer = queue.peek();
            Object node = enumer.nextElement();
            Enumeration children = children(model, node);
            
            if (!enumer.hasMoreElements()) {
                // remove head
                queue.poll();
            }
            if (children.hasMoreElements()) {
                // add at tail
                queue.offer(children);
            }
            return node;
        }
        
    }  // End of class BreadthFirstEnumeration
    
    
    /**
     * Implementation of a postorder traversal of a subtree in a TreeModel.
     */
    public static class PostorderModelEnumeration implements Enumeration {
        protected TreeModel model;
        protected Object root;
        protected Enumeration children;
        protected Enumeration subtree;
        
        public PostorderModelEnumeration(TreeModel model) {
            this(model, model.getRoot());
        }
        
        public PostorderModelEnumeration(TreeModel model, Object node) {
            this.model = model;
            root = node;
            children = children(model, root);
            subtree = EMPTY_ENUMERATION;
        }
        
        @Override
        public boolean hasMoreElements() {
            return root != null;
        }
        
        @Override
        public Object nextElement() {
            Object retval;
            
            if (subtree.hasMoreElements()) {
                retval = subtree.nextElement();
            } else if (children.hasMoreElements()) {
                subtree = new PostorderModelEnumeration(model,
                        children.nextElement());
                retval = subtree.nextElement();
            } else {
                retval = root;
                root = null;
            }
            
            return retval;
        }
        
    }  // End of class PostorderEnumeration
    
    /**
     * Implementation of a preorder traversal of a subtree with nodes of type TreeNode.
     */
    public static class PreorderNodeEnumeration<M extends TreeNode> implements Enumeration<M> {
        protected Deque<Enumeration<M>> stack;

        public PreorderNodeEnumeration(M rootNode) {
            // Vector is just used for getting an Enumeration easily
            Vector<M> v = new Vector<M>(1);
            v.addElement(rootNode);     
            stack = new ArrayDeque<Enumeration<M>>();
            stack.push(v.elements());
        }

        @Override
        public boolean hasMoreElements() {
            return (!stack.isEmpty() &&
                    stack.peek().hasMoreElements());
        }

        @Override
        public M nextElement() {
            Enumeration<M> enumer = stack.peek();
            M node = enumer.nextElement();
            Enumeration<M> children = node.children();

            if (!enumer.hasMoreElements()) {
                stack.pop();
            }
            if (children.hasMoreElements()) {
                stack.push(children);
            }
            return node;
        }

    }  // End of class PreorderEnumeration

    /**
     * Implementation of a postorder traversal of a subtree with nodes of type TreeNode.
     */
    public static class PostorderNodeEnumeration<M extends TreeNode> implements Enumeration<M> {
        protected M root;
        protected Enumeration<M> children;
        protected Enumeration<M> subtree;

        public PostorderNodeEnumeration(M rootNode) {
            super();
            root = rootNode;
            children = root.children();
            subtree = EMPTY_ENUMERATION;
        }

        @Override
        public boolean hasMoreElements() {
            return root != null;
        }

        @Override
        public M nextElement() {
            M retval;

            if (subtree.hasMoreElements()) {
                retval = subtree.nextElement();
            } else if (children.hasMoreElements()) {
                subtree = new PostorderNodeEnumeration<M>(
                                children.nextElement());
                retval = subtree.nextElement();
            } else {
                retval = root;
                root = null;
            }

            return retval;
        }

    }  // End of class PostorderEnumeration


    /**
     * Implementation of a breadthFirst traversal of a subtree with nodes of type TreeNode.
     */
    public static class BreadthFirstNodeEnumeration<M extends TreeNode> implements Enumeration<M> {
        protected Queue<Enumeration<M>> queue;
        
        public BreadthFirstNodeEnumeration(M rootNode) {
            // Vector is just used for getting an Enumeration easily
            Vector<M> v = new Vector<M>(1);
            v.addElement(rootNode);    
            queue = new ArrayDeque<Enumeration<M>>();
            queue.offer(v.elements());
        }
        
        @Override
        public boolean hasMoreElements() {
            return !queue.isEmpty() &&
                    queue.peek().hasMoreElements();
        }
        
        @Override
        public M nextElement() {
            // look at head
            Enumeration<M> enumer = queue.peek();
            M node = enumer.nextElement();
            Enumeration<M> children = node.children();
            
            if (!enumer.hasMoreElements()) {
                // remove head
                queue.poll();
            }
            if (children.hasMoreElements()) {
                // add at tail
                queue.offer(children);
            }
            return node;
        }
        
        
    }  // End of class BreadthFirstEnumeration

    /**
     * Creates and returns an Enumeration across the direct children of the 
     * rootNode in the given TreeModel. 
     * 
     * @param model the TreeModel which contains parent, must not be null
     * @return an Enumeration across the direct children of the model's root, the enumeration
     *    is empty if the root is null or contains no children
     */
    public static Enumeration children(TreeModel model) {
        return children(model, model.getRoot());
    }
    
    /**
     * Creates and returns an Enumeration across the direct children of parentNode
     * in the given TreeModel. 
     * 
     * @param model the TreeModel which contains parent, must not be null
     * @param parent the parent of the enumerated children
     * @return an Enumeration across the direct children of parent, the enumeration
     *    is empty if the parent is null or contains no children
     */
    public static Enumeration children(final TreeModel model, final Object parent) {
        if (parent == null || model.isLeaf(parent)) {
            return EMPTY_ENUMERATION;
        }
        Enumeration<?> e = new Enumeration() {

            int currentIndex = 0;
            @Override
            public boolean hasMoreElements() {
                return model.getChildCount(parent) > currentIndex;
            }

            @Override
            public Object nextElement() {
                return model.getChild(parent, currentIndex++);
            }
            
        };
        return e;
    }
    
    private TreeUtilities() {}
}
