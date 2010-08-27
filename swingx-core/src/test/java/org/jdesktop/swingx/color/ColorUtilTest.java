/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.color;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Color;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Karl George Schaefer
 *
 */
@RunWith(JUnit4.class)
public class ColorUtilTest {
    @Test
    public void testToHexString() {
        assertThat(ColorUtil.toHexString(Color.BLACK), is("#000000"));
        assertThat(ColorUtil.toHexString(Color.WHITE), is("#ffffff"));
    }
    
    @Test
    public void testToHexStringWithTransparentColors() {
        assertThat(ColorUtil.toHexString(ColorUtil.setAlpha(Color.BLACK, 0)), is("#000000"));
        assertThat(ColorUtil.toHexString(ColorUtil.setAlpha(Color.WHITE, 0)), is("#ffffff"));
    }
    
    @Test
    public void testBlendWith255() {
        assertThat(ColorUtil.blend(Color.BLACK, Color.WHITE), is(Color.WHITE));
    }
    
    @Test
    public void testBlendWithFiftyPercent() {
        assertThat(ColorUtil.blend(Color.BLACK, ColorUtil.setAlpha(Color.WHITE, 255 >> 1)),
                is(new Color(255 >> 1, 255 >> 1, 255 >> 1)));
    }
    
    @Test
    public void testBlendWithZero() {
        assertThat(ColorUtil.blend(Color.BLACK, ColorUtil.setAlpha(Color.WHITE, 0)), is(Color.BLACK));
    }
    
    @Test
    public void testBlendWithNullSrc() {
        assertThat(ColorUtil.blend(null, Color.WHITE), is(Color.WHITE));
    }
    
    @Test
    public void testBlendWithNullOver() {
        assertThat(ColorUtil.blend(Color.BLACK, null), is(Color.BLACK));
    }
}
