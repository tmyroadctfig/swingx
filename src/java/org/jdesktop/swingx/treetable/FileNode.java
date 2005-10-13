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
