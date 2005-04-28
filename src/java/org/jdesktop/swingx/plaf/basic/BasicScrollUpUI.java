/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.plaf.basic;

import java.awt.Component;
import org.jdesktop.swingx.JXPanel;

/**
 * Adds logic for creating a different title bar component that handles
 * alpha differently for enhanced scrollup/down behavior
 *
 * @author rbair
 */
public class BasicScrollUpUI extends BasicTitledPanelUI {
    
    /** Creates a new instance of BasicScrollUpUI */
    public BasicScrollUpUI() {
    }

    protected JGradientPanel createTopPanel() {
        return new ScrollUpTopPanel();
    }
    
    protected class ScrollUpTopPanel extends JGradientPanel {
        /**
         * rather than using the default algorithm, this panel will
         * <b>skip</b> its immediate parent in searching for an effective
         * alpha. That is, the parent may have an alpha of .1, but I want
         * this component to be drawn with an alpha of 1.0 (or whatever).
         * However, if some other parent in the heirarchy aside from the
         * immediate parent has an alpha of .1, I want to respect that
         * value.
         */
        public float getEffectiveAlpha() {
            if (isInheritAlpha()) {
                float a = getAlpha();
                Component c = getParent();
                if (c == null) {
                    return getAlpha();
                }
                while ((c = c.getParent()) != null) {
                    if (c instanceof JXPanel) {
                        a = Math.min(((JXPanel)c).getAlpha(), a);
                    }
                }
                return a;
            } else {
                return getAlpha();
            }
        }
    }
}