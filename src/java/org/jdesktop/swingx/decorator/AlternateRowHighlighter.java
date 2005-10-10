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

/**
 * AlternateRowHighlighter prepares a cell renderer to use different background
 * colors for alternating rows in a data view.
 *
 * @author Ramesh Gupta
 */
public class AlternateRowHighlighter extends Highlighter {
    private final static Color  defaultOddRowColor = Color.white;
    private final static Color  defaultEvenRowColor = new Color(0xF0, 0xF0, 0xE0);

    public final static Highlighter beige =
        new AlternateRowHighlighter(Color.white, new Color(245, 245, 220), null);

    public final static Highlighter linePrinter =
        new AlternateRowHighlighter(Color.white, new Color(0xCC, 0xCC, 0xFF), null);

    public final static Highlighter classicLinePrinter =
        new AlternateRowHighlighter(Color.white, new Color(0xCC, 0xFF, 0xCC), null);

    public final static Highlighter floralWhite =
        new AlternateRowHighlighter(Color.white, new Color(255, 250, 240), null);

    public final static Highlighter quickSilver =
        new AlternateRowHighlighter(Color.white, defaultEvenRowColor, null);

    private Color oddRowBackground = defaultOddRowColor;
    private Color evenRowBackground = defaultEvenRowColor;

    /**
     * Constructs a default <code>AlternateRowHighlighter</code> that prepares a
     * cell renderer to paint a white background for odd rows and a silver
     * <code>(0xF0, 0xF0, 0xE0)</code> background for even rows.
     */
    public AlternateRowHighlighter() {
    }

    /**
     * Constructs an <code>AlternateRowHighlighter</code> that prepares a
     * cell renderer to paint the specified background colors for odd and even
     * and even rows. A foreground color may also be specified. If null is
     * specified for the foreground color, the foreground color for the renderer
     * is unchanged. Otherwise, it is set to the specified foreground color for
     * both odd and even rows.
     *
     * @param oddRowBackground
     * @param evenRowBackground
     * @param foreground
     */
    public AlternateRowHighlighter(Color oddRowBackground,
                                   Color evenRowBackground, Color foreground) {
        super(oddRowBackground, foreground); // same background for odd and even
        this.oddRowBackground = oddRowBackground;
        this.evenRowBackground = evenRowBackground;
    }

    /**
     * Returns the background color for odd rows, or null if the background color
     * of the cell renderer should be left unchanged for odd rows.
     *
     * @return the background color for odd rows, or null if the background color
     * of the cell renderer should be left unchanged for odd rows
     */
    public Color getOddRowBackground() {
        return oddRowBackground;
    }

    /**
     * Sets the background color for odd rows to the specified color. If null is
     * specified, the background color for odd rows is left unchanged in the
     * renderer

     *
     * @param color the background color for odd rows, or null if the background
     * color of the cell renderer should be left unchanged for odd rows
     */
    public void setOddRowBackground(Color color) {
        oddRowBackground = color;
        fireStateChanged();
    }

    /**
     * Returns the background color for even rows, or null if the background color
     * of the cell renderer should be left unchanged for even rows.
     *
     * @return the background color for even rows, or null if the background color
     * of the cell renderer should be left unchanged for even rows
     */
    public Color getEvenRowBackground() {
        return evenRowBackground;
    }

    /**
     * Sets the background color for even rows to the specified color. If null is
     * specified, the background color for even rows is left unchanged in the
     * renderer.
     *
     * @param color the background color for even rows, or null if the background
     * color of the cell renderer should be left unchanged for even rows
     */
    public void setEvenRowBackground(Color color) {
        evenRowBackground = color;
        fireStateChanged();
    }

    /**
     * Computes the background color for the current rendering context for the
     * specified adapter. It first chooses the raw background color for the
     * renderer depending on whether the row being rendered is odd or even. If
     * the chosen background color is not null
     * calls {@link Highlighter#computeSelectedBackground(java.awt.Color) computeSelectedBackground}
     * passing in the chosen raw background color as the seed color, but only if
     * the row being rendered is selected.
     *
     * @param renderer the cell renderer component
     * @param adapter
     * @return the computed background color
     */
    protected Color computeBackground(Component renderer,
                                      ComponentAdapter adapter) {
        // row is zero-based; so even is actually odd!
        Color color = (adapter.row % 2) == 0 ?
            oddRowBackground : evenRowBackground;

        if ((color != null) && adapter.isSelected()) {
            color = computeSelectedBackground(color);
        }

        return color;
    }
}
