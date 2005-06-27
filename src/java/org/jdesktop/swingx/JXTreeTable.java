/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.swingx;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableCellEditor;
import org.jdesktop.swingx.treetable.TreeTableModel;


/**
 * <p><code>JXTreeTable</code> is a specialized {@link javax.swing.JTable table}
 * consisting of a single column in which to display hierarchical data, and any
 * number of other columns in which to display regular data. The interface for
 * the data model used by a <code>JXTreeTable</code> is
 * {@link org.jdesktop.swingx.treetable.TreeTableModel}. It extends the
 * {@link javax.swing.tree.TreeModel} interface to allow access to cell data by
 * column indices within each node of the tree hierarchy.</p>
 *
 * <p>The most straightforward way create and use a <code>JXTreeTable</code>, is to
 * first create a suitable data model for it, and pass that to a
 * <code>JXTreeTable</code> constructor, as shown below:
 * <pre>
 *  TreeTableModel  treeTableModel = new FileSystemModel(); // any TreeTableModel
 *  JXTreeTable      treeTable = new JXTreeTable(treeTableModel);
 *  JScrollPane     scrollpane = new JScrollPane(treeTable);
 * </pre>
 * See {@link javax.swing.JTable} for an explanation of why putting the treetable
 * inside a scroll pane is necessary.</p>
 *
 * <p>A single treetable model instance may be shared among more than one
 * <code>JXTreeTable</code> instances. To access the treetable model, always call
 * {@link #getTreeTableModel() getTreeTableModel} and
 * {@link #setTreeTableModel(org.jdesktop.swingx.treetable.TreeTableModel) setTreeTableModel}.
 * <code>JXTreeTable</code> wraps the supplied treetable model inside a private
 * adapter class to adapt it to a {@link javax.swing.table.TableModel}. Although
 * the model adapter is accessible through the {@link #getModel() getModel} method, you
 * should avoid accessing and manipulating it in any way. In particular, each
 * model adapter instance is tightly bound to a single table instance, and any
 * attempt to share it with another table (for example, by calling
 * {@link #setModel(javax.swing.table.TableModel) setModel})
 * will throw an <code>IllegalArgumentException</code>!
 *
 * @author Philip Milne
 * @author Scott Violet
 * @author Ramesh Gupta
 */
public class JXTreeTable extends JXTable {
    // TOTAL HACK to fix icons that disappeared in a regression after M3!
    private Icon    collapsedIcon = null;
    private Icon    expandedIcon = null;
    private Icon    closedIcon = null;
    private Icon    openIcon = null;
    private Icon    leafIcon = null;

    /**
     * Renderer used to render cells within the
     * {@link #isHierarchical(int) hierarchical} column.
     */
    private TreeTableCellRenderer renderer = null;
    // renderer extends JXTree and implements TableCellRenderer

    /**
     * Constructs a JXTreeTable using a
     * {@link org.jdesktop.swingx.treetable.DefaultTreeTableModel}.
     */
    public JXTreeTable() {
        this(new DefaultTreeTableModel());
    }

    /**
     * Constructs a JXTreeTable using the specified
     * {@link org.jdesktop.swingx.treetable.TreeTableModel}.
     *
     * @param treeModel model for the JXTreeTable
     */
    public JXTreeTable(TreeTableModel treeModel) {
        // Implementation note:
        // Make sure that the SAME instance of treeModel is passed to the
        // constructor for TreeTableCellRenderer as is passed in the first
        // argument to the following chained constructor for this JXTreeTable:
        this(treeModel, new JXTreeTable.TreeTableCellRenderer(treeModel));
    }

    /**
     * Constructs a <code>JXTreeTable</code> using the specified
     * {@link org.jdesktop.swing.treetable.TreeTableModel} and
     * {@link org.jdesktop.swing.treetable.TreeTableCellRenderer}. The renderer
     * must have been constructed using the same instance of
     * {@link org.jdesktop.swing.treetable.TreeTableModel} as passed to this
     * constructor.
     *
     * @param treeModel model for the JXTreeTable
     * @param renderer cell renderer for the tree portion of this JXTreeTable instance.
     * @throws IllegalArgumentException if an attempt is made to instantiate
     * JXTreeTable and TreeTableCellRenderer with different instances of TreeTableModel.
     */
    private JXTreeTable(TreeTableModel treeModel, TreeTableCellRenderer renderer) {
        // To avoid unnecessary object creation, such as the construction of a
        // DefaultTableModel, it is better to invoke super(TreeTableModelAdapter)
        // directly, instead of first invoking super() followed by a call to
        // setTreeTableModel(TreeTableModel).

        // Adapt tree model to table model before invoking super()
        super(new TreeTableModelAdapter(treeModel, renderer));

        // Enforce referential integrity; bail on fail
        if (treeModel != renderer.getModel()) { // do not use assert here!
            throw new IllegalArgumentException("Mismatched TreeTableModel");
        }

        // renderer-related initialization -- also called from setTreeTableModel()
        init(renderer); // private method
        initActions();
        // disable sorting
        super.setSortable(false);
        // Install the default editor.
        setDefaultEditor(AbstractTreeTableModel.hierarchicalColumnClass,
            new TreeTableCellEditor(renderer));

        // No grid.
        setShowGrid(false); // superclass default is "true"

        // Default intercell spacing
        setIntercellSpacing(spacing); // for both row margin and column margin

        // JTable supports row margins and intercell spacing, but JTree doesn't.
        // We must reconcile the differences in the semantics of rowHeight as
        // understood by JTable and JTree by overriding both setRowHeight() and
        // setRowMargin();
        boolean heightSet = isXTableRowHeightSet;
        setRowHeight(getRowHeight()); // call overridden setRowHeight()
        isXTableRowHeightSet = heightSet;
        setRowMargin(getRowMargin()); // call overridden setRowMargin()

    }


    private void initActions() {
        // Register the actions that this class can handle.
        ActionMap map = getActionMap();
        map.put("expand-all", new Actions("expand-all"));
        map.put("collapse-all", new Actions("collapse-all"));
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

    /** 
     * overridden to do nothing. 
     * 
     * TreeTable is not sortable by default, because 
     * Sorters/Filters currently don't work properly.
     * 
     */
    public void setSortable(boolean sortable) {
        // no-op
    }

    /**
     * <p>Sets whether the table draws horizontal lines between cells. It draws
     * the lines if <code>show</code> is true; otherwise it doesn't. By default,
     * a table draws the lines.</p>
     *
     * <p>If you want the lines to be drawn, make sure that the row margin or
     * horizontal intercell spacing is greater than zero.</p>
     *
     * @param show true, if horizontal lines should be drawn; false, if lines
     * should not be drawn
     * @see javax.swing.JTable#getShowHorizontalLines() getShowHorizontalLines
     * @see #setRowMargin(int) setRowMargin
     * @see javax.swing.JTable#setIntercellSpacing(java.awt.Dimension) setIntercellSpacing
     */
    public void setShowHorizontalLines(boolean show) {
        super.setShowHorizontalLines(show);
    }

    /**
     * <p>Sets whether the table draws vertical lines between cells. It draws
     * the lines if <code>show</code> is true; otherwise it doesn't. By default,
     * a table draws the lines.</p>
     *
     * <p>If you want the lines to be drawn, make sure that the column margin or
     * vertical intercell spacing is greater than zero.</p>
     *
     * @param show true, if vertical lines should be drawn; false, if lines
     * should not be drawn
     * @see javax.swing.JTable#getShowVerticalLines() getShowVerticalLines
     * @see #setColumnMargin(int) setColumnMargin
     * @see javax.swing.JTable#setIntercellSpacing(java.awt.Dimension) setIntercellSpacing
     */
    public void setShowVerticalLines(boolean show) {
        super.setShowVerticalLines(show);
    }

    /**
     * Overriden to invoke repaint for the particular location if
     * the column contains the tree. This is done as the tree editor does
     * not fill the bounds of the cell, we need the renderer to paint
     * the tree in the background, and then draw the editor over it.
     * You should not need to call this method directly.
     *
     * {@inheritDoc}
     */
    public boolean editCellAt(int row, int column, EventObject e) {
        expandOrCollapseNode(e);    // RG: Fix Issue 49!
        boolean canEdit = super.editCellAt(row, column, e);
        if (canEdit && isHierarchical(column)) {
            repaint(getCellRect(row, column, false));
        }
        return canEdit;
    }

    private void expandOrCollapseNode(EventObject e) {
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            // If the modifiers are not 0 (or the left mouse button),
            // tree may try and toggle the selection, and table
            // will then try and toggle, resulting in the
            // selection remaining the same. To avoid this, we
            // only dispatch when the modifiers are 0 (or the left mouse
            // button).
            if (me.getModifiers() == 0 ||
                me.getModifiers() == InputEvent.BUTTON1_MASK) {
                final int count = getColumnCount();
                for (int i = count - 1; i >= 0; i--) {
                    if (isHierarchical(i)) {
                        
                        int savedHeight = renderer.getRowHeight();
                        renderer.setRowHeight(getRowHeight());
                        MouseEvent pressed = new MouseEvent
                            (renderer,
                             me.getID(),
                             me.getWhen(),
                             me.getModifiers(),
                             me.getX() - getCellRect(0, i, false).x,
                             me.getY(),
                             me.getClickCount(),
                             me.isPopupTrigger());
                        renderer.dispatchEvent(pressed);
                        // For Mac OS X, we need to dispatch a MOUSE_RELEASED as well
                        MouseEvent released = new MouseEvent
                            (renderer,
                             java.awt.event.MouseEvent.MOUSE_RELEASED,
                             pressed.getWhen(),
                             pressed.getModifiers(),
                             pressed.getX(),
                             pressed.getY(),
                             pressed.getClickCount(),
                             pressed.isPopupTrigger());
                        renderer.dispatchEvent(released);
                        renderer.setRowHeight(savedHeight);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Overridden to provide a workaround for BasicTableUI anomaly. Make sure
     * the UI never tries to resize the editor. The UI currently uses different
     * techniques to paint the renderers and editors. So, overriding setBounds()
     * is not the right thing to do for an editor. Returning -1 for the
     * editing row in this case, ensures the editor is never painted.
     *
     * {@inheritDoc}
     */
    public int getEditingRow() {
        return isHierarchical(editingColumn) ? -1 : editingRow;
    }

    /**
     * Returns the actual row that is editing as <code>getEditingRow</code>
     * will always return -1.
     */
    private int realEditingRow() {
        return editingRow;
    }

    /**
     * Sets the data model for this JXTreeTable to the specified
     * {@link org.jdesktop.swingx.treetable.TreeTableModel}. The same data model
     * may be shared by any number of JXTreeTable instances.
     *
     * @param treeModel data model for this JXTreeTable
     */
    public void setTreeTableModel(TreeTableModel treeModel) {
        // The original TreeTable implementation didn't support this signature.

//      CHANGED LINE TO CORRECT ISSUE 151 - rlopes
      	//renderer = new TreeTableCellRenderer(treeModel);
        renderer.setModel(treeModel);
        // Adapt tree model to table model before invoking setModel()
        setModel(new TreeTableModelAdapter(treeModel, renderer));
        // Enforce referential integrity; bail on fail
        if (treeModel != renderer.getModel()) { // do not use assert here!
            throw new IllegalArgumentException("Mismatched TreeTableModel");
        }
//      COMMENTED LINE TO CORRECT ISSUE 151 - rlopes        
//    		 Not needed because the renderer doesn't change, and is already initialized
//         in the constructor   
//         init(renderer); // renderer is a JTree

        // Install the default editor.
        setDefaultEditor(AbstractTreeTableModel.hierarchicalColumnClass,
            new TreeTableCellEditor(renderer));

        // JTable supports row margins and intercell spacing, but JTree doesn't.
        // We must reconcile the differences in the semantics of rowHeight as
        // understood by JTable and JTree by overriding both setRowHeight() and
        // setRowMargin();
        boolean rememberRowHeightSet = isXTableRowHeightSet;
        setRowHeight(getRowHeight()); // call overridden setRowHeight()
        isXTableRowHeightSet = rememberRowHeightSet;
        setRowMargin(getRowMargin()); // call overridden setRowMargin()
    }

    /**
     * Returns the underlying TreeTableModel for this JXTreeTable.
     *
     * @return the underlying TreeTableModel for this JXTreeTable
     */
    public TreeTableModel getTreeTableModel() {
        return ((TreeTableModelAdapter) getModel()).getTreeTableModel();
    }

    /**
     * <p>Overrides superclass version to make sure that the specified
     * {@link javax.swing.table.TableModel} is compatible with JXTreeTable before
     * invoking the inherited version.</p>
     *
     * <p>Because JXTreeTable internally adapts an
     * {@link org.jdesktop.swingx.treetable.TreeTableModel} to make it a compatible
     * TableModel, <b>this method should never be called directly</b>. Use
     * {@link #setTreeTableModel(org.jdesktop.swingx.treetable.TreeTableModel) setTreeTableModel} instead.</p>
     *
     * <p>While it is possible to obtain a reference to this adapted
     * version of the TableModel by calling {@link javax.swing.JTable#getModel()},
     * any attempt to call setModel() with that adapter will fail because
     * the adapter might have been bound to a different JXTreeTable instance. If
     * you want to extract the underlying TreeTableModel, which, by the way,
     * <em>can</em> be shared, use {@link #getTreeTableModel() getTreeTableModel}
     * instead</p>.
     *
     * @param tableModel must be a TreeTableModelAdapter
     * @throws IllegalArgumentException if the specified tableModel is not an
     * instance of TreeTableModelAdapter
     */
    public final void setModel(TableModel tableModel) { // note final keyword
        if (tableModel instanceof TreeTableModelAdapter) {
            if (((TreeTableModelAdapter) tableModel).getTreeTable() == null) {
                // Passing the above test ensures that this method is being
                // invoked either from JXTreeTable/JTable constructor or from
                // setTreeTableModel(TreeTableModel)
                super.setModel(tableModel); // invoke superclass version

                ((TreeTableModelAdapter) tableModel).bind(this); // permanently bound
                // Once a TreeTableModelAdapter is bound to any JXTreeTable instance,
                // invoking JXTreeTable.setModel() with that adapter will throw an
                // IllegalArgumentException, because we really want to make sure
                // that a TreeTableModelAdapter is NOT shared by another JXTreeTable.
            }
            else {
                throw new IllegalArgumentException("model already bound");
            }
        }
        else {
            throw new IllegalArgumentException("unsupported model type");
        }
    }

    /**
     * Throws UnsupportedOperationException because variable height rows are
     * not supported.
     *
     * @param row ignored
     * @param rowHeight ignored
     * @throws UnsupportedOperationException because variable height rows are
     * not supported
     */
    public final void setRowHeight(int row, int rowHeight) {
        throw new UnsupportedOperationException("variable height rows not supported");
    }

    /**
     * Sets the row height for this JXTreeTable. Reconciles semantic differences
     * between JTable and JTree regarding row height.
     *
     * @param rowHeight height of a row
     */
    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
        adjustTreeRowHeight(); // JTree doesn't have setRowMargin. So adjust.
    }

    /**
     * <p>Sets the margin between columns.</p>
     *
     * <p>If you set the column margin to zero, make sure that you also set
     * <code>showVerticalLines</code> to <code>false</code>.</p>
     *
     * @param columnMargin margin between columns; must be greater than or equal to zero.
     * @see #setShowVerticalLines(boolean) setShowVerticalLines
     */
    public void setColumnMargin(int columnMargin) {
        super.setColumnMargin(columnMargin);
    }

    /**
     * <p>Overridden to ensure that private renderer state is kept in sync with the
     * state of the component. Calls the inherited version after performing the
     * necessary synchronization. If you override this method, make sure you call
     * this version from your version of this method.</p>
     *
     * <p>If you set row margin to zero, make sure that you also set
     * <code>showHorizontalLines</code> to <code>false</code>.</p>
     *
     * @param rowMargin margin or intercell spacing between rows
     * @see #setShowHorizontalLines(boolean) setShowHorizontalLines
     */
    public void setRowMargin(int rowMargin) {
        // No need to override setIntercellSpacing, because the change in
        // rowMargin will be funneled through this method anyway.
        super.setRowMargin(rowMargin);
        adjustTreeRowHeight(); // JTree doesn't have setRowMargin. So adjust.
    }

    /**
     * Reconciles semantic differences between JTable and JTree regarding
     * row height.
     */
    private void adjustTreeRowHeight() {
        final int treeRowHeight = rowHeight + (rowMargin << 1);
        if (renderer != null && renderer.getRowHeight() != treeRowHeight) {
            renderer.setRowHeight(treeRowHeight);
        }
    }

    /**
     * <p>Overridden to ensure that private renderer state is kept in sync with the
     * state of the component. Calls the inherited version after performing the
     * necessary synchronization. If you override this method, make sure you call
     * this version from your version of this method.</p>
     *
     * <p>This version maps the selection mode used by the renderer to match the
     * selection mode specified for the table. Specifically, the modes are mapped
     * as follows:
     * <pre>
     *  ListSelectionModel.SINGLE_INTERVAL_SELECTION: TreeSelectionModel.CONTIGUOUS_TREE_SELECTION;
     *  ListSelectionModel.MULTIPLE_INTERVAL_SELECTION: TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION;
     *  any other (default): TreeSelectionModel.SINGLE_TREE_SELECTION;
     * </pre>
     *
     * {@inheritDoc}
     *
     * @param mode any of the table selection modes
     */
    public void setSelectionMode(int mode) {
        if (renderer != null) {
            switch (mode) {
                case ListSelectionModel.SINGLE_INTERVAL_SELECTION: {
                    renderer.getSelectionModel().setSelectionMode(
                        TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
                    break;
                }
                case ListSelectionModel.MULTIPLE_INTERVAL_SELECTION: {
                    renderer.getSelectionModel().setSelectionMode(
                        TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                    break;
                }
                default: {
                    renderer.getSelectionModel().setSelectionMode(
                        TreeSelectionModel.SINGLE_TREE_SELECTION);
                    break;
                }
            }
        }
        super.setSelectionMode(mode);
    }

    /**
     * Overrides superclass version to provide support for cell decorators.
     *
     * @param renderer the <code>TableCellRenderer</code> to prepare
     * @param row the row of the cell to render, where 0 is the first row
     * @param column the column of the cell to render, where 0 is the first column
     * @return the <code>Component</code> used as a stamp to render the specified cell
     */
    public Component prepareRenderer(TableCellRenderer renderer, int row,
        int column) {
        // TOTAL HACK to fix icons that disappeared in a regression after M3!
        if (isHierarchical(column)) {
            if (collapsedIcon != null) {
                setCollapsedIcon(collapsedIcon);
            }
            if (expandedIcon != null) {
                setExpandedIcon(expandedIcon);
            }
            if (openIcon != null) {
                setOpenIcon(openIcon);
            }
            if (closedIcon != null) {
                setClosedIcon(closedIcon);
            }
            if (leafIcon != null) {
                setLeafIcon(leafIcon);
            }
        }
        
        Component component = super.prepareRenderer(renderer, row, column);
        // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
        ComponentAdapter    adapter = getComponentAdapter();
        adapter.row = row;
        adapter.column = column;
        
        return applyRenderer(component, //super.prepareRenderer(renderer, row, column),
            adapter); 
    }

    /**
     * Performs necessary housekeeping before the renderer is actually applied.
     *
     * @param component
     * @param adapter component data adapter
     * @throws NullPointerException if the specified component or adapter is null
     */
    protected Component applyRenderer(Component component,
        ComponentAdapter adapter) {
        if (component == null) {
            throw new IllegalArgumentException("null component");
        }
        if (adapter == null) {
            throw new IllegalArgumentException("null component data adapter");
        }

        if (isHierarchical(adapter.column)) {
            // After all decorators have been applied, make sure that relevant
            // attributes of the table cell renderer are applied to the
            // tree cell renderer before the hierarchical column is rendered!
            TreeCellRenderer tcr = renderer.getCellRenderer();
            if (tcr instanceof JXTree.DelegatingRenderer) {
                tcr = ((JXTree.DelegatingRenderer) tcr).getDelegateRenderer();
                
            }
            if (tcr instanceof DefaultTreeCellRenderer) {
                DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
                if (adapter.isSelected()) {
                    dtcr.setTextSelectionColor(component.getForeground());
                    dtcr.setBackgroundSelectionColor(component.getBackground());
               } else {
                    dtcr.setTextNonSelectionColor(component.getForeground());
                    dtcr.setBackgroundNonSelectionColor(component.getBackground());
                }
            } 
        }
        return component;
    }

    /**
     * Sets the specified TreeCellRenderer as the Tree cell renderer.
     *
     * @param cellRenderer to use for rendering tree cells.
     */
    public void setTreeCellRenderer(TreeCellRenderer cellRenderer) {
        if (renderer != null) {
            renderer.setCellRenderer(cellRenderer);
        }
    }

    public TreeCellRenderer getTreeCellRenderer() {
        return renderer.getCellRenderer();
    }

    
    public String getToolTipText(MouseEvent event) {
        int column = columnAtPoint(event.getPoint());
        if (isHierarchical(column)) {
            return renderer.getToolTipText(event);
        }
        return super.getToolTipText(event);
    }
    
    /**
     * Sets the specified icon as the icon to use for rendering collapsed nodes.
     *
     * @param icon to use for rendering collapsed nodes
     */
    public void setCollapsedIcon(Icon icon) {
        try {
            ( (BasicTreeUI) (renderer.getUI())).setCollapsedIcon(icon);
            // TOTAL HACK to fix icons that disappeared in a regression after M3!
            collapsedIcon = icon;
        }
        catch (ClassCastException ex) {
            /** @todo use logging apis instead */
            System.err.println(ex);
        }
    }

    /**
     * Sets the specified icon as the icon to use for rendering expanded nodes.
     *
     * @param icon to use for rendering expanded nodes
     */
    public void setExpandedIcon(Icon icon) {
        try {
            ( (BasicTreeUI) (renderer.getUI())).setExpandedIcon(icon);
            // TOTAL HACK to fix icons that disappeared in a regression after M3!
            expandedIcon = icon;
        }
        catch (ClassCastException ex) {
            /** @todo use logging apis instead */
            System.err.println(ex);
        }
    }

    /**
     * Sets the specified icon as the icon to use for rendering open container nodes.
     *
     * @param icon to use for rendering open nodes
     */
    public void setOpenIcon(Icon icon) {
        try {
            ((DefaultTreeCellRenderer) renderer.getCellRenderer()).setOpenIcon(icon);
            // TOTAL HACK to fix icons that disappeared in a regression after M3!
            openIcon = icon;
        }
        catch (ClassCastException ex) {
            /** @todo use logging apis instead */
            System.err.println(ex);
        }
    }

    /**
     * Sets the specified icon as the icon to use for rendering closed container nodes.
     *
     * @param icon to use for rendering closed nodes
     */
    public void setClosedIcon(Icon icon) {
        try {
            ((DefaultTreeCellRenderer) renderer.getCellRenderer()).setClosedIcon(icon);
            // TOTAL HACK to fix icons that disappeared in a regression after M3!
            closedIcon = icon;
        }
        catch (ClassCastException ex) {
            /** @todo use logging apis instead */
            System.err.println(ex);
        }
    }

    /**
     * Sets the specified icon as the icon to use for rendering leaf nodes.
     *
     * @param icon to use for rendering leaf nodes
     */
    public void setLeafIcon(Icon icon) {
        try {
            ((DefaultTreeCellRenderer) renderer.getCellRenderer()).setLeafIcon(icon);
            // TOTAL HACK to fix icons that disappeared in a regression after M3!
            leafIcon = icon;
        }
        catch (ClassCastException ex) {
            /** @todo use logging apis instead */
            System.err.println(ex);
        }
    }

    /**
     * Overridden to ensure that private renderer state is kept in sync with the
     * state of the component. Calls the inherited version after performing the
     * necessary synchronization. If you override this method, make sure you call
     * this version from your version of this method.
     */
    public void clearSelection() {
        if (renderer != null) {
            renderer.clearSelection();
        }
        super.clearSelection();
    }

    /**
     * Collapses all nodes in the treetable.
     */
    public void collapseAll() {
        renderer.collapseAll();
    }

    /**
     * Expands all nodes in the treetable.
     */
    public void expandAll() {
        renderer.expandAll();
    }

    /**
     * Collapses the node at the specified path in the treetable.
     *
     * @param path path of the node to collapse
     */
    public void collapsePath(TreePath path) {
        renderer.collapsePath(path);
    }

    /**
     * Expands the the node at the specified path in the treetable.
     *
     * @param path path of the node to expand
     */
    public void expandPath(TreePath path) {
        renderer.expandPath(path);
    }

    /**
     * Collapses the row in the treetable. If the specified row index is
     * not valid, this method will have no effect.
     */
    public void collapseRow(int row) {
        renderer.collapseRow(row);
    }

    /**
     * Expands the specified row in the treetable. If the specified row index is
     * not valid, this method will have no effect.
     */
    public void expandRow(int row) {
        renderer.expandRow(row);
    }

    /**
     * Determines whether or not the root node from the TreeModel is visible.
     *
     * @param visible true, if the root node is visible; false, otherwise
     */
    public void setRootVisible(boolean visible) {
        renderer.setRootVisible(visible);
        repaint();
    }

    /**
     * Returns true if the root node of the tree is displayed.
     *
     * @return true if the root node of the tree is displayed
     */
    public boolean isRootVisible() {
        return renderer.isRootVisible();
    }

    /**
     * Returns true if the value identified by path is currently viewable, which
     * means it is either the root or all of its parents are expanded. Otherwise,
     * this method returns false.
     *
     * @return true, if the value identified by path is currently viewable;
     * false, otherwise
     */
    public boolean isVisible(TreePath path) {
        return renderer.isVisible(path);
    }

    /**
     * Returns true if the node identified by path is currently expanded.
     * Otherwise, this method returns false.
     *
     * @param path path
     * @return true, if the value identified by path is currently expanded;
     * false, otherwise
     */
    public boolean isExpanded(TreePath path) {
        return renderer.isExpanded(path);
    }

    /**
     * Returns true if the node at the specified display row is currently expanded.
     * Otherwise, this method returns false.
     *
     * @param row row
     * @return true, if the node at the specified display row is currently expanded.
     * false, otherwise
     */
    public boolean isExpanded(int row) {
        return renderer.isExpanded(row);
    }

    /**
     * Returns true if the node identified by path is currently collapsed, 
     * this will return false if any of the values in path are currently not 
     * being displayed.   
     *
     * @param path path
     * @return true, if the value identified by path is currently collapsed;
     * false, otherwise
     */
    public boolean isCollapsed(TreePath path) {
        return renderer.isCollapsed(path);
    }

    /**
     * Returns true if the node at the specified display row is collapsed.
     *
     * @param row row
     * @return true, if the node at the specified display row is currently collapsed.
     * false, otherwise
     */
    public boolean isCollapsed(int row) {
        return renderer.isCollapsed(row);
    }

    
    /**
     * Returns an <code>Enumeration</code> of the descendants of the
     * path <code>parent</code> that
     * are currently expanded. If <code>parent</code> is not currently
     * expanded, this will return <code>null</code>.
     * If you expand/collapse nodes while
     * iterating over the returned <code>Enumeration</code>
     * this may not return all
     * the expanded paths, or may return paths that are no longer expanded.
     *
     * @param parent  the path which is to be examined
     * @return an <code>Enumeration</code> of the descendents of 
     *		<code>parent</code>, or <code>null</code> if
     *		<code>parent</code> is not currently expanded
     */
    
    public Enumeration getExpandedDescendants(TreePath parent) {
    	return renderer.getExpandedDescendants(parent);
    }

    
    /**
     * Sets the value of the <code>expandsSelectedPaths</code> property for the tree
     * part. This property specifies whether the selected paths should be expanded.
     *
     * @param expand true, if selected paths should be expanded; false, otherwise
     */
    public void setExpandsSelectedPaths(boolean expand) {
        renderer.setExpandsSelectedPaths(expand);
    }

    /**
     * Returns the value of the <code>expandsSelectedPaths</code> property.
     *
     * @return the value of the <code>expandsSelectedPaths</code> property
     */
    public boolean getExpandsSelectedPaths() {
        return renderer.getExpandsSelectedPaths();
    }

    /**
     * Returns the TreePath for a given x,y location.
     *
     * @param x x value
     * @param y y value
     *
     * @return the <code>TreePath</code> for the givern location.
     */
     public TreePath getPathForLocation(int x, int y) {
        int row = rowAtPoint(new Point(x,y));
        if (row == -1) {
          return null;  
        }
        return renderer.getPathForRow(row);
     }

    /**
     * Returns the TreePath for a given row.
     *
     * @param row
     *
     * @return the <code>TreePath</code> for the given row.
     */
     public TreePath getPathForRow(int row) {
        return renderer.getPathForRow(row);
     }

     /**
      * Returns the row for a given TreePath.
      *
      * @param path
      * @return the row for the given <code>TreePath</code>.
      */
     public int getRowForPath(TreePath path) {
       return renderer.getRowForPath(path);
     }

    /**
     * Sets the value of the <code>scrollsOnExpand</code> property for the tree
     * part. This property specifies whether the expanded paths should be scrolled
     * into view. In a look and feel in which a tree might not need to scroll
     * when expanded, this property may be ignored.
     *
     * @param scroll true, if expanded paths should be scrolled into view;
     * false, otherwise
     */
    public void setScrollsOnExpand(boolean scroll) {
        renderer.setScrollsOnExpand(scroll);
    }

    /**
     * Returns the value of the <code>scrollsOnExpand</code> property.
     *
     * @return the value of the <code>scrollsOnExpand</code> property
     */
    public boolean getScrollsOnExpand() {
        return renderer.getScrollsOnExpand();
    }

    /**
     * Sets the value of the <code>showsRootHandles</code> property for the tree
     * part. This property specifies whether the node handles should be displayed.
     * If handles are not supported by a particular look and feel, this property
     * may be ignored.
     *
     * @param visible true, if root handles should be shown; false, otherwise
     */
    public void setShowsRootHandles(boolean visible) {
        renderer.setShowsRootHandles(visible);
        repaint();
    }

    /**
     * Returns the value of the <code>showsRootHandles</code> property.
     *
     * @return the value of the <code>showsRootHandles</code> property
     */
    public boolean getShowsRootHandles() {
        return renderer.getShowsRootHandles();
    }

    /**
     * Adds a listener for <code>TreeExpansion</code> events.
     * 
     * TODO (JW): redirect event source to this. 
     * 
     * @param tel a TreeExpansionListener that will be notified 
     * when a tree node is expanded or collapsed
     */
    public void addTreeExpansionListener(TreeExpansionListener tel) {
        renderer.addTreeExpansionListener(tel);
    }

    /**
     * Removes a listener for <code>TreeExpansion</code> events.
     * @param tel the <code>TreeExpansionListener</code> to remove
     */
    public void removeTreeExpansionListener(TreeExpansionListener tel) {
        renderer.removeTreeExpansionListener(tel);
    }

    /**
     * Adds a listener for <code>TreeSelection</code> events.
     * TODO (JW): redirect event source to this. 
     * 
     * @param tsl a TreeSelectionListener that will be notified 
     * when a tree node is selected or deselected
     */
    public void addTreeSelectionListener(TreeSelectionListener tsl) {
        renderer.addTreeSelectionListener(tsl);
    }

    /**
     * Removes a listener for <code>TreeSelection</code> events.
     * @param tsl the <code>TreeSelectionListener</code> to remove
     */
    public void removeTreeSelectionListener(TreeSelectionListener tsl) {
        renderer.removeTreeSelectionListener(tsl);
    }

    /**
     * Adds a listener for <code>TreeWillExpand</code> events.
     * TODO (JW): redirect event source to this. 
     * 
     * @param tel a TreeWillExpandListener that will be notified 
     * when a tree node will be expanded or collapsed 
     */
    public void addTreeWillExpandListener(TreeWillExpandListener tel) {
        renderer.addTreeWillExpandListener(tel);
    }

    /**
     * Removes a listener for <code>TreeWillExpand</code> events.
     * @param tel the <code>TreeWillExpandListener</code> to remove
     */
    public void removeTreeWillExpandListener(TreeWillExpandListener tel) {
        renderer.removeTreeWillExpandListener(tel);
     }
 
    
    /**
     * Returns the selection model for the tree portion of the this treetable.
     *
     * @return selection model for the tree portion of the this treetable
     */
    public TreeSelectionModel getTreeSelectionModel() {
        return renderer.getSelectionModel();    // RG: Fix JDNC issue 41
    }

    /**
     * Overriden to invoke supers implementation, and then,
     * if the receiver is editing a Tree column, the editors bounds is
     * reset. The reason we have to do this is because JTable doesn't
     * think the table is being edited, as <code>getEditingRow</code> returns
     * -1, and therefore doesn't automaticly resize the editor for us.
     */
    public void sizeColumnsToFit(int resizingColumn) {
        /** @todo Review wrt doLayout() */
        super.sizeColumnsToFit(resizingColumn);
        // rg:changed
        if (getEditingColumn() != -1 && isHierarchical(editingColumn)) {
            Rectangle cellRect = getCellRect(realEditingRow(),
                getEditingColumn(), false);
            Component component = getEditorComponent();
            component.setBounds(cellRect);
            component.validate();
        }
    }

    /**
     * Overridden to message super and forward the method to the tree.
     * Since the tree is not actually in the component hieachy it will
     * never receive this unless we forward it in this manner.
     */
    public void updateUI() {
        super.updateUI();
        if (renderer != null) {
            //  final int savedHeight = renderer.getRowHeight();
            renderer.updateUI();
            //  renderer.setRowHeight(savedHeight);

            // Do this so that the editor is referencing the current renderer
            // from the tree. The renderer can potentially change each time
            // laf changes.
            setDefaultEditor(AbstractTreeTableModel.hierarchicalColumnClass,
                new TreeTableCellEditor(renderer));

            if (getBackground() == null || getBackground()instanceof UIResource) {
                setBackground(renderer.getBackground());
            }
        }
    }

    /**
     * Determines if the specified column contains hierarchical nodes.
     *
     * @param column zero-based index of the column
     * @return true if the class of objects in the specified column implement
     * the {@link javax.swing.tree.TreeNode} interface; false otherwise.
     */
    public boolean isHierarchical(int column) {
        return AbstractTreeTableModel.hierarchicalColumnClass.isAssignableFrom(
            getColumnClass(column));
    }

    /**
     * Initializes this JXTreeTable and permanently binds the specified renderer
     * to it.
     *
     * @param renderer private tree/renderer permanently and exclusively bound
     * to this JXTreeTable.
     */
    private final void init(TreeTableCellRenderer renderer) {
        this.renderer = renderer;
        // Force the JTable and JTree to share their row selection models.
        ListToTreeSelectionModelWrapper selectionWrapper =
            new ListToTreeSelectionModelWrapper();

        // JW: when would that happen?
        if (renderer != null) {
            renderer.bind(this); // IMPORTANT: link back!
            renderer.setSelectionModel(selectionWrapper);
        }

        setSelectionModel(selectionWrapper.getListSelectionModel());
        setDefaultRenderer(AbstractTreeTableModel.hierarchicalColumnClass,
            renderer);
        
        // propagate the lineStyle property to the renderer
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                JXTreeTable.this.renderer.putClientProperty(evt.getPropertyName(), evt.getNewValue());
                
            }
            
        };
        addPropertyChangeListener("JTree.lineStyle", l);
        
    }


    /**
     * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
     * to listen for changes in the ListSelectionModel it maintains. Once
     * a change in the ListSelectionModel happens, the paths are updated
     * in the DefaultTreeSelectionModel.
     */
    class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {
        /** Set to true when we are updating the ListSelectionModel. */
        protected boolean updatingListSelectionModel;

        public ListToTreeSelectionModelWrapper() {
            super();
            getListSelectionModel().addListSelectionListener
                (createListSelectionListener());
        }

        /**
         * Returns the list selection model. ListToTreeSelectionModelWrapper
         * listens for changes to this model and updates the selected paths
         * accordingly.
         */
        ListSelectionModel getListSelectionModel() {
            return listSelectionModel;
        }

        /**
         * This is overridden to set <code>updatingListSelectionModel</code>
         * and message super. This is the only place DefaultTreeSelectionModel
         * alters the ListSelectionModel.
         */
        public void resetRowSelection() {
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    super.resetRowSelection();
                }
                finally {
                    updatingListSelectionModel = false;
                }
            }
            // Notice how we don't message super if
            // updatingListSelectionModel is true. If
            // updatingListSelectionModel is true, it implies the
            // ListSelectionModel has already been updated and the
            // paths are the only thing that needs to be updated.
        }

        /**
         * Creates and returns an instance of ListSelectionHandler.
         */
        protected ListSelectionListener createListSelectionListener() {
            return new ListSelectionHandler();
        }

        /**
         * If <code>updatingListSelectionModel</code> is false, this will
         * reset the selected paths from the selected rows in the list
         * selection model.
         */
        protected void updateSelectedPathsFromSelectedRows() {
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    // This is way expensive, ListSelectionModel needs an
                    // enumerator for iterating.
                    int min = listSelectionModel.getMinSelectionIndex();
                    int max = listSelectionModel.getMaxSelectionIndex();

                    clearSelection();
                    if (min != -1 && max != -1) {
                        for (int counter = min; counter <= max; counter++) {
                            if (listSelectionModel.isSelectedIndex(counter)) {
                                TreePath selPath = renderer.getPathForRow(
                                    counter);

                                if (selPath != null) {
                                    addSelectionPath(selPath);
                                }
                            }
                        }
                    }
                }
                finally {
                    updatingListSelectionModel = false;
                }
            }
        }

        /**
         * Class responsible for calling updateSelectedPathsFromSelectedRows
         * when the selection of the list changse.
         */
        class ListSelectionHandler implements ListSelectionListener {
            public void valueChanged(ListSelectionEvent e) {
                updateSelectedPathsFromSelectedRows();
            }
        }
    }

    private static class TreeTableModelAdapter extends AbstractTableModel {
        /**
         * Maintains a TreeTableModel and a JTree as purely implementation details.
         * Developers can plug in any type of custom TreeTableModel through a
         * JXTreeTable constructor or through setTreeTableModel().
         *
         * @param model Underlying data model for the JXTreeTable that will ultimately
         * be bound to this TreeTableModelAdapter
         * @param tree TreeTableCellRenderer instantiated with the same model as
         * specified by the model parameter of this constructor
         * @throws IllegalArgumentException if a null model argument is passed
         * @throws IllegalArgumentException if a null tree argument is passed
         */
        TreeTableModelAdapter(TreeTableModel model, JTree tree) {
            assert model != null;
            assert tree != null;

            this.tree = tree; // need tree to implement getRowCount()
            this.model = model;

            // Install a TreeModelListener that can update the table when
            // tree changes. We use delayedFireTableDataChanged as we can
            // not be guaranteed the tree will have finished processing
            // the event before us.
            model.addTreeModelListener(new TreeModelListener() {
                public void treeNodesChanged(TreeModelEvent e) {
                    delayedFireTableDataChanged(e, 0);
                }

                public void treeNodesInserted(TreeModelEvent e) {
                    delayedFireTableDataChanged(e, 1);
                }

                public void treeNodesRemoved(TreeModelEvent e) {
                    delayedFireTableDataChanged(e, 2);
                }

                public void treeStructureChanged(TreeModelEvent e) {
                    delayedFireTableDataChanged();
                }
            });

            tree.addTreeExpansionListener(new TreeExpansionListener() {
                // Don't use fireTableRowsInserted() here; the selection model
                // would get updated twice.
                public void treeExpanded(TreeExpansionEvent event) {
                    fireTableDataChanged();
                }

                public void treeCollapsed(TreeExpansionEvent event) {
                    fireTableDataChanged();
                }
            });
        }

        /**
         * Returns the real TreeTableModel that is wrapped by this TreeTableModelAdapter.
         *
         * @return the real TreeTableModel that is wrapped by this TreeTableModelAdapter
         */
        public TreeTableModel getTreeTableModel() {
            return model;
        }

        /**
         * Returns the JXTreeTable instance to which this TreeTableModelAdapter is
         * permanently and exclusively bound. For use by
         * {@link org.jdesktop.swingx.JXTreeTable#setModel(javax.swing.table.TableModel)}.
         *
         * @return JXTreeTable to which this TreeTableModelAdapter is permanently bound
         */
        protected JXTreeTable getTreeTable() {
            return treeTable;
        }

        /**
         * Immutably binds this TreeTableModelAdapter to the specified JXTreeTable.
         *
         * @param treeTable the JXTreeTable instance that this adapter is bound to.
         */
        protected final void bind(JXTreeTable treeTable) {
            // Suppress potentially subversive invocation!
            // Prevent clearing out the deck for possible hijack attempt later!
            if (treeTable == null) {
                throw new IllegalArgumentException("null treeTable");
            }

            if (this.treeTable == null) {
                this.treeTable = treeTable;
            }
            else {
                throw new IllegalArgumentException("adapter already bound");
            }
        }

        // Wrappers, implementing TableModel interface.
        // TableModelListener management provided by AbstractTableModel superclass.

        public Class getColumnClass(int column) {
            return model.getColumnClass(column);
        }

        public int getColumnCount() {
            return model.getColumnCount();
        }

        public String getColumnName(int column) {
            return model.getColumnName(column);
        }

        public int getRowCount() {
            return tree.getRowCount();
        }

        public Object getValueAt(int row, int column) {
            return model.getValueAt(nodeForRow(row), column);
        }

        public boolean isCellEditable(int row, int column) {
            return model.isCellEditable(nodeForRow(row), column);
        }

        public void setValueAt(Object value, int row, int column) {
            model.setValueAt(value, nodeForRow(row), column);
        }

        protected Object nodeForRow(int row) {
            return tree.getPathForRow(row).getLastPathComponent();
        }

        /**
         * Invokes fireTableDataChanged after all the pending events have been
         * processed. SwingUtilities.invokeLater is used to handle this.
         */
        private void delayedFireTableDataChanged() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fireTableDataChanged();
                }
            });
        }

        /**
         * Invokes fireTableDataChanged after all the pending events have been
         * processed. SwingUtilities.invokeLater is used to handle this.
         */
        private void delayedFireTableDataChanged(final TreeModelEvent tme, final int typeChange) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int indices[] = tme.getChildIndices();
                    TreePath path = tme.getTreePath();
                    if (indices != null) { 
                        if (tree.isExpanded(path)) { // Dont bother to update if the parent 
                                                    // node is collapsed
                            int startingRow = tree.getRowForPath(path)+1;
                            int min = Integer.MAX_VALUE;
                            int max = Integer.MIN_VALUE;
                            for (int i=0;i<indices.length;i++) {
                                if (indices[i] < min) {
                                    min = indices[i];
                                }
                                if (indices[i] > max) {
                                    max = indices[i];
                                }
                            }
                            switch (typeChange) {
                                case 0 :
                                    fireTableRowsUpdated(startingRow + min, startingRow+max);
                                break;
                                case 1: 
                                    fireTableRowsInserted(startingRow + min, startingRow+max);
                                break;
                                case 2:
                                    fireTableRowsDeleted(startingRow + min, startingRow+max);
                                break;
                            }
                        }
                    }
                    else {  // case where the event is fired to identify root.
                        fireTableDataChanged();
                    }
                }
            });
        }



        private TreeTableModel model; // immutable
        private final JTree tree; // immutable
        private JXTreeTable treeTable = null; // logically immutable
    }

    static class TreeTableCellRenderer extends JXTree implements
        TableCellRenderer {
        // Force user to specify TreeTableModel instead of more general TreeModel
        public TreeTableCellRenderer(TreeTableModel model) {
            super(model);
            putClientProperty("JTree.lineStyle", "None");
            setRootVisible(false); // superclass default is "true"
            setShowsRootHandles(true); // superclass default is "false"
                /** @todo Support truncated text directly in DefaultTreeCellRenderer. */
            setCellRenderer(new ClippedTreeCellRenderer());
        }

        /**
         * Immutably binds this TreeTableModelAdapter to the specified JXTreeTable.
         * For internal use by JXTreeTable only.
         *
         * @param treeTable the JXTreeTable instance that this renderer is bound to
         */
        public final void bind(JXTreeTable treeTable) {
            // Suppress potentially subversive invocation!
            // Prevent clearing out the deck for possible hijack attempt later!
            if (treeTable == null) {
                throw new IllegalArgumentException("null treeTable");
            }

            if (this.treeTable == null) {
                this.treeTable = treeTable;
            }
            else {
                throw new IllegalArgumentException("renderer already bound");
            }
        }

        /**
         * updateUI is overridden to set the colors of the Tree's renderer
         * to match that of the table.
         */
        public void updateUI() {
            super.updateUI();
            // Make the tree's cell renderer use the table's cell selection
            // colors.
            TreeCellRenderer tcr = getCellRenderer();
            if (tcr instanceof DefaultTreeCellRenderer) {
                DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
                // For 1.1 uncomment this, 1.2 has a bug that will cause an
                // exception to be thrown if the border selection color is null.
                dtcr.setBorderSelectionColor(null);
                dtcr.setTextSelectionColor(
                    UIManager.getColor("Table.selectionForeground"));
                dtcr.setBackgroundSelectionColor(
                    UIManager.getColor("Table.selectionBackground"));
            }
        }

        /**
         * Sets the row height of the tree, and forwards the row height to
         * the table.
         */
        public void setRowHeight(int rowHeight) {
            super.setRowHeight(rowHeight);
            if (rowHeight > 0) {
                if (treeTable != null) {
                    // Reconcile semantic differences between JTable and JTree
                    final int tableRowMargin = treeTable.getRowMargin();
                    assert tableRowMargin >= 0;
                    final int tableRowHeight = rowHeight - (tableRowMargin << 1);
                    if (treeTable.getRowHeight() != tableRowHeight) {
                        boolean heightSet = treeTable.isXTableRowHeightSet;
                        treeTable.setRowHeight(tableRowHeight);
                        treeTable.isXTableRowHeightSet = heightSet;
                    }
                }
            }
        }

        /**
         * This is overridden to set the height to match that of the JTable.
         */
        public void setBounds(int x, int y, int w, int h) {
            if (treeTable != null) {
                y = 0;
                // It is not enough to set the height to treeTable.getHeight()
                h = treeTable.getRowCount() * this.getRowHeight();
            }
            super.setBounds(x, y, w, h);
        }

        /**
         * Sublcassed to translate the graphics such that the last visible
         * row will be drawn at 0,0.
         */
        public void paint(Graphics g) {
            int rowMargin = treeTable.getRowMargin();
            // MUST account for rowMargin for precise positioning.
            // Offset by (rowMargin * 3)/2, and remember to offset by an
            // additional pixel if rowMargin is odd!
            int margins = rowMargin + (rowMargin >> 1) + (rowMargin % 2);
            int translationOffset = margins + visibleRow * getRowHeight();
            g.translate(0, - translationOffset);

            hierarchicalColumnWidth = getWidth();
            super.paint(g);

            // Draw the Table border if we have focus.
            if (highlightBorder != null) {
                // #170: border not drawn correctly
                // JW: position the border to be drawn in translated area
                // still not satifying in all cases...
				// RG: Now it satisfies (at least for the row margins)
				// Still need to make similar adjustments for column margins...
                highlightBorder.paintBorder(this, g, 0, 
                        translationOffset,
                        getWidth(), 
                        // uhhh getRowHeight() + 1 - 2 * (margins) 
						getRowHeight() - (rowMargin << 1) - rowMargin);	// RG: subtract (rowMargin * 3)
            }
        }

        public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            assert table == treeTable;

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            else {
                setBackground(table.getBackground());
               setForeground(table.getForeground());
            }

            highlightBorder = null;
            if (treeTable != null) {
                if (treeTable.realEditingRow() == row &&
                    treeTable.getEditingColumn() == column) {
                }
                else if (hasFocus) {
                    highlightBorder = UIManager.getBorder(
                        "Table.focusCellHighlightBorder");
                }
            }

            
            visibleRow = row;

            return this;
        }

        private class ClippedTreeCellRenderer extends DefaultTreeCellRenderer {
            public void paint(Graphics g) {
                String fullText = super.getText();
                // getText() calls tree.convertValueToText();
                // tree.convertValueToText() should call treeModel.convertValueToText(), if possible

                String shortText = SwingUtilities.layoutCompoundLabel(
                    this, g.getFontMetrics(), fullText, getIcon(),
                    getVerticalAlignment(), getHorizontalAlignment(),
                    getVerticalTextPosition(), getHorizontalTextPosition(),
                    getItemRect(itemRect), iconRect, textRect,
                    getIconTextGap());

                /** @todo setText is more heavyweight than we want in this
                 * situation. Make JLabel.text protected instead of private.
                 */

                setText(shortText); // temporarily truncate text
                super.paint(g);
                setText(fullText); // restore full text
            }

            private Rectangle getItemRect(Rectangle itemRect) {
                getBounds(itemRect);
                itemRect.width = hierarchicalColumnWidth - itemRect.x;
                return itemRect;
            }

            // Rectangles filled in by SwingUtilities.layoutCompoundLabel();
            private final Rectangle iconRect = new Rectangle();
            private final Rectangle textRect = new Rectangle();
            // Rectangle filled in by this.getItemRect();
            private final Rectangle itemRect = new Rectangle();
        }

        /** Border to draw around the tree, if this is non-null, it will
         * be painted. */
        protected Border highlightBorder = null;
        protected JXTreeTable treeTable = null;
        protected int visibleRow = 0;

        // A JXTreeTable may not have more than one hierarchical column
        private int hierarchicalColumnWidth = 0;
    }

    /**
     * Returns the adapter that knows how to access the component data model.
     * The component data adapter is used by filters, sorters, and highlighters.
     *
     * @return the adapter that knows how to access the component data model
     */
    protected ComponentAdapter getComponentAdapter() {
        // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
        return dataAdapter;
    }

    private final ComponentAdapter  dataAdapter = new TreeTableDataAdapter(this);

    // Define defaultRowHeight to allow default document icon for leaf items to
    // draw properly
    // JW: unused? commented until needed
    //private final static int defaultRowHeight = 20;
    private final static Dimension spacing = new Dimension(0, 2);

    static class TreeTableDataAdapter extends JXTable.TableAdapter {
        private final JXTreeTable table;

        /**
         * Constructs a <code>TreeTableDataAdapter</code> for the specified
         * target component.
         *
         * @param component the target component
         */
        public TreeTableDataAdapter(JXTreeTable component) {
            super(component);
            table = component;
        }
        public JXTreeTable getTreeTable() {
            return table;
        }

        public boolean isExpanded() {
            return super.isExpanded(); /** @todo implement this method */
        }

        public boolean hasFocus() {
            boolean focus = super.hasFocus(); /** @todo implement this method */
            return focus;
        }

        public boolean isLeaf() {
            return super.isLeaf(); /** @todo implement this method */
        }
        /**
         *
         * @return true if the cell identified by this adapter displays hierarchical
         *      nodes; false otherwise
         */
        public boolean isHierarchical() {
            return table.isHierarchical(column);
        }
    }

}
