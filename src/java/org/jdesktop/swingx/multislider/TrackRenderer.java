package org.jdesktop.swingx.multislider;

import java.awt.Graphics2D;
import org.jdesktop.swingx.*;


public interface TrackRenderer {
    public void paintTrack(Graphics2D g, JXMultiThumbSlider slider);
}