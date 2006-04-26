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

package org.jdesktop.swingx.painter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * <p>A Painter instance that paints an image. Any Image is acceptable. This
 * Painter also allows the developer to specify a "Style" -- CENTERED, TILED,
 * SCALED, POSITIONED, and CSS_POSITIONED; with the following meanings:</p>
 *
 * <ul>
 *  <li><b>CENTERED</b>: draws the image unscaled and positioned in the center of
 * the component</li>
 *  <li><b>TILED</b>: draws the image repeatedly across the component, filling the
 * entire background.</li>
 *  <li><b>SCALED</b>: draws the image stretched large enough (or small enough) to 
 * cover the entire component. The stretch may not preserve the aspect ratio of the
 * original image.</li>
 *  <li><b>POSITIONED</b>: draws the image at the location specified by the imageLocation
 * property. This style of drawing will respect the imageScale property.</li>
 *  <li><b>CSS_POSITIONED</b>: draws the image using CSS style background positioning.
 *It will use the location specified by the imageLocation property. This property should
 *contain a point with the x and y values between 0 and 1. 0,0 will put the image in the
 *upper left hand corner, 1,1 in the lower right, and 0.5,0.5 in the center. All other values
 *will be interpolated accordingly. For a more
 * complete defintion of the positioning algorithm see the 
 * <a href="http://www.w3.org/TR/CSS21/colors.html#propdef-background-position">CSS 2.1 spec</a>.
 * </li>
 * </ul>
 *
 * @author Richard
 */
public class ImagePainter extends AbstractPainter {
    /**
     * Logger to use
     */
    private static final Logger LOG = Logger.getLogger(ImagePainter.class.getName());
    
    /**
     * <p>An enumeration of Styles supported by the ImagePainter. CENTERED indicates
     * that the image should be centered. If the image is too large for the
     * canvas area, it will be clipped, but remain centered</p>
     *
     * <p>TILED indicates that the image should be painted multiple times, as
     * a series of tiles</p>
     *
     * <p>SCALED indicates that the image should be scaled to fit the canvas area.
     * The smallest dimension (Math.min(width, height)) will be used to constrain
     * the image.
     */
    public static enum Style {CENTERED, TILED, SCALED, POSITIONED, CSS_POSITIONED};
    
    /**
     * The image to draw
     */
    private Image img;
    
    /**
     * Specifies how to draw the image, i.e. what kind of Style to use
     * when drawing
     */
    private Style style = Style.CENTERED;
    
    /**
     * Create a new ImagePainter. By default there is no image, and the Style
     * is set to Style.CENTERED
     */
    public ImagePainter() {
        super();
    }
    
    /**
     * Create a new ImagePainter with the specified image and the Style
     * Style.CENTERED
     *
     * @param image the image to be painted
     */
    public ImagePainter(Image image) {
        super();
        this.img = image;
    }
    
    /**
     * Create a new ImagePainter with the specified image and style.
     *
     * @param image the image to be painted
     * @param style the style of the image
     */
    public ImagePainter(Image image, Style style) {
        super();
        this.img = image;
        this.style = style;
    }
    
    /**
     * Sets the image to use for the background of this panel. This image is
     * painted whether the panel is opaque or translucent.
     *
     * @param image if null, clears the image. Otherwise, this will set the
     * image to be painted. If the preferred size has not been explicitly set,
     * then the image dimensions will alter the preferred size of the panel.
     */
    public void setImage(Image image) {
        if (image != img) {
            Image oldImage = img;
            img = image;
            firePropertyChange("image", oldImage, img);
        }
    }
    
    /**
     * @return the image used for painting the background of this panel
     */
    public Image getImage() {
        return img;
    }
    
    /**
     * Sets what style to use when painting the image
     *
     * @param s The style constant to apply to the image.
     */
    public void setStyle(Style s) {
        if (style != s) {
            Style oldStyle = style;
            style = s;
            firePropertyChange("style", oldStyle, s);
        }
    }

    /**
     * @return the Style used for drawing the image (CENTERED, TILED, etc).
     */
    public Style getStyle() {
        return style;
    }
    
    /**
     * @inheritDoc
     */
    public void paintBackground(Graphics2D g, JComponent component) {
        if (img != null) {
            int imgWidth = img.getWidth(null);
            int imgHeight = img.getHeight(null);
            if (imgWidth == -1 || imgHeight == -1) {
                //image hasn't completed loading, do nothing
            } else {
                switch (style) {
                    case CENTERED:
                        Rectangle clipRect = g.getClipBounds();
                        int imageX = (component.getWidth() - imgWidth) / 2;
                        int imageY = (component.getHeight() - imgHeight) / 2;
                        Rectangle r = SwingUtilities.computeIntersection(imageX, imageY, imgWidth, imgHeight, clipRect);
                        if (r.x == 0 && r.y == 0 && (r.width == 0 || r.height == 0)) {
                            return;
                        }
                        //I have my new clipping rectangle "r" in clipRect space.
                        //It is therefore the new clipRect.
                        clipRect = r;
                        //since I have the intersection, all I need to do is adjust the
                        //x & y values for the image
                        int txClipX = clipRect.x - imageX;
                        int txClipY = clipRect.y - imageY;
                        int txClipW = clipRect.width;
                        int txClipH = clipRect.height;

                        g.drawImage(img, clipRect.x, clipRect.y, clipRect.x + clipRect.width, clipRect.y + clipRect.height,
                                txClipX, txClipY, txClipX + txClipW, txClipY + txClipH, null);
                        break;
                    case TILED:
                        if (img instanceof BufferedImage) {
                            BufferedImage b = (BufferedImage)img;
                            TexturePaint paint = new TexturePaint(b,
                                    new Rectangle2D.Double(0, 0, b.getWidth(), b.getHeight()));
                            g.setPaint(paint);
                            g.fillRect(0, 0, component.getWidth(), component.getHeight());
                        } else {
                            //TODO!
                            LOG.fine("unimplemented");
                        }
                    case SCALED:
                          g.drawImage(img, 0, 0, component.getWidth(), component.getHeight(), null);
                        break;
                    case POSITIONED:
                          g.drawImage(img, (int)imagePosition.getX(), (int)imagePosition.getY(), 
                                  (int)(((double)img.getWidth(null))*imageScale),
                                  (int)(((double)img.getHeight(null))*imageScale),
                                  null);
                        break;
                    case CSS_POSITIONED:
                        double x = imagePosition.getX() * (component.getWidth()-img.getWidth(null));
                        double y = imagePosition.getY() * (component.getHeight()-img.getHeight(null));
                        g.drawImage(img,(int)x,(int)y,null);
                        break;
                    default:
                        LOG.fine("unimplemented");
                        g.drawImage(img, 0, 0, null);
                        break;
                }
            }
        }
    }
    
    private Point2D imagePosition = new Point2D.Double(0,0);
    public void setImagePosition(Point2D imagePosition) {
        this.imagePosition = imagePosition;
    }
    public Point2D getImagePosition() {
        return imagePosition;
    }

    private double imageScale = 1.0;
    public void setImageScale(double imageScale) {
        this.imageScale = imageScale;
    }

}
