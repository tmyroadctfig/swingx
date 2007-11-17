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
package org.jdesktop.swingx.calendar;

import java.util.Date;
import java.util.SortedSet;

import org.jdesktop.swingx.event.DateSelectionListener;


/**
 * @author Joshua Outwater
 */
public interface DateSelectionModel {
    public static enum SelectionMode {
        /**
         * Mode that allows for selection of a single day.
         */
        SINGLE_SELECTION,
        /**
         * Mode that allows for selecting of multiple consecutive days.
         */
        SINGLE_INTERVAL_SELECTION,
        /**
         * Mode that allows for selecting disjoint days.
         */
        MULTIPLE_INTERVAL_SELECTION
    }

    /**
     * Get the selection mode
     *
     * @return return the current selection mode
     */
    public SelectionMode getSelectionMode();

    /**
     * Set the selection mode
     *
     * @param mode new selection mode
     */
    public void setSelectionMode(final SelectionMode mode);

    /**
     * Gets what the first day of the week is; e.g.,
     * <code>Calendar.SUNDAY</code> in the U.S., <code>Calendar.MONDAY</code>
     * in France.
     *
     * This is needed when the model selection mode is <code>WEEK_INTERVAL_SELECTION</code>.
     * The default value is <code>Calendar.SUNDAY</code>
     *
     * @return int The first day of the week.
     */
    public int getFirstDayOfWeek();

    /**
     * Sets what the first day of the week is; e.g.,
     * <code>Calendar.SUNDAY</code> in US, <code>Calendar.MONDAY</code>
     * in France.
     *
     * @param firstDayOfWeek The first day of the week.
     * @see java.util.Calendar
     */
    public void setFirstDayOfWeek(final int firstDayOfWeek);

    /**
     * Add the specified selection interval to the selection model
     *
     * @param startDate interval start date
     * @param endDate   interval end date
     */
    public void addSelectionInterval(Date startDate, Date endDate);

    /**
     * Set the specified selection interval to the selection model
     *
     * @param startDate interval start date
     * @param endDate   interval end date
     */
    public void setSelectionInterval(Date startDate, Date endDate);

    /**
     * Remove the specifed selection interval from the selection model
     *
     * @param startDate interval start date
     * @param endDate   interval end date
     */
    public void removeSelectionInterval(Date startDate, Date endDate);

    /**
     * Clear any selection from the selection model. Fires an Event of 
     * type SELECTION_CLEARED if there had been a selection, does nothing
     * otherwise.
     */
    public void clearSelection();

    /**
     * Get the current selection
     *
     * @return sorted set of selected dates
     */
    public SortedSet<Date> getSelection();

    /**
     * Return true if the date specified is selected, false otherwise
     *
     * @param date date to check for selection
     * @return true if the date is selected, false otherwise
     */
    public boolean isSelected(final Date date);

    /**
     * Return true if the selection is empty, false otherwise
     *
     * @return true if the selection is empty, false otherwise
     */
    public boolean isSelectionEmpty();

    /**
     * Add the specified listener to this model
     *
     * @param listener listener to add to this model
     */
    public void addDateSelectionListener(DateSelectionListener listener);

    /**
     * Remove the specified listener to this model
     *
     * @param listener listener to remove from this model
     */
    public void removeDateSelectionListener(DateSelectionListener listener);

    /**
     * Returns a <code>SortedSet</code> of <code>Date</codes>s that are unselectable
     *
     * @return sorted set of dates
     */
    public SortedSet<Date> getUnselectableDates();

    /**
     * Set which dates are unable to be selected
     *
     * @param unselectableDates dates that are unselectable, must not be null
     */
    public void setUnselectableDates(SortedSet<Date> unselectableDates);

    /**
     * Return true is the specified date is unselectable
     *
     * @param unselectableDate the date to check for unselectability
     * @return true is the date is unselectable, false otherwise
     */
    public boolean isUnselectableDate(Date unselectableDate);

    /**
     * Return the upper bound date that is allowed to be selected for this
     * model
     *
     * @return upper bound date or null if not set
     */
    public Date getUpperBound();

    /**
     * Set the upper bound date that is allowed to be selected for this model
     *
     * @param upperBound upper bound
     */
    public void setUpperBound(final Date upperBound);

    /**
     * Return the lower bound date that is allowed to be selected for this
     * model
     *
     * @return lower bound date or null if not set
     */
    public Date getLowerBound();

    /**
     * Set the lower bound date that is allowed to be selected for this model
     *
     * @param lowerBound lower bound date or null if not set
     */
    public void setLowerBound(final Date lowerBound);

    /**
     * Set the property to mark upcoming selections as intermediate/
     * final. This will fire a event of type adjusting_start/stop.
     * 
     * The default value is false.
     * 
     * Note: Client code marking as intermediate must take care of
     * finalizing again.
     * 
     * @param adjusting a flag to turn the adjusting property on/off.
     */
    public void setAdjusting(boolean adjusting);

    /**
     * Returns the property to decide whether the selection is 
     * intermediate or final.
     * 
     * @return the adjusting property.
     */
    public boolean isAdjusting();
    
    
}
