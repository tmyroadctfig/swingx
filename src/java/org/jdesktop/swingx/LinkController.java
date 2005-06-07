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

import javax.swing.JList;
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
            } else if (evt.getSource() instanceof JList) {
                System.out.println("rollover - old/new:  " + evt.getOldValue() + "/" + evt.getNewValue());
                rollover((JList) evt.getSource(), (Point) evt.getOldValue(),
                        (Point) evt.getOldValue());
            }
        } else if (RolloverProducer.CLICKED_KEY.equals(evt.getPropertyName())) {
//            if (evt.getSource() instanceof JTable) {
//                click((JTable) evt.getSource(), (Point) evt.getOldValue(),
//                        (Point) evt.getNewValue());
//            }
        }
    }

    // not used: JTable link clicking happens through the editor
//    private void click(JTable table, Point oldLocation, Point newLocation) {
//        if (isLinkColumn(table, newLocation)) {
//            Link link = (Link) table.getValueAt(newLocation.y, newLocation.x);
//            if (link != null) {
//                // ARRRGGHH...
//                link.setVisited(true);
//                table.setValueAt(link, newLocation.y, newLocation.x);
//            }
//        }
//        
//    }

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
    
    private void rollover(JList list, Point oldLocation, Point newLocation) {
        if (oldLocation != null) {
            Rectangle r = list.getCellBounds(oldLocation.y, oldLocation.y);
//            r.x = 0;
//            r.width = table.getWidth();
            if (r != null) {
                list.repaint(r);
            }
        }
        if (newLocation != null) {
            Rectangle r = list.getCellBounds(newLocation.y, newLocation.y);
//            r.x = 0;
//            r.width = table.getWidth();
            if (r != null) {
                list.repaint(r);
            }
        }
        setLinkCursor(list, newLocation);
        list.repaint();
    }

    private void setLinkCursor(JList list, Point location) {
        if (isLinkColumn(list, location)) {
          //  if (oldCursor == null) {
                oldCursor = list.getCursor();
                list.setCursor(Cursor
                        .getPredefinedCursor(Cursor.HAND_CURSOR));
           //     System.out.println("set link cursor old/new: " + oldCursor + "/" + list.getCursor());
           // }
        } else {
          //  if (oldCursor != null) {
            //    System.out.println("set link cursor old/new: " + oldCursor + "/" + list.getCursor());
                list.setCursor(oldCursor);
                oldCursor = null;
           // }
        }

    }
    private boolean isLinkColumn(JList list, Point location) {
        // JW: Quickfix - the index might be -1 if 
        // hitting outside of the columns
        if (location == null || location.y < 0) return false;
        return (list.getModel().getElementAt(location.y) instanceof Link);
    }
}
