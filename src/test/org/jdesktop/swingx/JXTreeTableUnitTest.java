/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HierarchicalColumnHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

// import de.kleopatra.view.LFSwitcher;

public class JXTreeTableUnitTest extends InteractiveTestCase {

    private TreeTableModel treeTableModel;

    public JXTreeTableUnitTest() {
        super("JXTreeTable Unit Test");
    }

    /**
     * Issue #151: renderer properties ignored after setting treeTableModel.
     * 
     */
    public void testRendererProperties() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        // storing negates of properties
        boolean expandsSelected = !treeTable.getExpandsSelectedPaths();
        boolean scrollsOnExpand = !treeTable.getScrollsOnExpand();
        boolean showRootHandles = !treeTable.getShowsRootHandles();
        boolean rootVisible = !treeTable.isRootVisible();
        // setting negates properties
        treeTable.setExpandsSelectedPaths(expandsSelected);
        treeTable.setScrollsOnExpand(scrollsOnExpand);
        treeTable.setShowsRootHandles(showRootHandles);
        treeTable.setRootVisible(rootVisible);
        // assert negates are set - sanity assert
        assertEquals("expand selected", expandsSelected, treeTable
                .getExpandsSelectedPaths());
        assertEquals("scrolls expand", scrollsOnExpand, treeTable
                .getScrollsOnExpand());
        assertEquals("shows handles", showRootHandles, treeTable
                .getShowsRootHandles());
        assertEquals("root visible", rootVisible, treeTable.isRootVisible());
        // setting a new model
        treeTable.setTreeTableModel(new DefaultTreeTableModel());
        // assert negates are set
        assertEquals("expand selected", expandsSelected, treeTable
                .getExpandsSelectedPaths());
        assertEquals("scrolls expand", scrollsOnExpand, treeTable
                .getScrollsOnExpand());
        assertEquals("shows handles", showRootHandles, treeTable
                .getShowsRootHandles());
        assertEquals("root visible", rootVisible, treeTable.isRootVisible());

    }

    /**
     * Issue #148: line style client property not respected by renderer.
     * 
     */
    public void testLineStyle() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        String propertyName = "JTree.lineStyle";
        treeTable.putClientProperty(propertyName, "Horizontal");
        JXTree renderer = (JXTree) treeTable.getCellRenderer(0, 0);
        assertEquals(propertyName + " set on renderer", "Horizontal", renderer
                .getClientProperty(propertyName));
    }

    /**
     * sanity test: arbitrary client properties not passed to renderer.
     * 
     */
    public void testArbitraryClientProperty() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        String propertyName = "someproperty";
        treeTable.putClientProperty(propertyName, "Horizontal");
        JXTree renderer = (JXTree) treeTable.getCellRenderer(0, 0);
        assertNull(propertyName + " not set on renderer", renderer
                .getClientProperty(propertyName));

    }

    // ---------------------------- interactive tests


    public void interactiveTestCompareTreeProperties() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setShowsRootHandles(false);
        treeTable.setRootVisible(false);
        JXTreeTable other = new JXTreeTable(treeTableModel);
        other.setRootVisible(true);
        other.setShowsRootHandles(false);
        JFrame frame = wrapWithScrollingInFrame(treeTable, other, "compare rootVisible");
        frame.setVisible(true);
    }
    /**    
     * setting tree properties: tree not updated correctly.
     */    
    public void interactiveTestTreeProperties() {
        final JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setShowsRootHandles(false);
        // storing negates of properties
        Action toggle = new AbstractAction("Toggle Properties") {

            public void actionPerformed(ActionEvent e) {
                boolean expandsSelected = !treeTable.getExpandsSelectedPaths();
                boolean scrollsOnExpand = !treeTable.getScrollsOnExpand();
                boolean showRootHandles = !treeTable.getShowsRootHandles();
                boolean rootVisible = !treeTable.isRootVisible();
                // setting negates properties
//                treeTable.setExpandsSelectedPaths(expandsSelected);
//                treeTable.setScrollsOnExpand(scrollsOnExpand);
//                treeTable.setShowsRootHandles(showRootHandles);
                treeTable.setRootVisible(rootVisible);
                
            }
            
        };
//        treeTable.getActionMap().put("toggleProperties", toggle);
//        treeTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F5"), "toggleProperties");
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "Toggle Tree properties ");
        addAction(frame, toggle);
        frame.setVisible(true);
    }
    
    /**    issue #148
     *   did not work on LFs which normally respect lineStyle
     *   winLF does not respect it anyway...
     */    
    public void interactiveTestFilterAndLineStyle() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        // issue #148
        // did not work on LFs which normally respect lineStyle
        // winLF does not respect it anyway...
        treeTable.putClientProperty("JTree.lineStyle", "Angled");
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
        treeTable.setHighlighters(new HighlighterPipeline(new Highlighter[] {
                AlternateRowHighlighter.quickSilver,
                new HierarchicalColumnHighlighter(),
                new PatternHighlighter(null, Color.red, "s.*",
                        Pattern.CASE_INSENSITIVE, 0, -1), }));
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "QuickSilver-, Column-, PatternHighligher and LineStyle");
        frame.setVisible(true);
    }

    
    /**
     * Issue #204: weird filtering.
     *
     */
    public void interactiveTestFilters() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.putClientProperty("JTree.lineStyle", "Angled");
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
        treeTable.setFilters(new FilterPipeline(new Filter[] {
                new PatternFilter( "d.*",
                        Pattern.CASE_INSENSITIVE, 0), }));
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "PatternFilter");
        frame.setVisible(true);
    }
    
    public void interactiveTestFiltersAndRowHeight() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
        treeTable.setHighlighters(new HighlighterPipeline(new Highlighter[] {
                AlternateRowHighlighter.linePrinter,
                new HierarchicalColumnHighlighter(), }));
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "LinePrinter-, ColumnHighlighter and RowHeight");
        frame.setVisible(true);
    }

    public void interactiveTestAlternateRowHighlighter() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable
                .setHighlighters(new HighlighterPipeline(
                        new Highlighter[] { AlternateRowHighlighter.classicLinePrinter, }));
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "ClassicLinePrinter and RowHeight");
        frame.setVisible(true);
    }

    public void interactiveTestBackgroundHighlighter() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setHighlighters(new HighlighterPipeline(new Highlighter[] {
                AlternateRowHighlighter.notePadBackground,
                new HierarchicalColumnHighlighter(), }));
        treeTable.setBackground(new Color(0xFF, 0xFF, 0xCC)); // notepad
        treeTable.setGridColor(Color.cyan.darker());
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
        treeTable.setShowHorizontalLines(true);
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "NotePadBackground- HierarchicalColumnHighlighter and horiz lines");
        frame.setVisible(true);
    }

    public void interactiveTestLedgerBackground() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        treeTable.setGridColor(Color.cyan.darker());
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
        treeTable.setShowHorizontalLines(true);
        JFrame frame = wrapWithScrollingInFrame(treeTable, "LedgerBackground");
        frame.setVisible(true);
    }

    public void interactiveTestHierarchicalColumn() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setHighlighters(new HighlighterPipeline(
                new Highlighter[] { new HierarchicalColumnHighlighter(), }));
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "HierarchicalColumnHigh");
        frame.setVisible(true);
    }

    public void interactiveTestIntercellSpacing1() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setIntercellSpacing(new Dimension(1, 1));
        treeTable.setShowGrid(true);
        JFrame frame = wrapWithScrollingInFrame(treeTable, "Intercellspacing 1");
        frame.setVisible(true);
    }

    public void interactiveTestIntercellSpacing2() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setIntercellSpacing(new Dimension(2, 2));
        treeTable.setShowGrid(true);
        JFrame frame = wrapWithScrollingInFrame(treeTable, "Intercellspacing 2");
        frame.setVisible(true);
    }

    public void interactiveTestIntercellSpacing3() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setIntercellSpacing(new Dimension(3, 3));
        treeTable.setShowGrid(true);
        JFrame frame = wrapWithScrollingInFrame(treeTable, "Intercellspacing 3");
        frame.setVisible(true);
    }

    public void interactiveTestHighlighterRowHeight() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setHighlighters(new HighlighterPipeline(
                new Highlighter[] { new Highlighter(Color.orange, null), }));
        treeTable.setIntercellSpacing(new Dimension(15, 15));
        treeTable.setRowHeight(48);
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "Orange, IntercellSpacing15, big rowheight");
        frame.setVisible(true);
    }

    public void interactiveTestHighLighters() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setIntercellSpacing(new Dimension(15, 15));
        treeTable.setRowHeight(48);
        // not supported in JXTreeTable
 //       treeTable.setRowHeight(0, 96);
        treeTable.setShowGrid(true);
        Highlighter conditional = new ConditionalHighlighter(Color.BLUE, null, 0, 0) {

            protected boolean test(ComponentAdapter adapter) {
                return adapter.hasFocus();
            }
            
        };
        treeTable.setHighlighters(new HighlighterPipeline(
                new Highlighter[] {
                        conditional,
                        new Highlighter(Color.orange, null),
                        new HierarchicalColumnHighlighter(),
                        new PatternHighlighter(null, Color.red,
                                ".*D.*", 0, 0, 0), }));
        JFrame frame = wrapWithScrollingInFrame(treeTable, "Highlighters");
        frame.setVisible(true);
    }


    // ------------------ init
    protected void setUp() throws Exception {
        super.setUp();
        treeTableModel = new FileSystemModel();
    }

    public static void main(String[] args) {
        // LFSwitcher.metalLF();
        JXTreeTableUnitTest test = new JXTreeTableUnitTest();
        try {
         //   test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*HighLighters");
         //      test.runInteractiveTests("interactive.*ilter.*");
           test.runInteractiveTests("interactive.*Prop.*");
        } catch (Exception ex) {

        }
    }
}
