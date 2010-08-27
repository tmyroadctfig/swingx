/*
 * Created on 31.07.2006
 *
 */
package org.jdesktop.swingx;

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * "hand test" sandbox restrictions 
 * (here: around Utilities.initNamesAndValue, #353-swingx).
 * 
 * JW: Need to investigate further. Running the test via the 
 * build test-target lets this TestCase fail. Eclipse only?
 * Setting the securityManager has side-effects on the TestRunner. 
 *  
 * 
 */
@RunWith(JUnit4.class)
public class SandboxTest extends TestCase {
    private static final Logger LOG = Logger.getLogger(SandboxTest.class
            .getName());
    /**
     * quick test if table doesn't throw securityExceptions.
     * 
     *
     */
    @Test
    public void testSecurityManager() {
        if (System.getSecurityManager() == null) {
            LOG.info("cannot run testSecurityManager - no SecurityManager installed");
            return;
        }
        @SuppressWarnings("unused")
        JXTable table = new JXTable();
        try {
            System.getProperty("user.home", "not specified");
            fail("Sandbox without security priviledges");
        } catch (SecurityException e) {
            // nothing to do - that's what we expect
        }
        
    }
    
    /**
     * Sanity: make sure the second-time-around is reached!
     *
     */
    @Test
    public void testSecurityManagerAgain() {
        if (System.getSecurityManager() == null) {
            LOG.info("cannot run testSecurityManagerAgain - no SecurityManager installed");
            return;
        }
        try {
            System.getProperty("user.home", "not specified");
            fail("Sandbox without security priviledges");
        } catch (SecurityException e) {
            // nothing to do - that's what we expect
        }
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // A - install the default SecurityManager. 
        // Doing so we are not allowed to reverse the install -
        // which makes this testCase to a manual-run-only affair
        // (the securityManager is not uninstalled when running 
        // other test cases - in Eclipse, when running the 
        // bulk "all tests" of a projects. 
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
        // B- if we install a SecurityManager we need to be sure
        // that we are allowed to uninstall it.
        // BUT: with this custom manager on, JXTable instantiation
        // fails with a rather weird stack-trace. Gave up for now...
//        if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new SecurityManager() {
//
//                    @Override
//                    public void checkPermission(Permission perm) {
//                        if ("setSecurityManager".equals(perm.getName())) return;
//                        super.checkPermission(perm);
//                    }
//                    
//                });
//            }        
              
    }
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // be sure to uninstall the manager
//        System.setSecurityManager(null);
    }
    
    
    
}
