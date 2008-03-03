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
package org.jdesktop.swingx.combobox;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@code ComboBoxModel} for {@code Map}s. The model will always present a {@code Map}
 * consistently, once it is instantiated. However, unless the {@code Map} is ordered, as a
 * {@code java.util.TreeMap} is, the model is not guaranteed to present the maps in a consistent
 * order between instantiations.
 * 
 * @author jm158417
 * @author Karl George Schaefer
 * 
 * @param <K>
 *                the type of keys maintained by the map backing this model
 * @param <V>
 *                the type of mapped values
 */
public class MapComboBoxModel<K, V> extends ListComboBoxModel<K> {

    /**
     * The map backing this model.
     */
    protected Map<K, V> map_data;
    
    /**
     * @deprecated (since release 0.9.2) no longer used, remove after 0.9.3 release
     */
    @Deprecated
    protected boolean inverted;
    
    /**
     * Creates an empty model.
     */
    public MapComboBoxModel() {
        this(new HashMap<K, V>());
    }
    
    /**
     * Creates a model backed by the specified map.
     * 
     * @param map
     *                the map backing this model
     */
    public MapComboBoxModel(Map<K, V> map) {
        this.map_data = map;
        
        buildIndex();
        
        if(data.size() > 0) {
            selected = data.get(0);
        }
    }
    
    /**
     * Builds an index for this model and assigns it to {@link ListComboBoxModel#data} This method
     * ensures that the map is always presented in a consistent order.
     * <p>
     * This method is called by the constructor.
     */
    protected void buildIndex() {
        data = new ArrayList<K>(map_data.keySet());
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSize() {
        return map_data.size();
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent evt) {
        if(evt.getActionCommand().equals(UPDATE)) {
            buildIndex();
            fireContentsChanged(this, 0, getSize());
        }
    }

    /**
     * Selects an item from the model and returns that map value.
     * 
     * @param selectedItem
     *                the item to select
     * @return the value for the selected item
     */
    public V getValue(Object selectedItem) {
        return map_data.get(selectedItem);
    }
    
    /**
     * Selects an item from the model and returns that map value.
     * 
     * @param selectedItem
     *                selects the item at the specified index in this model
     * @return the value for the item at the selected index
     */
    public V getValue(int selectedItem) {
        return getValue(getElementAt(selectedItem));
    }
}
