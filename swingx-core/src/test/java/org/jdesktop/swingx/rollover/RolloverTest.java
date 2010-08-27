/*
 * Created on 06.10.2005
 *
 */
package org.jdesktop.swingx.rollover;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate.AndHighlightPredicate;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class RolloverTest extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RolloverTest.class
            .getName());
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        RolloverTest test = new RolloverTest();
        try {
//            test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Exit.*");
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 

    }

    private TableModel sortableTableModel;
    private Highlighter backgroundHighlighter;
    private Highlighter foregroundHighlighter;
    private ListModel listModel;
    private FileSystemModel treeTableModel;

//---------------------------- interactive tests of rollover effects

    /**
     * Issue #1249-swingx: RolloverProducer clears rollover point when inserting child
     * 
     * Happens f.i. when starting an edit.
     * 
     */
    public void interactiveExitToChild() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                Color.MAGENTA, null, Color.MAGENTA, null));
        JXFrame frame = wrapWithScrollingInFrame(table, "rollover child");
        addStatusMessage(frame, "edit under mouse, move");
        show(frame);
    }
    
    /**
     * Issue #1193-swingx: rollover state not updated on scrolling/mouseWheel
     * 
     * visualize behaviour on 
     * - scrolling (with mouse wheel)
     * - resizing (added custom actions)
     */
    public void interactiveTreeRolloverScroll() {
        final JXTree table = new JXTree(new FileSystemModel());
        table.setCellRenderer(new DefaultTreeRenderer());
        table.setRolloverEnabled(true);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null));
        final JXFrame frame = getResizableFrame(table);
        show(frame);
    }

    /**
     * Issue #1193-swingx: rollover state not updated on scrolling/mouseWheel
     * 
     * visualize behaviour on 
     * - scrolling (with mouse wheel)
     * - resizing (added custom actions)
     */
    public void interactiveListRolloverScroll() {
        final JXList table = new JXList(AncientSwingTeam.createNamedColorListModel());
        table.setRolloverEnabled(true);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null));
        final JXFrame frame = getResizableFrame(table);
        show(frame);
    }
    
    /**
     * Issue #1193-swingx: rollover state not updated on scrolling/mouseWheel
     * 
     * visualize behaviour on 
     * - scrolling (with mouse wheel)
     * - resizing (added custom actions)
     */
    public void interactiveTableRolloverScroll() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        table.setEditable(false);
        table.setHorizontalScrollEnabled(true);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null));
        final JXFrame frame = getResizableFrame(table);
        show(frame);
    }



    /**
     * @param table
     * @return
     */
    private JXFrame getResizableFrame(final JComponent table) {
        final JXFrame frame = wrapWithScrollingInFrame(table, "rollover and wheel");
        Action hd = new AbstractAction("horizontalDecrease") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension dim = frame.getSize();
                dim.width -= 50;
                frame.setSize(dim);
            }
            
        };
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke("A"), "horizontalDecrease");
        table.getActionMap().put("horizontalDecrease", hd);
        Action hi = new AbstractAction("horizontalDecrease") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension dim = frame.getSize();
                dim.width += 50;
                frame.setSize(dim);
            }
            
        };
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke("D"), "horizontalIncrease");
        table.getActionMap().put("horizontalIncrease", hi);
        Action vd = new AbstractAction("verticalDecrease") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension dim = frame.getSize();
                dim.height -= 20;
                frame.setSize(dim);
            }
            
        };
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke("W"), "verticalDecrease");
        table.getActionMap().put("verticalDecrease", vd);
        Action vi = new AbstractAction("verticalIncrease") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension dim = frame.getSize();
                dim.height += 20;
                frame.setSize(dim);
            }
            
        };
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke("S"), "verticalIncrease");
        table.getActionMap().put("verticalIncrease", vi);
        addStatusComponent(frame, new JLabel("Horizontal Resize: A <--> D "));
        addStatusComponent(frame, new JLabel("Vertical Resize: W <--> S "));
        return frame;
    }


    
    public void interactiveTableRollover() {
        JXTable table = new JXTable(sortableTableModel);
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "Table with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    
    
    public void interactiveListRollover() {
        final JXList table = new JXList(listModel);
        table.setRolloverEnabled(true);
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "List with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    
    public void interactiveTreeRollover() {
        final JXTree table = new JXTree(treeTableModel);
        table.setRolloverEnabled(true);
        table.setComponentPopupMenu(createPopup());
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JTree tree = new JTree(treeTableModel);
        tree.setComponentPopupMenu(createPopup());
        JXFrame frame = wrapWithScrollingInFrame(table, tree, "JXTree (at left) with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        addMessage(frame, "background highlight not working in JXTree");
        frame.setVisible(true);
    }

    public JPopupMenu createPopup() {
        JPopupMenu popup = new JPopupMenu();
        popup.add("dummy");
        return popup;
    }
    
    public void interactiveTreeTableRollover() {
        final JXTreeTable table = new JXTreeTable(treeTableModel);
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "TreeTable with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /**
     * Example for per-cell rollover decoration in JXTreeTable.
     */
    public void interactiveTreeTableRolloverHierarchical() {
        final JXTreeTable table = new JXTreeTable(treeTableModel);
        HighlightPredicate andPredicate = new AndHighlightPredicate(
                new HighlightPredicate.ColumnHighlightPredicate(0),
                HighlightPredicate.ROLLOVER_ROW
                );
        final Highlighter foregroundHighlighter = new ColorHighlighter(andPredicate, null,
                Color.MAGENTA);
        final Highlighter backgroundHighlighter = new ColorHighlighter(andPredicate, Color.YELLOW,
                null);
        table.setHighlighters(foregroundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "TreeTable with rollover - effect hierarchical column");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    table.setHighlighters(foregroundHighlighter);
                } else {
                    table.setHighlighters(backgroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    
//---------------------- unit tests
    
    /**
     * Issue #1193-swingx: fix rollover mouse to cell mapping on scrolling/resizing.
     *  
     */
    @Test
    public void testTableRolloverProducerComponentListener() {
        JXTable table = new JXTable();
        assertComponentListener(table, true);
        table.setRolloverEnabled(false);
        assertComponentListener(table, false);
    }
    /**
     * Issue #1193-swingx: fix rollover mouse to cell mapping on scrolling/resizing.
     *  
     */
    @Test
    public void testTreeRolloverProducerComponentListener() {
        JXTree table = new JXTree();
        assertComponentListener(table, false);
        table.setRolloverEnabled(true);
        assertComponentListener(table, true);
    }
    
    /**
     * Issue #1193-swingx: fix rollover mouse to cell mapping on scrolling/resizing.
     *  
     */
    @Test
    public void testListRolloverProducerComponentListener() {
        JXList table = new JXList();
        assertComponentListener(table, false);
        table.setRolloverEnabled(true);
        assertComponentListener(table, true);
    }
    

    /**
     * @param table
     */
    private void assertComponentListener(JComponent table, boolean expected) {
        TestUtils.assertContainsType(table.getComponentListeners(), 
                RolloverProducer.class, expected ? 1 : 0);
    }
    

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sortableTableModel = new AncientSwingTeam();
        listModel = new AbstractListModel() {

            public int getSize() {
                return sortableTableModel.getRowCount();
            }

            public Object getElementAt(int index) {
                return sortableTableModel.getValueAt(index, 0);
            }
            
        };
        treeTableModel = new FileSystemModel();
        foregroundHighlighter = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null,
                Color.MAGENTA);
        backgroundHighlighter = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW,
                null);
     }
    

    @Test
    public void testXDummy() {
        
    }

}
