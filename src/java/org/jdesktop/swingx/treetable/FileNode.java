/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.treetable;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * FileNode
 *
 * @author Ramesh Gupta
 */
public class FileNode extends DefaultMutableTreeNode {
    public FileNode(File file) {
	this.file = file;
	this.isDir = file.isDirectory();
    }

    /* @todo Add userObject support
       public FileNode(Object userObject) {
       ...
       }

       public FileNode(Object userObject, boolean allowsChildren) {
       ...
       }
    */

    public boolean getAllowsChildren() {
	return isDir;
    }

    protected List getChildren() {	// rg:changed return type
	if (children == null) {
	    try {
		final String[] files = file.list();
		if (files != null) {
		    // Create an empty list of FileNodes (#elements = files.length)
		    children = new Vector(files.length);
		    final String path = file.getPath();
		    for (int i = 0; i < files.length; i++) {
			final File childFile = new File(path, files[i]);
			children.add(new FileNode(childFile));
		    }
		}
	    }
	    catch (SecurityException se) {
	    }
	}

	return children;
    }

    public File getFile() {
	return file;
    }

    public boolean isLeaf() {
	return !isDir;
    }

    public String toString() {
	return file.getName();
    }

    private	final File		file;
    private final boolean		isDir;
}
