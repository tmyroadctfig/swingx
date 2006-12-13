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
package org.jdesktop.swingx.renderer;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.RolloverProducer;
import org.jdesktop.swingx.RolloverRenderer;
import org.jdesktop.swingx.action.LinkAction;

/**
 * A Renderer/Editor for "Links". <p>
 * 
 * The renderer is configured with a LinkAction<T>. 
 * It's mostly up to the developer to guarantee that the all
 * values which are passed into the getXXRendererComponent(...) are
 * compatible with T: she can provide a runtime class to check against.
 * If it isn't the renderer will configure the
 * action with a null target. <p>
 * 
 * It's recommended to not use the given Action anywhere else in code,
 * as it is updated on each getXXRendererComponent() call which might
 * lead to undesirable side-effects. <p>
 * 
 * Internally uses JXHyperlink for both CellRenderer and CellEditor
 * It's recommended to not reuse the same instance for both functions. <p>
 * 
 * PENDING: make renderer respect selected cell state.
 * 
 * PENDING: TreeCellRenderer has several issues
 *   - no icons
 *   - usual background highlighter issues
 * 
 * @author Jeanette Winzenburg
 */
public class LinkTableCellRendererExt extends AbstractLinkCellRendererExt<JTable> implements
        TableCellRenderer {


    /**
     * Instantiate a LinkRenderer with null LinkAction and null
     * targetClass.
     *
     */
    public LinkTableCellRendererExt() {
        this(null, null);
    }

    /**
     * Instantiate a LinkRenderer with the LinkAction to use with
     * target values. 
     * 
     * @param linkAction the action that acts on values.
     */
    public LinkTableCellRendererExt(LinkAction linkAction) {
        this(linkAction, null);
    }
    
    /**
     * Instantiate a LinkRenderer with a LinkAction to use with
     * target values and the type of values the action can cope with. <p>
     * 
     * It's up to developers to take care of matching types.
     * 
     * @param linkAction the action that acts on values.
     * @param targetClass the type of values the action can handle.
     */
    public LinkTableCellRendererExt(LinkAction linkAction, Class targetClass) {
        super(linkAction, targetClass);
    }
    
//------------------------ TableCellRenderer

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if ((value != null) && !isTargetable(value)) {
            value = null;
        }
        CellContext<JTable> context = getCellContext();
        context.installContext(table, value, row, column, isSelected, hasFocus, true, true);
        configureVisuals(context);
        configureContent(context);    
        return rendererComponent;
    }

    @Override
    protected CellContext<JTable> getCellContext() {
        if (cellContext == null) {
            cellContext = new TableCellContext();
        }
        return cellContext;
    }

}
