/*
 * ScrollUpLayout.java
 *
 * Created on April 15, 2005, 1:22 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Scrollable;
import javax.swing.ViewportLayout;

/**
 * Organizes JXScrollUps (or other components) in a vertical
 * layout.
 *
 * @author rbair
 */
public class ScrollUpLayout implements LayoutManager2 {
    private static final int SPACE = 10;
    
    private List<Component> comps = new ArrayList<Component>();
    
    /** Creates a new instance of ScrollUpLayout */
    public ScrollUpLayout() {
    }

    public void addLayoutComponent(Component comp, Object constraints) {
        comps.remove(comp);
        comps.add(comp);
    }

    public void addLayoutComponent(String name, Component comp) {
        addLayoutComponent(comp, name);
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension pref = new Dimension();
        for (Component c : comps) {
            Dimension dim = c.getPreferredSize();
            pref.width = Math.max(pref.width, dim.width);
            pref.height += dim.height;
        }
        pref.height += ((comps.size() + 1) * SPACE);
        pref.width += SPACE;
        return pref;
    }

    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    public Dimension maximumLayoutSize(Container target) {
        return preferredLayoutSize(target);
    }

    public void layoutContainer(Container parent) {
        //start with the first component
        Dimension pref = preferredLayoutSize(parent);
        pref.width = Math.max(parent.getWidth(), pref.width);
        parent.setSize(pref);
        Dimension offset = new Dimension(SPACE/2, SPACE);
        for (Component c : comps) {
//            c.setLocation(offset.width, offset.height);
//            c.setSize(pref.width - SPACE, c.getHeight());
            c.setBounds(offset.width, offset.height, pref.width - SPACE, c.getPreferredSize().height);
            offset.height += c.getHeight();
            offset.height += SPACE;
        }
    }

    public void invalidateLayout(Container target) {
    }

    public float getLayoutAlignmentY(Container target) {
        return .5f;
    }

    public float getLayoutAlignmentX(Container target) {
        return .5f;
    }

    public void removeLayoutComponent(Component comp) {
        comps.remove(comp);
    }
    
}
