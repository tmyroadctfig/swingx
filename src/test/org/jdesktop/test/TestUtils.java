/*
 * TestUtils.java
 *
 * Created on October 31, 2006, 9:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.test;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Logger;

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.painter.BusyPainter;

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
     * report against the expected values.
     * 
     * @param report the PropertyReport which received the event
     * @param property the expected name of the property
     * @param oldValue the expected old value 
     * @param newValue the expected new value
     * @param single flag to denote if we expect one event only
     */
    public static void assertPropertyChangeEvent(PropertyChangeReport report, 
            String property, Object oldValue, Object newValue, boolean single) {
        if (report.getEventCount() > 1) {
            LOG.info("events: " + report.getEventNames());
        }
        if (single) {
            assertEquals("exactly one event", 1, report.getEventCount());
            assertEquals("property", property, report.getLastProperty());
            assertEquals("last old value", oldValue, report.getLastOldValue());
            assertEquals("last old value", newValue, report.getLastNewValue());
        } else {
            assertEquals("one event of property " + property, 1, report.getEventCount(property));
            assertEquals("old property", oldValue, report.getLastOldValue(property));
            assertEquals("new property", newValue, report.getLastNewValue(property));
        }
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
     * @param event the event to assert.
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

    public static void assertPCEFiring(AbstractBean bean) {
        // add property listener
        PropertyChangeReport report = new PropertyChangeReport();
        bean.addPropertyChangeListener(report);

        //fire all props
        Method[] mets = bean.getClass().getDeclaredMethods();
        for (Method met: mets) {
            String name = met.getName();
            if (name.startsWith("set") && name.length() > 3 && Character.isUpperCase(name.charAt(3)) && met.getParameterTypes().length == 1) {
                Class c = met.getParameterTypes()[0];
                Object newVal;
                Object val;
                try {
                    String getterPrefix = boolean.class.equals(c) || Boolean.class.equals(c) ? "is" : "get";
                    val = bean.getClass().getMethod(getterPrefix + name.substring(3)).invoke(bean);

                    if (c.equals(int.class) || c.equals(Integer.class)) {
                        newVal = (Integer) val + 1;
                    } else if (c.equals(Color.class)) {
                        newVal = Color.RED.equals(val) ? Color.RED.darker() : Color.RED;
                    } else if (c.equals(Boolean.class) || c.equals(boolean.class)) {
                        newVal = !(Boolean) val;
                    } else if (c.equals(Double.class) || c.equals(double.class)) {
                        newVal = (Double) val + .5d;
                    } else if (c.equals(Shape.class)) {
                        newVal = val instanceof Rectangle ? new Ellipse2D.Double(2,2,5,5) : new Rectangle(0,0,50,50);
                    } else if (c.isEnum()) {
                        Object[] enums = c.getEnumConstants();
                        newVal = val.equals(enums[0]) ? enums[1] : enums[0];
                    } else {
                        System.err.println("Handling for type " + met.getParameterTypes()[0] + " not handled yet ... if you got this message, please implement the support for objects used by bean you are testing.");
                        newVal = null;
                        continue;
                    }
                    //the test itself
                    met.invoke(bean, newVal);
                    // verify the result
                    TestUtils.assertPropertyChangeEvent(report, Character.toLowerCase(name.charAt(3)) + name.substring(4), val, newVal);
                    // clear test report
                    report.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
