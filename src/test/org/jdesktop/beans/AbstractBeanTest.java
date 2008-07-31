/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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

import junit.framework.TestCase;

import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;

/**
 * Testing AbstractBean.
 * 
 * @author Jeanette Winzenburg
 */
public class AbstractBeanTest extends TestCase {
    
    /**
     * test clone: listener on original must not be registered to clone.
     */
    public void testClone() {
        CloneableBean bean = new CloneableBean();
        PropertyChangeReport report = new PropertyChangeReport();
        bean.addPropertyChangeListener(report);
        String property = "dummy";
        bean.setProperty(property);
        TestUtils.assertPropertyChangeEvent(report, "property", null, property);
        report.clear();
        CloneableBean clone = (CloneableBean) bean.clone();
        assertEquals(0, clone.getPropertyChangeListeners().length);
        clone.setProperty("other");
        assertEquals(0, report.getEventCount());
    }
    
    public static class CloneableBean extends AbstractBean implements Cloneable {

        public void setProperty(String property) {
            firePropertyChange("property", null, property);
        }
        
        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
            return null;
        }
        
    }

}
