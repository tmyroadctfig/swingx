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
package org.jdesktop.swingx.decorator;

import java.awt.Color;

import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Test <code>ComponentAdapter</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class ComponentAdapterTest extends TestCase {
 
    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on ComponentAdapter.
     */
    public void testTreeGetString() {
        JXTreeT list = new JXTreeT(AncientSwingTeam.createNamedColorTreeModel());
        list.expandAll();
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        list.setCellRenderer(new DefaultTreeRenderer(sv));
        String text = sv.getString(((DefaultMutableTreeNode) list.getPathForRow(2).getLastPathComponent()).getUserObject());
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getString());
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on ComponentAdapter.
     */
    public void testTreeGetFilteredString() {
        JXTreeT list = new JXTreeT(AncientSwingTeam.createNamedColorTreeModel());
        list.expandAll();
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        list.setCellRenderer(new DefaultTreeRenderer(sv));
        String text = sv.getString(((DefaultMutableTreeNode) list.getPathForRow(2).getLastPathComponent()).getUserObject());
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getFilteredStringAt(2, 0));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on ComponentAdapter.
     */
    public void testTreeGetStringColumn() {
        JXTreeT list = new JXTreeT(AncientSwingTeam.createNamedColorTreeModel());
        list.expandAll();
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        list.setCellRenderer(new DefaultTreeRenderer(sv));
        String text = sv.getString(((DefaultMutableTreeNode) list.getPathForRow(2).getLastPathComponent()).getUserObject());
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getString(0));
    }

    /**
     * Subclass to access ComponentAdapter.
     * @author Jeanette Winzenburg
     */
    public static class JXTreeT extends JXTree {

        public JXTreeT(TreeModel model) {
            super(model);
        }

        @Override
        public ComponentAdapter getComponentAdapter(int row) {
            return super.getComponentAdapter(row);
        }

    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on ComponentAdapter.
     */
    public void testListGetString() {
        JXListT list = new JXListT(AncientSwingTeam.createNamedColorListModel());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        list.setCellRenderer(new DefaultListRenderer(sv));
        String text = sv.getString(list.getElementAt(2));
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getString());
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on ComponentAdapter.
     */
    public void testListGetFilteredString() {
        JXListT list = new JXListT(AncientSwingTeam.createNamedColorListModel());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        list.setCellRenderer(new DefaultListRenderer(sv));
        String text = sv.getString(list.getElementAt(2));
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getFilteredStringAt(2, 0));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on ComponentAdapter.
     */
    public void testListGetStringColumn() {
        JXListT list = new JXListT(AncientSwingTeam.createNamedColorListModel());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        list.setCellRenderer(new DefaultListRenderer(sv));
        String text = sv.getString(list.getElementAt(2));
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getString(0));
    }

    /**
     * Subclass to access ComponentAdapter.
     * @author Jeanette Winzenburg
     */
    public static class JXListT extends JXList {

        public JXListT(ListModel model) {
            super(model);
        }

        @Override
        public ComponentAdapter getComponentAdapter(int row) {
            return super.getComponentAdapter(row);
        }

    }
    
    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on ComponentAdapter.
     */
    public void testTableGetString() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 2));
        ComponentAdapter adapter = table.getComponentAdapter(2, 2);
        assertEquals(text, adapter.getString());
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on ComponentAdapter.
     */
    public void testTableGetFilteredString() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 2));
        ComponentAdapter adapter = table.getComponentAdapter(2, 2);
        assertEquals(text, adapter.getFilteredStringAt(2, 2));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on ComponentAdapter.
     */
    public void testTableGetStringColumn() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 2));
        ComponentAdapter adapter = table.getComponentAdapter(2, 2);
        assertEquals(text, adapter.getString(2));
    }

    /**
     * Overridden to get access to componentAdapter.
     * 
     * @author Jeanette Winzenburg
     */
    public static class JXTableT extends JXTable {

        public JXTableT(TableModel model) {
            super(model);
        }

        @Override
        public ComponentAdapter getComponentAdapter(int row, int column) {
            return super.getComponentAdapter(row, column);
        }
        
    }
    /**
     * Issue #??- ComponentAdapter's default implementation does not
     *    return the value at the adapter's view state.
     * 
     * Clarified the documentation: the assumption for the base implementation
     * is that model coordinates == view coordinates, that is it's up to 
     * subclasses to implement the model correctly if they support different
     * coordinate systems.
     *
     */
    public void testComponentAdapterCoordinates() {
        final JXTable table = new JXTable(createAscendingModel(0, 10));
        Object originalFirstRowValue = table.getValueAt(0,0);
        Object originalLastRowValue = table.getValueAt(table.getRowCount() - 1, 0);
        assertEquals("view row coordinate equals model row coordinate", 
                table.getModel().getValueAt(0, 0), originalFirstRowValue);
        // sort first column - actually does not change anything order 
        table.toggleSortOrder(0);
        // sanity asssert
        assertEquals("view order must be unchanged ", 
                table.getValueAt(0, 0), originalFirstRowValue);
        // invert sort
        table.toggleSortOrder(0);
        // sanity assert
        assertEquals("view order must be reversed changed ", 
                table.getValueAt(0, 0), originalLastRowValue);
        ComponentAdapter adapter = new ComponentAdapter(table) {

            @Override
            public String getColumnIdentifier(int columnIndex) {
                return null;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Object getFilteredValueAt(int row, int column) {
                return getValueAt(table.convertRowIndexToModel(row), column);
//                return table.getValueAt(row, modelToView(column)); // in view coordinates
            }


            @Override
            public Object getValueAt(int row, int column) {
                return table.getModel().getValueAt(row, column);
            }

            
            @Override
            public Object getValue() {
                return getValueAt(table.convertRowIndexToModel(row), viewToModel(column));
            }

            @Override
            public boolean hasFocus() {
                return false;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public boolean isEditable() {
                return false;
            }
            
            @Override
            public boolean isSelected() {
                return false;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
              
            }
            
        };
        assertEquals("adapter filteredValue expects row view coordinates", 
                table.getValueAt(0, 0), adapter.getFilteredValueAt(0, 0));
        // adapter coordinates are view coordinates
        adapter.row = 0;
        adapter.column = 0;
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                table.getValueAt(0, 0), adapter.getValue());
        
        assertEquals(adapter.getFilteredValueAt(0, adapter.getColumnCount() -1), 
                adapter.getValue(adapter.getColumnCount()-1));
        
        
    }

    private DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 5);
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }

    public void testDummy() {
        
    }
}
