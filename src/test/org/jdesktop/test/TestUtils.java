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
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.border.Border;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * Extends assert to get all the ease-of-use assert methods
 * @author rbair
 */
public final class TestUtils extends Assert {
    private static final Logger LOG = Logger.getLogger(TestUtils.class
            .getName());
    private TestUtils() {}
    
    public static void assertContainsType(Object[] objects, Class<?> clazz, int count) {
        if (objects.length == 0 && count == 0) return;
        assertTrue("not enough elements: expected == " + count 
                +" but was == " + objects.length, count <= objects.length);
        int found = 0;
        for (Object object : objects) {
            if (clazz.isAssignableFrom(object.getClass())) {
                found++;
            }
        };
        assertEquals("unexpected number of elements of type " + clazz, count, found);
    }
    
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
                assertEquals("old property " + property, oldValue, report.getLastOldValue(property));
            }
            if (verifyArrayItems && newValue != null && newValue.getClass().isArray()) {
                Collection l1 = newValue instanceof Collection ? (Collection) newValue : Arrays.asList((Object[])newValue);
                Collection l2 = report.getLastNewValue(property) instanceof Collection ? (Collection) report.getLastNewValue(property) : Arrays.asList((Object[]) report.getLastNewValue(property));
                assertEquals("new value of property " + property, l1.size(), l2.size());
                int index = 0;
                for (Iterator i1 = l1.iterator(), i2 = l2.iterator(); i1.hasNext() && i2.hasNext(); ) {
                    Object o1 = i1.next(); 
                    Object o2 = i2.next(); 
//                    if (o1 instanceof Date) {
//                        o1 = ((Date) o1).getTime();
//                        o2 = ((Date) o2).getTime();
//                    }
                    assertEquals("new value [" + index++ + "] of property " + property, o1, o2);
                }
            } else {
                assertEquals("new value of property " + property, newValue, report.getLastNewValue(property));
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

    public static void assertPCEFiring(Object bean, Collection<String> excludes) {
        // add property listener
        PropertyChangeReport report = new PropertyChangeReport();
        Method addPCL;
        try {
            addPCL = bean.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class);
        } catch (Exception e1) {
            LOG.fine("Skipping " + bean + " due to missing addPropertyChangeListener() method.");
            return;
        }

        //fire all props
        Method[] mets = bean.getClass().getDeclaredMethods();
        for (Method met: mets) {
            if (!Modifier.isPublic(met.getModifiers())) {
                continue;
            }
            String name = met.getName();
            // skip all except bean setters
            if (!name.startsWith("set") || name.length() < 3 || !Character.isUpperCase(name.charAt(3)) || met.getParameterTypes().length != 1) {
                continue;
            }
            // some of the setXXX methods we are just not going to test
            if ("setUI".equals(name)) {
                continue;
            }
            // some of the setXYZ methods are not bean properties setters, but shortcuts for setting properties of models or methods producing other then PCE events.
            if (excludes != null && excludes.contains(StringUtils.uncapitalise(name.substring(3)))) {
                continue;
            }
            try {
                bean = bean.getClass().newInstance();
                addPCL.invoke(bean, report);
            } catch (Exception e1) {
                // ignore and test all with whatever instance we've got from the caller;
            }
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
                    newVal = new String[] {"a", "b", "c", "d", "e", "f", "g"};
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
                } else if (c.equals(Dimension.class)) {
                    newVal = new Dimension(50,50);
                    if (newVal.equals(val)) {
                        newVal = new Dimension(100, 50);
                    }
                } else if (c.equals(Point2D.class)) {
                    newVal = new Point2D.Double(50,50);
                    if (newVal.equals(val)) {
                        newVal = new Point2D.Double(100, 50);
                    }
                } else if (c.equals(Insets.class)) {
                    newVal = new Insets(5,5,5,9);
                    if (newVal.equals(val)) {
                        newVal = new Insets(9,7,8, 5);
                    }
                } else if (c.equals(Rectangle2D.class)) {
                    newVal = new Rectangle2D.Double(5,5,5,9);
                    if (newVal.equals(val)) {
                        newVal = new Rectangle2D.Double(9,7,8, 5);
                    }
                } else if (c.equals(List.class)) {
                    newVal = new ArrayList(Arrays.asList(new String[] {"test"}));
                } else if (c.equals(Border.class)) {
                    newVal = BorderFactory.createEmptyBorder();
                    if (newVal.equals(val)) {
                        newVal = BorderFactory.createEtchedBorder();
                    }
                } else if (c.equals(Icon.class)) {
                    newVal = new ImageIcon(ImageIO.read(TestUtils.class.getResource("org/jdesktop/swingx/resources/images/green-orb.png")));
                } else if (c.equals(Image.class)) {
                    newVal = ImageIO.read(TestUtils.class.getResource("org/jdesktop/swingx/resources/images/green-orb.png"));
                } else if (c.equals(URL.class)) {
                    newVal = TestUtils.class.getResource("org/jdesktop/swingx/resources/images/green-orb.png");
                } else if (c.equals(File.class)) {
                    newVal = new File("dummy");
                } else if (c.equals(Date.class)) {
                    Calendar cal = GregorianCalendar.getInstance();
                    // set specific date range
                    cal.set(2009, 1, 1, 0, 0, 0);
                    // set ms to 0
                    cal.set(Calendar.MILLISECOND, 0);
                    newVal = cal.getTime();
                    if (newVal.equals(val)) {
                        cal.set(2009, 3, 1, 0, 0, 0);
                        newVal = cal.getTime();
                    }
                } else if (c.equals(Date[].class)) {
                    Calendar cal = GregorianCalendar.getInstance();
                    // set specific date range
                    cal.set(2009, 1, 11, 0, 0, 0);
                    // set ms to 0
                    cal.set(Calendar.MILLISECOND, 0);
                    Date d1 = cal.getTime();
                    cal.set(2009, 1, 15, 0, 0, 0);
                    newVal = new Date[] {d1, cal.getTime()};
                } else if (c.equals(JComponent.class)) {
                    newVal = new JComponent() {};
                } else if (c.equals(TimeZone.class)) {
                    newVal = TimeZone.getDefault();
                    if (newVal.equals(val)) {
                        newVal = TimeZone.getTimeZone(TimeZone.getAvailableIDs()[1]);
                    }
                } else if (c.equals(ComponentOrientation.class)) {
                    newVal = ComponentOrientation.RIGHT_TO_LEFT;
                    if (newVal.equals(val)) {
                        newVal = ComponentOrientation.LEFT_TO_RIGHT;
                    }
                } else if (c.isEnum()) {
                    Object[] enums = c.getEnumConstants();
                    newVal = val.equals(enums[0]) ? enums[1] : enums[0];
                } else {
//                    System.err.println("Handling for type " + met.getParameterTypes()[0] + " not handled yet ... if you got this message, please implement the support for objects used by bean you are testing.");
                    LOG.fine("Handling for type " + met.getParameterTypes()[0] + " not handled yet ... if you got this message, please implement the support for objects used by bean you are testing.");
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
                LOG.fine("by swingx convention: no printstackTrace allowed - " + e);
//                e.printStackTrace();
            }
        }
    }
}
