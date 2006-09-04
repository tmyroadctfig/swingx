/*
 * Created on 07.10.2005
 *
 */
package org.jdesktop.swingx;

import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.util.ListDataReport;

public class JXListIssues extends JXListTest {

    /**
     * Issue 377-swingx: list with filters enabled fires incorrect events.
     * 
     * needs a deeper fix: currently the wrapper fires an unspecified 
     * contentsChange on all events received. What's required is to
     * map the event indices ... with the usual caveats (not always
     * possible because continous intervals might map to discontinous).
     */
    public void testListDataEvents() {
        JXList list = new JXList(ascendingListModel, true);
        ListDataReport report = new ListDataReport();
        list.getModel().addListDataListener(report);
        // remove row 
        ascendingListModel.remove(0);
        assertEquals("list must have fired event", 1, report.getEventCount());
        assertEquals("list must have fired event of type removed", 
                1, report.getRemovedEventCount());
        
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
