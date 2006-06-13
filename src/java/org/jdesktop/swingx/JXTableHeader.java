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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.event.TableColumnModelExtListener;
import org.jdesktop.swingx.table.ColumnHeaderRenderer;
import org.jdesktop.swingx.table.TableColumnExt;

/**
 * TableHeader with extended functionality if associated Table is of
 * type JXTable.<p>
 * 
 * The enhancements:
 * <ul>
 * <li> supports pluggable handler to control user interaction for sorting
 * <li> uses ColumnHeaderRenderer which can show the sort icon
 * <li> triggers column pack (== auto-resize to exactly fit the contents)
 *  on double-click in resize region.
 * </ul>
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class JXTableHeader extends JTableHeader 
    implements TableColumnModelExtListener {

    private SortGestureRecognizer sortGestureRecognizer;

    public JXTableHeader() {
        super();
    }

    public JXTableHeader(TableColumnModel columnModel) {
        super(columnModel);
    }

    /**
     * Sets the associated JTable. Enables enhanced header
     * features if table is of type JXTable.<p>
     * 
     * PENDING: who is responsible for synching the columnModel?
     */
    @Override
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

    /**
     * Implementing TableColumnModelExt: listening to column property changes.
     * @param event change notification from a contained TableColumn.
     */
    public void columnPropertyChanged(PropertyChangeEvent event) {
       repaint(); 
    }
    /**
     * overridden to respect the column tooltip, if available. 
     * 
     * @return the column tooltip of the column at the mouse position 
     *   if not null or super if not available.
     */
    @Override
    public String getToolTipText(MouseEvent event) {
        String columnToolTipText = getColumnToolTipText(event);
        return columnToolTipText != null ? columnToolTipText : super.getToolTipText(event);
    }

    /**
     * 
     * @param event the mouseEvent representing the mouse location.
     * @return the column tooltip of the column below the mouse location,
     *   or null if not available.
     */
    protected String getColumnToolTipText(MouseEvent event) {
        if (getXTable() == null) return null;
        int column = columnAtPoint(event.getPoint());
        if (column < 0) return null;
        TableColumnExt columnExt = getXTable().getColumnExt(column);
        return columnExt != null ? columnExt.getToolTipText() : null;
    }
    
    public JXTable getXTable() {
        if (!(getTable() instanceof JXTable))
            return null;
        return (JXTable) getTable();
    }

    /**
     * Overridden to adjust for a minimum height as returned by
     * #getMinimumHeight.
     * 
     * @inheritDoc
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension pref = super.getPreferredSize();
        pref.height = getMinimumHeight(pref.height);
        return pref;
    }
    
    /**
     * Allows to enforce a minimum heigth in the 
     * getXXSize methods.
     * 
     * Here: jumps in if the table's columnControl is visible and
     *   the input height is 0  - this happens if all
     *   columns are hidden - and configures the default
     *   header renderer with a dummy value for measuring.
     * 
     * @param height the prefHeigth as calcualated by super.
     * @return a minimum height for the preferredSize.
     */
    protected int getMinimumHeight(int height) {
        if ((height == 0) && (getXTable() != null) 
                && getXTable().isColumnControlVisible()){
            TableCellRenderer renderer = getDefaultRenderer();
            Component comp = renderer.getTableCellRendererComponent(getTable(), 
                        "dummy", false, false, -1, -1);
            height = comp.getPreferredSize().height;
        }
        return height;
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
        return ColumnHeaderRenderer.createColumnHeaderRenderer();
    }

    /**
     * Lazily creates and returns the SortGestureRecognizer.
     * 
     * @return the SortGestureRecognizer used in Headerlistener.
     */
    public SortGestureRecognizer getSortGestureRecognizer() {
        if (sortGestureRecognizer == null) {
            sortGestureRecognizer = createSortGestureRecognizer();
        }
        return sortGestureRecognizer;
        
    }
    
    /**
     * Set the SortGestureRecognizer for use in the HeaderListener.
     * 
     * @param recognizer the recognizer to use in HeaderListener.
     */
    public void setSortGestureRecognizer(SortGestureRecognizer recognizer) {
        this.sortGestureRecognizer = recognizer;
    }
    
    /**
     * creates and returns the default SortGestureRecognizer.
     * @return the SortGestureRecognizer used in Headerlistener.
     * 
     */
    protected SortGestureRecognizer createSortGestureRecognizer() {
        return new SortGestureRecognizer();
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
        }

        private boolean shouldIgnore(MouseEvent e) {
            return !SwingUtilities.isLeftMouseButton(e)
              || !table.isEnabled();
        }

        private void doSort(MouseEvent e) {
            JXTable table = getXTable();
            if (!table.isSortable())
                return;
            if (getSortGestureRecognizer().isResetSortOrderGesture(e)) {
                table.resetSortOrder();
                repaint();
            } else if (getSortGestureRecognizer().isToggleSortOrderGesture(e)){
                int column = columnAtPoint(e.getPoint());
                if (column >= 0) {
                    table.toggleSortOrder(column);
                }
                uncacheResizingColumn();
                repaint();
            }

        }

        private void doResize(MouseEvent e) {
            if (e.getClickCount() != 2)
                return;
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
            if (!getSortGestureRecognizer().isSortOrderGesture(e))
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
            return cachedResizingColumn != null; // inResize;
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
        }
    }

    /**
     * Encapsulates decision about which MouseEvents should
     * trigger sort/unsort events.
     * 
     * Here: a single left click for toggling sort order, a
     * single SHIFT-left click for unsorting.
     * 
     */
    public static class SortGestureRecognizer {
        public boolean isResetSortOrderGesture(MouseEvent e) {
            return isSortOrderGesture(e) && isResetModifier(e);
        }

        protected boolean isResetModifier(MouseEvent e) {
            return ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK);
        }
        
        public boolean isToggleSortOrderGesture(MouseEvent e) {
            return isSortOrderGesture(e) && !isResetModifier(e);
        }
        
        public boolean isSortOrderGesture(MouseEvent e) {
            return e.getClickCount() == 1;
        }
    }


}
