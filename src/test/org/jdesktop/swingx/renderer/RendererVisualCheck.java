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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.EditorPaneLinkVisitor;
import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXEditorPaneTest;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.LinkModel;
import org.jdesktop.swingx.LinkRenderer;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.LinkModelAction;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;



/**
 * Visual check of extended Swingx renderers.
 * 
 * @author Jeanette Winzenburg
 */
public class RendererVisualCheck extends InteractiveTestCase {
    public static void main(String[] args) {
//        setSystemLF(true);
        RendererVisualCheck test = new RendererVisualCheck();
        try {
//            test.runInteractiveTests();
          test.runInteractiveTests(".*Disabled.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Check if all defaults show up as expected.
     *
     */
    public void interactiveListDisabledIconRenderer() {
        final TableModel model = createTableModelWithDefaultTypes();
        ListModel listModel = new AbstractListModel() {

            public Object getElementAt(int index) {
                return model.getValueAt(index, 4);
            }

            public int getSize() {
                return model.getRowCount();
            }
            
        };
        final JList standard = new JList(listModel);
        final JList enhanced = new JList(listModel);
        enhanced.setCellRenderer(new DefaultListRenderer<JLabel>(new RenderingIconController()));

        AbstractAction action = new AbstractAction("toggle disabled") {

            public void actionPerformed(ActionEvent e) {
                standard.setEnabled(!standard.isEnabled());
                enhanced.setEnabled(!enhanced.isEnabled());
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(standard, enhanced, "Compare renderers: default <--> enhanced");
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    /**
     * Check if all defaults show up as expected.
     *
     */
    public void interactiveTableDefaultRenderers() {
        TableModel model = createTableModelWithDefaultTypes();
        final JTable standard = new JTable(model);
        final JTable enhanced = new JTable(model) {

            @Override
            protected void createDefaultRenderers() {
                defaultRenderersByColumnClass = new UIDefaults();
                setDefaultRenderer(Object.class, DefaultTableRenderer.createDefaultTableRenderer());
                RenderingLabelController controller = new RenderingLabelController(FormatToStringConverter.NUMBER_TO_STRING);
                controller.setHorizontalAlignment(JLabel.RIGHT);
                setDefaultRenderer(Number.class, new DefaultTableRenderer<JLabel>(controller));
                setDefaultRenderer(Date.class, DefaultTableRenderer.createDefaultTableRenderer(
                        FormatToStringConverter.DATE_TO_STRING));
                TableCellRenderer renderer  = new DefaultTableRenderer<JLabel>(new RenderingIconController());
                setDefaultRenderer(Icon.class, renderer);
                setDefaultRenderer(ImageIcon.class, renderer);
            }
            
        };
        AbstractAction action = new AbstractAction("toggle disabled") {

            public void actionPerformed(ActionEvent e) {
                standard.setEnabled(!standard.isEnabled());
                enhanced.setEnabled(!enhanced.isEnabled());
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(standard, enhanced, "Compare renderers: default <--> enhanced");
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    /**
     * @return
     */
    private TableModel createTableModelWithDefaultTypes() {
        String[] names = {"Object", "Number", "Double", "Date", "ImageIcon"};
        final Class[] types = {Object.class, Number.class, Double.class, Date.class, ImageIcon.class};
        DefaultTableModel model = new DefaultTableModel(names, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
            
        };
        Date today = new Date();
        Icon icon = new ImageIcon(JXTable.class.getResource("resources/images/kleopatra.jpg"));
        for (int i = 0; i < 10; i++) {
            Object[] values = new Object[] {"row " + i, i, Math.random() * 100, new Date(today.getTime() + i * 100000), icon};
            model.addRow(values);
        }
        return model;
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
        TableCellRenderer renderer = DefaultTableRenderer.createDefaultTableRenderer();
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
        TableCellRenderer renderer = DefaultTableRenderer.createDefaultTableRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JXTable: Unselected focused background: core/ext renderer");
        getStatusBar(frame).add(new JLabel("different background for unselected lead: first column is not-editable"));    
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Compare xtable using core default renderer vs. swingx default renderer.<p>
     * 
     * Unselected background of lead is different for editable/not-editable cells.
     * With core renderer: can't because Highlighter hack jumps in.
     * 
     */
    public void interactiveXTableCompareFocusedCellBackgroundPluggable() {
        TableModel model = new AncientSwingTeam() {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        
        JXTable xtable = new JXTable(model);
        xtable.setBackground(Highlighter.notePadBackground.getBackground()); // ledger
        JXTable table = new JXTable(model);
        table.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        table.setDefaultRenderer(Object.class, DefaultTableRenderer.createDefaultTableRenderer());
        TableCellRenderer booleanRenderer = new DefaultTableRenderer<AbstractButton>(
                new RenderingButtonController());
        table.setDefaultRenderer(Boolean.class, booleanRenderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JXTable- pluggable: Unselected focused background: core/ext renderer");
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
        table.setDefaultRenderer(Object.class, DefaultTableRenderer.createDefaultTableRenderer());
        table.setEnabled(false);
        final JXList list = new JXList(new String[] {"one", "two", "and something longer"});
        list.setEnabled(false);
//        list.setCellRenderer(new DefaultListCellRendererExt());
        list.setCellRenderer(DefaultListRenderer.createDefaultListRenderer());
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
     * Example for custom ToStringConverter: bound to bean property.
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
        PropertyToStringConverter converter = new PropertyToStringConverter("name");
        RenderingLabelController nameController = new RenderingLabelController(converter);
        xtable.getColumn(0).setCellRenderer(new DefaultTableRenderer<JLabel>(nameController));
        PropertyToStringConverter scoreConverter = new PropertyToStringConverter("score");
        xtable.getColumn(1).setCellRenderer(DefaultTableRenderer.createDefaultTableRenderer(scoreConverter));
        xtable.packAll();
        JXList list = new JXList(players);
        // we share the component controller between table and list
        list.setCellRenderer(new DefaultListRenderer<JLabel>(nameController));
        showWithScrollingInFrame(xtable, list, "JXTable/JXList: Custom property renderer");

    }

  /**
  * Simple example to bind a toStringConverter to a single property of the value.
  */
    public static class PropertyToStringConverter implements ToStringConverter {
        private String property;

        public PropertyToStringConverter(String property) {
            this.property = property;
        }

        /**
         * {@inheritDoc} <p>
         * Implemented to return the toString of the named property value.
         */
        public String getStringValue(Object value) {
            try {
                PropertyDescriptor desc = getPropertyDescriptor(value.getClass(), property);
                return TO_STRING.getStringValue(getValue(value, desc));
            } catch (Exception e) {
                // nothing much we can do here...
                
            }
            return "";
        }
        
    }
    
    /**
     * extended link renderer in table.
     *
     */
    public void interactiveTestTableLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        RenderingComponentController<JXHyperlink> controller = new RenderingHyperlinkController(action, LinkModel.class);
        table.setDefaultRenderer(LinkModel.class, new DefaultTableRenderer<JXHyperlink>(controller));
        LinkModelAction action2 = new LinkModelAction<LinkModel>(visitor);
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
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        LinkModelAction action = new LinkModelAction(visitor);
        RenderingComponentController<JXHyperlink> context = new RenderingHyperlinkController(action, LinkModel.class);
        list.setCellRenderer(new DefaultListRenderer<JXHyperlink>(context));
        JFrame frame = wrapWithScrollingInFrame(list, visitor.getOutputComponent(), "show link renderer in list");
        frame.setVisible(true);

    }

    /**
     * Use a custom button controller to show both checkbox icon and text to
     * render Actions in a JXList.
     */
    public void interactiveTableWithListColumnControl() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        JXList list = new JXList();
        // quick-fill and hook to table columns' visibility state
        configureList(list, table);
        // a custom rendering button controller showing both checkbox and text
        RenderingButtonController wrapper = new RenderingButtonController() {

            @Override
            protected void format(CellContext context) {
                if (!(context.getValue() instanceof AbstractActionExt)) {
                    super.format(context);
                    return;
                }
                rendererComponent.setSelected(((AbstractActionExt) context.getValue()).isSelected());
                rendererComponent.setText(((AbstractActionExt) context.getValue()).getName());
            }
            
        };
        wrapper.setHorizontalAlignment(JLabel.LEADING);
        list.setCellRenderer(new DefaultListRenderer<AbstractButton>(wrapper));
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
    private void configureList(final JXList list, final JXTable table) {
        final List<Action> actions = new ArrayList();
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
        // bind action to space
        list.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),
                "toggleSelectedActionState");
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
        xtable.addHighlighter(AlternateRowHighlighter.genericGrey);
        xtable.setDefaultRenderer(Color.class, new ColorRenderer(true));
        JXTable table = new JXTable(model);
        table.addHighlighter(AlternateRowHighlighter.genericGrey);
        TableCellRenderer renderer = createColorRendererExt();
        table.setDefaultRenderer(Color.class, renderer);
        JXFrame frame = showWithScrollingInFrame(xtable, table, "JXTable/highlighter: Custom color renderer - standard/ext");
        addStatusMessage(frame, "Highlighter hide custom color renderer background for unselected");
    }

    /**
     * Compare xtable using custom color renderer - standard vs. ext.<p>
     * Adds highlighter which respects renderer's dont touch.
     */
    public void interactiveTableCustomColorRendererWithHighlighterDontTouch() {
        TableModel model = new AncientSwingTeam();
        JXTable xtable = new JXTable(model);
        Highlighter highlighter = createPropertyRespectingHighlighter(AlternateRowHighlighter.genericGrey);
        xtable.addHighlighter(highlighter);
        xtable.setDefaultRenderer(Color.class, new ColorRenderer(true));
        JXTable table = new JXTable(model);
        table.addHighlighter(highlighter);
        TableCellRenderer renderer = createColorRendererExt();
        table.setDefaultRenderer(Color.class, renderer);
        JXFrame frame = showWithScrollingInFrame(xtable, table, "JXTable/highlighter dont-touch: Custom color renderer - standard/ext");
        addStatusMessage(frame, "Highlighter doesn't touch custom color renderer visual properties");
    }

    /**
     * Creates and returns a Highlighter which does nothing if the 
     * rendererComponent has the dont-touch property set. Otherwise
     * delegates highlighting to the delegate.
     * 
     * @param delegate
     * @return
     */
    private Highlighter createPropertyRespectingHighlighter(final Highlighter delegate) {
        Highlighter highlighter = new Highlighter() {

            @Override
            public Component highlight(Component renderer, ComponentAdapter adapter) {
                if (((JComponent) renderer).getClientProperty("renderer-dont-touch") != null) return renderer;
                return delegate.highlight(renderer, adapter);
            }
            
        };
        return highlighter;
    }


    /**
     * xtable/xlist using the same rendererController.<p>
     * 
     * Note: here they really share the same instance. 
     * That's possible only if neither renderer controller nor component controller
     * rely on the cellContext's type - hmmm... better don't!
     * 
     *  
     */
    public void interactiveTableAndListCustomColorRenderer() {
        TableModel tableModel = new AncientSwingTeam();
        RendererController controller = createColorRendererController();
        JXTable xtable = new JXTable(tableModel);
        xtable.setDefaultRenderer(Color.class, new DefaultTableRenderer(controller));
        ListModel model = createListColorModel();
        JXList list = new JXList(model);
        ListCellRenderer renderer = new DefaultListRenderer(controller);
        list.setCellRenderer(renderer);
        showWithScrollingInFrame(xtable, list, "JXTable/JXList: Custom color renderer - sharing the renderer controller");

    }


    /**
     * xtable/xlist using the same custom component controller.<p>
     * 
     * Note: this is cleaner than sharing the same RendererController.
     *  
     */
    public void interactiveTableAndListCustomColorRenderingController() {
        TableModel tableModel = new AncientSwingTeam();
        RenderingComponentController<JLabel> controller = createColorRenderingLabelController();
        JXTable xtable = new JXTable(tableModel);
        xtable.setDefaultRenderer(Color.class, new DefaultTableRenderer<JLabel>(controller));
        ListModel model = createListColorModel();
        JXList list = new JXList(model);
        ListCellRenderer renderer = new DefaultListRenderer<JLabel>(controller);
        list.setCellRenderer(renderer);
        showWithScrollingInFrame(xtable, list, "JXTable/JXList: Custom color renderer - sharing the component controller");

    }

    
    /**
     * creates and returns a color ext renderer.
     * @return
     */
    protected TableCellRenderer createColorRendererExt() {
        RenderingComponentController<JLabel> context = createColorRenderingLabelController();
        TableCellRenderer renderer = new DefaultTableRenderer<JLabel>(context);
        return renderer;
    }

    /**
     * creates and returns a color ext renderer.
     * @return
     */
    protected ListCellRenderer createListColorRendererExt() {
        RenderingComponentController<JLabel> context = createColorRenderingLabelController();
        ListCellRenderer renderer = new DefaultListRenderer<JLabel>(context);
        return renderer;
    }

    /**
     * creates and returns a renderer controller specialized on Color values.<p>
     * NOTE: while this is possible, it'll probably will turn out as not recommended -
     * content-related state should be configured in a component controller. 
     * 
     * @return
     */
    private RendererController createColorRendererController() {
        RendererController context = new RendererController<JLabel, JComponent>(new RenderingLabelController() ) {
            Border selectedBorder;
            @Override
            protected void configureColors(CellContext<JComponent> context) {
                super.configureColors(context);
                if (context.getValue() instanceof Color) {
                    getRendererComponent().setBackground((Color) context.getValue());
                    getRendererComponent().putClientProperty("renderer-dont-touch", "color");
                } else {
                    getRendererComponent().putClientProperty("renderer-dont-touch", null);
                }
            }

            @Override
            protected void configureBorder(CellContext<JComponent> context) {
                if (context.isSelected()) {
                    selectedBorder = BorderFactory.createMatteBorder(2, 5,
                            2, 5, context.getSelectionBackground());
                } else {
                    selectedBorder = BorderFactory.createMatteBorder(2, 5,
                            2, 5, context.getBackground());
                }
                getRendererComponent().setBorder(selectedBorder);
            }
            
        };
        return context;
    }

    /**
     * Creates and returns a component controller specialized on Color values. <p>
     * Note: this implementation set's the tooltip
     * @return
     */
    private RenderingComponentController<JLabel> createColorRenderingLabelController() {
        RenderingComponentController<JLabel> context = new RenderingLabelController() {
            Border selectedBorder;
            @Override
            protected void format(CellContext context) {
                super.format(context);
                if (context.getValue() instanceof Color) {
                    rendererComponent.setBackground((Color) context.getValue());
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
                if (context.getValue() instanceof Color) {
                    Color newColor = (Color) context.getValue();
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
            return getter.invoke(bean, null);
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
    

}
