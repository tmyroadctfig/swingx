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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/**
 * Basic implementation of the <code>JXHyperlink</code> UI. <br>
 * This is copied from org.jdesktop.jdnc.plaf.basic.BasicLinkButtonUI
 */
public class BasicHyperlinkUI extends BasicButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new BasicHyperlinkUI();
    }

    private static Rectangle viewRect = new Rectangle();

    private static Rectangle textRect = new Rectangle();

    private static Rectangle iconRect = new Rectangle();

    private static MouseListener handCursorListener = new HandCursor();

    protected int dashedRectGapX;

    protected int dashedRectGapY;

    protected int dashedRectGapWidth;

    protected int dashedRectGapHeight;

    private Color focusColor;

    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);

        b.setOpaque(false);
        b.setBorderPainted(false);
        b.setRolloverEnabled(true);
        b.setBorder(BorderFactory.createEmptyBorder());

        dashedRectGapX = UIManager.getInt("ButtonUI.dashedRectGapX");
        dashedRectGapY = UIManager.getInt("ButtonUI.dashedRectGapY");
        dashedRectGapWidth = UIManager.getInt("ButtonUI.dashedRectGapWidth");
        dashedRectGapHeight = UIManager.getInt("ButtonUI.dashedRectGapHeight");
        focusColor = UIManager.getColor("ButtonUI.focus");

        b.setHorizontalAlignment(AbstractButton.LEADING);
    }

    protected void installListeners(AbstractButton b) {
        super.installListeners(b);
        b.addMouseListener(handCursorListener);
    }

    protected void uninstallListeners(AbstractButton b) {
        super.uninstallListeners(b);
        b.removeMouseListener(handCursorListener);
    }

    protected Color getFocusColor() {
        return focusColor;
    }

    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        FontMetrics fm = g.getFontMetrics();

        Insets i = c.getInsets();

        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = b.getWidth() - (i.right + viewRect.x);
        viewRect.height = b.getHeight() - (i.bottom + viewRect.y);

        textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

        Font f = c.getFont();
        g.setFont(f);

        // layout the text and icon
        String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(), b
                .getIcon(), b.getVerticalAlignment(), b
                .getHorizontalAlignment(), b.getVerticalTextPosition(), b
                .getHorizontalTextPosition(), viewRect, iconRect, textRect, b
                .getText() == null ? 0 : b.getIconTextGap());

        clearTextShiftOffset();

        // perform UI specific press action, e.g. Windows L&F shifts text
        if (model.isArmed() && model.isPressed()) {
            paintButtonPressed(g, b);
        }

        // Paint the Icon
        if (b.getIcon() != null) {
            paintIcon(g, c, iconRect);
        }

//        Composite oldComposite = ((Graphics2D) g).getComposite();
//
//        if (model.isRollover()) {
//            ((Graphics2D) g).setComposite(AlphaComposite.getInstance(
//                    AlphaComposite.SRC_OVER, 0.5f));
//        }

        if (text != null && !text.equals("")) {
            View v = (View) c.getClientProperty(BasicHTML.propertyKey);
            if (v != null) {
                paintHTMLText(g, b, textRect, text, v);
            } else {
                paintText(g, b, textRect, text);
            }
        }

        if (b.isFocusPainted() && b.hasFocus()) {
            // paint UI specific focus
            paintFocus(g, b, viewRect, textRect, iconRect);
        }

//        ((Graphics2D) g).setComposite(oldComposite);
    }

    /**
     * Method which renders the text of the current button if html.
     * <p>
     * @param g Graphics context
     * @param b Current button to render
     * @param textRect Bounding rectangle to render the text.
     * @param text String to render
     * @param v the View to use.
     */
    protected void paintHTMLText(Graphics g, AbstractButton b, 
            Rectangle textRect, String text, View v) {
        textRect.x += getTextShiftOffset();
        textRect.y += getTextShiftOffset();
        if (b.getModel().isRollover()) {
            // fix #441-swingx - underline not painted for html
            if (text.length() > 7) {
                text = text.trim();
                text = "<html><u>" + text.substring(6, text.length() - 7) + "</u></html>";
                View tmp = BasicHTML.createHTMLView(b, text);
                tmp.paint(g, textRect);
            } else {
                // whatever this is, it's not html!
                paintUnderline(g, b, textRect, text);
            }
         } else {
             v.paint(g, textRect);
         }
        textRect.x -= getTextShiftOffset();
        textRect.y -= getTextShiftOffset();
    }

    /**
     * {@inheritDoc} <p>
     * Overridden to paint the underline on rollover.
     */
    @Override
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect,
            String text) {
        super.paintText(g, b, textRect, text);
        if (b.getModel().isRollover()) {
            paintUnderline(g, b, textRect, text);
        }
    }

    private void paintUnderline(Graphics g, AbstractButton b, Rectangle rect,
            String text) {
        // JW: copied from JXTable.LinkRenderer
        FontMetrics fm = g.getFontMetrics();
        int descent = fm.getDescent();

        // REMIND(aim): should we be basing the underline on
        // the font's baseline instead of the text bounds?
        g.drawLine(rect.x + getTextShiftOffset(),
          (rect.y + rect.height) - descent + 1 + getTextShiftOffset(),
          rect.x + rect.width + getTextShiftOffset(),
          (rect.y + rect.height) - descent + 1 + getTextShiftOffset());
    }

    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
            Rectangle textRect, Rectangle iconRect) {
        if (b.getParent() instanceof JToolBar) {
            // Windows doesn't draw the focus rect for buttons in a toolbar.
            return;
        }

        // focus painted same color as text
        int width = b.getWidth();
        int height = b.getHeight();
        g.setColor(getFocusColor());
        BasicGraphicsUtils.drawDashedRect(g, dashedRectGapX, dashedRectGapY,
                width - dashedRectGapWidth, height - dashedRectGapHeight);
    }

    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        // setTextShiftOffset();
    }

    static class HandCursor extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            e.getComponent().setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void mouseExited(MouseEvent e) {
            e.getComponent().setCursor(null);
        }
    }
}
