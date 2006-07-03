package org.jdesktop.swingx.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.DateSelectionListener;
import org.jdesktop.swingx.DateSelectionModel;
import org.jdesktop.swingx.calendar.DateUtils;
import org.jdesktop.swingx.calendar.JXMonthView;
import org.jdesktop.swingx.calendar.JXMonthView.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.plaf.MonthViewUI;

public class BasicMonthViewUI extends MonthViewUI {
    private static final int LEADING_DAY_OFFSET = 1;
    private static final int NO_OFFSET = 0;
    private static final int TRAILING_DAY_OFFSET = -1;

    private static final int CALENDAR_SPACING = 10;
    private static final Point NO_SUCH_CALENDAR = new Point(-1, -1);

    /** Formatter used to format the day of the week to a numerical value. */
    protected static final SimpleDateFormat dayOfMonthFormatter = new SimpleDateFormat("d");

    private static String[] monthsOfTheYear;

    protected JXMonthView monthView;
    protected long firstDisplayedDate;
    protected int firstDisplayedMonth;
    protected int firstDisplayedYear;
    protected long lastDisplayedDate;
    protected long today;
    protected SortedSet<Date> selection;

    private boolean usingKeyboard = false;
    /** For interval selections we need to record the date we pivot around. */
    private long pivotDate = -1;
    private boolean ltr;
    private boolean showingWeekNumber;
    private int arrowPaddingX = 3;
    private int arrowPaddingY = 3;
    private int boxPaddingX;
    private int boxPaddingY;
    private int fullMonthBoxHeight;
    private int fullBoxWidth;
    private int fullBoxHeight;
    private int startX;
    private int startY;
    private Dimension dim = new Dimension();
    private PropertyChangeListener propertyChangeListener;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private Handler handler;
    private ImageIcon monthUpImage;
    private ImageIcon monthDownImage;
    private Rectangle dirtyRect = new Rectangle();
    private Rectangle bounds = new Rectangle();
    private Font derivedFont;

    /**
     * Date span used by the keyboard actions to track the original selection.
     */
    private SortedSet<Date> originalDateSpan;
    private int calendarWidth;
    private int monthBoxHeight;
    private int boxWidth;
    private int boxHeight;
    private int calendarHeight;
    /** The number of calendars able to be displayed horizontally. */
    private int numCalRows = 1;
    /** The number of calendars able to be displayed vertically. */
    private int numCalCols = 1;


    @SuppressWarnings({"UNUSED_SYMBOL"})
    public static ComponentUI createUI(JComponent c) {
        return new BasicMonthViewUI();
    }

    public void installUI(JComponent c) {
        monthView = (JXMonthView)c;
        monthView.setLayout(createLayoutManager());
        ltr = monthView.getComponentOrientation().isLeftToRight();
        LookAndFeel.installProperty(monthView, "opaque", Boolean.TRUE);

        // Get string representation of the months of the year.
        monthsOfTheYear = new DateFormatSymbols().getMonths();

        installComponents();
        installDefaults();
        installKeyboardActions();
        installListeners();
    }

    public void uninstallUI(JComponent c) {
        uninstallListeners();
        uninstallKeyboardActions();
        uninstallDefaults();
        uninstallComponents();
        monthView.setLayout(null);
        monthView = null;
    }

    protected void installComponents() {}

    protected void uninstallComponents() {}

    protected void installDefaults() {
        String[] daysOfTheWeek =
                (String[])UIManager.get("JXMonthView.daysOfTheWeek");
        if (daysOfTheWeek == null) {
            String[] dateFormatSymbols =
                new DateFormatSymbols().getShortWeekdays();
            daysOfTheWeek = new String[JXMonthView.DAYS_IN_WEEK];
            for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
                daysOfTheWeek[i - 1] = dateFormatSymbols[i];
            }
        }
        monthView.setDaysOfTheWeek(daysOfTheWeek);
        monthView.setBoxPaddingX((Integer)UIManager.get("JXMonthView.boxPaddingX"));
        monthView.setBoxPaddingY((Integer)UIManager.get("JXMonthView.boxPaddingY"));
        monthView.setMonthStringBackground(UIManager.getColor("JXMonthView.monthStringBackground"));
        monthView.setMonthStringForeground(UIManager.getColor("JXMonthView.monthStringForeground"));
        monthView.setDaysOfTheWeekForeground(UIManager.getColor("JXMonthView.daysOfTheWeekForeground"));
        monthView.setSelectedBackground(UIManager.getColor("JXMonthView.selectedBackground"));
        monthView.setFlaggedDayForeground(UIManager.getColor("JXMonthView.flaggedDayForeground"));
        monthView.setFont(UIManager.getFont("JXMonthView.font"));
        monthDownImage = new ImageIcon(
                JXMonthView.class.getResource(UIManager.getString("JXMonthView.monthDownFileName")));
        monthUpImage = new ImageIcon(
                JXMonthView.class.getResource(UIManager.getString("JXMonthView.monthUpFileName")));
    }

    protected void uninstallDefaults() {}

    protected void installKeyboardActions() {
        // Setup the keyboard handler.
        InputMap inputMap = monthView.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "acceptSelection");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "cancelSelection");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "selectPreviousDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "selectNextDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "selectDayInPreviousWeek");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "selectDayInNextWeek");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK, false), "addPreviousDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK, false), "addNextDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK, false), "addToPreviousWeek");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK, false), "addToNextWeek");

        // Needed to allow for keyboard control in popups.
        inputMap = monthView.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "acceptSelection");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "cancelSelection");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "selectPreviousDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "selectNextDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "selectDayInPreviousWeek");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "selectDayInNextWeek");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK, false), "adjustSelectionPreviousDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK, false), "adjustSelectionNextDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK, false), "adjustSelectionPreviousWeek");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK, false), "adjustSelectionNextWeek");

        ActionMap actionMap = monthView.getActionMap();
        actionMap.put("acceptSelection", new KeyboardAction(KeyboardAction.ACCEPT_SELECTION));
        actionMap.put("cancelSelection", new KeyboardAction(KeyboardAction.CANCEL_SELECTION));

        actionMap.put("selectPreviousDay", new KeyboardAction(KeyboardAction.SELECT_PREVIOUS_DAY));
        actionMap.put("selectNextDay", new KeyboardAction(KeyboardAction.SELECT_NEXT_DAY));
        actionMap.put("selectDayInPreviousWeek", new KeyboardAction(KeyboardAction.SELECT_DAY_PREVIOUS_WEEK));
        actionMap.put("selectDayInNextWeek", new KeyboardAction(KeyboardAction.SELECT_DAY_NEXT_WEEK));

        actionMap.put("adjustSelectionPreviousDay", new KeyboardAction(KeyboardAction.ADJUST_SELECTION_PREVIOUS_DAY));
        actionMap.put("adjustSelectionNextDay", new KeyboardAction(KeyboardAction.ADJUST_SELECTION_NEXT_DAY));
        actionMap.put("adjustSelectionPreviousWeek", new KeyboardAction(KeyboardAction.ADJUST_SELECTION_PREVIOUS_WEEK));
        actionMap.put("adjustSelectionNextWeek", new KeyboardAction(KeyboardAction.ADJUST_SELECTION_NEXT_WEEK));
    }

    protected void uninstallKeyboardActions() {}

    protected void installListeners() {
        propertyChangeListener = createPropertyChangeListener();
        mouseListener = createMouseListener();
        mouseMotionListener = createMouseMotionListener();

        monthView.addPropertyChangeListener(propertyChangeListener);
        monthView.addMouseListener(mouseListener);
        monthView.addMouseMotionListener(mouseMotionListener);
        monthView.getSelectionModel().addDateSelectionListener(getHandler());
    }

    protected void uninstallListeners() {
        monthView.getSelectionModel().removeDateSelectionListener(getHandler());
        monthView.removeMouseMotionListener(mouseMotionListener);
        monthView.removeMouseListener(mouseListener);
        monthView.removePropertyChangeListener(propertyChangeListener);

        mouseMotionListener = null;
        mouseListener = null;
        propertyChangeListener = null;
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return getHandler();
    }

    protected LayoutManager createLayoutManager() {
        return getHandler();
    }

    protected MouseListener createMouseListener() {
        return getHandler();
    }

    protected MouseMotionListener createMouseMotionListener() {
        return getHandler();
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }

        return handler;
    }

    public boolean isUsingKeyboard() {
        return usingKeyboard;
    }

    public void setUsingKeyboard(boolean val) {
        usingKeyboard = val;
    }


    /**
     * Returns true if the date passed in is the same as today.
     *
     * @param date long representing the date you want to compare to today.
     * @return true if the date passed is the same as today.
     */
    protected boolean isToday(long date) {
        return date == today;
    }

    public long getDayAt(int x, int y) {
           if (ltr ? (startX > x) : (startX < x) || startY > y) {
            return -1;
        }

        // Determine which column of calendars we're in.
        int calCol = (ltr ? (x - startX) : (startX - x)) /
                (calendarWidth + CALENDAR_SPACING);

        // Determine which row of calendars we're in.
        int calRow = (y - startY) / (calendarHeight + CALENDAR_SPACING);

        if (calRow > numCalRows - 1 || calCol > numCalCols - 1) {
            return -1;
        }

        // Determine what row (week) in the selected month we're in.
        int row = 1;
        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();
        row += (((y - startY) -
                (calRow * (calendarHeight + CALENDAR_SPACING))) -
                (boxPaddingY + monthBoxHeight + boxPaddingY)) /
                (boxPaddingY + boxHeight + boxPaddingY);
        // The first two lines in the calendar are the month and the days
        // of the week.  Ignore them.
        row -= 2;

        if (row < 0 || row > 5) {
            return -1;
        }

        // Determine which column in the selected month we're in.
        int col = ((ltr ? (x - startX) : (startX - x)) -
                (calCol * (calendarWidth + CALENDAR_SPACING))) /
                (boxPaddingX + boxWidth + boxPaddingX);

        // If we're showing week numbers we need to reduce the selected
        // col index by one.
        if (showingWeekNumber) {
            col--;
        }

        // Make sure the selected column matches up with a day of the week.
        if (col < 0 || col > JXMonthView.DAYS_IN_WEEK - 1) {
            return -1;
        }

        // Use the first day of the month as a key point for determining the
        // date of our click.
        // The week index of the first day will always be 0.
        Calendar cal = monthView.getCalendar();
        cal.setTimeInMillis(firstDisplayedDate);
        //_cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, calCol + (calRow * numCalCols));

        int firstDayViewIndex = getDayOfWeekViewIndex(cal.get(Calendar.DAY_OF_WEEK));
        int daysToAdd = (row * JXMonthView.DAYS_IN_WEEK) + (col - firstDayViewIndex);
        if (daysToAdd < 0 || daysToAdd >
                (cal.getActualMaximum(Calendar.DAY_OF_MONTH) - 1)) {
            return -1;
        }

        cal.add(Calendar.DAY_OF_MONTH, daysToAdd);

        long selected = cal.getTimeInMillis();

        // Restore the time.
        cal.setTimeInMillis(firstDisplayedDate);

        return selected;
    }


    /**
     * Convenience method so subclasses can get the currently painted day's day of the
     * week. It is assumed the calendar, _cal, is already set to the correct day.
     *
     * @see java.util.Calendar
     * @return day of the week (Calendar.SATURDAY, Calendar.SUNDAY, ...)
     */
    protected int getDayOfTheWeek() {
        return monthView.getCalendar().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Get the view index for the specified day of the week.  This value will range
     * from 0 to DAYS_IN_WEEK - 1.  For example if the first day of the week was set
     * to Calendar.MONDAY and we requested the view index for Calendar.TUESDAY the result
     * would be 1.
     *
     * @param dayOfWeek day of the week to calculate view index for, acceptable values are
     * <code>Calendar.MONDAY</code> - <code>Calendar.SUNDAY</code>
     * @return view index for the specified day of the week
     */
    private int getDayOfWeekViewIndex(int dayOfWeek) {
        int result = dayOfWeek - monthView.getFirstDayOfWeek();
        if (result < 0) {
            result += JXMonthView.DAYS_IN_WEEK;
        }
        return result;
    }
    /**
     * Returns an index defining which, if any, of the buttons for
     * traversing the month was pressed.  This method should only be
     * called when <code>setTraversable</code> is set to true.
     *
     * @param x x position of the pointer
     * @param y y position of the pointer
     * @return MONTH_UP, MONTH_DOWN or -1 when no button is selected.
     */
    protected int getTraversableButtonAt(int x, int y) {
        Point rowCol = getCalRowColAt(x, y);
        if (NO_SUCH_CALENDAR.equals(rowCol)) {
            return -1;
        }

        // See if we're in the month string area.
        y = ((y - startY) -
            (rowCol.x * (calendarHeight + CALENDAR_SPACING))) - monthView.getBoxPaddingY();
        if (y < arrowPaddingY || y > (monthBoxHeight - arrowPaddingY)) {
            return -1;
        }

        x = ((ltr ? (x - startX) : (startX - x)) -
            (rowCol.y * (calendarWidth + CALENDAR_SPACING)));

        if (x > arrowPaddingX && x < (arrowPaddingX +
                monthDownImage.getIconWidth() + arrowPaddingX)) {
            return JXMonthView.MONTH_DOWN;
        }

        if (x > (calendarWidth - arrowPaddingX * 2 -
                monthUpImage.getIconWidth()) &&
                x < (calendarWidth - arrowPaddingX)) {
            return JXMonthView.MONTH_UP;
        }
        return -1;
    }

    /**
     * Get the row and column for the calendar at the specified coordinates
     *
     * @param x x location
     * @param y y location
     * @return a new <code>Point</code> object containing the row as the x value
     * and column as the y value
     */
    protected Point getCalRowColAt(int x, int y) {
        if (ltr ? (startX > x) : (startX < x) || startY > y) {
            return NO_SUCH_CALENDAR;
        }

        Point result = new Point();
        // Determine which row of calendars we're in.
        result.x = (y - startY) / (calendarHeight + CALENDAR_SPACING);

        // Determine which column of calendars we're in.
        result.y = (ltr ? (x - startX) : (startX - x)) /
                (calendarWidth + CALENDAR_SPACING);

        // Make sure the row and column of calendars calculated is being
        // managed.
        if (result.x > numCalRows - 1 || result.y > numCalCols -1) {
            result = NO_SUCH_CALENDAR;
        }

        return result;
    }


    /**
     * Calculates the startX/startY position for centering the calendars
     * within the available space.
     */
    private void calculateStartPosition() {
        // Calculate offset in x-axis for centering calendars.
        int width = monthView.getWidth();
        startX = (width - ((calendarWidth * numCalCols) +
                (CALENDAR_SPACING * (numCalCols - 1)))) / 2;
        if (!ltr) {
            startX = width - startX;
        }

        // Calculate offset in y-axis for centering calendars.
        startY = (monthView.getHeight() - ((calendarHeight * numCalRows) +
                (CALENDAR_SPACING * (numCalRows - 1 )))) / 2;
    }

    /**
     * Calculates the numCalCols/numCalRows that determine the number of
     * calendars that can be displayed.
     */
    private void calculateNumDisplayedCals() {
        int oldNumCalCols = numCalCols;
        int oldNumCalRows = numCalRows;

        // Determine how many columns of calendars we want to paint.
        numCalCols = 1;
        numCalCols += (monthView.getWidth() - calendarWidth) /
                (calendarWidth + CALENDAR_SPACING);

        // Determine how many rows of calendars we want to paint.
        numCalRows = 1;
        numCalRows += (monthView.getHeight() - calendarHeight) /
                (calendarHeight + CALENDAR_SPACING);

        if (oldNumCalCols != numCalCols ||
                oldNumCalRows != numCalRows) {
            calculateLastDisplayedDate();
        }
    }


    public long calculateLastDisplayedDate() {
        Calendar cal = monthView.getCalendar();
        cal.setTimeInMillis(firstDisplayedDate);

        // Figure out the last displayed date.
        cal.add(Calendar.MONTH, ((numCalCols * numCalRows) - 1));
        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        lastDisplayedDate = cal.getTimeInMillis();

        return lastDisplayedDate;
    }

    private void calculateDirtyRectForSelection() {
        if (selection == null || selection.isEmpty()) {
            dirtyRect.x = 0;
            dirtyRect.y = 0;
            dirtyRect.width = 0;
            dirtyRect.height = 0;
        } else {
            Calendar cal = monthView.getCalendar();
            cal.setTime(selection.first());
            calculateBoundsForDay(dirtyRect, NO_OFFSET);
            cal.add(Calendar.DAY_OF_MONTH, 1);

            Rectangle tmpRect;
            while (cal.getTimeInMillis() <= selection.last().getTime()) {
                calculateBoundsForDay(bounds, NO_OFFSET);
                tmpRect = dirtyRect.union(bounds);
                dirtyRect.x = tmpRect.x;
                dirtyRect.y = tmpRect.y;
                dirtyRect.width = tmpRect.width;
                dirtyRect.height = tmpRect.height;
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            // Restore the time.
            cal.setTimeInMillis(firstDisplayedDate);
        }
    }

    /**
     * Calculate the bounding box for drawing a date.  It is assumed that the
     * calendar, _cal, is already set to the date you want to find the offset
     * for.
     *
     * @param bounds Bounds of the date to draw in.
     */
    private void calculateBoundsForDay(Rectangle bounds, int monthOffset) {
        Calendar cal = monthView.getCalendar();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);

        // If we are calculating the bounds for a leading/trailing day we need to
        // adjust the month we are in to calculate the bounds correctly.
        month += monthOffset;

        // Determine what row/column we are in.
        int diffMonths = month - firstDisplayedMonth +
                ((year - firstDisplayedYear) * JXMonthView.MONTHS_IN_YEAR);
        int calRowIndex = diffMonths / numCalCols;
        int calColIndex = diffMonths - (calRowIndex * numCalCols);

        // Modify the index relative to the first day of the week.
        bounds.x = getDayOfWeekViewIndex(cal.get(Calendar.DAY_OF_WEEK));

        // Offset for location of the day in the week.
        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();

        // If we're showing week numbers then increase the bounds.x
        // by one more boxPaddingX boxWidth boxPaddingX.
        if (showingWeekNumber) {
            bounds.x++;
        }

        // Calculate the x location.
        bounds.x = ltr ?
                bounds.x * (boxPaddingX + boxWidth + boxPaddingX) :
                (bounds.x + 1) * (boxPaddingX + boxWidth + boxPaddingX);

        // Offset for the column the calendar is displayed in.
        bounds.x += calColIndex * (calendarWidth + CALENDAR_SPACING);

        // Adjust by centering value.
        bounds.x = ltr ? startX + bounds.x : startX - bounds.x;

        // Initial offset for Month and Days of the Week display.
        bounds.y = boxPaddingY + monthBoxHeight + boxPaddingY +
            + boxPaddingY + boxHeight + boxPaddingY;

        // Offset for centering and row the calendar is displayed in.
        bounds.y += startY + calRowIndex *
                (calendarHeight + CALENDAR_SPACING);

        // Offset for Week of the Month.
        bounds.y += (weekOfMonth - 1) *
                (boxPaddingY + boxHeight + boxPaddingY);

        bounds.width = boxPaddingX + boxWidth + boxPaddingX;
        bounds.height = boxPaddingY + boxHeight + boxPaddingY;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);

        Object oldAAValue = null;
        Graphics2D g2 = (g instanceof Graphics2D) ? (Graphics2D)g : null;
        if (g2 != null && monthView.isAntialiased()) {
            oldAAValue = g2.getRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        Rectangle clip = g.getClipBounds();

        if (monthView.isOpaque()) {
            g.setColor(monthView.getBackground());
            g.fillRect(clip.x, clip.y, clip.width, clip.height);
        }
        g.setColor(monthView.getForeground());

        // Reset the calendar.
        Calendar cal = monthView.getCalendar();
        cal.setTimeInMillis(firstDisplayedDate);

        // Center the calendars horizontally/vertically in the available space.
        for (int row = 0; row < numCalRows; row++) {
            // Check if this row falls in the clip region.
            bounds.x = 0;
            bounds.y = startY +
                    row * (calendarHeight + CALENDAR_SPACING);
            bounds.width = monthView.getWidth();
            bounds.height = calendarHeight;

            if (!bounds.intersects(clip)) {
                cal.add(Calendar.MONTH, numCalCols);
                continue;
            }

            for (int column = 0; column < numCalCols; column++) {
                // Check if the month to paint falls in the clip.
                bounds.x = startX +
                        (ltr ?
                            column * (calendarWidth + CALENDAR_SPACING) :
                            -(column * (calendarWidth + CALENDAR_SPACING) +
                                    calendarWidth));
                bounds.y = startY +
                        row * (calendarHeight + CALENDAR_SPACING);
                bounds.width = calendarWidth;
                bounds.height = calendarHeight;

                // Paint the month if it intersects the clip.  If we don't move
                // the calendar forward a month as it would have if paintMonth
                // was called.
                if (bounds.intersects(clip)) {
                    paintMonth(g, bounds.x, bounds.y, bounds.width, bounds.height);
                } else {
                    cal.add(Calendar.MONTH, 1);
                }
            }
        }

        // Restore the calendar.
        cal.setTimeInMillis(firstDisplayedDate);
        if (g2 != null && monthView.isAntialiased()) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                oldAAValue);
        }
    }

    /**
     * Paints a month.  It is assumed the calendar, <code>monthView.getCalendar()</code>, is already set to the
     * first day of the month to be painted.
     *
     * @param g Graphics object.
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @SuppressWarnings({"UNUSED_SYMBOL"})
    private void paintMonth(Graphics g, int x, int y, int width, int height) {
        Calendar cal = monthView.getCalendar();
        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Rectangle clip = g.getClipBounds();
        long day;
        int oldWeek = -1;

        // Paint month name background.
        paintMonthStringBackground(g, x, y,
                width, boxPaddingY + monthBoxHeight + boxPaddingY);

        // Paint arrow buttons for traversing months if enabled.
        if (monthView.isTraversable()) {
            g.drawImage(monthDownImage.getImage(),
                    x + arrowPaddingX, y + ((fullMonthBoxHeight - monthDownImage.getIconHeight()) / 2), null);
            g.drawImage(monthUpImage.getImage(), x + width - arrowPaddingX - monthUpImage.getIconWidth(),
                    y + ((fullMonthBoxHeight - monthDownImage.getIconHeight()) / 2), null);
        }

        // Paint month name.
        Font oldFont = monthView.getFont();
        g.setFont(derivedFont);
        FontMetrics fm = monthView.getFontMetrics(derivedFont);
        String monthName = monthsOfTheYear[cal.get(Calendar.MONTH)];
        monthName = monthName + " " + cal.get(Calendar.YEAR);

        g.setColor(monthView.getMonthStringForeground());
        int tmpX =
                x + (calendarWidth / 2) -
                        (fm.stringWidth(monthName) / 2);
        int tmpY = y + boxPaddingY + ((monthBoxHeight - boxHeight) / 2) +
                fm.getAscent();
        g.drawString(monthName, tmpX, tmpY);
        g.setFont(oldFont);

        // Paint background of the short names for the days of the week.
        tmpX = ltr ? x + (showingWeekNumber ? fullBoxWidth : 0) : x;
        tmpY = y + fullMonthBoxHeight;
        int tmpWidth = width - (showingWeekNumber ? fullBoxWidth : 0);
        paintDayOfTheWeekBackground(g, tmpX, tmpY, tmpWidth, fullBoxHeight);

        // Paint short representation of day of the week.
        int dayIndex = monthView.getFirstDayOfWeek() - 1;
        g.setFont(derivedFont);
        g.setColor(monthView.getDaysOfTheWeekForeground());
        fm = monthView.getFontMetrics(derivedFont);
        String[] daysOfTheWeek = monthView.getDaysOfTheWeek();
        for (int i = 0; i < JXMonthView.DAYS_IN_WEEK; i++) {
            tmpX = ltr ?
                    x + (i * fullBoxWidth) + boxPaddingX +
                            (boxWidth / 2) -
                            (fm.stringWidth(daysOfTheWeek[dayIndex]) /
                                    2) :
                    x + width - (i * fullBoxWidth) - boxPaddingX -
                            (boxWidth / 2) -
                            (fm.stringWidth(daysOfTheWeek[dayIndex]) /
                                    2);
            if (showingWeekNumber) {
                tmpX += ltr ? fullBoxWidth : -fullBoxWidth;
            }
            tmpY = y + fullMonthBoxHeight + boxPaddingY + fm.getAscent();
            g.drawString(daysOfTheWeek[dayIndex], tmpX, tmpY);
            dayIndex++;
            if (dayIndex == JXMonthView.DAYS_IN_WEEK) {
                dayIndex = 0;
            }
        }
        g.setFont(oldFont);


        if (showingWeekNumber) {
            tmpX = ltr ? x : x + width - fullBoxWidth;
            paintWeekOfYearBackground(g, tmpX, y + fullMonthBoxHeight + fullBoxHeight, fullBoxWidth,
                    calendarHeight - (fullMonthBoxHeight + fullBoxHeight));
        }

        // TODO: paint leading days in the month
        int oldY = -1;
        for (int i = 0; i < days; i++) {
            calculateBoundsForDay(bounds, NO_OFFSET);
            // Paint the week numbers if we're displaying them.
            if (showingWeekNumber && oldY != bounds.y) {
                oldY = bounds.y;
                int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
                if (weekOfYear != oldWeek) {
                    tmpX = ltr ? x : x + width - fullBoxWidth;
                    paintWeekOfYear(g, tmpX, bounds.y, fullBoxWidth, fullBoxHeight, weekOfYear);
                    oldWeek = weekOfYear;
                }
            }

            if (bounds.intersects(clip)) {
                day = cal.getTimeInMillis();

                // Paint bounding box around any date that falls within the
                // selection.
                if (monthView.isSelectedDate(day)) {
                    // Keep track of the rectangle for the currently
                    // selected date so we don't have to recalculate it
                    // later when it becomes unselected.  This is only
                    // useful for SINGLE_SELECTION mode.
                    if (monthView.getSelectionMode() == SelectionMode.SINGLE_SELECTION) {
                        dirtyRect.x = bounds.x;
                        dirtyRect.y = bounds.y;
                        dirtyRect.width = bounds.width;
                        dirtyRect.height = bounds.height;
                    }
                }

                if (monthView.isUnselectableDate(day)) {
                    paintUnselectableDayBackground(g, bounds.x, bounds.y,
                            bounds.width, bounds.height, day);
                    paintUnselectableDayForeground(g, bounds.x, bounds.y,
                            bounds.width, bounds.height, day);
                }
                else if (monthView.isFlaggedDate(day)) {
                    paintFlaggedDayBackground(g, bounds.x, bounds.y,
                            bounds.width, bounds.height, day);
                    paintFlaggedDayForeground(g, bounds.x, bounds.y,
                            bounds.width, bounds.height, day);
                } else {
                    paintDayBackground(g, bounds.x, bounds.y,
                            bounds.width, bounds.height, day);
                    paintDayForeground(g, bounds.x, bounds.y,
                            bounds.width, bounds.height, day);
                }
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        // TODO: paint trailing days in the month
    }

    private void paintDayOfTheWeekBackground(Graphics g, int x, int y, int width, int height) {
        g.drawLine(x + boxPaddingX, y + height - 1, x + width - boxPaddingX, y + height - 1);
    }

    private void paintWeekOfYearBackground(Graphics g, int x, int y, int width, int height) {
        x = ltr ? x + width - 1 : x;
        g.drawLine(x, y + boxPaddingY, x, y + height - boxPaddingY);
    }

    /**
     * Paints the week of the year
     *
     * @param g Graphics object
     * @param x x-coordinate of upper left corner.
     * @param y y-coordinate of upper left corner.
     * @param width width of bounding box
     * @param height height of bounding box
     * @param weekOfYear week of the year
     */
    @SuppressWarnings({"UNUSED_SYMBOL"})
    private void paintWeekOfYear(Graphics g, int x, int y, int width, int height, int weekOfYear) {
        String str = Integer.toString(weekOfYear);
        FontMetrics fm;

        g.setColor(monthView.getDayForeground(getDayOfTheWeek()));

        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();

        fm = g.getFontMetrics();
        g.drawString(str,
                ltr ?
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(str) :
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(str) - 1,
                y + boxPaddingY + fm.getAscent());
    }

    /**
     * Paints the background of the month string.  The bounding box for this
     * background can be modified by setting its insets via
     * setMonthStringInsets.  The color of the background can be set via
     * setMonthStringBackground.
     *
     * @see org.jdesktop.swingx.calendar.JXMonthView#setMonthStringBackground
     * @see org.jdesktop.swingx.calendar.JXMonthView#setMonthStringInsets
     * @param g Graphics object to paint to.
     * @param x x-coordinate of upper left corner.
     * @param y y-coordinate of upper left corner.
     * @param width width of the bounding box.
     * @param height height of the bounding box.
     */
    protected void paintMonthStringBackground(Graphics g, int x, int y,
                                              int width, int height) {
        // Modify bounds by the month string insets.
        Insets monthStringInsets = monthView.getMonthStringInsets();
        x = ltr ? x + monthStringInsets.left : x + monthStringInsets.right;
        y = y + monthStringInsets.top;
        width = width - monthStringInsets.left - monthStringInsets.right;
        height = height - monthStringInsets.top - monthStringInsets.bottom;

        Graphics2D g2 = (Graphics2D)g;
        GradientPaint gp = new GradientPaint(x, y + height, new Color(238, 238, 238), x, y, new Color(204, 204, 204));
        g2.setPaint(gp);
        g2.fillRect(x, y, width - 1, height - 1);
        g2.setPaint(new Color(153, 153, 153));
        g2.drawRect(x, y, width - 1, height - 1);
    }

    /**
     * Paint the background for the specified day.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param date long value representing the day being painted
     * @see  org.jdesktop.swingx.calendar.JXMonthView#isSelectedDate
     * @see  #isToday
     */
    protected void paintDayBackground(Graphics g, int x, int y, int width, int height,
                                      long date) {
        if (monthView.isSelectedDate(date)) {
            g.setColor(monthView.getSelectedBackground());
            g.fillRect(x, y, width, height);
        }

        // If the date is today make sure we draw it's background over the selected
        // background.
        if (isToday(date)) {
            // Paint the gradiented border
            GradientPaint gp = new GradientPaint(x, y, new Color(91, 123, 145), x, y + height, new Color(68, 86, 98));
            Graphics2D g2 = (Graphics2D)g;
            g2.setPaint(gp);
            g2.drawRect(x, y, width - 1, height - 1);
        }
    }

    /**
     * Paint the foreground for the specified day.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param date long value representing the day being painted
     */
    protected void paintDayForeground(Graphics g, int x, int y, int width, int height,
                                      long date) {
        String numericDay = dayOfMonthFormatter.format(date);
        FontMetrics fm;

        g.setColor(monthView.getDayForeground(getDayOfTheWeek()));

        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();

        fm = g.getFontMetrics();
        g.drawString(numericDay,
                ltr ?
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(numericDay) :
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(numericDay) - 1,
                y + boxPaddingY + fm.getAscent());
    }

    /**
     * Paint the background for the specified flagged day.  The default implementation just
     * calls <code>paintDayBackground</code>.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param date long value representing the flagged day being painted
     */
    protected void paintFlaggedDayBackground(Graphics g, int x, int y, int width, int height, long date) {
        paintDayBackground(g, x, y, width, height, date);
    }

    /**
     * Paint the foreground for the specified flagged day.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param date long value representing the flagged day being painted
     */
    protected void paintFlaggedDayForeground(Graphics g, int x, int y, int width, int height, long date) {
        String numericDay = dayOfMonthFormatter.format(date);
        FontMetrics fm;

        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();

        Font oldFont = monthView.getFont();
        g.setColor(monthView.getFlaggedDayForeground());
        g.setFont(derivedFont);
        fm = monthView.getFontMetrics(derivedFont);
        g.drawString(numericDay,
                ltr ?
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(numericDay):
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(numericDay) - 1,
                y + boxPaddingY + fm.getAscent());
        g.setFont(oldFont);
    }

    /**
     * Paint the foreground for the specified unselectable day.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param date long value representing the flagged day being painted
     */
    protected void paintUnselectableDayBackground(Graphics g, int x, int y, int width, int height, long date) {
        paintDayBackground(g, x, y, width, height, date);
    }

    /**
     * Paint the foreground for the specified unselectable day.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param date long value representing the flagged day being painted
     */
    protected void paintUnselectableDayForeground(Graphics g, int x, int y, int width, int height, long date) {
        paintDayForeground(g, x, y, width, height, date);
        g.setColor(Color.RED);

        String numericDay = dayOfMonthFormatter.format(date);
        FontMetrics fm = monthView.getFontMetrics(derivedFont);
        width = fm.stringWidth(numericDay);
        height = fm.getAscent();
        x = ltr ? x + boxPaddingX + boxWidth - fm.stringWidth(numericDay) :
                x + boxPaddingX +
                        boxWidth - fm.stringWidth(numericDay) - 1;
        y = y + boxPaddingY;

        g.drawLine(x, y, x + width, y + height);
        g.drawLine(x + 1, y, x + width + 1, y + height);
        g.drawLine(x + width, y, x, y + height);
        g.drawLine(x + width - 1, y, x - 1, y + height);
    }

    private long cleanupDate(long date) {
        Calendar cal = monthView.getCalendar();
        cal.setTimeInMillis(date);
        // We only want to compare the day, month and year
        // so reset all other values to 0.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private class Handler implements ComponentListener, MouseListener, MouseMotionListener, LayoutManager,
            PropertyChangeListener, DateSelectionListener {
        private boolean asKirkWouldSay_FIRE;
        private long startDate;
        private long endDate;

        public void mouseClicked(MouseEvent e) {}

        public void mousePressed(MouseEvent e) {
            // If we were using the keyboard we aren't anymore.
            setUsingKeyboard(false);

            if (!monthView.isEnabled()) {
                return;
            }

            if (!monthView.hasFocus() && monthView.isFocusable()) {
                monthView.requestFocusInWindow();
            }

            // Check if one of the month traverse buttons was pushed.
            if (monthView.isTraversable()) {
                int arrowType = getTraversableButtonAt(e.getX(), e.getY());
                if (arrowType == JXMonthView.MONTH_DOWN) {
                    monthView.setFirstDisplayedDate(
                            DateUtils.getPreviousMonth(monthView.getFirstDisplayedDate()));
                    calculateDirtyRectForSelection();
                    return;
                } else if (arrowType == JXMonthView.MONTH_UP) {
                    monthView.setFirstDisplayedDate(
                            DateUtils.getNextMonth(monthView.getFirstDisplayedDate()));
                    calculateDirtyRectForSelection();
                    return;
                }
            }

            SelectionMode selectionMode = monthView.getSelectionMode();
            if (selectionMode == SelectionMode.NO_SELECTION) {
                return;
            }

            long selected = monthView.getDayAt(e.getX(), e.getY());
            if (selected == -1) {
                return;
            }

            // Update the selected dates.
            startDate = selected;
            endDate = selected;

            if (selectionMode == SelectionMode.SINGLE_INTERVAL_SELECTION ||
                    selectionMode == SelectionMode.WEEK_INTERVAL_SELECTION ||
                    selectionMode == SelectionMode.MULTIPLE_INTERVAL_SELECTION) {
                pivotDate = selected;
            }

            if (selectionMode == SelectionMode.MULTIPLE_INTERVAL_SELECTION && e.isControlDown()) {
                monthView.addSelectionInterval(new Date(startDate), new Date(endDate));
            } else {
                monthView.setSelectionInterval(new Date(startDate), new Date(endDate));
            }

            // Arm so we fire action performed on mouse release.
            asKirkWouldSay_FIRE = true;
        }

        public void mouseReleased(MouseEvent e) {
            // If we were using the keyboard we aren't anymore.
            setUsingKeyboard(false);

            if (!monthView.isEnabled()) {
                return;
            }

            if (!monthView.hasFocus() && monthView.isFocusable()) {
                monthView.requestFocusInWindow();
            }

            if (asKirkWouldSay_FIRE) {
                monthView.postActionEvent();
            }
            asKirkWouldSay_FIRE = false;
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}

        public void mouseDragged(MouseEvent e) {
            // If we were using the keyboard we aren't anymore.
            setUsingKeyboard(false);
            SelectionMode selectionMode = monthView.getSelectionMode();

            if (!monthView.isEnabled() || selectionMode == SelectionMode.NO_SELECTION) {
                return;
            }

            long selected = monthView.getDayAt(e.getX(), e.getY());

            if (selected == -1) {
                return;
            }

            long oldStart = startDate;
            long oldEnd = endDate;

            if (selectionMode == SelectionMode.SINGLE_SELECTION) {
                if (selected == oldStart) {
                    return;
                }
                startDate = selected;
                endDate = selected;
            } else {
                if (selected <= pivotDate) {
                    startDate = selected;
                    endDate = pivotDate;
                } else if (selected > pivotDate) {
                    startDate = pivotDate;
                    endDate = selected;
                }
            }

            if (oldStart == startDate && oldEnd == endDate) {
                return;
            }

            if (selectionMode == SelectionMode.MULTIPLE_INTERVAL_SELECTION && e.isControlDown()) {
                monthView.addSelectionInterval(new Date(startDate), new Date(endDate));
            } else {
                monthView.setSelectionInterval(new Date(startDate), new Date(endDate));
            }

            // Set trigger.
            asKirkWouldSay_FIRE = true;
        }

        public void mouseMoved(MouseEvent e) {}

        public void addLayoutComponent(String name, Component comp) {}

        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            layoutContainer(parent);
            return new Dimension(dim);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        public void layoutContainer(Container parent) {
            // Loop through year and get largest representation of the month.
            // Keep track of the longest month so we can loop through it to
            // determine the width of a date box.
            int currDays;
            int longestMonth = 0;
            int daysInLongestMonth = 0;

            int currWidth;
            int longestMonthWidth = 0;

            // We use a bold font for figuring out size constraints since
            // it's larger and flaggedDates will be noted in this style.
            derivedFont = monthView.getFont().deriveFont(Font.BOLD);
            FontMetrics fm = monthView.getFontMetrics(derivedFont);

            Calendar cal = monthView.getCalendar();
            cal.set(Calendar.MONTH, cal.getMinimum(Calendar.MONTH));
            cal.set(Calendar.DAY_OF_MONTH,
                    cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            for (int i = 0; i < cal.getMaximum(Calendar.MONTH); i++) {
                currWidth = fm.stringWidth(monthsOfTheYear[i]);
                if (currWidth > longestMonthWidth) {
                    longestMonthWidth = currWidth;
                }
                currDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (currDays > daysInLongestMonth) {
                    longestMonth = cal.get(Calendar.MONTH);
                    daysInLongestMonth = currDays;
                }
                cal.add(Calendar.MONTH, 1);
            }

            // Loop through the days of the week and adjust the box width
            // accordingly.
            boxHeight = fm.getHeight();
            String[] daysOfTheWeek = monthView.getDaysOfTheWeek();
            for (String dayOfTheWeek : daysOfTheWeek) {
                currWidth = fm.stringWidth(dayOfTheWeek);
                if (currWidth > boxWidth) {
                    boxWidth = currWidth;
                }
            }

            // Loop through longest month and get largest representation of the day
            // of the month.
            cal.set(Calendar.MONTH, longestMonth);
            cal.set(Calendar.DAY_OF_MONTH,
                    cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            for (int i = 0; i < daysInLongestMonth; i++) {
                currWidth = fm.stringWidth(
                        dayOfMonthFormatter.format(cal.getTime()));
                if (currWidth > boxWidth) {
                    boxWidth = currWidth;
                }
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            // If we are displaying week numbers find the largest displayed week number.
            if (showingWeekNumber) {
                int val = cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
                currWidth = fm.stringWidth(Integer.toString(val));
                if (currWidth > boxWidth) {
                    boxWidth = currWidth;
                }
            }

            // If the calendar is traversable, check the icon heights and
            // adjust the month box height accordingly.
            monthBoxHeight = boxHeight;
            if (monthView.isTraversable()) {
                int newHeight = monthDownImage.getIconHeight() +
                        arrowPaddingY + arrowPaddingY;
                if (newHeight > monthBoxHeight) {
                    monthBoxHeight = newHeight;
                }
            }

            // Modify boxWidth if month string is longer
            int boxPaddingX = monthView.getBoxPaddingX();
            int boxPaddingY = monthView.getBoxPaddingY();
            dim.width = (boxWidth + (2 * boxPaddingX)) * JXMonthView.DAYS_IN_WEEK;
            if (dim.width < longestMonthWidth) {
                double diff = longestMonthWidth - dim.width;
                if (monthView.isTraversable()) {
                    diff += monthDownImage.getIconWidth() +
                            monthUpImage.getIconWidth() + (arrowPaddingX * 4);
                }
                boxWidth += Math.ceil(diff / (double)JXMonthView.DAYS_IN_WEEK);
            }


            // Keep track of a full box height/width and full month box height
            fullBoxWidth = boxWidth + boxPaddingX + boxPaddingX;
            fullBoxHeight = boxHeight + boxPaddingY + boxPaddingY;
            fullMonthBoxHeight = monthBoxHeight + boxPaddingY + boxPaddingY;

            // Keep track of calendar width and height for use later.
            calendarWidth = fullBoxWidth * JXMonthView.DAYS_IN_WEEK;
            if (showingWeekNumber) {
                calendarWidth += fullBoxWidth;
            }

            calendarHeight = (fullBoxHeight * 7) + fullMonthBoxHeight;

            // Calculate minimum width/height for the component.
            int prefRows = monthView.getPreferredRows();
            dim.height = (calendarHeight * prefRows) +
                    (CALENDAR_SPACING * (prefRows - 1));

            int prefCols = monthView.getPreferredCols();
            dim.width = (calendarWidth * prefCols) +
                    (CALENDAR_SPACING * (prefCols - 1));

            // Add insets to the dimensions.
            Insets insets = monthView.getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;

            // Restore calendar.
            cal.setTimeInMillis(firstDisplayedDate);

            calculateNumDisplayedCals();
            calculateStartPosition();

            if (!monthView.getSelectionModel().isSelectionEmpty()) {
                long startDate = selection.first().getTime();
                if (startDate > lastDisplayedDate ||
                        startDate < firstDisplayedDate) {
                    // Already does the recalculation for the dirty rect.
                    monthView.ensureDateVisible(startDate);
                } else {
                    calculateDirtyRectForSelection();
                }
            }
        }


        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();

            if ("componentOrientation".equals(property)) {
                ltr = monthView.getComponentOrientation().isLeftToRight();
                monthView.revalidate();
                calculateStartPosition();
                calculateDirtyRectForSelection();
            } else if (JXMonthView.ENSURE_DATE_VISIBILITY.equals(property)) {
                calculateDirtyRectForSelection();
            } else if (JXMonthView.SELECTION_MODEL.equals(property)) {
                DateSelectionModel selectionModel = (DateSelectionModel) evt.getOldValue();
                selectionModel.removeDateSelectionListener(getHandler());
                selectionModel = (DateSelectionModel) evt.getNewValue();
                selectionModel.addDateSelectionListener(getHandler());
            } else if (JXMonthView.FIRST_DISPLAYED_DATE.equals(property)) {
                firstDisplayedDate = (Long)evt.getNewValue();
            } else if (JXMonthView.FIRST_DISPLAYED_MONTH.equals(property)) {
                firstDisplayedMonth = (Integer)evt.getNewValue();
            } else if (JXMonthView.FIRST_DISPLAYED_YEAR.equals(property)) {
                firstDisplayedYear = (Integer)evt.getNewValue();
            } else if ("today".equals(property)) {
                today = (Long)evt.getNewValue();
            } else if (JXMonthView.BOX_PADDING_X.equals(property) || JXMonthView.BOX_PADDING_Y.equals(property) ||
                    JXMonthView.TRAVERSABLE.equals(property) || JXMonthView.DAYS_OF_THE_WEEK.equals(property) ||
                    "border".equals(property) || "font".equals(property) || JXMonthView.WEEK_NUMBER.equals(property)) {
                boxPaddingX = monthView.getBoxPaddingX();
                boxPaddingY = monthView.getBoxPaddingY();
                showingWeekNumber = monthView.isShowingWeekNumber();
                monthView.revalidate();
            }
        }

        public void componentResized(ComponentEvent e) {
            monthView.revalidate();
            monthView.repaint();
        }

        public void componentMoved(ComponentEvent e) {}

        public void componentShown(ComponentEvent e) {}

        public void componentHidden(ComponentEvent e) {}

        public void valueChanged(DateSelectionEvent ev) {
            selection = ev.getSelection();
            // repaint old dirty region
            monthView.repaint(dirtyRect);
            // calculate new dirty region based on selection
            calculateDirtyRectForSelection();
            // repaint new selection
            monthView.repaint(dirtyRect);
        }
    }

    /**
     * Class that supports keyboard traversal of the JXMonthView component.
     */
    private class KeyboardAction extends AbstractAction {
        public static final int ACCEPT_SELECTION = 0;
        public static final int CANCEL_SELECTION = 1;
        public static final int SELECT_PREVIOUS_DAY = 2;
        public static final int SELECT_NEXT_DAY = 3;
        public static final int SELECT_DAY_PREVIOUS_WEEK = 4;
        public static final int SELECT_DAY_NEXT_WEEK = 5;
        public static final int ADJUST_SELECTION_PREVIOUS_DAY = 6;
        public static final int ADJUST_SELECTION_NEXT_DAY = 7;
        public static final int ADJUST_SELECTION_PREVIOUS_WEEK = 8;
        public static final int ADJUST_SELECTION_NEXT_WEEK = 9;

        private int action;

        public KeyboardAction(int action) {
            this.action = action;
        }

        public void actionPerformed(ActionEvent ev) {
            SelectionMode selectionMode = monthView.getSelectionMode();

            if (selectionMode != SelectionMode.NO_SELECTION) {
                if (!isUsingKeyboard()) {
                    originalDateSpan = monthView.getSelection();
                }

                if (action >= ACCEPT_SELECTION && action <= CANCEL_SELECTION && isUsingKeyboard()) {
                    if (action == CANCEL_SELECTION) {
                        // Restore the original selection.
                        if (!originalDateSpan.isEmpty()) {
                            monthView.setSelectionInterval(originalDateSpan.first(), originalDateSpan.last());
                        } else {
                            monthView.clearSelection();
                        }
                        monthView.postActionEvent();
                    } else {
                        // Accept the keyboard selection.
                        monthView.postActionEvent();
                    }
                    setUsingKeyboard(false);
                } else if (action >= SELECT_PREVIOUS_DAY && action <= SELECT_DAY_NEXT_WEEK) {
                    setUsingKeyboard(true);
                    pivotDate = -1;
                    traverse(action);
                } else if (selectionMode == SelectionMode.SINGLE_INTERVAL_SELECTION &&
                        action >= ADJUST_SELECTION_PREVIOUS_DAY && action <= ADJUST_SELECTION_NEXT_WEEK) {
                    setUsingKeyboard(true);
                    addToSelection(action);
                }
            }
        }

        private void traverse(int action) {
            long oldStart = selection.isEmpty() ? System.currentTimeMillis() : selection.first().getTime();
            Calendar cal = monthView.getCalendar();
            cal.setTimeInMillis(oldStart);
            switch (action) {
                case SELECT_PREVIOUS_DAY:
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    break;
                case SELECT_NEXT_DAY:
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                case SELECT_DAY_PREVIOUS_WEEK:
                    cal.add(Calendar.DAY_OF_MONTH, -JXMonthView.DAYS_IN_WEEK);
                    break;
                case SELECT_DAY_NEXT_WEEK:
                    cal.add(Calendar.DAY_OF_MONTH, JXMonthView.DAYS_IN_WEEK);
                    break;
            }

            long newStartDate = cal.getTimeInMillis();
            if (newStartDate != oldStart) {
                final Date startDate = new Date(newStartDate);
                monthView.setSelectionInterval(startDate, startDate);
                monthView.ensureDateVisible(newStartDate);
            }
            // Restore the original time value.
            cal.setTimeInMillis(firstDisplayedDate);
        }

        /**
         * If we are in a mode that allows for range selection this method
         * will extend the currently selected range.
         *
         * NOTE: This may not be the expected behavior for the keyboard controls
         * and we ay need to update this code to act in a way that people expect.
         */
        private void addToSelection(int action) {
            long newStartDate;
            long newEndDate;
            long selectionStart;
            long selectionEnd;

            if (!selection.isEmpty()) {
                newStartDate = selectionStart = selection.first().getTime();
                newEndDate = selectionEnd = selection.last().getTime();
            } else {
                newStartDate = selectionStart = cleanupDate(System.currentTimeMillis());
                newEndDate = selectionEnd = newStartDate;
            }

            if (-1 == pivotDate) {
                pivotDate = newStartDate;
            }

            boolean isForward = true;

            Calendar cal = monthView.getCalendar();
            switch (action) {
                case ADJUST_SELECTION_PREVIOUS_DAY:
                    if (newEndDate <= pivotDate) {
                        cal.setTimeInMillis(newStartDate);
                        cal.add(Calendar.DAY_OF_MONTH, -1);
                        newStartDate = cal.getTimeInMillis();
                    } else {
                        cal.setTimeInMillis(newEndDate);
                        cal.add(Calendar.DAY_OF_MONTH, -1);
                        newEndDate = cal.getTimeInMillis();
                    }
                    isForward = false;
                    break;
                case ADJUST_SELECTION_NEXT_DAY:
                    if (newStartDate >= pivotDate) {
                        cal.setTimeInMillis(newEndDate);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        newStartDate = pivotDate;
                        newEndDate = cal.getTimeInMillis();
                    } else {
                        cal.setTimeInMillis(newStartDate);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        newStartDate = cal.getTimeInMillis();
                    }
                    break;
                case ADJUST_SELECTION_PREVIOUS_WEEK:
                    if (newEndDate <= pivotDate) {
                        cal.setTimeInMillis(newStartDate);
                        cal.add(Calendar.DAY_OF_MONTH, -JXMonthView.DAYS_IN_WEEK);
                        newStartDate = cal.getTimeInMillis();
                    } else {
                        cal.setTimeInMillis(newEndDate);
                        cal.add(Calendar.DAY_OF_MONTH, -JXMonthView.DAYS_IN_WEEK);
                        long newTime = cal.getTimeInMillis();
                        if (newTime <= pivotDate) {
                            newStartDate = newTime;
                            newEndDate = pivotDate;
                        } else {
                            newEndDate = cal.getTimeInMillis();
                        }

                    }
                    isForward = false;
                    break;
                case ADJUST_SELECTION_NEXT_WEEK:
                    if (newStartDate >= pivotDate) {
                        cal.setTimeInMillis(newEndDate);
                        cal.add(Calendar.DAY_OF_MONTH, JXMonthView.DAYS_IN_WEEK);
                        newEndDate = cal.getTimeInMillis();
                    } else {
                        cal.setTimeInMillis(newStartDate);
                        cal.add(Calendar.DAY_OF_MONTH, JXMonthView.DAYS_IN_WEEK);
                        long newTime = cal.getTimeInMillis();
                        if (newTime >= pivotDate) {
                            newStartDate = pivotDate;
                            newEndDate = newTime;
                        } else {
                            newStartDate = cal.getTimeInMillis();
                        }
                    }
                    break;
            }
            if (newStartDate != selectionStart || newEndDate != selectionEnd) {
                monthView.setSelectionInterval(new Date(newStartDate), new Date(newEndDate));
                monthView.ensureDateVisible(isForward ? newEndDate : newStartDate);
            }

            // Restore the original time value.
            cal.setTimeInMillis(firstDisplayedDate);
        }
    }
}