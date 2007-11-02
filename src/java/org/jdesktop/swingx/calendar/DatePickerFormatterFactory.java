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
package org.jdesktop.swingx.calendar;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Default formatter factory for the JXDatePicker component.  This factory
 * creates and returns a formatter that can handle a variety of date formats.
 *
 * @author Joshua Outwater
 */
public class DatePickerFormatterFactory extends AbstractFormatterFactory {
    /** Cached formatter */
    protected AbstractFormatter formatter = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractFormatter getFormatter(JFormattedTextField ftf) {
        if (formatter == null) {
            formatter = new DatePickerFormatter();
        }
        return formatter;
    }

    /**
     * Default formatter class for the JXDatePicker component.  This formatter
     * supports the following three default formats:
     * <ul>
     * <li>EEE MM/dd/yyyy (Fri 04/09/2004)
     * <li>MM/dd/yyyy     (04/09/2004)
     * <li>dd/yy          (04/09)
     * </ul>
     * These formats are localizable and fields may be re-arranged, such as
     * swapping the month and day fields.  The keys for localizing these fields
     * are:
     * <ul>
     * <li>JXDatePicker.longFormat
     * <li>JXDatePicker.mediumFormat
     * <li>JXDatePicker.shortFormat
     * </ul>
     * It is important to order the formats in the order of most complex to
     * least complex as it is possible for less complex formats to match more
     * complex strings.
     */
    private class DatePickerFormatter extends
            JFormattedTextField.AbstractFormatter {
        private SimpleDateFormat _formats[] = null;
        private int _formatIndex = 0;
        
        public DatePickerFormatter() {
            _formats = new SimpleDateFormat[3];
            String format = UIManager.getString("JXDatePicker.longFormat");
            if (format == null) {
                format = "EEE MM/dd/yyyy";
            }
            _formats[0] = new SimpleDateFormat(format);

            format = UIManager.getString("JXDatePicker.mediumFormat");
            if (format == null) {
                format = "MM/dd/yyyy";
            }
            _formats[1] = new SimpleDateFormat(format);

            format = UIManager.getString("JXDatePicker.shortFormat");
            if (format == null) {
                format = "MM/dd";
            }
            _formats[2] = new SimpleDateFormat(format);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object stringToValue(String text) throws ParseException {
            Object result = null;
            ParseException pex = null;

            if (text == null || text.trim().length() == 0) {
                return null;
            }

            // If the current formatter did not work loop through the other
            // formatters and see if any of them can parse the string passed
            // in.
            for (int i = 0; i < _formats.length; i++) {
                try {
                    result = _formats[i].parse(text);

                    // We got a successful formatter.  Update the current
                    // formatter index.
                    _formatIndex = i;
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
        @Override
        public String valueToString(Object value) {
            if (value != null) {
                return _formats[_formatIndex].format(value);
            }
            return null;
        }
    }
}
