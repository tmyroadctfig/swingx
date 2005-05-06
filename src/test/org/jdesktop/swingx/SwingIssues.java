/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.jdesktop.swingx.table.TableColumnModelTest;

/**
 * Collection of open swingx issues - formulated as TestCases.<p>
 * 
 * The testcases (classes are named *Issues to not 
 * interfere with the default build) will fail until
 * the issues are resolved. After resolving the testcases
 * should be integrated into the corresponding 
 * "normal" tests.<p>
 * 
 * PENDING: need ant target to run all issues... 
 * Until then this testsuite collection can be used to run all
 * issue tests manually.  
 * 
 * @author Jeanette Winzenburg
 */
public class SwingIssues {

    public static Test suite() {
        TestSuite suite = new TestSuite("JUnit Tests - Open Issues in Swing layer of JDNC");
//        suite.addTest(new TestSuite(BindingIssues.class));
//        suite.addTest(new TestSuite(JFormIssues.class));
//        suite.addTest(new TestSuite(DefaultTableModelExtIssues.class));
        suite.addTest(new TestSuite(JXTableIssues.class));
        suite.addTest(new TestSuite(TableColumnModelTest.class));

        return suite;
    }

    //  ---------------------------Main

    /**
     * manually starting a TestRunner.
     *  
     */
//    public static void main(String[] args) {
//        junit.swingui.TestRunner.run(SwingTest.class);
//    }

}