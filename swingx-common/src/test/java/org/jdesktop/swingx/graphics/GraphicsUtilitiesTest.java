package org.jdesktop.swingx.graphics;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.image.BufferedImage;

import org.junit.Test;

public class GraphicsUtilitiesTest {
    @Test
    public void testClear() {
        BufferedImage img = GraphicsUtilities.createCompatibleImage(1, 1);
        GraphicsUtilities.clear(img);
        assertThat(GraphicsUtilities.getPixels(img, 0, 0, 1, 1, null)[0], is(0));
    }
}
