/*
 * Created on 07.10.2005
 *
 */
package org.jdesktop.swingx;

import javax.swing.JList;
import javax.swing.plaf.UIResource;

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
     * Issue #601-swingx: allow LAF to hook in LAF provided renderers.
     * 
     * Expected: plain ol' list does install UIResource (while tree doesn't)
     */
    public void testLAFRendererList() {
        JList list = new JList();
        assertNotNull("default renderer installed", list.getCellRenderer());
        assertTrue("expected UIResource, but was: " + list.getCellRenderer().getClass(), 
                list.getCellRenderer() instanceof UIResource);
    }

    /**
     * Issue #601-swingx: allow LAF to hook in LAF provided renderers.
     * 
     * Expected: plain ol' list does install UIResource (while tree doesn't)
     */
    public void testLAFRendererXList() {
        JXList list = new JXList();
        assertNotNull("default renderer installed", list.getCellRenderer());
        assertTrue("expected UIResource, but was: " + list.getCellRenderer().getClass(), 
                list.getCellRenderer() instanceof UIResource);
    }

    public void testDummy() {
        
    }
}
