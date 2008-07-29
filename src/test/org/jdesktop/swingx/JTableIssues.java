/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.CellEditorReport;
import org.jdesktop.test.ListSelectionReport;

/**
 * @author Jeanette Winzenburg
 */
public class JTableIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JTableIssues.class
            .getName());
    
    public static void main(String args[]) {
//      setSystemLF(true);
      JTableIssues test = new JTableIssues();
      try {
        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*ColumnControl.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    
    
    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     *
     * Hmm .. unexpected: we get two stopped?
     */
    public void testStopEditingCoreTable() {
        JTable table = new JTable(10, 2);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        // sanity
        assertFalse(report.hasEvents());
        table.getCellEditor().stopCellEditing();
        assertFalse("table must not be editing", table.isEditing());
        assertEquals("", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }

    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     *
     * Hmm .. unexpected: we get two stopped? 
     * Here: let the table prepare the editor (but not install)
     * 
     *  in this case the generic.stopCellEditing calls super 
     *  twice!
     */
    public void testStopEditingTableGenericPrepared() {
        JTable table = new JTable(10, 2);
        TableCellEditor direct = table.getDefaultEditor(Object.class);
        CellEditorReport report = new CellEditorReport();
        direct.addCellEditorListener(report);
        TableCellEditor editor = table.getCellEditor(0, 0);
        // sanity:
        assertSame(direct, editor);
        assertFalse(report.hasEvents());
        table.prepareEditor(editor, 0, 0);
        // sanity: prepare did not fire ..
        assertFalse(report.hasEvents());
        editor.stopCellEditing();
        assertEquals("prepared - must have fired exactly one event", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }

    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     *
     * Hmm .. unexpected: we get two stopped? 
     * Here: get the table's editor and prepare manually.
     * this test passes ... what is in the prepare which 
     * fires?
     * In this case it calls super.stop once only ... 
     * 
     */
    public void testStopEditingTableGenericGetComp() {
        JTable table = new JTable(10, 2);
        TableCellEditor editor = table.getCellEditor(0, 0);
        CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        editor.getTableCellEditorComponent(table, "something", false, 0, 0);
        editor.stopCellEditing();
        assertEquals("", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }

    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     * 
     * Core issue: 
     * Table's generic editor must not return a null component.
     */
    public void testTableGenericEditorNullTable() {
        JTable table = new JTable(10, 2);
        TableCellEditor editor = table.getCellEditor(0, 0);
        Component comp = editor.getTableCellEditorComponent(
                null, "something", false, 0, 0);
        assertNotNull("editor must not return null component", comp);
    }
    
    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     *
     * Hmm .. unexpected: we get two stopped? 
     * 
     * Here: Must not throw NPE if calling stopCellEditing without previous 
     *   getXXComponent.
     */
    public void testTableGenericEditorIsolatedNPE() {
        JTable table = new JTable(10, 2);
        TableCellEditor editor = table.getCellEditor(0, 0);
        editor.stopCellEditing();
    }
    
    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     *
     * Hmm .. unexpected: we get two stopped? Test 
     * DefaultCellEditor - okay.
     */
    public void testStopEditingDefaultCellEditor() {
        TableCellEditor editor = new DefaultCellEditor(new JTextField());
        CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        editor.stopCellEditing();
        assertEquals("", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }


    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    public void testNullGridColor() {
        JTable table = new JTable();
//        assertNotNull(UIManager.getColor("Table.gridColor"));
        assertNotNull(table.getGridColor());
        assertEquals(UIManager.getColor("Table.gridColor"), table.getGridColor());
        table.setGridColor(null);
    }

    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    public void testNullSelectionBackground() {
        JTable table = new JTable();
        assertNotNull(table.getSelectionBackground());
        table.setSelectionBackground(null);
    }
    
    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    public void testNullSelectionForeground() {
        JTable table = new JTable();
        table.setSelectionForeground(null);
    }
    /**
     * Issue #282-swingx: compare disabled appearance of
     * collection views.
     *
     */
    public void testDisabledRenderer() {
        JList list = new JList(new Object[] {"one", "two"});
        list.setEnabled(false);
        // sanity
        assertFalse(list.isEnabled());
        Component comp = list.getCellRenderer().getListCellRendererComponent(list, "some", 0, false, false);
        assertEquals(list.isEnabled(), comp.isEnabled());
        JTable table = new JTable(10, 2);
        table.setEnabled(false);
        // sanity
        assertFalse(table.isEnabled());
        comp = table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals(table.isEnabled(), comp.isEnabled());
    }

    /**
     * Characterization method: table.addColumn and invalid modelIndex.
     * 
     * Doesn't blow up because DefaultTableModel.getColumnName is lenient,
     * that is has no precondition on the index.
     *
     */
    public void testAddColumn() {
        JTable table = new JTable(0, 0);
        table.addColumn(new TableColumn(1));
    }
    
//----------------------- interactive
 
    
    /**
     * Core Issue ??: standalone table header throws NPE on mouse
     * events.
     * 
     * Base reason is that the ui assume a not-null table at varying
     * places in their code.
     * 
     * Reason is an unsafe implementation of viewIndexForColumn. Unconditionally
     * queries the table for index conversion.
     */
    public void interactiveNPEStandaloneHeader() {
        JXTable table = new JXTable(new AncientSwingTeam());
        JXTableHeader header = new JXTableHeader(table.getColumnModel());
        JXFrame frame = showWithScrollingInFrame(header, "Standalone header: NPE on mouse gestures");
        addMessage(frame, "exact place/gesture is LAF dependent. Base error is to assume header.getTable() != null");
    }

    /**
     * 
     * 
     * 
     */
    public void interactiveToolTipOverEmptyCell() {
        final DefaultTableModel model = new DefaultTableModel(50, 2);
        model.setValueAt("not empty", 0, 0);
        final JTable table = new JTable(model) {
            
            @Override
            public String getToolTipText(MouseEvent event) {
                int column = columnAtPoint(event.getPoint());
                if (column == 0) {
                    return "first column";
                }
                return null;
            }

            @Override
            public Point getToolTipLocation(MouseEvent event) {
                int column = columnAtPoint(event.getPoint());
                int row = rowAtPoint(event.getPoint());
                Rectangle cellRect = getCellRect(row, column, false);
                if (!getComponentOrientation().isLeftToRight()) {
                    cellRect.translate(cellRect.width, 0);
                }
                // PENDING JW: otherwise we get a small (borders only) tooltip for null
                // core issue? yeah ... probably
//                return getValueAt(row, column) == null ? null : cellRect.getLocation();
                return cellRect.getLocation();
            }
            

        };
        JXFrame frame = wrapWithScrollingInFrame(table, "Tooltip over empty");
        show(frame);
    }
    
    /**
     * forum: table does not scroll after setRowSelectionInterval?
     * 
     * 
     */
    public void interactiveAutoScroll() {
        final DefaultTableModel model = new DefaultTableModel(50, 2);
        final JTable table = new JTable(model);
        table.setAutoscrolls(true);
        Action action = new AbstractAction("select last row: scrolling?") {

            public void actionPerformed(ActionEvent e) {
                
                int selected = table.getRowCount() - 1;
                if (selected >= 0) {
                    table.setRowSelectionInterval(selected, selected);
                }
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "insert at selection");
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    /**
     * Issue #272-swingx: inserted row is selected.
     * Not a bug: documented behaviour of DefaultListSelectionModel.
     *
     */
    public void interactiveInsertAboveSelection() {
        final DefaultTableModel model = new DefaultTableModel(10, 2);
        final JTable table = new JTable(model);
        Action action = new AbstractAction("insertRow") {

            public void actionPerformed(ActionEvent e) {
                
                int selected = table.getSelectedRow();
                if (selected < 0) return;
                model.insertRow(selected, new Object[2]);
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "insert at selection");
        addAction(frame, action);
        frame.setVisible(true);
    }

    
    
    public void interactiveLeadAnchor() {
        final JTable table = new JTable(10, 3) {

            @Override
            public void tableChanged(TableModelEvent e) {
                super.tableChanged(e);
                if (isDataChanged(e) || isStructureChanged(e)) {
                    focusFirstCell();
                }
            }

            private void focusFirstCell() {
                if (getColumnCount() > 0) {
                    getColumnModel().getSelectionModel()
                            .removeSelectionInterval(0, 0);
                }
                if (getRowCount() > 0) {
                    getSelectionModel().removeSelectionInterval(0, 0);
                }

            }

            private boolean isDataChanged(TableModelEvent e) {
                return e.getType() == TableModelEvent.UPDATE
                        && e.getFirstRow() == 0
                        && e.getLastRow() == Integer.MAX_VALUE;
            }

            private boolean isStructureChanged(TableModelEvent e) {
                return e == null
                        || e.getFirstRow() == TableModelEvent.HEADER_ROW;
            }

        };
        JXFrame frame = wrapWithScrollingInFrame(table, "auto-lead - force in table subclass");
        Action toggleAction = new AbstractAction("Toggle TableModel") {

            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() > 0) {
                    table.setModel(new DefaultTableModel());
                } else {
                    table.setModel(new DefaultTableModel(10, 3));
                }
            }

        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

//---------------------- unit tests 
    
    /**
     * Issue #4614616: renderer lookup broken for interface types.
     * 
     */
    public void testNPERendererForInterface() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Comparable.class;
            }
            
        };
        JTable table = new JTable(model);
        table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
    }

    /**
     * Issue #4614616: editor lookup broken for interface types.
     * 
     */
    public void testNPEEditorForInterface() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Comparable.class;
            }
            
        };
        JTable table = new JTable(model);
        table.prepareEditor(table.getCellEditor(0, 0), 0, 0);
    }

    /**
     * isCellEditable is doc'ed as: if false, setValueAt 
     * will have no effect.
     * 
     * 
     */
    public void testSetValueDoNothing() {
        JTable table = new JTable(10, 3) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
        };
        Object value = table.getValueAt(0, 0);
        // sanity...
        assertFalse(table.isCellEditable(0, 0));
        table.setValueAt("wrong", 0, 0);
        assertEquals("value must not be changed", value, table.getValueAt(0, 0));
    }
    
    /**
     * Issue #272-swingx: inserted row is selected.
     * Not a bug: documented behaviour of DefaultListSelectionModel.
     *
     */
    public void testInsertBeforeSelected() {
        DefaultTableModel model = new DefaultTableModel(10, 2);
        JTable table = new JTable(model);
        table.setRowSelectionInterval(3, 3);
        model.insertRow(3, new Object[2]);
        int[] selected = table.getSelectedRows();
        assertEquals(1, selected.length);
    }

    /**
     * Issue #272-swingx: inserted row is selected.
     * Not a bug: documented behaviour of DefaultListSelectionModel.
     */
    public void testInsertBeforeSelectedSM() {
        DefaultListSelectionModel model = new DefaultListSelectionModel();
        model.setSelectionInterval(3, 3);
        model.insertIndexInterval(3, 1, true);
        int max = model.getMaxSelectionIndex();
        int min = model.getMinSelectionIndex();
        assertEquals(max, min);
    }

    /**
     * test contract: getColumn(int) throws ArrayIndexOutofBounds with 
     * invalid column index.
     * 
     * Subtle autoboxing issue: 
     * JTable has convenience method getColumn(Object) to access by 
     * identifier, but doesn't have delegate method to columnModel.getColumn(int)
     * Clients assuming the existence of a direct delegate no longer get a
     * compile-time error message in 1.5 due to autoboxing. 
     * Furthermore, the runtime exception is unexpected (IllegalArgument
     * instead of AIOOB).
     *
     */
    public void testTableColumnOffRange() {
        JTable table = new JTable(2, 1);
        try {
            table.getColumn(1);
            fail("accessing invalid column index must throw ArrayIndexOutofBoundExc");
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing: contracted runtime exception
        } catch (Exception e) {
           fail("unexpected exception: " + e + "\n" +
              "accessing invalid column index must throw ArrayIndexOutofBoundExc");
        }
    }

    
    public void testTableRowAtNegativePoint() {
        JTable treeTable = new JTable(1, 4);
        int negativeYRowHeight = - treeTable.getRowHeight();
        int negativeYRowHeightPlusOne = negativeYRowHeight + 1;
        int negativeYMinimal = -1;
        // just outside of negative row before first row
        assertEquals("negative y location rowheight " + negativeYRowHeight + " must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeight)));
        // just inside of negative row before first row
        assertEquals("negative y location " + negativeYRowHeightPlusOne +" must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeightPlusOne)));
        // just outside of first row
        assertEquals("minimal negative y location must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYMinimal)));
        
    }

    public void testLeadSelectionAfterStructureChanged() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public void fireTableRowsDeleted(int firstRow, int lastRow) {
                fireTableStructureChanged();
            }
            
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        JTable table = new JTable(model);
        int rowIndex = table.getRowCount() - 1;
        table.addRowSelectionInterval(rowIndex, rowIndex);
        model.removeRow(rowIndex);
        // JW: this was pre-1.5u5 (?), changed (1.5u6?) to return - 1
//        assertEquals("", rowIndex, table.getSelectionModel().getAnchorSelectionIndex());
        assertEquals("", -1, table.getSelectionModel().getAnchorSelectionIndex());
        ListSelectionReport report = new ListSelectionReport();
        table.getSelectionModel().addListSelectionListener(report);
    }

    /**
     * as of jdk1.5u6 the lead/anchor is no longer automatically set.
     * before (last code I saw is jdk1.5u4) - tableChanged would call
     * checkLeadAnchor after structureChanged. 
     * CheckLeadAnchor did set the lead/anchor
     * to the first row if count > 0.
     * 
     * Always: BasicTableUI repaints the lead-cell in focusGained.
     * 
     * Now: need to explicitly set _both_ anchor and lead to >= 0
     * need to set anchor first. Need to do so for both row/column selection model.
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     * 
     */
    public void testInitialLeadAnchor() throws InterruptedException, InvocationTargetException {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run testLeadAnchorOnFocusGained - headless environment");
            return;
        }
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public void fireTableRowsDeleted(int firstRow, int lastRow) {
                fireTableStructureChanged();
            }
            
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        final JTable table = new JTable(model);
        JFrame frame = new JFrame("anchor on focus");
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
        // JW: need to explicitly set _both_ anchor and lead to >= 0
        // need to set anchor first
//        table.getSelectionModel().setAnchorSelectionIndex(0);
//        table.getSelectionModel().setLeadSelectionIndex(0);
//        table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(0);
//        table.getColumnModel().getSelectionModel().setLeadSelectionIndex(0);

        table.requestFocus();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                assertTrue("table is focused ", table.hasFocus());
                assertEquals("anchor must be 0", 0, table.getSelectionModel().getAnchorSelectionIndex());
                assertEquals("lead must be 0", 0, table.getSelectionModel().getLeadSelectionIndex());

            }
        });
    }

    /**
     * as of jdk1.5u6 the lead/anchor is no longer automatically set.
     * before (last code I saw is jdk1.5u4) - tableChanged would call
     * checkLeadAnchor after structureChanged. CheckLeadAnchor did set the lead/anchor
     * to the first row if count > 0.
     * 
     * Always: BasicTableUI repaints the lead-cell in focusGained.
     * 
     * Now: need to explicitly set _both_ anchor and lead to >= 0
     * need to set anchor first. Need to do so for both row/column selection model.
     * 
     */
    public void testLeadAnchorAfterStructureChanged() {
        final JTable table = new JTable(10, 2);
        // JW: need to explicitly set _both_ anchor and lead to >= 0
        // need to set anchor first
        table.getSelectionModel().setAnchorSelectionIndex(0);
        table.getSelectionModel().setLeadSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
        // sanity...
        assertEquals("anchor must be 0", 0, table.getSelectionModel().getAnchorSelectionIndex());
        assertEquals("lead must be 0", 0, table.getSelectionModel().getLeadSelectionIndex());
        table.setModel(new DefaultTableModel(20, 3));
        // regression: lead/anchor unconditionally reset to -1 
        assertEquals("anchor must be 0", 0, table.getSelectionModel().getAnchorSelectionIndex());
        assertEquals("lead must be 0", 0, table.getSelectionModel().getLeadSelectionIndex());
        
    }

}
