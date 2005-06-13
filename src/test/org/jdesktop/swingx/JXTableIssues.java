/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.decorator.ComponentAdapter;

/**
 * @author Jeanette Winzenburg
 */
public class JXTableIssues extends InteractiveTestCase {



    public JXTableIssues() {
        super("JXTableIssues");
        // TODO Auto-generated constructor stub
    }

    
    public void testComponentAdapterCoordinates() {
        JXTable table = new JXTable(createModel(0, 10));
        Object originalFirstRowValue = table.getValueAt(0,0);
        Object originalLastRowValue = table.getValueAt(table.getRowCount() - 1, 0);
        assertEquals("view row coordinate equals model row coordinate", 
                table.getModel().getValueAt(0, 0), originalFirstRowValue);
        // sort first column - actually does not change anything order 
        table.setSorter(0);
        // sanity asssert
        assertEquals("view order must be unchanged ", 
                table.getValueAt(0, 0), originalFirstRowValue);
        // invert sort
        table.setSorter(0);
        // sanity assert
        assertEquals("view order must be reversed changed ", 
                table.getValueAt(0, 0), originalLastRowValue);
        ComponentAdapter adapter = table.getComponentAdapter();
        assertEquals("adapter filteredValue expects view coordinates", 
                table.getValueAt(0, 0), adapter.getFilteredValueAt(0, 0));
        // adapter coordinates are view coordinates
        adapter.row = 0;
        adapter.column = 0;
        assertEquals("adapter filteredValue expects view coordinates", 
                table.getValueAt(0, 0), adapter.getValue());
        
        
    }
    
//-------------------- adapted jesse wilson: #223


    /**
     * Enhancement: modifying (= filtering by resetting the content) should keep 
     * selection
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
     * Issue #223: deleting row above selection does not
     * update the view selection correctly.
     */
    public void testDeleteRowAboveSelection() {
        CompareTableBehaviour compare = new CompareTableBehaviour(new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" });
        compare.table.getSelectionModel().setSelectionInterval(2, 5);
        compare.xTable.getSelectionModel().setSelectionInterval(2, 5);
        Object[] selectedObjects = new Object[] { "C", "D", "E", "F" };
        assertSelection(compare.tableModel, compare.table.getSelectionModel(), selectedObjects);
        assertSelection(compare.tableModel, compare.xTable.getSelectionModel(), selectedObjects);
        compare.tableModel.removeRow(0);
        assertSelection(compare.tableModel, compare.table.getSelectionModel(), selectedObjects);
        assertSelection(compare.tableModel, compare.xTable.getSelectionModel(), selectedObjects);
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
    
    private DefaultTableModel createModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 5);
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }
    
    /**
     * JW: Still needed? moved to main testCase?
     *
     */
    public void testNewRendererInstance() {
        JXTable table = new JXTable();
        TableCellRenderer newRenderer = table.getNewDefaultRenderer(Boolean.class);
        TableCellRenderer sharedRenderer = table.getDefaultRenderer(Boolean.class);
        assertNotNull(newRenderer);
        assertNotSame("new renderer must be different from shared", sharedRenderer, newRenderer);
        assertNotSame("new renderer must be different from object renderer", 
                table.getDefaultRenderer(Object.class), newRenderer);
    }

    /**
     * Issue #??: JXTable pattern search differs from 
     * PatternHighlighter/Filter.
     * 
     */
    public void testRespectPatternInSearch() {
        JXTable table = new JXTable(createModel(0, 11));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        int found = table.search(Pattern.compile(lastName), -1, false);
        assertEquals("found must be equal to row", row, found);
        found = table.search(Pattern.compile(lastName), found, false);
        assertEquals("search must fail", -1, found);
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
     */
    public void testWildCardInSearchByString() {
        JXTable table = new JXTable(createModel(0, 11));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        int found = table.search(lastName, -1);
        assertEquals("found must be equal to row", row, found);
        found = table.search(lastName, found);
        assertEquals("search must succeed", 10, found);
    }


    public static void main(String args[]) {
        JXTableIssues test = new JXTableIssues();
        try {
          test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Siz.*");
         //   test.runInteractiveTests("interactive.*Render.*");
         //   test.runInteractiveTests("interactive.*Toggle.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }
}
