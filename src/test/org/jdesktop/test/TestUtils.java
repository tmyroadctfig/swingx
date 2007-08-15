/*
 * TestUtils.java
 *
 * Created on October 31, 2006, 9:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Logger;

import junit.framework.Assert;

/**
 * Extends assert to get all the ease-of-use assert methods
 * @author rbair
 */
public final class TestUtils extends Assert {
    private static final Logger LOG = Logger.getLogger(TestUtils.class
            .getName());
    private TestUtils() {}
    
    /**
     * Asserts the last received propertyChangeEvent of the 
     * report against the expected values.
     * 
     * @param report the PropertyReport which received the event
     * @param source the expected event source
     * @param property the expected name of the property
     * @param oldValue the expected old value 
     * @param newValue the expected new value
     */
    public static void assertPropertyChangeEvent(PropertyChangeReport report, 
            Object source, String property, Object oldValue, Object newValue) {
        assertPropertyChangeEvent(report, property, oldValue, newValue);
        assertEquals("event source", source, report.getLastSource());
    }

    /**
     * Asserts the last received propertyChangeEvent of the 
     * report against the expected values.
     * 
     * @param report the PropertyReport which received the event
     * @param property the expected name of the property
     * @param oldValue the expected old value 
     * @param newValue the expected new value
     */
    public static void assertPropertyChangeEvent(PropertyChangeReport report, String property, Object oldValue, Object newValue) {
        if (report.getEventCount() > 1) {
            LOG.info("events: " + report.getEventNames());
        }
        assertEquals("exactly one event", 1, report.getEventCount());
        assertEquals("property", property, report.getLastProperty());
        assertEquals("last old value", oldValue, report.getLastOldValue());
        assertEquals("last old value", newValue, report.getLastNewValue());
    }

    /**
     * Asserts the last received propertyChangeEvent of the 
     * report against the expected values (arrays).
     * 
     * @param report the PropertyReport which received the event
     * @param property the expected name of the property
     * @param oldValue the expected old aray value 
     * @param newValue the expected new array value
     */
    public static void assertPropertyChangeEvent(PropertyChangeReport report, String property, Object[] oldValue, Object[] newValue) {
        if (report.getEventCount() > 1) {
            LOG.info("events: " + report.getEventNames());
        }
        assertEquals("exactly one event", 1, report.getEventCount());
        assertEquals("property", property, report.getLastProperty());
        assertTrue("last old array value", Arrays.equals(oldValue, (Object[]) report.getLastOldValue()));
        assertTrue("last new array value", Arrays.equals(newValue, (Object[])report.getLastNewValue()));
    }
    

    /**
     * Asserts the last received propertyChangeEvent of the 
     * report against the expected values.
     * 
     * @param report the PropertyReport which received the event
     * @param property the expected name of the property
     * @param oldValue the expected old value 
     * @param newValue the expected new value
     */
    public static void assertPropertyChangeEvent(PropertyChangeEvent event, String property, Object oldValue, Object newValue) {
        assertNotNull("event must be not null", event);
        assertEquals("property", property, event.getPropertyName());
        assertEquals("last old value", oldValue, event.getOldValue());
        assertEquals("last old value", newValue, event.getNewValue());
    }
    
    public static void assertPropertyChangeNotification(
            Object bean, String propertyName, Object expected) throws Exception {
        
        //add the property change listener
        Method m = bean.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class);
        PropertyChangeReport rpt = new PropertyChangeReport();
        m.invoke(bean, rpt);
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        
        //the original bean value, before being set to 'expected'
        Object originalValue = pd.getReadMethod().invoke(bean);
        
        //set the bean value to 'expected'
        m = pd.getWriteMethod();
        m.invoke(bean, expected);
        
        //get the new bean value, after being set to 'expected'
        Object newValue = pd.getReadMethod().invoke(bean);
        
        if (newValue == originalValue || 
                (newValue != null && newValue.equals(originalValue))) {
            //assert that we don't get an event, because newValue was the same
            //as old value (should only get an event if they differ)
            assertEquals(0, rpt.getEventCount());
        } else {
            //assert bean's property is newValue
            assertEquals(expected, newValue);
            //assert that there is exactly one event
            assertEquals(1, rpt.getEventCount());
            //assert that the event's property name is correct
            assertEquals(propertyName, rpt.getLastEvent().getPropertyName());
            //assert that the original value is the old value of the event
            assertEquals(originalValue, rpt.getLastOldValue());
            //assert that the expected value is the new value of the event
            assertEquals(expected, rpt.getLastNewValue());
        }
    }
}
