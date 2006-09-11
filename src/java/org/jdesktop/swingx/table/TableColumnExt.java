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
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JavaBean;

/**
 * TableColumn extension with enhanced support for view column configuration.
 * The general drift is to strengthen the TableColumn abstraction as <b>the</b>
 * place to configure and dynamically update view column properties, covering a
 * broad range of typical customization requirements.
 * <p>
 * 
 * A prominent group of properties allows fine-grained, per-column control of
 * corresponding Table/-Header features.
 * 
 * <ul>
 * <li><b>Sorting</b>: <code>sortable</code> controls whether this column
 * should be sortable by user's sort gestures; <code>Comparator</code> can hold a
 * column specific type.
 * 
 * <li><b>Editing</b>: <code>editable</code> controls whether cells of this
 * column should be accessible to in-table editing.
 * 
 * <li><b>Tooltip</b>: <code>toolTipText</code> holds the column tooltip
 * which is shown when hovering over the column's header.
 * </ul>
 * 
 * TODO: explain visible (collaborator-used-by TableColumnModelExt,
 * ColumnControlButton)
 * <p>
 * 
 * Analogous to <code>JComponent</code>, this class supports per-instance
 * "client" properties. They are meant as a small-scale extension mechanism.
 * They are similar to regular bean properties in that registered
 * <code>PropertyChangeListener</code>s are notified about changes. TODO:
 * example?
 * <p>
 * 
 * TODO: explain prototype (sizing, collaborator-used-by ColumnFactory (?))
 * <p>
 * 
 * @author Ramesh Gupta
 * @author Amy Fowler
 * @author Jeanette Winzenburg
 * 
 * @see TableColumnModelExt
 * @see ColumnFactory
 * @see JComponent#putClientProperty
 */
public class TableColumnExt extends TableColumn
    implements Cloneable {

    protected boolean visible = true;
    protected Object prototypeValue = null;


    /** per-column comparator  */
    protected Comparator comparator;
    /** per-column sortable property. */
    protected boolean sortable = true;
    /** per-column editable property. */
    protected boolean editable = true;
    /** per-column header tooltip text. */
    private String toolTipText;
    
    /** storage for client properties. */
    protected Hashtable<Object, Object> clientProperties = null;

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
    }

    /** 
     * Returns true if the user <i>can</i> resize the TableColumn's width, 
     * false otherwise. This is a usability override: it takes into account
     * the case where it's principally <i>allowed</i> to resize the column
     * but not possible because the column has fixed size.
     * 
     * @return a boolean indicating whether the user can resize this column.
     */
    @Override
    public boolean getResizable() {
        // TODO JW: resizable is a bound property, so to be strict
        // we'll need to override setMin/MaxWidth to fire resizable
        // property change.
        return super.getResizable() && (getMinWidth() < getMaxWidth());
    }

    /**
     * Sets the editable property. This property allows to mark all cells in a
     * column as read-only, independent of the per-cell editability as returned
     * by the <code>TableModel.isCellEditable</code>. If the cell is
     * read-only in the model layer, this property will have no effect.
     * 
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
     * @see #setComparator
     * @return <code>Comparator</code> to use for this column
     */
    public Comparator getComparator() {
        return comparator;
    }

    /**
     * Sets the comparator to use for this column.
     * JXTable sorting api respects this property by routing
     * to the SortController.
     * 
     * @see #getComparator
     * @param comparator a custom comparator to use in interactive
     *    sorting.
     */
    public void setComparator(Comparator comparator) {
        Comparator old = getComparator();
        this.comparator = comparator;
        firePropertyChange("comparator", old, getComparator());
    }
    
    /**
     * @see #setSortable
     * @return boolean indicating whether this view column is sortable
     */
    public boolean isSortable() {
        return sortable;
    }

    /**
     * Sets the sortable property. JXTable sorting api respects this
     * property by not sorting this column if false. The default value
     * is true.
     * @see #isSortable 
     * @param sortable boolean indicating whether or not this column can
     *        be sorted in the table
     */
    public void setSortable(boolean sortable) {
        boolean old = isSortable();
        this.sortable = sortable;
        firePropertyChange("sortable", old, isSortable());
    }
 
    /**
     * Returns the text of the column ToolTip. This is typically used
     * by <code>JXTableHeader</code> on mouseOver the column's header cell.
     * 
     * @return the text of the column ToolTip.
     * @see #setToolTipText(String)
     */
    public String getToolTipText() {
        return toolTipText;
    }
    
    /**
     * Sets the text of the header ToolTip for this column. 
     * 
     * @param toolTipText text to show.
     */
    public void setToolTipText(String toolTipText) {
        String old = getToolTipText();
        this.toolTipText = toolTipText;
        firePropertyChange("toolTipText", old, getToolTipText());
        
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
     * Sets the <code>key</code> "client property" to <code>value</code>. 
     * If <code>value</code> is <code>null</code> this method will remove the property. 
     * Changes to
     * client properties are reported with <code>PropertyChange</code> events.
     * The name of the property (for the sake of PropertyChange events) is
     * <code>key.toString()</code>.
     * <p>
     * The <code>get/putClientProperty</code> methods provide access to a
     * per-instance hashtable, which is intended for small scale extensions of
     * TableColumn.
     * <p>
     * 
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
     * Returns the value of the property with the specified key. Only properties
     * added with <code>putClientProperty</code> will return a non-<code>null</code>
     * value.
     * 
     * @param key Object which is used as key to retrieve value
     * @return Object containing value of client property or <code>null</code>
     * 
     * @see #putClientProperty
     */
    public Object getClientProperty(Object key) {
        return ((key == null) || (clientProperties == null)) ?
                null : clientProperties.get(key);
    }

    private Hashtable<Object, Object> getClientProperties() {
        if (clientProperties == null) {
            clientProperties = new Hashtable<Object, Object>();
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
         final TableColumnExt copy = new TableColumnExt(
             this.getModelIndex(), this.getWidth(),
             this.getCellRenderer(), this.getCellEditor());

         copy.setEditable(this.isEditable());
         copy.setHeaderValue(this.getHeaderValue());	// no need to copy setTitle();
         copy.setToolTipText(getToolTipText());
         copy.setIdentifier(this.getIdentifier());
         copy.setMaxWidth(this.getMaxWidth());
         copy.setMinWidth(this.getMinWidth());
         copy.setPreferredWidth(this.getPreferredWidth());
         copy.setPrototypeValue(this.getPrototypeValue());
         // JW: isResizable is overridden to return a calculated property!
         copy.setResizable(super.getResizable());
         copy.setVisible(this.isVisible());
         copy.setSortable(this.isSortable());
         copy.setComparator(getComparator());
         copyClientPropertiesTo(copy);
         return copy;
     }

     /**
      * copies all clientProperties of this TableColumn to the target
      * column.
      * 
      * @param copy the target column.
      */
     protected void copyClientPropertiesTo(TableColumnExt copy) {
        if (clientProperties == null) return;
        for(Object key: clientProperties.keySet()) {
            copy.putClientProperty(key, getClientProperty(key));
        }
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
