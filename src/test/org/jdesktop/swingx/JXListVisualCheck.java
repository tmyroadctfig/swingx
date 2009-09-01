/*
 * Created on 10.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.hyperlink.EditorPaneLinkVisitor;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.hyperlink.LinkModelAction;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.sort.RowFilters;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.After;
import org.junit.Before;

public class JXListVisualCheck extends InteractiveTestCase { //JXListTest {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXListVisualCheck.class
            .getName());
    public static void main(String[] args) {
        setSystemLF(true);
//        LookAndFeel l;
//        SynthConstants s;
        JXListVisualCheck test = new JXListVisualCheck();
        try {
//            NimbusLookAndFeel n;
//            Region my = XRegion.XLIST;
//            setLookAndFeel("Nimbus");
//            new XRegion("XList", "XListUI", false);
//          test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Match.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue 1161-swingx: JXList not completely updated on setRowFilter
     * Issue 1162-swingx: JXList getNextMatch access model directly
     */
    public void interactiveNextMatch() {
        JList core = new JList(AncientSwingTeam.createNamedColorListModel());
        final JXList list = new JXList(core.getModel(), true);
        list.toggleSortOrder();
        JXFrame frame = showWithScrollingInFrame(list, core, "x <-> core: nextMatch");
        Action toggleFilter = new AbstractAction("toggleFilter") {
            RowFilter filter = RowFilters.regexFilter(Pattern.CASE_INSENSITIVE, "^b");
            @Override
            public void actionPerformed(ActionEvent e) {
                list.setRowFilter(list.getRowFilter() == null ? filter : null);
            }
        };
        addAction(frame, toggleFilter);
    }
    
    public void interactiveTestCompareFocusedCellBackground() {
        final JXList xlist = new JXList(listModel);
        LOG.info("xlist ui" + xlist.getUI());
        final Color bg = new Color(0xF5, 0xFF, 0xF5);
        final JList list = new JList(listModel);
//        xlist.setBackground(bg);
//        list.setBackground(bg);
        JXFrame frame = wrapWithScrollingInFrame(xlist, list, 
                "unselectedd focused background: JXList/JList");
        Action toggle = new AbstractAction("toggle background") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Color old = xlist.getBackground();
                Color back = ((old == null) || (old instanceof UIResource)) ? bg : null;
                xlist.setBackground(back);
                list.setBackground(back);
                if (back == null) {
                    // force ui default background
                    list.updateUI();
                    xlist.updateUI();
                }
                
            }
        };
        addAction(frame, toggle);
        show(frame);
    }

    public void interactiveTestTablePatternFilter5() {
        JXList list = new JXList(listModel);
        String pattern = "Row";
        list.setHighlighters(new ColorHighlighter(// columns not really important, ListAdapter.getXXValue
        // uses row only
        new PatternPredicate(pattern, 0), null, 
                Color.red));
        showWithScrollingInFrame(list, "PatternHighlighter: " + pattern);
    }

    public void interactiveTestTableAlternateHighlighter1() {
        JXList list = new JXList(listModel);
        list.addHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.LINE_PRINTER));

        showWithScrollingInFrame(list, "AlternateRowHighlighter - lineprinter");
    }

    /**
     * Plain rollover highlight, had been repaint issues.
     *
     */
    public void interactiveTestRolloverHighlight() {
        JXList list = new JXList(listModel);
        list.setRolloverEnabled(true);
        ColorHighlighter rollover = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, new Color(0xF0, 0xF0, 0xE0), 
                        null);
        list.addHighlighter(rollover);
        showWithScrollingInFrame(list, "rollover highlight");
    }

    /**
     * Plain rollover highlight in multi-column layout, had been repaint issues.
     *
     */
    public void interactiveTestRolloverHighlightMultiColumn() {
        LOG.info("rtol-map? " + UIManager.get("List.focusInputMap.RightToLeft"));
        LOG.info("ancestor maps? " + UIManager.get("Table.ancestorInputMap"));
        LOG.info("ancestor rtol maps? " + UIManager.get("Table.ancestorInputMap.RightToLeft"));
        
        JXList list = new JXList(listModel);
        list.setRolloverEnabled(true);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, new Color(0xF0, 0xF0, 0xE0), 
                null));
        JList core = new JList(listModel);
        core.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        JComponent box = Box.createVerticalBox();
        box.add(new JScrollPane(list));
        box.add(new JScrollPane(core));
        JXFrame frame = wrapInFrame(box, "rollover highlight - horz. Wrap");
        addComponentOrientationToggle(frame);
        show(frame);
    }
    /**
     * Issue #503-swingx: rolloverEnabled disables custom cursor
     *
     */
    public void interactiveTestRolloverHighlightCustomCursor() {
        JXList list = new JXList(listModel);
        list.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        list.setRolloverEnabled(true);
        list.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, new Color(0xF0, 0xF0, 0xE0), 
                null));
        showWithScrollingInFrame(list, "rollover highlight - custom cursor");
    }
    /**
     * Issue #20: Highlighters and LinkRenderers don't work together
     * fixed with overhaul of SwingX renderers?
     */
    public void interactiveTestRolloverHighlightAndLink() {
        JXList list = new JXList(createListModelWithLinks());
        EditorPaneLinkVisitor editorPaneLinkVisitor = new EditorPaneLinkVisitor();
        LinkModelAction<?> action = new LinkModelAction<LinkModel>(editorPaneLinkVisitor);
        HyperlinkProvider h = new HyperlinkProvider(action, LinkModel.class);
        list.setCellRenderer(new DefaultListRenderer(h));
        list.setRolloverEnabled(true);
        list.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, new Color(0xF0, 0xF0, 0xE0), 
                null));
        showWithScrollingInFrame(list, editorPaneLinkVisitor.getOutputComponent(), "rollover highlight with links");
    }

    

    protected ListModel createListModel() {
        JList list = new JList();
        return new DefaultComboBoxModel(list.getActionMap().allKeys());
    }

    protected DefaultListModel createAscendingListModel(int startRow, int count) {
        DefaultListModel l = new DefaultListModel();
        for (int row = startRow; row < startRow  + count; row++) {
            l.addElement(new Integer(row));
        }
        return l;
    }
    protected DefaultListModel createListModelWithLinks() {
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        listModel = createListModel();
        ascendingListModel = createAscendingListModel(0, 20);
    }

    protected ListModel listModel;
    protected DefaultListModel ascendingListModel;

    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }

}
