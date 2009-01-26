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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.test.TestUtils;

/**
 * Reflection based test for testing PCE firing.
 * 
 * @author rah003
 */
public class BeanEventsTest extends InteractiveTestCase {
    
    static Logger log = Logger.getAnonymousLogger();

    public void testAllPainterPCEFiring() throws Exception {
        List<Class> painters = InOutFactory.searchClassPath("org.jdesktop.swingx.", ".class");
        log.fine("Got " + painters.size());
        for (Class painter : painters) {
            if (!AbstractBean.class.isAssignableFrom(painter)) {
                continue;
            }
            try {
                Object inst = painter.newInstance();
                log.fine("Testing " + painter);
                TestUtils.assertPCEFiring((AbstractBean) inst);
            } catch (Exception e) {
                log.fine("ignoring " + painter + " because of " + e.getMessage());
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
