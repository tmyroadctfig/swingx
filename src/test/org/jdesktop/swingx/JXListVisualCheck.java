/*
 * Created on 10.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JList;

import org.jdesktop.swingx.action.LinkModelAction;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.SortKey;
import org.jdesktop.swingx.decorator.SortOrder;

public class JXListVisualCheck extends JXListTest {
    private static final Logger LOG = Logger.getLogger(JXListVisualCheck.class
            .getName());
    public static void main(String[] args) {
        setSystemLF(true);
        JXListVisualCheck test = new JXListVisualCheck();
        try {
          test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Column.*");
         //   test.runInteractiveTests("interactive.*TableHeader.*");
         //   test.runInteractiveTests("interactive.*Render.*");
//            test.runInteractiveTests("interactive.*Sort.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    
    public void interactiveTestSort() {
        final JXList list = new JXList(listModel, true);
        JXFrame frame = wrapWithScrollingInFrame(list, "Toggle sorter");
        Action toggleSortOrder = new AbstractAction("Toggle Sort Order") {

            public void actionPerformed(ActionEvent e) {
                list.toggleSortOrder();
                
            }
            
        };
        addAction(frame, toggleSortOrder);
        Action resetSortOrder = new AbstractAction("Reset Sort Order") {

            public void actionPerformed(ActionEvent e) {
                list.resetSortOrder();
                
            }
            
        };
        addAction(frame, resetSortOrder);
        frame.setVisible(true);
        
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
        String pattern = "Row";
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
        LinkModelAction action = new LinkModelAction(new EditorPaneLinkVisitor());
        list.setCellRenderer(new LinkRenderer(action, LinkModel.class));
        list.setRolloverEnabled(true);
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

}
