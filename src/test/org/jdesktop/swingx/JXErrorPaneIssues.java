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

import org.jdesktop.swingx.error.ErrorInfo;

/**
 * Test to expose known issues around <code>JXError*</code>.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class JXErrorPaneIssues extends InteractiveTestCase {
    public static void main(String[] args) {
//      setSystemLF(true);
      JXErrorPaneIssues test = new JXErrorPaneIssues();
      try {
        test.runInteractiveTests();
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }

    }

    /**
     * 
     * reported in forum - details not visible, details close on focus change.
     * Worksforme on XP, maybe OS dependent?
     *
     */
    public void interactiveNoDetails() {
        JXErrorPane.showDialog(new NullPointerException("something to show"));
    }
    /**
     * Issue #468-swingx: JXErrorPane can't cope with null errorInfo.
     *
     */
    public void interactiveNPEWithNullErrorInfo() {
        JXErrorPane errorPane = new JXErrorPane();
        JXErrorPane.showDialog(null, errorPane);
    }
    
    /**
     * Issue #467-swingx: calling updateUI throws error.
     *
     */
    public void interactiveUpdateUI() {
        final JXErrorPane errorPane = new JXErrorPane();
        // work around issue #468-swingx: errorPane must cope with null errorInfo.
        //errorPane.setErrorInfo(new ErrorInfo("title", "xxxx-yyy", null, null, null, null, null));
        errorPane.updateUI();
    }

    /**
     * do-nothing method - suppress warning if there are no other
     * test fixtures to run.
     *
     */
    public void testDummy() {
        
    }

}
