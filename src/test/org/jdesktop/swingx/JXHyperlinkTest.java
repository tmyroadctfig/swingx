/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.action.LinkAction;
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
        final JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        Link link = new Link("Click me!", null, JXEditorPaneTest.class.getResource("resources/test.html"));
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
        linkAction.setVisitingDelegate(action);
        JXHyperlinkButton hyperlink = new JXHyperlinkButton(linkAction);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(editorPane));
        panel.add(hyperlink, BorderLayout.SOUTH);
        JFrame frame = wrapInFrame(panel, "simple show button link");
        frame.setSize(200, 200);
        frame.setVisible(true);
        
    }

  
    public void interactiveTestLinkRenderer() {
        final JXEditorPane editorPane = new JXEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        JXTable table = new JXTable(createModelWithLinks()) {
            public boolean editCellAt(int row, int col, EventObject event) {
                boolean editing = super.editCellAt(row, col, event);
                return editing;
            }
        };
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

        LinkRenderer editor = (LinkRenderer) table.getDefaultEditor(Link.class);
        editor.setVisitingDelegate(action);
        JFrame frame = wrapWithScrollingInFrame(table, editorPane, "show link renderer");
        frame.setVisible(true);

    }
    private TableModel createModelWithLinks() {
        DefaultTableModel model = new DefaultTableModel(0, 3) {
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        for (int i = 0; i < 4; i++) {
            try {
                Link link = new Link("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new Link("a link text " + i, null, url);
                }
                model.addRow(new Object[] {"text only " + i, link, new Integer(i) });
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return model;
    }

    public static void main(String[] args) throws Exception {
    //    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JXHyperlinkTest test = new JXHyperlinkTest();
        try {
            test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Rend.*");
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 
    }
}
