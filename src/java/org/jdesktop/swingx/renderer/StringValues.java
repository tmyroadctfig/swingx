/*
 * $Id$
 *
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.renderer;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

/**
 * A collection of common {@code StringValue} implementations.
 * <p>
 * TODO potentially move {@link StringValue#TO_STRING} and
 * {@link StringValue#EMPTY} here
 * 
 * @author Karl George Schaefer
 */
public final class StringValues {
    /**
     * A {@code StringValue} that presents the current L&F display name for a
     * given file. If the value passed to {@code FILE_NAME} is not a
     * {@link File}, this has the same effect as {@link StringValue#EMPTY}.
     */
    @SuppressWarnings("serial")
    public static final StringValue FILE_NAME = new StringValue() {
        public String getString(Object value) {
            if (value instanceof File) {
                FileSystemView fsv = FileSystemView.getFileSystemView();

                return fsv.getSystemDisplayName((File) value);
            }

            return EMPTY.getString(value);
        }
    };

    /**
     * A {@code StringValue} that presents the current L&F type name for a
     * given file. If the value passed to {@code FILE_TYPE} is not a
     * {@link File}, this has the same effect as {@link StringValue#EMPTY}.
     */
    @SuppressWarnings("serial")
    public static final StringValue FILE_TYPE = new StringValue() {
        public String getString(Object value) {
            if (value instanceof File) {
                FileSystemView fsv = FileSystemView.getFileSystemView();
                
                return fsv.getSystemTypeDescription((File) value);
            }
            
            return EMPTY.getString(value);
        }
    };
    
    private StringValues() {
        // does nothing
    }
}
