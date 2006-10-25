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

/**
 * Test to expose known issues of <code>JXHeader</code>.
 * <p>
 * 
 * Ideally, there would be at least one failing test method per open issue in
 * the issue tracker. Plus additional failing test methods for not fully
 * specified or not yet decided upon features/behaviour.
 * <p>
 * 
 * If an issue is fixed and the corresponding methods are passing, they
 * should be moved over to the XXTest.
 * 
 * @author Jeanette Winzenburg
 */
public class JXHeaderIssues extends InteractiveTestCase {

    /**
     * This issue has been fixed, but remains here (otherwise I get a warning
     * when running this test. Not sure if this JXHeaderIssues should just be
     * removed, or what).
     *
     * Issue #403-swingx: JXHeader doesn't show custom values.
     * <p>
     * 
     * Breaking if values are passed in the constructor.
     */
    public void testTitleInContructor() {
        String title = "customTitle";
        JXHeader header = new JXHeader(title, null);
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

//------------------ interactive 
    
    /**
     * Issue #403-swingx: JXHeader doesn't show custom values.<p>
     * 
     * All values are passed in the constructor.
     */
    public void interactiveCustomProperties() {
        URL url = getClass().getResource("resources/images/wellTop.gif");
        assertNotNull(url);
        JXHeader header = new JXHeader("MyTitle", "MyDescription", new ImageIcon(url));
        showInFrame(header, "JXHeader with custom properties");
    }
    
    public static void main(String args[]) {
        JXHeaderIssues test = new JXHeaderIssues();
        try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }
    

}
