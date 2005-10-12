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
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PipelineEvent;
import org.jdesktop.swingx.decorator.PipelineListener;
import org.jdesktop.swingx.decorator.Selection;
import org.jdesktop.swingx.decorator.Sorter;

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

    private Selection selection;

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
            return getModelSize();
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
            linkController = new LinkController();
            addPropertyChangeListener(linkController);
        } else {
            removeMouseListener(rolloverProducer);
            removeMouseMotionListener(rolloverProducer);
            rolloverProducer = null;
            removePropertyChangeListener(linkController);
            linkController = null;
        }
        firePropertyChange("rolloverEnabled", old, isRolloverEnabled());
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

    public void setLinkVisitor(ActionListener linkVisitor) {
        if (linkVisitor != null) {
            setRolloverEnabled(true);
            getDelegatingRenderer().setLinkVisitor(linkVisitor);
        } else {
            // JW: think - need to revert?
        }

    }

    /**
     * listens to rollover properties. 
     * Repaints effected component regions.
     * Updates link cursor.
     * 
     * @author Jeanette Winzenburg
     */
    public class LinkController implements PropertyChangeListener {

        private Cursor oldCursor;
        public void propertyChange(PropertyChangeEvent evt) {
            if (RolloverProducer.ROLLOVER_KEY.equals(evt.getPropertyName())) {
                   rollover((JXList) evt.getSource(), (Point) evt.getOldValue(),
                            (Point) evt.getOldValue());
            } else if (RolloverProducer.CLICKED_KEY.equals(evt.getPropertyName())) {
                    click((JXList) evt.getSource(), (Point) evt.getOldValue(),
                            (Point) evt.getNewValue());
            }
        }

        
//    --------------------------------- JList rollover
        
        private void rollover(JXList list, Point oldLocation, Point newLocation) {
            setLinkCursor(list, newLocation);
            // JW: partial repaints incomplete
            list.repaint();
        }

        private void click(JXList list, Point oldLocation, Point newLocation) {
            if (!isLinkElement(list, newLocation)) return;
            ListCellRenderer renderer = list.getCellRenderer();
            // PENDING: JW - don't ask the model, ask the list!
            Component comp = renderer.getListCellRendererComponent(list, list.getModel().getElementAt(newLocation.y), newLocation.y, false, true);
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
        private void setLinkCursor(JXList list, Point location) {
            if (isLinkElement(list, location)) {
                    oldCursor = list.getCursor();
                    list.setCursor(Cursor
                            .getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                    list.setCursor(oldCursor);
                    oldCursor = null;
            }

        }
        private boolean isLinkElement(JXList list, Point location) {
            if (location == null || location.y < 0) return false;
            // PENDING: JW - don't ask the model, ask the list!
            return (list.getModel().getElementAt(location.y) instanceof LinkModel);
        }
        

    }


//--------------------------- searching
    
    
    // ---------------------------- filters

    public Object getElementAt(int viewIndex) {
        return getModel().getElementAt(viewIndex);
    }

    /**
     * PENDING: misnomer - this is the view size!
     * @return
     */
    public int getModelSize() {
        return getModel().getSize();
    }

    public int convertRowIndexToModel(int viewIndex) {
        return isFilterEnabled() ? getFilters().convertRowIndexToModel(
                viewIndex) : viewIndex;
    }

    public int convertRowIndexToView(int modelIndex) {
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
     * PENDING: cleanup state transitions!! - currently this can be safely applied
     * once only to enable. Internal state is inconsistent if trying to disable
     * again.   
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

    private Selection getSelection() {
        if (selection == null) {
            selection = new Selection(filters, getSelectionModel());
        }
        return selection;
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
        Sorter sorter = null;
        if (old != null) {
            old.removePipelineListener(pipelineListener);
            sorter = old.getSorter();
        }
        if (pipeline == null) {
            pipeline = new FilterPipeline();
        }
        filters = pipeline;
        filters.setSorter(sorter);
        use(filters);
        getSelection().setFilters(filters);
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
                    getSelection().lock();
                    fireContentsChanged(this, -1, -1);
                    updateSelection(e);
                    getFilters().flush();

                }

            };
            return l;
        }

        protected void updateSelection(ListDataEvent e) {
            if (e.getType() == ListDataEvent.INTERVAL_REMOVED) {
                getSelection()
                        .removeIndexInterval(e.getIndex0(), e.getIndex1());
            } else if (e.getType() == ListDataEvent.INTERVAL_ADDED) {

                int minIndex = Math.min(e.getIndex0(), e.getIndex1());
                int maxIndex = Math.max(e.getIndex0(), e.getIndex1());
                int length = maxIndex - minIndex + 1;
                getSelection().insertIndexInterval(minIndex, length, true);
            } else {
                getSelection().clearModelSelection();
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

    private class DelegatingRenderer implements ListCellRenderer {

        private LinkRenderer linkRenderer;

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

        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = null;

            if (value instanceof LinkModel) {
                comp = getLinkRenderer().getListCellRendererComponent(list,
                        value, index, isSelected, cellHasFocus);
            } else {
                comp = delegateRenderer.getListCellRendererComponent(list,
                        value, index, isSelected, cellHasFocus);
            }
            if (highlighters != null) {
                ComponentAdapter adapter = getComponentAdapter();
                adapter.column = 0;
                adapter.row = index;
                comp = highlighters.apply(comp, adapter);
            }
            return comp;
        }

        private LinkRenderer getLinkRenderer() {
            if (linkRenderer == null) {
                linkRenderer = new LinkRenderer();
            }
            return linkRenderer;
        }

        public void setLinkVisitor(ActionListener linkVisitor) {
            getLinkRenderer().setVisitingDelegate(linkVisitor);

        }

        public void updateUI() {
            updateRendererUI(linkRenderer);
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
