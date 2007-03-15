/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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


package org.jdesktop.swingx.painter;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author joshy
 */
public class PainterTableCellRenderer extends DefaultTableCellRenderer {
    private Painter foregroundPainter;
    private Painter backgroundPainter;
    private Painter selectionForegroundPainter;
    private Painter selectionBackgroundPainter;
    
    
    /** Creates a new instance of PainterTableCellRenderer */
    public PainterTableCellRenderer() {
    }
    
    public Painter getForegroundPainter() {
        return foregroundPainter;
    }
    
    public void setForegroundPainter(Painter foregroundPainter) {
        this.foregroundPainter = foregroundPainter;
    }
    
    public Painter getBackgroundPainter() {
        return backgroundPainter;
    }
    
    public void setBackgroundPainter(Painter backgroundPainter) {
        this.backgroundPainter = backgroundPainter;
    }
    
    public Painter getSelectionForegroundPainter() {
        return selectionForegroundPainter;
    }
    
    public void setSelectionForegroundPainter(Painter selectionPainter) {
        this.selectionForegroundPainter = selectionPainter;
    }
    
    private boolean isSelected = false;
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        // initialize the renderer (which is 'this')
        super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        setOpaque(false);
        this.isSelected = isSelected;
        return this;
    }
    
    protected void paintComponent(Graphics g) {
        
        if(isSelected && selectionBackgroundPainter != null) {
            applyPainter(selectionBackgroundPainter, g);
        } else {
            if (backgroundPainter != null) {
                applyPainter(backgroundPainter,g);
            }
        }
        
        if(isSelected && selectionForegroundPainter != null) {
            applyPainter(selectionForegroundPainter, g);
        } else {
            if (foregroundPainter != null) {
                applyPainter(foregroundPainter, g);
            } else {
                super.paintComponent(g);
            }
        }
        
    }    
    
    private void applyPainter(final Painter painter, final Graphics g) {
        Graphics2D g2 = (Graphics2D)g.create();
        painter.paint(g2, this,
                this.getWidth(),
                this.getHeight());
        g2.dispose();
    }
    
    public Painter getSelectionBackgroundPainter() {
        return selectionBackgroundPainter;
    }
    
    public void setSelectionBackgroundPainter(Painter selectionBackgroundPainter) {
        this.selectionBackgroundPainter = selectionBackgroundPainter;
    }
    
}
