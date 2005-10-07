/*
 * Created on 07.10.2005
 *
 */
package org.jdesktop.swingx;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;

public class JXListIssues extends JXListTest {

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
        final JXList list = new JXList(ascendingListModel);
        // a side-effect of setFilterEnabled is to clear the selection!
        // this is done in JList.setModel(..) which is called when 
        // changing filterEnabled!
        list.setFilterEnabled(true);
        assertEquals(20, list.getModelSize());
        final int modelRow = 0;
        // set a selection 
        list.setSelectedIndex(modelRow);
        ListSelectionListener l = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int viewRow = list.getSelectedIndex(); //table.convertRowIndexToView(modelRow);
                assertEquals("view index visible", 0, viewRow);
                int convertedRow = list.convertRowIndexToModel(viewRow);
                
            }
            
        };
        list.getSelectionModel().addListSelectionListener(l);
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getModelSize());
    }


    /**
     * setFilterEnabled throws NPE if formerly had selection.
     * 
     * Reason is internal state mismanagement... filterEnabled must be
     * set before calling super.setModel!
     *
     */
    public void testSetFilterEnabledWithSelection() {
        final JXList list = new JXList(ascendingListModel);
        // a side-effect of setFilterEnabled is to clear the selection!
        // this is done in JList.setModel(..) which is called when 
        // changing filterEnabled!
        assertEquals(20, list.getModelSize());
        final int modelRow = 0;
        // set a selection 
        list.setSelectedIndex(modelRow);
        list.setFilterEnabled(true);
        
    }
}
