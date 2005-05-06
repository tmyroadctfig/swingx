/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.text.html.HTMLDocument;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXEditorPane;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.action.ActionFactory;
import org.jdesktop.swingx.action.ActionManager;

/**
 * A unit test for the JXEditorPane
 *
 * @author Mark Davidson
 */
public class JXEditorPaneTest extends TestCase {

    private static String testText = "This is an example of some text";

    private static  boolean DEBUG = false;

    public void testInitialization() throws IOException {
        URL url = JXEditorPaneTest.class.getResource("resources/test.html");
        JXEditorPane editor = new JXEditorPane();
        editor.setContentType("text/html");
        editor.setPage(url);

        assertTrue(editor.getDocument() instanceof HTMLDocument);
        assertNotNull(editor.getCaretListener());
        assertNotNull(editor.getUndoableEditListener());

        editor = new JXEditorPane("text/html", "");
        editor.setPage(url);
        assertTrue(editor.getDocument() instanceof HTMLDocument);
        assertNotNull(editor.getCaretListener());
        assertNotNull(editor.getUndoableEditListener());

        editor = new JXEditorPane();
        assertFalse(editor.getDocument() instanceof HTMLDocument);
        assertNull(editor.getCaretListener());
    }

    public void testRegistration() {

    }

    public void testCutPastePlain() {
        JXEditorPane editor = new JXEditorPane("text/plain", testText);
        editorCutPaste(editor);
    }

    /**
     * XXX currently the html cut and paste is broken. A work around has been
     * implemented to use only plain text on the clip. This test will fail
     * if the text contains some markup like &lt;b&gt;foo&lt;/b&gt;
     */
    public void testCutPasteHtml() {
        JXEditorPane editor = new JXEditorPane("text/html", testText);
        editorCutPaste(editor);
    }

    public void editorCutPaste(JEditorPane editor) {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        if (DEBUG) {
            System.out.println("Document: " + editor.getDocument());
        }

        // set selection markers
        editor.select(4, 15);

        String selected = editor.getSelectedText();
        if (DEBUG) {
            System.out.println("Selected: \"" + selected + "\" length: " + selected.length());
        }

        // get cut and paste actions and execute them simultaniously

        ActionMap map = editor.getActionMap();
        Action cut = map.get("cut-to-clipboard");
        Action paste = map.get("paste-from-clipboard");

        assertNotNull(cut);
        assertNotNull(paste);

        String before = editor.getText();

        if (DEBUG) {
            System.out.println("Before cut: " + before);
        }

        cut.actionPerformed(new ActionEvent(editor, 0,
                                            (String)cut.getValue(Action.ACTION_COMMAND_KEY)));

        if (DEBUG) {
            System.out.println("After cut: " + editor.getText());
        }

        // XXX caret position should be moved in the cut operation
        editor.setCaretPosition(4);

        paste.actionPerformed(new ActionEvent(editor, 0,
                                              (String)paste.getValue(Action.ACTION_COMMAND_KEY)));
        if (DEBUG) {
            System.out.println("After paste: " + editor.getText());
        }
        assertEquals(before, editor.getText());
    }

    public static void main(String[] args) throws Exception {
        Action[] actions = new Action[14];

        actions[0] = ActionFactory.createTargetableAction("cut-to-clipboard", "Cut", "C");
        actions[1] = ActionFactory.createTargetableAction("copy-to-clipboard", "Copy", "P");
        actions[2] = ActionFactory.createTargetableAction("paste-from-clipboard", "Paste", "T");

        actions[3] = ActionFactory.createTargetableAction("undo", "Undo", "U");
        actions[4] = ActionFactory.createTargetableAction("redo", "Redo", "R");

        actions[5] = ActionFactory.createTargetableAction("left-justify", "Left", "L", true,
                                                          "position-group");
        actions[6] = ActionFactory.createTargetableAction("center-justify", "Center", "C", true,
                                                          "position-group");
        actions[7] = ActionFactory.createTargetableAction("right-justify", "Right", "R", true,
                                                          "position-group");

        actions[8] = ActionFactory.createTargetableAction("font-bold", "Bold", "B", true);
        actions[9] = ActionFactory.createTargetableAction("font-italic", "Italic", "I", true);
        actions[10] = ActionFactory.createTargetableAction("font-underline", "Underline", "U", true);

        actions[11] = ActionFactory.createTargetableAction("InsertUnorderedList", "UL", "U", true);
        actions[12] = ActionFactory.createTargetableAction("InsertOrderedList", "OL", "O", true);
        actions[13] = ActionFactory.createTargetableAction("InsertHR", "HR", "H");

        // JW: changed on reorg to remove reference to Application 
     //   Application app = Application.getInstance();
        ActionManager manager = ActionManager.getInstance();

        // Populate the toolbar. Must use the ActionContainerFactory to ensure
        // that toggle actions are supported.
        ActionContainerFactory factory = manager.getFactory();

        JToolBar toolbar = new JToolBar();
        for (int i = 0; i < actions.length; i++) {
            manager.addAction(actions[i]);
            toolbar.add(factory.createButton(actions[i]));
        }

        URL url = JXEditorPaneTest.class.getResource("resources/test.html");
        JXEditorPane editor = new JXEditorPane(url);
        editor.setPreferredSize(new Dimension(600, 400));

        toolbar.add(editor.getParagraphSelector());

        JFrame frame = new JFrame("Editor tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(toolbar, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(editor), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }
}
