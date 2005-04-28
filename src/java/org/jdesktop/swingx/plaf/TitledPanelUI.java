/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.plaf;

import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

/**
 *
 * @author rbair
 */
public abstract class TitledPanelUI extends PanelUI {
	/**
	 * Adds the given JComponent as a decoration on the right of the title
	 * @param decoration
	 */
	public abstract void addRightDecoration(JComponent decoration);

	/**
	 * Adds the given JComponent as a decoration on the left of the title
	 * @param decoration
	 */
	public abstract void addLeftDecoration(JComponent decoration);
    /**
     * @return the Container acting as the title bar for this component
     */
    public abstract Container getTitleBar();
}
