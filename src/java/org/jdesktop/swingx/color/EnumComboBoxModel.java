package org.jdesktop.swingx.color;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.jdesktop.swingx.JXGradientChooser;

public class EnumComboBoxModel extends AbstractListModel implements ComboBoxModel {
    
    Object selected = null;
    
    public EnumComboBoxModel() {
	selected = JXGradientChooser.GradientStyle.values()[0];
    }
    
    public int getSize() {
	return JXGradientChooser.GradientStyle.values().length;
    }
    
    public Object getElementAt(int index) {
	return JXGradientChooser.GradientStyle.values()[index];
    }
    
    public void setSelectedItem(Object anItem)	{
	selected = anItem;
    }
    
    public Object getSelectedItem()	{
	return selected;
    }
}
