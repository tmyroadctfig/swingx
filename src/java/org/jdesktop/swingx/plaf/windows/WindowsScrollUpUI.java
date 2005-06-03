/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.plaf.windows;

import java.awt.GradientPaint;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.plaf.basic.BasicScrollUpUI;

/**
 *
 * @author rbair
 */
public class WindowsScrollUpUI extends BasicScrollUpUI {
    
    /**
     * Creates a new instance of WindowsScrollUpUI 
     */
    public WindowsScrollUpUI() {
    }
    
    /**
     * Returns an instance of the UI delegate for the specified component.
     * Each subclass must provide its own static <code>createUI</code>
     * method that returns an instance of that UI delegate subclass.
     * If the UI delegate subclass is stateless, it may return an instance
     * that is shared by multiple components.  If the UI delegate is
     * stateful, then it should return a new instance per component.
     * The default implementation of this method throws an error, as it
     * should never be invoked.
     */
    public static ComponentUI createUI(JComponent c) {
        return new WindowsScrollUpUI();
    }	


    private final class WindowScrollUpTopPanel extends ScrollUpTopPanel {
        public WindowScrollUpTopPanel(JXTitledPanel panel) {
            super(panel);
            // TODO Auto-generated constructor stub
        }

        /**
         * Override createGradientPaint so that I can create a paint that does not
         * provide a smooth gradient, but rather starts the gradient towards the end
         * of the panel.
         */
        protected GradientPaint createGradientPaint() {
            return new GradientPaint((getWidth() * .1f), 
                                     0, 
                                     titledPanel.getTitleLightBackground(), 
                                     getWidth(), 
                                     0, 
                                     titledPanel.getTitleDarkBackground());
        }
    }
}
