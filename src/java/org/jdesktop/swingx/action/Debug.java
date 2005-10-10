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

package org.jdesktop.swingx.action;

/**
 * Simple class that holds the value of the debug flag. 
 * use Debug.setDebug(true). Can be passed as an applet value.
 *
 * Should figure a way to set the default value ie.
 *
 * debug = Boolean.valueOf(System.getProperty("debug")).booleanValue();
 *
 * However, System.getProperty is not allowed by the applet security model.
 * 
 * TODO: This should also contain the logger.
 */
class Debug {

    public static boolean debug = false;

    public static void setDebug(boolean isdebug) {
	debug = isdebug;
    }

    public static boolean isDebug() {
	return debug;
    }

    /**
     * A helper method to print exceptions
     * TODO: should probabaly use the logger API.
     */
    public static void printException(String message, Exception ex) {
	System.out.println("Exception encountered: " + message);
	if (isDebug()) {
	    ex.printStackTrace();
	}
    }
}
