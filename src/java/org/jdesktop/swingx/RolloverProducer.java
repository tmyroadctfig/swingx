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
import javax.swing.JTree;

/**
 * Mouse/Motion/Listener which stores mouse location as 
 * client property in the target JComponent.
 * 
 * Note: assumes that the component it is listening to is 
 * of type JComponent!
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

        protected void updateRollover(MouseEvent e, String property) {
            updateRolloverPoint((JComponent) e.getComponent(), e.getPoint());
            updateClientProperty((JComponent) e.getSource(), property);
        }

        protected Point rollover = new Point(-1, -1);
        
        protected void updateClientProperty(JComponent component, String property) {
            Point p = (Point) component.getClientProperty(property);
            if (p == null || (rollover.x != p.x) || (rollover.y != p.y)) {
                component.putClientProperty(property, new Point(rollover));
            }
        }

        /**
         * Subclasses must override to map the given mouse coordinates into
         * appropriate client coordinates. The result must be stored in the 
         * rollover field. 
         * 
         * Here: does nothing.
         * 
         * @param component
         * @param mousePoint
         */
        protected void updateRolloverPoint(JComponent component, Point mousePoint) {
            
        }
        
        
    }