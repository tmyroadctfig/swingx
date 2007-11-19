/**
 * 
 */
package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.util.Contract;

/**
 * A utility class for obtaining configuration properties from the
 * {@code UIDefaults}. This class handles SwingX-specific L&F needs, such as
 * the installation of painters.
 * <p>
 * The {@code getSafeXXX} methods are designed for use with
 * {@code LookAndFeelAddon}s. Any addon that attempts to obtain a property
 * defined in the defaults (available from {@code UIManager.get}) to set a
 * property that will be added to the defaults for the addon should use the
 * "safe" methods. The methods ensure that a valid value is always returned.
 * 
 * @author Karl George Schaefer
 * 
 * @see UIManager
 * @see UIDefaults
 */
public class UIManagerExt {
    private static class UIDefaultsExt {
        //use vector; we want synchronization
        private Vector<String> resourceBundles;

        /**
         * Maps from a Locale to a cached Map of the ResourceBundle. This is done
         * so as to avoid an exception being thrown when a value is asked for.
         * Access to this should be done while holding a lock on the
         * UIDefaults, eg synchronized(this).
         */
        private Map<Locale, Map<String, String>> resourceCache;
        
        UIDefaultsExt() {
            resourceCache = new HashMap<Locale, Map<String,String>>();
        }
        
        private Object getFromResourceBundle(Object key, Locale l) {

            if( resourceBundles == null ||
                resourceBundles.isEmpty() ||
                !(key instanceof String) ) {
                return null;
            }

            // A null locale means use the default locale.
            if( l == null ) {
                    l = Locale.getDefault();
            }

            synchronized(this) {
                return getResourceCache(l).get((String)key);
            }
        }

        /**
         * Returns a Map of the known resources for the given locale.
         */
        private Map<String, String> getResourceCache(Locale l) {
            Map<String, String> values = (Map<String, String>) resourceCache.get(l);

            if (values == null) {
                values = new HashMap<String, String>();
                for (int i=resourceBundles.size()-1; i >= 0; i--) {
                    String bundleName = (String)resourceBundles.get(i);
                    
                    try {
                        ResourceBundle b = ResourceBundle.
                            getBundle(bundleName, l, UIManagerExt.class.getClassLoader());
                        Enumeration<String> keys = b.getKeys();

                        while (keys.hasMoreElements()) {
                            String key = (String)keys.nextElement();

                            if (values.get(key) == null) {
                                Object value = b.getObject(key);

                                values.put(key, (String) value);
                            }
                        }
                    } catch( MissingResourceException mre ) {
                        // Keep looking
                    }
                }
                resourceCache.put(l, values);
            }
            return values;
        }

        public synchronized void addResourceBundle(String bundleName) {
            if( bundleName == null ) {
                return;
            }
            if( resourceBundles == null ) {
                resourceBundles = new Vector<String>(5);
            }
            if (!resourceBundles.contains(bundleName)) {
                resourceBundles.add( bundleName );
                resourceCache.clear();
            }
        }
        
        public synchronized void removeResourceBundle( String bundleName ) {
            if( resourceBundles != null ) {
                resourceBundles.remove( bundleName );
            }
            resourceCache.clear();
        }
    }
    
    private static UIDefaultsExt uiDefaultsExt = new UIDefaultsExt();
    
    private UIManagerExt() {
        //does nothing
    }
    
    public static void addResourceBundle(String bundleName) {
        uiDefaultsExt.addResourceBundle(bundleName);
    }
    
    public static void removeResourceBundle(String bundleName) {
        uiDefaultsExt.removeResourceBundle(bundleName);
    }
    
    public static String getString(Object key) {
        return getString(key, null);
    }
    
    public static String getString(Object key, Locale l) {
        String value = UIManager.getString(key, l);
        
        if (value == null) {
            value = (String) uiDefaultsExt.getFromResourceBundle(key, l);
        }
        
        return value;
    }
    
    /**
     * Returns a painter from the defaults. If the value for {@code key} is not
     * a {@code Painter}, {@code null} is returned.
     * 
     * @param key
     *                an {@code Object} specifying the painter
     * @return the {@code Painter} object
     * @throws NullPointerException
     *                 if {@code key} is {@code null}
     */
    public static Painter<?> getPainter(Object key) {
        Object value = UIManager.getDefaults().get(key);
        return (value instanceof Painter) ? (Painter<?>) value : null;
    }
    
    /**
     * Returns a painter from the defaults that is appropriate for the given
     * locale. If the value for {@code key} is not a {@code Painter},
     * {@code null} is returned.
     * 
     * @param key
     *                an {@code Object} specifying the painter
     * @param l
     *                the {@code Locale} for which the painter is desired; refer
     *                to {@code UIDefaults} for details on how a {@code null}
     *                {@code Locale} is handled
     * @return the {@code Painter} object
     * @throws NullPointerException
     *                 if {@code key} is {@code null}
     */
    public static Painter<?> getPainter(Object key, Locale l) {
        Object value = UIManager.getDefaults().get(key, l);
        return (value instanceof Painter) ? (Painter<?>) value : null;
    }
    
    /**
     * Returns a color from the defaults. If the value for {@code key} is not a
     * {@code Color}, {@code defaultColor} is returned.
     * 
     * @param key
     *                an {@code Object} specifying the color
     * @param defaultColor
     *                the color to return if the color specified by {@code key}
     *                does not exist
     * @return the {@code Color} object
     * @throws NullPointerException
     *                 if {@code key} or {@code defaultColor} is {@code null}
     */
    public static Color getSafeColor(Object key, Color defaultColor) {
        Contract.asNotNull(defaultColor, "defaultColor cannot be null");
        
        Color safeColor = UIManager.getColor(key);
        
        if (safeColor == null) {
            safeColor = defaultColor;
        }
        
        return safeColor;
    }
    
    /**
     * Returns a font from the defaults. If the value for {@code key} is not a
     * {@code Font}, {@code defaultFont} is returned.
     * 
     * @param key
     *                an {@code Object} specifying the font
     * @param defaultFont
     *                the font to return if the font specified by {@code key}
     *                does not exist
     * @return the {@code Font} object
     * @throws NullPointerException
     *                 if {@code key} or {@code defaultFont} is {@code null}
     */
    public static Font getSafeFont(Object key, Font defaultFont) {
        Contract.asNotNull(defaultFont, "defaultFont cannot be null");
        
        Font safeFont = UIManager.getFont(key);
        
        if (safeFont == null) {
            safeFont = defaultFont;
        }
        
        return safeFont;
    }
}
