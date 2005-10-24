/*
 * Created on 25.07.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;

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
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.util.ComponentTreeTableModel;

/**
 * @author Jeanette Winzenburg
 */
public class JXTreeTableVisualCheck extends JXTreeTableUnitTest {
    public static void main(String[] args) {
        setSystemLF(true);
        JXTreeTableVisualCheck test = new JXTreeTableVisualCheck();
        try {
            test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*HighLighters");
         //      test.runInteractiveTests("interactive.*SortingFilter.*");
//           test.runInteractiveTests("interactive.*Node.*");
         //     test.runInteractiveTests("interactive.*Focus.*");
        } catch (Exception ex) {

        }
    }

    /**
     * Issue #82-swingx: update probs with insert node.
     * 
     * Adapted from example code in report.
     *
     */
    public void interactiveTestInsertNode() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final InsertTreeTableModel model = new InsertTreeTableModel(root);
        final  DefaultMutableTreeNode leaf = model.addChild(root);
        JXTree tree = new JXTree(model);
        JXTreeTable treeTable = new JXTreeTable(model);
        JXFrame frame = wrapWithScrollingInFrame(tree, treeTable, "update on insert");
        Action insertAction = new AbstractAction("insert node") {

            public void actionPerformed(ActionEvent e) {
                model.addChild(leaf);
                setEnabled(false);
                
            }
            
        };
        addAction(frame, insertAction);
        frame.setVisible(true);
    }
 
    /**
     * Model used to show insert update issue.
     */
    public static class InsertTreeTableModel extends DefaultTreeTableModel {
        public InsertTreeTableModel(TreeNode root) {
            super(root);
        }

        public int getColumnCount() {
            return 2;
        }

        private DefaultMutableTreeNode addChild(DefaultMutableTreeNode parent) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Child");
            parent.add(newNode);
            fireTreeNodesInserted(this, getPathToRoot(parent),
                    new int[] { parent.getIndex(newNode) },
                    new Object[] { newNode });

            return newNode;
        }
    }

    /**
     * see effect of switching treeTableModel.
     * Problem when toggling back to FileSystemModel: hierarchical 
     * column does not show filenames, need to click into table first.
     *
     */
    public void interactiveTestSetModel() {
        final JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(treeTable, "toggle model");
        frame.setVisible(true);
        final TreeTableModel model = new ComponentTreeTableModel(frame);
        Action action = new AbstractAction("Toggle model") {

            public void actionPerformed(ActionEvent e) {
                TreeTableModel myModel = treeTable.getTreeTableModel();
                treeTable.setTreeTableModel(myModel == model ? treeTableModel : model);
                
            }
            
        };
        addAction(frame, action);
    }
    
    public void interactiveTestFocusedCellBackground() {
        JXTreeTable xtable = new JXTreeTable(treeTableModel);
        xtable.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        JFrame frame = wrapWithScrollingInFrame(xtable, "Unselected focused background");
        frame.setVisible(true);
    }

    /**
     * Issue #226: no per-cell tooltips in TreeColumn.
     */
    public void interactiveTestToolTips() {
        JXTreeTable tree = new JXTreeTable(treeTableModel);
        // JW: don't use this idiom - Stackoverflow...
        // multiple delegation - need to solve or discourage
        tree.setTreeCellRenderer(createRenderer());
        tree.setDefaultRenderer(Object.class, createTableRenderer(tree.getDefaultRenderer(Object.class)));
        JFrame frame = wrapWithScrollingInFrame(tree, "tooltips");
        frame.setVisible(true);  
    }

    private TableCellRenderer createTableRenderer(final TableCellRenderer delegate) {
        TableCellRenderer l = new TableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component result = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JComponent) result).setToolTipText(String.valueOf(value));
                return result;
            }
            
        };
        return l;
    }


    private TreeCellRenderer createRenderer() {
        final TreeCellRenderer delegate = new DefaultTreeCellRenderer();
        TreeCellRenderer renderer = new TreeCellRenderer() {

            public Component getTreeCellRendererComponent(JTree tree, Object value, 
                    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component result = delegate.getTreeCellRendererComponent(tree, value, 
                        selected, expanded, leaf, row, hasFocus);
                ((JComponent) result).setToolTipText(String.valueOf(tree.getPathForRow(row)));
                 return result;
            }
            
        };
        return renderer;
    }

    /**
     * reported: boolean not showing - not reproducible 
     * 
     */
    public void interactiveTestBooleanRenderer() {
        final JXTreeTable treeTable = new JXTreeTable(new MyTreeTableModel());
        treeTable.setRootVisible(true);
        JFrame frame = wrapWithScrollingInFrame(treeTable, "boolean renderers");
        frame.setVisible(true);
  
    }
    private class MyTreeTableModel extends DefaultTreeTableModel {
        
        public MyTreeTableModel() {            
            final DefaultMutableTreeNode root = 
                new DefaultMutableTreeNode("Root");
            
            root.add(new DefaultMutableTreeNode("A"));
            root.add(new DefaultMutableTreeNode("B"));
            this.setRoot(root);
        }
        
        public int getColumnCount() {
            return 2;
        }
        
        public Class getColumnClass(int column) {
            if (column == 1) {
                return Boolean.class;
            }
            return super.getColumnClass(column);
        }
        
        public boolean isCellEditable(int row, int column) {
            return true;
        }
        
        public boolean isCellEditable(Object value, int column) {
            return true;
        }
        public Object getValueAt(Object o, int column) {
            if (column == 0) {
                return o.toString();
            }
            
            return new Boolean(true);
        }
    }


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
        Action toggleHandles = new AbstractAction("Toggle Handles") {

            public void actionPerformed(ActionEvent e) {
                treeTable.setShowsRootHandles(!treeTable.getShowsRootHandles());
                
            }
            
        };
        Action toggleRoot = new AbstractAction("Toggle Root") {

            public void actionPerformed(ActionEvent e) {
                treeTable.setRootVisible(!treeTable.isRootVisible());
                
            }
            
        };
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
        JXFrame frame = wrapWithScrollingInFrame(treeTable,
                "Toggle Tree properties ");
        addAction(frame, toggleRoot);
        addAction(frame, toggleHandles);
        frame.setVisible(true);
    }

    /**    
     * Issue #242: CCE when setting icons.
     */    
    public void interactiveTestTreeIcons() {
        final JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        Icon downIcon = new ImageIcon(getClass().getResource("resources/images/" + "wellbottom.gif"));
//        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
//        renderer.setClosedIcon(downIcon);
//        treeTable.setTreeCellRenderer(renderer);
        treeTable.setClosedIcon(downIcon);
//        Action toggleHandles = new AbstractAction("Toggle Handles") {
//
//            public void actionPerformed(ActionEvent e) {
//                treeTable.setShowsRootHandles(!treeTable.getShowsRootHandles());
//                
//            }
//            
//        };
//        Action toggleRoot = new AbstractAction("Toggle Root") {
//
//            public void actionPerformed(ActionEvent e) {
//                treeTable.setRootVisible(!treeTable.isRootVisible());
//                
//            }
//            
//        };
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "Toggle Tree icons ");
//        addAction(frame, toggleRoot);
//        addAction(frame, toggleHandles);
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
                new PatternHighlighter(null, Color.red, "^s",
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
                new PatternFilter( "^d",
                        Pattern.CASE_INSENSITIVE, 0), }));
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "PatternFilter");
        frame.setVisible(true);
    }
    
    /**
     * Issue #??: weird sorting.
     *
     */
    public void interactiveTestSortingFilters() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
        treeTable.setFilters(new FilterPipeline(new Filter[] {
                new ShuttleSorter(1, false), }));
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "SortingFilter");
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
                                "D", 0, 0, 0), }));
        JFrame frame = wrapWithScrollingInFrame(treeTable, "Highlighters");
        frame.setVisible(true);
    }


}
