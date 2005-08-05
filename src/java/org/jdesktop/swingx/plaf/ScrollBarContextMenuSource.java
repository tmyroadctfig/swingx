/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;

/**
 * @author (C) 2004 Jeanette Winzenburg, Berlin
 * @version $Revision$ - $Date$
 */
public class ScrollBarContextMenuSource extends ContextMenuSource {

    String[] keys = { /*null, null,  need to add scrollHere!*/ 
          "minScroll", "maxScroll",  
          null,  
          "negativeUnitIncrement", "positiveUnitIncrement",
          null,
          "negativeBlockIncrement", "positiveBlockIncrement",
    };
    
    String[] defaultValuesVertical = {
          "Top", "Bottom",
          null,
          "Scroll Up", "Scroll Down",
          null,
          "Page Up", "Page Down",
    };

    String[] defaultValuesHorizontal = {
            "Left Edge", "Right Edge",
            null,
            "Scroll Left", "Scroll Right",
            null,
            "Page Left", "Page Right",
      };
    
    private int orientation;

    public ScrollBarContextMenuSource(int orientation) {
        this.orientation = orientation;
    }

    public String[] getKeys() {
        // TODO Auto-generated method stub
        return keys;
    }

    public void updateActionEnabled(JComponent component, ActionMap map) {
        // TODO Auto-generated method stub

    }

    protected void initNames(Map<String, String> names) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) {
                names.put(keys[i],  getValue(keys[i], 
                        orientation == JScrollBar.VERTICAL ?
                                defaultValuesVertical[i] : defaultValuesHorizontal[i]));
            }
        }

    }

    protected String getResourcePrefix() {
        return "JScrollBar." + getOrientationToken();
    }

    private String getOrientationToken() {
        return orientation == JScrollBar.VERTICAL ? "vertical." : "horizontal.";
    }

}
