/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.action.TargetableAction;

public class FindTest extends InteractiveTestCase {

    public static void main(String args[]) {
//      setSystemLF(true);
      FindTest test = new FindTest();
      try {
        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Editor.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    @Override
    protected void setUp() {
        editorURL = FindTest.class.getResource("resources/test.txt");
        SearchFactory.getInstance().setShowFindInToolBar(true);
    }
    

    private URL editorURL;

    public FindTest() {
        super("Find Action Test");
    }

    public void testCreate() {
        // JXFindDialog dialog = new JXFindDialog(null);
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
//        JXFindDialog dialog = new JXFindDialog();
//        dialog.doFind();
    }

    public void interactiveShowDialog() {
        SearchFactory.getInstance().showFindInput(null, new TestSearchable());
    }

    public void interactiveShowTable() {
        showComponent(new JXTable(new TestTableModel()));
    }

    public void interactiveShowSplitPane() {
       showComponent(createEditor(), new JXTable(new TestTableModel()));
    }
    
    public void interactiveShowEditor() {
        showComponent(createEditor());
    }

    /**
     * @return
     * @throws IOException
     */
    private JXEditorPane createEditor()  {
        try {
            return new JXEditorPane(editorURL);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public void showComponent(JComponent component, JComponent second) {
        
        JXFrame frame;
        if (second != null) {
          frame = wrapWithScrollingInFrame(component, second, "Find");
        } else {
            frame= wrapWithScrollingInFrame(component, "Find");
        }
        
        Action action = new TargetableAction("Find", "find");
        addAction(frame, action);
        frame.setSize(600, 400);
        frame.setVisible(true);
        
    }
    public void showComponent(JComponent component) {
        showComponent(component, null);
        // Must add a menu bar so that the Ctrl-F will work.
//        JMenuBar menubar = new JMenuBar();
//        JMenu menu = menubar.add(new JMenu("File"));
//        menu.add(new JMenuItem(action));
//
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.add(new JScrollPane(component), BorderLayout.CENTER);
//
//        JXFrame frame = new JXFrame("Find in JXEditorPane");
//        frame.setJMenuBar(menubar);
//        frame.getRootPaneExt().setToolBar(toolbar);
//        frame.add(panel);
//
//        frame.pack();
//        frame.setVisible(true);
    }

    
    
    public static class TestTableModel extends AbstractTableModel {

        private static String[] data = { "One", "Two", "Three",
                                         "Four", "Five" };

        public int getRowCount() { return 100; }
        public int getColumnCount() { return data.length; }

        public Object getValueAt(int row, int column) {
            StringBuffer buffer = new StringBuffer(data[column]);
            buffer.append(row);
            return buffer.toString();
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
