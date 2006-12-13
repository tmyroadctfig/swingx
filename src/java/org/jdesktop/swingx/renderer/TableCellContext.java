package org.jdesktop.swingx.renderer;

import java.awt.Color;

import javax.swing.JTable;

/**
 * Table specific cellContext.
 */
public class TableCellContext extends CellContext<JTable> {

    @Override
    public boolean isEditable() {
        return getComponent() != null ? getComponent().isCellEditable(
                getRow(), getColumn()) : false;
    }

    @Override
    protected Color getSelectionBackground() {
        return getComponent() != null ? getComponent()
                .getSelectionBackground() : null;
    }

    @Override
    protected Color getSelectionForeground() {
        return getComponent() != null ? getComponent()
                .getSelectionForeground() : null;
    }

    @Override
    protected String getUIPrefix() {
        return "Table.";
    }

}