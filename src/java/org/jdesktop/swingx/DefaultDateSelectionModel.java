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

import org.jdesktop.swingx.calendar.JXMonthView;
import org.jdesktop.swingx.event.DateSelectionEvent;
import static org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.event.EventListenerMap;

import java.util.*;

/**
 * This implementation of the <code>DateSelectionModel</code> stores all dates without hours,
 * minutes, seconds and milliseconds.
 *
 * @author Joshua Outwater
 */
public class DefaultDateSelectionModel implements DateSelectionModel {
    private JXMonthView monthView;
    private EventListenerMap listenerMap;
    private SelectionMode selectionMode;
    private Set<Date> selectedDates;
    private Calendar cal;

    public DefaultDateSelectionModel(JXMonthView monthView) {
        this.listenerMap = new EventListenerMap();
        this.monthView = monthView;
        selectionMode = SelectionMode.SINGLE_SELECTION;
        selectedDates = new TreeSet<Date>();
        cal = Calendar.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectionMode(final SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
        clearSelection();
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectionInterval(Date startDate, Date endDate) {
        cleanupDate(startDate);
        cleanupDate(endDate);
        if (startDate.after(endDate)) {
            return;
        }

        switch (selectionMode) {
            case NO_SELECTION:
                return;
            case SINGLE_SELECTION:
                clearSelectionImpl();
                selectedDates.add(startDate);
                break;
            case SINGLE_INTERVAL_SELECTION:
                clearSelectionImpl();
                setSingleIntervalSelection(startDate, endDate);
                break;
            case WEEK_INTERVAL_SELECTION:
                clearSelection();
                setWeekIntervalSelection(startDate, endDate);
                break;
            case MULTIPLE_INTERVAL_SELECTION:
                addSelectionImpl(startDate, endDate);
                break;
            default:
                break;
        }
        fireValueChanged(EventType.DATES_ADDED);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectionInterval(final Date startDate, final Date endDate) {
        cleanupDate(startDate);
        cleanupDate(endDate);
        switch (selectionMode) {
            case NO_SELECTION:
                return;
            case SINGLE_SELECTION:
                clearSelectionImpl();
                selectedDates.add(startDate);
                break;
            case SINGLE_INTERVAL_SELECTION:
                clearSelectionImpl();
                setSingleIntervalSelection(startDate, endDate);
                break;
            case WEEK_INTERVAL_SELECTION:
                clearSelectionImpl();
                setWeekIntervalSelection(startDate, endDate);
                break;
            case MULTIPLE_INTERVAL_SELECTION:
                clearSelectionImpl();
                addSelectionImpl(startDate, endDate);
                break;
            default:
                break;
        }
        fireValueChanged(EventType.DATES_SET);
    }

    /**
     * TODO: This is really only useful for multiple selection.  Maybe restrict to that mode???
     */
     /**
     * {@inheritDoc}
     */
    public void removeSelectionInterval(final Date startDate, final Date endDate) {
        cleanupDate(startDate);
        cleanupDate(endDate);
        if (startDate.after(endDate)) {
            return;
        }

        cal.setTime(startDate);
        Date date = cal.getTime();
        while (date.before(endDate) || date.equals(endDate)) {
            selectedDates.remove(date);
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }
        fireValueChanged(EventType.DATES_REMOVED);
    }

    /**
     * {@inheritDoc}
     */
    public void clearSelection() {
        clearSelectionImpl();
        fireValueChanged(EventType.SELECTION_CLEARED);
    }

    private void clearSelectionImpl() {
        selectedDates.clear();
    }

    /**
     * {@inheritDoc}
     */
    public SortedSet<Date> getSelection() {
        return new TreeSet<Date>(selectedDates);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSelected(final Date date) {
        return selectedDates.contains(date);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSelectionEmpty() {
        return selectedDates.size() == 0;
    }

    /**
     * {@inheritDoc}
     */
    public void addDateSelectionListener(DateSelectionListener l) {
        listenerMap.add(DateSelectionListener.class, l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeDateSelectionListener(DateSelectionListener l) {
        listenerMap.remove(DateSelectionListener.class, l);
    }

    public List<DateSelectionListener> getDateSelectionListeners() {
        return listenerMap.getListeners(DateSelectionListener.class);
    }

    protected void fireValueChanged(DateSelectionEvent.EventType eventType) {
        List<DateSelectionListener> listeners = getDateSelectionListeners();
        DateSelectionEvent e = null;

        for (DateSelectionListener listener : listeners) {
            if (e == null) {
                e = new DateSelectionEvent(this, eventType);
            }
            listener.valueChanged(e);
        }
    }

    private void setWeekIntervalSelection(Date startDate, Date endDate) {
        Date newStart = startDate;
        Date newEnd = endDate;

        // Make sure if we are over 7 days we span full weeks.
        cal.setTime(startDate);
        int firstDayOfWeek = monthView.getFirstDayOfWeek();
        cal.setFirstDayOfWeek(firstDayOfWeek);
        int count = 1;
        while (cal.getTime().before(endDate)) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            count++;
        }
        if (count > JXMonthView.DAYS_IN_WEEK) {
            // Make sure start date is on the beginning of the
            // week.
            cal.setTime(startDate);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != firstDayOfWeek) {
                // Move the start date back to the first day of the
                // week.
                int daysFromStart = dayOfWeek - firstDayOfWeek;
                if (daysFromStart < 0) {
                    daysFromStart += JXMonthView.DAYS_IN_WEEK;
                }
                cal.add(Calendar.DAY_OF_MONTH, -daysFromStart);
                count += daysFromStart;
                newStart = cal.getTime();
            }

            // Make sure we have full weeks.  Otherwise modify the
            // end date.
            int remainder = count % JXMonthView.DAYS_IN_WEEK;
            if (remainder != 0) {
                cal.setTime(endDate);
                cal.add(Calendar.DAY_OF_MONTH, (JXMonthView.DAYS_IN_WEEK - remainder));
                newEnd = cal.getTime();
            }
        }
        addSelectionImpl(newStart, newEnd);
    }

    private void setSingleIntervalSelection(Date startDate, Date endDate) {
        cleanupDate(startDate);
        cleanupDate(endDate);
        clearSelectionImpl();
        cal.setTime(startDate);
        Date date = cal.getTime();
        while (date.before(endDate) || date.equals(endDate)) {
            selectedDates.add(date);
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }
    }

    private void addSelectionImpl(final Date startDate, final Date endDate) {
        cleanupDate(startDate);
        cleanupDate(endDate);
        cal.setTime(startDate);
        Date date = cal.getTime();
        while (date.before(endDate) || date.equals(endDate)) {
            selectedDates.add(date);
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }
    }

    // For ease of comparison dates stored in this model must be stored without
    // hours, minutes, seconds and milliseconds
    private void cleanupDate(Date date) {
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date.setTime(cal.getTimeInMillis());
    }
}
