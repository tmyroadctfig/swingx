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
package org.jdesktop.swingx.renderer;

import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;

/**
 * Base type for Format-backed ToStringConverters. Has static defaults
 * for Date and Number. <p>
 * 
 * PENDING: need to update on Locale change? How to detect? When?
 * 
 * @author Jeanette Winzenburg
 */
public class FormatToStringConverter implements ToStringConverter {
    /**
     * Default converter for <code>Date</code> types. Uses the default format
     * as returned from <code>DateFormat</code>.
     */
    public final static FormatToStringConverter DATE_TO_STRING = new FormatToStringConverter() {
        
        /**
         * {@inheritDoc}
         */
        public String getStringValue(Object value) {
            if (format == null) {
                format = DateFormat.getDateInstance();
            }
            return super.getStringValue(value);
        }
        
    };
    
    /**
     * Default converter for <code>Number</code> types. Uses the default format
     * as returned from <code>NumberFormat</code>.
     */
    public final static FormatToStringConverter NUMBER_TO_STRING = new FormatToStringConverter() {
        
        /**
         * {@inheritDoc}
         */
        public String getStringValue(Object value) {
            if (format == null) {
                format = NumberFormat.getNumberInstance();
            }
            return super.getStringValue(value);
        }
        
    };

    /** the format used in creating the String representation. */
    protected Format format;

    /**
     * Instantiates a formatted converter with null format.
     *
     */
    public FormatToStringConverter() {
        this(null);
    }
    
    /**
     * Instantiates a formatted converter with the given Format.
     * 
     * @param format the format to use in creating the String representation.
     */
    public FormatToStringConverter(Format format) {
       this.format = format; 
    }
    
    /**
     * 
     * @return the format used in creating the String representation.
     */
    public Format getFormat() {
        return format;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getStringValue(Object value) {
        if (value == null) return "";
        if (format != null) {
            try {
                return format.format(value);
            } catch (IllegalArgumentException e) {
                // didn't work, nothing we can do
            }
        }
        return value.toString();
    }

}
