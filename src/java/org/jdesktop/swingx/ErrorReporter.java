/*
* $Id$
*
* Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
* Santa Clara, California 95054, U.S.A. All rights reserved.
*/

/**
 * Abstract class contains all the declarations that needs to be
 * implemented to add error reporting to sdandart JXErrorDislog.
 *
 * @author Alexander Zuev
 * @version 1.0
 */
package org.jdesktop.swingx;

public abstract class ErrorReporter {
    /**
     * This method will be called if user clicks 'report' button
     * on the error dialog.
     *
     * @param info <code>IncidentInfo</code> that incapsulates all the information
     *        system wants to report using this report facility.
     */
    public abstract void reportIncident(IncidentInfo info);

    /**
     * This method allows on-the-fly configuration of the 'report' button.
     * If this method returns non-null value this string will replace default
     * text on this button.
     *
     * @return string to be put on 'report' button or null for default string.
     */
    public String getActionName() {
        return null;
    }
}
