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
package org.jdesktop.swingx.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXMonthViewVisualCheck;

/**
 * JW: renamed from PainterInteractiveTest to fix the build failure. Revisit!
 * @author rbair
 */
public class BlenderVisualCheck extends InteractiveTestCase {

	   private static final Logger LOG = Logger.getLogger(JXMonthViewVisualCheck.class
	            .getName());
	    public static void main(String[] args) {
//	      setSystemLF(true);
	    	BlenderVisualCheck  test = new BlenderVisualCheck();
	      try {
	          test.runInteractiveTests();
//	        test.runInteractiveTests(".*Move.*");
	      } catch (Exception e) {
	          System.err.println("exception when executing interactive tests:");
	          e.printStackTrace();
	      }
	  }


	    /**
	     * Issue #563-swingx: arrow keys active even if not focused.
	     * focus the button and use the arrow keys: selection moves.
	     * Reason was that the WHEN_IN_FOCUSED_WINDOW key bindings
	     * were always installed. 
	     * 
	     * Fixed by dynamically bind/unbind component input map bindings
	     * based on the JXMonthView's componentInputMapEnabled property.
	     *
	     */
	    public void interactiveMistargetedKeyStrokes() throws Exception {
	        JComponent panel = new JPanel();
	        JXImagePanel ipa = new JXImagePanel() {
	        	@Override
	        	public void paint(Graphics g) {
	        		g.setColor(Color.BLACK);
	        		g.fillRect(0, 0, getWidth(), getHeight());
	        		super.paint(g);
	        	}
	        };
	        ipa.setImage(ImageIO.read(getClass().getResourceAsStream("/org/jdesktop/swingx/resources/images/dog.jpg")));
	        showInFrame(ipa, "default - for debugging only");
	    }
	    /**
	     * Issue #563-swingx: arrow keys active even if not focused.
	     * focus the button and use the arrow keys: selection moves.
	     * Reason was that the WHEN_IN_FOCUSED_WINDOW key bindings
	     * were always installed. 
	     * 
	     * Fixed by dynamically bind/unbind component input map bindings
	     * based on the JXMonthView's componentInputMapEnabled property.
	     *
	     */
	    public void interactiveMistargetedKeyStrokes2() throws Exception {
	        JXImagePanel ipa = new JXImagePanel() {
	        	@Override
	        	public void paint(Graphics g) {
	        		super.paint(g);
	        		BlendComposite bc = BlendComposite.getInstance(BlendComposite.BlendingMode.COLOR);
	        		((Graphics2D) g).setComposite(bc);
	        		g.setColor(Color.BLACK);
	        		g.fillRect(0, 0, getWidth(), getHeight());
	        	}
	        };
	        ipa.setImage(ImageIO.read(getClass().getResourceAsStream("/org/jdesktop/swingx/resources/images/dog.jpg")));
	        showInFrame(ipa, "default - for debugging only");
	    }

	    
    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
