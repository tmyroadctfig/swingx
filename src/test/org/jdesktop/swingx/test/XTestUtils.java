/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXTable;

/**
 * Static convenience methods for testing. Note that the resources
 * are available in the test src hierarchy. 
 * 
 * @author Jeanette Winzenburg
 */
public class XTestUtils {
    private static final Logger LOG = Logger.getLogger(XTestUtils.class
            .getName());
    
    private static String IMAGE_DIR = "resources/images/";
    private static Class BASE = JXTable.class; 
    private static String IMAGE_NAME = "kleopatra.jpg";
    /**
     * 
     * @return the default icon for the swingx testing context.
     */
    public static Icon loadDefaultIcon() {
        return loadDefaultIcon(IMAGE_NAME);
    }


    /**
     * 
     * @param name the name relative to the default image package.
     * @return
     */
    public static Icon loadDefaultIcon(String name) {
        URL url = BASE.getResource(IMAGE_DIR + name);
        return new ImageIcon(url);
    }
    
    
    public static BufferedImage loadDefaultImage() {
        try {
            return ImageIO.read(BASE.getResource(IMAGE_DIR + IMAGE_NAME));
        } catch (IOException e) {
            LOG.warning("no reason this should happen .... we are ");
        }
        return null;
    }
    
    /**
     * 
     * @param days
     * @return the current day offset by days with all time elements 
     *   set to 0
     */
    public static Date getCleanedToday(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
    /**
     * 
     * @return the current date with all time elements set to 0
     */
    public static Date getCleanedToday() {
        return getCleanedDate(Calendar.getInstance());
    }
    
    /**
     * Cleans the calendars time elements and returns its date.
     * 
     * @param cal the calendar to clean
     * @return the calendar's date with all time elements set to 0
     */
    public static Date getCleanedDate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


}
