/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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
 */

package org.jdesktop.swingx;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.tree.DefaultXTreeCellEditor;


/**
 * JXTree.
 *
 * PENDING: support filtering/sorting.
 * 
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 */
public class JXTree extends JTree {
    private static final Logger LOG = Logger.getLogger(JXTree.class.getName());
    private Method conversionMethod = null;
    private final static Class[] methodSignature = new Class[] {Object.class};
    private final static Object[] methodArgs = new Object[] {null};
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final TreePath[] EMPTY_TREEPATH_ARRAY = new TreePath[0];

    protected FilterPipeline filters;
    protected HighlighterPipeline highlighters;
    private ChangeListener highlighterChangeListener;

    private DelegatingRenderer delegatingRenderer;

    /**
     * Mouse/Motion/Listener keeping track of mouse moved in
     * cell coordinates.
     */
    private RolloverProducer rolloverProducer;

    /**
     * RolloverController: listens to cell over events and
     * repaints entered/exited rows.
     */
    private TreeRolloverController linkController;
    private boolean overwriteIcons;
    private Searchable searchable;
    private CellEditorRemover editorRemover;
    
    
    
    /**
     * Constructs a <code>JXTree</code> with a sample model. The default model
     * used by this tree defines a leaf node as any node without children.
     */
    public JXTree() {
	init();
    }

    /**
     * Constructs a <code>JXTree</code> with each element of the specified array
     * as the child of a new root node which is not displayed. By default, this
     * tree defines a leaf node as any node without children.
     *
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     *
     * @param value an array of objects that are children of the root.
     */
    public JXTree(Object[] value) {
        super(value);
	init();
    }

    /**
     * Constructs a <code>JXTree</code> with each element of the specified
     * Vector as the child of a new root node which is not displayed.
     * By default, this tree defines a leaf node as any node without children.
     *
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     *
     * @param value an Vector of objects that are children of the root.
     */
    public JXTree(Vector value) {
        super(value);
	init();
    }

    /**
     * Constructs a <code>JXTree</code> created from a Hashtable which does not
     * display with root. Each value-half of the key/value pairs in the HashTable
     * becomes a child of the new root node. By default, the tree defines a leaf
     * node as any node without children.
     *
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     *
     * @param value a Hashtable containing objects that are children of the root.
     */
    public JXTree(Hashtable value) {
        super(value);
	init();
    }

    /**
     * Constructs a <code>JXTree</code> with the specified TreeNode as its root,
     * which displays the root node. By default, the tree defines a leaf node as
     * any node without children.
     *
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     *
     * @param root root node of this tree
     */
    public JXTree(TreeNode root) {
        super(root, false);
        init();
    }

    /**
     * Constructs a <code>JXTree</code> with the specified TreeNode as its root,
     * which displays the root node and which decides whether a node is a leaf
     * node in the specified manner.
     *
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     *
     * @param root root node of this tree
     * @param asksAllowsChildren if true, only nodes that do not allow children
     * are leaf nodes; otherwise, any node without children is a leaf node;
     * @see javax.swing.tree.DefaultTreeModel#asksAllowsChildren
     */
    public JXTree(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
	init();
    }

    /**
     * Constructs an instance of <code>JXTree</code> which displays the root
     * node -- the tree is created using the specified data model.
     * 
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     * 
     * @param newModel
     *            the <code>TreeModel</code> to use as the data model
     */
    public JXTree(TreeModel newModel) {
        super(newModel);
        init();
    }

    @Override
    public void setModel(TreeModel newModel) {
        // To support delegation of convertValueToText() to the model...
        // JW: method needs to be set before calling super
        // otherwise there are size caching problems
        conversionMethod = getValueConversionMethod(newModel);
        super.setModel(newModel);
    }

    /**
     * Tries to find and return a method for Object --> to String conversion on the
     * model by reflection. Looks for a signature:
     * 
     * <pre> <code>
     *   String convertValueToText(Object);
     * </code> </pre>
     * 
     * 
     * 
     * PENDING JW: check - does this work with restricted permissions?
     * JW: widened access for testing - do test!
     * 
     * @param model the model to detect the method
     * @return the <code> Method </code> or null if the model has no method with
     *   the expected signature 
     */
    protected Method getValueConversionMethod(TreeModel model) {
        try {
            return model == null ? null : model.getClass().getMethod(
                    "convertValueToText", methodSignature);
        } catch (NoSuchMethodException ex) {
            LOG.finer("ex " + ex);
            LOG.finer("no conversionMethod in " + model.getClass());
        }
        return null;
    }

    
    @Override
    public String convertValueToText(Object value, boolean selected,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
        // Delegate to model, if possible. Otherwise fall back to superclass...
        if (value != null) {
            if (conversionMethod == null) {
                return value.toString();
            } else {
                try {
                    methodArgs[0] = value;
                    return (String) conversionMethod.invoke(getModel(),
                            methodArgs);
                } catch (Exception ex) {
                    LOG.finer("ex " + ex);
                    LOG.finer("can't invoke " + conversionMethod);
                }
            }
        }
        return "";
    }

    private void init() {
        // To support delegation of convertValueToText() to the model...
        // JW: need to set again (is done in setModel, but at call
        // in super constructor the field is not yet valid)
        conversionMethod = getValueConversionMethod(getModel());
        // Issue #233-swingx: default editor not bidi-compliant 
        // manually install an enhanced TreeCellEditor which 
        // behaves slightly better in RtoL orientation.
        // Issue #231-swingx: icons lost
        // Anyway, need to install the editor manually because
        // the default install in BasicTreeUI doesn't know about
        // the DelegatingRenderer and therefore can't see
        // the DefaultTreeCellRenderer type to delegate to. 
        // As a consequence, the icons are lost in the default
        // setup.
        // JW PENDING need to mimic ui-delegate default re-set?
        // JW PENDING alternatively, cleanup and use DefaultXXTreeCellEditor in incubator
        TreeCellRenderer xRenderer = getCellRenderer();
        if (xRenderer instanceof JXTree.DelegatingRenderer) {
            TreeCellRenderer delegate = ((JXTree.DelegatingRenderer) xRenderer).getDelegateRenderer();
            if (delegate instanceof DefaultTreeCellRenderer) { 
                setCellEditor(new DefaultXTreeCellEditor(this, (DefaultTreeCellRenderer) delegate));
            }   
        }

        // Register the actions that this class can handle.
        ActionMap map = getActionMap();
        map.put("expand-all", new Actions("expand-all"));
        map.put("collapse-all", new Actions("collapse-all"));
        map.put("find", createFindAction());
        // JW: this should be handled by the LF!
        KeyStroke findStroke = KeyStroke.getKeyStroke("control F");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(findStroke, "find");

    }

    /**
     * A small class which dispatches actions.
     * TODO: Is there a way that we can make this static?
     */
    private class Actions extends UIAction {
        Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
            if ("expand-all".equals(getName())) {
		expandAll();
            }
            else if ("collapse-all".equals(getName())) {
                collapseAll();
            }
        }
    }


//-------------------- search support
    
    private Action createFindAction() {
        Action findAction = new UIAction("find") {

            public void actionPerformed(ActionEvent e) {
                doFind();
                
            }
            
        };
        return findAction;
    }

    protected void doFind() {
        SearchFactory.getInstance().showFindInput(this, getSearchable());
    }

    /**
     * 
     * @return a not-null Searchable for this editor.
     */
    public Searchable getSearchable() {
        if (searchable == null) {
            searchable = new TreeSearchable();
        }
        return searchable;
    }

    /**
     * sets the Searchable for this editor. If null, a default 
     * searchable will be used.
     * 
     * @param searchable
     */
    public void setSearchable(Searchable searchable) {
        this.searchable = searchable;
    }
    
 
    /**
     * A searchable targetting the visible rows of a JXTree.
     * 
     * PENDING: value to string conversion should behave as nextMatch (?) which
     * uses the convertValueToString().
     * 
     */
    public class TreeSearchable extends AbstractSearchable {

        protected void findMatchAndUpdateState(Pattern pattern, int startRow,
                boolean backwards) {
            SearchResult searchResult = null;
            if (backwards) {
                for (int index = startRow; index >= 0 && searchResult == null; index--) {
                    searchResult = findMatchAt(pattern, index);
                }
            } else {
                for (int index = startRow; index < getSize()
                        && searchResult == null; index++) {
                    searchResult = findMatchAt(pattern, index);
                }
            }
            updateState(searchResult);

        }

        protected SearchResult findExtendedMatch(Pattern pattern, int row) {
            return findMatchAt(pattern, row);
        }

        /**
         * Matches the cell content at row/col against the given Pattern.
         * Returns an appropriate SearchResult if matching or null if no
         * matching
         * 
         * @param pattern
         * @param row
         *            a valid row index in view coordinates
         *            a valid column index in view coordinates
         * @return an appropriate <code>SearchResult</code> if matching or
         * null if no matching
         */
        protected SearchResult findMatchAt(Pattern pattern, int row) {
            TreePath path = getPathForRow(row);
            Object value = null;
            if (path != null) {
                value = path.getLastPathComponent();
            }
            if (value != null) {
                Matcher matcher = pattern.matcher(value.toString());
                if (matcher.find()) {
                    return createSearchResult(matcher, row, -1);
                }
            }
            return null;
        }

        protected int getSize() {
            return getRowCount();
        }

        protected void moveMatchMarker() {
            int row = lastSearchResult.foundRow;
            setSelectionRow(row);
            if (row >= 0) {
                scrollRowToVisible(row);
            }

        }

    }
    
    /**
     * Collapses all nodes in the tree table.
     */
    public void collapseAll() {
        for (int i = getRowCount() - 1; i >= 0 ; i--) {
            collapseRow(i);
        }
    }

    /**
     * Expands all nodes in the tree table.
     */
    public void expandAll() {
        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }


    /**
     * overridden to always return a not-null array 
     * (following SwingX convention).
     */
    @Override
    public int[] getSelectionRows() {
        int[] rows = super.getSelectionRows();
        return rows != null ? rows : EMPTY_INT_ARRAY; 
    }
    
    

    /**
     * overridden to always return a not-null array 
     * (following SwingX convention).
     */
    @Override
    public TreePath[] getSelectionPaths() {
        // TODO Auto-generated method stub
        TreePath[] paths = super.getSelectionPaths();
        return paths != null ? paths : EMPTY_TREEPATH_ARRAY; 
    }

    public HighlighterPipeline getHighlighters() {
        return highlighters;
    }

    /** Assigns a HighlighterPipeline to the table. */
    public void setHighlighters(HighlighterPipeline pipeline) {
        HighlighterPipeline old = getHighlighters();
        if (old != null) {
            old.removeChangeListener(getHighlighterChangeListener());
        }
        highlighters = pipeline;
        if (highlighters != null) {
            highlighters.addChangeListener(getHighlighterChangeListener());
        }
        firePropertyChange("highlighters", old, getHighlighters());
    }

    private ChangeListener getHighlighterChangeListener() {
        if (highlighterChangeListener == null) {
            highlighterChangeListener = new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    repaint();
                    
                }
                
            };
        }
        return highlighterChangeListener;
    }


    /**
     * Property to enable/disable rollover support. This can be enabled
     * to show "live" rollover behaviour, f.i. the cursor over LinkModel cells. 
     * Default is disabled.
     * @param rolloverEnabled
     */
    public void setRolloverEnabled(boolean rolloverEnabled) {
        boolean old = isRolloverEnabled();
        if (rolloverEnabled == old) return;
        if (rolloverEnabled) {
            rolloverProducer = createRolloverProducer();
            addMouseListener(rolloverProducer);
            addMouseMotionListener(rolloverProducer);
            getLinkController().install(this);
        } else {
            removeMouseListener(rolloverProducer);
            removeMouseMotionListener(rolloverProducer);
            rolloverProducer = null;
            getLinkController().release();
        }
        firePropertyChange("rolloverEnabled", old, isRolloverEnabled());
    }

    protected TreeRolloverController getLinkController() {
        if (linkController == null) {
            linkController = createLinkController();
        }
        return linkController;
    }

    protected TreeRolloverController createLinkController() {
        return new TreeRolloverController();
    }

    /**
     * creates and returns the RolloverProducer to use with this tree.
     * A "hit" for rollover is covering the total width of the tree.
     * Additionally, a pressed to the right (but outside of the label bounds)
     * is re-dispatched as a pressed just inside the label bounds. This 
     * is a first go for #166-swingx.
     * 
     * @return <code>RolloverProducer</code> to use with this tree
     */
    protected RolloverProducer createRolloverProducer() {
        RolloverProducer r = new RolloverProducer() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                JXTree tree = (JXTree) e.getComponent();
                Point mousePoint = e.getPoint();
              int labelRow = tree.getRowForLocation(mousePoint.x, mousePoint.y);
              // default selection
              if (labelRow >= 0) return;
              int row = tree.getClosestRowForLocation(mousePoint.x, mousePoint.y);
              Rectangle bounds = tree.getRowBounds(row);
              if (bounds == null) {
                  row = -1;
              } else {
                  if ((bounds.y + bounds.height < mousePoint.y) || 
                       bounds.x > mousePoint.x)   {
                      row = -1;
                  }
              }
              // no hit
              if (row < 0) return;
              tree.dispatchEvent(new MouseEvent(tree, e.getID(), e.getWhen(), 
                      e.getModifiers(), bounds.x + bounds.width - 2, mousePoint.y,
                      e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            }

            protected void updateRolloverPoint(JComponent component,
                    Point mousePoint) {
                JXTree tree = (JXTree) component;
                int row = tree.getClosestRowForLocation(mousePoint.x, mousePoint.y);
                Rectangle bounds = tree.getRowBounds(row);
                if (bounds == null) {
                    row = -1;
                } else {
                    if ((bounds.y + bounds.height < mousePoint.y) || 
                            bounds.x > mousePoint.x)   {
                           row = -1;
                       }
                }
                int col = row < 0 ? -1 : 0;
                rollover.x = col;
                rollover.y = row;
            }

        };
        return r;
    }

  
   
    /**
     * returns the rolloverEnabled property.
     *
     * TODO: Why doesn't this just return rolloverEnabled???
     *
     * @return if rollober is enabled.
     */
    public boolean isRolloverEnabled() {
        return rolloverProducer != null;
    }


    /**
     * listens to rollover properties. 
     * Repaints effected component regions.
     * Updates link cursor.
     * 
     * @author Jeanette Winzenburg
     */
    public  class TreeRolloverController<T extends JTree>  extends RolloverController<T> {
    
        private Cursor oldCursor;
        
//    -------------------------------------JTree rollover
        
        protected void rollover(Point oldLocation, Point newLocation) {
            setRolloverCursor(newLocation);
            //setLinkCursor(list, newLocation);
            // JW: conditional repaint not working?
            component.repaint();
//            if (oldLocation != null) {
//                Rectangle r = tree.getRowBounds(oldLocation.y);
////                r.x = 0;
////                r.width = table.getWidth();
//                if (r != null)
//                tree.repaint(r);
//            }
//            if (newLocation != null) {
//                Rectangle r = tree.getRowBounds(newLocation.y);
////                r.x = 0;
////                r.width = table.getWidth();
//                if (r != null)
//                tree.repaint(r);
//            }
        }


        private void setRolloverCursor(Point location) {
            if (hasRollover(location)) {
                if (oldCursor == null) {
                    oldCursor = component.getCursor();
                    component.setCursor(Cursor
                            .getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            } else {
                if (oldCursor != null) {
                    component.setCursor(oldCursor);
                    oldCursor = null;
                }
            }

        }


        protected RolloverRenderer getRolloverRenderer(Point location, boolean prepare) {
            TreeCellRenderer renderer = component.getCellRenderer();
            RolloverRenderer rollover = renderer instanceof RolloverRenderer 
                ? (RolloverRenderer) renderer : null;
            if ((rollover != null) && !rollover.isEnabled()) {
                rollover = null;
            }
            if ((rollover != null) && prepare) {
                TreePath path = component.getPathForRow(location.y);
                Object element = path != null ? path.getLastPathComponent() : null;
                renderer.getTreeCellRendererComponent(component, element, false, 
                        false, false, 
                        location.y, false);
            }
            return rollover;
        }


        protected Point getFocusedCell() {
            // TODO Auto-generated method stub
            return null;
        }

    }


    
    private DelegatingRenderer getDelegatingRenderer() {
        if (delegatingRenderer == null) {
            // only called once... to get hold of the default?
            delegatingRenderer = new DelegatingRenderer();
            delegatingRenderer.setDelegateRenderer(super.getCellRenderer());
        }
        return delegatingRenderer;
    }


    public TreeCellRenderer getCellRenderer() {
        return getDelegatingRenderer();
    }

    public void setCellRenderer(TreeCellRenderer renderer) {
        // PENDING: do something against recursive setting
        // == multiple delegation...
        getDelegatingRenderer().setDelegateRenderer(renderer);
        super.setCellRenderer(delegatingRenderer);
    }

    /**
     * sets the icon for the handle of an expanded node.
     * 
     * Note: this will only succeed if the current ui delegate is
     * a BasicTreeUI otherwise it will do nothing.
     * 
     * @param expanded
     */
    public void setExpandedIcon(Icon expanded) {
        if (getUI() instanceof BasicTreeUI) {
            ((BasicTreeUI) getUI()).setExpandedIcon(expanded);
        }
    }
    
    /**
     * sets the icon for the handel of a collapsed node.
     * 
     * Note: this will only succeed if the current ui delegate is
     * a BasicTreeUI otherwise it will do nothing.
     *  
     * @param collapsed
     */
    public void setCollapsedIcon(Icon collapsed) {
        if (getUI() instanceof BasicTreeUI) {
            ((BasicTreeUI) getUI()).setCollapsedIcon(collapsed);
        }
    }
    
    /**
     * set the icon for a leaf node.
     * 
     * Note: this will only succeed if current renderer is a 
     * DefaultTreeCellRenderer.
     * 
     * @param leafIcon
     */
    public void setLeafIcon(Icon leafIcon) {
        getDelegatingRenderer().setLeafIcon(leafIcon);
        
    }
    
    /**
     * set the icon for a open non-leaf node.
     * 
     * Note: this will only succeed if current renderer is a 
     * DefaultTreeCellRenderer.
     * 
     * @param openIcon
     */
    public void setOpenIcon(Icon openIcon) {
        getDelegatingRenderer().setOpenIcon(openIcon);
    }
    
    /**
     * set the icon for a closed non-leaf node.
     * 
     * Note: this will only succeed if current renderer is a 
     * DefaultTreeCellRenderer.
     * 
     * @param closedIcon
     */
    public void setClosedIcon(Icon closedIcon) {
        getDelegatingRenderer().setClosedIcon(closedIcon);
    }
    
    /**
     * Property to control whether per-tree icons should be 
     * copied to the renderer on setCellRenderer.
     * 
     * the default is false for backward compatibility.
     * 
     * PENDING: should update the current renderer's icons when 
     * setting to true?
     * 
     * @param overwrite
     */
    public void setOverwriteRendererIcons(boolean overwrite) {
        if (overwriteIcons == overwrite) return;
        boolean old = overwriteIcons;
        this.overwriteIcons = overwrite;
        firePropertyChange("overwriteRendererIcons", old, overwrite);
    }

    public boolean isOverwriteRendererIcons() {
        return overwriteIcons;
    }
    
    public class DelegatingRenderer implements TreeCellRenderer, RolloverRenderer {
        private Icon    closedIcon = null;
        private Icon    openIcon = null;
        private Icon    leafIcon = null;
       
        private TreeCellRenderer delegate;
        
        public DelegatingRenderer() {
            initIcons(new DefaultTreeCellRenderer());
        }

        /**
         * initially sets the icons to the defaults as given
         * by a DefaultTreeCellRenderer.
         * 
         * @param renderer
         */
        private void initIcons(DefaultTreeCellRenderer renderer) {
            closedIcon = renderer.getDefaultClosedIcon();
            openIcon = renderer.getDefaultOpenIcon();
            leafIcon = renderer.getDefaultLeafIcon();
        }

        /**
         * Set the delegate renderer. 
         * Updates the folder/leaf icons. 
         * 
         * THINK: how to update? always override with this.icons, only
         * if renderer's icons are null, update this icons if they are not,
         * update all if only one is != null.... ??
         * 
         * @param delegate
         */
        public void setDelegateRenderer(TreeCellRenderer delegate) {
            if (delegate == null) {
                delegate = new DefaultTreeCellRenderer();
            }
            this.delegate = delegate;
            updateIcons();
        }
        
        /**
         * tries to set the renderers icons. Can succeed only if the
         * delegate is a DefaultTreeCellRenderer.
         * THINK: how to update? always override with this.icons, only
         * if renderer's icons are null, update this icons if they are not,
         * update all if only one is != null.... ??
         * 
         */
        private void updateIcons() {
            if (!isOverwriteRendererIcons()) return;
            setClosedIcon(closedIcon);
            setOpenIcon(openIcon);
            setLeafIcon(leafIcon);
        }

        public void setClosedIcon(Icon closedIcon) {
            if (delegate instanceof DefaultTreeCellRenderer) {
                ((DefaultTreeCellRenderer) delegate).setClosedIcon(closedIcon);
            }
            this.closedIcon = closedIcon;
        }
        
        public void setOpenIcon(Icon openIcon) {
            if (delegate instanceof DefaultTreeCellRenderer) {
                ((DefaultTreeCellRenderer) delegate).setOpenIcon(openIcon);
            }
            this.openIcon = openIcon;
        }
        
        public void setLeafIcon(Icon leafIcon) {
            if (delegate instanceof DefaultTreeCellRenderer) {
                ((DefaultTreeCellRenderer) delegate).setLeafIcon(leafIcon);
            }
            this.leafIcon = leafIcon;
        }
        
        //--------------- TreeCellRenderer
        
        public TreeCellRenderer getDelegateRenderer() {
            return delegate;
        }
            public Component getTreeCellRendererComponent(JTree tree, Object value, 
                    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component result = delegate.getTreeCellRendererComponent(tree, value, 
                        selected, expanded, leaf, row, hasFocus);

                    if (highlighters != null) {
                        getComponentAdapter().row = row;
                        result = highlighters.apply(result, getComponentAdapter());
                    }

                 return result;
            }
            
            //------------------ RolloverRenderer
            
            public boolean isEnabled() {
                return (delegate instanceof RolloverRenderer) && 
                   ((RolloverRenderer) delegate).isEnabled();
            }
            
            public void doClick() {
                if (isEnabled()) {
                    ((RolloverRenderer) delegate).doClick();
                }
            }

    }

    
//----------------------- edit
    
    /**
     * Overridden to terminate edits on focusLost. 
     * This method updates the internal CellEditorRemover.
     * 
     * @see #updateEditorRemover()
     */
    @Override
    public void startEditingAtPath(TreePath path) {
        super.startEditingAtPath(path);
        if (isEditing()) {
            updateEditorRemover();
        }
    }

    
    /**
     * Overridden to release the CellEditorRemover, if any.
     */
    @Override
    public void removeNotify() {
        if (editorRemover != null) {
            editorRemover.release();
            editorRemover = null;
        }
        super.removeNotify();
    }

    /**
     * Lazily creates and updates the internal CellEditorRemover.
     * 
     *
     */
    private void updateEditorRemover() {
        if (editorRemover == null) {
            editorRemover = new CellEditorRemover();
        }
        editorRemover.updateKeyboardFocusManager();
    }

    /** This class tracks changes in the keyboard focus state. It is used
     * when the JXTree is editing to determine when to terminate the edit.
     * If focus switches to a component outside of the JXTree, but in the
     * same window, this will terminate editing. The exact terminate 
     * behaviour is controlled by the invokeStopEditing property.
     * 
     * @see javax.swing.JTree#setInvokesStopCellEditing(boolean)
     * 
     */
    public class CellEditorRemover implements PropertyChangeListener {
        /** the focusManager this is listening to. */
        KeyboardFocusManager focusManager;

        public CellEditorRemover() {
            updateKeyboardFocusManager();
        }

        /**
         * Updates itself to listen to the current KeyboardFocusManager. 
         *
         */
        public void updateKeyboardFocusManager() {
            KeyboardFocusManager current = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            setKeyboardFocusManager(current);
        }

        /**
         * stops listening.
         *
         */
        public void release() {
            setKeyboardFocusManager(null);
        }
        
        /**
         * Sets the focusManager this is listening to. 
         * Unregisters/registers itself from/to the old/new manager, 
         * respectively. 
         * 
         * @param current the KeyboardFocusManager to listen too.
         */
        private void setKeyboardFocusManager(KeyboardFocusManager current) {
            if (focusManager == current)
                return;
            KeyboardFocusManager old = focusManager;
            if (old != null) {
                old.removePropertyChangeListener("permanentFocusOwner", this);
            }
            focusManager = current;
            if (focusManager != null) {
                focusManager.addPropertyChangeListener("permanentFocusOwner",
                        this);
            }

        }
        public void propertyChange(PropertyChangeEvent ev) {
            if (!isEditing()) {
                return;
            }

            Component c = focusManager.getPermanentFocusOwner();
            while (c != null) {
                JXTree tree = JXTree.this;
                if (c == tree) {
                    // focus remains inside the table
                    return;
                } else if ((c instanceof Window) ||
                           (c instanceof Applet && c.getParent() == null)) {
                    if (c == SwingUtilities.getRoot(tree)) {
                        if (tree.getInvokesStopCellEditing()) {
                            tree.stopEditing();
                        }
                        if (tree.isEditing()) {
                            tree.cancelEditing();
                        }
                    }
                    break;
                }
                c = c.getParent();
            }
        }
    }

    
    protected ComponentAdapter getComponentAdapter() {
        return dataAdapter;
    }

    private final ComponentAdapter dataAdapter = new TreeAdapter(this);

    protected static class TreeAdapter extends ComponentAdapter {
        private final JXTree tree;

        /**
         * Constructs a <code>TableCellRenderContext</code> for the specified
         * target component.
         *
         * @param component the target component
         */
        public TreeAdapter(JXTree component) {
            super(component);
            tree = component;
        }
        public JXTree getTree() {
            return tree;
        }

        public boolean hasFocus() {
            return tree.isFocusOwner() && (tree.getLeadSelectionRow() == row);
        }

        public Object getValueAt(int row, int column) {
            TreePath path = tree.getPathForRow(row);
            return path.getLastPathComponent();
        }

        public Object getFilteredValueAt(int row, int column) {
            /** TODO: Implement filtering */
            return getValueAt(row, column);
        }

        public boolean isSelected() {
            return tree.isRowSelected(row);
        }

        @Override
        public boolean isExpanded() {
            return tree.isExpanded(row);
        }

        @Override
        public boolean isLeaf() {
            return tree.getModel().isLeaf(getValue());
        }

        public boolean isCellEditable(int row, int column) {
            return false;	/** TODO:  */
        }

        public void setValueAt(Object aValue, int row, int column) {
            /** TODO:  */
        }
        
        public String getColumnName(int columnIndex) {
            return "Column_" + columnIndex;
        }
        
        public String getColumnIdentifier(int columnIndex) {
            return null;
        }
    }


}
