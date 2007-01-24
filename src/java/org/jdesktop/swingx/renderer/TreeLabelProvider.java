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
 *
 */
package org.jdesktop.swingx.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * A LabelProvider specialized on rendering in JTree.  
 * This is no longer used, replaced by <code>WrappingProvider</code>. Will be
 * removed as soon as the replacement is fully functional.
 * 
 * @author Jeanette Winzenburg
 * 
 * @deprecated use WrappingProvider instead.
 * 
 * @see WrappingProvider
 */
public class TreeLabelProvider extends LabelProvider {

    boolean selected;
    boolean hasFocus;
    Color treeSelectionBackground;
    Color treeBackground;
    private boolean drawsFocusBorderAroundIcon;
    private boolean drawDashedFocusIndicator;
    private Color borderSelectionColor;
    
    public TreeLabelProvider() {
        this(null);
    }
    /**
     * @param converter
     */
    public TreeLabelProvider(StringValue converter) {
        super(converter);
        Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
        drawsFocusBorderAroundIcon = (value != null && ((Boolean)value).
                                      booleanValue());
        value = UIManager.get("Tree.drawDashedFocusIndicator");
        drawDashedFocusIndicator = (value != null && ((Boolean)value).
                                    booleanValue());
        setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));

    }
    /**
     * @param color
     */
    private void setBorderSelectionColor(Color color) {
        borderSelectionColor = color;
        
    }
    
    /**
     * {@inheritDoc} <p>
     * Overridden to copy the context's selected and focused state which are
     * needed during the paint.
     * 
     */
    @Override
    protected void configureState(CellContext context) {
        super.configureState(context);
        configureIcon(context);
        configureBorder(context);
        this.selected = context.isSelected();
        this.hasFocus = context.isFocused();
    }
    
    /**
     * @param context
     */
    protected void configureBorder(CellContext context) {
       rendererComponent.setBorder(null);
        
    }
    protected void configureIcon(CellContext context) {
        if (context.getComponent().isEnabled()) {
            rendererComponent.setIcon(getIcon(context));
        } else {
            rendererComponent.setDisabledIcon(context.getIcon());
            
        }
    }
    
    /**
     * Returns the icon to use for rendering the current tree node.
     * Here: returns the default icon as returned by the cell context.
     * 
     * @param context
     * @return
     */
    protected Icon getIcon(CellContext context) {
        return context.getIcon();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JLabel createRendererComponent() {
        return new TreeRendererLabel();
    }

    /**
     * A label specialized in rendering tree cells. 
     * 
     * Mostly c&p'ed from DefaultTreeCellRenderer.
     */
    class TreeRendererLabel extends JRendererLabel {
        private Color focusBGColor;
        
        TreeRendererLabel() {
            setOpaque(false);
        }
//        /**
//         * Subclassed to map <code>FontUIResource</code>s to null. If 
//         * <code>font</code> is null, or a <code>FontUIResource</code>, this
//         * has the effect of letting the font of the JTree show
//         * through. On the other hand, if <code>font</code> is non-null, and not
//         * a <code>FontUIResource</code>, the font becomes <code>font</code>.
//         */
//        public void setFont(Font font) {
//        if(font instanceof FontUIResource)
//            font = null;
//        super.setFont(font);
//        }


        /**
         * Paints the value.  The background is filled based on selected.
         */
       public void paint(Graphics g) {
            Color bColor = getBackground();
           
//            if(selected) {
//                bColor = getBackgroundSelectionColor();
//            } else {
//                bColor = getBackgroundNonSelectionColor();
//                if(bColor == null)
//                    bColor = getBackground();
//            }
            int imageOffset = -1;
            if(bColor != null) {

                imageOffset = getLabelStart();
                g.setColor(bColor);
                if(getComponentOrientation().isLeftToRight()) {
                    g.fillRect(imageOffset, 0, getWidth() - imageOffset,
                               getHeight());
                } else {
                    g.fillRect(0, 0, getWidth() - imageOffset,
                               getHeight());
                }
            }

            if (hasFocus) {
                if (drawsFocusBorderAroundIcon) {
                    imageOffset = 0;
                }
                else if (imageOffset == -1) {
                    imageOffset = getLabelStart();
                }
                if(getComponentOrientation().isLeftToRight()) {
                    paintFocus(g, imageOffset, 0, getWidth() - imageOffset,
                               getHeight());
                } else {
                    paintFocus(g, 0, 0, getWidth() - imageOffset, getHeight());
                }
            }
            super.paint(g);
       }

       private void paintFocus(Graphics g, int x, int y, int w, int h) {
            Color       bsColor = borderSelectionColor;

            if (bsColor != null && (selected || !drawDashedFocusIndicator)) {
                g.setColor(bsColor);
                g.drawRect(x, y, w - 1, h - 1);
            }
            if (drawDashedFocusIndicator) {
                Color color = getBackground();
//                if (selected) {
//                    color = getBackgroundSelectionColor();
//                } else {
//                    color = getBackgroundNonSelectionColor();
//                    if(color == null) {
//                        color = getBackground();
//                    }
//                }
                
                if (treeBackground != color) {
                    treeBackground = color;
                    focusBGColor = new Color(~color.getRGB());
                }
                g.setColor(focusBGColor);
                BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
            }
       }

       private int getLabelStart() {
            Icon currentI = getIcon();
            if(currentI != null && getText() != null) {
                return currentI.getIconWidth() + Math.max(0, getIconTextGap() -1);
            }
            return 0;
       }

       /**
        * Overrides <code>JComponent.getPreferredSize</code> to
        * return slightly wider preferred size value.
        */
       public Dimension getPreferredSize() {
            Dimension        retDimension = super.getPreferredSize();

            if(retDimension != null)
                retDimension = new Dimension(retDimension.width + 3,
                                             retDimension.height);
            return retDimension;
       }

    }
}
