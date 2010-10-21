package org.jdesktop.beans.editors;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author joshy
 */
public class ImageURLEditor extends PropertyEditorSupport {
    Image image = null;
    String imageURL = null;
    ImagePicker picker = new ImagePicker();
    /** Creates a new instance of ImageEditor */
    public ImageURLEditor() {
        picker.imageView.addPropertyChangeListener("image",new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                image = picker.imageView.getImage();
                if(picker.imageView.getImageURL() != null) {
                    imageURL = picker.imageView.getImageURL().toString();
                }
                firePropertyChange();
            }
        });
    }
    
    @Override
    public String getValue() {
        return imageURL;
    }
    
    @Override
    public void setValue(Object object) {
        imageURL = (String)object;
        super.setValue(imageURL);
        picker.imageView.setImage(image);
    }
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // do nothing right now
    }
    
    @Override
    public String getAsText() {
        return imageURL;
    }
    
    @Override
    public void paintValue(Graphics graphics, Rectangle r) {
        //graphics.drawImage(image, (int)r.getX(), (int)r.getY(),
        //        (int)r.getWidth(), (int)r.getHeight(), null);
    }
    
    
    @Override
    public boolean isPaintable() {
        return false;
    }
    
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
    
    @Override
    public Component getCustomEditor() {
        return picker;
    }
    
}
