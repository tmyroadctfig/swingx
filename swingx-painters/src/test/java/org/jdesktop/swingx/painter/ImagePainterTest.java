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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;


/**
 * Test for ImagePainter.
 */
public class ImagePainterTest extends AbstractAreaPainterTest {
    /**
     * {@inheritDoc}
     */
    @Override
    protected ImagePainter createTestingPainter() {
        return new ImagePainter();
    }
    
    @Test
    @Override
    public void testDefaults() {
        super.testDefaults();
        
        ImagePainter ip = (ImagePainter) p;
        assertThat(ip.getImage(), is(nullValue()));
        assertThat(ip.getImageScale(), is(1d));
        assertThat(ip.getScaleType(), is(ImagePainter.ScaleType.InsideFit));
        assertThat(ip.isHorizontalRepeat(), is(false));
        assertThat(ip.isScaleToFit(), is(false));
        assertThat(ip.isVerticalRepeat(), is(false));
    }
}
