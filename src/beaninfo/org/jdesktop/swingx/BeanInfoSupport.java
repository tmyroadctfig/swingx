/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rbair
 */
public abstract class BeanInfoSupport extends SimpleBeanInfo {
    private static Map/*<Class,Boolean>*/ introspectingState = new HashMap/*<Class,Boolean>*/();
    private Class beanClass;
    private int defaultPropertyIndex = -1;
    private int defaultEventIndex = -1;
    protected java.awt.Image iconColor16 = null;                    
    protected java.awt.Image iconColor32 = null;
    protected java.awt.Image iconMono16 = null;
    protected java.awt.Image iconMono32 = null;                  
    protected String iconNameC16 = null;                 
    protected String iconNameC32 = null;
    protected String iconNameM16 = null;
    protected String iconNameM32 = null;               
    private static Map/*<Class,BeanDescriptor>*/ beanDescriptors = new HashMap/*<Class,BeanDescriptor>*/();
    private static Map/*<Class,PropertyDescriptor[]>*/ propertyDescriptors = new HashMap/*<Class,PropertyDescriptor[]>*/();
    private static Map/*<Class,EventSetDescriptor[]>*/ eventDescriptors = new HashMap/*<Class,EventSetDescriptor[]>*/();
    private static Map/*<Class,MethodDescriptor[]>*/ methodDescriptors = new HashMap/*<Class,MethodDescriptor[]>*/();
    
    /** Creates a new instance of BeanInfoSupport */
    public BeanInfoSupport(Class beanClass) {
        this.beanClass = beanClass;
        Boolean b = (Boolean)introspectingState.get(beanClass);
        boolean introspecting = b == null ? false : b.booleanValue();
        if (!introspecting) {
            introspecting = true;
            introspectingState.put(beanClass, Boolean.valueOf(introspecting));
            try {
                BeanInfo info = Introspector.getBeanInfo(beanClass);
                beanDescriptors.put(beanClass, info.getBeanDescriptor());
                propertyDescriptors.put(beanClass, info.getPropertyDescriptors());
                eventDescriptors.put(beanClass, info.getEventSetDescriptors());
                methodDescriptors.put(beanClass, info.getMethodDescriptors());
                defaultPropertyIndex = info.getDefaultPropertyIndex();
                defaultEventIndex = info.getDefaultEventIndex();
                iconColor16 = info.getIcon(BeanInfo.ICON_COLOR_16x16);
                iconColor32 = info.getIcon(BeanInfo.ICON_COLOR_32x32);
                iconMono16 = info.getIcon(BeanInfo.ICON_MONO_16x16);
                iconMono32 = info.getIcon(BeanInfo.ICON_MONO_32x32);
            } catch (Exception e) {
                e.printStackTrace();
            }
            initialize();
            introspecting = false;
            introspectingState.put(beanClass, Boolean.valueOf(introspecting));
        }
    }
    
    protected abstract void initialize();
    
    protected void setIconsBasedOn(Class clazz) {
        try {
            BeanInfo info = Introspector.getBeanInfo(clazz);
            iconColor16 = info.getIcon(BeanInfo.ICON_COLOR_16x16);
            iconColor32 = info.getIcon(BeanInfo.ICON_COLOR_32x32);
            iconMono16 = info.getIcon(BeanInfo.ICON_MONO_16x16);
            iconMono32 = info.getIcon(BeanInfo.ICON_MONO_32x32);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        return (BeanDescriptor)beanDescriptors.get(beanClass);
    }
    
    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return (PropertyDescriptor[])propertyDescriptors.get(beanClass);
    }
    
    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return (EventSetDescriptor[])eventDescriptors.get(beanClass);
    }
    
    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return (MethodDescriptor[])methodDescriptors.get(beanClass);
    }
    
    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }
    
    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean.
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }
    
    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.   Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param  iconKind  The kind of icon requested.  This should be
     *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32,
     *    ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon.  May
     *    return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        switch ( iconKind ) {
            case ICON_COLOR_16x16:
                return getImage(iconNameC16, iconColor16);
            case ICON_COLOR_32x32:
                return getImage(iconNameC32, iconColor32);
            case ICON_MONO_16x16:
                return getImage(iconNameM16, iconMono16);
            case ICON_MONO_32x32:
                return getImage(iconNameM32, iconMono32);
            default:
                return null;
        }
    }
    
    private java.awt.Image getImage(String name, java.awt.Image img) {
        if (img == null) {
            if (name != null) {
                img = loadImage(name);
            }
        }
        return img;
    }
}
