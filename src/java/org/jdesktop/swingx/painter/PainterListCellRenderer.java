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
import java.awt.Insets;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author joshy
 */
public class PainterListCellRenderer extends DefaultListCellRenderer {
    private Painter foregroundPainter;
    private Painter backgroundPainter;
    private Painter selectionPainter;
    
    
    /** Creates a new instance of PainterListCellRenderer */
    public PainterListCellRenderer() {
    }
    
    
    public Painter getBackgroundPainter() {
        return backgroundPainter;
    }
    
    public void setBackgroundPainter(Painter backgroundPainter) {
        this.backgroundPainter = backgroundPainter;
    }
    
    public Painter getSelectionPainter() {
        return selectionPainter;
    }
    
    public void setSelectionPainter(Painter selectionPainter) {
        this.selectionPainter = selectionPainter;
    }
    
    private boolean isSelected = false;
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        // initialize the renderer (which is 'this')
        super.getListCellRendererComponent(list, value,
                index, isSelected, cellHasFocus);
        setOpaque(false);
        this.isSelected = isSelected;
        return this;
    }
    
    protected void paintComponent(Graphics g) {
        if (backgroundPainter != null) {
            applyPainter(backgroundPainter,g);
        }
        if (foregroundPainter != null) {
            applyPainter(foregroundPainter, g);
        } else {
            super.paintComponent(g);
        }
        if(isSelected) {
            if(selectionPainter != null) {
                applyPainter(selectionPainter,g);
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
    
    public Painter getForegroundPainter() {
        return foregroundPainter;
    }
    
    public void setForegroundPainter(Painter foregroundPainter) {
        this.foregroundPainter = foregroundPainter;
    }
}
