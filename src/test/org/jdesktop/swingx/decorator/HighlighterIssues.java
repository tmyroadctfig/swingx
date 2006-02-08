/*
 * Created on 14.10.2005
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXEditorPaneTest;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.LinkModel;
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
     * Issue #258-swingx: DefaultTableCellRenderer has memory. 
     * How to formulate as test?
     * this is testing the hack (reset the memory in HighlighterPipeline to null), not
     * any highlighter!
     */
    public void testTableUnSelectedDoNothingHighlighter() {
        JXTable table = new JXTable(10, 2);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setForeground(foreground);
        table.setHighlighters(new HighlighterPipeline(new Highlighter[]{ }));
        Component comp = table.prepareRenderer(renderer, 0, 0);
        assertEquals("do nothing highlighter must not change foreground", foreground, comp.getForeground());
    }

    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    public void interactiveTableUnSelectedDoNothingHighlighter() {
        TableModel model = createTableModelWithLinks();
        JXTable table = new JXTable(model) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                ComponentAdapter componentAdapter = getComponentAdapter();
                componentAdapter.row = row;
                componentAdapter.column = column;
                return emptyHighlighter.highlight(comp, componentAdapter);
            }
            
        };
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setForeground(foreground);
        JXFrame frame = wrapWithScrollingInFrame(table,  
                "table colored renderer with empty highlighter");
        frame.setVisible(true);
        
    }

    private TableModel createTableModelWithLinks() {
        String[] columnNames = { "text only", "Link editable", "Link not-editable", "Bool editable", "Bool not-editable" };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                    return !getColumnName(column).contains("not");
            }
            
        };
        for (int i = 0; i < 4; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a link text " + i, null, url);
                }
                model.addRow(new Object[] {"text only " + i, link, link, Boolean.TRUE, Boolean.TRUE });
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return model;
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
