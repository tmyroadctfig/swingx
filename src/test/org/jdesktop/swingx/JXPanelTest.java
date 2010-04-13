/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;

import junit.framework.TestCase;

import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.plaf.PainterUIResource;
import org.jdesktop.test.EDTRunner;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests JXPanel.
 * 
 * @author Karl Schaefer
 */
@RunWith(EDTRunner.class)
public class JXPanelTest extends TestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JXPanelTest.class
            .getName());
    
    /**
     * Issue #1199-swingx: must listen to change on painter
     */
    @Test
    public void testPainterChangeListener() {
        JXPanel panel = new JXPanel();
        final MattePainter painter = new MattePainter(Color.RED);
        int listenerCount = painter.getPropertyChangeListeners().length;
        panel.setBackgroundPainter(painter);
        assertEquals(listenerCount +1, painter.getPropertyChangeListeners().length);
        panel.setBackgroundPainter(null);
        assertEquals(listenerCount, painter.getPropertyChangeListeners().length);
    }
    
    @Test
    public void testScrollableWidthTrackProperty() {
        JXPanel panel = new JXPanel();
        ScrollableSizeHint oldTrack = panel.getScrollableWidthHint();
        PropertyChangeReport report = new PropertyChangeReport(panel);
        ScrollableSizeHint none = ScrollableSizeHint.HORIZONTAL_STRETCH;
        panel.setScrollableWidthHint(none);
        assertSame(none, panel.getScrollableWidthHint());
        TestUtils.assertPropertyChangeEvent(report, "scrollableWidthHint", oldTrack, none);
    }
    @Test
    public void testScrollableHeightTrackProperty() {
        JXPanel panel = new JXPanel();
        ScrollableSizeHint oldTrack = panel.getScrollableHeightHint();
        PropertyChangeReport report = new PropertyChangeReport(panel);
        ScrollableSizeHint none = ScrollableSizeHint.VERTICAL_STRETCH;
        panel.setScrollableHeightHint(none);
        assertSame(none, panel.getScrollableHeightHint());
        TestUtils.assertPropertyChangeEvent(report, "scrollableHeightHint", oldTrack, none);
    }
    
    @Test (expected = NullPointerException.class)
    public void testScrollableHeightTrackNull() {
        new JXPanel().setScrollableHeightHint(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testScrollableHeightTrackIllegal() {
        new JXPanel().setScrollableHeightHint(ScrollableSizeHint.HORIZONTAL_STRETCH);
    }
    
    @Test (expected = NullPointerException.class)
    public void testScrollableWidthTrackNull() {
        new JXPanel().setScrollableWidthHint(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testScrollableWidthTrackIllegal() {
        new JXPanel().setScrollableWidthHint(ScrollableSizeHint.VERTICAL_STRETCH);
    }
    
    /**
     * Initial value (for backward compatibility: FIT)
     * 
     */
    @Test
    public void testScrollableSizeTrackProperty() {
        JXPanel panel = new JXPanel();
        assertSame(ScrollableSizeHint.FIT, panel.getScrollableWidthHint());
        assertSame(ScrollableSizeHint.FIT, panel.getScrollableHeightHint());
    }
    
    /**
     * Sanity: compatibility for width tracking.
     */
    @Test
    public void testScrollableTracksWidth() {
        JXPanel panel = new JXPanel();
        assertTrue(panel.getScrollableTracksViewportWidth());
        panel.setScrollableTracksViewportWidth(false);
        assertFalse(panel.getScrollableTracksViewportWidth());
    }
    
    /**
     * Sanity: compatibility for height tracking.
     */
    @Test
    public void testScrollableTracksHeight() {
        JXPanel panel = new JXPanel();
        assertTrue(panel.getScrollableTracksViewportHeight());
        panel.setScrollableTracksViewportHeight(false);
        assertFalse(panel.getScrollableTracksViewportHeight());
    }

//----------------- test scrollable Size Track
    
    @Test
    public void testOrientationCompatible() {
        assertVerticalCompatible(true, ScrollableSizeHint.NONE, ScrollableSizeHint.FIT, 
                ScrollableSizeHint.VERTICAL_STRETCH);
        assertVerticalCompatible(false, ScrollableSizeHint.HORIZONTAL_STRETCH);
        assertHorizontalCompatible(true, ScrollableSizeHint.NONE, ScrollableSizeHint.FIT, 
                ScrollableSizeHint.HORIZONTAL_STRETCH);
        assertHorizontalCompatible(false, ScrollableSizeHint.VERTICAL_STRETCH);
    }
    /**
     * 
     */
    private void assertVerticalCompatible(boolean compatible, ScrollableSizeHint... tracks) {
        for (ScrollableSizeHint track : tracks) {
            assertEquals("vertical expected on " + track, compatible, track.isVerticalCompatible());
        }
    }
    /**
     * 
     */
    private void assertHorizontalCompatible(boolean compatible, ScrollableSizeHint... tracks) {
        for (ScrollableSizeHint track : tracks) {
            assertEquals("horizontal expected on " + track, compatible, track.isHorizontalCompatible());
        }
    }

    /**
     * Test contract - NPE on null component
     */
    @Test 
    public void testScrollableSizeTrackNPE() {
        for (ScrollableSizeHint behaviour : ScrollableSizeHint.values()) {
            try {
                behaviour.getTracksParentSize(null);
                fail("null component must throw NPE, didn't on " + behaviour);
            } catch (NullPointerException e) {
                // expected
            }
        }
    }
  
    
    
    /**
     * SwingX #962: ensure that background painter is initially {@code null}.
     * <p>
     * Added this test with the rollback of changes for SwingX #964. Remove when
     * #964 is solved.
     */
    @Test
    public void testBackgroundPainterIsNull() {
        Painter<?> painter = new JXPanel().getBackgroundPainter();
        
        assertThat(painter, is(nullValue()));
    }
    
    /**
     * SwingX #962: ensure that background painter is initially {@code null}.
     * <p>
     * SwingX #964: UI-delegate Painters can hide user-specified background
     * color. No longer return {@code null}, we now pass the background color to
     * the painter. Painter should start as {@code UIResource}.
     */
    @Test
    @Ignore("reactivate with #964")
    public void testBackgroundPainterIsUIResource() {
        Painter<?> painter = new JXPanel().getBackgroundPainter();
        
        assertThat(painter, is(instanceOf(UIResource.class)));
    }
    
    /**
     * SwingX #964: ensure setting background color sets painter.
     */
    @Test
    @Ignore("reactivate with #964")
    public void testSetBackgroundSetsPainter() {
        JXPanel panel = new JXPanel();
        
        //assure painter is null
        panel.setBackgroundPainter(null);
        
        panel.setBackground(Color.BLACK);
        
        assertThat(panel.getBackgroundPainter(), is(notNullValue()));
    }
    
    /**
     * SwingX #964: ensure setting background color sets painter with a {@code
     * UIResource} set the background painter with a {@code UIResource} if the
     * background painter is {@code null} or a {@code UIResource}.
     */
    @Test
    @SuppressWarnings("unchecked")
    @Ignore("reactivate with #964")
    public void testSetBackgroundWithUIResourceSetsPainterWithUIResource() {
        JXPanel panel = new JXPanel();
        
        //assure painter is null
        panel.setBackgroundPainter(null);
        
        panel.setBackground(new ColorUIResource(Color.BLACK));
        
        assertThat(panel.getBackgroundPainter(), is(instanceOf(UIResource.class)));
        
        Painter myResource = new PainterUIResource(new MattePainter(Color.BLACK));
        panel.setBackgroundPainter(myResource);
        
        panel.setBackground(new ColorUIResource(Color.BLACK));
        
        assertThat(panel.getBackgroundPainter(), is(instanceOf(UIResource.class)));
        assertThat(panel.getBackgroundPainter(), is(not(myResource)));
    }
}
