/*
 * Created on 23.05.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;

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
//                System.out.println("rollover - old/new:  " + evt.getOldValue() + "/" + evt.getNewValue());
                rollover((JList) evt.getSource(), (Point) evt.getOldValue(),
                        (Point) evt.getOldValue());
            }
        } else if (RolloverProducer.CLICKED_KEY.equals(evt.getPropertyName())) {
            if (evt.getSource() instanceof JList) {
                click((JList) evt.getSource(), (Point) evt.getOldValue(),
                        (Point) evt.getNewValue());
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
        if (location == null || location.x < 0) return false;
        return (table.getColumnClass(location.x) == Link.class);
    }
    
    private void rollover(JList list, Point oldLocation, Point newLocation) {
        setLinkCursor(list, newLocation);
        // JW: partial repaints incomplete
        list.repaint();
    }

    private void click(JList list, Point oldLocation, Point newLocation) {
        if (!isLinkElement(list, newLocation)) return;
        ListCellRenderer renderer = list.getCellRenderer();
        Component comp = renderer.getListCellRendererComponent(list, list.getModel().getElementAt(newLocation.y), newLocation.y, false, true);
        if (comp instanceof AbstractButton) {
            // this is fishy - needs to be removed as soon as JList is editable
            ((AbstractButton) comp).doClick();
            list.repaint();
        }
    }
    
    /**
     * something weird: cursor in JList behaves different from JTable?
     * @param list
     * @param location
     */
    private void setLinkCursor(JList list, Point location) {
        if (isLinkElement(list, location)) {
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
    private boolean isLinkElement(JList list, Point location) {
        if (location == null || location.y < 0) return false;
        return (list.getModel().getElementAt(location.y) instanceof Link);
    }
}
