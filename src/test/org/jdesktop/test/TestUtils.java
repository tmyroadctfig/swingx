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
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;

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
     * report against the expected values.
     * 
     * @param report the PropertyReport which received the event
     * @param property the expected name of the property
     * @param oldValue the expected old value 
     * @param newValue the expected new value
     * @param single flag to denote if we expect one event only
     * @param verifyArrayItems check array items one by one rather then whole arrays
     */
    public static void assertPropertyChangeEvent(PropertyChangeReport report, 
            String property, Object oldValue, Object newValue, boolean single, boolean verifyArrayItems) {
        if (report.getEventCount() > 1) {
            LOG.info("events: " + report.getEventNames());
        }
        if (single) {
            assertEquals("exactly one event", 1, report.getEventCount());
            assertEquals("property", property, report.getLastProperty());
            if (verifyArrayItems && oldValue != null && oldValue.getClass().isArray()) {
                List l1 = Arrays.asList((Object[]) oldValue);
                List l2 = Arrays.asList((Object[]) report.getLastOldValue());
                assertEquals("last old value", l1.size(), l2.size());
                for (int i = 0; i < l1.size();i++) {
                    assertEquals("last old value", l1.get(i), l2.get(i));
                }
            } else {
                assertEquals("last old value", oldValue, report.getLastOldValue());
            }
            if (verifyArrayItems && newValue != null && newValue.getClass().isArray()) {
                List l1 = Arrays.asList(newValue);
                List l2 = Arrays.asList(report.getLastNewValue());
                assertEquals("last new value", l1.size(), l2.size());
                for (int i = 0; i < l1.size();i++) {
                    assertEquals("last new value", l1.get(i), l2.get(i));
                }
            } else {
                assertEquals("last new value", newValue, report.getLastNewValue());
            }
        } else {
            assertEquals("one event of property " + property, 1, report.getEventCount(property));
            if (verifyArrayItems && oldValue != null && oldValue.getClass().isArray()) {
                List l1 = Arrays.asList((Object[]) oldValue);
                List l2 = Arrays.asList((Object[]) report.getLastOldValue(property));
                assertEquals("old value", l1.size(), l2.size());
                for (int i = 0; i < l1.size();i++) {
                    assertEquals("old value", l1.get(i), l2.get(i));
                }
            } else {
                assertEquals("old property", oldValue, report.getLastOldValue(property));
            }
            if (verifyArrayItems && newValue != null && newValue.getClass().isArray()) {
                List l1 = Arrays.asList((Object[]) newValue);
                List l2 = Arrays.asList((Object[]) report.getLastNewValue(property));
                assertEquals("new value", l1.size(), l2.size());
                for (int i = 0; i < l1.size();i++) {
                    assertEquals("new value", l1.get(i), l2.get(i));
                }
            } else {
                assertEquals("new property", newValue, report.getLastNewValue(property));
            }
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
                    } else if (c.equals(Color.class) || c.equals(Paint.class)) {
                        newVal = Color.RED.equals(val) ? Color.RED.darker() : Color.RED;
                    } else if (c.equals(Boolean.class) || c.equals(boolean.class)) {
                        newVal = !(Boolean) val;
                    } else if (c.equals(Double.class) || c.equals(double.class)) {
                        newVal = (Double) val + .5d;
                    } else if (c.equals(Float.class) || c.equals(float.class)) {
                        newVal = (Float) val + .73f;
                    } else if (c.equals(Shape.class)) {
                        newVal = val instanceof Rectangle ? new Ellipse2D.Double(2,2,5,5) : new Rectangle(0,0,50,50);
                    } else if (c.equals(String.class)) {
                        newVal = "blah";
                        if (newVal.equals(val)) {
                            newVal = "blahblah";
                        }
                    } else if (c.equals(Font.class)) {
                        newVal = new Font("times", Font.BOLD, 24);
                        if (newVal.equals(val)) {
                            newVal = new Font("times", Font.PLAIN, 16);
                        }
                    } else if (c.equals(AffineTransform.class)) {
                        newVal = AffineTransform.getRotateInstance(.45);
                        if (newVal.equals(val)) {
                            newVal = AffineTransform.getScaleInstance(.2, .2);
                        }
                    } else if (c.equals(Properties.class)) {
                        Properties p = new Properties();
                        p.put("bla", "blah");
                        newVal = p;
                    } else if (c.equals(String[].class)) {
                        newVal = new String[] {"a", "b"};
                    } else if (c.equals(Preferences.class)) {
                        newVal = new AbstractPreferences(null, "") {

                            @Override
                            protected AbstractPreferences childSpi(String name) {
                                return this;
                            }

                            @Override
                            protected String[] childrenNamesSpi() throws BackingStoreException {
                                return null;
                            }

                            @Override
                            protected void flushSpi() throws BackingStoreException {
                            }

                            @Override
                            protected String getSpi(String key) {
                                return null;
                            }

                            @Override
                            protected String[] keysSpi() throws BackingStoreException {
                                return null;
                            }

                            @Override
                            protected void putSpi(String key, String value) {
                            }

                            @Override
                            protected void removeNodeSpi() throws BackingStoreException {
                            }

                            @Override
                            protected void removeSpi(String key) {
                            }

                            @Override
                            protected void syncSpi() throws BackingStoreException {
                            }};
                    } else if (c.equals(BufferedImage.class)) {
                        newVal = GraphicsUtilities.createCompatibleImage(100,111);
                    } else if (c.equals(Connection.class)) {
                        newVal = null;
                        // not really going to implement all methods from connection interface here...
                    } else if (c.equals(Painter[].class)) {
                        newVal = new Painter[] {new MattePainter()};
                    } else if (c.equals(Painter.class)) {
                        newVal = new MattePainter();
                        if (newVal.equals(val)) {
                            newVal = new MattePainter(Color.BLUE, true);
                        }
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
                    TestUtils.assertPropertyChangeEvent(report, Character.toLowerCase(name.charAt(3)) + name.substring(4), val, newVal, false, true);
                    // clear test report
                    report.clear();
                } catch (NoSuchMethodException e) {
                    // getter doesn't exist ... not really a bean method ... skip
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
