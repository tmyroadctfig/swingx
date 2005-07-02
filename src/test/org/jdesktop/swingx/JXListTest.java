/*
 * Created on 07.06.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Point;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListModel;

import org.jdesktop.swingx.action.EditorPaneLinkVisitor;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternHighlighter;

/**
 * @author (C) 2004 Jeanette Winzenburg, Berlin
 * @version $Revision$ - $Date$
 */
public class JXListTest extends InteractiveTestCase {

    private ListModel listModel;

    public void testDummy() {
        
    }
    public JXListTest() {
        super("JXList Tests");
    }

    public void interactiveTestCompareFocusedCellBackground() {
        JXList xlist = new JXList(listModel);
        xlist.setBackground(new Color(0xF5, 0xFF, 0xF5));
        JList list = new JList(listModel);
        list.setBackground(new Color(0xF5, 0xFF, 0xF5));
        JFrame frame = wrapWithScrollingInFrame(xlist, list, "unselectedd focused background: JXList/JList");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter5() {
        JXList list = new JXList(listModel);
        String pattern = ".*Row.*";
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new PatternHighlighter(null, Color.red, pattern, 0, 1),
        }));
        JFrame frame = wrapWithScrollingInFrame(list, "PatternHighlighter: " + pattern);
        frame.setVisible(true);
    }

    public void interactiveTestTableAlternateHighlighter1() {
        JXList list = new JXList(listModel);
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            AlternateRowHighlighter.
            linePrinter,
        }));

        JFrame frame = wrapWithScrollingInFrame(list, "AlternateRowHighlighter - lineprinter");
        frame.setVisible(true);
    }

    public void interactiveTestRolloverHighlight() {
        JXList list = new JXList(listModel);
    //    table.setLinkVisitor(new EditorPaneLinkVisitor());
        list.setRolloverEnabled(true);
        Highlighter conditional = new ConditionalHighlighter(
                new Color(0xF0, 0xF0, 0xE0), null, -1, -1) {

            protected boolean test(ComponentAdapter adapter) {
                Point p = (Point) adapter.getComponent().getClientProperty(RolloverProducer.ROLLOVER_KEY);
     
                return p != null &&  p.y == adapter.row;
            }
            
        };
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {conditional }));
        JFrame frame = wrapWithScrollingInFrame(list, "rollover highlight");
        frame.setVisible(true);

    }

    /**
     * Issue #20: Highlighters and LinkRenderers don't work together
     *
     */
    public void interactiveTestRolloverHighlightAndLink() {
        JXList list = new JXList(createListModelWithLinks());
        list.setLinkVisitor(new EditorPaneLinkVisitor());
    //    table.setRolloverEnabled(true);
        Highlighter conditional = new ConditionalHighlighter(
                new Color(0xF0, 0xF0, 0xE0), null, -1, -1) {

            protected boolean test(ComponentAdapter adapter) {
                Point p = (Point) adapter.getComponent().getClientProperty(RolloverProducer.ROLLOVER_KEY);
     
                return p != null &&  p.y == adapter.row;
            }
            
        };
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {conditional }));
        JFrame frame = wrapWithScrollingInFrame(list, "rollover highlight with links");
        frame.setVisible(true);

    }
    private ListModel createListModel() {
        JXList list = new JXList();
        return new DefaultComboBoxModel(list.getActionMap().allKeys());
    }

    private ListModel createListModelWithLinks() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 20; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a resource", null, url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }

    protected void setUp() throws Exception {
        super.setUp();
        listModel = createListModel();
    }
    
    public static void main(String[] args) {
        setSystemLF(true);
        JXListTest test = new JXListTest();
        try {
          test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Column.*");
         //   test.runInteractiveTests("interactive.*TableHeader.*");
         //   test.runInteractiveTests("interactive.*Render.*");
          //  test.runInteractiveTests("interactive.*High.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
}
