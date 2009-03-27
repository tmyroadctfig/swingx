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

import javax.swing.Action;

import org.jdesktop.swingx.JXMonthView;


/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class BasicCalendarHeaderHandler extends CalendarHeaderHandler {
    
    
    
    @Override
    public void install(JXMonthView monthView) {
        super.install(monthView);
        getHeaderComponent().setActions(monthView.getActionMap().get("scrollToPreviousMonth"),
                monthView.getActionMap().get("scrollToNextMonth"),
                monthView.getActionMap().get("zoomOut"));

    }

    
    @Override
    public void uninstall(JXMonthView monthView) {
        getHeaderComponent().setActions(null, null, null);
        super.uninstall(monthView);
    }


    @Override
    public BasicCalendarHeader getHeaderComponent() {
        // TODO Auto-generated method stub
        return (BasicCalendarHeader) super.getHeaderComponent();
    }

    @Override
    protected BasicCalendarHeader createCalendarHeader() {
        // TODO Auto-generated method stub
        return new BasicCalendarHeader();
    }

}
