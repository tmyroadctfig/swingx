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

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Support for change notification, usable by {@code TreeModel}s.
 * 
 * The changed/inserted/removed is expressed in terms of a {@code TreePath},
 * it's up to the client model to build it as appropriate.
 * 
 * This is inspired by {@code AbstractTreeModel} from Christian Kaufhold,
 * www.chka.de.
 * 
 * @author JW
 */
public final class TreeModelSupport {
    protected EventListenerList listeners;

    private TreeModel treeModel;

    /**
     * Creates the support class for the given {@code TreeModel}.
     * 
     * @param model
     *            the model to support
     * @throws NullPointerException
     *             if {@code model} is {@code null}
     */
    public TreeModelSupport(TreeModel model) {
        if (model == null)
            throw new NullPointerException("model must not be null");
        listeners = new EventListenerList();
        this.treeModel = model;
    }

    /** Call when there is a new root, which may be null, i.e. not existent. */
    public void fireNewRoot() {
        Object[] pairs = listeners.getListenerList();

        Object root = treeModel.getRoot();

        /*
         * Undocumented. I think it is the only reasonable/possible solution to
         * use use null as path if there is no root. TreeModels without root
         * aren't important anyway, since JTree doesn't support them (yet).
         */
        TreePath path = (root != null) ? new TreePath(root) : null;

        TreeModelEvent e = null;

        for (int i = pairs.length - 2; i >= 0; i -= 2) {
            if (pairs[i] == TreeModelListener.class) {
                if (e == null)
                    e = createTreeModelEvent(path);

                ((TreeModelListener) pairs[i + 1]).treeStructureChanged(e);
            }
        }
    }

    /**
     * Call when everything but the root has changed. Only may be called when
     * the root is not null. Otherwise there isn't a structure to have changed.
     */
    public void fireStructureChanged() {
        fireTreeStructureChanged(new TreePath(treeModel.getRoot()));
    }

    /**
     * Call when a node has changed its leaf state.
     * 
     * @param path
     *            a path ending in the (old) leaf
     */
    public void firePathLeafStateChanged(TreePath path) {
        fireTreeStructureChanged(path);
    }

    /**
     * Call when the tree structure below the path has completely changed.
     * 
     * @param parentPath
     *            the path to the node that changed
     */
    public void fireTreeStructureChanged(TreePath parentPath) {
        Object[] pairs = listeners.getListenerList();

        TreeModelEvent e = null;

        for (int i = pairs.length - 2; i >= 0; i -= 2) {
            if (pairs[i] == TreeModelListener.class) {
                if (e == null)
                    e = createTreeModelEvent(parentPath);

                ((TreeModelListener) pairs[i + 1]).treeStructureChanged(e);
            }
        }
    }

    /**
     * Call when the path itself has changed, but no structure changes have
     * occurred.
     */
    public void firePathChanged(TreePath path) {
        Object node = path.getLastPathComponent();
        TreePath parentPath = path.getParentPath();

        if (parentPath == null)
            fireChildrenChanged(path, null, null);
        else {
            Object parent = parentPath.getLastPathComponent();

            fireChildChanged(parentPath, treeModel
                    .getIndexOfChild(parent, node), node);
        }
    }

    public void fireChildAdded(TreePath parentPath, int index, Object child) {
        fireChildrenAdded(parentPath, new int[] { index },
                new Object[] { child });
    }

    public void fireChildChanged(TreePath parentPath, int index, Object child) {
        fireChildrenChanged(parentPath, new int[] { index },
                new Object[] { child });
    }

    public void fireChildRemoved(TreePath parentPath, int index, Object child) {
        fireChildrenRemoved(parentPath, new int[] { index },
                new Object[] { child });
    }

    public void fireChildrenAdded(TreePath parentPath, int[] indices,
            Object[] children) {
        Object[] pairs = listeners.getListenerList();

        TreeModelEvent e = null;

        for (int i = pairs.length - 2; i >= 0; i -= 2) {
            if (pairs[i] == TreeModelListener.class) {
                if (e == null)
                    e = createTreeModelEvent(parentPath, indices, children);

                ((TreeModelListener) pairs[i + 1]).treeNodesInserted(e);
            }
        }
    }

    /**
     * @param parentPath
     * @return
     */
    private TreeModelEvent createTreeModelEvent(TreePath parentPath) {
        return createTreeModelEvent(parentPath, null, null);
    }

    /**
     * @param parentPath
     * @param indices
     * @param children
     * @return
     */
    private TreeModelEvent createTreeModelEvent(TreePath parentPath,
            int[] indices, Object[] children) {
        return new TreeModelEvent(treeModel, parentPath, indices, children);
    }

    public void fireChildrenChanged(TreePath parentPath, int[] indices,
            Object[] children) {
        Object[] pairs = listeners.getListenerList();

        TreeModelEvent e = null;

        for (int i = pairs.length - 2; i >= 0; i -= 2) {
            if (pairs[i] == TreeModelListener.class) {
                if (e == null)
                    e = createTreeModelEvent(parentPath, indices, children);

                ((TreeModelListener) pairs[i + 1]).treeNodesChanged(e);
            }
        }
    }

    public void fireChildrenRemoved(TreePath parentPath, int[] indices,
            Object[] children) {
        Object[] pairs = listeners.getListenerList();

        TreeModelEvent e = null;

        for (int i = pairs.length - 2; i >= 0; i -= 2) {
            if (pairs[i] == TreeModelListener.class) {
                if (e == null)
                    e = createTreeModelEvent(parentPath, indices, children);
                ((TreeModelListener) pairs[i + 1]).treeNodesRemoved(e);
            }
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(TreeModelListener.class, l);
    }

    public TreeModelListener[] getTreeModelListeners() {
        return listeners.getListeners(TreeModelListener.class);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(TreeModelListener.class, l);
    }
}
