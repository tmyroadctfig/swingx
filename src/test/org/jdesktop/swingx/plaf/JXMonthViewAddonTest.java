/**
 * 
 */
package org.jdesktop.swingx.plaf;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

/**
 * @author Karl George Schaefer
 *
 */
public class JXMonthViewAddonTest extends AbstractComponentAddonTest {
    public void testUIDefaults() {
        LookAndFeelAddons.contribute(new MonthViewAddon());
        
        assertTrue(UIManager.get("JXMonthView.background") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.monthStringBackground") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.monthStringForeground") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.daysOfTheWeekForeground") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.weekOfTheYearForeground") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.unselectableDayForeground") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.selectedBackground") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.flaggedDayForeground") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.leadingDayForeground") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.trailingDayForeground") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.font") instanceof UIResource);
        assertTrue(UIManager.get("JXMonthView.monthDownFileName") instanceof Icon);
        assertTrue(UIManager.get("JXMonthView.monthUpFileName") instanceof Icon);
        assertTrue(UIManager.get("JXMonthView.boxPaddingX") instanceof Integer);
        assertTrue(UIManager.get("JXMonthView.boxPaddingY") instanceof Integer);
    }
}
