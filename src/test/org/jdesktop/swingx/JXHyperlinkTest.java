/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.action.LinkAction;
import org.jdesktop.swingx.action.LinkModelAction;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter.UIAlternateRowHighlighter;
import org.jdesktop.swingx.util.PropertyChangeReport;

/**
 * @author Jeanette Winzenburg
 */
public class JXHyperlinkTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXHyperlinkTest.class
            .getName());
    
    private PropertyChangeReport report;

    public JXHyperlinkTest() {
        super("JXHyperlinkLabel Test");
    }

    public static void main(String[] args) throws Exception {
//      setSystemLF(true);
      JXHyperlinkTest test = new JXHyperlinkTest();
      try {
//          test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Table.*");
          test.runInteractiveTests("interactive.*List.*");
//          test.runInteractiveTests("interactive.*Simple.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
  }
    
    /**
     * test control of the clicked property.
     * 
     * Default behaviour
     * 
     *
     */
    public void testAutoClicked() {
       // no action 
       JXHyperlink hyperlink = new JXHyperlink();
       hyperlink.doClick();
       assertTrue("hyperlink autoClicks if it has no action", hyperlink.isClicked());
       
       LinkAction<Object> emptyAction = createEmptyLinkAction();
       JXHyperlink hyperlink2 = new JXHyperlink(emptyAction);
       hyperlink2.doClick();
       assertFalse(emptyAction.isVisited());
       assertFalse("hyperlink does nothing if has action", hyperlink2.isClicked());
       
       LinkAction emptyAction3 = createEmptyLinkAction();
       JXHyperlink hyperlink3 = new JXHyperlink(emptyAction3);
       hyperlink3.setOverrulesActionOnClick(true);
       hyperlink3.doClick();
       assertFalse(emptyAction.isVisited());
       assertTrue("hyperlink overrules action", hyperlink3.isClicked());
       
    }
    
    public void testOverrulesActionOnClick() {
        JXHyperlink hyperlink = new JXHyperlink();
        assertFalse(hyperlink.getOverrulesActionOnClick());
        hyperlink.addPropertyChangeListener(report);
        hyperlink.setOverrulesActionOnClick(true);
        assertTrue(hyperlink.getOverrulesActionOnClick()); 
        assertEquals(1, report.getEventCount("overrulesActionOnClick"));
    }
    /**
     * sanity (duplicate of LinkActionTest method) to
     * guarantee that hyperlink is updated as expected.
     *
     */
    public void testLinkActionSetTarget() {
        LinkAction<Object> linkAction = createEmptyLinkAction();
        linkAction.setVisited(true);
        JXHyperlink hyperlink = new JXHyperlink(linkAction);
        Object target = new Object();
        linkAction.setTarget(target);
        assertEquals(linkAction.getName(), hyperlink.getText());
        assertFalse(hyperlink.isClicked());
    }
    /**
     * test that hyperlink.setClicked doesn't change action.isVisited();
     *
     */
    public void testSetClickedActionUnchanged() {
        LinkAction<Object> linkAction = createEmptyLinkAction();
        linkAction.setVisited(true);
        JXHyperlink hyperlink = new JXHyperlink(linkAction);
        // sanity assert..
        assertTrue(hyperlink.isClicked());
        hyperlink.setClicked(false);
        // action state must be unchanged;
        assertTrue(linkAction.isVisited());
        
    }
    /**
     * test hyperlink's clicked property.
     *
     */
    public void testClicked() {
        JXHyperlink hyperlink = new JXHyperlink();
        boolean isClicked = hyperlink.isClicked();
        assertFalse(isClicked);
        hyperlink.addPropertyChangeListener(report);
        hyperlink.setClicked(!isClicked);
        assertEquals(1, report.getEventCount("clicked"));
    }
    
    /**
     * JXHyperlink must handle null action gracefully.
     * 
     * Was NPE in configureFromAction
     *
     */
    public void testInitNullAction() {
        JXHyperlink hyperlink = new JXHyperlink();
        assertNull(hyperlink.getAction());
        
    }

    /**
     * JXHyperlink must handle null action gracefully.
     * 
     * Was NPE in configureFromAction
     *
     */
    public void testSetNullAction() {
        LinkAction action = createEmptyLinkAction();
        JXHyperlink hyperlink = new JXHyperlink(action);
        assertEquals("hyperlink action must be equal to linkAction", action, hyperlink.getAction());
        hyperlink.setAction(null);
        assertNull(hyperlink.getAction());
    }
    /**
     * JXHyperlink must handle null action gracefully.
     * 
     * Was NPE in configureFromAction
     *
     */
    public void testSetAction() {
        JXHyperlink hyperlink = new JXHyperlink();
        LinkAction action = createEmptyLinkAction();
        hyperlink.setAction(action);
        assertEquals("hyperlink action must be equal to linkAction", 
                action, hyperlink.getAction());
    }

    /**
     * test that JXHyperlink visited state keeps synched 
     * to LinkAction.
     *
     */
    public void testListeningVisited() {
       LinkAction<Object> linkAction = createEmptyLinkAction();
       JXHyperlink hyperlink = new JXHyperlink(linkAction);
       // sanity: both are expected to be false
       assertEquals(linkAction.isVisited(), hyperlink.isClicked());
       assertFalse(linkAction.isVisited());
       linkAction.setVisited(!linkAction.isVisited());
       assertEquals(linkAction.isVisited(), hyperlink.isClicked());
    }
    
    /**
     * test initial visited state in JXHyperlink is synched to
     * linkAction given in constructor.
     * 
     * There was the usual "init" problem with the constructor.
     * Solved by chaining.
     * 
     */
    public void testInitialVisitedSynched() {
        LinkAction<Object> linkAction = createEmptyLinkAction();
       linkAction.setVisited(true);
       // sanity: linkAction is changed to true
       assertTrue(linkAction.isVisited());
       JXHyperlink hyperlink = new JXHyperlink(linkAction);
       assertEquals(linkAction.isVisited(), hyperlink.isClicked());
    }

    
    
    /**
     * visually check how differently configured buttons behave on
     * clicked.
     *
     */
    public void interactiveCompareClicked() {
        JComponent box = Box.createVerticalBox();
        JXHyperlink noActionHyperlink = new JXHyperlink();
        noActionHyperlink.setText("have no action - auto-click");
        box.add(noActionHyperlink);
        LinkAction doNothingAction = createEmptyLinkAction("have do nothing action - follow action");
        JXHyperlink doNothingActionHyperlink = new JXHyperlink(doNothingAction);
        box.add(doNothingActionHyperlink);
        
        LinkAction doNothingAction2 = createEmptyLinkAction("have do nothing action - overrule");
        JXHyperlink overruleActionHyperlink = new JXHyperlink(doNothingAction2);
        overruleActionHyperlink.setOverrulesActionOnClick(true);
        box.add(overruleActionHyperlink);
        JXFrame frame = wrapInFrame(box, "compare clicked control");
        frame.setVisible(true);
        
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

        LinkModelAction linkAction = new LinkModelAction<LinkModel>(link);
        linkAction.setVisitingDelegate(visitor);
        JXHyperlink hyperlink = new JXHyperlink(linkAction);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(visitor.getOutputComponent()));
        panel.add(hyperlink, BorderLayout.SOUTH);
        JFrame frame = wrapInFrame(panel, "simple hyperlink");
        frame.setSize(200, 200);
        frame.setVisible(true);
        
    }

//---------------------- interactive test: JXTable
    
    public void interactiveTableLinkRendererSimpleText() {
        LinkAction linkAction = new LinkAction<Object>(null) {

            public void actionPerformed(ActionEvent e) {
                LOG.info("hit: " + getTarget());
                
            }
            
        };
        
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultRenderer(LinkModel.class, new LinkRenderer(action, LinkModel.class));
        LinkModelAction action2 = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultEditor(LinkModel.class, new LinkRenderer(action2, LinkModel.class));
        table.getColumn(0).setCellRenderer(new LinkRenderer(linkAction, LinkModel.class));
//        table.getColumn(0).setCellEditor(new LinkRenderer(linkAction));
        table.getColumnExt(0).setEditable(false);
        JFrame frame = wrapWithScrollingInFrame(table, visitor.getOutputComponent(), "table and simple links");
        frame.setVisible(true);
        
    }
    public void interactiveTestTableLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultRenderer(LinkModel.class, new LinkRenderer(action, LinkModel.class));
        LinkModelAction action2 = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultEditor(LinkModel.class, new LinkRenderer(action2, LinkModel.class));
        JFrame frame = wrapWithScrollingInFrame(table, visitor.getOutputComponent(), "show link renderer in table");
        frame.setVisible(true);

    }
    
    public void interactiveTestTableLinkRendererEmptyHighlighterPipeline() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultRenderer(LinkModel.class, new LinkRenderer(action, LinkModel.class));
        LinkModelAction action2 = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultEditor(LinkModel.class, new LinkRenderer(action2, LinkModel.class));
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] { }));
        JFrame frame = wrapWithScrollingInFrame(table, visitor.getOutputComponent(), 
                "show link renderer in table with empty highlighterPipeline");
        frame.setVisible(true);

    }

    public void interactiveTestTableLinkRendererNullHighlighter() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultRenderer(LinkModel.class, new LinkRenderer(action, LinkModel.class));
        LinkModelAction action2 = new LinkModelAction<LinkModel>(visitor);
        table.setDefaultEditor(LinkModel.class, new LinkRenderer(action2, LinkModel.class));
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {new Highlighter() }));
        JFrame frame = wrapWithScrollingInFrame(table, visitor.getOutputComponent(), 
                "show link renderer in table with null highlighter");
        frame.setVisible(true);

    }

    public void interactiveTestTableLinkRendererLFStripingHighlighter() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        LinkModelAction action = new LinkModelAction(visitor);
        table.setDefaultRenderer(LinkModel.class, new LinkRenderer(action, LinkModel.class));
        LinkModelAction action2 = new LinkModelAction(visitor);
        table.setDefaultEditor(LinkModel.class, new LinkRenderer(action2, LinkModel.class));
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] { 
                new UIAlternateRowHighlighter()}));
        JFrame frame = wrapWithScrollingInFrame(table, visitor.getOutputComponent(), 
                "show link renderer in table with LF striping highlighter");
        frame.setVisible(true);

    }

    
//----------------- interactive tests: JXList
 
    public void interactiveTestListLinkRendererPlayer() {
        LinkAction<Player> linkAction = new LinkAction<Player>() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("hit: " + getTarget());
                
            }
            
            protected void installTarget() {
                setName(getTarget() != null ? getTarget().name : "");
            }
            
        };
        
        JXList list = new JXList(createPlayerModel());
        list.setRolloverEnabled(true);
        // descending order - check if the action is performed for the 
        // correct value
        list.setFilterEnabled(true);
        FilterPipeline pipeline = new FilterPipeline(new Filter[] { new ShuttleSorter(0, false)});
        list.setFilters(pipeline);
        list.setCellRenderer(new LinkRenderer(linkAction, Player.class));
        JFrame frame = wrapWithScrollingInFrame(list, "show simple bean link renderer in list");
        frame.setVisible(true);

    }
    
    private ListModel createPlayerModel() {
        DefaultListModel model = new DefaultListModel();
        model.addElement(new Player("Henry", 10));
        model.addElement(new Player("Berta", 112));
        model.addElement(new Player("Dave", 20));
        return model;
    }

    public static class Player {
        String name;
        int score;
        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }
        @Override
        public String toString() {
            return name + " has score: " + score;
        }
        
        
    }
    public void interactiveTestListLinkRendererSimpleText() {
        LinkAction linkAction = new LinkAction<Object>(null) {

            public void actionPerformed(ActionEvent e) {
                LOG.info("hit: " + getTarget());
                
            }
            
        };
        
        JXList list = new JXList(createTextOnlyListModel(20));
        list.setRolloverEnabled(true);
        list.setCellRenderer(new LinkRenderer(linkAction, LinkModel.class));
        JFrame frame = wrapWithScrollingInFrame(list, "show simple link renderer in list");
        frame.setVisible(true);

    }

    public void interactiveTestListLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXList list = new JXList(createListModelWithLinks(20));
        list.setRolloverEnabled(true);
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        LinkModelAction action = new LinkModelAction(visitor);
        list.setCellRenderer(new LinkRenderer(action, LinkModel.class));
        JFrame frame = wrapWithScrollingInFrame(list, visitor.getOutputComponent(), "show link renderer in list");
        frame.setVisible(true);

    }
    
    public void interactiveTestListLinkRendererLFStripingHighlighter() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXList list = new JXList(createListModelWithLinks(20));
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        list.setCellRenderer(new LinkRenderer(action, LinkModel.class));
        list.setRolloverEnabled(true);
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {
                new UIAlternateRowHighlighter()}));
        JFrame frame = wrapWithScrollingInFrame(list, visitor.getOutputComponent(), 
                "show link renderer in list with LFStriping highlighter");
        frame.setVisible(true);

    }
    
    public void interactiveTestListLinkRendererEmptyHighlighterPipeline() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXList list = new JXList(createListModelWithLinks(20));
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        list.setCellRenderer(new LinkRenderer(action, LinkModel.class));
        list.setRolloverEnabled(true);
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] { }));
        JFrame frame = wrapWithScrollingInFrame(list, visitor.getOutputComponent(), 
                "show link renderer in list empty highlighterPipeline");
        frame.setVisible(true);

    }

    public void interactiveTestListLinkRendererNullHighlighter() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXList list = new JXList(createListModelWithLinks(20));
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        LinkModelAction action = new LinkModelAction<LinkModel>(visitor);
        list.setCellRenderer(new LinkRenderer(action, LinkModel.class));
        list.setRolloverEnabled(true);
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {new Highlighter() }));
        JFrame frame = wrapWithScrollingInFrame(list, visitor.getOutputComponent(), 
                "show link renderer in list null highlighter");
        frame.setVisible(true);

    }

    private ListModel createTextOnlyListModel(int count) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < count; i++) {
                model.addElement("text #" + i);
        }
        return model;
    }
    
    private ListModel createListModelWithLinks(int count) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < count; i++) {
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
        String[] columnNames = { "text only", "Link editable", "Link not-editable", "Bool editable", "Bool not-editable" };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                    return !getColumnName(column).contains("not");
            }
            
        };
        for (int i = 0; i < 4; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a link text " + i, null, url);
                }
                model.addRow(new Object[] {"text only " + i, link, link, Boolean.TRUE, Boolean.TRUE });
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return model;
    }

    
    protected LinkAction<Object> createEmptyLinkAction() {
        LinkAction<Object> linkAction = new LinkAction<Object>(null) {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
               
       };
        return linkAction;
    }

    protected LinkAction createEmptyLinkAction(String name) {
        LinkAction linkAction = createEmptyLinkAction();
        linkAction.setName(name);
        return linkAction;
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        report = new PropertyChangeReport();
    }

}
