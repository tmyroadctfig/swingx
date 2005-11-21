/*
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Default formatter for the JXDatePicker component.  This factory
 * creates and returns a formatter that can handle a variety of date
 * formats.
 *
 * @author Joshua Outwater
 */
public class JXDatePickerFormatter extends
        JFormattedTextField.AbstractFormatter {
    private DateFormat _formats[] = null;

    public JXDatePickerFormatter() {
        _formats = new DateFormat[3];
        _formats[0] = new SimpleDateFormat(UIManager.getString("JXDatePicker.longFormat"));
        _formats[1] = new SimpleDateFormat(UIManager.getString("JXDatePicker.mediumFormat"));
        _formats[2] = new SimpleDateFormat(UIManager.getString("JXDatePicker.shortFormat"));
    }

    public JXDatePickerFormatter(DateFormat formats[]) {
        _formats = formats;
    }

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
        if (value != null) {
            return _formats[0].format(value);
        }
        return null;
    }
}