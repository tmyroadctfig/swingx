/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.table.ColumnHeaderRenderer;

/**
 * @author Jeanette Winzenburg
 */
public class JXTableHeader extends JTableHeader {

    public JXTableHeader() {
        super();
    }

    public JXTableHeader(TableColumnModel columnModel) {
        super(columnModel);
    }

    public void setTable(JTable table) {
        super.setTable(table);
//        setColumnModel(table.getColumnModel());
        // the additional listening option makes sense only if the table
        // actually is a JXTable
        if (getXTable() != null) {
            installHeaderListener();
        } else {
            uninstallHeaderListener();
        }
    }

    public JXTable getXTable() {
        if (!(getTable() instanceof JXTable))
            return null;
        return (JXTable) getTable();
    }

    /**
     * returns the (visible) view index for the given column
     * or -1 if not visible or not contained in this header's
     * columnModel.
     * 
     * 
     * @param aColumn
     * @return
     */
    private int getViewIndexForColumn(TableColumn aColumn) {
        if (aColumn == null)
            return -1;
        TableColumnModel cm = getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }

    protected TableCellRenderer createDefaultRenderer() {
        // return super.createDefaultRenderer();
        return ColumnHeaderRenderer.createColumnHeaderRenderer();
    }

    protected void installHeaderListener() {
        if (headerListener == null) {
            headerListener = new HeaderListener();
            addMouseListener(headerListener);
            addMouseMotionListener(headerListener);

        }
    }

    protected void uninstallHeaderListener() {
        if (headerListener != null) {
            removeMouseListener(headerListener);
            removeMouseMotionListener(headerListener);
            headerListener = null;
        }
    }

    private MouseInputListener headerListener;

    private class HeaderListener implements MouseInputListener {
        private TableColumn cachedResizingColumn;

        public void mouseClicked(MouseEvent e) {
            if (shouldIgnore(e)) {
                return;
            }
            if (isInResizeRegion(e)) {
                doResize(e);
            } else {
                doSort(e);
            }
            // uncacheResizingColumn();
        }

        private boolean shouldIgnore(MouseEvent e) {
            return !SwingUtilities.isLeftMouseButton(e);
            // && table.isEnabled());
        }

        private void doSort(MouseEvent e) {
            JXTable table = getXTable();
            if (!table.isSortable() || (e.getClickCount() != 1))
                return;
            if ((e.getModifiersEx() & e.SHIFT_DOWN_MASK) == e.SHIFT_DOWN_MASK) {
                table.resetSorter();
            } else {
                int column = columnAtPoint(e.getPoint());
                if (column >= 0) {
                    table.setSorter(column);
                }
                uncacheResizingColumn();
                // header.repaint();
            }

        }

        private void doResize(MouseEvent e) {
            if (e.getClickCount() != 2)
                return;
            // int column = header.columnAtPoint(e.getPoint());
            int column = getViewIndexForColumn(cachedResizingColumn);
            System.out.println(column);
            if (column >= 0) {
                (getXTable()).packColumn(column, 5);
            }
            uncacheResizingColumn();

        }


        public void mouseReleased(MouseEvent e) {
            cacheResizingColumn(e);
        }

        public void mousePressed(MouseEvent e) {
            cacheResizingColumn(e);
        }

        private void cacheResizingColumn(MouseEvent e) {
            if (e.getClickCount() != 1)
                return;
            TableColumn column = getResizingColumn();
            if (column != null) {
                cachedResizingColumn = column;
            }
        }

        private void uncacheResizingColumn() {
            cachedResizingColumn = null;
        }

        private boolean isInResizeRegion(MouseEvent e) {
            // JTableHeader header = (JTableHeader) e.getSource();
            // // JW: kind of a hack - there's no indication in the
            // // JTableHeader api to find if we are in the resizing
            // // region before actually receiving a click
            // // checked the header.resizingColumn should be set on
            // // first click?
            // // doesn't work probably because this listener is messaged before
            // // ui-delegate listener
            // // return header.getResizingColumn() != null;
            // Cursor cursor = header.getCursor();
            // boolean inResize = cursor != null ?
            // (cursor.getType() == Cursor.E_RESIZE_CURSOR || cursor.getType()
            // == Cursor.W_RESIZE_CURSOR ) :
            // false;
            return cachedResizingColumn != null; // inResize;
            // return inResize;
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
            uncacheResizingColumn();
        }

        public void mouseDragged(MouseEvent e) {
            uncacheResizingColumn();
        }

        public void mouseMoved(MouseEvent e) {
            // uncacheResizingColumn();

        }
    }

}
