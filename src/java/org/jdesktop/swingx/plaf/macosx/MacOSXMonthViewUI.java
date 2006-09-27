package org.jdesktop.swingx.plaf.macosx;

import org.jdesktop.swingx.plaf.basic.BasicMonthViewUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

/**
 * Created by IntelliJ IDEA.
 * User: joutwate
 * Date: Sep 26, 2006
 * Time: 6:31:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MacOSXMonthViewUI extends BasicMonthViewUI {

    @SuppressWarnings({"UNUSED_SYMBOL"})
    public static ComponentUI createUI(JComponent c) {
        return new MacOSXMonthViewUI();
    }
}
