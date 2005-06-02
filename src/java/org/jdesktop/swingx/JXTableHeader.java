/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * @author (C) 2004 Jeanette Winzenburg, Berlin
 * @version $Revision$ - $Date$
 */
public class JXTableHeader extends JTableHeader {

    private final static MouseAdapter   headerListener = new MouseAdapter() {
        // MouseAdapter must be stateless
        public void mouseClicked(MouseEvent e) {
            if (isInResizeRegion(e)) {
                doResize(e);
                return;
            }
            JTableHeader    header = (JTableHeader) e.getSource();
            JXTable     table = (JXTable) header.getTable();
            if (!table.isSortable()) return;
            if ((e.getModifiersEx() & e.SHIFT_DOWN_MASK) == e.SHIFT_DOWN_MASK) {
                table.resetSorter();
            }
            else {

                int column = header.getColumnModel().getColumnIndexAtX(e.getX());
                if (column >= 0) {
                    table.setSorter(column);
                }
            }
            header.repaint();
        }

        private void doResize(MouseEvent e) {
            if (e.getClickCount() < 2) return;
            JTableHeader header = (JTableHeader) e.getSource();
            if (header.getTable() instanceof JXTable) {
                int column = header.getColumnModel().getColumnIndexAtX(e.getX());
                if (column >= 0) {
                    ((JXTable) header.getTable()).packColumn(column, 5);
                }
            }
            
        }

        private boolean isInResizeRegion(MouseEvent e) {
            JTableHeader header = (JTableHeader) e.getSource();
            // JW: kind of a hack - there's no indication in the 
            // JTableHeader api to find if we are in the resizing
            // region before actually receiving a click
            // checked the header.resizingColumn should be set on
            // first click?
            // doesn't work probably because this listener is messaged before
            // ui-delegate listener
            // return header.getResizingColumn() != null;
            Cursor cursor = header.getCursor();
            boolean inResize = cursor != null ? 
                    (cursor.getType() == Cursor.E_RESIZE_CURSOR || cursor.getType() == Cursor.W_RESIZE_CURSOR ) :
                     false;   
            return inResize;
        }
    };
 
    public JXTableHeader() {
        super();
        installListeners();
    }
    
    public JXTableHeader(TableColumnModel columnModel) {
        super();
        installListeners();
    }
    
    private void installListeners() {
        addMouseListener(headerListener);
        
    }

    public void setTable(JTable table) {
        // TODO Auto-generated method stub
        super.setTable(table);
    }
}
