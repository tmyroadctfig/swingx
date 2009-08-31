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
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ListUI;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.XListAddon;
import org.jdesktop.swingx.renderer.AbstractRenderer;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.rollover.ListRolloverController;
import org.jdesktop.swingx.rollover.ListRolloverProducer;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.rollover.RolloverRenderer;
import org.jdesktop.swingx.search.ListSearchable;
import org.jdesktop.swingx.search.SearchFactory;
import org.jdesktop.swingx.search.Searchable;
import org.jdesktop.swingx.sort.ListSortController;
import org.jdesktop.swingx.sort.SortController;
import org.jdesktop.swingx.sort.StringValueRegistry;
import org.jdesktop.swingx.table.TableColumnExt;

/**
 * Enhanced List component with support for general SwingX sorting/filtering,
 * rendering, highlighting, rollover and search functionality. List specific
 * enhancements include ?? PENDING JW ...
 * 
 * <h2>Sorting and Filtering</h2>
 * JXList supports sorting and filtering. 
 * 
 * Changed to use core support. Usage is very similar to J/X/Table.
 * It provides api to apply a specific sort order, to toggle the sort order and to reset a sort.
 * Sort sequence can be configured by setting a custom comparator.
 * 
 * <pre><code>
 * list.setAutoCreateRowSorter(true);
 * list.setComparator(myComparator);
 * list.setSortOrder(SortOrder.DESCENDING);
 * list.toggleSortOder();
 * list.resetSortOrder();
 * </code></pre>
 * 
 * <p>
 * JXList provides api to access items of the underlying model in view coordinates
 * and to convert from/to model coordinates. 
 * 
 * <b>Note</b>: JXList needs a specific ui-delegate - BasicXListUI and subclasses - which
 * is aware of model vs. view coordiate systems and which controls the synchronization of 
 * selection/dataModel and sorter state. SwingX comes with a subclass for Synth. 
 *  
 * <h2>Rendering and Highlighting</h2>
 * 
 * As all SwingX collection views, a JXList is a HighlighterClient (PENDING JW:
 * formally define and implement, like in AbstractTestHighlighter), that is it
 * provides consistent api to add and remove Highlighters which can visually
 * decorate the rendering component.
 * <p>
 * 
 * <pre><code>
 * 
 * JXList list = new JXList(new Contributors());
 * // implement a custom string representation, concated from first-, lastName
 * StringValue sv = new StringValue() {
 *     public String getString(Object value) {
 *        if (value instanceof Contributor) {
 *           Contributor contributor = (Contributor) value;
 *           return contributor.lastName() + ", " + contributor.firstName(); 
 *        }
 *        return StringValues.TO_STRING(value);
 *     }
 * };
 * list.setCellRenderer(new DefaultListRenderer(sv); 
 * // highlight condition: gold merits
 * HighlightPredicate predicate = new HighlightPredicate() {
 *    public boolean isHighlighted(Component renderer,
 *                     ComponentAdapter adapter) {
 *       if (!(value instanceof Contributor)) return false;              
 *       return ((Contributor) value).hasGold();
 *    }
 * };
 * // highlight with foreground color 
 * list.addHighlighter(new PainterHighlighter(predicate, goldStarPainter);      
 * 
 * </code></pre>
 * 
 * <i>Note:</i> to support the highlighting this implementation wraps the
 * ListCellRenderer set by client code with a DelegatingRenderer which applies
 * the Highlighter after delegating the default configuration to the wrappee. As
 * a side-effect, getCellRenderer does return the wrapper instead of the custom
 * renderer. To access the latter, client code must call getWrappedCellRenderer.
 * <p>
 * 
 * <h2>Rollover</h2>
 * 
 * As all SwingX collection views, a JXList supports per-cell rollover. If
 * enabled, the component fires rollover events on enter/exit of a cell which by
 * default is promoted to the renderer if it implements RolloverRenderer, that
 * is simulates live behaviour. The rollover events can be used by client code
 * as well, f.i. to decorate the rollover row using a Highlighter.
 * 
 * <pre><code>
 * 
 * JXList list = new JXList();
 * list.setRolloverEnabled(true);
 * list.setCellRenderer(new DefaultListRenderer());
 * list.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
 *      null, Color.RED);      
 * 
 * </code></pre>
 * 
 * 
 * <h2>Search</h2>
 * 
 * As all SwingX collection views, a JXList is searchable. A search action is
 * registered in its ActionMap under the key "find". The default behaviour is to
 * ask the SearchFactory to open a search component on this component. The
 * default keybinding is retrieved from the SearchFactory, typically ctrl-f (or
 * cmd-f for Mac). Client code can register custom actions and/or bindings as
 * appropriate.
 * <p>
 * 
 * JXList provides api to vend a renderer-controlled String representation of
 * cell content. This allows the Searchable and Highlighters to use WYSIWYM
 * (What-You-See-Is-What-You-Match), that is pattern matching against the actual
 * string as seen by the user.
 * 
 * 
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 */
public class JXList extends JList {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXList.class.getName());
    
    /**
     * UI Class ID
     */
    public final static String uiClassID = "XListUI";
    
    /**
     * Registers a Addon for JXList.
     */
    // @KEEP JW- will be used if sortable/filterable again
    static {
        LookAndFeelAddons.contribute(new XListAddon());
    }

    

    public static final String EXECUTE_BUTTON_ACTIONCOMMAND = "executeButtonAction";

    /**
     * The pipeline holding the highlighters.
     */
    protected CompoundHighlighter compoundHighlighter;

    /** listening to changeEvents from compoundHighlighter. */
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
    private ListRolloverController<JXList> linkController;

    /** A wrapper around the default renderer enabling decoration. */
    private DelegatingRenderer delegatingRenderer;

    private Searchable searchable;

    private Comparator<?> comparator;

    private boolean autoCreateRowSorter;

    private RowSorter<? extends ListModel> rowSorter;

    private boolean sortable;

    private boolean sortsOnUpdates;

    private StringValueRegistry stringValueRegistry;

    /**
    * Constructs a <code>JXList</code> with an empty model and filters disabled.
    *
    */                                           
    public JXList() {
        this(false);
    }

    /**
     * Constructs a <code>JXList</code> that displays the elements in the
     * specified, non-<code>null</code> model and automatic creation of a RowSorter disabled.
     *
     * @param dataModel   the data model for this list
     * @exception IllegalArgumentException   if <code>dataModel</code>
     *                                           is <code>null</code>
     */                                           
    public JXList(ListModel dataModel) {
        this(dataModel, false);
    }

    /**
     * Constructs a <code>JXList</code> that displays the elements in
     * the specified array and automatic creation of a RowSorter disabled.
     *
     * @param  listData  the array of Objects to be loaded into the data model
     * @throws IllegalArgumentException   if <code>listData</code>
     *                                          is <code>null</code>
     */
    public JXList(Object[] listData) {
        this(listData, false);
    }

    /**
     * Constructs a <code>JXList</code> that displays the elements in
     * the specified <code>Vector</code> and automatic creation of a RowSorter disabled.
     *
     * @param  listData  the <code>Vector</code> to be loaded into the
     *          data model
     * @throws IllegalArgumentException   if <code>listData</code>
     *                                          is <code>null</code>
     */
    public JXList(Vector<?> listData) {
        this(listData, false);
    }


    /**
     * Constructs a <code>JXList</code> with an empty model and
     * automatic creation of a RowSorter as given.
     * 
     * @param autoCreateRowSorter <code>boolean</code> to determine if 
     *  a RowSorter should be created automatically.
     */
    public JXList(boolean autoCreateRowSorter) {
        init(autoCreateRowSorter);
    }

    /**
     * Constructs a <code>JXList</code> with the specified model and
     * automatic creation of a RowSorter as given.
     * 
     * @param dataModel   the data model for this list
     * @param autoCreateRowSorter <code>boolean</code> to determine if 
     *  a RowSorter should be created automatically.
     * @throws IllegalArgumentException   if <code>dataModel</code>
     *                                          is <code>null</code>
     */
    public JXList(ListModel dataModel, boolean autoCreateRowSorter) {
        super(dataModel);
        init(autoCreateRowSorter);
    }

    /**
     * Constructs a <code>JXList</code> that displays the elements in
     * the specified array and automatic creation of a RowSorter as given.
     *
     * @param  listData  the array of Objects to be loaded into the data model
     * @param autoCreateRowSorter <code>boolean</code> to determine if 
     *  a RowSorter should be created automatically.
     * @throws IllegalArgumentException   if <code>listData</code>
     *                                          is <code>null</code>
     */
    public JXList(Object[] listData, boolean autoCreateRowSorter) {
        super(listData);
        if (listData == null) 
           throw new IllegalArgumentException("listData must not be null");
        init(autoCreateRowSorter);
    }

    /**
     * Constructs a <code>JXList</code> that displays the elements in
     * the specified <code>Vector</code> and filtersEnabled property.
     *
     * @param  listData  the <code>Vector</code> to be loaded into the
     *          data model
     * @param autoCreateRowSorter <code>boolean</code> to determine if 
     *  a RowSorter should be created automatically.
     * @throws IllegalArgumentException if <code>listData</code> is <code>null</code>
     */
    public JXList(Vector<?> listData, boolean autoCreateRowSorter) {
        super(listData);
        if (listData == null) 
           throw new IllegalArgumentException("listData must not be null");
        init(autoCreateRowSorter);
    }


    private void init(boolean autoCreateRowSorter) {
        setAutoCreateRowSorter(autoCreateRowSorter);
        setSortable(true);
        setSortsOnUpdates(true);
        Action findAction = createFindAction();
        getActionMap().put("find", findAction);
        
        KeyStroke findStroke = SearchFactory.getInstance().getSearchAccelerator();
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(findStroke, "find");
    }

    private Action createFindAction() {
        return new UIAction("find") {
            public void actionPerformed(ActionEvent e) {
                doFind();
            }
        };
    }

    /** 
     * Starts a search on this List's visible items. This implementation asks the
     * SearchFactory to open a find widget on itself.
     */
    protected void doFind() {
        SearchFactory.getInstance().showFindInput(this, getSearchable());
    }

    /**
     * Returns a Searchable for this component, guaranteed to be not null. This 
     * implementation lazily creates a ListSearchable if necessary.
     *  
     * @return a not-null Searchable for this list.
     * 
     * @see #setSearchable(Searchable)
     * @see org.jdesktop.swingx.search.ListSearchable
     */
    public Searchable getSearchable() {
        if (searchable == null) {
            searchable = new ListSearchable(this);
        }
        return searchable;
    }

    /**
     * Sets the Searchable for this component. If null, a default 
     * Searchable will be created and used.
     * 
     * @param searchable the Searchable to use for this component, may be null to indicate
     *   using the list's default searchable.
     * @see #getSearchable()
     */
    public void setSearchable(Searchable searchable) {
        this.searchable = searchable;
    }
    
//--------------------- Rollover support
    
    /**
     * Sets the property to enable/disable rollover support. If enabled, the list
     * fires property changes on per-cell mouse rollover state, i.e. 
     * when the mouse enters/leaves a list cell. <p>
     * 
     * This can be enabled to show "live" rollover behaviour, f.i. the cursor over a cell 
     * rendered by a JXHyperlink.<p>
     * 
     * Default value is disabled.
     * 
     * @param rolloverEnabled a boolean indicating whether or not the rollover
     *   functionality should be enabled.
     * 
     * @see #isRolloverEnabled()
     * @see #getLinkController()
     * @see #createRolloverProducer()
     * @see org.jdesktop.swingx.rollover.RolloverRenderer  
     *    
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

    /**
     * Returns a boolean indicating whether or not rollover support is enabled. 
     *
     * @return a boolean indicating whether or not rollover support is enabled. 
     * 
     * @see #setRolloverEnabled(boolean)
     */
    public boolean isRolloverEnabled() {
        return rolloverProducer != null;
    }
    
    /**
     * Returns the RolloverController for this component. Lazyly creates the 
     * controller if necessary, that is the return value is guaranteed to be 
     * not null. <p>
     * 
     * PENDING JW: rename to getRolloverController
     * 
     * @return the RolloverController for this tree, guaranteed to be not null.
     * 
     * @see #setRolloverEnabled(boolean)
     * @see #createLinkController()
     * @see org.jdesktop.swingx.rollover.RolloverController
     */
    protected ListRolloverController<JXList> getLinkController() {
        if (linkController == null) {
            linkController = createLinkController();
        }
        return linkController;
    }

    /**
     * Creates and returns a RolloverController appropriate for this component.
     * 
     * @return a RolloverController appropriate for this component.
     * 
     * @see #getLinkController()
     * @see org.jdesktop.swingx.rollover.RolloverController
     */
    protected ListRolloverController<JXList> createLinkController() {
        return new ListRolloverController<JXList>();
    }


    /**
     * Creates and returns the RolloverProducer to use with this tree.
     * <p>
     * 
     * @return <code>RolloverProducer</code> to use with this tree
     * 
     * @see #setRolloverEnabled(boolean)
     */
    protected RolloverProducer createRolloverProducer() {
        return new ListRolloverProducer();
    }

    //--------------------- public sort api
    
    /**
     * Returns {@code true} if whenever the model changes, a new
     * {@code RowSorter} should be created and installed
     * as the table's sorter; otherwise, returns {@code false}. 
     *
     * @return true if a {@code RowSorter} should be created when
     *         the model changes
     * @since 1.6
     */
    public boolean getAutoCreateRowSorter() {
        return autoCreateRowSorter;
    }

    /**
     * Specifies whether a {@code RowSorter} should be created for the
     * list whenever its model changes.
     * <p>
     * When {@code setAutoCreateRowSorter(true)} is invoked, a {@code
     * RowSorter} is immediately created and installed on the
     * list.  While the {@code autoCreateRowSorter} property remains
     * {@code true}, every time the model is changed, a new {@code
     * RowSorter} is created and set as the list's row sorter.<p>
     * 
     * The default value is false.
     *
     * @param autoCreateRowSorter whether or not a {@code RowSorter}
     *        should be automatically created
     * @beaninfo
     *        bound: true
     *    preferred: true
     *  description: Whether or not to turn on sorting by default.
     */
    public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
        if (getAutoCreateRowSorter() == autoCreateRowSorter) return;
        boolean oldValue = getAutoCreateRowSorter();
        this.autoCreateRowSorter = autoCreateRowSorter;
        if (autoCreateRowSorter) {
            setRowSorter(createDefaultRowSorter());
        }
        firePropertyChange("autoCreateRowSorter", oldValue,
                           getAutoCreateRowSorter());
    }

    /**
     * Creates and returns the default RowSorter. Note that this is already
     * configured to the current ListModel.
     * 
     * PENDING JW: review method signature - better expose the need for the
     * model by adding a parameter? 
     * 
     * @return the default RowSorter.
     */
    protected RowSorter<? extends ListModel> createDefaultRowSorter() {
        return new ListSortController<ListModel>(getModel());
    }
    /**
     * Returns the object responsible for sorting.
     *
     * @return the object responsible for sorting
     * @since 1.6
     */
    public RowSorter<? extends ListModel> getRowSorter() {
        return rowSorter;
    }

    /**
     * Sets the <code>RowSorter</code>.  <code>RowSorter</code> is used
     * to provide sorting and filtering to a <code>JXList</code>.
     * <p>
     * This method clears the selection and resets any variable row heights.
     * <p>
     * If the underlying model of the <code>RowSorter</code> differs from
     * that of this <code>JXList</code> undefined behavior will result.
     *
     * @param sorter the <code>RowSorter</code>; <code>null</code> turns
     *        sorting off
     */
    public void setRowSorter(RowSorter<? extends ListModel> sorter) {
        RowSorter<? extends ListModel> oldRowSorter = getRowSorter();
        this.rowSorter = sorter;
        
//        if (sortManager != null) {
//            oldRowSorter = sortManager.sorter;
//            sortManager.dispose();
//            sortManager = null;
//        }
//        rowModel = null;
//        clearSelectionAndLeadAnchor();
//        if (sorter != null) {
//            sortManager = new SortManager(sorter);
//        }
//        resizeAndRepaint();
        configureSorterProperties();
        firePropertyChange("rowSorter", oldRowSorter, sorter);
    }

    /**
     * Propagates sort-related properties from table/columns to the sorter if it
     * is of type SortController, does nothing otherwise.
     * 
     */
    protected void configureSorterProperties() {
        if (getSortController() == null) return;
        // configure from table properties
        getSortController().setSortable(sortable);
        getSortController().setSortsOnUpdates(sortsOnUpdates);
        getSortController().setComparator(0, comparator);
        getSortController().setStringValueProvider(getStringValueRegistry());
    }

    /**
     * Sets &quot;sortable&quot; property indicating whether or not this list
     * isSortable. 
     * 
     * <b>Note</b>: as of post-1.0 this property is propagated to the SortController. 
     * Whether or not a change triggers a re-sort is up to either the concrete controller 
     * implementation (the default doesn't) or client code. This behaviour is
     * different from old SwingX style sorting.
     * 
     * @see TableColumnExt#isSortable()
     * @param sortable boolean indicating whether or not this table supports
     *        sortable columns
     */
    public void setSortable(boolean sortable) {
        boolean old = isSortable();
        this.sortable = sortable;
        if (getSortController() != null) {
            getSortController().setSortable(sortable);
        }
        firePropertyChange("sortable", old, isSortable());
    }

    /**
     * Returns the table's sortable property.<p>
     * 
     * Note: as of post-1.0 this property is propagated to the SortController. 
     * 
     * @return true if the table is sortable.
     */
    public boolean isSortable() {
        return getSortController() != null ? getSortController().isSortable() : sortable;
    }

    /**
     * If true, specifies that a sort should happen when the underlying
     * model is updated (<code>rowsUpdated</code> is invoked).  For
     * example, if this is true and the user edits an entry the
     * location of that item in the view may change.  The default is
     * true.
     *
     * @param sortsOnUpdates whether or not to sort on update events
     */
    public void setSortsOnUpdates(boolean sortsOnUpdates) {
        boolean old = getSortsOnUpdates();
        this.sortsOnUpdates = sortsOnUpdates;
        if (getSortController() != null) {
            getSortController().setSortsOnUpdates(sortsOnUpdates);
        }
        firePropertyChange("sortsOnUpdates", old, getSortsOnUpdates());
    }
    
    /**
     * Returns true if  a sort should happen when the underlying
     * model is updated; otherwise, returns false.
     *
     * @return whether or not to sort when the model is updated
     */
    public boolean getSortsOnUpdates() {
        return getSortController() != null ? getSortController().getSortsOnUpdates() : sortsOnUpdates;
    }

    /**
     * Sets the filter to the sorter, if available and of type SortController.
     * Does nothing otherwise.
     * <p>
     *
     * @param filter the filter used to determine what entries should be
     *        included
     */
    public void setRowFilter(RowFilter<? super ListModel, ? super Integer> filter) {
        if (getSortController() == null) return;
        getSortController().setRowFilter(filter);
    }
    
    /**
     * Returns the filter of the sorter, if available and of type SortController.
     * Returns null otherwise.<p>
     * 
     * PENDING JW: generics? had to remove return type from getSortController to 
     * make this compilable, so probably wrong. 
     * 
     * @return the filter used in the sorter.
     */
    @SuppressWarnings("unchecked")
    public RowFilter<?, ?> getRowFilter() {
        return getSortController() != null ? getSortController().getRowFilter() : null;
    }
    
    /**
     * Sets the sortorder cycle of the sorter, if available and of type SortController. Does
     * nothing otherwise.<p>
     * 
     * PENDING JW: make property of the table as well, to propagate to sorter if
     *   reset?
     * 
     * @param cycle the sequence of zero or more not-null SortOrders to cycle through.
     * @throws NullPointerException if the array or any of its elements are null
     * 
     */
    public void setSortOrderCycle(SortOrder... cycle) {
        if (getSortController() == null) return;
        getSortController().setSortOrderCycle(cycle);
    }
    
    /**
     * Returns the sequence of sortOrders of the sorter, if available and of type SortController.
     * Null otherwise.
     *   
     * @return the sort order cycle of the sorter, if available, null otherwise.
     */
    public SortOrder[] getSortOrderCycle() {
        return getSortController() != null ? getSortController().getSortOrderCycle() : null;
    }

    /**
     * Resets sorting of all columns.
     * Delegates to the SortController if available, or does nothing if not.<p>
     * 
     * PENDING JW: method name - consistent in SortController and here.
     * 
     */
    public void resetSortOrder() {
        if (getSortController() == null)
            return;
        getSortController().resetSortOrders();
    }

    /**
     * 
     * Toggles the sort order of the list.
     * Delegates to the SortController if available, or does nothing if not.<p>
     * 
     * <p>
     * The exact behaviour is defined by the SortController's toggleSortOrder
     * implementation. Typically a unsorted list is sorted in ascending order,
     * a sorted list's order is reversed.
     * <p>
     * 
     * 
     */
    public void toggleSortOrder() {
        if (getSortController() == null)
            return;
        getSortController().toggleSortOrder(0);
    }

    /**
     * Sorts the list using SortOrder.
     * Delegates to the SortController if available, or does nothing if not.<p>
     * 
     * @param sortOrder the sort order to use.
     * 
     */
    public void setSortOrder(SortOrder sortOrder) {
        if (getSortController() == null)
            return;
        getSortController().setSortOrder(0, sortOrder);
    }


    /**
     * Returns the SortOrder. 
     * Delegates to the SortController if available, or returns SortOrder.UNSORTED if not.<p>
     * 
     * @return the current SortOrder
     */
    public SortOrder getSortOrder() {
        if (getSortController() == null)
            return SortOrder.UNSORTED;
        return getSortController().getSortOrder(0);
    }

    /**
     * 
     * @return the comparator used.
     * @see #setComparator(Comparator)
     */
    public Comparator<?> getComparator() {
        return comparator;
    }
    
    /**
     * Sets the comparator to use for sorting.<p>
     *  
     * <b>Note</b>: as of post-1.0 the property is propagated to the SortController,
     * if available.
     * Whether or not a change triggers a re-sort is up to either the concrete controller 
     * implementation (the default doesn't) or client code. This behaviour is
     * different from old SwingX style sorting.
     * 
     * @param comparator the comparator to use.
     */
    public void setComparator(Comparator<?> comparator) {
        Comparator<?> old = getComparator();
        this.comparator = comparator;
        updateSortAfterComparatorChange();
        firePropertyChange("comparator", old, getComparator());
    }
    
    /**
     * Updates the SortController's comparator, if available. Does nothing otherwise. 
     *
     */
    protected void updateSortAfterComparatorChange() {
        if (getSortController() == null) return;
        getSortController().setComparator(0, getComparator());
    }

    /**
     * Returns the currently active SortController. May be null if RowSorter
     * is null or not of type SortController.
     * 
     * @return the currently active <code>SortController</code> may be null
     */
    @SuppressWarnings("unchecked")
    private SortController<? extends ListModel> getSortController() {
        if (getRowSorter() instanceof SortController<?>) {
            // JW: the RowSorter is always of type <? extends ListModel>
            // so the unchecked cast is safe
            return (SortController<? extends ListModel>) getRowSorter();
        }
        return null;
    }
    
    
    // ---------------------------- filters

    /**
     * Returns the element at the given index. The index is in view coordinates
     * which might differ from model coordinates if filtering is enabled and
     * filters/sorters are active.
     * 
     * @param viewIndex the index in view coordinates
     * @return the element at the index
     * @throws IndexOutOfBoundsException if viewIndex < 0 or viewIndex >=
     *         getElementCount()
     */
    public Object getElementAt(int viewIndex) {
        return getModel().getElementAt(convertIndexToModel(viewIndex));
    }

    /**
     * Returns the number of elements in this list in view 
     * coordinates. If filters are active this number might be
     * less than the number of elements in the underlying model.
     * 
     * @return number of elements in this list in view coordinates
     */
    public int getElementCount() {
        return getRowSorter() != null ? 
                getRowSorter().getViewRowCount(): getModel().getSize();
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
        return getRowSorter() != null ? 
                getRowSorter().convertRowIndexToModel(viewIndex):viewIndex;
    }

    /**
     * Convert index from model coordinates to view coordinates accounting
     * for the presence of sorters and filters.
     * 
     * @param modelIndex index in model coordinates
     * @return index in view coordinates if the model index maps to a view coordinate
     *          or -1 if not contained in the view.
     * 
     */
    public int convertIndexToView(int modelIndex) {
        return getRowSorter() != null 
            ? getRowSorter().convertRowIndexToView(modelIndex) : modelIndex;
    }

    /**
     * 
     * @return the underlying model, same as getModel().
     * @deprecated no longer used - custom ui-delegate does-the-right-thing when
     *   accessing elements.
     */
    @Deprecated
    public ListModel getWrappedModel() {
        return getModel();
    }


    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to update selectionMapper
     */
    @Override 
    public void setSelectionModel(ListSelectionModel newModel) {
        super.setSelectionModel(newModel);
//        getSelectionMapper().setViewSelectionModel(getSelectionModel());
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Sets the underlying data model. Note that if isFilterEnabled you must
     * call getWrappedModel to access the model given here. In this case
     * getModel returns a wrapper around the data!
     * 
     * @param model the data model for this list.
     * 
     */
    @Override
    public void setModel(ListModel model) {
        super.setModel(model);
        if (getAutoCreateRowSorter()) {
            setRowSorter(createDefaultRowSorter());
        }
    }


    // ---------------------------- uniform data model

    /**
     * @return the unconfigured ComponentAdapter.
     */
    protected ComponentAdapter getComponentAdapter() {
        if (dataAdapter == null) {
            dataAdapter = new ListAdapter(this);
        }
        return dataAdapter;
    }

    /**
     * Convenience to access a configured ComponentAdapter.
     * Note: the column index of the configured adapter is always 0.
     * 
     * @param index the row index in view coordinates, must be valid.
     * @return the configured ComponentAdapter.
     */
    protected ComponentAdapter getComponentAdapter(int index) {
        ComponentAdapter adapter = getComponentAdapter();
        adapter.column = 0;
        adapter.row = index;
        return adapter;
    }
    
    /**
     * A component adapter targeted at a JXList.
     */
    protected static class ListAdapter extends ComponentAdapter {
        private final JXList list;

        /**
         * Constructs a <code>ListAdapter</code> for the specified target
         * JXList.
         * 
         * @param component  the target list.
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
        @Override
        public boolean hasFocus() {
            /** TODO: Think through printing implications */
            return list.isFocusOwner() && (row == list.getLeadSelectionIndex());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getRowCount() {
            return list.getModel().getSize();
        }

        /**
         * {@inheritDoc} <p>
         * Overridden to return value at implicit view coordinates.
         */
        @Override
        public Object getValue() {
            return getFilteredValueAt(row, 0);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object getValueAt(int row, int column) {
            return list.getModel().getElementAt(row);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object getFilteredValueAt(int row, int column) {
            return list.getElementAt(row);
        }

        
        /**
         * {@inheritDoc}
         */
        @Override
        public String getFilteredStringAt(int row, int column) {
            return list.getStringAt(row);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getString() {
            return getFilteredStringAt(row, 0);
        }

        /**
         * {@inheritDoc}
         * This is implemented to query the table's StringValueRegistry for an appropriate
         * StringValue and use that for getting the string representation.
         */
        @Override
        public String getStringAt(int row, int column) {
            StringValue sv = list.getStringValueRegistry().getStringValue(row, column);
            return sv.getString(getValueAt(row, column));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEditable() {
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSelected() {
            /** TODO: Think through printing implications */
            return list.isSelectedIndex(row);
        }

    }

    // ------------------------------ renderers


    
    /**
     * Sets the <code>Highlighter</code>s to the table, replacing any old settings.
     * None of the given Highlighters must be null.<p>
     * 
     * This is a bound property. <p> 
     * 
     * Note: as of version #1.257 the null constraint is enforced strictly. To remove
     * all highlighters use this method without param.
     * 
     * @param highlighters zero or more not null highlighters to use for renderer decoration.
     * @throws NullPointerException if array is null or array contains null values.
     * 
     * @see #getHighlighters()
     * @see #addHighlighter(Highlighter)
     * @see #removeHighlighter(Highlighter)
     * 
     */
    public void setHighlighters(Highlighter... highlighters) {
        Highlighter[] old = getHighlighters();
        getCompoundHighlighter().setHighlighters(highlighters);
        firePropertyChange("highlighters", old, getHighlighters());
    }

    /**
     * Returns the <code>Highlighter</code>s used by this table.
     * Maybe empty, but guarantees to be never null.
     * 
     * @return the Highlighters used by this table, guaranteed to never null.
     * @see #setHighlighters(Highlighter[])
     */
    public Highlighter[] getHighlighters() {
        return getCompoundHighlighter().getHighlighters();
    }
    /**
     * Appends a <code>Highlighter</code> to the end of the list of used
     * <code>Highlighter</code>s. The argument must not be null. 
     * <p>
     * 
     * @param highlighter the <code>Highlighter</code> to add, must not be null.
     * @throws NullPointerException if <code>Highlighter</code> is null.
     * 
     * @see #removeHighlighter(Highlighter)
     * @see #setHighlighters(Highlighter[])
     */
    public void addHighlighter(Highlighter highlighter) {
        Highlighter[] old = getHighlighters();
        getCompoundHighlighter().addHighlighter(highlighter);
        firePropertyChange("highlighters", old, getHighlighters());
    }

    /**
     * Removes the given Highlighter. <p>
     * 
     * Does nothing if the Highlighter is not contained.
     * 
     * @param highlighter the Highlighter to remove.
     * @see #addHighlighter(Highlighter)
     * @see #setHighlighters(Highlighter...)
     */
    public void removeHighlighter(Highlighter highlighter) {
        Highlighter[] old = getHighlighters();
        getCompoundHighlighter().removeHighlighter(highlighter);
        firePropertyChange("highlighters", old, getHighlighters());
    }
    
    /**
     * Returns the CompoundHighlighter assigned to the table, null if none.
     * PENDING: open up for subclasses again?.
     * 
     * @return the CompoundHighlighter assigned to the table.
     */
    protected CompoundHighlighter getCompoundHighlighter() {
        if (compoundHighlighter == null) {
            compoundHighlighter = new CompoundHighlighter();
            compoundHighlighter.addChangeListener(getHighlighterChangeListener());
        }
        return compoundHighlighter;
    }

    /**
     * Returns the <code>ChangeListener</code> to use with highlighters. Lazily 
     * creates the listener.
     * 
     * @return the ChangeListener for observing changes of highlighters, 
     *   guaranteed to be <code>not-null</code>
     */
    protected ChangeListener getHighlighterChangeListener() {
        if (highlighterChangeListener == null) {
            highlighterChangeListener = createHighlighterChangeListener();
        }
        return highlighterChangeListener;
    }

    /**
     * Creates and returns the ChangeListener observing Highlighters.
     * <p>
     * Here: repaints the table on receiving a stateChanged.
     * 
     * @return the ChangeListener defining the reaction to changes of
     *         highlighters.
     */
    protected ChangeListener createHighlighterChangeListener() {
        return new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                repaint();
            }
        };
    }

    /**
     * Returns the StringValueRegistry which defines the string representation for
     * each cells. This is strictly for internal use by the table, which has the 
     * responsibility to keep in synch with registered renderers.<p>
     * 
     * Currently exposed for testing reasons, client code is recommended to not use nor override.
     * 
     * @return
     */
    protected StringValueRegistry getStringValueRegistry() {
        if (stringValueRegistry == null) {
            stringValueRegistry = createDefaultStringValueRegistry();
        }
        return stringValueRegistry;
    }

    /**
     * Creates and returns the default registry for StringValues.<p>
     * 
     * @return the default registry for StringValues.
     */
    protected StringValueRegistry createDefaultStringValueRegistry() {
        return new StringValueRegistry();
    }
    
    
    
    /**
     * Returns the string representation of the cell value at the given position. 
     * 
     * @param row the row index of the cell in view coordinates
     * @return the string representation of the cell value as it will appear in the 
     *   table. 
     */
    public String getStringAt(int row) {
        // changed implementation to use StringValueRegistry
        StringValue stringValue = getStringValueRegistry().getStringValue(
                convertIndexToModel(row), 0);
        return stringValue.getString(getElementAt(row));
    }

    private DelegatingRenderer getDelegatingRenderer() {
        if (delegatingRenderer == null) {
            // only called once... to get hold of the default?
            delegatingRenderer = new DelegatingRenderer();
        }
        return delegatingRenderer;
    }

    /**
     * Creates and returns the default cell renderer to use. Subclasses
     * may override to use a different type. Here: returns a <code>DefaultListRenderer</code>.
     * 
     * @return the default cell renderer to use with this list.
     */
    protected ListCellRenderer createDefaultCellRenderer() {
        return new DefaultListRenderer();
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to return the delegating renderer which is wrapped around the
     * original to support highlighting. The returned renderer is of type 
     * DelegatingRenderer and guaranteed to not-null<p>
     * 
     * @see #setCellRenderer(ListCellRenderer)
     * @see DelegatingRenderer
     */
    @Override
    public ListCellRenderer getCellRenderer() {
        return getDelegatingRenderer();
    }

    /**
     * Returns the renderer installed by client code or the default if none has
     * been set.
     * 
     * @return the wrapped renderer.
     * @see #setCellRenderer(ListCellRenderer)
     */
    public ListCellRenderer getWrappedCellRenderer() {
        return getDelegatingRenderer().getDelegateRenderer();
    }
    
    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to wrap the given renderer in a DelegatingRenderer to support
     * highlighting. <p>
     * 
     * Note: the wrapping implies that the renderer returned from the getCellRenderer
     * is <b>not</b> the renderer as given here, but the wrapper. To access the original,
     * use <code>getWrappedCellRenderer</code>.
     * 
     * @see #getWrappedCellRenderer()
     * @see #getCellRenderer()
     * 
     */
    @Override
    public void setCellRenderer(ListCellRenderer renderer) {
        // JW: Pending - probably fires propertyChangeEvent with wrong newValue?
        // how about fixedCellWidths?
        // need to test!!
        getDelegatingRenderer().setDelegateRenderer(renderer);
        getStringValueRegistry().setStringValue(
                renderer instanceof StringValue ? (StringValue) renderer: null, 
                        0);
        super.setCellRenderer(delegatingRenderer);
    }

    /**
     * A decorator for the original ListCellRenderer. Needed to hook highlighters
     * after messaging the delegate.<p>
     * 
     * PENDING JW: formally implement UIDependent?
     */
    public class DelegatingRenderer implements ListCellRenderer, RolloverRenderer {
        /** the delegate. */
        private ListCellRenderer delegateRenderer;

        /**
         * Instantiates a DelegatingRenderer with list's default renderer as delegate.
         */
        public DelegatingRenderer() {
            this(null);
        }
        
        /**
         * Instantiates a DelegatingRenderer with the given delegate. If the
         * delegate is null, the default is created via the list's factory method.
         * 
         * @param delegate the delegate to use, if null the list's default is
         *   created and used.
         */
        public DelegatingRenderer(ListCellRenderer delegate) {
            setDelegateRenderer(delegate);
        }

        /**
         * Sets the delegate. If the
         * delegate is null, the default is created via the list's factory method.
         * 
         * @param delegate the delegate to use, if null the list's default is
         *   created and used.
         */
        public void setDelegateRenderer(ListCellRenderer delegate) {
            if (delegate == null) {
                delegate = createDefaultCellRenderer();
            }
            delegateRenderer = delegate;
        }

        /**
         * Returns the delegate.
         * 
         * @return the delegate renderer used by this renderer, guaranteed to
         *   not-null.
         */
        public ListCellRenderer getDelegateRenderer() {
            return delegateRenderer;
        }

        /**
         * Updates the ui of the delegate.
         */
         public void updateUI() {
             updateRendererUI(delegateRenderer);
         }

         /**
          * 
          * @param renderer the renderer to update the ui of.
          */
         private void updateRendererUI(ListCellRenderer renderer) {
             if (renderer == null) return;
             Component comp = null;
             if (renderer instanceof AbstractRenderer) {
                 comp = ((AbstractRenderer) renderer).getComponentProvider().getRendererComponent(null);
             } else if (renderer instanceof Component) {
                 comp = (Component) renderer;
             } else {
                 try {
                     comp = renderer.getListCellRendererComponent(
                             JXList.this, null, -1, false, false);
                } catch (Exception e) {
                    // nothing to do - renderer barked on off-range row
                }
             }
             if (comp != null) {
                 SwingUtilities.updateComponentTreeUI(comp);
             }

         }
         
         // --------- implement ListCellRenderer
        /**
         * {@inheritDoc} <p>
         * 
         * Overridden to apply the highlighters, if any, after calling the delegate.
         * The decorators are not applied if the row is invalid.
         */
       public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = delegateRenderer.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
            if ((compoundHighlighter != null) && (index >= 0) && (index < getElementCount())) {
                comp = compoundHighlighter.highlight(comp, getComponentAdapter(index));
            }
            return comp;
        }


        // implement RolloverRenderer
        
        /**
         * {@inheritDoc}
         * 
         */
        public boolean isEnabled() {
            return (delegateRenderer instanceof RolloverRenderer) && 
               ((RolloverRenderer) delegateRenderer).isEnabled();
        }
        
        /**
         * {@inheritDoc}
         */
        public void doClick() {
            if (isEnabled()) {
                ((RolloverRenderer) delegateRenderer).doClick();
            }
        }
        
    }

    // --------------------------- updateUI

    
    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to update renderer and Highlighters.
     */
    @Override
    public void updateUI() {
        // PENDING JW: temporary during dev to quickly switch between default and custom ui
        if (getUIClassID() == super.getUIClassID()) {
            super.updateUI();
        } else {    
            setUI((ListUI) LookAndFeelAddons.getUI(this, ListUI.class));
        }
        updateRendererUI();
        updateHighlighterUI();
    }

    @Override
    public String getUIClassID() {
        // PENDING JW: temporary during dev to quickly switch between default and custom ui
//        return super.getUIClassID();
        return uiClassID;
    }

    private void updateRendererUI() {
        if (delegatingRenderer != null) {
            delegatingRenderer.updateUI();
        } else {
            ListCellRenderer renderer = getCellRenderer();
            if (renderer instanceof Component) {
                SwingUtilities.updateComponentTreeUI((Component) renderer);
            }
        }
    }

    /**
     * Updates highlighter after <code>updateUI</code> changes.
     * 
     * @see org.jdesktop.swingx.decorator.UIDependent
     */
    protected void updateHighlighterUI() {
        if (compoundHighlighter == null) return;
        compoundHighlighter.updateUI();
    }

}