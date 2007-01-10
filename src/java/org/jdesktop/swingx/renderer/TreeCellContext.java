package org.jdesktop.swingx.renderer;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;

/**
 * Table specific cellContext.
 */
public class TreeCellContext extends CellContext<JTree> {
    Icon leafIcon;
    Icon closedIcon;
    Icon openIcon;

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
}