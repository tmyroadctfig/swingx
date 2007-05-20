/*
 * $Id$
 * 
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jdesktop.swingx.treetable;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

/**
 * {@code AbstractMutableTreeTableNode} provides an implementation of most of
 * the {@code MutableTreeTableNode} features.
 * 
 * @author Karl Schaefer
 */
public abstract class AbstractMutableTreeTableNode implements
        MutableTreeTableNode {
    /** this node's parent, or null if this node has no parent */
    protected MutableTreeTableNode parent;

    /** array of children, may be null if this node has no children */
    protected Vector<MutableTreeTableNode> children;

    /** optional user object */
    protected transient Object userObject;

    protected boolean allowsChildren;

    public AbstractMutableTreeTableNode() {
        this(null);
    }

    public AbstractMutableTreeTableNode(Object userObject) {
        this(userObject, true);
    }

    public AbstractMutableTreeTableNode(Object userObject,
            boolean allowsChildren) {
        this.userObject = userObject;
        this.allowsChildren = allowsChildren;
        children = new Vector<MutableTreeTableNode>();
    }

    public void add(MutableTreeTableNode child) {
        insert(child, getChildCount());
    }

    /**
     * {@inheritDoc}
     */
    public void insert(MutableTreeTableNode child, int index) {
        if (!allowsChildren) {
            throw new IllegalStateException("this node cannot accept children");
        }

        children.add(index, child);

        if (child.getParent() != this) {
            child.setParent(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(int index) {
        children.remove(index).setParent(null);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(MutableTreeTableNode node) {
        children.remove(node);
        node.setParent(null);
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromParent() {
        parent.remove(this);
    }

    /**
     * {@inheritDoc}
     */
    public void setParent(MutableTreeTableNode newParent) {
        if (newParent != null && newParent.getAllowsChildren()) {
            if (parent != null && parent.getIndex(this) != -1) {
                parent.remove(this);
            }
        } else if (newParent != null) {
            throw new IllegalArgumentException(
                    "newParent does not allow children");
        }

        parent = newParent;

        if (parent != null && parent.getIndex(this) == -1) {
            parent.insert(this, parent.getChildCount());
        }
    }

    /**
     * Returns this node's user object.
     * 
     * @return the Object stored at this node by the user
     * @see #setUserObject
     * @see #toString
     */
    public Object getUserObject() {
        return userObject;
    }

    /**
     * {@inheritDoc}
     */
    public void setUserObject(Object object) {
        userObject = object;
    }

    /**
     * {@inheritDoc}
     */
    public TreeTableNode getChildAt(int childIndex) {
        return children.elementAt(childIndex);
    }

    /**
     * {@inheritDoc}
     */
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    /**
     * {@inheritDoc}
     */
    public TreeTableNode getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration<? extends MutableTreeTableNode> children() {
        return children.elements();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getAllowsChildren() {
        return allowsChildren;
    }

    /**
     * Determines whether or not this node is allowed to have children. If
     * {@code allowsChildren} is {@code false}, all of this node's children are
     * removed.
     * <p>
     * Note: By default, a node allows children.
     * 
     * @param allowsChildren
     *            {@code true} if this node is allowed to have children
     */
    public void setAllowsChildren(boolean allowsChildren) {
        this.allowsChildren = allowsChildren;

        if (!this.allowsChildren) {
            children.removeAllElements();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    /**
     * Determines whether the specified column is editable.
     * 
     * @param column
     *            the column to query
     * @return always returns {@code false}
     */
    public boolean isEditable(int column) {
        return false;
    }

    /**
     * Sets the value for the given {@code column}.
     * 
     * @impl does nothing. It is provided for convenience.
     * @param aValue
     *            the value to set
     * @param column
     *            the column to set the value on
     */
    public void setValueAt(Object aValue, int column) {
        // does nothing
    }

    /**
     * Returns the result of sending <code>toString()</code> to this node's
     * user object, or null if this node has no user object.
     * 
     * @see #getUserObject
     */
    public String toString() {
        if (userObject == null) {
            return null;
        } else {
            return userObject.toString();
        }
    }
}
