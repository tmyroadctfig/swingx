/*
 * Created on 07.06.2005
 *
 */
package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.text.Document;

import org.jdesktop.swingx.JXEditorPane;
import org.jdesktop.swingx.util.Link;

/**
 * A ActionListener using a JXEditorPane to "visit" a Link.
 * 
 * @author Jeanette Winzenburg
 */
public class EditorPaneLinkVisitor implements ActionListener {
    private JXEditorPane editorPane;
    
    public EditorPaneLinkVisitor() {
        this(null);
    }
    
    public EditorPaneLinkVisitor(JXEditorPane pane) {
        if (pane == null) {
            pane = createDefaultEditorPane();
        }
        this.editorPane = pane;
    }
    
    public JXEditorPane getOutputComponent() {
        return editorPane;
    }
    
    protected JXEditorPane createDefaultEditorPane() {
        final JXEditorPane editorPane = new JXEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        return editorPane;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Link) {
            Link link = (Link) e.getSource();
            visit(link);
        }
   
    }

    public void visit(Link link) {
        try {
            // make sure to reload
            editorPane.getDocument().putProperty(Document.StreamDescriptionProperty, null);
            // JW: editorPane defaults to asynchronous loading
            // no need to explicitly start a thread - really?
            editorPane.setPage(link.getURL());
            link.setVisited(true);
        } catch (IOException e1) {
            editorPane.setText("<html>Error 404: couldn't show " + link.getURL() + " </html>");
        }
    }
    
}
