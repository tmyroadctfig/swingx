/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx.plaf.basic;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.plaf.UIManagerExt;
import org.jdesktop.swingx.renderer.CellContext;

/**
 * CellContext internally used by BasisMonthViewUI rendering.
 * 
 * @author Jeanette Winzenburg
 */
class MonthViewCellContext extends CellContext<JXMonthView> {

    private DayState dayState;

    public void installMonthContext(JXMonthView component, Object value, boolean selected, 
             DayState dayState) {
        super.installContext(component, value, -1, -1, selected, false,
                true, true);
        this.dayState = dayState;
    }

    @Override
    protected Color getForeground() {
        if (DayState.LEADING == dayState) {
            return UIManagerExt.getColor(getUIPrefix() + "leadingDayForeground");
        }
        if (DayState.TRAILING == dayState) {
            return UIManagerExt.getColor(getUIPrefix() + "trailingDayForeground");
        }
        if ((DayState.TITLE == dayState) && (getComponent() != null)) {
            return getComponent().getMonthStringForeground();
        }
        return super.getForeground();
    }

    

    @Override
    protected Color getBackground() {
        if ((DayState.TITLE == dayState) && (getComponent() != null)) {
            return getComponent().getMonthStringBackground();
        }
        return super.getBackground();
    }

    @Override
    protected Color getSelectionBackground() {
        if (DayState.LEADING == dayState || DayState.TRAILING == dayState) return getBackground();
        return getComponent() != null ? getComponent().getSelectionBackground() : null;
    }

    @Override
    protected Color getSelectionForeground() {
        if (DayState.LEADING == dayState || DayState.TRAILING == dayState) return getForeground();
        return getComponent() != null ? getComponent().getSelectionForeground() : null;
    }

    
    
    @Override
    protected Border getBorder() {
        if (getComponent() == null) {
            return super.getBorder();
        }
        if (isToday()) {
            int x = getComponent().getBoxPaddingX();
            int y = getComponent().getBoxPaddingY();
           Border todayBorder = BorderFactory.createLineBorder(getComponent().getTodayBackground());
           Border empty = BorderFactory.createEmptyBorder(y - 1, x - 1, y - 1, x -1);
           return BorderFactory.createCompoundBorder(todayBorder, empty);
        }
        return BorderFactory.createEmptyBorder(getComponent().getBoxPaddingY(), getComponent().getBoxPaddingX(), getComponent().getBoxPaddingY(), getComponent().getBoxPaddingX());
    }

    /**
     * @return
     */
    protected boolean isToday() {
        return DayState.TODAY == dayState;
    }

    @Override
    protected String getUIPrefix() {
        return "JXMonthView.";
    }

    
}
