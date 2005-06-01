/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.LabelProperties;
import org.jdesktop.swingx.decorator.Sorter;
import org.jdesktop.swingx.icon.SortArrowIcon;

/**
 * Header renderer class which renders column sort feedback (arrows).
 *
 * @author Amy Fowler
 * @author Ramesh Gupta
 */
public class ColumnHeaderRenderer extends JPanel implements TableCellRenderer {

    private static TableCellRenderer	sharedInstance = null;
    private static Icon					defaultDownIcon = new SortArrowIcon(false);
    private static Icon					defaultUpIcon = new SortArrowIcon(true);
//    private static Border               defaultMarginBorder = BorderFactory.createEmptyBorder(2,2,2,2);
    private static Border defaultArrowBorder = BorderFactory.createEmptyBorder(0, 2, 0, 4);
    private Icon    downIcon = defaultDownIcon;
    private Icon    upIcon = defaultUpIcon;
    private JLabel	arrow = new JLabel((Icon) null, JLabel.CENTER);
    private boolean antiAliasedText = false;

    private TableCellRenderer delegateRenderer;
    private Component delegateRendererComponent;
    
//    private boolean backgroundSet = false;
//    private boolean foregroundSet = false;
//    private boolean fontSet = false;

    private LabelProperties label;
    
    public static TableCellRenderer getSharedInstance() {
         if (sharedInstance == null) {
             sharedInstance = new ColumnHeaderRenderer();
         }
         return sharedInstance;
     }

    public static ColumnHeaderRenderer createColumnHeaderRenderer() {
        return new ColumnHeaderRenderer();
    }
    
    private ColumnHeaderRenderer() {
        setLayout(new BorderLayout());
        label = new LabelProperties();
        initDelegate();
        add(arrow, BorderLayout.EAST);
        // different combinations of this.opaque and delegate.opaque
        // all have issues
        // 1. if the delegate is not explicitly set to false the border looks wrong
        // 2. if this is set to true we can have custom background per cell but no
        //  setting the header background has no effect - and changing LF doesn't
        //  take up the LF default background ... hmmm
        // 3. if this is set to false we can't have custom cell background
        // any ideas?
        setOpaque(true);
    }


    private void initDelegateComponent(Component comp) {
        delegateRendererComponent = comp;
        add(comp);
        if (comp instanceof JComponent) {
            ((JComponent) comp).setOpaque(false);
        }
        
    }

    private void initDelegate() {
        delegateRendererComponent = null;
        JTableHeader header = new JTableHeader();
        delegateRenderer = header.getDefaultRenderer();
        // some renderers can't cope with null table
        try {
            Component comp = delegateRenderer.getTableCellRendererComponent(null,
                    null, false, false, -1, -1);
            initDelegateComponent(comp);
        } catch (Exception e) {
            // can't do anything for now, try later in a normal 
            // getTableCellRendererComponent
        }
       
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
        configureDelegate(table, value, isSelected, hasFocus, rowIndex, columnIndex);
        if (table instanceof JXTable) {
            // We no longer limit ourselves to a single "currently sorted column"
            Sorter	sorter = ((JXTable) table).getSorter(columnIndex);

            if (sorter == null) {
                arrow.setIcon(null);
                arrow.setBorder(null);
            }
            else {
                arrow.setIcon(sorter.isAscending() ? upIcon : downIcon);
                arrow.setBorder(defaultArrowBorder);
            }
        }
        return this;
    }

    private void configureDelegate(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
        Component comp = delegateRenderer.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, rowIndex, columnIndex);
        if (delegateRendererComponent == null) {
            initDelegateComponent(comp);
        }
        
        Border border = UIManager.getBorder("TableHeader.cellBorder");
        if (comp instanceof JComponent) {
            JComponent jComp = (JComponent) comp;
            border = jComp.getBorder();
            jComp.setBorder(null);
        }
        applyLabelProperties();
        setBorder(border);
        
    }

    private void applyLabelProperties() {
        if (delegateRendererComponent instanceof JLabel) {
            label.applyPropertiesTo((JLabel) delegateRendererComponent);
        } else {
            label.applyPropertiesTo(delegateRenderer);
        }
    }


    public void setAntiAliasedText(boolean antiAlias) {
        this.antiAliasedText = antiAlias;
    }

    public boolean getAntiAliasedText() {
        return antiAliasedText;
    }

    public void setBackground(Color background) {
        // this is called somewhere along initialization of super?
        if (label != null) {
            label.setBackground(background);
        }
        super.setBackground(background);
    }

    public void setForeground(Color foreground) {
        super.setForeground(foreground);
        // this is called somewhere along initialization of super?
        if (label != null) {
            label.setForeground(foreground);
        }
    }

    public void setFont(Font font) {
        super.setFont(font);
        // this is called somewhere along initialization of super?
        if (label != null) {
            label.setFont(font);
        }
    }

    public void setDownIcon(Icon icon) {
        this.downIcon = icon;
    }

    public Icon getDownIcon() {
        return downIcon;
    }

    public void setUpIcon(Icon icon) {
        this.upIcon = icon;
    }

    public Icon getUpIcon() {
        return upIcon;
    }

    public void setHorizontalAlignment(int alignment) {
        label.setHorizontalAlignment(alignment);
    }

    public int getHorizontalAlignment() {
        return label.getHorizontalAlignment();
    }

    public void setHorizontalTextPosition(int textPosition) {
        label.setHorizontalTextPosition(textPosition);
    }

    public int getHorizontalTextPosition() {
        return label.getHorizontalTextPosition();
    }

    public void setIcon(Icon icon) {
        label.setIcon(icon);
    }

    public Icon getIcon() {
        return label.getIcon();
    }

    public void setIconTextGap(int iconTextGap) {
        label.setIconTextGap(iconTextGap);
    }

    public int getIconTextGap() {
        return label.getIconTextGap();
    }

    public void setVerticalAlignment(int alignment) {
        label.setVerticalAlignment(alignment);
    }

    public int getVerticalAlignment() {
        return label.getVerticalAlignment();
    }

    public void setVerticalTextPosition(int textPosition) {
        label.setVerticalTextPosition(textPosition);
    }

    public int getVerticalTextPosition() {
        return label.getVerticalTextPosition();
    }

    public void paint(Graphics g) {
        if (antiAliasedText) {
            Graphics2D g2 = (Graphics2D) g;
            Object save = g2.getRenderingHint(RenderingHints.
                                              KEY_TEXT_ANTIALIASING);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            super.paint(g2);

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, save);
        } else {
            super.paint(g);
        }
    }
    
    public void updateUI() {
        super.updateUI();
        if (arrow != null) {
            arrow.updateUI();
        }
        
        initDelegate();
        if (delegateRendererComponent instanceof JComponent) {
            ((JComponent) delegateRendererComponent).updateUI();
        }
    }
}
