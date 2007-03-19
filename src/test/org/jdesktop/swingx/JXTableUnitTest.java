/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable.GenericEditor;
import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.decorator.SortKey;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.decorator.Sorter;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.NumberEditorExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.ChangeReport;
import org.jdesktop.test.PropertyChangeReport;

/**
* Tests of <code>JXTable</code>.
* 
* TODO: update hyperlink related test to use HyperlinkProvider instead of the
* deprecated LinkRenderer.
* 
* @author Jeanette Winzenburg
*/
public class JXTableUnitTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTableUnitTest.class
            .getName());

    protected DynamicTableModel tableModel = null;
    protected TableModel sortableTableModel;

    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;
    // stored ui properties to reset in teardown
    private Object uiTableRowHeight;

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
        uiTableRowHeight = UIManager.get("JXTable.rowHeight");
    }

    
    @Override
    protected void tearDown() throws Exception {
        UIManager.put("JXTable.rowHeight", uiTableRowHeight);
        super.tearDown();
    }

    /**
     * Test default behaviour: hack around DefaultTableCellRenderer 
     * color memory is on. 
     *
     */
    public void testDTCRendererHackEnabled() {
        JXTable table = new JXTable(10, 2);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        table.prepareRenderer(renderer, 0, 0);
        assertEquals(Boolean.TRUE, table.getClientProperty(JXTable.USE_DTCR_COLORMEMORY_HACK));
        assertNotNull(renderer.getClientProperty("rendererColorMemory.background"));
        assertNotNull(renderer.getClientProperty("rendererColorMemory.foreground"));
    }

    /**
     * Test custom behaviour: hack around DefaultTableCellRenderer 
     * color memory is disabled.
     *
     */
    public void testDTCRendererHackDisabled() {
        JXTable table = new JXTable(10, 2);
        table.putClientProperty(JXTable.USE_DTCR_COLORMEMORY_HACK, null);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        table.prepareRenderer(renderer, 0, 0);
        assertNull(renderer.getClientProperty("rendererColorMemory.background"));
        assertNull(renderer.getClientProperty("rendererColorMemory.foreground"));
    }

    /**
     * Issue #282-swingx: compare disabled appearance of
     * collection views.
     *
     */
    public void testDisabledRenderer() {
        JXList list = new JXList(new Object[] {"one", "two"});
        list.setEnabled(false);
        // sanity
        assertFalse(list.isEnabled());
        Component comp = list.getCellRenderer().getListCellRendererComponent(list, "some", 0, false, false);
        assertEquals(list.isEnabled(), comp.isEnabled());
        JXTable table = new JXTable(10, 2);
        table.setEnabled(false);
        // sanity
        assertFalse(table.isEnabled());
        comp = table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals(table.isEnabled(), comp.isEnabled());
    }

    /**
     * Test assumptions of accessing table model/view values through
     * the table's componentAdapter.
     * 
     * PENDING: the default's getValue() implementation is incorrect!
     *
     */
    public void testComponentAdapterCoordinates() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
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
        ComponentAdapter adapter = table.getComponentAdapter();
        assertEquals("adapter filteredValue expects row view coordinates", 
                table.getValueAt(0, 0), adapter.getFilteredValueAt(0, 0));
        // adapter coordinates are view coordinates
        adapter.row = 0;
        adapter.column = 0;
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                table.getValueAt(0, 0), adapter.getValue());
        
        
    }

    /**
     * Issue 373-swingx: table must unsort column on sortable change.
     *
     * Here we test if switching sortable to false on the sorted column
     * resets the sorting.
     * 
     */
    public void testTableUnsortedColumnOnColumnSortableChange() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.toggleSortOrder(0);
        assertTrue(table.getSortOrder(0).isSorted());
        columnExt.setSortable(false);
        assertFalse("table must have unsorted column on sortable change", 
                table.getSortOrder(0).isSorted());
    }
    
    /**
     * Issue 373-swingx: table must unsort column on sortable change.
     *
     * Here we test if switching sortable to false on unsorted column has
     * no effect.
     */
    public void testTableSortedColumnOnNotSortedColumnSortableChange() {
        JXTable table = new JXTable(10, 2);
        int unsortedColumn = 1;
        TableColumnExt columnExt = table.getColumnExt(unsortedColumn);
        table.toggleSortOrder(0);
        assertTrue(table.getSortOrder(0).isSorted());
        columnExt.setSortable(false);
        assertTrue("table must keep sortorder on unsorted column sortable change", 
                table.getSortOrder(0).isSorted());
    }

    
    /**
     * Issue 372-swingx: table must cancel edit if column property 
     *   changes to not editable.
     * Here we test if the table is not editing after editable property
     * of the currently edited column is changed to false.
     */
    public void testTableNotEditingOnColumnEditableChange() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingColumn());
        columnExt.setEditable(false);
        assertFalse(table.isCellEditable(0, 0));
        assertFalse("table must have terminated edit",table.isEditing());
    }
    
    /**
     * Issue 372-swingx: table must cancel edit if column property 
     *   changes to not editable.
     * Here we test if the table is still editing after the editability 
     * change of a non-edited column.
     * 
     */
    public void testTableEditingOnNotEditingColumnEditableChange() {
        JXTable table = new JXTable(10, 2);
        int notEditingColumn = 1;
        TableColumnExt columnExt = table.getColumnExt(notEditingColumn);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingColumn());
        columnExt.setEditable(false);
        assertFalse(table.isCellEditable(0, notEditingColumn));
        assertTrue("table must still be editing", table.isEditing());
    }
    
    /**
     * Issue 372-swingx: table must cancel edit if column property 
     *   changes to not editable.
     * Here we test if the table is still editing after the editability 
     * change of a non-edited column, special case of hidden column. <p>
     * NOTE: doesn't really test, the columnModel doesn't
     * fire propertyChanges for hidden columns (see Issue #??-swingx)
     * 
     */
    public void testTableEditingOnHiddenColumnEditableChange() {
        JXTable table = new JXTable(10, 2);
        int hiddenNotEditingColumn = 1;
        TableColumnExt columnExt = table.getColumnExt(hiddenNotEditingColumn);
        columnExt.setVisible(false);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingColumn());
        columnExt.setEditable(false);
        assertTrue("table must still be editing", table.isEditing());
    }

    /**
     * Test if default column creation and configuration is 
     * controlled completely by ColumnFactory.
     *
     */
    public void testColumnConfigControlledByFactory() {
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                assertNull(columnExt.getHeaderValue());
            }
            
        };
        JXTable table = new JXTable();
        table.setColumnFactory(factory);
        table.setModel(new DefaultTableModel(10, 2));
        assertEquals(null, table.getColumn(0).getHeaderValue());
    }
    /**
     * Sanity test for cleanup of createDefaultColumns.
     *
     */
    public void testColumnFactory() {
        JXTable table = new JXTable(sortableTableModel);
        List<TableColumn> columns = table.getColumns();
        // for all model columns and in same order..
        assertEquals(sortableTableModel.getColumnCount(), columns.size());
        for (int i = 0; i < sortableTableModel.getColumnCount(); i++) {
            // there must have been inserted a TableColumnExt with 
            // title == headerValue == column name in model
            assertTrue(columns.get(i) instanceof TableColumnExt);
            assertEquals(sortableTableModel.getColumnName(i), 
                    String.valueOf(columns.get(i).getHeaderValue()));
        }
    }
    /**
     * Tests per-table ColumnFactory: bound property, reset to shared.
     * 
     */
    public void testSetColumnFactory() {
        JXTable table = new JXTable();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        ColumnFactory factory = createCustomColumnFactory();
        table.setColumnFactory(factory);
        assertEquals(1, report.getEventCount());
        assertTrue(report.hasEvents("columnFactory"));
        assertSame(factory, report.getLastNewValue("columnFactory"));
        assertSame(ColumnFactory.getInstance(), report.getLastOldValue("columnFactory"));
        report.clear();
        table.setColumnFactory(null);
        assertEquals(1, report.getEventCount());
        assertTrue(report.hasEvents("columnFactory"));
        assertSame(factory, report.getLastOldValue("columnFactory"));
        assertSame(ColumnFactory.getInstance(), report.getLastNewValue("columnFactory"));
    }
    
    /**
     * Tests per-table ColumnFactory: use individual.
     * 
     */
    public void testUseCustomColumnFactory() {
        JXTable table = new JXTable();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        ColumnFactory factory = createCustomColumnFactory();
        table.setColumnFactory(factory);
        // sanity...
        assertSame(factory, report.getLastNewValue("columnFactory"));
        table.setModel(new DefaultTableModel(2, 5));
        assertEquals(String.valueOf(0), table.getColumnExt(0).getTitle());
    }
    
    /**
     * Creates and returns a custom columnFactory for testing. 
     * Sets column title to modelIndex.
     * 
     * @return the custom ColumnFactory.
     */
    protected ColumnFactory createCustomColumnFactory() {
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model,
                    TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                columnExt.setTitle(String.valueOf(columnExt.getModelIndex()));
            }

        };
        return factory;
        
    }
    
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
        JXTable table = new JXTable(model);
        table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
    }


    /**
     * Issue #366-swingx: enhance generic editor to take custom
     * textfield as argument.
     * 
     */
    public void testGenericEditor() {
        JTextField textField = new JTextField(20);
        GenericEditor editor = new GenericEditor(textField);
        assertEquals("Table.editor", textField.getName());
        // sanity
        assertSame(textField, editor.getComponent());
    }
    
    /**
     * test default rowHeight calculation with default font.
     * Beware: the default height is the font's height + 2, but 
     * bounded by a "magic" minimum of 18.
     */
    public void testRowHeightFromFont() {
        // sanity
        assertNull("no ui rowheight", UIManager.get("JXTable.rowHeight"));
        JXTable table = new JXTable();
        // wrong assumption: there's a "magic" minimum of 18!           
        int fontDerivedHeight = table.getFontMetrics(table.getFont()).getHeight() + 2;
        assertEquals("default rowHeight based on fontMetrics height " +
                        "plus top plus bottom border (== 2)", 
                        Math.max(18, fontDerivedHeight), 
                        table.getRowHeight());
    }
    
    
    /**
     * test default rowHeight calculation with bigger font.
     *
     */
    public void testRowHeightFromBigFont() {
        // sanity
        assertNull("no ui rowheight", UIManager.get("JXTable.rowHeight"));
        JXTable table = new JXTable();
        table.setFont(table.getFont().deriveFont(table.getFont().getSize() * 2f));
        table.updateUI();
        assertEquals("default rowHeight based on fontMetrics height " +
                        "plus top plus bottom border (== 2)", 
                        table.getFontMetrics(table.getFont()).getHeight() + 2, 
                        table.getRowHeight());
    }
    
    /**
     * Issue #359-swingx: table doesn't respect ui-setting of rowheight.
     * 
     * lower bound is enforced to "magic number", 18
     *
     */
    public void testUIRowHeightLowerBound() {
        int tinyRowHeight = 5;
        UIManager.put("JXTable.rowHeight", tinyRowHeight);
        JXTable table = new JXTable();
        assertEquals("table must respect ui rowheight", tinyRowHeight, table.getRowHeight());
        
    }


    /**
     * Issue #359-swingx: table doesn't respect ui-setting of rowheight.
     * 
     * upper bound is taken correctly.
     */
    public void testUIRowHeightUpperBound() {
        int monsterRowHeight = 50;
        UIManager.put("JXTable.rowHeight", monsterRowHeight);
        JXTable table = new JXTable();
        assertEquals("table must respect ui rowheight", monsterRowHeight, table.getRowHeight());
        
    }
    /**
     * Issue #342-swingx: default margins in JXTreeTable.
     * 
     * This is not only a treeTable issue: the coupling of 
     * margins to showing gridlines (and still get a "nice" 
     * looking selection) is not overly obvious in JTable as
     * well. Added convenience method to adjust margins to 
     * 0/1 if hiding/showing grid lines.
     *
     */
    public void testShowGrid() {
        JXTable table = new JXTable(10, 3);
        // sanity: initial margins are (1, 1), grid on
        assertEquals(1, table.getRowMargin());
        assertTrue(table.getShowHorizontalLines());
        assertEquals(1, table.getColumnMargin());
        assertTrue(table.getShowVerticalLines());
        // hide grid
        boolean show = false;
        table.setShowGrid(show, show);
        assertEquals(0, table.getRowMargin());
        assertEquals(show, table.getShowHorizontalLines());
        assertEquals(0, table.getColumnMargin());
        assertEquals(show, table.getShowVerticalLines());
        
    }
    
    /**
     * Issue ??-swingx: NPE if tableChanged is messaged with a null event.
     *
     */
    public void testNullTableEventNPE() {
        JXTable table = new JXTable();
        // don't throw null events
        table.tableChanged(null);
        assertFalse(table.isUpdate(null));
        assertFalse(table.isDataChanged(null));
        assertTrue(table.isStructureChanged(null));
        // correct detection of structureChanged
        TableModelEvent structureChanged = new TableModelEvent(table.getModel(), -1, -1);
        assertFalse(table.isUpdate(structureChanged));
        assertFalse(table.isDataChanged(structureChanged));
        assertTrue(table.isStructureChanged(structureChanged));
        // correct detection of insert/remove
        TableModelEvent insert = new TableModelEvent(table.getModel(), 0, 10, -1, TableModelEvent.INSERT);
        assertFalse(table.isUpdate(insert));
        assertFalse(table.isDataChanged(insert));
        assertFalse(table.isStructureChanged(insert));
        // correct detection of update
        TableModelEvent update = new TableModelEvent(table.getModel(), 0, 10);
        assertTrue(table.isUpdate(update));
        assertFalse(table.isDataChanged(update));
        assertFalse(table.isStructureChanged(update));
        // correct detection of dataChanged
        TableModelEvent dataChanged = new TableModelEvent(table.getModel());
        assertFalse(table.isUpdate(dataChanged));
        assertTrue(table.isDataChanged(dataChanged));
        assertFalse(table.isStructureChanged(dataChanged));
        
    }
    /**
     * test new mutable columnControl api.
     *
     */
    public void testSetColumnControl() {
        JXTable table = new JXTable();
        JComponent columnControl = table.getColumnControl();
        assertTrue(columnControl instanceof ColumnControlButton);
        JComponent newControl = new JButton();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setColumnControl(newControl);
        assertSame(newControl, table.getColumnControl());
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("columnControl"));
        assertSame(newControl, report.getLastNewValue("columnControl"));
    }
    
    /**
     * characterization tests: constructors and exceptions.
     *
     */
    public void testConstructorsWithNullArguments() {
        try {
            new JXTable((Object[][]) null, (Object[]) null);
            fail("null arrays must throw NPE");
        } catch (NullPointerException e) {
            // nothing to do - expected
        } catch (Exception e) {
            fail("unexpected exception type (expected NPE)" + e);
        }
        try {
            new JXTable((Object[][]) null, new Object[] {  });
            fail("null arrays must throw NPE");
        } catch (NullPointerException e) {
            // nothing to do - expected
        } catch (Exception e) {
            fail("unexpected exception type (expected NPE)" + e);
        }
        try {
            new JXTable(new Object[][] {{ }, { }}, (Object[]) null);
            fail("null arrays throw NPE");
            
        } catch (NullPointerException e) {
            // nothing to do - expected
            
        } catch (Exception e) {
            fail("unexpected exception type (expected NPE)" + e);
        }
    }
    /**
     * expose JTable.autoStartsEdit.
     *
     */
    public void testAutoStartEdit() {
        JXTable table = new JXTable(10, 2);
        assertTrue(table.isAutoStartEditOnKeyStroke());
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setAutoStartEditOnKeyStroke(false);
        assertFalse("autoStart must be toggled to false", table.isAutoStartEditOnKeyStroke());
        // the following assumption is wrong because the old client property key is
        // different from the method name, leading to two events fired.
        // assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("autoStartEditOnKeyStroke"));
    }
    
    /**
     * add editable property.
     *
     */
    public void testEditable() {
        JXTable table = new JXTable(10, 2);
        assertTrue("default editable must be true", table.isEditable());
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setEditable(!table.isEditable());
        assertFalse("editable must be toggled to false", table.isEditable());
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("editable"));
    }
    
    /**
     * test effect of editable on cell editing.
     *
     */
    public void testCellEditable() {
        JXTable table = new JXTable(10, 2);
        assertTrue("default table editable must be true", table.isEditable());
        assertTrue("default cell editable must be true", table.isCellEditable(0, 0));
        table.setEditable(!table.isEditable());
        assertFalse("editable must be toggled to false", table.isEditable());
        assertFalse("each cell must be not editable", table.isCellEditable(0, 0));
    }
    
    /**
     * 
     */
    public void testSetValueCellNotEditable() {
        JXTable table = new JXTable(10, 2);
        Object value = table.getValueAt(0, 0);
        table.setEditable(false);
        // sanity...
        assertFalse("each cell must be not editable", table.isCellEditable(0, 0));
        table.setValueAt("wrong", 0, 0);
        assertEquals("cell value must not be changed", value, table.getValueAt(0, 0));
        
    }
    /**
     * Issue #262-swingx: expose terminateEditOnFocusLost as property.
     * 
     * setting client property is reflected in getter and results in event firing.
     *
     */
    public void testGetTerminateEditOnFocusLost() {
       JXTable table = new JXTable();
       // sanity assert: setting client property set's property
       PropertyChangeReport report = new PropertyChangeReport();
       table.addPropertyChangeListener(report);
       table.putClientProperty("terminateEditOnFocusLost", !table.isTerminateEditOnFocusLost());
       assertEquals(table.getClientProperty("terminateEditOnFocusLost"), table.isTerminateEditOnFocusLost());
       assertEquals(1, report.getEventCount());
       assertEquals(1, report.getEventCount("terminateEditOnFocusLost"));
    }
    

    /**
     * Issue #262-swingx: expose terminateEditOnFocusLost as property.
     * 
     * default value is true.
     * 
     */
    public void testInitialTerminateEditOnFocusLost() {
       JXTable table = new JXTable();
       assertTrue("terminate edit must be on by default", table.isTerminateEditOnFocusLost());
    }

    /**
     * Issue #262-swingx: expose terminateEditOnFocusLost as property.
     * 
     * setter is same as setting client property and results in event firing.
     */
    public void testSetTerminateEditOnFocusLost() {
       JXTable table = new JXTable();
       // sanity assert: setting client property set's property
       PropertyChangeReport report = new PropertyChangeReport();
       table.addPropertyChangeListener(report);
       table.setTerminateEditOnFocusLost(!table.isTerminateEditOnFocusLost());
       assertEquals(table.getClientProperty("terminateEditOnFocusLost"), table.isTerminateEditOnFocusLost());
       assertEquals(1, report.getEventCount());
       assertEquals(1, report.getEventCount("terminateEditOnFocusLost"));
       assertEquals(Boolean.FALSE, report.getLastNewValue("terminateEditOnFocusLost"));
    }

    /**
     * sanity test while cleaning up: 
     * getColumns() should return the exact same
     * ordering as getColumns(false);
     *
     */
    public void testColumnSequence() {
        JXTable table = new JXTable(10, 20);
        table.getColumnExt(5).setVisible(false);
        table.getColumnModel().moveColumn(table.getColumnCount() - 1, 0);
        assertEquals(table.getColumns(), table.getColumns(false));
    }
    /**
     * programmatic sorting of hidden column (through table api).
     * 
     */
    public void testSetSortOrderHiddenColumn() {
        JXTable table = new JXTable(new AncientSwingTeam());
        Object identifier = "Last Name";
        TableColumnExt columnExt = table.getColumnExt(identifier);
        columnExt.setVisible(false);
        table.setSortOrder(identifier, SortOrder.ASCENDING);
        assertEquals("sorted column must be at " + identifier, columnExt, table.getSortedColumn());
        assertEquals("column must be sorted after setting sortOrder on " + identifier, SortOrder.ASCENDING, table.getSortOrder(identifier));
        Object otherIdentifier = "First Name";
        table.setSortOrder(otherIdentifier, SortOrder.UNSORTED);
        assertNull("table must be unsorted after resetting sortOrder on " + otherIdentifier,
                table.getSortedColumn());
    }

    /**
     * added xtable.setSortOrder(Object, SortOrder)
     * 
     */
    public void testSetSortOrderByIdentifier() {
        JXTable table = new JXTable(new AncientSwingTeam());
        Object identifier = "Last Name";
        TableColumnExt columnExt = table.getColumnExt(identifier);
        table.setSortOrder(identifier, SortOrder.ASCENDING);
        assertEquals("sorted column must be at " + identifier, columnExt, table.getSortedColumn());
        assertEquals("column must be sorted after setting sortOrder on " + identifier, SortOrder.ASCENDING, table.getSortOrder(identifier));
        Object otherIdentifier = "First Name";
        table.setSortOrder(otherIdentifier, SortOrder.UNSORTED);
        assertNull("table must be unsorted after resetting sortOrder on " + otherIdentifier,
                table.getSortedColumn());
    }
    
    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    public void testSetSortOrderByIdentifierColumnNotSortable() {
        JXTable table = new JXTable(new AncientSwingTeam());
        Object identifier = "Last Name";
        TableColumnExt columnX = table.getColumnExt(identifier);
        //  make column not sortable.
        columnX.setSortable(false);
        table.setSortOrder(identifier, SortOrder.ASCENDING);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(0));
    }

    /**
     * testing new sorter api: 
     * toggleSortOrder(Object), resetSortOrder.
     *
     */
    public void testToggleSortOrderByIdentifier() {
        JXTable table = new JXTable(sortableTableModel);
        Object firstColumn = "First Name";
        Object secondColumn = "Last Name";
        assertSame(SortOrder.UNSORTED, table.getSortOrder(secondColumn));
        table.toggleSortOrder(firstColumn);
        assertSame(SortOrder.ASCENDING, table.getSortOrder(firstColumn));
        // sanity: other columns uneffected
        assertSame(SortOrder.UNSORTED, table.getSortOrder(secondColumn));
        table.toggleSortOrder(firstColumn);
        assertSame(SortOrder.DESCENDING, table.getSortOrder(firstColumn));
        table.resetSortOrder();
        assertSame(SortOrder.UNSORTED, table.getSortOrder(firstColumn));
    }

    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    public void testToggleSortOrderByIdentifierColumnNotSortable() {
        JXTable table = new JXTable(new AncientSwingTeam());
        Object identifier = "Last Name";
        TableColumnExt columnX = table.getColumnExt(identifier);
        // old way: make column not sortable.
        columnX.setSortable(false);
        table.toggleSortOrder(identifier);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(identifier));
       
    }

    /**
     * added xtable.setSortOrder(int, SortOrder)
     * 
     */
    public void testSetSortOrder() {
        JXTable table = new JXTable(new AncientSwingTeam());
        int col = 0;
        TableColumnExt columnExt = table.getColumnExt(col);
        table.setSortOrder(col, SortOrder.ASCENDING);
        assertEquals("sorted column must be at " + col, columnExt, table.getSortedColumn());
        assertEquals("column must be sorted after setting sortOrder on " + col, SortOrder.ASCENDING, table.getSortOrder(col));
        int otherColumn = col + 1;
        table.setSortOrder(otherColumn, SortOrder.UNSORTED);
        assertNull("table must be unsorted after resetting sortOrder on " + otherColumn,
                table.getSortedColumn());
    }
    
    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    public void testSetSortOrderColumnNotSortable() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnX = table.getColumnExt(0);
        // old way: make column not sortable.
        columnX.setSortable(false);
        table.setSortOrder(0, SortOrder.ASCENDING);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(0));
       
    }

    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    public void testToggleSortOrderColumnNotSortable() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnX = table.getColumnExt(0);
        // old way: make column not sortable.
        columnX.setSortable(false);
        table.toggleSortOrder(0);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(0));
       
    }
   
    
    /**
     * JXTable has responsibility to guarantee usage of 
     * TableColumnExt comparator.
     * 
     */
    public void testComparatorToPipeline() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setComparator(Collator.getInstance());
        table.toggleSortOrder(0);
        SortKey sortKey = SortKey.getFirstSortKeyForColumn(table.getFilters().getSortController().getSortKeys(), 0);
        assertNotNull(sortKey);
        assertEquals(columnX.getComparator(), sortKey.getComparator());
    }

    /**
     * resetSortOrders didn't check for tableHeader != null.
     * Didn't show up before new sorter api because method was protected and 
     * only called from JXTableHeader.
     *
     */
    public void testResetSortOrderNPE() {
        JXTable table = new JXTable(sortableTableModel);
        table.setTableHeader(null);
        table.resetSortOrder();
    }
    /**
     * testing new sorter api: 
     * getSortOrder(int), toggleSortOrder(int), resetSortOrder().
     *
     */
    public void testToggleSortOrder() {
        JXTable table = new JXTable(sortableTableModel);
        assertSame(SortOrder.UNSORTED, table.getSortOrder(0));
        table.toggleSortOrder(0);
        assertSame(SortOrder.ASCENDING, table.getSortOrder(0));
        // sanity: other columns uneffected
        assertSame(SortOrder.UNSORTED, table.getSortOrder(1));
        table.toggleSortOrder(0);
        assertSame(SortOrder.DESCENDING, table.getSortOrder(0));
        table.resetSortOrder();
        assertSame(SortOrder.UNSORTED, table.getSortOrder(0));
    }
    
    /**
     * Issue #256-swingX: viewport - do track height.
     * 
     * 
     */
    public void testTrackViewportHeight() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run trackViewportHeight - headless environment");
            return;
        }
        JXTable table = new JXTable(10, 2);
        table.setFillsViewportHeight(true);
        Dimension tablePrefSize = table.getPreferredSize();
        JScrollPane scrollPane = new JScrollPane(table);
        JXFrame frame = wrapInFrame(scrollPane, "");
        frame.setSize(500, tablePrefSize.height * 2);
        frame.setVisible(true);
        assertEquals("table height be equal to viewport", 
                table.getHeight(), scrollPane.getViewport().getHeight());
     }
 
    /**
     * Issue #256-swingX: viewport - don't track height.
     * 
     * 
     */
    public void testNotTrackViewportHeight() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run notTrackViewportHeight - headless environment");
            return;
        }
        JXTable table = new JXTable(10, 2);
        table.setFillsViewportHeight(false);
        Dimension tablePrefSize = table.getPreferredSize();
        JScrollPane scrollPane = new JScrollPane(table);
        JXFrame frame = wrapInFrame(scrollPane, "");
        // make sure the height is > table pref height
        frame.setSize(500, tablePrefSize.height * 2);
        frame.setVisible(true);
        assertEquals("table height must be unchanged", 
                tablePrefSize.height, table.getHeight());
    }

    
    /**
     * Issue #256-swingx: added fillsViewportHeight property.
     * 
     * check "fillsViewportHeight" property change fires event.
     *
     */
    public void testDefaultFillsViewport() {
        JXTable table = new JXTable(10, 1);
        boolean fill = table.getFillsViewportHeight();
        assertTrue("fillsViewport is on by default", fill);
    }
    
    
    /**
     * Issue #256-swingx: added fillsViewportHeight property.
     * 
     * check "fillsViewportHeight" property change fires event.
     *
     */
    public void testFillsViewportProperty() {
        JXTable table = new JXTable(10, 1);
        boolean fill = table.getFillsViewportHeight();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setFillsViewportHeight(!fill);
        assertEquals("must have fired propertyChange for fillsViewportHeight", 1, report.getEventCount("fillsViewportHeight"));
        assertEquals("property must equal newValue", table.getFillsViewportHeight(), report.getLastNewValue("fillsViewportHeight"));
    }
    
    /**
     * Issue #256-swingX: viewport - don't change background
     * in configureEnclosingScrollPane.
     * 
     * 
     */
    public void testUnchangedViewportBackground() {
        JXTable table = new JXTable(10, 2);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setSize(500, 500);
        Color viewportColor = scrollPane.getViewport().getBackground();
        Color tableColor = table.getBackground();
        if ((viewportColor != null) && viewportColor.equals(tableColor)) {
            LOG.info("cannot run test unchanged viewport background because \n" +
                        "viewport has same color as table. \n" +
                        "viewport: " + viewportColor + 
                        "\n table: " + tableColor);
            return;
        }
        scrollPane.setViewportView(table);
        table.configureEnclosingScrollPane();
        assertEquals("viewport background must be unchanged", 
                viewportColor, scrollPane.getViewport().getBackground());
        
        
    }

    /**
     * Issue #214-swingX: improved auto-resize.
     * 
     * 
     */
    public void testTrackViewportWidth() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run trackViewportWidth - headless environment");
            return;
        }
        JXTable table = new JXTable(10, 2);
        table.setHorizontalScrollEnabled(true);
        Dimension tablePrefSize = table.getPreferredSize();
        JScrollPane scrollPane = new JScrollPane(table);
        JXFrame frame = wrapInFrame(scrollPane, "");
        frame.setSize(tablePrefSize.width * 2, tablePrefSize.height);
        frame.setVisible(true);
        assertEquals("table width must be equal to viewport", 
                table.getWidth(), scrollPane.getViewport().getWidth());
     }

    /**
     * Issue #214-swingX: improved auto-resize.
     * 
     *
     */
    public void testSetHorizontalEnabled() {
        JXTable table = new JXTable(10, 2);
        table.setHorizontalScrollEnabled(true);
        assertTrue("enhanced resize property must be enabled", 
                table.isHorizontalScrollEnabled());
        assertHorizontalActionSelected(table, true);
    }

    private void assertHorizontalActionSelected(JXTable table, boolean selected) {
        Action showHorizontal = table.getActionMap().get(
                JXTable.HORIZONTALSCROLL_ACTION_COMMAND);
        assertEquals("horizontAction must be selected"  , selected, 
                ((BoundAction) showHorizontal).isSelected());
    }
 
    /**
     * Issue #214-swingX: improved auto-resize.
     * test autoResizeOff != intelliResizeOff 
     * after sequence a) set intelli, b) setAutoResize
     * 
     */
    public void testNotTrackViewportWidth() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run trackViewportWidth - headless environment");
            return;
        }
        JXTable table = new JXTable(10, 2);
        table.setHorizontalScrollEnabled(true);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        Dimension tablePrefSize = table.getPreferredSize();
        JScrollPane scrollPane = new JScrollPane(table);
        JXFrame frame = wrapInFrame(scrollPane, "");
        frame.setSize(tablePrefSize.width * 2, tablePrefSize.height);
        frame.setVisible(true);
        assertEquals("table width must not be equal to viewport", 
               table.getPreferredSize().width, table.getWidth());
     }
 
    /**
     * Issue #214-swingX: improved auto-resize.
     * test autoResizeOff != intelliResizeOff
     * 
     */
    public void testAutoResizeOffNotHorizontalScrollEnabled() {
        JXTable table = new JXTable(10, 2);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        // sanity: horizontal action must be selected
        assertHorizontalActionSelected(table, false);
        assertFalse("autoResizeOff must not enable enhanced resize", 
                table.isHorizontalScrollEnabled());
     }
 
    /**
     * Issue #214-swingX: improved auto-resize.
     * 
     * testing doc'd behaviour: horizscrollenabled toggles between
     * enhanced resizeOff and the resizeOn mode which had been active 
     * when toggling on. 
     * 
     */
    public void testOldAutoResizeOn() {
        JXTable table = new JXTable(10, 2);
        int oldAutoResize = table.getAutoResizeMode();
        table.setHorizontalScrollEnabled(true);
        table.setHorizontalScrollEnabled(false);
        assertEquals("old on-mode must be restored", oldAutoResize, table.getAutoResizeMode());
       }
    
    /**
     * Issue #214-swingX: improved auto-resize.
     * 
     * testing doc'd behaviour: horizscrollenabled toggles between
     * enhanced resizeOff and the resizeOn mode which had been active 
     * when toggling on. Must not restore raw resizeOff mode.
     * 
     * 
     */
    public void testNotOldAutoResizeOff() {
        JXTable table = new JXTable(10, 2);
        int oldAutoResize = table.getAutoResizeMode();
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        table.setHorizontalScrollEnabled(true);
        table.setHorizontalScrollEnabled(false);
        assertEquals("old on-mode must be restored", oldAutoResize, table.getAutoResizeMode());
       }
    /**
     * Issue #214-swingX: improved auto-resize.
     * test autoResizeOff != intelliResizeOff 
     * after sequence a) set intelli, b) setAutoResize
     * 
     */
    public void testAutoResizeOffAfterHorizontalScrollEnabled() {
        JXTable table = new JXTable(10, 2);
        table.setHorizontalScrollEnabled(true);
        // sanity: intelliResizeOff enabled
        assertTrue(table.isHorizontalScrollEnabled());
        // sanity: horizontal action must be selected
        assertHorizontalActionSelected(table, true);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        assertFalse("autoResizeOff must not enable enhanced resize", 
                table.isHorizontalScrollEnabled());
        // sanity: horizontal action must be selected
        assertHorizontalActionSelected(table, false);
     }

    /**
     * Issue 252-swingx: getColumnExt throws ClassCastException if tableColumn
     * is not of type TableColumnExt.
     *
     */
    public void testTableColumnType() {
        JXTable table = new JXTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(new DefaultTableModel(2, 1));
        TableColumnModel columnModel = new DefaultTableColumnModel();
        columnModel.addColumn(new TableColumn(0));
        table.setColumnModel(columnModel);
        // valid column index must not throw exception
        TableColumnExt tableColumnExt = table.getColumnExt(0);
        assertNull("getColumnExt must return null on type mismatch", tableColumnExt);
    }


    /**
     * test contract: getColumnExt(int) throws ArrayIndexOutofBounds with 
     * invalid column index.
     *
     */
    public void testTableColumnExtOffRange() {
        JXTable table = new JXTable(2, 1);
        try {
            table.getColumnExt(1);
            fail("accessing invalid column index must throw ArrayIndexOutofBoundExc");
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing: contracted runtime exception
        } catch (Exception e) {
           fail("unexpected exception: " + e + "\n" +
              "accessing invalid column index must throw ArrayIndexOutofBoundExc");
        }
    }

    /**
     * test contract: getColumn(int) throws ArrayIndexOutofBounds with 
     * invalid column index.<p>
     *
     * Subtle autoboxing issue:  
     * JTable has convenience method getColumn(Object) to access by 
     * identifier, but doesn't have delegate method to columnModel.getColumn(int)
     * Clients assuming the existence of a direct delegate no longer get a
     * compile-time error message in 1.5 due to autoboxing. 
     * Furthermore, the runtime exception is unexpected (IllegalArgument
     * instead of AIOOB). <p>
     * 
     * Added getColumn(int) to JXTable api to solve.
     * 
     */
    public void testTableColumnOffRange() {
        JXTable table = new JXTable(2, 1);
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


    /**
     * Issue #251-swingx: JXTable doesn't respect TableColumn editability.
     * report, test and fix by nicfagn (Nicola Fagnani),
     *
     */
    public void testTableColumnEditable() {
        DefaultTableModel model = new DefaultTableModel( 2, 2 );
        JXTable table = new JXTable( model );

        // DefaultTableModel allows to edit its cells.
        for( int i = 0; i < model.getRowCount(); i++ ) {
            for( int j = 0; j < model.getRowCount(); j++ ) {
                assertEquals(
                    "cell (" + i + "," + j + ") must be editable", 
                    true, table.isCellEditable( i, j ) );
            }
        }        

        // First column not editable.
        int column = 0;
        table.getColumnExt( column ).setEditable( false );
        for( int i = 0; i < model.getRowCount(); i++ ) {
            for( int j = 0; j < model.getRowCount(); j++ ) {
                assertEquals(
                    "cell (" + i + "," + j + ") must " +
                    (j == column ? "not" : "") + " be editable", 
                    !(j == column), table.isCellEditable( i, j ) );
            }
        }        
        table.getColumnExt( column ).setEditable( true );
                
        // Second column not editable.
        column = 1;
        table.getColumnExt( column ).setEditable( false );
        for( int i = 0; i < model.getRowCount(); i++ ) {
            for( int j = 0; j < model.getRowCount(); j++ ) {
                assertEquals(
                    "cell (" + i + "," + j + ") must " +
                    (j == column ? "not" : "") + " be editable", 
                    !(j == column), table.isCellEditable( i, j ) );
            }
        }        
        table.getColumnExt( column ).setEditable( true );
        
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

    /**
     * Issue #??-swingx: competing setHighlighters(null) break code.
     * 
     * More specifically: it doesn't compile without casting the null, that's why
     * it has to be commented here.
     *
     */
//    public void testHighlightersNull() {
//        JXTable table = new JXTable();
//        table.setHighlighters(null);
//    }

    /**
     * Issue #??-swingx: setHighlighters(null) throws NPE. 
     * 
     */
    public void testSetHighlightersNull() {
        JXTable table = new JXTable();
        table.setHighlighters((Highlighter) null);
        assertNull(table.getHighlighters());
    }
    
    /**
     * Issue #??-swingx: setHighlighters() throws NPE. 
     * 
     */
    public void testSetHighlightersNoHighlighter() {
        JXTable table = new JXTable();
        table.setHighlighters();
        assertNull(table.getHighlighters());
    }

    /**
     * Issue #??-swingx: setHighlighters() throws NPE. 
     * 
     * Test that null highlighter resets the pipeline to null.
     */
    public void testSetHighlightersReset() {
        JXTable table = new JXTable();
        table.addHighlighter(new Highlighter());
        // sanity
        assertEquals(1, table.getHighlighters().getHighlighters().length);
        table.setHighlighters();
        assertNull(table.getHighlighters());
    }

    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
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
     * test if renderer properties are updated on LF change. <p>
     * Note: this can be done examplary only. Here: we use the 
     * font of a rendererComponent returned by a LinkRenderer for
     * comparison. There's nothing to test if the font are equal
     * in System and crossplattform LF. <p>
     * 
     * There are spurious problems when toggling UI (since when?) 
     * with LinkRenderer
     * "no ComponentUI class for: org.jdesktop.swingx.LinkRenderer$1"
     * that's the inner class JXHyperlink which overrides updateUI.
     * 
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
        assertNotNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.CLICKED_KEY));
        assertNotNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.ROLLOVER_KEY));
        assertNotNull("execute button action must be registered", table.getActionMap().get(RolloverController.EXECUTE_BUTTON_ACTIONCOMMAND));
        table.setRolloverEnabled(false);
        assertNull("LinkController must not be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.CLICKED_KEY ));
        assertNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.ROLLOVER_KEY));
        assertNull("execute button action must be de-registered", table.getActionMap().get(RolloverController.EXECUTE_BUTTON_ACTIONCOMMAND));
    }
    
    private PropertyChangeListener getLinkControllerAsPropertyChangeListener(JXTable table, String propertyName) {
        PropertyChangeListener[] listeners = table.getPropertyChangeListeners(propertyName);
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof JXTable.TableRolloverController) {
                return (JXTable.TableRolloverController) listeners[i];
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
        table.toggleSortOrder(0);
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
        table.getFilters().getSortController().setSortKeys
            (Collections.singletonList(
                new SortKey(SortOrder.DESCENDING, 0)));
//                new ShuttleSorter(0, false));
        assertEquals(table.getRowHeight(), table.getRowHeight(1));
        assertEquals(25, table.getRowHeight(table.getRowCount() - 1));
        table.setRowHeight(table.getRowHeight());
        assertEquals(table.getRowHeight(), table.getRowHeight(table.getRowCount() - 1));
    }
    
    public void testResetIndividualRowHeight() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeightEnabled(true);
        table.setRowHeight(0, 25);
        table.getFilters().getSortController().setSortKeys
            (Collections.singletonList(
                new SortKey(SortOrder.DESCENDING, 0)));
//                new ShuttleSorter(0, false))
//                new ShuttleSorter(0, false));
        assertEquals("individual row height must be moved to last row", 
                25, table.getRowHeight(table.getRowCount() - 1));
        // reset
        table.setRowHeight(table.getRowHeight());
        assertEquals("individual row height must be reset", 
                table.getRowHeight(), table.getRowHeight(table.getRowCount() - 1));
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
     * PatternFilter does not throw - checks with modelToView if the 
     * column is visible and returns false match if not. Hmm...
     * 
     * 
     */
    public void testFilterInChainOnModelChange() {
        JXTable table = new JXTable(createAscendingModel(0, 10, 5, true));
        int columnCount = table.getColumnCount();
        assertEquals(5, columnCount);
        Filter filter = new PatternFilter(".*", 0, columnCount - 1);
        FilterPipeline pipeline = new FilterPipeline(new Filter[] {filter});
        table.setFilters(pipeline);
        assertEquals(10, pipeline.getOutputSize());
        table.setModel(new DefaultTableModel(10, columnCount - 1));
    }
    
    /**
     * Issue #119: Exception if sorter on last column and setting
     * model with fewer columns.
     * 
     * 
     * JW: related to #53-swingx - sorter not removed on column removed. 
     * 
     * Similar if sorter in filter pipeline -- absolutely need mutable
     * pipeline!!
     * Filed the latter part as Issue #55-swingx 
     *
     */
    public void testSorterInChainOnModelChange() {
        JXTable table = new JXTable(new DefaultTableModel(10, 5));
        int columnCount = table.getColumnCount();
        Sorter sorter = new ShuttleSorter(columnCount - 1, false);
        FilterPipeline pipeline = new FilterPipeline(new Filter[] {sorter});
        table.setFilters(pipeline);
        table.setModel(new DefaultTableModel(10, columnCount - 1));
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
        table.toggleSortOrder(columnCount - 1);
        table.setModel(new DefaultTableModel(10, columnCount - 1));
        assertTrue(table.getFilters().getSortController().getSortKeys().isEmpty());
    }
    
    /**
     * add api to access the sorted column.
     *
     */
    public void testSortedColumn() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
        TableColumn sortedColumn = table.getSortedColumn();
        assertEquals(columnX, sortedColumn);
        
    }
    /**
     * Issue #53-swingx: interactive sorter not removed if column removed.
     *
     */
    public void testSorterAfterColumnRemoved() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
        table.removeColumn(columnX);
        assertTrue("sorter must be removed when column removed", 
                table.getFilters().getSortController().getSortKeys().isEmpty());
        
    }
    
    /**
     * interactive sorter must be active if column is hidden.
     * THINK: no longer valid... check sortkeys instead?
     */
    public void testSorterAfterColumnHidden() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
        List<? extends SortKey> sortKeys = table.getFilters().getSortController().getSortKeys();
        columnX.setVisible(false);
        assertEquals("interactive sorter must be same as sorter in column", 
                sortKeys, table.getFilters().getSortController().getSortKeys());
//        assertEquals("interactive sorter must be same as sorter in column", 
//                columnX.getSorter(), table.getFilters().getSorter());
        
    }
    
    /**
     * Issue #54-swingx: hidden columns not removed.
     *
     */
    public void testRemoveAllColumns() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setVisible(false);
        // set empty model
        table.setModel(new DefaultTableModel(0, 0));
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
        assertTrue("sorter must be removed when column removed",
                table.getFilters().getSortController().getSortKeys().isEmpty());
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
        table.toggleSortOrder(1);
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
        table.toggleSortOrder(1);
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
        table.toggleSortOrder(0);
        // sort descending
        table.toggleSortOrder(0);
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
     *  adjusted to new JXTable sorter api (after the source tag jw_before_rowsorter)
     *  
     */
    public void testSelectionOnSorterRefresh() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.toggleSortOrder(0);
        SortOrder sortOrder = table.getSortOrder(0);
        // sanity assert
        assertTrue(sortOrder.isAscending());
        // select the first row
        table.setRowSelectionInterval(0, 0);
        // reverse sortorder
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.toggleSortOrder(0);
        
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
        table.toggleSortOrder(0);
        // invert sort
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
        // select last rows
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        // invert sort
        table.toggleSortOrder(0);
        model.removeRow(0);
    }

    public void testDeleteRowAboveIndividualRowHeight() {
        DefaultTableModel model = createAscendingModel(0, 10);
        JXTable table = new JXTable(model);
        table.setRowHeightEnabled(true);
        int selectedRow = table.getRowCount() - 1;
        table.setRowHeight(selectedRow, 25);
        table.toggleSortOrder(0);
        assertEquals("last row is individual", 25, table.getRowHeight(selectedRow));
        model.removeRow(0);
        assertEquals("last row is individual", 25, table.getRowHeight(selectedRow - 1));
        
    }

    /**
     * Issue #223 - part d)
     * 
     * test if selection is cleared after receiving a dataChanged.
     * Need to specify behaviour: lead/anchor of selectionModel are 
     * not changed in clearSelection(). So modelSelection has old 
     * lead which is mapped as a selection in the view (may be out-of 
     * range). Hmmm...
     * 
     */
    public void testSelectionAfterDataChanged() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20, 5, false);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // sanity
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.fireTableDataChanged();
        assertEquals("selection must be cleared", -1, table.getSelectedRow());
    }

    /**
     * Issue #223 - part d)
     * 
     * test if selection is cleared after receiving a dataChanged.
     * 
     */
    public void testCoreTableSelectionAfterDataChanged() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20, 5, false);
        JTable table = new JTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // sanity
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.fireTableDataChanged();
        assertEquals("selection must be cleared", -1, table.getSelectedRow());
        
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
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
        // revert order 
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
        // revert order 
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
        // revert order 
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
        // revert order - fails... track down
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
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
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Integer.class : super.getColumnClass(column);
            }
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }

    
    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first/last column depending on fillLast
     * starting from startRow.
     * with columnCount columns
     * @param startRow the value of the first row
     * @param rowCount the number of rows
     * @param columnCount the number of columns
     * @param fillLast boolean to indicate whether to ill the value in the first
     *   or last column
     * @return a configured DefaultTableModel.
     */
    protected DefaultTableModel createAscendingModel(int startRow, final int rowCount, 
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
    
    

    /**
     * check if setting to false really disables sortability.
     *
     */
    public void testSortable() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        boolean sortable = table.isSortable();
        // sanity assert: sortable defaults to true
        assertTrue("JXTable sortable defaults to true", sortable);
        table.toggleSortOrder(0);
        Object first = table.getValueAt(0, 0);
        table.setSortable(false);
        assertFalse(table.isSortable());
        // reverse the sorting order on first column
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(0);
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
        List<RowObject> rowObjects = new ArrayList<RowObject>();
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
        table.toggleSortOrder(1);
//        Filter filterB = new PatternFilter(".*", Pattern.CASE_INSENSITIVE, 1);
        table.setFilters(createFilterPipeline(true, 1)); //new FilterPipeline(new Filter[] {filterB}));
        table.toggleSortOrder(1);
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
        table.toggleSortOrder(0);
        table.toggleSortOrder(1);
        table.setFilters(createFilterPipeline(true, 1));
        table.setFilters(createFilterPipeline(false, 1));
        table.toggleSortOrder(0);
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
        table.toggleSortOrder(1);
        table.setFilters(null);
        assertEquals("rowCount must be original", rows, table.getRowCount());
        table.getValueAt(rows - 1, 0);

    
    }   

    /**
     * Symmetrical test for editors.
     *
     */
    public void testLazyEditorsByClass() {
        JXTable table = new JXTable();
        assertEquals("default Boolean editor", JXTable.BooleanEditor.class, table.getDefaultEditor(Boolean.class).getClass());
        assertEquals("default Number editor", NumberEditorExt.class, table.getDefaultEditor(Number.class).getClass());
        assertEquals("default Double editor", NumberEditorExt.class, table.getDefaultEditor(Double.class).getClass());
    }

    /**
     * Issue #134: JXTable - default renderers not loaded. To fix the issue the
     * JXTable internal renderers' access scope was changed to public. Note: if
     * the _JTable_ internal renderers access scope were to be widened then this
     * test has to be changed (the comparing class are hardcoded). <p>
     * 
     * This test is obsolete for swingx renderer: the renderer type is
     * always a DefaultTableRenderer, the difference is its configuration.
     * 
     */
    public void testLazyRenderersByClass() {
//        JXTable table = new JXTable();
        // testing against extended renderers
//        assertEquals("default Boolean renderer", BooleanRendererExt.class,
//                table.getDefaultRenderer(Boolean.class).getClass());
//        assertEquals("default Number renderer", NumberRendererExt.class, table
//                .getDefaultRenderer(Number.class).getClass());
//        assertEquals("default Double renderer", DoubleRendererExt.class, table
//                .getDefaultRenderer(Double.class).getClass());
//        assertEquals("default Date renderer", DateRendererExt.class, table
//                .getDefaultRenderer(Date.class).getClass());
//        assertEquals("default Icon renderer", IconRendererExt.class, table
//                .getDefaultRenderer(Icon.class).getClass());
        // testing against standard renderers
//        assertEquals("default Boolean renderer", JXTable.BooleanRenderer.class,
//                table.getDefaultRenderer(Boolean.class).getClass());
//        assertEquals("default Number renderer", JXTable.NumberRenderer.class,
//                table.getDefaultRenderer(Number.class).getClass());
//        assertEquals("default Double renderer", JXTable.DoubleRenderer.class,
//                table.getDefaultRenderer(Double.class).getClass());
//        assertEquals("default Date renderer", JXTable.DateRenderer.class, table
//                .getDefaultRenderer(Date.class).getClass());
//        assertEquals("default Icon renderer", JXTable.IconRenderer.class, table
//                .getDefaultRenderer(Icon.class).getClass());
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

        public Class<?> getColumnClass(int column) {
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
