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
