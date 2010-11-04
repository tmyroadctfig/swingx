/*
 * $Id$
 *
 * Copyright 2010 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.plaf.linux;

import static org.jdesktop.swingx.util.Contract.asNotNull;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * 
 * @author kschaefer
 * @deprecated replaced by SafeBorder in package plaf
 */
@Deprecated
class SafeBorder implements Border {
    private Border delegate;
    
    public SafeBorder(Border delegate) {
        this.delegate = asNotNull(delegate, "delegate cannot be null");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Insets getBorderInsets(Component c) {
        JLabel label = c instanceof JLabel ? (JLabel) c : new JLabel();
        
        //Synth requires the component to be a JLabel
        return delegate.getBorderInsets(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBorderOpaque() {
        return delegate.isBorderOpaque();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        delegate.paintBorder(c, g, x, y, width, height);
    }

    Border getDelegate() {
        return delegate;
    }
}
