package org.jdesktop.swingx.renderer;

import javax.swing.Icon;

/**
 * Compound implementation of both StringValue and IconValue. <p>
 * 
 * Quick hack around #590-swingx: LabelProvider should respect StringValue
 * when formatting (instead of going clever with icons).
 * 
 * Note: this will change!
 */
public class StringIconValue implements StringValue, IconValue {

    private StringValue stringDelegate;
    private IconValue iconDelegate;

    public StringIconValue(StringValue stringDelegate, IconValue iconDelegate) {
        this.stringDelegate = stringDelegate;
        this.iconDelegate = iconDelegate;
    }
    public String getString(Object value) {
        if (stringDelegate != null) {
            return stringDelegate.getString(value);
        }
        return "";
    }

    public Icon getIcon(Object value) {
        if (iconDelegate != null) {
            return iconDelegate.getIcon(value);
        }
        return null;
    }
    
}