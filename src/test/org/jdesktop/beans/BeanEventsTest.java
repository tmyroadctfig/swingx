/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.beans;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import junit.framework.TestCase;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.JXSearchPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.JXLoginPane.SimpleNamePanel;
import org.jdesktop.swingx.renderer.JRendererCheckBox;
import org.jdesktop.swingx.renderer.JRendererLabel;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jdesktop.swingx.renderer.WrappingIconPanel;
import org.jdesktop.test.TestUtils;

/**
 * Reflection based test for testing PCE firing.
 * 
 * @author rah003
 */
public class BeanEventsTest extends InteractiveTestCase {
    
    static Logger log = Logger.getAnonymousLogger();

    public void testAllPainterPCEFiring() throws Exception {
        log.setLevel(Level.ALL);
        List<Class> beanClasses = InOutFactory.searchClassPath("org.jdesktop.swingx.", ".class");
        MultiMap excludes = new MultiHashMap();
        // shorthand for getModel.setColumnMargin
        excludes.put(JXTable.class, "columnMargin");
        // no op due to sorting conflict 
        excludes.put(JXTreeTable.class, "sortable");
        // no op due to sorting conflict 
        excludes.put(JXTreeTable.class, "filters");
        // shorthand for getRenderer.setLargeModel
        excludes.put(JXTreeTable.class, "largeModel");
        // shorthand for getRenderer.setOverwriteRendererIcons
        excludes.put(JXTreeTable.class, "overwriteRendererIcons");
        // shorthand for getRenderer.setRootVisible
        excludes.put(JXTreeTable.class, "rootVisible");
        // shorthand for getRenderer.setToggleClickCount
        excludes.put(JXTreeTable.class, "toggleClickCount");
        // shorthand for getLayout.setDividerSize
        excludes.put(JXMultiSplitPane.class, "dividerSize");
        // shorthand for getLayout.setModel
        excludes.put(JXMultiSplitPane.class, "model");
        // shorthand for getSelectionModel.setSelectionMode
        excludes.put(JXMonthView.class, "selectionMode");
        // shorthand for getSelectionModel.setUpperBound
        excludes.put(JXMonthView.class, "upperBound");
        // shorthand for getSelectionModel.setLowerBound
        excludes.put(JXMonthView.class, "lowerBound");
        // shorthand for getSelectionModel.setSelectionInterval(newDate, newDate);
        excludes.put(JXMonthView.class, "selectionDate");
        // shorthand for JTextField.this.setText 
        excludes.put(SimpleNamePanel.class, "userName");
        // shorthand for getEditor.setFont 
        excludes.put(JXDatePicker.class, "font");
        // according to javadoc: api hack for testing 
        excludes.put(JXDatePicker.class, "linkDay");
        
        // incorrect method name ... shoud be addPatternFilter instead 
        excludes.put(JXSearchPanel.class, "patternFilter");
        // JRendererLabel doesn't fire events for performance reasons 
        excludes.put(JRendererLabel.class, "toolTipText");
        // JRendererLabel doesn't fire events for performance reasons 
        excludes.put(JRendererLabel.class, "painter");
        // JRendererCheckBox doesn't fire events for performance reasons 
        excludes.put(JRendererCheckBox.class, "toolTipText");
        // JRendererCheckBox doesn't fire events for performance reasons 
        excludes.put(JRendererCheckBox.class, "painter");
        // shorthand for getComponent.setPainter()
        excludes.put(WrappingIconPanel.class, "painter");
        // JXRendererHyperlink doesn't fire events for performance reasons 
        excludes.put(JXRendererHyperlink.class, "toolTipText");
        // JXRendererHyperlink doesn't fire events for performance reasons 
        excludes.put(JXRendererHyperlink.class, "painter");
        // shorthand for getModel.setMinimumValue 
        excludes.put(JXMultiThumbSlider.class, "minimumValue");
        // shorthand for getModel.setMaximumValue 
        excludes.put(JXMultiThumbSlider.class, "maximumValue");
        // shorthand for getContentPane.setMinimumSize
        excludes.put(JXCollapsiblePane.class, "minimumSize");
        // shorthand for getContentPane.setPreferredSize
        excludes.put(JXCollapsiblePane.class, "preferredSize");
        // shorthand for getContentPane.setBorder
        excludes.put(JXCollapsiblePane.class, "border");
        // this is a tricky one ... potentially a bug somewhere. In case preferredSize is not set yet, call to getPreferredSize() is propagated all the way up to Container, which in turn requests preferred size from the layout manager. On the other hand when preferred size is set, "old" preferred size for the purpose of event is determined (this time in Component) solely from the previous value of private variable preferredSize and therefore null
        excludes.put(JXImagePanel.class, "preferredSize");
        
        log.fine("Got " + beanClasses.size());
        for (Class beanClass : beanClasses) {
            if (!AbstractBean.class.isAssignableFrom(beanClass) && !JComponent.class.isAssignableFrom(beanClass) || TestCase.class.isAssignableFrom(beanClass)) {
                log.fine("Skipping " + beanClass);
                continue;
            }
            try {
                Object inst = beanClass.newInstance();
                log.info("Testing " + beanClass);
                TestUtils.assertPCEFiring( inst, (Collection<String>) excludes.get(beanClass));
            } catch (Exception e) {
                log.info("ignoring " + beanClass + " because of " + e.getMessage());
            }
        }
    }

    static class InOutFactory {

        /**
         * Classloader to be used to obtain resources from file system.
         */
        private ClassLoader classloader;

        /**
         * List of the resource found in the classpath.
         */
        private ArrayList list;

        /**
         * Extension of the resource to be found in the classpath.
         */
        private String extension;

        private String prefix;

        /**
         * Search for the resource with the extension in the classpath. Method
         * self-instantiate factory for every call to ensure thread safety.
         * @param extension Mandatory extension of the resource. If all resources
         * are required extension should be empty string. Null extension is not
         * allowed and will cause method to fail.
         * @return List of all resources with specified extension.
         */
        public static List searchClassPath(String prefix, String extension) {
            InOutFactory factory = new InOutFactory();
            factory.prefix = prefix;
            return factory.find(extension);
        }

        /**
         * Search for the resource with the extension in the classpath.
         * @param extension Mandatory extension of the resource. If all resources
         * are required extension should be empty string. Null extension is not
         * allowed and will cause method to fail.
         * @return List of all resources with specified extension.
         */
        private List find(String extension) {
            this.extension = extension;
            this.list = new ArrayList();
            this.classloader = this.getClass().getClassLoader();
            String classpath = System.getProperty("java.class.path");

            try {
                Method method =
                    this.classloader.getClass().getMethod("getClassPath", null);
                if (method != null) {
                    classpath = (String) method.invoke(this.classloader, null);
                }
            } catch (Exception e) {
                // ignore
            }
            if (classpath == null) {
                classpath = System.getProperty("java.class.path");
            }

            StringTokenizer tokenizer =
                new StringTokenizer(classpath, File.pathSeparator);
            String token;
            File dir;
            String name;
            while (tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();
                dir = new File(token);
                if (dir.isDirectory()) {
                    lookInDirectory("", dir);
                }
                if (dir.isFile()) {
                    name = dir.getName().toLowerCase();
                    if (name.endsWith(".zip") || name.endsWith(".jar")) {
                        this.lookInArchive(dir);
                    }
                }
            }
            return this.list;
        }

        /**
         * @param name Name of to parent directories in java class notation (dot
         * separator)
         * @param dir Directory to be searched for classes.
         */
        private void lookInDirectory(String name, File dir) {
            log.fine( "Looking in directory [" + dir.getName() + "].");
            File[] files = dir.listFiles();
            File file;
            String fileName;
            final int size = files.length;
            for (int i = 0; i < size; i++) {
                file = files[i];
                fileName = file.getName();
                if (file.isFile()
                    && fileName.toLowerCase().endsWith(this.extension)) {
                    try {
                        if (this.extension.equalsIgnoreCase(".class")) {
                            fileName = fileName.substring(0, fileName.length() - 6);
                            // filter ignored resources
                            if (!(name + fileName).startsWith(this.prefix)) {
                                continue;
                            }

                            log.fine(
                                "Found class: [" + name + fileName + "].");
                            this.list.add(Class.forName(name + fileName));
                        } else {
                            this.list.add(
                                this.classloader.getResource(
                                    name.replace('.', File.separatorChar)
                                        + fileName));
                        }
                    } catch (ClassNotFoundException e) {
                        // ignore
                    } catch (NoClassDefFoundError e) {
                            //ignore too
                    } catch (ExceptionInInitializerError e) {
                        if (e.getCause() instanceof HeadlessException) {
                            // running in headless env ... ignore 
                        } else {
                            throw e;
                        }
                    }
                }
                // search recursively.
                // I don't like that but we will see how it will work.
                if (file.isDirectory()) {
                    lookInDirectory(name + fileName + ".", file);
                }
            }

        }

        /**
         * Search archive files for required resource.
         * @param archive Jar or zip to be searched for classes or other resources.
         */
        private void lookInArchive(File archive) {
            log.fine(
                "Looking in archive ["
                    + archive.getName()
                    + "] for extension ["
                    + this.extension
                    + "].");
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(archive);
            } catch (IOException e) {
                log.warning(
                    "Non fatal error. Unable to read jar item.");
                return;
            }
            Enumeration entries = jarFile.entries();
            JarEntry entry;
            String entryName;
            while (entries.hasMoreElements()) {
                entry = (JarEntry) entries.nextElement();
                entryName = entry.getName();
                if (entryName.toLowerCase().endsWith(this.extension)) {
                    try {
                        if (this.extension.equalsIgnoreCase(".class")) {
                            // convert name into java classloader notation
                            entryName =
                                entryName.substring(0, entryName.length() - 6);
                            entryName = entryName.replace('/', '.');

                            // filter ignored resources
                            if (!entryName.startsWith(this.prefix)) {
                                continue;
                            }

                            log.fine(
                                "Found class: [" + entryName + "]. ");
                            this.list.add(Class.forName(entryName));
                        } else {
                            this.list.add(this.classloader.getResource(entryName));
                            log.fine(
                                "Found appropriate resource with name ["
                                    + entryName
                                    + "]. Resource instance:"
                                    + this.classloader.getResource(entryName));
                        }
                    } catch (Throwable e) {
                        // ignore
                        log.warning(
                            "Unable to load resource ["
                                + entryName
                                + "] form file ["
                                + archive.getAbsolutePath()
                                + "].");
                    }
                }
            }
        }
    }
}
