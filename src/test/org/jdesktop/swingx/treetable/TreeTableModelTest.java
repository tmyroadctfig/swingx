/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.treetable;

import java.util.logging.Logger;

import javax.swing.tree.TreeModel;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


import junit.framework.TestCase;

/**
 * Unit tests around TreeTableModel and related classes.
 */
@RunWith(JUnit4.class)
public class TreeTableModelTest extends TestCase {
    private static final Logger LOG = Logger.getLogger(TreeTableModelTest.class
            .getName());
    /**
     * Issue #??-swingx: FileSystemModel throws NPE in getChildCount.
     *
     */
    @Test
    public void testFileSystemModelNPE() {
        TreeModel model = new FileSystemModel();
        Object root = model.getRoot();
        for (int i = 0; i < model.getChildCount(root); i++) {
            Object child = model.getChild(root, i);
            // sanity
            assertNotNull("child must not be null at " + i, child);
            model.getChildCount(child);
        }     
    }

    
    /**
     * Issue #??-swingx: SimpleFileSystemModel throws NPE in getChildCount.
     */
    @Test
    public void testFileSystemModelSimple() {
        SimpleFileSystemModel model = new SimpleFileSystemModel();
        Object root = model.getRoot();
        for (int i = 0; i < model.getChildCount(root); i++) {
            Object child = model.getChild(root, i);
            // sanity
            assertNotNull("child must not be null at " + i, child);
            model.getChildCount(child);
        }     
    }

    /**
     * Issue #218-swingx: TreeTableModel impl break type contract for
     * hierarchical column.
     * 
     * Expected contract (non-doc'ed but common sense...)
     * 
     * <pre> <code>
     * 
     * Object value = model.getValueAt(node, column);
     * assert((value == null) || 
     *    (model.getColumnClass(column).isAssignableFrom(value.getClass())))
     *    
     * </code> </pre>
     * 
     * Here: FileSystemModel.
     * 
     */
    @Test
    public void testFileSystemTTM() {
        TreeTableModel model = new FileSystemModel();
        assertColumnClassAssignableFromValue(model);
    }

    /**
     * loops through all model columns to test type contract.
     * 
     * 
     * @param model the model to test.
     */
    private void assertColumnClassAssignableFromValue(TreeTableModel model) {
        for (int i = 0; i < model.getColumnCount(); i++) {
            Class clazz = model.getColumnClass(i);
            Object value = model.getValueAt(model.getRoot(), i);
            if (value != null) {
                assertTrue("column class must be assignable to value class at column " + i + "\n" +
                                "columnClass = " + model.getColumnClass(i) + "\n" +
                                "valueClass = " + value.getClass()
                        , clazz.isAssignableFrom(value.getClass()));
            } else {
                LOG.info("column " + i + " not testable - value == null");
            }
        }
    }


}
