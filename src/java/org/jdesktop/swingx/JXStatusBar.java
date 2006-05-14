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

package org.jdesktop.swingx;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.jdesktop.swingx.plaf.JXStatusBarAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.StatusBarUI;

/**
 * <p>A container for {@link javax.swing.JComponent}s that is typically placed at
 * the bottom of a form and runs the entire width of the form. There are 3
 * important functions that JXStatusBar provides (reasons why we created it
 * in the first place). First, JXStatusBar provides a hook for a pluggable look.
 * There is a definite look associated with status bars on windows, for instance.
 * By implementing a concrete subclass of JXPanel, we provide a way for the
 * pluggable look and feel system to modify the look of the status bar automatically.</p>
 *
 * <p>Second, JXStatusBar comes with its own layout manager. Each item is added to
 * the JXStatusBar with a JXStatusBar.Constraint as the constraint argument. The
 * JXStatusBar.Constraint contains an Insets object, as well as a "weight". The weight
 * is used the same as the GridBagLayout.</p>
 *
 * <p>Finally, JXStatusBar contains a built in JPopupMenu. This menu allows the
 * user to hide/show any of the items on the status bar. It also may optionally
 * contain an "add..." action that will allow the user to select from a handful
 * of prepacked beans that can be placed on the status bar.</p>
 *
 * <p>Constructing a JXStatusBar is very straitforward:
 * <pre><code>
 *      JXStatusBar bar = new JXStatusBar();
 *      JLabel statusLabel = new JLabel("Ready");
 *      bar.add(statusLabel, new JXStatusBar.Constraints(1.0); //weight of 0.0 and no insets
 *      JProgressBar pbar = new JProgressBar();
 *      bar.add(pbar); //weight of 0.0 and no insets
 * </code></pre></p>
 *
 * <p>Two common use cases for status bars is tracking application status and
 * progress. JXStatusBar does not manage these tasks, but instead special components
 * exist or can be created that do manage these tasks. For instance, if you application
 * has a TaskManager or some other repository of currently running jobs, you could
 * easily create a TaskManagerProgressBar that tracks those jobs. This component
 * could then be added to the JXStatusBar like any other component.</p>
 *
 * @author pdoubleya
 * @author rbair
 */
public class JXStatusBar extends JXPanel {
    /**
     * @see #getUIClassID // *
     * @see #readObject
     */
    static public final String uiClassID = "StatusBarUI";
    
    /**
     * Initialization that would ideally be moved into various look and feel
     * classes.
     */
    static {
        LookAndFeelAddons.contribute(new JXStatusBarAddon());
    }
    
//    //TODO this should be part of the plaf UI delegate
//    private Insets margin = new Insets(3, 3, 3, 3);
    
    public JXStatusBar() {
        super();
        setLayout(new Layout());
    }

    /**
     * Returns the look and feel (L&F) object that renders this component.
     * 
     * @return the StatusBarUI object that renders this component
     */
    @Override
    public StatusBarUI getUI() {
        return (StatusBarUI) ui;
    }

    /**
     * Sets the look and feel (L&F) object that renders this component.
     * 
     * @param ui
     *            the StatusBarUI L&F object
     * @see javax.swing.UIDefaults#getUI
     * @beaninfo bound: true hidden: true attribute: visualUpdate true
     *           description: The UI object that implements the Component's
     *           LookAndFeel.
     */
    public void setUI(StatusBarUI ui) {
        super.setUI(ui);
    }

    /**
     * Returns a string that specifies the name of the L&F class that renders
     * this component.
     * 
     * @return "StatusBarUI"
     * @see javax.swing.JComponent#getUIClassID
     * @see javax.swing.UIDefaults#getUI
     * @beaninfo expert: true description: A string that specifies the name of
     *           the L&F class.
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Notification from the <code>UIManager</code> that the L&F has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>.
     * 
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void updateUI() {
        setUI((StatusBarUI) LookAndFeelAddons
                .getUI(this, StatusBarUI.class));
    }

//    @Override
//    public Insets getInsets() {
//        Insets i = super.getInsets();
//        i.top += margin.top;
//        i.left += margin.left;
//        i.right += margin.right;
//        i.bottom += margin.bottom;
//        return i;
//    }
//    
//    @Override
//    public Insets getInsets(Insets insets) {
//        insets = super.getInsets(insets);
//        insets.top += margin.top;
//        insets.left += margin.left;
//        insets.right += margin.right;
//        insets.bottom += margin.bottom;
//        return insets;
//    }
    
    public void addSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        add(sep, new Constraint(new Insets(1, 5, 1, 5)));
    }
    
    public static class Constraint {
        private Insets insets;
        private double weight;
        
        public Constraint(Insets insets) {
            this(0.0, insets);
        }
        
        public Constraint(double weight) {
            this(weight, new Insets(0,0,0,0));
        }
        
        public Constraint(double weight, Insets insets) {
            this.weight = weight;
            this.insets = insets;
        }
        
        public double getWeight() {
            return weight;
        }
        
        public Insets getInsets() {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }
    }
    
    private static class Layout implements LayoutManager2 {
        private Map<Component,Constraint> constraints = new HashMap<Component,Constraint>();
        
        public void addLayoutComponent(String name, Component comp) {
            addLayoutComponent(comp, null);
        }

        public void addLayoutComponent(Component comp, Object constraint) {
            constraints.put(comp, (Constraint)constraint);
        }

        public void removeLayoutComponent(Component comp) {
            constraints.remove(comp);
        }

        public Dimension preferredLayoutSize(Container parent) {
            Dimension prefSize = new Dimension();
            for (Component comp : constraints.keySet()) {
                Dimension d = comp.getPreferredSize();
                Constraint c = constraints.get(comp);
                if (c != null) {
                    Insets i = c.getInsets();
                    d.width += i.left + i.right;
                    d.height += i.top + i.bottom;
                }
                prefSize.height = Math.max(prefSize.height, d.height);
                prefSize.width += d.width;
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
            double maxWeight = 0.0;
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    Constraint c = constraints.get(comp);
                    maxWeight += c == null ? 0.0 : c.getWeight();
                }
            }
            maxWeight = maxWeight == 0 ? 1.0 : maxWeight; //don't let maxWeight be 0
            
            //the amount of available space. If positive, it will be split up among
            //all visible components that have a positive weight
            //If negative, then no weights will be configured
            Insets parentInsets = parent.getInsets();
            int availableSpace = parent.getWidth() - preferredLayoutSize(parent).width;
            //the next X location to place a component at
            int nextX = parentInsets.left;
            int height = parent.getHeight() - parentInsets.top - parentInsets.bottom;
            
            //now lay out each visible component
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    Constraint c = constraints.get(comp);
                    double weight = c == null ? 0.0 : c.getWeight();
                    Insets insets = c == null ? new Insets(0,0,0,0) : c.getInsets();
                    
                    int spaceToTake = availableSpace > 0 ? 
                        (int)((weight/maxWeight) * availableSpace) : 0;
                    availableSpace -= spaceToTake;
                    
                    int width = comp.getPreferredSize().width + spaceToTake;

                    int x = nextX + insets.left;
                    int y = parentInsets.top + insets.top;
                    comp.setSize(width, height);
                    comp.setLocation(x, y);
                    nextX = x + width + insets.right;
                }
            }
        }
    }
}
