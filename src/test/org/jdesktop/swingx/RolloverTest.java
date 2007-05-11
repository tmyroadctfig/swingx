/*
 * Created on 06.10.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.RolloverHighlighter;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;

public class RolloverTest extends InteractiveTestCase {

    
    /**
     * @param args
     */
    public static void main(String[] args) {
        RolloverTest test = new RolloverTest();
        try {
            test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Rend.*");
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 

    }

    private TableModel sortableTableModel;
    private RolloverHighlighter backgroundHighlighter;
    private RolloverHighlighter foregroundHighlighter;
    private ListModel listModel;
    private FileSystemModel treeTableModel;

    public void testDummy() {
        
    }

//---------------------------- interactive tests of rollover effects
    
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
        table.setRolloverEnabled(true);
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
        foregroundHighlighter = new RolloverHighlighter(null, Color.MAGENTA);
        backgroundHighlighter = new RolloverHighlighter(Color.YELLOW, null);
     }
    

}
