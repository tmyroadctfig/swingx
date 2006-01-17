/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.AbstractListModel;


/**
 * Exposing open issues in Searchable implementations.
 * 
 * @author Jeanette Winzenburg
 */
public class FindIssues extends FindTest {

    /**
     * Issue #236-swingx: backwards match in first row shows not-found-message.
     * Trackdown from Nicfagn - findPanel.doSearch always returns the next startIndex
     * in backwards search that's -1 which is interpreted as "not-found"
     * 
     */
    public void testFindPanelFirstRowBackwards() {
        JXList list = new JXList( new AbstractListModel() {
            private String[] data = { "a", "b", "c" };
            public Object getElementAt(int index) {
                return data[ index ];
            }
            public int getSize() {
                return data.length;
            }
        });
        JXFindPanel findPanel = new JXFindPanel(list.getSearchable());
        findPanel.init();
        PatternModel patternModel = findPanel.getPatternModel();
        patternModel.setBackwards(true);
        patternModel.setRawText("a");
        int matchIndex = list.getSearchable().search(patternModel.getPattern(),
                patternModel.getFoundIndex(), patternModel.isBackwards());
        assertEquals("found match", matchIndex, findPanel.doSearch());
    }
    

}
