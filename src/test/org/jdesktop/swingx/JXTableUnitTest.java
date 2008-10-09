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
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable.GenericEditor;
import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.decorator.SortKey;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.decorator.Sorter;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.rollover.RolloverController;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.rollover.TableRolloverController;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.NumberEditorExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.CellEditorReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

/**
* Tests of <code>JXTable</code>.
* 
* 
* @author Jeanette Winzenburg
*/
@RunWith(JUnit4ClassRunner.class)
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

    @Before
    public void setUpJu4() throws Exception {
        // just a little conflict between ant and maven builds
        // junit4 @before methods needs to be public, while
        // junit3 setUp() inherited from super is protected
      this.setUp();
    }
    
    @Override
    protected void setUp() throws Exception {
       super.setUp();
        // set loader priority to normal
        if (tableModel == null) {
            tableModel = new DynamicTableModel();
        }
        sortableTableModel = new AncientSwingTeam();
        // make sure we have the same default for each test
        defaultToSystemLF = true;
        setSystemLF(defaultToSystemLF);
        uiTableRowHeight = UIManager.get("JXTable.rowHeight");
    }

    
    @Override
    @After
    public void tearDown() throws Exception {
        UIManager.put("JXTable.rowHeight", uiTableRowHeight);
        super.tearDown();
    }

    
    /**
     * Issue #924-swingx: problems indy rowheight and filters.
     * 
     * ArrayIndexOutOfBounds on insert. 
     *
     */
    @Test
    public void testIndividualRowHeightAndFilterInsert() {
        JXTable table = new JXTable(createAscendingModel(0, 50));
        table.setRowHeightEnabled(true);
        table.setRowHeight(1, 100);
        final FilterPipeline filterPipeline = new FilterPipeline(new PatternFilter("[123]",0,0));
        table.setFilters(filterPipeline);
        // sanity
        assertEquals(1, table.getValueAt(0, 0));
        ((DefaultTableModel) table.getModel()).addRow(new Object[] {1, null, null, null});
    }

    /**
     * Issue #924-swingx: problems indy rowheight and filters.
     * 
     * ArrayIndexOutOfBounds on remove. 
     *
     */
    @Test
    public void testIndividualRowHeightAndFilterRemove() {
        JXTable table = new JXTable(createAscendingModel(0, 50));
        table.setRowHeightEnabled(true);
        table.setRowHeight(1, 100);
        final FilterPipeline filterPipeline = new FilterPipeline(new PatternFilter("[123]",0,0));
        table.setFilters(filterPipeline);
        // sanity
        assertEquals(1, table.getValueAt(0, 0));
        ((DefaultTableModel) table.getModel()).removeRow(table.getModel().getRowCount() - 1);
    }


    /**
     * Issue #550-swingx: xtable must not reset columns' pref/size on 
     * structureChanged if autocreate is false.
     * 
     *  here: width (was no problem, default columnFactory only sets pref)
     */
    @Test
    public void testInitializeColumnWidth() {
        JXTable table = new JXTable(10, 2);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        table.setAutoCreateColumnsFromModel(false);
        int width = table.getColumn(0).getWidth() + 2;
        table.getColumn(0).setWidth(width);
        assertEquals("sanity: ", width, table.getColumn(0).getWidth());
        table.tableChanged(null);
        assertEquals("structure changed must not resize column", 
                width, table.getColumn(0).getWidth() );
    }
    
    /**
     * Issue #550-swingx: xtable must not reset columns' pref/width on 
     * structureChanged if autocreate is false.
     * 
     * here: prefWidth 
     */
    @Test
    public void testInitializeColumnPrefWidth() {
        JXTable table = new JXTable(10, 2);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        table.setAutoCreateColumnsFromModel(false);
        int width = table.getColumn(0).getPreferredWidth() + 2;
        table.getColumn(0).setPreferredWidth(width);
        assertEquals("sanity: ", width, table.getColumn(0).getPreferredWidth());
        table.tableChanged(null);
        assertEquals("structure changed must not resize column", width, 
                table.getColumn(0).getPreferredWidth() );
    }
    
    
    /**
     * Issue #847-swingx: JXTable respect custom corner if columnControl not visible
     * 
     * Test correct un-/config on toggling the controlVisible property
     */
    @Test
    public void testColumnControlVisible() {
        JXTable table = new JXTable(10, 2);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setColumnControlVisible(true);
        assertSame("sanity: column control set", table.getColumnControl(), scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
        table.setColumnControlVisible(false);
        assertEquals("columnControl must be removed from corner if not visible", 
                null, scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
    }
    
    /**
     * Issue #847-swingx: JXTable respect custom corner if columnControl not visible
     * 
     * @throws Exception
     */
    @Test
    public void testCornerRespectCustom() throws Exception {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run testCornerNPE - headless environment");
            return;
        }
        
        final JXTable table = new JXTable(10, 2);
        final JScrollPane scrollPane = new JScrollPane(table);
        final JFrame frame = new JFrame();
        frame.add(scrollPane);
        frame.pack();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                JPanel panel = new JPanel();
                scrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER, panel);
                assertEquals("sanity ...", panel, scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
                frame.remove(scrollPane);
                frame.add(scrollPane);
                if (table.isColumnControlVisible()) {
                    assertEquals(table.getColumnControl(), scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
                } else {
                    assertEquals("xTable respects custom corner if columnControl invisible", 
                            panel,
                        scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
                }
            }
        });
    }

    /**
     * Issue #844-swingx: JXTable throws NPE with custom corner.
     * 
     * @throws Exception
     */
    @Test
    public void testCornerNPE() throws Exception {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run testCornerNPE - headless environment");
            return;
        }
        
        JXTable table = new JXTable(10, 2);
        final JScrollPane scrollPane = new JScrollPane(table);
        final JFrame frame = new JFrame();
        frame.add(scrollPane);
        frame.pack();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                scrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER, new JPanel());
                assertNotNull("sanity ...", scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
                frame.remove(scrollPane);
                frame.add(scrollPane);
            }
        });
    }
    /**
     * Issue #844-swingx: JXTable throws NPE with custom corner.
     * Regression testing (Issue #155-swingx) - scrollpane policy must be respected.
     * @throws Exception
     */
    @Test
    public void testCornerNPEVerticalSPPolicy() throws Exception {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run testCornerNPE - headless environment");
            return;
        }
        
        final JXTable table = new JXTable(10, 2);
        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        table.setColumnControlVisible(true);
        final JFrame frame = new JFrame();
        frame.add(scrollPane);
        frame.pack();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                frame.remove(scrollPane);
                frame.add(scrollPane);
                assertSame(table.getColumnControl(), scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
                table.setColumnControlVisible(false);
                assertEquals(JScrollPane.VERTICAL_SCROLLBAR_NEVER, scrollPane.getVerticalScrollBarPolicy());
            }
        });
    }

    /**
     * Issue #844-swingx: JXTable throws NPE with custom corner.
     * Regression testing (Issue #155-swingx) - scrollpane policy must be respected.
     */
    @Test
    public void testCornerNPEVerticalSPOnUpdateUI(){
        final JXTable table = new JXTable(10, 2);
        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        table.setColumnControlVisible(true);
        table.updateUI();
        table.setColumnControlVisible(false);
        assertEquals(JScrollPane.VERTICAL_SCROLLBAR_NEVER, scrollPane.getVerticalScrollBarPolicy());
    }

    /**
     * Issue #838-swingx: table.prepareRenderer adds bogey listener to column highlighter.
     * 
     */
    @Test
    public void testColumnHighlighterListener() {
        JXTable table = new JXTable(10, 2);
        ColorHighlighter highlighter = new ColorHighlighter();
        table.getColumnExt(0).addHighlighter(highlighter);
        int listenerCount = highlighter.getChangeListeners().length;
        assertEquals(1, listenerCount);
        table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals(listenerCount, highlighter.getChangeListeners().length);
    }
    
    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on JXTable.
     */
    @Test
    public void testGetString() {
        JXTable table = new JXTable(new AncientSwingTeam());
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
        String text = table.getStringAt(0, 2);
        assertEquals(sv.getString(table.getValueAt(0, 2)), text);
    }
    
    
    @Test
    public void testCancelEditEnabled() {
        JXTable table = new JXTable(10, 3);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(table.isEditing(), table.getActionMap().get("cancel").isEnabled());
    }

    @Test
    public void testCancelEditDisabled() {
        JXTable table = new JXTable(10, 3);
        // sanity
        assertFalse(table.isEditing());
        assertEquals(table.isEditing(), table.getActionMap().get("cancel").isEnabled());
    }

    /**
     * NPE if Generic editor barks about constructor. Hacked around ...
     * 
     *  PENDING JW: too verbose ... strip down to essentials
     */
    @Test
    public void testGenericEditorNPE() {
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        Timestamp stamp = new Timestamp(date.getTime());
        Time time = new Time(date.getTime());
        DefaultTableModel model = new DefaultTableModel(1, 5) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() > 0) {
                    Object value = getValueAt(0, columnIndex);
                    if (value != null) {
                        return value.getClass();
                    }
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        model.setColumnIdentifiers(new Object[]{"Date - normal", "SQL Date", "SQL Timestamp", "SQL Time", "Date - as time"});
        model.setValueAt(date, 0, 0);
        model.setValueAt(sqlDate, 0, 1);
        model.setValueAt(stamp, 0, 2);
        model.setValueAt(time, 0, 3);
        model.setValueAt(date, 0, 4);
        JXTable table = new JXTable(model);
        table.editCellAt(0, 1);
    }

    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: do nothing if !isTerminateEditOnFocusLost.
     *
     */
    @Test
    public void testFocusTransferBackwardTerminateEditFalse() {
        JXTable table = new JXTable(10, 2);
        table.setTerminateEditOnFocusLost(false);
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField());        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, editor);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocusBackward();
        assertTrue("table must be editing", table.isEditing());
        assertEquals("", 0, report.getEventCount());
    }
   
    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: do nothing if !isTerminateEditOnFocusLost.
     *
     */
    @Test
    public void testFocusTransferForwardTerminateEditFalse() {
        JXTable table = new JXTable(10, 2);
        table.setTerminateEditOnFocusLost(false);
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField());        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, editor);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocus();
        assertTrue("table must be editing", table.isEditing());
        assertEquals("", 0, report.getEventCount());
    }

    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: respect false on backward.
     *
     */
    @Test
    public void testFocusTransferBackwardStopEditingFalse() {
        JXTable table = new JXTable(10, 2);
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField()){

            @Override
            public boolean stopCellEditing() {
                return false;
            }
            
        };
        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, editor);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocusBackward();
        assertTrue("table must be editing", table.isEditing());
        assertEquals("", 0, report.getEventCount());
    }
    
    
    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: respect editor false on forward.
     *
     */
    @Test
    public void testFocusTransferForwardStopEditingFalse() {
        JXTable table = new JXTable(10, 2);
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField()){

            @Override
            public boolean stopCellEditing() {
                return false;
            }
            
        };
        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, editor);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocus();
        assertTrue("table must be editing", table.isEditing());
        assertEquals("", 0, report.getEventCount());
    }
    

    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: edit stopped and editor fires on backward.
     *
     */
    @Test
    public void testFocusTransferBackwardStopEditing() {
        JXTable table = new JXTable(10, 2);
        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocusBackward();
        assertFalse("table must not be editing", table.isEditing());
        assertEquals("", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }
    

    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: edit stopped and editor fired. 
     *
     */
    @Test
    public void testFocusTransferForwardStopEditing() {
        JXTable table = new JXTable(10, 2);
        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocus();
        assertFalse("table must not be editing", table.isEditing());
        assertEquals("", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }
    
    

    /**
     * test that we have actions registered for forwared/backward
     * focus transfer.
     *
     */
    @Test
    public void testFocusTransferActions() {
        JXTable table = new JXTable();
        assertNotNull("must have forward action",
                table.getActionMap().get(JXTable.FOCUS_NEXT_COMPONENT));
        assertNotNull("must have backward action",
                table.getActionMap().get(JXTable.FOCUS_PREVIOUS_COMPONENT));
    }

    /**
     * test that we have bindings for forward/backward 
     * focusTransfer.
     *
     */
    @Test
    public void testFocusTransferKeyBinding() {
        JTable core = new JTable();
        Set forwardKeys = core.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set backwardKeys = core.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        JXTable table = new JXTable();
        for (Object key : forwardKeys) {
            InputMap map = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            assertNotNull("must have binding for forward focus transfer " + key, 
                    map.get((KeyStroke) key));
        }
        for (Object key : backwardKeys) {
            InputMap map = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            assertNotNull("must have binding for backward focus transfer " + key, 
                    map.get((KeyStroke) key));
        }
    }
    
    /**
     * test that we have no focusTransfer keys set.
     *
     */
    @Test
    public void testFocusTransferNoDefaultKeys() {
        JXTable table = new JXTable();
        assertTrue(table.getFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS).isEmpty());
        assertTrue(table.getFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS).isEmpty());
    }
    /**
     * test that pref scrollable width is updated after structure changed.
     *
     */
    @Test
    public void testPrefScrollableUpdatedOnStructureChanged() {
        JXTable compare = new JXTable(new AncientSwingTeam());
        Dimension compareDim = compare.getPreferredScrollableViewportSize();
        JXTable table = new JXTable(10, 6);
        Dimension initialDim = table.getPreferredScrollableViewportSize();
        assertFalse("configured must be different from default width", 
                compareDim.width == initialDim.width);
        table.setModel(compare.getModel());
        assertEquals(compareDim.width, table.getPreferredScrollableViewportSize().width);
    }
    /**
     * Issue #508-swingx: cleanup scrollable support.
     * 
     */
    @Test
    public void testVisibleRowCountUpdateSize() {
        JXTable table = new JXTable(10, 6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        table.setVisibleRowCount(table.getVisibleRowCount() * 2);
        // change the pref width of a column, the pref scrollable width must not
        // be changed. This is testing the table internal reset code.
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setPreferredWidth(columnExt.getPreferredWidth() * 2);
        assertEquals(dim.height * 2, table.getPreferredScrollableViewportSize().height);
        assertEquals(dim.width, table.getPreferredScrollableViewportSize().width);
    }
    
    /**
     * Issue #508-swingx: cleanup scrollable support
     *
     */
    @Test
    public void testVisibleColumnCountUpdateSize() {
        JXTable table = new JXTable(10, 14);
        table.setVisibleColumnCount(6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        table.setVisibleColumnCount(table.getVisibleColumnCount() * 2);
        assertEquals(dim.width * 2, table.getPreferredScrollableViewportSize().width);
        assertEquals(dim.height, table.getPreferredScrollableViewportSize().height);
    }
    
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * test preference of explicit setting (over calculated).
     *
     */
    @Test
    public void testPrefScrollableSetPreference() {
        JXTable table = new JXTable(10, 6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        Dimension other = new Dimension(dim.width + 20, dim.height + 20);
        table.setPreferredScrollableViewportSize(other);
        assertEquals(other, table.getPreferredScrollableViewportSize());
    }
    
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * test that max number of columns used for the preferred 
     * scrollable width i getVisibleColumnCount 
     *
     */
    @Test
    public void testPrefScrollableWidthMoreColumns() {
        JXTable table = new JXTable(10, 7);
        table.setVisibleColumnCount(6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        // sanity
        assertEquals(table.getVisibleColumnCount() + 1, table.getColumnCount());
        int width = 0;
        for (int i = 0; i < table.getVisibleColumnCount(); i++) {
            width += table.getColumn(i).getPreferredWidth();
        }
        assertEquals(width, dim.width);
    }
    
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * test that max number of columns used for the preferred 
     * scrollable width i getVisibleColumnCount 
     *
     */
    @Test
    public void testPrefScrollableWidthLessColumns() {
        JXTable table = new JXTable(10, 5);
        table.setVisibleColumnCount(6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        // sanity
        assertEquals(table.getVisibleColumnCount() - 1, table.getColumnCount());
        int width = 0;
        for (int i = 0; i < table.getColumnCount(); i++) {
            width += table.getColumn(i).getPreferredWidth();
        }
        width += 75;
        assertEquals(width, dim.width);
    }
    
    /**
     * test change back to default sizing (use-all)
     * 
     */
    @Test
    public void testVisibleColumnCountNegative() {
        JXTable table = new JXTable(10, 7);
        Dimension dim = table.getPreferredScrollableViewportSize();
        int visibleCount = table.getVisibleColumnCount();
        // custom
        table.setVisibleColumnCount(4);
        // change back to default
        table.setVisibleColumnCount(visibleCount);
        assertEquals(dim, table.getPreferredScrollableViewportSize());
    }
    
    /**
     * test default sizing: use all visible columns.
     *
     */
    @Test
    public void testPrefScrollableWidthDefault() {
       JXTable table = new JXTable(10, 7);
       Dimension dim = table.getPreferredScrollableViewportSize();
       assertEquals("default must use all visible columns", 
               table.getColumnCount() * 75, dim.width);
    }
    
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * Sanity: test initial visible column count.
     *
     */
    @Test
    public void testDefaultVisibleColumnCount() {
        JXTable table = new JXTable(10, 6);
        assertEquals(-1, table.getVisibleColumnCount());
    }
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * test custom setting of visible column count.
     * 
     */
    @Test
    public void testVisibleColumnCount() {
        JXTable table = new JXTable(30, 10);
        int visibleColumns = 7;
        table.setVisibleColumnCount(visibleColumns);
        assertEquals(visibleColumns, table.getVisibleColumnCount());
        Dimension dim = table.getPreferredScrollableViewportSize();
        assertEquals(visibleColumns * 75, dim.width);
    }
    
    
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * test that column widths are configured after setModel.
     *
     */    
    @Test
    public void testPrefColumnSetModel() {
        JXTable compare = new JXTable(new AncientSwingTeam());
        // make sure the init is called
        compare.getPreferredScrollableViewportSize();
        // table with arbitrary model
        JXTable table = new JXTable(30, 7);
        // make sure the init is called
        table.getPreferredScrollableViewportSize();
        // following should init the column width ...
        table.setModel(compare.getModel());
        for (int i = 0; i < table.getColumnCount(); i++) {
            assertEquals("prefwidths must be same at index " + i, 
                    compare.getColumnExt(i).getPreferredWidth(),
                    table.getColumnExt(i).getPreferredWidth());
        }
    }

    /**
     * Test bound property visibleRowCount.
     *
     */
    @Test
    public void testVisibleRowCountProperty() {
        JXTable table = new JXTable(10, 7);
        int count = table.getVisibleRowCount();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setVisibleRowCount(count + 1);
        TestUtils.assertPropertyChangeEvent(report, "visibleRowCount", count, count+1);
    }
    /**
     * Test bound property visibleColumnCount.
     *
     */
    @Test
    public void testVisibleColumnCountProperty() {
        JXTable table = new JXTable(10, 7);
        int count = table.getVisibleColumnCount();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setVisibleColumnCount(count + 1);
        TestUtils.assertPropertyChangeEvent(report, "visibleColumnCount", count, count+1);
    }
    
    /**
     * test doc'ed behaviour: set visible row count must
     * throw on negative row.
     *
     */
    @Test
    public void testVisibleRowCountNegative() {
        JXTable table = new JXTable(10, 7);
        try {
            table.setVisibleRowCount(-2);
            fail("negative count must throw IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected exception
        }        
    }
    

    /**
     * Issue #547-swingx: return copy of pref scrollable size
     * 
     */
    @Test
    public void testPrefScrollableSafeCalculatedDim() {
        JXTable table = new JXTable(10, 6);
        // sanity: compare the normal dim returns
        assertNotSame("pref size must not be the same", 
                table.getPreferredSize(), table.getPreferredSize());
        assertNotSame("pref scrollable dim must not be the same", 
                table.getPreferredScrollableViewportSize(), 
                table.getPreferredScrollableViewportSize());
    }

    /**
     * Issue #547-swingx: return copy of pref scrollable size
     * This is a super prob - does use the dim as set.
     */
    @Test
    public void testPrefScrollableSafeFixedDim() {
        JXTable table = new JXTable(10, 6);
        Dimension dim = new Dimension(200, 400);
        // sanity: compare to super prf size when set
        table.setPreferredSize(dim);
        assertEquals(dim, table.getPreferredSize());
        assertNotSame(dim, table.getPreferredSize());
        table.setPreferredScrollableViewportSize(dim);
        assertNotSame("pref scrollable dim must not be the same", 
                dim, table.getPreferredScrollableViewportSize());
    }
    
    /**
     * Issue #547-swingx: pref scrollable height included header.
     *
     */
    @Test
    public void testPrefScrollableHeight() {
        JXTable table = new JXTable(10, 6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        assertNotNull("pref scrollable must not be null", dim);
        assertEquals("scrollable height must no include header", 
                table.getVisibleRowCount() * table.getRowHeight(), dim.height);
    }     
    
    /**
     * Sanity: default visible row count == 20.
     *
     */
    @Test
    public void testDefaultVisibleRowCount() {
        JXTable table = new JXTable(10, 6);
        assertEquals(20, table.getVisibleRowCount());
    }
    /**
     * Issue #547-swingx: NPE in ColumnFactory configureColumnWidth 
     *    for hidden column
     *
     */
    @Test
    public void testPrefHiddenColumnNPE() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setPrototypeValue("Jessesmariaandjosefsapperlottodundteufel");
        columnExt.setVisible(false);
        // NPE
        table.getColumnFactory().configureColumnWidths(table, columnExt);
    }
    
    /**
     * Issue #547-swingx: NPE in ColumnFactory configureColumnWidth 
     *    for empty table .. no: doesn't because 
     *    table.getCellRenderer(row,column) does not use the row
     *    coordinate - so the illegal argument doesn't hurt.
     *
     */
    @Test
    public void testPrefEmptyTableNPE() {
        JXTable table = new JXTable(0, 4);
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setPrototypeValue("Jessesmariaandjosefsapperlottodundteufel");
        // NPE
        table.getColumnFactory().configureColumnWidths(table, columnExt);
    }
    
    /**
     * Issue #547-swingx: hidden columns' pref width not initialized.
     *
     * PENDING: the default initialize is working as expected only
     *  if the config is done before setting the model, that is 
     *  in the ColumnFactory. Need public api to programatically
     *  trigger the init after the fact? 
     */
    @Test
    public void testPrefHiddenColumn() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnExt = table.getColumnExt(0);
      columnExt.setPrototypeValue("Jessesmariaandjosefsapperlottodundteufel");
        TableCellRenderer renderer = table.getCellRenderer(0, 0);
        Component comp = renderer.getTableCellRendererComponent(null, columnExt.getPrototypeValue(), false, false, -1, -1);
        columnExt.setVisible(false);
        // make sure the column pref is initialized
        table.initializeColumnWidths();
        assertEquals("hidden column's pref must be set", 
                comp.getPreferredSize().width + table.getColumnMargin(), columnExt.getPreferredWidth());
    }

    /**
     * Issue #547-swingx: columns' pref width - header not taken 
     * if no prototype
     * 
     */
    @Test
    public void testPrefColumnTitle() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setHeaderValue("Jessesmariaandjosefsapperlottodundteufel");
        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        Component comp = renderer.getTableCellRendererComponent(table, columnExt.getHeaderValue(), false, false, -1, -1);
        // need to store the pref - header renderer is used during initialize!
        Dimension prefSize = comp.getPreferredSize();
        // make sure the column pref is initialized
        table.initializeColumnWidths();
        assertEquals("header must be measured", 
                prefSize.width + table.getColumnMargin(), columnExt.getPreferredWidth());
    }

    /**
     * Issue #547-swingx: columns' pref width - without
     * prototype the pref is minimally the (magic) standard if
     * the header doesn't exceed it.
     *
     */
    @Test
    public void testPrefStandardMinWithoutPrototype() {
        JXTable table = new JXTable(10, 6);
        TableColumnExt columnExt = table.getColumnExt(0);
        int standardWidth = columnExt.getPreferredWidth();
        // make sure the column pref is initialized
        table.getPreferredScrollableViewportSize();
        assertEquals("column pref width must be unchanged", 
                standardWidth, columnExt.getPreferredWidth());
    }
    
    /**
     * Issue #547-swingx: columns' pref width - added margin twice
     * if has prototype.
     * 
     * PENDING: the default initialize is working as expected only
     *  if the config is done before setting the model, that is 
     *  in the ColumnFactory. Need public api to programatically
     *  trigger the init after the fact? 
     */
    @Test
    public void testPrefColumnsDuplicateMargin() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnExt = table.getColumnExt(0);
        // force the prototype longer than the title
        // to avoid that header measuring is triggered
        // header renderer can have bigger fonts
        columnExt.setPrototypeValue(columnExt.getTitle() + "longer");
        TableCellRenderer renderer = table.getCellRenderer(0, 0);
        Component comp = renderer.getTableCellRendererComponent(null, columnExt.getPrototypeValue(), false, false, -1, -1);
        // make sure the column pref is initialized
        table.initializeColumnWidths();
        assertEquals("column margin must be added once", table.getColumnMargin(), 
                columnExt.getPreferredWidth() - comp.getPreferredSize().width);
    }

    /**
     * Issue #530-swingx: problems indy rowheight and filters
     *
     */
    @Test
    public void testIndividualRowHeightAndFilter() {
        JXTable table = new JXTable(createAscendingModel(0, 50));
        table.setRowHeightEnabled(true);
        table.setRowHeight(1, 100);
        final FilterPipeline filterPipeline = new FilterPipeline(new PatternFilter("[123]",0,0));
        table.setFilters(filterPipeline);
        // sanity
        assertEquals(1, table.getValueAt(0, 0));
        assertEquals(100, table.getRowHeight(0));
    }
    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    @Test
    public void testSetSelectionBackground() {
        JXTable table = new JXTable();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Color oldBackground = table.getSelectionBackground();
        Color color = Color.RED;
        table.setSelectionBackground(color);
        assertFalse(oldBackground.equals(table.getSelectionBackground()));
        assertEquals(color, table.getSelectionBackground());
        TestUtils.assertPropertyChangeEvent(report, "selectionBackground", oldBackground, color);
    }
    
    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    @Test
    public void testNullSelectionBackground() {
        JXTable table = new JXTable();
        table.setSelectionBackground(null);
    }

    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    @Test
    public void testSetSelectionForeground() {
        JXTable table = new JXTable();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Color oldForeground = table.getSelectionForeground();
        Color color = Color.RED;
        table.setSelectionForeground(color);
        assertFalse(oldForeground.equals(table.getSelectionForeground()));
        assertEquals(color, table.getSelectionForeground());
        TestUtils.assertPropertyChangeEvent(report, "selectionForeground", oldForeground, color);
    }
    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    @Test
    public void testNullSelectionForeground() {
        JXTable table = new JXTable();
        table.setSelectionForeground(null);
    }

    /**
     * Test default behaviour: hack around DefaultTableCellRenderer 
     * color memory is on. 
     *
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                table.getValueAt(0, 0), adapter.getValue(0));
    }

    /**
     * Issue 373-swingx: table must unsort column on sortable change.
     *
     * Here we test if switching sortable to false on the sorted column
     * resets the sorting, special case hidden column. This fails 
     * because columnModel doesn't fire property change events for
     * hidden columns (see Issue #369-swingx).
     * 
     */
    @Test
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
     * Issue 373-swingx: table must unsort column on sortable change.
     *
     * Here we test if switching sortable to false on the sorted column
     * resets the sorting.
     * 
     */
    @Test
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
    @Test
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
     * Here we test if the table actually canceled the edit.
     */
    @Test
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
     * Issue 372-swingx: table must cancel edit if column property 
     *   changes to not editable.
     * Here we test if the table is not editing after editable property
     * of the currently edited column is changed to false.
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testInitialTerminateEditOnFocusLost() {
       JXTable table = new JXTable();
       assertTrue("terminate edit must be on by default", table.isTerminateEditOnFocusLost());
    }

    /**
     * Issue #262-swingx: expose terminateEditOnFocusLost as property.
     * 
     * setter is same as setting client property and results in event firing.
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testSelectionMapperUpdatedOnSelectionModelChange() {
        JXTable table = new JXTable();
        ListSelectionModel model = new DefaultListSelectionModel();
        table.setSelectionModel(model);
        assertEquals(model, table.getSelectionMapper().getViewSelectionModel());
    }


    /**
     * test if LinkController/executeButtonAction is properly registered/unregistered on
     * setRolloverEnabled.
     *
     */
    @Test
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
            if (listeners[i] instanceof TableRolloverController) {
                return (TableRolloverController) listeners[i];
            }
        }
        return null;
    }
    
    

    /**
     * Issue #180-swingx: outOfBoundsEx if testColumn is hidden.
     *
     */
    @Test
    public void testHighlighterHiddenTestColumn() {
        JXTable table = new JXTable(sortableTableModel);
        table.getColumnExt(0).setVisible(false);
        Highlighter highlighter = new ColorHighlighter(new PatternPredicate("a", 0), null,
                Color.RED);
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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

    @Test
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
    @Test
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
    @Test
    public void testIndividualRowHeight() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeightEnabled(true);
        table.setRowHeight(0, 25);
        assertEquals(25, table.getRowHeight(0));
        assertEquals(table.getRowHeight(), table.getRowHeight(1));
        table.getFilters().getSortController().setSortKeys
            (Collections.singletonList(
                new SortKey(SortOrder.DESCENDING, 0)));
        assertEquals(table.getRowHeight(), table.getRowHeight(1));
        assertEquals(25, table.getRowHeight(table.getRowCount() - 1));
        table.setRowHeight(table.getRowHeight());
        assertEquals(table.getRowHeight(), table.getRowHeight(table.getRowCount() - 1));
    }
    
    @Test
    public void testResetIndividualRowHeight() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        table.setRowHeightEnabled(true);
        table.setRowHeight(0, 25);
        table.getFilters().getSortController().setSortKeys
            (Collections.singletonList(
                new SortKey(SortOrder.DESCENDING, 0)));
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testSorterAfterColumnHidden() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
        List<? extends SortKey> sortKeys = table.getFilters().getSortController().getSortKeys();
        columnX.setVisible(false);
        assertEquals("interactive sorter must be same as sorter in column", 
                sortKeys, table.getFilters().getSortController().getSortKeys());
    }
    
    /**
     * Issue #54-swingx: hidden columns not removed.
     *
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testMixedComparableTypesWithNonComparable() {
        
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, new Integer(2) },
                new Object[] { Boolean.TRUE, new Object() } };
        String[] columnNames = new String[] { "Critical", "Task" };
        DefaultTableModel model =  new DefaultTableModel(rowData, columnNames);
        final JXTable table = new JXTable(model);
        table.toggleSortOrder(1);
    }   

    @Test
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
    @Test
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
    @Test
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
    @Test
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
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     *
     */
    @Test
    public void testLeadFocusCell() throws InterruptedException, InvocationTargetException {
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
         SwingUtilities.invokeAndWait(new Runnable() {
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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

    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testAddRowAboveSelection() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        assertEquals("last row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.insertRow(0, new Object[table.getColumnCount()]);
        assertEquals("last row must still be selected after add above", table.getRowCount() - 1, table.getSelectedRow());
    }

    @Test
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
    @Test
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

    @Test
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
    @Test
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
    @Test
    public void testDeleteRowBelowSelection() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // sort ascending 
        table.toggleSortOrder(0);
        assertEquals("first row must be selected", selectedRow, table.getSelectedRow());
        ascendingModel.removeRow(selectedRow + 1);
        assertEquals("first row must still be selected after remove", selectedRow, table.getSelectedRow());
    }

    /**
     * Issue #223
     * test if selection is kept if row below selection is removed.
     *
     */
    @Test
    public void testDeleteRowBelowSelectionInvertedOrder() {
        DefaultTableModel ascendingModel = createAscendingModel(0, 20);
        JXTable table = new JXTable(ascendingModel);
        int selectedRow = 0;
        table.setRowSelectionInterval(selectedRow, selectedRow);
        // sort ascending
        table.toggleSortOrder(0);
        // revert order 
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
    @Test
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
     * quick check if overriding sortOnChange prevents auto-resort.
     *
     */
    @Test
    public void testSortOnChange() {
        JXTable table = new JXTable(createAscendingModel(0, 10)) {

            @Override
            protected boolean shouldSortOnChange(TableModelEvent e) {
                if (isUpdate(e)) {
                    return false;
                }
                return super.shouldSortOnChange(e);
            }
            
        };
        // sort ascending
        table.toggleSortOrder(0);
        Integer first = (Integer) table.getValueAt(0, 0);
        Integer second = (Integer) table.getValueAt(1, 0);
        // sanity
        assertTrue(first.intValue() < second.intValue());
        int high = first.intValue() + 100;
        // set a high value
        table.setValueAt(high, 0, 0);
        assertEquals("sort should not update after", high, table.getValueAt(0, 0));
    }
    

    /**
     * check if setting to false really disables sortability.
     *
     */
    @Test
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
    @Test
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
    @Test
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

        @Override
        public boolean isCellEditable(int row, int col) {
            return getRowObject(row).isEditable();
        }
    }

    

    /**
     * Issue #167: IllegalStateException if re-setting filter while
     * sorting.
     *
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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

        @Override
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

        @Override
        public boolean isCellEditable(int row, int column) {
            return (column == 1);
        }

        @Override
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
    
    // test per-column highlighting
    private static class TestingHighlighter extends AbstractHighlighter {
        private List<Highlighter> events;
        
        public TestingHighlighter(List<Highlighter> events) {
            this.events = events;
        }
        
        @Override
        protected Component doHighlight(Component component, ComponentAdapter adapter) {
            events.add(this);
            return component;
        }
    }
    
    @Test
    public void testColumnHighlighting() {
        JXTable table = new JXTable(tableModel);
        List<Highlighter> events = new ArrayList<Highlighter>();
        
        Highlighter tableHighlighter = new TestingHighlighter(events);
        Highlighter columnHighlighter = new TestingHighlighter(events);
        
        //sanity check
        assertEquals(0, events.size());
        
        table.addHighlighter(tableHighlighter);
        table.getColumnExt(0).addHighlighter(columnHighlighter);
        
        //explicity prepare the renderer
        table.prepareRenderer(new DefaultTableCellRenderer(), 0, 0);
        
        assertEquals(2, events.size());
        assertSame(events.get(0), tableHighlighter);
        assertSame(events.get(1), columnHighlighter);
        
        events.clear();
        
        //explicity prepare the renderer
        table.prepareRenderer(new DefaultTableCellRenderer(), 0, 1);
        
        assertEquals(1, events.size());
        assertSame(events.get(0), tableHighlighter);
    }
}
