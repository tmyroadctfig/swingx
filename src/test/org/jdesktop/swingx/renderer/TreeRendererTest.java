/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.test.ActionMapTreeTableModel;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.treetable.FileSystemModel;

/**
 * Tests behaviour of SwingX <code>DefaultTreeRenderer</code>. 
 * Contains characterization to
 * guarantee that it behaves similar to the standard. 
 * 
 * @author Jeanette Winzenburg
 */
public class TreeRendererTest extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(TreeRendererTest.class
            .getName());
    
    private DefaultTreeCellRenderer coreTreeRenderer;
    private DefaultTreeRenderer xTreeRenderer;

    
    @Override
    protected void setUp() throws Exception {
//        setSystemLF(true);
//        LOG.info("LF: " + UIManager.getLookAndFeel());
//        LOG.info("Theme: " + ((MetalLookAndFeel) UIManager.getLookAndFeel()).getCurrentTheme());
//        UIManager.put("Tree.drawsFocusBorderAroundIcon", Boolean.TRUE);
        coreTreeRenderer = new DefaultTreeCellRenderer();
        xTreeRenderer = new DefaultTreeRenderer();
    }

    public static void main(String[] args) {
        TreeRendererTest test = new TreeRendererTest();
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests(".*Wrapper.*");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * related to Issue #22-swingx: tree background highlighting broken.
     * test if background color is moved down to delegate component.
     *
     */
    public void testDelegateBackground() {
        WrappingProvider provider = new WrappingProvider();
        DefaultTreeRenderer renderer = new DefaultTreeRenderer(provider);
        Component comp = renderer.getTreeCellRendererComponent(null, "dummy", false, false, false, -1, false);
        assertTrue(comp instanceof WrappingIconPanel);
        comp.setBackground(Color.RED);
        // sanity
        assertTrue(provider.getRendererComponent(null).isBackgroundSet());
        assertEquals(Color.RED, provider.getRendererComponent(null).getBackground());
        // sanity
        assertTrue(provider.wrappee.getRendererComponent(null).isBackgroundSet());
        assertEquals(Color.RED, provider.wrappee.getRendererComponent(null).getBackground());
    }
    
    /**
     * related to Issue #22-swingx: tree background highlighting broken.
     * test if foreground color is moved down to delegate component.
     *
     */
    public void testDelegateForeground() {
        WrappingProvider provider = new WrappingProvider();
        DefaultTreeRenderer renderer = new DefaultTreeRenderer(provider);
        Component comp = renderer.getTreeCellRendererComponent(null, "dummy", false, false, false, -1, false);
        assertTrue(comp instanceof WrappingIconPanel);
        comp.setForeground(Color.RED);
        // sanity
        assertTrue(provider.getRendererComponent(null).isForegroundSet());
        assertEquals(Color.RED, provider.getRendererComponent(null).getForeground());
        // sanity
        assertTrue(provider.wrappee.getRendererComponent(null).isForegroundSet());
        assertEquals(Color.RED, provider.wrappee.getRendererComponent(null).getForeground());
    }


    /**
     * characterize opaqueness of rendering components.
     * Hmm... tree-magic is different
     */
    public void testTreeOpaqueRenderer() {
        // sanity
        assertFalse(new JLabel().isOpaque());
        
//        assertTrue(coreTreeRenderer.isOpaque());
//        assertTrue(xListRenderer.getRendererComponent().isOpaque());
    }

    /**
     * base existence/type tests while adding DefaultTableCellRendererExt.
     *
     */
    public void testTreeRendererExt() {
        DefaultTreeRenderer renderer = new DefaultTreeRenderer();
        assertTrue(renderer instanceof TreeCellRenderer);
        assertTrue(renderer instanceof Serializable);
    }

//---------------------- interactive methods

    /**
     * Example for using arbitrary wrappee controllers. Here: a 
     * checkbox representing entries in ActionMap.
     * 
     */
    public void interactiveTreeButtonFormatting() {
        TreeModel model = createActionTreeModel();
        JTree tree = new JTree(model);
        ButtonProvider wrapper = createButtonController();
        tree.setCellRenderer(new DefaultTreeRenderer(new WrappingProvider(wrapper)));
        
        JList list = new JList(createActionListModel());
        // can't re-use the same instance - the WrappingProvider adds the
        // wrappee component to a custom container.
        list.setCellRenderer(new DefaultListRenderer(createButtonController()));
        final JXFrame frame = wrapWithScrollingInFrame(tree, list, "custom renderer - same in tree and list");
        frame.setVisible(true);
    }
    

    /**
     * Custom format on JTree/JXTree (latter with highlighter).
     *
     */
    public void interactiveXTreeLabelFormattingHighlighter() {
        TreeModel model = createComponentHierarchyModel();
        JTree tree = new JTree(model);
        StringValue converter = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Component) {
                    return "Name: " + ((Component) value).getName();
                }
                return TO_STRING.getString(value);
            }
            
        };
        tree.setCellRenderer(new DefaultTreeRenderer(converter));
        JXTree xtree = new JXTree(model);
        xtree.setHighlighters(new ColorHighlighter(Color.RED, Color.YELLOW,
                HighlightPredicate.ROLLOVER_ROW));
        xtree.setRolloverEnabled(true);
        // share renderer
        xtree.setCellRenderer(tree.getCellRenderer());
        final JXFrame frame = wrapWithScrollingInFrame(tree, xtree, "custom format - tree vs. xtree (+Rollover renderer)");
        frame.setVisible(true);
    }

    /**
     * Custom tree colors in JTree. Compare core default renderer with Swingx
     * default renderer.
     * 
     */
    public void interactiveCompareTreeExtTreeColors() {
        JTree xtree = new JTree();
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        xtree.setBackground(background);
        xtree.setForeground(foreground);
        DefaultTreeCellRenderer coreTreeCellRenderer = new DefaultTreeCellRenderer();
        // to get a uniform color on both tree and node
        // the core default renderer needs to be configured
        coreTreeCellRenderer.setBackgroundNonSelectionColor(background);
        coreTreeCellRenderer.setTextNonSelectionColor(foreground);
        xtree.setCellRenderer(coreTreeCellRenderer);
        JTree tree = new JTree();
        tree.setBackground(background);
        tree.setForeground(foreground);
        // swingx renderer uses tree colors
        tree.setCellRenderer(xTreeRenderer);
        final JXFrame frame = wrapWithScrollingInFrame(xtree, tree,
                "custom tree colors - core vs. ext renderer");
        frame.setVisible(true);
    }
    
    /**
     * Component orientation in JTree. Compare core default renderer with Swingx
     * default renderer.
     * 
     */
    public void interactiveCompareTreeRToL() {
        JTree xtree = new JTree();
        xtree.setCellRenderer(coreTreeRenderer);
        JTree tree = new JTree();
        tree.setCellRenderer(xTreeRenderer);
        final JXFrame frame = wrapWithScrollingInFrame(xtree, tree,
                "orientation - core vs. ext renderer");
        Action toggleComponentOrientation = new AbstractAction(
                "toggle orientation") {

            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                }
            }

        };
        addAction(frame, toggleComponentOrientation);
        frame.setVisible(true);
    }

    /**
     * Format custom model.
     *
     * PENDING: editor uses default toString and looses icons -
     *   because the renderer is not a label.
     */
    public void interactiveDefaultWrapper() {
        JTree xtree = new JTree(createComponentHierarchyModel());
        StringValue componentFormat = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Component) {
                    return ((Component) value).getName();
                }
                return StringValue.TO_STRING.getString(value);
            }};
        xtree.setCellRenderer(new DefaultTreeRenderer(componentFormat));
        xtree.setEditable(true);
        JTree tree = new JTree(new FileSystemModel());
        StringValue format = new StringValue() {

            public String getString(Object value) {
                if (value instanceof File) {
                    return ((File) value).getName();
                }
                return StringValue.TO_STRING.getString(value);
            }
            
        };
        tree.setCellRenderer(new DefaultTreeRenderer(format));
        final JXFrame frame = wrapWithScrollingInFrame(xtree, tree, "wrapper and different models");
        frame.setVisible(true);
    }
//-------------------------- factory methods
    /**
     * 
     * @return a button controller specialized on ActionEntryNode.
     */
    private ButtonProvider createButtonController() {
        ButtonProvider wrapper = new ButtonProvider() {

            @Override
            protected void format(CellContext context) {
                boolean selected = false;
                String text = getStringValue(context);
                if (context.getValue() instanceof Action) {
                    Action action = (Action) context.getValue();
                    text = (String) action.getValue(Action.NAME);
                    if (action instanceof AbstractActionExt) {
                        selected = ((AbstractActionExt) action).isSelected();
                    }
                }
                rendererComponent.setSelected(selected);
                rendererComponent.setText(text);
            }
            
        };
        wrapper.setHorizontalAlignment(JLabel.LEADING);
        return wrapper;
    }


    /**
     * @return
     */
    private ListModel createActionListModel() {
        JXTable table = new JXTable(10, 10);
        table.setHorizontalScrollEnabled(true);
        ActionMap map = table.getActionMap();
        Object[] keys = map.keys();
        DefaultListModel model = new DefaultListModel();
        for (Object object : keys) {
           model.addElement(map.get(object)); 
        }
        return model;
    }

    /**
     * @return
     */
    private TreeModel createActionTreeModel() {
        JXTable table = new JXTable(10, 10);
        table.setHorizontalScrollEnabled(true);
        return new ActionMapTreeTableModel(table);
    }


    /**
 * @return
 */
private TreeModel createComponentHierarchyModel() {
    JXFrame frame = new JXFrame("dummy");
    frame.add(new JScrollPane(new JXTree()));
    return new ComponentTreeTableModel(frame);
}


}
