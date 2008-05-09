/*
 * Created on 07.10.2005
 *
 */
package org.jdesktop.swingx;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableModel;

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
     * Open question: should selectionMapper guard against invalid
     * selection indices from view selection? Currently it blows. 
     * Probably good because it's most certainly a programming error.
     */
    public void testInvalidViewSelect() {
        DefaultListModel model = new DefaultListModel();
        model.addElement("something");
        JXList list = new JXList(model, true);
        list.setSortOrder(SortOrder.ASCENDING);
        // list guards against invalid index
        list.setSelectedIndex(1);
        // selectionModel can't do anything (has no notion about size)
        // selectionMapper doesn't guard and blows on conversion - should it?
        list.getSelectionModel().setSelectionInterval(1, 1);
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
