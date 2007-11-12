/*
 * Created on 23.02.2007
 *
 */
package org.jdesktop.swingx;

import java.util.Locale;

import javax.swing.UIManager;

import org.jdesktop.swingx.resources.swingx;

/**
 * Standalone example for problems with application resourceBundle. <p>
 * 
 * Run in jdk5: the found resource value is always in the system's default
 * locale. <p>
 * 
 * Run in jdk6: the found resource value is correctly localized.
 * 
 * @author Karl Schaefer
 */
public class UIDefaultsResourceBundleCheck {
    /**
     * @param args
     *                unused
     */
    public static void main(String[] args) {
        UIManager.getDefaults().addResourceBundle(
                swingx.class.getName());
        System.out.println(UIManager.getString("LoginPane.1"));
        System.out.println(UIManager.getString("LoginPane.1", Locale.US));
        System.out.println(UIManager.getString("LoginPane.1", Locale.GERMAN));
        System.out.println(UIManager.getString("LoginPane.1", Locale.GERMANY));
        System.out.println(UIManager.getString("LoginPane.1", Locale.FRENCH));
        System.out.println(UIManager.getString("LoginPane.1", Locale.FRANCE));
        System.out.println(UIManager.getString("LoginPane.1", Locale.CANADA_FRENCH));
        System.out.println(UIManager.getString("LoginPane.1", new Locale("pt")));
        System.out.println(UIManager.getString("LoginPane.1", new Locale("pt", "BR")));
        System.out.println(UIManager.getString("LoginPane.1", Locale.KOREAN));
    }
}
