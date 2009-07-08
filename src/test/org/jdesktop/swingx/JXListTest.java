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
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import org.jdesktop.swingx.JXList.DelegatingRenderer;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.SearchPredicate;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.rollover.ListRolloverController;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Testing JXList. <p>
 * 
 * Note: all tests related to the disabled sorting/filtering support are moved to
 * JXListSortRevamp and forced to fail.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class JXListTest extends InteractiveTestCase {

    protected ListModel listModel;
    protected DefaultListModel ascendingListModel;

    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    

    /**
     * Issue #816-swingx: Delegating renderer must create list's default.
     * Consistent api: expose wrappedRenderer the same way as wrappedModel
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testGetString() {
        JXList list = new JXList(AncientSwingTeam.createNamedColorListModel());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        list.setCellRenderer(new DefaultListRenderer(sv));
        String text = list.getStringAt(0);
        assertEquals(sv.getString(list.getElementAt(0)), text);
    }
    

    /**
     * test that swingx renderer is used by default.
     *
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
     * test exceptions on null data(model, vector, array).
     *
     */
    @Test
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
           new JXList((Vector<?>) null);
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
     * add and test comparator property.
     * 
     */
    @Test
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
     * test if LinkController/executeButtonAction is properly registered/unregistered on
     * setRolloverEnabled.
     *
     */
    @Test
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
            if (listeners[i] instanceof ListRolloverController<?>) {
                return (ListRolloverController<?>) listeners[i];
            }
        }
        return null;
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
//    protected Filter createNumberFilter(final int lowerBound, final int upperBound, final boolean inside) {
//        PatternFilter f = new PatternFilter() {
//
//            @Override
//            public boolean test(int row) {
//                Object value = getInputValue(row, getColumnIndex());
//                if (!(value instanceof Number)) return false;
//                boolean isInside = ((Number) value).intValue() >= lowerBound 
//                    && ((Number) value).intValue() <= upperBound;
//                return inside ? isInside : !isInside;
//            }
//            
//        };
//        return f;
//    }
    
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
