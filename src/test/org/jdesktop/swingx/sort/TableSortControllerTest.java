/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.sort;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.test.AncientSwingTeam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit test of TableSortController.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class TableSortControllerTest extends AbstractTestSortController<TableSortController<TableModel>, TableModel> {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(TableSortControllerTest.class.getName());
    


    public static void main(String[] args) {
        TableSortControllerTest test = new TableSortControllerTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    /**
     * 
      */
    @Test
    public void testUseStringValueProvider() {
        registry.setStringValue(sv, Color.class);
        controller.setStringValueProvider(registry);
        RowFilter<Object, Object> filter = RowFilter.regexFilter("R/G/B: -2", 2);
        controller.setRowFilter(filter);
        assertTrue("view row count: " + controller.getViewRowCount(), controller.getViewRowCount() > 0);
    }

    @Override
    protected int getColumnCount() {
        return ((TableModel) controller.getModel()).getColumnCount();
    }
    

//-------------------- utility methods and setup
    
    /**
     * @param registry2
     * @param teamModel2
     */
    private void initColumnClasses(StringValueRegistry registry,
            TableModel model) {
        Map<Integer, Class<?>> classPerColumn = new HashMap<Integer, Class<?>>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (!Object.class.equals(model.getColumnClass(i))) {
                classPerColumn.put(i, model.getColumnClass(i));
            }
        }
        registry.setColumnClasses(classPerColumn);
    }

    /**
     * @param registry2
     * @param class1
     */
    private void installPerClass(StringValueRegistry registry,
            Class<?>... clazz ) {
        Map<Integer, Class<?>> classPerColumn = new HashMap<Integer, Class<?>>();
        for (int i = 0; i < clazz.length; i++) {
            classPerColumn.put(i, clazz[i]);
        }
        registry.setColumnClasses(classPerColumn);
    }

    @Override
    protected TableSortController<TableModel> createDefaultSortController(
            TableModel model) {
        return new TableSortController<TableModel>(model);
    }


    @Override
    protected TableModel createModel() {
        return new AncientSwingTeam();
    }


    @Override
    protected void setupModelDependentState(TableModel model) {
      initColumnClasses(registry,  model);

    }

    
}
