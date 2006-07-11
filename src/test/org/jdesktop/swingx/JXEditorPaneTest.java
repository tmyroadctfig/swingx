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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.JToolBar;
import javax.swing.text.html.HTMLDocument;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.action.ActionFactory;
import org.jdesktop.swingx.action.ActionManager;

/**
 * A unit test for the JXEditorPane
 *
 * @author Mark Davidson
 */
public class JXEditorPaneTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXEditorPaneTest.class
            .getName());
    private static String testText = "This is an example of some text";

    public static void main(String[] args) throws Exception {
//      setSystemLF(true);
      JXEditorPaneTest test = new JXEditorPaneTest();
      try {
          test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Table.*");
//          test.runInteractiveTests("interactive.*List.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
  }

    private static  boolean DEBUG = false;

    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * Can of worms? super does nothing to enable/disable default actions?
     * TransferHandler's actions are mixed in?
     * 
     */
    public void testXDisabledActionsOnNotEditable() {
        JXEditorPane editor = new JXEditorPane();
        editor.setEditable(false);
        Action action = editor.getActionMap().get("paste");
        LOG.info("enabled " + action.isEnabled());
        
    }
    
    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * Can of worms? super does nothing to enable/disable default actions?
     * TransferHandler's actions are mixed in?
     * 
     */
    public void testDisabledActionsOnNotEditable() {
        JEditorPane editor = new JEditorPane();
        editor.setEditable(false);
        Action action = editor.getActionMap().get("paste");
        LOG.info("enabled " + action.isEnabled());
        
    }
    
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

    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * checking action enabled behaviour of core editorpane.
     * Doing nothing to enable/disable depending on editable state?
     *
     */
    public void interactiveXEditorDefaultActions() {
        JXEditorPane editor = new JXEditorPane();
        Action[] actions = editor.getActions();
        ActionManager manager = ActionManager.getInstance();
        List actionNames = new ArrayList();
        StringBuffer buffer = new StringBuffer("No. of default actions: " + actions.length);
        ActionMap map = editor.getActionMap();
        Object[] keys = map.keys();
        int count = keys != null ? keys.length : 0;
        buffer.append("\n No. of actions in ActionMap: " + count);
        for (int i = 0; i < actions.length; i++) {
            Object id = actions[i].getValue(Action.NAME);
            manager.addAction(id, actions[i]);
            actionNames.add(id);
            buffer.append("\n" + actions[i].toString());
        }
        
        
        editor.setText(buffer.toString());
        ActionContainerFactory factory = new ActionContainerFactory(manager);

      JToolBar toolbar = factory.createToolBar(actionNames);
      toolbar.setOrientation(JToolBar.VERTICAL);
      editor.setEditable(false);
      editor.setPreferredSize(new Dimension(600, 400));

      JXFrame frame = wrapWithScrollingInFrame(editor, "Looking at swingx editor default actions");
      frame.getContentPane().add(toolbar, BorderLayout.WEST);
      frame.setVisible(true);
    }

    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * checking action enabled behaviour of core editorpane.
     * Doing nothing to enable/disable depending on editable state?
     *
     */
    public void interactiveEditorDefaultActions() {
        JEditorPane editor = new JEditorPane();
        editor.setText(testText);
        Action[] actions = editor.getActions();
        ActionManager manager = ActionManager.getInstance();
        List actionNames = new ArrayList();
        StringBuffer buffer = new StringBuffer("No. of default actions: " + actions.length);
        ActionMap map = editor.getActionMap();
        Object[] keys = map.keys();
        int count = keys != null ? keys.length : 0;
        buffer.append("\n No. of actions in ActionMap: " + count);
        for (int i = 0; i < actions.length; i++) {
            Object id = actions[i].getValue(Action.NAME);
            manager.addAction(id, actions[i]);
            actionNames.add(id);
            buffer.append("\n" + actions[i].toString());
        }
        editor.setText(buffer.toString());
        ActionContainerFactory factory = new ActionContainerFactory(manager);

      JToolBar toolbar = factory.createToolBar(actionNames);
      toolbar.setOrientation(JToolBar.VERTICAL);
      editor.setEditable(false);
      editor.setPreferredSize(new Dimension(600, 400));

      JXFrame frame = wrapWithScrollingInFrame(editor, "Looking at core default actions");
      frame.getContentPane().add(toolbar, BorderLayout.WEST);
      frame.setVisible(true);
    }
    /**
     * JW: this is oold - no idea if that's the way to handle actions!.
     *
     */
    public void interactiveEditorActions() {
        AbstractActionExt[] actions = new AbstractActionExt[14];

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
        List actionNames = new ArrayList();
        for (int i = 0; i < actions.length; i++) {
            manager.addAction(actions[i]);
            actionNames.add(actions[i].getActionCommand());
        }
        
        // Populate the toolbar. Must use the ActionContainerFactory to ensure
        // that toggle actions are supported.
        ActionContainerFactory factory = new ActionContainerFactory(manager);

//        JToolBar toolbar = new JToolBar();
//        for (int i = 0; i < actions.length; i++) {
//            manager.addAction(actions[i]);
//            toolbar.add(factory.createButton(actions[i]));
//        }

        JToolBar toolbar = factory.createToolBar(actionNames);
        
        URL url = JXEditorPaneTest.class.getResource("resources/test.html");
        JXEditorPane editor = null;
        try {
            editor = new JXEditorPane(url);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(600, 400));

//        toolbar.add(editor.getParagraphSelector());

        JXFrame frame = wrapWithScrollingInFrame(editor, "Editor tester");
        frame.getContentPane().add(toolbar, BorderLayout.NORTH);

        frame.pack();
        frame.setVisible(true);
    }
}
