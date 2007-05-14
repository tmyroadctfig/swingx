/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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

import java.awt.Color;

import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.RowGroupHighlightPredicate;

/**
 * A Factory which creates common Highlighters. 
 * 
 * @author Jeanette Winzenburg
 */
public final class HighlighterFactory {

    /**
     * Creates and returns a Highlighter which highlights every second row
     * with the given color as background. The rows between are not 
     * that is typically, they will show the container's background.
     * 
     * @param stripeBackground the background color for the striping.
     * @return a Highlighter striping every second row background. 
     */
    public static Highlighter createSimpleStriping(Color stripeBackground) {
        ColorHighlighter hl = new ColorHighlighter(stripeBackground, null, HighlightPredicate.ODD);
        return hl;
    }
    
    /**
     * Creates and returns a Highlighter which highlights every second row
     * with the given color as background. The rows between are not 
     * that is typically, they will show the container's background.
     * 
     * @param stripeBackground the background color for the striping.
     * @return a Highlighter striping every second row background. 
     */
    public static Highlighter createSimpleStriping(Color stripeBackground, int linesPerStripe) {
        HighlightPredicate predicate = new RowGroupHighlightPredicate(linesPerStripe);
        ColorHighlighter hl = new ColorHighlighter(stripeBackground, null, predicate);
        return hl;
    }
    /**
     * Creates and returns a Highlighter which highlights with
     * alternating background, starting with the base.
     * 
     * @param baseBackground the background color for the even rows.
     * @param alternateBackground background color for odd rows.
     * @return a Highlighter striping every second row background. 
     */
    public static Highlighter createAlternateStriping(Color baseBackground, Color alternateBackground) {
        ColorHighlighter base = new ColorHighlighter(baseBackground, null, HighlightPredicate.EVEN);
        ColorHighlighter alternate = new ColorHighlighter(alternateBackground, null, HighlightPredicate.ODD);
        
        return new CompoundHighlighter(base, alternate);
    }

    /**
     * Creates and returns a Highlighter which highlights with
     * alternating background, starting with the base.
     * 
     * @param baseBackground the background color for the even rows.
     * @param alternateBackground background color for odd rows.
     * @return a Highlighter striping every second row background. 
     */
    public static Highlighter createAlternateStriping(Color baseBackground, Color alternateBackground, int linesPerStripe) {
        HighlightPredicate predicate = new RowGroupHighlightPredicate(linesPerStripe);
        ColorHighlighter base = new ColorHighlighter(baseBackground, null, new NotHighlightPredicate(predicate));
        ColorHighlighter alternate = new ColorHighlighter(alternateBackground, null, predicate);
        
        return new CompoundHighlighter(base, alternate);
    }
}
