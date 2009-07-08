/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DefaultRowSorter;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RepaintManager;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.CellEditorReport;
import org.jdesktop.test.ListSelectionReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.Test;

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
//        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*ColumnControl.*");
          test.runInteractiveTests("interactive.*NPE.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

  //---------------- core sorting 
    
  //------------------ testing core    
      
      @Test
      public void testSetRowSorterChangeNotification() {
          JTable table = new JTable(new AncientSwingTeam());
          PropertyChangeReport report = new PropertyChangeReport();
          table.addPropertyChangeListener(report);
          table.setRowSorter(new TableRowSorter<TableModel>(table.getModel()));
          TestUtils.assertPropertyChangeEvent(report, "rowSorter", null, table.getRowSorter());
      }
      
      /**
       * core issue: rowSorter replaced on setAutoCreateRowSorter even without change to flag.
       */
      @Test
      public void testSetAutoCreateRowSorter() {
          JTable table = new JTable();
          assertEquals("sanity: core table autoCreate off initially", false, table.getAutoCreateRowSorter());
          assertNull("sanity: core rowSorter is not created", table.getRowSorter());
          table.setAutoCreateRowSorter(true);
          assertNotNull("sanity: core rowSorter is created", table.getRowSorter());
          TableModel model = new AncientSwingTeam();
          table.setModel(model);
          RowSorter<?> sorter = table.getRowSorter();
          table.setAutoCreateRowSorter(true);
          assertSame(sorter, table.getRowSorter());
      }

  //----------------------- interactive
   
      /**
       * Core Issue: the calculation of the repaint region after update is completely broken.
       * Nevertheless, the cell is updated correctly. Seems like someplace, the complete table
       * is marked as dirty? <p>
       * 
       * Reason is that always the complete table is repainted if we have individual rowheights.
       * slight dirt in the update code: even if already painted, the would be dirty-region
       * for same rowheights is calculated and repainted (repaintManager folds them into one
       * repaint request, though). 
       */
      public void interactiveRepaintIndiRowHeight() {
          DefaultTableModel model = new DefaultTableModel(20, 3) {
              /**
               * Overridden to reach fire rowUpdated (instead of cellUpdated)
               */
              @Override
              public void setValueAt(Object aValue, int row, int column) {
                  Vector rowVector = (Vector)dataVector.elementAt(row);
                  rowVector.setElementAt(aValue, column);
                  fireTableRowsUpdated(row, row);
              }
              
          };
          final JTable table = new JTable(model) ;
         for (int i = 4; i < table.getRowCount(); i++) {
              table.setRowHeight(i, table.getRowHeight() + i *4);
          }
          JXFrame frame = showInFrame(table, "repaint after update");
          Action action = new AbstractAction("update focused") {
              
              @Override
              public void actionPerformed(ActionEvent e) {
                  int selected = table.getRowCount() / 2;
                  if (selected < 0) return;
                  table.setValueAt("XX" + table.getValueAt(selected, 0), selected, 0);
              }
              
          };
          addAction(frame, action);
      }
      
      
      /**
       * NPE on null values (if comparable)? No, not any longer.
       */
      public void interactiveSortWithNull() {
          final JTable table = new JTable(new AncientSwingTeam());
          table.setValueAt(null, 4, 3);
          table.setAutoCreateRowSorter(true);
          JXFrame frame = showWithScrollingInFrame(table, "NPE with null number?");
      }

      /**
       * Core issue #6539455: table not properly repainted on update (from model).
       * 
       * Happens if the update is not triggered by an edit in the table itself. If
       * so, all is well (repaint called for all of the table). If not, repaint is
       * limited to the cell that has been updated (not even the whole row is
       * painted) - correct would be to repaint all rows between the old and new
       * row view index, inclusively.
       */
      public void interactiveSortOnUpdateNotEditing() {
          final JTable table = new JTable(new AncientSwingTeam());
          table.setAutoCreateRowSorter(true);
          ((TableRowSorter) table.getRowSorter()).setSortsOnUpdates(true);
          table.getRowSorter().toggleSortOrder(0);
          JXFrame frame = showWithScrollingInFrame(table,
                  "updates and repaint");
          Action edit = new AbstractAction("update first visible") {

              @Override
              public void actionPerformed(ActionEvent e) {
                  table.setValueAt("XXX" + table.getValueAt(0, 0), 0, 0);

              }
          };
          addAction(frame, edit);
      }
      
      /**
       * Core issue #6539455: table not properly repainted on update (from model).
       * 
       * Happens if the update is not triggered by an edit in the table itself. If
       * so, all is well (repaint called for all of the table). If not, repaint is
       * limited to the cell that has been updated (not even the whole row is
       * painted) - correct would be to repaint all rows between the old and new
       * row view index, inclusively.
       */
      public void interactiveSortOnUpdateNotEditingHack() {
          final JTable table = new JTableRepaintOnUpdate();
          table.setModel(new AncientSwingTeam());
          table.setAutoCreateRowSorter(true);
          ((TableRowSorter) table.getRowSorter()).setSortsOnUpdates(true);
          table.getRowSorter().toggleSortOrder(0);
          JXFrame frame = showWithScrollingInFrame(table,
                  "updates and repaint (hacked)");
          Action edit = new AbstractAction("update first visible") {

              @Override
              public void actionPerformed(ActionEvent e) {
                  table.setValueAt("XXX" + table.getValueAt(0, 0), 0, 0);

              }
          };
          addAction(frame, edit);
      }
      
      
      public static class JTableRepaintOnUpdate extends JTable {

          private UpdateHandler beforeSort;
          
          
          @Override
          public void sorterChanged(RowSorterEvent e) {
              super.sorterChanged(e);
              maybeRepaintOnSorterChanged(e);
          } 

          private void beforeUpdate(TableModelEvent e) {
              if (!isSorted()) return;
              beforeSort = new UpdateHandler(e);
          }
          
          /**
           * 
           */
          private void afterUpdate() {
              beforeSort = null;
          }
          
          
          /**
           * 
           */
          private void maybeRepaintOnSorterChanged(RowSorterEvent e) {
              if (beforeSort == null) return;
              if ((e == null) || (e.getType() != RowSorterEvent.Type.SORTED)) return;
              UpdateHandler afterSort = new UpdateHandler(beforeSort);
              if (afterSort.allHidden(beforeSort)) {
                  return;
              } else if (afterSort.complex(beforeSort)) {
                  repaint();
                  return;
              }
              int firstRow = afterSort.getFirstCombined(beforeSort);
              int lastRow = afterSort.getLastCombined(beforeSort);
              Rectangle first = getCellRect(firstRow, 0, false);
              first.width = getWidth();
              Rectangle last = getCellRect(lastRow, 0, false);
              repaint(first.union(last));
          }
          
          private class UpdateHandler {
              private int firstModelRow;
              private int lastModelRow;
              private int viewRow;
              private boolean allHidden;
              
              public UpdateHandler(TableModelEvent e) {
                  firstModelRow = e.getFirstRow();
                  lastModelRow = e.getLastRow();
                  convert();
              }
              
              public UpdateHandler(UpdateHandler e) {
                  firstModelRow = e.firstModelRow;
                  lastModelRow = e.lastModelRow;
                  convert();
              }
              
              public boolean allHidden(UpdateHandler e) {
                  return this.allHidden && e.allHidden;
              }
              
              public boolean complex(UpdateHandler e) {
                  return (firstModelRow != lastModelRow);
              }
              
              public int getFirstCombined(UpdateHandler e) {
                  if (allHidden) return e.viewRow;
                  if (e.allHidden) return viewRow;
                  return Math.min(viewRow, e.viewRow);
              }
              
              public int getLastCombined(UpdateHandler e) {
                  if (allHidden || e.allHidden) return getRowCount() - 1;
                  return Math.max(viewRow, e.viewRow);
                  
              }
              /**
               * @param e
               */
              private void convert() {
                  // multiple updates
                  if (firstModelRow != lastModelRow) {
                      // don't bother too much - calculation not guaranteed to do anything good
                      // just check if the all changed indices are hidden
                      allHidden = true;
                      for (int i = firstModelRow; i <= lastModelRow; i++) {
                          if (convertRowIndexToView(i) >= 0) {
                              allHidden = false;
                              break;
                          }
                      }
                      viewRow = -1;
                      return;
                  }
                  // single update
                  viewRow = convertRowIndexToView(firstModelRow);
                  allHidden = viewRow < 0;
              }
              
          }
          /**
           * @return
           */
          private boolean isSorted() {
              // JW: not good enough - need a way to decide if there are any sortkeys which
              // constitute a sort or any effective filters  
              return getRowSorter() != null;
          }



          @Override
          public void tableChanged(TableModelEvent e) {
              if (isUpdate(e)) {
                  beforeUpdate(e);
              }
              try {
                  super.tableChanged(e);
              } finally {
                  afterUpdate();
              }
          }
          

          /**
           * Convenience method to detect dataChanged table event type.
           * 
           * @param e the event to examine. 
           * @return true if the event is of type dataChanged, false else.
           */
          protected boolean isDataChanged(TableModelEvent e) {
              if (e == null) return false;
              return e.getType() == TableModelEvent.UPDATE && 
                  e.getFirstRow() == 0 &&
                  e.getLastRow() == Integer.MAX_VALUE;
          }
          
          /**
           * Convenience method to detect update table event type.
           * 
           * @param e the event to examine. 
           * @return true if the event is of type update and not dataChanged, false else.
           */
          protected boolean isUpdate(TableModelEvent e) {
              if (isStructureChanged(e)) return false;
              return e.getType() == TableModelEvent.UPDATE && 
                  e.getLastRow() < Integer.MAX_VALUE;
          }

          /**
           * Convenience method to detect a structureChanged table event type.
           * @param e the event to examine.
           * @return true if the event is of type structureChanged or null, false else.
           */
          protected boolean isStructureChanged(TableModelEvent e) {
              return e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW;
          }


      }
      /**
       * Core Issue ??: must not sort if mouse in resize region 
       */
      public void interactiveSortOnResize() {
          JTable table = new JTable(new AncientSwingTeam());
          table.setAutoCreateRowSorter(true);
          MouseListener l = new MouseAdapter() {};
          showWithScrollingInFrame(table, "must not sort in resize");
      }

      /**
       * Core issue #6539455: table not properly repainted on update (from model).
       * 
       * This setup differs from the examples (assuming we would add a second table, arggh)
       * above in that the sorter is shared as well as the model. In this case the repaint is
       * okay, as the second table receives the event from the sorter outside of its 
       * tableChanged, that is ignoreSort is false.
       */
      public void interactiveSharedRowSorter() {
          TableModel model = new AncientSwingTeam();
          final JTable one = new JTable();
          one.setDragEnabled(true);
          one.setAutoCreateRowSorter(true);
          one.setModel(model);
          JTable other = new JTable(model);
          other.setRowSorter(one.getRowSorter());
          JXFrame frame = showWithScrollingInFrame(one, other, "shared model and rowsorter");
          Action editFirst = new AbstractAction("prefix X on first") {

              @Override
              public void actionPerformed(ActionEvent e) {
                  Object old = one.getValueAt(0, 0);
                  one.setValueAt("X" + old, 0, 0);
              }
              
          };
          addAction(frame, editFirst);
          Action toggleSortOnUpdate = new AbstractAction("toggleSortsOnUpdate") {

              @Override
              public void actionPerformed(ActionEvent e) {
                  DefaultRowSorter<?, ?> sorter = (DefaultRowSorter<?, ?>) one.getRowSorter();
                  sorter.setSortsOnUpdates(!sorter.getSortsOnUpdates());
              } };
          addAction(frame, toggleSortOnUpdate);
      }

   //---------------- end core sorting   

    public void testFormatDefaultRenderer() {
        DefaultTableModel model = new DefaultTableModel(1, 1) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Date.class;
            }
            
        };
        model.setValueAt("definitely not a date", 0, 0);
        JTable table = new JTable(model);
        TableCellRenderer renderer = table.getCellRenderer(0, 0);
        table.prepareRenderer(renderer , 0, 0);
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

    public void interactiveAutoStartsEdit() {
        final String autoKey = "JTable.autoStartsEdit";
        final JTable table = new JTable(new AncientSwingTeam());
        table.putClientProperty(autoKey, Boolean.TRUE);
        final String autoStartName = "toggle AutoStart ";
        boolean isAuto = Boolean.TRUE.equals(table.getClientProperty(autoKey));
        Action autoStart = new AbstractActionExt(autoStartName + isAuto) {

            public void actionPerformed(ActionEvent e) {
                boolean isAuto = Boolean.TRUE.equals(table.getClientProperty(autoKey));
                table.putClientProperty(autoKey, isAuto ? Boolean.FALSE : Boolean.TRUE);
                setName(autoStartName + !isAuto);
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "autostart-edit behaviour");
        addAction(frame, autoStart);
        show(frame);
    }
    
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
//------------- from incubator ... PENDING: cleanup/remove
    

}
