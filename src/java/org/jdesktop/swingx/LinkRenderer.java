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
 * @author Jeanette Winzenburg
 */
public class LinkRenderer extends AbstractCellEditor implements
        TableCellRenderer, TableCellEditor {
    private JXHyperlinkButton linkButton;

    private LinkAction linkAction;

    public LinkRenderer() {
        this(null);
    }

    public LinkRenderer(ActionListener openAction) {
        linkAction = new LinkAction(null);
        linkButton = new JXHyperlinkButton(linkAction);
        linkButton.addActionListener(createEditorActionListener());
        setVisitingDelegate(openAction);
    }

    public void setVisitingDelegate(ActionListener openAction) {
        linkAction.setVisitingDelegate(openAction);
        
    }
    private ActionListener createEditorActionListener() {
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();

            }

        };
        return l;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        linkAction.setLink(value instanceof Link ? (Link) value : null);
        Point p = (Point) table
                .getClientProperty(RolloverProducer.ROLLOVER_KEY);
        if (hasFocus || (p != null && (p.x > 0) && (p.x == column) && (p.y == row))) {
            // JW: toggling model's rollover state is unreliable - hmmm...
             linkButton.getModel().setRollover(true);
           // linkButton.entered(true);
        } else {
             linkButton.getModel().setRollover(false);
          //  linkButton.exited(true);
        }
        return linkButton;
    }

    public Object getCellEditorValue() {
        return linkAction.getLink();
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        linkAction.setLink(value instanceof Link ? (Link) value : null);
        linkButton.getModel().setRollover(true); //entered(true);
        return linkButton;
    }

}
