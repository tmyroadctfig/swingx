/*
 * $Id$
 * 
 * Copyright 2005 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import javax.swing.JFormattedTextField;
import javax.swing.UIManager;

import org.jdesktop.swingx.util.Contract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Default formatter for the JXDatePicker component.  
 * It can handle a variety of date formats.
 *
 * @author Joshua Outwater
 */
public class JXDatePickerFormatter extends
        JFormattedTextField.AbstractFormatter {
    private DateFormat _formats[] = null;

    /**
     * Instantiates a formatter with the localized format patterns defined
     * in the swingx.properties.
     * 
     * These formats are localizable and fields may be re-arranged, such as
     * swapping the month and day fields.  The keys for localizing these fields
     * are:
     * <ul>
     * <li>JXDatePicker.longFormat
     * <li>JXDatePicker.mediumFormat
     * <li>JXDatePicker.shortFormat
     * </ul>
     *
     */
    public JXDatePickerFormatter() {
        this(null);
    }

    /**
     * Instantiates a formatter with the given date formats. If the 
     * array is null, default formats are created from the localized
     * patterns in swingx.properties. If empty?
     * 
     * @param formats the array of formats to use. May be null to 
     *   use defaults or empty to do nothing (?), but must not contain
     *   null formats.
     */
    public JXDatePickerFormatter(DateFormat formats[]) {
        if (formats == null) {
            formats = createDefaultFormats();
        }
        Contract.asNotNull(formats, "The array of DateFormats must not contain null formats");
        _formats = formats;
    }

    /**
     * PENDING: return defensive copy ...
     * 
     * @return the formats used by this formatter, guaranteed to be
     *   not null.
     */
    public DateFormat[] getFormats() {
        return _formats;
    }

    /**
     * {@inheritDoc}
     */
    public Object stringToValue(String text) throws ParseException {
        Object result = null;
        ParseException pex = null;

        if (text == null || text.trim().length() == 0) {
            return null;
        }

        // If the current formatter did not work loop through the other
        // formatters and see if any of them can parse the string passed
        // in.
        for (DateFormat _format : _formats) {
            try {
                result = (_format).parse(text);
                pex = null;
                break;
            } catch (ParseException ex) {
                pex = ex;
            }
        }

        if (pex != null) {
            throw pex;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String valueToString(Object value) throws ParseException {
        if ((value != null) && (_formats.length > 0)){
            return _formats[0].format(value);
        }
        return null;
    }
    
    /**
     * Creates and returns the localized default formats.
     * 
     * @return the localized default formats.
     */
    protected DateFormat[] createDefaultFormats() {
        DateFormat[] formats;
        formats = new DateFormat[3];
        formats[0] = new SimpleDateFormat(UIManager.getString("JXDatePicker.longFormat"));
        formats[1] = new SimpleDateFormat(UIManager.getString("JXDatePicker.mediumFormat"));
        formats[2] = new SimpleDateFormat(UIManager.getString("JXDatePicker.shortFormat"));
        return formats;
    }


}