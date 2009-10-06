/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.table;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.InteractiveTestCase;
import org.junit.Test;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class NumberEditorExtIssues extends InteractiveTestCase {

    private static final String TOO_BIG_INTEGER = "11111111111111111111111111";


    @Test (expected = ParseException.class)
    public void testNumberFormatterMinMax() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setMinimum(Integer.MIN_VALUE);
        formatter.stringToValue(TOO_BIG_INTEGER);
    }

    @Test
    public void testNumberFormatInteger() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        // this passes - everything fitting into double range is acceptable
        Number number = format.parse(TOO_BIG_INTEGER);
        // this blows - must fit into Integer.MIN/MAX
        new Integer(TOO_BIG_INTEGER);
    }

}
