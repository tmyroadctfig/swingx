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
package org.jdesktop.swingx;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import junit.framework.TestCase;

/**
 * Unit test for <code>JXHeader</code>.
 * <p>
 * 
 * All test methods in this class are expected to pass. 
 * 
 * @author Jeanette Winzenburg
 */
public class JXHeaderTest extends TestCase {

    /**
     * Issue #403-swingx: JXHeader doesn't show custom values.
     *
     */
    public void testIconSet() {
        URL url = getClass().getResource("resources/images/wellTop.gif");
        Icon icon = new ImageIcon(url);
        assertNotNull(url);
        JXHeader header = new JXHeader();
        header.setIcon(icon);
        // sanity: the property is set
        assertEquals(icon, header.getIcon());
        // fishing in the internals ... not really safe, there are 2 labels
        JLabel label = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
           if (header.getComponent(i) instanceof JLabel) {
               boolean second = label != null;
               label = (JLabel) header.getComponent(i);
               if (second) {
                   break;
               }
           }
        }
        assertEquals("the label's text must be equal to the headers title", 
                header.getIcon(), label.getIcon());
    }

    /**
     * Issue #403-swingx: JXHeader doesn't show custom values.
     *
     */
    public void testTitleSet() {
        JXHeader header = new JXHeader();
        String title = "customTitle";
        header.setTitle(title);
        // sanity: the property is set
        assertEquals(title, header.getTitle());
        // fishing in the internals ... not really safe, there are 2 labels
        JLabel label = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
           if (header.getComponent(i) instanceof JLabel) {
               label = (JLabel) header.getComponent(i);
               break;
           }
        }
        assertEquals("the label's text must be equal to the headers title", 
                header.getTitle(), label.getText());
    }
    

}
