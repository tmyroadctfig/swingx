/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;

/**
 * 
 * @author Jeanette Winzenburg
 */
public class ContextMenuAuxLF extends LookAndFeel {

    private UIDefaults myDefaults;

    public String getName() {
        return "ContextMenuAuxLF";
    }

    public String getID() {
        return getName();
    }

    public String getDescription() {

        return "nothing special - just adding some fun";
    }

    public boolean isNativeLookAndFeel() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public UIDefaults getDefaults() {
        if (myDefaults == null) {
            initDefaults();
        }
        return myDefaults;
    }

    private void initDefaults() {
        myDefaults = new MyUIDefaults();
        Object[] mydefaults = { "TextFieldUI",
                "org.jdesktop.swingx.plaf.ContextMenuAuxTextUI",
                "EditorPaneUI",
                "org.jdesktop.swingx.plaf.ContextMenuAuxTextUI",
                "PasswordFieldUI",
                "org.jdesktop.swingx.plaf.ContextMenuAuxTextUI", "TextAreaUI",
                "org.jdesktop.swingx.plaf.ContextMenuAuxTextUI", "TextPaneUI",
                "org.jdesktop.swingx.plaf.ContextMenuAuxTextUI", "ScrollBarUI",
                "org.jdesktop.swingx.plaf.ContextMenuAuxScrollBarUI", };
        myDefaults.putDefaults(mydefaults);
    }

    /**
     * UIDefaults without error msg.
     * 
     */
    private static class MyUIDefaults extends UIDefaults {

        protected void getUIError(String msg) {
            // TODO Auto-generated method stub

        }
    }
}
