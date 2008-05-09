/*
 * Created on 07.10.2005
 *
 */
package org.jdesktop.swingx;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.SortOrder;

public class JXListIssues extends JXListTest {

    public static void main(String[] args) {
        setSystemLF(true);
        JXListIssues test = new JXListIssues();
        try {
          test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Rollover.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

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
     * Issue #601-swingx: allow LAF to hook in LAF provided renderers.
     * 
     * Expected: plain ol' list does install UIResource (while tree doesn't)
     */
    public void testLAFRendererList() {
        JList tree = new JList();
        assertNotNull("default renderer installed", tree.getCellRenderer());
        assertTrue("expected UIResource, but was: " + tree.getCellRenderer().getClass(), 
                tree.getCellRenderer() instanceof UIResource);
    }

    /**
     * Issue #601-swingx: allow LAF to hook in LAF provided renderers.
     * 
     * Expected: plain ol' list does install UIResource (while tree doesn't)
     */
    public void testLAFRendererXList() {
        JXList tree = new JXList();
        assertNotNull("default renderer installed", tree.getCellRenderer());
        assertTrue("expected UIResource, but was: " + tree.getCellRenderer().getClass(), 
                tree.getCellRenderer() instanceof UIResource);
    }

    public void testConvertToViewPreconditions() {
        final JXList list = new JXList(ascendingListModel);
        // a side-effect of setFilterEnabled is to clear the selection!
        // this is done in JList.setModel(..) which is called when 
        // changing filterEnabled!
        list.setFilterEnabled(true);
        assertEquals(20, list.getElementCount());
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
        try {
            list.convertIndexToView(ascendingListModel.getSize());
            fail("accessing list out of range index must throw execption");
        } catch (IndexOutOfBoundsException ex) {
            // this is correct behaviour
        } catch (Exception ex) {
            fail("got " + ex);
        }
        
    }

    public void testDummy() {
        
    }
}
