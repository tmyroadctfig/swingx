/*
 * Created on 25.07.2005
 *
 */
package org.jdesktop.swingx;


import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

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
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.util.ComponentTreeTableModel;

/**
 * @author Jeanette Winzenburg
 */
public class JXTreeTableVisualCheck extends JXTreeTableUnitTest {
    private static final Logger LOG = Logger
            .getLogger(JXTreeTableVisualCheck.class.getName());
    public static void main(String[] args) {
        // NOTE JW: this property has be set "very early" in the application life-cycle
        // it's immutable once read from the UIManager (into a final static field!!)
//        System.setProperty("sun.swing.enableImprovedDragGesture", "true" );
        setSystemLF(true);
        JXTreeTableVisualCheck test = new JXTreeTableVisualCheck();
        try {
//            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Highligh.*");
         //      test.runInteractiveTests("interactive.*SortingFilter.*");
//           test.runInteractiveTests("interactive.*Expand.*");
             test.runInteractiveTests("interactive.*Scroll.*");
        } catch (Exception ex) {

        }
    }

    /**
     * issue #??-swingx: expose scrollPathToVisible in JXTreeTable.
     * 
     * Treetable should behave exactly like Tree - so
     * simply passing through to the hierarchical renderer is not quite
     * enough - need to force a scrollTo after expanding.
     *
     */
    public void interactiveScrollPathToVisible() {
        
        final JXFrame container = new JXFrame();
        final ComponentTreeTableModel model = new ComponentTreeTableModel(container);
        final JXTreeTable table = new JXTreeTable(model);
        final JXTree tree = new JXTree(model);
        Action action = new AbstractAction("path visible") {

            public void actionPerformed(ActionEvent e) {
                TreePath path = model.getPathToRoot(container.getContentPane());
                ((JTree) table.getDefaultRenderer(
                        AbstractTreeTableModel.hierarchicalColumnClass))
                        .scrollPathToVisible(path);
                
                tree.scrollPathToVisible(path);
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, tree, "compare scrollPathtovisible");
        addAction(frame, action);
        frame.setVisible(true);

    }

    /**
     * http://forums.java.net/jive/thread.jspa?threadID=13966&tstart=0
     * adjust hierarchical column width on expansion. The expansion
     * listener looks like doing the job. Important: auto-resize off, 
     * otherwise the table will run out of width to distribute!
     * 
     */
    public void interactiveUpdateWidthOnExpand() {
        
        final JXTreeTable tree = new JXTreeTable(treeTableModel);
        tree.setColumnControlVisible(true);
        JTree renderer = ((JTree) tree.getDefaultRenderer(AbstractTreeTableModel.hierarchicalColumnClass));            
        
        renderer.addTreeExpansionListener(new TreeExpansionListener(){

           public void treeCollapsed(TreeExpansionEvent event) {
           }

           public void treeExpanded(TreeExpansionEvent event) {
              
              final JTree renderer = (JTree)event.getSource();
              
              SwingUtilities.invokeLater(new Runnable(){
                 
                 public void run() {
                    tree.getColumnModel().getColumn(0).setPreferredWidth(renderer.getPreferredSize().width);

                 }
              });            
           }
           
        });
        JXFrame frame = wrapWithScrollingInFrame(tree, "adjust column on expand");
        frame.setVisible(true);

    }
    /**
     * visualize editing of the hierarchical column, both
     * in a tree and a treeTable
     *
     */
    public void interactiveTreeTableModelEditing() {
        final TreeTableModel model = new ComponentTreeTableModel(new JXFrame());
        final JXTreeTable table = new JXTreeTable(model);
        JTree tree =  new JTree(model) {

            @Override
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value instanceof Component) {
                    return ((Component) value).getName();
                }
                return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
            }
            
        };
        tree.setEditable(true);
        final JXFrame frame = wrapWithScrollingInFrame(table, tree, "Editing: compare treetable and tree");
        Action toggleComponentOrientation = new AbstractAction("toggle orientation") {

            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                }

            }

        };
        addAction(frame, toggleComponentOrientation);
        frame.setVisible(true);
        
    }

    /**
     * visualize editing of the hierarchical column, both
     * in a treeTable with a local version of TreeTableCellEditor
     *  and a treeTable with the head version. <p>
     *  
     *  Both are loosing the icon... ehem.
     *
     */
//    public void interactiveTreeTableEditingLocalVsHeadEditor() {
//        final TreeTableModel model = new ComponentTreeTableModel(new JXFrame());
//        final JXTreeTable table = new JXTreeTable(model);
//        final JXTreeTable tableHead = new JXTreeTable(model);
//        tableHead.setDefaultEditor(AbstractTreeTableModel.hierarchicalColumnClass,
//                new TreeTableCellEditorHead(((JXTree) tableHead.getDefaultRenderer(AbstractTreeTableModel.hierarchicalColumnClass))));
//        final JXFrame frame = wrapWithScrollingInFrame(table, tableHead, "Editing: compare treetable local and treetable head");
//        Action toggleComponentOrientation = new AbstractAction("toggle orientation") {
//
//            public void actionPerformed(ActionEvent e) {
//                ComponentOrientation current = frame.getComponentOrientation();
//                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
//                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//                } else {
//                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
//
//                }
//
//            }
//
//        };
//        addAction(frame, toggleComponentOrientation);
//        frame.setVisible(true);
//        
//    }

    /**
     * Issue #248-swingx: update probs with insert into empty model when root
     * not visible.
     * 
     * Looks like a core JTree problem: a collapsed root is not automatically expanded
     * on hiding. Should it? Yes, IMO (JW).
     * 
     * this exposed a slight glitch in JXTreeTable: toggling the initially invisible
     * root to visible did not result in showing the root in the the table. Needed
     * to modify setRootVisible to force a revalidate.
     *   
     */
    public void interactiveTestInsertNodeEmptyModel() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final InsertTreeTableModel model = new InsertTreeTableModel(root, true);
        final JTree tree = new JTree(model);
        tree.setRootVisible(false);
        final JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setColumnControlVisible(true);
        // treetable root invisible by default
        JXFrame frame = wrapWithScrollingInFrame(tree, treeTable, "insert into empty model");
        Action insertAction = new AbstractAction("insert node") {

            public void actionPerformed(ActionEvent e) {
                model.addChild(root);
                
            }
            
        };
        addAction(frame, insertAction);
        Action toggleRoot = new AbstractAction("toggle root") {
            public void actionPerformed(ActionEvent e) {
                boolean rootVisible = !tree.isRootVisible();
                treeTable.setRootVisible(rootVisible);
                tree.setRootVisible(rootVisible);
            }
            
        };
        addAction(frame, toggleRoot);
        frame.setVisible(true);
    }
 

    /**
     * Issue #247-swingx: update probs with insert node.
     * The insert under a collapsed node fires a dataChanged on the table 
     * which results in the usual total "memory" loss (f.i. selection)
     * to reproduce: run example, select root's child in both the tree and the 
     * treetable (left and right view), press the insert button, treetable looses 
     * selection, tree doesn't (the latter is the correct behaviour)
     * 
     * couldn't reproduce the reported loss of expansion state. Hmmm..
     *
     */
    public void interactiveTestInsertUnderCollapsedNode() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final InsertTreeTableModel model = new InsertTreeTableModel(root);
        DefaultMutableTreeNode childA = model.addChild(root);
        final DefaultMutableTreeNode childB = model.addChild(childA);
        model.addChild(childB);
        DefaultMutableTreeNode secondRootChild = model.addChild(root);
        model.addChild(secondRootChild);
        JXTree tree = new JXTree(model);
        final JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setColumnControlVisible(true);
        treeTable.setRootVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(tree, treeTable, "insert problem - root collapsed");
        Action insertAction = new AbstractAction("insert node") {

            public void actionPerformed(ActionEvent e) {
                model.addChild(childB);
           
            }
            
        };
        addAction(frame, insertAction);
        frame.setVisible(true);
    }

    /**
     * Issue #246-swingx: update probs with insert node.
     * 
     * The reported issue is an asymmetry in updating the parent: it's done
     * only if not expanded. With the arguments of #82-swingx, parent's
     * appearance might be effected by child changes if expanded as well.
     * 
     * Here's a test for insert: the crazy renderer removes the icon if 
     * childCount exceeds a limit. Select a node, insert a child, expand the node
     * and keep inserting children. Interestingly the parent is
     * always updated in the treeTable, but not in the tree
     * 
     *
     */
    public void interactiveTestInsertNodeAndChangedParentRendering() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final InsertTreeTableModel model = new InsertTreeTableModel(root);
        final  DefaultMutableTreeNode leaf = model.addChild(root);
        JXTree tree = new JXTree(model);
        final JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setColumnControlVisible(true);
        TreeCellRenderer renderer = new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                        row, hasFocus);
                TreePath path = tree.getPathForRow(row);
                if (path != null) {
                    Object node = path.getLastPathComponent();
                    if ((node != null) && (tree.getModel().getChildCount(node) > 3)) {
                        setIcon(null);
                    }
                }
                return comp;
            }
            
        };
        tree.setCellRenderer(renderer);
        treeTable.setTreeCellRenderer(renderer);
        treeTable.setRootVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(tree, treeTable, "update expanded parent on insert");
        Action insertAction = new AbstractAction("insert node") {

            public void actionPerformed(ActionEvent e) {
                int selected = treeTable.getSelectedRow();
                if (selected < 0 ) return;
                TreePath path = treeTable.getPathForRow(selected);
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) path.getLastPathComponent();
                model.addChild(parent);
                
            }
            
        };
        addAction(frame, insertAction);
        frame.setVisible(true);
    }
 

    /**
     * Issue #224-swingx: TreeTableEditor not bidi compliant.
     *
     * the textfield for editing is at the wrong position in RToL.
     */
    public void interactiveRToLTreeTableEditor() {
        final TreeTableModel model = new ComponentTreeTableModel(new JXFrame());
        final JXTreeTable table = new JXTreeTable(model);
        final JXFrame frame = wrapWithScrollingInFrame(table, "Editor: position follows Component orientation");
        Action toggleComponentOrientation = new AbstractAction("toggle orientation") {

            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                }

            }

        };
        addAction(frame, toggleComponentOrientation);
        frame.setVisible(true);
    }

    /**
     * Issue #223-swingx: Icons lost when editing.
     *  Regression after starting to fix #224-swingx? 
     *  
     *  
     */
    public void interactiveTreeTableEditorIcons() {
        final TreeTableModel model = new ComponentTreeTableModel(new JXFrame());
        final JXTreeTable table = new JXTreeTable(model);
        JXFrame frame = wrapWithScrollingInFrame(table, "Editor: icons showing");
        frame.setVisible(true);
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
        JTree tree = new JTree(model);
        final JXTreeTable treeTable = new JXTreeTable(model);
        JXFrame frame = wrapWithScrollingInFrame(tree, treeTable, "update on insert");
        Action insertAction = new AbstractAction("insert node") {

            public void actionPerformed(ActionEvent e) {
                int selected = treeTable.getSelectedRow();
                if (selected < 0 ) return;
                TreePath path = treeTable.getPathForRow(selected);
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) path.getLastPathComponent();
                model.addChild(parent);
                
            }
            
        };
        addAction(frame, insertAction);
        frame.setVisible(true);
    }
 
    
    
    /**
     * see effect of switching treeTableModel.
     * Problem when toggling back to FileSystemModel: hierarchical 
     * column does not show filenames, need to click into table first.
     * JW: fixed. The issue was updating of the conversionMethod 
     * field - needed to be done before calling super.setModel().
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
    

    /**
     * Issue #168-jdnc: dnd enabled breaks node collapse/expand.
     * 
     * 
     */
    public void interactiveToggleDnDEnabled() {
        final JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setColumnControlVisible(true);
        final JXTree tree = new JXTree(treeTableModel);
        JXFrame frame = wrapWithScrollingInFrame(treeTable, tree, "toggle dragEnabled (starting with false)");
        frame.setVisible(true);
        Action action = new AbstractAction("Toggle dnd") {

            public void actionPerformed(ActionEvent e) {
                
                boolean dragEnabled = !treeTable.getDragEnabled();
                treeTable.setDragEnabled(dragEnabled);
                tree.setDragEnabled(dragEnabled);
               
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
    public void interactiveTestFilterHighlightAndLineStyle() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        // issue #148
        // did not work on LFs which normally respect lineStyle
        // winLF does not respect it anyway...
        treeTable.putClientProperty("JTree.lineStyle", "Angled");
        treeTable.setRowHeight(22);
        treeTable.setRowMargin(1);
       // add a bunch of highlighters directly
        treeTable.addHighlighter(AlternateRowHighlighter.quickSilver);
        treeTable.addHighlighter(new HierarchicalColumnHighlighter());
        treeTable.addHighlighter(new PatternHighlighter(null, Color.red, "^s",
                Pattern.CASE_INSENSITIVE, 0, -1));
        // alternative: set a pipeline containing the bunch of highlighters
//        treeTable.setHighlighters(new HighlighterPipeline(new Highlighter[] {
//                AlternateRowHighlighter.quickSilver,
//                new HierarchicalColumnHighlighter(),
//                new PatternHighlighter(null, Color.red, "^s",
//                        Pattern.CASE_INSENSITIVE, 0, -1), }));
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
    
    
    public void interactiveTestHighlightAndRowHeight() {
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
        treeTable.addHighlighter(AlternateRowHighlighter.classicLinePrinter);
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

    public void interactiveTestHierarchicalColumnHighlight() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.addHighlighter(new HierarchicalColumnHighlighter());
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
        treeTable.addHighlighter(new Highlighter(Color.orange, null));
        treeTable.setIntercellSpacing(new Dimension(15, 15));
        treeTable.setRowHeight(48);
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "Orange, big rowheight");
        frame.setVisible(true);
    }

    public void interactiveTestHighlighters() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setIntercellSpacing(new Dimension(15, 15));
        treeTable.setRowHeight(48);
        // not supported in JXTreeTable
 //       treeTable.setRowHeight(0, 96);
        treeTable.setShowGrid(true);
        // set a bunch of highlighters as a pipeline
        treeTable.setHighlighters(new HighlighterPipeline(
                new Highlighter[] {
                        new Highlighter(Color.orange, null),
                        new HierarchicalColumnHighlighter(),
                        new PatternHighlighter(null, Color.red,
                                "D", 0, 0, 0), 
                        
        
                }));
        Highlighter conditional = new ConditionalHighlighter(Color.BLUE, Color.WHITE, 0, 0) {

            protected boolean test(ComponentAdapter adapter) {
                return adapter.hasFocus();
            }
            
        };
        // add the conditional highlighter later
        treeTable.addHighlighter(conditional);
        JFrame frame = wrapWithScrollingInFrame(treeTable, "Highlighters: conditional, orange, hierarchy, pattern D");
        frame.setVisible(true);
    }


}
