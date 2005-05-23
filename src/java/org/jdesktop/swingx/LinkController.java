/*
 * Created on 23.05.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;

import org.jdesktop.swingx.util.Link;

/**
 * listens to rollover properties. 
 * Repaints effected component regions.
 * Updates link cursor.
 * 
 * @author Jeanette Winzenburg
 */
public  class LinkController implements PropertyChangeListener {

    private Cursor oldCursor;
    public void propertyChange(PropertyChangeEvent evt) {
        if (RolloverProducer.ROLLOVER_KEY.equals(evt.getPropertyName())) {
            if (evt.getSource() instanceof JTable) {
                rollover((JTable) evt.getSource(), (Point) evt
                        .getOldValue(), (Point) evt.getNewValue());
            }
        } else if (RolloverProducer.CLICKED_KEY.equals(evt.getPropertyName())) {
//            if (evt.getSource() instanceof JTable) {
//                click((JTable) evt.getSource(), (Point) evt.getOldValue(),
//                        (Point) evt.getNewValue());
//            }
        }
    }

    private void click(JTable table, Point oldLocation, Point newLocation) {
        if (isLinkColumn(table, newLocation)) {
            Link link = (Link) table.getValueAt(newLocation.y, newLocation.x);
            if (link != null) {
                // ARRRGGHH...
                link.setVisited(true);
                table.setValueAt(link, newLocation.y, newLocation.x);
            }
        }
        
    }

    private void rollover(JTable table, Point oldLocation, Point newLocation) {
        if (oldLocation != null) {
            Rectangle r = table.getCellRect(oldLocation.y, oldLocation.x, false);
            r.x = 0;
            r.width = table.getWidth();
            table.repaint(r);
        }
        if (newLocation != null) {
            Rectangle r = table.getCellRect(newLocation.y, newLocation.x, false);
            r.x = 0;
            r.width = table.getWidth();
            table.repaint(r);
        }
        setLinkCursor(table, newLocation);
//        table.repaint();
    }

    private void setLinkCursor(JTable table, Point location) {
        if (isLinkColumn(table, location)) {
            if (oldCursor == null) {
                oldCursor = table.getCursor();
                table.setCursor(Cursor
                        .getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        } else {
            if (oldCursor != null) {
                table.setCursor(oldCursor);
                oldCursor = null;
            }
        }

    }
    private boolean isLinkColumn(JTable table, Point location) {
        // JW: Quickfix - the index might be -1 if 
        // hitting outside of the columns
        if (location == null || location.x < 0) return false;
        return (table.getColumnClass(location.x) == Link.class);
    }
    
}
