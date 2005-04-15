/*
 * JXScrollUpPane.java
 *
 * Created on April 14, 2005, 2:36 PM
 */

package org.jdesktop.swingx;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import javax.swing.JTree;

/**
 * Special container that animates the scrolling behavior
 * @author rb156199
 */
public class JXScrollUpPane extends JXPanel {
    /**
	 * The amount of time in milliseconds to wait between calls to the animation thread
	 */
	private static final int WAIT_TIME = 5;
	/**
	 * The delta in the Y direction to inc/dec the size of the scroll up by
	 */
	private static final int DELTA_Y = 10;
    /**
     * The starting alpha transparency level
     */
    private static final float ALPHA_START = 0.01f;
    /**
     * The ending alpha transparency level
     */
    private static final float ALPHA_END = 1.0f;

    public JXScrollUpPane() {
    }

    private final class CollapseListener implements PropertyChangeListener {
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            //start animating
            
        }
    }
    
    
}
