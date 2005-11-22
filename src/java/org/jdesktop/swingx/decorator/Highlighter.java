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

package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * <p><code>Highlighter</code> is a lightweight mechanism to modify the behavior
 * and attributes of cell renderers such as {@link javax.swing.ListCellRenderer},
 * {@link javax.swing.table.TableCellRenderer}, and
 * {@link javax.swing.tree.TreeCellRenderer} in a simple layered fashion.
 * While cell renderers are split along component lines, highlighters provide a
 * <em>common interface</em> for decorating cell renderers.
 * <code>Highlighter</code> achieves this by vectoring access to all component-specific
 * state and functionality through a {@link ComponentAdapter} object.</p>
 *
 * <p>The primary purpose of <code>Highlighter</code> is to decorate a cell
 * renderer in <em>controlled</em> ways, such as by applying a different color
 * or font to it. For example, {@link AlternateRowHighlighter} highlights cell
 * renderers with alternating background colors. In data visualization components
 * that support multiple columns with potentially different types of data, this
 * highlighter imparts the same background color consistently across <em>all</em>
 * columns of the {@link ComponentAdapter#target target} component
 * regardless of the actual cell renderer registered for any specific column.
 * Thus, the <code>Highlighter</code> mechanism is orthogonal to the cell
 * rendering mechanism.</p>
 *
 * <p>To use <code>Highlighter</code> you must first set up a
 * {@link HighlighterPipeline} using an array of <code>Highlighter</code> objects,
 * and then call setHighlighters() on a data visualization component, passing in
 * the highligher pipeline. If the array of highlighters is not null and is not
 * empty, the highlighters are applied to the selected renderer for each cell in
 * the order they appear in the array.
 * When it is time to render a cell, the cell renderer is primed as usual, after
 * which, the {@link Highlighter#highlight highlight} method of the first
 * highlighter in the {@link HighlighterPipeline} is invoked. The prepared
 * renderer, and a suitable {@link ComponentAdapter} object is passed to the
 * <code>highlight</code> method. The highlighter is expected to modify the
 * renderer in controlled ways, and return the modified renderer (or a substitute)
 * that is passed to the next highlighter, if any, in the pipeline. The renderer
 * returned by the <code>highlight</code> method of the last highlighter in the
 * pipeline is ultimately used to render the cell.</p>
 *
 * <p>The <code>Highlighter</code> mechanism enables multiple degrees of
 * freedom. In addition to specifying the actual cell renderer class, now you
 * can also specify the number, order, and class of highlighter objects. Using
 * highlighters is really simple, as shown by the following example:</p>
 *
 * <pre>
  Highlighter[]   highlighters = new Highlighter[] {
      new <b>AlternateRowHighlighter</b>(Color.white,
                                         new Color(0xF0, 0xF0, 0xE0), null),
      new <b>PatternHighlighter</b>(null, Color.red, "^s", 0, 0)
  };

  HighlighterPipeline highlighterPipeline = new HighlighterPipeline(highlighters);
  JXTable table = new JXTable();
  table.setHighlighters(highlighterPipeline);
 * </pre>
 *
 * <p>The above example allocates an array of <code>Highlighter</code> and populates
 * it with a new {@link AlternateRowHighlighter} and {@link PatternHighlighter}.
 * The first one in this example highlights all cells in odd rows with a white
 * background, and all cells in even rows with a silver background, but it does
 * not specify a foreground color explicitly. The second highlighter does not
 * specify a background color explicitly, but sets the foreground color to red
 * <em>if certain conditions are met</em> (see {@link PatternHighlighter} for
 * more details). In this example, if the cells in the first column of any
 * row start with the letter 's', then all cells in that row are highlighted with
 * a red foreground. Also, as mentioned earlier, the highlighters are applied in
 * the order they appear in the list.</p>
 *
 * <p> Highlighters are mutable by default, that is all there properties can be
 * changed dynamically. If so they fire changeEvents to registered ChangeListeners.
 * They can be marked as immutable at instantiation time - if so, trying to mutate
 * all properties will not have any effect, ChangeListeners are not registered and
 * no events are fired. </p>
 * 
 * <p> This base class has properties background/foreground and corresponding
 * selectionBackground/selectionForeground. It will apply those colors "absolutely", 
 * that is without actually computing any derived color. It's up to subclasses to 
 * implement color computation, if desired. </p>
 *
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 * 
 * @see ComponentAdapter
 * @see javax.swing.ListCellRenderer
 * @see javax.swing.table.TableCellRenderer
 * @see javax.swing.tree.TreeCellRenderer
 */
public class Highlighter {
    /**
     * Only one <code>ChangeEvent</code> is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;

    /** The listeners waiting for model changes. */
    protected EventListenerList listenerList = new EventListenerList();
    
    /** flag to indicate whether the Highlighter is mutable in every respect. */
    protected final boolean immutable;
    
    /**
     * Predefined <code>Highlighter</code> that highlights the background of
     * each cell with a pastel green "ledger" background color, and is most
     * effective when the {@link ComponentAdapter#target} component has
     * horizontal gridlines in <code>Color.cyan.darker()</code> color.
     * 
     * @deprecated set the component's background color instead!
     */
    public final static Highlighter ledgerBackground =
                new Highlighter(new Color(0xF5, 0xFF, 0xF5), null, true);

    /**
     * Predefined <code>Highlighter</code> that decorates the background of
     * each cell with a pastel yellow "notepad" background color, and is most
     * effective when the {@link ComponentAdapter#target} component has
     * horizontal gridlines in <code>Color.cyan.darker()</code> color.
     * 
     * @deprecated set the component's background color instead!
     */
    public final static Highlighter notePadBackground =
                new Highlighter(new Color(0xFF, 0xFF, 0xCC), null, true);

    private Color background = null;
    private Color foreground = null;
    private Color selectedBackground = null;
    private Color selectedForeground = null;

    /**
     * Default constructor for mutable Highlighter.
     * Initializes background, foreground, selectedBackground, and
     * selectedForeground to null.
     */
    public Highlighter() {
        this(null, null);
    }

    /**
     * Constructs a mutable <code>Highlighter</code> with the specified
     * background and foreground colors, selectedBackground and 
     * selectedForeground to null.
     *
     * @param cellBackground background color for the renderer, or null,
     *          to compute a suitable background
     * @param cellForeground foreground color for the renderer, or null,
     *          to compute a suitable foreground
     */
    public Highlighter(Color cellBackground, Color cellForeground) {
        this(cellBackground, cellForeground, false);
    }

    public Highlighter(Color cellBackground, Color cellForeground, boolean immutable) {
        this(cellBackground, cellForeground, null, null, immutable);
    }
    
    /**
     * Constructs a mutable <code>Highlighter</code> with the specified
     * background and foreground colors.
     *
     * @param cellBackground background color for the renderer, or null,
     *          to compute a suitable background
     * @param cellForeground foreground color for the renderer, or null,
     *          to compute a suitable foreground
     */
    public Highlighter(Color cellBackground, Color cellForeground, 
            Color selectedBackground, Color selectedForeground) {
        this(cellBackground, cellForeground, selectedBackground, selectedForeground, false);
    }

    /**
     * Constructs a <code>Highlighter</code> with the specified
     * background and foreground colors with mutability depending on
     * given flag.
     *
     * @param cellBackground background color for the renderer, or null,
     *          to compute a suitable background
     * @param cellForeground foreground color for the renderer, or null,
     *          to compute a suitable foreground
     */
    public Highlighter(Color cellBackground, Color cellForeground, 
            Color selectedBackground, Color selectedForeground, boolean immutable) {
        this.immutable = immutable;
        this.background = cellBackground; 
        this.foreground = cellForeground; 
        this.selectedBackground = selectedBackground;
        this.selectedForeground = selectedForeground;
    }

    /**
     * Decorates the specified cell renderer component for the given component
     * data adapter using highlighters that were previously set for the component.
     * This method unconditionally invokes {@link #doHighlight doHighlight} with
     * the same arguments as were passed in.
     *
     * @param renderer the cell renderer component that is to be decorated
     * @param adapter the {@link ComponentAdapter} for this decorate operation
     * @return the decorated cell renderer component
     */
    public Component highlight(Component renderer, ComponentAdapter adapter) {
        return doHighlight(renderer, adapter);
    }

    /**
     * This is the bottleneck decorate method that all highlighters must invoke
     * to decorate the cell renderer. This method invokes {@link #applyBackground
     * applyBackground}, {@link #applyForeground applyForeground},
     * to decorate the corresponding
     * attributes of the specified component within the given adapter. <p>
     *
     * Subclasses which want to decorate additional properties must override
     * this and additionally call custom applyXX methods.
     * 
     * @param renderer the cell renderer component that is to be decorated
     * @param adapter the {@link ComponentAdapter} for this decorate operation
     * @return the decorated cell renderer component
     */
    protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
        applyBackground(renderer, adapter);
        applyForeground(renderer, adapter);
        return renderer;
    }

    /**
     * Applies a suitable background for the renderer component within the
     * specified adapter. <p>
     * 
     * This implementation calls {@link #computeBackground computeBackground}
     * and applies the computed color to the component if the returned value is
     * != null. Otherwise it does nothing.
     *
     * @param renderer the cell renderer component that is to be decorated
     * @param adapter the {@link ComponentAdapter} for this decorate operation
     */
    protected void applyBackground(Component renderer, ComponentAdapter adapter) {
        Color color = computeBackground(renderer, adapter);
        if (color != null) {
            renderer.setBackground(color);
        }
    }

    /**
     * Applies a suitable foreground for the renderer component within the
     * specified adapter. <p>
     * 
     * This implementation calls {@link #computeForeground computeForeground}
     * and applies the computed color to the component if the returned value
     * is != null. Otherwise it does nothing.
     *
     * @param renderer the cell renderer component that is to be decorated
     * @param adapter the {@link ComponentAdapter} for this decorate operation
     */
    protected void applyForeground(Component renderer, ComponentAdapter adapter) {
        Color color = computeForeground(renderer, adapter);
        if (color != null) {
            renderer.setForeground(color);
        }
    }

    /**
     * <p>Computes a suitable background for the renderer component within the
     * specified adapter and returns the computed color. 
     * 
     * <p> In this implementation the returned color depends
     * on {@link ComponentAdapter#isSelected isSelected}: it will
     * return computSelected/-UnselectedBackground, respectively.</p> 
     *
     * @param renderer the cell renderer component that is to be decorated
     * @param adapter the {@link ComponentAdapter} for this decorate operation
     * @return a suitable background color for the specified component and adapter
     */
    protected Color computeBackground(Component renderer, ComponentAdapter adapter) {
        return adapter.isSelected() ? computeSelectedBackground(renderer, adapter) :
            computeUnselectedBackground(renderer, adapter);
    }



    /**
     * <p>Computes a suitable unselected background for the renderer component within the
     * specified adapter and returns the computed color. 
     * 
     * This implementation returns getBackground().
     * 
     * @param renderer
     * @param adapter
     * @return
     */
    protected Color computeUnselectedBackground(Component renderer, ComponentAdapter adapter) {
        return getBackground();
    }

    /**
     * <p>Computes a suitable selected background for the renderer component within the
     * specified adapter and returns the computed color. 
     * 
     * This implementation returns getSelectedBackground().
     * 
     * @param renderer
     * @param adapter
     * @return
     */
    protected Color computeSelectedBackground(Component renderer, ComponentAdapter adapter) {
        return getSelectedBackground();
    }

    /**
     * <p>Computes a suitable foreground for the renderer component within the
     * specified adapter and returns the computed color. 
     *  In this implementation the returned color depends
     * on {@link ComponentAdapter#isSelected isSelected}: it will
     * return computSelected/-UnselectedForeground, respectively.</p> 
     *</p>
     *
     * @param renderer the cell renderer component that is to be decorated
     * @param adapter the {@link ComponentAdapter} for this decorate operation
     * @return a suitable foreground color for the specified component and adapter
     */
    protected Color computeForeground(Component renderer, ComponentAdapter adapter) {
        return adapter.isSelected() ? computeSelectedForeground(renderer, adapter) :
            computeUnselectedForeground(renderer, adapter);
    }

    /**
     * <p>Computes a suitable unselected foreground for the renderer component within the
     * specified adapter and returns the computed color. 
     * 
     * This implementation returns getForeground().
     * 
     * @param renderer
     * @param adapter
     * @return
     */
    protected Color computeUnselectedForeground(Component renderer, ComponentAdapter adapter) {
        return getForeground();
    }

    /**
     * <p>Computes a suitable selected foreground for the renderer component within the
     * specified adapter and returns the computed color. 
     * 
     * This implementation returns getSelectedForeground().
     * 
     * @param renderer
     * @param adapter
     * @return
     */
    protected Color computeSelectedForeground(Component renderer, ComponentAdapter adapter) {
        return getSelectedForeground();
    }

    /**
     * Computes the selected background color. 
     * 
     * This implementation simply returns the selectedBackground property.
     * 
     * @deprecated this is no longer used by this implementation 
     * @param seed initial background color; must cope with null!
     * @return the background color for a selected cell
     */
    protected Color computeSelectedBackground(Color seed) {
        // JW: first go on fixing #178-swingx - return absolute color
        // this moves the responsibility of computation to subclasses.
        return selectedBackground;
    }

    /**
     * Computes the selected foreground color. 
     * 
     * This implementation simply returns the selectedBackground property.
     *
     * @deprecated this method is longer called by this implementation
     *          
     * @param seed initial foreground color; must cope with null!
     * @return the foreground color for a selected cell
     */
    protected Color computeSelectedForeground(Color seed) {
        // JW: first go on fixing #178-swingx - return absolute color
        // this moves the responsibility of computation to subclasses.
        return selectedForeground; 
    }

    /**
     * Returns immutable flag: if true, none of the setXX methods have
     * any effects, there are no listeners added and no change events fired.
     * @return
     */
    public boolean isImmutable() {
        return immutable;
    }
    /**
     * Returns the background color of this <code>Highlighter</code>.
     *
     * @return the background color of this <code>Highlighter</code>,
     *          or null, if no background color has been set
     */
    public Color getBackground() {
        return background;
    }

    /**
     * Sets the background color of this <code>Highlighter</code> and 
     * notifies registered ChangeListeners if this
     * is mutable. Does nothing if immutable.
     *  
     * @param color the background color of this <code>Highlighter</code>,
     *          or null, to clear any existing background color
     */
    public void setBackground(Color color) {
        if (isImmutable()) return;
        background = color;
        fireStateChanged();
    }

    /**
     * Returns the foreground color of this <code>Highlighter</code>.
     *
     * @return the foreground color of this <code>Highlighter</code>,
     *          or null, if no foreground color has been set
     */
    public Color getForeground() {
        return foreground;
    }

    /**
     * Sets the foreground color of this <code>Highlighter</code> and notifies
     * registered ChangeListeners if this is mutable. Does nothing if 
     * immutable.
     *
     * @param color the foreground color of this <code>Highlighter</code>,
     *          or null, to clear any existing foreground color
     */
    public void setForeground(Color color) {
        if (isImmutable()) return;
        foreground = color;
        fireStateChanged();
    }

    /**
     * Returns the selected background color of this <code>Highlighter</code>.
     *
     * @return the selected background color of this <code>Highlighter</code>,
     *          or null, if no selected background color has been set
     */
    public Color getSelectedBackground() {
        return selectedBackground;
    }

    /**
     * Sets the selected background color of this <code>Highlighter</code>
     * and notifies registered ChangeListeners if this is mutable. Does nothing
     * if immutable.
     *
     * @param color the selected background color of this <code>Highlighter</code>,
     *          or null, to clear any existing selected background color
     */
    public void setSelectedBackground(Color color) {
        if (isImmutable()) return;
        selectedBackground = color;
        fireStateChanged();
    }

    /**
     * Returns the selected foreground color of this <code>Highlighter</code>.
     *
     * @return the selected foreground color of this <code>Highlighter</code>,
     *          or null, if no selected foreground color has been set
     */
    public Color getSelectedForeground() {
        return selectedForeground;
    }

    /**
     * Sets the selected foreground color of this <code>Highlighter</code> and
     * notifies registered ChangeListeners if this is mutable. Does nothing if
     * immutable.
     *
     * @param color the selected foreground color of this <code>Highlighter</code>,
     *          or null, to clear any existing selected foreground color
     */
    public void setSelectedForeground(Color color) {
        if (isImmutable()) return;
        selectedForeground = color;
        fireStateChanged();
    }

    /**
     * Adds a <code>ChangeListener</code> if this is mutable. ChangeListeners are
     * notified after changes of any attribute. Does nothing if immutable. 
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        if (isImmutable()) return;
        listenerList.add(ChangeListener.class, l);
    }
    

    /**
     * Removes a <code>ChangeListener</code> if this is mutable. 
     * Does nothis if immutable.
     *
     * @param l the <code>ChangeListener</code> to remove
     * @see #addChangeListener
     */
    public void removeChangeListener(ChangeListener l) {
        if (isImmutable()) return;
        listenerList.remove(ChangeListener.class, l);
    }


    /**
     * Returns an array of all the change listeners
     * registered on this <code>Highlighter</code>.
     *
     * @return all of this model's <code>ChangeListener</code>s 
     *         or an empty
     *         array if no change listeners are currently registered
     *
     * @see #addChangeListener
     * @see #removeChangeListener
     *
     * @since 1.4
     */
    public ChangeListener[] getChangeListeners() {
        return (ChangeListener[])listenerList.getListeners(
                ChangeListener.class);
    }


    /** 
     * Runs each <code>ChangeListener</code>'s <code>stateChanged</code> method.
     * 
     */
    protected void fireStateChanged() {
        if (isImmutable()) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }          
        }
    }   

    
    public interface UIHighlighter {
        
        void updateUI();
    }
}
