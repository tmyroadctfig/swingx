/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
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
