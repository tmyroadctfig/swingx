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
    

    public void interactiveHeaderEmpty() {
        JXHeader header = new JXHeader();
        showInFrame(header, "empty constructor");
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

    /**
     * Sanity: trying to track default rendering hints.
     * @KEEP until Issue ?? with header antialiased is solved.
     */
//    public void interactiveLabel() {
//        JLabel label = new JLabel("JLabel tweaked * tracking, tracking ...") {
//
//            @Override
//            protected void paintComponent(Graphics g) {
//                if (ui != null) {
//                    Graphics scratchGraphics = (g == null) ? null : g.create();
//                    try {
//                        RenderingHints old = ((Graphics2D)scratchGraphics).getRenderingHints();
//                      LOG.info(getText() + ": all hints " + old
//                      + "\n     " + ": aliased " + ((Graphics2D)scratchGraphics).getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
//                      ((Graphics2D) scratchGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                              RenderingHints.VALUE_ANTIALIAS_ON);
//                        ui.update(scratchGraphics, this);
////                        SwingUtilities2.drawStringUnderlineCharAt(this, scratchGraphics, getText(), -1, 0, 10);
//                        RenderingHints after =((Graphics2D)scratchGraphics).getRenderingHints();
//                        LOG.warning(getText() + ": all hints " + after
//                                + "\n     " + ": aliased " + ((Graphics2D)scratchGraphics).getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING)
//                                + "\n     " + "equals? " + old.equals(after));
//                    }
//                    finally {
//                        scratchGraphics.dispose();
//                    }
//                }
//            }
//            
//        };
//        JComponent box = Box.createVerticalBox();
//        box.add(label);
//        box.add(new JXLabel("JXLabel * tracking ... tracking .."));
//        box.add(new JLabel("JLabel raw * tracking tracking ..."));
//        showInFrame(box, "label");
//    }

    @Override
    protected void setUp() throws Exception {
        setSystemLF(true);
        // forcing load of headerAddon
        new JXHeader();
    }
    
    /**
     * Dummy empty test just to keep it from whining.
     */
    public void testDummy() {
        // do nothing
    }
    
}
