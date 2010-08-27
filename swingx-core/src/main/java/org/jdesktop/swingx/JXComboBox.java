/*
 * $Id$
 * 
 * Copyright 2010 Sun Microsystems, Inc., 4150 Network Circle,
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.accessibility.Accessible;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.tree.TreeCellRenderer;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.plaf.UIDependent;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.JRendererPanel;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.rollover.RolloverRenderer;
import org.jdesktop.swingx.util.Contract;

/**
 * An enhanced {@code JComboBox} that provides the following additional functionality:
 * <p>
 * Auto-starts edits correctly for AutoCompletion when inside a {@code JTable}. A normal {@code
 * JComboBox} fails to recognize the first key stroke when it has been
 * {@link org.jdesktop.swingx.autocomplete.AutoCompleteDecorator#decorate(JComboBox) decorated}.
 * <p>
 * Adds highlighting support.
 * 
 * @author Karl Schaefer
 * @author Jeanette Winzenburg
 */
@SuppressWarnings("serial")
public class JXComboBox extends JComboBox {
    /**
     * A decorator for the original ListCellRenderer. Needed to hook highlighters
     * after messaging the delegate.<p>
     */
    public class DelegatingRenderer implements ListCellRenderer, RolloverRenderer, UIDependent {
        /** the delegate. */
        private ListCellRenderer delegateRenderer;
        private JRendererPanel wrapper;

        /**
         * Instantiates a DelegatingRenderer with combo box's default renderer as delegate.
         */
        public DelegatingRenderer() {
            this(null);
        }
        
        /**
         * Instantiates a DelegatingRenderer with the given delegate. If the
         * delegate is {@code null}, the default is created via the combo box's factory method.
         * 
         * @param delegate the delegate to use, if {@code null} the combo box's default is
         *   created and used.
         */
        public DelegatingRenderer(ListCellRenderer delegate) {
            wrapper = new JRendererPanel(new BorderLayout());
            setDelegateRenderer(delegate);
        }

        /**
         * Sets the delegate. If the delegate is {@code null}, the default is created via the combo
         * box's factory method.
         * 
         * @param delegate
         *            the delegate to use, if null the list's default is created and used.
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
         * {@inheritDoc}
         */
        public void updateUI() {
             wrapper.updateUI();
             
             if (delegateRenderer instanceof UIDependent) {
                 ((UIDependent) delegateRenderer).updateUI();
             } else if (delegateRenderer instanceof Component) {
                 SwingUtilities.updateComponentTreeUI((Component) delegateRenderer);
             } else if (delegateRenderer != null) {
                 try {
                     Component comp = delegateRenderer.getListCellRendererComponent(
                             getPopupListFor(JXComboBox.this), null, -1, false, false);
                     SwingUtilities.updateComponentTreeUI(comp);
                 } catch (Exception e) {
                     // nothing to do - renderer barked on off-range row
                 }
             }
         }
         
         // --------- implement ListCellRenderer
        /**
         * {@inheritDoc} <p>
         * 
         * Overridden to apply the highlighters, if any, after calling the delegate.
         * The decorators are not applied if the row is invalid.
         */
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            Component comp = null;

            if (index == -1) {
                comp = delegateRenderer.getListCellRendererComponent(list, value,
                        getSelectedIndex(), isSelected, cellHasFocus);
                
                if (isUseHighlightersForCurrentValue() && compoundHighlighter != null && getSelectedIndex() != -1) {
                    comp = compoundHighlighter.highlight(comp, getComponentAdapter(getSelectedIndex()));
                    
                    // this is done to "trick" BasicComboBoxUI.paintCurrentValue which resets all of
                    // the painted information after asking the list to render the value. the panel
                    // wrappers receives all of the post-rendering configuration, which is dutifully
                    // ignored by the real rendering component
                    wrapper.add(comp);
                    comp = wrapper;
                }
            } else {
                comp = delegateRenderer.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
                
                if ((compoundHighlighter != null) && (index >= 0) && (index < getItemCount())) {
                    comp = compoundHighlighter.highlight(comp, getComponentAdapter(index));
                }
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
    
    protected static class ComboBoxAdapter extends ComponentAdapter {
        private final JComboBox comboBox;

        /**
         * Constructs a <code>ListAdapter</code> for the specified target
         * JXList.
         * 
         * @param component  the target list.
         */
        public ComboBoxAdapter(JComboBox component) {
            super(component);
            comboBox = component;
        }

        /**
         * Typesafe accessor for the target component.
         * 
         * @return the target component as a {@link org.jdesktop.swingx.JXComboBox}
         */
        public JComboBox getComboBox() {
            return comboBox;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasFocus() {
            if (comboBox.isPopupVisible()) {
                JList list = getPopupListFor(comboBox);
                
                return list != null && list.isFocusOwner() && (row == list.getLeadSelectionIndex());
            }
            
            return comboBox.isFocusOwner();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getRowCount() {
            return comboBox.getModel().getSize();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object getValueAt(int row, int column) {
            return comboBox.getModel().getElementAt(row);
        }

        /**
         * {@inheritDoc}
         * This is implemented to query the table's StringValueRegistry for an appropriate
         * StringValue and use that for getting the string representation.
         */
        @Override
        public String getStringAt(int row, int column) {
            ListCellRenderer renderer = comboBox.getRenderer();
            
            if (renderer instanceof DelegatingRenderer) {
                renderer = ((DelegatingRenderer) renderer).getDelegateRenderer();
            }
            
            if (renderer instanceof StringValue) {
                return ((StringValue) renderer).getString(getValueAt(row, column));
            }
            
            return super.getStringAt(row, column);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Rectangle getCellBounds() {
            JList list = getPopupListFor(comboBox);
            
            if (list == null) {
                assert false;
                return new Rectangle(comboBox.getSize());
            }

            return list.getCellBounds(row, row);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return row == -1 && comboBox.isEditable();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEditable() {
            return isCellEditable(row, column);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSelected() {
            if (comboBox.isPopupVisible()) {
                JList list = getPopupListFor(comboBox);
                
                return list != null && row == list.getLeadSelectionIndex();
            }
            
            return comboBox.isFocusOwner();
        }
    }
    
    private ComboBoxAdapter dataAdapter;
    
    private DelegatingRenderer delegatingRenderer;
    
    private boolean useHighlightersForCurrentValue = true;
    
    private CompoundHighlighter compoundHighlighter;

    private ChangeListener highlighterChangeListener;

    private List<KeyEvent> pendingEvents = new ArrayList<KeyEvent>();

    private boolean isDispatching;

    /**
     * Creates a <code>JXComboBox</code> with a default data model. The default data model is an
     * empty list of objects. Use <code>addItem</code> to add items. By default the first item in
     * the data model becomes selected.
     * 
     * @see DefaultComboBoxModel
     */
    public JXComboBox() {
        super();
    }

    /**
     * Creates a <code>JXComboBox</code> that takes its items from an existing
     * <code>ComboBoxModel</code>. Since the <code>ComboBoxModel</code> is provided, a combo box
     * created using this constructor does not create a default combo box model and may impact how
     * the insert, remove and add methods behave.
     * 
     * @param model
     *            the <code>ComboBoxModel</code> that provides the displayed list of items
     * @see DefaultComboBoxModel
     */
    public JXComboBox(ComboBoxModel model) {
        super(model);
    }

    /**
     * Creates a <code>JXComboBox</code> that contains the elements in the specified array. By
     * default the first item in the array (and therefore the data model) becomes selected.
     * 
     * @param items
     *            an array of objects to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public JXComboBox(Object[] items) {
        super(items);
    }

    /**
     * Creates a <code>JXComboBox</code> that contains the elements in the specified Vector. By
     * default the first item in the vector (and therefore the data model) becomes selected.
     * 
     * @param items
     *            an array of vectors to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public JXComboBox(Vector<?> items) {
        super(items);
    }

    protected static JList getPopupListFor(JComboBox comboBox) {
        int count = comboBox.getUI().getAccessibleChildrenCount(comboBox);

        for (int i = 0; i < count; i++) {
            Accessible a = comboBox.getUI().getAccessibleChild(comboBox, i);
            
            if (a instanceof ComboPopup) {
                return ((ComboPopup) a).getList();
            }
        }

        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean processKeyBinding(KeyStroke ks, final KeyEvent e, int condition,
            boolean pressed) {
        boolean retValue = super.processKeyBinding(ks, e, condition, pressed);

        if (!retValue && editor != null) {
            if (isStartingCellEdit(e)) {
                pendingEvents.add(e);
            } else if (pendingEvents.size() == 2) {
                pendingEvents.add(e);
                isDispatching = true;

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            for (KeyEvent event : pendingEvents) {
                                editor.getEditorComponent().dispatchEvent(event);
                            }

                            pendingEvents.clear();
                        } finally {
                            isDispatching = false;
                        }
                    }
                });
            }
        }
        return retValue;
    }

    private boolean isStartingCellEdit(KeyEvent e) {
        if (isDispatching) {
            return false;
        }

        JTable table = (JTable) SwingUtilities.getAncestorOfClass(JTable.class, this);
        boolean isOwned = table != null
                && !Boolean.FALSE.equals(table.getClientProperty("JTable.autoStartsEdit"));

        return isOwned && e.getComponent() == table;
    }

    /**
     * @return the unconfigured ComponentAdapter.
     */
    protected ComponentAdapter getComponentAdapter() {
        if (dataAdapter == null) {
            dataAdapter = new ComboBoxAdapter(this);
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
     * @see #setRenderer(ListCellRenderer)
     * @see DelegatingRenderer
     */
    @Override
    public ListCellRenderer getRenderer() {
        // PENDING JW: something wrong here - why exactly can't we return super? 
        // not even if we force the initial setting in init?
//        return super.getCellRenderer();
        return getDelegatingRenderer();
    }

    /**
     * Returns the renderer installed by client code or the default if none has
     * been set.
     * 
     * @return the wrapped renderer.
     * @see #setCellRenderer(TreeCellRenderer)
     */
    public ListCellRenderer getWrappedRenderer() {
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
     */
    @Override
    public void setRenderer(ListCellRenderer renderer) {
        // PENDING: do something against recursive setting
        // == multiple delegation...
        ListCellRenderer oldValue = super.getRenderer();
        getDelegatingRenderer().setDelegateRenderer(renderer);
        super.setRenderer(delegatingRenderer);
        
        if (oldValue == delegatingRenderer) {
            firePropertyChange("renderer", null, delegatingRenderer);
        }
    }

    public boolean isUseHighlightersForCurrentValue() {
        return useHighlightersForCurrentValue;
    }
    
    public void setUseHighlightersForCurrentValue(boolean useHighlightersForCurrentValue) {
        boolean oldValue = isUseHighlightersForCurrentValue();
        this.useHighlightersForCurrentValue = useHighlightersForCurrentValue;
        firePropertyChange("useHighlightersForCurrentValue", oldValue,
                isUseHighlightersForCurrentValue());
    }
    
    /**
     * Returns the CompoundHighlighter assigned to the table, null if none. PENDING: open up for
     * subclasses again?.
     * 
     * @return the CompoundHighlighter assigned to the table.
     * @see #setCompoundHighlighter(CompoundHighlighter)
     */
    private CompoundHighlighter getCompoundHighlighter() {
        return compoundHighlighter;
    }

    /**
     * Assigns a CompoundHighlighter to the table, maybe null to remove all Highlighters.
     * <p>
     * 
     * The default value is <code>null</code>.
     * <p>
     * 
     * PENDING: open up for subclasses again?.
     * 
     * @param pipeline
     *            the CompoundHighlighter to use for renderer decoration.
     * @see #getCompoundHighlighter()
     * @see #addHighlighter(Highlighter)
     * @see #removeHighlighter(Highlighter)
     * 
     */
    private void setCompoundHighlighter(CompoundHighlighter pipeline) {
        CompoundHighlighter old = getCompoundHighlighter();
        if (old != null) {
            old.removeChangeListener(getHighlighterChangeListener());
        }
        compoundHighlighter = pipeline;
        if (compoundHighlighter != null) {
            compoundHighlighter.addChangeListener(getHighlighterChangeListener());
        }
        // PENDING: wrong event - the property is either "compoundHighlighter"
        // or "highlighters" with the old/new array as value
        firePropertyChange("highlighters", old, getCompoundHighlighter());
    }

    /**
     * Sets the <code>Highlighter</code>s to the column, replacing any old settings. None of the
     * given Highlighters must be null.
     * <p>
     * 
     * @param highlighters
     *            zero or more not null highlighters to use for renderer decoration.
     * 
     * @see #getHighlighters()
     * @see #addHighlighter(Highlighter)
     * @see #removeHighlighter(Highlighter)
     * 
     */
    public void setHighlighters(Highlighter... highlighters) {
        Contract.asNotNull(highlighters, "highlighters cannot be null or contain null");

        CompoundHighlighter pipeline = null;
        if (highlighters.length > 0) {
            pipeline = new CompoundHighlighter(highlighters);
        }

        setCompoundHighlighter(pipeline);
    }

    /**
     * Returns the <code>Highlighter</code>s used by this column. Maybe empty, but guarantees to be
     * never null.
     * 
     * @return the Highlighters used by this column, guaranteed to never null.
     * @see #setHighlighters(Highlighter[])
     */
    public Highlighter[] getHighlighters() {
        return getCompoundHighlighter() != null ? getCompoundHighlighter().getHighlighters()
                : CompoundHighlighter.EMPTY_HIGHLIGHTERS;
    }

    /**
     * Adds a Highlighter. Appends to the end of the list of used Highlighters.
     * <p>
     * 
     * @param highlighter
     *            the <code>Highlighter</code> to add.
     * @throws NullPointerException
     *             if <code>Highlighter</code> is null.
     * 
     * @see #removeHighlighter(Highlighter)
     * @see #setHighlighters(Highlighter[])
     */
    public void addHighlighter(Highlighter highlighter) {
        CompoundHighlighter pipeline = getCompoundHighlighter();
        if (pipeline == null) {
            setCompoundHighlighter(new CompoundHighlighter(highlighter));
        } else {
            pipeline.addHighlighter(highlighter);
        }
    }

    /**
     * Removes the given Highlighter.
     * <p>
     * 
     * Does nothing if the Highlighter is not contained.
     * 
     * @param highlighter
     *            the Highlighter to remove.
     * @see #addHighlighter(Highlighter)
     * @see #setHighlighters(Highlighter...)
     */
    public void removeHighlighter(Highlighter highlighter) {
        if ((getCompoundHighlighter() == null)) {
            return;
        }
        getCompoundHighlighter().removeHighlighter(highlighter);
    }

    /**
     * Returns the <code>ChangeListener</code> to use with highlighters. Lazily creates the
     * listener.
     * 
     * @return the ChangeListener for observing changes of highlighters, guaranteed to be
     *         <code>not-null</code>
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
     * A property change event is create for a state change.
     * 
     * @return the ChangeListener defining the reaction to changes of highlighters.
     */
    protected ChangeListener createHighlighterChangeListener() {
        return new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // need to fire change so JXComboBox can update
                firePropertyChange("highlighters", null, getHighlighters());
                repaint();
            }
        };
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to update renderer and highlighters.
     */
    @Override
    public void updateUI() {
        super.updateUI();
        
        ListCellRenderer renderer = getRenderer();
        
        if (renderer instanceof UIDependent) {
            ((UIDependent) renderer).updateUI();
        } else if (renderer instanceof Component) {
            SwingUtilities.updateComponentTreeUI((Component) renderer);
        }
        
        if (compoundHighlighter != null) {
            compoundHighlighter.updateUI();
        }
    }
}
