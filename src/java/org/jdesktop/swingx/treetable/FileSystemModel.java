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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

/**
 * FileSystemModel
 *
 * @author Ramesh Gupta
 */
public class FileSystemModel extends DefaultTreeTableModel {
    private static final Logger LOG = Logger.getLogger(FileSystemModel.class
            .getName());
    protected boolean asksAllowsChildren;

    public FileSystemModel() {
	this(new FileNode(new File(File.separator)));
    }

    public FileSystemModel(TreeNode root) {
	this(root, false);
    }

    public FileSystemModel(TreeNode root, boolean asksAllowsChildren) {
	super(root);
	this.asksAllowsChildren = asksAllowsChildren;
    }

    public Object getChild(Object parent, int index) {
	try {
	    return ((FileNode)parent).getChildren().get(index);
	}
	catch (Exception ex) {
	    return super.getChild(parent, index);
	}
    }

    public int getChildCount(Object parent) {
	try {
	    return ((FileNode)parent).getChildren().size();
	}
	catch (Exception ex) {
	    return super.getChildCount(parent);
	}
    }

    public int getColumnCount() {
	/**@todo Implement this org.jdesktopx.swing.treetable.TreeTableModel abstract method*/
	return 4;
    }

    public String getColumnName(int column) {
        switch(column) {
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
	final File file = ((FileNode)node).getFile();
	try {
	    switch(column) {
	    case 0:
		return file.getName();
	    case 1:
		return file.isFile() ? new Integer((int)file.length()) : ZERO;
	    case 2:
		return new Boolean(!file.isFile());
	    case 3:
		return new java.util.Date(file.lastModified());
	    }
	}
	catch  (Exception ex) {
                LOG.log(Level.WARNING, "Problem accessing file", ex);

	}

	return null;
    }

    // The the returned file length for directories.
    private static final Integer ZERO = new Integer(0);
}
