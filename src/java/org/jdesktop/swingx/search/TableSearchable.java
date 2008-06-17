/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx.search;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.SearchPredicate;

public class TableSearchable extends AbstractSearchable {


        protected JXTable table;

        /**
         * @param table
         */
        public TableSearchable(JXTable table) {
            this.table = table;
        }

        @Override
        protected void findMatchAndUpdateState(Pattern pattern, int startRow,
                boolean backwards) {
            SearchResult matchRow = null;
            if (backwards) {
                // CHECK: off-one end still needed?
                // Probably not - the findXX don't have side-effects any longer
                // hmmm... still needed: even without side-effects we need to
                // guarantee calling the notfound update at the very end of the
                // loop.
                for (int r = startRow; r >= -1 && matchRow == null; r--) {
                    matchRow = findMatchBackwardsInRow(pattern, r);
                    updateState(matchRow);
                }
            } else {
                for (int r = startRow; r <= getSize() && matchRow == null; r++) {
                    matchRow = findMatchForwardInRow(pattern, r);
                    updateState(matchRow);
                }
            }
            // KEEP - JW: Needed to update if loop wasn't entered!
            // the alternative is to go one off in the loop. Hmm - which is
            // preferable?
            // updateState(matchRow);

        }

        /**
         * called if sameRowIndex && !hasEqualRegEx. Matches the cell at
         * row/lastFoundColumn against the pattern. PRE: lastFoundColumn valid.
         * 
         * @param pattern
         * @param row
         * @return an appropriate <code>SearchResult</code> if matching or null
         */
        @Override
        protected SearchResult findExtendedMatch(Pattern pattern, int row) {
            return findMatchAt(pattern, row, lastSearchResult.foundColumn);
        }

        /**
         * Searches forward through columns of the given row. Starts at
         * lastFoundColumn or first column if lastFoundColumn < 0. returns an
         * appropriate SearchResult if a matching cell is found in this row or
         * null if no match is found. A row index out off range results in a
         * no-match.
         * 
         * @param pattern
         * @param row
         *            the row to search
         * @return an appropriate <code>SearchResult</code> if a matching cell
         * is found in this row or null if no match is found
         */
        private SearchResult findMatchForwardInRow(Pattern pattern, int row) {
            int startColumn = (lastSearchResult.foundColumn < 0) ? 0 : lastSearchResult.foundColumn;
            if (isValidIndex(row)) {
                for (int column = startColumn; column < table.getColumnCount(); column++) {
                    SearchResult result = findMatchAt(pattern, row, column);
                    if (result != null)
                        return result;
                }
            }
            return null;
        }

        /**
         * Searches forward through columns of the given row. Starts at
         * lastFoundColumn or first column if lastFoundColumn < 0. returns an
         * appropriate SearchResult if a matching cell is found in this row or
         * null if no match is found. A row index out off range results in a
         * no-match.
         * 
         * @param pattern
         * @param row
         *            the row to search
         * @return an appropriate <code>SearchResult</code> if a matching cell is found
         * in this row or null if no match is found
         */
        private SearchResult findMatchBackwardsInRow(Pattern pattern, int row) {
            int startColumn = (lastSearchResult.foundColumn < 0) ? table.getColumnCount() - 1
                    : lastSearchResult.foundColumn;
            if (isValidIndex(row)) {
                for (int column = startColumn; column >= 0; column--) {
                    SearchResult result = findMatchAt(pattern, row, column);
                    if (result != null)
                        return result;
                }
            }
            return null;
        }

        /**
         * Matches the cell content at row/col against the given Pattern.
         * Returns an appropriate SearchResult if matching or null if no
         * matching
         * 
         * @param pattern
         * @param row
         *            a valid row index in view coordinates
         * @param column
         *            a valid column index in view coordinates
         * @return an appropriate <code>SearchResult</code> if matching or null
         */
        protected SearchResult findMatchAt(Pattern pattern, int row, int column) {
         // this is pre-767-swingx: consistent string api
//            Object value = getValueAt(row, column);
//            if (value != null) {
//                Matcher matcher = pattern.matcher(value.toString());
//                if (matcher.find()) {
//                    return createSearchResult(matcher, row, column);
//                }
//            }
//             @KEEP JW replacement once we have a uniform string representatioon
            String text = table.getStringAt(row, column);
            if ((text != null) && (text.length() > 0 )) {
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    return createSearchResult(matcher, row, column);
                }
            }
            return null;
        }

        /**
         * Called if startIndex is different from last search, reset the column
         * to -1 and make sure a backwards/forwards search starts at last/first
         * row, respectively.
         * 
         * @param startIndex
         * @param backwards
         * @return adjusted <code>startIndex</code>
         */
        @Override
        protected int adjustStartPosition(int startIndex, boolean backwards) {
            lastSearchResult.foundColumn = -1;
            return super.adjustStartPosition(startIndex, backwards);
        }

        /**
         * Moves the internal start for matching as appropriate and returns the
         * new startIndex to use. Called if search was messaged with the same
         * startIndex as previously.
         * 
         * @param startRow
         * @param backwards
         * @return new start index to use
         */
        @Override
        protected int moveStartPosition(int startRow, boolean backwards) {
            if (backwards) {
                lastSearchResult.foundColumn--;
                if (lastSearchResult.foundColumn < 0) {
                    startRow--;
                }
            } else {
                lastSearchResult.foundColumn++;
                if (lastSearchResult.foundColumn >= table.getColumnCount()) {
                    lastSearchResult.foundColumn = -1;
                    startRow++;
                }
            }
            return startRow;
        }

        /**
         * Checks if the startIndex is a candidate for trying a re-match.
         * 
         * 
         * @param startIndex
         * @return true if the startIndex should be re-matched, false if not.
         */
        @Override
        protected boolean isEqualStartIndex(final int startIndex) {
            return super.isEqualStartIndex(startIndex)
                    && isValidColumn(lastSearchResult.foundColumn);
        }

        /**
         * checks if row is in range: 0 <= row < getRowCount().
         * 
         * @param column
         * @return true if the column is in range, false otherwise
         */
        private boolean isValidColumn(int column) {
            return column >= 0 && column < table.getColumnCount();
        }


        @Override
        protected int getSize() {
            return table.getRowCount();
        }

        /**
         * use and move the match highlighter.
         * PRE: markByHighlighter
         *
         */
        protected void moveMatchByHighlighter() {
            AbstractHighlighter searchHL = getConfiguredMatchHighlighter();
            // no match
            if (!hasMatch(lastSearchResult)) {
                return;
            } else {
                ensureInsertedSearchHighlighters(searchHL);
                Rectangle cellRect = table.getCellRect(lastSearchResult.foundRow, lastSearchResult.foundColumn, true);
                if (cellRect != null) {
                    table.scrollRectToVisible(cellRect);
                }
            }
        }

        /**
         * @return a highlighter configured for matching
         */
        protected AbstractHighlighter getConfiguredMatchHighlighter() {
            AbstractHighlighter searchHL = getMatchHighlighter();
            HighlightPredicate predicate = HighlightPredicate.NEVER;
            // no match
            if (hasMatch(lastSearchResult)) {
                predicate = new SearchPredicate(lastSearchResult.pattern, lastSearchResult.foundRow, table.convertColumnIndexToModel(lastSearchResult.foundColumn));
            }
            searchHL.setHighlightPredicate(predicate);
            return searchHL;
        }

        /**
         * @param result
         * @return {@code true} if the {@code result} contains a match;
         *         {@code false} otherwise
         */
        protected boolean hasMatch(SearchResult result) {
            boolean noMatch =  (result.getFoundRow() < 0) || (result.getFoundColumn() < 0);
            return !noMatch;
        }
        
        protected void moveMatchBySelection() {
            if (!hasMatch(lastSearchResult)) {
                return;
            }
            int row = lastSearchResult.foundRow;
            int column = lastSearchResult.foundColumn;
            table.changeSelection(row, column, false, false);
            if (!table.getAutoscrolls()) {
                // scrolling not handled by moving selection
                Rectangle cellRect = table.getCellRect(row, column, true);
                if (cellRect != null) {
                    table.scrollRectToVisible(cellRect);
                }
            }
        }
        
        @Override
        protected void moveMatchMarker() {
            if (markByHighlighter()) {
                moveMatchByHighlighter();
            } else { // use selection
                moveMatchBySelection();
            }
        }


        protected boolean markByHighlighter() {
            return Boolean.TRUE.equals(table.getClientProperty(MATCH_HIGHLIGHTER));
        }


        private void ensureInsertedSearchHighlighters(Highlighter highlighter) {
            if (!isInPipeline(highlighter)) {
                table.addHighlighter(highlighter);
            }
        }

        private boolean isInPipeline(Highlighter searchHighlighter) {
            Highlighter[] inPipeline = table.getHighlighters();
            if ((inPipeline.length > 0) && 
               (searchHighlighter.equals(inPipeline[inPipeline.length -1]))) {
                return true;
            }
            table.removeHighlighter(searchHighlighter);
            return false;
        }

        private AbstractHighlighter matchHighlighter;
        
        /**
         * @return a highlighter used for matching
         */
        protected AbstractHighlighter getMatchHighlighter() {
            if (matchHighlighter == null) {
                matchHighlighter = createMatchHighlighter();
            }
            return matchHighlighter;
        }

        /**
         * @return a highlighter used for matching
         */
        protected AbstractHighlighter createMatchHighlighter() {
            return new ColorHighlighter(HighlightPredicate.NEVER, Color.YELLOW.brighter(), 
                    null, Color.YELLOW.brighter(), 
                    null);
        }

        
//        private SearchHighlighter searchHighlighter;
        

//        protected SearchHighlighter getSearchHighlighter() {
//            if (searchHighlighter == null) {
//                searchHighlighter = createSearchHighlighter();
//            }
//            return searchHighlighter;
//        }
//        
//        protected SearchHighlighter createSearchHighlighter() {
//            return new SearchHighlighter();
//        }

    }