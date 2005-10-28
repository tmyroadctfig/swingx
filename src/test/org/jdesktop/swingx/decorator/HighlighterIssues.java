/*
 * Created on 14.10.2005
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ListModel;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter.UIAlternateRowHighlighter;


public class HighlighterIssues extends HighlighterTest {

    protected Color ledger = new Color(0xF5, 0xFF, 0xF5);
    
    public static void main(String args[]) {
//        setSystemLF(true);
        HighlighterIssues test = new HighlighterIssues();
        try {
           test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    
    /**
     * AlternateRowHighlighter and background.
     */
    public void interactiveUITableWithAlternateRow() {
        final UIAlternateRowHighlighter highlighter = new UIAlternateRowHighlighter();
        JXTable table = new JXTable(10, 2) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                ComponentAdapter componentAdapter = getComponentAdapter();
                componentAdapter.row = row;
                componentAdapter.column = column;
                return highlighter.highlight(comp, componentAdapter);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                highlighter.updateUI();
                repaint();
            }
            
            
            
        };
        table.setBackground(ledger);
        JXTable nohighlight = new JXTable(10, 2);
        nohighlight.setBackground(ledger);
        JXFrame frame = wrapWithScrollingInFrame(table, nohighlight, "colored table with alternate highlighter");
        frame.setVisible(true);
    }

    /**
     * AlternateRowHighlighter and background.
     */
    public void interactiveColoredTableWithAlternateRow() {
        JXTable table = new JXTable(10, 2) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                ComponentAdapter componentAdapter = getComponentAdapter();
                componentAdapter.row = row;
                componentAdapter.column = column;
                return AlternateRowHighlighter.genericGrey.highlight(comp, componentAdapter);
            }
            
        };
        table.setBackground(ledger);
        JXTable nohighlight = new JXTable(10, 2);
        nohighlight.setBackground(ledger);
        JXFrame frame = wrapWithScrollingInFrame(table, nohighlight, "colored table with alternate highlighter");
        frame.setVisible(true);
    }
    
    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    public void interactiveColoredListWithAlternateRow() {
        JXList list = new JXList(createListModel());
        list.setBackground(ledger);
        HighlighterPipeline pipeline = new HighlighterPipeline();
        pipeline.addHighlighter(AlternateRowHighlighter.genericGrey);
        list.setHighlighters(pipeline);
        JXList nohighlight = new JXList(createListModel());
        nohighlight.setBackground(ledger);
        JXFrame frame = wrapWithScrollingInFrame(list, nohighlight, "colored list with alternate highlighter");
        frame.setVisible(true);
        
    }

    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    public void interactiveColoredTreeWithAlternateRow() {
        JXTree nohighlight = new JXTree();
        nohighlight.setBackground(ledger);
        JXTree list = new JXTree();
        HighlighterPipeline pipeline = new HighlighterPipeline();
        pipeline.addHighlighter(AlternateRowHighlighter.genericGrey);
        list.setHighlighters(pipeline);
        list.setBackground(ledger);
        JXFrame frame = wrapWithScrollingInFrame(list, nohighlight, "colored tree with alternate highlighter");
        frame.setVisible(true);
        
    }
    


    
    
    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    public void testSelectedDoNothingHighlighter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        emptyHighlighter.highlight(allColored, adapter);
        assertEquals("default highlighter must not change foreground", foreground, allColored.getForeground());
        assertEquals("default highlighter must not change background", background, allColored.getBackground());
    }

    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    public void interactiveTableWithDoNothingHighlighter() {
        JXTable table = new JXTable(10, 2) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                ComponentAdapter componentAdapter = getComponentAdapter();
                componentAdapter.row = row;
                componentAdapter.column = column;
                return emptyHighlighter.highlight(comp, componentAdapter);
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, new JXTable(10, 2), "table with empty highlighter");
        frame.setVisible(true);
        
    }
    
    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    public void interactiveListWithDoNothingHighlighter() {
        JXList list = new JXList(createListModel());
        HighlighterPipeline pipeline = new HighlighterPipeline();
        pipeline.addHighlighter(emptyHighlighter);
        list.setHighlighters(pipeline);
        JXFrame frame = wrapWithScrollingInFrame(list, new JXList(createListModel()), "list with empty highlighter");
        frame.setVisible(true);
        
    }

    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    public void interactiveTreeWithDoNothingHighlighter() {
        JXTree list = new JXTree();
        HighlighterPipeline pipeline = new HighlighterPipeline();
        pipeline.addHighlighter(emptyHighlighter);
        list.setHighlighters(pipeline);
        JXFrame frame = wrapWithScrollingInFrame(list, new JXTree(), "list with empty highlighter");
        frame.setVisible(true);
        
    }
    


    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    public void interactiveDoNothingHighlighter() {
        JComponent box = Box.createVerticalBox();
        allColored.setText("unhighlighted");
        allColored.setOpaque(true);
        box.add(allColored);
        JLabel label = new JLabel("highlighted - unselected");
        label.setBackground(allColored.getBackground());
        label.setForeground(allColored.getForeground());
        label.setOpaque(true);
        ComponentAdapter adapter = createComponentAdapter(label, false);
        emptyHighlighter.highlight(label, adapter);
        box.add(label);
        label = new JLabel("highlighted - selected");
        label.setBackground(allColored.getBackground());
        label.setForeground(allColored.getForeground());
        label.setOpaque(true);
        adapter = createComponentAdapter(label, true);
        emptyHighlighter.highlight(label, adapter);
        box.add(label);
        JXFrame frame = wrapInFrame(box, "labels with empty highlighter");
        frame.setVisible(true);
    }

//------------------ helpers
    
    private ListModel createListModel() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 10; i++) {
            model.add(i, i);
        }
        return model;
    }


}
