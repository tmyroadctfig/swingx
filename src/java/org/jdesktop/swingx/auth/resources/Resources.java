/*
 * Created on Jan 27, 2005
 *
 */
package org.jdesktop.swingx.auth.resources;

import java.util.ListResourceBundle;

/**
 * @author Bino George
 *
 */
public class Resources extends ListResourceBundle {
	 private static final Object[][] contents = {
        {"okString", "OK"},
        {"okString.mnemonic", (int) 'O'},
        {"cancelString", "Cancel"},
        {"cancelString.mnemonic", (int) 'C'},
        {"nameString", "Name"},
        {"loginString", "Login"},    
        {"passwordString", "Password"},
        {"rememberUserString", "Remember Me"},
        {"rememberPasswordString", "Remember Password"},
        {"loggingInString", "Logging in.."},
        
    };

    public Object[][] getContents() {
        return contents;
    }
}
