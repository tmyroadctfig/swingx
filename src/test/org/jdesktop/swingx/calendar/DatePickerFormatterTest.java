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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.DatePickerFormatter.DatePickerFormatterUIResource;
import org.jdesktop.swingx.plaf.UIManagerExt;

/**
 * Unit tests for <code>DatePickerFormatter</code>.
 * 
 * Extracted from JXDatePickerTest.
 * 
 * @author Jeanette Winzenburg
 */
public class DatePickerFormatterTest extends TestCase {

    /**
     * Issue #691-swingx: locale setting not taken.
     * Here: test contructor with locale.
     */
    public void testPickerFormatterCustomLocale() {
        Locale locale = Locale.FRENCH;
        DatePickerFormatter formatter = new DatePickerFormatter(locale);
        SimpleDateFormat format = (SimpleDateFormat) formatter.getFormats()[0];
        String pattern = UIManagerExt.getString("JXDatePicker.longFormat", locale);
        assertEquals("format pattern must be same as from localized resource", 
                pattern, format.toPattern());
    }

    /**
     * Issue #691-swingx: locale setting not taken.
     * Here: test contructor with locale in uiresource.
     */
    public void testPickerFormatterUIResourceCustomLocale() {
        Locale locale = Locale.FRENCH;
        DatePickerFormatter formatter = new DatePickerFormatterUIResource(locale);
        SimpleDateFormat format = (SimpleDateFormat) formatter.getFormats()[0];
        String pattern = UIManagerExt.getString("JXDatePicker.longFormat", locale);
        assertEquals("format pattern must be same as from localized resource", 
                pattern, format.toPattern());
    }
    
    /**
     * Issue #691-swingx: locale setting not taken.
     * Here: test empty contructor == default locale in uiresource.
     */
    public void testPickerFormatterUIResourceDefaultLocale() {
        DatePickerFormatter formatter = new DatePickerFormatterUIResource();
        SimpleDateFormat format = (SimpleDateFormat) formatter.getFormats()[0];
        String pattern = UIManagerExt.getString("JXDatePicker.longFormat");
        assertEquals(pattern, format.toPattern());
    }

    /**
     * Issue #691-swingx: locale setting not taken.
     * Here: test empty contructor == default locale.
     */
    public void testPickerFormatterDefaultLocale() {
        DatePickerFormatter formatter = new DatePickerFormatter();
        SimpleDateFormat format = (SimpleDateFormat) formatter.getFormats()[0];
        String pattern = UIManagerExt.getString("JXDatePicker.longFormat");
        assertEquals(pattern, format.toPattern());
    }

    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: test formatter constructor with empty formats array
     * 
     */
    public void testPickerFormatterEmptyFormats() {
        DateFormat[] formats = new DateFormat[0];
        DatePickerFormatter formatter = new DatePickerFormatter(formats);
        assertNotNull(formatter.getFormats());
        assertEquals(formats.length, formatter.getFormats().length);
    }

    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: test formatter constructor with formats array contain
     * null
     * 
     */
    public void testPickerFormatterConstructorWithNullFormats() {
        DateFormat[] formats = new DateFormat[] { null };
        try {
            new DatePickerFormatter(formats);
            fail("constructor must throw NPE if array contains null formats");
        } catch (NullPointerException e) {
            // doc'ed behaviour
        }
    }


    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: test default constructor
     * 
     */
    public void testPickerFormatterDefaultConstructor() {
        DatePickerFormatter formatter = new DatePickerFormatter();
        assertNotNull(formatter.getFormats());
        assertEquals(3, formatter.getFormats().length);
    }

    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: test constructor with null parameter has same defaults
     *   as parameterless
     * 
     */
    public void testPickerFormatterConstructorWithParameterNull() {
        DatePickerFormatter defaultFormatter = new DatePickerFormatter();
        DateFormat[] defaultFormats = defaultFormatter.getFormats();
        DatePickerFormatter formatter = new DatePickerFormatter((DateFormat[])null);
        DateFormat[] formats = formatter.getFormats();
        assertNotNull("formats must not be null", formats);
        assertEquals(defaultFormats.length, formats.length);
        for (int i = 0; i < defaultFormats.length; i++) {
            assertEquals("format must be equals to default at " + i, 
                    defaultFormats[i], formats[i]);
        }
    }
    
    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: picker formatter must protect itself against 
     *   null formats.
     * 
     */
    public void testPickerFormatterUnsafeGetFormats() {
        DatePickerFormatter picker = new DatePickerFormatter();
        DateFormat[] formats = picker.getFormats();
        formats[0] = null;
        try {
            picker.valueToString(new Date());
        } catch (ParseException e) {
            // doc'ed - but there is no parsing involved?
        }
        // other exceptions are unexpected ...
    }



    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: picker formatter must protect itself against 
     *   empty formats.
     * 
     */
    public void testPickerFormatterEmptyValueToString() {
        DatePickerFormatter picker = new DatePickerFormatter(
                new DateFormat[0]);
        try {
            picker.valueToString(new Date());
        } catch (ParseException e) {
            // doc'ed - but there is no parsing involved?
        }
        // other exceptions are unexpected ...
    }

    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: picker formatter must protect itself against empty formats.
     * 
     */
    public void testPickerFormatterEmptyStringToValue() {
        DatePickerFormatter picker = new DatePickerFormatter(new DateFormat[0]);
        try {
            picker.stringToValue("unparseble");
        } catch (ParseException e) {
            // expected
        }
        // other exceptions are unexpected ...
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
