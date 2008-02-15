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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

import org.jdesktop.swingx.EditorPaneLinkVisitor;
import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXEditorPaneTest;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.LinkModel;
import org.jdesktop.swingx.LinkRenderer;
import org.jdesktop.swingx.RolloverRenderer;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.LinkModelAction;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
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
//            test.runInteractiveTests();
//          test.runInteractiveTests(".*Text.*");
//          test.runInteractiveTests(".*XLabel.*");
//          test.runInteractiveTests(".*Color.*");
          test.runInteractiveTests("interactive.*Fancy.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    public void interactiveTreeFancyButton() {
        JXTree tree = new JXTree();
        MattePainter painter = new MattePainter(Color.YELLOW);
        Highlighter hl = new PainterHighlighter(HighlightPredicate.ROLLOVER_ROW, painter);
        tree.addHighlighter(hl);
        ComponentProvider provider = new NormalButtonProvider(StringValue.TO_STRING, JLabel.LEADING);
        tree.setCellRenderer(new DefaultTreeRenderer(provider));
        tree.setRolloverEnabled(true);
        showWithScrollingInFrame(tree, "Fancy..");
    }
    
    public void interactiveFancyButton() {
        JXButton button = new JXButton("Dummy .... but lonnnnnnngg");
        button.setBorder(BorderFactory.createCompoundBorder(new DropShadowBorder(), button.getBorder()));
         showInFrame(button, "Fancy..");
    }
    public static class NormalButtonProvider extends ButtonProvider {

        private Border border;

        /**
         * @param toString
         * @param leading
         */
        public NormalButtonProvider(StringValue toString, int leading) {
            super(toString, leading);
            setBorderPainted(true);
        }

        
        @Override
        protected void configureState(CellContext context) {
            super.configureState(context);
            rendererComponent.setBorder(border);
        }


        @Override
        protected AbstractButton createRendererComponent() {
            JXButton button = new JXButton();
            border = BorderFactory.createCompoundBorder(
                    new DropShadowBorder(),
                    button.getBorder());
            return button;
        }
        
        
        
    }
    /**
     * example to configure treeTable hierarchical column (same for tree) with
     * custom icon and content mapping.
     * 
     */
    public void interactiveTreeTableCustomIcons() {
        TreeTableModel model = new FileSystemModel();
        JXTreeTable table = new JXTreeTable(model);
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof File) {
                    return FileSystemView.getFileSystemView().getSystemDisplayName((File) value); 
                } 
                return TO_STRING.getString(value);
            }
            
        };
        IconValue iv = new IconValue() {

            public Icon getIcon(Object value) {
                if (value instanceof File) {
                    return  FileSystemView.getFileSystemView().getSystemIcon((File) value);
                } 
                return null;
            }};
        table.setTreeCellRenderer(new DefaultTreeRenderer(iv, sv));
        showWithScrollingInFrame(table, "Tree/Table: wrapping provider with custom");
        
    }
    
    /**
     * Use formatting from sql date/time classes.
     *
     */
    public void interactiveTableSQLDateTime() {
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        Timestamp stamp = new Timestamp(date.getTime());
        Time time = new Time(date.getTime());
        DefaultTableModel model = new DefaultTableModel(1, 5) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() > 0) {
                    Object value = getValueAt(0, columnIndex);
                    if (value != null) {
                        return value.getClass();
                    }
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        model.setColumnIdentifiers(new Object[]{"Date - normal", "SQL Date", "SQL Timestamp", "SQL Time", "Date - as time"});
        model.setValueAt(date, 0, 0);
        model.setValueAt(sqlDate, 0, 1);
        model.setValueAt(stamp, 0, 2);
        model.setValueAt(time, 0, 3);
        model.setValueAt(date, 0, 4);
        JXTable table = new JXTable(model);
        // right align to see the difference to normal date renderer
        DefaultTableRenderer renderer = new DefaultTableRenderer(
                new LabelProvider(SwingConstants.RIGHT));
        table.setDefaultRenderer(Timestamp.class, renderer);
        table.setDefaultRenderer(Time.class, renderer);
        table.setDefaultRenderer(java.sql.Date.class, renderer);
        // format the given Date as short time
        table.getColumnExt(4).setCellRenderer(new DefaultTableRenderer(
                new FormatStringValue(DateFormat.getTimeInstance(DateFormat.SHORT))));
        showWithScrollingInFrame(table, "normal/sql date formatting"); 
    }
    
    /**
     * Quick example of using a JTextArea as rendering component.
     *
     */
    public void interactiveTextAreaRenderer() {
        DefaultTableModel model = new DefaultTableModel(0, 1);
        model.addRow(new String[] {"some really, maybe really really long text -  "
                + "wrappit .... where needed "});
        model.addRow(new String[] {"another really, maybe really really long text -  "
                + "with nothing but junk. wrappit .... where needed"});
        JXTable table = new JXTable(model);
        table.setVisibleRowCount(4);
        table.setVisibleColumnCount(2);
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setCellRenderer(new DefaultTableRenderer(new TextAreaProvider()));
        table.addHighlighter(
                HighlighterFactory.createAlternateStriping());
        // simulate some means to get a reasonable rowheight
        table.packColumn(0, -1);
        JTextArea textArea = (JTextArea) table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        table.setRowHeight(textArea.getPreferredSize().height);
        showWithScrollingInFrame(table, "textArea as rendering comp");
    }
    
    /**
     * use a JTextArea as rendering component.
     */
    public static class TextAreaProvider extends ComponentProvider<JTextArea> {

        @Override
        protected void configureState(CellContext context) {
        }

        @Override
        protected JTextArea createRendererComponent() {
            JTextArea area = new JTextArea(3, 20);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setOpaque(true);
            return area;
        }

        @Override
        protected void format(CellContext context) {
            rendererComponent.setText(getValueAsString(context));
        }
        
    }

    /**
     * Quick example of using a JXLabel as rendering component.
     * Looks funny .. wrapping jumps?
     */
    public void interactiveXLabelRenderer() {
        DefaultTableModel model = new DefaultTableModel(0, 1);
        model.addRow(new String[] {"some really, maybe really really long text -  "
                + "wrappit .... where needed "});
        model.addRow(new String[] {"another really, maybe really really long text -  "
                + "with nothing but junk. wrappit .... where needed"});
        JXTable table = new JXTable(model);
        table.setVisibleRowCount(4);
        table.setVisibleColumnCount(2);
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setCellRenderer(new DefaultTableRenderer(new XLabelProvider()));
        table.addHighlighter(
                HighlighterFactory.createAlternateStriping());
        table.setRowHeight(50);
        showWithScrollingInFrame(table, "textArea as rendering comp");
    }

    /**
     * Use a JXLabel as rendering component.
     */
    public static class XLabelProvider extends ComponentProvider<JXLabel> {

        @Override
        protected void configureState(CellContext context) {
        }

        @Override
        protected JXLabel createRendererComponent() {
            JXLabel label = new JXLabel();
            label.setOpaque(true);
            label.setLineWrap(true);
            return label;
        }

        @Override
        protected void format(CellContext context) {
            rendererComponent.setText(getValueAsString(context));
        }
        
    }
    /**
     * Check disabled appearance.
     *
     */
    public void interactiveListDisabledIconRenderer() {
        final TableModel model = createTableModelWithDefaultTypes();
        // 
        ListModel listModel = new AbstractListModel() {

            public Object getElementAt(int index) {
                if (index == 0) {
                    return "dummy";
                }
                return model.getValueAt(index - 1, 4);
            }

            public int getSize() {
                return model.getRowCount() + 1;
            }
            
        };
        final JList standard = new JList(listModel);
        final JList enhanced = new JList(listModel);
        enhanced.setCellRenderer(new DefaultListRenderer());

        AbstractAction action = new AbstractAction("toggle disabled") {

            public void actionPerformed(ActionEvent e) {
                standard.setEnabled(!standard.isEnabled());
                enhanced.setEnabled(!enhanced.isEnabled());
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(standard, enhanced, "Disabled - compare renderers: default <--> enhanced");
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    /**
     * Check disabled appearance for all renderers.
     *
     */
    public void interactiveTableDefaultRenderers() {
        TableModel model = createTableModelWithDefaultTypes();
        final JTable standard = new JTable(model);
        // used as source for swingx renderers
        final JXTable xTable = new JXTable();
        final JTable enhanced = new JTable(model) {

            @Override
            protected void createDefaultRenderers() {
                defaultRenderersByColumnClass = new UIDefaults();
                setDefaultRenderer(Object.class, xTable.getDefaultRenderer(Object.class));
                setDefaultRenderer(Number.class, xTable.getDefaultRenderer(Number.class));
                setDefaultRenderer(Date.class, xTable.getDefaultRenderer(Date.class));
                setDefaultRenderer(Icon.class, xTable.getDefaultRenderer(Icon.class));
                setDefaultRenderer(ImageIcon.class, xTable.getDefaultRenderer(ImageIcon.class));
                setDefaultRenderer(Boolean.class, xTable.getDefaultRenderer(Boolean.class));
            }
            
        };
        AbstractAction action = new AbstractAction("toggle disabled") {

            public void actionPerformed(ActionEvent e) {
                standard.setEnabled(!standard.isEnabled());
                enhanced.setEnabled(!enhanced.isEnabled());
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(standard, enhanced, "Disabled Compare renderers: default <--> enhanced");
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    /**
     * @return
     */
    private TableModel createTableModelWithDefaultTypes() {
        String[] names = {"Object", "Number", "Double", "Date", "ImageIcon", "Boolean"};
        final Class[] types = {Object.class, Number.class, Double.class, Date.class, ImageIcon.class, Boolean.class};
        DefaultTableModel model = new DefaultTableModel(names, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
            
        };
        Date today = new Date();
        Icon icon = XTestUtils.loadDefaultIcon();
        for (int i = 0; i < 10; i++) {
            Object[] values = new Object[] {"row " + i, i, Math.random() * 100, 
                    new Date(today.getTime() + i * 1000000), icon, i % 2 == 0};
            model.addRow(values);
        }
        return model;
    }

    /**
     * Compare core table using core default renderer vs. swingx default renderer.<p>
     * Unselected background of lead is different for editable/not-editable cells.
     */
    @SuppressWarnings("deprecation")
    public void interactiveTableCompareFocusedCellBackground() {
        TableModel model = new AncientSwingTeam() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        
        JTable xtable = new JTable(model);
        xtable.setBackground(HighlighterFactory.NOTEPAD); // ledger
        JTable table = new JTable(model);
        table.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        TableCellRenderer renderer = new DefaultTableRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JTable: Unselected focused background: core/ext renderer");
        getStatusBar(frame).add(new JLabel("background for unselected lead: first column is not-editable"));    
        frame.setVisible(true);
    }

    /**
     * Compare xtable using core default renderer vs. swingx default renderer.<p>
     * Obsolete - swingx renderers registered by default.
     * 
     * Unselected background of lead is different for editable/not-editable cells.
     * With core renderer: can't because LegacyHighlighter hack jumps in.
     * 
     */
    public void interactiveXTableCompareFocusedCellBackground() {
//        TableModel model = new AncientSwingTeam() {
//            public boolean isCellEditable(int row, int column) {
//                return column != 0;
//            }
//        };
//        
//        JXTable xtable = new JXTable(model);
//        xtable.setBackground(LegacyHighlighter.notePadBackground.getBackground()); // ledger
//        JXTable table = new JXTable(model) {
//
//            @Override
//            protected void createDefaultRenderers() {
//                defaultRenderersByColumnClass = new UIDefaults();
//                setDefaultRenderer(Object.class, new DefaultTableRenderer());
//                LabelProvider controller = new LabelProvider(FormatStringValue.NUMBER_TO_STRING);
//                controller.setHorizontalAlignment(JLabel.RIGHT);
//                setDefaultRenderer(Number.class, new DefaultTableRenderer(controller));
//                setDefaultRenderer(Date.class, new DefaultTableRenderer(
//                        FormatStringValue.DATE_TO_STRING));
//                TableCellRenderer renderer  = new DefaultTableRenderer(new LabelProvider(JLabel.CENTER));
//                setDefaultRenderer(Icon.class, renderer);
//                setDefaultRenderer(ImageIcon.class, renderer);
//                setDefaultRenderer(Boolean.class, new DefaultTableRenderer(new ButtonProvider()));
//            }
//
//        };
//        table.setBackground(xtable.getBackground()); // ledger
//        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JXTable: Unselected focused background: core/ext renderer");
//        getStatusBar(frame).add(new JLabel("different background for unselected lead: first column is not-editable"));    
//        frame.pack();
//        frame.setVisible(true);
    }

    /**
     * Issue #282-swingx: compare disabled appearance of
     * collection views.
     * Check if extended renderers behave correctly. Still open: header 
     * renderer disabled.
     */
    public void interactiveDisabledCollectionViews() {
        final JXTable table = new JXTable(new AncientSwingTeam());
//        table.setDefaultRenderer(Object.class, new DefaultTableRenderer());
        table.setEnabled(false);
        final JXList list = new JXList(new String[] {"one", "two", "and something longer"});
        list.setEnabled(false);
//        list.setCellRenderer(new DefaultListRenderer());
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

    /**
     * 
     * Example for custom StringValue: bound to bean property.
     * 
     * A column of xtable and the xlist share the same component controller.<p>
     * 
     *  
     */
    public void interactiveTableAndListCustomRenderer() {
        final ListModel players = createPlayerModel();
        TableModel tableModel = new AbstractTableModel() {
            String[] columnNames = {"Name", "Score", "Player.toString"};
            public int getColumnCount() {
                return 3;
            }

            public int getRowCount() {
                return players.getSize();
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                return players.getElementAt(rowIndex);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Player.class;
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }
            
            
        };
        JXTable xtable = new JXTable(tableModel);
        PropertyStringValue converter = new PropertyStringValue("name");
        LabelProvider nameController = new LabelProvider(converter);
        xtable.getColumn(0).setCellRenderer(new DefaultTableRenderer(nameController));
        PropertyStringValue scoreConverter = new PropertyStringValue("score");
        xtable.getColumn(1).setCellRenderer(new DefaultTableRenderer(scoreConverter));
        xtable.packAll();
        JXList list = new JXList(players);
        // we share the component controller between table and list
        list.setCellRenderer(new DefaultListRenderer(nameController));
        showWithScrollingInFrame(xtable, list, "JXTable/JXList: Custom property renderer");

    }

  /**
  * Simple example to bind a toStringConverter to a single property of the value.
  */
    public static class PropertyStringValue implements StringValue {
        private String property;

        public PropertyStringValue(String property) {
            this.property = property;
        }

        /**
         * {@inheritDoc} <p>
         * Implemented to return the toString of the named property value.
         */
        public String getString(Object value) {
            try {
                PropertyDescriptor desc = getPropertyDescriptor(value.getClass(), property);
                return TO_STRING.getString(getValue(value, desc));
            } catch (Exception e) {
                // nothing much we can do here...
                
            }
            return "";
        }
        
    }
    
    /**
     * Use custom converter for Dimension/Point (from demo)
     *
     */
    public void interactiveTableCustomRenderer() {
        JXTable table = new JXTable();
        StringValue converter = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Point) {
                    Point p = (Point) value;
                    return createString(p.x, p.y);
                } else if (value instanceof Dimension) {
                    Dimension dim = (Dimension) value;
                    return createString(dim.width, dim.height);
                }
               return "";
            }

            private String createString(int width, int height) {
                return "(" + width + ", " + height + ")";
            }
            
        };
        TableCellRenderer renderer = new DefaultTableRenderer(converter);
        table.setDefaultRenderer(Point.class, renderer);
        table.setDefaultRenderer(Dimension.class, renderer);
        JXFrame frame = showWithScrollingInFrame(table, "custom renderer (from demo) for Point/Dimension");
        ComponentTreeTableModel model = new ComponentTreeTableModel(frame);
        JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.expandAll();
        table.setModel(treeTable.getModel());
        
    }
    
//---------------- hyperlink rendering    
    /**
     * extended link renderer in table.
     *
     */
    public void interactiveTestTableLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        ComponentProvider<JXHyperlink> controller = new HyperlinkProvider(action, LinkModel.class);
        table.setDefaultRenderer(LinkModel.class, new DefaultTableRenderer(controller));
        LinkModelAction action2 = new LinkModelAction<LinkModel>(visitor);
        // TODO: obsolete - need to think about what to do with editable link cells?
        table.setDefaultEditor(LinkModel.class, new LinkRenderer(action2, LinkModel.class));
        JFrame frame = wrapWithScrollingInFrame(table, visitor.getOutputComponent(), "show link renderer in table");
        frame.setVisible(true);

    }
    
    /**
     * extended link renderer in list.
     *
     */
    public void interactiveTestListLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXList list = new JXList(createListModelWithLinks(20));
        list.setRolloverEnabled(true);
        LinkModelAction action = new LinkModelAction(visitor);
        ComponentProvider<JXHyperlink> context = new HyperlinkProvider(action, LinkModel.class);
        list.setCellRenderer(new DefaultListRenderer(context));
        JFrame frame = wrapWithScrollingInFrame(list, visitor.getOutputComponent(), "show link renderer in list");
        frame.setVisible(true);

    }

    /**
     * extended link renderer in tree.
     *
     */
    public void interactiveTestTreeLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTree tree = new JXTree(createTreeModelWithLinks(20));
        tree.setRolloverEnabled(true);
        LinkModelAction action = new LinkModelAction(visitor);
        ComponentProvider<JXHyperlink> context = new HyperlinkProvider(action, LinkModel.class);
        tree.setCellRenderer(new DefaultTreeRenderer(new WrappingProvider(context)));
        JFrame frame = wrapWithScrollingInFrame(tree, visitor.getOutputComponent(), "show link renderer in list");
        frame.setVisible(true);
    }
    
//----------------------- experiments with "CheckList" fakes
    
    /**
     * Use a custom button controller to show both checkbox icon and text to
     * render Actions in a JXList.
     */
    public void interactiveTableWithRolloverListColumnControl() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        JXList list = new JXList();
        // quick-fill and hook to table columns' visibility state
        configureList(list, table, true);
        // a custom rendering button controller showing both checkbox and text
        ButtonProvider wrapper = new RolloverRenderingButtonController();
        list.setCellRenderer(new DefaultListRenderer(wrapper));
        JXFrame frame = showWithScrollingInFrame(table, list,
                "rollover checkbox list-renderer");
        addStatusMessage(frame, "fake editable list: use rollover renderer - not really working");
        frame.pack();
    }


    /**
     * 
     * Naive implementation: toggle the selected status of the last
     * stored action. Doesn't work reliably.... requires to leave the
     * item before the next click updates the action.
     * 
     */
    public static class RolloverRenderingButtonController extends ButtonProvider
       implements RolloverRenderer {

        private AbstractActionExt actionExt;

        public RolloverRenderingButtonController() {
            setHorizontalAlignment(JLabel.LEADING);
        }
        
        @Override
        protected void format(CellContext context) {
            if (!(context.getValue() instanceof AbstractActionExt)) {
                super.format(context);
                return;
            }
            
            actionExt = (AbstractActionExt) context.getValue();
            rendererComponent.setAction(actionExt);
            rendererComponent.setSelected(actionExt.isSelected());
//            rendererComponent.setText(((AbstractActionExt) context.getValue()).getName());
        }

        public void doClick() {
            boolean selected = !actionExt.isSelected();
                        rendererComponent.doClick(0);
//            actionExt.setSelected(rendererComponent.isSelected());
            actionExt.setSelected(selected);
//            rendererComponent.setSelected(selected);
            
        }

        public boolean isEnabled() {
            // TODO Auto-generated method stub
            return true;
        }
        
    }
    
    /**
     * Use a custom button controller to show both checkbox icon and text to
     * render Actions in a JXList.
     */
    public void interactiveTableWithListColumnControl() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        JXList list = new JXList(); 
        Highlighter highlighter = HighlighterFactory.createSimpleStriping();
        table.addHighlighter(highlighter);
        list.addHighlighter(highlighter);
        // quick-fill and hook to table columns' visibility state
        configureList(list, table, false);
        // a custom rendering button controller showing both checkbox and text
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof AbstractActionExt) {
                    return ((AbstractActionExt) value).getName();
                }
                return "";
            }
            
        };
        BooleanValue bv = new BooleanValue() {

            public boolean getBoolean(Object value) {
                if (value instanceof AbstractActionExt) {
                    return ((AbstractActionExt) value).isSelected();
                }
                return false;
            }
            
        };
        ComponentProvider wrapper = new ButtonProvider(new MappedValue(sv, null, bv), JLabel.LEADING); 
        list.setCellRenderer(new DefaultListRenderer(wrapper));
        JXFrame frame = showWithScrollingInFrame(table, list,
                "checkbox list-renderer");
        addStatusMessage(frame, "fake editable list: space/doubleclick on selected item toggles column visibility");
        frame.pack();
    }

    /**
     * Fills the list with a collection of actions (as returned from the 
     * table's column control). Binds space and double-click to toggle
     * the action's selected state.
     * 
     * note: this is just an example to show-off the button renderer in a list!
     * ... it's very dirty!!
     * 
     * @param list
     * @param table
     */
    private void configureList(final JXList list, final JXTable table, boolean useRollover) {
        final List<Action> actions = new ArrayList<Action>();
        // quick and dirty access to column visibility actions
        @SuppressWarnings("all")
        ColumnControlButton columnControl = new ColumnControlButton(table, null) {

            @Override
            protected void addVisibilityActionItems() {
                actions.addAll(Collections
                        .unmodifiableList(getColumnVisibilityActions()));
            }

        };
        list.setModel(createListeningListModel(actions));
        // action toggling selected state of selected list item
        final Action toggleSelected = new AbstractActionExt(
                "toggle column visibility") {

            public void actionPerformed(ActionEvent e) {
                if (list.isSelectionEmpty())
                    return;
                AbstractActionExt selectedItem = (AbstractActionExt) list
                        .getSelectedValue();
                selectedItem.setSelected(!selectedItem.isSelected());
            }

        };
        if (useRollover) {
            list.setRolloverEnabled(true);
        } else {
            // bind action to space
            list.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),
                    "toggleSelectedActionState");
        }
        list.getActionMap().put("toggleSelectedActionState", toggleSelected);
        // bind action to double-click
        MouseAdapter adapter = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleSelected.actionPerformed(null);
                }
            }

        };
        list.addMouseListener(adapter);

    }
    /**
     * Creates and returns a ListModel containing the given actions. 
     * Registers a PropertyChangeListener with each action to get
     * notified and fire ListEvents.
     * 
     * @param actions the actions to add into the model.
     * @return the filled model.
     */
    private ListModel createListeningListModel(final List<Action> actions) {
        final DefaultListModel model = new DefaultListModel() {

            DefaultListModel reallyThis = this;
            @Override
            public void addElement(Object obj) {
                super.addElement(obj);
                ((Action) obj).addPropertyChangeListener(l);
                
            }
            
            PropertyChangeListener l = new PropertyChangeListener() {
                
                public void propertyChange(PropertyChangeEvent evt) {
                    int index = indexOf(evt.getSource());
                    if (index >= 0) {
                        fireContentsChanged(reallyThis, index, index);
                    }
                }
                
            };
        };
        for (Action action : actions) {
            model.addElement(action);
        }
        return model;
    }

//--------------- experiments with color renderer (content directly related to visuals)

    /**
     * Compare xtable using custom color renderer - standard vs. ext.<p>
     * 
     */
    public void interactiveTableCustomColorRenderer() {
        TableModel model = new AncientSwingTeam();
        JXTable xtable = new JXTable(model);
        xtable.setDefaultRenderer(Color.class, new ColorRenderer(true));
        JXTable table = new JXTable(model);
        TableCellRenderer renderer = createColorRendererExt();
        table.setDefaultRenderer(Color.class, renderer);
        showWithScrollingInFrame(xtable, table, "JXTable: Custom color renderer - standard/ext");
    }

    /**
     * Compare xtable using custom color renderer - standard vs. ext.<p>
     * Adds highlighter ... running amok.
     */
    public void interactiveTableCustomColorRendererWithHighlighter() {
        TableModel model = new AncientSwingTeam();
        JXTable xtable = new JXTable(model);
        xtable.addHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));
        xtable.setDefaultRenderer(Color.class, new ColorRenderer(true));
        JXTable table = new JXTable(model);
        table.addHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));
        TableCellRenderer renderer = createColorRendererExt();
        table.setDefaultRenderer(Color.class, renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JXTable/highlighter: Custom color renderer - standard/ext");
        addStatusMessage(frame, "Highlighter hide custom color renderer background for unselected");
        frame.setVisible(true);
    }

    /**
     * Compare xtable using custom color renderer - standard vs. ext.<p>
     * Adds highlighter which respects renderer's dont touch.
     */
    public void interactiveTableCustomColorRendererWithHighlighterDontTouch() {
        TableModel model = new AncientSwingTeam();
        JXTable xtable = new JXTable(model);
        Highlighter highlighter = createPropertyRespectingHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));

        xtable.addHighlighter(highlighter);
        xtable.setDefaultRenderer(Color.class, new ColorRenderer(true));
        JXTable table = new JXTable(model);
        table.addHighlighter(highlighter);
        TableCellRenderer renderer = createColorRendererExt();
        table.setDefaultRenderer(Color.class, renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JXTable/highlighter dont-touch: Custom color renderer - standard/ext");
        addStatusMessage(frame, "Highlighter doesn't touch custom color renderer visual properties");
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates and returns a LegacyHighlighter which does nothing if the 
     * rendererComponent has the dont-touch property set. Otherwise
     * delegates highlighting to the delegate.
     * 
     * @param delegate
     * @return
     */
    private Highlighter createPropertyRespectingHighlighter(final Highlighter delegate) {
        HighlightPredicate predicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                return ((JComponent) renderer).getClientProperty("renderer-dont-touch") == null;
            }
            
        };
        
        Highlighter highlighter = new AbstractHighlighter(predicate) {

            @Override
            public Component doHighlight(Component renderer, ComponentAdapter adapter) {
                return delegate.highlight(renderer, adapter);
            }
            
        };
        return highlighter;
    }



    /**
     * xtable/xlist using the same custom component controller.<p>
     * 
     */
    public void interactiveTableAndListCustomColorRenderingController() {
        TableModel tableModel = new AncientSwingTeam();
        ComponentProvider<JLabel> controller = createColorRenderingLabelController();
        JXTable xtable = new JXTable(tableModel);
        xtable.setDefaultRenderer(Color.class, new DefaultTableRenderer(controller));
        ListModel model = createListColorModel();
        JXList list = new JXList(model);
        ListCellRenderer renderer = new DefaultListRenderer(controller);
        list.setCellRenderer(renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, list, "JXTable/JXList: Custom color renderer - sharing the component controller");
        addMessage(frame, "share provider in normally in different comps is okay?");
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * xtable/xlist using the same custom component controller.<p>
     * 
     */
    public void interactiveTableAndTreeCustomColorRenderingController() {
        TableModel tableModel = new AncientSwingTeam();
        ComponentProvider<JLabel> controller = createColorRenderingLabelController();
        JXTable xtable = new JXTable(tableModel);
        xtable.setDefaultRenderer(Color.class, new DefaultTableRenderer(controller));
        TreeModel model = createTreeColorModel();
        JTree tree = new JTree(model);
        ComponentProvider wrapper = new WrappingProvider(controller); //;createColorRenderingLabelController());
        TreeCellRenderer renderer = new DefaultTreeRenderer(wrapper);
        tree.setCellRenderer(renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, tree, "JXTable/JXTree: Custom color renderer - sharing the component controller");
        addMessage(frame, "share provider in table and in wrappingProvider does not work?");
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * creates and returns a color ext renderer.
     * @return
     */
    protected TableCellRenderer createColorRendererExt() {
        ComponentProvider<JLabel> context = createColorRenderingLabelController();
        TableCellRenderer renderer = new DefaultTableRenderer(context);
        return renderer;
    }

    /**
     * creates and returns a color ext renderer.
     * @return
     */
    protected ListCellRenderer createListColorRendererExt() {
        ComponentProvider<JLabel> context = createColorRenderingLabelController();
        ListCellRenderer renderer = new DefaultListRenderer(context);
        return renderer;
    }

    /**
     * Creates and returns a component controller specialized on Color values. <p>
     * Note: this implementation set's the tooltip
     * @return
     */
    private ComponentProvider<JLabel> createColorRenderingLabelController() {
        ComponentProvider<JLabel> context = new LabelProvider() {
            Border selectedBorder;
            @Override
            protected void format(CellContext context) {
                super.format(context);
                Object value = context.getValue();
                if (value instanceof Color) {
                    rendererComponent.setBackground((Color) value);
                    rendererComponent.putClientProperty("renderer-dont-touch", "color");
                } else {
                    rendererComponent.putClientProperty("renderer-dont-touch", null);
                }
            }

            /**
             * 
             * @param context
             */
            @Override
            protected void configureState(CellContext context) {
                Object value = context.getValue();
                if (value instanceof Color) {
                    Color newColor = (Color) value;
                    rendererComponent.setToolTipText("RGB value: " + newColor.getRed() + ", "
                            + newColor.getGreen() + ", " + newColor.getBlue());

                } else {
                    rendererComponent.setToolTipText(null);
                }
                if (context.isSelected()) {
                    selectedBorder = BorderFactory.createMatteBorder(2, 5,
                            2, 5, context.getSelectionBackground());
                } else {
                    selectedBorder = BorderFactory.createMatteBorder(2, 5,
                            2, 5, context.getBackground());
                }
                rendererComponent.setBorder(selectedBorder);
            }
            
        };
        return context;
    }

    
//--------------------- utility    
    
    
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
    
    private TreeModel createTreeModelWithLinks(int count) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Links");
        for (int i = 0; i < count; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a link text " + i, null, url);
                }
                root.add(new DefaultMutableTreeNode(link));
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new DefaultTreeModel(root);
    }
    
    private TableModel createModelWithLinks() {
        String[] columnNames = { "text only", "Link editable",
                "Link not-editable", "Bool editable", "Bool not-editable" };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return getValueAt(0, columnIndex).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return !getColumnName(column).contains("not");
            }

        };
        for (int i = 0; i < 4; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null,
                        new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class
                            .getResource("resources/test.html");

                    link = new LinkModel("a link text " + i, null, url);
                }
                model.addRow(new Object[] { "text only " + i, link, link,
                        Boolean.TRUE, Boolean.TRUE });
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return model;
    }
    /**
     * 
     * @return a ListModel wrapped around the AncientSwingTeam's Color column.
     */
    private ListModel createListColorModel() {
        AncientSwingTeam tableModel = new AncientSwingTeam();
        int colorColumn = 2;
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            model.addElement(tableModel.getValueAt(i, colorColumn));
        }
        return model;
    }

    /**
     * @return a TreeModel with tree nodes of type Color.
     */
    private TreeModel createTreeColorModel() {
        final AncientSwingTeam tableModel = new AncientSwingTeam();
        final int colorColumn = 2;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Colors");
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            root.add(new DefaultMutableTreeNode(tableModel.getValueAt(row, colorColumn)));
        }
        return new DefaultTreeModel(root);
        
    }

    /**
     * copied from sun's tutorial.
     */
    public static class ColorRenderer extends JLabel implements
            TableCellRenderer {
        Border unselectedBorder = null;

        Border selectedBorder = null;

        boolean isBordered = true;

        public ColorRenderer(boolean isBordered) {
            this.isBordered = isBordered;
            setOpaque(true); // MUST do this for background to show up.
            putClientProperty("renderer-dont-touch", "color");
        }

        public Component getTableCellRendererComponent(JTable table,
                Object color, boolean isSelected, boolean hasFocus, int row,
                int column) {
            Color newColor = (Color) color;
            setBackground(newColor);
            if (isBordered) {
                if (isSelected) {
                    if (selectedBorder == null) {
                        selectedBorder = BorderFactory.createMatteBorder(2, 5,
                                2, 5, table.getSelectionBackground());
                    }
                    setBorder(selectedBorder);
                } else {
                    if (unselectedBorder == null) {
                        unselectedBorder = BorderFactory.createMatteBorder(2,
                                5, 2, 5, table.getBackground());
                    }
                    setBorder(unselectedBorder);
                }
            }

            setToolTipText("RGB value: " + newColor.getRed() + ", "
                    + newColor.getGreen() + ", " + newColor.getBlue());
            return this;
        }
    }   
    
    public static class Player {
        String name;
        int score;
        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }
        @Override
        public String toString() {
            return name + " has score: " + score;
        }
        public String getName() {
            return name;
        }
        public int getScore() {
            return score;
        }
        
        
    }

    /**
     * create and returns a ListModel containing Players.
     * @return
     */
    private ListModel createPlayerModel() {
        DefaultListModel model = new DefaultListModel();
        model.addElement(new Player("Henry", 10));
        model.addElement(new Player("Berta", 112));
        model.addElement(new Player("Dave", 20));
        return model;
    }

    
    /**
     * c&p'd from JGoodies BeanUtils.
     * 
     * Looks up and returns a <code>PropertyDescriptor</code> for the
     * given Java Bean class and property name using the standard 
     * Java Bean introspection behavior.
     * 
     * @param beanClass     the type of the bean that holds the property
     * @param propertyName  the name of the Bean property
     * @return the <code>PropertyDescriptor</code> associated with the given
     *     bean and property name as returned by the Bean introspection
     *     
     * @throws IntrospectionException if an exception occurs during
     *     introspection.
     * @throws NullPointerException if the beanClass or propertyName is <code>null</code>
     * 
     * @since 1.1.1
     */
    public static PropertyDescriptor getPropertyDescriptor(
        Class beanClass,
        String propertyName)
        throws IntrospectionException {

        BeanInfo info = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        for (int i = 0; i < descriptors.length; i++) {
            if (propertyName.equals(descriptors[i].getName()))
                return descriptors[i];
        }
        throw new IntrospectionException(
            "Property '" + propertyName + "' not found in bean " + beanClass);
    }

    /**
     * c&p'd from JGoodies BeanUtils.
     * 
     * Returns the value of the specified property of the given non-null bean.
     * This operation is unsupported if the bean property is read-only.<p>
     * 
     * If the read access fails, a PropertyAccessException is thrown
     * that provides the Throwable that caused the failure.
     * 
     * @param bean                the bean to read the value from
     * @param propertyDescriptor  describes the property to be read
     * @return the bean's property value
     * 
     * @throws NullPointerException           if the bean is <code>null</code>
     * @throws UnsupportedOperationException  if the bean property is write-only
     * @throws PropertyAccessException        if the new value could not be read
     */
    public static Object getValue(Object bean, PropertyDescriptor propertyDescriptor) {
        if (bean == null)
            throw new NullPointerException("The bean must not be null.");
        
        Method getter = propertyDescriptor.getReadMethod();
        if (getter == null) {
            throw new UnsupportedOperationException(
                "The property '" + propertyDescriptor.getName() + "' is write-only.");
        }
        
        try {
            return getter.invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException("can't access property: " + propertyDescriptor.getName());
        }

//    } catch (IllegalAccessException e) {
//        throw PropertyAccessException.createWriteAccessException(
//            bean, newValue, propertyDescriptor, e);
//    } catch (IllegalArgumentException e) {
//        throw PropertyAccessException.createWriteAccessException(
//            bean, newValue, propertyDescriptor, e);
//    }

    }    
    
    /**
     * do-nothing method - suppress warning if there are no other
     * test fixtures to run.
     *
     */
    public void testDummy() {
        
    }

}
