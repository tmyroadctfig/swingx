/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.ButtonUI;

import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.XButtonAddon;

/**
 * <p>A {@link org.jdesktop.swingx.painter.Painter} enabled subclass of {@link javax.swing.JButton}.
 * This class supports setting the foreground and background painters of the button separately.</p>
 *
 * <p>For example, if you wanted to blur <em>just the text</em> on the button, and let everything else be
 * handled by the UI delegate for your look and feel, then you could:
 * <pre><code>
 *  JXButton b = new JXButton("Execute");
 *  AbstractPainter fgPainter = (AbstractPainter)b.getForegroundPainter();
 *  StackBlurFilter filter = new StackBlurFilter();
 *  fgPainter.setFilters(filter);
 * </code></pre>
 *
 * <p>If <em>either</em> the foreground painter or the background painter is set,
 * then super.paintComponent() is not called. By setting both the foreground and background
 * painters to null, you get <em>exactly</em> the same painting behavior as JButton.
 * By contrast, the <code>Painters</code> installed by default will delegate to the UI delegate,
 * thus achieving the same look as a typical JButton, but at the cost of some additional painting
 * overhead.</p>
 *
 * @author rbair
 * @author rah003
 * @author Jan Stola
 * @author Karl George Schaefer
 */
public class JXButton extends JButton {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    public static final String uiClassID = "XButtonUI";
    
    private Painter<JXButton> fgPainter;
    private Painter<JXButton> bgPainter;
    
    static {
        LookAndFeelAddons.contribute(new XButtonAddon());
    }

    /** Creates a new instance of JXButton */
    public JXButton() {}
    public JXButton(String text) {
        super(text);
    }
    public JXButton(Action a) {
        super(a);
    }
    public JXButton(Icon icon) {
        super(icon);
    }
    public JXButton(String text, Icon icon) {
        super(text, icon);
    }

    public Painter getBackgroundPainter() {
        return bgPainter;
    }

    public void setBackgroundPainter(Painter p) {
        Painter old = getBackgroundPainter();
        this.bgPainter = p;
        firePropertyChange("backgroundPainter", old, getBackgroundPainter());
        repaint();
    }
    public Painter getForegroundPainter() {
        return fgPainter;
    }

    public void setForegroundPainter(Painter p) {
        Painter old = getForegroundPainter();
        this.fgPainter = p;
        firePropertyChange("foregroundPainter", old, getForegroundPainter());
        repaint();
    }

    private boolean paintBorderInsets = true;

    /**
     * Returns true if the background painter should paint where the border is
     * or false if it should only paint inside the border. This property is
     * true by default. This property affects the width, height,
     * and initial transform passed to the background painter.
     */
    public boolean isPaintBorderInsets() {
        return paintBorderInsets;
    }

    /**
     * Sets the paintBorderInsets property.
     * Set to true if the background painter should paint where the border is
     * or false if it should only paint inside the border. This property is true by default.
     * This property affects the width, height,
     * and initial transform passed to the background painter.
     *
     * This is a bound property.
     */
    public void setPaintBorderInsets(boolean paintBorderInsets) {
        boolean old = this.isPaintBorderInsets();
        this.paintBorderInsets = paintBorderInsets;
        firePropertyChange("paintBorderInsets", old, isPaintBorderInsets());
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }
    
    /**
     * Notification from the <code>UIManager</code> that the L&F has changed.
     * Replaces the current UI object with the latest version from the <code>UIManager</code>.
     * 
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void updateUI() {
      setUI((ButtonUI)LookAndFeelAddons.getUI(this, ButtonUI.class));
    }
}
