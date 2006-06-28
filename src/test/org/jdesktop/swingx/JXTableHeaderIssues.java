/*
 * Created on 28.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Dimension;

import org.jdesktop.swingx.decorator.SortOrder;

public class JXTableHeaderIssues extends JXTableHeaderTest {

    /**
     * Issue ??-swingx: header heigth depends on sort icon (for ocean only?) 
     * NOTE: this seems to be independent of the tweaks to xTableHeaders
     *   prefSize.
     */
    public void testSortedPreferredHeight() {
        JXTable table = new JXTable(10, 2);
        JXTableHeader tableHeader = (JXTableHeader) table.getTableHeader();
        Dimension dim = tableHeader.getPreferredSize();
        table.setSortOrder(0, SortOrder.ASCENDING);
        assertEquals("Header pref height must be unchanged if sorted",
                dim.height, tableHeader.getPreferredSize().height);
    }

}
