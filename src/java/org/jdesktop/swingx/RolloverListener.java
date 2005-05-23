/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JTable;

/**
 * Mouse/Motion/Listener which stores mouse location as 
 * client property in the target JComponent.
 * 
 * @author Jeanette Winzenburg
 */
public class RolloverListener implements MouseListener, MouseMotionListener {

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
            if (!(e.getSource() instanceof JTable)) return;
            JTable table = (JTable) e.getSource();
            int col = table.columnAtPoint(e.getPoint());
            int row = table.rowAtPoint(e.getPoint());
            if ((col < 0) || (row < 0)) {
                row = -1;
                col = -1;
            }
            table.putClientProperty(ROLLOVER_KEY, new Point(col, row));
            
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
            if (!(e.getSource() instanceof JTable)) return;
            JTable table = (JTable) e.getSource();
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
        
    }