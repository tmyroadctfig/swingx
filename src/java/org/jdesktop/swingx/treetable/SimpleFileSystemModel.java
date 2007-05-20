/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx.treetable;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 * A tree table model to simulate a file system.
 * <p>
 * This tree table model implementation does not extends
 * {@code AbstractTreeTableModel}. The file system metaphor demonstrates that
 * it is often easier to directly implement tree structures directly instead of
 * using intermediaries, such as {@code TreeNode}.
 * <p>
 * It would be possible to create this same class by extending
 * {@code AbstractTreeTableModel}, however the number of methods that you would
 * need to override almost precludes that means of implementation.
 * <p>
 * A "full" version of this model might allow editing of file names, the
 * deletion of files, and the movement of files. This simple implementation does
 * not intend to tackle such problems, but this implementation may be extended
 * to handle such details.
 * 
 * @author Ramesh Gupta
 * @author Karl Schaefer
 */
public class SimpleFileSystemModel implements TreeTableModel {
    protected EventListenerList listenerList;

    // The the returned file length for directories.
    private static final Integer ZERO = new Integer(0);

    private File root;

    public SimpleFileSystemModel() {
        this(new File(File.separator));
    }

    public SimpleFileSystemModel(File root) {
        this.root = root;
        this.listenerList = new EventListenerList();
    }

    /**
     * {@inheritDoc}
     */
    public File getChild(Object parent, int index) {
        if (parent instanceof File) {
            File parentFile = (File) parent;
            File[] files = parentFile.listFiles();

            if (files != null) {
                Arrays.sort(files);

                return files[index];
            }
        }

        return null;
    }

    public int getChildCount(Object parent) {
        if (parent instanceof File) {
            File parentFile = (File) parent;

            if (parentFile.isDirectory()) {
                return parentFile.listFiles().length;
            }
        }

        return 0;
    }

    public Class<?> getColumnClass(int column) {
        if (column == 2) {
            return Boolean.class;
        }

        return Object.class;
    }

    public int getColumnCount() {
        return 4;
    }

    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Name";
        case 1:
            return "Size";
        case 2:
            return "Directory";
        case 3:
            return "Modification Date";
        default:
            return "Column " + column;
        }
    }

    public Object getValueAt(Object node, int column) {
        if (node instanceof File) {
            File file = (File) node;
            switch (column) {
            case 0:
                return file.getName();
            case 1:
                return file.isFile() ? new Integer((int) file.length()) : ZERO;
            case 2:
                return new Boolean(file.isDirectory());
            case 3:
                return new Date(file.lastModified());
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getHierarchicalColumn() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(Object node, int column) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setHierarchicalColumn(int column) {
        //does nothing
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(Object value, Object node, int column) {
        //does nothing
    }

    /**
     * {@inheritDoc}
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    /**
     * {@inheritDoc}
     */
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof File && child instanceof File) {
            File parentFile = (File) parent;
            File[] files = parentFile.listFiles();
            
            Arrays.sort(files);
            
            for (int i = 0, len = files.length; i < len; i++) {
                if (files[i].equals(child)) {
                    return i;
                }
            }
        }
        
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public File getRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeaf(Object node) {
        if (node instanceof File) {
            return ((File) node).isFile();
        }
        
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    /**
     * {@inheritDoc}
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        //does nothing
    }
    
    public TreeModelListener[] getTreeModelListeners() {
        return listenerList.getListeners(TreeModelListener.class);
    }
}
