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
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.action.LinkAction;
import org.jdesktop.swingx.action.LinkModelAction;

/**
 * A Renderer/Editor for "Links". <p>
 * 
 * The renderer is configured with a LinkAction<T>. 
 * Currently it's up to the developer to guarantee that the all
 * values which are passed into the getXXRendererComponent(...) are
 * compatible with T. <p>
 * 
 * It's recommended to not use the given Action anywhere else in code,
 * as it is updated on each getXXRendererComponent() call which might
 * lead to undesirable side-effects. <p>
 * 
 * internally uses JXHyperlink for both (Note: don't reuse the same
 * instance for both functions). <p>
 * 
 * PENDING: make renderer respect selected cell state.
 * 
 * @author Jeanette Winzenburg
 */
public class LinkRenderer extends AbstractCellEditor implements
        TableCellRenderer, TableCellEditor, ListCellRenderer, RolloverRenderer {

    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    private JXHyperlink linkButton;

    private LinkAction<Object> linkAction;

    public LinkRenderer() {
        this(null);
    }

    public LinkRenderer(LinkAction linkAction) {
        linkButton = createHyperlink();
        linkButton.addActionListener(createEditorActionListener());
        setLinkAction(linkAction);
    }
    
    public void setLinkAction(LinkAction linkAction) {
        if (linkAction == null) {
            linkAction = createDefaultLinkAction();
        }
        this.linkAction = linkAction;
        linkButton.setAction(linkAction);
    }
    
    // does nothing...
    private LinkAction createDefaultLinkAction() {
        return new LinkAction() {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
    }


    /**
     * @return
     */
    private JXHyperlink createHyperlink() {
        return new JXHyperlink() {

            @Override
            public void updateUI() {
                super.updateUI();
                setBorderPainted(true);
                setOpaque(true);
            }
            
        };
    }

    public boolean isRolloverEnabled() {
        return true;
    }

    public Component getListCellRendererComponent(JList list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
        linkAction.setTarget(value);
        if (list != null) {
            Point p = (Point) list
                .getClientProperty(RolloverProducer.ROLLOVER_KEY);
            if (/*cellHasFocus ||*/ (p != null && (p.y >= 0) && (p.y == index))) {
                 linkButton.getModel().setRollover(true);
            } else {
                 linkButton.getModel().setRollover(false);
            }
            updateSelectionColors(list, isSelected);
            updateFocusBorder(cellHasFocus);
        };
        return linkButton;
    }
    

    private void updateSelectionColors(JList table, boolean isSelected) {
        if (isSelected) {
            // linkButton.setForeground(table.getSelectionForeground());
            linkButton.setBackground(table.getSelectionBackground());
        } else {
            // linkButton.setForeground(table.getForeground());
            linkButton.setBackground(table.getBackground());
        }

    }

//------------------------ TableCellRenderer
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        linkAction.setTarget(value);
        if (table !=  null) {
            Point p = (Point) table
                    .getClientProperty(RolloverProducer.ROLLOVER_KEY);
            if (/*hasFocus || */(p != null && (p.x >= 0) && (p.x == column) && (p.y == row))) {
                 linkButton.getModel().setRollover(true);
            } else {
                 linkButton.getModel().setRollover(false);
            }
            updateSelectionColors(table, isSelected);
            updateFocusBorder(hasFocus);
        }
        return linkButton;
    }

    private void updateSelectionColors(JTable table, boolean isSelected) {
            if (isSelected) {
//                linkButton.setForeground(table.getSelectionForeground());
                linkButton.setBackground(table.getSelectionBackground());
            }
            else {
//                linkButton.setForeground(table.getForeground());
                linkButton.setBackground(table.getBackground());
            }
    
    }

    private void updateFocusBorder(boolean hasFocus) {
        if (hasFocus) {
            linkButton.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            linkButton.setBorder(noFocusBorder);
        }

        
    }

//-------------------------- TableCellEditor
    
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        linkAction.setTarget(value);
        linkButton.getModel().setRollover(true); 
        updateSelectionColors(table, isSelected);
        return linkButton;
    }

    public Object getCellEditorValue() {
        return linkAction.getTarget();
    }

    
 
    @Override
    protected void fireEditingStopped() {
        fireEditingCanceled();
    }

    private ActionListener createEditorActionListener() {
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cancelCellEditing();

            }

        };
        return l;
    }


}
