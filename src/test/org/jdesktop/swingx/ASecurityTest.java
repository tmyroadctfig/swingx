/*
 * Created on 31.07.2006
 *
 */
package org.jdesktop.swingx;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXTable;

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
public class ASecurityTest extends TestCase {
    /**
     * quick test if table is 
     *
     */
    public void testSecurityManager() {
        JXTable table = new JXTable();
        try {
            System.getProperty("user.home", "not specified");
            fail("Sandbox without security priviledges");
        } catch (SecurityException e) {
            // nothing to do - that's what we expect
        }
        
    }
    
    public void testNoSecurityManager() {
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
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
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
