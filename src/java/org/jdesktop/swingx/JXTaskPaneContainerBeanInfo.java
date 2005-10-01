/*
 * Generated file - Do not edit!
 */
package org.jdesktop.swingx;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.util.Vector;

/**
 * BeanInfo class for JXTaskPaneContainer.
 */
public class JXTaskPaneContainerBeanInfo extends SimpleBeanInfo
{
   /** Description of the Field */
   protected BeanDescriptor bd = new BeanDescriptor(org.jdesktop.swingx.JXTaskPaneContainer.class);
   /** Description of the Field */
   protected Image iconMono16 = loadImage("resources/JXTaskPaneContainer16-mono.gif");
   /** Description of the Field */
   protected Image iconColor16 = loadImage("resources/JXTaskPaneContainer16.gif");
   /** Description of the Field */
   protected Image iconMono32 = loadImage("resources/JXTaskPaneContainer32-mono.gif");
   /** Description of the Field */
   protected Image iconColor32 = loadImage("resources/JXTaskPaneContainer32.gif");

   /** Constructor for the JXTaskPaneContainerBeanInfo object */
   public JXTaskPaneContainerBeanInfo() throws java.beans.IntrospectionException
   {
   	// setup bean descriptor in constructor. 
       bd.setName("JXTaskPaneContainer");

       bd.setShortDescription("A component that contains JXTaskPanes.");

       bd.setValue("isContainer",Boolean.TRUE);

       BeanInfo info = Introspector.getBeanInfo(getBeanDescriptor().getBeanClass().getSuperclass());
       String order = info.getBeanDescriptor().getValue("propertyorder") == null ? "" : (String) info.getBeanDescriptor().getValue("propertyorder");
       PropertyDescriptor[] pd = getPropertyDescriptors();
       for (int i = 0; i != pd.length; i++)
       {
          if (order.indexOf(pd[i].getName()) == -1)
          {
             order = order + (order.length() == 0 ? "" : ":") + pd[i].getName();
          }
       }
       getBeanDescriptor().setValue("propertyorder", order);
   }

   /**
    * Gets the additionalBeanInfo
    *
    * @return   The additionalBeanInfo value
    */
   public BeanInfo[] getAdditionalBeanInfo()
   {
      Vector bi = new Vector();
      BeanInfo[] biarr = null;
      try
      {
         for (Class cl = org.jdesktop.swingx.JXTaskPaneContainer.class.getSuperclass(); !cl.equals(java.awt.Component.class.getSuperclass()); cl = cl.getSuperclass()) {
            bi.addElement(Introspector.getBeanInfo(cl));
         }
         biarr = new BeanInfo[bi.size()];
         bi.copyInto(biarr);
      }
      catch (Exception e)
      {
         // Ignore it
      }
      return biarr;
   }

   /**
    * Gets the beanDescriptor
    *
    * @return   The beanDescriptor value
    */
   public BeanDescriptor getBeanDescriptor()
   {
      return bd;
   }

   /**
    * Gets the defaultPropertyIndex
    *
    * @return   The defaultPropertyIndex value
    */
   public int getDefaultPropertyIndex()
   {
      String defName = "";
      if (defName.equals(""))
      {
         return -1;
      }
      PropertyDescriptor[] pd = getPropertyDescriptors();
      for (int i = 0; i < pd.length; i++)
      {
         if (pd[i].getName().equals(defName))
         {
            return i;
         }
      }
      return -1;
   }

   /**
    * Gets the icon
    *
    * @param type  Description of the Parameter
    * @return      The icon value
    */
   public Image getIcon(int type)
   {
      if (type == BeanInfo.ICON_COLOR_16x16)
      {
         return iconColor16;
      }
      if (type == BeanInfo.ICON_MONO_16x16)
      {
         return iconMono16;
      }
      if (type == BeanInfo.ICON_COLOR_32x32)
      {
         return iconColor32;
      }
      if (type == BeanInfo.ICON_MONO_32x32)
      {
         return iconMono32;
      }
      return null;
   }

   /**
    * Gets the Property Descriptors
    *
    * @return   The propertyDescriptors value
    */
   public PropertyDescriptor[] getPropertyDescriptors() 
   {
      try
      {
         Vector descriptors = new Vector();
         PropertyDescriptor descriptor = null;

         return (PropertyDescriptor[]) descriptors.toArray(new PropertyDescriptor[descriptors.size()]);
      }
      catch (Exception e)
      {
        // Ignore it
      }
      return null;
   }

   /**
    * Gets the methodDescriptors attribute ...
    *
    * @return   The methodDescriptors value
    */
   public MethodDescriptor[] getMethodDescriptors() {
      Vector descriptors = new Vector();
      MethodDescriptor descriptor = null;
      Method[] m;
      Method method;

      try {
         m = Class.forName("org.jdesktop.swingx.JXTaskPaneContainer").getMethods();
      } catch (ClassNotFoundException e) {
         return new MethodDescriptor[0];
      }

      return (MethodDescriptor[]) descriptors.toArray(new MethodDescriptor[descriptors.size()]);
   }
}
