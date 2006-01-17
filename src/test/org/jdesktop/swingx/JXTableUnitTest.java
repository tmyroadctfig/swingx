/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.decorator.Sorter;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.util.AncientSwingTeam;
import org.jdesktop.swingx.util.ChangeReport;
import org.jdesktop.swingx.util.PropertyChangeReport;

/**
* Split from old JXTableUnitTest - contains unit test
* methods only.
* 
*/
public class JXTableUnitTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTableUnitTest.class
            .getName());

    protected DynamicTableModel tableModel = null;
    protected TableModel sortableTableModel;

    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;

    public JXTableUnitTest() {
        super("JXTable unit test");
    }

    protected void setUp() throws Exception {
       super.setUp();
        // set loader priority to normal
        if (tableModel == null) {
            tableModel = new DynamicTableModel();
        }
        sortableTableModel = new AncientSwingTeam();
        // make sure we have the same default for each test
        defaultToSystemLF = false;
        setSystemLF(defaultToSystemLF);
    }

    
    /**
     * Issue #232-swingx: selection not kept if selectionModel had been changed.
     *
     */
    public void testSelectionMapperUpdatedOnSelectionModelChange() {
        JXTable table = new JXTable();
        ListSelectionModel model = new DefaultListSelectionModel();
        table.setSelectionModel(model);
        assertEquals(model, table.getSelectionMapper().getViewSelectionModel());
    }

    public void testRemoveHighlighter() {
        JXTable table = new JXTable();
        // test cope with null
        table.removeHighlighter(null);
        Highlighter presetHighlighter = AlternateRowHighlighter.classicLinePrinter;
        HighlighterPipeline pipeline = new HighlighterPipeline(new Highlighter[] {presetHighlighter});
        table.setHighlighters(pipeline);
        ChangeReport report = new ChangeReport();
        pipeline.addChangeListener(report);
        table.removeHighlighter(new Highlighter());
        // sanity: highlighter was not contained
        assertFalse("pipeline must not have fired", report.hasEvents());
        // remove the presetHighlighter
        table.removeHighlighter(presetHighlighter);
        assertEquals("pipeline must have fired on remove", 1, report.getEventCount());
        assertEquals("pipeline must be empty", 0, pipeline.getHighlighters().length);
    }
    
    /**
     * test choking on precondition failure (highlighter must not be null).
     *
     */
    public void testAddNullHighlighter() {
        JXTable table = new JXTable();
        try {
            table.addHighlighter(null);
            fail("adding a null highlighter must throw NPE");
        } catch (NullPointerException e) {
            // pass - this is what we expect
        } catch (Exception e) {
            fail("adding a null highlighter throws exception different " +
                        "from the expected NPE \n" + e);
        }
    }
    
    public void testAddHighlighterWithNotEmptyPipeline() {
        JXTable table = new JXTable();
        Highlighter presetHighlighter = AlternateRowHighlighter.classicLinePrinter;
        HighlighterPipeline pipeline = new HighlighterPipeline(new Highlighter[] {presetHighlighter});
        table.setHighlighters(pipeline);
        Highlighter highlighter = new Highlighter();
        ChangeReport report = new ChangeReport();
        pipeline.addChangeListener(report);
        table.addHighlighter(highlighter);
        assertSame("pipeline must be same as preset", pipeline, table.getHighlighters());
        assertEquals("pipeline must have fired changeEvent", 1, report.getEventCount());
        assertPipelineHasAsLast(pipeline, highlighter);
    }
    
    private void assertPipelineHasAsLast(HighlighterPipeline pipeline, Highlighter highlighter) {
        Highlighter[] highlighters = pipeline.getHighlighters();
        assertTrue("pipeline must not be empty", highlighters.length > 0);
        assertSame("highlighter must be added as last", highlighter, highlighters[highlighters.length - 1]);
    }

    /**
     * test adding a highlighter.
     *
     *  asserts that a pipeline is created and set (firing a property change) and
     *  that the pipeline contains the highlighter.
     */
    public void testAddHighlighterWithNullPipeline() {
        JXTable table = new JXTable();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Highlighter highlighter = new Highlighter();
        table.addHighlighter(highlighter);
        assertNotNull("table must have created pipeline", table.getHighlighters());
        assertTrue("table must have fired propertyChange for highlighters", report.hasEvents("highlighters"));
        assertPipelineContainsHighlighter(table.getHighlighters(), highlighter);
    }
    
    /**
     * fails if the given highlighter is not contained in the pipeline.
     * PRE: pipeline != null, highlighter != null.
     * 
     * @param pipeline
     * @param highlighter
     */
    private void assertPipelineContainsHighlighter(HighlighterPipeline pipeline, Highlighter highlighter) {
        Highlighter[] highlighters = pipeline.getHighlighters();
        for (int i = 0; i < highlighters.length; i++) {
            if (highlighter.equals(highlighters[i])) return;
        }
        fail("pipeline does not contain highlighter");
        
    }

    /**
     * test if renderer properties are updated on LF change.
     * Note: this can be done examplary only. Here: we use the 
     * font of a rendererComponent returned by a LinkRenderer for
     * comparison. There's nothing to test if the font are equal
     * in System and crossplattform LF.
     */
    public void testUpdateRendererOnLFChange() {
        LinkRenderer comparison = new LinkRenderer();
        LinkRenderer linkRenderer = new LinkRenderer();
        JXTable table = new JXTable(2, 3);
        Component comparisonComponent = comparison.getTableCellEditorComponent(table, null, false, 0, 0);
        Font comparisonFont = comparisonComponent.getFont();
        table.getColumnModel().getColumn(0).setCellRenderer(linkRenderer);
        setSystemLF(!defaultToSystemLF);
        SwingUtilities.updateComponentTreeUI(comparisonComponent);
        if (comparisonFont.equals(comparisonComponent.getFont())) {
            LOG.info("cannot run test - equal font " + comparisonFont);
            return;
        }
        SwingUtilities.updateComponentTreeUI(table);
        Component rendererComp = table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals("renderer font must be updated", 
                comparisonComponent.getFont(), rendererComp.getFont());
        
    }
    /**
     * test if LinkController/executeButtonAction is properly registered/unregistered on
     * setRolloverEnabled.
     *
     */
    public void testLinkControllerListening() {
        JXTable table = new JXTable();
        table.setRolloverEnabled(true);
        assertNotNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table));
        assertNotNull("execute button action must be registered", table.getActionMap().get(JXTable.EXECUTE_BUTTON_ACTIONCOMMAND));
        table.setRolloverEnabled(false);
        assertNull("LinkController must not be listening", getLinkControllerAsPropertyChangeListener(table));
        assertNull("execute button action must be de-registered", table.getActionMap().get(JXTable.EXECUTE_BUTTON_ACTIONCOMMAND));
    }
    
    private PropertyChangeListener getLinkControllerAsPropertyChangeListener(JXTable table) {
        PropertyChangeListener[] listeners = table.getPropertyChangeListeners();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof JXTable.LinkController) {
                return (JXTable.LinkController) listeners[i];
            }
        }
        return null;
    }

    /**
     * Issue #180-swingx: outOfBoundsEx if testColumn is hidden.
     *
     */
    public void testHighlighterHiddenTestColumn() {
        JXTable table = new JXTable(sortableTableModel);
        table.getColumnExt(0).setVisible(false);
        Highlighter highlighter = new PatternHighlighter(null, Color.RED, "a", 0, 0);
        ComponentAdapter adapter = table.getComponentAdapter();
        adapter.row = 0;
        adapter.column = 0;
        highlighter.highlight(new JLabel(), adapter);
    }
    
    /**
     * 
     * Issue #173-swingx.
     * 
     * table.setFilters() leads to selectionListener
     * notification while internal table state not yet stable.
     * 
     * example (second one, from Nicola):
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    public void testSelectionListenerNotification() {
        final JXTable table = new JXTable(createAscendingModel(0, 20));
        final int modelRow = 0;
        // set a selection 
        table.setRowSelectionInterval(modelRow, modelRow);
        ListSelectionListener l = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int viewRow = table.getSelectedRow(); 
                assertTrue("view index visible", viewRow >= 0);
                // JW: the following checks if the reverse conversion succeeds
                table.convertRowIndexToModel(viewRow);
                
            }
            
        };
        table.getSelectionModel().addListSelectionListener(l);
        table.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
    }

    
    /**
     * 
     * Issue #172-swingx.
     * 
     * The sequence: clearSelection() - setFilter - setRowSelectionInterval
     * throws Exception.
     * 
     * example (first, from Diego):
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    public void testClearSelectionAndFilter() {
        JXTable table = new JXTable(createAscendingModel(0, 20));
        int modelRow = table.getRowCount() - 1;
        // set a selection near the end - will be invalid after filtering
        table.setRowSelectionInterval(modelRow, modelRow);
        table.clearSelection();
        table.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("9", 0, 0) }));
        int viewRow = table.convertRowIndexToView(modelRow);
        assertTrue("view index visible", viewRow >= 0);
        table.setRowSelectionInterval(viewRow, viewRow);
    }

    /**
     * 
     * Issue #172-swingx.
     * 
     * The sequence:  setFilter - clearSelection() - setRowSelectionInterval
     * is okay. 
     * 
     * Looks like in SelectionMapper.setPipeline needs to check for empty 
     * selection in view selectionModel and update the anchor/lead (in 
     * the view selection) to valid values! 
     * Now done in SelectionMapper.clearViewSelection, which fixes this test.
     * 
     * example (first, from Diego):
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    public void testFilterAndClearSelection() {
        JXTable table = new JXTable(createAscendingModel(0, 20));
        int modelRow = table.getRowCount() - 1;
        // set a selection near the end - will be invalid after filtering
        table.setRowSelectionInterval(modelRow, modelRow);
        table.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("9", 0, 0) }));
        table.clearSelection();
        int viewRow = table.convertRowIndexToView(modelRow);
        assertTrue("view index visible", viewRow >= 0);
        table.setRowSelectionInterval(viewRow, viewRow);
    }
    /**
     * 
     * Issue #172-swingx. 
     * 
     * 
     * reported exception if row removed (Ray, at the end of)
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    public void testSelectionAndRemoveRowOfMisbehavingModel() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public void fireTableRowsDeleted(int firstRow, int lastRow) {
                fireTableStructureChanged();
            }
            
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        JXTable table = new JXTable(model);
        int modelRow = table.getRowCount() - 1;
        table.setSorter(0);
        // set a selection near the end - will be invalid after filtering
        table.setRowSelectionInterval(modelRow, modelRow);
        model.removeRow(modelRow);
        int lastRow = table.getModel().getRowCount() - 1;
        int viewRow = table.convertRowIndexToView(lastRow);
        assertTrue("view index visible", viewRow >= 0);
        table.setRowSelectionInterval(viewRow, viewRow);
    }


    
    /**
     * 
     * Issue #172-swingx. 
     * 
     * 
     * reported exception if row removed (Ray, at the end of)
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    public void testSelectionAndRemoveRowOfMisbehavingModelRay() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public void fireTableRowsDeleted(int firstRow, int lastRow) {
                fireTableStructureChanged();
            }
            
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        JXTable table = new JXTable(model);
        int modelRow = table.getRowCount() - 1;
        Filter[] filters = new Filter[] {new ShuttleSorter(0, true)};
        FilterPipeline filterPipe = new FilterPipeline(filters);
        table.setFilters(filterPipe);        
        // set a selection near the end - will be invalid after filtering
        table.setRowSelectionInterval(modelRow, modelRow);
        model.removeRow(modelRow);
        int lastRow = table.getModel().getRowCount() - 1;
        int viewRow = table.convertRowIndexToView(lastRow);
        // JW: here's the problem - the anchor of the selectionModel is not updated correctly
        // after removing the last model row
        // not longer valid (as of 50u6)
//        assertEquals("anchor must be last", lastRow, table.getSelectionModel().getAnchorSelectionIndex());
        assertTrue("view index visible", viewRow >= 0);
        assertEquals("view index is last", viewRow, lastRow);
        table.setRowSelectionInterval(viewRow, viewRow);
    }



    /**
     * Issue #167-swingx: table looses individual row height 
     * on update.
     * 
     * This happened if the indy row is filtered and the selection is empty - 
     * updateSelectionAndRowHeight case analysis was incomplete. Fixed.
     *
     */
    public void testKeepRowHeightOnUpdateAndEmptySelection() {
        JXTable table = new JXTable(10, 3);
        table.setRowHeightEnabled(true);
        // sanity assert
        assertTrue("row height enabled", table.isRowHeightEnabled());
        table.setRowHeight(0, 25);
        // sanity assert
        assertEquals(25, table.getRowHeight(0));
        // setting an arbitrary value
        table.setValueAt("dummy", 1, 0);
        assertEquals(25, table.getRowHeight(0));
        // filter to leave only the row with the value set
        table.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("d", 0, 0)}));
        assertEquals(1, table.getRowCount());
        // setting an arbitrary value in the visible rows
        table.setValueAt("otherdummy", 0, 1);
        // reset filter to show all
        table.setFilters(null);
        assertEquals(25, table.getRowHeight(0));
        
        
    }
    


    /**
     * Issue #165-swingx: IllegalArgumentException when
     * hiding/reshowing columns "at end" of column model.
     *
     */
    public void testHideShowLastColumns() {
        JXTable table = new JXTable(10, 3);
        TableColumnExt ext = table.getColumnExt(2);
        for (int i = table.getModel().getColumnCount() - 1; i > 0; i--) {
           table.getColumnExt(i).setVisible(false); 
        }
        ext.setVisible(true);
    }

    /**
     * Issue #155-swingx: lost setting of initial scrollBarPolicy.
     *
     */
    public void testConserveVerticalScrollBarPolicy() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run conserveVerticalScrollBarPolicy - headless environment");
            return;
        }
        JXTable table = new JXTable(0, 3);
        JScrollPane scrollPane1 = new JScrollPane(table);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JXFrame frame = new JXFrame();
        frame.add(scrollPane1);
        frame.setSize(500, 400);
        frame.setVisible(true);
        assertEquals("vertical scrollbar policy must be always", 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                scrollPane1.getVerticalScrollBarPolicy());
    }

    public void testEnableRowHeight() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeight(0, 25);
        assertEquals("indy row height must not be set if disabled", table.getRowHeight(), table.getRowHeight(0));
        table.setRowHeightEnabled(true);
        assertEquals("indy row height must not be set on setting enabled", table.getRowHeight(), table.getRowHeight(0));
        table.setRowHeight(0, 25);
        assertEquals(25, table.getRowHeight(0));
        table.setRowHeightEnabled(false);
        assertEquals("indy row height must not be set if disabled", table.getRowHeight(), table.getRowHeight(0));
        
     
    }
    public void testIndividualRowHeightAfterSetModel() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeightEnabled(true);
        table.setRowHeight(0, 25);
        // sanity assert
        assertEquals(25, table.getRowHeight(0));
        table.setModel(sortableTableModel);
        assertEquals("individual rowheight must be reset", 
                table.getRowHeight(), table.getRowHeight(0));
        
    }
    public void testIndividualRowHeight() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeightEnabled(true);
        table.setRowHeight(0, 25);
        assertEquals(25, table.getRowHeight(0));
        assertEquals(table.getRowHeight(), table.getRowHeight(1));
        table.getFilters().setSorter(new ShuttleSorter(0, false));
        assertEquals(table.getRowHeight(), table.getRowHeight(1));
        assertEquals(25, table.getRowHeight(table.getRowCount() - 1));
        table.setRowHeight(table.getRowHeight());
        assertEquals(table.getRowHeight(), table.getRowHeight(table.getRowCount() - 1));
    }
    
    public void testResetIndividualRowHeight() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeightEnabled(true);
        table.setRowHeight(0, 25);
        table.getFilters().setSorter(new ShuttleSorter(0, false));
        assertEquals("individual row height must be moved to last row", 
                25, table.getRowHeight(table.getRowCount() - 1));
        // reset
        table.setRowHeight(table.getRowHeight());
        assertEquals("individual row height must be reset", 
                table.getRowHeight(), table.getRowHeight(table.getRowCount() - 1));
    }
    
    public void testRowModelAccess() {
        JXTable table = new JXTable(sortableTableModel);
        table.setRowHeight(0, 25);
//        SizeSequence sizing = table.getSuperRowModel();
//        assertNotNull(sizing);
    }

    /**
     * Issue #197: JXTable pattern search differs from 
     * PatternHighlighter/Filter.
     * 
     */
    public void testRespectPatternInSearch() {
        JXTable table = new JXTable(createAscendingModel(0, 11));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        Pattern strict = Pattern.compile("^" + lastName + "$");
        int found = table.getSearchable().search(strict, -1, false);
        assertEquals("found must be equal to row", row, found);
        found = table.getSearchable().search(strict, found, false);
        assertEquals("search must fail", -1, found);
    }

    /**
     * Issue #64-swingx: setFilters(null) throws NPE if has selection.
     *
     */
    public void testSetNullFilters() {
        JXTable table = new JXTable(sortableTableModel);
        table.setRowSelectionInterval(0, 0);
        table.setFilters(null);
        assertEquals("selected row must be unchanged", 0, table.getSelectedRow());
    }
    
    /**
     * Issue #119: Exception if sorter on last column and setting
     * model with fewer columns.
     * 
     * JW: related to #53-swingx - sorter not removed on column removed. 
     *
     */
    public void testInteractiveSorterOnModelChange() {
        JXTable table = new JXTable(sortableTableModel);
        int columnCount = table.getColumnCount();
        table.setSorter(columnCount - 1);
        table.setModel(new DefaultTableModel(10, columnCount - 1));
        assertEquals(null, table.getFilters().getSorter());
    }
    /**
     * sanity testing while refactoring support
     * for interactive sorter.
     *
     */
    public void testSorterToPipeline() {
        JXTable table = new JXTable(sortableTableModel);
        table.setSorter(0);
        TableColumnExt columnX = table.getColumnExt(0);
        assertEquals("interactive sorter must be same as sorter in column", 
                columnX.getSorter(), table.getFilters().getSorter());
        table.resetSorter();
        assertEquals("interactive sorter must be null", null, table.getFilters().getSorter());
    }
    
    /**
     * Issue #53-swingx: interactive sorter not removed if column removed.
     *
     */
    public void testSorterAfterColumnRemoved() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.setSorter(0);
        table.removeColumn(columnX);
        assertEquals("sorter must be removed when column removed", null, table.getFilters().getSorter());
        
    }
    
    /**
     * interactive sorter must be active if column is hidden.
     *
     */
    public void testSorterAfterColumnHidden() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.setSorter(0);
        columnX.setVisible(false);
        assertEquals("interactive sorter must be same as sorter in column", 
                columnX.getSorter(), table.getFilters().getSorter());
        
    }
    
    /**
     * Issue #54-swingx: hidden columns not removed.
     * 
     * NOTE: this is testing internal behaviour - don't!
     * it bubbles up to public behaviour in setModel(), 
     * check if anywhere else?
     *
     */
    public void testRemoveAllColumns() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setVisible(false);
        table.removeColumns();
        assertEquals("all columns must have been removed", 
                table.getColumnCount(), table.getColumnCount(true));
    }
    
    /**
     * Issue #54: hidden columns not removed on setModel.
     *
     */
    public void testRemoveAllColumsAfterModelChanged() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setVisible(false);
        table.setModel(new DefaultTableModel());
        assertEquals("all columns must have been removed", 0, table.getColumnCount(true));
        assertEquals("all columns must have been removed", 
                table.getColumnCount(), table.getColumnCount(true));
        assertEquals("sorter must be removed when column removed", null, table.getFilters().getSorter());
    }
    
    /**
     * testing contract of getColumnExt.
     *
     */
    public void testColumnExt() {
        JXTable table = new JXTable(sortableTableModel);
        /// arrgghhh... autoboxing ?
//        Object zeroName = table.getColumn(0).getIdentifier();
        Object zeroName = table.getColumnModel().getColumn(0).getIdentifier();
        Object oneName = table.getColumnModel().getColumn(1).getIdentifier();
        TableColumn column = table.getColumn(zeroName);
        ((TableColumnExt) column).setVisible(false);
        try {
            // access the invisible column by the inherited method
            table.getColumn(zeroName);
            fail("table.getColumn(identifier) guarantees to fail if identifier "
                    + "is unknown or column is hidden");
        } catch (Exception e) {
            // this is what we expect
        }
        // access the invisible column by new method
        TableColumnExt columnZero = table.getColumnExt(zeroName);
        // sanity..
        assertNotNull(columnZero);
        int viewIndexZero = table.convertColumnIndexToView(columnZero
                .getModelIndex());
        assertTrue("viewIndex must be negative for invisible", viewIndexZero < 0);
        // a different way to state the same
        assertEquals(columnZero.isVisible(), viewIndexZero >= 0);
        TableColumnExt columnOne = table.getColumnExt(oneName);
        // sanity..
        assertNotNull(columnOne);
        int viewIndexOne = table.convertColumnIndexToView(columnOne
                .getModelIndex());
        assertTrue("viewIndex must be positive for visible", viewIndexOne >= 0);
        assertEquals(columnOne.isVisible(), viewIndexOne >= 0);
    }
    /**
     * Issue #189, #214: Sorter fails if content is comparable with mixed types
     * 
     */
    public void testMixedComparableTypes() {
        
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, new Integer(2) },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        DefaultTableModel model =  new DefaultTableModel(rowData, columnNames);
        final JXTable table = new JXTable(model);
        table.setSorter(1);
    }   
    
    /**
     * Issue #189, #214: Sorter fails if content is 
     * mixed comparable/not comparable
     *
     */
    public void testMixedComparableTypesWithNonComparable() {
        
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, new Integer(2) },
                new Object[] { Boolean.TRUE, new Object() } };
        String[] columnNames = new String[] { "Critical", "Task" };
        DefaultTableModel model =  new DefaultTableModel(rowData, columnNames);
        final JXTable table = new JXTable(model);
        table.setSorter(1);
    }   

    public void testIncrementalSearch() {
        JXTable table = new JXTable(createAscendingModel(10, 10));
        int row = 0;
        String ten = table.getValueAt(row, 0).toString();
        // sanity assert
        assertEquals("10", ten);
        int found = table.getSearchable().search("1", -1);
        assertEquals("must have found first row", row, found);
        int second = table.getSearchable().search("10", found);
        assertEquals("must have found incrementally at same position", found, second);
    }
    
    /**
     * Issue #196: backward search broken.
     *
     */
    public void testBackwardSearch() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        int found = table.getSearchable().search(Pattern.compile(lastName), -1, true);
        assertEquals(row, found);
    }

    /**
     * Issue #187: filter update removes interactive sorter.
     *
     */
    public void testFilterUpdateKeepsSorter() {
        int rowCount = 20;
        int firstValue = 0;
        JXTable table = new JXTable(createAscendingModel(firstValue, rowCount));
        table.setSorter(0);
        // sort descending
        table.setSorter(0);
        Object value = table.getValueAt(0, 0);
        assertEquals("highest value", value, firstValue + rowCount - 1);
        PatternFilter filter = new PatternFilter(".*", 0, 0);
        // set a filter
        table.setFilters(new FilterPipeline(new Filter[] {filter}));
        assertEquals("highest value unchanged", value, table.getValueAt(0, 0 ));
        // update the filter
        filter.setPattern("^1", 0);
        assertTrue("sorter must be active", 
                ((Integer) table.getValueAt(0, 0)).intValue() > ((Integer) table.getValueAt(1, 0)));
    }
    
    /**
     * Issue #175: multiple registration as PipelineListener.
     * 
     *
     */
    public void testRegisterUniquePipelineListener() {
        JXTable table = new JXTable();
        PatternFilter noFilter = new PatternFilter(".*", 0, 1);
        table.setFilters(new FilterPipeline(new Filter[] {noFilter}));
        int listenerCount = table.getFilters().getPipelineListeners().length;
        table.setModel(createAscendingModel(0, 20));
        assertEquals("pipeline listener count must not change after setModel", listenerCount, table.getFilters().getPipelineListeners().length);
        
    }
    /**
     * Issue #174: componentAdapter.hasFocus() looks for anchor instead of lead.
     *
     */
    public void testLeadFocusCell() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run leadFocusCell - headless environment");
            return;
        }
        final JXTable table = new JXTable();
        table.setModel(createAscendingModel(0, 10));
        final JXFrame frame = new JXFrame();
        frame.add(table);
        frame.pack();
        frame.setVisible(true);
        table.requestFocus();
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        final int leadRow = table.getSelectionModel().getLeadSelectionIndex();
        int anchorRow = table.getSelectionModel().getAnchorSelectionIndex();
        table.addColumnSelectionInterval(0, 0);
        final int leadColumn = table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        int anchorColumn = table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
        assertEquals("lead must be last row", table.getRowCount() - 1, leadRow);
        assertEquals("anchor must be second last row", table.getRowCount() - 2, anchorRow);
        assertEquals("lead must be first column", 0, leadColumn);
        assertEquals("anchor must be first column", 0, anchorColumn);
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ComponentAdapter adapter = table.getComponentAdapter();
                adapter.row = leadRow;
                adapter.column = leadColumn;
                // difficult to test - hasFocus() implies that the table isFocusOwner()
                try {
                    assertTrue("adapter must have focus for leadRow/Column: " + adapter.row + "/" + adapter.column, 
                            adapter.hasFocus());
                } finally {
                    frame.dispose();
                }

            }
        });
    }
    
    /**
     * Issue #33-swingx: selection not restored after refresh of interactive sorter.
     *
     */
    public void testSelectionOnSorterRefresh() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setSorter(0);
        Sorter sorter = table.getSorter(0);
        // sanity assert
        assertTrue(sorter.isAscending());
        // select the first row
        table.setRowSelectionInterval(0, 0);
        sorter.setAscending(false);
        assertEquals("last row must be selected", table.getRowCount() - 1, table.getSelectedRow());
    }
    /**
     * Issue #173: 
     * ArrayIndexOOB if replacing model with one containing
     * fewer rows and the "excess" is selected.
     *
     */
    public void testSelectionAndToggleModel() {
        JXTable table = new JXTable();
        table.setModel(createAscendingModel(0, 10));
        // sort first column
        table.setSorter(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.setSorter(0);
        // set model with less rows
        table.setModel(createAscendingModel(0, 8));
        
    }
    
    /**
     * testing selection and adding rows.
     * 
     *
     */
    public void testSelectionAndAddRows() {
        JXTable table = new JXTable();
        DefaultTableModel model = createAscendingModel(0, 10);
        table.setModel(model);
        // sort first column
        table.setSorter(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.setSorter(0);
        
        Integer highestValue = new Integer(100);
        model.addRow(new Object[] { highestValue });
        assertEquals(highestValue, table.getValueAt(0, 0));
    }

    /**
     * Issue #??: removing row throws ArrayIndexOOB on selection
     *
     */
    public void testSelectionRemoveRowsReselect() {
        JXTable table = new JXTable();
        DefaultTableModel model = createAscendingModel(0, 10);
        table.setModel(model);
        // sort first column
        table.setSorter(0);
        // invert sort
        table.setSorter(0);
        // select last row
        int modelLast = table.getRowCount() - 1;
        table.setRowSelectionInterval(modelLast, modelLast);
        model.removeRow(table.convertRowIndexToModel(modelLast));
        table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
    }

    
    /**
     * Issue #16: removing row throws ArrayIndexOOB if
     * last row was selected
     *
     */
    public void testSelectionAndRemoveRows() {
        JXTable table = new JXTable();
        DefaultTableModel model = createAscendingModel(0, 10);
        table.setModel(model);
        // sort first column
        table.setSorter(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.setSorter(0);
        model.removeRow(0);
    }

    public void testDeleteRowAboveIndividualRowHeight() {
        DefaultTableModel model = createAscendingModel(0, 10);
        JXTable table = new JXTable(model);
        table.setRowHeightEnabled(true);
        int selectedRow = table.getRowCount() - 1;
        table.setRowHeight(selectedRow, 25);
        table.setSorter(0);
        assertEquals("last row is individual", 25, table.getRowHeight(selectedRow));
        model.removeRow(0);
        assertEquals("last row is individual", 25, table.getRowHeight(selectedRow - 1));
        
    }
    /**
     * Issue #223
     * 
     * test if selection is updated on remove row above selection.
     */
    public void testDeleteRowAboveSelection() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline
        table.setSorter(0);
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.removeRow(0);
        assertEquals("last row must still be selected after remove be selected", table.getRowCount() - 1, table.getSelectedRow());
        
    }

    /**
     * Issue #223
     * 
     * test if selection is updated on add row above selection.
     */
    public void testAddRowAboveSelection() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.insertRow(0, new Object[table.getColumnCount()]);
        assertEquals("last row must still be selected after add above", table.getRowCount() - 1, table.getSelectedRow());
    }

    public void testAddRowAboveIndividualRowHeigh() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        table.setRowHeightEnabled(true);
        int selectedRow = table.getRowCount() - 1;
        table.setRowHeight(selectedRow, 25);
        assertEquals("last row must have indy rowheight", 25, table.getRowHeight(selectedRow));
        ascendingModel.insertRow(0, new Object[table.getColumnCount()]);
        assertEquals("last row must still have indy rowheight after add above", 25, table.getRowHeight(selectedRow + 1));
    }

    /**
     * Issue #223
     * test if selection is updated on add row above selection.
     *
     */
    public void testAddRowAboveSelectionInvertedOrder() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        // select the last row in view coordinates
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline - ascending, no change
        table.setSorter(0);
        // revert order 
        table.setSorter(0);
        assertEquals("first row must be selected", 0, table.getSelectedRow());
        // remove row in model coordinates
        Object[] row = new Integer[table.getColumnCount()];
        // insert high value
        row[0] = new Integer(100);
        ascendingModel.addRow(row);
        // selection must be moved one below
        assertEquals("selection must be incremented by one ", 1, table.getSelectedRow());
        
    }

    public void testAddRowAboveIndividualRowHeightInvertedOrder() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        table.setRowHeightEnabled(true);
        // select the last row in view coordinates
        int selectedRow = table.getRowCount() - 1;
        table.setRowHeight(selectedRow, 25);
        // set a pipeline - ascending, no change
        table.setSorter(0);
        // revert order 
        table.setSorter(0);
        assertEquals("first row must have indy rowheight", 25, table.getRowHeight(0));
        // remove row in model coordinates
        Object[] row = new Integer[table.getColumnCount()];
        // insert high value
        row[0] = new Integer(100);
        ascendingModel.addRow(row);
        // selection must be moved one below
        assertEquals("row with indy height must be incremented by one ", 25, table.getRowHeight(1));
        
    }

    
    /**
     * Issue #223
     * test if selection is updated on remove row above selection.
     *
     */
    public void testDeleteRowAboveSelectionInvertedOrder() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        // select the last row in view coordinates
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline - ascending, no change
        table.setSorter(0);
        // revert order 
        table.setSorter(0);
        assertEquals("first row must be selected", 0, table.getSelectedRow());
        // remove row in model coordinates
        ascendingModel.removeRow(0);
        assertEquals("first row must still be selected after remove ", 0, table.getSelectedRow());
        
    }
    /**
     * Issue #223
     * test if selection is kept if row below selection is removed.
     *
     */
    public void testDeleteRowBelowSelection() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline
        table.setSorter(0);
        // revert order - fails... track down
//        table.setSorter(0);
        assertEquals("first row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.removeRow(selectedRow + 1);
        assertEquals("first row must still be selected after remove", selectedRow, table.getSelectedRow());
        
    }

    /**
     * Issue #223
     * test if selection is kept if row below selection is removed.
     *
     */
    public void testDeleteRowBelowSelectionInvertedOrder() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // set a pipeline
        table.setSorter(0);
        // revert order - fails... track down
        table.setSorter(0);
        assertEquals("last row must be selected", table.getRowCount() - 1, table.getSelectedRow());
        ascendingModel.removeRow(selectedRow + 1);
        assertEquals("last row must still be selected after remove", table.getRowCount() - 1, table.getSelectedRow());
        
    }
    /**
     * Issue #223
     * test if selection is kept if row in selection is removed.
     *
     */
    public void testDeleteLastRowInSelection() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        int lastSelectedRow = 1;
        table.setRowSelectionInterval(selectedRow, lastSelectedRow);
        // set a pipeline
        table.setSorter(0);
        int[] selectedRows = table.getSelectedRows();
        for (int i = selectedRow; i <= lastSelectedRow; i++) {
            assertEquals("row must be selected " + i, i, selectedRows[i]);
            
        }
        ascendingModel.removeRow(lastSelectedRow);
        int[] selectedRowsAfter = table.getSelectedRows();
        for (int i = selectedRow; i < lastSelectedRow; i++) {
            assertEquals("row must be selected " + i, i, selectedRowsAfter[i]);
            
        }
        assertFalse("removed row must not be selected " + lastSelectedRow, table.isRowSelected(lastSelectedRow));
        
    }

    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first column
     * starting from startRow.
     * @param startRow the value of the first row
     * @param count the number of rows
     * @return
     */
    protected DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 4) {
            public Class getColumnClass(int column) {
                return column == 0 ? Integer.class : super.getColumnClass(column);
            }
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }
    
    /**
     * check if setting to false really disables sortability.
     *
     */
    public void testSortable() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        boolean sortable = table.isSortable();
        // sanity assert: sortable defaults to true
        assertTrue("JXTable sortable defaults to true", sortable);
        table.setSorter(0);
        Object first = table.getValueAt(0, 0);
        table.setSortable(false);
        assertFalse(table.isSortable());
        // reverse the sorting order on first column
        table.setSorter(0);
        assertEquals("sorting on a non-sortable table must do nothing", first, table.getValueAt(0, 0));
    }
    
    /**
     * Issue #171: row-coordinate not transformed in isCellEditable (sorting)
     *
     */
    public void testSortedEditability() {
        int rows = 2;
        RowObjectTableModel model = createRowObjectTableModel(rows);
        JXTable table = new JXTable(model);
        RowObject firstInModel = model.getRowObject(0);
        assertEquals("rowObject data must be equal", firstInModel.getData1(), table.getValueAt(0, 0));
        assertEquals("rowObject editability must be equal", firstInModel.isEditable(), table.isCellEditable(0, 0));
        // nothing changed
        table.setSorter(0);
        Object firstDataValueInTable = table.getValueAt(0,0);
        boolean firstEditableValueInTable = table.isCellEditable(0, 0);
        assertEquals("rowObject data must be equal", firstInModel.getData1(), table.getValueAt(0, 0));
        assertEquals("rowObject editability must be equal", firstInModel.isEditable(), table.isCellEditable(0, 0));
        // sanity assert: first and last have different values/editability
        assertTrue("lastValue different from first", firstDataValueInTable !=
                table.getValueAt(rows - 1, 0));
        assertTrue("lastEditability different from first", firstEditableValueInTable !=
            table.isCellEditable(rows - 1, 0));
        // reverse order
        table.setSorter(0);
        assertEquals("last row data must be equal to former first", firstDataValueInTable, 
                table.getValueAt(rows - 1, 0));
        assertEquals("last row editability must be equal to former first", firstEditableValueInTable, 
                table.isCellEditable(rows - 1, 0));
    }

    /**
     * Issue #171: row-coordinate not transformed in isCellEditable (filtering)
     *
     */
    public void testFilteredEditability() {
        int rows = 2;
        RowObjectTableModel model = createRowObjectTableModel(rows);
        JXTable table = new JXTable(model);
        // sanity assert
        for (int i = 0; i < table.getRowCount(); i++) {
            assertEquals("even/uneven rows must be editable/notEditable " + i,
                    i % 2 == 0, table.isCellEditable(i, 0));
        }
        // need to chain two filters (to reach the "else" block in
        // filter.isCellEditable()
        PatternFilter filter = new PatternFilter("^NOT", 0, 1);
        PatternFilter noFilter = new PatternFilter(".*", 0, 1);

        table.setFilters(new FilterPipeline(new Filter[] {noFilter, filter}));
        assertEquals("row count is half", rows / 2, table.getRowCount());
        for (int i = 0; i < table.getRowCount(); i++) {
            assertFalse("all rows must be not-editable " + i, table.isCellEditable(i, 0));
            
        }
    }

//----------------------- test data for exposing #171 (Tim Dilks)
    
    /**
     * create test model - all cells in even rows are editable, 
     * in odd rows are not editable. 
     * @param rows the number of rows to create
     * @return
     */
    private RowObjectTableModel createRowObjectTableModel(int rows) {
        List rowObjects = new ArrayList();
        for (int i = 0; i < rows; i++) {
            rowObjects.add(new RowObject("somedata" + i, i % 2 == 0));
        }
        return new RowObjectTableModel(rowObjects);
    }

    /**
     * test object to map in test table model.
     */
    static class RowObject {

        private String data1;
        private boolean editable;

        public RowObject(String data1, boolean editable) {
            this.data1 = data1;
            this.editable = editable;
        }

        public String getData1() {
            return data1;
        }
      
        public boolean isEditable() {
            return editable;
        }

    }

    /** 
     *  test TableModel wrapping RowObject.
     */
    static class RowObjectTableModel extends AbstractTableModel {

        List data;

        public RowObjectTableModel(List data) {
            this.data = data;
        }

        public RowObject getRowObject(int row) {
            return (RowObject) data.get(row);
        }
        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return data.size();
        }

        public Object getValueAt(int row, int col) {
            RowObject object = getRowObject(row);
            switch (col) {
                case 0 :
                    return object.getData1();
                case 1 :
                    return object.isEditable() ? "EDITABLE" : "NOT EDITABLE";
                default :
                    return null;
            }
        }

        public boolean isCellEditable(int row, int col) {
            return getRowObject(row).isEditable();
        }
    }

    

    /**
     * Issue #167: IllegalStateException if re-setting filter while
     * sorting.
     *
     */
    public void testToggleFiltersWhileSorting() {
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, "AA" },
                new Object[] { Boolean.FALSE, "AB" },
                new Object[] { Boolean.FALSE, "AC" },
                new Object[] { Boolean.TRUE, "BA" },
                new Object[] { Boolean.FALSE, "BB" },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        final JXTable table = new JXTable(rowData, columnNames);
//        Filter filterA = new PatternFilter("A.*", Pattern.CASE_INSENSITIVE, 1);
        // simulates the sequence of user interaction as described in 
        // the original bug report in 
        // http://www.javadesktop.org/forums/thread.jspa?messageID=56285
        table.setFilters(createFilterPipeline(false, 1));//new FilterPipeline(new Filter[] {filterA}));
        table.setSorter(1);
//        Filter filterB = new PatternFilter(".*", Pattern.CASE_INSENSITIVE, 1);
        table.setFilters(createFilterPipeline(true, 1)); //new FilterPipeline(new Filter[] {filterB}));
        table.setSorter(1);
    }

    /**
     * Issue #167: IllegalStateException if re-setting filter while
     * sorting.
     * Another variant ...
     *
     */
    public void testToggleFiltersWhileSortingLonger() {
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, "AA" },
                new Object[] { Boolean.FALSE, "AB" },
                new Object[] { Boolean.FALSE, "AC" },
                new Object[] { Boolean.TRUE, "BA" },
                new Object[] { Boolean.FALSE, "BB" },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        final JXTable table = new JXTable(rowData, columnNames);
        // simulates the sequence of user interaction as described in 
        // the follow-up bug report in 
        // http://www.javadesktop.org/forums/thread.jspa?messageID=56285
        table.setFilters(createFilterPipeline(false, 1));
        table.setSorter(0);
        table.setSorter(1);
        table.setFilters(createFilterPipeline(true, 1));
        table.setFilters(createFilterPipeline(false, 1));
        table.setSorter(0);
    }
    
    private FilterPipeline createFilterPipeline(boolean matchAll, int col) {
//        RowSorterFilter filter = new RowSorterFilter();
//        if (matchAll) {
//            filter.setRowFilter(RowFilter.regexFilter(".*", col));
//            
//        } else {
//            filter.setRowFilter(RowFilter.regexFilter("A.*", col));
//        }
        Filter filter;
        if (matchAll) {
            filter = new PatternFilter(".*", Pattern.CASE_INSENSITIVE, col);
        } else {
           filter = new PatternFilter("^A", Pattern.CASE_INSENSITIVE, col);
        }
        return new FilterPipeline(new Filter[] {filter});
        
    }
    /**
     * Issue #125: setting filter to null doesn't clean up.
     * 
     * A visual consequence is that the hidden (by the old
     * filters) rows don't appear. A not-so visual consequence
     * is that the sorter is out of synch and accessing a row in
     * the region outside of the formerly filtered. 
     *
     */
    public void testRemoveFilterWhileSorting() {
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, "AA" },
                new Object[] { Boolean.FALSE, "AB" },
                new Object[] { Boolean.FALSE, "AC" },
                new Object[] { Boolean.TRUE, "BA" },
                new Object[] { Boolean.FALSE, "BB" },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        final JXTable table = new JXTable(rowData, columnNames);
        int rows = table.getRowCount();
//        Filter filterA = new PatternFilter("A.*", Pattern.CASE_INSENSITIVE, 1);
        table.setFilters(createFilterPipeline(false, 1)); //new FilterPipeline(new Filter[] {filterA}));
        table.setSorter(1);
        table.setFilters(null);
        assertEquals("rowCount must be original", rows, table.getRowCount());
        table.getValueAt(rows - 1, 0);

    
    }   

    /**
     * Issue #134: JXTable - default renderers not loaded.
     * To fix the issue the JXTable internal renderers' access scope
     *  was changed to public. Note: if the JTable internal renderers
     * access scope were to be widened then this test has to be changed
     * (the comparing class are hardcoded).
     *
     */
    public void testLazyRenderersByClass() {
        JXTable table = new JXTable();
        assertEquals("default Boolean renderer", JXTable.BooleanRenderer.class, table.getDefaultRenderer(Boolean.class).getClass());
        assertEquals("default Number renderer", JXTable.NumberRenderer.class, table.getDefaultRenderer(Number.class).getClass());
        assertEquals("default Double renderer", JXTable.DoubleRenderer.class, table.getDefaultRenderer(Double.class).getClass());
        assertEquals("default Date renderer", JXTable.DateRenderer.class, table.getDefaultRenderer(Date.class).getClass());
        assertEquals("default LinkModel renderer", LinkRenderer.class, table.getDefaultRenderer(LinkModel.class).getClass());
        assertEquals("default Icon renderer", JXTable.IconRenderer.class, table.getDefaultRenderer(Icon.class).getClass());
    }

    /** 
     * Issue #150: setting filters must not re-create columns.
     *
     */
    public void testTableColumnsWithFilters() {
        JXTable table = new JXTable(tableModel);
        assertEquals("table columns are equal to columns of model", 
                tableModel.getColumnCount(), table.getColumnCount());
        TableColumn column = table.getColumnExt(0);
        table.removeColumn(column);
        int columnCountAfterRemove = table.getColumnCount();
        assertEquals("table columns must be one less than columns of model",
                tableModel.getColumnCount() - 1, columnCountAfterRemove);
        table.setFilters(new FilterPipeline(new Filter[] {
                new ShuttleSorter(1, false), // column 1, descending
        }));
        assertEquals("table columns must be unchanged after setting filter",
                columnCountAfterRemove, table.getColumnCount());
        
    }
    
//-------------------------- tests for moving column control into swingx

    
    /**
     * hmm... sporadic ArrayIndexOOB after sequence:
     * 
     * filter(column), sort(column), hide(column), setFilter(null)
     *
     */
    public void testColumnControlAndFilters() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setColumnControlVisible(true);
        Filter filter = new PatternFilter("e", 0, 0);
        table.setFilters(new FilterPipeline(new Filter[] {filter}));
        // needed to make public in JXTable for testing
        //   table.getTable().setSorter(0);
        table.getColumnExt(0).setVisible(false);
        table.setFilters(null);

    }

    

    public static class DynamicTableModel extends AbstractTableModel {
        private Object columnSamples[];
        private Object columnSamples2[];
        public URL linkURL;

        public static final int IDX_COL_LINK = 6;

        public DynamicTableModel() {
            try {
                linkURL = new URL("http://www.sun.com");
            }
            catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }

            columnSamples = new Object[12];
            columnSamples[0] = new Integer(0);
            columnSamples[1] = "Simple String Value";
            columnSamples[2] = new Integer(1000);
            columnSamples[3] = Boolean.TRUE;
            columnSamples[4] = new Date(100);
            columnSamples[5] = new Float(1.5);
            columnSamples[IDX_COL_LINK] = new LinkModel("Sun Micro", "_blank", linkURL);
            columnSamples[7] = new Integer(3023);
            columnSamples[8] = "John Doh";
            columnSamples[9] = "23434 Testcase St";
            columnSamples[10] = new Integer(33333);
            columnSamples[11] = Boolean.FALSE;

            columnSamples2 = new Object[12];
            columnSamples2[0] = new Integer(0);
            columnSamples2[1] = "Another String Value";
            columnSamples2[2] = new Integer(999);
            columnSamples2[3] = Boolean.FALSE;
            columnSamples2[4] = new Date(333);
            columnSamples2[5] = new Float(22.22);
            columnSamples2[IDX_COL_LINK] = new LinkModel("Sun Web", "new_frame", linkURL);
            columnSamples[7] = new Integer(5503);
            columnSamples[8] = "Jane Smith";
            columnSamples[9] = "2343 Table Blvd.";
            columnSamples[10] = new Integer(2);
            columnSamples[11] = Boolean.TRUE;

        }

        public DynamicTableModel(Object columnSamples[]) {
            this.columnSamples = columnSamples;
        }

        public Class getColumnClass(int column) {
            return columnSamples[column].getClass();
        }

        public int getRowCount() {
            return 1000;
        }

        public int getColumnCount() {
            return columnSamples.length;
        }

        public Object getValueAt(int row, int column) {
            Object value;
            if (row % 3 == 0) {
                value = columnSamples[column];
            }
            else {
                value = columnSamples2[column];
            }
            return column == 0 ? new Integer(row >> 3) :
                column == 3 ? new Boolean(row % 2 == 0) : value;
        }

        public boolean isCellEditable(int row, int column) {
            return (column == 1);
        }

        public void setValueAt(Object aValue, int row, int column) {
            if (column == 1) {
                if (row % 3 == 0) {
                    columnSamples[column] = aValue;
                }
                else {
                    columnSamples2[column] = aValue;
                }
            }
            this.fireTableDataChanged();
        }
    }
}
