package org.jdesktop.swingx.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * Tree specific cellContext.
 */
public class TreeCellContext extends CellContext<JTree> {
    Icon leafIcon;
    Icon closedIcon;
    Icon openIcon;
    private Border treeFocusBorder;

    @Override
    public boolean isEditable() {
        return false;
//        return getComponent() != null ? getComponent().isCellEditable(
//                getRow(), getColumn()) : false;
    }

    
//    @Override
//    protected Color getBackground() {
//        return UIManager.getColor("Tree.textBackground");
//    }
//
//
//    @Override
//    protected Color getForeground() {
//        return UIManager.getColor("Tree.textForeground");
//    }


    @Override
    protected Color getSelectionBackground() {
        return UIManager.getColor("Tree.selectionBackground");
    }

    @Override
    protected Color getSelectionForeground() {
        return UIManager.getColor("Tree.selectionForeground");
    }

    @Override
    protected String getUIPrefix() {
        return "Tree.";
    }
    
    protected Icon getLeafIcon() {
        return leafIcon != null ? leafIcon : UIManager.getIcon(getUIKey("leafIcon"));
    }

    protected Icon getOpenIcon() {
        return openIcon != null ? openIcon : UIManager.getIcon(getUIKey("openIcon"));
    }
    
    protected Icon getClosedIcon() {
        return closedIcon != null ? closedIcon : UIManager.getIcon(getUIKey("closedIcon"));
    }
    
    public Icon getIcon() {
        if (isLeaf()) {
            return getLeafIcon();
        }
        if (isExpanded()) {
            return getOpenIcon();
        }
        return getClosedIcon();
    }


    @Override
    protected Border getFocusBorder() {
        if (treeFocusBorder == null) {
            treeFocusBorder = new TreeFocusBorder();
        }
        return treeFocusBorder;
    }
 
    public class TreeFocusBorder extends LineBorder {

        private Color treeBackground;
        private Color focusColor;

        /**
         * 
         * @param color
         */
        public TreeFocusBorder() {
            super(Color.BLACK);
            treeBackground = getBackground();
            focusColor = new Color(~treeBackground.getRGB());
         }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color color = UIManager.getColor("Tree.selectionBorderColor");
            if (color != null) {
                lineColor = color;
            }
            if (isDashed()) {
                if (treeBackground != c.getBackground()) {
                    treeBackground = c.getBackground();
                    focusColor = new Color(~treeBackground.getRGB());
                }
                
                Color old = g.getColor();
                g.setColor(focusColor);
                BasicGraphicsUtils.drawDashedRect(g, x, y, width, height);
                g.setColor(old);

            } else {
              super.paintBorder(c, g, x, y, width, height);
            }
            
        }

        /**
         * @return
         */
        private boolean isDashed() {
            return Boolean.TRUE.equals(UIManager.get("Tree.drawDashedFocusIndicator"));

        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
        
        
        
    }
    
}