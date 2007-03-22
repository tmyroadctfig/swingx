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

import junit.framework.TestCase;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

import org.jdesktop.swingx.image.AbstractFilter;

/**
 * Test for AbstractPainter
 */
public class AbstractPainterTest extends TestCase {
    private Graphics2D g;
    private BufferedImage img;
    private TestablePainter p;
    private TestableFilter filter;

    public void setUp() {
        p = new TestablePainter();
        img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        g = img.createGraphics();
        filter = new TestableFilter();
    }

    public void tearDown() {
        g.dispose();
        img.flush();
    }

    public void testPaint() {
        //test that an NPE is thrown if Graphics is null
        try {
            p.paint(null, null, 10, 10);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        //test that painting works if param object is null
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);

        //test that painting works if param object is specified
        p.reset();
        p.paint(g, "yo", 10, 10);
        assertTrue(p.painted);

        //test that no painting occurs if width and/or height is <= 0
        p.reset();
        p.paint(g, null, 0, 10);
        assertFalse(p.painted);
        p.paint(g, null, 10, 0);
        assertFalse(p.painted);
        p.paint(g, null, -1, 10);
        assertFalse(p.painted);
        p.paint(g, null, 10, -1);
        assertFalse(p.painted);
        p.paint(g, null, 0, 0);
        assertFalse(p.painted);

        //test that no painting occurs if visible is false
        p.reset();
        p.setVisible(false);
        p.paint(g, null, 10, 10);
        assertFalse(p.painted);
        p.setVisible(true);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);

        //test that a cache is used if I ask for it
        p.reset();
        p.setCacheable(true);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertFalse(p.painted);

        //test that a cache is NOT used if I don't want it
        p.reset();
        p.setCacheable(false);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);

        //test that a cache is used if I use filters
        p.reset();
        p.setFilters(filter);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertFalse(p.painted);

        //test that filters work
        p.reset();
        filter.reset();
        p.setFilters(filter);
        p.paint(g, null, 10, 10);
        assertTrue(filter.filtered);

        //test that clearing the cache works (causes the next paint to work)
        p.reset();
        p.setCacheable(true);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertFalse(p.painted);
        p.clearCache();
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);

        //test that configureGraphics is called in all painting situations
        p.reset();
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        assertTrue(p.configured);
        assertTrue(p.configureCalledFirst);
    }

    private static final class TestablePainter extends AbstractPainter {
        boolean painted = false;
        boolean configured = false;
        boolean configureCalledFirst = false;
        protected void doPaint(Graphics2D g, Object component, int width, int height) {
            painted = true;
        }
        protected void configureGraphics(Graphics2D g) {
            configured = true;
            configureCalledFirst = configured && !painted;
        }

        void reset() {
            painted = false;
            configured = false;
            setCacheable(false);
            clearCache();
            setFilters((BufferedImageOp[])null);
        }
    }

    private static final class TestableFilter extends AbstractFilter {
        boolean filtered = false;
        public BufferedImage filter(BufferedImage src, BufferedImage dest) {
            filtered = true;
            return src;
        }

        void reset() {
            filtered = false;
        }
    }
}
