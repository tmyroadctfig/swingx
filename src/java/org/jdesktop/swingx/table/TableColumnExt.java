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

package org.jdesktop.swingx.table;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.decorator.Sorter;

/**
 * TableColumn extension which adds support for view column configuration features
 * including column-visibility, sorting, and prototype values.
 *
 * @author Ramesh Gupta
 * @author Amy Fowler
 * @author Jeanette Winzenburg
 */
public class TableColumnExt extends javax.swing.table.TableColumn
    implements Cloneable {

    // removed - comparator now is a full-fledged bound property.
//    public static final String SORTER_COMPARATOR = "Sorter.COMPARATOR";
    protected boolean editable = true;
    protected boolean visible = true;
    protected Object prototypeValue = null;

    private Hashtable clientProperties = null;

    protected Sorter sorter = null;
    /** the comparator to use for this column */
    protected Comparator comparator;
    
    private Constructor	sorterConstructor = null;
    private final static Constructor	defaultSorterConstructor;
    private final static Class[]	sorterConstructorSignature =
        new Class[]{int.class, boolean.class};

    static {
        Constructor	constructor = null;
        try {
            Class	sorterClass = Class.forName("org.jdesktop.swingx.decorator.ShuttleSorter", true,
                                              TableColumnExt.class.getClassLoader());
            constructor = sorterClass.getConstructor(sorterConstructorSignature);
        }
        catch (Exception ex) {
        }
        defaultSorterConstructor = constructor;
    }

    /**
     * Creates new table view column with a model index = 0.
     */
    public TableColumnExt() {
        this(0);
    }

    /**
     * Creates new table view column with the specified model index.
     * @param modelIndex index of table model column to which this view column
     *        is bound.
     */
    public TableColumnExt(int modelIndex) {
        this(modelIndex, 75);	// default width taken from javax.swing.table.TableColumn
    }

    /**
     * Creates new table view column with the specified model index and column width.
     * @param modelIndex index of table model column to which this view column
     *        is bound.
     * @param width pixel width of view column
     */
    public TableColumnExt(int modelIndex, int width) {
        this(modelIndex, width, null, null);
    }

    /**
     * Creates new table view column with the specified model index, column
     * width, cell renderer and cell editor.
     * @param modelIndex index of table model column to which this view column
     *        is bound.
     * @param width pixel width of view column
     * @param cellRenderer the cell renderer which will render all cells in this
     *        view column
     * @param cellEditor the cell editor which will edit cells in this view column
     */
    public TableColumnExt(int modelIndex, int width,
                          TableCellRenderer cellRenderer, TableCellEditor cellEditor) {
        super(modelIndex, width, cellRenderer, cellEditor);
        this.sorterConstructor = defaultSorterConstructor;
    }

    /** cosmetic override: don't fool users if resize is
     * not possible due to fixed column width.
     */
    @Override
    public boolean getResizable() {
        return super.getResizable() && (getMinWidth() < getMaxWidth());
    }

    /**
     * Sets the editable property.  This property enables the table view to
     * control whether or not the user is permitted to edit cell values in this
     * view column, even if the model permits.  If the table model column corresponding to this view column
     * returns <code>true</code> for <code>isCellEditable</code> and this
     * property is <code>false</code>, then the user will not be permitted to
     * edit values from this view column, dispite the model setting.
     * If the model's <code>isCellEditable</code> returns <code>false</code>,
     * then this property will be ignored and cell edits will not be permitted
     * in this view column.
     * @see #isEditable
     * @see javax.swing.table.TableModel#isCellEditable
     * @param editable boolean indicating whether or not the user may edit cell
     *        values in this view column
     */
    public void setEditable(boolean editable) {
        boolean oldEditable = this.editable;
        this.editable = editable;
        firePropertyChange("editable",
                           Boolean.valueOf(oldEditable),
                           Boolean.valueOf(editable));
    }

    /**
     * @see #setEditable
     * @return boolean indicating whether or not the user may edit cell
     *        values in this view column
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets the prototypeValue property.  The value should be of a type
     * which corresponds to the column's class as defined by the table model.
     * If non-null, the JXTable instance will use this property to calculate
     * and set the initial preferredWidth of the column.  Note that this
     * initial preferredWidth will be overridden if the user resizes columns
     * directly.
     * @see #getPrototypeValue
     * @see org.jdesktop.swingx.JXTable#getPreferredScrollableViewportSize
     * @param value Object containing the value of the prototype to be used
     *         to calculate the initial preferred width of the column
     */
    public void setPrototypeValue(Object value) {
        Object oldPrototypeValue = this.prototypeValue;
        this.prototypeValue = value;
        firePropertyChange("prototypeValue",
                           oldPrototypeValue,
                           value);

    }

    /**
     * @see #setPrototypeValue
     * @return Object containing the value of the prototype to be used
     *         to calculate the initial preferred width of the column
     */
    public Object getPrototypeValue() {
        return prototypeValue;
    }

    /**
     * Sets a user-defined sorter for this column
     * @param sorterClassName String containing the name of the class which
     *        performs sorting on this view column
     */
    public void setSorterClass(String sorterClassName) {
        if ((sorterClassName == null) || (sorterClassName.length() == 0)){
            sorterConstructor = null;
        }
        else {
            try {
                Class	sorterClass = Class.forName(sorterClassName, true,
                                                  getClass().getClassLoader());
                sorterConstructor = sorterClass.getConstructor(sorterConstructorSignature);
            }
            catch (Exception ex) {
                sorterConstructor = null;
            }
        }
    }

    /**
     *
     * @return String containing the name of the class which
     *         performs sorting on this view column
     */
    public String getSorterClass() {
        return sorterConstructor == null ? null :
            sorterConstructor.getDeclaringClass().getName();
    }

    /**
     *
     * @return Sorter instance which performs sorting on this view column
     */
    public Sorter getSorter() {
        if (sorter == null) {
            if (sorterConstructor != null) {
                try {
                    sorter = (Sorter) sorterConstructor.newInstance(
                        new Object[] {
                            new Integer(getModelIndex()),
                            new Boolean(true)});
                   sorter.setComparator(getComparator());
                }
                catch (Exception ex) {
                }
            }
        }
        return sorter;
    }

    /**
     * returns the Comparator to use for this column.
     * @return <code>Comparator</code> to use for this column
     */
    public Comparator getComparator() {
        return comparator;
    }

    /**
     * sets the comparator to use for this column.
     * Updates the column's sorter with the given comparator.
     * NOTE: it's up to clients to not re-set the sorter's comparator
     * somewhere else - the column cannot guarantee to keep both in synch!
     *  
     * 
     * @param comparator
     */
    public void setComparator(Comparator comparator) {
        Comparator old = getComparator();
        this.comparator = comparator;
        if (sorter != null) {
            sorter.setComparator(comparator);
        }
        firePropertyChange("comparator", old, getComparator());
    }
    
    /**
     *
     * @return boolean indicating whether this view column is sortable
     */
    public boolean isSortable() {
        return sorterConstructor != null;
    }

    /**
     * Sets the title of this view column.  This is a convenience
     * wrapper for <code>setHeaderValue</code>.
     * @param title String containing the title of this view column
     */
    public void setTitle(String title) {
        setHeaderValue(title);				// simple wrapper
    }

    /**
     * Convenience method which returns the headerValue property after
     * converting it to a string. 
     * @return String containing the title of this view column or null if
     *   no headerValue is set.
     */
    public String getTitle() {
        Object header = getHeaderValue();
        return header != null ? header.toString() : null;	// simple wrapper
    }

    /**
     * Sets the visible property.  This property controls whether or not
     * this view column is currently visible in the table.
     * @see #setVisible
     * @param visible boolean indicating whether or not this view column is
     *        visible in the table
     */
    public void setVisible(boolean visible) {
        boolean oldVisible = this.visible;
        this.visible = visible;
        firePropertyChange("visible",
                           Boolean.valueOf(oldVisible),
                           Boolean.valueOf(visible));

    }

    /**
     * @see #setVisible
     * @return boolean indicating whether or not this view column is
     *        visible in the table
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Stores the object value using the specified key.
     * @see #getClientProperty
     * @param key Object which is used as key to retrieve value
     * @param value Object containing value of client property
     * @throws IllegalArgumentException if key == null
     */
    public void putClientProperty(Object key, Object value) {
        if (key == null)
            throw new IllegalArgumentException("null key");

        if ((value == null) && (getClientProperty(key) == null)) {
            return;
        }

        Object old = getClientProperty(key);
        if (value == null) {
            getClientProperties().remove(key);
        }
        else {
            getClientProperties().put(key, value);
        }

        firePropertyChange(key.toString(), old, value);
        /* Make all fireXXX methods in TableColumn protected instead of private */
    }

    /**
     * Retrieves the object value using the specified key.
     * @see #putClientProperty
     * @param key Object which is used as key to retrieve value
     * @return Object containing value of client property
     */
    public Object getClientProperty(Object key) {
        return ((key == null) || (clientProperties == null)) ?
                null : clientProperties.get(key);
    }

    private Hashtable getClientProperties() {
        if (clientProperties == null) {
            clientProperties = new Hashtable();
        }
        return clientProperties;
    }

    /**
      * Returns a clone of this TableColumn. Some implementations of TableColumn
      * may assume that all TableColumnModels are unique, therefore it is
      * recommended that the same TableColumn instance not be added more than
      * once to a TableColumnModel. To show TableColumns with the same column of
      * data from the model, create a new instance with the same modelIndex.
      *
      * @return a clone of this TableColumn
      */
     @Override
     public Object clone() {
         // TODO: JW: where are the client properties?
         final TableColumnExt copy = new TableColumnExt(
             this.getModelIndex(), this.getWidth(),
             this.getCellRenderer(), this.getCellEditor());

         copy.setEditable(this.isEditable());
         copy.setHeaderValue(this.getHeaderValue());	// no need to copy setTitle();
         copy.setIdentifier(this.getIdentifier());
         copy.setMaxWidth(this.getMaxWidth());
         copy.setMinWidth(this.getMinWidth());
         copy.setPreferredWidth(this.getPreferredWidth());
         copy.setPrototypeValue(this.getPrototypeValue());
         // JW: isResizable is overridden to return a calculated property!
         copy.setResizable(super.getResizable());
         copy.setVisible(this.isVisible());
         copy.setSorterClass(this.getSorterClass());
         copy.sorterConstructor = sorterConstructor;
         copy.setComparator(getComparator());
         return copy;
     }

     protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
         if ((oldValue != null && !oldValue.equals(newValue)) ||
              oldValue == null && newValue != null) {
             PropertyChangeListener pcl[] = getPropertyChangeListeners();
             if (pcl != null && pcl.length != 0) {
                 PropertyChangeEvent pce = new PropertyChangeEvent(this,
                     propertyName,
                     oldValue, newValue);

                 for (int i = 0; i < pcl.length; i++) {
                     pcl[i].propertyChange(pce);
                 }
             }
         }
     }
}
