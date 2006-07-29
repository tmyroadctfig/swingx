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
package org.jdesktop.swingx.autocomplete;

/**
 * This class is used to provide string representations for objects when
 * doing automatic completion.
 *
 * A class inherited from this class could be used, when the object's
 * <tt>toString</tt> method is not appropriate for automatic completion.
 *
 * An example for i18n:
 *
 * <code>
 * public class I18NStringConverter extends ObjectToStringConverter {
 *   ResourceBundle bundle;
 *
 *   public I18NStringConverter(ResourceBundle bundle) {
 *     this.bundle = bundle;
 *   }
 *   public String[] getPossibleStringsForItem(Object item) {
 *     String preferred = getPreferredStringForItem(item);
 *     return preferred==null ? null : new String[]{preferred};
 *   }
 *   public String getPreferredStringForItem(Object item) {
 *         return item==null ? null : bundle.getString(item.toString());
 *   }
 * }
 * </code>
 *
 * @author Thomas Bierhance
 */
public abstract class ObjectToStringConverter {
    
    /**
     * Returns all possible <tt>String</tt> representations for a given item.
     * @param item the item to convert
     * @return possible <tt>String</tt> representation for the given item.
     */
    public abstract String[] getPossibleStringsForItem(Object item);
    
    /**
     * Returns the preferred <tt>String</tt> representations for a given item.
     * @param item the item to convert
     * @return the preferred <tt>String</tt> representation for the given item.
     */
    public abstract String getPreferredStringForItem(Object item);
    
    /**
     * This field contains the default implementation, that returns <tt>item.toString()</tt>
     * for any item <tt>!=null</tt>. For any item <tt>==null</tt>, it returns <tt>null</tt> as well.
     */
    public static final ObjectToStringConverter DEFAULT_IMPLEMENTATION = new DefaultObjectToStringConverter();
    
    private static class DefaultObjectToStringConverter extends ObjectToStringConverter {
        public String[] getPossibleStringsForItem(Object item) {
            return item==null ? null : new String[]{item.toString()};
        }
        public String getPreferredStringForItem(Object item) {
            return item==null ? null : item.toString();
        }
    }    
}