/*
 * $Id$
 *
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.rollover;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * listens to rollover properties. Repaints effected component regions. Updates
 * link cursor.
 * 
 * @author Jeanette Winzenburg
 */
public class TableRolloverController<T extends JTable> extends
        RolloverController<T> {

    private Cursor oldCursor;

    // --------------------------- JTable rollover

    @Override
    protected void rollover(Point oldLocation, Point newLocation) {
        // check which rows are effected and need repaint
        boolean paintOldRow = paintRow(oldLocation);
        boolean paintNewRow = paintRow(newLocation);
        if (paintOldRow && paintNewRow) {
            if (oldLocation.y == newLocation.y) {
                // row unchanged, no need for repaint
                paintOldRow = false;
                paintNewRow = false;
            }
        }
        // check which columns are effected and need repaint
        boolean paintOldColumn = paintColumn(oldLocation);
        boolean paintNewColumn = paintColumn(newLocation);
        if (paintOldColumn && paintNewColumn) {
            if (oldLocation.x == newLocation.x) {
                // column unchanged, no need for repaint
                paintOldColumn = false;
                paintNewColumn = false;
            }
        }
        
        List<Rectangle> rectangles = paintRectangles(null, oldLocation, paintOldRow, paintOldColumn);
        rectangles = paintRectangles(rectangles, newLocation, paintNewRow, paintNewColumn);
        if (rectangles != null) {
            for (Rectangle rectangle : rectangles) {
                component.repaint(rectangle);
            }
        }
//        List<Integer> columns = new ArrayList<Integer>();
//        List<Integer> rows = new ArrayList<Integer>();
//        List<Rectangle> rowRectangles = new ArrayList<Rectangle>();
//        List<Rectangle> columnRectangles = new ArrayList<Rectangle>();
//        if (oldLocation != null) {
//            Rectangle r = component.getCellRect(oldLocation.y, oldLocation.x,
//                    false);
//            if (oldLocation.y >= 0) {
//                rows.add(oldLocation.y);
//                rowRectangles.add(new Rectangle(0, r.y, component.getWidth(),
//                        r.height));
//            }
//            if (oldLocation.x >= 0) {
//                columns.add(oldLocation.x);
//                columnRectangles.add(new Rectangle(r.x, 0, r.width, component
//                        .getHeight()));
//            }
//        }
//        if (newLocation != null) {
//            Rectangle r = component.getCellRect(newLocation.y, newLocation.x,
//                    false);
//            if (newLocation.y >= 0) {
//                rows.add(newLocation.y);
//                rowRectangles.add(new Rectangle(0, r.y, component.getWidth(),
//                        r.height));
//            }
//            if (newLocation.x >= 0) {
//                columns.add(newLocation.x);
//                columnRectangles.add(new Rectangle(r.x, 0, r.width, component
//                        .getHeight()));
//            }
//        }
//        if ((rows.size() > 1) && (rows.get(0) == rows.get(1))) {
//            rowRectangles.clear();
//        }
//        if ((columns.size() > 1) && (columns.get(0) == columns.get(1))) {
//            columnRectangles.clear();
//        }
//        for (Rectangle rectangle : columnRectangles) {
//            component.repaint(rectangle);
//        }
//        for (Rectangle rectangle : rowRectangles) {
//            component.repaint(rectangle);
//        }
        setRolloverCursor(newLocation);
    }

    /**
     * @param object
     * @param oldLocation
     * @param paintRow
     * @param paintNewRow
     * @return
     */
    private List<Rectangle> paintRectangles(List<Rectangle> rectangles, Point oldLocation,
            boolean paintRow, boolean paintColumn) {
        if (!paintRow && !paintColumn) return rectangles;
        if (rectangles == null) {
            rectangles = new ArrayList<Rectangle>();
        }
        Rectangle r = component.getCellRect(oldLocation.y, oldLocation.x,
                false);
        if (paintRow) {
            rectangles.add(new Rectangle(0, r.y, component.getWidth(),
                    r.height));
        }
        if (paintColumn) {
            rectangles.add(new Rectangle(r.x, 0, r.width, component
                    .getHeight()));
        }
        return rectangles;
    }

    /**
     * @param cellLocation
     * @return
     */
    private boolean paintColumn(Point cellLocation) {
        return cellLocation != null && cellLocation.x >= 0;
    }

    /**
     * @param cellLocation
     * @return
     */
    private boolean paintRow(Point cellLocation) {
        return cellLocation != null && cellLocation.y >= 0;
    }

    /**
     * overridden to return false if cell editable.
     */
    @Override
    protected boolean isClickable(Point location) {
        return super.isClickable(location)
                && !component.isCellEditable(location.y, location.x);
    }

    @Override
    protected RolloverRenderer getRolloverRenderer(Point location,
            boolean prepare) {
        TableCellRenderer renderer = component.getCellRenderer(location.y,
                location.x);
        RolloverRenderer rollover = renderer instanceof RolloverRenderer ? (RolloverRenderer) renderer
                : null;
        if ((rollover != null) && !rollover.isEnabled()) {
            rollover = null;
        }
        if ((rollover != null) && prepare) {
            component.prepareRenderer(renderer, location.y, location.x);
        }
        return rollover;
    }

    private void setRolloverCursor(Point location) {
        if (hasRollover(location)) {
            if (oldCursor == null) {
                oldCursor = component.getCursor();
                component.setCursor(Cursor
                        .getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        } else {
            if (oldCursor != null) {
                component.setCursor(oldCursor);
                oldCursor = null;
            }
        }

    }

    @Override
    protected Point getFocusedCell() {
        int leadRow = component.getSelectionModel().getLeadSelectionIndex();
        int leadColumn = component.getColumnModel().getSelectionModel()
                .getLeadSelectionIndex();
        return new Point(leadColumn, leadRow);
    }

}