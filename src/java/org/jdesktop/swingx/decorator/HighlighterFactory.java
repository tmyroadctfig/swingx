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
import java.util.HashMap;

import javax.swing.UIManager;

import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.RowGroupHighlightPredicate;
import org.jdesktop.swingx.decorator.LegacyHighlighter.UIHighlighter;

/**
 * A Factory which creates common Highlighters. 
 * 
 * @author Jeanette Winzenburg
 */
public final class HighlighterFactory {

    /**
     * Creates and returns a Highlighter which highlights every second row
     * with the color depending on LF. The rows between are not 
     * that is typically, they will show the container's background.
     * 
     * @return a Highlighter striping every second row background. 
     */
    public static Highlighter createSimpleUIStriping() {
        ColorHighlighter hl = new UIColorHighlighter(HighlightPredicate.ODD);
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
     * Creates and returns a Highlighter which highlights 
     * with alternate background, the first Color.WHITE, the second
     * with the color depending on LF. 
     * 
     * @return a Highlighter striping every second row background. 
     */
    public static Highlighter createAlternateUIStriping() {
        ColorHighlighter first = new ColorHighlighter(Color.WHITE, null, HighlightPredicate.EVEN);
        ColorHighlighter hl = new UIColorHighlighter(HighlightPredicate.ODD);
        return new CompoundHighlighter(first, hl);
    }

    /**
     * Creates and returns a Highlighter which highlights 
     * with alternate background, the first Color.WHITE, the second
     * with the color depending on LF. 
     * 
     * @return a Highlighter striping every second row background. 
     */
    public static Highlighter createAlternateUIStriping(int linesPerStripe) {
        HighlightPredicate predicate = new RowGroupHighlightPredicate(linesPerStripe);
        ColorHighlighter first = new ColorHighlighter(Color.WHITE, null, new NotHighlightPredicate(predicate));
        ColorHighlighter hl = new UIColorHighlighter(predicate);
        return new CompoundHighlighter(first, hl);
    }
    /**
     * Creates and returns a Highlighter which highlights with
     * alternating background, starting with the base.
     * 
     * @param baseBackground the background color for the even rows.
     * @param alternateBackground background color for odd rows.
     * @return a Highlighter striping alternating background. 
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
 
//--------------------------- UI dependent
    
    /**
     * A ColorHighlighter with UI-dependent background.
     */
    public static class UIColorHighlighter extends ColorHighlighter 
        implements UIDependent {

     private HashMap<Color, Color> colorMap;
     
     /**
      * Instantiates a ColorHighlighter with LF provided unselected
      * background and default predicate. All other colors are null.
      *
      */
     public UIColorHighlighter() {
         this(null);
     }
     

     /**
      * Instantiates a ColorHighlighter with LF provided unselected
      * background and the given predicate. All other colors are null.
     * @param odd the predicate to use
     */
    public UIColorHighlighter(HighlightPredicate odd) {
        super(null, null, odd);
        initColorMap();
        updateUI();
    }


    public void updateUI() {
         
         Color selection = UIManager.getColor("Table.selectionBackground");
         Color highlight = getMappedColor(selection);
         
         setBackground(highlight);
     }

     private Color getMappedColor(Color selection) {
         Color color = colorMap.get(selection);
         if (color == null) {
             color = GENERIC_GRAY;
         }
         return color;
     }
     /** 
      * this is a hack until we can think about something better!
      * we map all known selection colors to highlighter colors.
      *
      */
     private void initColorMap() {
         colorMap = new HashMap<Color, Color>();
         // Ocean
         colorMap.put(new Color(184, 207, 229), new Color(230, 238, 246));
         // xp blue
         colorMap.put(new Color(49, 106, 197), new Color(224, 233, 246));
         // xp silver
         colorMap.put(new Color(178, 180, 191), new Color(235, 235, 236));
         // xp olive
         colorMap.put(new Color(147, 160, 112), new Color(228, 231, 219));
         // win classic
         colorMap.put(new Color(10, 36, 106), new Color(218, 222, 233));
         // win 2k?
         colorMap.put(new Color(0, 0, 128), new Color(218, 222, 233));
         // default metal
         colorMap.put(new Color(205, 205, 255), new Color(235, 235, 255));
         // mac OS X
         colorMap.put(new Color(56, 117, 215), new Color(237, 243, 254));
         
     }
     
 }

}
