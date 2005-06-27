/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.text.Collator;
import java.util.Locale;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXTable;

public class SorterTest extends TestCase {

    /**
     * Issue #189: make sure to use the correct default collator.
     * 
     */
    public void testCollator() {
        Locale defaultLocale = Locale.getDefault();
        Locale western = Locale.GERMAN;
        Locale eastern = Locale.CHINESE;
        Collator westernCol = Collator.getInstance(western);
        Collator easternCol = Collator.getInstance(eastern);
        // sanity assert: collators are different
        assertFalse(westernCol.equals(easternCol));
        Locale.setDefault(western);
        // sanity assert: default collator is western
        assertEquals(westernCol, Collator.getInstance());
        Sorter sorter = new ShuttleSorter();
        assertEquals("sorter must use collator default locale",
                Collator.getInstance(), sorter.getCollator());
        Locale.setDefault(eastern);
        // sanity assert: default collator is eastern
        assertEquals(easternCol, Collator.getInstance());
        sorter.toggle();
        assertEquals("collator must use default locale",
                Collator.getInstance(), sorter.getCollator());
        Locale.setDefault(defaultLocale);
        
    }
}
