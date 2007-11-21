/*
 * $Id$
 * 
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.painter.Painter;

/**
 * A specialty "list" for working with UI defaults. Requires adds to be done
 * using key/value pairs. The purpose of this list is to enforce additions as
 * pairs.
 * <p>
 * {@code DefaultsList} validates the {@code value} objects to ensure that
 * {@code UIResource}s are used where appropriate. By default any {@code value}
 * that could be a {@code UIResource} and is not is logged as a warning.
 * However, if the system property {@code "swingx.enableStrictResourceChecking"}
 * is {@code true}, then a runtime exception is thrown instead. This more
 * strigent checking is useful when initially configuring UI delegates or when
 * creating test cases.
 * 
 * @author Karl George Schaefer
 */
public final class DefaultsList {
    private static final Logger LOG = Logger.getLogger(DefaultsList.class.getName());

    private List<Object> delegate;

    /**
     * Creates a {@code DefaultsList}.
     */
    public DefaultsList() {
        delegate = new ArrayList<Object>();
    }

    /**
     * Adds a key/value pair to the defaults list.
     * 
     * @param key
     *                the key that will be used to query {@code UIDefaults}
     * @param value
     *                the value associated with the key
     * @throws IllegalArgumentException
     *                 if {@code value} is a type that should be a
     *                 {@code UIResource} but is not. For instance, passing in a
     *                 {@code Border} that is not a {@code UIResource} will
     *                 cause an exception.  This checking must be enabled.
     */
    public void add(Object key, Object value) {
        delegate.add(key);
        delegate.add(asUIResource(value, value + " must be a UIResource"));
    }

    //TODO move to Contract?
    private static <T> T asUIResource(T value, String message) {
        if (!(value instanceof UIResource)) {
            boolean shouldThrow = false;
            
            shouldThrow |= value instanceof ActionMap;
            shouldThrow |= value instanceof Border;
            shouldThrow |= value instanceof Color;
            shouldThrow |= value instanceof Dimension;
            shouldThrow |= value instanceof Font;
            shouldThrow |= value instanceof Icon;
            shouldThrow |= value instanceof InputMap;
            shouldThrow |= value instanceof Insets;
            shouldThrow |= value instanceof Painter;
            
            if (shouldThrow) {
                if (Boolean.getBoolean("swingx.enableStrictResourceChecking")) {
                    throw new IllegalArgumentException(message);
                } else {
                    //where's the debug level?
                    LOG.log(Level.WARNING, message);
                }
            }
        }
        
        return value;
    }
    
    /**
     * Gets a copy of this list as an array.
     * 
     * @return an array containing all of the key/value pairs added to this list
     */
    public Object[] toArray() {
        return delegate.toArray();
    }
}
