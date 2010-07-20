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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jdesktop.beans.ClassSearchUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for AbstractPainter
 */
@RunWith(JUnit4.class)
public class AbstractPainterTest extends TestCase {
    private Graphics2D g;
    private BufferedImage img;
    private TestablePainter p;
    private TestableFilter filter;

    @Override
    @Before
       public void setUp() {
        p = new TestablePainter();
        img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        g = img.createGraphics();
        filter = new TestableFilter();
    }

    @Override
    @After
       public void tearDown() {
        g.dispose();
        img.flush();
    }

    @Test
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

        //test that configureGraphics is called in all painting situations
        p.reset();
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        assertTrue(p.configured);
        assertTrue(p.configureCalledFirst);
    }

    @Test
    public void testCaching() {
        p.setCacheable(true);
        // empty run to fill the cache
        p.paint(g, null, 10, 10);
        p.painted = false;
        //test that the cache is always used UNLESS shouldUseCache is false, or isCacheCleared is true, or the Painter is dirty
        p.paint(g, null, 10, 10);
        assertEquals(!p.shouldUseCache() || p.isCacheCleared() || p.isDirty(), p.painted);
        
        // disable cache
        p.reset();
        // setting filters means shouldUseCache() will ALWAYS return true!!!
        p.setFilters(filter);
        p.paint(g, null, 10, 10);
        assertEquals(p.shouldUseCache() || p.isCacheCleared() || p.isDirty(), p.painted);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertEquals(!p.shouldUseCache() || p.isCacheCleared() || p.isDirty(), p.painted);
        p.clearCache();
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertEquals(p.shouldUseCache() || p.isCacheCleared() || p.isDirty(), p.painted);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertEquals(!p.shouldUseCache() || p.isCacheCleared() || p.isDirty(), p.painted);

        //test that a cache is not used unless cacheable is true OR filters are set
        p.reset();
        p.setCacheable(true);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertFalse(p.painted);

        p.reset();
        p.setCacheable(false);
        p.setFilters(filter);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertFalse(p.painted);

        p.reset();
        p.setCacheable(true);
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
        p.setFilters(filter);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertFalse(p.painted);
        p.clearCache();
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);

        //test that the cache is NOT used if the width/height changes
        p.reset();
        p.setCacheable(true);
        p.setFilters(filter);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, null, 5, 5);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, null, 5, 5);
        assertFalse(p.painted);

        //test that the cache is NOT used if the public state changes
        p.reset();
        p.setCacheable(true);
        p.setFilters(filter);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.setAntialiasing(false); //these three lines are used to make SURE the state has _changed_ to false
        p.setAntialiasing(true);
        p.setAntialiasing(false);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.setAntialiasing(true);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.setAntialiasing(true); //ah! the state has not _really_ changed
        p.paint(g, null, 10, 10);
        assertFalse(p.painted);

        //test that the cache is not used if dependent state has changed. For this
        //test, the TestablePainter remembers the last "Object" it was sent, and
        //does an == comparison to see if the state has changed
        p.reset();
        p.setCacheable(true);
        p.setFilters(filter);
        p.paint(g, null, 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, null, 10, 10);
        assertFalse(p.painted);
        p.painted = false;
        p.paint(g, "hi!", 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, "Duke", 10, 10);
        assertTrue(p.painted);
        p.painted = false;
        p.paint(g, "Duke", 10, 10);
        assertFalse(p.painted);

        //using a TestableCompoundPainter, ensure that the cache is cleared if one of
        //the children's cache is cleared
        //setup...
        TestableCompoundPainter c = new TestableCompoundPainter();
        TestablePainter p1 = new TestablePainter();
        TestablePainter p2 = new TestablePainter();
        TestableFilter f2 = new TestableFilter();
        c.setPainters(p1, p2);
        p2.setFilters(f2);
        c.setFilters(filter);

        //ok, first time through, all 3 painters should be called, and both filters should be called
        c.paint(g, null, 10, 10);
        assertTrue(c.painted);
        assertTrue(filter.filtered);
        assertTrue(p1.painted);
        assertTrue(p2.painted);
        assertTrue(f2.filtered);

        c.painted = false;
        filter.filtered = false;
        p1.painted = false;
        p2.painted = false;
        f2.filtered = false;

        //next pass, nothing should be called (cache in c should be used)
        c.paint(g, null, 10, 10);
        assertFalse(c.painted);
        assertFalse(filter.filtered);
        assertFalse(p1.painted);
        assertFalse(p2.painted);
        assertFalse(f2.filtered);

        //next pass, cause c to be invalidated. This should cause c, filter, and p1 to be called,
        //but p2 should still be cached
        c.clearCache();
        c.paint(g, null, 10, 10);
        assertTrue(c.painted);
        assertTrue(filter.filtered);
        assertTrue(p1.painted);
        assertFalse(p2.painted);
        assertFalse(f2.filtered);

        c.painted = false;
        filter.filtered = false;
        p1.painted = false;
        p2.painted = false;
        f2.filtered = false;

        //finally, cause p2 to be invalidated. This should cause c, filter, p1, p2, and f2 to all be called
        p2.clearCache();
        c.paint(g, null, 10, 10);
        assertTrue(c.painted);
        assertTrue(filter.filtered);
        assertTrue(p1.painted);
        assertTrue(p2.painted);
        assertTrue(f2.filtered);

        c.painted = false;
        filter.filtered = false;
        p1.painted = false;
        p2.painted = false;
        f2.filtered = false;
    }

    @Test
    public void testVisibilityOfOriginallyProtectedMethodsInExtendingClasses() {
        List<Class<?>> allClasses = ClassSearchUtils.searchClassPath("org.jdesktop.swingx.");
        List<Class<?>> painters = new ArrayList<Class<?>>();
        for (Class<?> clazz : allClasses) {
            try {
                if (clazz.isAnonymousClass()) {
                    // skip inner classes as those expose methods for specific purpose (either tests of to overcome some implementation issue)
                    continue;
                }
                painters.add(clazz.asSubclass(AbstractPainter.class));
            } catch (ClassCastException e) {
                // ignore
            } catch (NoClassDefFoundError e) {
                // ignore
                // TODO: find out why class loading fails with this exception even if it was found on a classpath earlier
            }
        }
        // collect all protected methods
        Method[] methods = AbstractPainter.class.getDeclaredMethods();
        List<Method> mets = new ArrayList<Method>();
        for (Method m : methods) {
            if (Modifier.isProtected(m.getModifiers())) {
                mets.add(m);
            }
        }
        // check that the methods are not made public
        for (Class<?> clazz : painters) {
            for (Method m : mets) {
                try {
                    assertTrue("Class " + clazz.getName() + " must keep overriden AbstractPainter method " + m.getName() + " visibility intact.", Modifier.isProtected(clazz.getDeclaredMethod(m.getName(), m.getParameterTypes()).getModifiers()));
                } catch (NoSuchMethodException e) {
                    // not declared - ignore
                }
            }
        }
    }
    
    /**
     * Issue #??-swingx: clearCache has no detectable effect. Test was poorly designed. It has had
     * an effect for a long time, but the member is not bound, so the test was failing erroneously.
     * 
     * @throws IOException
     */
    public void testClearCacheDetectable() throws IOException {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        AbstractPainter<Object> painter = new ShapePainter() {
            @Override
            protected boolean shouldUseCache() {
                return isCacheable();
            }

        };
        assertFalse("cacheable is false by default", painter.isCacheable());
        painter.setCacheable(true);
        painter.paint(g, null, 10, 10);
        // sanity
        assertFalse("clean after paint", painter.isDirty());
        assertTrue("cacheable is enabled", painter.isCacheable());
        assertFalse("has a cached image", painter.isCacheCleared());
        painter.clearCache();
        assertTrue("has a cached image", painter.isCacheCleared());
    }
    
    //tests that compound behaviors, such as caching in compound situations, works
    //I don't extend CompoundPainter because I don't want to test the CompoundPainter
    //itself, just test the general concepts that go into *any* aggregate Painter
    //implementation    
    private static final class TestableCompoundPainter extends TestablePainter {
        TestablePainter[] painters;
        public void setPainters(TestablePainter... painters) {
            this.painters = painters;
        }

        @Override
        protected void validate(Object object) {
            super.validate(object);
            //iterate over all of the painters and query them to see if they
            //are valid. The first invalid one clears the cache and returns.
            for (TestablePainter p : painters) {
                p.validate(object);
                if ((p.shouldUseCache() && p.isCacheCleared()) || p.isDirty()) {
                    setDirty(true);
                    clearCache();
                    return;
                }
            }
        }

        @Override
        protected void doPaint(Graphics2D g, Object obj, int width, int height) {
            super.doPaint(g, obj, width, height);
            for (TestablePainter p : painters) {
                p.paint(g, obj, width, height);
            }
        }
    }
}
