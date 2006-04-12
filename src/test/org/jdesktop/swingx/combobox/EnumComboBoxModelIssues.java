/*
 * Created on 12.04.2006
 *
 */
package org.jdesktop.swingx.combobox;

import javax.swing.JComboBox;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;


public class EnumComboBoxModelIssues extends InteractiveTestCase {
    enum MyEnum1 {GoodStuff, BadStuff};
    public static void main(String[] args) throws Exception {
      EnumComboBoxModelIssues test = new EnumComboBoxModelIssues();
      try {
          test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Table.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
  }

    /**
     * Issue #303-swingx: EnumComboBoxModel getSelectedItem throws ClassCastException.
     * 
     * illegal implementation - doesn't comply to ComboBoxModel contract.
     *
     */
    public void testSelectedItem() {
        EnumComboBoxModel enumModel = new EnumComboBoxModel(MyEnum1.class);
        enumModel.setSelectedItem("something else");
        enumModel.getSelectedItem();
    }
 
//------------------ visuals
    
    /**
     * Issue #303-swingx: EnumComboBoxModel getSelectedItem throws ClassCastException.
     * 
     * a visual example as to how easily this might happen.
     * 
     */
    public void interactiveSelectedItem() {
        EnumComboBoxModel enumModel = new EnumComboBoxModel(MyEnum1.class);
        JComboBox box = new JComboBox(enumModel);
        box.setEditable(true);
        JXFrame frame = wrapInFrame(box, "enum combo throwing...");
        frame.setVisible(true);
    }
    
}
