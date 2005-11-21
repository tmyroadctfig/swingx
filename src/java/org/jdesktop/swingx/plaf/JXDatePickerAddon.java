package org.jdesktop.swingx.plaf;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.border.LineBorder;
import java.util.List;
import java.util.Arrays;

/**
 * @author Joshua Outwater
 */
public class JXDatePickerAddon extends AbstractComponentAddon {
    public JXDatePickerAddon() {
        super("JXDatePicker");
    }

    @Override
    protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        defaults.addAll(Arrays.asList(new Object[] {
                defaults.add(JXDatePicker.uiClassID),
                defaults.add("org.jdesktop.swingx.plaf.basic.BasicDatePickerUI"),
                "JXDatePicker.linkFormat",
                "Today is {0,date, dd MMMM yyyy}",
                "JXDatePicker.longFormat",
                "EEE MM/dd/yyyy",
                "JXDatePicker.mediumFormat",
                "MM/dd/yyyy",
                "JXDatePicker.shortFormat",
                "MM/dd",
                "JXDatePicker.border",
                new BorderUIResource(BorderFactory.createCompoundBorder(
                    LineBorder.createGrayLineBorder(),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3))),
                "JXDatePicker.numColumns",
                10
        }));
    }
}
