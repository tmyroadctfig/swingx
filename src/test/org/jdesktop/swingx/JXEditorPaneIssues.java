/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;

import org.jdesktop.swingx.action.ActionFactory;
import org.jdesktop.swingx.action.ActionManager;
import org.jdesktop.swingx.action.TargetManager;

/**
 * A unit test for the JXEditorPane
 *
 * @author Mark Davidson
 */
public class JXEditorPaneIssues extends JXEditorPaneTest {
    private static final Logger LOG = Logger.getLogger(JXEditorPaneIssues.class
            .getName());
    private static String testText = "This is an example of some text";


    public void testEditorActionManagerWoes() {
        JXEditorPane editable = new JXEditorPane();
        assertTrue(editable.isEditable());
        ActionManager.getInstance().addAction(ActionFactory.createTargetableAction("undo", "Undo", "U"));
        TargetManager.getInstance().addTarget(editable);
        
    }
    
    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * 
     * undo must not perform on non-editable editor.
     * 
     */
    public void testXUndoActionNotEditable() {
        JXEditorPane editor = new JXEditorPane();
        editor.setEditable(false);
        editor.setText(testText);
        assertEquals(testText, editor.getText());
        Action action = editor.getActionMap().get("undo");
        action.actionPerformed(null);
        assertEquals("undo must have no effect", testText, editor.getText());
    }
 
    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * undo is enabled if there is selected text and editor editable
     * disabled otherwise.
     * 
     */
    public void testXDisabledUndoAction() {
        JXEditorPane editor = new JXEditorPane();
        editor.setEditable(false);
        editor.setText(testText);
        assertEquals(testText, editor.getText());
        Action action = editor.getActionMap().get("undo");
        assertFalse("undo must not be enabled", action.isEnabled());
        ActionManager.getInstance().addAction(ActionFactory.createTargetableAction("undo", "Undo", "U"));
        TargetManager.getInstance().addTarget(editor);
    }

}
