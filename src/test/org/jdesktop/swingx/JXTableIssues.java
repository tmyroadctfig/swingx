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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.SortKey;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.CellEditorReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.SerializableSupport;
import org.jdesktop.test.TestUtils;
import org.junit.Test;

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

    public static void main(String args[]) {
        JXTableIssues test = new JXTableIssues();
        setSystemLF(true);
        try {
          test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Scroll.*");
         //   test.runInteractiveTests("interactive.*Render.*");
            test.runInteractiveTests("interactive.*ExtendOnRemoveAdd.*");
//            test.runInteractiveTests("interactive.*Repaint.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

    /**
     * Quick check for a forum report:
     * getValueAt called on init for each cell (even the invisible).
     * 
     * Looks okay: getValueAt called for visible cells only.
     * @throws Exception 
     * 
     */
    public void testGetValueOnInit() throws Exception {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        final List<Integer> set = new ArrayList<Integer>();
        final JXTable table = new JXTable() {

            @Override
            public Object getValueAt(int row, int column) {
                set.add(row);
                return super.getValueAt(row, column);
            }
            
        };
        showWithScrollingInFrame(table, "");
        table.setModel(new DefaultTableModel(100, 5));
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                // failing - one row off?
                assertEquals(table.getColumnCount() * table.getVisibleRowCount(), set.size());
                
            }
        });

    }

    /**
     * Issue #856-swingx: no notification on filters changed
     */
    public void testFiltersProperty() {
        JXTable table = new JXTable(10, 2);
        FilterPipeline pipeline = table.getFilters();
        assertNotNull("sanity: pipeline never null", pipeline);
        FilterPipeline other = new FilterPipeline();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setFilters(other);
        assertEquals("sanity: new pipeline set", other, table.getFilters());
        TestUtils.assertPropertyChangeEvent(report, "filters", pipeline, other, false);
    }

    /**
     * Issue #856-swingx: no notification on filters changed
     * 
     * Here: test JXList
     */
    public void testFiltersPropertyList() {
        JXList table = new JXList(true);
        FilterPipeline pipeline = table.getFilters();
        assertNotNull("sanity: pipeline never null", pipeline);
        FilterPipeline other = new FilterPipeline();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setFilters(other);
        assertEquals("sanity: new pipeline set", other, table.getFilters());
        TestUtils.assertPropertyChangeEvent(report, "filters", pipeline, other, false);
    }

    /**
     * Issue #847-swingx: JXTable respect custom corner if columnControl not visible
     * 
     *  LAF provided corners are handled in core since jdk6u10. 
     */
    public void testCornerRespectLAF() {
        Object corner = UIManager.get("Table.scrollPaneCornerComponent");
        if (!(corner instanceof Class)) {
            LOG.info("cannont run - LAF doesn't provide corner component");
            return;
        }
        final JXTable table = new JXTable(10, 2);
        final JScrollPane scrollPane = new JScrollPane(table);
        table.addNotify();
        assertNotNull(scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
        assertEquals(corner, scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER).getClass());
    }


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
     * Issue #359-swing: find suitable rowHeight.
     * 
     * Text selection in textfield has row of metrics.getHeight.
     * Suitable rowHeight should should take border into account:
     * for a textfield that's the metrics height plus 2.
     * 
     * PENDING: this passes locally, fails on server
     */
    public void testRowHeightFontMetrics() {
        JXTable table = new JXTable(10, 2);
        TableCellEditor editor = table.getCellEditor(1, 1);
        Component comp = table.prepareEditor(editor, 1, 1);
        assertEquals(comp.getPreferredSize().height, table.getRowHeight());
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
   
    
    
     
    private void assertSelection(TableModel tableModel, ListSelectionModel selectionModel, Object[] expected) {
        List<Object> selected = new ArrayList<Object>();
        for(int r = 0; r < tableModel.getRowCount(); r++) {
            if(selectionModel.isSelectedIndex(r)) selected.add(tableModel.getValueAt(r, 0));
        }
        
        List expectedList = Arrays.asList(expected);
        assertEquals("selected Objects must be as expected", expectedList, selected);
    
    }

//----------------- interactive

    /**
     * Unconditional repaint on cell update (through the default
     * identify filter). 
     */
    public void interactiveRepaintOnUpdateSingleCell() {
        JXTable table =  new JXTable(10, 5);
        // highlight complete row if first cell starts with a
        HighlightPredicate predicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                return adapter.getString(0).startsWith("a");
            }
            
        };
        ColorHighlighter highlighter = new ColorHighlighter(predicate, Color.MAGENTA, null, Color.MAGENTA, null);
        table.addHighlighter(highlighter);
        JXTable other = new JXTable(table.getModel());
        other.setFilters(new FilterPipeline(new IdentityFilter()));
        other.addHighlighter(highlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, other, "repaint on update in first");
        addMessage(frame, "edit first cell in left table (start with/out a)");
        show(frame);
    }
    
    public class IdentityFilter extends Filter {
        
        
        /**
         * PENDING JW: fires always, even without sorter ..
         * Could do better - but will break behaviour of apps which relied on
         * the (buggy) side-effect of repainting on each change.
         * 
         */
        @Override
        public void refresh() {
            if ((pipeline == null) ||
              (pipeline.getSortController().getSortKeys().size() == 0)) return;
            super.refresh();
        }

        @Override
        protected void init() {

        }

        @Override
        protected void reset() {

        }

        @Override
        protected void filter() {

        }

        @Override
        public int getSize() {
            return this.getInputSize();
        }

        @Override
        protected int mapTowardModel(int row) {
            return row;
        }

        @Override
        protected int mapTowardView(int row) {
            return row;
        }
    }

    /**
     * Issue #610-swingx: Cancel editing via Escape doesn't fire editingCanceled.
     * 
     * Reported against ComboBoxCellEditor in the autoComplete package, but actually
     * a JTable _never_ fires a editingCanceled for any editor. Reason is that the
     * cancel Action registered in BasisTableUI incorrectly calls table.removeEditor
     * instead of getCelleditor.cancelEditing.
     * 
     * Quick hack around that: JXTable registers its own cancel action.
     * 
     * Still open: esc when popup is open will only close the popup, not cancel the
     * edit (which requires a second esc). 
     *  
     */
    public void interactiveEditingCanceledOnEscape() {
        final JTextField field = new JTextField();
        JXTable xTable = new JXTable(10, 3);
        CellEditor editor = xTable.getDefaultEditor(Object.class);
        CellEditorListener l =  new CellEditorListener() {

            public void editingCanceled(ChangeEvent e) {
                field.setText("canceled");
                
            }

            public void editingStopped(ChangeEvent e) {
                field.setText("stopped");
                
            }};
        editor.addCellEditorListener(l);
        JTable table = new JTable(xTable.getModel());
        CellEditor coreEditor = table.getDefaultEditor(Object.class);
        coreEditor.addCellEditorListener(l);
        JXFrame frame = wrapWithScrollingInFrame(xTable, table, "#610-swingx: escape doesn't fire editing canceled");
        frame.add(field, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
    /**
     * row index conversion goes nuts if not re-sorted on update.
     * Looks like a repaint problem.
     */
    public void interactiveSortOnUpdate() {
        JXTable xtable = new JXTable(new AncientSwingTeam()) {

            @Override
            protected boolean shouldSortOnChange(TableModelEvent e) {
                if (isUpdate(e)) {
                    return false;
                }
                return super.shouldSortOnChange(e);
            }
            
        };
        JXTable table = new JXTable(xtable.getModel()) {

            @Override
            protected boolean shouldSortOnChange(TableModelEvent e) {
                if (isUpdate(e)) {
                    repaint(e);
                    return false;
                }
                return super.shouldSortOnChange(e);
            }

            /**
             * Hack to repaint at the correct row in terms of 
             * view coordinates.
             * @param e
             */
            private void repaint(TableModelEvent e) {
                int firstRow = convertRowIndexToView(e.getFirstRow());
                Rectangle rowRect = getCellRect(firstRow, 0, true);
                rowRect.width = getWidth();
                repaint(rowRect);
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "edit and shouldSortOnChange false");
        frame.setVisible(true);
    }
    

    public void interactiveIndividualRowHeightAndFilter() {
        final JXTable table = new JXTable(createAscendingModel(0, 50));
        table.setRowHeightEnabled(true);
        table.setRowHeight(1, 100);
        final FilterPipeline filterPipeline = new FilterPipeline(new PatternFilter(".*1.*",0,0));
        Action action = new AbstractAction("filter") {

            public void actionPerformed(ActionEvent e) {
                if (table.getFilters() == filterPipeline) {
                    table.setFilters(null);
                } else {
                    table.setFilters(filterPipeline);
                }
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "toggle filter and indi rowheight");
        addAction(frame, action);
        frame.setVisible(true);
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
        private List<Object> contents = new ArrayList<Object>();
        
        public void setContents(List<Object> contents) {
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
            @Override
            public Class<?> getColumnClass(int column) {
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
        fail("check status ...");
    }

    /**
     * Match highlighter fails to display correctly if column-based highlighter alters background
     * color.
     */
    public void interactiveColumnHighlightingWithSearch() {
        JXTable table = new JXTable(new AncientSwingTeam());
        
        table.getColumnExt("Favorite Color").setHighlighters(new AbstractHighlighter() {
            @Override
            protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
                Color color = (Color) adapter.getValue();
                
                if (renderer instanceof JComponent) {
                    ((JComponent) renderer).setBorder(BorderFactory.createLineBorder(color));
                }
                
                return renderer;
            }
        });
        
        table.getColumnExt(0).addHighlighter(
                new ColorHighlighter(new HighlightPredicate() {
                    public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                        return adapter.getValue().toString().contains("e");
                    }
                }, Color.GREEN, null));
        
        JFrame frame = wrapWithScrollingInFrame(table, "Column Highlighter with Search Test");
        table.putClientProperty(JXTable.MATCH_HIGHLIGHTER, true);
        //should highlight Jeff with Yellow
        table.getSearchable().search("e", 3);
        frame.setVisible(true);
    }
}
