
package org.jdesktop.swingx.multislider;

import java.awt.Graphics2D;
import org.jdesktop.swingx.*;

public interface ThumbRenderer {
    public void paintThumb(Graphics2D g, JXMultiThumbSlider.ThumbComp thumb, int index, boolean selected);
}
