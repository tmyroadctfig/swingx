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

    /**
     * Get the selection mode
     * @return return the current selection mode
     */
    public SelectionMode getSelectionMode();

    /**
     * Set the selection mode
     * @param mode new selection mode
     */
    public void setSelectionMode(final SelectionMode mode);

    public int getFirstDayOfWeek();

    public void setFirstDayOfWeek(final int firstDayOfWeek);

    /**
     * Add the specified selection interval to the selection model
     * @param startDate interval start date
     * @param endDate interval end date
     */
    public void addSelectionInterval(Date startDate, Date endDate);

    /**
     * Set the specified selection interval to the selection model
     * @param startDate interval start date
     * @param endDate interval end date
     */
    public void setSelectionInterval(Date startDate, Date endDate);

    /**
     * Remove the specifed selection interval from the selection model
     * @param startDate interval start date
     * @param endDate interval end date
     */
    public void removeSelectionInterval(Date startDate, Date endDate);

    /**
     * Clear any selection from the selection model
     */
    public void clearSelection();

    /**
     * Get the current selection
     * @return sorted set of selected dates
     */
    public SortedSet<Date> getSelection();

    /**
     * Return true if the date specified is selected, false otherwise
     * @param date date to check for selection
     * @return true if the date is selected, false otherwise
     */
    public boolean isSelected(final Date date);

    /**
     * Return true if the selection is empty, false otherwise
     * @return true if the selection is empty, false otherwise
     */
    public boolean isSelectionEmpty();

    /**
     * Add the specified listener to this model
     * @param listener listener to add to this model
     */
    public void addDateSelectionListener(DateSelectionListener listener);

    /**
     * Remove the specified listener to this model
     * @param listener listener to remove from this model
     */
    public void removeDateSelectionListener(DateSelectionListener listener);
}
