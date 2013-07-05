/*
 * Created on 31.07.2006
 *
 */
package org.jdesktop.swingx.plaf;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * "hand test" sandbox restrictions  for LookAndFeelAddons. Behaviour
 * (mainly of looking up an appropriate addon) should be the same as
 * in an unrestricted context - tested to work in super.
 * 
 * JW: Need to investigate further. Running the test via the 
 * build test-target lets this TestCase fail. Eclipse only?
 * Setting the securityManager has side-effects on the TestRunner. 
 *  
 * 
 */
@RunWith(JUnit4.class)
public class LookAndFeelAddonsSandboxOnTest extends LookAndFeelAddonsSandboxTest {
    private static final Logger LOG = Logger.getLogger(LookAndFeelAddonsSandboxOnTest.class
            .getName());
    
    /**
     * Accessing the services inside a privileged action.
     */
    @Test
    public void testAccessMetaInfPriviledged() {
        final Class<?> clazz = LookAndFeelAddons.class;
        // JW: just a reminder (to myself)
        // class.getResource interprets path as relative without 
        // leading slash
        // classloader.getResource always absolute
        final String services = "META-INF/services/" + clazz.getName();
        // using the classloader (just as ServiceLoader does)
        // absolute path always
        URL url = AccessController.doPrivileged(new PrivilegedAction<URL>() {
            @Override
            public URL run() {
                return clazz.getClassLoader().getResource(services);
            }
        });
        assertNotNull("services must be found", url);
    }
    
    /**
     * Testing privileged access to the ServiceLoader.
     * Bare grabbing inside the priviledgeAction doesn't work, 
     * probably because the classes are
     * loaded lazily
     */
    @Test
    public void testServiceLoaderPrivileged() {
        ServiceLoader<LookAndFeelAddons> loader =
                AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<LookAndFeelAddons>>() {
                    @Override
                    public ServiceLoader<LookAndFeelAddons> run() {
                        return ServiceLoader.load(LookAndFeelAddons.class);
                    }
                });  
        assertTrue("loader must have addons: fails here because the loading happens lazily", loader.iterator().hasNext());
    }

    /**
     * Testing privileged access to the ServiceLoader.
     * 
     * Here we access the iterator inside the priviledged access, thus 
     * forcing the load.
     */
    @Test
    public void testServiceLoaderIteratorPrivileged() {
        final ServiceLoader<LookAndFeelAddons> loader = ServiceLoader
                .load(LookAndFeelAddons.class);
        // need to access the iterator inside the priviledge
        // action
        // probably because it's lazily loaded
        AccessController
                .doPrivileged(new PrivilegedAction<Iterable<LookAndFeelAddons>>() {
                    @Override
                    public Iterable<LookAndFeelAddons> run() {
                        loader.iterator().hasNext();
                        return loader;
                    }
                });
        int count = 0;
        for (LookAndFeelAddons addons : loader) {
            count++;
        }

        assertEquals("loader must have addons", 7, count);
    }
    
    /**
     * Issue ??-swingx: accessing LookAndFeelAddons throws
     * ExceptionInInitializationError.
     * 
     * This is caused by a typo in getCrossPlatFormAddon in the fallback
     * branch, that is if accessing the property isn't allowed
     * (packagename was swing instead of swingx)
     * 
     *
     */
    @Test
    public void testCrossPlatformTypo() {
        LookAndFeelAddons.getAddon();
    }
    
    /**
     * Sanity: verify access to swing.addon denied.
     *
     */
    @Test(expected= SecurityException.class)
    public void testPropertySwingAddonDenied() {
        System.getProperty("swing.addon", "not specified");
    }
    
    /**
     * Sanity: verify access to swing.addon denied.
     *
     */
    @Test(expected= SecurityException.class)
    public void testPropertySwingCrossplatformAddonDenied() {
        System.getProperty("swing.crossplatformlafaddon", "not specified");
    }
    
    /**
     * Asserts that a security manager is installed, running the tests
     * here without doesn't make sense.
     */
    @Override
    @Before
    public void setUp() throws Exception {
        assertNotNull("Sandbox test cannot be run, no securityManager", 
                System.getSecurityManager());
    }
    
    /**
     * Install a security manager and sets the default LAF to system. 
     * 
     * Note that de-installing the
     * manager might not be allowed, so we can't run these tests automatically.
     */
    @BeforeClass
    public static void install() {
        // A - install the default SecurityManager. 
        // Doing so we are not allowed to reverse the install -
        // which makes this testCase to a manual-run-only affair
        // (the securityManager is not uninstalled when running 
        // other test cases - in Eclipse, when running the 
        // bulk "all tests" of a projects. 
        System.setSecurityManager(new SecurityManager());
        // B- if we install a SecurityManager we need to be sure
        // that we are allowed to uninstall it.
        // BUT: with this custom manager on, test running 
        // fails with a rather weird stack-trace. Gave up for now...
//        System.setSecurityManager(new SecurityManager() {
//
//            @Override
//            public void checkPermission(Permission perm) {
//                if ("setSecurityManager".equals(perm.getName())) return;
//                super.checkPermission(perm);    
//                //  java.security.AccessController.checkPermission(perm);
//
//            }
//
//        });
              
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void uninstall() {
        try {
            System.setSecurityManager(null);
        } catch (Exception e) {
            LOG.info("can't remove the security manager");
        }
    }
}
