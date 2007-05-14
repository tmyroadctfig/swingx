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

import javax.swing.event.ChangeListener;


/**
 * The legacy Highlighter. Note JW: this will be removed "some time" 
 * Highlighter overhaul is done. To keep it for now should ease the
 * switching over in legacy code, as all "old" highlighters
 * are direct or indirect descendents. 
 * 
 * <p><code>LegacyHighlighter</code> is a lightweight mechanism to modify the behavior
 * and attributes of cell renderers such as {@link javax.swing.ListCellRenderer},
 * {@link javax.swing.table.TableCellRenderer}, and
 * {@link javax.swing.tree.TreeCellRenderer} in a simple layered fashion.
 * While cell renderers are split along component lines, highlighters provide a
 * <em>common interface</em> for decorating cell renderers.
 * <code>LegacyHighlighter</code> achieves this by vectoring access to all component-specific
 * state and functionality through a {@link ComponentAdapter} object.</p>
 *
 * <p>The primary purpose of <code>LegacyHighlighter</code> is to decorate a cell
 * renderer in <em>controlled</em> ways, such as by applying a different color
 * or font to it. For example, {@link AlternateRowHighlighter} highlights cell
 * renderers with alternating background colors. In data visualization components
 * that support multiple columns with potentially different types of data, this
 * highlighter imparts the same background color consistently across <em>all</em>
 * columns of the {@link ComponentAdapter#target target} component
 * regardless of the actual cell renderer registered for any specific column.
 * Thus, the <code>LegacyHighlighter</code> mechanism is orthogonal to the cell
 * rendering mechanism.</p>
 *
 * <p>To use <code>LegacyHighlighter</code> you must first set up a
 * {@link CompoundHighlighter} using an array of <code>LegacyHighlighter</code> objects,
 * and then call setHighlighters() on a data visualization component, passing in
 * the highligher pipeline. If the array of highlighters is not null and is not
 * empty, the highlighters are applied to the selected renderer for each cell in
 * the order they appear in the array.
 * When it is time to render a cell, the cell renderer is primed as usual, after
 * which, the {@link LegacyHighlighter#highlight highlight} method of the first
 * highlighter in the {@link CompoundHighlighter} is invoked. The prepared
 * renderer, and a suitable {@link ComponentAdapter} object is passed to the
 * <code>highlight</code> method. The highlighter is expected to modify the
 * renderer in controlled ways, and return the modified renderer (or a substitute)
 * that is passed to the next highlighter, if any, in the pipeline. The renderer
 * returned by the <code>highlight</code> method of the last highlighter in the
 * pipeline is ultimately used to render the cell.</p>
 *
 * <p>The <code>LegacyHighlighter</code> mechanism enables multiple degrees of
 * freedom. In addition to specifying the actual cell renderer class, now you
 * can also specify the number, order, and class of highlighter objects. Using
 * highlighters is really simple, as shown by the following example:</p>
 *
 * <pre>
  LegacyHighlighter[]   highlighters = new LegacyHighlighter[] {
      new <b>AlternateRowHighlighter</b>(Color.white,
                                         new Color(0xF0, 0xF0, 0xE0), null),
      new <b>PatternHighlighter</b>(null, Color.red, "^s", 0, 0)
  };

  CompoundHighlighter compoundHighlighter = new CompoundHighlighter(highlighters);
  JXTable table = new JXTable();
  table.setHighlighters(compoundHighlighter);
 * </pre>
 *
 * <p>The above example allocates an array of <code>LegacyHighlighter</code> and populates
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
public class LegacyHighlighter extends AbstractHighlighter {
    /**
     * Predefined <code>LegacyHighlighter</code> that highlights the background of
     * each cell with a pastel green "ledger" background color, and is most
     * effective when the {@link ComponentAdapter#target} component has
     * horizontal gridlines in <code>Color.cyan.darker()</code> color.
     * 
     * @deprecated set the component's background color instead!
     */
    public final static LegacyHighlighter ledgerBackground =
                new LegacyHighlighter(new Color(0xF5, 0xFF, 0xF5), null, true);

    /**
     * Predefined <code>LegacyHighlighter</code> that decorates the background of
     * each cell with a pastel yellow "notepad" background color, and is most
     * effective when the {@link ComponentAdapter#target} component has
     * horizontal gridlines in <code>Color.cyan.darker()</code> color.
     * 
     * @deprecated set the component's background color instead!
     */
    public final static LegacyHighlighter notePadBackground =
                new LegacyHighlighter(new Color(0xFF, 0xFF, 0xCC), null, true);

    private Color background = null;
    private Color foreground = null;
    private Color selectedBackground = null;
    private Color selectedForeground = null;

    /** flag to indicate whether the Highlighter is immutable in every respect. */
    protected final boolean immutable;

    /**
     * Default constructor for mutable LegacyHighlighter.
     * Initializes background, foreground, selectedBackground, and
     * selectedForeground to null.
     */
    public LegacyHighlighter() {
        this(null, null);
    }

    /**
     * Constructs a mutable <code>LegacyHighlighter</code> with the specified
     * background and foreground colors, selectedBackground and 
     * selectedForeground to null.
     *
     * @param cellBackground background color for the renderer, or null,
     *          to compute a suitable background
     * @param cellForeground foreground color for the renderer, or null,
     *          to compute a suitable foreground
     */
    public LegacyHighlighter(Color cellBackground, Color cellForeground) {
        this(cellBackground, cellForeground, false);
    }

    public LegacyHighlighter(Color cellBackground, Color cellForeground, boolean immutable) {
        this(cellBackground, cellForeground, null, null, immutable);
    }
    
    /**
     * Constructs a mutable <code>LegacyHighlighter</code> with the specified
     * background and foreground colors.
     *
     * @param cellBackground background color for the renderer, or null,
     *          to compute a suitable background
     * @param cellForeground foreground color for the renderer, or null,
     *          to compute a suitable foreground
     */
    public LegacyHighlighter(Color cellBackground, Color cellForeground, 
            Color selectedBackground, Color selectedForeground) {
        this(cellBackground, cellForeground, selectedBackground, selectedForeground, false);
    }

    /**
     * Constructs a <code>LegacyHighlighter</code> with the specified
     * background and foreground colors with mutability depending on
     * given flag.
     *
     * @param cellBackground background color for the renderer, or null,
     *          to compute a suitable background
     * @param cellForeground foreground color for the renderer, or null,
     *          to compute a suitable foreground
     */
    public LegacyHighlighter(Color cellBackground, Color cellForeground, 
            Color selectedBackground, Color selectedForeground, boolean immutable) {
        super();
        this.immutable = immutable;
        this.background = cellBackground; 
        this.foreground = cellForeground; 
        this.selectedBackground = selectedBackground;
        this.selectedForeground = selectedForeground;
    }
    
// --------------------- implement Highlighter
    
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
     * @return unselected background color
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
     * @return selected background color
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
     * @return unselected foreground color
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
     * @return selected foreground color
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

//---------------------- state
    
    /**
     * {@inheritDoc}<p>
     * 
     * Overridden to not add listener if immutable.
     */
    @Override
    public void addChangeListener(ChangeListener l) {
        if (isImmutable()) return;
        super.addChangeListener(l);
    }
 
    
    /**
     * Returns the background color of this <code>LegacyHighlighter</code>.
     *
     * @return the background color of this <code>LegacyHighlighter</code>,
     *          or null, if no background color has been set
     */
    public Color getBackground() {
        return background;
    }

    /**
     * Sets the background color of this <code>LegacyHighlighter</code> and 
     * notifies registered ChangeListeners if this
     * is mutable. Does nothing if immutable.
     *  
     * @param color the background color of this <code>LegacyHighlighter</code>,
     *          or null, to clear any existing background color
     */
    public void setBackground(Color color) {
        if (isImmutable()) return;
        background = color;
        fireStateChanged();
    }

    /**
     * Returns the foreground color of this <code>LegacyHighlighter</code>.
     *
     * @return the foreground color of this <code>LegacyHighlighter</code>,
     *          or null, if no foreground color has been set
     */
    public Color getForeground() {
        return foreground;
    }

    /**
     * Sets the foreground color of this <code>LegacyHighlighter</code> and notifies
     * registered ChangeListeners if this is mutable. Does nothing if 
     * immutable.
     *
     * @param color the foreground color of this <code>LegacyHighlighter</code>,
     *          or null, to clear any existing foreground color
     */
    public void setForeground(Color color) {
        if (isImmutable()) return;
        foreground = color;
        fireStateChanged();
    }

    /**
     * Returns the selected background color of this <code>LegacyHighlighter</code>.
     *
     * @return the selected background color of this <code>LegacyHighlighter</code>,
     *          or null, if no selected background color has been set
     */
    public Color getSelectedBackground() {
        return selectedBackground;
    }

    /**
     * Sets the selected background color of this <code>LegacyHighlighter</code>
     * and notifies registered ChangeListeners if this is mutable. Does nothing
     * if immutable.
     *
     * @param color the selected background color of this <code>LegacyHighlighter</code>,
     *          or null, to clear any existing selected background color
     */
    public void setSelectedBackground(Color color) {
        if (isImmutable()) return;
        selectedBackground = color;
        fireStateChanged();
    }

    /**
     * Returns the selected foreground color of this <code>LegacyHighlighter</code>.
     *
     * @return the selected foreground color of this <code>LegacyHighlighter</code>,
     *          or null, if no selected foreground color has been set
     */
    public Color getSelectedForeground() {
        return selectedForeground;
    }

    /**
     * Sets the selected foreground color of this <code>LegacyHighlighter</code> and
     * notifies registered ChangeListeners if this is mutable. Does nothing if
     * immutable.
     *
     * @param color the selected foreground color of this <code>LegacyHighlighter</code>,
     *          or null, to clear any existing selected foreground color
     */
    public void setSelectedForeground(Color color) {
        if (isImmutable()) return;
        selectedForeground = color;
        fireStateChanged();
    }

/**
     * Returns immutable flag. If true, the Highlighter must not
     * change internal state in any way. In this case,
     * no listeners are added and no change events fired.
     * @return true if none of the setXX methods have any effect
     */
    public final boolean isImmutable() {
        return immutable;
    }

    //---------------------- 
    /**
     * Interface to implement if Highlighter state depends on the 
     * LookAndFeel. 
     * 
     * PENDING: this is more general than highlighters - move to
     * standalone?
     */
    public interface UIHighlighter {
        
        void updateUI();
    }

}
