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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;

/**
 * <p>A ComboBoxModel implementation that safely wraps an Enum. It allows the
 * developer to directly use an enum as their model for a combobox without any
 * extra work, though the display can can be further customized. </p>
 *
 * <h4>Simple Usage</h4>
 * 
 * <p>The simplest usage is to wrap an <code>enum</code> inside the 
 * <code>EnumComboBoxModel</code> and then set it as the model on the
 * combo box. The combo box will then appear on screen with each value 
 * in the <code>enum</code> as a value in the combobox.
 * </p>
 * <p>ex:</p>
 * <pre><code>
 *  enum MyEnum { GoodStuff, BadStuff };
 *  ...
 *  JComboBox combo = new JComboBox();
 *  combo.setModel(new EnumComboBoxModel(MyEnum.class));
 * </code></pre>
 *
 * <h4>Type safe access</h4>
 * <p>By using generics and co-variant types you can make accessing elements from the model
 * be completely typesafe. ex:
 *</p>
 *
 *<pre><code>
 *  EnumComboBoxModel<MyEnum> enumModel = new EnumComboBoxModel<MyEnum1>(MyEnum1.class);
 *  MyEnum first = enumModel.getElement(0);
 *  MyEnum selected = enumModel.getSelectedItem();
 *</code></pre>
 *
 * <h4>Advanced Usage</h4>
 * <p>Since the exact <code>toString()</code> value of each enum constant 
 * may not be exactly what you want on screen (the values 
 * won't have spaces, for example) you can override to 
 * toString() method on the values when you declare your 
 * enum. Thus the display value is localized to the enum 
 * and not in your GUI code. ex:
 * <pre><code>
 *    private enum MyEnum {GoodStuff, BadStuff;
 *        public String toString() {
 *           switch(this) {
 *               case GoodStuff: return "Some Good Stuff";
 *               case BadStuff: return "Some Bad Stuff";
 *           }
 *           return "ERROR";
 *        }
 *    };
 * </code></pre>
 * 
 *
 * @author joshy
 */
public class EnumComboBoxModel<E extends Enum<E>> 
        extends AbstractListModel implements ComboBoxModel {
    private Object selected = null;
    private List<E> list;

    public EnumComboBoxModel(Class<E> en) {
        EnumSet<E> ens = EnumSet.allOf(en);
        list = new ArrayList<E>(ens);
        selected = list.get(0);
    }

    public int getSize() {
        return list.size();
    }

    public E getElementAt(int index) {
        return list.get(index);
    }

    public void setSelectedItem(Object anItem) {
	selected = anItem;
        this.fireContentsChanged(this,0,getSize());
    }
    
    public E getSelectedItem() {
	return (E)selected;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));
        
        
        JComboBox combo1 = new JComboBox();
        combo1.setModel(new EnumComboBoxModel(MyEnum1.class));
        frame.add(combo1);
        
        JComboBox combo2 = new JComboBox();
        combo2.setModel(new EnumComboBoxModel(MyEnum2.class));
        frame.add(combo2);
        
        EnumComboBoxModel<MyEnum1> enumModel = new EnumComboBoxModel<MyEnum1>(MyEnum1.class);
        JComboBox combo3 = new JComboBox();
        combo3.setModel(enumModel);
        frame.add(combo3);
        
        MyEnum1 selected = enumModel.getSelectedItem();
        
        frame.pack();
        frame.setVisible(true);
    }
    
    private enum MyEnum1 {GoodStuff, BadStuff};
    private enum MyEnum2 {GoodStuff, BadStuff;
    public String toString() {
        switch(this) {
            case GoodStuff: return "Some Good Stuff";
            case BadStuff: return "Some Bad Stuff";
        }
        return "ERROR";
    }
    };

}