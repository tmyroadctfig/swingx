/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.util;

import java.lang.reflect.Method;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Utility for working with the UIManager
 * @author Richard Bair
 */
public final class UIManagerUtils {
	/**
	 * Hidden constructor
	 */
	private UIManagerUtils() {
	}
	
	/**
	 * Initializes the object in the UIDefaults denoted by 'key' to defaultObj <strong>only if</strong>
	 * the key is not already in the UIDefaults.
	 * @param key
	 * @param defaultObj
	 */
	public static void initDefault(String key, Object defaultObj) {
		Object obj = UIManager.get(key);
		if (obj == null) {
			UIManager.put(key, defaultObj);
		}
	}

	/**
	 * Initializes the object in the UIDefaults denoted by 'key' to either the property in the metal look and feel
	 * associated with defaultMetalObjName, or the defaultObj if all else fails.
	 * @param key
	 * @param defaultMetalObjName
	 * @param defaultObj
	 */
	public static void initDefault(String key, String defaultMetalObjName, Object defaultObj) {
		Object obj = UIManager.get(key);
		if (obj == null) {
			try {
                Method m = ((MetalLookAndFeel)UIManager.getLookAndFeel()).getClass().getMethod(defaultMetalObjName, defaultObj.getClass());
                UIManager.put(key, m.invoke(UIManager.getLookAndFeel(), defaultMetalObjName));
			} catch (Exception e) {
				UIManager.put(key, defaultObj);
			}
		}
	}
}
