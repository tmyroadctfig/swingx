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
 * <ul>
 * <li>PENDING: setters for icons?
 * <li>PENDING: use focus border as returned from list or table instead of
 * rolling its own? The missing ui-border probably is a consequence of the
 * border hacking as implemented in core default renderer. SwingX has a
 * composite default which should use the "normal" border.
 * </ul>
 */
public class TreeCellContext extends CellContext<JTree> {
    protected Icon leafIcon;

    protected Icon closedIcon;

    protected Icon openIcon;

    private Border treeFocusBorder;

    /**
     * {@inheritDoc}
     * <p>
     * PENDING: implement to return the tree cell editability!
     */
    @Override
    public boolean isEditable() {
        return false;
        // return getComponent() != null ? getComponent().isCellEditable(
        // getRow(), getColumn()) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Color getSelectionBackground() {
        return UIManager.getColor("Tree.selectionBackground");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Color getSelectionForeground() {
        return UIManager.getColor("Tree.selectionForeground");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUIPrefix() {
        return "Tree.";
    }

    /**
     * Returns the default icon to use for leaf cell.
     * 
     * @return the icon to use for leaf cell.
     */
    protected Icon getLeafIcon() {
        return leafIcon != null ? leafIcon : UIManager
                .getIcon(getUIKey("leafIcon"));
    }

    /**
     * Returns the default icon to use for open cell.
     * 
     * @return the icon to use for open cell.
     */
    protected Icon getOpenIcon() {
        return openIcon != null ? openIcon : UIManager
                .getIcon(getUIKey("openIcon"));
    }

    /**
     * Returns the default icon to use for closed cell.
     * 
     * @return the icon to use for closed cell.
     */
    protected Icon getClosedIcon() {
        return closedIcon != null ? closedIcon : UIManager
                .getIcon(getUIKey("closedIcon"));
    }

    /**
     * {@inheritDoc}
     * <p>
     * 
     * Overridden to return a default depending for the leaf/open cell state.
     */
    @Override
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
        public void paintBorder(Component c, Graphics g, int x, int y,
                int width, int height) {
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
            return Boolean.TRUE.equals(UIManager
                    .get("Tree.drawDashedFocusIndicator"));

        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

    }

}