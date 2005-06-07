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
            if (!(e.getSource() instanceof JTable)) return;
            JTable table = (JTable) e.getSource();
            int col = table.columnAtPoint(e.getPoint());
            int row = table.rowAtPoint(e.getPoint());
            if ((col < 0) || (row < 0)) {
                return;
            }
            table.putClientProperty(CLICKED_KEY, new Point(col, row));
            
        }

        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        public void mouseEntered(MouseEvent e) {
            if (e.getSource() instanceof JTable) {
                updateTableRolloverEntered((JTable) e.getSource(), e);
            } else if (e.getSource() instanceof JList) {
                updateListRolloverEntered((JList) e.getSource(), e);
            }
            
        }


        public void mouseExited(MouseEvent e) {
            if (e.getSource() instanceof JComponent) {
                ((JComponent) e.getSource()).putClientProperty(ROLLOVER_KEY, null);
            }
            
        }

//---------------- MouseMotionListener
        public void mouseDragged(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        public void mouseMoved(MouseEvent e) {
            if (e.getSource() instanceof JTable) {
                updateTableRollover((JTable) e.getSource(), e);
            } else if (e.getSource() instanceof JList) {
                updateListRollover((JList) e.getSource(), e);
            }
        }

        private void updateListRollover(JList list, MouseEvent e) {
            Point p = (Point) list.getClientProperty(ROLLOVER_KEY);
            int row = list.locationToIndex(e.getPoint());
            if (row >= 0) {
                Rectangle cellBounds = list.getCellBounds(row, row);
                if (!cellBounds.contains(e.getPoint())) {
                    row = -1;
                }
            }
            int col = row < 0 ? -1 : 0;
          //  System.out.println("moved - old/new: " + p + "/" + new Point(col, row));
            if ((col != p.x) || (row != p.y)) {
                list.putClientProperty(ROLLOVER_KEY, new Point(col, row));
            }
            
        }

        private void updateListRolloverEntered(JList list, MouseEvent e) {
            int row = list.locationToIndex(e.getPoint());
            if (row >= 0) {
                Rectangle cellBounds = list.getCellBounds(row, row);
                if (!cellBounds.contains(e.getPoint())) {
                    row = -1;
                }
            }
            int col = row < 0 ? -1 : 0;
        //    System.out.println("entered - old/new: " + list.getClientProperty(ROLLOVER_KEY) + "/" + new Point(col, row));
            list.putClientProperty(ROLLOVER_KEY, new Point(col, row));
            
        }
        private void updateTableRollover(JTable table, MouseEvent e) {
            Point p = (Point) table.getClientProperty(ROLLOVER_KEY);
            int col = table.columnAtPoint(e.getPoint());
            int row = table.rowAtPoint(e.getPoint());
            if ((col < 0) || (row < 0)) {
                row = -1;
                col = -1;
            }
            if ((col != p.x) || (row != p.y)) {
                table.putClientProperty(ROLLOVER_KEY, new Point(col, row));
            }
        }
        
        private void updateTableRolloverEntered(JTable table, MouseEvent e) {
            int col = table.columnAtPoint(e.getPoint());
            int row = table.rowAtPoint(e.getPoint());
            if ((col < 0) || (row < 0)) {
                row = -1;
                col = -1;
            }
            table.putClientProperty(ROLLOVER_KEY, new Point(col, row));
        }
        
    }