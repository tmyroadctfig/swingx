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

import org.jdesktop.swingx.event.DateSelectionEvent;
import static org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.event.EventListenerMap;

import java.util.*;

/**
 * @author Joshua Outwater
 */
public class DefaultDateSelectionModel implements DateSelectionModel {
    private EventListenerMap listenerMap;
    private SelectionMode selectionMode;
    private Set<Date> selectedDates;
    private Calendar cal;
    private int firstDayOfWeek;

    public DefaultDateSelectionModel() {
        this.listenerMap = new EventListenerMap();
        this.selectionMode = SelectionMode.SINGLE_SELECTION;
        this.selectedDates = new TreeSet<Date>();
        this.firstDayOfWeek = Calendar.SUNDAY;
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

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(final int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectionInterval(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            return;
        }

        switch (selectionMode) {
            case SINGLE_SELECTION:
                clearSelectionImpl();
                selectedDates.add(startDate);
                break;
            case SINGLE_INTERVAL_SELECTION:
                clearSelectionImpl();
                setSingleIntervalSelection(startDate, endDate);
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
        switch (selectionMode) {
            case SINGLE_SELECTION:
                clearSelectionImpl();
                selectedDates.add(startDate);
                break;
            case SINGLE_INTERVAL_SELECTION:
                clearSelectionImpl();
                setSingleIntervalSelection(startDate, endDate);
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

    private void setSingleIntervalSelection(Date startDate, Date endDate) {
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
        cal.setTime(startDate);
        Date date = cal.getTime();
        while (date.before(endDate) || date.equals(endDate)) {
            selectedDates.add(date);
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }
    }
}
