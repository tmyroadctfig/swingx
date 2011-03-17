package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.io.Serializable;

abstract class AbstractLayoutManager implements LayoutManager, Serializable {
    private static final long serialVersionUID = 1446292747820044161L;

    /**
     * {@inheritDoc}
     * <p>
     * This implementation does nothing.
     */
    @Override
    public void addLayoutComponent(String name, Component comp) {
        //does nothing
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation does nothing.
     */
    @Override
    public void removeLayoutComponent(Component comp) {
        // does nothing
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation defers to {@link #preferredLayoutSize(Container)}.
     */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }
}
