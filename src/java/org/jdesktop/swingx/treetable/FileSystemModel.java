/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.treetable;

import java.io.File;

import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

/**
 * FileSystemModel
 *
 * @author Ramesh Gupta
 */
public class FileSystemModel extends DefaultTreeTableModel {
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
	    ex.printStackTrace();
	}

	return null;
    }

    // The the returned file length for directories.
    private static final Integer ZERO = new Integer(0);
}
