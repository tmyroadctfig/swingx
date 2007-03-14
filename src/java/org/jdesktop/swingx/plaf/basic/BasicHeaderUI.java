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
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXEditorPane;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
//import org.jdesktop.swingx.painter.gradient.BasicGradientPainter;
import org.jdesktop.swingx.plaf.HeaderUI;
import org.jdesktop.swingx.plaf.PainterUIResource;

/**
 *
 * @author rbair
 */
public class BasicHeaderUI extends HeaderUI {
    protected JLabel titleLabel;
    protected JXEditorPane descriptionPane;
    protected JLabel imagePanel;
    private PropertyChangeListener propListener;
    //this will not be actually put onto the component. Instead, it will
    //be used behind the scenes for painting the text and icon
    private LayoutManager layout;
    
    /** Creates a new instance of BasicHeaderUI */
    public BasicHeaderUI() {
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
        return new BasicHeaderUI();
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
        assert c instanceof JXHeader;
        JXHeader header = (JXHeader)c;
        
        installDefaults(header);
        
        titleLabel = new JLabel("Title For Header Goes Here");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        descriptionPane = new JXEditorPane();
        //        descriptionPane.setContentType("text/html");
        descriptionPane.setEditable(false);
        descriptionPane.setOpaque(false);
        descriptionPane.setText("The description for the header goes here.\nExample: Click the Copy Code button to generate the corresponding Java code.");
        imagePanel = new JLabel();
        imagePanel.setIcon(UIManager.getIcon("Header.defaultIcon"));
        
        installComponents(header);
        installListeners(header);
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
        assert c instanceof JXHeader;
        JXHeader header = (JXHeader)c;
        
        uninstallDefaults(header);
        uninstallListeners(header);
        uninstallComponents(header);
        
        titleLabel = null;
        descriptionPane = null;
        imagePanel = null;
        layout = null;
    }
    
    protected void installDefaults(JXHeader h) {
        Painter p = h.getBackgroundPainter();
        if (p == null || p instanceof PainterUIResource) {
            h.setBackgroundPainter(createBackgroundPainter());
        }
    }
    
    protected void uninstallDefaults(JXHeader h) {
    }
    
    protected void installListeners(final JXHeader h) {
        propListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                onPropertyChange(h, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            }
        };
        h.addPropertyChangeListener(propListener);
    }
    
    protected void uninstallListeners(JXHeader h) {
        h.removePropertyChangeListener(propListener);
    }
    
    protected void onPropertyChange(JXHeader h, String propertyName, Object oldValue, Object newValue) {
        if ("title".equals(propertyName) || propertyName == null) {
            titleLabel.setText(h.getTitle());
        } else if ("description".equals(propertyName) || propertyName == null) {
            descriptionPane.setText(h.getDescription());
        } else if ("icon".equals(propertyName) || propertyName == null) {
            imagePanel.setIcon(h.getIcon());
        } else if ("enabled".equals(propertyName) || propertyName == null) {
            boolean enabled = h.isEnabled();
            titleLabel.setEnabled(enabled);
            descriptionPane.setEnabled(enabled);
            imagePanel.setEnabled(enabled);
        } else if ("titleFont".equals(propertyName)) {
            titleLabel.setFont((Font)newValue);
        } else if ("descriptionFont".equals(propertyName)) {
            descriptionPane.setFont((Font)newValue);
        }
    }
    
    protected void installComponents(JXHeader h) {
        h.setLayout(new GridBagLayout());
        h.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(12, 12, 0, 11), 0, 0));
        h.add(descriptionPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 24, 0, 11), 0, 0));
        h.add(imagePanel, new GridBagConstraints(1, 0, 1, 2, 0.0, 1.0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(12, 0, 11, 11), 0, 0));
    }
    
    protected void uninstallComponents(JXHeader h) {
        h.remove(titleLabel);
        h.remove(descriptionPane);
        h.remove(imagePanel);
    }
    
    protected Painter createBackgroundPainter() {
        return new PainterUIResource(new MattePainter(new GradientPaint(0, 0, Color.WHITE, 1, 0, UIManager.getColor("control"))));
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(titleLabel);
        panel.add(descriptionPane);
        panel.add(imagePanel);
        panel.setSize(c.getSize());
        layout.layoutContainer(panel);
        panel.paint(g);
    }
}
