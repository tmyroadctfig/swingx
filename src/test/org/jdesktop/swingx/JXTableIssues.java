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
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.decorator.SortKey;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.CellEditorReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.SerializableSupport;

/**
 * Test to exposed known issues of <code>JXTable</code>.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
public class JXTableIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTableIssues.class
            .getName());

    
    
    /**
     * test if created a new instance of the renderer. While the old
     * assertions are true, it's useless with swingx renderers: the renderer is
     * a new instance but not specialized to boolean. So added an assert
     * for the actual component type. Which fails for now - need to 
     * think if we really need the functionality!
     *
     */
    public void testNewRendererInstance() {
        JXTable table = new JXTable();
        TableCellRenderer newRenderer = table.getNewDefaultRenderer(Boolean.class);
        TableCellRenderer sharedRenderer = table.getDefaultRenderer(Boolean.class);
        // following assertions are useful only with core renderers 
        // (they differ by type on the renderer level)
        assertNotNull(newRenderer);
        assertNotSame("new renderer must be different from shared", sharedRenderer, newRenderer);
        assertNotSame("new renderer must be different from object renderer", 
                table.getDefaultRenderer(Object.class), newRenderer);
        Component comp = newRenderer.getTableCellRendererComponent(table, Boolean.TRUE, false, false, -1, -1);
        assertTrue("Boolean rendering component is expected to be a Checkbox by default" +
                        "\n but is " + comp.getClass(), 
                comp instanceof AbstractButton);
    }



    /**
     * Issue #4614616: editor lookup broken for interface types.
     * With editors (vs. renderers), the solution is not obvious -
     * interfaces can't be instantiated. As a consequence, the
     * GenericEditor can't cope (returns null as component which
     * it must not but that's another issue).
     *  
     */
    public void testNPEEditorForInterface() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Comparable.class;
            }
            
        };
        JXTable table = new JXTable(model);
        table.prepareEditor(table.getCellEditor(0, 0), 0, 0);
    }

    /**
     * Issue 373-swingx: table must unsort column on sortable change.
     *
     * Here we test if switching sortable to false on the sorted column
     * resets the sorting, special case hidden column. This fails 
     * because columnModel doesn't fire property change events for
     * hidden columns (see Issue #??-swingx).
     * 
     */
    public void testTableUnsortedColumnOnHiddenColumnSortableChange() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        Object identifier = columnExt.getIdentifier();
        table.toggleSortOrder(identifier);
        assertTrue(table.getSortOrder(identifier).isSorted());
        columnExt.setVisible(false);
        assertTrue(table.getSortOrder(identifier).isSorted());
        columnExt.setSortable(false);
        assertFalse("table must have unsorted column on sortable change", 
                table.getSortOrder(identifier).isSorted());
    }

    /**
     * Not defined: what should happen if the edited column is hidden? 
     * For sure, editing must be terminated - but canceled or stopped?
     * 
     * Here we test if the table is not editing after editable property
     * of the currently edited column is changed to false.
     */
    public void testTableNotEditingOnColumnVisibleChange() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingColumn());
        columnExt.setVisible(false);
        assertFalse("table must have terminated edit",table.isEditing());
        fail("forcing a fail - cancel editing is a side-effect of removal notification");
    }
   
    
    /**
     * Issue 372-swingx: table must cancel edit if column property 
     *   changes to not editable.
     * Here we test if the table actually canceled the edit.
     */
    public void testTableCanceledEditOnColumnEditableChange() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingColumn());
        TableCellEditor editor = table.getCellEditor();
        CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        columnExt.setEditable(false);
        // sanity
        assertFalse(table.isCellEditable(0, 0));
        assertEquals("editor must have fired canceled", 1, report.getCanceledEventCount());
        assertEquals("editor must not have fired stopped",0, report.getStoppedEventCount());
    }
    
    /**
     * a quick sanity test: reporting okay?. 
     * (doesn't belong here, should test the tools 
     * somewhere else)
     *
     */
    public void testCellEditorFired() {
        JXTable table = new JXTable(10, 2);
        table.editCellAt(0, 0);
        CellEditorReport report = new CellEditorReport();
        TableCellEditor editor = table.getCellEditor();
        editor.addCellEditorListener(report);
        editor.cancelCellEditing();
        assertEquals("total count must be equals to canceled",
                report.getCanceledEventCount(), report.getEventCount());
        assertEquals("editor must have fired canceled", 1, report.getCanceledEventCount());
        assertEquals("editor must not have fired stopped", 0, report.getStoppedEventCount());
        report.clear();
        assertEquals("canceled cleared", 0, report.getCanceledEventCount());
        assertEquals("total cleared", 0, report.getStoppedEventCount());
        // same cell, same editor
        table.editCellAt(0, 0);
        editor.stopCellEditing();
        assertEquals("total count must be equals to stopped",
                report.getStoppedEventCount(), report.getEventCount());
        assertEquals("editor must not have fired canceled", 0, report.getCanceledEventCount());
        // JW: surprising... it really fires twice?
        assertEquals("editor must have fired stopped", 1, report.getStoppedEventCount());
        
    }
    /**
     * Issue #359-swing: find suitable rowHeight.
     * 
     * Text selection in textfield has row of metrics.getHeight.
     * Suitable rowHeight should should take border into account:
     * for a textfield that's the metrics height plus 2.
     */
    public void testRowHeightFontMetrics() {
        JXTable table = new JXTable(10, 2);
        TableCellEditor editor = table.getCellEditor(1, 1);
        Component comp = table.prepareEditor(editor, 1, 1);
        assertEquals(comp.getPreferredSize().height, table.getRowHeight());
    }
    
    /**
     * Issue #349-swingx: table not serializable
     * 
     * Part of the problem is in TableRolloverController.
     *
     */
    public void testSerializationRollover() {
        JXTable table = new JXTable();
        try {
            SerializableSupport.serialize(table);
        } catch (IOException e) {
            fail("not serializable " + e);
        } catch (ClassNotFoundException e) {
            fail("not serializable " + e);
        }
    }

    /**
     * Issue #349-swingx: table not serializable
     * 
     * Part of it seems to be in BoundAction. 
     *
     */
    public void testSerializationRolloverFalse() {
        JXTable table = new JXTable();
        table.setRolloverEnabled(false);
        ActionMap actionMap = table.getActionMap();
        Object[] keys = actionMap.keys();
        for (int i = 0; i < keys.length; i++) {
            if (actionMap.get(keys[i]) instanceof BoundAction) {
                actionMap.remove(keys[i]);
            }
        }
        try {
            SerializableSupport.serialize(table);
        } catch (IOException e) {
            fail("not serializable " + e);
        } catch (ClassNotFoundException e) {
            fail("not serializable " + e);
        }
    }

    /**
     * Issue #349-swingx: table not serializable
     * 
     * Part of it seems to be in JXTableHeader. 
     *
     */
    public void testSerializationTableHeader() {
        JXTableHeader table = new JXTableHeader();
        try {
            SerializableSupport.serialize(table);
        } catch (IOException e) {
            fail("not serializable " + e);
        } catch (ClassNotFoundException e) {
            fail("not serializable " + e);
        }
    }


    /**
     * Issue??-swingx: turn off scrollbar doesn't work if the
     *   table was initially in autoResizeOff mode.
     *   
     * Problem with state management.  
     *
     */
    public void testHorizontalScrollEnabled() {
        JXTable table = new JXTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        assertEquals("horizontalScroll must be on", true, table.isHorizontalScrollEnabled());
        table.setHorizontalScrollEnabled(false);
        assertEquals("horizontalScroll must be off", false, table.isHorizontalScrollEnabled());
    }
    /**
     * we have a slight inconsistency in event values: setting the
     * client property to null means "false" but the event fired
     * has the newValue null.
     *
     * The way out is to _not_ set the client prop manually, always go
     * through the property setter.
     */
    public void testClientPropertyNull() {
        JXTable table = new JXTable();
        // sanity assert: setting client property set's property
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.putClientProperty("terminateEditOnFocusLost", null);
        assertFalse(table.isTerminateEditOnFocusLost());
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("terminateEditOnFocusLost"));
        assertEquals(false, report.getLastNewValue("terminateEditOnFocusLost"));
    }
    /**
     * JXTable has responsibility to guarantee usage of 
     * TableColumnExt comparator and update the sort if
     * the columns comparator changes.
     * 
     */
    public void testComparatorToPipelineDynamic() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
        columnX.setComparator(Collator.getInstance());
        // invalid assumption .. only the comparator must be used.
//        assertEquals("interactive sorter must be same as sorter in column", 
//                columnX.getSorter(), table.getFilters().getSorter());
        SortKey sortKey = SortKey.getFirstSortKeyForColumn(table.getFilters().getSortController().getSortKeys(), 0);
        assertNotNull(sortKey);
        assertEquals(columnX.getComparator(), sortKey.getComparator());
       
    }


    /**
     * Issue #256-swingX: viewport - toggle track height must
     * revalidate.
     * 
     * PENDING JW: the visual test looks okay - probably something wrong with the
     * test setup ... invoke doesn't help
     * 
     */
    public void testToggleTrackViewportHeight() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run trackViewportHeight - headless environment");
            return;
        }
        final JXTable table = new JXTable(10, 2);
        table.setFillsViewportHeight(true);
        final Dimension tablePrefSize = table.getPreferredSize();
        JScrollPane scrollPane = new JScrollPane(table);
        JXFrame frame = wrapInFrame(scrollPane, "");
        frame.setSize(500, tablePrefSize.height * 2);
        frame.setVisible(true);
        assertEquals("table height be equal to viewport", 
                table.getHeight(), scrollPane.getViewport().getHeight());
        table.setFillsViewportHeight(false);
        assertEquals("table height be equal to table pref height", 
                tablePrefSize.height, table.getHeight());
 
        
    }

    
//-------------------- adapted jesse wilson: #223


    /**
     * Enhancement: modifying (= filtering by resetting the content) should keep 
     * selection
     * 
     */
    public void testModifyTableContentAndSelection() {
        CompareTableBehaviour compare = new CompareTableBehaviour(new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" });
        compare.table.getSelectionModel().setSelectionInterval(2, 5);
        Object[] selectedObjects = new Object[] { "C", "D", "E", "F" };
        assertSelection(compare.tableModel, compare.table.getSelectionModel(), selectedObjects);
        compare.tableModel.setContents(new Object[] { "B", "C", "D", "F", "G", "H" });
        Object[] selectedObjectsAfterModify = (new Object[] { "C", "D", "F" });
        assertSelection(compare.tableModel, compare.table.getSelectionModel(), selectedObjectsAfterModify);
    }
    
    /**
     * Enhancement: modifying (= filtering by resetting the content) should keep 
     * selection
     */
    public void testModifyXTableContentAndSelection() {
        CompareTableBehaviour compare = new CompareTableBehaviour(new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" });
        compare.xTable.getSelectionModel().setSelectionInterval(2, 5);
        Object[] selectedObjects = new Object[] { "C", "D", "E", "F" };
        assertSelection(compare.tableModel, compare.xTable.getSelectionModel(), selectedObjects);
        compare.tableModel.setContents(new Object[] { "B", "C", "D", "F", "G", "H" });
        Object[] selectedObjectsAfterModify = (new Object[] { "C", "D", "F" });
        assertSelection(compare.tableModel, compare.xTable.getSelectionModel(), selectedObjectsAfterModify);
    }
   
    
    /**
     * test: deleting row below selection - should not change
     */
    public void testDeleteRowBelowSelection() {
        CompareTableBehaviour compare = new CompareTableBehaviour(new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" });
        compare.table.getSelectionModel().setSelectionInterval(2, 5);
        compare.xTable.getSelectionModel().setSelectionInterval(2, 5);
        Object[] selectedObjects = new Object[] { "C", "D", "E", "F" };
        assertSelection(compare.tableModel, compare.table.getSelectionModel(), selectedObjects);
        assertSelection(compare.tableModel, compare.xTable.getSelectionModel(), selectedObjects);
        compare.tableModel.removeRow(compare.tableModel.getRowCount() - 1);
        assertSelection(compare.tableModel, compare.table.getSelectionModel(), selectedObjects);
        assertSelection(compare.tableModel, compare.xTable.getSelectionModel(), selectedObjects);
    }
    
    /**
     * test: deleting last row in selection - should remove last item from selection. 
     */
    public void testDeleteLastRowInSelection() {
        CompareTableBehaviour compare = new CompareTableBehaviour(new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" });
        compare.table.getSelectionModel().setSelectionInterval(7, 8);
        compare.xTable.getSelectionModel().setSelectionInterval(7, 8);
        Object[] selectedObjects = new Object[] { "H", "I" };
        assertSelection(compare.tableModel, compare.table.getSelectionModel(), selectedObjects);
        assertSelection(compare.tableModel, compare.xTable.getSelectionModel(), selectedObjects);
        compare.tableModel.removeRow(compare.tableModel.getRowCount() - 1);
        Object[] selectedObjectsAfterDelete = new Object[] { "H" };
        assertSelection(compare.tableModel, compare.table.getSelectionModel(), selectedObjectsAfterDelete);
        assertSelection(compare.tableModel, compare.xTable.getSelectionModel(), selectedObjectsAfterDelete);
    }
     
    private void assertSelection(TableModel tableModel, ListSelectionModel selectionModel, Object[] expected) {
        List selected = new ArrayList();
        for(int r = 0; r < tableModel.getRowCount(); r++) {
            if(selectionModel.isSelectedIndex(r)) selected.add(tableModel.getValueAt(r, 0));
        }
        
        List expectedList = Arrays.asList(expected);
        assertEquals("selected Objects must be as expected", expectedList, selected);
    
    }

    
    public void interactiveDeleteRowAboveSelection() {
        CompareTableBehaviour compare = new CompareTableBehaviour(new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" });
        compare.table.getSelectionModel().setSelectionInterval(2, 5);
        compare.xTable.getSelectionModel().setSelectionInterval(2, 5);
        JComponent box = createContent(compare, createRowDeleteAction(0, compare.tableModel));
        JFrame frame = wrapInFrame(box, "delete above selection");
        frame.setVisible(true);
    }

    public void interactiveDeleteRowBelowSelection() {
        CompareTableBehaviour compare = new CompareTableBehaviour(new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" });
        compare.table.getSelectionModel().setSelectionInterval(6, 7);
        compare.xTable.getSelectionModel().setSelectionInterval(6, 7);
        JComponent box = createContent(compare, createRowDeleteAction(-1, compare.tableModel));
        JFrame frame = wrapInFrame(box, "delete below selection");
        frame.setVisible(true);
        
        
    }
    /**
     * Issue #370-swingx: "jumping" selection while dragging.
     * 
     */
    public void interactiveExtendSelection() {
        final UpdatingTableModel model = new UpdatingTableModel();
        JXTable table = new JXTable(model);
        // Swing Timer - EDT in Timer
        ActionListener l = new ActionListener() {
            int i = 0;

            public void actionPerformed(ActionEvent e) {
                model.updateCell(i++ % 10);
                
            }
            
        };
        Timer timer = new Timer(1000, l);
        timer.start();
        JXFrame frame = wrapWithScrollingInFrame(table, "#370 - extend selection by dragging");
        frame.setVisible(true);
    }
    
    /** 
     * Simple model for use in continous update tests. 
     * Issue #370-swingx: jumping selection on dragging.
     */
    private class UpdatingTableModel extends AbstractTableModel {

        private int[][] data = new int[10][5];

        public UpdatingTableModel() {
            for (int row = 0; row < data.length; row++) {
                fillRow(row);
            }
        }

        public int getRowCount() {
            return 10;
        }

        public int getColumnCount() {
            return 5;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        /**
         * update first column of row on EDT.
         * @param row
         */
        public void invokeUpdateCell(final int row) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateCell(row);
                }
            });
        }

        /**
         * update first column of row. Sorting on any column except the first
         * doesn't change row sequence - shouldn't interfere with selection
         * extension.
         * 
         * @param row
         */
        public void updateCell(final int row) {
            updateCell(row, 0);
            fireTableCellUpdated(row, 0);
        }

        public void fillRow(int row) {
            for (int col = 0; col < data[row].length; col++) {
                updateCell(row, col);
            }
        }

        /**
         * Fills the given cell with random value.
         * @param row
         * @param col
         */
        private void updateCell(int row, int col) {
            data[row][col] = (int) Math.round(Math.random() * 200);
        }
    }
  

    /**
     * Issue #282-swingx: compare disabled appearance of
     * collection views.
     *
     */
    public void interactiveDisabledCollectionViews() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        table.setEnabled(false);
        final JXList list = new JXList(new String[] {"one", "two", "and something longer"});
        list.setEnabled(false);
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
    public void interactiveDataChanged() {
        final DefaultTableModel model = createAscendingModel(0, 10, 5, false);
        JXTable xtable = new JXTable(model);
        xtable.setRowSelectionInterval(0, 0);
//        JTable table = new JTable(model);
//        table.setRowSelectionInterval(0, 0);
        AbstractAction action = new AbstractAction("fire dataChanged") {

            public void actionPerformed(ActionEvent e) {
                model.fireTableDataChanged();
                
            }
            
        };
//        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "selection after data changed");
        JXFrame frame = wrapWithScrollingInFrame(xtable, "selection after data changed");
        addAction(frame, action);
        frame.setVisible(true);
        
    }
    private JComponent createContent(CompareTableBehaviour compare, Action action) {
        JComponent box = new JPanel(new BorderLayout());
        box.add(new JScrollPane(compare.table), BorderLayout.WEST);
        box.add(new JScrollPane(compare.xTable), BorderLayout.EAST);
        box.add(new JButton(action), BorderLayout.SOUTH);
        return box;
    }

    private Action createRowDeleteAction(final int row, final ReallySimpleTableModel simpleTableModel) {
        Action delete = new AbstractAction("DeleteRow " + ((row < 0) ? "last" : "" + row)) {

            public void actionPerformed(ActionEvent e) {
                int rowToDelete = row;
                if (row < 0) {
                    rowToDelete = simpleTableModel.getRowCount() - 1;
                }
                if ((rowToDelete < 0) || (rowToDelete >= simpleTableModel.getRowCount())) {
                    return;
                }
                 simpleTableModel.removeRow(rowToDelete);
                 if (simpleTableModel.getRowCount() == 0) {
                     setEnabled(false);
                 }
            }
            
        };
        return delete;
    }

    public static class CompareTableBehaviour {
      
        public ReallySimpleTableModel tableModel;
        public JTable table;
        public JXTable xTable;

        public CompareTableBehaviour(Object[] model) {
            tableModel = new ReallySimpleTableModel();
            tableModel.setContents(model);
 
            table = new JTable(tableModel);
            xTable = new JXTable(tableModel);
          table.getColumnModel().getColumn(0).setHeaderValue("JTable");
          xTable.getColumnModel().getColumn(0).setHeaderValue("JXTable");
        }
    };
    
    /**
     * A one column table model where all the data is in an Object[] array.
     */
    static class ReallySimpleTableModel extends AbstractTableModel {
        private List contents = new ArrayList();
        public void setContents(List contents) {
            this.contents.clear();
            this.contents.addAll(contents);
            fireTableDataChanged();
        }
        public void setContents(Object[] contents) {
            setContents(Arrays.asList(contents));
        }
        public void removeRow(int row) {
            contents.remove(row);
            fireTableRowsDeleted(row, row);
        }
        public int getRowCount() {
            return contents.size();
        }
        public int getColumnCount() {
            return 1;
        }
        public Object getValueAt(int row, int column) {
            if(column != 0) throw new IllegalArgumentException();
            return contents.get(row);
        }
    }

    
    
//--------------------    
    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first column
     * starting from startRow.
     * @param startRow the value of the first row
     * @param rowCount the number of rows
     * @return
     */
    private DefaultTableModel createAscendingModel(int startRow, final int rowCount, 
            final int columnCount, boolean fillLast) {
        DefaultTableModel model = new DefaultTableModel(rowCount, columnCount) {
            public Class getColumnClass(int column) {
                Object value = rowCount > 0 ? getValueAt(0, column) : null;
                return value != null ? value.getClass() : super.getColumnClass(column);
            }
        };
        int filledColumn = fillLast ? columnCount - 1 : 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, filledColumn);
        }
        return model;
    }
    
    
    private DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 5);
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }
    

    /**
     * Issue #??: JXTable pattern search differs from 
     * PatternHighlighter/Filter. 
     * 
     * Fixing the issue (respect the pattern as is by calling 
     * pattern.matcher().matches instead of the find()) must 
     * make sure that the search methods taking the string 
     * include wildcards.
     *
     *  Note: this method passes as long as the issue is not
     *  fixed!
     *  
     *  TODO: check status!
     */
    public void testWildCardInSearchByString() {
        JXTable table = new JXTable(createAscendingModel(0, 11));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        int found = table.getSearchable().search(lastName, -1);
        assertEquals("found must be equal to row", row, found);
        found = table.getSearchable().search(lastName, found);
        assertEquals("search must succeed", 10, found);
    }

    /**
     * Issue #445-swingx: sort icon not updated on programatic sorting.
     *
     */
    public void interactiveHeaderUpdateOnSorting() {
        final JXTable table = new JXTable(createAscendingModel(0, 10));
        Action action = new AbstractActionExt("toggle sorter order") {

            public void actionPerformed(ActionEvent e) {
                table.toggleSortOrder(0);
                
            }
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "sort icon");
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    public static void main(String args[]) {
        JXTableIssues test = new JXTableIssues();
        try {
          test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Siz.*");
         //   test.runInteractiveTests("interactive.*Render.*");
//            test.runInteractiveTests(".*DataChanged.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }
}
