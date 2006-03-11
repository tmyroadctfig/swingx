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

package org.jdesktop.swingx.editors;

import java.awt.geom.Rectangle2D;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author rbair
 */
public class Rectangle2DPropertyEditor extends PropertyEditorSupport {

    /** Creates a new instance of Rectangle2DPropertyEditor */
    public Rectangle2DPropertyEditor() {
    }

    public Rectangle2D getValue() {
        return (Rectangle2D.Double)super.getValue();
    }

    public String getJavaInitializationString() {
        Rectangle2D rect = getValue();
        return rect == null ? "null" : "new java.awt.geom.Rectangle2D.Double(" + rect.getX() + ", " + rect.getY() + ", " + rect.getWidth() + ", " + rect.getHeight() + ")";
    }

    public void setAsText(String text) throws IllegalArgumentException {
        //the text could be in many different formats. All of the supported formats are as follows:
        //(where x and y are doubles of some form)
        //[x,y,w,h]
        //[x y w h]
        //x,y,w,h]
        //[x,y w,h
        //[ x , y w,h] or any other arbitrary whitespace
        // x , y w h] or any other arbitrary whitespace
        //[ x , y w, h or any other arbitrary whitespace
        //x,y w, h
        // x , y w,h (or any other arbitrary whitespace)
        //x y w h
        //or any other such permutation
        // (empty space)
        //null
        //[]
        //[ ]
        //any other value throws an IllegalArgumentException

        String originalParam = text;

        if (text != null) {
            //remove any opening or closing brackets
            text = text.replace('[', ' ');
            text = text.replace(']', ' ');
            text = text.replace(',', ' ');
            //trim whitespace
            text = text.trim();
        }

        //test for the simple case
        if (text == null || text.equals("") || text.equals("null")) {
            setValue(null);
            return;
        }

        //the first sequence of characters must now be a number. So, parse it out
        //ending at the first whitespace. Then trim and the remaining value must
        //be the second number. If there are any problems, throw and IllegalArgumentException
        try {
            int index = text.indexOf(' ');
            String x = text.substring(0, index).trim();
            text = text.substring(index).trim();
            index = text.indexOf(' ');
            String y = text.substring(0, index).trim();
            text = text.substring(index).trim();
            index = text.indexOf(' ');
            String w = text.substring(0, index).trim();
            String h = text.substring(index).trim();
            Rectangle2D.Double val = new Rectangle2D.Double(
                    Double.parseDouble(x),
                    Double.parseDouble(y),
                    Double.parseDouble(w),
                    Double.parseDouble(h));
            setValue(val);
        } catch (Exception e) {
            throw new IllegalArgumentException("The input value " + originalParam + " is not formatted correctly. Please " +
                    "try something of the form [x,y,w,h] or [x , y , w , h] or [x y w h]", e);
        }
    }

    public String getAsText() {
        Rectangle2D rect = getValue();
        return rect == null ? "[]" : "[" + rect.getX() + ", " + rect.getY() + ", " + rect.getWidth() + ", " + rect.getHeight() + "]";
    }

    public static void main(String... args) {
        test("[1.5,1.2,10,35]");
        test("1.5,1.2,10,35]");
        test("[1.5,1.2,10,35");
        test("[ 1.5 , 1.2 ,10,35]");
        test(" 1.5 , 1.2 ,10,35]");
        test("[ 1.5 , 1.2,10,35");
        test("1.5,1.2,10,35");
        test(" 1.5 , 1.2 10 35");
        test("1.5 1.2, 10 35");
        test("");
        test("null");
        test("[]");
        test("[ ]");
        test("[1.5 1.2 10 35]");
    }

    private static void test(String input) {
        System.out.print("Input '" + input + "'");
        try {
            Rectangle2DPropertyEditor ed = new Rectangle2DPropertyEditor();
            ed.setAsText(input);
            Rectangle2D rect = ed.getValue();
            System.out.println(" succeeded: " + rect);
        } catch (Exception e) {
            System.out.println(" failed: " + e.getMessage());
        }
    }
} 
