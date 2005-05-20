/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.jdesktop.swingx.util.Link;

/**
 * @author Jeanette Winzenburg
 */
public class JXHyperlinkTest extends InteractiveTestCase {

    public JXHyperlinkTest() {
        super("JXHyperlink Test");
    }
    public void interactiveTestUnderline() {
        Action action = new AbstractAction("Link@somewhere") {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        JXHyperlink hyperlink = new JXHyperlink(action );
        JFrame frame = wrapInFrame(hyperlink, "simple show label link");
        frame.setSize(200, 200);
        frame.setVisible(true);
        
    }
    
    public void interactiveTestUnderlineButton() {
        Action action = new AbstractAction("Link@somewhere") {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        JXHyperlinkButton hyperlink = new JXHyperlinkButton(action );
        JFrame frame = wrapInFrame(hyperlink, "simple show button link");
        frame.setSize(200, 200);
        frame.setVisible(true);
        
    }
    
 
    public void interactiveTestLink() throws Exception {
        final JXEditorPane editorPane = new JXEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        Link link = new Link("Click me!", null, new URL("https://swingx.dev.java.net"));
        Action action = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof Link) {
                    Link link = (Link) e.getSource();
                    try {
                        editorPane.setPage(link.getURL());
                        link.setVisited(true);
                    } catch (IOException e1) {
                        editorPane.setText("<html>Error 404: couldn't show " + link.getURL() + " </html>");
                    }
                }
                
            }
            
        };

        LinkAction linkAction = new LinkAction(link);
        linkAction.setDelegateAction(action);
        JXHyperlinkButton hyperlink = new JXHyperlinkButton(linkAction);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(editorPane));
        panel.add(hyperlink, BorderLayout.SOUTH);
        JFrame frame = wrapInFrame(panel, "simple show button link");
        frame.setSize(200, 200);
        frame.setVisible(true);
        
    }

    public static class LinkAction extends AbstractAction {
        
        private Link link;
        private Action delegate;
        private static final String VISIT_ACTION = "visit";
        private static final String VISITED_PROPERTY = Link.VISITED_PROPERTY;
        public LinkAction(Link link) {
            this.link = link;
            installLinkListener();
            putValue(Action.NAME, link.getText());
            putValue(Action.SHORT_DESCRIPTION, link.toString());
            putValue(VISITED_PROPERTY, new Boolean(link.getVisited()));
            

        }

        private void installLinkListener() {
            PropertyChangeListener l = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    putValue(evt.getPropertyName(), evt.getNewValue());
                    
                }
                
            };
            
        }

        public void setDelegateAction(Action delegate) {
            this.delegate = delegate;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (delegate != null) {
                delegate.actionPerformed(new ActionEvent(link, ActionEvent.ACTION_PERFORMED, VISIT_ACTION));
            }
            
        }
        
    }
    
    
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JXHyperlinkTest test = new JXHyperlinkTest();
        try {
            test.runInteractiveTests();
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 
    }
}
