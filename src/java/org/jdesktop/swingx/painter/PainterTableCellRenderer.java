/*
 * PainterTableCellRenderer.java
 *
 * Created on October 31, 2006, 4:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
