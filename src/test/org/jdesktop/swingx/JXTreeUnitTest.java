/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.JXTreeTableUnitTest.InsertTreeTableModel;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.tree.DefaultXTreeCellEditor;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.test.ChangeReport;
import org.jdesktop.test.PropertyChangeReport;


/**
 * @author Jeanette Winzenburg
 */
public class JXTreeUnitTest extends InteractiveTestCase {

    protected TreeTableModel treeTableModel;
        
    public JXTreeUnitTest() {
        super("JXTree Test");
    }

    /**
     * Issue #??-swingx: competing setHighlighters(null) break code.
     * 
     * More specifically: it doesn't compile without casting the null, that's why
     * it has to be commented here.
     *
     */
//    public void testHighlightersNull() {
//        JXTree tree = new JXTree();
//        tree.setHighlighters(null);
//    }

    /**
     * Issue #??-swingx: setHighlighters(null) throws NPE. 
     * 
     */
    public void testSetHighlightersNull() {
        JXTree tree = new JXTree();
        tree.setHighlighters((Highlighter) null);
        assertNull(tree.getHighlighters());
    }
    
    /**
     * Issue #??-swingx: setHighlighters() throws NPE. 
     * 
     */
    public void testSetHighlightersNoHighlighter() {
        JXTree tree = new JXTree();
        tree.setHighlighters();
        assertNull(tree.getHighlighters());
    }

    /**
     * Issue #??-swingx: setHighlighters() throws NPE. 
     * 
     * Test that null highlighter resets the pipeline to null.
     */
    public void testSetHighlightersReset() {
        JXTree tree = new JXTree();
        tree.addHighlighter(new Highlighter());
        // sanity
        assertEquals(1, tree.getHighlighters().getHighlighters().length);
        tree.setHighlighters();
        assertNull(tree.getHighlighters());
    }


    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
    public void testRemoveHighlighter() {
        JXTree table = new JXTree();
        // test cope with null
        table.removeHighlighter(null);
        Highlighter presetHighlighter = AlternateRowHighlighter.classicLinePrinter;
        HighlighterPipeline pipeline = new HighlighterPipeline(new Highlighter[] {presetHighlighter});
        table.setHighlighters(pipeline);
        ChangeReport report = new ChangeReport();
        pipeline.addChangeListener(report);
        table.removeHighlighter(new Highlighter());
        // sanity: highlighter was not contained
        assertFalse("pipeline must not have fired", report.hasEvents());
        // remove the presetHighlighter
        table.removeHighlighter(presetHighlighter);
        assertEquals("pipeline must have fired on remove", 1, report.getEventCount());
        assertEquals("pipeline must be empty", 0, pipeline.getHighlighters().length);
    }
    
    /**
     * test choking on precondition failure (highlighter must not be null).
     *
     */
    public void testAddNullHighlighter() {
        JXTree tree = new JXTree();
        try {
            tree.addHighlighter(null);
            fail("adding a null highlighter must throw NPE");
        } catch (NullPointerException e) {
            // pass - this is what we expect
        } catch (Exception e) {
            fail("adding a null highlighter throws exception different " +
                        "from the expected NPE \n" + e);
        }
    }
    
    public void testAddHighlighterWithNotEmptyPipeline() {
        JXTree tree = new JXTree();
        Highlighter presetHighlighter = AlternateRowHighlighter.classicLinePrinter;
        HighlighterPipeline pipeline = new HighlighterPipeline(new Highlighter[] {presetHighlighter});
        tree.setHighlighters(pipeline);
        Highlighter highlighter = new Highlighter();
        ChangeReport report = new ChangeReport();
        pipeline.addChangeListener(report);
        tree.addHighlighter(highlighter);
        assertSame("pipeline must be same as preset", pipeline, tree.getHighlighters());
        assertEquals("pipeline must have fired changeEvent", 1, report.getEventCount());
        assertPipelineHasAsLast(pipeline, highlighter);
    }
    
    private void assertPipelineHasAsLast(HighlighterPipeline pipeline, Highlighter highlighter) {
        Highlighter[] highlighters = pipeline.getHighlighters();
        assertTrue("pipeline must not be empty", highlighters.length > 0);
        assertSame("highlighter must be added as last", highlighter, highlighters[highlighters.length - 1]);
    }

    /**
     * test adding a highlighter.
     *
     *  asserts that a pipeline is created and set (firing a property change) and
     *  that the pipeline contains the highlighter.
     */
    public void testAddHighlighterWithNullPipeline() {
        JXTree tree = new JXTree();
        PropertyChangeReport report = new PropertyChangeReport();
        tree.addPropertyChangeListener(report);
        Highlighter highlighter = new Highlighter();
        tree.addHighlighter(highlighter);
        assertNotNull("table must have created pipeline", tree.getHighlighters());
        assertTrue("table must have fired propertyChange for highlighters", report.hasEvents("highlighters"));
        assertPipelineContainsHighlighter(tree.getHighlighters(), highlighter);
    }
    
    /**
     * fails if the given highlighter is not contained in the pipeline.
     * PRE: pipeline != null, highlighter != null.
     * 
     * @param pipeline
     * @param highlighter
     */
    private void assertPipelineContainsHighlighter(HighlighterPipeline pipeline, Highlighter highlighter) {
        Highlighter[] highlighters = pipeline.getHighlighters();
        for (int i = 0; i < highlighters.length; i++) {
            if (highlighter.equals(highlighters[i])) return;
        }
        fail("pipeline does not contain highlighter");
        
    }


    
    /**
     * Issue #254-swingx: expandAll doesn't expand if root not shown?
     *
     */
    public void testExpandAllWithInvisible() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final InsertTreeTableModel model = new InsertTreeTableModel(root);
        int childCount = 5;
        for (int i = 0; i < childCount; i++) {
            model.addChild(root);
        }
        final JXTree treeTable = new JXTree(model);
        // sanity...
        assertTrue(treeTable.isRootVisible());
        assertEquals("all children visible", childCount + 1, treeTable.getRowCount());
        treeTable.collapseAll();
        assertEquals(" all children invisible", 1, treeTable.getRowCount());
        treeTable.setRootVisible(false);
        assertEquals("no rows with invisible root", 0, treeTable.getRowCount());
        treeTable.expandAll();
        assertTrue(treeTable.getRowCount() > 0);
        
    }

    /**
     * test enhanced getSelectedRows contract: returned 
     * array != null
     *
     */
    public void testNotNullGetSelectedRows() {
        JXTree tree = new JXTree(treeTableModel);
        // sanity: no selection
        assertEquals(0, tree.getSelectionCount());
        assertNotNull("getSelectedRows guarantees not null array", tree.getSelectionRows());
    }
    
    /**
     * test enhanced getSelectedRows contract: returned 
     * array != null
     *
     */
    public void testNotNullGetSelectedPaths() {
        JXTree tree = new JXTree(treeTableModel);
        // sanity: no selection
        assertEquals(0, tree.getSelectionCount());
        assertNotNull("getSelectedPaths guarantees not null array", tree.getSelectionPaths());
    }
    /**
     * Issue #221-swingx: actionMap not initialized in JXTreeNode constructor.
     * Issue #231-swingx: icons lost in editor, enhanced default editor not installed.
     * 
     * PENDING: test all constructors!
     *
     */
    public void testInitInConstructors() {
        assertXTreeInit(new JXTree());
        assertXTreeInit(new JXTree(new Object[] {}));
        assertXTreeInit(new JXTree(new Vector()));
        assertXTreeInit(new JXTree(new Hashtable()));
        assertXTreeInit(new JXTree(new DefaultMutableTreeNode("dummy"), false));
        assertXTreeInit(new JXTree(new DefaultMutableTreeNode("dummy")));
        assertXTreeInit(new JXTree(new DefaultTreeModel(new DefaultMutableTreeNode("dummy"))));
    }

    /**
     * @param tree
     */
    private void assertXTreeInit(JXTree tree) {
        assertNotNull("Actions must be initialized", tree.getActionMap().get("find"));
        assertTrue("Editor must be DefaultXTreeCellEditor", 
                tree.getCellEditor() instanceof DefaultXTreeCellEditor);
        // JW: wrong assumption, available for TreeTableModel impl only?
//        assertNotNull("conversionMethod must be initialized", 
//                tree.getValueConversionMethod(tree.getModel()));
//        tree.getValueConversionMethod(tree.getModel());
    }

    /** 
     * JTree allows null model.
     * learning something new every day :-)
     *
     */
    public void testNullModel() {
        JXTree tree = new JXTree();
        assertNotNull(tree.getModel());
        tree.setModel(null);
        assertEquals(0, tree.getRowCount());
        // tree.getComponentAdapter().isLeaf();
    }

    
    protected void setUp() throws Exception {
        super.setUp();
        treeTableModel = new FileSystemModel();
    }

}
