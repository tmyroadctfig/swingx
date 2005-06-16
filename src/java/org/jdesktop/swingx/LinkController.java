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
import javax.swing.ListCellRenderer;


/**
 * listens to rollover properties. 
 * Repaints effected component regions.
 * Updates link cursor.
 * 
 * @author Jeanette Winzenburg
 */
public  class LinkController implements PropertyChangeListener {


    /*
     *  @TODO factor per component type
     */

    private Cursor oldCursor;
    public void propertyChange(PropertyChangeEvent evt) {
        if (RolloverProducer.ROLLOVER_KEY.equals(evt.getPropertyName())) {
            if (evt.getSource() instanceof JXTable) {
                rollover((JXTable) evt.getSource(), (Point) evt
                        .getOldValue(), (Point) evt.getNewValue());
            } else if (evt.getSource() instanceof JXList) {
//                System.out.println("rollover - old/new:  " + evt.getOldValue() + "/" + evt.getNewValue());
                rollover((JXList) evt.getSource(), (Point) evt.getOldValue(),
                        (Point) evt.getOldValue());
            } else if (evt.getSource() instanceof JXTree) {
//              System.out.println("rollover - old/new:  " + evt.getOldValue() + "/" + evt.getNewValue());
                rollover((JXTree) evt.getSource(), (Point) evt.getOldValue(),
                        (Point) evt.getOldValue());
            }
        } else if (RolloverProducer.CLICKED_KEY.equals(evt.getPropertyName())) {
            if (evt.getSource() instanceof JXList) {
                click((JXList) evt.getSource(), (Point) evt.getOldValue(),
                        (Point) evt.getNewValue());
            }
        }
    }

//--------------------------- JTable rollover
    
    private void rollover(JXTable table, Point oldLocation, Point newLocation) {
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

    private void setLinkCursor(JXTable table, Point location) {
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
    private boolean isLinkColumn(JXTable table, Point location) {
        if (location == null || location.x < 0) return false;
        return (table.getColumnClass(location.x) == LinkModel.class);
    }

    
//--------------------------------- JList rollover
    
    private void rollover(JXList list, Point oldLocation, Point newLocation) {
        setLinkCursor(list, newLocation);
        // JW: partial repaints incomplete
        list.repaint();
    }

    private void click(JXList list, Point oldLocation, Point newLocation) {
        if (!isLinkElement(list, newLocation)) return;
        ListCellRenderer renderer = list.getCellRenderer();
        // JW: PENDING - use componentAdapter to get value!
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
    private void setLinkCursor(JXList list, Point location) {
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
    private boolean isLinkElement(JXList list, Point location) {
        if (location == null || location.y < 0) return false;
        return (list.getModel().getElementAt(location.y) instanceof LinkModel);
    }
    
//-------------------------------------JTree rollover
    
    private void rollover(JXTree tree, Point oldLocation, Point newLocation) {
        //setLinkCursor(list, newLocation);
        // JW: conditional repaint not working?
        tree.repaint();
//        if (oldLocation != null) {
//            Rectangle r = tree.getRowBounds(oldLocation.y);
////            r.x = 0;
////            r.width = table.getWidth();
//            if (r != null)
//            tree.repaint(r);
//        }
//        if (newLocation != null) {
//            Rectangle r = tree.getRowBounds(newLocation.y);
////            r.x = 0;
////            r.width = table.getWidth();
//            if (r != null)
//            tree.repaint(r);
//        }
    }

}
