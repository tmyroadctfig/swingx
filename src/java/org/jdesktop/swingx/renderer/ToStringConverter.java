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


/**
 * A simplistic converter to return a String representation of an object. <p>
 * 
 * PENDING: use a  full-fledged Format instead? Would impose a higher 
 * burden onto implementors but could be re-used in editors.
 * 
 * @author Jeanette Winzenburg
 */
public interface ToStringConverter {
    
    public final static ToStringConverter TO_STRING = new ToStringConverter() {

        /**
         * {@inheritDoc} <p>
         * 
         * Implemented to return the values toString if value not-null. Otherwise,
         * returns an empty string.
         */
        public String getStringValue(Object value) {
            return (value != null) ? value.toString() : "";
        }
        
    };
    
    /**
     * Returns a string representation of the given value.
     * @param value the object to present as a string
     * @return a string representation of the given value, 
     *  guaranteed to be not null
     */
    String getStringValue(Object value);
}
