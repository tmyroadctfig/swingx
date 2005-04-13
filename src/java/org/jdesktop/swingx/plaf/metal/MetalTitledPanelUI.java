/*
 * MetalTitledPanelUI.java
 *
 * Created on April 12, 2005, 11:36 AM
 */

package org.jdesktop.swingx.plaf.metal;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.plaf.basic.BasicTitledPanelUI;

/**
 *
 * @author rb156199
 */
public class MetalTitledPanelUI extends BasicTitledPanelUI {
    
    /** Creates a new instance of MetalTitledPanelUI */
    public MetalTitledPanelUI() {
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
        return new MetalTitledPanelUI();
    }	
}
