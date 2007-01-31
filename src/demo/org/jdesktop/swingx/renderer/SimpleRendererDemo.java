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

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.AbstractListModel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.util.WindowUtils;

/**
 * A simple example about how to configure SwingX renderers.
 * 
 * @author Jeanette Winzenburg
 */
public class SimpleRendererDemo {
    private String dataSource = "resources/contributors.txt";
    private List<Contributor> contributors;
    private ListModel listModel;
    private TableModel tableModel;
    private DefaultMutableTreeNode rootNode;

    public SimpleRendererDemo() {
        try {
            initData();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * @return the component to show.
     */
    private Component createContent() {
        JXTable table = new JXTable(tableModel);
        table.setColumnControlVisible(true);
        table.getColumnExt(1).setToolTipText("Randomly generated - run again if you are disatisfied");
        table.packColumn(0, -1);
        JXList list = new JXList(listModel);
        JXTree tree = new JXTree(rootNode);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("JXTable", new JScrollPane(table));
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(new JScrollPane(list));
        splitPane.setRightComponent(new JScrollPane(tree));
        tabbedPane.addTab("JXList/JXTree", splitPane);
        // configure table, list, tree renderer to use the same string
        // representation 
        StringValue stringValue = new StringValue() {
            
            public String getString(Object value) {
                if (!(value instanceof Contributor)) return TO_STRING.getString(value);
                Contributor contributor = (Contributor) value;
                return contributor.lastName + ", " + contributor.firstName;
            }
            
        };
        table.setDefaultRenderer(Contributor.class, new DefaultTableRenderer(stringValue));
        list.setCellRenderer(new DefaultListRenderer(stringValue));
        tree.setCellRenderer(new DefaultTreeRenderer(stringValue));
        return tabbedPane;
    }

    
    
    /**
     * Create and fill a list of contributors from a resource and 
     * wrap view models around.
     * @throws IOException 
     * 
     */
    private void initData() throws IOException {
        contributors = new ArrayList<Contributor>();
        // fill the list from the resources
        readDataSource(contributors);
        // wrap a listModel around
        listModel = new AbstractListModel() {

            public Object getElementAt(int index) {
                if (index == 0) {
                    return "-- Contributors --";
                }
                return contributors.get(index - 1);
            }

            public int getSize() {
                return contributors.size() + 1;
            }
            
        };
        // generate a random number list
        final List<Number> merits = new ArrayList<Number>();
        // todo: following line produces a infinite loop - WHY?
//        for (Iterator iter = contributors.iterator(); iter.hasNext();) {
        for (int i = 0; i < contributors.size(); i++) {
            merits.add(new Double(Math.random() * 100).intValue());
        }
        // wrap a TableModel around
        tableModel = new AbstractTableModel() {

            public int getColumnCount() {
                return 2;
            }

            public int getRowCount() {
                return contributors.size();
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return contributors.get(rowIndex);
                case 1:
                    return merits.get(rowIndex);
                }
                return null;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return Contributor.class;
                case 1:
                    return Number.class;
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public String getColumnName(int column) {
                switch (column) {
                case 0:
                    return "Contributor";
                case 1:
                    return "Merits";
                }
                return super.getColumnName(column);
            }
        };
        // wrap a DefaultTreeNodes around 
        rootNode = new DefaultMutableTreeNode("Contributors");
        for (int i = 0; i < contributors.size(); i++) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(i) {

                @Override
                public Object getUserObject() {
                    return contributors.get((Integer) super.getUserObject());
                }
                
            };
            rootNode.add(node);
        }
        
    }
    
    private void readDataSource(List<Contributor> list) throws IOException {
        InputStream is = getClass().getResourceAsStream(dataSource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                list.add(new Contributor(line));
            }
        } finally {
            // do our best to close
            reader.close();
        }
    }
    
    public static class Contributor implements Comparable {
        private String firstName;
        private String lastName;
        private String userID;
        
        public Contributor(String rawData) {
            setData(rawData);
        }

        /**
         * @param rawData
         */
        private void setData(String rawData) {
            if (rawData == null) {
                lastName = " <unknown> ";
                return;
            }
            StringTokenizer tokenizer = new StringTokenizer(rawData);
            try {
               firstName = tokenizer.nextToken();
               lastName = tokenizer.nextToken();
               userID = tokenizer.nextToken();
            } catch (Exception ex) {
                // don't care ...
            }
            
        }

        public int compareTo(Object o) {
            if (!(o instanceof Contributor)) return -1;
            return lastName.compareTo(((Contributor) o).lastName);
        }
    }

    //---------------------------Main

    public static void main(String[] args) {
        final JXFrame frame = new JXFrame("SwingX :: Simple Renderer Demo", true);
        frame.add(new SimpleRendererDemo().createContent());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.pack();
                frame.setSize(400, 300);
                frame.setLocation(WindowUtils.getPointForCentering(frame));
                frame.setVisible(true);
            }
        });        
    }

}
