/**
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 */
package org.jdesktop.swingx;

import java.util.Date;
import java.util.SortedSet;

/**
 * @author Joshua Outwater
 */
public interface DateSelectionModel {
    public static enum SelectionMode {
        /**
         * Mode that disallows selection of days from the calendar.
         */
        NO_SELECTION,
        /**
         * Mode that allows for selection of a single day.
         */
        SINGLE_SELECTION,
        /**
         * Mode that allows for selecting of multiple consecutive days.
         */
        SINGLE_INTERVAL_SELECTION,
        /** Mode that allows for selecting disjoint days. */
        MULTIPLE_INTERVAL_SELECTION,
        /**
         * Mode where selections consisting of more than 7 days will
         * snap to a full week.
         */
        WEEK_INTERVAL_SELECTION
    }

    public SelectionMode getSelectionMode();
    public void setSelectionMode(final SelectionMode mode);

    public void addSelectionInterval(Date startDate, Date endDate);
    public void setSelectionInterval(Date startDate, Date endDate);
    public void removeSelectionInterval(Date startDate, Date endDate);
    public void clearSelection();
    public SortedSet<Date> getSelection();
    public boolean isSelected(final Date date);
    public boolean isSelectionEmpty();
    public void addDateSelectionListener(DateSelectionListener listener);
    public void removeDateSelectionListener(DateSelectionListener listener);
}
