/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;

import org.jdesktop.swingx.JXEditorPane.DocumentSearchable;
import org.jdesktop.swingx.search.AbstractSearchable;
import org.jdesktop.swingx.search.PatternModel;
import org.jdesktop.swingx.search.SearchFactory;
import org.jdesktop.swingx.search.Searchable;
import org.jdesktop.swingx.search.TableSearchable;

public class FindTest extends InteractiveTestCase {
    private static final Logger LOG = Logger
            .getLogger(FindTest.class.getName());
    public static void main(String args[]) {
      setSystemLF(true);
//      Locale.setDefault(new Locale("es"));
      FindTest test = new FindTest();
      try {
        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Compare.*");
//          test.runInteractiveTests("interactive.*Tree.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
   
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // sanity: new instance for each test
        SearchFactory.setInstance(new SearchFactory());
    }

    /**
     * Findbugs: NPE in SearchFactory - showFindDialog for null target component.
     * 
     */
    public void testSearchFactoryNullTarget() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run testSearchFactoryNullTarget - headless environment");
            return;
        }
        
        JXTable table = new JXTable(20, 2);
        SearchFactory.getInstance().showFindDialog(null, table.getSearchable());
    }
    /**
     * Issue #718-swingx: shared FindPanel not updated on LF change.
     * 
     * Here: check that dialog is new for different owner and old has been disposed.
     */
    public void testFindDialogNew() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        JXFrame frame = new JXFrame();
        JXTable table = new JXTable();
        frame.add(table);
        JComponent findPanel = SearchFactory.getInstance().getSharedFindPanel();
        // show search dialog for a searchable
        SearchFactory.getInstance().showFindDialog(table, table.getSearchable());
        Window window = SwingUtilities.getWindowAncestor(findPanel);
        assertSame(frame, window.getOwner());
        // setup of second searchable
        JXFrame second = new JXFrame();
        JXTree tree = new JXTree();
        second.add(tree);
        // show search dialog for a searchable
        SearchFactory.getInstance().showFindDialog(tree, tree.getSearchable());
        assertFalse("previous window must not be displayable", window.isDisplayable());
        assertSame(second, SwingUtilities.getWindowAncestor(findPanel).getOwner());
    }
    
    /**
     * Issue #720: JXTree - selection lost on release the searchable.
     * 
     * assert JXList behaviour.
     * Note: JXList not clearing the selection has been an accidental, undocumented
     * side-effect of DefaultListSelectionModel.setSelectionInterval - it backs
     * out silently on negative indices. 
     */
    public void testKeepSelectionOnNotFoundList() {
        JXList list = new JXList(new TestListModel());
        PatternModel model = new PatternModel();
        model.setRawText("one9");
        int row = list.getSearchable().search(model.getPattern());
        // sanity: found at expected row
        assertEquals(90, row);
        // sanity: selection is match marker 
        assertEquals(row, list.getSelectedIndex());
        list.getSearchable().search((Pattern) null);
        assertEquals("not found must not reset selection", row, list.getSelectedIndex());
    }

    /**
     * Issue #720: JXTree - selection lost on release the searchable.
     * 
     * assert JXTable behaviour.
     */
    public void testKeepSelectionOnNotFoundTable() {
        JXTable table = new JXTable(new TestTableModel());
        PatternModel model = new PatternModel();
        model.setRawText("one9");
        int row = table.getSearchable().search(model.getPattern());
        // sanity: found at expected row
        assertEquals(9, row);
        // sanity: selection is match marker 
        assertEquals(row, table.getSelectedRow());
        table.getSearchable().search((Pattern) null);
        assertEquals("not found must not reset selection", row, table.getSelectedRow());
    }

    /**
     * Issue #720: JXTree - selection lost on release the searchable.
     * 
     * Base problem was a slight difference in the tree- vs. ListSelectionModel 
     * (not completely documented). The TreeSelection
     * interprets a null path array as clearSelection, same for null path in 
     * setSelectionPaths and setSelectionPath. A null element in the array is
     * undocumented, but setPaths protects itself and interprets an array containing
     * null paths only as clearSelection as well.
     * 
     */
    public void testKeepSelectionOnNotFoundTree() {
        JXTree tree = new JXTree();
        PatternModel model = new PatternModel();
        model.setRawText("foo");
        int row = tree.getSearchable().search(model.getPattern());
        // sanity: found at expected row
        assertEquals(3, row);
        // sanity: selection is match marker 
        assertEquals(row, tree.getMinSelectionRow());
        tree.getSearchable().search((Pattern) null);
        assertEquals("not found must not reset selection", row, tree.getMinSelectionRow());
    }
    
    /**
     * Issue #487-swingx: NPE if instantiating with not null Searchable
     */
    @SuppressWarnings("unused")
    public void testFindBarNPEConstructor() {
        JXFindBar findBar = new JXFindBar(new JXTable().getSearchable());
    }
    /**
     * Issue #487-swingx: NPE if setting a not-null Searchable before 
     * showing
     */
    public void testFindBarNPE() {
        Searchable searchable = new JXTable().getSearchable();
        JXFindBar findBar = new JXFindBar();
        findBar.setSearchable(searchable);
    }
    

    /**
     * Issue #374-swingx: default search keystroke never found.
     * 
     * This was probably introduced when fixing #353-swingx: 
     * swingx must not try to leave sandbox.
     * 
     */
    public void testSearchKeyStroke() {
        if (System.getSecurityManager() != null) {
            LOG.info("cannot run testSearchKeyStroke - SecurityManager installed");
            return;
        }
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run testSearchKeyStroke - headless environment");
            return;
        }
        assertNotNull("searchfactory must return search accelerator", 
                SearchFactory.getInstance().getSearchAccelerator());
    }

    
    public void testWrapFindBar() {
        JXFindBar findBar = new JXFindBar();
        assertTrue("findbar must auto-wrap", findBar.getPatternModel().isWrapping());
    }
    
    
    

    /** 
     * Test SearchHighlight used in incremental search of JXTable.  
     *
     */
    public void testTableIncrementalHighlighter() {
        JXTable table = new JXTable(new TestTableModel());
        table.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER, Boolean.TRUE);
        int row = 3;
        int column = 1;
        String firstSearchText = "wo" + row;
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        // make sure we had a match
        int foundIndex = table.getSearchable().search(model.getPattern(), -1);
        assertEquals("must return be found", row, foundIndex);
        Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
        assertEquals(Color.YELLOW.brighter(), comp.getBackground());
    }


    /** 
     * test incremental search in JXTable.
     *
     */
    public void testTableIncremental() {
        JXTable table = new JXTable(new TestTableModel());
        String firstSearchText = "on";
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        // make sure we had a match
        int foundIndex = table.getSearchable().search(model.getPattern(), -1);
        assertEquals("must return be found", 0, foundIndex);
        // extended searchstring
        String secondSearchText ="one";
        model.setRawText(secondSearchText);
        // start search with row >> getRowCount()
        int secondFoundIndex = table.getSearchable().search(model.getPattern(), foundIndex);
        assertEquals("must not be found", foundIndex, secondFoundIndex);
        
    }

    /** 
     * test if starting a search in a non-contained index results
     * in not-found state.
     *
     */
    public void testTableInvalidStartIndex() {
        JXTable table = new JXTable(new TestTableModel());
        String firstSearchText = "one";
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        // make sure we had a match
        int foundIndex = table.getSearchable().search(model.getPattern(), -1);
        assertEquals("must return be found", 0, foundIndex);
        // start search with row >> getRowCount()
        int notFoundIndex = table.getSearchable().search(model.getPattern(), table.getRowCount() * 5, false);
        assertEquals("must not be found", -1, notFoundIndex);
        
    }
    /**
     * test if match in same row but different column is found in backwards
     * search.
     *
     */
    public void testTableFoundPreviousColumnInSameRow() {
        JXTable table = new JXTable(new TestTableModel());
        int lastColumn = table.getColumnCount() -1;
        int row = 90;
        String firstSearchText = table.getValueAt(row, lastColumn).toString();
        // need a pattern for backwards search
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        int foundIndex = table.getSearchable().search(model.getPattern(), -1, true);
        assertEquals("last line found", row, foundIndex);
        String secondSearchText = table.getValueAt(row, lastColumn - 1).toString();
        model.setRawText(secondSearchText);
        int secondFoundIndex = table.getSearchable().search(model.getPattern(), foundIndex, true);
        assertEquals("must find match in same row", foundIndex, secondFoundIndex);
    }

    
    /**
     * check if not-wrapping returns not found marker (-1).
     *
     */
    public void testTableNotFoundWithoutWrapping() {
        JXTable table = new JXTable(new TestTableModel());
        int row = 90;
        String searchText = table.getValueAt(row, 0).toString();
        int foundIndex = table.getSearchable().search(searchText);
        assertEquals("last line found", row, foundIndex);
        int notFoundIndex = table.getSearchable().search(searchText, foundIndex);
        assertEquals("nothing found after last line", -1, notFoundIndex);
    }
    
    /**
     * test if not-wrapping returns not found marker (-1) if the
     * last found position is the last row.
     *
     */
    public void testTableNotFoundLastRowWithoutWrapping() {
        JXTable table = new JXTable(new TestTableModel());
        int row = table.getRowCount() - 1;
        String searchText = table.getValueAt(row, 0).toString();
        int foundIndex = table.getSearchable().search(searchText);
        assertEquals("last line found", row, foundIndex);
        int notFoundIndex = table.getSearchable().search(searchText, foundIndex);
        assertEquals("nothing found after last line", -1, notFoundIndex);
    }

    /**
     * test if not-wrapping returns not found marker (-1) if the
     * last found position is the first row.
     *
     */
    public void testTableNotFoundFirstRowWithoutWrapping() {
        JXTable table = new JXTable(new TestTableModel());
        int row = 0;
        PatternModel model = new PatternModel();
        String searchText = table.getValueAt(row, 0).toString();
        model.setRawText(searchText);
        int foundIndex = table.getSearchable().search(model.getPattern(), row + 1, true);
        assertEquals("last line found", row, foundIndex);
        int notFoundIndex = table.getSearchable().search(model.getPattern(), foundIndex, true);
        assertEquals("nothing found after last line", -1, notFoundIndex);
    }


    /**
     * test if internal state is reset when search triggered through 
     * methods taking a string. 
     * 
     *
     */
    public void testEditorResetStateOnEmptySearchString() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        // initialize to found state
        int foundIndex = editor.getSearchable().search("ou");
        // sanity
        assertEquals(1, foundIndex);
        // search with null searchString
        int notFoundIndex = editor.getSearchable().search("", foundIndex);
        // sanity: nothing found
        assertEquals(-1, notFoundIndex);
        assertEquals(notFoundIndex, ((DocumentSearchable) editor.getSearchable()).lastFoundIndex);
    }

    /**
     * test if internal state is reset when search triggered through 
     * methods taking a string. 
     * 
     *
     */
    public void testEditorResetStateOnNullSearchString() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        // initialize to found state
        int foundIndex = editor.getSearchable().search("ou");
        // sanity
        assertEquals(1, foundIndex);
        // search with null searchString
        int notFoundIndex = editor.getSearchable().search((String) null, foundIndex);
        // sanity: nothing found
        assertEquals(-1, notFoundIndex);
        assertEquals(notFoundIndex, ((DocumentSearchable) editor.getSearchable()).lastFoundIndex);
    }
    /**
     * test if internal state is reset when search triggered through 
     * methods taking a string. 
     * 
     *
     */
    public void testEditorResetStateOnNullPattern() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        // initialize to found state
        int foundIndex = editor.getSearchable().search("ou");
        // sanity
        assertEquals(1, foundIndex);
        // search with null searchString
        int notFoundIndex = editor.getSearchable().search((Pattern) null, foundIndex);
        // sanity: nothing found
        assertEquals(-1, notFoundIndex);
        assertEquals(notFoundIndex, ((DocumentSearchable) editor.getSearchable()).lastFoundIndex);
    }
    /**
     * Issue #??-swingx: backwards search not implemented in JXEditorPane.
     *
     */
    public void testEditorBackwards() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        int first = 2;
        try {
            editor.getDocument().getText(first, editor.getDocument().getLength() - first);
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PatternModel model = new PatternModel();
        model.setRawText("fo");
        int foIndex = editor.getSearchable().search(model.getPattern(), text.length() - 1, true);
        assertEquals("found index must be last occurence", text.lastIndexOf("fo"), foIndex);
        
    }
    
    /**
     * Test incremental searching: index must be the same if
     * extended text still matches.
     *
     */
    public void testEditorIncremental() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        String search = "fo";
        int first = editor.getSearchable().search(search, 0);
        assertEquals(0, first);
        String searchExt = search + "u";
        int second = editor.getSearchable().search(searchExt, first);
        assertEquals("index must be same if extension matches", first, second);
    }
    
    
    /**
     * NPE on no-match?
     */
    public void testEditorIncrementalNotFound() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        String search = "ou";
        int first = editor.getSearchable().search(search, 0);
        assertEquals(1, first);
        String searchExt = search + "u";
        try {
            int second = editor.getSearchable().search(searchExt, first);
            assertEquals("not found", -1, second);
            
        } catch (NullPointerException npe) {
            fail("npe");
        }
    }

    /**
     * test that search moves forward.
     *
     */
    public void testEditorFindNext() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        String search = "fou";
        int first = editor.getSearchable().search(search, -1);
        assertEquals(0, first);
        int second = editor.getSearchable().search(search, first);
        assertEquals(4, second);
    }
    /**
     * Testing graceful handling of start index out-of range
     * of document size.
     *
     */
    public void testEditorTolerateExceedingStartIndex() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("fou four");
        try {
            int foIndex = editor.getSearchable().search("fo", 20);
            assertEquals(-1, foIndex);
            
        } catch (Exception ex) {
            fail("search must not throw if index out off range");
        }
    }

    public void testEditorEmptyDocument() {
        JXEditorPane editor = new JXEditorPane();
        int foIndex = editor.getSearchable().search("fo", -1);
        assertEquals(-1, foIndex);
        foIndex = editor.getSearchable().search("fo", 0);
        assertEquals(-1, foIndex);
    }
    
    /**
     * testing incremental search: 
     * must start search at given position (inclusive).
     * 
     * This implies that search(xx, -1) is equivalent to 
     * search(xx, 0) if the match is at position 0.
     *
     */
    public void testEditorBoundarySearchIndex() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("f");
       // can't test in one method - the searchable has internal state
        int startOff = editor.getSearchable().search("f", -1);
        assertEquals("must return first occurence if startIndex if off", 0, startOff);
        // sanity - must not find mismatch if longer
        int foIndex = editor.getSearchable().search("fo", -1);
        assertEquals("must not find exceeding text", -1, foIndex);
        foIndex = editor.getSearchable().search("f", 0);
        assertEquals("must return first occurence from startIndex inclusively",0 , foIndex);
    }
    
    /**
     * Issue #100-swingx: expect to return the start of the match.
     * Only then it's possible to implement a reasonably behaved
     * incremental search.
     *
     */
    public void testEditorFindMatchPosition() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("fou four");
        int foIndex = editor.getSearchable().search("fo", -1);
        assertEquals(0, foIndex);
    }
    
    /**
     * testing Searchable assumption along the lines of: 
     * found = searchable.search(text)
     * searchable.getValueAt(found).startsWith(text) or
     * searchable.getValueAt(found).contains(text)
     * 
     */
    public void testEditorFindMatch() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("fou four");
        int foIndex = editor.getSearchable().search("fo", -1);
        assertEquals("selected text must be equals to input", "fo", editor.getSelectedText());
        try {
            String textAt = editor.getText(foIndex, 2);
            assertEquals("fo", textAt);
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    public void testEditor() {
        URL url = FindTest.class.getResource("resources/test.txt");
        try {
            JXEditorPane editor = new JXEditorPane(url);

            // There are 9 instances of "four" in the test document
            int useIndex = -1;
            int lastIndex = -1;
            for (int i = 0; i < 9; i++) {
                lastIndex = editor.getSearchable().search("four", useIndex);
                assertTrue(lastIndex != -1);
                assertTrue(lastIndex != useIndex);

                assertEquals("Error text selection is incorrect", "four", editor.getSelectedText());

                useIndex = lastIndex;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error finding resource for JXEditorPane", ex);
        }
    }

    public void testTable() {
        JXTable table = new JXTable(new TestTableModel());
        // There are 100 instances of "One" in the test document
        int useIndex = -1;
        int lastIndex = -1;
        for (int i = 0; i < 100; i++) {
            lastIndex = table.getSearchable().search("One", useIndex);
            assertTrue(lastIndex != -1);
            assertTrue(lastIndex != useIndex);

            assertEquals("Row not selected", lastIndex, table.getSelectedRow());
            assertEquals("Column not selected", 0, table.getSelectedColumn());

            String value = (String)table.getValueAt(table.getSelectedRow(),
                                                    table.getSelectedColumn());
            assertTrue(value.startsWith("One"));

            useIndex = lastIndex;
        }
    }

    public void testNullSearchable() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        JXFindPanel find = new JXFindPanel();
        find.match();
    }

    
    public static class TestTableModel extends AbstractTableModel {

        public static String[] data = { "One", "Two", "Three",
                                         "Four", "Five" };

        public int getRowCount() { return 100; }
        public int getColumnCount() { return data.length; }

        public Object getValueAt(int row, int column) {
            checkCoordinates(row, column);
            StringBuffer buffer = new StringBuffer(data[column]);
            buffer.append(row);
            return buffer.toString();
        }
        private void checkCoordinates(int row, int column) {
            if ((row < 0) || (row >= getRowCount())
                    || (column < 0) || (column >= getColumnCount()))
                throw new IllegalArgumentException("coordinates invalid - row/column: " 
                        + row + "/" + column);
                    
            
        }
    }

    public static class TestListModel extends AbstractListModel {

        public int getSize() {
            return 100;
        }

        public Object getElementAt(int index) {
            int dataPos = index % TestTableModel.data.length;
            return TestTableModel.data[dataPos] + index;
        }
        
    }
    /**
     * A small class that implements the Searchable interface.
     */
    public static class TestSearchable extends JLabel implements Searchable {

        private boolean succeed;

        public TestSearchable() {
            this(false);
        }

        /**
         * @param succeed flag to indicate that all searches succeed.
         */
        public TestSearchable(boolean succeed) {
            this.succeed = succeed;
        }

        public int search(String searchString) {
            return search(searchString, -1);
        }
        
        public int search(String searchString, int startIndex) {
            return succeed ? 100 : -1;
        }

        public int search(String searchString, int startIndex, boolean backward) {
            return succeed ? 100 : -1;
        }
        public int search(Pattern pattern) {
            return search(pattern, -1);
        }

        public int search(Pattern pattern, int startIndex) {
            return succeed ? 100 : -1;
        }

        public int search(Pattern pattern, int startIndex, boolean backwards) {
            return succeed ? 100 : -1;
        }

    }

    
}
