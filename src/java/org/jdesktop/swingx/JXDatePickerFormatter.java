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