/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.treetable;

import java.util.logging.Logger;

import javax.swing.tree.TreeModel;

import junit.framework.TestCase;

public class TreeTableModelIssues extends TestCase {
    private static final Logger LOG = Logger
        .getLogger(TreeTableModelIssues.class.getName());

    /**
     * Issue #??-swingx: FileSystemModel throws NPE in getChildCount.
     *
     */
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
