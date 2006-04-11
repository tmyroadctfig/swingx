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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jdesktop.swingx.action.LinkAction;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PipelineEvent;
import org.jdesktop.swingx.decorator.PipelineListener;
import org.jdesktop.swingx.decorator.SelectionMapper;
import org.jdesktop.swingx.decorator.SortKey;

/**
 * JXList
 * 
 * Enabled Rollover/LinkModel handling. Enabled Highlighter support.
 * 
 * Added experimental support for filtering/sorting. This feature is disabled by
 * default because it has side-effects which might break "normal" expectations
 * when using a JList: if enabled all row coordinates (including those returned
 * by the selection) are in view coordinates. Furthermore, the model returned
 * from getModel() is a wrapper around the actual data.
 * 
 * 
 * 
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 */
public class JXList extends JList {
    public static final String EXECUTE_BUTTON_ACTIONCOMMAND = "executeButtonAction";

    /** The pipeline holding the filters. */
    protected FilterPipeline filters;

    /**
     * The pipeline holding the highlighters.
     */
    protected HighlighterPipeline highlighters;

    /** listening to changeEvents from highlighterPipeline. */
    private ChangeListener highlighterChangeListener;

    /** The ComponentAdapter for model data access. */
    protected ComponentAdapter dataAdapter;

    /**
     * Mouse/Motion/Listener keeping track of mouse moved in cell coordinates.
     */
    private RolloverProducer rolloverProducer;

    /**
     * RolloverController: listens to cell over events and repaints
     * entered/exited rows.
     */
    private LinkController linkController;

    /** A wrapper around the default renderer enabling decoration. */
    private DelegatingRenderer delegatingRenderer;

    private WrappingListModel wrappingModel;

    private PipelineListener pipelineListener;

    private boolean filterEnabled;

    private SelectionMapper selectionMapper;

    private Searchable searchable;

    public JXList() {
        init();
    }

    public JXList(ListModel dataModel) {
        super(dataModel);
        init();
    }

    public JXList(Object[] listData) {
        super(listData);
        init();
    }

    public JXList(Vector listData) {
        super(listData);
        init();
    }

    private void init() {
        Action findAction = createFindAction();
        getActionMap().put("find", findAction);
        // JW: this should be handled by the LF!
        KeyStroke findStroke = KeyStroke.getKeyStroke("control F");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(findStroke, "find");
        
    }

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
     * @returns a not-null Searchable for this editor.  
     */
    public Searchable getSearchable() {
        if (searchable == null) {
            searchable = new ListSearchable();
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
    

    public class ListSearchable extends AbstractSearchable {

        @Override
        protected void findMatchAndUpdateState(Pattern pattern, int startRow, boolean backwards) {
            SearchResult searchResult = null;
            if (backwards) {
                for (int index = startRow; index >= 0 && searchResult == null; index--) {
                    searchResult = findMatchAt(pattern, index);
                }
            } else {
                for (int index = startRow; index < getSize() && searchResult == null; index++) {
                    searchResult = findMatchAt(pattern, index);
                }
            }
            updateState(searchResult);
        }

        @Override
        protected SearchResult findExtendedMatch(Pattern pattern, int row) {
            
            return findMatchAt(pattern, row);
        }
        /**
         * Matches the cell content at row/col against the given Pattern.
         * Returns an appropriate SearchResult if matching or null if no
         * matching
         * 
         * @param pattern 
         * @param row a valid row index in view coordinates
         * @param column a valid column index in view coordinates
         * @return
         */
        protected SearchResult findMatchAt(Pattern pattern, int row) {
            Object value = getElementAt(row);
            if (value != null) {
                Matcher matcher = pattern.matcher(value.toString());
                if (matcher.find()) {
                    return createSearchResult(matcher, row, -1);
                }
            }
            return null;
        }
        
        @Override
        protected int getSize() {
            return getElementCount();
        }

        @Override
        protected void moveMatchMarker() {
          setSelectedIndex(lastSearchResult.foundRow);
          if (lastSearchResult.foundRow >= 0) {
              ensureIndexIsVisible(lastSearchResult.foundRow);
          }
            
        }

    }
    /**
     * Property to enable/disable rollover support. This can be enabled to show
     * "live" rollover behaviour, f.i. the cursor over LinkModel cells. Default
     * is disabled.
     * 
     * @param rolloverEnabled
     */
    public void setRolloverEnabled(boolean rolloverEnabled) {
        boolean old = isRolloverEnabled();
        if (rolloverEnabled == old)
            return;
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

    
    protected LinkController getLinkController() {
        if (linkController == null) {
            linkController = createLinkController();
        }
        return linkController;
    }

    protected LinkController createLinkController() {
        return new LinkController();
    }


    /**
     * creates and returns the RolloverProducer to use with this tree.
     * 
     * @return
     */
    protected RolloverProducer createRolloverProducer() {
        RolloverProducer r = new RolloverProducer() {
            protected void updateRolloverPoint(JComponent component,
                    Point mousePoint) {
                JXList list = (JXList) component;
                int row = list.locationToIndex(mousePoint);
                if (row >= 0) {
                    Rectangle cellBounds = list.getCellBounds(row, row);
                    if (!cellBounds.contains(mousePoint)) {
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
     * @return
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
    public static class LinkController implements PropertyChangeListener {

        private Cursor oldCursor;
        private JList list;
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (RolloverProducer.ROLLOVER_KEY.equals(evt.getPropertyName())) {
                   rollover((JList) evt.getSource(), (Point) evt.getOldValue(),
                            (Point) evt.getOldValue());
            } else if (RolloverProducer.CLICKED_KEY.equals(evt.getPropertyName())) {
                    click((JList) evt.getSource(), (Point) evt.getOldValue(),
                            (Point) evt.getNewValue());
            }
        }


        public void install(JList list) {
            release();  
            this.list = list;
            list.addPropertyChangeListener(RolloverProducer.CLICKED_KEY, this);
            list.addPropertyChangeListener(RolloverProducer.ROLLOVER_KEY, this);
            registerExecuteButtonAction();
          }
          
          public void release() {
              if (list == null) return;
              list.removePropertyChangeListener(this);
              list.removePropertyChangeListener(RolloverProducer.CLICKED_KEY, this);
              list.removePropertyChangeListener(RolloverProducer.ROLLOVER_KEY, this);
              unregisterExecuteButtonAction();
              list = null;
          }

//    --------------------------------- JList rollover
        
        private void rollover(JList list, Point oldLocation, Point newLocation) {
            setRolloverCursor(list, newLocation);
            // JW: partial repaints incomplete
            list.repaint();
        }

        private void click(JList list, Point oldLocation, Point newLocation) {
            if (!isRolloverCell(list, newLocation)) return;
            ListCellRenderer renderer = list.getCellRenderer();
            // PENDING: JW - don't ask the model, ask the list!
            Object element = list.getModel().getElementAt(newLocation.y);
            Component comp = renderer.getListCellRendererComponent(list, element, newLocation.y, false, true);
            if (comp instanceof AbstractButton) {
                // this is fishy - needs to be removed as soon as JList is editable
                ((AbstractButton) comp).doClick();
                list.repaint();
            }
        }
        
        /**
         * something weird: cursor in JList behaves different from JTable?
         * @param list
         * @param location
         */
        private void setRolloverCursor(JList list, Point location) {
            if (isRolloverCell(list, location)) {
                    oldCursor = list.getCursor();
                    list.setCursor(Cursor
                            .getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                    list.setCursor(oldCursor);
                    oldCursor = null;
            }

        }
        private boolean isRolloverCell(JList list, Point location) {
            if (location == null || location.y < 0) return false;
            ListCellRenderer renderer = list.getCellRenderer();
            return (renderer instanceof RolloverRenderer)
               && ((RolloverRenderer) renderer).isEnabled();
        }

        private void unregisterExecuteButtonAction() {
            list.getActionMap().put(EXECUTE_BUTTON_ACTIONCOMMAND, null);
            KeyStroke space = KeyStroke.getKeyStroke("released SPACE");
            list.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(space , null);
        }

        private void registerExecuteButtonAction() {
            list.getActionMap().put(EXECUTE_BUTTON_ACTIONCOMMAND, createExecuteButtonAction());
            KeyStroke space = KeyStroke.getKeyStroke("released SPACE");
            list.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(space , EXECUTE_BUTTON_ACTIONCOMMAND);
            
        }

        private Action createExecuteButtonAction() {
            Action action = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    AbstractButton button = getClickableRendererComponent();
                    if (button != null) {
                        button.doClick();
                        list.repaint();
                    }
                }

                @Override
                public boolean isEnabled() {
                    return isClickable();
                }

                private boolean isClickable() {
                    return getClickableRendererComponent() != null;
                }
                
                private AbstractButton getClickableRendererComponent() {
                    if (list == null || !list.isEnabled() || !list.hasFocus()) return null;
                    int leadRow = list.getLeadSelectionIndex();
                    if (leadRow < 0 ) return null;
                    ListCellRenderer renderer = list.getCellRenderer();
                    Object element = list.getModel().getElementAt(leadRow);
                    Component rendererComp = renderer.getListCellRendererComponent(list, element, leadRow, false, true);
                    return rendererComp instanceof AbstractButton ? (AbstractButton) rendererComp : null;
                }
                
            };
            return action;
        }


    }

   
    // ---------------------------- filters

    /**
     * returns the element at the given index. The index is
     * in view coordinates which might differ from model 
     * coordinates if filtering is enabled and filters/sorters
     * are active.
     * 
     * @param viewIndex the index in view coordinates
     * @return the element at the index
     * @throws IndexOutOfBoundsException 
     *          if viewIndex < 0 or viewIndex >= getElementCount()
     */
    public Object getElementAt(int viewIndex) {
        return getModel().getElementAt(viewIndex);
    }

    /**
     * Returns the number of elements in this list in view 
     * coordinates. If filters are active this number might be
     * less than the number of elements in the underlying model.
     * 
     * @return
     */
    public int getElementCount() {
        return getModel().getSize();
    }

    /**
     * Convert row index from view coordinates to model coordinates accounting
     * for the presence of sorters and filters.
     * 
     * @param viewIndex index in view coordinates
     * @return index in model coordinates
     * @throws IndexOutOfBoundsException if viewIndex < 0 or viewIndex >= getElementCount() 
     */
    public int convertIndexToModel(int viewIndex) {
        return isFilterEnabled() ? getFilters().convertRowIndexToModel(
                viewIndex) : viewIndex;
    }

    /**
     * Convert index from model coordinates to view coordinates accounting
     * for the presence of sorters and filters.
     * 
     * PENDING Filter guards against out of range - should not? 
     * 
     * @param modelIndex index in model coordinates
     * @return index in view coordinates if the model index maps to a view coordinate
     *          or -1 if not contained in the view.
     * 
     */
    public int convertIndexToView(int modelIndex) {
        return isFilterEnabled() ? getFilters().convertRowIndexToView(
                modelIndex) : modelIndex;
    }

    /**
     * returns the underlying model. If !isFilterEnabled this will be the same
     * as getModel().
     * 
     * @return
     */
    public ListModel getWrappedModel() {
        return isFilterEnabled() ? wrappingModel.getModel() : getModel();
    }

    /**
     * Enables/disables filtering support. If enabled all row indices -
     * including the selection - are in view coordinates and getModel returns a
     * wrapper around the underlying model.
     * 
     * Note: as an implementation side-effect calling this method clears the
     * selection (done in super.setModel).
     * 
     * PENDING: cleanup state transitions!! - currently this can be safely
     * applied once only to enable. Internal state is inconsistent if trying to
     * disable again.
     * 
     * see Issue #2-swinglabs.
     * 
     * @param enabled
     */
    public void setFilterEnabled(boolean enabled) {
        boolean old = isFilterEnabled();
        if (old == enabled)
            return;
        filterEnabled = enabled;
        if (!old) {
            wrappingModel = new WrappingListModel(getModel());
            super.setModel(wrappingModel);
        } else {
            ListModel model = wrappingModel.getModel();
            wrappingModel = null;
            super.setModel(model);
        }

    }

    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    /**
     * Overridden to update selectionMapper
     */
    @Override 
    public void setSelectionModel(ListSelectionModel newModel) {
        super.setSelectionModel(newModel);
        getSelectionMapper().setViewSelectionModel(getSelectionModel());
    }

    /**
     * set's the underlying data model. Note that if isFilterEnabled you must
     * call getWrappedModel to access the model given here. In this case
     * getModel returns a wrapper around the data!
     * 
     * 
     * 
     */
    public void setModel(ListModel model) {
        if (isFilterEnabled()) {
            wrappingModel.setModel(model);
        } else {
            super.setModel(model);
        }
    }

    /**
     * widened access for testing...
     * @return
     */
    protected SelectionMapper getSelectionMapper() {
        if (selectionMapper == null) {
            selectionMapper = new SelectionMapper(filters, getSelectionModel());
        }
        return selectionMapper;
    }

    public FilterPipeline getFilters() {
        if ((filters == null) && isFilterEnabled()) {
            setFilters(null);
        }
        return filters;
    }

    /** Sets the FilterPipeline for filtering table rows. 
     *  PRE: isFilterEnabled()
     * 
     * @param pipeline the filterPipeline to use.
     * @throws IllegalStateException if !isFilterEnabled()
     */
    public void setFilters(FilterPipeline pipeline) {
        if (!isFilterEnabled()) throw
            new IllegalStateException("filters not enabled - not allowed to set filters");

        FilterPipeline old = filters;
        List<? extends SortKey> sortKeys = null;
        if (old != null) {
            old.removePipelineListener(pipelineListener);
            sortKeys = old.getSortController().getSortKeys();
        }
        if (pipeline == null) {
            pipeline = new FilterPipeline();
        }
        filters = pipeline;
        filters.getSortController().setSortKeys(sortKeys);
        // JW: first assign to prevent (short?) illegal internal state
        // #173-swingx
        use(filters);
        getSelectionMapper().setFilters(filters);

    }

    /**
     * setModel() and setFilters() may be called in either order.
     * 
     * @param pipeline
     */
    private void use(FilterPipeline pipeline) {
        if (pipeline != null) {
            // check JW: adding listener multiple times (after setModel)?
            if (initialUse(pipeline)) {
                pipeline.addPipelineListener(getFilterPipelineListener());
                pipeline.assign(getComponentAdapter());
            } else {
                pipeline.flush();
            }
        }
    }

    /**
     * @return true is not yet used in this JXTable, false otherwise
     */
    private boolean initialUse(FilterPipeline pipeline) {
        if (pipelineListener == null)
            return true;
        PipelineListener[] l = pipeline.getPipelineListeners();
        for (int i = 0; i < l.length; i++) {
            if (pipelineListener.equals(l[i]))
                return false;
        }
        return true;
    }

    /** returns the listener for changes in filters. */
    protected PipelineListener getFilterPipelineListener() {
        if (pipelineListener == null) {
            pipelineListener = createPipelineListener();
        }
        return pipelineListener;
    }

    /** creates the listener for changes in filters. */
    protected PipelineListener createPipelineListener() {
        PipelineListener l = new PipelineListener() {
            public void contentsChanged(PipelineEvent e) {
                updateOnFilterContentChanged();
            }
        };
        return l;
    }

    /**
     * method called on change notification from filterpipeline.
     */
    protected void updateOnFilterContentChanged() {
        // make the wrapper listen to the pipeline?
        if (wrappingModel != null) {
            wrappingModel.updateOnFilterContentChanged();
        }
        revalidate();
        repaint();
    }

    private class WrappingListModel extends AbstractListModel {

        private ListModel delegate;

        private ListDataListener listDataListener;

        public WrappingListModel(ListModel model) {
            setModel(model);
        }

        public void updateOnFilterContentChanged() {
            fireContentsChanged(this, -1, -1);

        }

        public void setModel(ListModel model) {
            ListModel old = this.getModel();
            if (old != null) {
                old.removeListDataListener(listDataListener);
            }
            this.delegate = model;
            delegate.addListDataListener(getListDataListener());
            fireContentsChanged(this, -1, -1);
        }

        private ListDataListener getListDataListener() {
            if (listDataListener == null) {
                listDataListener = createListDataListener();
            }
            return listDataListener;
        }

        private ListDataListener createListDataListener() {
            ListDataListener l = new ListDataListener() {

                public void intervalAdded(ListDataEvent e) {
                    contentsChanged(e);

                }

                public void intervalRemoved(ListDataEvent e) {
                    contentsChanged(e);

                }

                public void contentsChanged(ListDataEvent e) {
                    getSelectionMapper().lock();
                    fireContentsChanged(this, -1, -1);
                    updateSelection(e);
                    getFilters().flush();

                }

            };
            return l;
        }

        protected void updateSelection(ListDataEvent e) {
            if (e.getType() == ListDataEvent.INTERVAL_REMOVED) {
                getSelectionMapper()
                        .removeIndexInterval(e.getIndex0(), e.getIndex1());
            } else if (e.getType() == ListDataEvent.INTERVAL_ADDED) {

                int minIndex = Math.min(e.getIndex0(), e.getIndex1());
                int maxIndex = Math.max(e.getIndex0(), e.getIndex1());
                int length = maxIndex - minIndex + 1;
                getSelectionMapper().insertIndexInterval(minIndex, length, true);
            } else {
                getSelectionMapper().clearModelSelection();
            }

        }

        public ListModel getModel() {
            return delegate;
        }

        public int getSize() {
            return getFilters().getOutputSize();
        }

        public Object getElementAt(int index) {
            return getFilters().getValueAt(index, 0);
        }

    }

    // ---------------------------- uniform data model

    protected ComponentAdapter getComponentAdapter() {
        if (dataAdapter == null) {
            dataAdapter = new ListAdapter(this);
        }
        return dataAdapter;
    }

    protected static class ListAdapter extends ComponentAdapter {
        private final JXList list;

        /**
         * Constructs a <code>ListDataAdapter</code> for the specified target
         * component.
         * 
         * @param component
         *            the target component
         */
        public ListAdapter(JXList component) {
            super(component);
            list = component;
        }

        /**
         * Typesafe accessor for the target component.
         * 
         * @return the target component as a {@link org.jdesktop.swingx.JXList}
         */
        public JXList getList() {
            return list;
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasFocus() {
            /** @todo Think through printing implications */
            return list.isFocusOwner() && (row == list.getLeadSelectionIndex());
        }

        public int getRowCount() {
            return list.getWrappedModel().getSize();
        }

        /**
         * {@inheritDoc}
         */
        public Object getValueAt(int row, int column) {
            return list.getWrappedModel().getElementAt(row);
        }

        public Object getFilteredValueAt(int row, int column) {
            return list.getElementAt(row);
        }

        public void setValueAt(Object aValue, int row, int column) {
            throw new UnsupportedOperationException(
                    "Method getFilteredValueAt() not yet implemented.");
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isSelected() {
            /** @todo Think through printing implications */
            return list.isSelectedIndex(row);
        }

        public String getColumnName(int columnIndex) {
            return "Column_" + columnIndex;
        }

        public String getColumnIdentifier(int columnIndex) {
            return null;
        }

    }

    // ------------------------------ renderers

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

    private DelegatingRenderer getDelegatingRenderer() {
        if (delegatingRenderer == null) {
            // only called once... to get hold of the default?
            delegatingRenderer = new DelegatingRenderer(super.getCellRenderer());
        }
        return delegatingRenderer;
    }

    public ListCellRenderer getCellRenderer() {
        return getDelegatingRenderer();
    }

    public void setCellRenderer(ListCellRenderer renderer) {
        // JW: Pending - probably fires propertyChangeEvent with wrong newValue?
        // how about fixedCellWidths?
        // need to test!!
        getDelegatingRenderer().setDelegateRenderer(renderer);
        super.setCellRenderer(delegatingRenderer);
    }

    private class DelegatingRenderer implements ListCellRenderer, RolloverRenderer {

        private ListCellRenderer delegateRenderer;

        public DelegatingRenderer(ListCellRenderer delegate) {
            setDelegateRenderer(delegate);
        }

        public void setDelegateRenderer(ListCellRenderer delegate) {
            if (delegate == null) {
                delegate = new DefaultListCellRenderer();
            }
            delegateRenderer = delegate;
        }

        public boolean isEnabled() {
            return (delegateRenderer instanceof RolloverRenderer) && 
               ((RolloverRenderer) delegateRenderer).isEnabled();
        }
        
        public void doClick() {
            if (isEnabled()) {
                ((RolloverRenderer) delegateRenderer).doClick();
            }
        }
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = null;

            comp = delegateRenderer.getListCellRendererComponent(list,
                        value, index, isSelected, cellHasFocus);
            if (highlighters != null) {
                ComponentAdapter adapter = getComponentAdapter();
                adapter.column = 0;
                adapter.row = index;
                comp = highlighters.apply(comp, adapter);
            }
            return comp;
        }


        public void updateUI() {
            updateRendererUI(delegateRenderer);
        }

        private void updateRendererUI(ListCellRenderer renderer) {
            if (renderer instanceof JComponent) {
                ((JComponent) renderer).updateUI();
            } else if (renderer != null) {
                Component comp = renderer.getListCellRendererComponent(
                        JXList.this, null, -1, false, false);
                if (comp instanceof JComponent) {
                    ((JComponent) comp).updateUI();
                }
            }

        }

    }

    // --------------------------- updateUI

    public void updateUI() {
        super.updateUI();
        updateRendererUI();
    }

    private void updateRendererUI() {
        if (delegatingRenderer != null) {
            delegatingRenderer.updateUI();
        } else {
            ListCellRenderer renderer = getCellRenderer();
            if (renderer instanceof JComponent) {
                ((JComponent) renderer).updateUI();
            }
        }
    }

}
