/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Cursor;
import java.awt.Graphics2D;

import java.io.File;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;


/**
 * <p>A panel that draws an image. The standard (and currently only supported)
 * mode is to draw the specified image starting at position 0,0 in the
 * panel. The component&amp;s preferred size is based on the image, unless
 * explicitly set by the user.</p>
 *
 * <p>In the future, the JXImagePanel will also support tiling of images,
 * scaling, resizing, cropping, segways etc.</p>
 *
 * <p>This component also supports allowing the user to set the image. If the
 * <code>JXImagePanel</code> is editable, then when the user clicks on the
 * <code>JXImagePanel</code> a FileChooser is shown allowing the user to pick
 * some other image to use within the <code>JXImagePanel</code>.</p>
 *
 * @author unattributed, rbair
 */
public class JXImagePanel extends JXPanel {
    public static enum Style {CENTERED, TILED, SCALED};
    
    /**
     * Text informing the user that clicking on this component will allow them to set the image
     */
    private static final String TEXT = "<html><i><b>Click here<br>to set the image</b></i></html>";
    /**
     * The image to draw
     */
    private ImageIcon img;
    /**
     * If true, then the image can be changed. Perhaps a better name is
     * &quot;readOnly&quot;, but editable was chosen to be more consistent
     * with other Swing components.
     */
    private boolean editable = false;
    /**
     * The mouse handler that is used if the component is editable
     */
    private MouseHandler mhandler = new MouseHandler();
    /**
     * If not null, then the user has explicitly set the preferred size of
     * this component, and this should be honored
     */
    private Dimension preferredSize;
    /**
     * Specifies how to draw the image, i.e. what kind of Style to use
     * when drawing
     */
    private Style style = Style.CENTERED;
    
    public JXImagePanel() {
    }
    
    /**
     * Sets the image to use for the background of this panel. This image is
     * painted whether the panel is opaque or translucent.
     * @param image if null, clears the image. Otherwise, this will set the
     * image to be painted. If the preferred size has not been explicitly set,
     * then the image dimensions will alter the preferred size of the panel.
     */
    public void setIcon(ImageIcon image) {
        if (image != img) {
            ImageIcon oldImage = img;
            img = (ImageIcon)image;
            firePropertyChange("icon", oldImage, img);
            invalidate();
            repaint();
        }
    }
    
    /**
     * @return the image used for painting the background of this panel
     */
    public ImageIcon getIcon() {
        return img;
    }
    
    /**
     * @param editable
     */
    public void setEditable(boolean editable) {
        if (editable != this.editable) {
            //if it was editable, remove the mouse handler
            if (this.editable) {
                removeMouseListener(mhandler);
            }
            this.editable = editable;
            //if it is now editable, add the mouse handler
            if (this.editable) {
                addMouseListener(new MouseHandler());
            }
            setToolTipText(editable ? TEXT : "");
            firePropertyChange("editable", !editable, editable);
            repaint();
        }
    }
    
    /**
     * @return whether the image for this panel can be changed or not via
     * the UI. setImage may still be called, even if <code>isEditable</code>
     * returns false.
     */
    public boolean isEditable() {
        return editable;
    }
    
    /**
     * Sets what style to use when painting the image
     *
     * @param s
     */
    public void setStyle(Style s) {
        if (style != s) {
            Style oldStyle = style;
            style = s;
            firePropertyChange("style", oldStyle, s);
            repaint();
        }
    }

    /**
     * @return the Style used for drawing the image (CENTERED, TILED, etc).
     */
    public Style getStyle() {
        return style;
    }
    
    public void setPreferredSize(Dimension pref) {
        preferredSize = pref;
        super.setPreferredSize(pref);
    }
    
    public Dimension getPreferredSize() {
        if (preferredSize == null && img != null) {
            //it has not been explicitly set, so return the width/height of the image
            return new Dimension(img.getIconWidth(), img.getIconHeight());
        } else {
            return super.getPreferredSize();
        }
    }
    
    /**
     * Overriden to paint the image on the panel
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            switch (style) {
                case CENTERED:
                    g.drawImage(img.getImage(),
                            (getWidth() - img.getIconWidth()) / 2,
                            (getHeight() - img.getIconHeight()) / 2,
                            img.getImageObserver());
                    break;
                case TILED:
                case SCALED:
                default:
                    System.err.println("unimplemented");
                    g.drawImage(img.getImage(), 0, 0, img.getImageObserver());
                    break;
            }
        }
    }
    
    /**
     * Handles click events on the component
     */
    private class MouseHandler extends MouseAdapter {
        private Cursor oldCursor;
        private JFileChooser chooser;
        
        public void mouseClicked(MouseEvent evt) {
            if (chooser == null) {
                chooser = new JFileChooser();
            }
            int retVal = chooser.showOpenDialog(JXImagePanel.this);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    setIcon(new ImageIcon(file.toURL()));
                } catch (Exception ex) {
                }
            }
        }
        
        public void mouseEntered(MouseEvent evt) {
            JLabel label = (JLabel)evt.getSource();
            if (oldCursor == null) {
                oldCursor = label.getCursor();
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }
        
        public void mouseExited(MouseEvent evt) {
            JLabel label = (JLabel)evt.getSource();
            if (oldCursor != null) {
                label.setCursor(oldCursor);
                oldCursor = null;
            }
        }
    }
}

