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
import static org.junit.Assert.assertThat;

import java.awt.Color;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.plaf.PainterUIResource;
import org.jdesktop.test.EDTRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests JXPanel.
 * 
 * @author Karl Schaefer
 */
@RunWith(EDTRunner.class)
public class JXPanelTest {
    /**
     * SwingX #962: ensure that background painter is initially {@code null}.
     * <p>
     * SwingX #964: UI-delegate Painters can hide user-specified background
     * color. No longer return {@code null}, we now pass the background color to
     * the painter. Painter should start as {@code UIResource}.
     */
    @Test
    public void testBackgroundPainterIsUIResource() {
        Painter<?> painter = new JXPanel().getBackgroundPainter();
        
        assertThat(painter, is(instanceOf(UIResource.class)));
    }
    
    /**
     * SwingX #964: ensure setting background color sets painter.
     */
    @Test
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
