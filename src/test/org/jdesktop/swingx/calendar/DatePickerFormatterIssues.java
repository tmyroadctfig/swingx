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
 *
 */
package org.jdesktop.swingx.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.plaf.UIManagerExt;

/**
 * Unit tests for <code>DatePickerFormatter</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class DatePickerFormatterIssues extends TestCase {

    /**
     * triggered by
     * Issue #690-swingx: custom dateformats lost on switching LF.
     * 
     * As of code comment: locale setting not taken?
     * 
     */
    public void testPickerFormatterCustomLocale() {
        Locale locale = Locale.FRENCH;
        DatePickerFormatter formatter = new DatePickerFormatter(locale);
        SimpleDateFormat format = (SimpleDateFormat) formatter.getFormats()[0];
        String pattern = UIManagerExt.getString("JXDatePicker.longFormat", locale);
        assertEquals(pattern, format.toPattern());
    }
    
    @SuppressWarnings("unused")
    private Calendar cal;

    public void setUp() {
        cal = Calendar.getInstance();
        // force loading of resources
        new JXDatePicker();
    }

    public void tearDown() {
    }

}
