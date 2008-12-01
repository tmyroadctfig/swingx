package org.jdesktop.swingx;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.test.AncientSwingTeam;

public class JXMultiSplitPaneVisualIssues extends InteractiveTestCase {
    
    /**
     * Use MultiSplitPane in demo. Here the layout is revalidated as
     * usual after replacing the component. Slight "jumping" of sizes 
     * (of comps in the column). Expected?
     * 
     */
    public void interactiveSplitPaneRevalidate() {
        String layout = 
            "(ROW " +
                 "(LEAF name=selector weight=0.3)" +
                 "(COLUMN weight=0.7 " +
                     "(LEAF name=demo weight=0.7) " +
                     "(LEAF name=source weight=0.3)" +
                 ")" +
            ")"; 
       
       final JXMultiSplitPane splitPane = new JXMultiSplitPane();
       splitPane.setModel(MultiSplitLayout.parseModel(layout));
       JXList list = new JXList(AncientSwingTeam.createNamedColorListModel());
       list.addHighlighter(HighlighterFactory.createSimpleStriping());
       JComponent listPanel = new JPanel(new BorderLayout());
       listPanel.add(new JScrollPane(list));
       splitPane.add(listPanel, "selector");
       JButton button = new JButton("demo");
       final JComponent buttonPanel = new JPanel(new BorderLayout());
       buttonPanel.add(button);
       JComponent panel = new JPanel(new BorderLayout());
       panel.setOpaque(true);
       panel.setBackground(Color.BLUE);
       final JComponent panelPanel = new JPanel(new BorderLayout());
       panelPanel.add(panel);
       splitPane.add(buttonPanel, "demo");
       splitPane.add(new JButton("source"), "source");
       JXFrame frame = wrapInFrame(splitPane, "replace upper right and revalidate"); 
       Action action = new AbstractAction("replace uppder right") {

        public void actionPerformed(ActionEvent e) {
            JComponent comp = getComponentByConstraint(splitPane.getMultiSplitLayout(), "demo");
            splitPane.remove(comp);
            if (comp == buttonPanel) {
                splitPane.add(panelPanel, "demo");
            } else if (comp == panelPanel) {
                splitPane.add(buttonPanel, "demo");
            } 
            splitPane.revalidate();
            splitPane.repaint();
        }
           
       };
       addAction(frame, action);
       frame.setSize(800, 600);
       frame.setVisible(true);
    }

    /**
     * Use MultiSplitPane in demo. Here we want to always layout according to the 
     * weights. Doesn't quite, probably do something wrong ;-) Sometimes, on 
     * replacing and re-layout the splitpane, the newly added components aren't 
     * updated to a size to "fill" the split completely.
     * 
     */
    public void interactiveSplitPaneByWeight() {
        String layout = 
            "(ROW " +
                 "(LEAF name=selector weight=0.3)" +
                 "(COLUMN weight=0.7 " +
                     "(LEAF name=demo weight=0.7) " +
                     "(LEAF name=source weight=0.3)" +
                 ")" +
            ")"; 
       
       final JXMultiSplitPane splitPane = new JXMultiSplitPane();
       splitPane.setModel(MultiSplitLayout.parseModel(layout));
       JXList list = new JXList(AncientSwingTeam.createNamedColorListModel());
       list.addHighlighter(HighlighterFactory.createSimpleStriping());
       JComponent listPanel = new JPanel(new BorderLayout());
       listPanel.add(new JScrollPane(list));
       splitPane.add(listPanel, "selector");
       JButton button = new JButton("demo");
       final JComponent buttonPanel = new JPanel(new BorderLayout());
       buttonPanel.add(button);
       JComponent panel = new JPanel(new BorderLayout());
       panel.setOpaque(true);
       panel.setBackground(Color.BLUE);
       final JComponent panelPanel = new JPanel(new BorderLayout());
       panelPanel.add(panel);
       splitPane.add(buttonPanel, "demo");
       splitPane.add(new JButton("source"), "source");
       JXFrame frame = wrapInFrame(splitPane, "replace upper right and layoutByWeight"); 
       Action action = new AbstractAction("replace demo") {

        public void actionPerformed(ActionEvent e) {
            JComponent comp = getComponentByConstraint(splitPane.getMultiSplitLayout(), "demo");
            splitPane.remove(comp);
            if (comp == buttonPanel) {
                splitPane.add(panelPanel, "demo");
            } else if (comp == panelPanel) {
                splitPane.add(buttonPanel, "demo");
            } 
            splitPane.invalidate();
            splitPane.validate();
            splitPane.getMultiSplitLayout().layoutByWeight(splitPane);
            splitPane.repaint();
        }
           
       };
       addAction(frame, action);
       frame.setSize(800, 600);
       splitPane.invalidate();
       splitPane.validate();
       splitPane.getMultiSplitLayout().layoutByWeight(splitPane);
       splitPane.repaint();
       frame.setVisible(true);
    }
    
    private JComponent getComponentByConstraint(MultiSplitLayout multiSplitLayout, String string) {
        Node node = multiSplitLayout.getNodeForComponent(string);
        return (JComponent) multiSplitLayout.getComponentForNode(node);
    }

    private static void prepare(JFrame f) {
        prepare(f, MultiSplitLayout.parseModel("(ROW (COLUMN weight=0.5 left.top left.bottom)" + 
                " (LEAF name=middle)" + 
                " (COLUMN weight=0.5 right.top right.bottom))"));
        
    }
    
    private static void prepare(JFrame f, Node model) {
        final JXMultiSplitPane msp;
        JXPanel p = new JXPanel(new BorderLayout());
        JXHeader header = new JXHeader();
        header.setTitle("<html><B>JXMultiSplitPaneIssue</b>");
        header.setDescription("<html>This is a bit tricky ... To reproduce the issue move one of the dividers and then click reset. Expected behavior is that original layout is restored. Actual behavior (bug) is that only two cells stay displayed and no divider is visible.");
        p.add(header, BorderLayout.NORTH);
        msp = new JXMultiSplitPane();
        msp.setModel(model);
        p.add(msp);
        JButton green = new JButton("mid");
        green.setBackground(Color.GREEN);
        msp.add(green, "middle");
        msp.add(new JButton("l.t"), "left.top");
        msp.add(new JButton("l.b"), "left.bottom");
        msp.add(new JButton("r.t"), "right.top");
        msp.add(new JButton("r.b"), "right.bottom");
        f.add(p);
        JButton b = new JButton(new AbstractAction("Reset") {

            public void actionPerformed(ActionEvent e) {
                // just reset the model
                msp.setModel(MultiSplitLayout.parseModel("(ROW (COLUMN weight=0.5 left.top left.bottom)" + 
                " (LEAF name=middle)" + 
                " (COLUMN weight=0.5 right.top right.bottom))"));
                msp.revalidate();
                msp.repaint();
            }

        });
        f.add(b, BorderLayout.SOUTH);
        f.setPreferredSize(new Dimension(400, 400));
    }
    
    public void interactiveModelChange() {
        JFrame f = new JFrame();
        prepare(f);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JXMultiSplitPaneVisualIssues test = new JXMultiSplitPaneVisualIssues();
        try {
//            test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Split.*");
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 
    }
    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
