/*
 * Created on 31.07.2006
 *
 */
package org.jdesktop.swingx.plaf;

import java.net.URL;
import java.text.DateFormat;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import junit.framework.TestCase;

import org.jdesktop.swingx.InteractiveTestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Testing LookAndFeelAddons properties/behaviour that might be effected
 * in a sandbox. This here is the test without SecurityManager, the 
 * subclass actually installs a manager.
 */
@RunWith(JUnit4.class)
public class LookAndFeelAddonsSandboxTest extends TestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(LookAndFeelAddonsSandboxTest.class
            .getName());
    
    
    /**
     * Issue ??-swingx: addon lookup doesn't work in security
     * restricted contexts.
     * 
     * Here test if we have access to the serviceProvider resource.
     * Any way to do in the sandbox?  
     */
    @Test
    public void testAccessMetaInf() {
        Class<?> clazz = LookAndFeelAddons.class;
        // JW: just a reminder (to myself)
        // class.getResource interprets path as relative without 
        // leading slash
        // classloader.getResource always absolute
        String services = "META-INF/services/" + clazz.getName();
        // using the classloader (just as ServiceLoader does)
        // absolute path always
        URL url = clazz.getClassLoader().getResource(services);
        assertNotNull("services must be found: fails in security restricted contexts because the loader " +
        		"has no access to the provider configuration file", 
        		url);
    }

    /**
     * Issue ??-swingx: addon lookup doesn't work in security
     * restricted contexts.
     * 
     * The core of the issue seems to be that 
     * ServiceLoader doesn't read the provided addons - expected
     * or not? 
     */
    @Test
    public void testServiceLoader() {
        ServiceLoader<LookAndFeelAddons> loader = ServiceLoader.load(LookAndFeelAddons.class);
        assertTrue("loader must have addons: fails in security restricted contexts because the loader " +
        		"has no access to the provider configuration file", 
        		loader.iterator().hasNext());
    }
    
    /**
     * Issue ??-swingx: addon lookup doesn't work in security
     * restricted contexts.
     * 
     * Here we test that the addon is changed to match a newly 
     * set LAF (Nimbus), 
     * assuming that the ui is initially set to system.
     */
    @Test
    public void testMatchingAddon() throws Exception {
        LookAndFeel old = UIManager.getLookAndFeel();
        try {
            assertTrue("sanity: addon is configured to update on LAF change", 
                    LookAndFeelAddons.isTrackingLookAndFeelChanges());
            InteractiveTestCase.setLookAndFeel("Nimbus");
            LookAndFeelAddons addon = LookAndFeelAddons.getAddon();
            assertTrue("addon must match Nimbus, but was: " + addon, addon.matches());
            
        } finally {
            UIManager.setLookAndFeel(old);
        }
    }
    
    /**
     * Issue ??-swingx: addon lookup doesn't work in security
     * restricted contexts.
     * 
     * Here we test for systemLAF, 
     * assuming that the ui is set to system.
     */
    @Test
    public void testSystemAddon() {
        LookAndFeelAddons addon = LookAndFeelAddons.getAddon();
        assertTrue("addon must be system addon, but was: " + addon, addon.isSystemAddon());
    }
    
    
    /**
     * Sets the default LAF to system. 
     * 
     */
    @BeforeClass
    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
