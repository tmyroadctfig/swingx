/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

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
                        link.setVisited(true);
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
        private PropertyChangeListener linkListener;
        
        public LinkAction(Link link) {
            setLink(link);
        }

        public void setLink(Link link) {
            uninstallLinkListener();
            this.link = link;
            installLinkListener();
            updateFromLink();
        }

        private void uninstallLinkListener() {
            if (link == null) return;
            link.removePropertyChangeListener(getLinkListener());
         
        }

        private void updateFromLink() {
            if (link != null) {
                putValue(Action.NAME, link.getText());
                putValue(Action.SHORT_DESCRIPTION, link.getURL().toString());
                putValue(VISITED_PROPERTY, new Boolean(link.getVisited()));
            } else {
                Object[] keys = getKeys();
                if (keys == null) return;
                for (int i = 0; i < keys.length; i++) {
                   putValue(keys[i].toString(), null); 
                }
            }
        }

        private void installLinkListener() {
            if (link == null) return;
            link.addPropertyChangeListener(getLinkListener());
        }

        private PropertyChangeListener getLinkListener() {
            if (linkListener == null) {
             linkListener = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    updateFromLink();
                }
                
            };
            }
            return linkListener;
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
  
    public void interactiveTestLinkRenderer() {
        JXTable table = new JXTable(createModelWithLinks());
        table.setDefaultRenderer(Link.class, new LinkRenderer());
        RolloverListener linkHandler = new RolloverListener();
        table.addMouseListener(linkHandler);
        table.addMouseMotionListener(linkHandler);
        table.addPropertyChangeListener(new LinkController());
        JFrame frame = wrapWithScrollingInFrame(table, "show link renderer");
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
                    link.setVisited(true);
                }
                model.addRow(new Object[] {"text only " + i, link, new Integer(i) });
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return model;
    }

    public static class LinkController implements PropertyChangeListener {

        private Cursor oldCursor;
        public void propertyChange(PropertyChangeEvent evt) {
            if (RolloverListener.ROLLOVER_KEY.equals(evt.getPropertyName())) {
                if (evt.getSource() instanceof JTable) {
                    rollover((JTable) evt.getSource(), (Point) evt
                            .getOldValue(), (Point) evt.getNewValue());
                }
            } else if (RolloverListener.CLICKED_KEY.equals(evt.getPropertyName())) {
                if (evt.getSource() instanceof JTable) {
                    click((JTable) evt.getSource(), (Point) evt.getOldValue(),
                            (Point) evt.getNewValue());
                }
            }
        }

        private void click(JTable table, Point oldLocation, Point newLocation) {
            if (isLinkColumn(table, newLocation)) {
                Link link = (Link) table.getValueAt(newLocation.y, newLocation.x);
                if (link != null) {
                    // ARRRGGHH...
                    link.setVisited(true);
                    table.setValueAt(link, newLocation.y, newLocation.x);
                }
            }
            
        }

        private void rollover(JTable table, Point oldLocation, Point newLocation) {
            if (oldLocation != null) {
                table.repaint(table.getCellRect(oldLocation.y, oldLocation.x, false));
            }
            if (newLocation != null) {
                table.repaint(table.getCellRect(newLocation.y, newLocation.x, false));
            }
            setLinkCursor(table, newLocation);
//            table.repaint();
        }
 
        private void setLinkCursor(JTable table, Point location) {
            if (isLinkColumn(table, location)) {
                if (oldCursor == null) {
                    oldCursor = table.getCursor();
                    table.setCursor(Cursor
                            .getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            } else {
                if (oldCursor != null) {
                    table.setCursor(oldCursor);
                    oldCursor = null;
                }
            }

        }
        private boolean isLinkColumn(JTable table, Point location) {
            // JW: Quickfix - the index might be -1 if 
            // hitting outside of the columns
            if (location == null || location.x < 0) return false;
            return (table.getColumnClass(location.x) == Link.class);
        }
        
    }
    public static class LinkRenderer implements TableCellRenderer {
        private JXHyperlinkButton linkButton;
        private LinkAction linkAction;
        
        public LinkRenderer() {
            linkAction = new LinkAction(null);
            linkButton = new JXHyperlinkButton(linkAction);
            
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            linkAction.setLink(value instanceof Link ? (Link) value : null);
            Point p = (Point) table.getClientProperty(RolloverListener.ROLLOVER_KEY);
            if (p != null && (p.x > 0) && (p.x == column) && (p.y == row)) {
                //JW: toggling model's rollover state is unreliable - hmmm...
//                linkButton.getModel().setRollover(true);
              linkButton.entered(true);
            } else {
//                linkButton.getModel().setRollover(false);
                linkButton.exited(true);
            }
            return linkButton;
        }
        
    }
    
    public static void main(String[] args) throws Exception {
    //    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JXHyperlinkTest test = new JXHyperlinkTest();
        try {
         //   test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Rend.*");
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 
    }
}
