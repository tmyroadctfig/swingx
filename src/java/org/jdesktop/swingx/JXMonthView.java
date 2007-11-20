/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.Timer;

import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DateSelectionModel;
import org.jdesktop.swingx.calendar.DateSpan;
import org.jdesktop.swingx.calendar.DefaultDateSelectionModel;
import org.jdesktop.swingx.event.EventListenerMap;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.MonthViewAddon;
import org.jdesktop.swingx.plaf.MonthViewUI;
import org.jdesktop.swingx.util.Contract;


/**
 * Component that displays a month calendar which can be used to select a day
 * or range of days.  By default the <code>JXMonthView</code> will display a
 * single calendar using the current month and year, using
 * <code>Calendar.SUNDAY</code> as the first day of the week.
 * <p>
 * The <code>JXMonthView</code> can be configured to display more than one
 * calendar at a time by calling
 * <code>setPreferredCalCols</code>/<code>setPreferredCalRows</code>.  These
 * methods will set the preferred number of calendars to use in each
 * column/row.  As these values change, the <code>Dimension</code> returned
 * from <code>getMinimumSize</code> and <code>getPreferredSize</code> will
 * be updated.  The following example shows how to create a 2x2 view which is
 * contained within a <code>JFrame</code>:
 * <pre>
 *     JXMonthView monthView = new JXMonthView();
 *     monthView.setPreferredCols(2);
 *     monthView.setPreferredRows(2);
 *
 *     JFrame frame = new JFrame();
 *     frame.getContentPane().add(monthView);
 *     frame.pack();
 *     frame.setVisible(true);
 * </pre>
 * <p>
 * <code>JXMonthView</code> can be further configured to allow any day of the
 * week to be considered the first day of the week.  Character
 * representation of those days may also be set by providing an array of
 * strings.
 * <pre>
 *    monthView.setFirstDayOfWeek(Calendar.MONDAY);
 *    monthView.setDaysOfTheWeek(
 *            new String[]{"S", "M", "T", "W", "Th", "F", "S"});
 * </pre>
 * <p>
 * This component supports flagging days.  These flagged days are displayed
 * in a bold font.  This can be used to inform the user of such things as
 * scheduled appointment.
 * <pre>
 *    // Create some dates that we want to flag as being important.
 *    Calendar cal1 = Calendar.getInstance();
 *    cal1.set(2004, 1, 1);
 *    Calendar cal2 = Calendar.getInstance();
 *    cal2.set(2004, 1, 5);
 *
 *    long[] flaggedDates = new long[] {
 *        cal1.getTimeInMillis(),
 *        cal2.getTimeInMillis(),
 *        System.currentTimeMillis()
 *    };
 *
 *    monthView.setFlaggedDates(flaggedDates);
 * </pre>
 * Applications may have the need to allow users to select different ranges of
 * dates.  There are four modes of selection that are supported, single,
 * multiple, week and no selection.  Once a selection is made an action is
 * fired, with exception of the no selection mode, to inform listeners that
 * selection has changed.
 * <pre>
 *    // Change the selection mode to select full weeks.
 *    monthView.setSelectionMode(JXMonthView.WEEK_INTERVAL_SELECTION);
 *
 *    // Add an action listener that will be notified when the user
 *    // changes selection via the mouse.
 *    monthView.getSelectionModel().addDateSelectionListener(new DateSelectionListener {
 *        public void valueChanged(DateSelectionEvent e) {
 *            System.out.println(e.getSelection());
 *        }
 *    });
 * </pre>
 * 
 *  
 * @author Joshua Outwater
 * @version  $Revision$
 */
public class JXMonthView extends JComponent {
    
    /*
     * moved from package calendar to swingx at version 1.51
     */
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
        /**
         * Mode that allows for selecting disjoint days.
         */
        MULTIPLE_INTERVAL_SELECTION,
        /**
         * Mode where selections consisting of more than 7 days will
         * snap to a full week.
         */
        WEEK_INTERVAL_SELECTION
    }

    /** action command used for commit actionEvent. */
    public static final String COMMIT_KEY = "monthViewCommit";
    /** action command used for cancel actionEvent. */
    public static final String CANCEL_KEY = "monthViewCancel";

    public static final String BOX_PADDING_X = "boxPaddingX";
    public static final String BOX_PADDING_Y = "boxPaddingY";
    public static final String DAYS_OF_THE_WEEK = "daysOfTheWeek";
    public static final String ENSURE_DATE_VISIBILITY = "ensureDateVisibility";
    public static final String FIRST_DISPLAYED_DATE = "firstDisplayedDate";
    public static final String FIRST_DISPLAYED_MONTH = "firstDisplayedMonth";
    public static final String FIRST_DISPLAYED_YEAR = "firstDisplayedYear";
    public static final String SELECTION_MODEL = "selectionModel";
    public static final String SHOW_LEADING_DATES = "showLeadingDates";
    public static final String SHOW_TRAILING_DATES = "showTrailingDates";
    public static final String TRAVERSABLE = "traversable";
    public static final String WEEK_NUMBER = "weekNumber";
    public static final String FLAGGED_DATES = "flaggedDates";

    /** Return value used to identify when the month down button is pressed. */
    public static final int MONTH_DOWN = 1;
    /** Return value used to identify when the month up button is pressed. */
    public static final int MONTH_UP = 2;

    @SuppressWarnings({"unused"})
    private static final int MONTH_TRAVERSABLE = 1;
    @SuppressWarnings({"unused"})
    private static final int YEAR_TRAVERSABLE = 2;

    static {
        LookAndFeelAddons.contribute(new MonthViewAddon());
    }

    /**
     * UI Class ID
     */
    public static final String uiClassID = "MonthViewUI";

    public static final int DAYS_IN_WEEK = 7;
    public static final int MONTHS_IN_YEAR = 12;

    /**
     * Insets used in determining the rectangle for the month string
     * background.
     */
    protected Insets _monthStringInsets = new Insets(0, 0, 0, 0);

    /**
     * Keeps track of the first date we are displaying.  We use this as a
     * restore point for the calendar.
     */
    private long firstDisplayedDate;
    private int firstDisplayedMonth;
    private int firstDisplayedYear;
//    private long lastDisplayedDate;

    private int boxPaddingX;
    private int boxPaddingY;
    private int minCalCols = 1;
    private int minCalRows = 1;
    private long today;
    private TreeSet<Long> flaggedDates;
    private int firstDayOfWeek;
    private boolean antiAlias;
    private boolean traversable;
    private boolean leadingDates;
    private boolean trailingDates;
    private Calendar cal;
    private String[] _daysOfTheWeek;
    private Color todayBackgroundColor;
    private Color monthStringBackground;
    private Color monthStringForeground;
    private Color daysOfTheWeekForeground;
    private Color selectedBackground;
    private String actionCommand = "selectionChanged";
    private Timer todayTimer = null;
    private Hashtable<Integer, Color> dayToColorTable = new Hashtable<Integer, Color>();
    private Color flaggedDayForeground;
    private boolean showWeekNumber;
    private DateSelectionModel model;
    private EventListenerMap listenerMap;
    private SelectionMode selectionMode;
    @SuppressWarnings({"FieldCanBeLocal"})
    private Date modifyedStartDate;
    @SuppressWarnings({"FieldCanBeLocal"})
    private Date modifyedEndDate;
    private boolean componentInputMapEnabled;
    private Calendar anchor;

    /**
     * Create a new instance of the <code>JXMonthView</code> class using the
     * month and year of the current day as the first date to display.
     */
    public JXMonthView() {
        this(System.currentTimeMillis(), null, null);
    }

    /**
     * Cretate a new instance of the <code>JXMonthView</code> class 
     * and set locale used to display month and day text.
     * 
     * @param locale desired locale
     */
    public JXMonthView(final Locale locale) {
        this(System.currentTimeMillis(), null, locale);
    }
    
    /**
     * Create a new instance of the <code>JXMonthView</code> class using the
     * month and year from <code>initialTime</code> as the first date to
     * display.
     *
     * @param firstDisplayedDate The first month to display.
     */
    public JXMonthView(long firstDisplayedDate) {
        this(firstDisplayedDate, null, null);
    }

    public JXMonthView(long firstDisplayedDate, final DateSelectionModel model) {
        this(firstDisplayedDate, model, null);
    }

    public JXMonthView(long firstDisplayedDate, final DateSelectionModel model, final Locale locale) {
        super();
        antiAlias = false;
        traversable = false;
        listenerMap = new EventListenerMap();
        selectionMode = SelectionMode.SINGLE_SELECTION;

        this.model = model;
        if (this.model == null) {
            this.model = new DefaultDateSelectionModel();
        }

        updateUI();

        // Set up calendar instance
        cal = Calendar.getInstance(getLocale());
        firstDayOfWeek = cal.getFirstDayOfWeek();
        cal.setFirstDayOfWeek(firstDayOfWeek);
        cal.setMinimalDaysInFirstWeek(1);

        // Keep track of today
        updateTodayFromCurrentTime();

        setFocusable(true);
        todayBackgroundColor = getForeground();


        setLocale(locale);
        anchor = (Calendar) cal.clone();
        setFirstDisplayedDate(firstDisplayedDate);
    }

    /**
     * @inheritDoc
     */
    public MonthViewUI getUI() {
        return (MonthViewUI)ui;
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui UI to use for this {@code JXMonthView}
     */
    public void setUI(MonthViewUI ui) {
        super.setUI(ui);
    }

    /**
     * Resets the UI property with the value from the current look and feel.
     *
     * @see UIManager#getUI
     */
    @Override
    public void updateUI() {
        setUI((MonthViewUI)LookAndFeelAddons.getUI(this, MonthViewUI.class));
        invalidate();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Returns the first displayed date.
     *
     * @return long The first displayed date.
     */
    public long getFirstDisplayedDate() {
        return firstDisplayedDate;
    }

    
    /**
     * Set the first displayed date.  We only use the month and year of
     * this date.  The <code>Calendar.DAY_OF_MONTH</code> field is reset to
     * 1 and all other fields, with exception of the year and month,
     * are reset to 0.
     *
     * @param date The first displayed date.
     */
    public void setFirstDisplayedDate(long date) {
        anchor.setTimeInMillis(date);
        
        long oldFirstDisplayedDate = firstDisplayedDate;
        int oldFirstDisplayedMonth = firstDisplayedMonth;
        int oldFirstDisplayedYear = firstDisplayedYear;

        cal.setTimeInMillis(anchor.getTimeInMillis());
        CalendarUtils.startOfMonth(cal);
        firstDisplayedDate = cal.getTimeInMillis();
        firstDisplayedMonth = cal.get(Calendar.MONTH);
        firstDisplayedYear = cal.get(Calendar.YEAR);

        firePropertyChange(FIRST_DISPLAYED_DATE, oldFirstDisplayedDate, firstDisplayedDate);
        firePropertyChange(FIRST_DISPLAYED_MONTH, oldFirstDisplayedMonth, firstDisplayedMonth);
        firePropertyChange(FIRST_DISPLAYED_YEAR, oldFirstDisplayedYear, firstDisplayedYear);

        // it's up to the ui to calculate
//        calculateLastDisplayedDate();

        repaint();
    }

    /**
     * Returns the anchor date. Currently, this is the "uncleaned" input date 
     * of setFirstDisplayedDate. This is a quick hack for Issue #618-swingx, to
     * have some invariant for testing. Do not use in client code, may change
     * without notice!
     * 
     * @return the "uncleaned" first display date.
     */
    protected Date getAnchorDate() {
        return anchor.getTime();
    }
    
    /**
     * Returns the last date able to be displayed.  For example, if the last
     * visible month was April the time returned would be April 30, 23:59:59.
     *
     * @return long The last displayed date.
     */
    public long getLastDisplayedDate() {
        return getUI().getLastDisplayedDate();
    }

//    private long calculateLastDisplayedDate() {
//        return getUI().calculateLastDisplayedDate();
//    }

    /**
     * Moves the <code>date</code> into the visible region of the calendar. If
     * the date is greater than the last visible date it will become the last
     * visible date. While if it is less than the first visible date it will
     * become the first visible date.
     * 
     * @param date Date to make visible.
     */
    public void ensureDateVisible(long date) {
        if (date < firstDisplayedDate) {
            setFirstDisplayedDate(date);
        } else {
            long lastDisplayedDate = getLastDisplayedDate();
            if (date > lastDisplayedDate) {

                cal.setTimeInMillis(date);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                cal.setTimeInMillis(lastDisplayedDate);
                int lastMonth = cal.get(Calendar.MONTH);
                int lastYear = cal.get(Calendar.YEAR);

                int diffMonths = month - lastMonth
                        + ((year - lastYear) * MONTHS_IN_YEAR);

                cal.setTimeInMillis(firstDisplayedDate);
                cal.add(Calendar.MONTH, diffMonths);
                setFirstDisplayedDate(cal.getTimeInMillis());
            }
        }

        firePropertyChange(ENSURE_DATE_VISIBILITY, null, date);
    }

    /**
     * Returns a date span of the selected dates.  The result will be null if
     * no dates are selected.
     *
     * @deprecated see #getSelection
     * @return Date span of the selected dates.
     */
    @Deprecated
    public DateSpan getSelectedDateSpan() {
        DateSpan result = null;
        Iterator<Date> itr = getSelection().iterator();
        if (itr.hasNext()) {
            Date date = itr.next();
            result = new DateSpan(date, date);
        }
        return result;
    }

    /**
     * Selects the dates in the DateSpan.  This method will not change the
     * initial date displayed so the caller must update this if necessary.
     * If we are in SINGLE_SELECTION mode only the start time from the DateSpan
     * will be used.  If we are in WEEK_INTERVAL_SELECTION mode the span will be
     * modified to be valid if necessary.
     *
     * @param dateSpan DateSpan defining the selected dates.  Passing
     * <code>null</code> will clear the selection.
     *
     * @deprecated see #setSelectionInterval
     */
    @Deprecated
    public void setSelectedDateSpan(DateSpan dateSpan) {
        setSelectionInterval(dateSpan.getStartAsDate(), dateSpan.getEndAsDate());
    }

    
//---------------- DateSelectionModel

    /**
     * Returns the date selection model which drives this
     * JXMonthView.
     * 
     * @return the date selection model
     */
    public DateSelectionModel getSelectionModel() {
        return model;
    }

    /**
     * Sets the date selection model to drive this monthView.
     * 
     * @param model the selection model to use, must not be null.
     * @throws NullPointerException if model is null
     */
    public void setSelectionModel(DateSelectionModel model) {
        Contract.asNotNull(model, "date selection model must not be null");
        DateSelectionModel oldModel = this.model;
        this.model = model;
        firePropertyChange(SELECTION_MODEL, oldModel, model);
    }

//-------------------- delegates to model
    
    /**
     * Clear any selection from the selection model
     */
    public void clearSelection() {
        getSelectionModel().clearSelection();
    }

    /**
     * Return true if the selection is empty, false otherwise
     *
     * @return true if the selection is empty, false otherwise
     */
    public boolean isSelectionEmpty() {
        return getSelectionModel().isSelectionEmpty();
    }

    /**
     * Get the current selection
     *
     * @return sorted set of selected dates
     */
   public SortedSet<Date> getSelection() {
        return getSelectionModel().getSelection();
    }

    /**
     * Adds the selection interval to the selection model. <b>All dates are
     * modified to remove their hour of day, minute, second, and millisecond
     * before being added to the selection model</b>.
     * 
     * @param startDate Start of date range to add to the selection
     * @param endDate End of date range to add to the selection
     */
    public void addSelectionInterval(Date startDate, Date endDate) {
        if (selectionMode != SelectionMode.NO_SELECTION) {
            modifyedStartDate = startDate;
            modifyedEndDate = endDate;
            if (selectionMode == SelectionMode.WEEK_INTERVAL_SELECTION) {
                cleanupWeekSelectionDates(startDate, endDate);
            }
            getSelectionModel().addSelectionInterval(
                    cleanupDate(modifyedStartDate),
                    cleanupDate(modifyedEndDate));
        }
    }

    /**
     * Sets the selection interval to the selection model.  <b>All dates are modified to remove their hour of
     * day, minute, second, and millisecond before being added to the selection model</b>.
     *
     * @param startDate Start of date range to set the selection to
     * @param endDate End of date range to set the selection to
     */
    public void setSelectionInterval(final Date startDate, final Date endDate) {
        if (selectionMode != SelectionMode.NO_SELECTION) {
            modifyedStartDate = startDate;
            modifyedEndDate = endDate;
            if (selectionMode == SelectionMode.WEEK_INTERVAL_SELECTION) {
                cleanupWeekSelectionDates(startDate, endDate);
            }
            getSelectionModel().setSelectionInterval(cleanupDate(modifyedStartDate), cleanupDate(modifyedEndDate));
        }
    }

    /**
     * Removes the selection interval from the selection model.  <b>All dates are modified to remove their hour of
     * day, minute, second, and millisecond before being added to the selection model</b>.
     *
     * @param startDate Start of the date range to remove from the selection
     * @param endDate End of the date range to remove from the selection
     */
    public void removeSelectionInterval(final Date startDate, final Date endDate) {
        getSelectionModel().removeSelectionInterval(cleanupDate(startDate), cleanupDate(endDate));
    }

    /**
     * Returns the current selection mode for this JXMonthView.
     *
     * @return int Selection mode.
     */
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    /**
     * Set the selection mode for this JXMonthView.

     * @param selectionMode The selection mode to use for this {@code JXMonthView}
     */
    public void setSelectionMode(final SelectionMode selectionMode) {
        SelectionMode oldSelectionMode = this.selectionMode;
        this.selectionMode = selectionMode;
        if (selectionMode == SelectionMode.NO_SELECTION || selectionMode == SelectionMode.SINGLE_SELECTION) {
            getSelectionModel().setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
        } else if (selectionMode == SelectionMode.SINGLE_INTERVAL_SELECTION ||
                selectionMode == SelectionMode.WEEK_INTERVAL_SELECTION) {
            getSelectionModel().setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION);
        } else {
            getSelectionModel().setSelectionMode(DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        }
        firePropertyChange("selectionMode", oldSelectionMode, this.selectionMode);
    }

   
    /**
     * Returns the selected date. 
     * 
     * @return the first Date in the selection or null if empty.
     */
    public Date getSelectedDate() {
        SortedSet<Date> selection = getSelection();
        return selection.isEmpty() ? null : selection.first();
    }

    /**
     * Sets the model's selection to the given date or clears the selection if
     * null.
     * 
     * @param newDate the selection date to set
     */
    public void setSelectedDate(Date newDate) {
        if (newDate == null) {
            clearSelection();
        } else {
            setSelectionInterval(newDate, newDate);
        }
    }

    /**
     * Returns true if the specified date falls within the _startSelectedDate
     * and _endSelectedDate range.  <b>All dates are modified to remove their hour of
     * day, minute, second, and millisecond before being added to the selection model</b>.
     *
     * @param date The date to check
     * @return true if the date is selected, false otherwise
     */
    public boolean isSelectedDate(Date date) {
        return getSelectionModel().isSelected(cleanupDate(date));
    }

    /**
     * Set the lower bound date that is allowed to be selected. <p>
     * 
     * <b>All dates are
     * modified to remove their hour of day, minute, second, and millisecond
     * before being added to the selection model</b>.
     * 
     * @param lowerBound the lower bound, null means none.
     */
    public void setLowerBound(Date lowerBound) {
        Date lower = lowerBound != null ? cleanupDate(lowerBound) : null;
        getSelectionModel().setLowerBound(lower);
    }

    /**
     * Set the upper bound date that is allowed to be selected. <p>
     * 
     * <b>All dates are
     * modified to remove their hour of day, minute, second, and millisecond
     * before being added to the selection model</b>.
     * 
     * @param upperBound the upper bound, null means none.
     */
    public void setUpperBound(Date upperBound) {
        Date upper = upperBound != null ? cleanupDate(upperBound) : null;
        getSelectionModel().setUpperBound(upper);
    }


    /**
     * Return the lower bound date that is allowed to be selected for this
     * model
     *
     * @return lower bound date or null if not set
     */
    public Date getLowerBound() {
        return getSelectionModel().getLowerBound();
    }

    /**
     * Return the upper bound date that is allowed to be selected for this
     * model
     *
     * @return upper bound date or null if not set
     */
    public Date getUpperBound() {
        return getSelectionModel().getUpperBound();
    }

    /**
     * Identifies whether or not the date passed is an unselectable date.
     * <p>
     * 
     * <b>All dates are modified to remove their hour of day, minute, second,
     * and millisecond before being added to the selection model</b>.
     * 
     * @param date date which to test for unselectable status
     * @return true if the date is unselectable, false otherwise
     */
    public boolean isUnselectableDate(Date date) {
        return getSelectionModel().isUnselectableDate(cleanupDate(date));
    }

    /**
     * Sets the dates that should be unselectable. This will replace the model's
     * current set of unselectable dates. The implication is that calling with
     * zero dates will remove all unselectable dates.
     * <p>
     * 
     * NOTE: neither the given array nor any of its elements must be null.
     * <p>
     * <b>All dates are modified to remove their hour of day, minute, second,
     * and millisecond before being added to the selection model</b>.
     * 
     * @param unselectableDates zero or more not-null dates that should be
     *        unselectable.
     * @throws NullPointerException if either the array or any of the elements
     *         are null
     */
    public void setUnselectableDates(Date... unselectableDates) {
        Contract.asNotNull(unselectableDates,
                "unselectable dates must not be null");
        SortedSet<Date> unselectableSet = new TreeSet<Date>();
        for (Date unselectableDate : unselectableDates) {
            unselectableSet.add(cleanupDate(unselectableDate));
        }
        getSelectionModel().setUnselectableDates(unselectableSet);
        repaint();
    }

//---------------------- delegates to model: long param    
    /**
     * Returns true if the specified date falls within the _startSelectedDate
     * and _endSelectedDate range.  <b>All dates are modified to remove their hour of
     * day, minute, second, and millisecond before being added to the selection model</b>.
     *
     * @param date The date to check
     * @return true if the date is selected, false otherwise
     */
    public boolean isSelectedDate(long date) {
        return getSelectionModel().isSelected(new Date(cleanupDate(date)));
    }

    /**
     * Identifies whether or not the date passed is an unselectable date.  <b>All dates are modified to remove their
     * hour of day, minute, second, and millisecond before being added to the selection model</b>.
     *
     * @param date date which to test for unselectable status
     * @return true if the date is unselectable, false otherwise
     */
    public boolean isUnselectableDate(long date) {
        return getSelectionModel().isUnselectableDate(new Date(cleanupDate(date)));
    }

    /**
     * An array of longs defining days that should be unselectable.  <b>All dates are modified to remove their hour of
     * day, minute, second, and millisecond before being added to the selection model</b>.
     *
     * @param unselectableDates the dates that should be unselectable
     */
    public void setUnselectableDates(long[] unselectableDates) {
        SortedSet<Date> unselectableSet = new TreeSet<Date>();
        if (unselectableDates != null) {
            for (long unselectableDate : unselectableDates) {
                unselectableSet.add(new Date(cleanupDate(unselectableDate)));
            }
        }
        getSelectionModel().setUnselectableDates(unselectableSet);
        repaint();
    }

    

    /**
     * Identifies whether or not the date passed is a flagged date.  <b>All dates are modified to remove their hour of
     * day, minute, second, and millisecond before being added to the selection model</b>
     *
     * @param date date which to test for flagged status
     * @return true if the date is flagged, false otherwise
     */
    public boolean isFlaggedDate(long date) {
        boolean result = false;
        if (flaggedDates != null) {
            result = flaggedDates.contains(cleanupDate(date));
        }
        return result;
    }

    /**
     * An array of longs defining days that should be flagged.
     *
     * @param flaggedDates the dates to be flagged
     */
    public void setFlaggedDates(long[] flaggedDates) {
        if (flaggedDates == null) {
            this.flaggedDates = null;
        } else {
            this.flaggedDates = new TreeSet<Long>();
            // Loop through the flaggedDates and clean them up so
            // the hour, minute, seconds and milliseconds to 0 so
            // we can compare times later.
            for (long flaggedDate : flaggedDates) {
                this.flaggedDates.add(cleanupDate(flaggedDate));
            }
        }
        firePropertyChange(FLAGGED_DATES, null, this.flaggedDates);
        repaint();
    }

    /**
     * Temporary api to allow testing of cleanup after setting TimeZone.
     * 
     * PENDING: need access to the set of flagged dates.
     * 
     * @return a boolean indicating if this monthView has flagged dates.
     */
    protected boolean hasFlaggedDates() {
        return (flaggedDates != null) && (flaggedDates.size() > 0);
    }
    /**
     * Whether or not to show leading dates for a months displayed by this component.
     *
     * @param value true if leading dates should be displayed, false otherwise.
     */
    public void setShowLeadingDates(boolean value) {
        if (leadingDates == value) {
            return;
        }

        leadingDates = value;
        firePropertyChange(SHOW_LEADING_DATES, !leadingDates, leadingDates);
    }

    /**
     * Whether or not we're showing leading dates.
     *
     * @return true if leading dates are shown, false otherwise.
     */
    public boolean isShowingLeadingDates() {
        return leadingDates;
    }

    /**
     * Whether or not to show trailing dates for the months displayed by this component.
     *
     * @param value true if trailing dates should be displayed, false otherwise.
     */
    public void setShowTrailingDates(boolean value) {
        if (trailingDates == value) {
            return;
        }

        trailingDates = value;
        firePropertyChange(SHOW_TRAILING_DATES, !trailingDates, trailingDates);
    }

    /**
     * Whether or not we're showing trailing dates.
     *
     * @return true if trailing dates are shown, false otherwise.
     */
    public boolean isShowingTrailingDates() {
        return trailingDates;
    }
    
    private void cleanupWeekSelectionDates(Date startDate, Date endDate) {
        int count = 1;
        cal.setTime(startDate);
        while (cal.getTimeInMillis() < endDate.getTime()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            count++;
        }

        if (count > JXMonthView.DAYS_IN_WEEK) {
            // Move the start date to the first day of the week.
            cal.setTime(startDate);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int firstDayOfWeek = getFirstDayOfWeek();
            int daysFromStart = dayOfWeek - firstDayOfWeek;
            if (daysFromStart < 0) {
                daysFromStart += JXMonthView.DAYS_IN_WEEK;
            }
            cal.add(Calendar.DAY_OF_MONTH, -daysFromStart);

            modifyedStartDate = cal.getTime();

            // Move the end date to the last day of the week.
            cal.setTime(endDate);
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int lastDayOfWeek = firstDayOfWeek - 1;
            if (lastDayOfWeek == 0) {
                lastDayOfWeek = Calendar.SATURDAY;
            }
            int daysTillEnd = lastDayOfWeek - dayOfWeek;
            if (daysTillEnd < 0) {
                daysTillEnd += JXMonthView.DAYS_IN_WEEK;
            }
            cal.add(Calendar.DAY_OF_MONTH, daysTillEnd);
            modifyedEndDate = cal.getTime();
        }
    }


    private Date cleanupDate(Date date) {
        // only modify defensive copies
        return new Date(cleanupDate(date.getTime()));
    }

    private long cleanupDate(long date) {
        cal.setTimeInMillis(date);
        CalendarUtils.startOfDay(cal);
        return cal.getTimeInMillis();
    }

    /**
     * Returns the padding used between days in the calendar.
     *
     * @return Padding used between days in the calendar
     */
    public int getBoxPaddingX() {
        return boxPaddingX;
    }

    /**
     * Sets the number of pixels used to pad the left and right side of a day.
     * The padding is applied to both sides of the days.  Therefore, if you
     * used the padding value of 3, the number of pixels between any two days
     * would be 6.
     *
     * @param boxPaddingX Number of pixels applied to both sides of a day
     */
    public void setBoxPaddingX(int boxPaddingX) {
        int oldBoxPadding = this.boxPaddingX;
        this.boxPaddingX = boxPaddingX;
        firePropertyChange(BOX_PADDING_X, oldBoxPadding, this.boxPaddingX);
    }

    /**
     * Returns the padding used above and below days in the calendar.
     *
     * @return Padding used between dats in the calendar
     */
    public int getBoxPaddingY() {
        return boxPaddingY;
    }

    /**
     * Sets the number of pixels used to pad the top and bottom of a day.
     * The padding is applied to both the top and bottom of a day.  Therefore,
     * if you used the padding value of 3, the number of pixels between any
     * two days would be 6.
     *
     * @param boxPaddingY Number of pixels applied to top and bottom of a day
     */
    public void setBoxPaddingY(int boxPaddingY) {
        int oldBoxPadding = this.boxPaddingY;
        this.boxPaddingY = boxPaddingY;
        firePropertyChange(BOX_PADDING_Y, oldBoxPadding, this.boxPaddingY);
    }

    /**
     * Returns whether or not the month view supports traversing months.
     *
     * @return <code>true</code> if month traversing is enabled.
     */
    public boolean isTraversable() {
        return traversable;
    }

    /**
     * Set whether or not the month view will display buttons to allow the
     * user to traverse to previous or next months.
     *
     * @param traversable set to true to enable month traversing,
     *        false otherwise.
     */
    public void setTraversable(boolean traversable) {
        if (traversable != this.traversable) {
            this.traversable = traversable;
            firePropertyChange(TRAVERSABLE, !this.traversable, this.traversable);
            repaint();
        }
    }

    /**
     * Returns whether or not this <code>JXMonthView</code> should display
     * week number.
     *
     * @return <code>true</code> if week numbers should be displayed
     */
    public boolean isShowingWeekNumber() {
        return showWeekNumber;
    }

    /**
     * Set whether or not this <code>JXMonthView</code> will display week
     * numbers or not.
     *
     * @param showWeekNumber true if week numbers should be displayed,
     *        false otherwise
     */
    public void setShowingWeekNumber(boolean showWeekNumber) {
        if (this.showWeekNumber != showWeekNumber) {
            this.showWeekNumber = showWeekNumber;
            firePropertyChange(WEEK_NUMBER, !this.showWeekNumber, showWeekNumber);
            repaint();
        }
    }

    /**
     * Sets the single character representation for each day of the
     * week.  For this method the first days of the week days[0] is assumed to
     * be <code>Calendar.SUNDAY</code>.
     *
     * @param days Array of characters that represents each day
     * @throws IllegalArgumentException if <code>days.length</code> != DAYS_IN_WEEK
     * @throws NullPointerException if <code>days</code> == null
     */
    public void setDaysOfTheWeek(String[] days)
            throws IllegalArgumentException, NullPointerException {
        if (days == null) {
            throw new NullPointerException("Array of days is null.");
        } else if (days.length != DAYS_IN_WEEK) {
            throw new IllegalArgumentException(
                    "Array of days is not of length " + DAYS_IN_WEEK + " as expected.");
        }

        String[] oldValue = _daysOfTheWeek;
        _daysOfTheWeek = days;
        firePropertyChange(DAYS_OF_THE_WEEK, oldValue, _daysOfTheWeek);
        repaint();
    }

    /**
     * Returns the single character representation for each day of the
     * week.
     *
     * @return Single character representation for the days of the week
     */
    public String[] getDaysOfTheWeek() {
        String[] days = new String[DAYS_IN_WEEK];
        System.arraycopy(_daysOfTheWeek, 0, days, 0, DAYS_IN_WEEK);
        return days;
    }

    /**
     * Gets what the first day of the week is; e.g.,
     * <code>Calendar.SUNDAY</code> in the U.S., <code>Calendar.MONDAY</code>
     * in France.
     *
     * @return int The first day of the week.
     */
    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    /**
     * Sets what the first day of the week is; e.g.,
     * <code>Calendar.SUNDAY</code> in US, <code>Calendar.MONDAY</code>
     * in France.
     *
     * @param firstDayOfWeek The first day of the week.
     * @see java.util.Calendar
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if (firstDayOfWeek == this.firstDayOfWeek) {
            return;
        }

        int oldFirstDayOfWeek = this.firstDayOfWeek;

        this.firstDayOfWeek = firstDayOfWeek;
        cal.setFirstDayOfWeek(this.firstDayOfWeek);
        model.setFirstDayOfWeek(this.firstDayOfWeek);

        firePropertyChange("firstDayOfWeek", oldFirstDayOfWeek, this.firstDayOfWeek);

        repaint();
    }

    /**
     * Gets the time zone.
     *
     * @return The <code>TimeZone</code> used by the <code>JXMonthView</code>.
     */
    public TimeZone getTimeZone() {
        return cal.getTimeZone();
    }

    /**
     * Sets the time zone with the given time zone value.
     * 
     * This is a bound property. 
     * 
     * @param tz The <code>TimeZone</code>.
     */
    public void setTimeZone(TimeZone tz) {
        TimeZone old =getTimeZone();
        cal.setTimeZone(tz);
        anchor.setTimeZone(tz);
        setFirstDisplayedDate(anchor.getTimeInMillis());
        updateTodayFromCurrentTime();
        updateDatesAfterTimeZoneChange(old);
        firePropertyChange("timeZone", old, getTimeZone());
        
    }

    /**
     * All dates are "cleaned" relative to the timezone they had been set.
     * After changing the timezone, the need to be updated to the new.
     * 
     * Here: clear everything. 
     * 
     * @param oldTimeZone the timezone before the change
     */
    protected void updateDatesAfterTimeZoneChange(TimeZone oldTimeZone) {
        clearSelection();
        setLowerBound(null);
        setUpperBound(null);
        setFlaggedDates(null);
        setUnselectableDates(new Date[0]);
    }
    /**
     * 
     */
    private void updateTodayFromCurrentTime() {
        setToday(cleanupDate(System.currentTimeMillis()));
        
    }

    /**
     * Returns true if anti-aliased text is enabled for this component, false
     * otherwise.
     *
     * @return boolean <code>true</code> if anti-aliased text is enabled,
     * <code>false</code> otherwise.
     */
    public boolean isAntialiased() {
        return antiAlias;
    }

    /**
     * Turns on/off anti-aliased text for this component.
     *
     * @param antiAlias <code>true</code> for anti-aliased text,
     * <code>false</code> to turn it off.
     */
    public void setAntialiased(boolean antiAlias) {
        if (this.antiAlias == antiAlias) {
            return;
        }
        this.antiAlias = antiAlias;
        firePropertyChange("antialiased", !this.antiAlias, this.antiAlias);
        repaint();
    }

    /**
     * Returns the selected background color.
     *
     * @return the selected background color.
     */
    public Color getSelectedBackground() {
        return selectedBackground;
    }

    /**
     * Sets the selected background color to <code>c</code>.  The default color
     * is <code>138, 173, 209 (Blue-ish)</code>
     *
     * @param c Selected background.
     */
    public void setSelectedBackground(Color c) {
        selectedBackground = c;
    }

    /**
     * Returns the color used when painting the today background.
     *
     * @return Color Color
     */
    public Color getTodayBackground() {
        return todayBackgroundColor;
    }

    /**
     * Sets the color used to draw the bounding box around today.  The default
     * is the background of the <code>JXMonthView</code> component.
     *
     * @param c color to set
     */
    public void setTodayBackground(Color c) {
        todayBackgroundColor = c;
        repaint();
    }

    /**
     * Returns the color used to paint the month string background.
     *
     * @return Color Color.
     */
    public Color getMonthStringBackground() {
        return monthStringBackground;
    }

    /**
     * Sets the color used to draw the background of the month string.  The
     * default is <code>138, 173, 209 (Blue-ish)</code>.
     *
     * @param c color to set
     */
    public void setMonthStringBackground(Color c) {
        monthStringBackground = c;
        repaint();
    }

    /**
     * Returns the color used to paint the month string foreground.
     *
     * @return Color Color.
     */
    public Color getMonthStringForeground() {
        return monthStringForeground;
    }

    /**
     * Sets the color used to draw the foreground of the month string.  The
     * default is <code>Color.WHITE</code>.
     *
     * @param c color to set
     */
    public void setMonthStringForeground(Color c) {
        monthStringForeground = c;
        repaint();
    }

    /**
     * Sets the color used to draw the foreground of each day of the week. These
     * are the titles
     *
     * @param c color to set
     */
    public void setDaysOfTheWeekForeground(Color c) {
        daysOfTheWeekForeground = c;
        repaint();
    }

    /**
     * @return Color Color
     */
    public Color getDaysOfTheWeekForeground() {
        return daysOfTheWeekForeground;
    }

    /**
     * Set the color to be used for painting the specified day of the week.
     * Acceptable values are Calendar.SUNDAY - Calendar.SATURDAY.
     *
     * @param dayOfWeek constant value defining the day of the week.
     * @param c         The color to be used for painting the numeric day of the week.
     */
    public void setDayForeground(int dayOfWeek, Color c) {
        dayToColorTable.put(dayOfWeek, c);
    }

    /**
     * Return the color that should be used for painting the numerical day of the week.
     *
     * @param dayOfWeek The day of week to get the color for.
     * @return The color to be used for painting the numeric day of the week.
     *         If this was no color has yet been defined the component foreground color
     *         will be returned.
     */
    public Color getDayForeground(int dayOfWeek) {
        Color c;
        c = dayToColorTable.get(dayOfWeek);
        if (c == null) {
            c = getForeground();
        }
        return c;
    }

    /**
     * Set the color to be used for painting the foreground of a flagged day.
     *
     * @param c The color to be used for painting.
     */
    public void setFlaggedDayForeground(Color c) {
        flaggedDayForeground = c;
    }

    /**
     * Return the color that should be used for painting the foreground of the flagged day.
     *
     * @return The color to be used for painting
     */
    public Color getFlaggedDayForeground() {
        return flaggedDayForeground;
    }

    /**
     * Returns a copy of the insets used to paint the month string background.
     *
     * @return Insets Month string insets.
     */
    public Insets getMonthStringInsets() {
        return (Insets) _monthStringInsets.clone();
    }

    /**
     * Insets used to modify the width/height when painting the background
     * of the month string area.
     *
     * @param insets Insets
     */
    public void setMonthStringInsets(Insets insets) {
        if (insets == null) {
            _monthStringInsets.top = 0;
            _monthStringInsets.left = 0;
            _monthStringInsets.bottom = 0;
            _monthStringInsets.right = 0;
        } else {
            _monthStringInsets.top = insets.top;
            _monthStringInsets.left = insets.left;
            _monthStringInsets.bottom = insets.bottom;
            _monthStringInsets.right = insets.right;
        }
        repaint();
    }

    /**
     * Returns the preferred number of columns to paint calendars in.
     *
     * @return int Columns of calendars.
     */
    public int getPreferredCols() {
        return minCalCols;
    }

    /**
     * The preferred number of columns to paint calendars.
     *
     * @param cols The number of columns of calendars.
     */
    public void setPreferredCols(int cols) {
        if (cols <= 0) {
            return;
        }
        minCalCols = cols;
        revalidate();
        repaint();
    }

    /**
     * Returns the preferred number of rows to paint calendars in.
     *
     * @return int Rows of calendars.
     */
    public int getPreferredRows() {
        return minCalRows;
    }

    /**
     * Sets the preferred number of rows to paint calendars.
     *
     * @param rows The number of rows of calendars.
     */
    public void setPreferredRows(int rows) {
        if (rows <= 0) {
            return;
        }
        minCalRows = rows;
        revalidate();
        repaint();
    }

    private void updateToday() {
        // Update today.
        cal.setTimeInMillis(today);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        setToday(cal.getTimeInMillis());

        // Restore calendar.
        cal.setTimeInMillis(firstDisplayedDate);
        repaint();
    }

    private void setToday(long today) {
        long oldToday = this.today;
        this.today = today;
        firePropertyChange("today", oldToday, this.today);
    }

    /**
     * Moves and resizes this component to conform to the new bounding
     * rectangle r. This component's new position is specified by r.x and
     * r.y, and its new size is specified by r.width and r.height
     *
     * @param r The new bounding rectangle for this component
     */
    @Override
    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    /**
     * Sets the font of this component.
     *
     * @param font The font to become this component's font; if this parameter
     *             is null then this component will inherit the font of its parent.
     */
    @Override
    public void setFont(Font font) {
        Font old = getFont();
        super.setFont(font);
        firePropertyChange("font", old, font);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNotify() {
        todayTimer.stop();
        super.removeNotify();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNotify() {
        super.addNotify();

        // Setup timer to update the value of today.
        int secondsTillTomorrow = 86400;

        if (todayTimer == null) {
            todayTimer = new Timer(secondsTillTomorrow * 1000,
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            updateToday();
                        }
                    });
        }

        // Modify the initial delay by the current time.
        cal.setTimeInMillis(System.currentTimeMillis());
        secondsTillTomorrow = secondsTillTomorrow -
                (cal.get(Calendar.HOUR_OF_DAY) * 3600) -
                (cal.get(Calendar.MINUTE) * 60) -
                cal.get(Calendar.SECOND);
        todayTimer.setInitialDelay(secondsTillTomorrow * 1000);
        todayTimer.start();

        // Restore calendar
        cal.setTimeInMillis(firstDisplayedDate);
    }

    public Calendar getCalendar() {
        return cal;
    }

    /**
     * Return a long representing the date at the specified x/y position.
     * The date returned will have a valid day, month and year.  Other fields
     * such as hour, minute, second and milli-second will be set to 0.
     *
     * @param x X position
     * @param y Y position
     * @return long The date, -1 if position does not contain a date.
     */
    public long getDayAt(int x, int y) {
        return getUI().getDayAt(x, y);
    }

    /**
     * Returns the string currently used to identiy fired ActionEvents.
     *
     * @return String The string used for identifying ActionEvents.
     */
    public String getActionCommand() {
        return actionCommand;
    }

    /**
     * Sets the string used to identify fired ActionEvents.
     *
     * @param actionCommand The string used for identifying ActionEvents.
     */
    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    /**
     * Adds an ActionListener.
     * <p/>
     * The ActionListener will receive an ActionEvent when a selection has
     * been made.
     *
     * @param l The ActionListener that is to be notified
     */
    public void addActionListener(ActionListener l) {
        listenerMap.add(ActionListener.class, l);
    }

    /**
     * Removes an ActionListener.
     *
     * @param l The action listener to remove.
     */
    public void removeActionListener(ActionListener l) {
        listenerMap.remove(ActionListener.class, l);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        java.util.List<T> listeners = listenerMap.getListeners(listenerType);
        T[] result;
        if (!listeners.isEmpty()) {
            //noinspection unchecked
            result = (T[]) java.lang.reflect.Array.newInstance(listenerType, listeners.size());
            result = listeners.toArray(result);
        } else {
            result = super.getListeners(listenerType);
        }
        return result;
    }

    /**
     * Creates and fires an ActionEvent with the given action 
     * command to all listeners.
     * 
     * @param actionCommand the command for the created.
     */
    protected void fireActionPerformed(String actionCommand) {
        ActionListener[] listeners = getListeners(ActionListener.class);
        ActionEvent e = null;

        for (ActionListener listener : listeners) {
            if (e == null) {
                e = new ActionEvent(JXMonthView.this,
                        ActionEvent.ACTION_PERFORMED,
                        actionCommand);
            }
            listener.actionPerformed(e);
        }
    }

    /**
     * TODO: remove after commit/cancel are installed.
     *
     * @deprecated use {@link #commitSelection()} or {@link #cancelSelection()}
     */
    public void postActionEvent() {
        // PENDING: remove 
        fireActionPerformed(getActionCommand());
    }

    /**
     * Commits the current selection. <p>
     * 
     * Resets the model's adjusting property to false
     * and fires an ActionEvent
     * with the COMMIT_KEY action command.
     * 
     * <p>PENDING: define what "commit selection" means ... currently
     * only fires (to keep the picker happy).
     * 
     * @see #cancelSelection()
     * @see org.jdesktop.swingx.calendar.DateSelectionModel#setAdjusting(boolean)
     */
    public void commitSelection() {
        getSelectionModel().setAdjusting(false);
        fireActionPerformed(COMMIT_KEY);
    }

    /**
     * Cancels the selection. <p>
     * 
     * Resets the model's adjusting to 
     * false and fires an ActionEvent with the CANCEL_KEY action command.
     * 
     * @see #commitSelection
     * @see org.jdesktop.swingx.calendar.DateSelectionModel#setAdjusting(boolean)
     */
    public void cancelSelection() {
        getSelectionModel().setAdjusting(false);
        fireActionPerformed(CANCEL_KEY);
        
    }

    /**
     * Sets the component input map enablement property.<p>
     * 
     * If enabled, the keybinding for WHEN_IN_FOCUSED_WINDOW are
     * installed, otherwise not. Changing this property will
     * install/clear the corresponding key bindings. Typically, clients 
     * which want to use the monthview in a popup, should enable these.<p>
     * 
     * The default value is false.
     * 
     * @param enabled boolean to indicate whether the component
     *   input map should be enabled.
     * @see #isComponentInputMapEnabled()  
     */
    public void setComponentInputMapEnabled(boolean enabled) {
        if (isComponentInputMapEnabled() == enabled) return;
        this.componentInputMapEnabled = enabled;
        firePropertyChange("componentInputMapEnabled", !enabled, isComponentInputMapEnabled());
    }

    /**
     * Returns the componentInputMapEnabled property.
     * 
     * @return a boolean indicating whether the component input map is 
     *   enabled.
     * @see #setComponentInputMapEnabled(boolean)  
     *   
     */
    public boolean isComponentInputMapEnabled() {
        return componentInputMapEnabled;
    }


    /**
     * Sets locale and resets text and format used to display months and days. 
     * Also resets firstDayOfWeek.
     * 
     * <p>
     * <b>Warning:</b> Since this resets any string labels that are cached in UI
     * (month and day names) and firstDayofWeek, use <code>setDaysOfTheWeek</code> and/or
     * setFirstDayOfWeek after (re)setting locale.
     * </p>
     * 
     * @param   locale new Locale to be used for formatting
     * @see     setDaysOfTheWeek
     * @see     setFirstDayOfWeek
     */
    @Override
    public void setLocale(Locale locale) {
        if (locale != null) {
            // Locale is bound property, no need to firePropertyChange
            super.setLocale(locale);

            cal = Calendar.getInstance(getLocale());
            setFirstDayOfWeek(cal.getFirstDayOfWeek());

            repaint();
        }
    }
    
    
    
//    public static void main(String args[]) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                JFrame frame = new JFrame();
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                JXMonthView mv = new JXMonthView();
//                mv.setShowingWeekNumber(true);
//                mv.setTraversable(true);
//                Calendar cal = Calendar.getInstance();
//                cal.set(2006, 5, 20);
//                mv.setUnselectableDates(new long[] { cal.getTimeInMillis() });
//                mv.setPreferredRows(2);
//                mv.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
//                cal.setTimeInMillis(System.currentTimeMillis());
//                mv.setSelectionInterval(cal.getTime(), cal.getTime());
//                mv.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                        System.out.println(
//                                ((JXMonthView) e.getSource()).getSelection());
//                    }
//                });
//                frame.getContentPane().add(mv);
//                frame.pack();
//                frame.setVisible(true);
//            }
//        });
//    }
}
