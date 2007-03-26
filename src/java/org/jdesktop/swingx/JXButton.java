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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * <p>A {@link org.jdesktop.swingx.painter.Painter} enabled subclass of {@link javax.swing.JButton}.
 * This class supports setting the foreground and background painters of the button separately. By default,
 * <code>JXButton</code> creates and installs two <code>Painter</code>s; one for the foreground, and one
 * for the background. These default <code>Painter</code>s delegate to the installed UI delegate.</p>
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
 * then super.paintComponent() is not called. By setting botht he foreground and background
 * painters to null, you get <em>exactly</em> the same painting behavior as JButton.
 * By contrast, the <code>Painters</code> installed by default will delegate to the UI delegate,
 * thus achieving the same look as a typical JButton, but at the cost of some additional painting
 * overhead.</p>
 *
 * <div class="examples">
 * <h3>Examples</h3>
 * {@demo org.jdesktop.swingx.JXButtonDemo ../../../../../demo}
 * </div>
 *
 * @author rbair
 */
public class JXButton extends JButton {
    //a dummy JDialog, needed to convince a rubber-stamp
    //like component to paint
    private static JDialog invisibleParent = new JDialog();
    //rubber-stamp like component for painting a button
    private static JStampButton stamp = new JStampButton();
    //initialize the rubber-stamp parent
    static {
        invisibleParent.add(stamp);
        invisibleParent.setSize(1, 1);
    }
    //the default fg and bg painters
    private static final Painter DEFAULT_BACKGROUND_PAINTER = new DefaultBackgroundPainter();
    private static final Painter DEFAULT_FOREGROUND_PAINTER = new DefaultForegroundPainter();

    private Painter fgPainter = DEFAULT_FOREGROUND_PAINTER;
    private Painter bgPainter = DEFAULT_BACKGROUND_PAINTER;

    /** Creates a new instance of JXButton */
    public JXButton() {}
    public JXButton(String text) { super(text); }
    
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
    
    protected void paintComponent(Graphics g) {
        Painter bgPainter = getBackgroundPainter();
        Painter fgPainter = getForegroundPainter();
        if (bgPainter == null && fgPainter == null) {
            super.paintComponent(g);
        } else {
            if (bgPainter != null) {
                bgPainter.paint((Graphics2D)g, this, getWidth(), getHeight());
            }

            if (fgPainter != null) {
                fgPainter.paint((Graphics2D)g, this, getWidth(), getHeight());
            }
        }
    }
        
    private static final class DefaultBackgroundPainter extends AbstractPainter<JButton> {
        protected void doPaint(Graphics2D g, JButton b, int width, int height) {
            copySettings(b, stamp);
            stamp.setSize(b.getWidth(), b.getHeight());
            stamp.setText("");
            stamp.setBorderPainted(true);
            stamp.setContentAreaFilled(true);
            stamp.paint(g);
        }

        //if any of the state of the JButton that affects the background has changed,
        //then I must clear the cache. This is really hard to get right, there are
        //bound to be bugs. An alternative is to NEVER cache.
        protected boolean shouldUseCache() {
            return false;
        }
    }
    private static final class DefaultForegroundPainter extends AbstractPainter<JButton> {
        protected void doPaint(Graphics2D g, JButton b, int width, int height) {
            copySettings(b, stamp);
            stamp.setSize(b.getWidth(), b.getHeight());
            stamp.setText(b.getText());
            stamp.setBorderPainted(false);
            stamp.setContentAreaFilled(false);
            stamp.paint(g);
        }

        //if any of the state of the JButton that affects the foreground has changed,
        //then I must clear the cache. This is really hard to get right, there are
        //bound to be bugs. An alternative is to NEVER cache.
        protected boolean shouldUseCache() {
            return false;
        }
    }
    private static void copySettings(JButton src, JStampButton dest) {
        dest.setBounds(src.getBounds());
        dest.setText(src.getText());
        dest.setBackground(src.getBackground());
        dest.setForeground(src.getForeground());
        dest.setBorder(src.getBorder());
        dest.setBorderPainted(src.isBorderPainted());
        dest.setDefaultButton(src.isDefaultButton());
        dest.setDefaultCapable(src.isDefaultCapable());
        dest.setSelected(src.isSelected());
        dest.setMargin(src.getMargin());
        dest.setIcon(src.getIcon());
        dest.setPressedIcon(src.getPressedIcon());
        dest.setSelectedIcon(src.getSelectedIcon());
        dest.setRolloverIcon(src.getRolloverIcon());
        dest.setRolloverSelectedIcon(src.getRolloverSelectedIcon());
        dest.setDisabledIcon(src.getDisabledIcon());
        dest.setDisabledSelectedIcon(src.getDisabledSelectedIcon());
        dest.setVerticalAlignment(src.getVerticalAlignment());
        dest.setHorizontalAlignment(src.getHorizontalAlignment());
        dest.setVerticalTextPosition(src.getVerticalTextPosition());
        dest.setHorizontalTextPosition(src.getHorizontalTextPosition());
        dest.setIconTextGap(src.getIconTextGap());
        dest.setFocusPainted(src.isFocusPainted());
        dest.setContentAreaFilled(src.isContentAreaFilled());
        dest.setRolloverEnabled(src.isRolloverEnabled());
        dest.setMnemonic(src.getMnemonic());
        dest.setDisplayedMnemonicIndex(src.getDisplayedMnemonicIndex());
        dest.setModel(src.getModel());
        dest.setAlignmentY(src.getAlignmentY());
        dest.setAlignmentX(src.getAlignmentX());
//        dest.setVisible(src.isVisible());
    }
    private static final class JStampButton extends JButton {
        private boolean isDefault = false;
        public void setDefaultButton(boolean b) { isDefault = b; }
        public boolean isDefaultButton() { return isDefault; }
        public void setModel(final ButtonModel delegate) {
            super.setModel(new ButtonModel() {
                public boolean isArmed() { return delegate.isArmed(); }
                public boolean isSelected() { return delegate.isSelected(); }
                public boolean isEnabled() { return delegate.isEnabled(); }
                public boolean isPressed() { return delegate.isPressed(); }
                public boolean isRollover() { return delegate.isRollover(); }
                public void setArmed(boolean b) {}
                public void setSelected(boolean b) {}
                public void setEnabled(boolean b) {}
                public void setPressed(boolean b) {}
                public void setRollover(boolean b) {}
                public void setMnemonic(int i) {}
                public int getMnemonic() { return delegate.getMnemonic(); }
                public void setActionCommand(String string) {}
                public String getActionCommand() { return delegate.getActionCommand(); }
                public void setGroup(ButtonGroup buttonGroup) {}
                public void addActionListener(ActionListener actionListener) {}
                public void removeActionListener(ActionListener actionListener) {}
                public Object[] getSelectedObjects() { return delegate.getSelectedObjects(); }
                public void addItemListener(ItemListener itemListener) {}
                public void removeItemListener(ItemListener itemListener) {}
                public void addChangeListener(ChangeListener changeListener) {}
                public void removeChangeListener(ChangeListener changeListener) {}
            });
        }
    }
}
