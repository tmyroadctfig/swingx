package org.jdesktop.swingx.plaf;

import org.jdesktop.swingx.calendar.JXMonthView;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class JXMonthViewAddon extends AbstractComponentAddon {
    public JXMonthViewAddon() {
        super("JXMonthView");
    }

    protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        defaults.addAll(Arrays.asList(new Object[] {
                defaults.add(JXMonthView.uiClassID),
                defaults.add("org.jdesktop.swingx.plaf.basic.BasicMonthViewUI"),
                "JXMonthView.monthStringBackground", new Color(138, 173, 209),
                "JXMonthView.monthStringForeground", new Color(68, 68, 68),
                "JXMonthView.daysOfTheWeekForeground", new Color(68, 68, 68),
                "JXMonthView.selectedBackground", new Color(197, 220, 240),
                "JXMonthView.font", UIManager.getFont("Button.font"),
                "JXMonthView.monthDownFileName", "resources/month-down.png",
                "JXMonthView.monthUpFileName", "resources/month-up.png"
        }));
    }
}
