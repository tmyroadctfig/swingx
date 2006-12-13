package org.jdesktop.swingx.renderer;

import java.awt.Color;

import javax.swing.JList;

/**
 * Table specific cellContext.
 */
public class ListCellContext extends CellContext<JList> {


    @Override
    protected Color getSelectionBackground() {
        return getComponent() != null ? getComponent().getSelectionBackground() : null;
    }

    @Override
    protected Color getSelectionForeground() {
        return getComponent() != null ? getComponent().getSelectionForeground() : null;
    }

    @Override
    protected String getUIPrefix() {
        return "List.";
    }
    
    
    
}