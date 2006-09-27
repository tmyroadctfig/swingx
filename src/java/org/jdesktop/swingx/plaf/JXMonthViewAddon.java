package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import org.jdesktop.swingx.calendar.JXMonthView;

public class JXMonthViewAddon extends AbstractComponentAddon {
    public JXMonthViewAddon() {
        super("JXMonthView");
    }

    @Override
    protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        super.addBasicDefaults(addon, defaults);
        defaults.addAll(Arrays.asList(new Object[] {
                defaults.add(JXMonthView.uiClassID),
                defaults.add("org.jdesktop.swingx.plaf.basic.BasicMonthViewUI"),
                "JXMonthView.monthStringBackground", new ColorUIResource(138, 173, 209),
                "JXMonthView.monthStringForeground", new ColorUIResource(68, 68, 68),
                "JXMonthView.daysOfTheWeekForeground", new ColorUIResource(68, 68, 68),
                "JXMonthView.weekOfTheYearForeground", new ColorUIResource(68, 68, 68),
                "JXMonthView.unselectableDayForeground", new ColorUIResource(Color.RED),
                "JXMonthView.selectedBackground", new ColorUIResource(197, 220, 240),
                "JXMonthView.flaggedDayForeground", new ColorUIResource(Color.RED),
                "JXMonthView.leadingDayForeground", new ColorUIResource(Color.LIGHT_GRAY),
                "JXMonthView.trailingDayForeground", new ColorUIResource(Color.LIGHT_GRAY),
                "JXMonthView.font", UIManager.getFont("Button.font"),
                "JXMonthView.monthDownFileName", "resources/month-down.png",
                "JXMonthView.monthUpFileName", "resources/month-up.png",
                "JXMonthView.boxPaddingX", 3,
                "JXMonthView.boxPaddingY", 3
        }));
    }
}
