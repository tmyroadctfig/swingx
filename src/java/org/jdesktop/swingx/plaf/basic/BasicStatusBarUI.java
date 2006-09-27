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

package org.jdesktop.swingx.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXStatusBar.Constraint;
import org.jdesktop.swingx.plaf.StatusBarUI;

/**
 *
 * @author rbair
 */
public class BasicStatusBarUI extends StatusBarUI {
    /**
     * The one and only JXStatusBar for this UI delegate
     */
    private JXStatusBar statusBar;
    
    /** Creates a new instance of BasicStatusBarUI */
    public BasicStatusBarUI() {
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
        return new BasicStatusBarUI();
    }	
    
    /**
     * Configures the specified component appropriate for the look and feel.
     * This method is invoked when the <code>ComponentUI</code> instance is being installed
     * as the UI delegate on the specified component.  This method should
     * completely configure the component for the look and feel,
     * including the following:
     * <ol>
     * <li>Install any default property values for color, fonts, borders,
     *     icons, opacity, etc. on the component.  Whenever possible, 
     *     property values initialized by the client program should <i>not</i> 
     *     be overridden.
     * <li>Install a <code>LayoutManager</code> on the component if necessary.
     * <li>Create/add any required sub-components to the component.
     * <li>Create/install event listeners on the component.
     * <li>Create/install a <code>PropertyChangeListener</code> on the component in order
     *     to detect and respond to component property changes appropriately.
     * <li>Install keyboard UI (mnemonics, traversal, etc.) on the component.
     * <li>Initialize any appropriate instance data.
     * </ol>
     * @param c the component where this UI delegate is being installed
     *
     * @see #uninstallUI
     * @see javax.swing.JComponent#setUI
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void installUI(JComponent c) {
        assert c instanceof JXStatusBar;
        statusBar = (JXStatusBar)c;
        
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 22));
        statusBar.setLayout(createLayout());

    }

    /**
     * Reverses configuration which was done on the specified component during
     * <code>installUI</code>.  This method is invoked when this 
     * <code>UIComponent</code> instance is being removed as the UI delegate 
     * for the specified component.  This method should undo the
     * configuration performed in <code>installUI</code>, being careful to 
     * leave the <code>JComponent</code> instance in a clean state (no 
     * extraneous listeners, look-and-feel-specific property objects, etc.).
     * This should include the following:
     * <ol>
     * <li>Remove any UI-set borders from the component.
     * <li>Remove any UI-set layout managers on the component.
     * <li>Remove any UI-added sub-components from the component.
     * <li>Remove any UI-added event/property listeners from the component.
     * <li>Remove any UI-installed keyboard UI from the component.
     * <li>Nullify any allocated instance data objects to allow for GC.
     * </ol>
     * @param c the component from which this UI delegate is being removed;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @see #installUI
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void uninstallUI(JComponent c) {
        assert c instanceof JXStatusBar;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        
        //paint the background if opaque
        if (statusBar.isOpaque()) {
            Graphics2D g2 = (Graphics2D)g;
            paintBackground(g2, statusBar);
            
            //now paint the separators
            Insets sepInsets = new Insets(0, 0, 0, 0);
            getSeparatorInsets(sepInsets);
            for (int i=0; i<statusBar.getComponentCount()-1; i++) {
                Component comp = statusBar.getComponent(i);
                int x = comp.getX() + comp.getWidth() + sepInsets.left;
                int y = sepInsets.top;
                int w = getSeparatorWidth() - sepInsets.left - sepInsets.right;
                int h = c.getHeight() - sepInsets.top - sepInsets.bottom;
                
                paintSeparator(g2, statusBar, x, y, w, h);
            }
        }
    }

    //----------------------------------------------------- Extension Points
    protected void paintBackground(Graphics2D g, JXStatusBar bar) {
        g.setColor(bar.getBackground());
        g.fillRect(0, 0, bar.getWidth(), bar.getHeight());
    }
    
    protected void paintSeparator(Graphics2D g, JXStatusBar bar, int x, int y, int w, int h) {
        Color fg = UIManager.getColor("Separator.foreground");
        Color bg = UIManager.getColor("Separator.background");
        
        x += w / 2;
        g.setColor(fg);
        g.drawLine(x, y, x, h);

        g.setColor(bg);
        g.drawLine(x+1, y, x+1, h);
    }
    
    protected void getSeparatorInsets(Insets insets) {
        insets.top = 4;
        insets.left = 4;
        insets.bottom = 2;
        insets.right = 4;
    }
    
    protected int getSeparatorWidth() {
        return 10;
    }
    
    protected LayoutManager createLayout() {
        //This is in the UI delegate because the layout
        //manager takes into account spacing for the separators between components
        return new LayoutManager2() {
            private Map<Component,Constraint> constraints = new HashMap<Component,Constraint>();

            public void addLayoutComponent(String name, Component comp) {
                addLayoutComponent(comp, null);
            }

            public void addLayoutComponent(Component comp, Object constraint) {
                //we accept an Insets, a ResizeBehavior, or a Constraint.
                if (constraint instanceof Insets) {
                    constraint = new Constraint((Insets)constraint);
                } else if (constraint instanceof Constraint.ResizeBehavior) {
                    constraint = new Constraint((Constraint.ResizeBehavior)constraint);
                }

                constraints.put(comp, (Constraint)constraint);
            }

            public void removeLayoutComponent(Component comp) {
                constraints.remove(comp);
            }

            public Dimension preferredLayoutSize(Container parent) {
                Dimension prefSize = new Dimension();
                int count = 0;
                for (Component comp : constraints.keySet()) {
                    Constraint c = constraints.get(comp);
                    Dimension d = comp.getPreferredSize();
                    int prefWidth = 0;
                    if (c != null) {
                        Insets i = c.getInsets();
                        d.width += i.left + i.right;
                        d.height += i.top + i.bottom;
                        prefWidth = c.getPreferredWidth();
                    }
                    prefSize.height = Math.max(prefSize.height, d.height);
                    prefSize.width += Math.max(d.width, prefWidth);

                    //If this is not the last component, add extra space between each
                    //component (for the separator).
                    count++;
                    if (constraints.size() < count) {
                        prefSize.width += getSeparatorWidth();
                    }
                }

                Insets insets = parent.getInsets();
                prefSize.height += insets.top + insets.bottom;
                prefSize.width += insets.left + insets.right;
                return prefSize;
            }

            public Dimension minimumLayoutSize(Container parent) {
                return preferredLayoutSize(parent);
            }

            public Dimension maximumLayoutSize(Container target) {
                return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }

            public float getLayoutAlignmentX(Container target) {
                return .5f;
            }

            public float getLayoutAlignmentY(Container target) {
                return .5f;
            }

            public void invalidateLayout(Container target) {
                //I don't hold on to any state, so nothing to do here
            }

            public void layoutContainer(Container parent) {
                //find out the maximum weight of all the visible components
                int numFilledComponents = 0;
                for (Component comp : parent.getComponents()) {
                    Constraint c = constraints.get(comp);
                    if (c != null && c.getResizeBehavior() == Constraint.ResizeBehavior.FILL) {
                        numFilledComponents++;
                    }
                }
                double weight = numFilledComponents > 0 ? 1 / numFilledComponents : 0;

                //the amount of available space. If positive, it will be split up among
                //all visible components that have a FILL resize behavior
                Insets parentInsets = parent.getInsets();
                int availableSpace = parent.getWidth() - preferredLayoutSize(parent).width;
                //the next X location to place a component at
                int nextX = parentInsets.left;
                int height = parent.getHeight() - parentInsets.top - parentInsets.bottom;

                //now lay out each visible component
                for (int i=0; i<parent.getComponentCount(); i++) {
                    Component comp = parent.getComponent(i);
                    Constraint c = constraints.get(comp);
                    Constraint.ResizeBehavior rb = c == null ? null : c.getResizeBehavior();
                    Insets insets = c == null ? new Insets(0,0,0,0) : c.getInsets();
                    int prefWidth = c == null ? 0 : c.getPreferredWidth() - insets.left - insets.right;

                    int spaceToTake = availableSpace > 0 && rb == Constraint.ResizeBehavior.FILL ? 
                        (int)(weight * availableSpace) : 0;
                    availableSpace -= spaceToTake;

                    int width = comp.getPreferredSize().width + spaceToTake;
                    width = Math.max(width, prefWidth);

                    int x = nextX + insets.left;
                    int y = parentInsets.top + insets.top;
                    comp.setSize(width, height);
                    comp.setLocation(x, y);
                    nextX = x + width + insets.right;
                    
                    //If this is not the last component, add extra space
                    //for the separator
                    if (i < parent.getComponentCount() - 1) {
                        nextX += getSeparatorWidth();
                    }
                }
            }
        };
    }
}
