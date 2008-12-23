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
package org.jdesktop.swingx.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DateSelectionModel;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.event.DateSelectionListener;
import org.jdesktop.swingx.plaf.MonthViewUI;
import org.jdesktop.swingx.plaf.UIManagerExt;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.renderer.LabelProvider;
import org.jdesktop.swingx.renderer.PainterAware;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;

/**
 * Base implementation of the <code>JXMonthView</code> UI.<p>
 *
 * <b>Note</b>: The api changed considerably between releases 0.9.4 and 0.9.5.  
 * The old methods are still available but deprecated and are no longer maintained. 
 * <p>
 * 
 * The general drift of the change was to delegate all text rendering to a dedicated
 * rendering controller (currently named RenderingHandler), similar to 
 * the collection view rendering. The UI itself keeps layout and positioning of
 * the rendering components. Plus updating on property changes received from the 
 * monthView. <p>
 * 
 * The rendering approach is used by default. Subclass providers can turn it off
 * by overriding createRenderingHandler to return null. This is recommended until
 * the change has stabilized. In future, custom painting will be achieved by 
 * implementing custom RenderingHandlers.
 * 
 * <p>   
 * Painting: coordinate systems.
 * 
 * <ul>
 * <li> Screen coordinates of months/days, accessible via the getXXBounds() methods. These
 * coordinates are absolute in the system of the monthView. 
 * <li> The grid of visible months with logical row/column coordinates. The logical 
 * coordinates are adjusted to ComponentOrientation. 
 * <li> The grid of days in a month with logical row/column coordinates. The logical 
 * coordinates are adjusted to ComponentOrientation. The columns 
 * are the days of the week, the rows are the weeks in a month. The column header shows
 * the localized names of the days and has the row coordinate -1. It is shown always.
 * The row header shows the week number in the year and has the column coordinate -1. It
 * is shown only if the showingWeekNumber property is true.  
 * </ul>
 * 
 * On the road to "zoomable" date range views (Vista-style).<p>
 * 
 * Added support (doesn't do anything yet, zoom-logic must yet be defined) 
 * by way of an active calendar header which is added to the monthView if zoomable. 
 * It is disabled by default. In this mode, the view is always
 * traversable and shows exactly one calendar. It is orthogonal to the classic 
 * mode, that is client code should not be effected in any way as long as the mode 
 * is not explicitly enabled. <p>
 * 
 * @author dmouse
 * @author rbair
 * @author rah003
 * @author Jeanette Winzenburg
 */
public class BasicMonthViewUI extends MonthViewUI {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(BasicMonthViewUI.class
            .getName());

    
    private static final int WEEKS_IN_MONTH = 6;
    private static final int CALENDAR_SPACING = 10;

    
    /** Return value used to identify when the month down button is pressed. */
    public static final int MONTH_DOWN = 1;
    /** Return value used to identify when the month up button is pressed. */
    public static final int MONTH_UP = 2;


    private static final int WEEK_HEADER_COLUMN = -1;


    private static final int DAYS_IN_WEEK = 7;


    private static final int DAY_HEADER_ROW = -1;

    /** 
     * Formatter used to format the day of the week to a numerical value. 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected final SimpleDateFormat dayOfMonthFormatter = new SimpleDateFormat("d");
    /** localized names of all months.
     * protected for testing only!
     * PENDING: JW - should be property on JXMonthView, for symmetry with
     *   daysOfTheWeek? 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected String[] monthsOfTheYear;

    /** the component we are installed for. */
    protected JXMonthView monthView;
    // listeners
    private PropertyChangeListener propertyChangeListener;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private Handler handler;

    // fields related to visible date range
    /** start of day of the first visible month. */
    private Date firstDisplayedDate;
    /** first visible month. */
    private int firstDisplayedMonth;
    /** first visible year. */
    private int firstDisplayedYear;
    /** end of day of the last visible month. */
    private Date lastDisplayedDate;
    /** 
    
    //---------- fields related to selection/navigation


    /** flag indicating keyboard navigation. */
    private boolean usingKeyboard = false;
    /** For interval selections we need to record the date we pivot around. */
    private Date pivotDate = null;
    /**
     * Date span used by the keyboard actions to track the original selection.
     */
    private SortedSet<Date> originalDateSpan;

    //------------------ visuals
    /** 
     * Used as the font for days of week and title. 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected Font derivedFont;

    protected boolean isLeftToRight;
    protected Icon monthUpImage;
    protected Icon monthDownImage;
    
    /**
     * Foreground of weekOfTheYear.
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    private Color weekOfTheYearForeground;
    /**
     * Foreground for crossing out unselectable dates.
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    private Color unselectableDayForeground;
    /**
     * Foreground for leading dates.
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    private Color leadingDayForeground;
    /**
     * Foreground for trailing dates.
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    private Color trailingDayForeground;

    /**
     * The padding for month traversal icons.
     * PENDING JW: decouple rendering and hit-detection.
     */
    private int arrowPaddingX = 3;
    private int arrowPaddingY = 3;
    
    
    /** 
     * height of month header of the view, that is the name and the arrows.
     * initially, it's the same as the day-box-height, adjusted to arrow icon height
     * and arrow padding if traversable
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    private int monthBoxHeight;
    /** height of month header including the monthView's box padding. */
    private int fullMonthBoxHeight;
    /** 
     * raw witdth of a "day" box calculated from fontMetrics and "widest" content.
     *  this is the same for days-of-the-week, weeks-of-the-year and days
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    private int boxWidth;
    /** 
     * raw height of a "day" box calculated from fontMetrics and "widest" content.
     *  this is the same for days-of-the-week, weeks-of-the-year and days
     * @deprecated no longer used in paint/layout with renderer.
     * 
     */
    @Deprecated
    private int boxHeight;
    /** 
     * width of a "day" box including the monthView's box padding
     * this is the same for days-of-the-week, weeks-of-the-year and days
     */
    private int fullBoxWidth;
    /** 
     * height of a "day" box including the monthView's box padding
     * this is the same for days-of-the-week, weeks-of-the-year and days
     */
    private int fullBoxHeight;
    /** the width of a single month display. */
    private int calendarWidth;
    /** the height of a single month display. */
    private int calendarHeight;
    /** the height of a single month grid cell, including padding. */
    private int fullCalendarHeight;
    /** the width of a single month grid cell, including padding. */
    private int fullCalendarWidth;
    /** The number of calendars displayed vertically. */
    private int calendarRowCount = 1;
    /** The number of calendars displayed horizontally. */
    private int calendarColumnCount = 1;
    
    /**
     * The bounding box of the grid of visible months. 
     */
    protected Rectangle calendarGrid = new Rectangle();
    
    /** array of bounds of the month strings. 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    private Rectangle[] monthStringBounds = new Rectangle[12];
    /** array of bounds of the year strings. 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    private Rectangle[] yearStringBounds = new Rectangle[12];
    /**
     * The Strings used for the day headers. This is the fall-back for
     * the monthView if no custom strings are set. 
     * PENDING JW: delegate to RenderingHandler?
     */
    private String[] daysOfTheWeek;

    /**
     * Provider of configured components for text rendering.
     */
    private CalendarRenderingHandler renderingHandler;
    /**
     * The CellRendererPane for stamping rendering comps.
     */
    private CellRendererPane rendererPane;


    private BasicCalendarHeader calendarHeader;


    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new BasicMonthViewUI();
    }

    /**
     * Installs the component as appropriate for the current lf.
     * 
     * PENDING JW: clarify sequence of installXX methods. 
     */
    @Override
    public void installUI(JComponent c) {
        monthView = (JXMonthView)c;
        monthView.setLayout(createLayoutManager());
        
        // PENDING JW: move to installDefaults or installComponents?
        installRenderingHandler();
        
        installDefaults();
        installDelegate();
        installComponents();
        installKeyboardActions();
        updateLocale(false);
        installListeners();
    }


    @Override
    public void uninstallUI(JComponent c) {
        
        uninstallRenderingHandler();
        uninstallListeners();
        uninstallKeyboardActions();
        uninstallDefaults();
        uninstallComponents();
        monthView.setLayout(null);
        monthView = null;
    }

    /**
     * Creates and configures the calendar header. Adds to the MonthView if
     * zoomable.
     */
    protected void installComponents() {
        calendarHeader = createCalendarHeader();
        calendarHeader.setFont(getAsNotUIResource(createDerivedFont()));
        calendarHeader.setBackground(getAsNotUIResource(monthView.getMonthStringBackground()));
        if (isZoomable()) {
            monthView.add(calendarHeader);
        }
    }

    /**
     * Returns a Font based on the param which is not of type UIResource. 
     * 
     * @param font the base font
     * @return a font not of type UIResource, may be null.
     */
    private Font getAsNotUIResource(Font font) {
        if (!(font instanceof UIResource)) return font;
        // PENDING JW: correct way to create another font instance?
       return font.deriveFont(font.getAttributes());
    }
    
    /**
     * Returns a Color based on the param which is not of type UIResource. 
     * 
     * @param color the base color
     * @return a color not of type UIResource, may be null.
     */
    private Color getAsNotUIResource(Color color) {
        if (!(color instanceof UIResource)) return color;
        // PENDING JW: correct way to create another color instance?
        float[] rgb = color.getRGBComponents(null);
        return new Color(rgb[0], rgb[1], rgb[2], rgb[3]);
    }

    protected void uninstallComponents() {
        monthView.remove(calendarHeader);
        calendarHeader.setActions(null, null, null);
        calendarHeader = null;
    }

    /**
     * Installs default values. 
     * 
     * This is refactored to only install default properties on the monthView.
     * Extracted install of this delegate's properties into installDelegate. 
     *  
     */
    protected void installDefaults() {
        // PENDING JW: move to installDefaults?
        LookAndFeel.installProperty(monthView, "opaque", Boolean.TRUE);
        
       // JW: access all properties via the UIManagerExt ..
        //        BasicLookAndFeel.installColorsAndFont(monthView, 
//                "JXMonthView.background", "JXMonthView.foreground", "JXMonthView.font");
        
        if (isUIInstallable(monthView.getBackground())) {
            monthView.setBackground(UIManagerExt.getColor("JXMonthView.background"));
        }
        if (isUIInstallable(monthView.getForeground())) {
            monthView.setForeground(UIManagerExt.getColor("JXMonthView.foreground"));
        }
        if (isUIInstallable(monthView.getFont())) {
            // PENDING JW: missing in managerExt? Or not applicable anyway?
            monthView.setFont(UIManager.getFont("JXMonthView.font"));
        }
        if (isUIInstallable(monthView.getMonthStringBackground())) {
            monthView.setMonthStringBackground(UIManagerExt.getColor("JXMonthView.monthStringBackground"));
        }
        if (isUIInstallable(monthView.getMonthStringForeground())) {
            monthView.setMonthStringForeground(UIManagerExt.getColor("JXMonthView.monthStringForeground"));
        }
        if (isUIInstallable(monthView.getDaysOfTheWeekForeground())) {
            monthView.setDaysOfTheWeekForeground(UIManagerExt.getColor("JXMonthView.daysOfTheWeekForeground"));
        }
        if (isUIInstallable(monthView.getSelectionBackground())) {
            monthView.setSelectionBackground(UIManagerExt.getColor("JXMonthView.selectedBackground"));
        }
        if (isUIInstallable(monthView.getSelectionForeground())) {
            monthView.setSelectionForeground(UIManagerExt.getColor("JXMonthView.selectedForeground"));
        }
        if (isUIInstallable(monthView.getFlaggedDayForeground())) {
            monthView.setFlaggedDayForeground(UIManagerExt.getColor("JXMonthView.flaggedDayForeground"));
        }
        
        monthView.setBoxPaddingX(UIManagerExt.getInt("JXMonthView.boxPaddingX"));
        monthView.setBoxPaddingY(UIManagerExt.getInt("JXMonthView.boxPaddingY"));
    }

    /**
     * Installs this ui delegates properties.
     */
    protected void installDelegate() {
        isLeftToRight = monthView.getComponentOrientation().isLeftToRight();
        // PENDING JW: remove here if rendererHandler takes over control completely
        // as is, some properties are duplicated
        monthDownImage = UIManager.getIcon("JXMonthView.monthDownFileName");
        monthUpImage = UIManager.getIcon("JXMonthView.monthUpFileName");
        weekOfTheYearForeground = UIManagerExt.getColor("JXMonthView.weekOfTheYearForeground");
        unselectableDayForeground = UIManagerExt.getColor("JXMonthView.unselectableDayForeground");
        leadingDayForeground = UIManagerExt.getColor("JXMonthView.leadingDayForeground");
        trailingDayForeground = UIManagerExt.getColor("JXMonthView.trailingDayForeground");
        derivedFont = createDerivedFont();
        
        // install date related state
        setFirstDisplayedDay(monthView.getFirstDisplayedDay());
    }

    /**
     * Checks and returns whether the given property should be replaced
     * by the UI's default value.
     * 
     * @param property the property to check.
     * @return true if the given property should be replaced by the UI#s
     *   default value, false otherwise. 
     */
    protected boolean isUIInstallable(Object property) {
       return (property == null) || (property instanceof UIResource);
    }
    
    protected void uninstallDefaults() {}

    protected void installKeyboardActions() {
        // Setup the keyboard handler.
        // PENDING JW: change to when-ancestor? just to be on the safe side
        // if we make the title contain active comps
        installKeyBindings(JComponent.WHEN_FOCUSED);
        // JW: removed the automatic keybindings in WHEN_IN_FOCUSED
        // which caused #555-swingx (binding active if not focused)
        ActionMap actionMap = monthView.getActionMap();
        KeyboardAction acceptAction = new KeyboardAction(KeyboardAction.ACCEPT_SELECTION);
        actionMap.put("acceptSelection", acceptAction);
        KeyboardAction cancelAction = new KeyboardAction(KeyboardAction.CANCEL_SELECTION);
        actionMap.put("cancelSelection", cancelAction);

        actionMap.put("selectPreviousDay", new KeyboardAction(KeyboardAction.SELECT_PREVIOUS_DAY));
        actionMap.put("selectNextDay", new KeyboardAction(KeyboardAction.SELECT_NEXT_DAY));
        actionMap.put("selectDayInPreviousWeek", new KeyboardAction(KeyboardAction.SELECT_DAY_PREVIOUS_WEEK));
        actionMap.put("selectDayInNextWeek", new KeyboardAction(KeyboardAction.SELECT_DAY_NEXT_WEEK));

        actionMap.put("adjustSelectionPreviousDay", new KeyboardAction(KeyboardAction.ADJUST_SELECTION_PREVIOUS_DAY));
        actionMap.put("adjustSelectionNextDay", new KeyboardAction(KeyboardAction.ADJUST_SELECTION_NEXT_DAY));
        actionMap.put("adjustSelectionPreviousWeek", new KeyboardAction(KeyboardAction.ADJUST_SELECTION_PREVIOUS_WEEK));
        actionMap.put("adjustSelectionNextWeek", new KeyboardAction(KeyboardAction.ADJUST_SELECTION_NEXT_WEEK));

        actionMap.put(JXMonthView.COMMIT_KEY, acceptAction);
        actionMap.put(JXMonthView.CANCEL_KEY, cancelAction);
    }

    /**
     * @param inputMap
     */
    private void installKeyBindings(int type) {
        InputMap inputMap = monthView.getInputMap(type);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "acceptSelection");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "cancelSelection");

        // @KEEP quickly check #606-swingx: keybindings not working in internalframe
        // eaten somewhere
//        inputMap.put(KeyStroke.getKeyStroke("F1"), "selectPreviousDay");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "selectPreviousDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "selectNextDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "selectDayInPreviousWeek");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "selectDayInNextWeek");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK, false), "adjustSelectionPreviousDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK, false), "adjustSelectionNextDay");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK, false), "adjustSelectionPreviousWeek");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK, false), "adjustSelectionNextWeek");
    }

    /**
     * @param inputMap
     */
    private void uninstallKeyBindings(int type) {
        InputMap inputMap = monthView.getInputMap(type);
        inputMap.clear();
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

    /**
     * 
     */
    private void installRenderingHandler() {
        renderingHandler = createRenderingHandler();
        if (renderingHandler != null) {
            rendererPane = new CellRendererPane();
            monthView.add(rendererPane);
        }
    }
    
    private void uninstallRenderingHandler() {
        if (renderingHandler == null) return;
        monthView.remove(rendererPane);
        rendererPane = null;
        renderingHandler = null;
    }

    /**
     * Returns the RenderingHandler to use. May return null to indicate that
     * the "old" painting mechanism should be used.
     * 
     * This implementation returns an instance of RenderingHandler.
     * 
     * @return the renderingHandler to use for painting the day boxes or
     *   null if the old painting mechanism should be used.
     */
    protected CalendarRenderingHandler createRenderingHandler() {
        return new RenderingHandler();
    }
    
    /**
     * The RenderingHandler responsible for text rendering. It provides 
     * and configures a rendering component for the given cell of
     * a JXMonthView. <p>
     * 
     * 
     */
    protected  static class RenderingHandler implements CalendarRenderingHandler {
        /** The CellContext for content and default visual config. */
        private CalendarCellContext cellContext;
        /** The providers to use per DayState. */
        private Map<CalendarState, ComponentProvider<?>> providers;
        // Formatters/state used by Providers. 
        /** Localized month strings used in title. */
        private String[] monthNames;
        //-------- Highlight properties
        /** The Painter used for highlighting unselectable dates. */
        private TextCrossingPainter textCross;
        /** The foreground color for unselectable date highlight. */
        private Color unselectableDayForeground;
        /** The foreground color for week of year column. */
        private Color weekOfTheYearForeground;

        /**
         * Instantiates a RenderingHandler and installs default state.
         */
        public RenderingHandler() {
            install();
        }
        
        private void install() {
            weekOfTheYearForeground = UIManagerExt.getColor("JXMonthView.weekOfTheYearForeground");
            unselectableDayForeground = UIManagerExt.getColor("JXMonthView.unselectableDayForeground");
            textCross = new TextCrossingPainter<JLabel>();
            cellContext = new CalendarCellContext();
            installProviders();
        }

        /**
         * Creates and stores ComponentProviders for all DayStates.
         */
        private void installProviders() {
            providers = new HashMap<CalendarState, ComponentProvider<?>>();
            FormatStringValue sv = new FormatStringValue(new SimpleDateFormat("d")) {

                @Override
                public String getString(Object value) {
                    if (value instanceof Calendar) {
                        ((DateFormat) getFormat()).setTimeZone(((Calendar) value).getTimeZone());
                        value = ((Calendar) value).getTime();
                    }
                    return super.getString(value);
                }

            };
            ComponentProvider<?> provider = new LabelProvider(sv, JLabel.RIGHT);
            providers.put(CalendarState.IN_MONTH, provider);
            providers.put(CalendarState.TODAY, provider);
            providers.put(CalendarState.TRAILING, provider);
            providers.put(CalendarState.LEADING, provider);

            StringValue wsv = new StringValue() {

                public String getString(Object value) {
                    if (value instanceof Calendar) {
                        value = ((Calendar) value).get(Calendar.WEEK_OF_YEAR);
                    }
                    return StringValues.TO_STRING.getString(value);
                }

            };
            ComponentProvider<?> weekOfYearProvider = new LabelProvider(wsv,
                    JLabel.RIGHT);
            providers.put(CalendarState.WEEK_OF_YEAR, weekOfYearProvider);

            ComponentProvider<?> dayOfWeekProvider = new LabelProvider(JLabel.CENTER) {

                @Override
                protected String getValueAsString(CellContext context) {
                    Object value = context.getValue();
                    // PENDING JW: this is breaking provider's contract in its
                    // role as StringValue! Don't in the general case.
                    if (value instanceof Calendar) {
                        int day = ((Calendar) value).get(Calendar.DAY_OF_WEEK);
                        return ((JXMonthView) context.getComponent()).getDayOfTheWeek(day);
                    }
                    return super.getValueAsString(context);
                }
                
            };
            providers.put(CalendarState.DAY_OF_WEEK, dayOfWeekProvider);

            StringValue tsv = new StringValue() {

                public String getString(Object value) {
                    if (value instanceof Calendar) {
                        String month = monthNames[((Calendar) value)
                                .get(Calendar.MONTH)];
                        return month + " "
                                + ((Calendar) value).get(Calendar.YEAR); 
                    }
                    return StringValues.TO_STRING.getString(value);
                }

            };
            ComponentProvider<?> titleProvider = new LabelProvider(tsv,
                    JLabel.CENTER);
            providers.put(CalendarState.TITLE, titleProvider);
        }
        

        /**
         * Updates internal state to the given Locale.
         * 
         * @param locale the new Locale.
         */
        public void setLocale(Locale locale) {
            monthNames = new DateFormatSymbols(locale).getMonths();
        }

        /**
         * Configures and returns a component for rendering of the given monthView cell.
         * 
         * @param monthView the JXMonthView to render onto
         * @param calendar the cell value
         * @param dayState the DayState of the cell
         * @return a component configured for rendering the given cell
         */
        public JComponent prepareRenderingComponent(JXMonthView monthView, Calendar calendar, CalendarState dayState) {
            cellContext.installMonthContext(monthView, calendar, 
                    isSelected(monthView, calendar, dayState), 
                    isFocused(monthView, calendar, dayState),
                    dayState);
            JComponent comp = providers.get(dayState).getRendererComponent(cellContext);
            return highlight(comp, monthView, calendar, dayState);
        }


        /**
         * 
         * PENDING JW: replace hard-coded logic by giving over to highlighters.
         * 
         * @param monthView the JXMonthView to render onto
         * @param calendar the cell value
         * @param dayState the DayState of the cell
         * @param dayState
         */
        private JComponent highlight(JComponent comp, JXMonthView monthView,
                Calendar calendar, CalendarState dayState) {
            if ((CalendarState.LEADING == dayState) || (CalendarState.TRAILING == dayState)) return comp;
            if (CalendarState.WEEK_OF_YEAR == dayState) {
                if (weekOfTheYearForeground != null) {
                    comp.setForeground(weekOfTheYearForeground);
                } 
                return comp;
            }
            if (CalendarState.TITLE == dayState) {
                comp.setFont(getDerivedFont(comp.getFont()));
                return comp;
            }
            if (CalendarState.DAY_OF_WEEK == dayState) {
                comp.setFont(getDerivedFont(comp.getFont()));
                if (monthView.getDaysOfTheWeekForeground() != null) {
                    comp.setForeground(monthView.getDaysOfTheWeekForeground());
                }
                return comp;
            }
            if (monthView.isFlaggedDate(calendar.getTime())) {
                comp.setForeground(monthView.getFlaggedDayForeground());
            } else {
                Color perDay = monthView.getDayForeground(calendar.get(Calendar.DAY_OF_WEEK));
                if ((perDay != null) && (perDay != monthView.getForeground())) {
                    // PENDING JW: this here prevents selection foreground on all
                    // if not checked for "normal" foreground
                    comp.setForeground(perDay);
                }
            }
            if (monthView.isUnselectableDate(calendar.getTime()) 
                    && (comp instanceof PainterAware )) {
                textCross.setForeground(unselectableDayForeground);
                ((PainterAware) comp).setPainter(textCross);
            }
            
            return comp;
            
        }

        /**
         * @param font
         * @return
         */
        private Font getDerivedFont(Font font) {
            return font.deriveFont(Font.BOLD);
        }

        /**
         * @param monthView
         * @param calendar
         * @param dayState
         * @return
         */
        private boolean isFocused(JXMonthView monthView, Calendar calendar,
                CalendarState dayState) {
            return false;
        }
        
        /**
         * @param monthView the JXMonthView to render onto
         * @param calendar the cell value
         * @param dayState the DayState of the cell
         * @return
         */
        private boolean isSelected(JXMonthView monthView, Calendar calendar,
                CalendarState dayState) {
            if (!isSelectable(dayState)) return false;
            return monthView.isSelected(calendar.getTime());
        }

        
        /**
         * @param dayState
         * @return
         */
        private boolean isSelectable(CalendarState dayState) {
            return (CalendarState.IN_MONTH == dayState) || (CalendarState.TODAY == dayState);
        }

    }

    //----------------------- controller
    
    /**
     * Binds/clears the keystrokes in the component input map, 
     * based on the monthView's componentInputMap enabled property.
     * 
     * @see org.jdesktop.swingx.JXMonthView#isComponentInputMapEnabled()
     */
    protected void updateComponentInputMap() {
        if (monthView.isComponentInputMapEnabled()) {
            installKeyBindings(JComponent.WHEN_IN_FOCUSED_WINDOW);
        } else {
            uninstallKeyBindings(JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
    }


   /**
     * Updates internal state according to monthView's locale. Revalidates the
     * monthView.
     * 
     * @deprecated use {@link #updateLocale(boolean)}
     */
    @Deprecated
    protected void updateLocale() {
        updateLocale(true);
    }

    /**
     * Updates internal state according to monthView's locale. Revalidates the
     * monthView if the boolean parameter is true.
     * 
     * @param revalidate a boolean indicating whether the monthView should be 
     * revalidated after the change.
     */
    protected void updateLocale(boolean revalidate) {
        Locale locale = monthView.getLocale();
        if (renderingHandler != null) {
            renderingHandler.setLocale(locale);
        }
        monthsOfTheYear = new DateFormatSymbols(locale).getMonths();

        // fixed JW: respect property in UIManager if available
        // PENDING JW: what to do if weekdays had been set
        // with JXMonthView method? how to detect?
        daysOfTheWeek = (String[]) UIManager.get("JXMonthView.daysOfTheWeek");

        if (daysOfTheWeek == null) {
            daysOfTheWeek = new String[7];
            String[] dateFormatSymbols = new DateFormatSymbols(locale)
                    .getShortWeekdays();
            daysOfTheWeek = new String[JXMonthView.DAYS_IN_WEEK];
            for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
                daysOfTheWeek[i - 1] = dateFormatSymbols[i];
            }
        }
        installHeaderActions();
        if (revalidate) {
            monthView.invalidate();
            monthView.validate();
        }
    }

   @Override
   public String[] getDaysOfTheWeek() {
       String[] days = new String[daysOfTheWeek.length];
       System.arraycopy(daysOfTheWeek, 0, days, 0, days.length);
       return days;
   }
   

//---------------------- listener creation    
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



    // ----------------------- mapping day coordinates

    /**
     * Returns the bounds of the day in the grid of days which contains the
     * given location. The bounds are in monthView screen coordinate system.
     * <p>
     * 
     * Note: this is a pure geometric mapping. The returned rectangle need not
     * necessarily map to a date in the month which contains the location, it
     * can represent a week-number/column header or a leading/trailing date.
     * 
     * @param x the x position of the location in pixel
     * @param y the y position of the location in pixel
     * @return the bounds of the day which contains the location, or null if
     *         outside
     */
    protected Rectangle getDayBoundsAtLocation(int x, int y) {
        Rectangle days = getMonthDetailsBoundsAtLocation(x, y);
        if ((days == null) || (!days.contains(x, y)))
            return null;
        int calendarRow = (y - days.y) / fullBoxHeight;
        int calendarColumn = (x - days.x) / fullBoxWidth;
        return new Rectangle(days.x + calendarColumn * fullBoxWidth, days.y
                + calendarRow * fullBoxHeight, fullBoxWidth, fullBoxHeight);
    }
    
    /**
     * Returns the logical coordinates of the day which contains the given
     * location. The p.x of the returned value represents the day of week, the
     * p.y represents the week of the month. The transformation takes care of
     * ComponentOrientation.
     * <p>
     * 
     * Note: this is a pure geometric mapping. The returned grid position need not
     * necessarily map to a date in the month which contains the location, it
     * can represent a week-number/column header or a leading/trailing date.
     * 
     * @param x the x position of the location in pixel
     * @param y the y position of the location in pixel
     * @return the logical coordinates of the day in the grid of days in a month
     *         or null if outside.
     */
    protected Point getDayGridPositionAtLocation(int x, int y) {
        Rectangle days = getMonthDetailsBoundsAtLocation(x, y);
        if ((days == null) ||(!days.contains(x, y))) return null;
        int calendarRow = (y - days.y) / fullBoxHeight;
        int calendarColumn = (x - days.x) / fullBoxWidth;
        if (!isLeftToRight) {
            int start = days.x + days.width;
            calendarColumn = (start - x) / fullBoxWidth;
        }
        if (monthView.isShowingWeekNumber()) {
            calendarColumn -= 1;
        }
        return new Point(calendarColumn, calendarRow - 1);
    }

    /**
     * Returns the given date's position in the grid of the month it is contained in.
     * 
     * @param date the Date to get the logical position for, must not be null.
     * @return the logical coordinates of the day in the grid of days in a
     *   month or null if the Date is not visible. 
     */
    protected Point getDayGridPosition(Date date) {
        if (!isVisible(date)) return null;
        Calendar calendar = getCalendar(date);
        Date startOfDay = CalendarUtils.startOfDay(calendar, date);
        // there must be a less ugly way?
        // columns
        CalendarUtils.startOfWeek(calendar);
        int column = 0;
        while (calendar.getTime().before(startOfDay)) {
            column++;
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        Date startOfWeek = CalendarUtils.startOfWeek(calendar, date);
        calendar.setTime(date);
        CalendarUtils.startOfMonth(calendar);
        int row = 0;
        while (calendar.getTime().before(startOfWeek)) {
            row++;
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }
        return new Point(column, row);
    }
    
    /**
     * Returns the Date at the given location. May be null if the
     * coordinates don't map to a day in the month which contains the 
     * coordinates. Specifically: hitting leading/trailing dates returns null.
     * 
     * Mapping pixel to calendar day.
     *
     * @param x the x position of the location in pixel
     * @param y the y position of the location in pixel
     * @return the day at the given location or null if the location
     *   doesn't map to a day in the month which contains the coordinates.
     */ 
    @Override
    public Date getDayAtLocation(int x, int y) {
        Point dayInGrid = getDayGridPositionAtLocation(x, y);
        if ((dayInGrid == null) || (dayInGrid.x < 0) || (dayInGrid.y < 0)) return null;
        Date month = getMonthAtLocation(x, y);
        return getDayInMonth(month, dayInGrid.y, dayInGrid.x);
    }
    
    /**
     * Returns the bounds of the given day.
     * The bounds are in monthView coordinate system.<p>
     * 
     * PENDING JW: this most probably should be public as it is the logical
     * reverse of getDayAtLocation <p>
     * 
     * @param date the Date to return the bounds for. Must not be null.
     * @return the bounds of the given date or null if not visible.
     */
    protected Rectangle getDayBounds(Date date) {
        if (!isVisible(date)) return null;
        Point position = getDayGridPosition(date);
        Rectangle monthBounds = getMonthBounds(date);
        monthBounds.y += getMonthHeaderHeight() + (position.y + 1) * fullBoxHeight;
        if (monthView.isShowingWeekNumber()) {
            position.x++;
        }
        if (isLeftToRight) {
           monthBounds.x += position.x * fullBoxWidth; 
        } else {
            int start = monthBounds.x + monthBounds.width - fullBoxWidth; 
            monthBounds.x = start - position.x * fullBoxWidth;
        }
        monthBounds.width = fullBoxWidth;
        monthBounds.height = fullBoxHeight;
        return monthBounds;
    }
    
    /**
     * Returns the bounds of the day box at logical coordinates in the given month.
     * The row's range is from DAY_HEADER to WEEKS_IN_MONTH - 1. Column's range is from
     * WEEK_HEADER to DAYS_IN_WEEK - 1.
     * 
     * @param month the month containing the day box  
     * @param row the logical row (== week) coordinate in the day grid 
     * @param column the logical column (== day) coordinate in the day grid
     * @return the bounds of the daybox or null if not showing
     * @throws IllegalArgumentException if row or column are out off range.
     */
    protected Rectangle getDayBoundsInMonth(Date month, int row, int column) {
        checkValidRow(row, column);
        if ((WEEK_HEADER_COLUMN == column) && !monthView.isShowingWeekNumber()) return null;
        Rectangle monthBounds = getMonthBounds(month);
        if (monthBounds == null) return null;
        monthBounds.y += getMonthHeaderHeight() + (row + 1) * fullBoxHeight;
        if (monthView.isShowingWeekNumber()) {
            column++;
        }
        if (isLeftToRight) {
           monthBounds.x += column * fullBoxWidth; 
        } else {
            int start = monthBounds.x + monthBounds.width - fullBoxWidth; 
            monthBounds.x = start - column * fullBoxWidth;
        }
        monthBounds.width = fullBoxWidth;
        monthBounds.height = fullBoxHeight;
        return monthBounds;
    }
    
    /**
     * @param row
     */
    private void checkValidRow(int row, int column) {
        if ((column < WEEK_HEADER_COLUMN) || (column >= DAYS_IN_WEEK)) 
            throw new IllegalArgumentException("illegal column in day grid " + column);
        if ((row < DAY_HEADER_ROW) || (row >= WEEKS_IN_MONTH)) 
            throw new IllegalArgumentException("illegal row in day grid" + row);
    }

    /**
     * Returns a boolean indicating if the given Date is visible. Trailing/leading
     * dates of the last/first displayed month are considered to be invisible.
     * 
     * @param date the Date to check for visibility. Must not be null.
     * @return true if the date is visible, false otherwise.
     */
    private boolean isVisible(Date date) {
        if (getFirstDisplayedDay().after(date) || getLastDisplayedDay().before(date)) return false;
        return true;
    }

    /**
     * Returns the Date defined by the logical 
     * grid coordinates relative to the given month. May be null if the
     * logical coordinates represent a header in the day grid or is outside of the
     * given month.
     * 
     * Mapping logical day grid coordinates to Date.<p>
     * 
     * PENDING JW: relax the startOfMonth pre? Why did I require it?
     * 
     * @param month a calendar representing the first day of the month, must not
     *   be null.
     * @param row the logical row index in the day grid of the month
     * @param column the logical column index in the day grid of the month
     * @return the day at the logical grid coordinates in the given month or null
     *    if the coordinates 
     * @throws IllegalStateException if the month is not the start of the month.   
     */
    protected Date getDayInMonth(Date month, int row, int column) {
        if ((row < 0) || (column < 0)) return null;
        Calendar calendar = getCalendar(month);
        int monthField = calendar.get(Calendar.MONTH);
        if (!CalendarUtils.isStartOfMonth(calendar))
            throw new IllegalStateException("calendar must be start of month but was: " + month.getTime());
        CalendarUtils.startOfWeek(calendar);
        calendar.add(Calendar.DAY_OF_MONTH, row * JXMonthView.DAYS_IN_WEEK + column);
        if (calendar.get(Calendar.MONTH) == monthField) {
            return calendar.getTime();
        } 
        return null;
        
    }
    
    
    // ------------------- mapping month parts 
 

    /**
     * Mapping pixel to bounds.<p>
     * 
     * PENDING JW: define the "action grid". Currently this replaces the old
     * version to remove all internal usage of deprecated methods.
     *  
     * @param x the x position of the location in pixel
     * @param y the y position of the location in pixel
     * @return the bounds of the active header area in containing the location
     *   or null if outside.
     */
    protected int getTraversableGridPositionAtLocation(int x, int y) {
        Rectangle headerBounds = getMonthHeaderBoundsAtLocation(x, y);
        if (headerBounds == null) return -1;
        if (y < headerBounds.y + arrowPaddingY) return -1;
        if (y > headerBounds.y + headerBounds.height - arrowPaddingY) return -1;
        headerBounds.setBounds(headerBounds.x + arrowPaddingX, y, 
                headerBounds.width - 2 * arrowPaddingX, headerBounds.height);
        if (!headerBounds.contains(x, y)) return -1;
        Rectangle hitArea = new Rectangle(headerBounds.x, headerBounds.y, monthUpImage.getIconWidth(), monthUpImage.getIconHeight());
        if (hitArea.contains(x, y)) {
            return isLeftToRight ? MONTH_DOWN : MONTH_UP;
        }
        hitArea.translate(headerBounds.width - monthUpImage.getIconWidth(), 0);
        if (hitArea.contains(x, y)) {
            return isLeftToRight ? MONTH_UP : MONTH_DOWN;
        } 
        return -1;
    }
    
    /**
     * Returns the bounds of the month header which contains the 
     * given location. The bounds are in monthView coordinate system.
     * 
     * <p>
     * 
     * @param x the x position of the location in pixel
     * @param y the y position of the location in pixel
     * @return the bounds of the month which contains the location, 
     *   or null if outside
     */
    protected Rectangle getMonthHeaderBoundsAtLocation(int x, int y) {
        Rectangle header = getMonthBoundsAtLocation(x, y);
        if (header == null) return null;
        header.height = getMonthHeaderHeight();
        return header;
    }
    
    /**
     * Returns the bounds of the month details which contains the 
     * given location. The bounds are in monthView coordinate system.
     * 
     * @param x the x position of the location in pixel
     * @param y the y position of the location in pixel
     * @return the bounds of the details grid in the month at
     *   location or null if outside.
     */
    private Rectangle getMonthDetailsBoundsAtLocation(int x, int y) {
        Rectangle month = getMonthBoundsAtLocation(x, y);
        if (month == null) return null;
        int startOfDaysY = month.y + getMonthHeaderHeight();
        if (y < startOfDaysY) return null;
        month.y = startOfDaysY;
        month.height = month.height - getMonthHeaderHeight();
        return month;
    }

    
    // ---------------------- mapping month coordinates    

    /**
      * Returns the bounds of the month which contains the 
     * given location. The bounds are in monthView coordinate system.
     * 
     * <p>
     * 
     * Mapping pixel to bounds.
     * 
     * @param x the x position of the location in pixel
     * @param y the y position of the location in pixel
     * @return the bounds of the month which contains the location, 
     *   or null if outside
     */
    protected Rectangle getMonthBoundsAtLocation(int x, int y) {
        if (!calendarGrid.contains(x, y)) return null;
        int calendarRow = (y - calendarGrid.y) / fullCalendarHeight;
        int calendarColumn = (x - calendarGrid.x) / fullCalendarWidth;
        return new Rectangle( 
                calendarGrid.x + calendarColumn * fullCalendarWidth,
                calendarGrid.y + calendarRow * fullCalendarHeight,
                calendarWidth, calendarHeight);
    }
    
    
    /**
     * 
     * Returns the logical coordinates of the month which contains
     * the given location. The p.x of the returned value represents the column, the
     * p.y represents the row the month is shown in. The transformation takes
     * care of ComponentOrientation. <p>
     * 
     * Mapping pixel to logical grid coordinates.
     * 
     * @param x the x position of the location in pixel
     * @param y the y position of the location in pixel
     * @return the logical coordinates of the month in the grid of month shown by
     *   this monthView or null if outside. 
     */
    protected Point getMonthGridPositionAtLocation(int x, int y) {
        if (!calendarGrid.contains(x, y)) return null;
        int calendarRow = (y - calendarGrid.y) / fullCalendarHeight;
        int calendarColumn = (x - calendarGrid.x) / fullCalendarWidth;
        if (!isLeftToRight) {
            int start = calendarGrid.x + calendarGrid.width;
            calendarColumn = (start - x) / fullCalendarWidth;
              
        }
        return new Point(calendarColumn, calendarRow);
    }

    /**
     * Returns the Date representing the start of the month which 
     * contains the given location.<p>
     * 
     * Mapping pixel to calendar day.
     *
     * @param x the x position of the location in pixel
     * @param y the y position of the location in pixel
     * @return the start of the month which contains the given location or 
     *    null if the location is outside the grid of months.
     */
    protected Date getMonthAtLocation(int x, int y) {
        Point month = getMonthGridPositionAtLocation(x, y);
        if (month ==  null) return null;
        return getMonth(month.y, month.x);
    }
    
    /**
     * Returns the Date representing the start of the month at the given 
     * logical position in the grid of months. <p>
     * 
     * Mapping logical grid coordinates to Calendar.
     * 
     * @param row the rowIndex in the grid of months.
     * @param column the columnIndex in the grid months.
     * @return a Date representing the start of the month at the given
     *   logical coordinates.
     *   
     * @see #getMonthGridPosition(Date)  
     */
    protected Date getMonth(int row, int column) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MONTH, 
                row * calendarColumnCount + column);
        return calendar.getTime();
        
    }

    /**
     * Returns the logical grid position of the month containing the given date.
     * The Point's x value is the column in the grid of months, the y value
     * is the row in the grid of months.
     * 
     * Mapping Date to logical grid position, this is the reverse of getMonth(int, int).
     * 
     * @param date the Date to return the bounds for. Must not be null.
     * @return the postion of the month that contains the given date or null if not visible.
     * 
     * @see #getMonth(int, int)
     * @see #getMonthBounds(int, int)
     */
    protected Point getMonthGridPosition(Date date) {
        if (!isVisible(date)) return null;
        // start of grid
        Calendar calendar = getCalendar();
        int firstMonth = calendar.get(Calendar.MONTH);
        int firstYear = calendar.get(Calendar.YEAR);
        
        // 
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        
        int diffMonths = month - firstMonth
            + ((year - firstYear) * JXMonthView.MONTHS_IN_YEAR);
        
        int row = diffMonths / calendarColumnCount;
        int column = diffMonths % calendarColumnCount;

        return new Point(column, row);
    }

    /**
     * Returns the bounds of the month at the given logical coordinates
     * in the grid of visible months.<p>
     * 
     * Mapping logical grip position to pixel.
     * 
     * @param row the rowIndex in the grid of months.
     * @param column the columnIndex in the grid months.
     * @return the bounds of the month at the given logical logical position.
     * 
     * @see #getMonthGridPositionAtLocation(int, int)
     * @see #getMonthBoundsAtLocation(int, int)
     */
    protected Rectangle getMonthBounds(int row, int column) {
        int startY = calendarGrid.y + row * fullCalendarHeight;
        int startX = calendarGrid.x + column * fullCalendarWidth;
        if (!isLeftToRight) {
            startX = calendarGrid.x + (calendarColumnCount - 1 - column) * fullCalendarWidth;
        }
        return new Rectangle(startX, startY, calendarWidth, calendarHeight);
    }

    /**
     * Returns the bounds of the month containing the given date.
     * The bounds are in monthView coordinate system.<p>
     * 
     * Mapping Date to pixel.
     * 
     * @param date the Date to return the bounds for. Must not be null.
     * @return the bounds of the month that contains the given date or null if not visible.
     * 
     * @see #getMonthAtLocation(int, int)
     */
    protected Rectangle getMonthBounds(Date date) {
        Point position = getMonthGridPosition(date);
        return position != null ? getMonthBounds(position.y, position.x) : null;
    }
    
    /**
     * Returns the bounds of the month containing the given date.
     * The bounds are in monthView coordinate system.<p>
     * 
     * Mapping Date to pixel.
     * 
     * @param date the Date to return the bounds for. Must not be null.
     * @return the bounds of the month that contains the given date or null if not visible.
     * 
     * @see #getMonthAtLocation(int, int)
     */
    protected Rectangle getMonthHeaderBounds(Date date, boolean includeInsets) {
        Point position = getMonthGridPosition(date);
        if (position == null) return null;
        Rectangle bounds = getMonthBounds(position.y, position.x);
        bounds.height = getMonthHeaderHeight();
        if (!includeInsets) {
            
        }
        return bounds;
    }


    //---------------- accessors for sizes
    
    /**
     * Returns the size of a month.
     * @return the size of a month.
     */
    protected Dimension getMonthSize() {
        return new Dimension(calendarWidth, calendarHeight);
    }
    
    /**
     * Returns the size of a day including the padding.
     * @return the size of a month.
     */
    protected Dimension getDaySize() {
        return new Dimension(fullBoxWidth, fullBoxHeight);
    }
    /**
     * Returns the height of the month header.
     * 
     * @return the height of the month header.
     */
    protected int getMonthHeaderHeight() {
        return fullMonthBoxHeight;
    }

    

    //-------------------  layout    
    
    /**
     * Called from layout: calculates properties
     * of grid of months.
     */
    private void calculateMonthGridLayoutProperties() {
        calculateMonthGridRowColumnCount();
        calculateMonthGridBounds();
    }
    
    /**
     * Calculates the bounds of the grid of months. 
     * 
     * CalendarRow/ColumnCount and calendarWidth/Height must be
     * initialized before calling this. 
     */
    private void calculateMonthGridBounds() {
        calendarGrid.setBounds(calculateCalendarGridX(), 
                calculateCalendarGridY(), 
                calculateCalendarGridWidth(), 
                calculateCalendarGridHeight());
    }


    private int calculateCalendarGridY() {
        return (monthView.getHeight() - calculateCalendarGridHeight()) / 2;
    }

    private int calculateCalendarGridX() {
        return (monthView.getWidth() - calculateCalendarGridWidth()) / 2; 
    }
    
    private int calculateCalendarGridHeight() {
        return ((calendarHeight * calendarRowCount) +
                (CALENDAR_SPACING * (calendarRowCount - 1 )));
    }

    private int calculateCalendarGridWidth() {
        return ((calendarWidth * calendarColumnCount) +
                (CALENDAR_SPACING * (calendarColumnCount - 1)));
    }

    /**
     * Calculates and updates the numCalCols/numCalRows that determine the
     * number of calendars that can be displayed. Updates the last displayed
     * date if appropriate.
     * 
     */
    private void calculateMonthGridRowColumnCount() {
        int oldNumCalCols = calendarColumnCount;
        int oldNumCalRows = calendarRowCount;

        calendarRowCount = 1;
        calendarColumnCount = 1;
        if (!isZoomable()) {
            // Determine how many columns of calendars we want to paint.
            int addColumns = (monthView.getWidth() - calendarWidth)
                    / (calendarWidth + CALENDAR_SPACING);
            // happens if used as renderer in a tree.. don't know yet why
            if (addColumns > 0) {
                calendarColumnCount += addColumns;
            }

            // Determine how many rows of calendars we want to paint.
            int addRows = (monthView.getHeight() - calendarHeight)
                    / (calendarHeight + CALENDAR_SPACING);
            if (addRows > 0) {
                calendarRowCount += addRows;
            }
        }
        if (oldNumCalCols != calendarColumnCount
                || oldNumCalRows != calendarRowCount) {
            updateLastDisplayedDay(getFirstDisplayedDay());
        }
    }

    /**
     * @return
     */
    protected boolean isZoomable() {
        return monthView.isZoomable();
    }


    

//-------------------- painting

    
    /**
     * Overridden to extract the background painting for ease-of-use of 
     * subclasses.
     */
    @Override
    public void update(Graphics g, JComponent c) {
        paintBackground(g);
        paint(g, c);
    }

    /**
     * Paints the background of the component. This implementation fill the
     * monthView's area with its background color if opaque, does nothing
     * if not opaque. Subclasses can override but must comply to opaqueness
     * contract.
     * 
     * @param g the Graphics to fill.
     * 
     */
    protected void paintBackground(Graphics g) {
        if (monthView.isOpaque()) {
            g.setColor(monthView.getBackground());
            g.fillRect(0, 0, monthView.getWidth(), monthView.getHeight());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        Rectangle clip = g.getClipBounds();
        if (!useRenderingHandler()) {
            paintBackground(clip, g);
        }
        // Get a calender set to the first displayed date
        Calendar cal = getCalendar();
        // loop through grid of months
        for (int row = 0; row < calendarRowCount; row++) {
            for (int column = 0; column < calendarColumnCount; column++) {
                // get the bounds of the current month.
                Rectangle bounds = getMonthBounds(row, column);
                // Check if this row falls in the clip region.
                if (bounds.intersects(clip)) {
                    if (!useRenderingHandler()) {
                        paintMonth(g, bounds.x, bounds.y, bounds.width, bounds.height, cal);
                    } else {
                        paintMonth(g, cal);
                    }
                }
                cal.add(Calendar.MONTH, 1);
            }
        }

    }

    /**
     * Paints the month represented by the given Calendar.
     * 
     * @param g the graphics to paint into
     * @param calendar the calendar representing the month to paint.
     */
    protected void paintMonth(Graphics g, Calendar calendar) {
        paintMonthHeader(g, calendar);
        paintDayHeader(g, calendar);
        paintWeekHeader(g, calendar);
        paintDays(g, calendar);
    }

    /**
     * Paints the header of a month.
     * 
     * @param g the graphics to paint into
     * @param calendar the calendar representing the the month to paint, must
     *   not be null
     */
    protected void paintMonthHeader(Graphics g, Calendar calendar) {
        Rectangle page = getMonthHeaderBounds(calendar.getTime(), false);
        paintDayOfMonth(g, page, calendar, CalendarState.TITLE);
//        JComponent comp = renderingHandler.prepareRenderingComponent(monthView,
//                calendar, CalendarState.TITLE);
//        renderComponentAt(g, comp, page);
    }

    /**
     * Paints the day column header.
     * 
     * @param g the graphics to paint into
     * @param calendar the calendar representing the the month to paint, must
     *   not be null
     */
    protected void paintDayHeader(Graphics g, Calendar calendar) {
        paintDaysOfWeekSeparator(g, calendar);
        Calendar cal = (Calendar) calendar.clone();
        CalendarUtils.startOfWeek(cal);
        for (int i = 0; i < JXMonthView.DAYS_IN_WEEK; i++) {
            Rectangle dayBox = getDayBoundsInMonth(calendar.getTime(), DAY_HEADER_ROW, i);
            paintDayOfMonth(g, dayBox, cal, CalendarState.DAY_OF_WEEK);
//            JComponent comp = renderingHandler.prepareRenderingComponent(monthView, cal, CalendarState.DAY_OF_WEEK);
//            renderComponentAt(g, comp, dayBox);
            cal.add(Calendar.DATE, 1);
        }
    }

    /**
     * Paints the day column header.
     * 
     * @param g the graphics to paint into
     * @param calendar the calendar representing the the month to paint, must
     *   not be null
     */
    protected void paintWeekHeader(Graphics g, Calendar cal) {
        if (!monthView.isShowingWeekNumber())
            return;
        paintWeekOfYearSeparator(g, cal);
    
        Calendar calendar = (Calendar) cal.clone();
        int weeks = getWeeks(calendar);
        calendar.setTime(cal.getTime());
        for (int week = 0; week <= weeks; week++) {
            Rectangle dayBox = getDayBoundsInMonth(cal.getTime(), week, WEEK_HEADER_COLUMN);
            paintDayOfMonth(g, dayBox, calendar, CalendarState.WEEK_OF_YEAR);
//            JComponent comp = renderingHandler.prepareRenderingComponent(monthView, calendar, CalendarState.WEEK_OF_YEAR);
//            renderComponentAt(g, comp, dayBox);
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }
    }

    /**
     * Paints the days of the given month.
     * 
     * @param g the graphics to paint into
     * @param calendar the calendar representing the the month to paint, must
     *   not be null
     */
    protected void paintDays(Graphics g, Calendar cal) {
        Calendar calendar = (Calendar) cal.clone();
        CalendarUtils.startOfMonth(calendar);
        Date startOfMonth = calendar.getTime();
        CalendarUtils.endOfMonth(calendar);
        Date endOfMonth = calendar.getTime();
        // reset the clone
        calendar.setTime(cal.getTime());
        // adjust to start of week
        calendar.setTime(cal.getTime());
        CalendarUtils.startOfWeek(calendar);
        for (int week = 0; week < WEEKS_IN_MONTH; week++) {
            for (int day = 0; day < 7; day++) {
                CalendarState state = null;
                if (calendar.getTime().before(startOfMonth)) {
                    if (monthView.isShowingLeadingDays()) {
                        state = CalendarState.LEADING;
                    }
                } else if (calendar.getTime().after(endOfMonth)) {
                    if (monthView.isShowingTrailingDays()) {
                        state = CalendarState.TRAILING;
                    }

                } else {
                    state = isToday(calendar.getTime()) ? CalendarState.TODAY : CalendarState.IN_MONTH;
                }
                if (state != null) {
                    Rectangle bounds = getDayBoundsInMonth(startOfMonth, week, day);
                    paintDayOfMonth(g, bounds, calendar, state);
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
    }


    /**
     * Paints a day which is of the current month with the given state.<p>
     * 
     * PENDING JW: mis-nomer - this is in fact called for rendering any day-related
     * state (including weekOfYear, dayOfWeek headers) and for rendering
     * the month header as well, that is from everywhere.
     *  Rename to paintSomethingGeneral. Think about impact for subclasses 
     *  (what do they really need? feedback please!)
     * 
     * @param g the graphics to paint into.
     * @param bounds the rectangle to paint the day into
     * @param calendar the calendar representing the day to paint
     * @param state the calendar state
     */
    protected void paintDayOfMonth(Graphics g, Rectangle bounds, Calendar calendar, CalendarState state) {
        JComponent comp = renderingHandler.prepareRenderingComponent(monthView, calendar, 
                state);
        rendererPane.paintComponent(g, comp, monthView, bounds.x, bounds.y,
                bounds.width, bounds.height, true);
    }

    /**
     * Paints the separator between row header (weeks of year) and days.
     * 
     * @param g the Graphics to paint into
     * @param cal the calendar representing the month
     */
    protected void paintWeekOfYearSeparator(Graphics g, Calendar cal) {
        Rectangle r = getSeparatorBounds(cal, 0, WEEK_HEADER_COLUMN);
        if (r == null) return;
        g.setColor(monthView.getForeground());
        g.drawLine(r.x, r.y, r.x, r.y + r.height);
    }

    /**
     * Paints the separator between column header (days of week) and days.
     * 
     * @param g the Graphics to paint into
     * @param cal the calendar representing the month
     */
    protected void paintDaysOfWeekSeparator(Graphics g, Calendar cal) {
        Rectangle r = getSeparatorBounds(cal, DAY_HEADER_ROW, 0);
        if (r == null) return;
        g.setColor(monthView.getForeground());
        g.drawLine(r.x, r.y, r.x + r.width, r.y);
    }
    /**
     * @param cal
     * @param row
     * @param column
     * @return
     */
    private Rectangle getSeparatorBounds(Calendar cal, int row, int column) {
        Rectangle separator = getDayBoundsInMonth(cal.getTime(), row, column);
        if (separator == null) return null;
        if (column < 0) {
            separator.height *= WEEKS_IN_MONTH;
            if (isLeftToRight) {
                separator.x += separator.width - 1;
            }
            separator.width = 1;
        } else if (row < 0) {
            int oldWidth = separator.width;
            separator.width *= DAYS_IN_WEEK;
            if (!isLeftToRight) {
                separator.x -= separator.width - oldWidth;
            }
            separator.y += separator.height - 1;
            separator.height = 1;
        }
        return separator;
    }

    /**
     * Returns the number of weeks to paint in the current month, as represented
     * by the given calendar.
     * 
     * Note: the given calendar must not be changed.
     * 
     * @param month the calendar specifying the the first day of the month to
     *        paint, must not be null
     * @return the number of weeks of this month.
     */
    protected int getWeeks(Calendar month) {
        Date old = month.getTime();
        CalendarUtils.startOfWeek(month);
        int firstWeek = month.get(Calendar.WEEK_OF_YEAR);
        month.setTime(old);
        CalendarUtils.endOfMonth(month);
        int lastWeek = month.get(Calendar.WEEK_OF_YEAR);
        if (lastWeek < firstWeek) {
            lastWeek = month.getActualMaximum(Calendar.WEEK_OF_YEAR) + 1;
        }
        month.setTime(old);
        return lastWeek - firstWeek;
    }


    /**
     * Returns a boolean indicating whether to use the rendering mechanism
     * instead of drawing the text manually. If true, it's safe to access the
     * renderingHandler.
     * <p>
     * 
     * Note: (mostly) for debugging the rendering mechanism can be disabled per
     * monthView instance. To do so, set a client property with key
     * "disableRendering" to Boolean.TRUE.
     * <p>
     * 
     * @return a boolean indicating whether to use the renderer mechanism.
     */
    private boolean useRenderingHandler() {
        if (Boolean.TRUE.equals(monthView.getClientProperty("disableRendering"))) return false;
        return renderingHandler != null;
    }

    private void traverseMonth(int arrowType) {
        if (arrowType == MONTH_DOWN) {
            previousMonth();
        } else if (arrowType == MONTH_UP) {
            nextMonth();
        }
    }

    private void nextMonth() {
        Date upperBound = monthView.getUpperBound();
        if (upperBound == null
                || upperBound.after(getLastDisplayedDay()) ){
            Calendar cal = getCalendar();
            cal.add(Calendar.MONTH, 1);
            monthView.setFirstDisplayedDay(cal.getTime());
        }
    }

    private void previousMonth() {
        Date lowerBound = monthView.getLowerBound();
        if (lowerBound == null
                || lowerBound.before(getFirstDisplayedDay())){
            Calendar cal = getCalendar();
            cal.add(Calendar.MONTH, -1);
            monthView.setFirstDisplayedDay(cal.getTime());
        }
    }

//--------------------------- displayed dates, calendar

    
    /**
     * Returns the monthViews calendar configured to the firstDisplayedDate.
     * 
     * NOTE: it's safe to change the calendar state without resetting because
     * it's JXMonthView's responsibility to protect itself.
     * 
     * @return the monthView's calendar, configured with the firstDisplayedDate.
     */
    protected Calendar getCalendar() {
        return getCalendar(getFirstDisplayedDay());
    }
    
    /**
     * Returns the monthViews calendar configured to the given time.
     * 
     * NOTE: it's safe to change the calendar state without resetting because
     * it's JXMonthView's responsibility to protect itself.
     * 
     * @param date the date to configure the calendar with
     * @return the monthView's calendar, configured with the given date.
     */
    protected Calendar getCalendar(Date date) {
        Calendar calendar = monthView.getCalendar();
        calendar.setTime(date);
        return calendar;
    }

    

    /**
     * Updates the lastDisplayedDate property based on the given first and 
     * visible # of months.
     * 
     * @param first the date of the first visible day.
     */
    private void updateLastDisplayedDay(Date first) {
        Calendar cal = getCalendar(first);
        cal.add(Calendar.MONTH, ((calendarColumnCount * calendarRowCount) - 1));
        CalendarUtils.endOfMonth(cal);
        lastDisplayedDate = cal.getTime();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastDisplayedDay() {
        return lastDisplayedDate;
    }

    /*-------------- refactored: encapsulate aliased fields
     */

    /**
     * Sets the firstDisplayedDate property to the given value. Must update
     * dependent state as well. <p>
     * 
     * Here: updated lastDisplayedDatefirstDisplayedMonth/Year accordingly.
     * <p>
     * 
     * PENDING JW: remove call to repaint() because this method is used
     * both at install and from propertyChange
     * 
     * @param firstDisplayedDate the firstDisplayedDate to set
     */
    protected void setFirstDisplayedDay(Date firstDisplayedDate) {
        Calendar calendar = getCalendar(firstDisplayedDate);
        this.firstDisplayedDate = firstDisplayedDate;
        this.firstDisplayedMonth = calendar.get(Calendar.MONTH);
        this.firstDisplayedYear = calendar.get(Calendar.YEAR);
        updateLastDisplayedDay(firstDisplayedDate);
//        monthView.repaint();
    }
    /**
     * @return the firstDisplayedDate
     */
    protected Date getFirstDisplayedDay() {
        return firstDisplayedDate != null ? firstDisplayedDate : monthView.getFirstDisplayedDay();
    }

    /**
     * @return the firstDisplayedMonth
     */
    protected int getFirstDisplayedMonth() {
        return firstDisplayedMonth;
    }


    /**
     * @return the firstDisplayedYear
     */
    protected int getFirstDisplayedYear() {
        return firstDisplayedYear;
    }


    /**
     * @return the selection
     */
    protected SortedSet<Date> getSelection() {
        return monthView.getSelection();
    }
    
    
    /**
     * @return the start of today.
     */
    protected Date getToday() {
        return monthView.getToday();
    }

    /**
     * Returns true if the date passed in is the same as today.
     *
     * PENDING JW: really want the exact test?
     * 
     * @param date long representing the date you want to compare to today.
     * @return true if the date passed is the same as today.
     */
    protected boolean isToday(Date date) {
        return date.equals(getToday());
    }
    

//-----------------------end encapsulation
 
    
//------------------ Handler implementation 
//  
    /**
     * temporary: removed SelectionMode.NO_SELECTION, replaced
     * all access by this method to enable easy re-adding, if we want it.
     * If not - remove.
     */
    private boolean canSelectByMode() {
        return true;
    }
    

    private class Handler implements  
        MouseListener, MouseMotionListener, LayoutManager,
            PropertyChangeListener, DateSelectionListener {
        private boolean armed;
        private Date startDate;
        private Date endDate;

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
                int arrowType = getTraversableGridPositionAtLocation(e.getX(), e.getY());
                if (arrowType != -1) {
                    traverseMonth(arrowType);
                    return;
                }
            }

            if (!canSelectByMode()) {
                return;
            }

            Date cal = getDayAtLocation(e.getX(), e.getY());
            if (cal == null) {
                return;
            }

            // Update the selected dates.
            startDate = cal;
            endDate = cal;

            if (monthView.getSelectionMode() == SelectionMode.SINGLE_INTERVAL_SELECTION ||
//                    selectionMode == SelectionMode.WEEK_INTERVAL_SELECTION ||
                    monthView.getSelectionMode() == SelectionMode.MULTIPLE_INTERVAL_SELECTION) {
                pivotDate = startDate;
            }

            monthView.getSelectionModel().setAdjusting(true);
            
            if (monthView.getSelectionMode() == SelectionMode.MULTIPLE_INTERVAL_SELECTION && e.isControlDown()) {
                monthView.addSelectionInterval(startDate, endDate);
            } else {
                monthView.setSelectionInterval(startDate, endDate);
            }

            // Arm so we fire action performed on mouse release.
            armed = true;
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
            
            if (armed) {
                monthView.commitSelection();
            }
            armed = false;
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}

        public void mouseDragged(MouseEvent e) {
            // If we were using the keyboard we aren't anymore.
            setUsingKeyboard(false);
            if (!monthView.isEnabled() || !canSelectByMode()) {
                return;
            }
            Date cal = getDayAtLocation(e.getX(), e.getY());
            if (cal == null) {
                return;
            }

            Date selected = cal;
            Date oldStart = startDate;
            Date oldEnd = endDate;

            if (monthView.getSelectionMode() == SelectionMode.SINGLE_SELECTION) {
                if (selected.equals(oldStart)) {
                    return;
                }
                startDate = selected;
                endDate = selected;
            } else {
                if (selected.before(pivotDate)) {
                    startDate = selected;
                    endDate = pivotDate;
                } else if (selected.after(pivotDate)) {
                    startDate = pivotDate;
                    endDate = selected;
                }
            }

            if (startDate.equals(oldStart) && endDate.equals(oldEnd)) {
                return;
            }

            if (monthView.getSelectionMode() == SelectionMode.MULTIPLE_INTERVAL_SELECTION && e.isControlDown()) {
                monthView.addSelectionInterval(startDate, endDate);
            } else {
                monthView.setSelectionInterval(startDate, endDate);
            }

            // Set trigger.
            armed = true;
        }

        public void mouseMoved(MouseEvent e) {}

//------------------------ layout
        
        
        private Dimension preferredSize = new Dimension();

        public void addLayoutComponent(String name, Component comp) {}

        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            layoutContainer(parent);
            return new Dimension(preferredSize);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        public void layoutContainer(Container parent) {
            // handle backward compatibility until the deprecated methods are
            // removed
            if (!useRenderingHandler()) {
                layoutContainerFM(parent);
                return;
            }

            int maxMonthWidth = 0;
            int maxMonthHeight = 0;
            Calendar calendar = getCalendar();
            for (int i = calendar.getMinimum(Calendar.MONTH); i <= calendar.getMaximum(Calendar.MONTH); i++) {
                calendar.set(Calendar.MONTH, i);
                CalendarUtils.startOfMonth(calendar);
                JComponent comp = renderingHandler.prepareRenderingComponent(monthView, calendar, CalendarState.TITLE);
                Dimension pref = comp.getPreferredSize();
                maxMonthWidth = Math.max(maxMonthWidth, pref.width);
                maxMonthHeight = Math.max(maxMonthHeight, pref.height);
            }
            
            int maxBoxWidth = 0;
            int maxBoxHeight = 0;
            calendar = getCalendar();
            CalendarUtils.startOfWeek(calendar);
            for (int i = 0; i < JXMonthView.DAYS_IN_WEEK; i++) {
                JComponent comp = renderingHandler.prepareRenderingComponent(monthView, calendar, CalendarState.DAY_OF_WEEK);
                Dimension pref = comp.getPreferredSize();
                maxBoxWidth = Math.max(maxBoxWidth, pref.width);
                maxBoxHeight = Math.max(maxBoxHeight, pref.height);
                calendar.add(Calendar.DATE, 1);
            }
            
            calendar = getCalendar();
            for (int i = 0; i < calendar.getMaximum(Calendar.DAY_OF_MONTH); i++) {
                JComponent comp = renderingHandler.prepareRenderingComponent(monthView, calendar, CalendarState.IN_MONTH);
                Dimension pref = comp.getPreferredSize();
                maxBoxWidth = Math.max(maxBoxWidth, pref.width);
                maxBoxHeight = Math.max(maxBoxHeight, pref.height);
                calendar.add(Calendar.DATE, 1);
            }
            
            // PENDING JW: currently doesn't handle monthHeader pref > sum of box widths
            fullBoxWidth = maxBoxWidth;
            fullBoxHeight = maxBoxHeight;
            boxWidth = maxBoxWidth - 2 * monthView.getBoxPaddingX();
            boxHeight = maxBoxHeight - 2 * monthView.getBoxPaddingY();

            monthBoxHeight = Math.max(boxHeight, maxMonthHeight);
            fullMonthBoxHeight = monthBoxHeight ; 

            // Keep track of calendar width and height for use later.
            calendarWidth = fullBoxWidth * JXMonthView.DAYS_IN_WEEK;
            if (monthView.isShowingWeekNumber()) {
                calendarWidth += fullBoxWidth;
            }
            fullCalendarWidth = calendarWidth + CALENDAR_SPACING;
            
            calendarHeight = (fullBoxHeight * 7) + fullMonthBoxHeight;
            fullCalendarHeight = calendarHeight + CALENDAR_SPACING;
            // Calculate minimum width/height for the component.
            int prefRows = getPreferredRows();
            preferredSize.height = (calendarHeight * prefRows) +
                    (CALENDAR_SPACING * (prefRows - 1));

            int prefCols = getPreferredColumns();
            preferredSize.width = (calendarWidth * prefCols) +
                    (CALENDAR_SPACING * (prefCols - 1));

            // Add insets to the dimensions.
            Insets insets = monthView.getInsets();
            preferredSize.width += insets.left + insets.right;
            preferredSize.height += insets.top + insets.bottom;
           
            calculateMonthGridLayoutProperties();
            
            if (isZoomable()) {
                calendarHeader.setBounds(getMonthHeaderBounds(monthView.getFirstDisplayedDay(), false));
            }
        }

        /**
         * @return
         */
        private int getPreferredColumns() {
            return isZoomable() ? 1 : monthView.getPreferredColumnCount();
        }

        /**
         * @return
         */
        private int getPreferredRows() {
            return isZoomable() ? 1 : monthView.getPreferredRowCount();
        }

        /**
         * Old style layout - not using the renderer.
         * 
         * @deprecated no longer used in paint/layout with renderer.
         */
        @Deprecated
        protected void layoutContainerFM(Container parent) {
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
            FontMetrics fm = monthView.getFontMetrics(derivedFont);
            // JW PENDING: relies on calendar being set at least to year?
            // No, just on the bare calendar - so don't care about actual time
            Calendar cal = getCalendar();
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
            boxWidth = 0;
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
            boolean showingWeekNumber = monthView.isShowingWeekNumber();
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
            preferredSize.width = (boxWidth + (2 * boxPaddingX)) * JXMonthView.DAYS_IN_WEEK;
            if (preferredSize.width < longestMonthWidth) {
                double diff = longestMonthWidth - preferredSize.width;
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
            fullCalendarWidth = calendarWidth + CALENDAR_SPACING;
            
            calendarHeight = (fullBoxHeight * 7) + fullMonthBoxHeight;
            fullCalendarHeight = calendarHeight + CALENDAR_SPACING;
            // Calculate minimum width/height for the component.
            int prefRows = getPreferredRows();
            preferredSize.height = (calendarHeight * prefRows) +
                    (CALENDAR_SPACING * (prefRows - 1));

            int prefCols = getPreferredColumns();
            preferredSize.width = (calendarWidth * prefCols) +
                    (CALENDAR_SPACING * (prefCols - 1));

            // Add insets to the dimensions.
            Insets insets = monthView.getInsets();
            preferredSize.width += insets.left + insets.right;
            preferredSize.height += insets.top + insets.bottom;
           
            calculateMonthGridLayoutProperties();
        }


        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();

            if ("componentOrientation".equals(property)) {
                isLeftToRight = monthView.getComponentOrientation().isLeftToRight();
                monthView.revalidate();
            } else if (JXMonthView.SELECTION_MODEL.equals(property)) {
                DateSelectionModel selectionModel = (DateSelectionModel) evt.getOldValue();
                selectionModel.removeDateSelectionListener(getHandler());
                selectionModel = (DateSelectionModel) evt.getNewValue();
                selectionModel.addDateSelectionListener(getHandler());
            } else if ("firstDisplayedDay".equals(property)) {
                setFirstDisplayedDay(((Date) evt.getNewValue()));
                monthView.repaint();
            } else if (JXMonthView.BOX_PADDING_X.equals(property) 
                    || JXMonthView.BOX_PADDING_Y.equals(property) 
                    || JXMonthView.TRAVERSABLE.equals(property) 
                    || JXMonthView.DAYS_OF_THE_WEEK.equals(property) 
                    || "border".equals(property) 
                    || "showingWeekNumber".equals(property)
                    || "traversable".equals(property) 
                   
                    ) {
                monthView.revalidate();
                monthView.repaint();
            } else if ("zoomable".equals(property)) {
                updateZoomable();
            } else if ("font".equals(property)) {
                derivedFont = createDerivedFont();
                calendarHeader.setFont(getAsNotUIResource(createDerivedFont()));
                monthView.revalidate();
            } else if ("componentInputMapEnabled".equals(property)) {
                updateComponentInputMap();
            } else if ("locale".equals(property)) { // "locale" is bound property
                updateLocale(true);
            } else if ("timeZone".equals(property)) {
                dayOfMonthFormatter.setTimeZone((TimeZone) evt.getNewValue());
//            } else if ("flaggedDates".equals(property)
//                || "showingTrailingDays".equals(property)
//                || "showingLeadingDays".equals(property)
//                || "today".equals(property)
//                || "antialiased".equals(property)
//                || "selectionBackground".equals(property)
//                || "selectionForeground".equals(property)
//                || "flaggedDayForeground".equals(property)
//                
//                ) {
//                monthView.repaint();
                // too many properties, simply repaint always (waiting for complaints ;-)
            } else {
                monthView.repaint();
//                LOG.info("got propertyChange:" + property);
            }
        }

        public void valueChanged(DateSelectionEvent ev) {
            monthView.repaint();
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
            if (!canSelectByMode())
                return;
            if (!isUsingKeyboard()) {
                originalDateSpan = getSelection();
            }
            // JW: removed the isUsingKeyboard from the condition
            // need to fire always.
            if (action >= ACCEPT_SELECTION && action <= CANCEL_SELECTION) { 
                // refactor the logic ...
                if (action == CANCEL_SELECTION) {
                    // Restore the original selection.
                    if ((originalDateSpan != null)
                            && !originalDateSpan.isEmpty()) {
                        monthView.setSelectionInterval(
                                originalDateSpan.first(), originalDateSpan
                                        .last());
                    } else {
                        monthView.clearSelection();
                    }
                    monthView.cancelSelection();
                } else {
                    // Accept the keyboard selection.
                    monthView.commitSelection();
                }
                setUsingKeyboard(false);
            } else if (action >= SELECT_PREVIOUS_DAY
                    && action <= SELECT_DAY_NEXT_WEEK) {
                setUsingKeyboard(true);
                monthView.getSelectionModel().setAdjusting(true);
                pivotDate = null;
                traverse(action);
            } else if (monthView.getSelectionMode() == SelectionMode.SINGLE_INTERVAL_SELECTION
                    && action >= ADJUST_SELECTION_PREVIOUS_DAY
                    && action <= ADJUST_SELECTION_NEXT_WEEK) {
                setUsingKeyboard(true);
                monthView.getSelectionModel().setAdjusting(true);
                addToSelection(action);
            }
        }

        private void traverse(int action) {
            Date oldStart = monthView.isSelectionEmpty() ? 
                    monthView.getToday() : monthView.getFirstSelectionDate();
            Calendar cal = getCalendar(oldStart);
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

            Date newStartDate = cal.getTime();
            if (!newStartDate.equals(oldStart)) {
                monthView.setSelectionInterval(newStartDate, newStartDate);
                monthView.ensureDateVisible(newStartDate);
            }
        }

        /**
         * If we are in a mode that allows for range selection this method
         * will extend the currently selected range.
         *
         * NOTE: This may not be the expected behavior for the keyboard controls
         * and we ay need to update this code to act in a way that people expect.
         *
         * @param action action for adjusting selection
         */
        private void addToSelection(int action) {
            Date newStartDate;
            Date newEndDate;
            Date selectionStart;
            Date selectionEnd;
            if (!monthView.isSelectionEmpty()) {
                newStartDate = selectionStart = monthView.getFirstSelectionDate();
                newEndDate = selectionEnd = monthView.getLastSelectionDate();
            } else {
                newStartDate = selectionStart = monthView.getToday();
                newEndDate = selectionEnd = newStartDate;
            }

            if (pivotDate == null) {
                pivotDate = newStartDate;
            }

            boolean isForward = true;
            // want a copy to play with - each branch sets and reads the time
            // actually don't care about the pre-set time.
            Calendar cal = getCalendar();
            switch (action) {
                case ADJUST_SELECTION_PREVIOUS_DAY:
                    if (!newEndDate.after(pivotDate)) {
                        cal.setTime(newStartDate);
                        cal.add(Calendar.DAY_OF_MONTH, -1);
                        newStartDate = cal.getTime();
                    } else {
                        cal.setTime(newEndDate);
                        cal.add(Calendar.DAY_OF_MONTH, -1);
                        newEndDate = cal.getTime();
                    }
                    isForward = false;
                    break;
                case ADJUST_SELECTION_NEXT_DAY:
                    if (!newStartDate.before(pivotDate)) {
                        cal.setTime(newEndDate);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        newStartDate = pivotDate;
                        newEndDate = cal.getTime();
                    } else {
                        cal.setTime(newStartDate);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        newStartDate = cal.getTime();
                    }
                    break;
                case ADJUST_SELECTION_PREVIOUS_WEEK:
                    if (!newEndDate.after(pivotDate)) {
                        cal.setTime(newStartDate);
                        cal.add(Calendar.DAY_OF_MONTH, -JXMonthView.DAYS_IN_WEEK);
                        newStartDate = cal.getTime();
                    } else {
                        cal.setTime(newEndDate);
                        cal.add(Calendar.DAY_OF_MONTH, -JXMonthView.DAYS_IN_WEEK);
                        Date newTime = cal.getTime();
                        if (!newTime.after(pivotDate)) {
                            newStartDate = newTime;
                            newEndDate = pivotDate;
                        } else {
                            newEndDate = cal.getTime();
                        }

                    }
                    isForward = false;
                    break;
                case ADJUST_SELECTION_NEXT_WEEK:
                    if (!newStartDate.before(pivotDate)) {
                        cal.setTime(newEndDate);
                        cal.add(Calendar.DAY_OF_MONTH, JXMonthView.DAYS_IN_WEEK);
                        newEndDate = cal.getTime();
                    } else {
                        cal.setTime(newStartDate);
                        cal.add(Calendar.DAY_OF_MONTH, JXMonthView.DAYS_IN_WEEK);
                        Date newTime = cal.getTime();
                        if (!newTime.before(pivotDate)) {
                            newStartDate = pivotDate;
                            newEndDate = newTime;
                        } else {
                            newStartDate = cal.getTime();
                        }
                    }
                    break;
            }
            if (!newStartDate.equals(selectionStart) || !newEndDate.equals(selectionEnd)) {
                monthView.setSelectionInterval(newStartDate, newEndDate);
                monthView.ensureDateVisible(isForward ? newEndDate  : newStartDate);
            }

        }
        

    }

//--------------------- deprecated painting api
//--------------------- this is still serviced (if a ui doesn't install a renderingHandler)
//--------------------- but no longer actively maintained    


    /**
     * Create a derived font used to when painting various pieces of the
     * month view component.  This method will be called whenever
     * the font on the component is set so a new derived font can be created.
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected Font createDerivedFont() {
        return monthView.getFont().deriveFont(Font.BOLD);
    }
    


    /**
     * 
     */
    protected void updateZoomable() {
        if (monthView.isZoomable()) {
            monthView.add(calendarHeader);
        } else {
            monthView.remove(calendarHeader);
        }
        monthView.revalidate();
        monthView.repaint();
    }

    protected BasicCalendarHeader createCalendarHeader() {
        BasicCalendarHeader header = new BasicCalendarHeader();
        return header;
    }

    /**
     * @param header
     */
    private void installHeaderActions() {
        final StringValue tsv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Calendar) {
                    String month = monthsOfTheYear[((Calendar) value)
                            .get(Calendar.MONTH)];
                    return month + " "
                            + ((Calendar) value).get(Calendar.YEAR); 
                }
                return StringValues.TO_STRING.getString(value);
            }

        };
        final AbstractActionExt zoomOut = new AbstractActionExt(tsv.getString(getCalendar())) {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        }; 
        AbstractActionExt prev = new AbstractActionExt(null, monthDownImage) {

            public void actionPerformed(ActionEvent e) {
                previousMonth();
                zoomOut.setName(tsv.getString(getCalendar()));
                
            }
            
        };
        AbstractActionExt next = new AbstractActionExt(null, monthUpImage) {

            public void actionPerformed(ActionEvent e) {
                nextMonth();
                zoomOut.setName(tsv.getString(getCalendar()));
                
            }
            
        };
        calendarHeader.setActions(prev, next, zoomOut);
    }

    /**
     * Paints a month.  It is assumed the given calendar is already set to the
     * first day of the month to be painted.<p>
     * 
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object.
     * @param left x location of month
     * @param top y location of month
     * @param width width of month
     * @param height height of month
     * @param calendar the calendar specifying the the first day of the month to paint, 
     *  must not be null
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintMonth(Graphics g, int left, int top, int width, int height, Calendar calendar) {
        paintMonthHeader(g, left, top, width, calendar);
    
        paintDaysOfTheWeek(g, left, top, width, calendar);
        // new top is below monthBox and daysOfWeek header
        int yNew = top + fullMonthBoxHeight + fullBoxHeight;
        // paint the column of week numbers
        paintWeeksOfYear(g, left, yNew, width, calendar);
    
        int xOffset = 0;
        if (monthView.isShowingWeekNumber()) {
            xOffset = fullBoxWidth;
        }
        if (isLeftToRight) {
            paintDays(g, left + xOffset, yNew, width - xOffset, calendar);
        } else {
            paintDays(g, left , yNew, width - xOffset, calendar);
        }
    
    }

    /**
     * Paints the header of a month. It is assumed the given calendar is already
     * set to the first day of the month to be painted.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     * 
     * @param g Graphics object.
     * @param x x location of month
     * @param y y location of month
     * @param width width of month
     * @param height height of month
     * @param calendar the calendar specifying the the first day of the month to
     *        paint, must not be null
     * @deprecated no longer used in paint/layout with renderer.
     * 
     */
    @Deprecated
    protected void paintMonthHeader(Graphics g, int x, int y, int width,
            Calendar calendar) {
        // Paint month name background.
        paintMonthStringBackground(g, x, y, width, fullMonthBoxHeight, calendar);
    
        paintMonthStringForeground(g, x, y, width, fullMonthBoxHeight, calendar);
    
        if (monthView.isTraversable()) {
            // draw the icons
            monthDownImage.paintIcon(monthView, g, x + arrowPaddingX,
                    y
                            + ((fullMonthBoxHeight - monthDownImage
                                    .getIconHeight()) / 2));
            monthUpImage.paintIcon(monthView, g, x + width - arrowPaddingX
                    - monthUpImage.getIconWidth(),
                    y
                            + ((fullMonthBoxHeight - monthDownImage
                                    .getIconHeight()) / 2));
        }
    }

    /**
     * Paints the background of the month string.  The bounding box for this
     * background can be modified by setting its insets via
     * setMonthStringInsets.  The color of the background can be set via
     * setMonthStringBackground.
     *
     * PENDING JW: switch over to use renderer.
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object to paint to.
     * @param x x-coordinate of upper left corner.
     * @param y y-coordinate of upper left corner.
     * @param width width of the bounding box.
     * @param height height of the bounding box.
     * 
     * @see org.jdesktop.swingx.JXMonthView#setMonthStringBackground
     * @see org.jdesktop.swingx.JXMonthView#setMonthStringInsets
     * 
     * @deprecated
     */
    @Deprecated
    protected void paintMonthStringBackground(Graphics g, int x, int y,
                                              int width, int height, Calendar cal) {
        // Modify bounds by the month string insets.
        Insets monthStringInsets = monthView.getMonthStringInsets();
        x = isLeftToRight ? x + monthStringInsets.left : x + monthStringInsets.right;
        y = y + monthStringInsets.top;
        width = width - monthStringInsets.left - monthStringInsets.right;
        height = height - monthStringInsets.top - monthStringInsets.bottom;
    
        g.setColor(monthView.getMonthStringBackground());
        g.fillRect(x, y, width, height);
    }

    /**
     * 
     * PENDING JW: switch over to use renderer.
     * 
     * Note: the given calendar must not be changed.
     * 
     * @param g Graphics object to paint to.
     * @param x x-coordinate of upper left corner.
     * @param y y-coordinate of upper left corner.
     * @param width width of the bounding box.
     * @param height height of the bounding box.
     * @param cal the calendar specifying the day to use, must not be null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintMonthStringForeground(Graphics g, int x, int y,
                                              int width, int height, Calendar cal) {
        // Paint month name.
        // 
        Font oldFont = monthView.getFont();
    
        // TODO: Calculating the bounds of the text dynamically so we can invoke
        // a popup for selecting the month/year to view.
        g.setFont(derivedFont);
        FontMetrics fm = monthView.getFontMetrics(derivedFont);
        int month = cal.get(Calendar.MONTH);
        String monthName = monthsOfTheYear[month];
        String yearString = Integer.toString(cal.get(Calendar.YEAR));
    
        Rectangle2D rect = fm.getStringBounds(monthName, g);
        monthStringBounds[month] = new Rectangle((int) rect.getX(), (int) rect.getY(),
                (int) rect.getWidth(), (int) rect.getHeight());
        int spaceWidth = (int) fm.getStringBounds(" ", g).getWidth();
        rect = fm.getStringBounds(yearString, g);
        yearStringBounds[month] = new Rectangle((int) rect.getX(), (int) rect.getY(),
                (int) rect.getWidth(), (int) rect.getHeight());
        // END
    
        g.setColor(monthView.getMonthStringForeground());
        int tmpX =
                x + (calendarWidth / 2) -
                        ((monthStringBounds[month].width + yearStringBounds[month].width + spaceWidth) / 2);
        int tmpY = y + monthView.getBoxPaddingY() + ((monthBoxHeight - boxHeight) / 2) +
                fm.getAscent();
        monthStringBounds[month].x = tmpX;
        yearStringBounds[month].x = (monthStringBounds[month].x + monthStringBounds[month].width +
                spaceWidth);
    
        paintMonthStringForeground(g,monthName, monthStringBounds[month].x, tmpY, yearString, yearStringBounds[month].x, tmpY, cal);
        g.setFont(oldFont);
    }

    /**
     * Paints only text for month and year. No calculations made. Used by custom LAFs. 
     * <p>
     * 
     * Note: the given calendar must not be changed.
     * 
     * @param g Graphics to paint into.
     * @param monthName Name of the month.
     * @param monthX Month string x coordinate.
     * @param monthY Month string y coordinate.
     * @param yearName Name (number) of the year.
     * @param yearX Year string x coordinate.
     * @param yearY Year string y coordinate.
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintMonthStringForeground(Graphics g, String monthName, int monthX, int monthY, 
            String yearName, int yearX, int yearY, Calendar cal) {
        g.drawString(monthName, monthX, monthY);
        g.drawString(yearName, yearX, yearY);
    }

    /**
     * PENDING JW: this implementation actually only paints the line separating the
     * weekOfYear column from the days.
     * 
     * @param g
     * @param x
     * @param y
     * @param width
     * @param height
     * @param cal
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintWeekOfYearBackground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        int boxPaddingY = monthView.getBoxPaddingY();
        x = isLeftToRight ? x + width - 1 : x;
        g.setColor(monthView.getForeground());
        g.drawLine(x, y + boxPaddingY, x, y + height - boxPaddingY);
    }

    /**
     * 
     * Paints all days in the days' grid, that is the month area below
     * the daysOfWeek and to the right/left (depending on 
     * the monthView's componentOrientation) of the weekOfYears. 
     * The calendar
     * represents the first day of the month to paint. <p>
     * 
     * Note: the calendar must not be changed.
     * 
     * @param g Graphics object.
     * @param left the left boundary of the day grid.
     * @param top the upper boundary of the day grid
     * @param width the width of the day grid.
     * @param cal the calendar specifying the the first day of the month to paint, 
     *   must not be null
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintDays(Graphics g, int left, int top, int width, Calendar cal) {
        
        Calendar calendar = (Calendar) cal.clone();
        CalendarUtils.startOfMonth(calendar);
        Date startOfMonth = calendar.getTime();
        CalendarUtils.endOfMonth(calendar);
        Date endOfMonth = calendar.getTime();
        // reset the clone
        calendar.setTime(cal.getTime());
        // alwaysfill the day grid in the month completely
//        int weeks = getWeeks(calendar);
        // adjust to start of week 
        calendar.setTime(cal.getTime());
        CalendarUtils.startOfWeek(calendar);
        // painting a grid of day boxes, all with dimensions 
        // width == fullBoxWidth and height = fullBoxHeight.
        int topOfDay = top;
        // 
        for (int week = 0; week < WEEKS_IN_MONTH; week++) {
            // PENDING JW: further simplify - now that we have the reverse mapping
            // (from logical to bounds) - use it! That will keep the RToL logic
            // out of here (which was the whole point of introducing logical coords).
//            for (int week = 0; week <= weeks; week++) {
            int leftOfDay = isLeftToRight ? left : left + width - fullBoxWidth;
            
            for (int day = 0; day < 7; day++) {
                if (calendar.getTime().before(startOfMonth)) {
                    // leading
                    paintLeadingDay(g, leftOfDay, topOfDay, calendar);
                   
                } else if (calendar.getTime().after(endOfMonth)) {
                    paintTrailingDay(g, leftOfDay, topOfDay, calendar);
                    
                } else {
                    paintDay(g, leftOfDay, topOfDay, calendar);
                }
                leftOfDay = isLeftToRight ? leftOfDay + fullBoxWidth : leftOfDay - fullBoxWidth;
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            // the assumption is not always true - fails if we had the turn-on of DST with
//            if (!CalendarUtils.isStartOfWeek(calendar)) {
//                throw new IllegalStateException("started painting at " + firstStartOfWeek + 
//                              " should still be on the start of a week instead of " + calendar.getTime());
//            }
            topOfDay += fullBoxHeight;
        }
    }
    /**
     * PENDING JW: this implementation actually only paints the line separating the
     * daysOfTheWeek row from the days.
     * 
     * 
     * @param g Graphics object.
     * @param x x location of day header
     * @param y y location of day header
     * @param width width of day header
     * @param height height day header
     * @param calendar the calendar specifying the the first day of the month to
     *        paint, must not be null
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintDayOfTheWeekBackground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        int boxPaddingX = monthView.getBoxPaddingX();
        g.setColor(monthView.getForeground());
        g.drawLine(x + boxPaddingX, y + height - 1, x + width - boxPaddingX, y + height - 1);
    }

    /**
     * Paints background of the given rectangle.
     * 
     * PENDING JW: this is called from paint, which shouldnt do 
     * it? It's the responsibility of super update(comp, g).
     * 
     * @param clip
     * @param g
     * 
     * @deprecated no longer used - it's up to subclasses to implement
     * if they think it's needed.
     */
    @Deprecated
    protected void paintBackground(final Rectangle clip, final Graphics g) {
        if (monthView.isOpaque()) {
            g.setColor(monthView.getBackground());
            g.fillRect(clip.x, clip.y, clip.width, clip.height);
        }
    }

    /**
     * Paints the row which contains the days of the week.
     * 
     * @param g
     * @param x
     * @param y
     * @param width
     * @param calendar
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintDaysOfTheWeek(Graphics g, int x, int y, int width,
            Calendar calendar) {
        // Paint background of the short names for the days of the week.
        boolean showingWeekNumber = monthView.isShowingWeekNumber();
        int tmpX = isLeftToRight ? x + (showingWeekNumber ? fullBoxWidth : 0) : x;
        int tmpY = y + fullMonthBoxHeight;
        int tmpWidth = width - (showingWeekNumber ? fullBoxWidth : 0);
        paintDayOfTheWeekBackground(g, tmpX, tmpY, tmpWidth, fullBoxHeight, calendar);
    
        // Paint short representation of day of the week.
        int dayIndex = monthView.getFirstDayOfWeek() - 1;
        String[] daysOfTheWeek = monthView.getDaysOfTheWeek();
    
        Font oldFont = monthView.getFont();
        g.setFont(derivedFont);
        g.setColor(monthView.getDaysOfTheWeekForeground());
        FontMetrics fm = monthView.getFontMetrics(derivedFont);
        for (int i = 0; i < JXMonthView.DAYS_IN_WEEK; i++) {
            tmpX = isLeftToRight ?
                    x + (i * fullBoxWidth) + monthView.getBoxPaddingX() +
                            (boxWidth / 2) -
                            (fm.stringWidth(daysOfTheWeek[dayIndex]) /
                                    2) :
                    x + width - (i * fullBoxWidth) - monthView.getBoxPaddingX() -
                            (boxWidth / 2) -
                            (fm.stringWidth(daysOfTheWeek[dayIndex]) /
                                    2);
            if (showingWeekNumber) {
                tmpX += isLeftToRight ? fullBoxWidth : -fullBoxWidth;
            }
            tmpY = y + fullMonthBoxHeight + monthView.getBoxPaddingY() + fm.getAscent();
            g.drawString(daysOfTheWeek[dayIndex], tmpX, tmpY);
            dayIndex++;
            if (dayIndex == JXMonthView.DAYS_IN_WEEK) {
                dayIndex = 0;
            }
        }
        g.setFont(oldFont);
    }

    /**
     * Paints a day the current month, represented by the calendar in a day-box
     * located at left/top. The size of the day-box is defined by
     * fullBoxWidth/Height. The appearance of the day depends on its state (like
     * unselectable, flagged, selected)
     * <p>
     * 
     * Note: the given calendar must not be changed.
     * 
     * @param g the Graphics to paint into.
     * @param left the left boundary of the day-box to paint.
     * @param top the upper boundary of the day-box to paint.
     * @param calendar the calendar specifying the the day to paint, must not be
     *        null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintDay(Graphics g, int left, int top, Calendar calendar) {
        if (monthView.isUnselectableDate(calendar.getTime())) {
            paintUnselectableDayBackground(g, left, top, fullBoxWidth,
                    fullBoxHeight, calendar);
            paintUnselectableDayForeground(g, left, top, fullBoxWidth,
                    fullBoxHeight, calendar);
    
        } else if (monthView.isFlaggedDate(calendar.getTime())) {
            paintFlaggedDayBackground(g, left, top, fullBoxWidth,
                    fullBoxHeight, calendar);
            paintFlaggedDayForeground(g, left, top, fullBoxWidth,
                    fullBoxHeight, calendar);
        } else {
            paintDayBackground(g, left, top, fullBoxWidth, fullBoxHeight,
                    calendar);
            paintDayForeground(g, left, top, fullBoxWidth, fullBoxHeight,
                    calendar);
        }
    }

    /**
     * Paints a trailing day of the current month, represented by the calendar,
     * in a day-box located at left/top. The size of the day-box is defined by
     * fullBoxWidth/Height. Does nothing if the monthView's
     * isShowingTrailingDates is false.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     * 
     * @param g the Graphics to paint into.
     * @param left the left boundary of the day-box to paint.
     * @param top the upper boundary of the day-box to paint.
     * @param calendar the calendar specifying the the day to paint, must not be
     *        null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintTrailingDay(Graphics g, int left, int top,
            Calendar calendar) {
        if (!monthView.isShowingTrailingDays())
            return;
        paintTrailingDayBackground(g, left, top, fullBoxWidth, fullBoxHeight,
                calendar);
        paintTrailingDayForeground(g, left, top, fullBoxWidth, fullBoxHeight,
                calendar);
    }

    /**
     * Paints a leading day of the current month, represented by the calendar,
     * in a day-box located at left/top. The size of the day-box is defined by
     * fullBoxWidth/Height. Does nothing if the monthView's
     * isShowingLeadingDates is false.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     * 
     * @param g the Graphics to paint into.
     * @param left the left boundary of the day-box to paint.
     * @param top the upper boundary of the day-box to paint.
     * @param calendar the calendar specifying the the day to paint, must not be
     *        null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintLeadingDay(Graphics g, int left, int top,
            Calendar calendar) {
        if (!monthView.isShowingLeadingDays())
            return;
        paintLeadingDayBackground(g, left, top, fullBoxWidth, fullBoxHeight,
                calendar);
        paintLeadingDayForeground(g, left, top, fullBoxWidth, fullBoxHeight,
                calendar);
    }

    /**
     * Paints the weeks of year if the showingWeek property is true. Does
     * nothing otherwise.
     * 
     * It is assumed the given calendar is already set to the first day of the
     * month. The calendar is unchanged when leaving this method.
     * 
     * Note: the given calendar must not be changed.
     * 
     * PENDING JW: this implementation doesn't need the height - should it be
     * given anyway for symetry in case subclasses need it?
     * 
     * @param g Graphics object.
     * @param x x location of month
     * @param initialY y the upper bound of the "weekNumbers-box"
     * @param width width of month
     * @param cal the calendar specifying the the first day of the month to
     *        paint, must not be null
     *        
     * @deprecated       
     */
    @Deprecated
    protected void paintWeeksOfYear(Graphics g, int x, int initialY, int width,
            Calendar cal) {
        if (!monthView.isShowingWeekNumber())
            return;
        int tmpX = isLeftToRight ? x : x + width - fullBoxWidth;
        paintWeekOfYearBackground(g, tmpX, initialY, fullBoxWidth,
                calendarHeight - (fullMonthBoxHeight + fullBoxHeight), cal);
    
        Calendar calendar = (Calendar) cal.clone();
        int weeks = getWeeks(calendar);
        calendar.setTime(cal.getTime());
        for (int weekOfYear = 0; weekOfYear <= weeks; weekOfYear++) {
                paintWeekOfYearForeground(g, tmpX, initialY, fullBoxWidth,
                        fullBoxHeight, calendar);
            initialY += fullBoxHeight;
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }
    
    }

    /**
     * Paints the week of the year of the week of the year represented by the
     * given calendar.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object
     * @param x x-coordinate of upper left corner.
     * @param y y-coordinate of upper left corner.
     * @param width width of bounding box
     * @param height height of bounding box
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    @SuppressWarnings({"UNUSED_SYMBOL", "UnusedDeclaration"})
    protected void paintWeekOfYearForeground(Graphics g, int x, int y, int width, int height, 
            Calendar cal) {
        String str = Integer.toString(cal.get(Calendar.WEEK_OF_YEAR));
        FontMetrics fm;
    
        g.setColor(weekOfTheYearForeground);
    
        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();
    
        fm = g.getFontMetrics();
        g.drawString(str,
                isLeftToRight ?
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(str) :
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(str) - 1,
                y + boxPaddingY + fm.getAscent());
    }

    /**
     * Paint the background for the day specified by the given calendar.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     * 
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param cal the calendar specifying the day to paint, must not be null
     * @see  org.jdesktop.swingx.JXMonthView#isSelectedDate
     * @see  #isToday
     * 
     * @deprecated no longer used in paint/layout with renderer.
     * 
     */
    @Deprecated
    protected void paintDayBackground(Graphics g, int x, int y, int width, int height,
                                      Calendar cal) {
        Date date = cal.getTime(); 
        
        if (monthView.isSelected(date)) {
            g.setColor(monthView.getSelectionBackground());
            g.fillRect(x, y, width, height);
        }
    
        // If the date is today make sure we draw it's background over the selected
        // background.
        if (isToday(date)) {
            g.setColor(monthView.getTodayBackground());
            g.drawRect(x, y, width - 1, height - 1);
        }
    }

    /**
     * Paint the foreground for the specified day.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param cal the calendar specifying the day to paint, must not be null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintDayForeground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        String numericDay = dayOfMonthFormatter.format(cal.getTime());
    
        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();
        
        g.setColor(monthView.getDayForeground(cal.get(Calendar.DAY_OF_WEEK)));
        paintDayForeground(g, numericDay, isLeftToRight ? x + boxPaddingX + boxWidth : x + boxPaddingX + boxWidth - 1,
                y + boxPaddingY, cal);
    }

    /**
     * Paints string of the day. No calculations made. Used by LAFs.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     * @param g Graphics to paint on.
     * @param numericDay Text representation of the day.
     * @param x X coordinate of the upper <b>right</b> corner.
     * @param y Y coordinate of the upper <b>right</b> corner.
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintDayForeground(Graphics g, String numericDay, int x, int y, Calendar cal) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(numericDay, x - fm.stringWidth(numericDay), y + fm.getAscent());
    }

    /**
     * Paint the background for the specified flagged day. The default implementation just calls
     * <code>paintDayBackground</code>.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     * 
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param cal the calendar specifying the day to paint, must not be null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintFlaggedDayBackground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        paintDayBackground(g, x, y, width, height, cal);
    }

    /**
     * Paint the foreground for the specified flagged day.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param cal the calendar specifying the day to paint, must not be null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintFlaggedDayForeground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        Date date = cal.getTime();
        String numericDay = dayOfMonthFormatter.format(date);
        FontMetrics fm;
    
        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();
    
        Font oldFont = monthView.getFont();
        g.setColor(monthView.getFlaggedDayForeground());
        g.setFont(derivedFont);
        fm = monthView.getFontMetrics(derivedFont);
        g.drawString(numericDay,
                isLeftToRight ?
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(numericDay):
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(numericDay) - 1,
                y + boxPaddingY + fm.getAscent());
        g.setFont(oldFont);
    }

    /**
     * Paint the foreground for the specified unselectable day.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param cal the calendar specifying the day to paint, must not be null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintUnselectableDayBackground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        paintDayBackground(g, x, y, width, height, cal);
    }

    /**
     * Paint the foreground for the specified unselectable day.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param cal the calendar specifying the day to paint, must not be null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintUnselectableDayForeground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        paintDayForeground(g, x, y, width, height, cal);
        g.setColor(unselectableDayForeground);
    
        String numericDay = dayOfMonthFormatter.format(cal.getTime());
        FontMetrics fm = monthView.getFontMetrics(derivedFont);
        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();
        width = fm.stringWidth(numericDay);
        height = fm.getAscent();
        x = isLeftToRight ? x + boxPaddingX + boxWidth - fm.stringWidth(numericDay) :
                x + boxPaddingX +
                        boxWidth - fm.stringWidth(numericDay) - 1;
        y = y + boxPaddingY;
    
        g.drawLine(x, y, x + width, y + height);
        g.drawLine(x + 1, y, x + width + 1, y + height);
        g.drawLine(x + width, y, x, y + height);
        g.drawLine(x + width - 1, y, x - 1, y + height);
    }

    /**
     * Paint the background for the specified leading day.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param cal the calendar specifying the day to paint, must not be null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintLeadingDayBackground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        paintDayBackground(g, x, y, width, height, cal);
    }

    /**
     * Paint the foreground for the specified leading day.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param cal the calendar specifying the day to paint, must not be null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintLeadingDayForeground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        String numericDay = dayOfMonthFormatter.format(cal.getTime());
        FontMetrics fm;
    
        g.setColor(leadingDayForeground);
    
        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();
    
        fm = g.getFontMetrics();
        int ltorOffset = x + boxPaddingX +
                boxWidth - fm.stringWidth(numericDay);
        g.drawString(numericDay,
                isLeftToRight ?
                        ltorOffset :
                        ltorOffset - 1,
                y + boxPaddingY + fm.getAscent());
    }

    /**
     * Paint the background for the specified trailing day.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param cal the calendar specifying the day to paint, must not be null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintTrailingDayBackground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        paintLeadingDayBackground(g, x, y, width, height, cal);
    }

    /**
     * Paint the foreground for the specified trailing day.
     * <p>
     * 
     * Note: the given calendar must not be changed.
     *
     * @param g Graphics object to paint to
     * @param x x-coordinate of upper left corner
     * @param y y-coordinate of upper left corner
     * @param width width of bounding box for the day
     * @param height height of bounding box for the day
     * @param cal the calendar specifying the day to paint, must not be null
     * 
     * @deprecated no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected void paintTrailingDayForeground(Graphics g, int x, int y, int width, int height, Calendar cal) {
        String numericDay = dayOfMonthFormatter.format(cal.getTime());
        FontMetrics fm;
    
        g.setColor(trailingDayForeground);
    
        int boxPaddingX = monthView.getBoxPaddingX();
        int boxPaddingY = monthView.getBoxPaddingY();
    
        fm = g.getFontMetrics();
        g.drawString(numericDay,
                isLeftToRight ?
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(numericDay) :
                        x + boxPaddingX +
                                boxWidth - fm.stringWidth(numericDay) - 1,
                y + boxPaddingY + fm.getAscent());
    }
}
