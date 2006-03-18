package org.jdesktop.swingx.color;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.imageio.ImageIO;
import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.JXGradientChooser;

public class GradientThumbRenderer implements JXMultiThumbSlider.ThumbRenderer {
    
    private final JXGradientChooser gradientPicker;
    
    private JXMultiThumbSlider slider;
    
    private Image thumb_black;
    
    private Image thumb_gray;
    
    public GradientThumbRenderer(JXGradientChooser gradientPicker, JXMultiThumbSlider slider) {
	super();
	this.gradientPicker = gradientPicker;
	this.slider = slider;
	try {
	    thumb_black = ImageIO.read(this.getClass().getResourceAsStream("/icons/thumb_black.png"));
	    thumb_gray = ImageIO.read(this.getClass().getResourceAsStream("/icons/thumb_gray.png"));
	} catch (Exception ex)		{
	    ex.printStackTrace();
	}
    }
    
    public void paintThumb(Graphics2D g, JXMultiThumbSlider.ThumbComp thumb, int index, boolean selected) {
	int w = thumb.getWidth();
	Color c = (Color)gradientPicker.getSlider().getModel().getThumbAt(index).getObject();
	c = ColorUtil.removeAlpha(c);
	g.setColor(c);
	g.fillRect(0, 0, w - 1, w - 1);
	if (selected) {
	    g.drawImage(thumb_black, 0, 0, null);
	} else {
	    g.drawImage(thumb_gray, 0, 0, null);
	}
    }
}
