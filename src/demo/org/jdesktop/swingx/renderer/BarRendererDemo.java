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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.util.WindowUtils;

/**
 * A unusual example about how to configure SwingX renderers.<p>
 * 
 * The requirement is to visualize a numerical value by a bar with widthe relative 
 * to the value and make the cell height zoomable to get a quick impression of the 
 * distribution of the widths. The zooming is done traditionally, by adjusting
 * the target components' rowHeight in a MouseWheelListener. <p>
 * 
 * What's new in SwingX is the easy way to setup the rendering, we need: 
 * <ul>
 * <li> a custom IconValue which configures the icon width as appropriate
 * <li> a custom Highlighter which adjusts the font and the icon height to the target 
 * component's actual rowHeight.
 * </ul>
 * 
 *  
 * @author Jeanette Winzenburg
 */
public class BarRendererDemo {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(BarRendererDemo.class
            .getName());
    private ListModel listModel;
    private NumberTableModel tableModel;
    private DefaultMutableTreeNode rootNode;

    public BarRendererDemo() {
        initData();
    }

    /**
     * Configure the given collection components with the same
     * rendering representation.
     * <
     * Note: this method is extracted for emphasis only :-)
     */
    private void configureRendering(JXTable table, JXList list, JXTree tree) {
        StringValue stringValue = FormatStringValue.NUMBER_TO_STRING;
        IconValue iconValue = new IconValue() {
            BarIcon icon = new BarIcon();
            double upperBound = 1000;
            public Icon getIcon(Object value) {
                if (value instanceof Number) {
                    // reset default height
                    icon.setIconHeight(16);
                    icon.setValue(((Double) value).doubleValue() / upperBound);
                    return icon;
                }
                return null;
            }
            
        };
        MappedValue mv = new MappedValue(stringValue, iconValue);
        table.setDefaultRenderer(Double.class, new DefaultTableRenderer(mv));
        list.setCellRenderer(new DefaultListRenderer(mv));
        tree.setCellRenderer(new DefaultTreeRenderer((StringValue) mv));
    }

    /**
     * Configures and installs the shared highlighter for the table, list and tree.
     * 
     */
    private void configureHighlighting(JXTable table, JXList list, JXTree tree) {
        final int defaultHeight = table.getRowHeight();
        Highlighter zoomHighlighter = new AbstractHighlighter() {

            @Override
            protected Component doHighlight(Component component,
                    ComponentAdapter adapter) {
                int rowHeight = getRowHeight(adapter);
                if (rowHeight != defaultHeight) {
                    adjustFont(component, rowHeight);
                    adjustIcon(component, rowHeight);
                }
                return component;
            }

            private void adjustIcon(Component component, int rowHeight) {
                // PENDING: handle tree - the type is WrappingIconProvider
                if (!(component instanceof JLabel)) return;
                Icon icon = ((JLabel) component).getIcon();
                if (icon instanceof BarIcon) {
                    ((BarIcon) icon).setIconHeight(rowHeight - 2);
                }
            }

            private void adjustFont(Component component, int rowHeight) {
                Font font = component.getFont();
                component.setFont(font.deriveFont((float) (rowHeight - 4)));
            }

            private int getRowHeight(ComponentAdapter adapter) {
                if (adapter.getComponent() instanceof JXTable) {
                    return ((JTable) adapter.getComponent()).getRowHeight();
                }
                if (adapter.getComponent() instanceof JXTree) {
                    return ((JTree) adapter.getComponent()).getRowHeight();
                }
                if (adapter.getComponent() instanceof JXList) {
                    return ((JList) adapter.getComponent()).getFixedCellHeight();
                }
                return defaultHeight;
            }
            
        };
        table.addHighlighter(zoomHighlighter);
        list.addHighlighter(zoomHighlighter);
        tree.addHighlighter(zoomHighlighter);
    }

    /**
     * Configures and registeres the shared MouseWheelListener for the table, list
     * and tree.
     */
    private void installZoomControl(JXTable table, JXList list, JXTree tree) {
        MouseWheelListener wheel = new MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent e) {
                if (!e.isControlDown()) return;
                if (e.getComponent() instanceof JXTable) {
                    JXTable table = (JXTable) e.getComponent();
                    int height = table.getRowHeight() + e.getWheelRotation();
                    if (height > 2) {
                        table.setRowHeight(height);
                    }
                }
                if (e.getComponent() instanceof JXList) {
                    JXList list = (JXList) e.getComponent();
                    int height = list.getFixedCellHeight() + e.getWheelRotation();
                    if (height > 2) {
                        list.setFixedCellHeight(height);
                    }
                }
                if (e.getComponent() instanceof JXTree) {
                    JXTree table = (JXTree) e.getComponent();
                    int height = table.getRowHeight() + e.getWheelRotation();
                    if (height > 2) {
                        table.setRowHeight(height);
                    }
                }
            }
            
        };
        table.addMouseWheelListener(wheel);
        list.addMouseWheelListener(wheel);
        tree.addMouseWheelListener(wheel);
    }

    /**
     * A quick implemenation of a progress bar like icon. Paints a
     * filled rectangle of fixed height and a configurable width
     * relative to the icon's max width.
     * 
     */
    public static class BarIcon implements Icon {

        private int value;
        private double relative;
        private int maxWidth = 40;
        private int height = 16;

        public int getIconHeight() {
            return height;
        }
        
        public void setIconHeight(int height) {
            this.height = height;
        }

        public int getIconWidth() {
            return value;
        }
        
        public int getMaxIconWidth() {
            return maxWidth;
        }
        
        public void setMaxIconWidth(int max) {
            this.maxWidth  = max;
            updateRelative();
        }
        
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, value, getIconHeight());
        }
        
        /**
         * Set the relative width of the icon.
         * @param thumb a value in the interval [0, 1] indicating the
         *   width relative to the icon's max.
         */
        public void setValue(double thumb) {
            this.relative = thumb;
            updateRelative();
        }

        private void updateRelative() {
            double d = relative * getMaxIconWidth();
            this.value = Double.valueOf(d).intValue();
        }
    }

    /**
     * Configures some defaults, namely the same fixed rowheight on the
     * table, list and tree.
     */
    private void configureComponents(JXTable table, JXList list, JXTree tree) {
        table.setColumnControlVisible(true);
        table.setVisibleRowCount(30);
        table.setShowGrid(false, true);
        
        list.setFixedCellHeight(table.getRowHeight());
        tree.setRowHeight(table.getRowHeight());
    }
    

    /**
     * Creates and configures the content pane.
     * 
     * @return the component to show.
     */
    private Component createContent() {
        // create
        JXTable table = new JXTable();
        JXList list = new JXList();
        JXTree tree = new JXTree();
        // add
        configureRendering(table, list, tree);
        configureHighlighting(table, list, tree);
        installZoomControl(table, list, tree);
        table.setModel(tableModel);
        list.setModel(listModel);
        tree.setModel(new DefaultTreeModel(rootNode));
        configureComponents(table, list, tree);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("JXTable", new JScrollPane(table));
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(new JScrollPane(list));
        splitPane.setRightComponent(new JScrollPane(tree));
////        splitPane.setDividerLocation(250);
        tabbedPane.addTab("JXList/JXTree", splitPane);
        return tabbedPane;
    }

    


    private void initData() {
        tableModel = new NumberTableModel(1000, 6);
        listModel = new AbstractListModel() {

            public Object getElementAt(int index) {
                return tableModel.getValueAt(index, 0);
            }

            public int getSize() {
                // TODO Auto-generated method stub
                return tableModel.getRowCount();
            }
            
        };
        rootNode = new DefaultMutableTreeNode("Numbers");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
           rootNode.add(new DefaultMutableTreeNode(tableModel.getValueAt(i, 0))); 
        }
    }
    
    public static class NumberTableModel extends DefaultTableModel {
        
        Double max;
        NumberTableModel(int rows, int columns) {
            super(rows, columns);
            max = 1000.0;
            fillNumbers();
        }

        public Double getUpperBound() {
            return max;
        }
        
        public Double getLowerBound() {
            return 0.0;
        }
        /**
         * 
         */
        private void fillNumbers() {
            for (int row = 0; row < getRowCount(); row++) {
                for (int column = 0; column < getColumnCount(); column++) {
                    setValueAt(Math.random() * max, row, column);
                }
            }
            fireTableRowsUpdated(0, getRowCount() - 1);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Double.class;
        }
        
        
    }
    //---------------------------Main

    public static void main(String[] args) {
        initLF();
        final JXFrame frame = new JXFrame("SwingX :: Bar Renderer Demo", true);
        frame.add(new BarRendererDemo().createContent());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.pack();
                frame.setSize(600, 400);
                frame.setLocation(WindowUtils.getPointForCentering(frame));
                frame.setVisible(true);
            }
        });        
    }

    /**
     * 
     */
    private static void initLF() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
