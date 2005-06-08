/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;

/**
 * Mouse/Motion/Listener which stores mouse location as 
 * client property in the target JComponent.
 * 
 * @author Jeanette Winzenburg
 */
public class RolloverProducer implements MouseListener, MouseMotionListener {

//----------------- mouseListener
        
        public static final String CLICKED_KEY = "swingx.clicked";
        public static final String ROLLOVER_KEY = "swingx.rollover";
        
        public void mouseClicked(MouseEvent e) {
            updateRollover(e, CLICKED_KEY);
        }

        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        public void mouseEntered(MouseEvent e) {
            updateRollover(e, ROLLOVER_KEY);
        }


        public void mouseExited(MouseEvent e) {
            if (e.getSource() instanceof JComponent) {
                ((JComponent) e.getSource()).putClientProperty(ROLLOVER_KEY, null);
                ((JComponent) e.getSource()).putClientProperty(CLICKED_KEY, null);
            }
            
        }

//---------------- MouseMotionListener
        public void mouseDragged(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        public void mouseMoved(MouseEvent e) {
            updateRollover(e, ROLLOVER_KEY);
        }

        private void updateRollover(MouseEvent e, String property) {
            if (e.getSource() instanceof JTable) {
                updateRolloverPoint((JTable) e.getSource(), e.getPoint());
            } else if (e.getSource() instanceof JList) {
                updateRolloverPoint((JList) e.getSource(), e.getPoint());
            } else {
                return;
            }
            updateClientProperty((JComponent) e.getSource(), property);
        }

        Point rollover = new Point();
        
        private void updateClientProperty(JComponent component, String property) {
            Point p = (Point) component.getClientProperty(property);
            if (p == null || (rollover.x != p.x) || (rollover.y != p.y)) {
                component.putClientProperty(property, new Point(rollover));
            }
        }

        private void updateRolloverPoint(JList list, Point mousePoint) {
            int row = list.locationToIndex(mousePoint);
            if (row >= 0) {
                Rectangle cellBounds = list.getCellBounds(row, row);
                if (!cellBounds.contains(mousePoint)) {
                    row = -1;
                }
            }
            int col = row < 0 ? -1 : 0;
            rollover.x = col;
            rollover.y = row;
        }

        private void updateRolloverPoint(JTable table, Point mousePoint) {
            int col = table.columnAtPoint(mousePoint);
            int row = table.rowAtPoint(mousePoint);
            if ((col < 0) || (row < 0)) {
                row = -1;
                col = -1;
            }
            rollover.x = col;
            rollover.y = row;
        }
        
        
    }