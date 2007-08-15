/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * A unit test for the JXImagePanel
 *
 * @author rah003
 */
public class JXImagePanelVisualCheck extends InteractiveTestCase {

    public static void main(String[] args) throws Exception {
      JXImagePanelVisualCheck test = new JXImagePanelVisualCheck();
      try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
  }
    /**
     * Issue #410-swingx: JXImagePanel paints in the border area 
     * Expected results: Border is painted around the image rather then over it.
     */
    public void interactivePaintBorder() throws Exception {
        URL url = new URL("https://swingx.dev.java.net/branding/images/header_jnet_new.jpg");
        
        assertNotNull(url);
        JPanel p = new JPanel(new BorderLayout());
        JXImagePanel imagePanel = new JXImagePanel(url);
        imagePanel.setBorder(new CompoundBorder(new EmptyBorder(10,10,10,10), new
DropShadowBorder()));
        p.add(imagePanel);
        showInFrame(p, "JXImagePanel with drop shadow border");
    }
}
