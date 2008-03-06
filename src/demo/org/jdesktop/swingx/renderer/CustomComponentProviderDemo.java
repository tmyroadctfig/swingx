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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.util.WindowUtils;

/**
 * A example about how to create a custom ComponentProvider.<p>
 * 
 *  
 * @author Jeanette Winzenburg
 */
public class CustomComponentProviderDemo {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(CustomComponentProviderDemo.class
            .getName());
    private TableModel tableModel;
    private DefaultMutableTreeNode rootNode;

    public CustomComponentProviderDemo() {
        initData();
    }

    /**
     * Use a JXMonthView as rendering component.
     */
    public static class MonthViewProvider extends ComponentProvider<JXMonthView> {

        Border border = BorderFactory.createEmptyBorder(10, 5, 10, 5);
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected void configureState(CellContext context) {
            rendererComponent.setBorder(border);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void format(CellContext context) {
            if (context.getValue() instanceof Date) {
                Date date = (Date) context.getValue();
                rendererComponent.setSelectionDate(date);
                rendererComponent.ensureDateVisible(date);
            } else {
                rendererComponent.clearSelection();
                rendererComponent.ensureDateVisible(rendererComponent.getToday());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected JXMonthView createRendererComponent() {
            return new JXMonthView();
        }

        
    }
    /**
     * Configure the given collection components with the same
     * rendering representation.
     * 
     * Note: this method is extracted for emphasis only :-)
     */
    private void configureRendering(JXTable table, JXTree tree) {
        ComponentProvider provider = new MonthViewProvider();
        table.setDefaultRenderer(Date.class, new DefaultTableRenderer(provider));
        tree.setCellRenderer(new DefaultTreeRenderer(new WrappingProvider(provider)));
    }

    /**
     * Configures and installs shared highlighter for the table and tree.
     * 
     */
    private void configureHighlighting(JXTable table, JXTree tree) {
        Highlighter hl = HighlighterFactory.createSimpleStriping();
        table.addHighlighter(hl);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        HighlightPredicate predicate = new HighlightPredicate() {
            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                Object value = adapter.getValue();
                if (adapter.getValue() instanceof DefaultMutableTreeNode) {
                    value = ((DefaultMutableTreeNode) adapter.getValue()).getUserObject();
                }
                if (value instanceof Date) {
                   return cal.getTime().before((Date) value); 
                }
                return false;
            }
            
        };
        ColorHighlighter nextYear = new ColorHighlighter(predicate, null, Color.RED);
        table.addHighlighter(nextYear);
        tree.addHighlighter(nextYear);
    }


    /**
     * Configures meta-data dependent properties, namely cell sizing.
     * 
     */
    private void configureComponents(JXTable table, JXTree tree) {
        table.setColumnControlVisible(true);
        Component comp = table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        int height = comp.getPreferredSize().height;
        int width = comp.getPreferredSize().width;
        table.setRowHeight(height);
        table.getColumn(0).setMaxWidth(width);
        table.setVisibleRowCount(3);
        table.packAll();
        tree.setRowHeight(table.getRowHeight());
        tree.setVisibleRowCount(table.getVisibleRowCount());
    }
    

    /**
     * Creates and configures the content pane.
     * 
     * @return the component to show.
     */
    private Component createContent() {
        // create
        JXTable table = new JXTable();
        JXTree tree = new JXTree();
        // configure base properties
        configureRendering(table, tree);
        configureHighlighting(table, tree);
        table.setModel(tableModel);
        tree.setModel(new DefaultTreeModel(rootNode));
        // configure meta-data dependent properties
        configureComponents(table, tree);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("JXTable", new JScrollPane(table));
        tabbedPane.addTab("JXTree", new JScrollPane(tree));
        return tabbedPane;
    }

    


    private void initData() {
        tableModel = createTableModel();
        rootNode = new DefaultMutableTreeNode("Dates");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
           rootNode.add(new DefaultMutableTreeNode(tableModel.getValueAt(i, 0))); 
        }
    }
    
    /**
     * @return
     */
    private TableModel createTableModel() {
        String[] header = { "Month Context", "Date",};
        DefaultTableModel model = new DefaultTableModel(header, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Date.class;
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            
        };
        Map<Object, Date> dates = createDates();
        for (Object text : dates.keySet()) {
            Object[] row = new Object[] {dates.get(text), text};
            model.addRow(row);
        }
        return model;
    }

    private Map<Object, Date> createDates() {
        Map<Object, Date> dates = new HashMap<Object, Date>();
        Calendar cal = Calendar.getInstance();
        DateFormat format = DateFormat.getDateInstance();
        for (int i = 0; i < 10; i++) {
//            dates.put("none", null);
            dates.put(format.format(cal.getTime()), cal.getTime());
            cal.add(Calendar.MONTH, 2);
        }
        return dates;
    }
    
    
    //---------------------------Main


    public static void main(String[] args) {
        initLF();
        final JXFrame frame = new JXFrame("SwingX :: Bar Renderer Demo", true);
        frame.add(new CustomComponentProviderDemo().createContent());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.pack();
//                frame.setSize(600, 400);
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
