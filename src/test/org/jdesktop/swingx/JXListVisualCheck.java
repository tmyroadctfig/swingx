/*
 * Created on 10.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JList;

import org.jdesktop.swingx.action.LinkModelAction;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.RolloverHighlighter;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;

public class JXListVisualCheck extends JXListTest {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXListVisualCheck.class
            .getName());
    public static void main(String[] args) {
        setSystemLF(true);
        JXListVisualCheck test = new JXListVisualCheck();
        try {
          test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Rollover.*");
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
        showWithScrollingInFrame(xlist, list, "unselectedd focused background: JXList/JList");
    }

    public void interactiveTestTablePatternFilter5() {
        JXList list = new JXList(listModel);
        String pattern = "Row";
        list.setHighlighters(new PatternHighlighter(null, Color.red, 
                pattern, 0, 1));
        showWithScrollingInFrame(list, "PatternHighlighter: " + pattern);
    }

    public void interactiveTestTableAlternateHighlighter1() {
        JXList list = new JXList(listModel);
        list.addHighlighter(AlternateRowHighlighter.linePrinter);
        showWithScrollingInFrame(list, "AlternateRowHighlighter - lineprinter");
    }

    /**
     * Plain rollover highlight, had been repaint issues.
     *
     */
    public void interactiveTestRolloverHighlight() {
        JXList list = new JXList(listModel);
        list.setRolloverEnabled(true);
        list.addHighlighter(new RolloverHighlighter(new Color(0xF0, 0xF0, 0xE0), null));
        showWithScrollingInFrame(list, "rollover highlight");
    }

    /**
     * Plain rollover highlight in multi-column layout, had been repaint issues.
     *
     */
    public void interactiveTestRolloverHighlightMultiColumn() {
        JXList list = new JXList(listModel);
        list.setRolloverEnabled(true);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.addHighlighter(new RolloverHighlighter(new Color(0xF0, 0xF0, 0xE0), null));
        showWithScrollingInFrame(list, "rollover highlight - horz. Wrap");
    }
    /**
     * Issue #503-swingx: rolloverEnabled disables custom cursor
     *
     */
    public void interactiveTestRolloverHighlightCustomCursor() {
        JXList list = new JXList(listModel);
        list.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        list.setRolloverEnabled(true);
        list.addHighlighter(new RolloverHighlighter(new Color(0xF0, 0xF0, 0xE0), null));
        showWithScrollingInFrame(list, "rollover highlight - custom cursor");
    }
    /**
     * Issue #20: Highlighters and LinkRenderers don't work together
     * fixed with overhaul of SwingX renderers?
     */
    public void interactiveTestRolloverHighlightAndLink() {
        JXList list = new JXList(createListModelWithLinks());
        EditorPaneLinkVisitor editorPaneLinkVisitor = new EditorPaneLinkVisitor();
        LinkModelAction action = new LinkModelAction(editorPaneLinkVisitor);
        HyperlinkProvider h = new HyperlinkProvider(action, LinkModel.class);
        list.setCellRenderer(new DefaultListRenderer(h));
        list.setRolloverEnabled(true);
        list.addHighlighter(new RolloverHighlighter(new Color(0xF0, 0xF0, 0xE0), null));
        showWithScrollingInFrame(list, editorPaneLinkVisitor.getOutputComponent(), "rollover highlight with links");
    }

}
