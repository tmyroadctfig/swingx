/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXList.DelegatingRenderer;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.SearchPredicate;
import org.jdesktop.swingx.decorator.SelectionMapper;
import org.jdesktop.swingx.decorator.SortKey;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.rollover.ListRolloverController;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.ListDataReport;
import org.jdesktop.test.PropertyChangeReport;

/**
 * Testing JXList.
 * 
 * @author Jeanette Winzenburg
 */
public class JXListTest extends InteractiveTestCase {

    protected ListModel listModel;
    protected DefaultListModel ascendingListModel;

    
    /**
     * Issue #855-swingx: throws AIOOB on repeated remove/add.
     * Reason is that the lead/anchor is not removed in removeIndexInterval
     */
    public void testAddRemoveSelect() {
        DefaultListModel model = new DefaultListModel();
        model.addElement("something");
        JXList list = new JXList(model, true);
        list.setSortOrder(SortOrder.ASCENDING);
        list.setSelectedIndex(0);
        model.remove(0);
        assertTrue("sanity - empty selection after remove", list.isSelectionEmpty());
        model.addElement("element");
        assertTrue("sanity - empty selection re-adding", list.isSelectionEmpty());
        list.setSelectedIndex(0);
    }
    
    /**
     * Issue #855-swingx: throws AIOOB on repeated remove/add.
     * Reason is that the lead/anchor is not removed in removeIndexInterval.
     * 
     * Compare JXTable behaviour: doesn't blow. JXList probably does because of 
     * the necessary event mapping. Sequence of selection/pipeline induced 
     * cleanup is different.
     * 
     */
    public void testAddRemoveSelectTable() {
        DefaultTableModel model = new DefaultTableModel(0, 1);
        model.addRow(new Object[] {"something"});
        JXTable list = new JXTable(model);
        list.setSortOrder(0, SortOrder.ASCENDING);
        list.setRowSelectionInterval(0, 0);
        model.removeRow(0);
        assertTrue("sanity - empty selection after remove", list.getSelectionModel().isSelectionEmpty());
        model.addRow(new Object[] {"something"});
        assertTrue("sanity - empty selection re-adding", list.getSelectionModel().isSelectionEmpty());
        list.setRowSelectionInterval(0, 0);
    }

    /**
     * Issue #816-swingx: Delegating renderer must create list's default.
     * Consistent api: expose wrappedRenderer the same way as wrappedModel
     */
    public void testWrappedRendererDefault() {
        JXList list = new JXList();
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        assertSame("wrapping renderer must use list's default on null", 
                 renderer.getDelegateRenderer(), list.getWrappedCellRenderer());
    }

    /**
     * Issue #816-swingx: Delegating renderer must create list's default.
     * Consistent api: expose wrappedRenderer the same way as wrappedModel
     */
    public void testWrappedRendererCustom() {
        JXList list = new JXList();
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        ListCellRenderer custom = new DefaultListRenderer();
        list.setCellRenderer(custom);
        assertSame("wrapping renderer must use list's default on null", 
                 renderer.getDelegateRenderer(), list.getWrappedCellRenderer());
    }
    
    /**
     * Issue #816-swingx: Delegating renderer must create list's default.
     * Delegating uses default on null, here: default default.
     */
    public void testDelegatingRendererUseDefaultSetNull() {
        JXList list = new JXList();
        ListCellRenderer defaultRenderer = list.createDefaultCellRenderer();
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        list.setCellRenderer(null);
        assertEquals("wrapping renderer must use list's default on null", 
                defaultRenderer.getClass(), renderer.getDelegateRenderer().getClass());
    }

    /**
     * Issue #816-swingx: Delegating renderer must create list's default.
     * Delegating has default from list initially, here: default default.
     */
    public void testDelegatingRendererUseDefault() {
        JXList list = new JXList();
        ListCellRenderer defaultRenderer = list.createDefaultCellRenderer();
        assertEquals("sanity: creates default", DefaultListRenderer.class, 
                defaultRenderer.getClass());
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        assertEquals(defaultRenderer.getClass(), renderer.getDelegateRenderer().getClass());
    }
    
    /**
     * Issue #816-swingx: Delegating renderer must create list's default.
     * Delegating has default from list initially, here: custom default.
     */
    public void testDelegatingRendererUseCustomDefaultSetNull() {
        JXList list = new JXList() {

            @Override
            protected ListCellRenderer createDefaultCellRenderer() {
                return new CustomDefaultRenderer();
            }
            
        };
        ListCellRenderer defaultRenderer = list.createDefaultCellRenderer();
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        list.setCellRenderer(null);
        assertEquals("wrapping renderer must use list's default on null",
                defaultRenderer.getClass(), renderer.getDelegateRenderer().getClass());
    }
    
    /**
     * Issue #816-swingx: Delegating renderer must create list's default.
     * Delegating has default from list initially, here: custom default.
     */
    public void testDelegatingRendererUseCustomDefault() {
        JXList list = new JXList() {

            @Override
            protected ListCellRenderer createDefaultCellRenderer() {
                return new CustomDefaultRenderer();
            }
            
        };
        ListCellRenderer defaultRenderer = list.createDefaultCellRenderer();
        assertEquals("sanity: creates custom", CustomDefaultRenderer.class, 
                defaultRenderer.getClass());
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        assertEquals(defaultRenderer.getClass(), renderer.getDelegateRenderer().getClass());
    }
    /**
     * Dummy extension for testing - does nothing more as super.
     */
    public static class CustomDefaultRenderer extends DefaultListCellRenderer {
    }
    
    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on JXTable.
     */
    public void testGetString() {
        JXList list = new JXList(AncientSwingTeam.createNamedColorListModel());
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
        String text = list.getStringAt(0);
        assertEquals(sv.getString(list.getElementAt(0)), text);
    }
    

    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * Here: test single item remove fires interval removed
     */
    public void testListDataEventRemove() {
        JXList list = new JXList(ascendingListModel, true);
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove row 
        ascendingListModel.remove(0);
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type removed", 
                1, report.getRemovedEventCount());
        assertEquals("the index of removed item  ", 0, report.getLastEvent().getIndex0());
        
    }
    
    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * Here: test that a single item removal of a filtered element doesn't fire.
     */
    public void testListDataEventRemoveSingleFiltered() {
        JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        // filter out the first 10
        list.setFilters(new FilterPipeline(createNumberFilter(10, 100, true)));
        assertEquals(10, list.getElementCount());
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove a row which had been filtered 
        ascendingListModel.remove(0);
        assertEquals("list must not have fired event for filtered remove", 
                0, report.getEventCount());
    }
    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * Here: test that a removing a interval which is partly filtered maps
     *   correctly to the visible interval.
     *   
     */
    public void testListDataEventRemoveCutIntervalFiltered() {
        JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        // filter out the first 10
        list.setFilters(new FilterPipeline(createNumberFilter(10, 100, true)));
        assertEquals(10, list.getElementCount());
        assertEquals(10, list.getElementAt(0));
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove a row which had been filtered 
        ascendingListModel.removeRange(8, 12);
        assertEquals(7, ascendingListModel.get(7));
        assertEquals(13, ascendingListModel.get(8));
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type removed", 
                1, report.getRemovedEventCount());
        assertEquals("first removed index ", 0, report.getLastRemovedEvent().getIndex0());
        assertEquals("last removed index", 2, report.getLastRemovedEvent().getIndex1());
    }
    
    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * Here: a removing a interval including all unfiltered value
     *   
     */
    public void testListDataEventRemoveIntervalFilteredAll() {
        JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        // filter out the first 7 and last 7
        list.setFilters(new FilterPipeline(createNumberFilter(8, 12, true)));
        assertEquals(5, list.getElementCount());
        assertEquals(8, list.getElementAt(0));
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove a row which had been filtered 
        ascendingListModel.removeRange(7, 12);
        assertEquals(0, list.getElementCount());
        assertEquals(14, ascendingListModel.getSize());
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type removed", 
                1, report.getRemovedEventCount());
        assertEquals("first removed index ", 0, report.getLastRemovedEvent().getIndex0());
        assertEquals("last removed index", 4, report.getLastRemovedEvent().getIndex1());
    }
    
    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * set single item fires contentsChanged with single index.
     */
    public void testListDataEventSingleSet() {
        JXList list = new JXList(ascendingListModel, true);
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // sanity
        assertEquals(0, report.getEventCount());
        // remove row 
        ascendingListModel.setElementAt(-1, 0);
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type changed", 
                1, report.getChangedEventCount());
        assertEquals("changed index ", 0, report.getLastEvent().getIndex1());
        
    }
    
    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * insert fires intervalAdded.
     */
    public void testListDataEventAdd() {
        JXList list = new JXList(ascendingListModel, true);
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove row 
        ascendingListModel.insertElementAt(-1, 0);
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type added", 
                1, report.getAddedEventCount());
        
    }


    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * event source is the wrapping list model.
     */
    public void testListDataEventSource() {
        JXList list = new JXList(ascendingListModel, true);
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // some change
        ascendingListModel.insertElementAt(-1, 0);
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type changed", 
                list.getModel(), report.getLastEvent().getSource());
        
    }

    
    /**
     * Issue #477-swingx: list with filter not updated after setModel.
     * 
     * Reason is that there's no call to filter.flush in that path
     * of action. Probably due to mostly c&p from JXTable - which
     * always goes through tableChanged (which JList doesn't).
     * How to test?
     *
     */
    public void testSetModelFlushFilter() {
        final JXList list = new JXList();
        list.setFilterEnabled(true);
        PatternFilter filter = new PatternFilter(".*1.*", 0, 0);
        final FilterPipeline pipeline = new FilterPipeline(filter);
        final DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 10; i++)
            model.addElement("Element " + i);
        list.setFilters(pipeline);
        list.setModel(model);
        assertEquals(1, list.getElementCount());
    }

    /**
     * Issue #477-swingx:
     * 
     * Selection must be cleared after setModel. This is from
     * super's contract.
     *
     */
    public void testSetModelEmptySelection() {
        final JXList list = new JXList();
        list.setFilterEnabled(true);
        final DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 10; i++)
            model.addElement("Element " + i);
        list.setModel(model);
        int selection = 0;
        list.setSelectedIndex(selection);
        PatternFilter filter = new PatternFilter(".*", 0, 0);
        final FilterPipeline pipeline = new FilterPipeline(filter);
        list.setFilters(pipeline);
        assertEquals("setting filters must keep selection", selection, list.getSelectedIndex());
        list.setModel(model);
        assertEquals(model.getSize(), list.getElementCount());
        assertTrue("setting model must clear selectioon", list.isSelectionEmpty());
    }
    
    /**
     * test that swingx renderer is used by default.
     *
     */
    public void testDefaultListRenderer() {
        JXList list = new JXList();
        ListCellRenderer renderer = ((DelegatingRenderer) list.getCellRenderer()).getDelegateRenderer();
        assertTrue("default renderer expected to be DefaultListRenderer " +
                        "\n but is " + renderer.getClass(),
                renderer instanceof DefaultListRenderer);
    }
    
    /**
     * Issue #473-swingx: NPE in list with highlighter. <p> 
     * 
     * Renderers are doc'ed to cope with invalid input values.
     * Highlighters can rely on valid ComponentAdapter state. 
     * JXList delegatingRenderer is the culprit which does set
     * invalid ComponentAdapter state. Negative invalid index.
     *
     */
    public void testIllegalNegativeListRowIndex() {
        JXList list = new JXList(new Object[] {1, 2, 3});
        ListCellRenderer renderer = list.getCellRenderer();
        renderer.getListCellRendererComponent(list, "dummy", -1, false, false);
        SearchPredicate predicate = new SearchPredicate("\\QNode\\E");
        Highlighter searchHighlighter = new ColorHighlighter(predicate, null, Color.RED);
        list.addHighlighter(searchHighlighter);
        renderer.getListCellRendererComponent(list, "dummy", -1, false, false);
    }
    
    /**
     * Issue #473-swingx: NPE in list with highlighter. <p> 
     * 
     * Renderers are doc'ed to cope with invalid input values.
     * Highlighters can rely on valid ComponentAdapter state. 
     * JXList delegatingRenderer is the culprit which does set
     * invalid ComponentAdapter state. Invalid index > valid range.
     *
     */
    public void testIllegalExceedingListRowIndex() {
        JXList list = new JXList(new Object[] {1, 2, 3});
        ListCellRenderer renderer = list.getCellRenderer();
        renderer.getListCellRendererComponent(list, "dummy", list.getElementCount(), false, false);
        SearchPredicate predicate = new SearchPredicate("\\QNode\\E");
        Highlighter searchHighlighter = new ColorHighlighter(predicate, null, Color.RED);
        list.addHighlighter(searchHighlighter);
        renderer.getListCellRendererComponent(list, "dummy", list.getElementCount(), false, false);
    }
    
    /**
     * test convenience method accessing the configured adapter.
     *
     */
    public void testConfiguredComponentAdapter() {
        JXList list = new JXList(new Object[] {1, 2, 3});
        ComponentAdapter adapter = list.getComponentAdapter();
        assertEquals(0, adapter.column);
        assertEquals(0, adapter.row);
        adapter.row = 1;
        // corrupt adapter
        adapter.column = 1;
        adapter = list.getComponentAdapter(0);
        assertEquals(0, adapter.column);
        assertEquals(0, adapter.row);
    }
    
    /**
     * Test assumptions of accessing list model/view values through
     * the list's componentAdapter.
     * 
     * PENDING: the default's getValue() implementation is incorrect!
     *
     */
    public void testComponentAdapterCoordinates() {
        JXList list = new JXList(ascendingListModel, true);
        Object originalFirstRowValue = list.getElementAt(0);
        Object originalLastRowValue = list.getElementAt(list.getElementCount() - 1);
        assertEquals("view row coordinate equals model row coordinate", 
                list.getModel().getElementAt(0), originalFirstRowValue);
        // sort first column - actually does not change anything order 
        list.toggleSortOrder();
        // sanity asssert
        assertEquals("view order must be unchanged ", 
                list.getElementAt(0), originalFirstRowValue);
        // invert sort
        list.toggleSortOrder();
        // sanity assert
        assertEquals("view order must be reversed changed ", 
                list.getElementAt(0), originalLastRowValue);
        ComponentAdapter adapter = list.getComponentAdapter();
        assertEquals("adapter filteredValue expects row view coordinates", 
                list.getElementAt(0), adapter.getFilteredValueAt(0, 0));
        // adapter coordinates are view coordinates
        adapter.row = 0;
        adapter.column = 0;
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                list.getElementAt(0), adapter.getValue());
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                list.getElementAt(0), adapter.getValue(0));
    }
    

    /**
     * test exceptions on null data(model, vector, array).
     *
     */
    public void testNullData() {
        try {
            new JXList((ListModel) null);
            fail("JXList contructor must throw on null data");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (Exception e) {
            fail("unexpected exception type " + e);
        }
        
        try {
           new JXList((Vector) null);
            fail("JXList contructor must throw on null data");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (Exception e) {
            fail("unexpected exception type " + e);
        }
        
        try {
            new JXList((Object[]) null);
             fail("JXList contructor must throw on null data");
         } catch (IllegalArgumentException e) {
             // expected
         } catch (Exception e) {
             fail("unexpected exception type " + e);
         }
    }
    

    /**
     * test filterEnabled property on initialization.
     *
     */
    public void testConstructorFilterEnabled() {
        // 
        assertFilterEnabled(new JXList(), false);
        assertFilterEnabled(new JXList(new DefaultListModel()), false);
        assertFilterEnabled(new JXList(new Vector()), false);
        assertFilterEnabled(new JXList(new Object[] { }), false);
        
        assertFilterEnabled(new JXList(false), false);
        assertFilterEnabled(new JXList(new DefaultListModel(), false), false);
        assertFilterEnabled(new JXList(new Vector(), false), false);
        assertFilterEnabled(new JXList(new Object[] { }, false), false);

        assertFilterEnabled(new JXList(true), true);
        assertFilterEnabled(new JXList(new DefaultListModel(), true), true);
        assertFilterEnabled(new JXList(new Vector(), true), true);
        assertFilterEnabled(new JXList(new Object[] { }, true), true);
    }
    
    private void assertFilterEnabled(JXList list, boolean b) {
        assertEquals(b, list.isFilterEnabled());
    }

    /**
     * added xtable.setSortOrder(int, SortOrder)
     * 
     */
    public void testSetSortOrder() {
        JXList list = new JXList(ascendingListModel, true);
        list.setSortOrder(SortOrder.ASCENDING);
        assertEquals("column must be sorted after setting sortOrder on ", SortOrder.ASCENDING, list.getSortOrder());
    }
    
    /**
     * JXList has responsibility to guarantee usage of 
     * its comparator: setComparator if already sorted.
     */
    public void testDynamicComparatorToSortController() {
        JXList list = new JXList(listModel, true);
        list.toggleSortOrder();
        list.setComparator(Collator.getInstance());
        SortKey sortKey = SortKey.getFirstSortKeyForColumn(list.getFilters().getSortController().getSortKeys(), 0);
        assertNotNull(sortKey);
        assertEquals(list.getComparator(), sortKey.getComparator());
    }

    /**
     * JXList has responsibility to guarantee usage of 
     * its comparator: toggle.
     */
    public void testToggleComparatorToSortController() {
        JXList list = new JXList(listModel, true);
        list.setComparator(Collator.getInstance());
        list.toggleSortOrder();
        SortKey sortKey = SortKey.getFirstSortKeyForColumn(list.getFilters().getSortController().getSortKeys(), 0);
        assertNotNull(sortKey);
        assertEquals(list.getComparator(), sortKey.getComparator());
    }

    /**
     * JXList has responsibility to guarantee usage of 
     * its comparator: set.
     */
    public void testSetComparatorToSortController() {
        JXList list = new JXList(listModel, true);
        list.setComparator(Collator.getInstance());
        list.setSortOrder(SortOrder.DESCENDING);
        SortKey sortKey = SortKey.getFirstSortKeyForColumn(list.getFilters().getSortController().getSortKeys(), 0);
        assertNotNull(sortKey);
        assertEquals(list.getComparator(), sortKey.getComparator());
    }
    
    /**
     * add and test comparator property.
     * 
     */
    public void testComparator() {
        JXList list = new JXList();
        assertNull(list.getComparator());
        Collator comparator = Collator.getInstance();
        PropertyChangeReport report = new PropertyChangeReport();
        list.addPropertyChangeListener(report);
        list.setComparator(comparator);
        assertEquals(comparator, list.getComparator());
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("comparator"));
        
    }
    /**
     * testing new sorter api: 
     * getSortOrder(), toggleSortOrder(), resetSortOrder().
     *
     */
    public void testToggleSortOrder() {
        JXList list = new JXList(ascendingListModel, true);
        assertSame(SortOrder.UNSORTED, list.getSortOrder());
        list.toggleSortOrder();
        assertSame(SortOrder.ASCENDING, list.getSortOrder());
        list.toggleSortOrder();
        assertSame(SortOrder.DESCENDING, list.getSortOrder());
        list.resetSortOrder();
        assertSame(SortOrder.UNSORTED, list.getSortOrder());
    }

    /**
     * prepare sort testing: internal probs with SortController?
     */
    public void testSortController() {
        JXList list = new JXList(ascendingListModel, true);
        assertNotNull("sortController must be initialized", list.getSortController());
    }
    
    /**
     * Issue #232-swingx: selection not kept if selectionModel had been changed.
     *
     */
    public void testSelectionMapperUpdatedOnSelectionModelChange() {
        JXList table = new JXList(true);
        // created lazily, to see the failure,
        // need to get hold before replacing list's selection
        SelectionMapper mapper = table.getSelectionMapper();
        ListSelectionModel model = new DefaultListSelectionModel();
        table.setSelectionModel(model);
        assertEquals(model, mapper.getViewSelectionModel());
    }

    /**
     * Issue #232-swingx: selection not kept if selectionModel had been changed.
     *
     *  PENDING: selectionMapper shouldn't be available if list not filterable? 
     */
    public void testSelectionMapperFilterDisabled() {
        JXList table = new JXList();
        // created lazily, need to get hold before replacing list's selection
        SelectionMapper mapper = table.getSelectionMapper();
        ListSelectionModel model = new DefaultListSelectionModel();
        table.setSelectionModel(model);
        assertEquals(model, mapper.getViewSelectionModel());
    }

    /**
     * test if LinkController/executeButtonAction is properly registered/unregistered on
     * setRolloverEnabled.
     *
     */
    public void testLinkControllerListening() {
        JXList table = new JXList();
        table.setRolloverEnabled(true);
        assertNotNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.CLICKED_KEY));
        assertNotNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.ROLLOVER_KEY));
        assertNotNull("execute button action must be registered", table.getActionMap().get(JXList.EXECUTE_BUTTON_ACTIONCOMMAND));
        table.setRolloverEnabled(false);
        assertNull("LinkController must not be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.CLICKED_KEY ));
        assertNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.ROLLOVER_KEY));
        assertNull("execute button action must be de-registered", table.getActionMap().get(JXList.EXECUTE_BUTTON_ACTIONCOMMAND));
    }

    private PropertyChangeListener getLinkControllerAsPropertyChangeListener(JXList table, String propertyName) {
        PropertyChangeListener[] listeners = table.getPropertyChangeListeners(propertyName);
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof ListRolloverController) {
                return (ListRolloverController) listeners[i];
            }
        }
        return null;
    }

    public void testConvertToModelPreconditions() {
        final JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
        try {
            list.convertIndexToModel(list.getElementCount());
            fail("accessing list out of range index must throw execption");
        } catch (IndexOutOfBoundsException ex) {
            // this is correct behaviour
        } catch (Exception ex) {
            fail("got " + ex);
        }
        
    }
 

    public void testElementAtPreconditions() {
        final JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
        try {
            list.getElementAt(list.getElementCount());
            fail("accessing list out of range index must throw execption");
        } catch (IndexOutOfBoundsException ex) {
            // this is correct behaviour
        } catch (Exception ex) {
            fail("got " + ex);
        }
        
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
        final JXList list = new JXList(ascendingListModel, true);
        assertEquals(20, list.getElementCount());
        final int modelRow = 0;
        // set a selection 
        list.setSelectedIndex(modelRow);
        ListSelectionListener l = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int viewRow = list.getSelectedIndex(); 
                assertEquals("view index visible", 0, viewRow);
                // JW: the following checks if the reverse conversion succeeds
                list.convertIndexToModel(viewRow);
                
            }
            
        };
        list.getSelectionModel().addListSelectionListener(l);
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
    }


    /**
     * setFilterEnabled throws NPE if formerly had selection.
     * 
     *
     */
    public void testSetFilterEnabledWithSelection() {
        final JXList list = new JXList(ascendingListModel);
        assertEquals(20, list.getElementCount());
        final int modelRow = 0;
        // set a selection 
        list.setSelectedIndex(modelRow);
        list.setFilterEnabled(true);
        
    }

    public void testEmptyFilter() {
        JXList list = new JXList(ascendingListModel);
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
    }
    
    public void testFilterEnabled() {
        JXList list = new JXList(ascendingListModel, true);
        assertNotSame(ascendingListModel, list.getModel());
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }

    /**
     * Emergency break for #2-swinglabs: 
     * it's not allowed to reset filterEnabled property to false again.
     *
     */
    public void testFilterEnabledAndDisabled() {
        JXList list = new JXList(ascendingListModel, true);
        try {
            list.setFilterEnabled(false);
            fail("must not reset the filterEnabled property");
        } catch (IllegalStateException e) {
            // do nothing this is the exception we expect. 
        } catch (Exception e) { 
            fail("unexpected exception type" + e);
        }
//        assertSame(ascendingListModel, list.getModel());
//        assertEquals(ascendingListModel.getSize(), list.getElementCount());
//        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }
    
    public void testSortingFilterEnabled() {
        JXList list = new JXList(ascendingListModel, true);
        FilterPipeline pipeline = list.getFilters();
        assertNotNull(pipeline);
        list.setSortOrder(SortOrder.DESCENDING);
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(list.getElementCount() - 1));
        
    }
    
    public void testSortingKeepsModelSelection() {
        JXList list = new JXList(ascendingListModel, true);
        list.setSelectedIndex(0);
        list.setSortOrder(SortOrder.DESCENDING);
        assertEquals("last row must be selected after sorting", 
                ascendingListModel.getSize() - 1, list.getSelectedIndex());
    }

    /**
     * Issue #2-swinglabs: setting filter if not enabled throws exception on selection.
     * Reported by Kim.
     * 
     * Fix: should not accept filter if not enabled. 
     * PENDING JW: Doesn't? appears to be fixed? Check!  
     *  
     *
     */
    public void testFilterDisabled() {
        JXList list = new JXList();
        list.setModel(ascendingListModel);
        Filter[] filter = new Filter[] { new PatternFilter("1", 0, 0) };
        try {
            list.setFilters(new FilterPipeline(filter));
            fail("setFilter must not be called if filters not enabled");
        } catch (IllegalStateException e) {
            // do nothing, this is the documented behaviour
        } catch (Exception e) {
            fail("unexpected exception type " + e);
        }
    }

    
    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails after quick fix for #370-swingx. 
     *
     */
    public void testSelectionAfterAddAtFirst() {
        JXList list = new JXList(ascendingListModel, true);
        // selecte second row
        list.setSelectedIndex(0);
        Object oldFirst = list.getElementAt(0);
        Object newFirst = new Integer(-1);
        // add first 
        ascendingListModel.insertElementAt(newFirst, 0);
        // sanity
        assertEquals(newFirst, list.getElementAt(0));
        assertEquals(oldFirst, list.getElementAt(1));
        assertEquals("first row must be selected inserting new first", 
                0, list.getSelectedIndex());
    }

    /**
     * sanity test: compare table with list behaviour (#377-swingx)
     * 
     */
    public void testSelectionAfterAddAtFirstCompareTable() {
        DefaultTableModel ascendingModel = new DefaultTableModel(20, 2);
        JXTable table = new JXTable(ascendingModel);
        // select second row
        table.setRowSelectionInterval(0, 0);
        // remove first
        ascendingModel.addRow(new Object[]{"", ""});
        assertEquals("second row must be selected after adding new first", 
                0, table.getSelectedRow());
        
    }

    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails after quick fix for #370-swingx. 
     *
     */
    public void testSelectionAfterAddAbove() {
        JXList list = new JXList(ascendingListModel, true);
        // selecte second row
        list.setSelectedIndex(1);
        Object oldFirst = list.getElementAt(1);
        Object newFirst = new Integer(-1);
        // add first 
        ascendingListModel.insertElementAt(newFirst, 0);
        // sanity
        assertEquals(newFirst, list.getElementAt(0));
        assertEquals(oldFirst, list.getElementAt(2));
        assertEquals("second row must be selected inserting new first", 
                2, list.getSelectedIndex());
    }

    /**
     * Issue #223
     * test if selection is updated on add row above selection.
     *
     */
    public void testAddRowAboveSelectionInvertedOrder() {
        JXList list = new JXList(ascendingListModel, true);
        // select the last row in view coordinates
        int selectedRow = list.getElementCount() - 2;
        list.setSelectedIndex(selectedRow);
        // set a pipeline - ascending, no change
        list.toggleSortOrder();
        // revert order 
        list.toggleSortOrder();
        assertEquals("second row must be selected", 1, list.getSelectedIndex());
        // add row in model coordinates
        // insert high value
        Object row = new Integer(100);
        ascendingListModel.addElement(row);
        // selection must be moved one below
        assertEquals("selection must be incremented by one ", 2, list.getSelectedIndex());
        
    }

    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails after quick fix for #370-swingx. 
     *
     */
    public void testSelectionAfterDeleteAbove() {
        JXList list = new JXList(ascendingListModel, true);
        // selecte second row
        list.setSelectedIndex(1);
        // remove first 
        ascendingListModel.remove(0);
        assertEquals("first row must be selected removing old first", 
                0, list.getSelectedIndex());
        
    }
    /**
     * sanity test: compare table with list behaviour (#370-swingx)
     * 
     */
    public void testSelectionAfterDeleteAboveCompareTable() {
        DefaultTableModel ascendingModel = new DefaultTableModel(20, 2);
        JXTable table = new JXTable(ascendingModel);
        // select second row
        table.setRowSelectionInterval(1, 1);
        // remove first
        ascendingModel.removeRow(0);
        assertEquals("first row must be selected after removing old first", 
                0, table.getSelectedRow());
        
    }

    protected ListModel createListModel() {
        JXList list = new JXList();
        return new DefaultComboBoxModel(list.getActionMap().allKeys());
    }

    protected DefaultListModel createAscendingListModel(int startRow, int count) {
        DefaultListModel l = new DefaultListModel();
        for (int row = startRow; row < startRow  + count; row++) {
            l.addElement(new Integer(row));
        }
        return l;
    }
    protected DefaultListModel createListModelWithLinks() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 20; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a resource", null, url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }

    /**
     * Creates and returns a number filter, passing values which are numbers and
     * have int values inside or outside of the bounds (included), depending on the given 
     * flag.
     * 
     * @param lowerBound
     * @param upperBound
     * @param inside 
     * @return
     */
    protected Filter createNumberFilter(final int lowerBound, final int upperBound, final boolean inside) {
        PatternFilter f = new PatternFilter() {

            @Override
            public boolean test(int row) {
                Object value = getInputValue(row, getColumnIndex());
                if (!(value instanceof Number)) return false;
                boolean isInside = ((Number) value).intValue() >= lowerBound 
                    && ((Number) value).intValue() <= upperBound;
                return inside ? isInside : !isInside;
            }
            
        };
        return f;
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        listModel = createListModel();
        ascendingListModel = createAscendingListModel(0, 20);
    }
    public JXListTest() {
        super("JXList Tests");
    }

    
}
