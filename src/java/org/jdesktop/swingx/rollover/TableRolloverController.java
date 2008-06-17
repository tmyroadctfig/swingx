package org.jdesktop.swingx.rollover;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


/**
     * listens to rollover properties. 
     * Repaints effected component regions.
     * Updates link cursor.
     * 
     * @author Jeanette Winzenburg
     */
    public class TableRolloverController<T extends JTable>  extends RolloverController<T> {

        private Cursor oldCursor;

//    --------------------------- JTable rollover
        
        @Override
        protected void rollover(Point oldLocation, Point newLocation) {
            if (oldLocation != null) {
                Rectangle r = component.getCellRect(oldLocation.y, oldLocation.x, false);
                r.x = 0;
                r.width = component.getWidth();
                component.repaint(r);
            }
            if (newLocation != null) {
                Rectangle r = component.getCellRect(newLocation.y, newLocation.x, false);
                r.x = 0;
                r.width = component.getWidth();
                component.repaint(r);
            }
            setRolloverCursor(newLocation);
        }

        /**
         * overridden to return false if cell editable.
         */
        @Override
        protected boolean isClickable(Point location) {
            return super.isClickable(location) && !component.isCellEditable(location.y, location.x);
        }

        @Override
        protected RolloverRenderer getRolloverRenderer(Point location, boolean prepare) {
            TableCellRenderer renderer = component.getCellRenderer(location.y, location.x);
            RolloverRenderer rollover = renderer instanceof RolloverRenderer ?
                    (RolloverRenderer) renderer : null;
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
            int leadRow = component.getSelectionModel()
                    .getLeadSelectionIndex();
            int leadColumn = component.getColumnModel().getSelectionModel()
                    .getLeadSelectionIndex();
            return new Point(leadColumn, leadRow);
        }

    }