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
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.image.AbstractFilter;

/**
 * @author rbair
 */
public class CompoundPainterTest extends TestCase {
    private Graphics2D g;
    private BufferedImage img;
    private TestableCompoundPainter cp1;
    private TestableFilter f1;
    private TestablePainter p1;
    private TestablePainter p2;
    private TestableFilter f2;
    private TestableCompoundPainter cp2;
    private TestablePainter p3;
    private TestablePainter p4;
    private TestableFilter f3;
    private TestablePainter p5;

    private TestableCompoundPainter onlyCachedPainters;
    
    public void setUp() {
        img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        g = img.createGraphics();
        cp1 = new TestableCompoundPainter();
        f1 = new TestableFilter();
        p1 = new TestablePainter();
        p2 = new TestablePainter();
        f2 = new TestableFilter();
        cp2 = new TestableCompoundPainter();
        p3 = new TestablePainter();
        p4 = new TestablePainter();
        f3 = new TestableFilter();
        p5 = new TestablePainter();

        cp1.setPainters(p1, p2, cp2);
        cp1.setFilters(f1);
        p2.setFilters(f2);
        cp2.setPainters(p3, p4, p5);
        p4.setFilters(f3);

        onlyCachedPainters = new TestableCompoundPainter();
        onlyCachedPainters.setPainters(p2, p4);
        onlyCachedPainters.setFilters(f1);
    }

    public void tearDown() {
        g.dispose();
        img.flush();
    }

    //tests to make sure the cache is not used on the first pass through
    //the painting routines. That is, a cache shouldn't be used unless
    //something has been painted to the cache first.
    public void testCacheNotUsedFirstPass() {
        cp1.paint(g, null, 10, 10);
        assertTrue(cp1.painted);
        assertTrue(f1.filtered);
        assertTrue(p1.painted);
        assertTrue(p2.painted);
        assertTrue(f2.filtered);
        assertTrue(cp2.painted);
        assertTrue(p3.painted);
        assertTrue(p4.painted);
        assertTrue(f3.filtered);
        assertTrue(p5.painted);
    }

    public void testCacheNotUsedFirstPass2() {
        onlyCachedPainters.paint(g, null, 10, 10);
        assertTrue(onlyCachedPainters.painted);
        assertTrue(f1.filtered);
        assertTrue(p2.painted);
        assertTrue(f2.filtered);
        assertTrue(p4.painted);
        assertTrue(f3.filtered);
    }

    public void testCacheUsedSecondPass() {
        cp1.paint(g, null, 10, 10);
        reset();
        cp1.paint(g, null, 10, 10);
        assertFalse(cp1.painted);
        assertFalse(f1.filtered);
        assertFalse(p1.painted);
        assertFalse(p2.painted);
        assertFalse(f2.filtered);
        assertFalse(cp2.painted);
        assertFalse(p3.painted);
        assertFalse(p4.painted);
        assertFalse(f3.filtered);
        assertFalse(p5.painted);
    }

    public void testCacheUsedSecondPass2() {
        onlyCachedPainters.paint(g, null, 10, 10);
        reset();
        onlyCachedPainters.paint(g, null, 10, 10);
        assertFalse(onlyCachedPainters.painted);
        assertFalse(f1.filtered);
        assertFalse(p2.painted);
        assertFalse(f2.filtered);
        assertFalse(p4.painted);
        assertFalse(f3.filtered);
    }

    public void testIfChildPainterIsInvalid() {
        testCacheUsedSecondPass();
        p4.clearCache();
        cp1.paint(g, null, 10, 10);
        assertTrue(cp1.painted);
        assertTrue(f1.filtered);
        assertTrue(p1.painted);
        assertFalse(p2.painted);
        assertFalse(f2.filtered);
        assertTrue(cp2.painted);
        assertTrue(p3.painted);
        assertTrue(p4.painted);
        assertTrue(f3.filtered);
        assertTrue(p5.painted);
    }

    private void reset() {
        cp1.painted = false;
        f1.filtered = false;
        p1.painted = false;
        p2.painted = false;
        f2.filtered = false;
        cp2.painted = false;
        p3.painted = false;
        p4.painted = false;
        f3.filtered = false;
        p5.painted = false;

        onlyCachedPainters.painted = false;
    }

    //tests that compound behaviors, such as caching in compound situations, works
    private static final class TestableCompoundPainter extends CompoundPainter {
        boolean painted = false;
        
        protected void doPaint(Graphics2D g, Object obj, int width, int height) {
            painted = true;
            super.doPaint(g, obj, width, height);
        }

        void reset() {
            painted = false;
        }
    }
}
