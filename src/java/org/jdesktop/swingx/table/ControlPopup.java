/*
 * Created on 22.06.2006
 *
 */
package org.jdesktop.swingx.table;

import java.awt.ComponentOrientation;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;

import org.jdesktop.swingx.table.ColumnControlButton.ColumnVisibilityAction;

/**
 * encapsulates the popup component which is the delegate for
 * all popup visuals, use by a ColumnControlButton.
 * 
 * For now, that's a simple extraction of what a ColumnControl needs. 
 * Usage will drive further evolution.
 * 
 * @see {@link DefaultControlPopup }
 * 
 * 
 */
public interface ControlPopup {

    /**
     * Updates all internal visuals after changing a UI-delegate. <p>
     * 
     * The method called by ColumnControlButton in it's updateUI.
     * As there is a good probability that at the time of a
     * ColumnControlButton is updated after a ui-delegate change the
     * popup is not visible/part of the container hierarchy, this
     * method must be messaged manually. 
     * 
     * @see javax.swing.JComponent#updateUI()
     *
     */
    void updateUI();

    /**
     * Toggles the popup's visibility. This method is responsible for
     * placing itself relative to the given owner if toggled to visible.
     * 
     * @param owner the JComponent which triggered the visibility change, typically
     *   a ColumnControlButton.
     */
    void toggleVisibility(JComponent owner);

    /**
     * @see javax.swing.JComponent#applyComponentOrientation(ComponentOrientation).
     * @param o the <code>ComponentOrientation to apply to all internal widgets.
     */
    void applyComponentOrientation(ComponentOrientation o);

    /**
     * Removes all items from the popup. 
     */
    void removeAll();

    /**
     * Adds items corresponding to the column's visibility actions. <p>
     * 
     * TODO JW: get the generics correct!
     * 
     * @param actions List of ColumnVisibilityActions to add.
     */
    void addVisibilityActionItems(List<ColumnVisibilityAction> actions);

    /**
     * add additional actions to the popup. Does nothing if 
     * actions is empty or !canControl().
     * 
     * @param actions List of actions to add to the popup.
     */
    void addAdditionalActionItems(List<Action> actions);

}