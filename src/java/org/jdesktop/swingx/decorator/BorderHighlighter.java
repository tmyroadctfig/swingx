/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.decorator;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * A Highlighter that applies a border the the renderer component.
 * 
 * The resulting border can be configured to 
 * - ignore the component's border, set this highlighter's border
 * - compound of this highlighter's border and component border, with
 *   this highlighter's border either inner or outer.
 *   
 * The default setting is compound outer.
 *   
 */
public class BorderHighlighter extends AbstractHighlighter {

    private Border paddingBorder;
    private boolean inner;
    private boolean compound;

   
    /**
     * 
     * Instantiates a BorderHighlighter no padding. The
     * Highlighter is applied unconditionally.
     * 
     */
    public BorderHighlighter() {
        this(null);
    }
    /**
     * 
     * Instantiates a BorderHighlighter with the given padding. The
     * Highlighter is applied unconditionally.
     * 
     * @param paddingBorder the border to apply as visual decoration.
     * 
     * @throws NullPointerException is the border is null.
     */
    public BorderHighlighter(Border paddingBorder) {
        this(paddingBorder, null);
    }

    /**
     * 
     * Instantiates a BorderHighlighter with the given padding, 
     * HighlightPredicate and default compound property. 
     * If the predicate is null, the highlighter
     * will be applied unconditionally.
     * 
     * @param paddingBorder the border to apply as visual decoration.
     * @param predicate the HighlightPredicate to use
     * 
     * @throws NullPointerException is the border is null.
     */
    public BorderHighlighter(Border paddingBorder, HighlightPredicate predicate) {
        this(paddingBorder, predicate, true);
    }

    /**
     * 
     * Instantiates a BorderHighlighter with the given padding, 
     * HighlightPredicate, compound property and default inner property. 
     * If the predicate is null, the highlighter
     * will be applied unconditionally.
     * 
     * @param paddingBorder the border to apply as visual decoration.
     * @param predicate the HighlightPredicate to use
     * @param compound the compound property.
     * @throws NullPointerException is the border is null.
     */
    public BorderHighlighter(Border paddingBorder, 
            HighlightPredicate predicate, boolean compound) {
        super(predicate);
        if (paddingBorder == null)
            throw new NullPointerException("border must not be null");
        this.paddingBorder = paddingBorder;
        this.compound = compound;
    }
    
    /**
     * 
     * Instantiates a BorderHighlighter with the given padding, 
     * HighlightPredicate and compound property. If the predicate is null, the highlighter
     * will be applied unconditionally.
     * 
     * @param paddingBorder the border to apply as visual decoration.
     * @param predicate the HighlightPredicate to use
     * @param compound the compound property
     * @param inner the inner property
     * @throws NullPointerException is the border is null.
     */
    public BorderHighlighter(Border paddingBorder, 
            HighlightPredicate predicate, boolean compound, boolean inner) {
        super(predicate);
        if (paddingBorder == null)
            throw new NullPointerException("border must not be null");
        this.paddingBorder = paddingBorder;
        this.compound = compound;
        this.inner = inner;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
        if (renderer instanceof JComponent) {
            ((JComponent) renderer).setBorder(compoundBorder(
                    ((JComponent) renderer).getBorder()));
        }
        return renderer;
    }

    /**
     * Sets the compound property. If true, the highlight border will be compounded
     * with the renderer's border, if any. Otherwise, the highlight border will
     * replace the renderer's border.<p>
     * 
     * The default value is true;
     * 
     * @param compound a boolean indicating whether the highlight border should be 
     *  compounded with the component's border.
     */
    public void setCompound(boolean compound) {
        if (isCompound() == compound) return;
        this.compound = compound;
        fireStateChanged();
    }
    
    /**
     * 
     * @return the compound property.
     * @see #setCompound(boolean)
     */
    public boolean isCompound() {
        return compound;
    }
    
    /**
     * Sets the inner property. If true/false and compounded is enabled
     * the highlight border will be the inner/outer border of the compound. 
     * 
     * The default value is false;
     * 
     * @param inner a boolean indicating whether the highlight border should be 
     *  compounded as inner or outer border.
     */
    public void setInner(boolean inner) {
        if (isInner() == inner) return;
        this.inner = inner;
        fireStateChanged();
    }
    
    /**
     * 
     * @return the compound property.
     * @see #setInner(boolean)
     */
    public boolean isInner() {
        return inner;
    }
    
    /**
     * PRE: paddingBorder != null.
     * @param border
     * @return
     */
    private Border compoundBorder(Border border) {
        if (compound) {
            if (border != null) {
                if (inner) {
                    return BorderFactory.createCompoundBorder(border,
                            paddingBorder);
                }
                return BorderFactory.createCompoundBorder(paddingBorder,
                        border);
            }
        }
        return paddingBorder;
    }

}

