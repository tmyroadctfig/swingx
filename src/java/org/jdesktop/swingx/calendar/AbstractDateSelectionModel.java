/*
 * $Id$
 *
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
 *
 */
package org.jdesktop.swingx.calendar;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.event.DateSelectionListener;
import org.jdesktop.swingx.event.EventListenerMap;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;

/**
 * Abstract base implementation of DateSelectionModel. Implements
 * notification and Calendar related properties.
 * 
 * @author Jeanette Winzenburg
 */
public abstract class AbstractDateSelectionModel implements DateSelectionModel {
    public static final SortedSet<Date> EMPTY_DATES = Collections.unmodifiableSortedSet(new TreeSet<Date>());
    
    protected EventListenerMap listenerMap;
    protected boolean adjusting;
    protected Calendar cal;
    protected Locale locale;

    public AbstractDateSelectionModel() {
        this(null);
    }
    public AbstractDateSelectionModel(Locale locale) {
        this.listenerMap = new EventListenerMap();
        setLocale(locale);
    }

    /**
     * {@inheritDoc}
     */
    public Calendar getCalendar() {
        return (Calendar) cal.clone();
    }

    /**
     * {@inheritDoc}
     */
    public int getFirstDayOfWeek() {
        return cal.getFirstDayOfWeek();
    }

    /**
     * {@inheritDoc}
     */
    public void setFirstDayOfWeek(final int firstDayOfWeek) {
        if (firstDayOfWeek == getFirstDayOfWeek()) return;
        cal.setFirstDayOfWeek(firstDayOfWeek);
        fireValueChanged(EventType.CALENDAR_CHANGED);
    }

    /**
     * {@inheritDoc}
     */
    public int getMinimalDaysInFirstWeek() {
        return cal.getMinimalDaysInFirstWeek();
    }

    /**
     * {@inheritDoc}
     */
    public void setMinimalDaysInFirstWeek(int minimalDays) {
        if (minimalDays == getMinimalDaysInFirstWeek()) return;
        cal.setMinimalDaysInFirstWeek(minimalDays);
        fireValueChanged(EventType.CALENDAR_CHANGED);
    }

    
    /**
     * {@inheritDoc}
     */
    public TimeZone getTimeZone() {
        return cal.getTimeZone();
    }

    /**
     * {@inheritDoc}
     */
    public void setTimeZone(TimeZone timeZone) {
        if (getTimeZone().equals(timeZone)) return;
        TimeZone oldTimeZone = getTimeZone();
        cal.setTimeZone(timeZone);
        adjustDatesToTimeZone(oldTimeZone);
        fireValueChanged(EventType.CALENDAR_CHANGED);
    }

    /**
     * Adjusts all stored dates to a new time zone.
     * This method is called after the change had been made. <p>
     * 
     * This implementation resets all dates to null, clears everything. 
     * Subclasses may override to really map to the new time zone.
     *
     * @param oldTimeZone the old time zone
     * 
     */
    protected void adjustDatesToTimeZone(TimeZone oldTimeZone) {
        clearSelection();
        setLowerBound(null);
        setUpperBound(null);
        setUnselectableDates(EMPTY_DATES);
    }
    /**
     * {@inheritDoc}
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * {@inheritDoc}
     */
    public void setLocale(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (locale.equals(getLocale())) return;
        this.locale = locale;
        if (cal != null) {
            cal = Calendar.getInstance(cal.getTimeZone(), locale);
        } else {
            cal = Calendar.getInstance(locale);
        }
        fireValueChanged(EventType.CALENDAR_CHANGED);
    }

//------------------- listeners
    
    /**
     * {@inheritDoc}
     */
    public boolean isAdjusting() {
        return adjusting;
    }

    /**
     * {@inheritDoc}
     */
    public void setAdjusting(boolean adjusting) {
        if (adjusting == isAdjusting()) return;
        this.adjusting = adjusting;
       fireValueChanged(adjusting ? EventType.ADJUSTING_STARTED : EventType.ADJUSTING_STOPPED);
        
    }

//----------------- notification    
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
                e = new DateSelectionEvent(this, eventType, isAdjusting());
            }
            listener.valueChanged(e);
        }
    }



}
