/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.action.EditorPaneLinkVisitor;
import org.jdesktop.swingx.action.LinkAction;

/**
 * @author Jeanette Winzenburg
 */
public class JXHyperlinkTest extends InteractiveTestCase {

    public JXHyperlinkTest() {
        super("JXHyperlinkLabel Test");
    }

    public void testDummy() {
        
    }
    
    public void interactiveTestUnderlineButton() {
        Action action = new AbstractAction("LinkModel@somewhere") {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        JXHyperlink hyperlink = new JXHyperlink(action );
        JFrame frame = wrapInFrame(hyperlink, "show underline - no link action");
        frame.setSize(200, 200);
        frame.setVisible(true);
        
    }
    
 
    public void interactiveTestLink() throws Exception {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        LinkModel link = new LinkModel("Click me!", null, JXEditorPaneTest.class.getResource("resources/test.html"));

        LinkAction linkAction = new LinkAction(link);
        linkAction.setVisitingDelegate(visitor);
        JXHyperlink hyperlink = new JXHyperlink(linkAction);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(visitor.getOutputComponent()));
        panel.add(hyperlink, BorderLayout.SOUTH);
        JFrame frame = wrapInFrame(panel, "simple hyperlink");
        frame.setSize(200, 200);
        frame.setVisible(true);
        
    }

  
    public void interactiveTestTableLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        table.setRolloverEnabled(true);

        LinkRenderer editor = (LinkRenderer) table.getDefaultEditor(LinkModel.class);
        editor.setVisitingDelegate(visitor);
        JFrame frame = wrapWithScrollingInFrame(table, visitor.getOutputComponent(), "show link renderer in table");
        frame.setVisible(true);

    }
    
    public void interactiveTestListLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXList list = new JXList(createListModelWithLinks());
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setLinkVisitor(visitor);
        JFrame frame = wrapWithScrollingInFrame(list, visitor.getOutputComponent(), "show link renderer in list");
        frame.setVisible(true);

    }
    
    
    private ListModel createListModelWithLinks() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 20; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a link text " + i, null, url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }
    private TableModel createModelWithLinks() {
        DefaultTableModel model = new DefaultTableModel(0, 3) {
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        for (int i = 0; i < 4; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a link text " + i, null, url);
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
     //   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
