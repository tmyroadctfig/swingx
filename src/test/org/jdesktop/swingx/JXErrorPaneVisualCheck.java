/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.util.logging.Level;

import org.jdesktop.swingx.error.ErrorInfo;

/**
 * A unit test for the JXErrorPane
 *
 * @author rah003
 */
public class JXErrorPaneVisualCheck extends InteractiveTestCase {

    public static void main(String[] args) throws Exception {
      JXErrorPaneVisualCheck test = new JXErrorPaneVisualCheck();
      try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
  }
    /**
     * Issue #45-swinglabs: JXErrorPane paints message text over action buttons 
     *
     */
    public void interactiveLongMessageText() {
        ErrorInfo errorInfo = new ErrorInfo("Server Error",
                "The request cannot be carried out\n1\n2\n3\n4\n5\n6\n7" +
                "\n8\n9\n0\n1\n2\n3\n4\nThis text should be shown in scroll pane.", "Server Error",
                null, new Exception(), Level.SEVERE, null);
        JXErrorPane.showDialog(null,errorInfo );
    }
    
    /**
     * Issue #468-swingx: JXErrorPane can't cope with null errorInfo.
     *
     */
    public void interactiveNPEWithDefaultErrorInfo() {
        JXErrorPane errorPane = new JXErrorPane();
        JXErrorPane.showDialog(null, errorPane);
    }
    
    /**
     * Issue #468-swingx: JXErrorPane can't cope with null errorInfo.
     *
     */
    public void interactiveSetNullErrorInfo() {
        JXErrorPane errorPane = new JXErrorPane();
        try {
            errorPane.setErrorInfo(null);
            fail("Failed to fail while setting null ErrorInfo");
        } catch (NullPointerException e) {
            assertEquals("Unexpected error message", "ErrorInfo can\'t be null. Provide valid ErrorInfo object.", e.getMessage());
            // ignore - expected.
        }
    }
    
    /**
     * Issue #467-swingx: calling updateUI throws error.
     *
     */
    public void interactiveUpdateUI() {
        final JXErrorPane errorPane = new JXErrorPane();
        errorPane.updateUI();
    }

}
