/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Color;

import javax.swing.AbstractListModel;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.SearchPredicate;
import org.jdesktop.swingx.search.PatternModel;
import org.jdesktop.swingx.search.SearchFactory;
import org.jdesktop.swingx.search.Searchable;
import org.jdesktop.swingx.search.TableSearchable;


/**
 * Exposing open issues in Searchable implementations.
 * PENDING JW: should be in package search - but references swingx package internals. 
 * 
 * @author Jeanette Winzenburg
 */
public class FindIssues extends FindTest {

    public static void main(String args[]) {
        setSystemLF(true);
//        Locale.setDefault(new Locale("es"));
        FindIssues test = new FindIssues();
        try {
//          test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Mark.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    

    /**
     * Issue #487-swingx: NPE if setting a not-null Searchable before 
     * showing. Hack around ...
     */
    public void testFindBarNPEComponents() {
        Searchable searchable = new JXTable().getSearchable();
        JXFindBar findBar = new JXFindBar();
        // old hack
//        findBar.addNotify();
        findBar.setSearchable(searchable);
        fail("quick hack ... remove me if really fixed");
    }


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

    // -------------------- interactive tests

    
    public void interactiveTableMarkAllMatches() {
        JXTable table = new JXTable();
        table.setSearchable(new XTableSearchable(table));
        table.setModel(new TestTableModel());
        SearchFactory.getInstance().setUseFindBar(true);
        showWithScrollingInFrame(table, "quick sample for mark all matches");
    }
    
    /**
     * Searchable which highlights all matches.
     */
    public static class XTableSearchable extends TableSearchable {
        
        /**
         * @param table
         */
        public XTableSearchable(JXTable table) {
            super(table);
        }

        @Override
        protected AbstractHighlighter getConfiguredMatchHighlighter() {
            CompoundHighlighter searchHL = (CompoundHighlighter) getMatchHighlighter();
            if (!hasMatch(lastSearchResult)) {
                searchHL.setHighlightPredicate(HighlightPredicate.NEVER);
            } else {
                searchHL.setHighlightPredicate(new SearchPredicate(lastSearchResult.getPattern()));
                ((AbstractHighlighter) searchHL.getHighlighters()[1]).setHighlightPredicate(
                        new SearchPredicate(lastSearchResult.getPattern(), 
                                lastSearchResult.getFoundRow(), lastSearchResult.getFoundColumn()));
            }
            return searchHL;
        }

        @Override
        protected AbstractHighlighter createMatchHighlighter() {
            ColorHighlighter base = new ColorHighlighter(Color.YELLOW.brighter(), null, 
                    Color.YELLOW.darker(), null);
            ColorHighlighter cell = new ColorHighlighter(Color.YELLOW.darker(), null);
            CompoundHighlighter match = new CompoundHighlighter(base, cell);
            return match;
        }

    }

    /**
     * #463-swingx: batch find and cellSelection don't play nicely.
     *
     */
    public void interactiveTableBatchWithCellSelectionIssue() {
        JXTable table = new JXTable(new TestTableModel());
        table.setCellSelectionEnabled(true);
        showWithScrollingInFrame(table, "batch find with cell selection");
    }

}
