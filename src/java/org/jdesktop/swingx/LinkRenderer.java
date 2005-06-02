/*
 * Created on 23.05.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.action.LinkAction;
import org.jdesktop.swingx.util.Link;

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
        TableCellRenderer, TableCellEditor {
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

//------------------------ TableCellRenderer
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        linkAction.setLink(value instanceof Link ? (Link) value : null);
        Point p = (Point) table
                .getClientProperty(RolloverProducer.ROLLOVER_KEY);
        if (hasFocus || (p != null && (p.x > 0) && (p.x == column) && (p.y == row))) {
             linkButton.getModel().setRollover(true);
        } else {
             linkButton.getModel().setRollover(false);
        }
        return linkButton;
    }

//-------------------------- TableCellEditor
    
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        linkAction.setLink(value instanceof Link ? (Link) value : null);
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
