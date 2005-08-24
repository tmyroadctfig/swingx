/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.net.URL;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.table.AbstractTableModel;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXEditorPane;
import org.jdesktop.swingx.JXFindDialog;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.Searchable;
import org.jdesktop.swingx.action.TargetableAction;

public class FindTest extends TestCase {

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
                lastIndex = editor.search("four", useIndex);
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
            lastIndex = table.search("One", useIndex);
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

    /**
     * Simple test to ensure that flags are set correctly.
     */
    public void testFlags() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        JXFindDialog dialog = new JXFindDialog(new TestSearchable());

        boolean[] states = { true, false, false, true, true };
        for (int i = 0; i < states.length; i++) {
//            dialog.setMatchFlag(states[i]);
//            assertEquals(states[i], dialog.getMatchFlag());
//
//            dialog.setWrapFlag(states[i]);
//            assertEquals(states[i], dialog.getWrapFlag());
//
//            dialog.setBackwardsFlag(states[i]);
//            assertEquals(states[i], dialog.getBackwardsFlag());
        }
    }


    public void testMatchCase() {
    }

    public static void main(String[] args) {
        showDialog();
        showTable();
        showEditor();
        showSplitPane();
    }

    private static void showDialog() {
        JXFindDialog dialog = new JXFindDialog(new TestSearchable());
        dialog.setVisible(true);
    }

    private static void showEditor() {
        try {
            showComponent(new JXEditorPane(FindTest.class.getResource("resources/test.txt")));
        } catch (Exception ex) {
            throw new RuntimeException("Error finding resource for JXEditorPane", ex);
        }
    }

    private static void showTable() {
        showComponent(new JXTable(new TestTableModel()));
    }

    private static void showSplitPane() {
        try {
            showComponent(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                         new JXEditorPane(FindTest.class.getResource("resources/test.txt")),
                                         new JXTable(new TestTableModel())));
        } catch (Exception ex) {
            throw new RuntimeException("Error finding resource for JXEditorPane", ex);
        }
    }

    private static void showComponent(Component component) {
        Action action = new TargetableAction("Find", "find");
        JToolBar toolbar = new JToolBar();
        JButton button = new JButton(action);
        button.setFocusable(false);
        toolbar.add(button);

        // Must add a menu bar so that the Ctrl-F will work.
        JMenuBar menubar = new JMenuBar();
        JMenu menu = menubar.add(new JMenu("File"));
        menu.add(new JMenuItem(action));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(component), BorderLayout.CENTER);

        JFrame frame = new JFrame();
        frame.setJMenuBar(menubar);
        frame.getContentPane().add(panel);

        frame.pack();
        frame.setVisible(true);
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
