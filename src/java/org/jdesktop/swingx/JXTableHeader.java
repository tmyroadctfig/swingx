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
package org.jdesktop.swingx;

import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.table.ColumnHeaderRenderer;

/**
 * TableHeader with extended functionality if associated Table is of
 * type JXTable.<p>
 * 
 * The enhancements:
 * <ul>
 * <li> toggles sort of column on mouseClicked if table isSortable
 * <li> uses ColumnHeaderRenderer which can show the sort icon
 * <li> triggers column pack (== auto-resize to exactly fit the contents)
 *  on double-click in resize region.
 * </ul>
 * 
 * @author Jeanette Winzenburg
 */
public class JXTableHeader extends JTableHeader {

    public JXTableHeader() {
        super();
    }

    public JXTableHeader(TableColumnModel columnModel) {
        super(columnModel);
    }

    /**
     * Sets the associated JTable. Enables enhanced header
     * features if table is of type JXTable.
     */
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

    
    public void updateUI() {
        super.updateUI();
        if (getDefaultRenderer() instanceof JComponent) {
            ((JComponent) getDefaultRenderer()).updateUI();
         
        }
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
            return !SwingUtilities.isLeftMouseButton(e)
              || !table.isEnabled();
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
            }
            repaint();

        }

        private void doResize(MouseEvent e) {
            if (e.getClickCount() != 2)
                return;
            // int column = header.columnAtPoint(e.getPoint());
            int column = getViewIndexForColumn(cachedResizingColumn);
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
