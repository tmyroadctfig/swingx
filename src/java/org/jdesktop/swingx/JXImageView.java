/*
 * JXImageView.java
 *
 * Created on April 25, 2006, 9:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.event.MouseInputAdapter;

/**
 * a panel which shows an image centered. the user can drag an image into the panel
 * to display it. The view has built in actions for 
 *  scaling, 
 * rotating,  
 * opening a new image, and 
 * cropping.
 *
 * has dashed rect and text indicating you should drag there.
 *
 * allows user to drag image within the panel, if allowed. shows move cursor
 * or hand cursor.
 *
 * allows to set a crop/ restriction rect, if allowed
 * @author joshy
 */
public class JXImageView extends JXPanel {
    
    // the image this view will show
    private Image image;
    
    // location to draw image. if null then draw in the center
    private Point2D imageLocation;
    
    // an action which will open a file chooser and load the selected image
    // if any.
    public Action getOpenAction() {
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(JXImageView.this);
                File file = chooser.getSelectedFile();
                if(file != null) {
                    try {
                        setImage(file);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        };
        action.putValue(Action.NAME,"Open");
        return action;
    }

        /** Creates a new instance of JXImageView */
    public JXImageView() {
        MouseInputAdapter mia = new MoveHandler();
        addMouseMotionListener(mia);
        addMouseListener(mia);
    }

    /* === properties === */
    public Point2D getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(Point2D imageLocation) {
        this.imageLocation = imageLocation;
    }
    
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        setImageLocation(null);
        repaint();
    }
    
    public void setImage(URL url) throws IOException {
        setImage(ImageIO.read(url));
    }
    
    public void setImage(File file) throws IOException {
        setImage(ImageIO.read(file));
    }
    
    
    /* === overriden methods === */
    
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0,0,getWidth(),getHeight());
        if(getImage() != null) {
            if(getImageLocation() == null) {
                g.drawImage(getImage(),
                        (getWidth()-getImage().getWidth(null))/2,
                        (getHeight()-getImage().getHeight(null))/2,
                        null);
            } else {
                g.drawImage(getImage(),
                        (int)getImageLocation().getX(),
                        (int)getImageLocation().getY(),
                        null);
            }
        }
    }

    
    /* === Internal helper classes === */

    private class MoveHandler extends MouseInputAdapter {

        private Point prev = null;

        public void mousePressed(MouseEvent evt) {
            prev = evt.getPoint();
        }

        public void mouseDragged(MouseEvent evt) {
            Point curr = evt.getPoint();
            int offx = curr.x - prev.x;
            int offy = curr.y - prev.y;
            Point2D offset = getImageLocation();
            if (offset == null) {
                if (image != null) {
                    offset = new Point2D.Double((getWidth() - getImage().getWidth(null)) / 2, (getHeight() - getImage().getHeight(null)) / 2);
                } else {
                    offset = new Point2D.Double(0, 0);
                }
            }
            offset = new Point2D.Double(offset.getX() + offx, offset.getY() + offy);
            setImageLocation(offset);
            prev = curr;
            repaint();
        }

        public void mouseReleased(MouseEvent evt) {
            prev = null;
        }
    }

    
}
