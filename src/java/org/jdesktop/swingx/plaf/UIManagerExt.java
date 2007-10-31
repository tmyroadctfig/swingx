/**
 * 
 */
package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.util.Locale;

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
    private UIManagerExt() {
        //does nothing
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
}
