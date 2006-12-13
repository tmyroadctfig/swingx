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
package org.jdesktop.swingx.renderer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.EditorPaneLinkVisitor;
import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXEditorPaneTest;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.LinkModel;
import org.jdesktop.swingx.LinkRenderer;
import org.jdesktop.swingx.action.LinkModelAction;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Visual check of extended Swingx renderers.
 * 
 * @author Jeanette Winzenburg
 */
public class RendererVisualCheck extends InteractiveTestCase {
    public static void main(String[] args) {
        setSystemLF(true);
        RendererVisualCheck test = new RendererVisualCheck();
        try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }


    /**
     * Compare core table using core default renderer vs. swingx default renderer.<p>
     * Unselected background of lead is different for editable/not-editable cells.
     */
    public void interactiveTableCompareFocusedCellBackground() {
        TableModel model = new AncientSwingTeam() {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        
        JTable xtable = new JTable(model);
        xtable.setBackground(Highlighter.notePadBackground.getBackground()); // ledger
        JTable table = new JTable(model);
        table.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        TableCellRenderer renderer = new DefaultTableCellRendererExt();
        table.setDefaultRenderer(Object.class, renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JTable: Unselected focused background: core/ext renderer");
        getStatusBar(frame).add(new JLabel("background for unselected lead: first column is not-editable"));    
        frame.setVisible(true);
    }

    /**
     * Compare xtable using core default renderer vs. swingx default renderer.<p>
     * 
     * Unselected background of lead is different for editable/not-editable cells.
     * With core renderer: can't because Highlighter hack jumps in.
     * 
     */
    public void interactiveXTableCompareFocusedCellBackground() {
        TableModel model = new AncientSwingTeam() {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        
        JXTable xtable = new JXTable(model);
        xtable.setBackground(Highlighter.notePadBackground.getBackground()); // ledger
        JXTable table = new JXTable(model);
        table.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        TableCellRenderer renderer = new DefaultTableCellRendererExt();
        table.setDefaultRenderer(Object.class, renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JXTable: Unselected focused background: core/ext renderer");
        getStatusBar(frame).add(new JLabel("different background for unselected lead: first column is not-editable"));    
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #282-swingx: compare disabled appearance of
     * collection views.
     * Check if extended renderers behave correctly. Still open: header 
     * renderer disabled.
     */
    public void interactiveDisabledCollectionViews() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        table.setDefaultRenderer(Object.class, new DefaultTableCellRendererExt());
        table.setEnabled(false);
        final JXList list = new JXList(new String[] {"one", "two", "and something longer"});
        list.setEnabled(false);
        list.setCellRenderer(new DefaultListCellRendererExt());
        final JXTree tree = new JXTree(new FileSystemModel());
        tree.setEnabled(false);
        JComponent box = Box.createHorizontalBox();
        box.add(new JScrollPane(table));
        box.add(new JScrollPane(list));
        box.add(new JScrollPane(tree));
        JXFrame frame = wrapInFrame(box, "disabled collection views");
        AbstractAction action = new AbstractAction("toggle disabled") {

            public void actionPerformed(ActionEvent e) {
                table.setEnabled(!table.isEnabled());
                list.setEnabled(!list.isEnabled());
                tree.setEnabled(!tree.isEnabled());
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }

    public void interactiveTestTableLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultRenderer(LinkModel.class, new LinkTableCellRendererExt(action, LinkModel.class));
        LinkModelAction action2 = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultEditor(LinkModel.class, new LinkRenderer(action2, LinkModel.class));
        JFrame frame = wrapWithScrollingInFrame(table, visitor.getOutputComponent(), "show link renderer in table");
        frame.setVisible(true);

    }
    
    public void interactiveTestListLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXList list = new JXList(createListModelWithLinks(20));
        list.setRolloverEnabled(true);
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        LinkModelAction action = new LinkModelAction(visitor);
        list.setCellRenderer(new LinkListCellRendererExt(action, LinkModel.class));
        JFrame frame = wrapWithScrollingInFrame(list, visitor.getOutputComponent(), "show link renderer in list");
        frame.setVisible(true);

    }

    private ListModel createListModelWithLinks(int count) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < count; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a link text " + i, null, url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }
    

    private TableModel createModelWithLinks() {
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

    

}
