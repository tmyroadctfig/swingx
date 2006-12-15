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
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
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
import org.jdesktop.swingx.action.LinkModelAction;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
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
        TableCellRenderer renderer = DefaultTableRenderer.createDefaultTableRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        TableCellRenderer booleanRenderer = new DefaultTableRenderer<AbstractButton>(new RenderingButtonController());
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
     * xtable/xlist using the same rendererController.<p>
     * 
     * Note: here they really share the same instance. 
     * That's possible only if neither controller nor rendereringController
     * rely on the cellContext's type - hmmm...
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
            
            
        };
        JXTable xtable = new JXTable(tableModel);
        RenderingPropertyController nameController = new RenderingPropertyController("name");
        xtable.getColumn(0).setCellRenderer(new DefaultTableRenderer<JLabel>(nameController));
        xtable.getColumn(1).setCellRenderer(new DefaultTableRenderer<JLabel>(new RenderingPropertyController("score")));
        xtable.packAll();
        JXList list = new JXList(players);
        list.setCellRenderer(new DefaultListRenderer<JLabel>(nameController));
        showWithScrollingInFrame(xtable, list, "JXTable/JXList: Custom color renderer");

    }

    public static class RenderingPropertyController extends RenderingLabelController {
        
        private String property;

        public RenderingPropertyController(String property) {
            this.property = property;
        }

        @Override
        protected String getStringValue(CellContext context) {
            
            Object value = context.getValue();
            try {
                PropertyDescriptor desc = getPropertyDescriptor(value.getClass(), property);
                return getValue(value, desc).toString();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                
            }
            return "";
        }
        
    }
    
    /**
     * ext link renderer in table.
     *
     */
    public void interactiveTestTableLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        RenderingComponentController<JXHyperlink> context = new RenderingHyperlinkController(action, LinkModel.class);
        RendererController configurator = new RendererController<JXHyperlink, JComponent>(context) {

            @Override
            protected void configureColors(CellContext<JComponent> context) {
              if (context.isSelected()) {
//            linkButton.setForeground(table.getSelectionForeground());
            getRendererComponent().setBackground(context.getSelectionBackground());
        }
        else {
//            linkButton.setForeground(table.getForeground());
            getRendererComponent().setBackground(context.getBackground());
        }
            }
            
        };
        table.setDefaultRenderer(LinkModel.class, new DefaultTableRenderer<JXHyperlink>(configurator));
        LinkModelAction action2 = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultEditor(LinkModel.class, new LinkRenderer(action2, LinkModel.class));
        JFrame frame = wrapWithScrollingInFrame(table, visitor.getOutputComponent(), "show link renderer in table");
        frame.setVisible(true);

    }
    
    /**
     * ext link renderer in list.
     *
     */
    public void interactiveTestListLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXList list = new JXList(createListModelWithLinks(20));
        list.setRolloverEnabled(true);
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        LinkModelAction action = new LinkModelAction(visitor);
        RenderingComponentController<JXHyperlink> context = new RenderingHyperlinkController(action, LinkModel.class);
        RendererController configurator = new RendererController<JXHyperlink, JComponent>(context) {

            @Override
            protected void configureColors(CellContext<JComponent> context) {
              if (context.isSelected()) {
//            linkButton.setForeground(table.getSelectionForeground());
            getRendererComponent().setBackground(context.getSelectionBackground());
        }
        else {
//            linkButton.setForeground(table.getForeground());
            getRendererComponent().setBackground(context.getBackground());
        }
            }
            
        };
        list.setCellRenderer(new DefaultListRenderer<JXHyperlink>(configurator));
        JFrame frame = wrapWithScrollingInFrame(list, visitor.getOutputComponent(), "show link renderer in list");
        frame.setVisible(true);

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
        showWithScrollingInFrame(xtable, table, "JXTable/highlighter: Custom color renderer - standard/ext");

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
        showWithScrollingInFrame(xtable, table, "JXTable/highlighter dont-touch: Custom color renderer - standard/ext");
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
     * That's possible only if neither controller nor rendereringController
     * rely on the cellContext's type - hmmm...
     * 
     *  
     */
    public void interactiveTableAndListCustomColorRenderer() {
        TableModel tableModel = new AncientSwingTeam();
        RendererController controller = createColorRendererContext();
        JXTable xtable = new JXTable(tableModel);
        xtable.setDefaultRenderer(Color.class, new DefaultTableRenderer(controller));
        ListModel model = createListColorModel();
        JXList list = new JXList(model);
        ListCellRenderer renderer = new DefaultListRenderer(controller);
        list.setCellRenderer(renderer);
        showWithScrollingInFrame(xtable, list, "JXTable/JXList: Custom color renderer");

    }


    
    /**
     * creates and returns a color ext renderer.
     * @return
     */
    protected TableCellRenderer createColorRendererExt() {
        
        RendererController context = createColorRendererContext();
        
        TableCellRenderer renderer = new DefaultTableRenderer(context);
        return renderer;
    }

    /**
     * creates and returns a color ext renderer.
     * @return
     */
    protected ListCellRenderer createListColorRendererExt() {
        
        RendererController context = createColorRendererContext();
        
        ListCellRenderer renderer = new DefaultListRenderer(context);
        return renderer;
    }

    
    private RendererController createColorRendererContext() {
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
     * @return
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
