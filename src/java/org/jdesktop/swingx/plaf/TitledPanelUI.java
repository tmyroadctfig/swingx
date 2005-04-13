/*
 * TitledPanelUI.java
 *
 * Created on April 12, 2005, 11:25 AM
 */

package org.jdesktop.swingx.plaf;

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
}
