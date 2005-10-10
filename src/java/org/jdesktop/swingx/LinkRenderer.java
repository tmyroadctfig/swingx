/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.action.LinkAction;

/**
 * A Renderer/Editor for Links.
 * 
 * internally uses JXHyperlink for both (Note: don't reuse the same
 * instance for both functions).
 * 
 * PENDING: make renderer respect selected cell state.
 * 
 * @author Jeanette Winzenburg
 */
public class LinkRenderer extends AbstractCellEditor implements
        TableCellRenderer, TableCellEditor, ListCellRenderer {
    private JXHyperlink linkButton;

    private LinkAction linkAction;

    public LinkRenderer() {
        this(null);
    }

    public LinkRenderer(ActionListener visitingDelegate) {
        linkAction = new LinkAction(null);
        linkButton = new JXHyperlink(linkAction);
        linkButton.addActionListener(createEditorActionListener());
        setVisitingDelegate(visitingDelegate);
    }

    public void setVisitingDelegate(ActionListener openAction) {
        linkAction.setVisitingDelegate(openAction);
        
    }

    public Component getListCellRendererComponent(JList list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
        linkAction.setLink(value instanceof LinkModel ? (LinkModel) value : null);
        Point p = (Point) list
            .getClientProperty(RolloverProducer.ROLLOVER_KEY);
        if (cellHasFocus || (p != null && (p.y >= 0) && (p.y == index))) {
             linkButton.getModel().setRollover(true);
        } else {
             linkButton.getModel().setRollover(false);
        }
        return linkButton;
    }
    
//------------------------ TableCellRenderer
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        linkAction.setLink(value instanceof LinkModel ? (LinkModel) value : null);
        Point p = (Point) table
                .getClientProperty(RolloverProducer.ROLLOVER_KEY);
        // JW: check - px > 0 looks fishy! probably meant >= 0?
        if (hasFocus || (p != null && (p.x >= 0) && (p.x == column) && (p.y == row))) {
             linkButton.getModel().setRollover(true);
        } else {
             linkButton.getModel().setRollover(false);
        }
        return linkButton;
    }

//-------------------------- TableCellEditor
    
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        linkAction.setLink(value instanceof LinkModel ? (LinkModel) value : null);
        linkButton.getModel().setRollover(true); 
        return linkButton;
    }

    public Object getCellEditorValue() {
        return linkAction.getLink();
    }

    private ActionListener createEditorActionListener() {
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();

            }

        };
        return l;
    }


}
