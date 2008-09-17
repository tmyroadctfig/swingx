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
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.renderer.CellContext;

/**
 * CellContext internally used by BasisMonthViewUI rendering.
 * 
 * @author Jeanette Winzenburg
 */
class MonthViewCellContext extends CellContext<JXMonthView> {

    private boolean trailing;
    private boolean leading;
    private boolean today;


    public void installInMonthDayContext(JXMonthView component, Object value, boolean selected, 
             boolean today) {
        super.installContext(component, value, -1, -1, selected, false,
                true, true);
        this.leading = false;
        this.trailing = false;
        this.today = today;
    }

    public void installOutMonthDayContext(JXMonthView component, Object value, boolean leading) {
        installInMonthDayContext(component, value, false, false);
        if (leading) {
            this.leading = true;
            this.trailing = false;
        } else {
            this.leading = false;
            this.trailing = true;
        }
    }
    
    
    @Override
    protected Color getForeground() {
        if (leading) {
            return UIManager.getColor(getUIPrefix() + "leadingDayForeground");
        }
        if (trailing) {
            return UIManager.getColor(getUIPrefix() + "trailingDayForeground");
        }
        return super.getForeground();
    }


    @Override
    protected Color getSelectionBackground() {
        if (leading || trailing) return getBackground();
        return getComponent() != null ? getComponent().getSelectedBackground() : null;
    }

    @Override
    protected Color getSelectionForeground() {
        return getForeground();
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
        return today;
    }

    @Override
    protected String getUIPrefix() {
        return "JXMonthView.";
    }

    
}
